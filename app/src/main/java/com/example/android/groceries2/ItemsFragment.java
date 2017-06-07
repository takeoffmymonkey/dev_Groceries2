package com.example.android.groceries2;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.groceries2.data.GroceriesDbHelper;


import static com.example.android.groceries2.data.GroceriesDbHelper.GROCERIES_TABLE_CREATE;
import static com.example.android.groceries2.data.GroceriesDbHelper.GROCERIES_TABLE_DROP;

/**
 * Created by takeoff on 002 02 Jun 17.
 */

public class ItemsFragment extends Fragment {

    private int itemsTotal = 0;

    SQLiteDatabase db = MainActivity.dbHelper.getReadableDatabase();

    View itemsView;

    public ItemsFragment() {

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        Cursor cursor = db.query(GroceriesDbHelper.TABLE_GROCERIES, new String[]{"_id", "name"},
                null, null, null, null, null);
        itemsTotal = cursor.getCount();

        if (itemsTotal == 0) {
            itemsView = inflater.inflate(R.layout.empty, container, false);

            FloatingActionButton fabAddInit = (FloatingActionButton)
                    itemsView.findViewById(R.id.fab_add_item_to_db_init);

            fabAddInit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), EditorActivity.class);
                    startActivity(intent);
                }
            });

            TextView textView = (TextView) itemsView.findViewById(R.id.empty_text);
            TextView textSubView = (TextView) itemsView.findViewById(R.id.empty_text_sub);


            textView.setText("No added items");
            textSubView.setText("Add an item with + button");


        } else {


            itemsView = inflater.inflate(R.layout.tab_items, container, false);
            FloatingActionButton fabAdd =
                    (FloatingActionButton) itemsView.findViewById(R.id.fab_add_item_to_db);
            FloatingActionButton fabDelete =
                    (FloatingActionButton) itemsView.findViewById(R.id.fab_approve_list);

            fabAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //updateItem();
                }
            });

            fabDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });


            // Find the ListView which will be populated with the pet data
            //android.widget.ListView{4283e3b8 V.ED.VC. ......I. 0,0-0,0 #7f0e0073 app:id/list}
            ListView itemsListView = (ListView) itemsView.findViewById(R.id.items_list);


            // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
            /*View emptyView = getActivity().findViewById(R.id.empty_view);
            itemsListView.setEmptyView(emptyView);*/

            // Setup an Adapter to create a list item for each row of pet data in the Cursor.
            // There is no pet data yet (until the loader finishes) so pass in null for the Cursor.
            CursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item, cursor,
                    new String[]{"name"}, new int[]{R.id.item_name}, 0);

            itemsListView.setAdapter(simpleCursorAdapter);


        }

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
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.settings_option_add_item_dummy:

                String s = ("INSERT INTO groceries (" +
                        "_id, name, price, weight, measure) VALUES (" +
                        Integer.toString(itemsTotal + 1) + ", \"Test\"," + " 1, 1, 1);");

                itemsTotal++;
                db.execSQL(s);

                Cursor cursor = db.query(GroceriesDbHelper.TABLE_GROCERIES, null, null, null, null, null, null);

                Toast.makeText(getActivity(), "Item added" + "(" + cursor.getCount() + ")", Toast.LENGTH_SHORT)
                        .show();
                getActivity().recreate();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.settings_option_add_item:
                Intent intent = new Intent(getActivity(), EditorActivity.class);
                startActivity(intent);
                return true;

            case R.id.settings_option_delete_all_items:

                db.execSQL(GROCERIES_TABLE_DROP);
                db.execSQL(GROCERIES_TABLE_CREATE);
                itemsTotal = 0;
                Toast.makeText(getActivity(), "All items successfully deleted!", Toast.LENGTH_SHORT)
                        .show();


                getActivity().recreate();
                return true;
        }


        return super.onOptionsItemSelected(item);
    }




/*    public void updateItem() {
        Cursor cursor = db.query(GroceriesDbHelper.TABLE_GROCERIES, null, null, null, null, null, null);

        testText = (TextView) itemsView.findViewById(R.id.test_text_field);
        testText2 = (TextView) itemsView.findViewById(R.id.test_text_field2);


        cursor.moveToFirst();
        int itemId = cursor.getInt(cursor.getColumnIndex("_id"));
        String itemName = cursor.getString(cursor.getColumnIndex(GroceriesDbHelper.ITEM_NAME));
        int itemPrice = cursor.getInt(cursor.getColumnIndex(GroceriesDbHelper.ITEM_PRICE));
        int itemWeight = cursor.getInt(cursor.getColumnIndex(GroceriesDbHelper.ITEM_WEIGHT));
        int itemMeasure = cursor.getInt(cursor.getColumnIndex(GroceriesDbHelper.ITEM_MEASURE));


        testText.setText("ID:" + itemId + " Name:" + itemName + " Price:"
                + itemPrice + " Weight: " + itemWeight + " Measure: " + itemMeasure);
        testText2.setText("Rows:" + cursor.getCount());


        cursor.close();
    }*/
}
