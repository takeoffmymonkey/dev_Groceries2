package com.example.android.groceries2.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.groceries2.MainActivity;
import com.example.android.groceries2.R;

import static android.R.attr.id;

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

        final int id = cursor.getInt(cursor.getColumnIndex("_id"));
        final String[] id1 = {Integer.toString(id)};

        changeCursor(cursor);
        Cursor cursor1 = db.query(GroceriesDbHelper.TABLE_GROCERIES, null, null, null, null, null, null);
        cursor1.move(id);

        boolean checkBoxState = false;

        CheckBox itemCheckBox = (CheckBox) view.findViewById(R.id.item_checkbox);
        final TextView itemTextView = (TextView) view.findViewById(R.id.item_name);

        String name = cursor1.getString(cursor.getColumnIndex("name"));
        int check = cursor1.getInt(cursor.getColumnIndex("checked"));

        if (check == 1) checkBoxState = true;
        itemCheckBox.setText(name);
        itemCheckBox.setChecked(checkBoxState);



        itemCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checkBoxState = false;

                Cursor cursor = db.query(GroceriesDbHelper.TABLE_GROCERIES, null, null, null, null, null, null);
                cursor.move(id);
                int check = cursor.getInt(cursor.getColumnIndex("checked"));
                if (check == 1) checkBoxState = true;

                if (!checkBoxState) {
                    ContentValues values = new ContentValues();
                    values.put("checked", 1);
                    db.update("groceries", values, "_id = ?", id1);
                    swapCursor(cursor);
                    Toast.makeText(view.getContext(), "Checked:" + id1[0], Toast.LENGTH_SHORT).show();


                } else {
                    ContentValues values = new ContentValues();
                    values.put("checked", 0);
                    db.update("groceries", values, "_id = ?", id1);
                    swapCursor(cursor);
                    Toast.makeText(view.getContext(), "Unchecked:" + id1[0], Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
