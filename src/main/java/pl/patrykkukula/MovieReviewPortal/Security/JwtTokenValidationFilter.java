package pl.patrykkukula.MovieReviewPortal.Security;

import com.google.common.net.HttpHeaders;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.patrykkukula.MovieReviewPortal.Utils.JwtUtils;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenValidationFilter extends OncePerRequestFilter {
    @Value("${SECRET_KEY}")
    private String secretKey;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (token != null && jwtUtils.validateJwtToken(token)) {
                String jwt = token.substring(7).trim();
                String username = jwtUtils.getUsernameFromToken(jwt);
                List<GrantedAuthority> grantedAuthorities = jwtUtils.getAuthoritiesFromToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, grantedAuthorities);

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            log.info("Jwt validation failed:{} ", ex.getMessage());
            throw new AuthenticationServiceException("Invalid Jwt Token", ex);
        }
    }
}
