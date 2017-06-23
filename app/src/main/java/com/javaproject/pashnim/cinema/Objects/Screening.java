package com.javaproject.pashnim.cinema.Objects;

import org.joda.time.LocalDateTime;

/**
 * Created by Nimrod on 15/06/2017.
 */

public class Screening
{
    public Screening()
    {

    }

    public LocalDateTime ScreeningTime;
    public int MovieId;
    public int HallId;
    public int [][] Seats;
}

