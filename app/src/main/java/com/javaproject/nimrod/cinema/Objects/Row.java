package com.javaproject.nimrod.cinema.Objects;

import java.io.Serializable;
import java.util.ArrayList;

public class Row implements Serializable
{
	public ArrayList<Integer> Seats;

	public Row()
	{

	}

	public Row(ArrayList<Integer> seats)
	{
		Seats = seats;
	}
}
