package com.example.android.groceries2.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.groceries2.MainActivity;
import com.example.android.groceries2.R;

import static com.example.android.groceries2.MainActivity.db;
import static com.example.android.groceries2.data.GroceriesDbHelper.ID_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.ITEMS_TABLE_NAME;
import static com.example.android.groceries2.data.GroceriesDbHelper.LIST_AMOUNT_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.LIST_ITEM_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.MEASURE_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.MEASURE_TABLE_NAME;
import static com.example.android.groceries2.data.GroceriesDbHelper.NAME_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.PRICE_COLUMN;

/**
 * Created by takeoff on 021 21 Jun 17.
 */

public class ListLogCursorAdapter extends CursorAdapter {
    public ListLogCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //Inflate a list item view using the layout specified in item_list.xml
        return LayoutInflater.from(context).inflate(R.layout.item_activity_list, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {


        //Create itemPrice textView object
        TextView itemPriceTextView = (TextView) view.findViewById(R.id.activity_list_item_price);
        //Set price
        itemPriceTextView.setText(MainActivity.formatPrice(cursor
                .getFloat(cursor.getColumnIndex(PRICE_COLUMN))));

        //Create checkBox object
        TextView itemInt = (TextView) view.findViewById(R.id.activity_list_item_checkbox);
        //Get item's code
        int itemCode = cursor.getInt(cursor.getColumnIndex(LIST_ITEM_COLUMN));

        //Get cursor with NAME_COLUMN and ITEMS_MEASURE_COLUMN columns for required ID
        Cursor itemsTableCursor = db.query(ITEMS_TABLE_NAME,
                new String[]{NAME_COLUMN, MEASURE_COLUMN},
                ID_COLUMN + "=?", new String[]{Integer.toString(itemCode)},
                null, null, null);

        //Move cursor to 1st row
        itemsTableCursor.moveToFirst();

        //Save string with proper name
        String name = itemsTableCursor.getString(itemsTableCursor.getColumnIndexOrThrow(NAME_COLUMN));
        //Set name
        itemInt.setText(name);

        //Get code of the items measure
        int measureInItems = itemsTableCursor
                .getInt(itemsTableCursor.getColumnIndexOrThrow(MEASURE_COLUMN));

        //Close itemsTableCursor cursor
        itemsTableCursor.close();

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

        //Create itemAmount textView object
        TextView itemAmountTextView = (TextView) view.findViewById(R.id.activity_list_item_amount);
        //Set amount
        itemAmountTextView.setText(Float.toString(cursor.getFloat(cursor.getColumnIndex(LIST_AMOUNT_COLUMN)))
                + " " + measure);


    }
}
