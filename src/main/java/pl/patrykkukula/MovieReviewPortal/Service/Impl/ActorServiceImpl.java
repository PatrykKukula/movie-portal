package pl.patrykkukula.MovieReviewPortal.Service.Impl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.*;
import pl.patrykkukula.MovieReviewPortal.Dto.EntityWithRate;
import pl.patrykkukula.MovieReviewPortal.Dto.Rate.RateDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Rate.RatingResult;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Mapper.ActorMapper;
import pl.patrykkukula.MovieReviewPortal.Model.*;
import pl.patrykkukula.MovieReviewPortal.Repository.ActorRateRepository;
import pl.patrykkukula.MovieReviewPortal.Repository.ActorRepository;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.IActorService;

import java.util.List;
import java.util.Optional;
import static java.lang.String.valueOf;
import static pl.patrykkukula.MovieReviewPortal.Mapper.ActorMapper.*;
import static pl.patrykkukula.MovieReviewPortal.Utils.ServiceUtils.validateId;
import static pl.patrykkukula.MovieReviewPortal.Utils.ServiceUtils.validateSorting;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActorServiceImpl implements IActorService {
    private final ActorRepository actorRepository;
    private final ActorRateRepository actorRateRepository;
    private final UserDetailsServiceImpl userDetailsService;
    private final CacheLookupServiceImpl cacheLookupService;
    /*
        COMMON SECTION
     */
    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @CacheEvict(value = {"all-actors", "all-actors-summary"}, allEntries = true)
    public Long addActor(ActorDto actorDto) {
        Actor actor = actorRepository.save(mapToActor(actorDto));
        return actor.getActorId();
    }
    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @Transactional
    @CacheEvict(value = {"actor", "actor-details"})
    public void removeActor(Long actorId) {
        validateId(actorId);
        Actor actor = actorRepository.findByIdWithMovies(actorId).orElseThrow(() -> new ResourceNotFoundException("Actor", "id", valueOf(actorId)));
        actor.getMovies().forEach(movie -> movie.getActors().remove(actor));
        actorRepository.deleteById(actorId);
    }
    @Override
    @Cacheable("actor-details")
    public ActorDtoWithMovies fetchActorByIdWithMovies(Long actorId) {
        validateId(actorId);
        Actor actor = actorRepository.findByIdWithMovies(actorId)
                .orElseThrow(() -> new ResourceNotFoundException("Actor", "actor id", valueOf(actorId)));
        Double rate = actorRateRepository.getAverageActorRate(actorId);
        Integer rateNumber = actorRepository.countActorRates(actorId);
        return mapToActorDtoWithMovies(actor, rate, rateNumber);
    }
    @Override
    @Cacheable(value = "all-actors")
    public List<ActorDto> fetchAllActors(String sorted) {
        String validatedSorted = validateSorting(sorted);
        return validatedSorted.equals("ASC") ?
             actorRepository.findAllSortedByNameAsc().stream().map(ActorMapper::mapToActorDto).toList() :
                actorRepository.findAllSortedByNameDesc().stream().map(ActorMapper::mapToActorDto).toList();
    }
    @Override
    public List<ActorDto> fetchAllActorsByNameOrLastName(String name, String sorted) {
        String validatedSorted = validateSorting(sorted);
       return validatedSorted.equals("ASC") ?
               actorRepository.findAllByFirstOrLastNameAsc(name).stream().map(ActorMapper::mapToActorDto).toList() :
               actorRepository.findAllByFirstOrLastNameDesc(name).stream().map(ActorMapper::mapToActorDto).toList();
    }
    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @CacheEvict(value = {"actor", "actor-details"}, key = "#rateDto.entityId")
    public RatingResult addRateToActor(RateDto rateDto) {
        validateId(rateDto.getEntityId());
        Optional<UserEntity> user = userDetailsService.getLoggedUserEntity();
        if (user.isPresent()) {
            Optional<ActorRate> optCurrentRate = actorRateRepository.findByActorIdAndUserId(rateDto.getEntityId(), user.get().getUserId());
            if (optCurrentRate.isPresent()) {
                ActorRate currentRate = optCurrentRate.get();
                currentRate.setRate(rateDto.getRate());
                ActorRate updatedRate = actorRateRepository.save(currentRate);
                return new RatingResult(updatedRate.getActor().averageActorRate(), true);
            } else {
                Actor actor = actorRepository.findByIdWithRates(rateDto.getEntityId())
                        .orElseThrow(() -> new ResourceNotFoundException("Actor", "actor id", String.valueOf(rateDto.getEntityId())));
                ActorRate actorRate = ActorRate.builder()
                        .actor(actor)
                        .user(user.get())
                        .rate(rateDto.getRate())
                        .build();
                ActorRate addedRate = actorRateRepository.save(actorRate);
                actor.getActorRates().add(addedRate);
                return new RatingResult(addedRate.getActor().averageActorRate(), false);
            }
        }
        throw new AccessDeniedException("Log in to add rate");
    }
    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @CacheEvict(value = {"actor", "actor-details"})
    public Double removeRate(Long actorId) {
        validateId(actorId);
        Optional<UserEntity> user = userDetailsService.getLoggedUserEntity();
        if (user.isPresent()) {
            int deletedRows = actorRateRepository.deleteByActorIdAndUserId(actorId, user.get().getUserId());
            if (deletedRows == 1) {
                Actor actor = actorRepository.findByIdWithRates(actorId).orElseThrow(() -> new ResourceNotFoundException("Actor", "Actor id", String.valueOf(actorId)));
                return actor.averageActorRate();
            }
            else throw new IllegalStateException("You didn't rate this actor");
        }
        throw new AccessDeniedException("Log in to remove rate");
    }
    @Override
    @Cacheable("top-rated-actors")
    public List<EntityWithRate> fetchTopRatedActors() {
        return actorRepository.findTopRatedActors().stream().map(actor -> (EntityWithRate) ActorMapper.mapToActorDtoWithAverageRate(actor, actor.averageActorRate())).toList();
    }
    /*
        REST API SECTION
     */
    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @CacheEvict(value = {"actor", "actor-details"}, key = "#actorId")
    public void updateActor(ActorUpdateDto actorDto, Long actorId) {
        validateId(actorId);
        Actor actor = cacheLookupService.findActorById(actorId);
        actorRepository.save(mapToActorUpdate(actorDto, actor));
    }
    /*
        VAADIN VIEW SECTION
     */
    @Override
    public ActorDto fetchActorById(Long actorId) {
        validateId(actorId);
        Actor actor = cacheLookupService.findActorById(actorId);
        return mapToActorDto(actor);
    }
    @Override
    public List<ActorSummaryDto> fetchAllActorsSummaryByIds(List<Long> actorIds) {
        return actorRepository.findAllById(actorIds).stream()
                .map(ActorMapper::mapToActorSummary)
                .toList();
    }
    @Override
    @Cacheable("all-actors-summary")
    public List<ActorSummaryDto> fetchAllActorsSummary() {
        return actorRepository.findAll().stream()
                .map(ActorMapper::mapToActorSummary)
                .toList();
    }
    @Override
    @Cacheable(value = "actors-search", unless = "#result.isEmpty() or #searchedText == null or #searchedText.length() <= 3")
    public List<ActorViewDto> fetchAllActorsView(String searchedText, String sorting) {
        String validatedSorting = validateSorting(sorting);
        Sort sort = validatedSorting.equals("ASC") ?
                Sort.by("firstName").ascending() :
                Sort.by("firstName").descending();

        return searchedText == null || searchedText.isEmpty() ?
                actorRepository.findAllWithActorRates(sort).stream().map(ActorMapper::mapToActorViewDto).toList() :
                actorRepository.findAllWithRatesByNameOrLastName(searchedText, sort).stream().map(ActorMapper::mapToActorViewDto).toList();
    }
    @Override
    @CacheEvict(value = {"actor", "actor-details"}, key = "#actorId")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public void updateActorVaadin(Long actorId, ActorDto actorDto) {
        validateId(actorId);
        Actor actor = cacheLookupService.findActorById(actorId);
        actorRepository.save(mapToActorUpdateVaadin(actorDto, actor));
    }
    @Override
    public RateDto fetchRateByActorIdAndUserId(Long actorId, Long userId) {
        Optional<ActorRate> optionalActorRate = actorRateRepository.findByActorIdAndUserId(actorId, userId);
        if (optionalActorRate.isPresent()) {
            ActorRate actorRate = optionalActorRate.get();
            return RateDto.builder()
                    .entityId(actorRate.getActorRateId())
                    .rate(actorRate.getRate())
                    .build();
        }
        return null;
    }
}
