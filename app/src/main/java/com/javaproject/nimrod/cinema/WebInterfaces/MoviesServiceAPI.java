package com.javaproject.nimrod.cinema.WebInterfaces;

import com.javaproject.nimrod.cinema.Objects.Hall;
import com.javaproject.nimrod.cinema.Objects.MovieDetails;
import com.javaproject.nimrod.cinema.Objects.PurchaseRequest;
import com.javaproject.nimrod.cinema.Objects.Screening;
import com.javaproject.nimrod.cinema.Objects.Seat;
import com.javaproject.nimrod.cinema.Objects.User;

import java.util.List;
import java.util.jar.Attributes;

import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Nimrod on 15/06/2017.
 */

public interface MoviesServiceAPI
{
    @GET(WebApiConstants.Movies.RelativeUrl)
    Single<List<MovieDetails>> GetAllMovies();

    @GET(WebApiConstants.Movies.SpecificMovie)
    Call<MovieDetails> GetMovie(@Path(WebApiConstants.Movies.MovieId) String id);

    @POST(WebApiConstants.Movies.RelativeUrl)
    Single<String> AddMovie(@Body MovieDetails movie);

    @DELETE(WebApiConstants.Movies.SpecificMovie)
    Single<ResponseBody> DeleteMovie(@Path(WebApiConstants.Movies.MovieId) String movieId);

    @GET(WebApiConstants.Images.RelativeGetImage)
    Call<ResponseBody> GetMoviePicture(@Path(WebApiConstants.Images.ImageName) String name);

    @GET(WebApiConstants.Movies.GetMovieScreenings)
    Single<List<Screening>> GetMovieScreenings(@Path(WebApiConstants.Movies.MovieId) String id, @Query("future") boolean futureScreening);

    @POST(WebApiConstants.Screenings.SaveSeats)
    Single<String> SaveSelectedSeats(@Path(WebApiConstants.Screenings.ScreeningId) String screeningId, @Body List<Seat> seats);

    @FormUrlEncoded
    @PUT(WebApiConstants.Screenings.SaveSeats)
    Single<ResponseBody> CancelSeatSelection(@Path(WebApiConstants.Screenings.ScreeningId) String screeningId, @Field("selectionId") String selectionId);

    @POST(WebApiConstants.Purchases.RelativeUrl)
    Single<String> MakePurchase(@Body PurchaseRequest request);

    @Multipart
    @POST(WebApiConstants.Images.RelativeUrl)
    Single<ResponseBody> UploadImage(@Part MultipartBody.Part image);

    @GET(WebApiConstants.Halls.RelativeUrl)
    Single<List<Hall>> GetHalls();

    @POST(WebApiConstants.Screenings.RelativeUrl)
    Single<String> AddScreening(@Body Screening screening);

    @DELETE(WebApiConstants.Screenings.SpecificScreening)
    Single<ResponseBody> DeleteScreening(@Path(WebApiConstants.Screenings.ScreeningId) String screeningId);

    @POST(WebApiConstants.Halls.RelativeUrl)
    Single<String> AddHall(@Body Hall hall);

    @DELETE(WebApiConstants.Halls.SpecificHall)
    Single<ResponseBody> DeleteHall(@Path(WebApiConstants.Halls.HallId) String hallId);

    @POST(WebApiConstants.Users.RelativeUrl)
    Single<User> ValidateUser(@Body User user);
}
