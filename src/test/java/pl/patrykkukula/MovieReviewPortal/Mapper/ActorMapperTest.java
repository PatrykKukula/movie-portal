package pl.patrykkukula.MovieReviewPortal.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.patrykkukula.MovieReviewPortal.Constants.MovieCategory;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.*;
import pl.patrykkukula.MovieReviewPortal.Model.Actor;
import pl.patrykkukula.MovieReviewPortal.Model.ActorRate;
import pl.patrykkukula.MovieReviewPortal.Model.Movie;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class ActorMapperTest {
    private ActorDto actorDto;
    private Actor actor;
    private Movie movie;

    @BeforeEach
    public void setUp(){
        actorDto = ActorDto.builder()
                .firstName("Joe")
                .lastName("Doe")
                .country("Poland")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();
        actor = Actor.builder()
                .actorId(1L)
                .firstName("Jane")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990,1,1))
                .country("Poland")
                .build();
        movie = Movie.builder()
                .movieId(1L)
                .category(MovieCategory.COMEDY)
                .title("Movie")
                .build();
    }
    @Test
    public void shouldMapActorDtoToActorCorrectly(){
        Actor mappedActor = ActorMapper.mapToActor(actorDto);

        assertEquals("Joe", mappedActor.getFirstName());
        assertEquals("Doe", mappedActor.getLastName());
        assertEquals(LocalDate.of(1990, 1, 1), mappedActor.getDateOfBirth());
        assertEquals("Poland", mappedActor.getCountry());
    }
    @Test
    public void shouldMapActorToActorDtoCorrectly(){
        ActorDto actorDto = ActorMapper.mapToActorDto(actor);

        assertEquals("Jane", actorDto.getFirstName());
        assertEquals("Doe", actorDto.getLastName());
        assertEquals(LocalDate.of(1990, 1, 1), actorDto.getDateOfBirth());
        assertEquals("Poland", actorDto.getCountry());
        assertEquals(1, actorDto.getId());
    }
    @Test
    public void shouldMapActorToActorViewDtoCorrectly(){
        actor.setActorRates(new ArrayList<>());
        ActorViewDto mappedActor = ActorMapper.mapToActorViewDto(actor);

        assertEquals("Jane", mappedActor.getFirstName());
        assertEquals("Doe", mappedActor.getLastName());
        assertEquals(LocalDate.of(1990, 1, 1), mappedActor.getDateOfBirth());
        assertEquals("Poland", mappedActor.getCountry());
        assertEquals(1, mappedActor.getId());
    }
    @Test
    public void shouldMapActorToActorDtoWithMoviesCorrectly(){
        actor.setMovies(List.of(movie));
        ActorDtoWithMovies mappedActor = ActorMapper.mapToActorDtoWithMovies(actor, 5.0, 100);

        assertEquals("Jane", mappedActor.getFirstName());
        assertEquals("Doe", mappedActor.getLastName());
        assertEquals(LocalDate.of(1990, 1, 1), mappedActor.getDateOfBirth());
        assertEquals("Poland", mappedActor.getCountry());
        assertEquals(1, mappedActor.getMovies().size());
        assertEquals(5.0, mappedActor.getRating());
        assertEquals(100, mappedActor.getRateNumber());
    }
    @Test
    public void shouldMapActorUpdateDtoToActorCorrectly(){
        ActorUpdateDto actorDto = ActorUpdateDto.builder().firstName("Joe").build();
        Actor mappedActor = ActorMapper.mapToActorUpdate(actorDto, actor);

        assertEquals("Joe", mappedActor.getFirstName(), "should update first name");
        assertEquals("Doe", mappedActor.getLastName(), "should not update last name");
        assertEquals(LocalDate.of(1990, 1, 1), mappedActor.getDateOfBirth(), "should not update date of birth");
        assertEquals("Poland", mappedActor.getCountry(), "should not update country");
    }
    @Test
    public void shouldMapActorToActorSummaryDtoCorrectly(){
        ActorSummaryDto mappedActor = ActorMapper.mapToActorSummary(actor);

        assertEquals("Jane Doe", mappedActor.getFullName());
    }
    @Test
    public void shouldMapActorToActorUpdateVaadinCorrectly(){
        Actor mappedActor = ActorMapper.mapToActorUpdateVaadin(actorDto, actor);

        assertEquals("Joe", mappedActor.getFirstName());
    }
    @Test
    public void shouldMapActorToActorDtoWithUserRateCorrectly(){
        ActorRate actorRate = ActorRate.builder().actorRateId(1L).rate(4).actor(new Actor()).build();

        ActorDtoWithUserRate mappedActor = ActorMapper.mapToActorDtoWithUserRate(actorRate);

        assertEquals(4, mappedActor.getUserRate());
    }
    @Test
    public void shouldMapToActorDtoWithAverageRateCorrectly(){
        ActorDtoWithUserRate mappedActor = ActorMapper.mapToActorDtoWithAverageRate(actor, 2.5);

        assertEquals(2.5, mappedActor.getAverageRate(), 0.01);
    }
}
