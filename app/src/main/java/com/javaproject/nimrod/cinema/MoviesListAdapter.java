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
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Nimrod on 13/06/2017.
 */

public class MoviesListAdapter extends android.support.v7.widget.RecyclerView.Adapter<MoviesListAdapter.MoviesAdapterViewHolder>
{
    List<MovieDisplay> _moviesList;
    MovieClickedListener m_listener;
    private List<MovieDisplay> _displayedMovies;

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
        if (position < _displayedMovies.size())
            holder.bind(_displayedMovies.get(position));
        else
            Log.d("RecyclerView",
                    "onBindViewHolder: Trying to bind position: " +
                            position +
                            "when data size is: " +
                            _moviesList.size());
    }

    @Override
    public int getItemCount()
    {
        return _displayedMovies == null ? 0 : _displayedMovies.size();
    }

    public void SetData(List<MovieDisplay> movies)
    {
        _moviesList = movies;
        _displayedMovies = movies;

        notifyDataSetChanged();
    }

    public void FilterDataByMovieName(String query)
    {
        _displayedMovies = _moviesList.
                stream().
                filter(movie -> movie.MovieDetails.Name.toLowerCase().contains(query.toLowerCase())).
                collect(Collectors.toList());

        notifyDataSetChanged();
    }

    public void SetImageAt(Bitmap image, int position)
    {
        if (position < _moviesList.size())
            _moviesList.get(position).MoviePicture = image;

        notifyItemChanged(position);
    }

    public void ClearFilteredData()
    {
        _displayedMovies = _moviesList;

        notifyDataSetChanged();
    }

    class MoviesAdapterViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.tv_movie_desc_preview) TextView _movieTitle;
        @BindView(R.id.iv_movie_image_preview) ImageView _moviePicture;
        @BindView(R.id.card_view) CardView _cardView;

        MoviesAdapterViewHolder(View view)
        {
            super(view);

            ButterKnife.bind(this, view);
        }

        @OnClick({R.id.card_view, R.id.iv_movie_image_preview, R.id.tv_movie_desc_preview})
        public void OnChooseMovieClicked()
        {
            m_listener.OnMovieItemClicked(_displayedMovies.get(getAdapterPosition()));
        }

        public void bind(final MovieDisplay movie)
        {
            _movieTitle.setText(movie.MovieDetails.Name);

            if (movie.MoviePicture != null)
                _moviePicture.setImageBitmap(movie.MoviePicture);
        }

    }
}
