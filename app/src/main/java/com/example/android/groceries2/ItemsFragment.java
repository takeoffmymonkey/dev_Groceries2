package com.example.android.groceries2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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


import static com.example.android.groceries2.MainActivity.dbHelper;

import static com.example.android.groceries2.data.GroceriesDbHelper.CHECKED_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.ITEMS_MEASURE_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.ITEMS_TABLE_CREATE_COMMAND;
import static com.example.android.groceries2.data.GroceriesDbHelper.ITEMS_TABLE_DROP_COMMAND;
import static com.example.android.groceries2.data.GroceriesDbHelper.ITEMS_TABLE_NAME;
import static com.example.android.groceries2.data.GroceriesDbHelper.ITEMS_PRICE_COLUMN;

import static com.example.android.groceries2.data.GroceriesDbHelper.NAME_COLUMN;


/**
 * Created by takeoff on 002 02 Jun 17.
 */

public class ItemsFragment extends Fragment {


    private ItemsCursorAdapter itemsCursorAdapter;


    public ItemsFragment() {

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        final Cursor cursor = db.query(ITEMS_TABLE_NAME, null,
                null, null, null, null, null);


        View itemsView = inflater.inflate(R.layout.tab_items, container, false);
        FloatingActionButton fabAddItem =
                (FloatingActionButton) itemsView.findViewById(R.id.fab_add_item_to_db);
        FloatingActionButton fabApproveList =
                (FloatingActionButton) itemsView.findViewById(R.id.fab_approve_list);
        FloatingActionButton fabAddInit = (FloatingActionButton)
                itemsView.findViewById(R.id.fab_add_item_to_db_init);

        fabAddInit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditorActivity.class);
                startActivity(intent);
            }
        });

        fabAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditorActivity.class);
                startActivity(intent);
            }
        });

        fabApproveList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SQLiteDatabase db = dbHelper.getReadableDatabase();
                dbHelper.createOrUpdateActiveListTable(db);
                TabLayout tabhost = (TabLayout) getActivity().findViewById(R.id.tabs);
                tabhost.getTabAt(1).select();
                db.close();
            }
        });


        // Find the ListView which will be populated with the pet data
        GridView itemsGridView = (GridView) itemsView.findViewById(R.id.items_list);


        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = itemsView.findViewById(R.id.items_empty_view);
        itemsGridView.setEmptyView(emptyView);


        itemsCursorAdapter = new ItemsCursorAdapter(getContext(), cursor, 0);
        itemsGridView.setAdapter(itemsCursorAdapter);

        setHasOptionsMenu(true);


        return itemsView;


    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_items, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.settings_option_add_item_dummy:

                String[] names = getResources().getStringArray(R.array.array_auto_name_list);

                for (String i : names) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(NAME_COLUMN, i);
                    contentValues.put(ITEMS_PRICE_COLUMN, Math.random() * 11.4);
                    contentValues.put(ITEMS_MEASURE_COLUMN, 1);
                    db.insert(ITEMS_TABLE_NAME, null, contentValues);
                }


                Cursor cursor = db.query(ITEMS_TABLE_NAME, null, null, null, null, null, null);

                Toast.makeText(getActivity(), "Item added" + "(" + cursor.getCount() + ")", Toast.LENGTH_SHORT)
                        .show();
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

        db.close();
        return super.onOptionsItemSelected(item);
    }


    private class UpdateItem extends AsyncTask<Integer, Void, Boolean> {

        //Actions to perform in main thread before background execusion
        @Override
        protected void onPreExecute() {


        }

        @Override
        protected Boolean doInBackground(Integer... params) {
            int i = params[0];

            ContentValues values = new ContentValues();
            values.put(CHECKED_COLUMN, 1);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            try {
                db.update(ITEMS_TABLE_NAME, values, "_id = ?", new String[]{Integer.toString(i)});
                db.close();
                return true;
            } catch (SQLiteException e) {
                db.close();
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(getActivity(), "Checked", Toast.LENGTH_SHORT).show();
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = db.query(ITEMS_TABLE_NAME, null, null, null, null, null, null);
                itemsCursorAdapter.changeCursor(cursor);
                db.close();
            } else Toast.makeText(getActivity(), "SQL error", Toast.LENGTH_SHORT).show();
        }
    }
}
