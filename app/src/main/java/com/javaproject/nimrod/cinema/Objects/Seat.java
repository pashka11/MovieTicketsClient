package com.javaproject.nimrod.cinema.Objects;

/**
 * Created by Nimrod on 16-Mar-17.
 */

public class Seat
{
    public int RowNumber;
    public int SeatNumber;

    public Seat()
    {

    }

    public Seat(int row, int seat) {
        this.RowNumber = row;
        this.SeatNumber = seat;
    }
}
