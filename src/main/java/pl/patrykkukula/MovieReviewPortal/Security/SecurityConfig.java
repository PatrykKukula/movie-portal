package pl.patrykkukula.MovieReviewPortal.Security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import pl.patrykkukula.MovieReviewPortal.Exception.AccessDeniedHandlerImpl;
import pl.patrykkukula.MovieReviewPortal.Exception.AuthEntryPointImpl;
import pl.patrykkukula.MovieReviewPortal.Security.Filter.JWTValidationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private JWTValidationFilter jwtValidationFilter;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.securityContext(scc -> scc.requireExplicitSave(false));
        http.csrf(csrf -> csrf.disable());
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS));

        http.authorizeHttpRequests(request ->
            request.requestMatchers("/**").permitAll()
//                    .requestMatchers(HttpMethod.GET).permitAll()
//                    .requestMatchers("/movies/{movieId}/rate", "/movies/rate").authenticated()
//                    .requestMatchers("/actors/**").hasRole("ADMIN")
//                    .requestMatchers("/directors/**").hasRole("ADMIN")
//                    .requestMatchers("/movies/**").hasRole("ADMIN")
//                    .requestMatchers("/topics/**").authenticated()
//                    .requestMatchers("/comments/**").authenticated()

        );
        http.exceptionHandling(ehc -> {
            ehc.accessDeniedHandler(new AccessDeniedHandlerImpl());
            ehc.authenticationEntryPoint(new AuthEntryPointImpl());
        });
        http.addFilterBefore(jwtValidationFilter, BasicAuthenticationFilter.class);
        return http.build();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(passwordEncoder);
        authProvider.setUserDetailsService(userDetailsService);
        return authProvider;
    }
}
