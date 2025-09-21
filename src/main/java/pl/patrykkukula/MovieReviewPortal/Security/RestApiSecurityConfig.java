package pl.patrykkukula.MovieReviewPortal.Security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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
                            .requestMatchers(HttpMethod.GET, "/api/user/**").permitAll()
                            .requestMatchers(HttpMethod.POST, "/api/actors", "/api/movies", "/api/directors", "/api/user/**",
                                    "/api/movies/*/add-actor/*", "/api/movies/*/remove-actor/*"
                            ).hasAnyRole("ADMIN", "MODERATOR")
                            .requestMatchers(HttpMethod.POST,"/api/topics/**", "/api/comments/**").authenticated()
                            .requestMatchers(HttpMethod.DELETE, "/api/user/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.DELETE, "/api/actors/*", "/api/movies/*", "/api/directors/*", "/api/user/**"
                           ).hasAnyRole("ADMIN", "MODERATOR")
                            .requestMatchers(HttpMethod.DELETE ,"/api/topics/*", "/api/comments/*").authenticated()
                            .requestMatchers(HttpMethod.GET, "/api/actors/**", "/api/movies/**", "/api/directors/**", "/api/user/**"
                            , "/api/topics/**", "/api/comments/**").permitAll()
                            .requestMatchers(HttpMethod.PATCH, "/api/actors/*", "/api/movies/*", "/api/directors/*").hasAnyRole("ADMIN", "MODERATOR")
                            .requestMatchers(HttpMethod.PATCH, "/api/topics/*", "/api/comments/*").authenticated()
                            .requestMatchers("/api/actors/rate/**", "/api/directors/rate/**", "/api/movies/rate/**").authenticated()
                            .requestMatchers("/api/movies/*/**", "/api/user/remove-role", "/api/user/ban", "/api/user/remove-ban").hasRole("ADMIN")
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

