package pl.patrykkukula.MovieReviewPortal.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.patrykkukula.MovieReviewPortal.Constants.MovieCategory;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.*;
import pl.patrykkukula.MovieReviewPortal.Model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DirectorMapperTest {
    private DirectorDto directorDto;
    private Director director;
    private DirectorUpdateDto directorUpdateDto;
    private Movie movie;

    @BeforeEach
    public void setUp(){
        directorDto = DirectorDto.builder()
                .firstName("Joe")
                .lastName("Doe")
                .country("Poland").dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();
        director = Director.builder()
                .directorId(1L)
                .firstName("Jane")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990,1,1))
                .country("Poland")
                .build();
        directorUpdateDto = DirectorUpdateDto.builder()
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
    public void shouldMapDirectorDtoToDirectorCorrectly(){
        Director mappedDirector = DirectorMapper.mapToDirector(directorDto);

        assertEquals("Joe", mappedDirector.getFirstName());
        assertEquals("Doe", mappedDirector.getLastName());
        assertEquals(LocalDate.of(1990, 1, 1), mappedDirector.getDateOfBirth());
        assertEquals("Poland", mappedDirector.getCountry());
    }
    @Test
    public void shouldMapDirectorToDirectorDtoCorrectly(){
        DirectorDto mappedDirector = DirectorMapper.mapToDirectorDto(director);

        assertEquals("Jane", mappedDirector.getFirstName());
        assertEquals("Doe", mappedDirector.getLastName());
        assertEquals(LocalDate.of(1990, 1, 1), mappedDirector.getDateOfBirth());
        assertEquals("Poland", mappedDirector.getCountry());
        assertEquals(1, mappedDirector.getId());
    }
    @Test
    public void shouldMapDirectorToActorViewDtoCorrectly(){
        director.setDirectorRates(new ArrayList<>());
        DirectorViewDto mappedDirector = DirectorMapper.mapToDirectorViewDto(director);

        assertEquals("Jane", mappedDirector.getFirstName());
        assertEquals("Doe", mappedDirector.getLastName());
        assertEquals(LocalDate.of(1990, 1, 1), mappedDirector.getDateOfBirth());
        assertEquals("Poland", mappedDirector.getCountry());
        assertEquals(1, mappedDirector.getId());
    }
    @Test
    public void shouldMapDirectorToDirectorDtoWithMoviesCorrectly(){
        director.setMovies(List.of(movie));
        DirectorDtoWithMovies mappedDirector = DirectorMapper.mapToDirectorDtoWithMovies(director, 5.0, 100);

        assertEquals("Jane", mappedDirector.getFirstName());
        assertEquals("Doe", mappedDirector.getLastName());
        assertEquals(LocalDate.of(1990, 1, 1), mappedDirector.getDateOfBirth());
        assertEquals("Poland", mappedDirector.getCountry());
        assertEquals(1, mappedDirector.getMovies().size());
        assertEquals(5.0, mappedDirector.getRating());
        assertEquals(100, mappedDirector.getRateNumber());
    }
    @Test
    public void shouldMapDirectorUpdateDtoToDirectorCorrectly(){
        DirectorUpdateDto directorDto = DirectorUpdateDto.builder().firstName("Joe").build();
        Director mappedDirector = DirectorMapper.mapToDirectorUpdate(directorDto, director);

        assertEquals("Joe", mappedDirector.getFirstName(), "should update first name");
        assertEquals("Doe", mappedDirector.getLastName(), "should not update last name");
        assertEquals(LocalDate.of(1990, 1, 1), mappedDirector.getDateOfBirth(), "should not update date of birth");
        assertEquals("Poland", mappedDirector.getCountry(), "should not update country");
    }
    @Test
    public void shouldMapToDirectorUpdateVaadinCorrectly(){
        Director mappedDirector = DirectorMapper.mapToDirectorUpdateVaadin(directorDto, director);

        assertEquals("Joe", mappedDirector.getFirstName());
    }
    @Test
    public void shouldMapDirectorToDirectorSummaryDtoCorrectly(){
        DirectorSummaryDto mappedDirector = DirectorMapper.mapToDirectorSummary(director);

        assertEquals("Jane Doe", mappedDirector.getFullName());
    }
    @Test
    public void shouldMapDirectorToDirectorDtoWithUserRateCorrectly(){
        DirectorRate directorRate = DirectorRate.builder().directorRateId(1L).rate(4).director(new Director()).build();

        DirectorDtoWithUserRate mappedDirector = DirectorMapper.mapToDirectorDtoWithAverageRate(directorRate);

        assertEquals(4, mappedDirector.getUserRate());
    }
    @Test
    public void shouldMapDirectorToDirectorDtoWithAverageRateCorrectly(){
        DirectorDtoWithUserRate mappedDirector = DirectorMapper.mapToDirectorDtoWithAverageRate(director, 2.5);

        assertEquals(2.5, mappedDirector.getAverageRate(), 0.01);
    }
}
