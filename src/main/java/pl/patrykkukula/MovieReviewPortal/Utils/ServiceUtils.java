package pl.patrykkukula.MovieReviewPortal.Utils;
import pl.patrykkukula.MovieReviewPortal.Exception.InvalidIdException;

import java.util.Optional;
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
        Optional<T> value = ofNullable(field.get());
        if (value.isPresent()) {
            if (value.get() instanceof String s) {
                if (!s.trim().isEmpty()) update.accept(field.get());
            } else update.accept(value.get());
        }
    }
    public static <T> void updateDateField(Supplier<T> field, Consumer<T> update) {
        ofNullable(field.get()).ifPresent(update);
    }
}
