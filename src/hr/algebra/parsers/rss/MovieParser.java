/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.parsers.rss;

import hr.algebra.factory.ParserFactory;
import hr.algebra.factory.UrlConnectionFactory;
import hr.algebra.model.Actor;
import hr.algebra.model.Genre;
import hr.algebra.model.Movie;
import hr.algebra.utils.FileUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 *
 * @author Tomo
 */
public class MovieParser {

    private static final String RSS_URL = "https://www.blitz-cinestar.hr/rss.aspx?najava=1";
    private static final String EXT = ".jpg";
    private static final String DIR = "assets";
    private static final String DEL = ", ";

    private MovieParser() {
    }

    public static List<Movie> parse() throws IOException, XMLStreamException {
        List<Movie> movies = new ArrayList<>();
        HttpURLConnection con = UrlConnectionFactory.getHttpUrlConnection(RSS_URL, 10000, "GET");
        try (InputStream is = con.getInputStream()) {
            XMLEventReader reader = ParserFactory.createStaxParser(is);

            Optional<TagType> tagType = Optional.empty();
            Movie movie = null;
            StartElement startElement = null;
            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();
                switch (event.getEventType()) {
                    case XMLStreamConstants.START_ELEMENT:
                        startElement = event.asStartElement();
                        String qName = startElement.getName().getLocalPart();
                        tagType = TagType.from(qName);
                        break;
                    case XMLStreamConstants.CHARACTERS:
                        if (tagType.isPresent()) {
                            Characters characters = event.asCharacters();
                            String data = characters.getData().trim();
                            switch (tagType.get()) {
                                case ITEM:
                                    movie = new Movie();
                                    movies.add(movie);
                                    break;
                                case TITLE:
                                    if (movie != null && !data.isEmpty()) {
                                        movie.setTitle(data);
                                    }
                                    break;
                                case ORIGNAME:
                                    if (movie != null && !data.isEmpty()) {
                                        movie.setOriginalName(data);
                                    }
                                    break;
                                case DESCRIPTION:
                                    if (movie != null && !data.isEmpty()) {
                                        movie.setDescription(data);
                                    }
                                    break;
                                case DIRECTORS:
                                    if (movie != null && !data.isEmpty()) {
                                        String[] actors = data.split(DEL);
                                        List<Actor> actorsList = new ArrayList<>();
                                        for (String actorr : actors) {
                                            String[] actor1 = actorr.split(" ");
                                            try {
                                                actorsList.add(new Actor(actor1[0], actor1[1]));
                                            } catch (Exception ex) {
                                            }
                                        }
                                        movie.setDirectors(actorsList);
                                    }
                                    break;
                                case ACTORS:
                                    if (movie != null && !data.isEmpty()) {
                                        String[] allActors = data.split(DEL);
                                        List<Actor> actorsList = new ArrayList<>();
                                        for (String actor : allActors) {
                                            String[] actor1 = actor.split(" ");
                                            try {
                                                actorsList.add(new Actor(actor1[0], actor1[1]));
                                            } catch (Exception ex) {

                                            }
                                        }
                                        movie.setActors(actorsList);
                                    }
                                    break;

                                case DURATION:
                                    if (movie != null && !data.isEmpty()) {
                                        try {
                                            movie.setDuration(Integer.parseInt(data));
                                        } catch (Exception ex) {

                                        }
                                    }

                                    break;
                                case GENRES:
                                    if (movie != null && !data.isEmpty()) {
                                        String[] genres = data.split(DEL);
                                        List<Genre> genreList = new ArrayList<>();
                                        for (String genre : genres) {
                                            genreList.add(new Genre(genre));
                                        }
                                        movie.setGenres(genreList);
                                    }
                                    break;
                                case PICTURE:
                                    if (movie != null && !data.isEmpty()) {
                                        handlePicture(movie, data);
                                    }
                                    break;
                                case PUBDATE:
                                    if (movie != null && !data.isEmpty()) {
                                        try {
                                            movie.setPublishedDate(LocalDateTime.parse(data, Movie.dateTimeFormater));
                                        } catch (Exception e) {
                                             movie.setPublishedDate(LocalDateTime.now());
                                        }
                                    }
                                    break;
                            }
                        }
                        break;
                }
            }
        }
        return movies;

    }

    private static void handlePicture(Movie movie, String pictureUrl) {

        try {
            String ext = pictureUrl.substring(pictureUrl.lastIndexOf("."));
            if (ext.length() > 4) {
                ext = EXT;
            }
            String pictureName = UUID.randomUUID() + ext;
            String localPicturePath = DIR + File.separator + pictureName;

            FileUtils.copyFromUrl(pictureUrl, localPicturePath);
            movie.setPicturePath(localPicturePath);

        } catch (IOException ex) {
            Logger.getLogger(MovieParser.class
                    .getName()).log(Level.SEVERE, null, ex);

        }
    }

    private enum TagType {

        ITEM("item"),
        TITLE("title"),
        ORIGNAME("orignaziv"),
        DESCRIPTION("description"),
        DIRECTORS("redatelj"),
        ACTORS("glumci"),
        DURATION("trajanje"),
        GENRES("zanr"),
        PICTURE("plakat"),
        PUBDATE("pubDate");

        private final String name;

        private TagType(String name) {
            this.name = name;
        }

        private static Optional<TagType> from(String name) {
            for (TagType value : values()) {
                if (value.name.equals(name)) {
                    return Optional.of(value);
                }
            }
            return Optional.empty();
        }
    }

}
