package com.example.android.groceries2.activities;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.groceries2.R;
import com.example.android.groceries2.adapters.ListInfoCursorAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.android.groceries2.activities.MainActivity.db;
import static com.example.android.groceries2.activities.MainActivity.dbHelper;
import static com.example.android.groceries2.db.GroceriesDbHelper.LOG_DATE_COMPLETE_COLUMN;
import static com.example.android.groceries2.db.GroceriesDbHelper.LOG_DATE_CREATED_COLUMN;
import static com.example.android.groceries2.db.GroceriesDbHelper.LOG_TABLE_NAME;
import static com.example.android.groceries2.db.GroceriesDbHelper.LOG_TOTAL_COLUMN;
import static com.example.android.groceries2.db.GroceriesDbHelper.NAME_COLUMN;

/**
 * Created by takeoff on 006 06 Jun 17.
 */

public class ListInfoActivity extends AppCompatActivity {


    private boolean isActive;

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


        FloatingActionButton fabApproveList = (FloatingActionButton)
                findViewById(R.id.fab_approve_list);

        FloatingActionButton fabReactivateList = (FloatingActionButton)
                findViewById(R.id.fab_reactivate_list);

        //Get listNameString from intent
        final String listName = getIntent().getStringExtra("listName");
        final int listVersion = getIntent().getIntExtra("listVersion", 0);

        //Check if this list is active
        if (listVersion == dbHelper.getActiveListVersion()) {
            isActive = true;
            fabApproveList.setVisibility(View.VISIBLE);
            fabDeleteList.setVisibility(View.GONE);
            fabReactivateList.setVisibility(View.GONE);
        } else {
            isActive = false;
            fabApproveList.setVisibility(View.GONE);
            fabDeleteList.setVisibility(View.VISIBLE);
            fabReactivateList.setVisibility(View.VISIBLE);
        }

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
            long dateCreatedInMs = cursorLog.getLong(cursorLog
                    .getColumnIndexOrThrow(LOG_DATE_CREATED_COLUMN));
            //Convert date to string with proper formatting
            String dateCreatedString = formatter.format(new Date(dateCreatedInMs));
            //Set text to as has been formatted
            created.setText(dateCreatedString);


            //Check if complete column is null
            if (cursorLog.getInt(cursorLog.getColumnIndexOrThrow(LOG_DATE_COMPLETE_COLUMN)) == 0) {
                //Set text to incomplete
                complete.setText("incomplete");
                complete.setTextColor(Color.argb(255, 30, 177, 108));
                complete.setTypeface(null, Typeface.BOLD_ITALIC);

            } else {
                //Get date from LOG_DATE_COMPLETE_COLUMN in ms
                long dateCompleteInMs = cursorLog.getLong(cursorLog
                        .getColumnIndexOrThrow(LOG_DATE_COMPLETE_COLUMN));
                //Convert date to string with proper formatting
                String dateCompleteString = formatter.format(new Date(dateCompleteInMs));
                //Set text to as has been formatted
                complete.setText(dateCompleteString);
            }

            //Set text of total
            total.setText(MainActivity.formatPrice(cursorLog.getFloat(cursorLog
                    .getColumnIndex(LOG_TOTAL_COLUMN))));

        }

        //Close cursor
        cursorLog.close();


        //Create cursor for adapter
        Cursor cursor = db.query(listName, null,
                null, null, null, null, null);

        //Create cursor adapter
        ListInfoCursorAdapter cursorAdapter = new ListInfoCursorAdapter(this, cursor, 0);
        //Set adapter to list view
        listView.setAdapter(cursorAdapter);


        fabDeleteList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Create alert dialog object
                AlertDialog.Builder builder = new AlertDialog.Builder(ListInfoActivity.this);
                //Set title of the dialog
                builder.setMessage("Are you sure you want to delete this list?")
                        //Set ability to press back
                        .setCancelable(true)
                        //Set Ok button with click listener
                        .setPositiveButton("Delete",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        dbHelper.deleteListTable(listVersion);

                                        Intent intent = new Intent(ListInfoActivity.this, LogActivity.class);
                                        intent.setFlags(intent.getFlags()|Intent.FLAG_ACTIVITY_NO_HISTORY);
                                        startActivity(intent);

                                        Toast.makeText(ListInfoActivity.this, listName + " deleted",
                                                Toast.LENGTH_SHORT).show();
                                        dialog.cancel();

                                    }
                                })

                        //Set cancel button with click listener
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //Close the dialog window
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alert = builder.create();
                alert.show();


            }
        });


        fabApproveList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ListInfoActivity.this);
                //Set title of the dialog
                builder.setMessage("Mark this list as complete?")
                        //Set ability to press back
                        .setCancelable(true)
                        //Set Ok button with click listener
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                    }
                                })

                        //Set cancel button with click listener
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //Close the dialog window
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });


        fabReactivateList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create alert dialog object
                AlertDialog.Builder builder = new AlertDialog.Builder(ListInfoActivity.this);
                //Set title of the dialog
                builder.setMessage("Reactivate list? Current active list (if exists) will be marked as complete.")
                        //Set ability to press back
                        .setCancelable(true)
                        //Set Ok button with click listener
                        .setPositiveButton("Reactivate",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                    }
                                })

                        //Set cancel button with click listener
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //Close the dialog window
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (isActive) inflater.inflate(R.menu.menu_log_list_active, menu);
        else inflater.inflate(R.menu.menu_log_list, menu);
        return true;
    }


    //Move back to log tab if Up is pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intent = new Intent(ListInfoActivity.this, LogActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
