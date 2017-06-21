package com.example.android.groceries2;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.R.id.list;
import static com.example.android.groceries2.MainActivity.db;
import static com.example.android.groceries2.data.GroceriesDbHelper.ID_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.ITEMS_TABLE_NAME;
import static com.example.android.groceries2.data.GroceriesDbHelper.LOG_DATE_COMPLETE_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.LOG_DATE_CREATED_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.LOG_TABLE_NAME;
import static com.example.android.groceries2.data.GroceriesDbHelper.LOG_TOTAL_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.MEASURE_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.NAME_COLUMN;


/**
 * Created by takeoff on 006 06 Jun 17.
 */

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Layout for the activity
        setContentView(R.layout.activity_list);

        //Visual elements
        //name textView
        TextView name = (TextView) findViewById(R.id.list_name_var);
        //created textView
        TextView created = (TextView) findViewById(R.id.list_created_var);
        //complete textView
        TextView complete = (TextView) findViewById(R.id.list_complete_var);
        //total textView
        TextView total = (TextView) findViewById(R.id.list_total_var);
        //listView
        ListView listView = (ListView) findViewById(R.id.list_in_activity_list);
        //Floating button to delete the list
        FloatingActionButton fabDeleteList = (FloatingActionButton)
                findViewById(R.id.fab_delete_list);


        //Get listName from intent
        String listName;
        listName = getIntent().getStringExtra("listName");

        //Get data from log table
        Cursor cursorLog = db.query(LOG_TABLE_NAME,
                null,
                NAME_COLUMN + "=?", new String[]{listName},
                null, null, null);

        //Cursor must contain at least 1 row
        if (cursorLog.getCount() > 0) {

            //Move to 1st row
            cursorLog.moveToFirst();

            //Set name
            name.setText(listName);

            //Date formatting object
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy (hh:mm:ss)");

            //String for storing creation date
            long dateCreatedInMs = cursorLog.getLong(cursorLog.getColumnIndexOrThrow(LOG_DATE_CREATED_COLUMN));
            //Convert date to string with proper formatting
            String dateCreatedString = formatter.format(new Date(dateCreatedInMs));
            //Set text to as has been formatted
            created.setText(dateCreatedString);


            //Check if complete column is null
            if (cursorLog.getString(cursorLog.getColumnIndexOrThrow(LOG_DATE_COMPLETE_COLUMN)) == null) {
                //Set text to incomplete
                complete.setText("Incomplete");
            } else {
                //Get date from LOG_DATE_COMPLETE_COLUMN in ms
                long dateCompleteInMs = cursorLog.getLong(cursorLog.getColumnIndexOrThrow(LOG_DATE_COMPLETE_COLUMN));
                //Convert date to string with proper formatting
                String dateCompleteString = formatter.format(new Date(dateCompleteInMs));
                //Set text to as has been formatted
                complete.setText(dateCompleteString);
            }

            //Set text of total
            total.setText(Float.toString(cursorLog.getFloat(cursorLog.getColumnIndex(LOG_TOTAL_COLUMN))));

        }

        //Close cursor
        cursorLog.close();

    }

}
