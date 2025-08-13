package pl.patrykkukula.MovieReviewPortal.Service.Impl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.*;
import pl.patrykkukula.MovieReviewPortal.Dto.Rate.RateDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Rate.RatingResult;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Mapper.ActorMapper;
import pl.patrykkukula.MovieReviewPortal.Mapper.DirectorMapper;
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

@Service
@RequiredArgsConstructor
public class ActorServiceImpl implements IActorService {
    private final ActorRepository actorRepository;
    private final ActorRateRepository actorRateRepository;
    private final UserDetailsServiceImpl userDetailsService;
    /*
        COMMON SECTION
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Long addActor(ActorDto actorDto) {
        Actor actor = actorRepository.save(mapToActor(actorDto));
        return actor.getActorId();
    }
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void removeActor(Long actorId) {
        validateId(actorId);
        Actor actor = actorRepository.findByIdWithMovies(actorId).orElseThrow(() -> new ResourceNotFoundException("Actor", "id", valueOf(actorId)));
        actor.getMovies().forEach(movie -> movie.getActors().remove(actor));
        actorRepository.deleteById(actorId);
    }
    @Override
    public ActorDtoWithMovies fetchActorByIdWithMovies(Long actorId) {
        validateId(actorId);
        Actor actor = actorRepository.findByIdWithMovies(actorId)
                .orElseThrow(() -> new ResourceNotFoundException("Actor", "actor id", valueOf(actorId)));
        Double rate = actorRateRepository.getAverageActorRate(actorId);
        Integer rateNumber = actorRepository.countActorRates(actorId);
        return mapToActorDtoWithMovies(actor, rate, rateNumber);
    }
    @Override
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
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public RatingResult addRateToActor(RateDto rateDto) {
        validateId(rateDto.getEntityId());
        UserEntity user = userDetailsService.getUserEntity();
        Optional<ActorRate> optCurrentRate = actorRateRepository.findByActorIdAndUserId(rateDto.getEntityId(), user.getUserId());
        if (optCurrentRate.isPresent()) {
            ActorRate currentRate = optCurrentRate.get();
            currentRate.setRate(rateDto.getRate());
            ActorRate updatedRate = actorRateRepository.save(currentRate);
            return new RatingResult(updatedRate.getActor().averageActorRate(), true);

        }
        else {
            Actor actor = actorRepository.findByIdWithRates(rateDto.getEntityId())
                    .orElseThrow(() -> new ResourceNotFoundException("Actor", "actor id", String.valueOf(rateDto.getEntityId())));
            ActorRate actorRate = ActorRate.builder()
                    .actor(actor)
                    .user(user)
                    .rate(rateDto.getRate())
                    .build();
            ActorRate addedRate = actorRateRepository.save(actorRate);
            actor.getActorRates().add(addedRate);
            return new RatingResult(addedRate.getActor().averageActorRate(), false);
        }
    }
    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Double removeRate(Long actorId) {
        validateId(actorId);
        UserEntity user = userDetailsService.getUserEntity();
        actorRateRepository.deleteByActorIdAndUserId(actorId, user.getUserId());
        Actor actor = actorRepository.findByIdWithRates(actorId).orElseThrow(() -> new ResourceNotFoundException("Actor", "Actor id", String.valueOf(actorId)));
        return actor.averageActorRate();
    }
    /*
        REST API SECTION
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void updateActor(ActorUpdateDto actorDto, Long actorId) {
        validateId(actorId);
        Actor actor = actorRepository.findById(actorId).orElseThrow(() -> new ResourceNotFoundException("Actor", "id", valueOf(actorId)));
        actorRepository.save(mapToActorUpdate(actorDto, actor));
    }
    /*
        VAADIN VIEW SECTION
     */
    @Override
    public ActorDto fetchActorById(Long actorId) {
        validateId(actorId);
        Actor actor = actorRepository.findById(actorId)
                .orElseThrow(() -> new ResourceNotFoundException("Actor", "actor id", valueOf(actorId)));
        return mapToActorDto(actor);
    }
    @Override
    public List<ActorSummaryDto> fetchAllActorsSummaryByIds(List<Long> actorIds) {
        return actorRepository.findAllById(actorIds).stream()
                .map(ActorMapper::mapToActorSummary)
                .toList();
    }
    @Override
    public List<ActorSummaryDto> fetchAllActorsSummary() {
        return actorRepository.findAll().stream()
                .map(ActorMapper::mapToActorSummary)
                .toList();
    }

    @Override
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
    public void updateActorVaadin(Long actorId, ActorDto actorDto) {
        validateId(actorId);
        Actor actor = actorRepository.findById(actorId).orElseThrow(() -> new ResourceNotFoundException("Actor", "id", valueOf(actorId)));
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
