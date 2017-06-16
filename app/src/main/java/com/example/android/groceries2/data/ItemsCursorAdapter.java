package com.example.android.groceries2.data;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.groceries2.EditorActivity;
import com.example.android.groceries2.MainActivity;
import com.example.android.groceries2.R;


import static com.example.android.groceries2.MainActivity.dbHelper;
import static com.example.android.groceries2.data.GroceriesDbHelper.CHECKED_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.ITEMS_TABLE_NAME;
import static com.example.android.groceries2.data.GroceriesDbHelper.LIST_AMOUNT_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.LIST_ITEM_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.NAME_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.ID_COLUMN;

/**
 * Created by takeoff on 009 09 Jun 17.
 */


/**
 * {@link ItemsCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of pet data as its data source. This adapter knows
 * how to create list items for each row of pet data in the {@link Cursor}.
 */
public class ItemsCursorAdapter extends CursorAdapter {


    /**
     * Constructs a new {@link ItemsCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ItemsCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in items_item.xml
        return LayoutInflater.from(context).inflate(R.layout.item_items, parent, false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {

        //Create text view object for item's name
        TextView itemNameTextView = (TextView) view.findViewById(R.id.item_name);

        //Set its name to NAME_COLUMN
        itemNameTextView.setText(cursor.getString(cursor.getColumnIndex(NAME_COLUMN)));

        //Set color of the view (according to CHECKED_COLUMN state of the row)
        if (cursor.getInt(cursor.getColumnIndex(CHECKED_COLUMN)) == 1)
            view.setBackgroundColor(Color.GRAY);
        else view.setBackgroundColor(Color.WHITE);

        //Get ID_COLUMN of current row in int
        final int rowIdInt = cursor.getInt(cursor.getColumnIndex(ID_COLUMN));

        //Set click listener to the view
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Check if the row was checked
                if (cursor.getInt(cursor.getColumnIndex(CHECKED_COLUMN)) == 0) {
                    //Row wasn't checked
                    //Create alert dialog:
                    //Create alert dialog object
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    //Create inflater object
                    LayoutInflater inflater = LayoutInflater.from(view.getContext());
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

                                            //Create contentValuesItemsTable var to store CHECKED_COLUMN value
                                            ContentValues contentValuesItemsTable
                                                    = new ContentValues();
                                            //Put new value into contentValuesItemsTable
                                            contentValuesItemsTable.put(CHECKED_COLUMN, 1);
                                            //Open db connection
                                            SQLiteDatabase db = dbHelper.getReadableDatabase();
                                            //Update checked field
                                            db.update(ITEMS_TABLE_NAME, contentValuesItemsTable,
                                                    ID_COLUMN + "=?",
                                                    new String[]{Integer.toString(rowIdInt)});

                                            //Prepare new values for List table
                                            //Read the value (amount) of the dialog
                                            // TODO: 016 16 Jun 17 invalid int bug 
                                            int amount = Integer
                                                    .parseInt(editNumber.getText().toString());

                                            //Create contentValuesListTable var
                                            ContentValues contentValuesListTable
                                                    = new ContentValues();
                                            //Put new value into contentValuesItemsTable
                                            contentValuesListTable.put(LIST_ITEM_COLUMN, rowIdInt);
                                            //Put new value into contentValuesItemsTable
                                            contentValuesListTable.put(LIST_AMOUNT_COLUMN, amount);

                                            //Check if there activeListTable
                                            if (!dbHelper.getListActiveState(db)) {
                                                //No active List table:
                                                //Create active List table
                                                dbHelper.createListTable(db);
                                                //Insert new item into List table
                                                db.insert(dbHelper.getCurrentListTableName(db),
                                                        null, contentValuesListTable);

                                            } else {
                                                //There is active table
                                                db.insert(dbHelper.getCurrentListTableName(db),
                                                        null, contentValuesListTable);
                                            }

                                            //Close db connection
                                            db.close();

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
                                            //Close the dialog window
                                            dialog.cancel();
                                        }
                                    });

                    AlertDialog alert = builder.create();
                    alert.show();


                } else {
                    //Row was checked


                }

            }
        });


    }


}
