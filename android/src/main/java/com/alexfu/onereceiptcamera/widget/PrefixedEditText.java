package com.alexfu.onereceiptcamera.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.EditText;

public class PrefixedEditText extends EditText {
    public PrefixedEditText(Context context) {
        super(context);
    }

    public PrefixedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PrefixedEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setPrefix(String prefix) {
        setCompoundDrawables(new TextDrawable(prefix), null, null, null);
    }

    private class TextDrawable extends Drawable {
        private String mText = "";

        public TextDrawable(String text) {
            mText = text;
            setBounds(0, 0, (int) getPaint().measureText(mText) + 2, (int) getTextSize());
        }

        @Override
        public void draw(Canvas canvas) {
            Paint paint = getPaint();
            paint.setColor(getCurrentHintTextColor());
            int lineBaseline = getLineBounds(0, null);
            canvas.drawText(mText, 0, canvas.getClipBounds().top + lineBaseline, paint);
        }

        @Override
        public void setAlpha(int alpha) {/* Not supported */}

        @Override
        public void setColorFilter(ColorFilter colorFilter) {/* Not supported */}

        @Override
        public int getOpacity() {
            return 1;
        }
    }
}