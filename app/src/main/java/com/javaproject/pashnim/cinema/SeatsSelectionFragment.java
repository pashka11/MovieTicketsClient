package com.javaproject.pashnim.cinema;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.javaproject.pashnim.cinema.Objects.MovieDetails;
import com.javaproject.pashnim.cinema.Objects.Screening;

import java.util.ArrayList;

/**
 * Created by Nimrod on 24/06/2017.
 */

public class SeatsSelectionFragment extends Fragment implements MainActivity.DataReceiver
{


    // Constants
    public enum SeatState
    {
        Free(0),
        Occupied(1),
        Chosen(2);

        private int value;

        SeatState(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    TextView _screeningSummeryView;
    GridView _seatsView;
    private LinearLayout _rowsNumbersView;

    private Screening _chosenScreening;
    private MovieDetails _chosenMovie;
    private ArrayList<ArrayList<Integer>> _seats;

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

        _seatsView = (GridView) v.findViewById(R.id.gv_seats);
        _screeningSummeryView = (TextView) v.findViewById(R.id.tv_screening_summery);
        _rowsNumbersView = (LinearLayout) v.findViewById(R.id.ll_rows_numbers);

        // Initialize the movie summary
        _screeningSummeryView.setText(ConstructScreeningSummary(_chosenScreening));

        // Init row numbers
        _rowsNumbersView.setWeightSum(_seats.size());
        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);

        for (int rows = 1; rows <= _seats.size(); rows++)
        {
            TextView tv = new TextView(container.getContext());
            tv.setTextSize(14f);
            tv.setText(String.valueOf(rows));
            _rowsNumbersView.addView(tv, params);
        }
        //_rowsNumbersView.setText(String.format("%s%d\n", _rowsNumbersView.getText().toString(), rows));//_rowsNumbersView.append(rows + "\n");

        // Initialize the grid
        _seatsView.setNumColumns(_seats.get(0).size());

        _seatsView.setOnItemClickListener((parent, view, position, id) ->
        {
            int rowIndex = CalcRowFromPosition(position);
            int seatIndex = CalcSeatInRowFromPosition(position);

            int seatStatus = _seats.get(rowIndex).get(seatIndex);

            if (seatStatus != SeatState.Occupied.getValue())
            {
                boolean isSeatChosen = seatStatus == SeatState.Chosen.getValue();

                ((ImageView) view).setImageDrawable(
                        isSeatChosen ?
                                getResources().getDrawable(R.drawable.cinemaseatfree, null) :
                                getResources().getDrawable(R.drawable.cinemaseatchosen, null));
                _seats.get(rowIndex).set(seatIndex,
                        isSeatChosen ?
                                SeatState.Free.getValue() :
                                SeatState.Chosen.getValue());
            }
        });

        _seatsView.setAdapter(new SeatsAdapter());

        return v;
    }

    private String ConstructScreeningSummary(Screening screening)
    {
        StringBuilder builder = new StringBuilder();

        builder.append(_chosenMovie.Name).append("\n").append("מועד הקרנה: ");
        builder.append(screening.Time.toString("HH:mm dd/MM/yyyy"));

        return builder.toString();
    }

    @Override
    public void PassData(Object obj1, Object obj2)
    {
        if (obj1 instanceof Screening && obj2 instanceof  MovieDetails)
        {
            _seats = ((Screening) obj1).Seats;
            _chosenScreening = ((Screening) obj1);
            _chosenMovie = (MovieDetails) obj2;
        }
        else
            Log.d("Seat Selection", "Unexpected data received");
    }

    private int CalcRowFromPosition(int pos)
    {
        return pos / _seats.get(0).size();
    }

    private int CalcSeatInRowFromPosition(int pos)
    {
        return pos % _seats.get(0).size();
    }

    private class SeatsAdapter extends BaseAdapter
    {
        @Override
        public int getCount()
        {
            return _seats.size() * _seats.get(0).size();
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

            int rowNum = CalcRowFromPosition(position);
            int seatNum = CalcSeatInRowFromPosition(position);

            int seatDrawable = _seats.get(rowNum).get(seatNum) == 0 ? R.drawable.cinemaseatfree :
                    _seats.get(rowNum).get(seatNum) == 1 ? R.drawable.cinemaseatocuppied : R.drawable.cinemaseatchosen;

            image.setImageDrawable(getResources().
                    getDrawable(seatDrawable, null));

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
