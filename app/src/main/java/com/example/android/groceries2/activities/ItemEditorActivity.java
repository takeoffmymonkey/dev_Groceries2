package com.example.android.groceries2.activities;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.groceries2.R;
import com.example.android.groceries2.adapters.ImageAdapter;

import static com.example.android.groceries2.activities.MainActivity.db;
import static com.example.android.groceries2.db.GroceriesDbHelper.ID_COLUMN;
import static com.example.android.groceries2.db.GroceriesDbHelper.ITEMS_TABLE_NAME;
import static com.example.android.groceries2.db.GroceriesDbHelper.MEASURE_COLUMN;
import static com.example.android.groceries2.db.GroceriesDbHelper.NAME_COLUMN;
import static com.example.android.groceries2.db.GroceriesDbHelper.PRICE_COLUMN;

/**
 * Created by takeoff on 006 06 Jun 17.
 */

public class ItemEditorActivity extends AppCompatActivity {

    private String name;

    private float price;

    private int measure;

    private String measurement = "1";

    private EditText nameEditText;

    private EditText priceEditText;

    private Spinner measurementSpinner;

    private ImageView icon;

    private int itemId = 0;


    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return false;
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        Log.w("WARNING: ", "IN ONSTART OF EDITOR ACTIVITY");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w("WARNING: ", "IN ONRESUME OF EDITOR ACTIVITY");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w("WARNING: ", "IN ONPAUSE OF EDITOR ACTIVITY");
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.w("WARNING: ", "IN ONSTOP OF EDITOR ACTIVITY");
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Log.w("WARNING: ", "IN ONRESTART OF EDITOR ACTIVITY");
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        nameEditText = (EditText) findViewById(R.id.editor_name);
        priceEditText = (EditText) findViewById(R.id.dialog_edit_price_number_field);
        measurementSpinner = (Spinner) findViewById(R.id.editor_measurement);
        icon = (ImageView) findViewById(R.id.item_icon);


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





        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ItemEditorActivity.this);

                final LayoutInflater inflater = LayoutInflater.from(ItemEditorActivity.this);
                //Create view object containing dialog_item_amount layout
                View iconSelectView = inflater.inflate(R.layout.dialog_item_icon, null);

                GridView gridview = (GridView) iconSelectView.findViewById(R.id.icons_gridview);
                ImageAdapter imageAdapter = new ImageAdapter(ItemEditorActivity.this);

                gridview.setAdapter(imageAdapter);


                gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v,
                                            int position, long id) {
                        Toast.makeText(ItemEditorActivity.this, "fsdf" + position,
                                Toast.LENGTH_SHORT).show();
                    }
                });

                //Set title of the dialog
                builder.setTitle("Set icon")
                        //Set message
                        .setView(iconSelectView)
                        //Set ability to press back
                        .setCancelable(true)
                        //Set Ok button with click listener
                        .setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //Close the dialog window
                                        dialog.cancel();
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //Close the dialog window
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        getSupportActionBar().setTitle("Add new item");

        //Receive id of the item if bundled
        itemId = getIntent().getIntExtra("ID", 0);

        if (itemId != 0) {

            getSupportActionBar().setTitle("Edit item");

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
                    //test
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

        //Name field should not be empty
        if (newName.equals("")) {
            //It is empty

            //Calling warning message
            warningMessage("Name");

            //Set text back (if it was there)
            nameEditText.setText(exName);

        } else {
            //it is not empty

            //Get new price as string
            String newPriceString = priceEditText.getText().toString().trim();

            //price should not be empty
            if (newPriceString.equals("")) {
                newPriceString = "0.0";
            }

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
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);


        }


    }


    private void warningMessage(String input) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //Set title of the dialog
        builder.setTitle("Incorrect input.")
                //Set message
                .setMessage(input + " field should not be empty.")
                //Set ability to press back
                .setCancelable(true)
                //Set Ok button with click listener
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Close the dialog window
                                dialog.cancel();
                            }
                        });

        AlertDialog alert = builder.create();
        alert.show();


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }
}