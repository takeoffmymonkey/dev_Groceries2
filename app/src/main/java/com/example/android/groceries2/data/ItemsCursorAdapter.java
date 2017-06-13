package com.example.android.groceries2.data;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.icu.text.IDNA;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.groceries2.EditorActivity;
import com.example.android.groceries2.MainActivity;
import com.example.android.groceries2.R;


import static android.os.Build.ID;
import static com.example.android.groceries2.data.GroceriesDbHelper.ITEMS_CHECKED_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.ITEMS_MEASURE_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.ITEMS_TABLE_CREATE_COMMAND;
import static com.example.android.groceries2.data.GroceriesDbHelper.ITEMS_TABLE_DROP_COMMAND;
import static com.example.android.groceries2.data.GroceriesDbHelper.ITEMS_TABLE_NAME;
import static com.example.android.groceries2.data.GroceriesDbHelper.ITEMS_PRICE_COLUMN;
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
        // Inflate a list item view using the layout specified in list_item.xml

        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
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
    public void bindView(final View view, Context context, final Cursor cursor) {


        final int id = cursor.getInt(cursor.getColumnIndex(ID_COLUMN));
        final String[] id1 = {Integer.toString(cursor.getInt(cursor.getColumnIndex(ID_COLUMN)))};

        Cursor cursor1 = db.query(GroceriesDbHelper.ITEMS_TABLE_NAME, null, null, null, null, null, null);
        cursor1.move(id);

        boolean checkBoxState = false;

        CheckBox itemCheckBox = (CheckBox) view.findViewById(R.id.item_checkbox);

        String name = cursor1.getString(cursor.getColumnIndex(NAME_COLUMN));
        int check = cursor1.getInt(cursor.getColumnIndex(ITEMS_CHECKED_COLUMN));


        float price = cursor1.getFloat(cursor.getColumnIndex(ITEMS_PRICE_COLUMN));


        TextView quantity = (TextView) view.findViewById(R.id.item_quantity);
        quantity.setText(Float.toString(price));


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
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();


                /*                boolean checkBoxState = false;

                Cursor cursor = db.query(GroceriesDbHelper.ITEMS_TABLE_NAME, null, null, null, null, null, null);
                cursor.move(id);
                int check = cursor.getInt(cursor.getColumnIndex("checked"));
                if (check == 1) checkBoxState = true;

                if (!checkBoxState) {
                    ContentValues values = new ContentValues();
                    values.put("checked", 1);
                    db.update("groceries", values, "_id = ?", id1);
                    view.setBackgroundColor(Color.GRAY);
                    //Toast.makeText(view.getContext(), "Checked:" + id1[0], Toast.LENGTH_SHORT).show();


                } else {
                    ContentValues values = new ContentValues();
                    values.put("checked", 0);
                    db.update("groceries", values, "_id = ?", id1);
                    view.setBackgroundColor(Color.WHITE);
                    //Toast.makeText(view.getContext(), "Unchecked:" + id1[0], Toast.LENGTH_SHORT).show();
                }*/
            }
        });

    }

}
