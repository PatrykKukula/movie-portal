package pl.patrykkukula.MovieReviewPortal.Exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import pl.patrykkukula.MovieReviewPortal.Dto.Response.ErrorResponseDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static pl.patrykkukula.MovieReviewPortal.Constants.ResponseConstants.STATUS_401;
import static pl.patrykkukula.MovieReviewPortal.Constants.ResponseConstants.STATUS_401_MESSAGE;

public class AuthEntryPointImpl implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(SC_UNAUTHORIZED);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        String message = authException.getMessage();

        String path = request.getRequestURI();
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                "Path: " + path,
                "Status code: " + STATUS_401,
                "Status message: " + STATUS_401_MESSAGE,
                "Error message: " + message,
                LocalDateTime.now());

        new ObjectMapper().writeValue(response.getOutputStream(), errorResponse);
    }
}
