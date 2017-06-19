package com.example.android.groceries2.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.example.android.groceries2.R;
import java.text.DecimalFormat;
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
 * Created by takeoff on 013 13 Jun 17.
 */

public class ListCursorAdapter extends CursorAdapter {
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
    public void bindView(View view, Context context, Cursor cursor) {

        //Get LIST_ITEM_COLUMN value of the item in List table
        int itemCode = cursor.getInt(cursor.getColumnIndexOrThrow(LIST_ITEM_COLUMN));

        //Get cursor with NAME_COLUMN and ITEMS_MEASURE_COLUMN columns for required ID
        Cursor itemsTableCursor = db.query(ITEMS_TABLE_NAME,
                new String[]{NAME_COLUMN, MEASURE_COLUMN},
                ID_COLUMN + "=?", new String[]{Integer.toString(itemCode)},
                null, null, null);

        //Move cursor to 1st row
        itemsTableCursor.moveToFirst();

        //Save string with proper name
        String name = itemsTableCursor.getString(itemsTableCursor.getColumnIndexOrThrow(NAME_COLUMN));

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

        //Create checkBox object
        CheckBox itemNameCheckBox = (CheckBox) view.findViewById(R.id.list_item_checkbox);
        //Set its text
        itemNameCheckBox.setText(name);

        //Create itemAmount textView object
        TextView itemAmountTextView = (TextView) view.findViewById(R.id.list_item_amount);
        //Get itemAmount value from cursor
        int itemAmount = cursor.getInt(cursor.getColumnIndexOrThrow(LIST_AMOUNT_COLUMN));
        //Set itemAmount + measure as text to itemAmount textView
        itemAmountTextView.setText(Integer.toString(itemAmount) + " " + measure);

        //Create itemPrice textView object
        TextView itemPriceTextView = (TextView) view.findViewById(R.id.list_item_price);
        //Get itemPrice value from cursor
        float itemPrice = cursor.getFloat(cursor.getColumnIndexOrThrow(PRICE_COLUMN));
        //Create proper decimal format object
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        //Set itemPriceTextView to the product of item's amount and price
        itemPriceTextView.setText("~" + decimalFormat.format(itemPrice * itemAmount) + " грн");

    }
}
