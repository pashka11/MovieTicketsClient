package com.javaproject.pashnim.cinema;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Pasha on 10-Jun-17.
 */

public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder
{
    protected TextView m_movieDescription;
    protected ImageView m_moviePicture;

    public MoviesAdapterViewHolder(View view)
    {
        super(view);

        this.m_movieDescription = (TextView)view.findViewById(R.id.text_fast);
        this.m_moviePicture = (ImageView) view.findViewById(R.id.image_fast);

        view.setClickable(true);

    }

}
