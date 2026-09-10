package com.cinemesh.soap.endpoint;

import com.cinemesh.model.Movie;
import com.cinemesh.service.MovieService;
import com.cinemesh.soap.dto.MovieSoapDto.*;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.List;
import java.util.stream.Collectors;

@Endpoint
public class MovieEndpoint {

    private static final String NAMESPACE_URI = "http://cinemesh.com/ws/movie";
    private final MovieService movieService;

    public MovieEndpoint(MovieService movieService) {
        this.movieService = movieService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AddMovieRequest")
    @ResponsePayload
    public AddMovieResponse addMovie(@RequestPayload AddMovieRequest request) {
        AddMovieResponse response = new AddMovieResponse();
        try {
            Movie movie = movieService.addMovie(
                    request.getTitle(), request.getDescription(), request.getDurationMinutes(),
                    request.getGenre(), request.getLanguage(), request.getPosterUrl(), request.getRating()
            );
            response.setStatus("SUCCESS");
            response.setMovie(mapMovie(movie));
            response.setMessage("Movie added successfully");
        } catch (Exception e) {
            response.setStatus("FAILURE");
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetAllMoviesRequest")
    @ResponsePayload
    public GetAllMoviesResponse getAllMovies(@RequestPayload GetAllMoviesRequest request) {
        GetAllMoviesResponse response = new GetAllMoviesResponse();
        List<MovieDto> movies = movieService.getAllMovies().stream()
                .map(this::mapMovie)
                .collect(Collectors.toList());
        response.setMovies(movies);
        return response;
    }

    private MovieDto mapMovie(Movie m) {
        MovieDto dto = new MovieDto();
        dto.setMovieId(m.getId());
        dto.setTitle(m.getTitle());
        dto.setDescription(m.getDescription());
        dto.setDurationMinutes(m.getDurationMinutes());
        dto.setGenre(m.getGenre());
        dto.setLanguage(m.getLanguage());
        dto.setPosterUrl(m.getPosterUrl());
        dto.setRating(m.getRating());
        return dto;
    }
}
