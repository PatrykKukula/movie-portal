package pl.patrykkukula.MovieReviewPortal.Security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import pl.patrykkukula.MovieReviewPortal.Exception.AccessDeniedHandlerImpl;
import pl.patrykkukula.MovieReviewPortal.Exception.AuthEntryPointImpl;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
@Order(1)
public class RestApiSecurityConfig {
        private final JwtTokenValidationFilter jwtTokenValidationFilter;

        @Bean
        protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.securityMatcher("/api/**");
            http.securityContext(scc -> scc.requireExplicitSave(false));
            http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
            http.csrf(csrf -> csrf.disable());

            http.authorizeHttpRequests(request ->
                    request.requestMatchers("/api/auth/**").permitAll()
                            .requestMatchers(HttpMethod.POST, "/api/actors", "/api/movies", "/api/directors", "/api/user/**"
                            ,"/api/topics/**", "/api/comments/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.DELETE, "/api/actors/*", "/api/movies/*", "/api/directors/*", "/api/user/**"
                            ,"/api/topics/*", "/api/comments/*").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.GET, "/api/actors/**", "/api/movies/**", "/api/directors/**", "/api/user/**"
                            , "/api/topics/**", "/api/comments/**").permitAll()
                            .requestMatchers(HttpMethod.PATCH, "/api/actors/*", "/api/movies/*", "/api/directors/*", "/api/topics/*"
                            , "/api/comments/*").hasRole("ADMIN")
                            .requestMatchers("/api/actors/rate/**", "/api/directors/rate/**", "/api/directors/rate").authenticated()
                            .requestMatchers("/api/movies/*/**").hasRole("ADMIN")
            );
            http.exceptionHandling(ehc -> {
                ehc.accessDeniedHandler(new AccessDeniedHandlerImpl());
                ehc.authenticationEntryPoint(new AuthEntryPointImpl());
            });
            http.httpBasic(Customizer.withDefaults());
            http.addFilterBefore(jwtTokenValidationFilter, UsernamePasswordAuthenticationFilter.class);
            return http.build();
        }
}

