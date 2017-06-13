package com.example.pasha.pashnimcinema;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Pasha on 10-Jun-17.
 */

public class ListViewListOfFilm extends RecyclerView.ViewHolder{
    protected TextView description;
    protected ImageView pic;

    public ListViewListOfFilm (View view){
        super(view);
        this.description = (TextView)view.findViewById(R.id.text_fast);
        this.pic = (ImageView) view.findViewById(R.id.image_fast);
        view.setClickable(true);

    }

}
