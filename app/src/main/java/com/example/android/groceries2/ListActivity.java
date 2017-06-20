package com.example.android.groceries2;


import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;


/**
 * Created by takeoff on 006 06 Jun 17.
 */

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list);

        FloatingActionButton fabDeleteLitst = (FloatingActionButton)
                findViewById(R.id.fab_delete_list);
    }
}
