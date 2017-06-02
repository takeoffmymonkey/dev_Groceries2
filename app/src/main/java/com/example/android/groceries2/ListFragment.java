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
import android.widget.TextView;


import com.example.android.groceries2.data.GroceriesDbHelper;

/**
 * Created by takeoff on 002 02 Jun 17.
 */

public class ListFragment extends Fragment {

    private int itemsTotal = 0;

    SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();

    TextView testText;
    TextView testText2;
    View itemsView;

    public ListFragment() {

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        itemsView = inflater.inflate(R.layout.items_list, container, false);


        FloatingActionButton fabAdd = (FloatingActionButton) itemsView.findViewById(R.id.fab_add_item);
        FloatingActionButton fabDelete = (FloatingActionButton) itemsView.findViewById(R.id.fab_delete_db);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateItem();
            }
        });

        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        setHasOptionsMenu(true);

        return itemsView;


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
            case R.id.settings_option_1:
                String s = ("INSERT INTO groceries (" +
                        "_id, name, price, weight, measure) VALUES (" +
                        Integer.toString(itemsTotal + 1) + ", \"Test\"," + " 1, 1, 1);");

                itemsTotal++;
                db.execSQL(s);
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.settings_option_2:

                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void updateItem() {
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
    }
}
