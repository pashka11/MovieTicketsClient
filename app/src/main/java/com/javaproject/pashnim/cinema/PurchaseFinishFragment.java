package com.javaproject.pashnim.cinema;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.javaproject.pashnim.cinema.Objects.MovieDetails;
import com.javaproject.pashnim.cinema.Objects.Screening;
import com.javaproject.pashnim.cinema.Objects.Seat;
import com.javaproject.pashnim.cinema.Validation.TextInputLayoutDataAdapter;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.CreditCard;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import java.util.List;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Nimrod on 06/08/2017.
 */

public class PurchaseFinishFragment extends Fragment implements Validator.ValidationListener, MainActivity.DataReceiver
{
    private Validator _validator;

    @BindView(R.id.tv_purchase_summery)
    TextView _purchaseSummeryView;
    @BindView(R.id.til_given_name) @NotEmpty(messageResId = R.string.empty_field_error)
    TextInputLayout _givenName;
    @BindView(R.id.til_last_name) @NotEmpty(messageResId = R.string.empty_field_error)
    TextInputLayout _lastName;
    @BindView(R.id.til_email) @Email(messageResId = R.string.email_error)
    TextInputLayout _emailAddress;
    @BindView(R.id.til_phone) @Pattern(regex = "^05[0-9]{8}$", messageResId = R.string.phone_number_error)
    TextInputLayout _phoneNumber;
    @BindView(R.id.til_card_number) @CreditCard(messageResId = R.string.card_number_error)
    TextInputLayout _cardNumber;
    @BindView(R.id.til_card_expiration) @Pattern(messageResId = R.string.card_expiration_error, regex = "^(0[1-9]|1[0-2])\\/([0-9][0-9])$")
    TextInputLayout _cardExpiration;

    private String _purchaseSummery;
    private String _seatsSelectionId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_purchase, container, false);
        // Binding Fields
        ButterKnife.bind(this, v);

        // Setting fields validator
        _validator = new Validator(this);
        _validator.setValidationListener(this);
        _validator.registerAdapter(TextInputLayout.class, new TextInputLayoutDataAdapter());

        _purchaseSummeryView.setText(_purchaseSummery);

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    private String ConstructPurchaseSummery(MovieDetails movie, Screening scr, List<Seat> selectedSeats)
    {
        StringBuilder builder = new StringBuilder();
        builder.append(movie.Name).append(" , Hall ").append(scr.HallId)
                .append(" at ").append(scr.Time.toString("HH:mm dd/MM, ")).append("Seats: ");

        builder.append(selectedSeats
                    .parallelStream()
                    .map(seat -> "Row " +
                            seat.RowNumber +
                            " Seat " +
                            seat.SeatNumber)
                    .collect(Collectors.joining(", ")));

        return builder.toString();
    }

    @OnClick(R.id.btn_purchase)
    public void Validate()
    {
        _validator.validate();
    }

    @Override
    public void onValidationSucceeded()
    {
        Toast.makeText(getContext(), "Yay! we got it right!", Toast.LENGTH_SHORT).show();

        // Valid fields
        // If fine, continue
        // Else show toast to fix fields

        // Send server request and wait.

        // If success show success dialog with Approval number
        // Else show failed message and ask retry
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

        nextItem.setVisible(false);
        backItem.setVisible(true);

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors)
    {
        for (ValidationError error : errors)
        {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(getContext());

            // Display error messages
            if (view instanceof TextInputLayout)
                ((TextInputLayout) view).setError(message);
            else
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }

        Toast.makeText(getContext(), R.string.illegal_fields, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.back_action:
            {
                // TODO : Send cancel seats selection to server!

                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void PassData(Object ... objects) throws Exception
    {
        if (objects.length < 3)
            throw new Exception("Not enough arguments received");

        Object obj1 = objects[0];
        Object obj2 = objects[1];
        Object obj3 = objects[2];
        Object obj4 = objects[3];

        if (obj1 instanceof Screening && obj2 instanceof MovieDetails && obj3 instanceof List && obj4 instanceof String)
        {
            Screening scr = (Screening) obj1;
            MovieDetails movie = (MovieDetails) obj2;
            List<Seat> selectedSeats = (List<Seat>) obj3;
            _seatsSelectionId = (String) obj4;

            _purchaseSummery = ConstructPurchaseSummery(movie, scr, selectedSeats);

            if (_purchaseSummeryView != null)
                _purchaseSummeryView.setText(_purchaseSummery);
        }
        else
            Log.d("Seat Selection", "Unexpected data received");
    }
}


