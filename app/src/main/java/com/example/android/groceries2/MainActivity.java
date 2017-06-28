/*
This is MainActivity
 */
package com.example.android.groceries2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.groceries2.data.CategoryAdapter;
import com.example.android.groceries2.data.GroceriesDbHelper;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import static com.example.android.groceries2.data.GroceriesDbHelper.DB_NAME;
import static com.example.android.groceries2.data.GroceriesDbHelper.DB_VERSION;


public class MainActivity extends AppCompatActivity {

    //Global link to dbHelper object
    public static GroceriesDbHelper dbHelper;
    //Global Link to db object
    public static SQLiteDatabase db;

    //Create tab layout object for tabs
    static TabLayout tabLayout;


    public static int primaryColor;
    public static int primaryDarkColor;
    public static int primaryLightColor;
    public static int accentColor;
    public static int primaryTextColor;
    public static int secondaryTextColor;
    public static int dividerColor;
    public static int iconsColor;

    public static String nums[];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set view for main activity class
        setContentView(R.layout.activity_main);


        primaryColor = getResources().getColor(R.color.colorPrimary);
        primaryDarkColor = getResources().getColor(R.color.colorPrimaryDark);
        primaryLightColor = getResources().getColor(R.color.colorPrimaryLight);
        accentColor = getResources().getColor(R.color.colorAccent);
        primaryTextColor = getResources().getColor(R.color.colorPrimaryText);
        secondaryTextColor = getResources().getColor(R.color.colorSecondaryText);
        dividerColor = getResources().getColor(R.color.colorDivider);
        iconsColor = getResources().getColor(R.color.colorIcons);

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

        //tabLayout.setTabTextColors(iconsColor, primaryTextColor);


        //Link tab layout with view pager
        tabLayout.setupWithViewPager(viewPager);

        int tab = getIntent().getIntExtra("tab", 0);

        if (tab != 0) {
            selectTab(tab);
        }


        String[] numsTemp = new String[1008];

        numsTemp[0] = "0.1";
        numsTemp[1] = "0.2";
        numsTemp[2] = "0.3";
        numsTemp[3] = "0.4";
        numsTemp[4] = "0.5";
        numsTemp[5] = "0.6";
        numsTemp[6] = "0.7";
        numsTemp[7] = "0.8";
        numsTemp[8] = "0.9";

        int count = 1;

        for (int i = 9; i < numsTemp.length; i++) {

            numsTemp[i] = Integer.toString(count);
            count++;
        }


        nums = numsTemp;

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

    public static String formatPrice(float price) {

        //in case minus 0 is returned
        price = Math.abs(price);

        Locale locale = new Locale("uk", "UA");

        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);

        return formatter.format(price);
    }


}
