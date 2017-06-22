package com.example.android.groceries2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import static com.example.android.groceries2.MainActivity.db;
import static com.example.android.groceries2.data.GroceriesDbHelper.ID_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.MEASURE_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.PRICE_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.ITEMS_TABLE_NAME;
import static com.example.android.groceries2.data.GroceriesDbHelper.NAME_COLUMN;

/**
 * Created by takeoff on 006 06 Jun 17.
 */

public class EditorActivity extends AppCompatActivity {

    private String name;

    private float price;

    private int measure;

    private String measurement = "1";

    private EditText nameEditText;

    private EditText priceEditText;

    private Spinner measurementSpinner;

    private int itemId = 0;


    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        nameEditText = (EditText) findViewById(R.id.editor_name);
        priceEditText = (EditText) findViewById(R.id.dialog_edit_price_number_field);
        measurementSpinner = (Spinner) findViewById(R.id.editor_measurement);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        nameEditText.setOnTouchListener(touchListener);
        priceEditText.setOnTouchListener(touchListener);
        measurementSpinner.setOnTouchListener(touchListener);


        setupSpinner();

        FloatingActionButton fabApproveItem = (FloatingActionButton)
                findViewById(R.id.fab_approve_item);

        fabApproveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveItem(name, price, itemId);
            }
        });


        //Receive id of the item if bundled
        itemId = getIntent().getIntExtra("ID", 0);

        if (itemId != 0) {


            class Query extends AsyncTask<Void, Void, Boolean> {

                @Override
                protected Boolean doInBackground(Void... params) {
                    //Get cursor with proper data for the id
                    Cursor cursor = db.query(ITEMS_TABLE_NAME,
                            new String[]{NAME_COLUMN, PRICE_COLUMN, MEASURE_COLUMN},
                            ID_COLUMN + "=?", new String[]{Integer.toString(itemId)},
                            null, null, null);

                    //Moving cursor to 1st row
                    cursor.moveToFirst();


                    name = cursor.getString(cursor.getColumnIndex(NAME_COLUMN));
                    price = cursor.getFloat(cursor.getColumnIndex(PRICE_COLUMN));
                    measure = cursor.getInt(cursor.getColumnIndex(MEASURE_COLUMN));

                    cursor.close();
                    return true;
                }

                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    super.onPostExecute(aBoolean);

                    measurementSpinner.setSelection(measure - 1);

                    nameEditText.setText(name);
                    priceEditText.setText(Float.toString(price));

                }
            }

            new Query().execute();


        }


    }

    private void setupSpinner() {
        ArrayAdapter measurementSpinnerAdapter =
                ArrayAdapter.createFromResource(this, R.array.array_measurement_options,
                        android.R.layout.simple_spinner_item);


        // Specify dropdown layout style - simple list view with 1 item per line
        measurementSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        measurementSpinner.setAdapter(measurementSpinnerAdapter);


        // Set the integer mSelected to the constant values
        measurementSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);

                String[] measures = getResources().getStringArray(R.array.array_measurement_options);

                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(measures[0])) {
                        measurement = "1";
                    } else if (selection.equals(measures[1])) {
                        measurement = "2";
                    } else if (selection.equals(measures[2])) {
                        measurement = "3";
                    } else if (selection.equals(measures[3])) {
                        measurement = "4";
                    } else if (selection.equals(measures[4])) {
                        measurement = "5";
                    } else if (selection.equals(measures[5])) {
                        measurement = "6";
                    }

                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                measurement = "1";
            }
        });
    }


    //Save item, but get the init name to compare with new one
    //0 - add mode
    //!0 - edit mode
    private void saveItem(String exName, float exPrice, int itemId) {

        //Get new name
        String newName = nameEditText.getText().toString().trim();
        //Get new price as string
        String newPriceString = priceEditText.getText().toString().trim();
        //Convert newPriceString to float
        Float newPrice = Float.parseFloat(newPriceString);
        //Get new measurement

        if (itemId == 0) {
            //add item mode
            ContentValues contentValues = new ContentValues();
            contentValues.put(NAME_COLUMN, newName);
            contentValues.put(PRICE_COLUMN, newPrice);
            contentValues.put(MEASURE_COLUMN, measurement);
            db.insert(ITEMS_TABLE_NAME, null, contentValues);
            Toast.makeText(this, "New item added", Toast.LENGTH_SHORT).show();

        } else {
            //edit mode
            ContentValues contentValues = new ContentValues();
            contentValues.put(NAME_COLUMN, newName);
            contentValues.put(PRICE_COLUMN, newPrice);
            contentValues.put(MEASURE_COLUMN, measurement);
            db.update(ITEMS_TABLE_NAME, contentValues, ID_COLUMN + "=?",
                    new String[]{Integer.toString(itemId)});
            Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show();
        }

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

}