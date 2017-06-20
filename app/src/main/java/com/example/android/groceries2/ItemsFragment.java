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
import android.util.Log;
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

/*        //Create floating action button for approving the list
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
        });*/


        //Find the gridView to hold items
        GridView itemsGridView = (GridView) itemsView.findViewById(R.id.items_list);

        //Find empty view when nothing to show
        View emptyView = itemsView.findViewById(R.id.items_empty_view);
        //Set it to gridView
        itemsGridView.setEmptyView(emptyView);

        //Create cursor
        Cursor cursor = db.query(ITEMS_TABLE_NAME, null, null, null, null, null, null);

        //Create cursor adapter object and pass cursor there
        itemsCursorAdapter = new ItemsCursorAdapter(getContext(), cursor, 0);

        //Set adapter to the grid view
        itemsGridView.setAdapter(itemsCursorAdapter);

        //This fragment has options menu
        setHasOptionsMenu(true);

        //Return fragment's view
        return itemsView;

    }


    //Set menu layout
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_items, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    //When option from menu is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.settings_option_populate_list:
                //TODO: 019 19 Jun 17 move this to dbHelper
                //Get array of items' names from resources
                String[] names = getResources().getStringArray(R.array.array_auto_name_list);
                //Get array of items' measures from resources
                int[] measures = getResources().getIntArray(R.array.array_auto_measure_list);
                //Create array of items' prices
                float[] prices = {
                        78.95f, //Филе кур.
                        7.45f, // Батон 450
                        6.35f, // Хлебцы (Своя) 100
                        5.4f, // Мария (Своя) 170
                        16.99f, // Мария (Прем) 200
                        33.95f, // Сыр диетич. 185
                        35.99f, // Сыр топл. (Прем) 200
                        8.85f, // Лактония 400
                        10.46f, // Закваска (Фанни) 450
                        17.7f, // Закваска (Ягот) 900
                        29.95f, // Бананы
                        9.89f, // Кабачки
                        14.95f, // Огурцы
                        12.69f, // Морковь
                        10.45f, // Овсянка (Своя) 500
                        20.6f, // Овсянка (Карап) 200
                        20.69f, // Хлопья греч. (Сто) 400
                        44.53f, // Хлопья греч. (Ново) 800
                        9.95f, // Зубная щетка
                        24.73f}; // Беруши

                for (int i = 0; i < prices.length; i++) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(NAME_COLUMN, names[i]);
                    contentValues.put(PRICE_COLUMN, prices[i]);
                    contentValues.put(MEASURE_COLUMN, measures[i] + 1);
                    db.insert(ITEMS_TABLE_NAME, null, contentValues);
                }

                refreshItemsCursor();

                return true;


            case R.id.settings_option_add_item:
                Intent intent = new Intent(getActivity(), EditorActivity.class);
                startActivity(intent);
                return true;

            // Respond to a click on the "Delete all entries" menu option
            case R.id.settings_option_delete_all_items:


                db.execSQL(ITEMS_TABLE_DROP_COMMAND);
                db.execSQL(ITEMS_TABLE_CREATE_COMMAND);

                Toast.makeText(getActivity(), "All items successfully deleted!", Toast.LENGTH_SHORT)
                        .show();
                refreshItemsCursor();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public static void refreshItemsCursor() {

        class NewItemsCursor extends AsyncTask<Integer, Void, Cursor> {

            //Actions to perform in main thread before background execusion
            @Override
            protected void onPreExecute() {
            }

            //Actions to perform on background thread
            @Override
            protected Cursor doInBackground(Integer... params) {
                Cursor cursor = db.query(ITEMS_TABLE_NAME, null, null, null, null, null, null);
                return cursor;

            }

            @Override
            protected void onPostExecute(Cursor cursor) {
                itemsCursorAdapter.changeCursor(cursor);
            }
        }

        new NewItemsCursor().execute(1);

    }


}
