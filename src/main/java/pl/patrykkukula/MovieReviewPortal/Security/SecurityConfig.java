package pl.patrykkukula.MovieReviewPortal.Security;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import pl.patrykkukula.MovieReviewPortal.Exception.AccessDeniedHandlerImpl;
import pl.patrykkukula.MovieReviewPortal.Exception.AuthEntryPointImpl;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {


    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.securityContext(scc -> scc.requireExplicitSave(false));
        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(request ->
            request.requestMatchers("/auth/**").permitAll()
                    .requestMatchers(HttpMethod.GET).permitAll()
                    .requestMatchers("/movies/{movieId}/rate", "movies/rate").authenticated()
                    .requestMatchers("/actors/**").hasRole("ADMIN")
                    .requestMatchers("/directors/**").hasRole("ADMIN")
                    .requestMatchers("/movies/**").hasRole("ADMIN")
                    .requestMatchers("/topics/**").hasRole("ADMIN")
                    .requestMatchers("/comments/**").authenticated()

        );
        http.exceptionHandling(ehc -> {
            ehc.accessDeniedHandler(new AccessDeniedHandlerImpl());
            ehc.authenticationEntryPoint(new AuthEntryPointImpl());
        });
        http.httpBasic(withDefaults());
        http.formLogin(form -> form.loginPage("/login").permitAll());
        return http.build();
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
