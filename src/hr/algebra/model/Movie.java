/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 *
 * @author Tomo
 */
public class Movie {

    public static DateTimeFormatter dateTimeFormater = DateTimeFormatter.RFC_1123_DATE_TIME;

    private int id;
    private String title;
    private LocalDateTime publishedDate;
    private String description;
    private String originalName;
    private List<Actor> directors;
    private List<Actor> actors;
    private int duration;
    private List<Genre> genres;
    private String picturePath;

    public Movie() {
    }

    public Movie(String title, LocalDateTime publishedDate, String description, String originalName, int duration, String picturePath) {
        this.title = title;
        this.publishedDate = publishedDate;
        this.description = description;
        this.originalName = originalName;
        this.duration = duration;
        this.picturePath = picturePath;
    }

    public Movie(int id, String title, LocalDateTime publishedDate, String description, String originalName, int duration, String picturePath) {
        this.id = id;
        this.title = title;
        this.publishedDate = publishedDate;
        this.description = description;
        this.originalName = originalName;
        this.duration = duration;
        this.picturePath = picturePath;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPublishedDate(LocalDateTime publishedDate) {
        this.publishedDate = publishedDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public void setDirectors(List<Actor> directors) {
        this.directors = directors;
    }

    public void setActors(List<Actor> actors) {
        this.actors = actors;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getPublishedDate() {
        return publishedDate;
    }

    public String getDescription() {
        return description;
    }

    public String getOriginalName() {
        return originalName;
    }

    public List<Actor> getDirectors() {
        return directors;
    }

    public List<Actor> getActors() {
        return actors;
    }

    public int getDuration() {
        return duration;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public String getPicturePath() {
        return picturePath;
    }

    @Override
    public String toString() {
        return title + " (" + originalName + ")";
    }

}
