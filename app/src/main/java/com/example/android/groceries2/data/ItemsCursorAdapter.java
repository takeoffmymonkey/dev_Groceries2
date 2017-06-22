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
import android.widget.EditText;
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

        //Set its name to NAME_COLUMN
        itemNameTextView.setText(cursor.getString(cursor.getColumnIndexOrThrow(NAME_COLUMN)));

        //Set color of the view (according to CHECKED_COLUMN state of the row)
        if (cursor.getInt(cursor.getColumnIndexOrThrow(CHECKED_COLUMN)) == 1)
            view.setBackgroundColor(Color.GREEN);
        else view.setBackgroundColor(Color.WHITE);

        //Get ID_COLUMN of current row in int
        final int rowIdInt = cursor.getInt(cursor.getColumnIndexOrThrow(ID_COLUMN));

        //Get PRICE_COLUMN of current row in int
        final float rowPriceInt = cursor.getFloat(cursor.getColumnIndexOrThrow(PRICE_COLUMN));

        //Set click listener to the view
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Cursor freshItemsTableCursor = db.query(ITEMS_TABLE_NAME,
                        new String[]{CHECKED_COLUMN},
                        ID_COLUMN + "=?", new String[]{Integer.toString(rowIdInt)},
                        null, null, null);

                //Move cursor to 1st row
                freshItemsTableCursor.moveToFirst();

                //Check if the row was checked
                if (freshItemsTableCursor.getInt(freshItemsTableCursor.getColumnIndexOrThrow(CHECKED_COLUMN)) == 0) {
                    //Row wasn't checked
                    //Create alert dialog:
                    //Create alert dialog object
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    //Create inflater object
                    final LayoutInflater inflater = LayoutInflater.from(view.getContext());
                    //Create view object containing dialog_edit_item layout
                    View editItemDialogView = inflater.inflate(R.layout.dialog_edit_item, null);
                    //Create edit text object linked to to editor_price id
                    final EditText editNumber = (EditText) editItemDialogView
                            .findViewById(R.id.dialog_edit_price_number_field);
                    //Set title of the dialog
                    builder.setTitle("Please set items amount")
                            //Set custom view of the dialog
                            .setView(editItemDialogView)
                            //Set ability to press back
                            .setCancelable(true)
                            //Set Ok button with click listener
                            .setPositiveButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            //Set bg to gray
                                            view.setBackgroundColor(Color.GREEN);

                                            //Create contentValuesItemsTable var to store CHECKED_COLUMN value
                                            ContentValues contentValuesItemsTable
                                                    = new ContentValues();
                                            //Put new value into contentValuesItemsTable
                                            contentValuesItemsTable.put(CHECKED_COLUMN, 1);
                                            //Update checked field
                                            db.update(ITEMS_TABLE_NAME, contentValuesItemsTable,
                                                    ID_COLUMN + "=?",
                                                    new String[]{Integer.toString(rowIdInt)});

                                            float amount = Float
                                                    .parseFloat(editNumber.getText().toString());

                                            //Create contentValuesListTable var
                                            ContentValues contentValuesListTable
                                                    = new ContentValues();
                                            //Put new value into contentValuesItemsTable
                                            contentValuesListTable.put(LIST_ITEM_COLUMN, rowIdInt);
                                            //Put new value into contentValuesItemsTable
                                            contentValuesListTable.put(LIST_AMOUNT_COLUMN, amount);
                                            //Put new value into contentValuesItemsTable
                                            contentValuesListTable.put(PRICE_COLUMN, rowPriceInt);

                                            //Check if there activeListTable (other than List_0)
                                            if (dbHelper.getActiveListVersion() == 0) {
                                                //No active List table:
                                                //Create active List table
                                                dbHelper.createListTable();
                                                //Insert new item into List table
                                                db.insert(dbHelper.getLatestListName(),
                                                        null, contentValuesListTable);

                                            } else {
                                                //There is active table
                                                db.insert(dbHelper.getLatestListName(),
                                                        null, contentValuesListTable);
                                            }


                                            freshItemsTableCursor.close();
                                            ItemsFragment.refreshItemsCursor();
                                            Log.e ("WARNING: ", "CALLING refreshListCursor");
                                            ListFragment.refreshListCursor();

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
                                            // TODO: 016 16 Jun 17 pass items' data to editor
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
                                            ItemsFragment.refreshItemsCursor();
                                            ListFragment.refreshListCursor();

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


                    //Remove item from List table:
                    //Get currentListTableName
                    String currentListTableName = dbHelper.getLatestListName();
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
                        dbHelper.deleteListTable(dbHelper.getLatestListVersion());
                    }

                    //Close the cursor
                    listTableCursor.close();

                    freshItemsTableCursor.close();
                    ItemsFragment.refreshItemsCursor();
                    ListFragment.refreshListCursor();

                }


            }


        });


    }


}
