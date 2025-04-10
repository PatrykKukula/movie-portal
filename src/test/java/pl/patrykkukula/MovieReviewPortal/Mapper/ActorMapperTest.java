package pl.patrykkukula.MovieReviewPortal.Mapper;
import org.junit.jupiter.api.Test;
import pl.patrykkukula.MovieReviewPortal.Dto.ActorDto;
import pl.patrykkukula.MovieReviewPortal.Dto.ActorDtoWithMovies;
import pl.patrykkukula.MovieReviewPortal.Dto.UpdateDto.ActorUpdateDto;
import pl.patrykkukula.MovieReviewPortal.Model.Actor;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
public class ActorMapperTest {

    @Test
    public void shouldMapActorDtoToActorCorrectly(){
        ActorDto actorDto = ActorDto.builder().firstName("Jane").lastName("Doe")
                .country("Poland").dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();

        Actor actor = ActorMapper.mapToActor(actorDto);

        assertEquals("Jane", actor.getFirstName());
        assertEquals("Doe", actor.getLastName());
        assertEquals(LocalDate.of(1990, 1, 1), actor.getDateOfBirth());
        assertEquals("Poland", actor.getCountry());
    }
    @Test
    public void shouldMapActorToActorDtoCorrectly(){
        Actor actor = createActor();

        ActorDto actorDto = ActorMapper.mapToActorDto(actor);

        assertEquals("Jane", actorDto.getFirstName());
        assertEquals("Doe", actorDto.getLastName());
        assertEquals(LocalDate.of(1990, 1, 1), actorDto.getDateOfBirth());
        assertEquals("Poland", actorDto.getCountry());
        assertEquals(1, actorDto.getId());
    }
    @Test
    public void shouldMapActorToActorDtoWithMoviesCorrectly(){
        Actor actor = createActor();

        ActorDtoWithMovies actorDto = ActorMapper.mapToActorDtoWithMovies(actor);

        assertEquals("Jane", actorDto.getFirstName());
        assertEquals("Doe", actorDto.getLastName());
        assertEquals(LocalDate.of(1990, 1, 1), actorDto.getDateOfBirth());
        assertEquals("Poland", actorDto.getCountry());
        assertEquals(0, actorDto.getMovies().size());
    }
    @Test
    public void shouldMapActorUpdateDtoToActorCorrectly(){
        ActorUpdateDto actorDto = ActorUpdateDto.builder().firstName("Joe").build();

        Actor actor = ActorMapper.mapToActorUpdate(actorDto, createActor());

        assertEquals("Joe", actor.getFirstName(), "should update first name");
        assertEquals("Doe", actor.getLastName(), "should not update last name");
        assertEquals(LocalDate.of(1990, 1, 1), actor.getDateOfBirth(), "should not update date of birth");
        assertEquals("Poland", actor.getCountry(), "should not update country");
    }

    private Actor createActor(){
        return Actor.builder()
                .actorId(1L)
                .firstName("Jane")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990,1,1))
                .country("Poland")
                .build();
    }
}
