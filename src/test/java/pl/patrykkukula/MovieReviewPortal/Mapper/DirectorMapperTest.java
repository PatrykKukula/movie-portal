package pl.patrykkukula.MovieReviewPortal.Mapper;
import org.junit.jupiter.api.Test;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorDtoWithMovies;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorUpdateDto;
import pl.patrykkukula.MovieReviewPortal.Model.Director;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DirectorMapperTest {
    @Test
    public void shouldMapActorDtoToActorCorrectly(){
        DirectorDto directorDto = DirectorDto.builder().firstName("Jane").lastName("Doe")
                .country("Poland").dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();

        Director director = DirectorMapper.mapToDirector(directorDto);

        assertEquals("Jane", director.getFirstName());
        assertEquals("Doe", director.getLastName());
        assertEquals(LocalDate.of(1990, 1, 1), director.getDateOfBirth());
        assertEquals("Poland", director.getCountry());
    }
    @Test
    public void shouldMapActorToActorDtoCorrectly(){
        Director director = createDirector();

        DirectorDto directorDto = DirectorMapper.mapToDirectorDto(director);

        assertEquals("Jane", directorDto.getFirstName());
        assertEquals("Doe", directorDto.getLastName());
        assertEquals(LocalDate.of(1990, 1, 1), directorDto.getDateOfBirth());
        assertEquals("Poland", directorDto.getCountry());
        assertEquals(1, directorDto.getId());
    }
    @Test
    public void shouldMapActorToActorDtoWithMoviesCorrectly(){
        Director director = createDirector();

        DirectorDtoWithMovies directorDto = DirectorMapper.mapToDirectorDtoWithMovies(director);

        assertEquals("Jane", directorDto.getFirstName());
        assertEquals("Doe", directorDto.getLastName());
        assertEquals(LocalDate.of(1990, 1, 1), directorDto.getDateOfBirth());
        assertEquals("Poland", directorDto.getCountry());
        assertEquals(0, directorDto.getMovies().size());
    }
    @Test
    public void shouldMapActorUpdateDtoToActorCorrectly(){
        DirectorUpdateDto directorDto = DirectorUpdateDto.builder().firstName("Joe").build();

        Director director = DirectorMapper.mapToDirectorUpdate(directorDto, createDirector());

        assertEquals("Joe", director.getFirstName(), "should update first name");
        assertEquals("Doe", director.getLastName(), "should not update last name");
        assertEquals(LocalDate.of(1990, 1, 1), director.getDateOfBirth(), "should not update date of birth");
        assertEquals("Poland", director.getCountry(), "should not update country");
    }
    private Director createDirector(){
        return Director.builder()
                .directorId(1L)
                .firstName("Jane")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990,1,1))
                .country("Poland")
                .build();
    }
}
