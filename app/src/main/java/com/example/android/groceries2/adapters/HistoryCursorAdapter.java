package com.example.android.groceries2.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.groceries2.R;
import com.example.android.groceries2.activities.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.android.groceries2.db.GroceriesDbHelper.LOG_DATE_COMPLETE_COLUMN;
import static com.example.android.groceries2.db.GroceriesDbHelper.LOG_TOTAL_COLUMN;
import static com.example.android.groceries2.db.GroceriesDbHelper.LOG_VERSION_COLUMN;

/**
 * Created by takeoff on 013 13 Jun 17.
 */

public class HistoryCursorAdapter extends CursorAdapter {


    public HistoryCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //Inflate a list item view using the layout specified in item_history.xml.xml
        return LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
    }


    //This method binds data from cursors' row to the given item layout.
    @Override
    public void bindView(final View view, Context context, Cursor cursor) {

        //Date formatting object
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        ImageView itemImage = (ImageView) view.findViewById(R.id.log_item_image);

        //Set name
        TextView logItemName = (TextView) view.findViewById(R.id.log_item_value);
        logItemName.setText(Integer.toString(cursor.getInt(cursor
                .getColumnIndexOrThrow(LOG_VERSION_COLUMN))));

        //Set total
        TextView logTotal = (TextView) view.findViewById(R.id.log_item_total_value);
        logTotal.setText(MainActivity.formatPrice
                (cursor.getFloat(cursor.getColumnIndexOrThrow(LOG_TOTAL_COLUMN))));

        //Create text view for completion date info
        TextView logItemCompleteDate = (TextView) view.findViewById(R.id.log_item_date_complete_value);
        logItemCompleteDate.setTextColor(MainActivity.secondaryTextColor);
        logItemCompleteDate.setTypeface(logItemCompleteDate.getTypeface(), Typeface.NORMAL);

        ColorStateList oldColors = logItemCompleteDate.getTextColors();

        //If there is no record
        if (cursor.getInt(cursor.getColumnIndexOrThrow(LOG_DATE_COMPLETE_COLUMN)) == 0) {

            //Set text to incomplete
            logItemCompleteDate.setText("incomplete");
            logItemCompleteDate.setTextColor(MainActivity.primaryDarkColor);
            logItemCompleteDate.setTypeface(logItemCompleteDate.getTypeface(), Typeface.BOLD_ITALIC);

            Glide.with(context).load(R.drawable.empty_basket).into(itemImage);

        } else {

            logItemCompleteDate.setTextColor(MainActivity.secondaryTextColor);
            logItemCompleteDate.setTypeface(null, Typeface.NORMAL);

            Glide.with(context).load(R.drawable.empty_basket_3).into(itemImage);

            //Get date from LOG_DATE_COMPLETE_COLUMN in ms
            logItemCompleteDate.setTextColor(oldColors);
            long dateCompleteInMs = cursor.getLong(cursor.getColumnIndexOrThrow(LOG_DATE_COMPLETE_COLUMN));
            //Convert date to string with proper formatting
            String dateCompleteString = formatter.format(new Date(dateCompleteInMs));
            //Set text to as has been formatted
            logItemCompleteDate.setText(dateCompleteString);
        }

    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }
}
