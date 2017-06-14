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
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.groceries2.data.ItemsCursorAdapter;
import com.example.android.groceries2.data.ListCursorAdapter;

import java.util.List;

import static com.example.android.groceries2.data.GroceriesDbHelper.ITEMS_TABLE_NAME;
import static com.example.android.groceries2.data.GroceriesDbHelper.LIST_TABLE_CREATE_COMMAND;
import static com.example.android.groceries2.data.GroceriesDbHelper.LIST_TABLE_DROP_COMMAND;
import static com.example.android.groceries2.data.GroceriesDbHelper.LIST_TABLE_NAME;

/**
 * Created by takeoff on 002 02 Jun 17.
 */

public class ListFragment extends Fragment {
    SQLiteDatabase db = MainActivity.dbHelper.getReadableDatabase();

    View listView;

    ListCursorAdapter listCursorAdapter;

    public ListFragment() {

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Cursor cursor = db.query(LIST_TABLE_NAME, null,
                null, null, null, null, null);

        listView = inflater.inflate(R.layout.tab_list, container, false);

        FloatingActionButton fabAddInit =
                (FloatingActionButton) listView.findViewById(R.id.fab_complete_list);

        // Find the ListView which will be populated with the pet data
        ListView listListView = (ListView) listView.findViewById(R.id.list_list);


        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.

        View emptyView = listView.findViewById(R.id.list_empty_view);

        listCursorAdapter = new ListCursorAdapter(getContext(), cursor, 0);

        listListView.setEmptyView(emptyView);
        listListView.setAdapter(listCursorAdapter);


        setHasOptionsMenu(true);

        return listView;

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.settings_option_mark_as_complete:
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.settings_option_delete_list:
                db.execSQL(LIST_TABLE_DROP_COMMAND);
                db.execSQL(LIST_TABLE_CREATE_COMMAND);
                Cursor cursor = db.query(LIST_TABLE_NAME, null,
                        null, null, null, null, null);
                listCursorAdapter.changeCursor(cursor);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
