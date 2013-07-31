package com.alexfu.onereceiptcamera.widget;

import android.content.Context;
import android.util.AttributeSet;

import java.text.DecimalFormat;
import java.util.Currency;
import java.util.Locale;

public class CurrencyEditText extends PrefixedEditText {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("00.00");
    public CurrencyEditText(Context context) {
        super(context);
        setPrefix(Currency.getInstance(Locale.getDefault()).getSymbol());
    }

    public CurrencyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPrefix(Currency.getInstance(Locale.getDefault()).getSymbol());
    }

    public CurrencyEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setPrefix(Currency.getInstance(Locale.getDefault()).getSymbol());
    }
}