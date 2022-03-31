/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.dal;

import hr.algebra.model.Actor;
import hr.algebra.model.Genre;
import hr.algebra.model.Movie;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Tomo
 */
public interface Repository {

    void createUser(String username, String password) throws Exception;

    String logIn(String username, String password) throws Exception;

    void uploadMovies(List<Movie> movies) throws Exception;

    int uploadMovie(Movie movies) throws Exception;

    List<String> deleteMovies() throws Exception;

    List<Actor> getAllActors() throws Exception;

    public List<Movie> getAllMovies() throws Exception;

    public List<Genre> getAllGenres() throws Exception;

    public void deleteMovie(Integer selectedMovieId) throws Exception;

    public void updateMovie(int movieId, Movie movie, Set<Actor> actors,  Set<Actor> directors,  Set<Genre> genres) throws Exception;

}
