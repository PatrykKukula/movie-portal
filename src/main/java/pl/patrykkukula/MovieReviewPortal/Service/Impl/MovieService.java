package pl.patrykkukula.MovieReviewPortal.Service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.patrykkukula.MovieReviewPortal.Dto.MovieDto;
import pl.patrykkukula.MovieReviewPortal.Repository.MovieRepository;
import pl.patrykkukula.MovieReviewPortal.Service.IMovieService;

@Service
@RequiredArgsConstructor
public class MovieService implements IMovieService {

    private final MovieRepository movieRepository;


    @Override
    public void addMovie(MovieDto movieDto) {




    }
}
