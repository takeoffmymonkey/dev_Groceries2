/*
This is MainActivity
 */
package com.example.android.groceries2.activities;


import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.groceries2.R;
import com.example.android.groceries2.adapters.CategoryAdapter;
import com.example.android.groceries2.db.GroceriesDbHelper;

import java.text.NumberFormat;
import java.util.Locale;

import static com.example.android.groceries2.db.GroceriesDbHelper.DB_NAME;
import static com.example.android.groceries2.db.GroceriesDbHelper.DB_VERSION;
import static com.example.android.groceries2.db.GroceriesDbHelper.IMAGE_COLUMN;
import static com.example.android.groceries2.db.GroceriesDbHelper.ITEMS_TABLE_NAME;
import static com.example.android.groceries2.db.GroceriesDbHelper.LIST_AMOUNT_COLUMN;
import static com.example.android.groceries2.db.GroceriesDbHelper.LIST_ITEM_COLUMN;
import static com.example.android.groceries2.db.GroceriesDbHelper.MEASURE_COLUMN;
import static com.example.android.groceries2.db.GroceriesDbHelper.MEASURE_TABLE_NAME;
import static com.example.android.groceries2.db.GroceriesDbHelper.NAME_COLUMN;
import static com.example.android.groceries2.db.GroceriesDbHelper.PRICE_COLUMN;


public class MainActivity extends AppCompatActivity {

    public static int primaryColor;
    public static int primaryDarkColor;
    public static int primaryLightColor;
    public static int accentColor;
    public static int primaryTextColor;
    public static int secondaryTextColor;
    public static int dividerColor;
    public static int iconsColor;

    public static GroceriesDbHelper dbHelper;
    public static SQLiteDatabase db;
    static TabLayout tabLayout;
    public static String nums[];
    public static int snackLines;
    public static boolean snackOn = false;
    public static Snackbar snackBar;
    public static String[] images;
    public static Integer[] imagesIDs;

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_FIRST_LAUNCH = "firstLaunch";
    boolean firstLaunch;
    public static final String APP_PREFERENCES_RUSSIAN_OK = "russianOk";
    boolean russianOk;

    SharedPreferences mSettings;


    @Override
    protected void onStart() {
        super.onStart();
        Log.w("WARNING: ", "IN ONSTART OF MAIN ACTIVITY");
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.w("WARNING: ", "IN ONRESUME OF MAIN ACTIVITY");
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.w("WARNING: ", "IN ONPAUSE OF MAIN ACTIVITY");
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.w("WARNING: ", "IN ONSTOP OF MAIN ACTIVITY");
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Log.w("WARNING: ", "IN ONRESTART OF MAIN ACTIVITY");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w("WARNING: ", "IN ONCREATE OF MAIN ACTIVITY");


        setContentView(R.layout.activity_main);


        //Get db object
        dbHelper = new GroceriesDbHelper(this, DB_NAME, null, DB_VERSION);
        db = dbHelper.getReadableDatabase();


        //Tab to open
        int tab = 0;
        if (dbHelper.getActiveListVersion() == 0) tab = 1;

        //Get settings values
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        firstLaunch = mSettings.getBoolean(APP_PREFERENCES_FIRST_LAUNCH, true);
        russianOk = mSettings.getBoolean(APP_PREFERENCES_RUSSIAN_OK, false);


        //Check if first launch
        if (firstLaunch) {// This is first launch
            //Create items
            String[] names = getResources().getStringArray(R.array.array_auto_name_list);
            int[] measures = getResources().getIntArray(R.array.array_auto_measure_list);
            String[] prices = getResources().getStringArray(R.array.array_auto_price_list);
            int[] images = getResources().getIntArray(R.array.array_auto_image_list);
            for (int i = 0; i < prices.length; i++) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(NAME_COLUMN, names[i]);
                contentValues.put(PRICE_COLUMN, Float.parseFloat(prices[i]));
                contentValues.put(MEASURE_COLUMN, measures[i] + 1);
                contentValues.put(IMAGE_COLUMN, images[i]);
                db.insert(ITEMS_TABLE_NAME, null, contentValues);
            }
            //Update to not first launch
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putBoolean(APP_PREFERENCES_FIRST_LAUNCH, false);
            editor.apply();
        }


        //Check if russian user is a normal person
        if (!russianOk && Locale.getDefault().getDisplayLanguage().equals("русский")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Я не мог не заметить, что ты предпочитаешь русский язык..")
                    .setMessage("Чей Крым?")
                    .setCancelable(false)
                    .setPositiveButton("Украинский",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //Update to normal russian
                                    SharedPreferences.Editor editor = mSettings.edit();
                                    editor.putBoolean(APP_PREFERENCES_RUSSIAN_OK, true);
                                    editor.apply();
                                    //Respond to user
                                    Toast.makeText(MainActivity.this, "Так, синку", Toast.LENGTH_SHORT).show();
                                    dialog.cancel();
                                }
                            })
                    .setNeutralButton("Я аполитичен",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //Update to normal russian
                                    SharedPreferences.Editor editor = mSettings.edit();
                                    editor.putBoolean(APP_PREFERENCES_RUSSIAN_OK, true);
                                    editor.apply();
                                    Toast.makeText(MainActivity.this,
                                            "Молодец. Посмотри на досуге происхождение слова идиот",
                                            Toast.LENGTH_SHORT).show();
                                    dialog.cancel();
                                }
                            })
                    //Set cancel button with click listener
                    .setNegativeButton("Русский",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Toast.makeText(MainActivity.this,
                                            "Ваши данный успешно добавлены в базу Миротворец!",
                                            Toast.LENGTH_SHORT).show();
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }


        Resources resources = getResources();

        //Get colors
        primaryColor = resources.getColor(R.color.colorPrimary);
        primaryDarkColor = resources.getColor(R.color.colorPrimaryDark);
        primaryLightColor = resources.getColor(R.color.colorPrimaryLight);
        accentColor = resources.getColor(R.color.colorAccent);
        primaryTextColor = resources.getColor(R.color.colorPrimaryText);
        secondaryTextColor = resources.getColor(R.color.colorSecondaryText);
        dividerColor = resources.getColor(R.color.colorDivider);
        iconsColor = resources.getColor(R.color.colorIcons);


        //Setting adapter and view pager
        CategoryAdapter adapter = new CategoryAdapter(getSupportFragmentManager());
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabTextColors(iconsColor, iconsColor);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (snackOn && snackBar != null) {
                    snackBar.dismiss();
                    MainActivity.snackLines = 0;
                    setSnackOnState(false, null);
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (snackOn && snackBar != null) {
                    snackBar.dismiss();
                    MainActivity.snackLines = 0;
                    setSnackOnState(false, null);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (snackOn && snackBar != null) {
                    snackBar.dismiss();
                    MainActivity.snackLines = 0;
                    setSnackOnState(false, null);
                }
            }
        });


        //Check what tab to select
        int tabFromIntent = getIntent().getIntExtra("tab", -1);
        if (tabFromIntent != -1) {//There is a request to tab select from intent
            selectTab(tabFromIntent);
        } else { //there is no intent request for tab select
            selectTab(tab);
        }


        //Create array for number picker of selecting amount dialog
        String[] numsTemp = new String[999];
        int first = 0;
        int second = 1;

        for (int i = 0; i < numsTemp.length; i++) {

            if (second == 10) {

                first++;
                second = 1;
                numsTemp[i] = Integer.toString(first);

            } else {
                numsTemp[i] = Integer.toString(first) + "." + Integer.toString(second);
                second++;
            }

        }

        nums = numsTemp;


        //Create arrays for images names and ids

        images = resources.getStringArray(R.array.array_images);
        Integer[] imagesIDsTemp = new Integer[147];
        String packageName = getPackageName();
        String type = "drawable";

        for (int i = 0; i < imagesIDsTemp.length; i++) {

            imagesIDsTemp[i] = resources.getIdentifier(images[i], type, packageName);

        }

        imagesIDs = imagesIDsTemp;

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


    public static String getActiveListAsString() {


        Cursor cursorActiveList = db.query(dbHelper.getActiveListName(), null, null,
                null, null, null, null);

        int rows = cursorActiveList.getCount();

        if (rows > 0) {

            snackLines = rows + 2;

            cursorActiveList.moveToFirst();

            StringBuilder sb = new StringBuilder();

            float total = 0f;

            Cursor measureTableCursor = db.query(MEASURE_TABLE_NAME, null, null,
                    null, null, null, null);

            measureTableCursor.moveToFirst();

            for (int i = 0; i < rows; i++) {


                String name = cursorActiveList.getString(cursorActiveList
                        .getColumnIndex(LIST_ITEM_COLUMN));

                float amount = cursorActiveList.getFloat(cursorActiveList
                        .getColumnIndex(LIST_AMOUNT_COLUMN));

                int measure = cursorActiveList.getInt(cursorActiveList
                        .getColumnIndex(MEASURE_COLUMN));

                String amountString;

                measureTableCursor.moveToPosition(measure - 1);

                String measureString = measureTableCursor.getString
                        (measureTableCursor.getColumnIndex(MEASURE_COLUMN));

                if (measureString.equals("items")) measureString = "it.";

                if (amount == Math.round(amount)) {
                    amountString = Integer.toString(Math.round(amount));
                } else {
                    amountString = Float.toString(amount);
                }

                float price = cursorActiveList.getFloat(cursorActiveList
                        .getColumnIndex(PRICE_COLUMN));

                total += price;

                String temp = name + " (" + amountString + measureString + ")" + " = " +
                        MainActivity.formatPrice(price) + "\n";

                float divider = temp.length() / 20f;

                if (divider > 1 && divider < 4) {
                    if (divider > 3) snackLines += 3;
                    else if (divider > 2) snackLines += 2;
                    else snackLines++;
                }

                sb.append(temp);

                cursorActiveList.moveToNext();
            }

            sb.append("= = = =" + "\n");
            sb.append("Total: " + MainActivity.formatPrice(total));

            String sendMessage = sb.toString();

            measureTableCursor.close();

            cursorActiveList.close();


            return sendMessage;

        }

        snackLines = 0;
        cursorActiveList.close();

        return null;
    }


    public static void showSnackBar(View view, String snackText) {
        final Snackbar snackBar = Snackbar.make(view,
                snackText,
                Snackbar.LENGTH_INDEFINITE);


        View snackbarView = snackBar.getView();
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setMaxLines(MainActivity.snackLines);


        //snackbarView.setBackgroundColor(MainActivity.primaryTextColor);
        //snackbarView.setBackgroundColor(Color.DKGRAY);

        snackBar.setAction("Close", new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                snackBar.dismiss();
                setSnackOnState(false, null);
                MainActivity.snackLines = 0;

                Log.e("WARNING: ", "KILLING SNACK");
            }


        });


        snackbarView.setBackgroundColor(primaryDarkColor);
        snackBar.setActionTextColor(Color.WHITE);

        snackBar.show();

        Log.e("WARNING: ", "SHOWING SNACK");

        setSnackOnState(true, snackBar);

    }


    public static void setSnackOnState(boolean state, Snackbar snackbar) {
        snackOn = state;
        snackBar = snackbar;
    }


    public boolean getSnackOnState() {
        return snackOn;
    }


}