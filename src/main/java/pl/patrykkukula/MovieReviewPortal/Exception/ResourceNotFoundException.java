package pl.patrykkukula.MovieReviewPortal.Exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, String field, String value) {
        super(String.format("Resource %s not found for field %s and value %s", resource, field, value));
    }
}
