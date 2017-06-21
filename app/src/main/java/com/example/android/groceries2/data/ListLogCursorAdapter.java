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

import com.example.android.groceries2.LogFragment;
import com.example.android.groceries2.R;

import static com.example.android.groceries2.R.id.list_item_amount;
import static com.example.android.groceries2.data.GroceriesDbHelper.LIST_AMOUNT_COLUMN;
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

        Log.e("WARNING: ", Integer.toString(cursor.getCount()));


        //Create checkBox object
        CheckBox itemNameCheckBox = (CheckBox) view.findViewById(R.id.activity_list_item_checkbox);
        Log.e("WARNING: ", cursor.getString(cursor.getColumnIndex(NAME_COLUMN)));
        //Set name
        itemNameCheckBox.setText(cursor.getString(cursor.getColumnIndex(NAME_COLUMN)));

        //Create itemAmount textView object
        TextView itemAmountTextView = (TextView) view.findViewById(R.id.activity_list_item_amount);
        //Set amount
        itemAmountTextView.setText(Integer.toString(cursor.getInt(cursor.getColumnIndex(LIST_AMOUNT_COLUMN))));

        //Create itemPrice textView object
        TextView itemPriceTextView = (TextView) view.findViewById(R.id.activity_list_item_price);
        //Set price
        itemPriceTextView.setText(Float.toString(cursor.getFloat(cursor.getColumnIndex(PRICE_COLUMN))));


    }
}
