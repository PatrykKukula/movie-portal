package pl.patrykkukula.MovieReviewPortal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "AuditorAwareImpl")
public class MovieReviewPortalApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovieReviewPortalApplication.class, args);
	}

}
