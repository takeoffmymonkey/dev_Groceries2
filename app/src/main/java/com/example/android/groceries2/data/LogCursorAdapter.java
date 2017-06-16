package com.example.android.groceries2.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.groceries2.R;

import static com.example.android.groceries2.data.GroceriesDbHelper.LOG_DATE_CREATED_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.NAME_COLUMN;

/**
 * Created by takeoff on 013 13 Jun 17.
 */

public class LogCursorAdapter extends CursorAdapter {
    public LogCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_log, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView logItemName = (TextView) view.findViewById(R.id.log_item);
        TextView logItemCreationDate = (TextView) view.findViewById(R.id.log_item_date);

        logItemName.setText(cursor.getString(cursor.getColumnIndexOrThrow(NAME_COLUMN)));
        logItemCreationDate.setText(cursor.getString(cursor.getColumnIndexOrThrow(LOG_DATE_CREATED_COLUMN)));


    }
}
