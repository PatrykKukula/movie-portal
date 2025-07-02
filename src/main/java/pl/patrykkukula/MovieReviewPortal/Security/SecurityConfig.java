package pl.patrykkukula.MovieReviewPortal.Security;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.patrykkukula.MovieReviewPortal.Exception.AccessDeniedHandlerImpl;
import pl.patrykkukula.MovieReviewPortal.Exception.AuthEntryPointImpl;
import pl.patrykkukula.MovieReviewPortal.View.Account.LoginView;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig extends VaadinWebSecurity {
    private static final String LOGOUT_URL = "/movies";

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.securityContext(scc -> scc.requireExplicitSave(false));
                http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

        http.authorizeHttpRequests(request ->
            request.requestMatchers("/movies/{movieId}/rate", "/movies/rate").authenticated()
                    .requestMatchers("/actors/add", "actors/edit").hasRole("ADMIN")
                    .requestMatchers("/directors/add", "/directors/edit").hasRole("ADMIN")
                    .requestMatchers("/movies/add", "movies/edit").hasRole("ADMIN")
                    .requestMatchers("/topics/add", "topics/edit").authenticated()
                    .requestMatchers("/comments/add", "comments/edit").authenticated()
                    .requestMatchers("/login/**", "/logout/**", "/register", "/verify", "/reset").permitAll()
                    .requestMatchers(HttpMethod.GET).permitAll()
        );
        http.exceptionHandling(ehc -> {
            ehc.accessDeniedHandler(new AccessDeniedHandlerImpl());
            ehc.authenticationEntryPoint(new AuthEntryPointImpl());
        });

        super.configure(http);
        setLoginView(http, LoginView.class);
        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl(LOGOUT_URL));
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
