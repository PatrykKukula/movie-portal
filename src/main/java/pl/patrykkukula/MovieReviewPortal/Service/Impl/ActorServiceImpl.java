package pl.patrykkukula.MovieReviewPortal.Service.Impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorDtoWithMovies;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorSummaryDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorUpdateDto;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Mapper.ActorMapper;
import pl.patrykkukula.MovieReviewPortal.Model.Actor;
import pl.patrykkukula.MovieReviewPortal.Repository.ActorRepository;
import pl.patrykkukula.MovieReviewPortal.Service.IActorService;
import pl.patrykkukula.MovieReviewPortal.Utils.ServiceUtils;

import java.util.List;

import static java.lang.String.valueOf;
import static pl.patrykkukula.MovieReviewPortal.Mapper.ActorMapper.*;
import static pl.patrykkukula.MovieReviewPortal.Utils.ServiceUtils.validateId;
import static pl.patrykkukula.MovieReviewPortal.Utils.ServiceUtils.validateSorting;

@Service
@RequiredArgsConstructor
public class ActorServiceImpl implements IActorService {

    private final ActorRepository actorRepository;

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
        return mapToActorDtoWithMovies(actor);
    }
    @Override
    public List<ActorDto> fetchAllActors(String sorted) {
        String validatedSorted = validateSorting(sorted);
        return validatedSorted.equals("ASC") ?
             actorRepository.findAllSortedByNameAsc().stream().map(ActorMapper::mapToActorDto).toList() :
                actorRepository.findAllSortedByNameDesc().stream().map(ActorMapper::mapToActorDto).toList();
    }
    @Override
    public List<ActorSummaryDto> fetchAllActorsSummary() {
        return actorRepository.findAll().stream()
                .map(ActorMapper::mapToActorSummary)
                .toList();
    }
    @Override
    public List<ActorDto> fetchAllActorsByNameOrLastName(String name, String sorted) {
        String validatedSorted = validateSorting(sorted);
       return validatedSorted.equals("ASC") ?
               actorRepository.findAllByFirstOrLastNameAsc(name).stream().map(ActorMapper::mapToActorDto).toList() :
               actorRepository.findAllByFirstOrLastNameDesc(name).stream().map(ActorMapper::mapToActorDto).toList();
    }
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void updateActor(ActorUpdateDto actorDto, Long actorId) {
        validateId(actorId);
        Actor actor = actorRepository.findById(actorId).orElseThrow(() -> new ResourceNotFoundException("Actor", "id", valueOf(actorId)));
        actorRepository.save(mapToActorUpdate(actorDto, actor));
    }
}
