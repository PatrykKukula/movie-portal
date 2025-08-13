package pl.patrykkukula.MovieReviewPortal.Service.Impl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.*;
import pl.patrykkukula.MovieReviewPortal.Dto.Rate.RateDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Rate.RatingResult;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Mapper.DirectorMapper;
import pl.patrykkukula.MovieReviewPortal.Model.Director;
import pl.patrykkukula.MovieReviewPortal.Model.DirectorRate;
import pl.patrykkukula.MovieReviewPortal.Model.UserEntity;
import pl.patrykkukula.MovieReviewPortal.Repository.DirectorRateRepository;
import pl.patrykkukula.MovieReviewPortal.Repository.DirectorRepository;
import pl.patrykkukula.MovieReviewPortal.Repository.MovieRepository;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.IDirectorService;
import java.util.List;
import java.util.Optional;
import static java.lang.String.valueOf;
import static pl.patrykkukula.MovieReviewPortal.Mapper.DirectorMapper.*;
import static pl.patrykkukula.MovieReviewPortal.Utils.ServiceUtils.validateId;
import static pl.patrykkukula.MovieReviewPortal.Utils.ServiceUtils.validateSorting;

@Service
@RequiredArgsConstructor
public class DirectorServiceImpl implements IDirectorService {
    private final DirectorRepository directorRepository;
    private final MovieRepository movieRepository;
    private final DirectorRateRepository directorRateRepository;
    private final UserDetailsServiceImpl userDetailsService;
    /*
        COMMON SECTION
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Long addDirector(DirectorDto directorDto) {
        Director director = mapToDirector(directorDto);
        director.setCreatedBy("ADMIN"); //zmienione na potrzeby vaadin bez logowania
        Director savedDirector = directorRepository.save(director);
        return savedDirector.getDirectorId();
    }
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void removeDirector(Long directorId) {
        validateId(directorId);
          Director director = directorRepository.findByIdWithMovies(directorId)
                  .orElseThrow(() -> new ResourceNotFoundException("Director", "id", String.valueOf(directorId)));
        director.getMovies().forEach(movie -> {
            movie.setDirector(null);
            movieRepository.save(movie);
        });
        directorRepository.deleteById(directorId);
    }
    @Override
    public DirectorDtoWithMovies fetchDirectorByIdWithMovies(Long directorId) {
        validateId(directorId);
        Director director = directorRepository.findByIdWithMovies(directorId)
                .orElseThrow(() -> new ResourceNotFoundException("Director", "id", String.valueOf(directorId)));
        Double rate = directorRateRepository.getAverageDirectorRate(directorId);
        Integer rateNumber = directorRepository.countDirectorRates(directorId);
        return mapToDirectorDtoWithMovies(director, rate, rateNumber);
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
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public RatingResult addRateToDirector(RateDto rateDto) {
        validateId(rateDto.getEntityId());
        UserEntity user = userDetailsService.getUserEntity();
        Optional<DirectorRate> optCurrentRate = directorRateRepository.findByDirectorIdAndUserId(rateDto.getEntityId(), user.getUserId());
        if (optCurrentRate.isPresent()) {
            DirectorRate currentRate = optCurrentRate.get();
            currentRate.setRate(rateDto.getRate());
            DirectorRate updatedRate = directorRateRepository.save(currentRate);
            return new RatingResult(updatedRate.getDirector().averageDirectorRate(), true);
        }
        else {
            Director director = directorRepository.findByIdWithRates(rateDto.getEntityId())
                    .orElseThrow(() -> new ResourceNotFoundException("Director", "director id", String.valueOf(rateDto.getEntityId())));
            DirectorRate directorRate = DirectorRate.builder()
                    .director(director)
                    .user(user)
                    .rate(rateDto.getRate())
                    .build();
            DirectorRate addedRate = directorRateRepository.save(directorRate);
            director.getDirectorRates().add(addedRate);
            return new RatingResult(addedRate.getDirector().averageDirectorRate(), false);
        }
    }
    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Double removeRate(Long directorId) {
        validateId(directorId);
        UserEntity user = userDetailsService.getUserEntity();
        directorRateRepository.deleteByDirectorIdAndUserId(directorId, user.getUserId());
        Director director = directorRepository.findByIdWithRates(directorId).orElseThrow(() -> new ResourceNotFoundException("Director", "director id", String.valueOf(directorId)));
        return director.averageDirectorRate();
    }
    /*
        REST API SECTION
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void updateDirector(DirectorUpdateDto directorDto, Long directorId) {
        validateId(directorId);
        Director director = directorRepository.findById(directorId).orElseThrow(() -> new ResourceNotFoundException("Director", "id", String.valueOf(directorId)));
        directorRepository.save(mapToDirectorUpdate(directorDto, director));
    }
    /*
        VAADIN VIEW SECTION
     */
    @Override
    public DirectorDto fetchDirectorById(Long directorId) {
        validateId(directorId);
        Director director = directorRepository.findById(directorId)
                .orElseThrow(() -> new ResourceNotFoundException("Director", "director id", valueOf(directorId)));
        return mapToDirectorDto(director);
    }
    @Override
    public DirectorSummaryDto fetchDirectorSummaryById(Long id) {
        Director director = directorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Director", "id", String.valueOf(id)));
        return DirectorMapper.mapToDirectorSummary(director);
    }
    @Override
    public List<DirectorSummaryDto> fetchAllDirectorsSummary() {
        return directorRepository.findAll().stream()
                .map(DirectorMapper::mapToDirectorSummary)
                .toList();
    }
    @Override
    public List<DirectorViewDto> fetchAllDirectorsView(String searchedText, String sorting) {
        String validatedSorting = validateSorting(sorting);
        Sort sort = validatedSorting.equals("ASC") ?
                Sort.by("firstName").ascending() :
                Sort.by("firstName").descending();

        return searchedText == null || searchedText.isEmpty() ?
                directorRepository.findAllWithDirectorRates(sort).stream().map(DirectorMapper::mapToDirectorViewDto).toList() :
                directorRepository.findAllWithRatesByNameOrLastName(searchedText, sort).stream().map(DirectorMapper::mapToDirectorViewDto).toList();
    }
    @Override
    public void updateDirectorVaadin(Long directorId, DirectorDto directorDto) {
        validateId(directorId);
        Director director = directorRepository.findById(directorId).orElseThrow(() -> new ResourceNotFoundException("Director", "id", valueOf(directorId)));
        directorRepository.save(mapToDirectorUpdateVaadin(directorDto, director));
    }
    @Override
    public RateDto fetchRateByDirectorIdAndUserId(Long directorId, Long userId) {
        Optional<DirectorRate> optionalDirectorRate = directorRateRepository.findByDirectorIdAndUserId(directorId, userId);
        if (optionalDirectorRate.isPresent()) {
            DirectorRate directorRate = optionalDirectorRate.get();
            return RateDto.builder()
                    .entityId(directorRate.getDirectorRateId())
                    .rate(directorRate.getRate())
                    .build();
        }
        return null;
    }
}
