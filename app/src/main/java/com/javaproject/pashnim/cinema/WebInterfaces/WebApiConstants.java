package com.javaproject.pashnim.cinema.WebInterfaces;

/**
 * Created by Nimrod on 15/06/2017.
 */

public final class WebApiConstants
{
    public final static String BaseUrl = "http://10.100.102.11:8081/api/";

    public final class Movies
    {
        public final static String MovieId = "id";
        public final static String RelativeUrl = "movies";
        public final static String Url = BaseUrl + RelativeUrl;
        public final static String GetAllMovies = RelativeUrl;
        public final static String GetMovie = RelativeUrl + "/{" + MovieId + "}";
        public final static String GetMovieScreenings = GetMovie + "/screenings";
    }

    public final class Screenings
    {
        public final static String BaseScreeningsUrl = "screenings";
        public final static String GetSpecificScreening = BaseScreeningsUrl + "/{id}";
    }

    public final class Images
    {
        public final static String RelativeUrl = "images";
        public final static String Url = BaseUrl  + RelativeUrl;
        public final static String ImageName = "name";
        public final static String RelativeGetImage = RelativeUrl + "/{" + ImageName + "}";
    }
}
