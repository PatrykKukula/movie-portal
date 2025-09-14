package pl.patrykkukula.MovieReviewPortal.Exception;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import pl.patrykkukula.MovieReviewPortal.Dto.Response.ErrorResponseDto;
import java.io.IOException;
import java.time.LocalDateTime;
import static jakarta.servlet.http.HttpServletResponse.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static pl.patrykkukula.MovieReviewPortal.Constants.ResponseConstants.*;

public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(SC_FORBIDDEN);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        String message = (accessDeniedException!=null && accessDeniedException.getMessage()!=null) ? accessDeniedException.getMessage() : "Unauthorized";
        String path = request.getRequestURI();
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                "Path: " + path,
                "Status code: " + STATUS_403,
                "Status message: " + STATUS_403_MESSAGE,
                "Error message: " + message,
                LocalDateTime.now());

        new ObjectMapper().writeValue(response.getOutputStream(), errorResponse);
    }
}
