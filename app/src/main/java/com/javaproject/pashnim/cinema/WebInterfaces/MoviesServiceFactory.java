package com.javaproject.pashnim.cinema.WebInterfaces;

import android.util.Log;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

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
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(WebApiConstants.BaseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            Log.e("tag", "before creating service");
            m_service = retrofit.create(MoviesServiceAPI.class);
        }

        return m_service;
    }
}
