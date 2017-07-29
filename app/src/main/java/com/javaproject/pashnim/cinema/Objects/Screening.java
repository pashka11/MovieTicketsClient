package com.javaproject.pashnim.cinema.Objects;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;

/**
 * Created by Nimrod on 15/06/2017.
 */

public class Screening
{
    public int Id;
    public LocalDateTime Time;
    public int HallId;
    public ArrayList<ArrayList<Integer>> Seats;
    public int Price;
    public int MovieId;

    public Screening()
    {

    }
    public Screening(int screeningId, int movieId, LocalDateTime screeningTime, int hallId, int price, ArrayList<ArrayList<Integer>> seats)
    {
        this.MovieId = movieId;
        this.Id = screeningId;
        this.Time = screeningTime;
        this.HallId = hallId;
        this.Price = price;
        this.Seats = seats;
    }
}
