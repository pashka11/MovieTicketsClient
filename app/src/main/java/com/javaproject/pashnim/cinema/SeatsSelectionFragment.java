package com.javaproject.pashnim.cinema;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Created by Nimrod on 24/06/2017.
 */

public class SeatsSelectionFragment extends Fragment
{

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_seat_selection, container, false);

        GridView grid = (GridView) v.findViewById(R.id.grid_seats);

        int [][] testData = new int[3][3];

        grid.setNumColumns(testData.length);

        grid.setOnItemClickListener((parent, view, position, id) ->
        {
            int x = position/testData.length;
            int y = position % testData[0].length;

            testData[x][y] = testData[x][y] == 1 ? 0 : 1;

            ((ImageView)view).setImageDrawable(getResources().getDrawable(R.drawable.pirate, null));
        });

        grid.setAdapter(new SeatsAdapter(testData));

        return v;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }

    private class SeatsAdapter extends BaseAdapter
    {
        int[][] m_Seats;

        public SeatsAdapter(int[][] seats)
        {
            m_Seats = seats;
        }

        @Override
        public int getCount()
        {
            return m_Seats.length * m_Seats[0].length;
        }

        @Override
        public Object getItem(int position)
        {
            return null;
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ImageView image = new ImageView(parent.getContext());
            //image.setLayoutParams(new ViewGroup.LayoutParams(20,20));
            image.setScaleType(ImageView.ScaleType.FIT_XY);
            image.setAdjustViewBounds(true);
            image.setImageDrawable(getResources().getDrawable(R.drawable.img, null));

            return image;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_ticket_order_process,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        MenuItem backItem = menu.findItem(R.id.back_action);
        MenuItem nextItem = menu.findItem(R.id.next_action);

        nextItem.setVisible(true);
        backItem.setVisible(true);

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.next_action:
            {
                // TODO : Handle next

                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
