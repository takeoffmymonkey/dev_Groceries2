package com.example.android.groceries2.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.groceries2.R;
import com.example.android.groceries2.activities.MainActivity;

import static com.example.android.groceries2.activities.MainActivity.db;
import static com.example.android.groceries2.activities.MainActivity.dbHelper;
import static com.example.android.groceries2.db.GroceriesDbHelper.CHECKED_COLUMN;
import static com.example.android.groceries2.db.GroceriesDbHelper.ID_COLUMN;
import static com.example.android.groceries2.db.GroceriesDbHelper.IMAGE_COLUMN;
import static com.example.android.groceries2.db.GroceriesDbHelper.ITEMS_TABLE_NAME;
import static com.example.android.groceries2.db.GroceriesDbHelper.LIST_AMOUNT_COLUMN;
import static com.example.android.groceries2.db.GroceriesDbHelper.LIST_ITEM_COLUMN;
import static com.example.android.groceries2.db.GroceriesDbHelper.MEASURE_COLUMN;
import static com.example.android.groceries2.db.GroceriesDbHelper.MEASURE_TABLE_NAME;
import static com.example.android.groceries2.db.GroceriesDbHelper.PRICE_COLUMN;
import static com.example.android.groceries2.fragments.ItemsFragment.refreshItemsCursor;
import static com.example.android.groceries2.fragments.ListFragment.refreshListCursor;

/**
 * Created by takeoff on 013 13 Jun 17.
 */

public class ListCursorAdapter extends CursorAdapter {


    boolean isChecked;

    public ListCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //Inflate a list item view using the layout specified in item_list.xml
        return LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);
    }


    //This method binds data from cursors' row to the given item layout.
    @Override
    public void bindView(final View view, Context context, Cursor cursor) {


        int isCheckedInt = cursor.getInt(cursor.getColumnIndex(CHECKED_COLUMN));

        if (isCheckedInt == 0) isChecked = false;
        else isChecked = true;

        //Get LIST_ITEM_COLUMN value of the item in List table
        String itemName = cursor.getString(cursor.getColumnIndexOrThrow(LIST_ITEM_COLUMN));


        //Get code of the items measure
        int measureInItems = cursor
                .getInt(cursor.getColumnIndexOrThrow(MEASURE_COLUMN));


        //Set item's image
        ImageView itemImage = (ImageView) view.findViewById(R.id.list_item_image);
        int itemImageInt = cursor.getInt(cursor.getColumnIndex(IMAGE_COLUMN));


        String imageName = MainActivity.images[itemImageInt - 1];

        Glide.with(context)
                .load(context.getResources()
                        .getIdentifier(imageName, "drawable", context.getPackageName()))
                .into(itemImage);


        //Get cursor with MEASURE_MEASURE_COLUMN text from Measure_table
        Cursor measureTableCursor = db.query(MEASURE_TABLE_NAME,
                new String[]{MEASURE_COLUMN},
                ID_COLUMN + "=?", new String[]{Integer.toString(measureInItems)},
                null, null, null);

        //Move cursor to 1st row
        measureTableCursor.moveToFirst();

        //Save string with proper measure
        String measure = measureTableCursor
                .getString(measureTableCursor.getColumnIndexOrThrow(MEASURE_COLUMN));

        //Close measureTableCursor cursor
        measureTableCursor.close();

        //Create text view object
        final TextView itemNameTextView = (TextView) view.findViewById(R.id.list_item_name);
        //Set its text
        itemNameTextView.setText(itemName);

        //Create itemAmount textView object
        TextView itemAmountTextView = (TextView) view.findViewById(R.id.list_item_amount);
        //Get itemAmount value from cursor
        float itemAmount = cursor.getFloat(cursor.getColumnIndexOrThrow(LIST_AMOUNT_COLUMN));
        //Set itemAmount + measure as text to itemAmount textView

        //Get the rounded value
        int itemAmountRound = Math.round(itemAmount);

        //Check if it is round
        if (itemAmount == itemAmountRound) {
            //It is round

            //Check if it is 1 item
            if (measure.equals("items") && itemAmountRound == 1) {
                //it is 1 item

                //set appropriate text
                itemAmountTextView.setText("" + itemAmountRound + " item");

            } else {
                //It is round but not 1
                itemAmountTextView.setText(itemAmountRound + " " + measure);

            }


        } else {
            //it is not round
            itemAmountTextView.setText(Float.toString(itemAmount) + " " + measure);
        }


        //Create itemPrice textView object
        TextView itemPriceTextView = (TextView) view.findViewById(R.id.list_item_price);
        //Get itemPrice value from cursor
        float itemPrice = cursor.getFloat(cursor.getColumnIndexOrThrow(PRICE_COLUMN));
        //Set itemPriceTextView to the product of item's amount and price
        itemPriceTextView.setText("(" + MainActivity.formatPrice(itemPrice) + ")");


        //Get ID_COLUMN of current row in int
        final int rowIdInt = cursor.getInt(cursor.getColumnIndexOrThrow(ID_COLUMN));

        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.list_item_checkbox);

        //Set color of the view (according to CHECKED_COLUMN state of the row)
        if (isChecked) {
            view.setBackgroundColor(MainActivity.primaryLightColor);
            itemNameTextView.setPaintFlags(itemNameTextView.getPaintFlags()
                    | Paint.STRIKE_THRU_TEXT_FLAG);
            checkBox.setChecked(true);

        } else {
            view.setBackgroundColor(Color.WHITE);
            itemNameTextView.setPaintFlags(0);
            checkBox.setChecked(false);
        }


        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onClicking(rowIdInt, checkBox, view, itemNameTextView);

            }
        });

        //Set onClickListener
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClicking(rowIdInt, checkBox, view, itemNameTextView);
            }
        });


        //Set item to long-clickable
        view.setLongClickable(true);

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                //Close snack if there is one open
                if (MainActivity.snackOn && MainActivity.snackBar != null) {
                    MainActivity.snackBar.dismiss();
                    MainActivity.snackLines = 0;
                    MainActivity.setSnackOnState(false, null);
                }


                //Update Items table
                ContentValues contentValuesItemsTable
                        = new ContentValues();
                contentValuesItemsTable.put(CHECKED_COLUMN, 0);
                db.update(ITEMS_TABLE_NAME, contentValuesItemsTable,
                        ID_COLUMN + "=?",
                        new String[]{Integer.toString(rowIdInt)});


                //refresh cursors
                refreshItemsCursor(null, null, 0);
                refreshListCursor(null, null, 0);

                return true;
            }
        });


    }

    private void onClicking(int rowIdInt, CheckBox checkBox, View view, TextView itemNameTextView) {

        if (MainActivity.snackOn && MainActivity.snackBar != null) {
            MainActivity.snackBar.dismiss();
            MainActivity.snackLines = 0;
            MainActivity.setSnackOnState(false, null);
            return;
        }

        ContentValues contentValues = new ContentValues();

        String activeListName = dbHelper.getLatestListName();

        //view was checked
        if (isChecked) {

            view.setBackgroundColor(MainActivity.iconsColor);

            itemNameTextView.setPaintFlags(0);

            checkBox.setChecked(false);

            contentValues.put(CHECKED_COLUMN, 0);

        }

        //view was unchecked
        else {

            view.setBackgroundColor(MainActivity.primaryLightColor);

            itemNameTextView.setPaintFlags(itemNameTextView.getPaintFlags()
                    | Paint.STRIKE_THRU_TEXT_FLAG);


            checkBox.setChecked(true);

            contentValues.put(CHECKED_COLUMN, 1);

        }


        //Update table
        db.update(activeListName, contentValues,
                ID_COLUMN + "=?",
                new String[]{Integer.toString(rowIdInt)});

        //Update check state
        isChecked = !isChecked;

        refreshListCursor(null, null, 0);

    }
}
