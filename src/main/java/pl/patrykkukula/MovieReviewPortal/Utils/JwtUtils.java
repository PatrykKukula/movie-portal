package pl.patrykkukula.MovieReviewPortal.Utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtils{

    @Value("${jwt.secret}")
    private String secretKey;

    public String generateJwtToken(String email, List<? extends GrantedAuthority> authorities){
        SecretKey secret = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .issuedAt(new Date())
                .subject(email)
                .claim("authorities",authorities.stream().map((authority -> authority.getAuthority())).collect(Collectors.joining(",")))
                .expiration(new Date(new Date().getTime() + 900000))
                .signWith(secret)
                .compact();
    }
    public boolean validateJwtToken(String jwt){
        try {
            parseToken(jwt);
            return true;
        } catch (SecurityException ex) {
            log.error("Invalid JWT signature: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty: {}", ex.getMessage());
        } catch (Exception ex) {
            log.error("JWT token validation error: {}", ex.getMessage());
        }
        return false;
    }
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }
    public List<GrantedAuthority> getAuthoritiesFromToken(String token) {
        Claims claims = parseToken(token);

        String authorities = claims.get("authorities", String.class);
        return AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);
    }
    private Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
