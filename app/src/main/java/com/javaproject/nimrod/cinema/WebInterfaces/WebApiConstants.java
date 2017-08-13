package com.javaproject.nimrod.cinema.WebInterfaces;

/**
 * Created by Nimrod on 15/06/2017.
 */

public final class WebApiConstants
{
    public final static String BaseUrl = "http://10.100.102.13:8081/api/";

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
        public final static String ScreeningId = "id";
        public final static String BaseScreeningsUrl = "screenings";
        public final static String SpecificScreening = BaseScreeningsUrl + "/{" + ScreeningId + "}";
        public final static String SaveSeats = SpecificScreening + "/seats/save";
    }

    public final class Images
    {
        public final static String RelativeUrl = "images";
        public final static String Url = BaseUrl  + RelativeUrl;
        public final static String ImageName = "name";
        public final static String RelativeGetImage = RelativeUrl + "/{" + ImageName + "}";
    }

    public class Purchases
    {
        public final static String RelativeUrl = "purchases";
        public final static String Url = BaseUrl  + RelativeUrl;
        public final static String PurchaseId = "purchaseId";
        public final static String RelativeGetPurchase = RelativeUrl + "/{" + PurchaseId + "}";
    }
}
