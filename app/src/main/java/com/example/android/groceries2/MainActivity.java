/*
This is MainActivity
 */
package com.example.android.groceries2;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.android.groceries2.data.GroceriesDbHelper;


public class MainActivity extends AppCompatActivity {

    private int itemsTotal = 0;
    GroceriesDbHelper dbHelper;
    SQLiteDatabase db;
    TextView testText;
    TextView testText2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Create dbHelper object
        dbHelper = new GroceriesDbHelper(this, GroceriesDbHelper.DATABASE_NAME,
                null, GroceriesDbHelper.DBVERSION);

        //Create db reference
        db = dbHelper.getWritableDatabase();


        FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fab_add_item);
        FloatingActionButton fabDelete = (FloatingActionButton) findViewById(R.id.fab_delete_db);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String s = ("INSERT INTO groceries (" +
                        "_id, name, price, weight, measure) VALUES (" +
                        Integer.toString(itemsTotal + 1) + ", \"Test\"," + " 1, 1, 1);");

                itemsTotal++;
                db.execSQL(s);
                updateItem();
            }
        });

        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDatabase("groceries_db");
            }
        });


    }


    public void updateItem() {
        Cursor cursor = db.query(GroceriesDbHelper.TABLE_GROCERIES, null, null, null, null, null, null);

        testText = (TextView) findViewById(R.id.test_text_field);
        testText2 = (TextView) findViewById(R.id.test_text_field2);

        cursor.moveToFirst();
        int itemId = cursor.getInt(cursor.getColumnIndex("_id"));
        String itemName = cursor.getString(cursor.getColumnIndex(GroceriesDbHelper.ITEM_NAME));
        int itemPrice = cursor.getInt(cursor.getColumnIndex(GroceriesDbHelper.ITEM_PRICE));
        int itemWeight = cursor.getInt(cursor.getColumnIndex(GroceriesDbHelper.ITEM_WEIGHT));
        int itemMeasure = cursor.getInt(cursor.getColumnIndex(GroceriesDbHelper.ITEM_MEASURE));


        testText.setText("ID:" + itemId + " Name:" + itemName + " Price:"
                + itemPrice + " Weight: " + itemWeight + " Measure: " + itemMeasure);
        testText2.setText("Rows:" + cursor.getCount());


        cursor.close();
    }
}
