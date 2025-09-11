package pl.patrykkukula.MovieReviewPortal.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.*;
import pl.patrykkukula.MovieReviewPortal.Dto.EntityWithRate;
import pl.patrykkukula.MovieReviewPortal.Dto.Rate.RateDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Rate.RatingResult;
import pl.patrykkukula.MovieReviewPortal.Exception.InvalidIdException;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Model.*;
import pl.patrykkukula.MovieReviewPortal.Repository.DirectorRateRepository;
import pl.patrykkukula.MovieReviewPortal.Repository.DirectorRepository;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.CacheLookupServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.DirectorServiceImpl;

import java.time.LocalDate;
import java.util.*;

import static java.time.LocalDate.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DirectorServiceImplTest {

    @Mock
    private DirectorRepository directorRepository;
    @Mock
    private UserDetailsServiceImpl userDetailsService;
    @Mock
    private CacheLookupServiceImpl cacheLookupService;
    @Mock
    private DirectorRateRepository directorRateRepository;
    @InjectMocks
    private DirectorServiceImpl directorService;

    private DirectorDto directorDto;
    private Director director1;
    private Director director2;
    private DirectorUpdateDto directorUpdateDto;
    private RateDto rateDto;
    private UserEntity user;
    private DirectorRate directorRate;
    private final LocalDate date = of(2020, 1, 1);
    @BeforeEach
    void setUp() {
        directorDto = DirectorDto.builder()
                .id(1L)
                .firstName("Alex")
                .lastName("Smith")
                .country("Norway")
                .dateOfBirth(date)
                .build();
        director1 = Director.builder()
                .directorId(1L)
                .firstName("Alex")
                .lastName("Smith")
                .country("Norway")
                .dateOfBirth(date)
                .movies(Collections.emptyList())
                .build();
        director2 = Director.builder()
                .directorId(1L)
                .firstName("Ben")
                .lastName("Milk")
                .country("USA")
                .dateOfBirth(date)
                .build();
        directorUpdateDto = DirectorUpdateDto.builder()
                .firstName("Mark")
                .build();
        rateDto = RateDto.builder()
                .rate(5)
                .entityId(1L)
                .build();
        user = UserEntity.builder()
                .username("user")
                .userId(1L)
                .build();
        directorRate = DirectorRate.builder()
                .director(director1)
                .rate(5)
                .build();
        director1.setDirectorRates(Arrays.asList(directorRate));
        director2.setDirectorRates(Arrays.asList(directorRate));
    }
    @Test
    public void shouldAddDirectorCorrectly() {
        when(directorRepository.save(any(Director.class))).thenReturn(director1);

        Long actorId = directorService.addDirector(directorDto);
        assertEquals(1L, actorId);
    }
    @Test
    public void shouldRemoveDirectorCorrectly() {
        when(directorRepository.findByIdWithMovies(anyLong())).thenReturn(Optional.of(director1));
        doNothing().when(directorRepository).deleteById(anyLong());

        directorService.removeDirector(1L);

        verify(directorRepository, times(1)).findByIdWithMovies(anyLong());
        verify(directorRepository, times(1)).deleteById(anyLong());
    }
    @Test
    public void shouldThrowInvalidIdExceptionWhenIdIsInvalid() {
        InvalidIdException ex = assertThrows(InvalidIdException.class, () -> directorService.removeDirector(-1L));
        assertTrue(ex.getMessage().contains("ID cannot be less than 1 or null"));
    }
    @Test
    public void shouldThrowResourceNotFoundExceptionWhenDirectorNotFound() {
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> directorService.removeDirector(1L));
        assertTrue(ex.getMessage().contains("Resource Director not found"));
    }
    @Test
    public void shouldFetchDirectorCorrectly() {
        when(directorRepository.findByIdWithMovies(anyLong())).thenReturn(Optional.of(director1));
        when(directorRateRepository.getAverageDirectorRate(anyLong())).thenReturn(5.0);
        when(directorRepository.countDirectorRates(anyLong())).thenReturn(100);

        DirectorDtoWithMovies fetchedDirector = directorService.fetchDirectorByIdWithMovies(1L);

        assertEquals(1L, fetchedDirector.getId());
        assertEquals("Alex", fetchedDirector.getFirstName());
        assertEquals(0, fetchedDirector.getMovies().size());
        assertEquals(100, fetchedDirector.getRateNumber());
        assertEquals(5.0, fetchedDirector.getRating());
    }
    @Test
    public void shouldFetchAllDirectorsCorrectlySortedAsc(){
        when(directorRepository.findAllSortedAsc()).thenReturn(List.of(director1, director2));

        List<DirectorDto> directors = directorService.fetchAllDirectors("ASC");
        assertEquals(2, directors.size());
        assertEquals("Alex", directors.get(0).getFirstName());
        assertEquals("Ben", directors.get(1).getFirstName());
    }
    @Test
    public void shouldFetchAllDirectorsCorrectlySortedDesc(){
        when(directorRepository.findAllSortedDesc()).thenReturn(List.of(director2, director1));

        List<DirectorDto> directors = directorService.fetchAllDirectors("DESC");
        assertEquals(2, directors.size());
        assertEquals("Alex", directors.get(1).getFirstName());
        assertEquals("Ben", directors.get(0).getFirstName());
    }
    @Test
    public void shouldFetchAllActorsCorrectlySortedAscWhenInputSortIsInvalid(){
        when(directorRepository.findAllSortedAsc()).thenReturn(List.of(director1, director2));

        List<DirectorDto> actors = directorService.fetchAllDirectors("X");
        assertEquals(2, actors.size());
        assertEquals("Alex", actors.get(0).getFirstName());
        assertEquals("Ben", actors.get(1).getFirstName());
    }
    @Test
    public void shouldReturnEmptyListWhenNoDirectorsFound() {
        when(directorRepository.findAllSortedAsc()).thenReturn(Collections.emptyList());

        List<DirectorDto> actors = directorService.fetchAllDirectors("ASC");
        assertEquals(0, actors.size());
    }
    @Test
    public void shouldFetchAllDirectorsCorrectlyByNameOrLastNameAsc(){
        when(directorRepository.findAllByFirstOrLastNameSortedAsc(anyString())).thenReturn(List.of(director1));

        List<DirectorDto> actors = directorService.fetchAllDirectorsByNameOrLastName("Alex","ASC");
        assertEquals(1, actors.size());
        assertEquals("Alex", actors.get(0).getFirstName());
    }
    @Test
    public void shouldFetchReturnEmptyListWhenNoActorsFoundByNameOrLastNameAsc(){
        when(directorRepository.findAllByFirstOrLastNameSortedAsc(anyString())).thenReturn(Collections.emptyList());

        List<DirectorDto> actors = directorService.fetchAllDirectorsByNameOrLastName("d","ASC");
        assertEquals(0, actors.size());
    }
    @Test
    public void shouldUpdateDirectorCorrectly() {
        when(cacheLookupService.findDirectorById(anyLong())).thenReturn(director1);
        when(directorRepository.save(any(Director.class))).thenReturn(director1);

        directorService.updateDirector(directorUpdateDto, 1L);

        verify(cacheLookupService, times(1)).findDirectorById(anyLong());
        verify(directorRepository, times(1)).save(any(Director.class));
    }
    @Test
    public void shouldAddRateCorrectlyWhenUserAlreadyRated() {
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.of(user));
        when(directorRateRepository.findByDirectorIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(new DirectorRate()));
        when(directorRateRepository.save(any(DirectorRate.class))).thenReturn(directorRate);

        RatingResult ratingResult = directorService.addRateToDirector(rateDto);

        assertEquals(5, ratingResult.getRate());
        assertTrue(ratingResult.getWasRated());
    }
    @Test
    public void shouldAddRateCorrectlyWhenUserDidNotRateAlready() {
        director1.setDirectorRates(new ArrayList<>());
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.of(user));
        when(directorRateRepository.findByDirectorIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(directorRepository.findByIdWithRates(anyLong())).thenReturn(Optional.of(director1));
        when(directorRateRepository.save(any(DirectorRate.class))).thenReturn(directorRate);

        RatingResult ratingResult = directorService.addRateToDirector(rateDto);

        assertEquals(5, ratingResult.getRate());
        assertFalse(ratingResult.getWasRated());
    }
    @Test
    public void shouldThrowAccessDeniedExceptionWhenAddRateAndNotLoggedIn() {
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.empty());

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> directorService.addRateToDirector(rateDto));
        assertEquals("Log in to add rate", ex.getMessage());
    }
    @Test
    public void shouldThrowResourceNotFoundExceptionWhenAddRateAndActorNotFound() {
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.of(user));
        when(directorRepository.findByIdWithRates(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> directorService.addRateToDirector(rateDto));
        assertTrue(ex.getMessage().contains("Resource"));
    }
    @Test
    public void shouldRemoveRateCorrectly() {
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.of(user));
        when(directorRateRepository.deleteByDirectorIdAndUserId(anyLong(), anyLong())).thenReturn(1);
        when(directorRepository.findByIdWithRates(anyLong())).thenReturn(Optional.of(director1));

        Double avgRate = directorService.removeRate(1L);

        verify(directorRateRepository, times(1)).deleteByDirectorIdAndUserId(1L, 1L);
        assertEquals(5.0, avgRate);
    }
    @Test
    public void shouldThrowAccessDeniedExceptionWhenRemoveRateAndUserNotLoggedIn() {
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.empty());

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> directorService.removeRate(1L));
        assertEquals("Log in to remove rate", ex.getMessage());
    }
    @Test
    public void shouldThrowIllegalStateExceptionWhenRemoveRateAndUserDidNotRateDirector() {
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.of(user));
        when(directorRateRepository.deleteByDirectorIdAndUserId(anyLong(), anyLong())).thenReturn(0);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> directorService.removeRate(1L));
        assertEquals("You didn't rate this director", ex.getMessage());
    }
    @Test
    public void shouldReturnTopRatedDirectorsCorrectly() {
        when(directorRepository.findTopRatedDirectors()).thenReturn(List.of(director1, director2));

        List<EntityWithRate> topRated = directorService.fetchTopRatedDirectors();

        assertEquals(2, topRated.size());
    }
    @Test
    public void shouldReturnEmptyListWhenNoActorsExistAndInvokingTopRatedDirectors() {
        when(directorRepository.findTopRatedDirectors()).thenReturn(Collections.emptyList());

        List<EntityWithRate> topRated = directorService.fetchTopRatedDirectors();

        assertEquals(0, topRated.size());
    }
    @Test
    public void shouldFetchDirectorByIdCorrectly() {
        when(cacheLookupService.findDirectorById(anyLong())).thenReturn(director1);

        DirectorDto returnedDirector = directorService.fetchDirectorById(1L);

        assertEquals("Alex", returnedDirector.getFirstName());
        assertEquals("Smith", returnedDirector.getLastName());
    }
    @Test
    public void shouldThrowIllegalArgumentExceptionWhenFetchDirectorByIdAndInvalidId() {
        InvalidIdException ex = assertThrows(InvalidIdException.class, () -> directorService.fetchDirectorById(0L));
        assertEquals("ID cannot be less than 1 or null", ex.getMessage());
    }
    @Test
    public void shouldFetchAllDirectorSummaryCorrectly() {
        when(directorRepository.findAll()).thenReturn(List.of(director1,director2));

        List<DirectorSummaryDto> directors = directorService.fetchAllDirectorsSummary();

        assertEquals(2, directors.size());
    }
    @Test
    public void shouldReturnEmptyListWhenFetchAllSirectorsSummaryAndNoResultsFound() {
        when(directorRepository.findAll()).thenReturn(Collections.emptyList());

        List<DirectorSummaryDto> directors = directorService.fetchAllDirectorsSummary();

        assertEquals(0, directors.size());
    }
    @Test
    public void shouldFetchAllDirectorsViewCorrectlyWithNoParametersInput() {
        when(directorRepository.findAllWithDirectorRates(any(Sort.class))).thenReturn(List.of(director1, director2));

        List<DirectorViewDto> directors = directorService.fetchAllDirectorsView(null, null);

        assertEquals(2, directors.size());
    }
    @Test
    public void shouldFetchAllDirectorsViewCorrectlyByName() {
        when(directorRepository.findAllWithRatesByNameOrLastName(anyString(), any(Sort.class))).thenReturn(List.of(director1));

        List<DirectorViewDto> directors = directorService.fetchAllDirectorsView("Alex", null);

        assertEquals(1, directors.size());
    }
    @Test
    public void shouldUpdateDirectorVaadinCorrectly() {
        when(cacheLookupService.findDirectorById(anyLong())).thenReturn(director1);

        directorService.updateDirectorVaadin(1L, directorDto);

        verify(cacheLookupService, times(1)).findDirectorById(1L);
        verify(directorRepository, times(1)).save(director1);
    }
    @Test
    public void shouldFetchRateByDirectorIdAndUserIdCorrectly() {
        when(directorRateRepository.findByDirectorIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(directorRate));

        RateDto fetchedRate = directorService.fetchRateByDirectorIdAndUserId(1L, 1L);

        assertEquals(5, fetchedRate.getRate());
    }
    @Test
    public void shouldReturnNullWhenFetchRateByDirectorIdAndUserIdAndRateDoesNotExist() {
        when(directorRateRepository.findByDirectorIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());

        RateDto fetchedRate = directorService.fetchRateByDirectorIdAndUserId(1L, 1L);

        assertNull(fetchedRate);
    }
}
