/*
This is MainActivity
 */
package com.example.android.groceries2;

import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.groceries2.data.CategoryAdapter;
import com.example.android.groceries2.data.GroceriesDbHelper;

import static com.example.android.groceries2.data.GroceriesDbHelper.DB_NAME;
import static com.example.android.groceries2.data.GroceriesDbHelper.DB_VERSION;


public class MainActivity extends AppCompatActivity {

    //Global link to dbHelper object
    public static GroceriesDbHelper dbHelper;
    //Global Link to db object
    public static SQLiteDatabase db;

    //Create tab layout object for tabs
    static TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set view for main activity class
        setContentView(R.layout.activity_main);

        //Create dbHelper object
        dbHelper = new GroceriesDbHelper(this, DB_NAME,
                null, DB_VERSION);

        //Create db object
        db = dbHelper.getReadableDatabase();

        //Create adapter for fragments
        CategoryAdapter adapter = new CategoryAdapter(getSupportFragmentManager());

        //Create view pager for fragments content
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        //Set a PagerAdapter that will supply views for this pager as needed
        viewPager.setAdapter(adapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);

        //Link tab layout with view pager
        tabLayout.setupWithViewPager(viewPager);

        int tab = getIntent().getIntExtra("tab", 0);

        if (tab != 0) {
            selectTab(tab);
        }

    }

    //Selects proper tab
    public static void selectTab(int number) {
        //Make it open a proper tab
        tabLayout.getTabAt(number).select();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Close any open database object
        dbHelper.close();
    }
}
