package pl.patrykkukula.MovieReviewPortal.Exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import pl.patrykkukula.MovieReviewPortal.Dto.Response.ErrorResponseDto;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

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
    @ExceptionHandler(IllegalResourceModifyException.class)
    private ResponseEntity<ErrorResponseDto> handleIllegalResourceModifyException(IllegalResourceModifyException ex, WebRequest request) {
        String path = request.getDescription(false);
        LocalDateTime occurrenceTime = now();
        String errorMessage = ex.getMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto(path, STATUS_400, STATUS_400_MESSAGE, errorMessage, occurrenceTime));
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
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseDto> handleMissingRequestParameterException(MissingServletRequestParameterException ex, WebRequest request) {
        String path = request.getDescription(false);
        LocalDateTime occurrenceTime = now();
        String errorMessage = ex.getMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(path, STATUS_400, STATUS_400_MESSAGE, errorMessage, occurrenceTime));
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDto> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        String path = request.getDescription(false);
        LocalDateTime occurrenceTime = now();
        String errorMessage = ex.getMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(path, STATUS_400, STATUS_400_MESSAGE, errorMessage, occurrenceTime));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(field -> field.getField() + ": " + field.getDefaultMessage())
                .collect(Collectors.joining(", "));


        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto(
                        request.getDescription(false),
                        "400",
                        "Bad Request",
                        errorMessage,
                        LocalDateTime.now()
                ));
    }
}
