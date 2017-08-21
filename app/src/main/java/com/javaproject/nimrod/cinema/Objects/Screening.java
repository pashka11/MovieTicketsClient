package com.javaproject.nimrod.cinema.Objects;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;

/**
 * Created by Nimrod on 15/06/2017.
 */

public class Screening
{
    public String Id;
    public LocalDateTime Time;
    public String HallId;
    public ArrayList<Row> Seats;
    public int Price;
    public String MovieId;

    public Screening()
    {

    }

    public Screening(String screeningId, String movieId, LocalDateTime screeningTime, String hallId, int price, ArrayList<Row> seats)
    {
        this.MovieId = movieId;
        this.Id = screeningId;
        this.Time = screeningTime;
        this.HallId = hallId;
        this.Price = price;
        this.Seats = seats;
    }

    public Screening(String movieId, LocalDateTime screeningTime, String hallId, int price, ArrayList<Row> seats)
    {
        this ("", movieId, screeningTime, hallId, price, seats);
    }

    @Override
    public String toString()
    {
        return Time.toString("dd/MM/yyyy, HH:mm") + ", Hall " + HallId;
    }
}

