package com.javaproject.nimrod.cinema.Objects;

public class Hall
{
    public String HallId;
    public int Row;
    public int Column;

    public Hall() {
    }

    public Hall(String hallId , int row, int column)
    {
        this.HallId = hallId;
        this.Row = row;
        this.Column =column;
    }

    @Override
    public String toString()
    {
        return "Hall " + HallId;
    }
}