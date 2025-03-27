package pl.patrykkukula.MovieReviewPortal.Exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import pl.patrykkukula.MovieReviewPortal.Dto.ErrorResponseDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static pl.patrykkukula.MovieReviewPortal.Constants.ResponseConstants.*;

public class AccesDeniadHandlerImpl implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        String message = (accessDeniedException!=null && accessDeniedException.getMessage()!=null) ? accessDeniedException.getMessage() : "Unauthorized";
        String path = request.getRequestURI();
        LocalDateTime occurrenceAt = LocalDateTime.now();
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                "Path: " + path,
                "Status code: " + STATUS_403,
                "Status message: " + STATUS_403_MESSAGE,
                "Error message: " + message,
                occurrenceAt);

        response.getWriter().write(errorResponse.toString());
    }
}
