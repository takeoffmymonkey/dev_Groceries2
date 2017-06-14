/*
This is MainActivity
 */
package com.example.android.groceries2;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import com.example.android.groceries2.data.CategoryAdapter;
import com.example.android.groceries2.data.GroceriesDbHelper;


public class MainActivity extends AppCompatActivity {

    static public GroceriesDbHelper dbHelper;
    static public boolean EMPTY = true;
    public CategoryAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Create dbHelper object
        dbHelper = new GroceriesDbHelper(this, GroceriesDbHelper.DB_NAME,
                null, GroceriesDbHelper.DB_VERSION);


        adapter = new CategoryAdapter(getSupportFragmentManager());
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(viewPager);

    }

}
