package pl.patrykkukula.MovieReviewPortal.Utils;
import pl.patrykkukula.MovieReviewPortal.Exception.InvalidIdException;
import java.util.function.Consumer;
import java.util.function.Supplier;
import static java.util.Optional.ofNullable;

public class ServiceUtils {

    public static void validateId(Long id) {
        if (id == null || id < 1) throw new InvalidIdException();
    }

    public static String validateSorting(String sorting) {
        return (sorting != null && (sorting.equalsIgnoreCase("ASC") || sorting.equalsIgnoreCase("DESC"))) ? sorting : "ASC";
    }
    public static <T> void updateField(Supplier<T> field, Consumer<T> update) {
        String value =  (String)field.get();
            if (value != null && !value.trim().isEmpty()) update.accept(field.get());
        }
    public static <T> void updateDateField(Supplier<T> field, Consumer<T> update) {
        ofNullable(field.get()).ifPresent(update);
    }
}
