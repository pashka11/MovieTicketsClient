package com.javaproject.nimrod.cinema;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.javaproject.nimrod.cinema.DataInterfaces.DataReceiver;
import com.javaproject.nimrod.cinema.Objects.MovieDetails;
import com.javaproject.nimrod.cinema.Objects.PurchaseRequest;
import com.javaproject.nimrod.cinema.Objects.Screening;
import com.javaproject.nimrod.cinema.Objects.Seat;
import com.javaproject.nimrod.cinema.Validation.TextInputLayoutDataAdapter;
import com.javaproject.nimrod.cinema.WebInterfaces.MoviesServiceFactory;
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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Nimrod on 06/08/2017.
 */

public class PurchaseFinishFragment extends Fragment implements Validator.ValidationListener, DataReceiver
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
    private Screening _selectedScreening;
    private int _totalPrice;

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

    /**
     * Create a purchase summary from the given items
     * @param movie
     * @param scr
     * @param selectedSeats
     * @param price
     * @return
     */
    private String ConstructPurchaseSummery(MovieDetails movie, Screening scr, List<Seat> selectedSeats,int price)
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
        builder.append("\n\n Total Price:  ").append(price);
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
        ClearErrorOnAllFields();

        PurchaseRequest request = new PurchaseRequest();
        request.Email = _emailAddress.getEditText().getText().toString();
        request.GivenName = _givenName.getEditText().getText().toString();
        request.LastName = _lastName.getEditText().getText().toString();
        request.PhoneNumber = _phoneNumber.getEditText().getText().toString();
        request.ScreeningId = _selectedScreening.Id;
        request.SeatsSelectionId = _seatsSelectionId;
        request.TotalPrice = _totalPrice;

        final ProgressDialog progressDialog = new ProgressDialog(getContext(),
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Please wait...");
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.show();

        // Send server request and wait.
        MoviesServiceFactory.GetInstance().MakePurchase(request).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe((s, throwable) -> {
                            progressDialog.dismiss();

                            if (s != null)
                                ShowPurchaseCompletionDialog(s);
                            else
                                Snackbar.make(getView(), "Failed making purchase", Snackbar.LENGTH_LONG).show();
                        }
                );
    }

    private void ShowPurchaseCompletionDialog(String selectionId)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar));

        TextView messageText = new TextView(getContext());
        messageText.setText(String.format(getString(R.string.purchase_approval_text), selectionId, _emailAddress.getEditText().getText().toString()));
        messageText.setTextSize(20f);
        messageText.setGravity(Gravity.CENTER);

        builder.setView(messageText);

        builder.setPositiveButton("Ok", (dialog, id) -> {
            ((MainActivity)getActivity()).CloseAllFragments();
            ((MainActivity)getActivity()).ShowMoviesListFragment();
        });

        AlertDialog dlg = builder.create();

        dlg.show();

        final Button positiveButton = dlg.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
        positiveButtonLL.width = ViewGroup.LayoutParams.MATCH_PARENT;
        positiveButton.setLayoutParams(positiveButtonLL);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        MenuItem nextItem = menu.findItem(R.id.next_action);

        nextItem.setVisible(false);
        searchItem.setVisible(false);

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors)
    {
        ClearErrorOnAllFields();

        for (ValidationError error : errors)
        {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(getContext());

            // Display error messages
            if (view instanceof TextInputLayout)
                ((TextInputLayout) view).setError(message);
            else
                Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
        }

        Snackbar.make(getView(), R.string.illegal_fields, Snackbar.LENGTH_LONG).show();
    }

    private void ClearErrorOnAllFields()
    {
        _givenName.setError(null);
        _lastName.setError(null);
        _emailAddress.setError(null);
        _cardExpiration.setError(null);
        _cardNumber.setError(null);
        _phoneNumber.setError(null);
        _givenName.setErrorEnabled(false);
        _lastName.setErrorEnabled(false);
        _emailAddress.setErrorEnabled(false);
        _cardExpiration.setErrorEnabled(false);
        _cardNumber.setErrorEnabled(false);
        _phoneNumber.setErrorEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
            {
                MoviesServiceFactory.GetInstance().CancelSeatSelection(_selectedScreening.Id, _seatsSelectionId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> {
                        })
                        .subscribe((responseBody, throwable) ->
                        {
                            if (throwable != null)
                                Snackbar.make(getView(),
                                        "Failed canceling seats selection \n seats will not be available for the rest of the waiting time",
                                        Snackbar.LENGTH_SHORT).show();

                            // Tell the activity back was pressed
                            getActivity().onBackPressed();
                        });

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
            _selectedScreening = (Screening) obj1;
            MovieDetails movie = (MovieDetails) obj2;
            List<Seat> selectedSeats = (List<Seat>) obj3;
            _seatsSelectionId = (String) obj4;
            _totalPrice = _selectedScreening.Price * selectedSeats.size();

            _purchaseSummery = ConstructPurchaseSummery(movie, _selectedScreening, selectedSeats,_totalPrice);

            if (_purchaseSummeryView != null)
                _purchaseSummeryView.setText(_purchaseSummery);
        }
        else
            Log.d("Seat Selection", "Unexpected data received");
    }
}


