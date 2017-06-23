package com.javaproject.pashnim.cinema.WebInterfaces;

import com.google.gson.GsonBuilder;
import com.javaproject.pashnim.cinema.Objects.TypeSerializers.LocalDateSerializer;
import com.javaproject.pashnim.cinema.Objects.TypeSerializers.LocalDateTimeSerializer;
import com.javaproject.pashnim.cinema.Objects.TypeSerializers.LocalTimeSerializer;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
                    .addConverterFactory(GsonConverterFactory.create(gson.create()))
                    .build();

            m_service = retrofit.create(MoviesServiceAPI.class);
        }

        return m_service;
    }
}
