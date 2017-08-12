package com.javaproject.pashnim.cinema.WebInterfaces;

import com.javaproject.pashnim.cinema.Objects.MovieDetails;
import com.javaproject.pashnim.cinema.Objects.Screening;
import com.javaproject.pashnim.cinema.Objects.Seat;

import java.util.List;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Nimrod on 15/06/2017.
 */

public interface MoviesServiceAPI
{
    // Movies
    @GET(WebApiConstants.Movies.RelativeUrl)
    Single<List<MovieDetails>> GetAllMovies();

    @GET(WebApiConstants.Movies.GetMovie)
    Call<MovieDetails> GetMovie(@Path(WebApiConstants.Movies.MovieId) String id);

    @POST(WebApiConstants.Movies.RelativeUrl)
    Call<ResponseBody> AddMovie(@Body MovieDetails movie);

    @GET(WebApiConstants.Images.RelativeGetImage)
    Call<ResponseBody> GetMoviePicture(@Path(WebApiConstants.Images.ImageName) String name);

    @GET(WebApiConstants.Movies.GetMovieScreenings)
    Single<List<Screening>> GetMovieScreenings(@Path(WebApiConstants.Movies.MovieId) String id);

    @POST(WebApiConstants.Screenings.SaveSeats)
    Single<String> SaveSelectedSeats(@Path(WebApiConstants.Screenings.ScreeningId) String screeningId, @Body List<Seat> seats);
}
