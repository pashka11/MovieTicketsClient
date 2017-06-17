package com.javaproject.pashnim.cinema;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Paint;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

/**
 * Created by Pasha on 17-Jun-17.
 */

public class DateDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener{

    EditText txtDate;

    public DateDialog(View view){
        txtDate=(EditText)view;
    }
    public Dialog onCreateDialog(Bundle savedInstanceSate){
        final Calendar c =Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return  new DatePickerDialog(getActivity(),this,year,month,day);

    }
    public  void  onDateSet(DatePicker view,int year,int month,int day){
        String date = day+"--"+(month+1)+"--"+year;

        txtDate.setText(date);
    }
}
