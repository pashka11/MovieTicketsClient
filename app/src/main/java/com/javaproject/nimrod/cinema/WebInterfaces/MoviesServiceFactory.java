package com.javaproject.nimrod.cinema.WebInterfaces;

import com.google.gson.GsonBuilder;
import com.javaproject.nimrod.cinema.Objects.TypeSerializers.LocalDateSerializer;
import com.javaproject.nimrod.cinema.Objects.TypeSerializers.LocalDateTimeSerializer;
import com.javaproject.nimrod.cinema.Objects.TypeSerializers.LocalTimeSerializer;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public final class MoviesServiceFactory
{
    private static MoviesServiceAPI m_service = null;

    private MoviesServiceFactory()
    {
    }

    public static MoviesServiceAPI GetInstance()
    {
        if (m_service == null)
        {
            // Creating gson with custom date handling serializers
            GsonBuilder gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
                    .registerTypeAdapter(LocalTime.class, new LocalTimeSerializer())
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer());

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(WebApiConstants.BaseUrl)
                    .client(new OkHttpClient.Builder().writeTimeout(20, TimeUnit.SECONDS)
                            .readTimeout(20, TimeUnit.SECONDS).build())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson.create()))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            m_service = retrofit.create(MoviesServiceAPI.class);
        }

        return m_service;
    }
}
