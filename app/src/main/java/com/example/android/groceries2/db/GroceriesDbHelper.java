package com.example.android.groceries2.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.example.android.groceries2.R;
import com.example.android.groceries2.activities.HistoryActivity;
import com.example.android.groceries2.activities.MainActivity;
import com.example.android.groceries2.fragments.ItemsFragment;
import com.example.android.groceries2.fragments.ListFragment;

import static com.example.android.groceries2.activities.HistoryActivity.historyProgressBar;
import static com.example.android.groceries2.activities.MainActivity.db;


/**
 * Created by takeoff on 001 01 Jun 17.
 * <p>
 * Info:
 * A helper class to manage database creation and version management.
 * <p>
 * You create a subclass implementing onCreate(SQLiteDatabase), onUpgrade(SQLiteDatabase, int, int)
 * and optionally onOpen(SQLiteDatabase), and this class takes care of opening the database if it
 * exists, creating it if it does not, and upgrading it as necessary. Transactions are used to make
 * sure the database is always in a sensible checkBoxState.
 * <p>
 * This class makes it easy for ContentProvider implementations to defer opening and upgrading the
 * database until first use, to avoid blocking application startup with long-running database upgrades.
 * <p>
 * Note: this class assumes monotonically increasing version numbers for upgrades.
 */

public class GroceriesDbHelper extends SQLiteOpenHelper {

    private Context context;

    public static final String DB_NAME = "GROCERIES_db";
    public static final int DB_VERSION = 1;
    public static final String ID_COLUMN = "_id";
    public static final String PRICE_COLUMN = "price";
    public static final String NAME_COLUMN = "name";
    public static final String CHECKED_COLUMN = "checked";
    public static final String MEASURE_COLUMN = "measure";
    public static final String IMAGE_COLUMN = "image";


    /*ITEMS table*/
    public static final String ITEMS_TABLE_NAME = "ITEMS_table";
    //table create command
    public static final String ITEMS_TABLE_CREATE_COMMAND = "CREATE TABLE " + ITEMS_TABLE_NAME + " (" +
            ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NAME_COLUMN + " TEXT NOT NULL UNIQUE, " +
            PRICE_COLUMN + " REAL NOT NULL DEFAULT 0, " +
            MEASURE_COLUMN + " INTEGER NOT NULL, " +
            IMAGE_COLUMN + " INTEGER DEFAULT 11, " +
            CHECKED_COLUMN + " INTEGER DEFAULT 0);";
    //table drop command
    public static final String ITEMS_TABLE_DROP_COMMAND = "DROP TABLE " + ITEMS_TABLE_NAME + ";";


    /*MEASURE table*/
    public static final String MEASURE_TABLE_NAME = "MEASURE_table";
    //table create command
    static final String MEASURE_TABLE_CREATE_COMMAND = "CREATE TABLE " + MEASURE_TABLE_NAME + " (" +
            ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MEASURE_COLUMN + " TEXT NOT NULL UNIQUE);";


    /*LOG table*/
    public static final String LOG_TABLE_NAME = "LOG_table";
    static public final String LOG_VERSION_COLUMN = "version";

    static public final String LOG_ACTIVE_COLUMN = "active";
    static public final String LOG_DATE_CREATED_COLUMN = "created";
    static public final String LOG_DATE_COMPLETE_COLUMN = "complete";
    static public final String LOG_TOTAL_COLUMN = "total";
    //table create command
    static public final String LOG_TABLE_CREATE_COMMAND = "CREATE TABLE " + LOG_TABLE_NAME + " (" +
            ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NAME_COLUMN + " TEXT NOT NULL UNIQUE, " +
            LOG_VERSION_COLUMN + " INTEGER UNIQUE, " +
            LOG_ACTIVE_COLUMN + " INTEGER DEFAULT 0, " +
            LOG_TOTAL_COLUMN + " REAL DEFAULT 0, " +
            LOG_DATE_CREATED_COLUMN + " INTEGER NOT NULL UNIQUE, " +
            LOG_DATE_COMPLETE_COLUMN + " INTEGER DEFAULT 0);";
    //DROP TABLE command
    public static final String LOG_TABLE_DROP_COMMAND = "DROP TABLE " + LOG_TABLE_NAME + ";";


    /*LIST table*/
    private static final String LIST_TABLE_NAME_part_1 = "List_";
    public static final String LIST_ITEM_COLUMN = "item";
    public static final String LIST_AMOUNT_COLUMN = "amount";
    //CREATE TABLE command
    private String LIST_INIT_TABLE_CREATE_COMMAND = "CREATE TABLE " + LIST_TABLE_NAME_part_1 + "0" +
            " (" +
            ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            LIST_ITEM_COLUMN + " TEXT NOT NULL UNIQUE, " +
            LIST_AMOUNT_COLUMN + " REAL, " +
            PRICE_COLUMN + " REAL NOT NULL DEFAULT 0, " +
            MEASURE_COLUMN + " INTEGER NOT NULL, " +
            IMAGE_COLUMN + " INTEGER DEFAULT 132, " +
            CHECKED_COLUMN + " INTEGER DEFAULT 0);";


    /*IMAGES table*/
    public static final String IMAGES_TABLE_NAME = "IMAGES_table";
    public static final String IMAGES_EN_COLUMN = "en";
    public static final String IMAGES_UA_COLUMN = "ua";
    public static final String IMAGES_RU_COLUMN = "ru";
    //CREATE TABLE command
    private String IMAGES_TABLE_CREATE_COMMAND = "CREATE TABLE " + IMAGES_TABLE_NAME +
            " (" +
            ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            IMAGES_EN_COLUMN + " TEXT, " +
            IMAGES_UA_COLUMN + " TEXT, " +
            IMAGES_RU_COLUMN + " TEXT);";


    public GroceriesDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                             int version) {
        super(context, name, factory, version);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create tables

        db.execSQL(ITEMS_TABLE_CREATE_COMMAND);
        db.execSQL(LOG_TABLE_CREATE_COMMAND);
        db.execSQL(LIST_INIT_TABLE_CREATE_COMMAND);

        db.execSQL(MEASURE_TABLE_CREATE_COMMAND);
        //Get the array with measurement values
        String[] measures = context.getResources().getStringArray(R.array.array_measurement_options);
        //Insert measurement values to MEASURE_table
        for (String i : measures) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MEASURE_COLUMN, i);
            db.insert(MEASURE_TABLE_NAME, null, contentValues);
        }

        db.execSQL(IMAGES_TABLE_CREATE_COMMAND);
        //Get the array with images names
        String[] images = context.getResources().getStringArray(R.array.array_images);
        //Insert images to IMAGES_table
        for (String i : images) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(IMAGES_EN_COLUMN, i);
            db.insert(IMAGES_TABLE_NAME, null, contentValues);
        }


        //Populate list on startup
        ItemsFragment.populateList();

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Db stays version 1, nothing to do here
    }


    /*Returns the number of list in Log table*/
    public int getListsCount() {
        Cursor cursor = db.query(LOG_TABLE_NAME,
                new String[]{ID_COLUMN},
                null, null, null, null, null);

        int count = cursor.getCount();

        cursor.close();

        return count;

    }

    /*Returns version of the list which is on the last row of LOG table*/
    public int getLatestListVersion() {

        int latestCode;

        Cursor cursor = db.query(LOG_TABLE_NAME,
                new String[]{LOG_VERSION_COLUMN},
                null, null, null, null, null);

        //Check if there are lists at all
        if (cursor.getCount() > 0) {
            //There are lists

            cursor.moveToLast();
            latestCode = cursor.getInt(cursor.getColumnIndexOrThrow(LOG_VERSION_COLUMN));
            cursor.close();

            return latestCode;

        } else {
            //No lists

            cursor.close();
            return 0;

        }
    }


    /*Returns name of the list which is on the last row of LOG table*/
    public String getLatestListName() {
        return "List_" + getLatestListVersion();
    }


    /*Returns version of the active list*/
    public int getActiveListVersion() {

        int activeVersion;

        Cursor cursor = db.query(LOG_TABLE_NAME,
                new String[]{LOG_VERSION_COLUMN},
                LOG_ACTIVE_COLUMN + "=?", new String[]{"1"},
                null, null, null);

        //Must be only 1 active list
        if (cursor.getCount() == 1) {
            //There are lists

            cursor.moveToFirst();
            activeVersion = cursor.getInt(cursor.getColumnIndexOrThrow(LOG_VERSION_COLUMN));
            cursor.close();

            return activeVersion;

        } else {
            //No lists

            cursor.close();

            return 0;
        }
    }


    /*Returns name of the active list*/
    public String getActiveListName() {
        return "List_" + getActiveListVersion();
    }


    /*Updates active state of lists*/
    public void setActiveListVersion(int version) {

        //if 0 received
        if (version == 0) {
            //Set all items to 0

            //update db
            //Set all lists to inactive state
            ContentValues contentValues = new ContentValues();
            contentValues.put(LOG_ACTIVE_COLUMN, 0);
            db.update(LOG_TABLE_NAME, contentValues,
                    LOG_ACTIVE_COLUMN + "=?", new String[]{"1"});

        } else {
            //Version is not 0

            Cursor cursor = db.query(LOG_TABLE_NAME,
                    new String[]{LOG_VERSION_COLUMN},
                    LOG_VERSION_COLUMN + "=?", new String[]{Integer.toString(version)},
                    null, null, null);

            //Version must be real
            if (cursor.getCount() == 1) {
                //List found

                //Set all lists to inactive state
                ContentValues contentValues = new ContentValues();
                contentValues.put(LOG_ACTIVE_COLUMN, 0);
                db.update(LOG_TABLE_NAME, contentValues,
                        LOG_ACTIVE_COLUMN + "=?", new String[]{"1"});

                //Set required list to active state
                ContentValues contentValues2 = new ContentValues();
                contentValues2.put(LOG_ACTIVE_COLUMN, 1);
                db.update(LOG_TABLE_NAME, contentValues2,
                        LOG_VERSION_COLUMN + "=?", new String[]{Integer.toString(version)});

                cursor.close();

            } else {
                //List not found

                Log.w("WARNING: ", "setActiveListVersion(): No such version: " + version);

                cursor.close();
            }
        }

    }


    public void reactivateList(int version) {


        class AsyncReactivate extends AsyncTask<Integer, Void, Boolean> {

            @Override
            protected Boolean doInBackground(Integer... versionArr) {


                int version = versionArr[0];

                approveCurrentList();

                Cursor cursor = db.query(LOG_TABLE_NAME,
                        new String[]{LOG_TOTAL_COLUMN},
                        LOG_VERSION_COLUMN + "=?", new String[]{Integer.toString(version)},
                        null, null, null);

                //Version must be real
                if (cursor.getCount() == 1) {
                    //List found

                    cursor.moveToFirst();

                    //Delete old list
                    float total = cursor.getFloat(cursor.getColumnIndex(LOG_TOTAL_COLUMN));
                    db.delete(LOG_TABLE_NAME, LOG_VERSION_COLUMN + "=?",
                            new String[]{Integer.toString(version)});
                    cursor.close();

                    //Set all lists to inactive state
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(LOG_ACTIVE_COLUMN, 0);
                    db.update(LOG_TABLE_NAME, contentValues,
                            LOG_ACTIVE_COLUMN + "=?", new String[]{"1"});

                    //Insert new list
                    ContentValues contentValues2 = new ContentValues();
                    contentValues2.put(NAME_COLUMN, "List_" + version);
                    contentValues2.put(LOG_VERSION_COLUMN, version);
                    contentValues2.put(LOG_ACTIVE_COLUMN, 1);
                    contentValues2.put(LOG_TOTAL_COLUMN, total);
                    contentValues2.put(LOG_DATE_CREATED_COLUMN, System.currentTimeMillis());
                    contentValues2.put(LOG_DATE_COMPLETE_COLUMN, 0);
                    db.insert(LOG_TABLE_NAME, null, contentValues2);


                    //Uncheck all items in list
                    ContentValues contentValues5 = new ContentValues();
                    contentValues5.put(CHECKED_COLUMN, 0);
                    db.update("List_" + version, contentValues5,
                            CHECKED_COLUMN + "=?", new String[]{"1"});

                    //Check items in ITEMS table according to requested table
                    Cursor cursorListTable = db.query("List_" + version, null, null, null, null, null, null);

                    int count = cursorListTable.getCount();

                    if (count > 0) {

                        cursorListTable.moveToFirst();

                        for (int i = 0; i < count; i++) {

                            String item = cursorListTable.getString(cursorListTable
                                    .getColumnIndex(LIST_ITEM_COLUMN));

                            int image = cursorListTable.getInt(cursorListTable
                                    .getColumnIndex(IMAGE_COLUMN));

                            float amount = cursorListTable.getFloat(cursorListTable
                                    .getColumnIndex(LIST_AMOUNT_COLUMN));

                            float totalPrice = cursorListTable.getFloat(cursorListTable
                                    .getColumnIndex(PRICE_COLUMN));

                            int measure = cursorListTable.getInt(cursorListTable
                                    .getColumnIndex(MEASURE_COLUMN));

                            float price = totalPrice / amount;

                            ContentValues contentValues4 = new ContentValues();
                            contentValues4.put(NAME_COLUMN, item);
                            contentValues4.put(PRICE_COLUMN, price);
                            contentValues4.put(MEASURE_COLUMN, measure);
                            contentValues4.put(IMAGE_COLUMN, image);
                            contentValues4.put(CHECKED_COLUMN, 1);

                            if (db.update(ITEMS_TABLE_NAME, contentValues4,
                                    NAME_COLUMN + "=?", new String[]{item}) < 1) {
                                //Item is not in the ITEMS table
                                db.insert(ITEMS_TABLE_NAME, null, contentValues4);
                            }

                            cursorListTable.moveToNext();
                        }

                        cursorListTable.close();
                    }


                } else {
                    //List not found

                    Log.w("WARNING: ", "reactivateList(): No such version: " + version);

                    cursor.close();
                }

                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {

                HistoryActivity.refreshHistoryCursor(null, null, 0);
                historyProgressBar.setVisibility(View.GONE);
            }
        }

        historyProgressBar.setVisibility(View.VISIBLE);

        new AsyncReactivate().execute(version);

    }

    /*Creates new List table*/
    public void createListTable() {

        int count = getListsCount();
        int newVersion = 0;


        //Calculate proper version
        if (count == 0) {//no rows found
            newVersion = count + 1;
        } else { //rows found
            Cursor logTableCursor = db.query(LOG_TABLE_NAME,
                    new String[]{"MAX(" + LOG_VERSION_COLUMN + ")"},
                    null, null, null, null, null);
            if (logTableCursor.getCount() > 0) {
                logTableCursor.moveToFirst();
                String maxVersion = logTableCursor.getString(0);
                Log.e("WARNING: ", "createListTable(): maxVersion: " + maxVersion);
                newVersion = Integer.parseInt(maxVersion) + 1;
            } else {
                Log.e("WARNING: ", "createListTable(): Cursor has no rows");
            }
            logTableCursor.close();
        }


        //Create new table
        String LIST_TABLE_CREATE_COMMAND = "CREATE TABLE " + LIST_TABLE_NAME_part_1 + newVersion +
                " (" +
                ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LIST_ITEM_COLUMN + " INTEGER NOT NULL UNIQUE, " +
                LIST_AMOUNT_COLUMN + " REAL, " +
                PRICE_COLUMN + " REAL NOT NULL DEFAULT 0, " +
                MEASURE_COLUMN + " INTEGER NOT NULL, " +
                IMAGE_COLUMN + " INTEGER DEFAULT 11, " +
                CHECKED_COLUMN + " INTEGER DEFAULT 0);";
        db.execSQL(LIST_TABLE_CREATE_COMMAND);


        //Update LOG_table
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME_COLUMN, LIST_TABLE_NAME_part_1 + newVersion);
        contentValues.put(LOG_VERSION_COLUMN, newVersion);
        contentValues.put(LOG_DATE_CREATED_COLUMN, System.currentTimeMillis());
        db.insert(LOG_TABLE_NAME, null, contentValues);//add new record to LOG_table


        //Set latest list table to active state
        setActiveListVersion(newVersion);

    }


    /*Deletes List table*/
    public void deleteListTable(int version) {

        Cursor cursor = db.query(LOG_TABLE_NAME,
                new String[]{LOG_VERSION_COLUMN},
                LOG_VERSION_COLUMN + "=?", new String[]{Integer.toString(version)},
                null, null, null);

        //Version must be real
        if (cursor.getCount() == 1) {
            //List found

            cursor.close();

            String LIST_TABLE_DROP_COMMAND = "DROP TABLE " + "List_" + version + ";";

            //Drop current table
            db.execSQL(LIST_TABLE_DROP_COMMAND);

            //Check if list was active
            if (getActiveListVersion() == version) {
                //List was active

                //Uncheck all items in Item table
                ContentValues contentValues = new ContentValues();
                contentValues.put(CHECKED_COLUMN, 0);
                db.update(ITEMS_TABLE_NAME, contentValues,
                        CHECKED_COLUMN + "=?",
                        new String[]{"1"});

            }

            //Delete current list from LOG_table
            db.delete(LOG_TABLE_NAME, LOG_VERSION_COLUMN + "=?",
                    new String[]{Integer.toString(version)});

        } else {
            //List not found

            Log.w("WARNING: ", "deleteListTable(): No such version: " + version);

            cursor.close();

        }
    }


    /*Mark current list as complete:*/
    public boolean approveCurrentList() {

        int latestVersion = getLatestListVersion();
        String latestName = "List_" + latestVersion;
        //Check if latest list is active
        if (latestVersion == getActiveListVersion()) {
            //List is active

            //Uncheck all items in Item table
            ContentValues contentValuesItems = new ContentValues();
            contentValuesItems.put(CHECKED_COLUMN, 0);
            db.update(ITEMS_TABLE_NAME, contentValuesItems,
                    CHECKED_COLUMN + "=?",
                    new String[]{"1"});

            //Check all items in List_? table
            ContentValues contentValuesList = new ContentValues();
            contentValuesList.put(CHECKED_COLUMN, 1);
            db.update(latestName, contentValuesList,
                    CHECKED_COLUMN + "=?",
                    new String[]{"0"});

            //Update log table
            ContentValues contentValuesLog = new ContentValues();
            contentValuesLog.put(LOG_ACTIVE_COLUMN, 0);
            contentValuesLog.put(LOG_DATE_COMPLETE_COLUMN, System.currentTimeMillis());
            db.update(LOG_TABLE_NAME, contentValuesLog,
                    NAME_COLUMN + "=?",
                    new String[]{latestName});

        }

        return true;
    }


    /*Deletes all items and lists
    * 0 - delete lists
    * 1 - delete items
    */
    public void deleteAll(int command) {

        //Check if to delete lists
        if (command == 0) {

            //Resetting active version to 0
            setActiveListVersion(0);

            Cursor cursor = db.query(LOG_TABLE_NAME,
                    new String[]{LOG_VERSION_COLUMN},
                    null, null, null, null, null);

            int cursorRowsCount = cursor.getCount();

            //Check if there are rows
            if (cursorRowsCount > 0) {

                cursor.moveToFirst();

                //Delete all list tables
                for (int i = 0; i < cursorRowsCount; i++) {

                    int a = cursor.getInt(cursor.getColumnIndex(LOG_VERSION_COLUMN));
                    db.execSQL("DROP TABLE " + "List_" + a + ";");
                    cursor.moveToNext();
                    Log.w("INFO: ", "List_" + a + " deleted");

                }
            }

            //Drop log table
            db.execSQL(LOG_TABLE_DROP_COMMAND);
            //Create log table
            db.execSQL(LOG_TABLE_CREATE_COMMAND);


            //Need to uncheck everything that was checked
            ContentValues contentValues = new ContentValues();
            contentValues.put(CHECKED_COLUMN, "0");
            db.update(ITEMS_TABLE_NAME, contentValues,
                    CHECKED_COLUMN + "=?",
                    new String[]{"1"});

        } else if (command == 1) {
            //Need to delete items table

            //Drop items table
            db.execSQL(ITEMS_TABLE_DROP_COMMAND);

            //Create items table
            db.execSQL(ITEMS_TABLE_CREATE_COMMAND);

            //Delete current list if there is
            int currentActiveListVersion = getActiveListVersion();

            if (currentActiveListVersion != 0) deleteListTable(currentActiveListVersion);

        }


    }


    /*Returns total for required list table*/
    public static float getTotal(int listTableVersion) {

        Log.e("WARNING: ", "getTotal() input version:" + listTableVersion);

        if (listTableVersion == 0) {
            //version 0 requested

            Log.e("WARNING: ", "getTotal() output:" + 0.0f);

            return 0.0f;

        } else {
            //version other than 0

            Cursor cursor = db.query(LOG_TABLE_NAME,
                    new String[]{LOG_VERSION_COLUMN, LOG_TOTAL_COLUMN},
                    LOG_VERSION_COLUMN + "=?", new String[]{Integer.toString(listTableVersion)},
                    null, null, null);

            //Check if row is found, should be only one
            if (cursor.getCount() == 1) {
                //row is found

                cursor.moveToFirst();

                float total;
                total = cursor.getFloat(cursor.getColumnIndex(LOG_TOTAL_COLUMN));

                cursor.close();

                Log.e("WARNING: ", "getTotal() output:" + total);

                return total;

            } else {
                //row is not found

                Log.w("WARNING: ", "getTotal(): no such list version: " + listTableVersion);

                cursor.close();

                Log.e("WARNING: ", "getTotal() output:" + "-1");

                return -1;
            }

        }

    }


    /*Updates total
    * action 0 - minus
    * action 1 - plus*/
    public static void updateTotal(int listVersion, int action, float change) {

        Log.e("WARNING: ", "updateTotal() input:"
                + " version: " + listVersion
                + ", action: " + action + ", change: " + change);

        //Check if version is 0
        if (listVersion == 0) {
            //Version is 0

            Log.w("WARNING: ", "setTotal(): can't set new total for List_0");

        } else {
            //list version is not 0

            float currentTotal = getTotal(listVersion);

            Log.e("WARNING: ", "updateTotal(): received total from getTotal():" + currentTotal);

            //check if there is such version (-1 means no such version)
            if (currentTotal == -1) {
                //no such version

            } else {
                //there is such version

                float newTotal = 0.0f;

                switch (action) {

                    case 0:
                        //"minus" action

                        newTotal = currentTotal - change;

                        break;

                    case 1:
                        //"plus" action

                        newTotal = currentTotal + change;

                        break;
                }


                //update table
                ContentValues contentValues = new ContentValues();
                contentValues.put(LOG_TOTAL_COLUMN, newTotal);
                db.update(LOG_TABLE_NAME, contentValues,
                        LOG_VERSION_COLUMN + "=?",
                        new String[]{Integer.toString(listVersion)});


                String formattedTotal = MainActivity.formatPrice(newTotal);

                Log.e("WARNING: ", "updateTotal(): " + "returning newTotal: " + newTotal);

                //update text views
                ItemsFragment.itemsTotalTextView.setText("Total: " + formattedTotal);
                ListFragment.listTotalTextView.setText("Total: " + formattedTotal);

            }

        }

    }


}
