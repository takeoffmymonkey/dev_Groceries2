package com.example.android.groceries2.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.groceries2.ListFragment;
import com.example.android.groceries2.MainActivity;
import com.example.android.groceries2.R;

import static com.example.android.groceries2.MainActivity.db;
import static com.example.android.groceries2.MainActivity.dbHelper;
import static com.example.android.groceries2.data.GroceriesDbHelper.CHECKED_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.ID_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.ITEMS_TABLE_NAME;
import static com.example.android.groceries2.data.GroceriesDbHelper.LIST_AMOUNT_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.LIST_ITEM_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.MEASURE_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.MEASURE_TABLE_NAME;
import static com.example.android.groceries2.data.GroceriesDbHelper.NAME_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.PRICE_COLUMN;

/**
 * Created by takeoff on 013 13 Jun 17.
 */

public class ListCursorAdapter extends CursorAdapter {
    public ListCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //Inflate a list item view using the layout specified in item_list.xml
        return LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);
    }


    //This method binds data from cursors' row to the given item layout.
    @Override
    public void bindView(final View view, Context context, Cursor cursor) {

        //Get LIST_ITEM_COLUMN value of the item in List table
        int itemCode = cursor.getInt(cursor.getColumnIndexOrThrow(LIST_ITEM_COLUMN));


        //Get cursor with NAME_COLUMN and ITEMS_MEASURE_COLUMN columns for required ID
        Cursor itemsTableCursor = db.query(ITEMS_TABLE_NAME,
                new String[]{NAME_COLUMN, MEASURE_COLUMN},
                ID_COLUMN + "=?", new String[]{Integer.toString(itemCode)},
                null, null, null);


        //Move cursor to 1st row
        itemsTableCursor.moveToFirst();

        //Save string with proper name
        String name = itemsTableCursor.getString(itemsTableCursor.getColumnIndexOrThrow(NAME_COLUMN));


        //Get code of the items measure
        int measureInItems = itemsTableCursor
                .getInt(itemsTableCursor.getColumnIndexOrThrow(MEASURE_COLUMN));

        //Close itemsTableCursor cursor
        itemsTableCursor.close();

        //Get cursor with MEASURE_MEASURE_COLUMN text from Measure_table
        Cursor measureTableCursor = db.query(MEASURE_TABLE_NAME,
                new String[]{MEASURE_COLUMN},
                ID_COLUMN + "=?", new String[]{Integer.toString(measureInItems)},
                null, null, null);

        //Move cursor to 1st row
        measureTableCursor.moveToFirst();

        //Save string with proper measure
        String measure = measureTableCursor
                .getString(measureTableCursor.getColumnIndexOrThrow(MEASURE_COLUMN));

        //Close measureTableCursor cursor
        measureTableCursor.close();

        //Create text view object
        TextView itemNameTextView = (TextView) view.findViewById(R.id.list_item_name);
        //Set its text
        itemNameTextView.setText(name);

        //Create itemAmount textView object
        TextView itemAmountTextView = (TextView) view.findViewById(R.id.list_item_amount);
        //Get itemAmount value from cursor
        float itemAmount = cursor.getFloat(cursor.getColumnIndexOrThrow(LIST_AMOUNT_COLUMN));
        //Set itemAmount + measure as text to itemAmount textView

        //Get the rounded value
        int itemAmountRound = Math.round(itemAmount);

        //Check if it is round
        if (itemAmount == itemAmountRound) {
            //It is round

            //Check if it is 1 item
            if (measure.equals("items") && itemAmountRound == 1) {
                //it is 1 item

                //set appropriate text
                itemAmountTextView.setText("" + itemAmountRound + " item");

            }
            //Check if it is 1 pack
            else if (measure.equals("packs") && itemAmountRound == 1) {
                //it is 1 item

                //set appropriate text
                itemAmountTextView.setText("" + itemAmountRound + " pack");

            }

        } else {
            //it is not round
            itemAmountTextView.setText(Float.toString(itemAmount) + " " + measure);
        }


        //Create itemPrice textView object
        TextView itemPriceTextView = (TextView) view.findViewById(R.id.list_item_price);
        //Get itemPrice value from cursor
        float itemPrice = cursor.getFloat(cursor.getColumnIndexOrThrow(PRICE_COLUMN));
        //Set itemPriceTextView to the product of item's amount and price
        itemPriceTextView.setText(MainActivity.formatPrice(itemPrice));


        //Get ID_COLUMN of current row in int
        final int rowIdInt = cursor.getInt(cursor.getColumnIndexOrThrow(ID_COLUMN));

        //Set color of the view (according to CHECKED_COLUMN state of the row)
        if (cursor.getInt(cursor.getColumnIndexOrThrow(CHECKED_COLUMN)) == 1)
            view.setBackgroundColor(Color.GREEN);
        else view.setBackgroundColor(Color.WHITE);


        //Set onClickListener
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String activeListName = dbHelper.getLatestListName();

                final Cursor freshListTableCursor = db.query(activeListName,
                        new String[]{CHECKED_COLUMN},
                        ID_COLUMN + "=?", new String[]{Integer.toString(rowIdInt)},
                        null, null, null);

                //Move cursor to 1st row
                freshListTableCursor.moveToFirst();

                //Create ContentValues
                ContentValues contentValues = new ContentValues();

                //Check if the row was checked
                if (freshListTableCursor.getInt(freshListTableCursor.getColumnIndexOrThrow(CHECKED_COLUMN)) == 0) {
                    //Row wasn't checked
                    //Set color
                    view.setBackgroundColor(Color.GREEN);

                    //Update table
                    contentValues.put(CHECKED_COLUMN, 1);
                    db.update(activeListName, contentValues,
                            ID_COLUMN + "=?",
                            new String[]{Integer.toString(rowIdInt)});

                    //Update cursor
                    ListFragment.refreshListCursor();

                } else {
                    //Row was checked
                    //Set color
                    view.setBackgroundColor(Color.WHITE);

                    //Update table
                    contentValues.put(CHECKED_COLUMN, 0);
                    db.update(activeListName, contentValues,
                            ID_COLUMN + "=?",
                            new String[]{Integer.toString(rowIdInt)});

                    //Update cursor
                    ListFragment.refreshListCursor();
                }

                //Close cursor
                freshListTableCursor.close();

            }
        });

    }
}
