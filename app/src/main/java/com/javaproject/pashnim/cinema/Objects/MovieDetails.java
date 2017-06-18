package com.javaproject.pashnim.cinema.Objects;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by Nimrod on 15/06/2017.
 */

public class MovieDetails implements Serializable
{
    public MovieDetails()
    {

    }

    public MovieDetails(int id, String name, String description, String imageName, Date releaseDate, String director, List<String> actors)
    {
        Id = id;
        Name = name;
        Description = description;
        ImageName = imageName;
        ReleaseDate = releaseDate;
        Director = director;
        Actors = actors;
    }

    public int Id;
    public String Name;
    public String Description;
    public String ImageName;
    public Date ReleaseDate;
    public String Director;
    public List<String> Actors;
}
