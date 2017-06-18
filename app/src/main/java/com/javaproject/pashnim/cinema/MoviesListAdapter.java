package com.javaproject.pashnim.cinema;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.javaproject.pashnim.cinema.DisplayObjects.MovieDisplay;

import java.util.List;

/**
 * Created by Nimrod on 13/06/2017.
 */

public class MoviesListAdapter extends android.support.v7.widget.RecyclerView.Adapter<MoviesListAdapter.MoviesAdapterViewHolder>
{
    List<MovieDisplay> m_moviesList;
    Context m_context;
    MovieClickedListener m_listener;

    public MoviesListAdapter(MovieClickedListener listener)
    {
        m_listener = listener;
    }

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
        if (position < m_moviesList.size())
            holder.bind(m_moviesList.get(position));
        else
            Log.d("RecyclerView",
                    "onBindViewHolder: Trying to bind position: " +
                            position +
                            "when data size is: " +
                            m_moviesList.size());
    }

    @Override
    public int getItemCount()
    {
        return m_moviesList == null ? 0 : m_moviesList.size();
    }

    public void SetData(List<MovieDisplay> movies)
    {
        m_moviesList = movies;

        notifyDataSetChanged();
    }

    public void SetImageAt(Bitmap image, int position)
    {
        if (position < m_moviesList.size())
            m_moviesList.get(position).MoviePicture = image;

        notifyItemChanged(position);
    }

    class MoviesAdapterViewHolder extends RecyclerView.ViewHolder
    {
        TextView m_movieTitle;
        ImageView m_moviePicture;

        MoviesAdapterViewHolder(View view)
        {
            super(view);

            this.m_movieTitle = (TextView)view.findViewById(R.id.text_fast);
            this.m_moviePicture = (ImageView) view.findViewById(R.id.image_fast);

            view.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    m_listener.OnMovieItemClicked(getAdapterPosition());
                }
            });
        }

        public void bind(final MovieDisplay movie)
        {
            m_movieTitle.setText(movie.MovieDetails.Name);

            if (movie.MoviePicture != null)
                m_moviePicture.setImageBitmap(movie.MoviePicture);
        }

    }
}
