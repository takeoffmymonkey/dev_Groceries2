package com.example.android.groceries2.data;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.android.groceries2.EditorActivity;
import com.example.android.groceries2.ItemsFragment;
import com.example.android.groceries2.ListFragment;
import com.example.android.groceries2.R;

import static com.example.android.groceries2.MainActivity.db;
import static com.example.android.groceries2.MainActivity.dbHelper;
import static com.example.android.groceries2.data.GroceriesDbHelper.CHECKED_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.ITEMS_TABLE_NAME;
import static com.example.android.groceries2.data.GroceriesDbHelper.LIST_AMOUNT_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.LIST_ITEM_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.MEASURE_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.MEASURE_TABLE_NAME;
import static com.example.android.groceries2.data.GroceriesDbHelper.NAME_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.ID_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.PRICE_COLUMN;

/**
 * Created by takeoff on 009 09 Jun 17.
 */


public class ItemsCursorAdapter extends CursorAdapter {

    public ItemsCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //Inflate a list item view using the layout specified in items_item.xml
        return LayoutInflater.from(context).inflate(R.layout.item_items, parent, false);
    }


    //This method binds data from cursors' row to the given item layout.
    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {


        //Create text view object for item's name
        TextView itemNameTextView = (TextView) view.findViewById(R.id.item_name);


        final int lightPrimaryColor = view.getResources().getColor(R.color.colorPrimaryLight);



        //Set its name to NAME_COLUMN
        itemNameTextView.setText(cursor.getString(cursor.getColumnIndexOrThrow(NAME_COLUMN)));

        //Set color of the view (according to CHECKED_COLUMN state of the row)
        if (cursor.getInt(cursor.getColumnIndexOrThrow(CHECKED_COLUMN)) == 1)
            view.setBackgroundColor(lightPrimaryColor);
        else view.setBackgroundColor(Color.WHITE);

        //Get ID_COLUMN of current row in int
        final int rowIdInt = cursor.getInt(cursor.getColumnIndexOrThrow(ID_COLUMN));

        //Get PRICE_COLUMN of current row in int
        final float rowPriceFloat = cursor.getFloat(cursor.getColumnIndexOrThrow(PRICE_COLUMN));

        //Set click listener to the view
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Cursor freshItemsTableCursor = db.query(ITEMS_TABLE_NAME,
                        new String[]{CHECKED_COLUMN, NAME_COLUMN, MEASURE_COLUMN},
                        ID_COLUMN + "=?", new String[]{Integer.toString(rowIdInt)},
                        null, null, null);

                //Move cursor to 1st row
                freshItemsTableCursor.moveToFirst();


                //Name of the item
                final String itemName = freshItemsTableCursor.getString(freshItemsTableCursor
                        .getColumnIndex(NAME_COLUMN));

                //Check if the row was checked
                if (freshItemsTableCursor.getInt(freshItemsTableCursor.getColumnIndexOrThrow(CHECKED_COLUMN)) == 0) {
                    //Row wasn't checked
                    //Create alert dialog:
                    //Create alert dialog object
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    //Create inflater object
                    final LayoutInflater inflater = LayoutInflater.from(view.getContext());
                    //Create view object containing dialog_item_amount layout
                    View editItemDialogView = inflater.inflate(R.layout.dialog_item_amount, null);
                    //Create edit text object linked to to editor_price id

                    final NumberPicker numberPicker1 = (NumberPicker) editItemDialogView
                            .findViewById(R.id.dialog_picker_1);
                    numberPicker1.setMaxValue(999);
                    numberPicker1.setMinValue(0);
                    numberPicker1.setValue(1);


                    final NumberPicker numberPicker2 = (NumberPicker) editItemDialogView
                            .findViewById(R.id.dialog_picker_2);
                    numberPicker2.setMaxValue(99);
                    numberPicker2.setMinValue(0);

                    final TextView dialogMeasure = (TextView) editItemDialogView
                            .findViewById(R.id.dialog_measure);

                    int measureInItems = freshItemsTableCursor
                            .getInt(freshItemsTableCursor.getColumnIndex(MEASURE_COLUMN));


                    Cursor cursorForMeasure = db.query(MEASURE_TABLE_NAME,
                            new String[]{MEASURE_COLUMN},
                            ID_COLUMN + "=?", new String[]{Integer.toString(measureInItems)},
                            null, null, null);

                    cursorForMeasure.moveToFirst();

                    String measureInMeasure = cursorForMeasure
                            .getString(cursorForMeasure.getColumnIndex(MEASURE_COLUMN));

                    cursorForMeasure.close();

                    dialogMeasure.setText(measureInMeasure);


                    //Set title of the dialog
                    builder.setTitle("Please set amount of "
                            + itemName)
                            //Set custom view of the dialog
                            .setView(editItemDialogView)
                            //Set ability to press back
                            .setCancelable(true)
                            //Set Ok button with click listener
                            .setPositiveButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            //Set bg to green
                                            view.setBackgroundColor(lightPrimaryColor);

                                            //Create contentValuesItemsTable var to store CHECKED_COLUMN value
                                            ContentValues contentValuesItemsTable
                                                    = new ContentValues();
                                            //Put new value into contentValuesItemsTable
                                            contentValuesItemsTable.put(CHECKED_COLUMN, 1);
                                            //Update checked field
                                            db.update(ITEMS_TABLE_NAME, contentValuesItemsTable,
                                                    ID_COLUMN + "=?",
                                                    new String[]{Integer.toString(rowIdInt)});

                                            int amountPicker1 = numberPicker1.getValue();

                                            int amountPicker2 = numberPicker2.getValue();

                                            String amountString = Integer.toString(amountPicker1) +
                                                    "." + Integer.toString(amountPicker2);

                                            float amount = Float
                                                    .parseFloat(amountString);

                                            //Create contentValuesListTable var
                                            ContentValues contentValuesListTable
                                                    = new ContentValues();
                                            //Put new value into contentValuesItemsTable
                                            contentValuesListTable.put(LIST_ITEM_COLUMN, rowIdInt);
                                            //Put new value into contentValuesItemsTable
                                            contentValuesListTable.put(LIST_AMOUNT_COLUMN, amount);
                                            //Put new value into contentValuesItemsTable
                                            float itemTotalPrice = rowPriceFloat * amount;
                                            contentValuesListTable.put(PRICE_COLUMN, itemTotalPrice);

                                            //int for active version
                                            int activeVersion = dbHelper.getActiveListVersion();

                                            //Check if there activeListTable (other than List_0)
                                            if (activeVersion == 0) {
                                                //No active List table:

                                                //Create active List table
                                                dbHelper.createListTable();

                                                int newActiveVersion = dbHelper.getActiveListVersion();

                                                //Insert new item into List table
                                                db.insert("List_" + newActiveVersion,
                                                        null, contentValuesListTable);

                                                //update total considering new version
                                                dbHelper.updateTotal(newActiveVersion,
                                                        1, itemTotalPrice);


                                            } else {
                                                //There is active table

                                                //Insert new item into List table
                                                db.insert("List_" + activeVersion,
                                                        null, contentValuesListTable);

                                                //update total
                                                dbHelper.updateTotal(activeVersion, 1, itemTotalPrice);
                                            }


                                            //refresh cursors
                                            ItemsFragment.refreshItemsCursor(context, itemName
                                                    + " added to the list", 2);
                                            ListFragment.refreshListCursor(null, null, 0);

                                            //close cursor
                                            freshItemsTableCursor.close();

                                            //Close the dialog window
                                            dialog.cancel();
                                        }
                                    })
                            //Set neutral button (Edit item) with click listener
                            .setNeutralButton("Edit item",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //Redirect user to Editor activity
                                            //Create intent object
                                            Intent intent = new Intent(view.getContext(), EditorActivity.class);
                                            intent.putExtra("ID", rowIdInt);
                                            //Start new activity
                                            view.getContext().startActivity(intent);
                                            //Close the dialog window
                                            dialog.cancel();
                                        }
                                    })
                            //Set cancel button with click listener
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            freshItemsTableCursor.close();

                                            //Close the dialog window
                                            dialog.cancel();
                                        }
                                    });

                    AlertDialog alert = builder.create();
                    alert.show();


                } else {
                    //Row was checked

                    //Set bg to white
                    view.setBackgroundColor(Color.WHITE);

                    //Uncheck it in Items_table:
                    //Create contentValuesItemsTable var to store CHECKED_COLUMN value
                    ContentValues contentValuesItemsTable
                            = new ContentValues();
                    //Put new value into contentValuesItemsTable
                    contentValuesItemsTable.put(CHECKED_COLUMN, 0);
                    //Update checked field
                    db.update(ITEMS_TABLE_NAME, contentValuesItemsTable,
                            ID_COLUMN + "=?",
                            new String[]{Integer.toString(rowIdInt)});

                    //Get currentListTableVersion
                    int currentListTableVersion = dbHelper.getActiveListVersion();
                    //Get currentListTableName
                    String currentListTableName = "List_" + currentListTableVersion;

                    Log.e("WARNING: ", "currentListTableVersion: " + currentListTableVersion);

                    //Get totalPrice of the item
                    Cursor cursorItemsTotalPrice = db.query(currentListTableName,
                            new String[]{PRICE_COLUMN},
                            LIST_ITEM_COLUMN + "=?", new String[]{Integer.toString(rowIdInt)},
                            null, null, null);

                    Log.e("WARNING: ", "cursor len: " + cursorItemsTotalPrice.getCount());

                    //move to 1st row
                    cursorItemsTotalPrice.moveToFirst();
                    //get float of price
                    float itemTotalPrice = cursorItemsTotalPrice.getFloat(cursorItemsTotalPrice.
                            getColumnIndex(PRICE_COLUMN));
                    //close cursor
                    cursorItemsTotalPrice.close();
                    //Update total
                    dbHelper.updateTotal(currentListTableVersion, 0, itemTotalPrice);


                    //Remove item from List table
                    db.delete(currentListTableName,
                            LIST_ITEM_COLUMN + "=?",
                            new String[]{Integer.toString(rowIdInt)});

                    //Check if List table is empty now:
                    //Get proper cursor
                    Cursor listTableCursor = db.query(currentListTableName,
                            //1 column is sufficient
                            new String[]{ID_COLUMN},
                            null, null, null, null, null);
                    if (listTableCursor.getCount() == 0) {
                        //List table is empty
                        //Delete the table
                        dbHelper.deleteListTable(currentListTableVersion);
                    }


                    //Close the cursor
                    listTableCursor.close();


                    ItemsFragment.refreshItemsCursor(context, itemName
                            + " removed from the list", 2);

                    freshItemsTableCursor.close();
                    //refresh cursors

                    ListFragment.refreshListCursor(null, null, 0);

                }

            }


        });


        //Set item to long-clickable
        view.setLongClickable(true);

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //Redirect user to Editor activity
                //Create intent object
                Intent intent = new Intent(view.getContext(), EditorActivity.class);
                intent.putExtra("ID", rowIdInt);
                //Start new activity
                view.getContext().startActivity(intent);
                return true;
            }
        });

    }

}
