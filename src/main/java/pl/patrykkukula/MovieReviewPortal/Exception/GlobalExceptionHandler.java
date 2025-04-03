package pl.patrykkukula.MovieReviewPortal.Exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import pl.patrykkukula.MovieReviewPortal.Dto.ErrorResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import static java.time.LocalDateTime.now;
import static pl.patrykkukula.MovieReviewPortal.Constants.ResponseConstants.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    private ResponseEntity<ErrorResponseDto> handleRuntimeException(RuntimeException ex, WebRequest request) {
        String path = request.getDescription(false);
        LocalDateTime occurrenceTime = now();
        String errorMessage = ex.getMessage();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDto(path, STATUS_500, STATUS_500_MESSAGE, errorMessage, occurrenceTime));
    }
    @ExceptionHandler(InvalidIdException.class)
    private ResponseEntity<ErrorResponseDto> handleInvalidIdException(InvalidIdException ex, WebRequest request) {
        String path = request.getDescription(false);
        LocalDateTime occurrenceTime = now();
        String errorMessage = ex.getMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto(path, STATUS_400, STATUS_400_MESSAGE, errorMessage, occurrenceTime));
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    private ResponseEntity<ErrorResponseDto> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        String path = request.getDescription(false);
        LocalDateTime occurrenceTime = now();
        String errorMessage = ex.getMessage();
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDto(path, STATUS_404, STATUS_404_MESSAGE, errorMessage, occurrenceTime));
    }
    @ExceptionHandler(IllegalArgumentException.class)
    private ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        String path = request.getDescription(false);
        LocalDateTime occurrenceTime = now();
        String errorMessage = ex.getMessage();
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDto(path, STATUS_404, STATUS_404_MESSAGE, errorMessage, occurrenceTime));
    }
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleUsernameNotFoundException(UsernameNotFoundException ex, WebRequest request) {
        String path = request.getDescription(false);
        LocalDateTime occurrenceTime = now();
        String errorMessage = ex.getMessage();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(path, STATUS_404, STATUS_404_MESSAGE, errorMessage, occurrenceTime));
    }
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalStateException(IllegalStateException ex, WebRequest request) {
        String path = request.getDescription(false);
        LocalDateTime occurrenceTime = now();
        String errorMessage = ex.getMessage();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDto(path, STATUS_500, STATUS_500_MESSAGE, errorMessage, occurrenceTime));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<List<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(field -> field.getField() + ": " + field.getDefaultMessage())
                .toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }
}
