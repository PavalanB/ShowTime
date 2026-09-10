package com.cinemesh.soap.dto;

import jakarta.xml.bind.annotation.*;
import java.util.List;

public class MovieSoapDto {
    public static final String NAMESPACE_URI = "http://cinemesh.com/ws/movie";

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class MovieDto {
        private Long movieId;
        private String title;
        private String description;
        private int durationMinutes;
        private String genre;
        private String language;
        private String posterUrl;
        private String rating;

        public Long getMovieId() { return movieId; }
        public void setMovieId(Long movieId) { this.movieId = movieId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public int getDurationMinutes() { return durationMinutes; }
        public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
        public String getGenre() { return genre; }
        public void setGenre(String genre) { this.genre = genre; }
        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }
        public String getPosterUrl() { return posterUrl; }
        public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }
        public String getRating() { return rating; }
        public void setRating(String rating) { this.rating = rating; }
    }

    @XmlRootElement(name = "AddMovieRequest", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class AddMovieRequest {
        private String title;
        private String description;
        private int durationMinutes;
        private String genre;
        private String language;
        private String posterUrl;
        private String rating;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public int getDurationMinutes() { return durationMinutes; }
        public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
        public String getGenre() { return genre; }
        public void setGenre(String genre) { this.genre = genre; }
        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }
        public String getPosterUrl() { return posterUrl; }
        public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }
        public String getRating() { return rating; }
        public void setRating(String rating) { this.rating = rating; }
    }

    @XmlRootElement(name = "AddMovieResponse", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class AddMovieResponse {
        private String status;
        private MovieDto movie;
        private String message;

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public MovieDto getMovie() { return movie; }
        public void setMovie(MovieDto movie) { this.movie = movie; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    @XmlRootElement(name = "GetAllMoviesRequest", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class GetAllMoviesRequest {}

    @XmlRootElement(name = "GetAllMoviesResponse", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class GetAllMoviesResponse {
        @XmlElement(name = "movies")
        private List<MovieDto> movies;

        public List<MovieDto> getMovies() { return movies; }
        public void setMovies(List<MovieDto> movies) { this.movies = movies; }
    }
}
