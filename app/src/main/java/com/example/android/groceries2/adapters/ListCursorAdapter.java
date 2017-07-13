package com.example.android.groceries2.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.groceries2.R;
import com.example.android.groceries2.activities.MainActivity;

import static com.example.android.groceries2.activities.MainActivity.db;
import static com.example.android.groceries2.activities.MainActivity.dbHelper;
import static com.example.android.groceries2.db.GroceriesDbHelper.CHECKED_COLUMN;
import static com.example.android.groceries2.db.GroceriesDbHelper.ID_COLUMN;
import static com.example.android.groceries2.db.GroceriesDbHelper.IMAGE_COLUMN;
import static com.example.android.groceries2.db.GroceriesDbHelper.LIST_AMOUNT_COLUMN;
import static com.example.android.groceries2.db.GroceriesDbHelper.LIST_ITEM_COLUMN;
import static com.example.android.groceries2.db.GroceriesDbHelper.MEASURE_COLUMN;
import static com.example.android.groceries2.db.GroceriesDbHelper.MEASURE_TABLE_NAME;
import static com.example.android.groceries2.db.GroceriesDbHelper.PRICE_COLUMN;
import static com.example.android.groceries2.fragments.ListFragment.refreshListCursor;

/**
 * Created by takeoff on 013 13 Jun 17.
 */

public class ListCursorAdapter extends CursorAdapter {


    public ListCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);
    }


    @Override
    public void bindView(final View view, Context context, Cursor cursor) {

        //Get current row ID
        final int rowIdInt = cursor.getInt(cursor.getColumnIndexOrThrow(ID_COLUMN));


        //Set item's image
        ImageView itemImage = (ImageView) view.findViewById(R.id.list_item_image);
        int itemImageInt = cursor.getInt(cursor.getColumnIndex(IMAGE_COLUMN));
        String imageName = MainActivity.images[itemImageInt - 1];
        Glide.with(context)
                .load(context.getResources()
                        .getIdentifier(imageName, "drawable", context.getPackageName()))
                .into(itemImage);


        //Set item name
        String itemName = cursor.getString(cursor.getColumnIndexOrThrow(LIST_ITEM_COLUMN));
        final TextView itemNameTextView = (TextView) view.findViewById(R.id.list_item_name);
        itemNameTextView.setText(itemName);


        //Get item measure
        int measureInItems = cursor
                .getInt(cursor.getColumnIndexOrThrow(MEASURE_COLUMN));
        Cursor measureTableCursor = db.query(MEASURE_TABLE_NAME,
                new String[]{MEASURE_COLUMN},
                ID_COLUMN + "=?", new String[]{Integer.toString(measureInItems)},
                null, null, null);
        measureTableCursor.moveToFirst();
        String measure = measureTableCursor
                .getString(measureTableCursor.getColumnIndexOrThrow(MEASURE_COLUMN));
        measureTableCursor.close();


        //Set item amount + measure
        TextView itemAmountTextView = (TextView) view.findViewById(R.id.list_item_amount);
        float itemAmount = cursor.getFloat(cursor.getColumnIndexOrThrow(LIST_AMOUNT_COLUMN));
        int itemAmountRound = Math.round(itemAmount);
        //Check if it is round
        if (itemAmount == itemAmountRound) { //It is round
            //Check if it is 1 item
            if (measure.equals("items") && itemAmountRound == 1) { //it is 1 item
                itemAmountTextView.setText("" + itemAmountRound + " item");
            } else { //It is round but not 1
                itemAmountTextView.setText(itemAmountRound + " " + measure);
            }
        } else { //it is not round
            itemAmountTextView.setText(Float.toString(itemAmount) + " " + measure);
        }


        //Set item price
        TextView itemPriceTextView = (TextView) view.findViewById(R.id.list_item_price);
        float itemPrice = cursor.getFloat(cursor.getColumnIndexOrThrow(PRICE_COLUMN));
        itemPriceTextView.setText("(" + MainActivity.formatPrice(itemPrice) + ")");


        //Create checkbox object
        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.list_item_checkbox);


        //Get checked state from the table
        int isCheckedInt = cursor.getInt(cursor.getColumnIndex(CHECKED_COLUMN));
        boolean isChecked = (isCheckedInt == 1);


        //Set style of the view according to checked state
        if (isChecked) { //Selected style
            view.setBackgroundColor(MainActivity.primaryLightColor);
            itemNameTextView.setPaintFlags(itemNameTextView.getPaintFlags()
                    | Paint.STRIKE_THRU_TEXT_FLAG);
            checkBox.setChecked(true);
        } else { //Normal style
            view.setBackgroundColor(Color.WHITE);
            itemNameTextView.setPaintFlags(0);
            checkBox.setChecked(false);
        }


        //Set onClickListener on the whole view
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ContentValues contentValues = new ContentValues();
                String activeTable = dbHelper.getActiveListName();


                //Check if snack is open and close it
                if (MainActivity.snackOn && MainActivity.snackBar != null) {
                    MainActivity.snackBar.dismiss();
                    MainActivity.snackLines = 0;
                    MainActivity.setSnackOnState(false, null);
                    return;
                }


                //Get current checked state
                Cursor checkedStateCursor = db.query(activeTable,
                        new String[]{CHECKED_COLUMN},
                        ID_COLUMN + "=?", new String[]{Integer.toString(rowIdInt)},
                        null, null, null);
                checkedStateCursor.moveToFirst();
                int isChecked = checkedStateCursor.getInt(checkedStateCursor
                        .getColumnIndex(CHECKED_COLUMN));
                checkedStateCursor.close();


                //Apply appropriate style and prepare value for updating the table
                if (isChecked == 1) { //view was checked
                    //Normalise style
                    view.setBackgroundColor(MainActivity.iconsColor);
                    itemNameTextView.setPaintFlags(0);
                    checkBox.setChecked(false);
                    //Prepare value for update
                    contentValues.put(CHECKED_COLUMN, 0);
                } else { //view was unchecked
                    //Apply selected style
                    view.setBackgroundColor(MainActivity.primaryLightColor);
                    itemNameTextView.setPaintFlags(itemNameTextView.getPaintFlags()
                            | Paint.STRIKE_THRU_TEXT_FLAG);
                    checkBox.setChecked(true);
                    //Prepare value for update
                    contentValues.put(CHECKED_COLUMN, 1);
                }

                //Update table with prepared value
                db.update(dbHelper.getActiveListName(), contentValues,
                        ID_COLUMN + "=?",
                        new String[]{Integer.toString(rowIdInt)});


                //Update cursor
                refreshListCursor(null, null, 0);
            }
        });





        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ContentValues contentValues = new ContentValues();
                String activeTable = dbHelper.getActiveListName();


                //Check if snack is open and close it
                if (MainActivity.snackOn && MainActivity.snackBar != null) {
                    MainActivity.snackBar.dismiss();
                    MainActivity.snackLines = 0;
                    MainActivity.setSnackOnState(false, null);
                    return;
                }


                //Get current checked state
                Cursor checkedStateCursor = db.query(activeTable,
                        new String[]{CHECKED_COLUMN},
                        ID_COLUMN + "=?", new String[]{Integer.toString(rowIdInt)},
                        null, null, null);
                checkedStateCursor.moveToFirst();
                int isChecked = checkedStateCursor.getInt(checkedStateCursor
                        .getColumnIndex(CHECKED_COLUMN));
                checkedStateCursor.close();


                //Apply appropriate style and prepare value for updating the table
                if (isChecked == 1) { //view was checked
                    //Normalise style
                    view.setBackgroundColor(MainActivity.iconsColor);
                    itemNameTextView.setPaintFlags(0);
                    checkBox.setChecked(false);
                    //Prepare value for update
                    contentValues.put(CHECKED_COLUMN, 0);
                } else { //view was unchecked
                    //Apply selected style
                    view.setBackgroundColor(MainActivity.primaryLightColor);
                    itemNameTextView.setPaintFlags(itemNameTextView.getPaintFlags()
                            | Paint.STRIKE_THRU_TEXT_FLAG);
                    checkBox.setChecked(true);
                    //Prepare value for update
                    contentValues.put(CHECKED_COLUMN, 1);
                }

                //Update table with prepared value
                db.update(dbHelper.getActiveListName(), contentValues,
                        ID_COLUMN + "=?",
                        new String[]{Integer.toString(rowIdInt)});


                //Update cursor
                refreshListCursor(null, null, 0);

            }
        });



/*        //Set item to long-clickable
        view.setLongClickable(true);

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                //Close snack if there is one open
                if (MainActivity.snackOn && MainActivity.snackBar != null) {
                    MainActivity.snackBar.dismiss();
                    MainActivity.snackLines = 0;
                    MainActivity.setSnackOnState(false, null);
                }


                //Update Items table
                ContentValues contentValuesItemsTable
                        = new ContentValues();
                contentValuesItemsTable.put(CHECKED_COLUMN, 0);
                db.update(ITEMS_TABLE_NAME, contentValuesItemsTable,
                        ID_COLUMN + "=?",
                        new String[]{Integer.toString(rowIdInt)});


                //refresh cursors
                refreshItemsCursor(null, null, 0);
                refreshListCursor(null, null, 0);

                return true;
            }
        });*/


    }

}
