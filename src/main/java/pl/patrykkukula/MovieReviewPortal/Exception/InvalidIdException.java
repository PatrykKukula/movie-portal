package pl.patrykkukula.MovieReviewPortal.Exception;

public class InvalidIdException extends RuntimeException {
    public InvalidIdException() {
        super("ID cannot be less than 1 or null");
    }
}
