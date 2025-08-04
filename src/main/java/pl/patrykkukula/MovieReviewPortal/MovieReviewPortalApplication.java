package pl.patrykkukula.MovieReviewPortal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "AuditorAwareImpl")
@EnableMethodSecurity
public class MovieReviewPortalApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovieReviewPortalApplication.class, args);
	}
}
