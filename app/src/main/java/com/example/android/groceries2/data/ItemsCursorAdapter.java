package com.example.android.groceries2.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.groceries2.R;

import org.w3c.dom.Text;

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
    public void bindView(View view, Context context, Cursor cursor) {

        CheckBox itemCheckBox = (CheckBox) view.findViewById(R.id.item_checkbox);
        String name = cursor.getString(cursor.getColumnIndex("name"));
        int check = cursor.getInt(cursor.getColumnIndex("checked"));
        boolean checked = false;
        if (check == 1) checked = true;
        itemCheckBox.setText(name);
        itemCheckBox.setChecked(checked);

    }
}
