/*
This is MainActivity
 */
package com.example.android.groceries2;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.android.groceries2.data.GroceriesDbHelper;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create dbHelper object
        GroceriesDbHelper dbHelper = new GroceriesDbHelper(this, GroceriesDbHelper.DATABASE_NAME,
                null, GroceriesDbHelper.DBVERSION);

        //Create db reference
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

        TextView testText = (TextView) findViewById(R.id.test_text_field);

        testText.setText(sqLiteDatabase.toString());

    }


}
