package com.javaproject.nimrod.cinema;

import android.app.Fragment;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.javaproject.nimrod.cinema.DataInterfaces.DataReceiver;
import com.javaproject.nimrod.cinema.Objects.MovieDetails;
import com.javaproject.nimrod.cinema.Objects.Row;
import com.javaproject.nimrod.cinema.Objects.Screening;
import com.javaproject.nimrod.cinema.Objects.Seat;
import com.javaproject.nimrod.cinema.WebInterfaces.MoviesServiceFactory;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Nimrod on 24/06/2017.
 */

public class SeatsSelectionFragment extends Fragment implements DataReceiver
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

    @BindView(R.id.pb_seats) ProgressBar _progressBar;
    @BindView(R.id.tv_screening_summery) TextView _screeningSummeryView;
    @BindView(R.id.gv_seats) GridView _seatsView;
    @BindView(R.id.ll_screen) LinearLayout _screen;

    private Screening _selectedScreening;
    private MovieDetails _chosenMovie;
    private ArrayList<Row> _seats;

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
        ButterKnife.bind(this, v);

        // Initialize the movie summary
        _screeningSummeryView.setText(ConstructScreeningSummary(_selectedScreening));

        // Initialize the grid
        _seatsView.setNumColumns(_seats.get(0).Seats.size() + 1);

        _seatsView.setAdapter(new SeatsAdapter());

        InitSeatsSelection();

        return v;
    }

    // Sets all the previously selected seats back to free
    private void InitSeatsSelection()
    {
        if (_seats != null)
            _seats.forEach(row ->
            {
                for (int seat = 0; seat < row.Seats.size(); seat++)
                    if (row.Seats.get(seat) == SeatState.Chosen.getValue())
                        row.Seats.set(seat, SeatState.Free.getValue());
            });
    }

    // When a seat was chosen
    @OnItemClick(R.id.gv_seats)
    void SeatClicked(AdapterView<?> parent, View view, int position, long id)
    {
        if (!SeatsSelectionFragment.this.IsNumbersColumn(position))
        {
            int rowIndex = SeatsSelectionFragment.this.CalcRowFromPosition(position);
            int seatIndex = SeatsSelectionFragment.this.CalcSeatInRowFromPosition(position);

            int seatStatus = _seats.get(rowIndex).Seats.get(seatIndex);

            if (seatStatus != SeatState.Occupied.getValue())
            {
                boolean isSeatChosen = seatStatus == SeatState.Chosen.getValue();

                ((ImageView) view).setImageDrawable(
                        isSeatChosen ?
                                SeatsSelectionFragment.this.getResources().getDrawable(R.drawable.cinemaseatfree, null) :
                                SeatsSelectionFragment.this.getResources().getDrawable(R.drawable.cinemaseatchosen, null));
                _seats.get(rowIndex).Seats.set(seatIndex,
                        isSeatChosen ?
                                SeatState.Free.getValue() :
                                SeatState.Chosen.getValue());
            }
        }
    }

    private String ConstructScreeningSummary(Screening screening)
    {
        return new StringBuilder()
                .append(_chosenMovie.Name).append("\n")
                .append("Hall ").append(screening.HallId)//.append("\n")
                //.append("Screening Date: ")
                .append(" at ").append(screening.Time.toString("HH:mm dd/MM/yyyy ")).toString();
    }

    @Override
    public void PassData(Object ... objects) throws Exception
    {
        if (objects.length < 2)
            throw new Exception("Not enough arguments received");

        Object obj1 = objects[0];
        Object obj2 = objects[1];

        if (obj1 instanceof Screening && obj2 instanceof  MovieDetails)
        {
            _seats = ((Screening) obj1).Seats;
            _selectedScreening = ((Screening) obj1);
            _chosenMovie = (MovieDetails) obj2;
        }
        else
            Log.d("Seat Selection", "Unexpected data received");
    }

    private boolean IsNumbersColumn(int pos)
    {
        return (pos + 1) % (_seats.get(0).Seats.size() + 1) == 0;
    }

    private int CalcRowFromPosition(int pos)
    {
        return pos / (_seats.get(0).Seats.size() + 1);
    }

    private int CalcSeatInRowFromPosition(int pos)
    {
        return pos % (_seats.get(0).Seats.size() + 1);
    }

    private class SeatsAdapter extends BaseAdapter
    {
        private final int _seatColumnWidth;
        private final float _textColWidth;

        private final float FONT_SIZE = 12f;

        public SeatsAdapter()
        {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            _seatColumnWidth = displayMetrics.widthPixels / _seats.get(0).Seats.size();

            Paint p = new Paint();
            final float densityMultiplier = displayMetrics.density;
            final float scaledPx = FONT_SIZE * densityMultiplier;
            p.setTextSize(scaledPx);

            _textColWidth = p.measureText("99");
        }

        @Override
        public int getCount()
        {
            return _seats.size() * (_seats.get(0).Seats.size() + 1);
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
//            if (convertView != null)
//                return convertView;

            int rowNum = CalcRowFromPosition(position);
            int seatNum = CalcSeatInRowFromPosition(position);

            if (!IsNumbersColumn(position))
            {
                ImageView image = new ImageView(parent.getContext());

                image.setLayoutParams(new GridView.LayoutParams(_seatColumnWidth, _seatColumnWidth));
                image.setPadding(7, 5, 7, 5);

                image.setAdjustViewBounds(true);

                int seatDrawable = _seats.get(rowNum).Seats.get(seatNum) == 0 ? R.drawable.cinemaseatfree :
                        _seats.get(rowNum).Seats.get(seatNum) == 1 ? R.drawable.cinemaseatocuppied : R.drawable.cinemaseatchosen;

                image.setImageDrawable(getResources().
                        getDrawable(seatDrawable, null));

                return image;
            }
            else
            {
                TextView tv = new TextView(parent.getContext());
                tv.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                tv.setTextSize((float) ((_seatColumnWidth * 0.35) / getResources().getDisplayMetrics().scaledDensity));
                tv.setHeight(_seatColumnWidth);
                tv.setGravity(Gravity.CENTER);
                tv.setMaxLines(1);
                tv.setText(String.valueOf(rowNum + 1));

                return tv;
            }
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        MenuItem nextItem = menu.findItem(R.id.next_action);

        nextItem.setVisible(true);
        searchItem.setVisible(false);

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
            {
                // Tell the activity back was pressed
                getActivity().onBackPressed();

                return true;
            }
            case R.id.next_action:
            {
                CreateSeatSelectionRequest();

                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void CreateSeatSelectionRequest()
    {
        // Create seats list from seats matrix
        List<Seat> selectedSeats = new ArrayList<>();

        // Collect the chosen seats
        for (int row = 0; row < _seats.size(); row++)
            for (int seat = 0; seat < _seats.get(row).Seats.size(); seat++)
                if (_seats.get(row).Seats.get(seat) == SeatState.Chosen.getValue())
                    selectedSeats.add(new Seat(row, seat));

        // If the user didn't select any seat
        if (selectedSeats.size() == 0)
        {
            Snackbar.make(getView(), "No seats were selected", Snackbar.LENGTH_LONG).show();
            return;
        }

        // Start save seats request
        MoviesServiceFactory.GetInstance().SaveSelectedSeats(_selectedScreening.Id, selectedSeats)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable ->
                {
                    _seatsView.setVisibility(View.GONE);
                    _screen.setVisibility(View.GONE);
                    _screeningSummeryView.setVisibility(View.GONE);
                    _progressBar.setVisibility(View.VISIBLE);
                })
                .subscribe((selectionId, throwable) ->
                {
                    _progressBar.setVisibility(View.GONE);

                    // If the seats selection failed (seats are occupied or db error)
                    if (!TextUtils.isEmpty(selectionId))
                    {
                        ((MainActivity) getActivity()).ShowPurchaseDetailsFragment(selectedSeats, selectionId);
                    }
                    else
                        Snackbar.make(getView(), "Failed saving seats", Snackbar.LENGTH_LONG).show();

                    _screen.setVisibility(View.VISIBLE);
                    _seatsView.setVisibility(View.VISIBLE);
                    _screeningSummeryView.setVisibility(View.VISIBLE);
                });
    }
}
