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
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.groceries2.EditorActivity;
import com.example.android.groceries2.MainActivity;
import com.example.android.groceries2.R;


import static com.example.android.groceries2.data.GroceriesDbHelper.CHECKED_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.ITEMS_TABLE_NAME;
import static com.example.android.groceries2.data.GroceriesDbHelper.LIST_AMOUNT_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.LIST_ITEM_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.LIST_TABLE_NAME;
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

    SQLiteDatabase db = MainActivity.dbHelper.getReadableDatabase();


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


        final int id0 = cursor.getInt(cursor.getColumnIndex(ID_COLUMN));
        final String[] id1 = {Integer.toString(cursor.getInt(cursor.getColumnIndex(ID_COLUMN)))};

        final Cursor cursor1 = db.query(ITEMS_TABLE_NAME, null, null, null, null, null, null);
        cursor1.move(id0);

        boolean checkBoxState = false;

        CheckBox itemCheckBox = (CheckBox) view.findViewById(R.id.item_checkbox);

        String name = cursor1.getString(cursor.getColumnIndex(NAME_COLUMN));
        int check = cursor1.getInt(cursor.getColumnIndex(CHECKED_COLUMN));



/*        TextView measureTextView = (TextView) view.findViewById(R.id0.item_measure);

        int measure = cursor1.getInt(cursor.getColumnIndex(ITEMS_MEASURE_COLUMN));

        // TODO: 013 13 Jun 17 narrow down query
        Cursor cursor2 = db.query(MEASURE_TABLE_NAME, null, null, null, null, null, null);

        cursor2.move(measure);
        String s = cursor2.getString(cursor2.getColumnIndex(MEASURE_MEASURE_COLUMN));
        measureTextView.setText(s);*/

        if (check == 1) {
            checkBoxState = true;
            view.setBackgroundColor(Color.GRAY);
        } else {
            view.setBackgroundColor(Color.WHITE);
        }
        itemCheckBox.setText(name);
        itemCheckBox.setChecked(checkBoxState);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                LayoutInflater inflater = LayoutInflater.from(view.getContext());
                View test = inflater.inflate(R.layout.dialog_edit_item, null);

                final EditText amt = (EditText) test.findViewById(R.id.editor_price);

                boolean checkBoxState = false;
                final Cursor cursor = db.query(ITEMS_TABLE_NAME, null, null, null, null, null, null);
                cursor.move(id0);


                builder.setTitle("Please set items amount")
                        //.setMessage("Setting items amount..")
                        .setView(test)
                        .setCancelable(true) //to be able to press back
                        .setNeutralButton("Edit item",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent intent = new Intent(view.getContext(), EditorActivity.class);
                                        view.getContext().startActivity(intent);
                                        dialog.cancel();
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                        .setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        ContentValues contentValues = new ContentValues();
                                        contentValues.put(LIST_ITEM_COLUMN, id0);

                                        int amount = Integer.parseInt(amt.getText().toString());
                                        contentValues.put(LIST_AMOUNT_COLUMN, amount);
                                        long newID = db.insert(LIST_TABLE_NAME, null, contentValues);

                                        Toast.makeText(view.getContext(), contentValues.toString() +
                                                        "new id" + newID,
                                                Toast.LENGTH_SHORT).show();
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();


                int check = cursor.getInt(cursor.getColumnIndex(CHECKED_COLUMN));
                if (check == 1) checkBoxState = true;
                if (!checkBoxState) {
                    ContentValues values = new ContentValues();
                    values.put(CHECKED_COLUMN, 1);
                    db.update(ITEMS_TABLE_NAME, values, "_id = ?", id1);
                    view.setBackgroundColor(Color.GRAY);
                    Toast.makeText(view.getContext(), "Checked:" + id1[0], Toast.LENGTH_SHORT).show();
                } else {
                    ContentValues values = new ContentValues();
                    values.put(CHECKED_COLUMN, 0);
                    db.update(ITEMS_TABLE_NAME, values, "_id = ?", id1);
                    view.setBackgroundColor(Color.WHITE);
                    Toast.makeText(view.getContext(), "Unchecked:" + id1[0], Toast.LENGTH_SHORT).show();
                }

                cursor.close();
                cursor1.close();

            }
        });

    }

}
