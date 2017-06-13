/*
This is MainActivity
 */
package com.example.android.groceries2;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import com.example.android.groceries2.data.GroceriesDbHelper;


public class MainActivity extends AppCompatActivity {

    static public GroceriesDbHelper dbHelper;
    static public boolean EMPTY = true;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Create dbHelper object
        dbHelper = new GroceriesDbHelper(this, GroceriesDbHelper.DB_NAME,
                null, GroceriesDbHelper.DB_VERSION);


        CategoryAdapter adapter = new CategoryAdapter(getSupportFragmentManager());
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(viewPager);


/*        *//*Write to shared preferences *//*
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.temp), "f u");
        editor.apply();

        *//*Read from shared preferences*//*
        SharedPreferences sharedPref2 = getPreferences(Context.MODE_PRIVATE);
        String defaultValue = getResources().getString(R.string.temp);
        String defaultValue1 = sharedPref2.getString(getString(R.string.temp), defaultValue);*/


    }

}
