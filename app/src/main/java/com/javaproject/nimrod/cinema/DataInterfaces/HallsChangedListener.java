package com.javaproject.nimrod.cinema.DataInterfaces;

import com.javaproject.nimrod.cinema.Objects.Hall;

import java.util.List;

/**
 * Created by Nimrod on 20/08/2017.
 */

public interface HallsChangedListener
{
    void HallsChanged(List<Hall> movies);
}
