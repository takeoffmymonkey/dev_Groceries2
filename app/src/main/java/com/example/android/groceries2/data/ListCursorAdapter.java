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

import static com.example.android.groceries2.data.GroceriesDbHelper.LIST_AMOUNT_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.LIST_ITEM_COLUMN;
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

        CheckBox itemNameCheckBox = (CheckBox) view.findViewById(R.id.list_item_checkbox);

        TextView itemAmountTextView = (TextView) view.findViewById(R.id.list_item_amount);

        TextView itemPriceTextView = (TextView) view.findViewById(R.id.list_item_price);

        itemNameCheckBox.setText(cursor.getString(cursor.getColumnIndex(LIST_ITEM_COLUMN)));

        int amount = cursor.getInt(cursor.getColumnIndex(LIST_AMOUNT_COLUMN));

        itemAmountTextView.setText(Integer.toString(amount));

        int price = cursor.getInt(cursor.getColumnIndex(PRICE_COLUMN));

        itemPriceTextView.setText(Integer.toString(price));

    }
}
