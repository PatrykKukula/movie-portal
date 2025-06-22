package pl.patrykkukula.MovieReviewPortal.Security.Filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.patrykkukula.MovieReviewPortal.Security.JWTUtils;

import java.io.IOException;

import static pl.patrykkukula.MovieReviewPortal.Constants.SecurityConstants.BEARER_PREFIX;
import static pl.patrykkukula.MovieReviewPortal.Constants.SecurityConstants.JWT_HEADER;

@Component
public class JWTValidationFilter extends OncePerRequestFilter {

    @Autowired
    private JWTUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(JWT_HEADER);
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            Claims claims = jwtUtils.validateJwtToken(authHeader);
            if (claims != null) {
                jwtUtils.setAuthentication(claims);
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getContextPath().equals("/login");
    }
}
