package com.example.android.groceries2.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.groceries2.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.android.groceries2.data.GroceriesDbHelper.LOG_DATE_COMPLETE_COLUMN;
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

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy (hh:mm:ss)");

        TextView logItemName = (TextView) view.findViewById(R.id.log_item);
        logItemName.setText(cursor.getString(cursor.getColumnIndexOrThrow(NAME_COLUMN)));

        TextView logItemCreateDate = (TextView) view.findViewById(R.id.log_item_date_created);
        long dateCreatedInMs = cursor.getLong(cursor.getColumnIndexOrThrow(LOG_DATE_CREATED_COLUMN));
        String dateCreatedString = formatter.format(new Date(dateCreatedInMs));
        logItemCreateDate.setText("Created: " + dateCreatedString);

        TextView logItemCompleteDate = (TextView) view.findViewById(R.id.log_item_date_complete);
        if (cursor.getString(cursor.getColumnIndexOrThrow(LOG_DATE_COMPLETE_COLUMN)) == null) {
            logItemCompleteDate.setText("Complete: incomplete");
        } else {
            long dateCompleteInMs = cursor.getLong(cursor.getColumnIndexOrThrow(LOG_DATE_COMPLETE_COLUMN));
            String dateCompleteString = formatter.format(new Date(dateCompleteInMs));
            logItemCompleteDate.setText("Created: " + dateCompleteString);
        }

    }
}
