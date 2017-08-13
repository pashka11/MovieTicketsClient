package com.javaproject.nimrod.cinema;

import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.javaproject.nimrod.cinema.Objects.MovieDisplay;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Nimrod on 13/06/2017.
 */

public class MoviesListAdapter extends android.support.v7.widget.RecyclerView.Adapter<MoviesListAdapter.MoviesAdapterViewHolder>
{
    List<MovieDisplay> m_moviesList;
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

    public void FilterDataByMovieName(String query)
    {
        // TODO:
        // first - get movies from the list that match the query,
        // second - create a new list that contains the data to display
        // third - set the getItemCount and onBindViewHolder to use the filtered data list

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
        @BindView(R.id.tv_movie_desc_preview) TextView m_movieTitle;
        @BindView(R.id.iv_movie_image_preview) ImageView m_moviePicture;
        @BindView(R.id.card_view) CardView _cardView;

        MoviesAdapterViewHolder(View view)
        {
            super(view);

            ButterKnife.bind(this, view);

            //view.setOnClickListener(v -> m_listener.OnMovieItemClicked(getAdapterPosition()));
            _cardView.setOnClickListener(v -> m_listener.OnMovieItemClicked(getAdapterPosition()));
        }

        public void bind(final MovieDisplay movie)
        {
            m_movieTitle.setText(movie.MovieDetails.Name);

            if (movie.MoviePicture != null)
                m_moviePicture.setImageBitmap(movie.MoviePicture);
        }

    }
}
