package com.alexfu.onereceiptcamera.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

public class NonScrollableListView extends LinearLayout implements View.OnClickListener {

    private BaseAdapter mAdapter;
    private View mFooterView;
    private OnFooterClickListener mFooterClickListener;

    @Override
    public void onClick(View view) {
        if(view == mFooterView) {
            mFooterClickListener.onFooterClicked();
        }
    }

    public interface OnFooterClickListener {
        public void onFooterClicked();
    }

    public NonScrollableListView(Context context) {
        this(context, null);
    }

    public NonScrollableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    public NonScrollableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOrientation(VERTICAL);
    }

    public void setAdapter(BaseAdapter adapter) {
        mAdapter = adapter;
    }

    public void addItem() {
        int insertAt = getChildCount()-1;
        View row = mAdapter.getView(insertAt, null, this);
        addView(row, insertAt);
    }

    public void addItemAt(int position) {
        View row = mAdapter.getView(position, null, this);
        addView(row, position);
    }

    public void removeItemAt(int position) {
        removeViewAt(position);
    }

    public void addFooterView(View v) {
        mFooterView = v;
        addView(mFooterView, getChildCount());
    }

    public void setOnFooterClickedListener(OnFooterClickListener listener) {
        if(mFooterView != null) {
            mFooterClickListener = listener;
            mFooterView.setOnClickListener(this);
        }
    }
}