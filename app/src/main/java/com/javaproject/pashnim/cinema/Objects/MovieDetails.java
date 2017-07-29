package com.javaproject.pashnim.cinema.Objects;

import org.joda.time.LocalDate;

import java.io.Serializable;

/**
 * Created by Nimrod on 15/06/2017.
 */

public class MovieDetails implements Serializable
{
    public MovieDetails(int id, String name, String description, String imageName, String director, String actors, LocalDate releaseDate, String genres, int duration)
    {
        Id = id;
        Name = name;
        Description = description;
        ImageName = imageName;
        ReleaseDate = releaseDate;
        Director = director;
        Actors = actors;
        Duration = duration;
        Genres = genres;
    }

    public int Id;
    public String Name;
    public String Description;
    public String ImageName;
    public LocalDate ReleaseDate;
    public String Director;
    public int Duration;
    public String Genres;
    public String Actors;
}
