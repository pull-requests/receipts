package com.alexfu.onereceiptcamera.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alexfu.onereceiptcamera.DateUtils;
import com.alexfu.onereceiptcamera.ListUndoToken;
import com.alexfu.onereceiptcamera.LoadCategoriesTask;
import com.alexfu.onereceiptcamera.R;
import com.alexfu.onereceiptcamera.UndoBarController;
import com.alexfu.onereceiptcamera.data.SpinnerCategoryAdapter;
import com.alexfu.onereceiptcamera.data.SpinnerDateAdapter;
import com.alexfu.onereceiptcamera.data.SpinnerMonthAdapter;
import com.alexfu.onereceiptcamera.data.SpinnerYearAdapter;
import com.alexfu.onereceiptcamera.database.DatabaseManager;
import com.alexfu.onereceiptcamera.model.Item;
import com.alexfu.onereceiptcamera.model.Receipt;
import com.alexfu.onereceiptcamera.service.UploadImageService;
import com.alexfu.onereceiptcamera.widget.NonScrollableListView;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class EditReceiptActivity extends Activity
        implements View.OnClickListener,
        NonScrollableListView.OnFooterClickListener,
        UndoBarController.UndoListener,
        LoadCategoriesTask.OnCategoriesLoadedListener {

    private Receipt mReceipt;
    private Spinner mMonth, mDate, mYear, mCategory;
    private SpinnerDateAdapter mDatesAdapter;
    private SpinnerCategoryAdapter mCategoryAdapter;
    private boolean mIsNewReceipt = false;

    private NonScrollableListView mNoScrollListView;
    private ItemsAdapter mListAdapter;
    private ArrayList<Item> mItems;
    private ArrayList<Item> mToDeleteItems;
    private ImageView mImage;
    private CheckBox mCheckBoxSend;

    private File mCurrentReceiptImage;

    private UndoBarController mUndoBar;

    private TextView mTotal;
    private Double mCurrentTotal = 0.0;

    private static final int CAPTURE_IMAGE = 1;
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("\u00A400.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        mReceipt = getIntent().getParcelableExtra("receipt");
        mItems = new ArrayList<Item>();
        mIsNewReceipt = mReceipt == null;
        if(mIsNewReceipt) {
            setTitle(R.string.title_new_receipt);
        } else {
            setTitle(R.string.title_edit_receipt);
        }

        setContentView(R.layout.activity_edit_receipt);
        initViews();
    }

    @Override
    public void onBackPressed() {
        cancel();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            cancel();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        mNoScrollListView = (NonScrollableListView) findViewById(R.id.list);
        mImage = (ImageView) findViewById(R.id.image);
        mCheckBoxSend = (CheckBox) findViewById(R.id.send_receipt);
        findViewById(R.id.save).setOnClickListener(this);
        findViewById(R.id.cancel).setOnClickListener(this);

        View undobar = findViewById(R.id.undobar);
        mUndoBar = new UndoBarController(undobar, this);

        // Setup "Items"
        mListAdapter = new ItemsAdapter(mItems);
        mNoScrollListView.setAdapter(mListAdapter);

        // Add footer view to list
        View footer = LayoutInflater.from(this).inflate(R.layout.row_item_footer, mNoScrollListView, false);
        mNoScrollListView.addFooterView(footer);
        mNoScrollListView.setOnFooterClickedListener(this);
        initItems();

        // Setup "When"
        initDate();

        // Setup "Category"
        initCategory();

        // Setup receipt image
        if(mReceipt != null && mReceipt.getImagePath() != null) {
            mCheckBoxSend.setEnabled(!mReceipt.isSent());
            mCheckBoxSend.setChecked(mReceipt.isSent());
            mImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mImage.setImageURI(mReceipt.getImagePath());
            mImage.setOnClickListener(null);
        } else {
            mCheckBoxSend.setEnabled(false);
            mImage.setImageResource(R.drawable.placeholder_image);
            mImage.setOnClickListener(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem mi = menu.add("total");
        mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        View view = LayoutInflater.from(this).inflate(R.layout.ab_total_counter, null);
        mTotal = (TextView) view.findViewById(R.id.total);
        mTotal.setText(DECIMAL_FORMAT.format(mCurrentTotal));

        mi.setActionView(view);
        return super.onCreateOptionsMenu(menu);
    }

    private void initCategory() {
        mCategory = (Spinner) findViewById(R.id.edit_category);
        mCategoryAdapter = new SpinnerCategoryAdapter(this, new ArrayList<CharSequence>());
        mCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategory.setAdapter(mCategoryAdapter);

        new LoadCategoriesTask(this, this).execute();
    }

    private void initDate() {
        int month, date, year;
        if(mIsNewReceipt) {
            month = Calendar.getInstance().get(Calendar.MONTH);
            date = Calendar.getInstance().get(Calendar.DATE);
            year = Calendar.getInstance().get(Calendar.YEAR);
        } else {
            month = mReceipt.getDate().get(Calendar.MONTH);
            date = mReceipt.getDate().get(Calendar.DATE);
            year = mReceipt.getDate().get(Calendar.YEAR);
        }

        mMonth = (Spinner) findViewById(R.id.edit_when_month);
        mDate = (Spinner) findViewById(R.id.edit_when_date);
        mYear = (Spinner) findViewById(R.id.edit_when_year);

        // Setup month Spinner
        String[] months = getResources().getStringArray(R.array.months);
        SpinnerMonthAdapter monthAdapter = new SpinnerMonthAdapter(this, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMonth.setOnItemSelectedListener(onMonthSelected);
        mMonth.setAdapter(monthAdapter);
        mMonth.setSelection(month);

        // Setup date Spinner
        mDatesAdapter = new SpinnerDateAdapter(this, getDatesList(month));
        mDatesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDate.setAdapter(mDatesAdapter);
        mDate.setSelection(mDatesAdapter.getPosition(date));

        // Setup year Spinner
        ArrayAdapter<Integer> yearAdapter = new SpinnerYearAdapter(this, getYearsList());
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mYear.setAdapter(yearAdapter);
        mYear.setSelection(yearAdapter.getPosition(year));
    }

    private void initItems() {
        if(!mIsNewReceipt) {
            new AsyncTask<Void, Void, ArrayList<Item>>() {
                @Override
                protected ArrayList<Item> doInBackground(Void... voids) {
                    DatabaseManager man = new DatabaseManager(EditReceiptActivity.this);
                    return man.getItemsForReceipt(mReceipt.getId());
                }

                @Override
                protected void onPostExecute(ArrayList<Item> items) {
                    if(items.size() > 0) {
                        mItems.clear();
                        mItems.addAll(items);
                        for(int i = 0; i < mItems.size(); i++) {
                            mCurrentTotal += (mItems.get(i).getPrice() * mItems.get(i).getAmount());
                            mNoScrollListView.addItem();
                        }

                        invalidateTotalView();
                    }
                }
            }.execute();
        } else {
            // Initialize list with at least 1 item.
            mItems.add(new Item());
            mNoScrollListView.addItem();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mUndoBar.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mUndoBar.onRestoreInstanceState(savedInstanceState);
    }

    private AdapterView.OnItemSelectedListener onMonthSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            // Update date since number of days vary from month to month.
            mDatesAdapter.clear();
            mDatesAdapter.addAll(getDatesList(position));
            mDatesAdapter.notifyDataSetChanged();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {/* Do nothing */}
    };

    private ArrayList<Integer> getDatesList(int month) {
        return DateUtils.getDatesList(month);
    }

    private ArrayList<Integer> getYearsList() {
        return DateUtils.getYearsList();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.save) {
            save();
        } else if(view.getId() == R.id.cancel) {
            cancel();
        } else if(view == mImage) {
            mCurrentReceiptImage = createReceiptImage();
            Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCurrentReceiptImage));
            startActivityForResult(takePhotoIntent, CAPTURE_IMAGE);
        }
    }

    private void cancel() {
        setResult(Activity.RESULT_CANCELED);
        if(mCurrentReceiptImage != null) {
            mCurrentReceiptImage.delete();
        }
        finish();
    }

    private File createReceiptImage() {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "receipts_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Receipts");
        if(!storageDir.exists()) {
            storageDir.mkdirs();
        }

        try {
            File image = File.createTempFile(imageFileName, ".jpg", storageDir);
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CAPTURE_IMAGE && resultCode == Activity.RESULT_OK) {
            mImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mImage.setImageURI(Uri.fromFile(mCurrentReceiptImage));
            mImage.setOnClickListener(null);

            if(ReceiptsApp.getSettings().hasOneReceiptEmail()) {
                mCheckBoxSend.setEnabled(true);
            }
        }
    }

    private void invalidateTotalView() {
        if(mTotal != null)
            mTotal.setText(DECIMAL_FORMAT.format(mCurrentTotal));
    }

    private boolean validateFields() {
        if(mItems.size() == 0) {
            return false;
        }

        int counter = 0;
        for(Item item : mItems) {
            if(item.getName() != null && !item.getName().isEmpty()) {
                counter++;
            }
        }

        return counter > 0;
    }

    private long getSelectedDate() {
        int month = mMonth.getSelectedItemPosition();
        int date = (Integer) mDate.getSelectedItem();
        int year = (Integer) mYear.getSelectedItem();

        Calendar selectedDate = Calendar.getInstance();
        selectedDate.clear();
        selectedDate.set(Calendar.MONTH, month);
        selectedDate.set(Calendar.DATE, date);
        selectedDate.set(Calendar.YEAR, year);
        return selectedDate.getTimeInMillis();
    }

    private void save() {
        if(validateFields()) {
            if(mReceipt == null) {
                mReceipt = new Receipt();
            }

            mReceipt.setLabel(mItems.get(0).getName());
            mReceipt.setCategory(mCategory.getSelectedItem().toString());
            mReceipt.setDate(getSelectedDate());
            mReceipt.setTotal(mCurrentTotal);
            mReceipt.setSent(false);
            if(mCurrentReceiptImage != null)
                mReceipt.setImageUri(Uri.fromFile(mCurrentReceiptImage));

            if(mCurrentReceiptImage != null) {
                mReceipt.setImageUri(Uri.fromFile(mCurrentReceiptImage));
            }

            // Persist changes to database
            new AsyncTask<Void, Void, Long>() {
                @Override
                protected Long doInBackground(Void... voids) {
                    DatabaseManager man = new DatabaseManager(EditReceiptActivity.this);
                    long receiptId = mReceipt.getId();

                    if(mIsNewReceipt) {
                        receiptId = man.insertReceipt(mReceipt);
                    } else {
                        man.updateReceipt(mReceipt);
                    }

                    for(Item item : mItems) {
                        if(item.getName() != null && !item.getName().isEmpty()) {
                            if(item.getId() == -1) {
                                man.insertItem(item, (int) receiptId);
                            }
                            else {
                                man.updateItem(item);
                            }
                        }
                    }

                    if(mToDeleteItems != null) {
                        for(Item item : mToDeleteItems) {
                            man.deleteItem(item.getId());
                        }
                    }

                    return receiptId;
                }

                @Override
                protected void onPostExecute(Long receiptId) {
                    if(mCheckBoxSend.isChecked()) {
                        Intent sendReceiptService = new Intent(EditReceiptActivity.this, UploadImageService.class);
                        sendReceiptService.putExtra("receipt_id", receiptId);

                        if(mReceipt.getImagePath() != null) {
                            sendReceiptService.putExtra("uri", mReceipt.getImagePath().toString());
                        } else if(mCurrentReceiptImage != null) {
                            sendReceiptService.putExtra("path", mCurrentReceiptImage.getPath());
                        }

                        startService(sendReceiptService);
                    }
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            }.execute();
        } else {
            Toast.makeText(this, "Failed Validation!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFooterClicked() {
        Item newItem = new Item();
        mItems.add(newItem);
        mNoScrollListView.addItem();
    }

    @Override
    public void onUndo(Parcelable token) {
        ListUndoToken t = (ListUndoToken) token;
        mToDeleteItems.remove(t.object);
        mItems.add(t.position, t.object);
        mNoScrollListView.addItemAt(t.position);

        // Update total
        mCurrentTotal += t.object.getAmount()*t.object.getPrice();
        invalidateTotalView();
    }

    @Override
    public void onCategoriesLoaded(ArrayList<String> categories) {
        if(categories.size() > 0) {
            mCategoryAdapter.clear();
            mCategoryAdapter.addAll(categories);
            mCategoryAdapter.notifyDataSetChanged();

            if(!mIsNewReceipt)
                mCategory.setSelection(mCategoryAdapter.getPosition(mReceipt.getCategory()));
        }
    }

    private class ItemsAdapter extends BaseAdapter {
        private ArrayList<Item> mData;
        private final DecimalFormat PRICE_FORMAT = new DecimalFormat("00.00");

        public ItemsAdapter(ArrayList<Item> data) {
            mData = data;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Item getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mData.get(position).getId();
        }

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {
            Item item = getItem(position);
            if(view == null) {
                view = LayoutInflater.from(EditReceiptActivity.this).inflate(R.layout.row_item, viewGroup, false);
            }

            EditText name = (EditText) view.findViewById(R.id.edit_name);
            EditText amount = (EditText) view.findViewById(R.id.edit_multiplier);
            EditText price = (EditText) view.findViewById(R.id.edit_price);
            view.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDeleteItem(position);
                }
            });

            name.setText(item.getName());
            amount.setText(Integer.toString(item.getAmount()));
            price.setText(PRICE_FORMAT.format(item.getPrice()));

            name.addTextChangedListener(new TextWatcher() {
                CharSequence before, after;
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                    this.before = charSequence;
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                    this.after = charSequence;
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    onItemNameChanged(position, this.before, this.after);
                }
            });

            amount.addTextChangedListener(new TextWatcher() {
                int before, after;
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                    if(charSequence.length() > 0) {
                        this.before = Integer.valueOf(charSequence.toString());
                    } else {
                        this.before = 0;
                    }
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                    if(charSequence.length() > 0) {
                        this.after = Integer.valueOf(charSequence.toString());
                    } else {
                        this.after = 0;
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    onItemAmountChanged(position, this.before, this.after);
                }
            });

            price.addTextChangedListener(new TextWatcher() {
                double before, after;
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                    if(charSequence.length() > 0) {
                        this.before = Double.valueOf(charSequence.toString());
                    } else {
                        this.before = 0;
                    }
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                    if(charSequence.length() > 0) {
                        this.after = Double.valueOf(charSequence.toString());
                    } else {
                        this.after = 0;
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    onItemPriceChanged(position, this.before, this.after);
                }
            });

            return view;
        }

        private void onItemNameChanged(int position, CharSequence before, CharSequence after) {
            Item item = mItems.get(position);
            if(item != null) {
                item.setName(after.toString());
            }
        }

        private void onItemAmountChanged(int position, int before, int after) {
            Item item = mItems.get(position);
            if(item != null) {
                int oldAmount = item.getAmount();
                item.setAmount(after);
                mCurrentTotal = (mCurrentTotal-(item.getPrice()*oldAmount) + (item.getPrice()*after));
                invalidateTotalView();
            }
        }

        private void onItemPriceChanged(int position, double before, double after) {
            Item item = mItems.get(position);
            if(item != null) {
                item.setPrice(after);

                mCurrentTotal = (mCurrentTotal - (before*item.getAmount())) + (after*item.getAmount());
                invalidateTotalView();
            }
        }

        private void onDeleteItem(int position) {
            if(mToDeleteItems == null) {
                mToDeleteItems = new ArrayList<Item>();
            }

            Item item = mItems.remove(position);
            mNoScrollListView.removeItemAt(position);

            mToDeleteItems.add(item);

            String message = getResources().getQuantityString(
                    R.plurals.item_deleted, 1, 1);

            mUndoBar.showUndoBar(false, message,
                    new ListUndoToken(position, item)
            );

            // Update total
            mCurrentTotal -= item.getAmount()*item.getPrice();
            invalidateTotalView();
        }
    }
}