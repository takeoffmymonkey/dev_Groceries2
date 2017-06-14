package com.example.android.groceries2;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.groceries2.data.ListCursorAdapter;
import com.example.android.groceries2.data.LogCursorAdapter;

import static com.example.android.groceries2.data.GroceriesDbHelper.LIST_AMOUNT_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.LIST_TABLE_NAME;
import static com.example.android.groceries2.data.GroceriesDbHelper.LOG_TABLE_NAME;

/**
 * Created by takeoff on 002 02 Jun 17.
 */

public class LogFragment extends Fragment {

    SQLiteDatabase db = MainActivity.dbHelper.getReadableDatabase();

    View logView;
    LogCursorAdapter logCursorAdapter;


    public LogFragment() {

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Cursor cursor = db.query(LOG_TABLE_NAME, null,
                null, null, null, null, null);

        logView = inflater.inflate(R.layout.tab_log, container, false);


        // Find the ListView which will be populated with the pet data
        ListView logListView = (ListView) logView.findViewById(R.id.log_list);


        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.

        View emptyView = logView.findViewById(R.id.log_empty_view);

        logCursorAdapter = new LogCursorAdapter(getContext(), cursor, 0);

        logListView.setEmptyView(emptyView);
        logListView.setAdapter(logCursorAdapter);


        setHasOptionsMenu(true);

        return logView;


    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_log, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.settings_option_delete_all_lists:
                return true;
            // Respond to a click on the "Delete all entries" menu option

        }
        return super.onOptionsItemSelected(item);
    }


}
