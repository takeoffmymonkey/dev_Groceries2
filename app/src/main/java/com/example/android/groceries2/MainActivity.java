/*
This is MainActivity
 */
package com.example.android.groceries2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView testText = (TextView) findViewById(R.id.test_text_field);
        testText.setText("THIS IS A TEST");

    }


}
