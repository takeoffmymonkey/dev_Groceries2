/*
This is MainActivity
 */
package com.example.android.groceries2;

import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;


import com.example.android.groceries2.data.GroceriesDbHelper;


public class MainActivity extends AppCompatActivity {

    static public GroceriesDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create dbHelper object
        dbHelper = new GroceriesDbHelper(this, GroceriesDbHelper.DATABASE_NAME,
                null, GroceriesDbHelper.DBVERSION);


        CategoryAdapter adapter = new CategoryAdapter(getSupportFragmentManager());
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(viewPager);

    }

}
