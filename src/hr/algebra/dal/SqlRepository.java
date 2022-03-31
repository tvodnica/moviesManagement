/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.dal;

import hr.algebra.model.Actor;
import hr.algebra.model.Genre;
import hr.algebra.model.Movie;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;

/**
 *
 * @author Tomo
 */
public class SqlRepository implements Repository {

    private static final String FIRST_NAME = "Name";
    private static final String LAST_NAME = "LastName";

    private static final String ID_MOVIE = "IDMovie";
    private static final String TITLE = "title";
    private static final String PUBLISHED_DATE = "publishedDate";
    private static final String DESCRIPTION = "description";
    private static final String ORIGINAL_NAME = "originalName";
    private static final String DURATION = "duration";
    private static final String PICTURE_PATH = "picturePath";

    private static final String CREATE_USER = "{ CALL createUser (?,?) }";
    private static final String LOGIN = "{ CALL login (?,?,?) }";

    private static final String UPLOAD_MOVIE = "{ CALL uploadMovie (?,?,?,?,?,?,?) }";
    private static final String UPLOAD_MOVIE_ACTORS = "{ CALL uploadMovieActors (?,?,?) }";
    private static final String UPLOAD_MOVIE_DIRECTORS = "{ CALL uploadMovieDirectors (?,?,?) }";
    private static final String UPLOAD_MOVIE_GENRES = "{ CALL uploadMovieGenres (?,?) }";

    private static final String DELETE_ALL_MOVIES = "{ CALL deleteAllMovies () }";
    private static final String DELETE_MOVIE = "{ CALL deleteMovie (?) }";

    private static final String UPDATE_MOVIE = "{ CALL updateMovie (?,?,?,?,?,?,?) }";
    private static final String UPDATE_MOVIE_ACTORS = "{ CALL updateMovieActors (?,?,?) }";
    private static final String UPDATE_MOVIE_DIRECTORS = "{ CALL updateMovieDirectors (?,?,?) }";
    private static final String UPDATE_MOVIE_GENRES = "{ CALL updateMovieGenres (?,?) }";

    private static final String GET_ALL_ACTORS = "{ CALL getAllActors () }";
    private static final String GET_ALL_GENRES = "{ CALL getAllGenres () }";
    private static final String GET_ALL_MOVIES = "{ CALL getAllMovies () }";
    private static final String GET_MOVIE_ACTORS = "{ CALL getMovieActors (?) }";
    private static final String GET_MOVIE_DIRECTORS = "{ CALL getMovieDirectors (?) }";
    private static final String GET_MOVIE_GENRES = "{ CALL getMovieGenres (?) }";

    //-------------------USER MANAGEMENT----------------------
    @Override
    public void createUser(String username, String password) throws SQLException {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(CREATE_USER)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            stmt.executeUpdate();
        }
    }

    @Override
    public String logIn(String username, String password) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(LOGIN)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            stmt.registerOutParameter(3, Types.NVARCHAR);

            stmt.executeUpdate();
            return stmt.getString(3);
        }
    }

    //----------------------SELECT----------------------
    @Override
    public List<Actor> getAllActors() throws SQLException {
        List<Actor> actors = new ArrayList<>();
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(GET_ALL_ACTORS);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                actors.add(new Actor(rs.getString(FIRST_NAME), rs.getString(LAST_NAME)));
            }
        }
        return actors;
    }

    @Override
    public List<Movie> getAllMovies() throws Exception {
        List<Movie> allMovies = new ArrayList<>();
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt_movies = con.prepareCall(GET_ALL_MOVIES);
                CallableStatement stmt_actors = con.prepareCall(GET_MOVIE_ACTORS);
                CallableStatement stmt_directors = con.prepareCall(GET_MOVIE_DIRECTORS);
                CallableStatement stmt_genres = con.prepareCall(GET_MOVIE_GENRES)) {
            ResultSet rs = stmt_movies.executeQuery();
            while (rs.next()) {
                allMovies.add(
                        new Movie(
                                rs.getInt(ID_MOVIE),
                                rs.getString(TITLE),
                                LocalDateTime.parse(rs.getString(PUBLISHED_DATE)),
                                rs.getString(DESCRIPTION),
                                rs.getString(ORIGINAL_NAME),
                                rs.getInt(DURATION),
                                rs.getString(PICTURE_PATH)
                        ));
            }

            for (Movie movie : allMovies) {
                stmt_actors.setInt(1, movie.getId());
                stmt_directors.setInt(1, movie.getId());
                stmt_genres.setInt(1, movie.getId());

                List<Actor> actors = new ArrayList<>();
                List<Actor> directors = new ArrayList<>();
                List<Genre> genres = new ArrayList<>();

                ResultSet rs_actors = stmt_actors.executeQuery();
                while (rs_actors.next()) {
                    actors.add(new Actor(rs_actors.getString(FIRST_NAME), rs_actors.getString(LAST_NAME)));
                }
                ResultSet rs_directors = stmt_directors.executeQuery();
                while (rs_directors.next()) {
                    directors.add(new Actor(rs_directors.getString(FIRST_NAME), rs_directors.getString(LAST_NAME)));
                }
                ResultSet rs_genres = stmt_genres.executeQuery();
                while (rs_genres.next()) {
                    genres.add(new Genre(rs_genres.getString(FIRST_NAME)));
                }

                movie.setActors(actors);
                movie.setDirectors(directors);
                movie.setGenres(genres);
            }
        }

        return allMovies;
    }

    @Override
    public List<Genre> getAllGenres() throws Exception {
        List<Genre> genres = new ArrayList<>();
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(GET_ALL_GENRES);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                genres.add(new Genre(rs.getString(FIRST_NAME)));
            }
        }
        return genres;
    }

    //----------------------UPLOAD----------------------
    @Override
    public int uploadMovie(Movie movie) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement upload_movie = con.prepareCall(UPLOAD_MOVIE);
                CallableStatement upload_actors = con.prepareCall(UPLOAD_MOVIE_ACTORS);
                CallableStatement upload_directors = con.prepareCall(UPLOAD_MOVIE_DIRECTORS);
                CallableStatement upload_genres = con.prepareCall(UPLOAD_MOVIE_GENRES);) {

            upload_movie.setString(1, movie.getTitle());
            upload_movie.setString(2, movie.getPublishedDate().toString());
            upload_movie.setString(3, movie.getDescription());
            upload_movie.setString(4, movie.getOriginalName());
            upload_movie.setInt(5, movie.getDuration());
            upload_movie.setString(6, movie.getPicturePath());
            upload_movie.registerOutParameter(7, Types.INTEGER);

            upload_movie.executeUpdate();

                for (Actor actor : movie.getActors()) {
                    upload_actors.setInt(1, upload_movie.getInt(7));
                    upload_actors.setString(2, actor.getFirstName());
                    upload_actors.setString(3, actor.getLastName());
                    upload_actors.executeUpdate();

                }
                    for (Actor director : movie.getDirectors()) {
                        upload_directors.setInt(1, upload_movie.getInt(7));
                        upload_directors.setString(2, director.getFirstName());
                        upload_directors.setString(3, director.getLastName());
                        upload_directors.executeUpdate();
                }
                    for (Genre genre : movie.getGenres()) {
                        upload_genres.setInt(1, upload_movie.getInt(7));
                        upload_genres.setString(2, genre.getName());
                        upload_genres.executeUpdate();
                    }
                    return upload_movie.getInt(7);
                }
            }

            @Override
            public void uploadMovies
            (List<Movie> movies) throws Exception {
                DataSource dataSource = DataSourceSingleton.getInstance();
                try (Connection con = dataSource.getConnection();
                        CallableStatement stmt = con.prepareCall(UPLOAD_MOVIE);
                        CallableStatement upload_actors = con.prepareCall(UPLOAD_MOVIE_ACTORS);
                        CallableStatement upload_directors = con.prepareCall(UPLOAD_MOVIE_DIRECTORS);
                        CallableStatement upload_genres = con.prepareCall(UPLOAD_MOVIE_GENRES);) {
                    for (Movie movie : movies) {
                        stmt.setString(1, movie.getTitle());
                        stmt.setString(2, movie.getPublishedDate().toString());
                        stmt.setString(3, movie.getDescription());
                        stmt.setString(4, movie.getOriginalName());
                        stmt.setInt(5, movie.getDuration());
                        stmt.setString(6, movie.getPicturePath());
                        stmt.registerOutParameter(7, Types.INTEGER);

                        stmt.executeUpdate();

                        if (movie.getActors() != null && !movie.getActors().isEmpty()) {
                            for (Actor actor : movie.getActors()) {
                                upload_actors.setInt(1, stmt.getInt(7));
                                upload_actors.setString(2, actor.getFirstName());
                                upload_actors.setString(3, actor.getLastName());
                                upload_actors.executeUpdate();
                            }
                        }
                        if (movie.getDirectors() != null && !movie.getDirectors().isEmpty()) {
                            for (Actor director : movie.getDirectors()) {
                                upload_directors.setInt(1, stmt.getInt(7));
                                upload_directors.setString(2, director.getFirstName());
                                upload_directors.setString(3, director.getLastName());
                                upload_directors.executeUpdate();
                            }
                        }
                        if (movie.getGenres() != null && !movie.getGenres().isEmpty()) {
                            for (Genre genre : movie.getGenres()) {
                                upload_genres.setInt(1, stmt.getInt(7));
                                upload_genres.setString(2, genre.getName());
                                upload_genres.executeUpdate();
                            }
                        }
                    }
                }
            }

            //----------------------DELETE----------------------
            @Override
            public List<String> deleteMovies() throws Exception {
                DataSource dataSource = DataSourceSingleton.getInstance();
                List<String> allPicturePaths = new ArrayList<>();
                try (Connection con = dataSource.getConnection();
                        CallableStatement stmt = con.prepareCall(DELETE_ALL_MOVIES)) {
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        allPicturePaths.add(rs.getString(PICTURE_PATH));
                    }
                }
                return allPicturePaths;
            }

            @Override
            public void deleteMovie
            (Integer selectedMovieId) throws Exception {
                DataSource dataSource = DataSourceSingleton.getInstance();
                try (Connection con = dataSource.getConnection();
                        CallableStatement stmt = con.prepareCall(DELETE_MOVIE)) {
                    stmt.setInt(1, selectedMovieId);
                    stmt.executeUpdate();
                }
            }

            //----------------------UPDATE----------------------
            @Override
            public void updateMovie
            (int movieId, Movie movie
            , Set<Actor> actors, Set<Actor > directors
            , Set<Genre> genres) throws Exception {
                DataSource dataSource = DataSourceSingleton.getInstance();
                try (Connection con = dataSource.getConnection();
                        CallableStatement cs_movie = con.prepareCall(UPDATE_MOVIE);
                        CallableStatement cs_actors = con.prepareCall(UPDATE_MOVIE_ACTORS);
                        CallableStatement cs_directors = con.prepareCall(UPDATE_MOVIE_DIRECTORS);
                        CallableStatement cs_genres = con.prepareCall(UPDATE_MOVIE_GENRES)) {

                    cs_movie.setInt(1, movieId);
                    cs_movie.setString(2, movie.getTitle());
                    cs_movie.setString(3, movie.getPublishedDate().toString());
                    cs_movie.setString(4, movie.getDescription());
                    cs_movie.setString(5, movie.getOriginalName());
                    cs_movie.setInt(6, movie.getDuration());
                    cs_movie.setString(7, movie.getPicturePath());

                    cs_movie.executeUpdate();

                    for (Actor actor : actors) {
                        cs_actors.setInt(1, movieId);
                        cs_actors.setString(2, actor.getFirstName());
                        cs_actors.setString(3, actor.getLastName());

                        cs_actors.executeUpdate();
                    }
                    for (Actor actor : directors) {
                        cs_directors.setInt(1, movieId);
                        cs_directors.setString(2, actor.getFirstName());
                        cs_directors.setString(3, actor.getLastName());

                        cs_directors.executeUpdate();
                    }
                    for (Genre genre : genres) {
                        cs_genres.setInt(1, movieId);
                        cs_genres.setString(2, genre.getName());

                        cs_genres.executeUpdate();
                    }

                }
            }

        }
