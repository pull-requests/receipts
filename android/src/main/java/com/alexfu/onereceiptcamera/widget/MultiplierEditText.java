package com.alexfu.onereceiptcamera.widget;

import android.content.Context;
import android.util.AttributeSet;

public class MultiplierEditText extends PrefixedEditText {
    public MultiplierEditText(Context context) {
        super(context);
        setPrefix("x");
    }

    public MultiplierEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPrefix("x");
    }

    public MultiplierEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setPrefix("x");
    }
}
