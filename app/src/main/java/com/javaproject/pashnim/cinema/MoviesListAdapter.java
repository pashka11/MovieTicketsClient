package com.javaproject.pashnim.cinema;

import android.view.LayoutInflater;
import android.view.ViewGroup;

/**
 * Created by Nimrod on 13/06/2017. 55
 */

public class MoviesListAdapter extends android.support.v7.widget.RecyclerView.Adapter<MoviesAdapterViewHolder>
{
    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        return new MoviesAdapterViewHolder(
                LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.movie_list_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(MoviesAdapterViewHolder holder, int position)
    {
        // Here we get the item number "position" in the list we saved,
        // we will have an array with the movies retrieved with AsyncHttpClient
    }

    @Override
    public int getItemCount()
    {
        return 10; // Here we return the movies array data length or 0;
    }
}
