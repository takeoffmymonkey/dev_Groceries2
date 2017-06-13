package com.example.android.groceries2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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

import static com.example.android.groceries2.data.GroceriesDbHelper.ITEMS_TABLE_NAME;

/**
 * Created by takeoff on 006 06 Jun 17.
 */

public class EditorActivity extends AppCompatActivity {

    SQLiteDatabase db = MainActivity.dbHelper.getReadableDatabase();

    private String name;

    private String price;

    private String weight;

    private String measurement;

    private EditText nameEditText;

    private EditText priceEditText;

    private Spinner measurementSpinner;

    private boolean itemHasChanged = false;

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            itemHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        nameEditText = (EditText) findViewById(R.id.editor_name);
        priceEditText = (EditText) findViewById(R.id.editor_price);
        measurementSpinner = (Spinner) findViewById(R.id.editor_measurement);


        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        nameEditText.setOnTouchListener(touchListener);
        priceEditText.setOnTouchListener(touchListener);
        measurementSpinner.setOnTouchListener(touchListener);

        FloatingActionButton fabApproveItem = (FloatingActionButton)
                findViewById(R.id.fab_approve_item);

        fabApproveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveItem();
            }
        });

        setupSpinner();

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
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals("item(s)")) {
                        measurement = "item(s)";
                    } else if (selection.equals("Kg")) {
                        measurement = "Kg";
                    } else if (selection.equals("gr")) {
                        measurement = "gr";
                    } else if (selection.equals("L")) {
                        measurement = "L";
                    } else if (selection.equals("ml")) {
                        measurement = "ml";

                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                measurement = "item(s)";
            }
        });
    }


    private void saveItem() {

        name = nameEditText.getText().toString().trim();
        price = priceEditText.getText().toString().trim();

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("price", price);
        contentValues.put("measure", measurement);
        contentValues.put("checked", 0);
        db.insert(ITEMS_TABLE_NAME, null, contentValues);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

}
