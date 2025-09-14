package pl.patrykkukula.MovieReviewPortal.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtils{

    @Value("${SECRET_KEY}")
    private String secretKey;
    private final UserDetailsServiceImpl userDetailsService;

    public String generateJwtToken(){
        SecretKey secret = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        UserDetails authenticatedUser = userDetailsService.getAuthenticatedUser();

        return Jwts.builder()
                .issuedAt(new Date())
                .subject(authenticatedUser.getUsername())
                .claim("authorities", authenticatedUser.getAuthorities().stream().map((GrantedAuthority::getAuthority)).collect(Collectors.joining(",")))
                .expiration(new Date(new Date().getTime() + 900000))
                .signWith(secret)
                .compact();
    }
    public boolean validateJwtToken(String token){
        try {
            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token.substring(7));
            return true;
        } catch (SecurityException ex) {
            log.error("Invalid JWT signature");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty");
        }
        return false;
    }
    public String getUsernameFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }
    public List<GrantedAuthority> getAuthoritiesFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String authorities = claims.get("authorities", String.class);
        return AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);
    }
}
