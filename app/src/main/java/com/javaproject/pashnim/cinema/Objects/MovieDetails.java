package com.javaproject.pashnim.cinema.Objects;

import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nimrod on 15/06/2017.
 */

public class MovieDetails implements Serializable
{
    public MovieDetails()
    {
        Actors = new ArrayList<>();
    }

    public MovieDetails(int id, String name, String description, String imageName, LocalDate releaseDate, String director, short duration, String genres, List<String> actors)
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
    public short Duration;
    public String Genres;
    public List<String> Actors;
}
