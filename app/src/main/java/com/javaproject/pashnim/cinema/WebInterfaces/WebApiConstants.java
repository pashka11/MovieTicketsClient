package com.javaproject.pashnim.cinema.WebInterfaces;

/**
 * Created by Nimrod on 15/06/2017.
 */

public final class WebApiConstants
{
    public final static String BaseUrl = "http://10.100.102.12:8081/webapi/";

    public final class Movies
    {
        public final static String MovieId = "id";
        public final static String BaseMoviesUrl = "movies";
        public final static String GetAllMovies = BaseMoviesUrl;
        public final static String GetMovie = BaseMoviesUrl + "/{" + MovieId + "}";
        public final static String GetMovieScreenings = BaseMoviesUrl + GetMovie + "/screenings";
    }

    public final class Screenings
    {
        public final static String BaseScreeningsUrl = "screenings";
        public final static String GetSpecificScreening = BaseScreeningsUrl + "/{id}";
    }

    public final class Images
    {
        public final static String BaseImagesUrl = "images";
        public final static String ImageName = "name";
        public final static String GetImage = BaseImagesUrl + "/{" + ImageName + "}";
    }
}
