package pl.patrykkukula.MovieReviewPortal.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import pl.patrykkukula.MovieReviewPortal.Model.UserEntity;
import pl.patrykkukula.MovieReviewPortal.Repository.UserEntityRepository;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

import static pl.patrykkukula.MovieReviewPortal.Constants.SecurityConstants.BEARER_PREFIX;
import static pl.patrykkukula.MovieReviewPortal.Constants.SecurityConstants.JWT_EXPIRATION_TIME;

@Component
public class JWTUtils {

    @Value("${SECRET_KEY}")
    private String secret;
    private final UserEntityRepository userRepository;

    public JWTUtils(UserEntityRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    private static final Logger log = LoggerFactory.getLogger(JWTUtils.class);

    public String generateJwtToken(Authentication auth) {
        SecretKey secretKey = getSecretKey();
        log.debug("Authentication name:{}", auth.getName());
        log.debug("Authentication principal:{}", auth.getPrincipal());

        return Jwts.builder().issuer("MoviePortalApp").subject("JWT Token")
                .claim("email", auth.getName())
                .claim("authorities", auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(",")))
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + JWT_EXPIRATION_TIME))
                .signWith(secretKey).compact();
    }

    public Claims validateJwtToken(String authHeader) {
        try {
            String jwtToken = authHeader.substring(BEARER_PREFIX.length());
            SecretKey secretKey = getSecretKey();
            return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(jwtToken).getPayload();
        }
        catch (Exception ex) {
            throw new BadCredentialsException("Invalid JWT Token");
        }
    }
    public void setAuthentication(Claims claims) {
        String email = claims.get("email", String.class);
        String authorities = String.valueOf(claims.get("authorities"));
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, AuthorityUtils.createAuthorityList(authorities));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
