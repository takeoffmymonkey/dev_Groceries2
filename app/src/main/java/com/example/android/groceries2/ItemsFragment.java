package com.example.android.groceries2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.example.android.groceries2.data.ItemsCursorAdapter;

import static com.example.android.groceries2.MainActivity.db;

import static com.example.android.groceries2.data.GroceriesDbHelper.CHECKED_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.ITEMS_TABLE_CREATE_COMMAND;
import static com.example.android.groceries2.data.GroceriesDbHelper.ITEMS_TABLE_DROP_COMMAND;
import static com.example.android.groceries2.data.GroceriesDbHelper.ITEMS_TABLE_NAME;
import static com.example.android.groceries2.data.GroceriesDbHelper.MEASURE_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.NAME_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.PRICE_COLUMN;


/**
 * Created by takeoff on 002 02 Jun 17.
 */

public class ItemsFragment extends Fragment {

    //Create ItemsCursorAdapter link
    static ItemsCursorAdapter itemsCursorAdapter;

    //Required empty constructor
    public ItemsFragment() {

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        //Create the view object and inflate in with tab_items layout
        View itemsView = inflater.inflate(R.layout.tab_items, container, false);

        //Create floating action button for adding 1 item when list is empty
        FloatingActionButton fabAddInit = (FloatingActionButton)
                itemsView.findViewById(R.id.fab_add_item_to_db_init);
        //Set click listener on it
        fabAddInit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create object for intent
                Intent intent = new Intent(getActivity(), EditorActivity.class);
                //Create editor activity
                startActivity(intent);
            }
        });

        //Create floating action button for adding 1 item
        FloatingActionButton fabAddItem =
                (FloatingActionButton) itemsView.findViewById(R.id.fab_add_item_to_db);
        //Set click listener on it
        fabAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create object for intent
                Intent intent = new Intent(getActivity(), EditorActivity.class);
                //Create editor activity
                startActivity(intent);
            }
        });

        //Create floating action button for approving the list
        FloatingActionButton fabApproveList =
                (FloatingActionButton) itemsView.findViewById(R.id.fab_approve_list);
        //Set click listener on it
        fabApproveList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create Tablayout object that hold all tabs
                TabLayout tabHost = (TabLayout) getActivity().findViewById(R.id.tabs);
                //Make it open a proper tab
                tabHost.getTabAt(1).select();
            }
        });


        //Find the gridView to hold items
        GridView itemsGridView = (GridView) itemsView.findViewById(R.id.items_list);

        //Find empty view when nothing to show
        View emptyView = itemsView.findViewById(R.id.items_empty_view);
        //Set it to gridView
        itemsGridView.setEmptyView(emptyView);

        //Create cursor
        Cursor cursor = db.query(ITEMS_TABLE_NAME, null, null, null, null, null, null);

        itemsCursorAdapter = new ItemsCursorAdapter(getContext(), cursor, 0);
        itemsGridView.setAdapter(itemsCursorAdapter);


        //This fragment has options menu
        setHasOptionsMenu(true);

        //Return fragment's view
        return itemsView;

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_items, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.settings_option_populate_list:

                String[] names = getResources().getStringArray(R.array.array_auto_name_list);
                int[] measures = getResources().getIntArray(R.array.array_auto_measure_list);

                float[] prices = {
                        78.95f,
                        7.45f,
                        6.35f,
                        5.4f,
                        16.99f,
                        33.95f,
                        35.99f,
                        8.85f,
                        10.46f,
                        17.7f,
                        29.95f,
                        14.45f,
                        20.95f,
                        12.69f,
                        10.45f,
                        20.6f,
                        20.69f,
                        44.53f,
                        9.95f,
                        24.73f};

                for (int i = 0; i < prices.length; i++) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(NAME_COLUMN, names[i]);
                    contentValues.put(PRICE_COLUMN, prices[i]);
                    contentValues.put(MEASURE_COLUMN, measures[i]);
                    db.insert(ITEMS_TABLE_NAME, null, contentValues);
                }

                Cursor cursor = db.query(ITEMS_TABLE_NAME, null, null, null, null, null, null);

                itemsCursorAdapter.changeCursor(cursor);

                return true;
            // Respond to a click on the "Delete all entries" menu option

            case R.id.settings_option_check_item:
                new UpdateItem().execute(1);

                return true;


            case R.id.settings_option_add_item:
                Intent intent = new Intent(getActivity(), EditorActivity.class);
                startActivity(intent);
                return true;

            case R.id.settings_option_delete_all_items:


                db.execSQL(ITEMS_TABLE_DROP_COMMAND);
                db.execSQL(ITEMS_TABLE_CREATE_COMMAND);

                Toast.makeText(getActivity(), "All items successfully deleted!", Toast.LENGTH_SHORT)
                        .show();
                cursor = db.query(ITEMS_TABLE_NAME, null, null, null, null, null, null);

                itemsCursorAdapter.changeCursor(cursor);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class UpdateItem extends AsyncTask<Integer, Void, Boolean> {

        //Actions to perform in main thread before background execusion
        @Override
        protected void onPreExecute() {


        }

        //Actions to perform on background thread
        @Override
        protected Boolean doInBackground(Integer... params) {
            int i = params[0];

            ContentValues values = new ContentValues();
            values.put(CHECKED_COLUMN, 1);
            try {
                db.update(ITEMS_TABLE_NAME, values, "_id = ?", new String[]{Integer.toString(i)});
                return true;
            } catch (SQLiteException e) {
                return false;
            }

        }


        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                refreshItemsCursor();
            } else Toast.makeText(getActivity(), "SQL error", Toast.LENGTH_SHORT).show();
        }
    }


    public static void refreshItemsCursor() {
        Cursor cursor = db.query(ITEMS_TABLE_NAME, null, null, null, null, null, null);
        itemsCursorAdapter.changeCursor(cursor);
    }


}
