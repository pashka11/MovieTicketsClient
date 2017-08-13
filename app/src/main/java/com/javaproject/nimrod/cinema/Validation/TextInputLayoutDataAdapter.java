package com.javaproject.nimrod.cinema.Validation;

import android.support.design.widget.TextInputLayout;

import com.mobsandgeeks.saripaar.adapter.ViewDataAdapter;
import com.mobsandgeeks.saripaar.exception.ConversionException;

/**
 * Created by Nimrod on 07/08/2017.
 */

public class TextInputLayoutDataAdapter implements ViewDataAdapter<TextInputLayout, String>
{
    @Override
    public String getData(TextInputLayout view) throws ConversionException
    {
        return view.getEditText().getText().toString();
    }
}
