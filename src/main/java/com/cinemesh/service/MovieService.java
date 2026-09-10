package com.cinemesh.service;

import com.cinemesh.model.Movie;
import com.cinemesh.repository.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public Movie addMovie(String title, String description, int durationMinutes, String genre, String language, String posterUrl, String rating) {
        Movie movie = new Movie(title, description, durationMinutes, genre, language, posterUrl, rating);
        return movieRepository.save(movie);
    }

    public Movie updateMovie(Long id, String title, String description, int durationMinutes, String genre, String language, String posterUrl, String rating) {
        return movieRepository.findById(id).map(m -> {
            m.setTitle(title);
            m.setDescription(description);
            m.setDurationMinutes(durationMinutes);
            m.setGenre(genre);
            m.setLanguage(language);
            m.setPosterUrl(posterUrl);
            m.setRating(rating);
            return movieRepository.save(m);
        }).orElseThrow(() -> new IllegalArgumentException("Movie not found"));
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Optional<Movie> getMovieById(Long movieId) {
        return movieRepository.findById(movieId);
    }

    public void deleteMovie(Long movieId) {
        movieRepository.deleteById(movieId);
    }
}
