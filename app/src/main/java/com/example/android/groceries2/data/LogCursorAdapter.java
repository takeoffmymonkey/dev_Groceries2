package com.example.android.groceries2.data;

import com.example.android.groceries2.ListActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
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
        //Inflate a list item view using the layout specified in item_log.xml
        return LayoutInflater.from(context).inflate(R.layout.item_log, parent, false);
    }


    //This method binds data from cursors' row to the given item layout.
    @Override
    public void bindView(final View view, Context context, Cursor cursor) {

        final String listName = cursor.getString(cursor.getColumnIndexOrThrow(NAME_COLUMN));

        String[] tokens = listName.split("_");
        final int listVersion = Integer.parseInt(tokens[1]);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create object for intent
                Intent intent = new Intent(view.getContext(), ListActivity.class);
                intent.putExtra("listName", listName);
                intent.putExtra("listVersion", listVersion);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                //Create editor activity
                view.getContext().startActivity(intent);
            }
        });

        //Date formatting object
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy (hh:mm:ss)");

        //Create text view for name of the list
        TextView logItemName = (TextView) view.findViewById(R.id.log_item_value);
        //Set its text to NAME_COLUMN of LOG_table
        logItemName.setText(cursor.getString(cursor.getColumnIndexOrThrow(NAME_COLUMN)));

        //Create text view for creation date info
        TextView logItemCreateDate = (TextView) view.findViewById(R.id.log_item_date_created_value);
        //Get date from LOG_DATE_CREATED_COLUMN in ms
        long dateCreatedInMs = cursor.getLong(cursor.getColumnIndexOrThrow(LOG_DATE_CREATED_COLUMN));
        //Convert date to string with proper formatting
        String dateCreatedString = formatter.format(new Date(dateCreatedInMs));
        //Set text to as has been formatted
        logItemCreateDate.setText(dateCreatedString);

        //Create text view for completion date info
        TextView logItemCompleteDate = (TextView) view.findViewById(R.id.log_item_date_complete_value);
        ColorStateList oldColors = logItemCompleteDate.getTextColors();

        //If there is no record
        if (cursor.getInt(cursor.getColumnIndexOrThrow(LOG_DATE_COMPLETE_COLUMN)) == 0) {

            //Set text to incomplete
            logItemCompleteDate.setText("incomplete");
            logItemCompleteDate.setTextColor(Color.argb(255, 30, 177, 108));
            logItemCompleteDate.setTypeface(null, Typeface.BOLD_ITALIC);

        } else {
            //Get date from LOG_DATE_COMPLETE_COLUMN in ms
            logItemCompleteDate.setTextColor(oldColors);
            long dateCompleteInMs = cursor.getLong(cursor.getColumnIndexOrThrow(LOG_DATE_COMPLETE_COLUMN));
            //Convert date to string with proper formatting
            String dateCompleteString = formatter.format(new Date(dateCompleteInMs));
            //Set text to as has been formatted
            logItemCompleteDate.setText(dateCompleteString);
        }

    }
}
