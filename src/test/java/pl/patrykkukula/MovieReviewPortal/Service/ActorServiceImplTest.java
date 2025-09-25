package pl.patrykkukula.MovieReviewPortal.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.*;
import pl.patrykkukula.MovieReviewPortal.Dto.EntityWithRate;
import pl.patrykkukula.MovieReviewPortal.Dto.Rate.RateDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Rate.RatingResult;
import pl.patrykkukula.MovieReviewPortal.Exception.InvalidIdException;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Model.Actor;
import pl.patrykkukula.MovieReviewPortal.Model.ActorRate;
import pl.patrykkukula.MovieReviewPortal.Model.UserEntity;
import pl.patrykkukula.MovieReviewPortal.Repository.ActorRateRepository;
import pl.patrykkukula.MovieReviewPortal.Repository.ActorRepository;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.ActorServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.CacheLookupServiceImpl;

import java.time.LocalDate;
import java.util.*;

import static java.time.LocalDate.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ActorServiceImplTest {

    @Mock
    private ActorRepository actorRepository;
    @Mock
    private ActorRateRepository actorRateRepository;
    @Mock
    private UserDetailsServiceImpl userDetailsService;
    @InjectMocks
    private ActorServiceImpl actorService;
    @Mock
    private CacheLookupServiceImpl cacheLookupService;
    private ActorDto actorDto;
    private Actor actor1;
    private Actor actor2;
    private ActorUpdateDto actorUpdateDto;
    private RateDto rateDto;
    private UserEntity user;
    private ActorRate actorRate;
    private final LocalDate date = of(2020, 1, 1);

    @BeforeEach
    void setUp() {
        actorDto = ActorDto.builder()
                .id(1L)
                .firstName("Alex")
                .lastName("Smith")
                .country("Norway")
                .dateOfBirth(date)
                .build();
        actor1 = Actor.builder()
                .actorId(1L)
                .firstName("Alex")
                .lastName("Smith")
                .country("Norway")
                .dateOfBirth(date)
                .movies(Collections.emptyList())
                .actorRates(new ArrayList<>())
                .build();
        actor2 = Actor.builder()
                .actorId(1L)
                .firstName("Ben")
                .lastName("Milk")
                .country("USA")
                .dateOfBirth(date)
                .actorRates(new ArrayList<>())
                .build();
        actorUpdateDto = ActorUpdateDto.builder()
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
        actorRate = ActorRate.builder()
                .actor(actor1)
                .rate(5)
                .build();
        actor1.setActorRates(Arrays.asList(actorRate));
        actor2.setActorRates(Arrays.asList(actorRate));
    }
    @Test
    public void shouldAddActorCorrectly() {
        when(actorRepository.save(any(Actor.class))).thenReturn(actor1);

        Long actorId = actorService.addActor(actorDto);

        assertEquals(1L, actorId);
        verify(actorRepository, times(1)).save(any(Actor.class));
    }
    @Test
    public void shouldRemoveActorCorrectly() {
        when(actorRepository.findByIdWithMovies(anyLong())).thenReturn(Optional.of(actor1));
        doNothing().when(actorRepository).deleteById(anyLong());

        actorService.removeActor(1L);

        verify(actorRepository, times(1)).findByIdWithMovies(anyLong());
        verify(actorRepository, times(1)).deleteById(anyLong());
    }
    @Test
    public void shouldThrowInvalidIdExceptionWhenIdIsInvalid() {
        InvalidIdException ex = assertThrows(InvalidIdException.class, () -> actorService.removeActor(-1L));
        assertTrue(ex.getMessage().contains("ID cannot be less than 1 or null"));
    }
    @Test
    public void shouldThrowResourceNotFoundExceptionWhenActorNotFound() {
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> actorService.removeActor(1L));
        assertTrue(ex.getMessage().contains("Resource Actor not found"));
    }
    @Test
    public void shouldFetchActorCorrectly() {
        when(actorRepository.findByIdWithMovies(anyLong())).thenReturn(Optional.of(actor1));
        when(actorRateRepository.getAverageActorRate(anyLong())).thenReturn(5.0);
        when(actorRepository.countActorRates(anyLong())).thenReturn(100);

        ActorDtoWithMovies fetchedActor = actorService.fetchActorByIdWithMovies(1L);

        assertEquals(1L, fetchedActor.getId());
        assertEquals("Alex", fetchedActor.getFirstName());
        assertEquals(0, fetchedActor.getMovies().size());
        assertEquals(100, fetchedActor.getRateNumber());
        assertEquals(5.0, fetchedActor.getRating());
    }
    @Test
    public void shouldFetchAllActorsCorrectlySortedAsc() {
        when(actorRepository.findAllSortedByNameAsc()).thenReturn(List.of(actor1, actor2));

        List<ActorDto> actors = actorService.fetchAllActors("ASC");

        assertEquals(2, actors.size());
        assertEquals("Alex", actors.get(0).getFirstName());
        assertEquals("Ben", actors.get(1).getFirstName());
    }
    @Test
    public void shouldFetchAllActorsCorrectlySortedDesc() {
        when(actorRepository.findAllSortedByNameDesc()).thenReturn(List.of(actor2, actor1));

        List<ActorDto> actors = actorService.fetchAllActors("DESC");

        assertEquals(2, actors.size());
        assertEquals("Alex", actors.get(1).getFirstName());
        assertEquals("Ben", actors.get(0).getFirstName());
    }
    @Test
    public void shouldFetchAllActorsCorrectlySortedAscWhenInputSortIsInvalid() {
        when(actorRepository.findAllSortedByNameAsc()).thenReturn(List.of(actor1, actor2));

        List<ActorDto> actors = actorService.fetchAllActors("X");

        assertEquals(2, actors.size());
        assertEquals("Alex", actors.get(0).getFirstName());
        assertEquals("Ben", actors.get(1).getFirstName());
    }
    @Test
    public void shouldReturnEmptyListWhenNoActorsFound() {
        when(actorRepository.findAllSortedByNameAsc()).thenReturn(Collections.emptyList());

        List<ActorDto> actors = actorService.fetchAllActors("ASC");

        assertEquals(0, actors.size());
    }
    @Test
    public void shouldFetchAllActorsCorrectlyByNameOrLastNameAsc() {
        when(actorRepository.findAllByFirstOrLastNameAsc(anyString())).thenReturn(List.of(actor1));

        List<ActorDto> actors = actorService.fetchAllActorsByNameOrLastName("Alex", "ASC");

        assertEquals(1, actors.size());
        assertEquals("Alex", actors.get(0).getFirstName());
    }
    @Test
    public void shouldReturnEmptyListWhenNoActorsFoundByNameOrLastNameAsc() {
        when(actorRepository.findAllByFirstOrLastNameAsc(anyString())).thenReturn(Collections.emptyList());

        List<ActorDto> actors = actorService.fetchAllActorsByNameOrLastName("d", "ASC");

        assertEquals(0, actors.size());
    }
    @Test
    public void shouldUpdateActorCorrectly() {
        when(cacheLookupService.findActorById(anyLong())).thenReturn(actor1);
        when(actorRepository.save(any(Actor.class))).thenReturn(actor2);

        actorService.updateActor(actorUpdateDto, 1L);

        verify(cacheLookupService, times(1)).findActorById(anyLong());
        verify(actorRepository, times(1)).save(any(Actor.class));
    }
    @Test
    public void shouldAddRateCorrectlyWhenUserAlreadyRated() {
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.of(user));
        when(actorRateRepository.findByActorIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(new ActorRate()));
        when(actorRateRepository.save(any(ActorRate.class))).thenReturn(actorRate);

        RatingResult ratingResult = actorService.addRateToActor(rateDto);

        assertEquals(5, ratingResult.getRate());
        assertTrue(ratingResult.getWasRated());
    }
    @Test
    public void shouldAddRateCorrectlyWhenUserDidNotRateAlready() {
        actor1.setActorRates(new ArrayList<>());
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.of(user));
        when(actorRateRepository.findByActorIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(actorRepository.findByIdWithRates(anyLong())).thenReturn(Optional.of(actor1));
        when(actorRateRepository.save(any(ActorRate.class))).thenReturn(actorRate);

        RatingResult ratingResult = actorService.addRateToActor(rateDto);

        assertEquals(5, ratingResult.getRate());
        assertFalse(ratingResult.getWasRated());
    }
    @Test
    public void shouldThrowAccessDeniedExceptionWhenAddRateAndNotLoggedIn() {
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.empty());

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> actorService.addRateToActor(rateDto));
        assertEquals("Log in to add rate", ex.getMessage());
    }
    @Test
    public void shouldThrowResourceNotFoundExceptionWhenAddRateAndActorNotFound() {
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.of(user));
        when(actorRepository.findByIdWithRates(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> actorService.addRateToActor(rateDto));
        assertTrue(ex.getMessage().contains("Resource"));
    }
    @Test
    public void shouldRemoveRateCorrectly() {
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.of(user));
        when(actorRateRepository.deleteByActorIdAndUserId(anyLong(), anyLong())).thenReturn(1);
        when(actorRepository.findByIdWithRates(anyLong())).thenReturn(Optional.of(actor1));

        Double avgRate = actorService.removeRate(1L);

        verify(actorRateRepository, times(1)).deleteByActorIdAndUserId(1L, 1L);
        assertEquals(5.0, avgRate);
    }
    @Test
    public void shouldThrowAccessDeniedExceptionWhenRemoveRateAndUserNotLoggedIn() {
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.empty());

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> actorService.removeRate(1L));
        assertEquals("Log in to remove rate", ex.getMessage());
    }
    @Test
    public void shouldThrowIllegalStateExceptionWhenRemoveRateAndUserDidNotRateActor() {
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.of(user));
        when(actorRateRepository.deleteByActorIdAndUserId(anyLong(), anyLong())).thenReturn(0);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> actorService.removeRate(1L));
        assertEquals("You didn't rate this actor", ex.getMessage());
    }
    @Test
    public void shouldReturnTopRatedActorsCorrectly() {
        when(actorRepository.findTopRatedActors()).thenReturn(List.of(actor1, actor2));

        List<EntityWithRate> topRated = actorService.fetchTopRatedActors();

        assertEquals(2, topRated.size());
    }
    @Test
    public void shouldReturnEmptyListWhenNoActorsExistAndInvokingTopRatedActors() {
        when(actorRepository.findTopRatedActors()).thenReturn(Collections.emptyList());

        List<EntityWithRate> topRated = actorService.fetchTopRatedActors();

        assertEquals(0, topRated.size());
    }
    @Test
    public void shouldFetchActorByIdCorrectly() {
        when(cacheLookupService.findActorById(anyLong())).thenReturn(actor1);

        ActorDto returnedActor = actorService.fetchActorById(1L);

        assertEquals("Alex", returnedActor.getFirstName());
        assertEquals("Smith", returnedActor.getLastName());
    }
    @Test
    public void shouldThrowIllegalArgumentExceptionWhenFetchActorByIdAndInvalidId() {
        InvalidIdException ex = assertThrows(InvalidIdException.class, () -> actorService.fetchActorById(0L));
        assertEquals("ID cannot be less than 1 or null", ex.getMessage());
    }
    @Test
    public void shouldFetchAllActorsSummaryByIdsCorrectly() {
        when(actorRepository.findAllById(anyCollection())).thenReturn(List.of(actor1, actor2));

        List<ActorSummaryDto> actors = actorService.fetchAllActorsSummaryByIds(List.of(1L, 2L));

        assertEquals(2, actors.size());
    }
    @Test
    public void shouldReturnEmptyListWhenFetchAllActorsSummaryByIdsAndNoResultsFound() {
        when(actorRepository.findAllById(anyCollection())).thenReturn(Collections.emptyList());

        List<ActorSummaryDto> actors = actorService.fetchAllActorsSummaryByIds(Collections.emptyList());

        assertEquals(0, actors.size());
    }
    @Test
    public void shouldFetchAllActorsSummaryCorrectly() {
        when(actorRepository.findAll()).thenReturn(List.of(actor1, actor2));

        List<ActorSummaryDto> actors = actorService.fetchAllActorsSummary();

        assertEquals(2, actors.size());
    }
    @Test
    public void shouldReturnEmptyListWhenFetchAllActorsSummaryAndNoResultsFound() {
        when(actorRepository.findAll()).thenReturn(Collections.emptyList());

        List<ActorSummaryDto> actors = actorService.fetchAllActorsSummary();

        assertEquals(0, actors.size());
    }
    @Test
    public void shouldFetchAllActorsViewCorrectlyWithNoParametersInput() {
        when(actorRepository.findAllWithActorRates(any(Sort.class))).thenReturn(List.of(actor1, actor2));

        List<ActorViewDto> actors = actorService.fetchAllActorsView(null, null);

        assertEquals(2, actors.size());
    }
    @Test
    public void shouldFetchAllActorsViewCorrectlyByName() {
        when(actorRepository.findAllWithRatesByNameOrLastName(anyString(), any(Sort.class))).thenReturn(List.of(actor1));

        List<ActorViewDto> actors = actorService.fetchAllActorsView("Alex", null);

        assertEquals(1, actors.size());
    }
    @Test
    public void shouldUpdateActorVaadinCorrectly() {
        when(cacheLookupService.findActorById(anyLong())).thenReturn(actor1);

        actorService.updateActorVaadin(1L, actorDto);

        verify(cacheLookupService, times(1)).findActorById(1L);
        verify(actorRepository, times(1)).save(actor1);
    }
    @Test
    public void shouldFetchRateByActorIdAndUserIdCorrectly() {
        when(actorRateRepository.findByActorIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(actorRate));

        RateDto fetchedRate = actorService.fetchRateByActorIdAndUserId(1L, 1L);

        assertEquals(5, fetchedRate.getRate());
    }
    @Test
    public void shouldReturnNullWhenFetchRateByActorIdAndUserIdAndRateDoesNotExist() {
        when(actorRateRepository.findByActorIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());

        RateDto fetchedRate = actorService.fetchRateByActorIdAndUserId(1L, 1L);

        assertNull(fetchedRate);
    }
}

