package pl.patrykkukula.MovieReviewPortal.Utils;

import org.junit.jupiter.api.Test;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorDto;
import pl.patrykkukula.MovieReviewPortal.Exception.InvalidIdException;
import pl.patrykkukula.MovieReviewPortal.Model.Actor;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceUtilsTest {

    @Test
    public void shouldValidateIdCorrectly() {
        assertThrows(InvalidIdException.class, () -> ServiceUtils.validateId(null));
        assertThrows(InvalidIdException.class, () -> ServiceUtils.validateId(-1L));
        assertDoesNotThrow(() -> ServiceUtils.validateId(1L));
    }
    @Test
    public void shouldValidateSortingCorrectly() {
        String correctAscWithInvalidOutput = ServiceUtils.validateSorting("X");
        String correctAsc = ServiceUtils.validateSorting("ASC");
        String correctDesc = ServiceUtils.validateSorting("DESC");

        assertEquals("ASC",correctAscWithInvalidOutput);
        assertEquals("ASC",correctAsc);
        assertEquals("DESC",correctDesc);
    }
    @Test
    public void shouldUpdateFieldCorrectly() {
        Actor actor = new Actor();
        ActorDto actorDto = ActorDto.builder().firstName("name").build();
        Supplier<String> supplier = () -> actorDto.getFirstName();
        Consumer<String> consumer = newFirstName -> actor.setFirstName(newFirstName);

        ServiceUtils.updateField(supplier,consumer);
        assertEquals("name",actor.getFirstName());
    }
    @Test
    public void shouldNotUpdateFieldWithNullValues(){
        Actor actor = Actor.builder().firstName("Old name").build();

        Supplier<String> supplier = () -> null;
        Consumer<String> consumer = newFirstName -> actor.setFirstName(newFirstName);

        ServiceUtils.updateField(supplier,consumer);
        assertEquals("Old name",actor.getFirstName());
    }
}
