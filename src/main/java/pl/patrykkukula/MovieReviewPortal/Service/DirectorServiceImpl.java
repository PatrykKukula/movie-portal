package pl.patrykkukula.MovieReviewPortal.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.patrykkukula.MovieReviewPortal.Dto.DirectorDto;
import pl.patrykkukula.MovieReviewPortal.Dto.DirectorDtoWithMovies;
import pl.patrykkukula.MovieReviewPortal.Dto.UpdateDto.DirectorUpdateDto;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Mapper.DirectorMapper;
import pl.patrykkukula.MovieReviewPortal.Model.Director;
import pl.patrykkukula.MovieReviewPortal.Repository.DirectorRepository;
import pl.patrykkukula.MovieReviewPortal.Service.IDirectorService;
import java.util.List;
import static pl.patrykkukula.MovieReviewPortal.Mapper.DirectorMapper.*;
import static pl.patrykkukula.MovieReviewPortal.Utils.ServiceUtils.validateId;
import static pl.patrykkukula.MovieReviewPortal.Utils.ServiceUtils.validateSorting;

@Service
@RequiredArgsConstructor
public class DirectorServiceImpl implements IDirectorService {

    private final DirectorRepository directorRepository;

    @Override
    public Long addDirector(DirectorDto directorDto) {
        Director director = directorRepository.save(mapToDirector(directorDto));
        return director.getDirectorId();
    }
    @Override
    public void removeDirector(Long directorId) {
        validateId(directorId);
        if (directorRepository.findById(directorId).isEmpty()) throw new ResourceNotFoundException("Director", "id", String.valueOf(directorId));
        directorRepository.deleteById(directorId);
    }
    @Override
    public DirectorDtoWithMovies fetchDirectorByIdWithMovies(Long directorId) {
        validateId(directorId);
        Director director = directorRepository.findByIdWithMovies(directorId)
                .orElseThrow(() -> new ResourceNotFoundException("Director", "id", String.valueOf(directorId)));
        return mapToDirectorDtoWithMovies(director);
    }
    @Override
    public List<DirectorDto> fetchAllDirectors(String sorted) {
        String validatedSorting = validateSorting(sorted);
        return validatedSorting.equals("ASC") ?
                directorRepository.findAllSortedAsc().stream().map(DirectorMapper::mapToDirectorDto).toList() :
                directorRepository.findAllSortedDesc().stream().map(DirectorMapper::mapToDirectorDto).toList();
    }
    @Override
    public List<DirectorDto> fetchAllDirectorsByNameOrLastName(String name, String sorted) {
        String validatedSorted = validateSorting(sorted);
        return validatedSorted.equals("ASC") ?
                directorRepository.findAllByFirstOrLastNameSortedAsc(name).stream().map(DirectorMapper::mapToDirectorDto).toList() :
                directorRepository.findAllByFirstOrLastNameSortedDesc(name).stream().map(DirectorMapper::mapToDirectorDto).toList();
    }
    @Override
    @Transactional
    public void updateDirector(DirectorUpdateDto directorDto, Long directorId) {
        validateId(directorId);
        Director director = directorRepository.findById(directorId).orElseThrow(() -> new ResourceNotFoundException("Director", "id", String.valueOf(directorId)));
        directorRepository.save(mapToDirectorUpdate(directorDto, director));
    }
}
