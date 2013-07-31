package com.alexfu.onereceiptcamera.data;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alexfu.onereceiptcamera.DateUtils;
import com.alexfu.onereceiptcamera.R;
import com.alexfu.onereceiptcamera.model.Receipt;
import com.emilsjolander.components.stickylistheaders.StickyListHeadersAdapter;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class StickyAdapter extends BaseAdapter
        implements StickyListHeadersAdapter {

    private Context mContext;
    private ArrayList<Receipt> mReceipts;
    private int[] mCounter;
    private SparseBooleanArray mCheckedPositions;

    private static final int THIS_WEEK = 0;
    private static final int LAST_WEEK = 1;
    private static final int SOME_TIME_AGO = 2;

    public StickyAdapter(Context context, ArrayList<Receipt> receipts) {
        mContext = context;
        mReceipts = receipts;
        mCounter = new int[3];
        mCheckedPositions = new SparseBooleanArray();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        mCounter[THIS_WEEK] = 0;
        mCounter[LAST_WEEK] = 0;
        mCounter[SOME_TIME_AGO] = 0;
        for(Receipt r : mReceipts) {
            double weeksPassed = DateUtils.weeksPassed(r.getDate());

            if(weeksPassed < 1) {
                mCounter[THIS_WEEK]++;
            } else if(weeksPassed > 0 & weeksPassed < 2) {
                mCounter[LAST_WEEK]++;
            } else {
                mCounter[SOME_TIME_AGO]++;
            }
        }
    }

    @Override
    public View getHeaderView(int position, View view, ViewGroup viewGroup) {
        if(view == null) {
            view = LayoutInflater.from(mContext).
                    inflate(R.layout.header_receipt, viewGroup, false);
        }
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView counter = (TextView) view.findViewById(R.id.counter);

        int headerId = (int) getHeaderId(position);
        switch (headerId) {
            case THIS_WEEK:
                title.setText(R.string.header_this_week);
                break;
            case LAST_WEEK:
                title.setText(R.string.header_last_week);
                break;
            case SOME_TIME_AGO:
                title.setText(R.string.header_some_time_ago);
                break;
        }

        counter.setText(String.valueOf(mCounter[headerId]));

        return view;
    }

    @Override
    public long getHeaderId(int position) {
        Receipt receipt = getItem(position);
        double weeksPassed = DateUtils.weeksPassed(receipt.getDate());
        if(weeksPassed < 1) {
            return THIS_WEEK;
        } else if(weeksPassed > 0 && weeksPassed < 2) {
            return LAST_WEEK;
        } else {
            return SOME_TIME_AGO;
        }
    }

    @Override
    public int getCount() {
        return mReceipts.size();
    }

    @Override
    public Receipt getItem(int position) {
        return mReceipts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mReceipts.get(position).getId();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        Receipt r = getItem(position);
        if(view == null) {
            view = LayoutInflater.from(mContext).
                    inflate(R.layout.row_receipt, viewGroup, false);
        }

        TextView label = (TextView) view.findViewById(R.id.label);
        TextView date = (TextView) view.findViewById(R.id.date);
        TextView amount = (TextView) view.findViewById(R.id.amount);
        TextView numOfItems = (TextView) view.findViewById(R.id.num_of_items);

        label.setText(r.getLabel());
        date.setText(DateUtils.formatDate(r.getDate().getTimeInMillis(), "MMM dd yyyy"));
        amount.setText(new DecimalFormat("\u00A400.00").format(r.getTotal()));

        int itemCount = r.getNumberOfItems();
        if(itemCount > 1) {
            numOfItems.setText(mContext.getString(R.string.item_count, itemCount-1));
            numOfItems.setVisibility(View.VISIBLE);
        } else {
            if(numOfItems.getVisibility() == View.VISIBLE) {
                numOfItems.setVisibility(View.GONE);
            }
        }

        // Save original padding since it will be cleared when we
        // set the background programatically.
        int paddingTop = view.getPaddingTop();
        int paddingBottom = view.getPaddingBottom();
        int paddingLeft = view.getPaddingLeft();
        int paddingRight = view.getPaddingRight();

        if(mCheckedPositions.get(position)) {
            view.setBackgroundResource(R.drawable.list_selected_holo_light);
        } else {
            view.setBackgroundResource(R.drawable.background_row_receipt);
        }

        // Restore padding
        view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

        return view;
    }

    public void setChecked(int position, boolean checked) {
        mCheckedPositions.append(position, checked);
        notifyDataSetChanged();
    }

    public void clearChecked() {
        mCheckedPositions.clear();
        notifyDataSetChanged();
    }
}