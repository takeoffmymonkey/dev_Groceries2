package com.example.android.groceries2.data;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.groceries2.R;

import static com.example.android.groceries2.MainActivity.db;
import static com.example.android.groceries2.data.GroceriesDbHelper.ID_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.ITEMS_MEASURE_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.ITEMS_TABLE_NAME;
import static com.example.android.groceries2.data.GroceriesDbHelper.LIST_AMOUNT_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.LIST_ITEM_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.MEASURE_MEASURE_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.MEASURE_TABLE_NAME;
import static com.example.android.groceries2.data.GroceriesDbHelper.NAME_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.PRICE_COLUMN;

/**
 * Created by takeoff on 013 13 Jun 17.
 */

public class ListCursorAdapter extends CursorAdapter {
    public ListCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        //Get LIST_ITEM_COLUMN value of the item in List table
        int itemCode = cursor.getInt(cursor.getColumnIndex(LIST_ITEM_COLUMN));

        //Get cursor with NAME_COLUMN and ITEMS_MEASURE_COLUMN columns for required ID
        Cursor itemsTableCursor = db.query(ITEMS_TABLE_NAME,
                new String[]{NAME_COLUMN, ITEMS_MEASURE_COLUMN},
                ID_COLUMN + "=?", new String[]{Integer.toString(itemCode)},
                null, null, null);

        //Save string with proper name
        String name = itemsTableCursor.getString(itemsTableCursor.getColumnIndex(NAME_COLUMN));

        //Get code of the items measure
        int measureInItems = itemsTableCursor
                .getInt(itemsTableCursor.getColumnIndex(ITEMS_MEASURE_COLUMN));

        //Close itemsTableCursor cursor
        itemsTableCursor.close();

        //Get cursor with MEASURE_MEASURE_COLUMN text from Measure_table
        Cursor measureTableCursor = db.query(MEASURE_TABLE_NAME,
                new String[]{MEASURE_MEASURE_COLUMN},
                ID_COLUMN + "=?", new String[]{Integer.toString(measureInItems)},
                null, null, null);

        //Save string with proper measure
        String measure = measureTableCursor
                .getString(measureTableCursor.getColumnIndex(MEASURE_MEASURE_COLUMN));

        //Close measureTableCursor cursor
        measureTableCursor.close();

        //Create checkBox object
        CheckBox itemNameCheckBox = (CheckBox) view.findViewById(R.id.list_item_checkbox);
        //Set its text
        itemNameCheckBox.setText(name);

        //Create itemAmount textView object
        TextView itemAmountTextView = (TextView) view.findViewById(R.id.list_item_amount);
        //Get itemAmount value from cursor
        int itemAmount = cursor.getInt(cursor.getColumnIndex(LIST_AMOUNT_COLUMN));
        //Set itemAmount + measure as text to itemAmount textView
        itemAmountTextView.setText(Integer.toString(itemAmount) + " " + measure);

        //Create itemPrice textView object
        TextView itemPriceTextView = (TextView) view.findViewById(R.id.list_item_price);
        //Get itemPrice value from cursor
        int itemPrice = cursor.getInt(cursor.getColumnIndex(PRICE_COLUMN));
        //Set total itemPrice as product of itemPrice and ItemAmount to itemPrice textView
        itemPriceTextView.setText(Integer.toString(itemPrice * itemAmount));

    }
}
