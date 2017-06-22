package com.example.android.groceries2.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.groceries2.ItemsFragment;
import com.example.android.groceries2.ListFragment;
import com.example.android.groceries2.LogFragment;
import com.example.android.groceries2.R;

import static com.example.android.groceries2.MainActivity.db;


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

    //Database name
    public static final String DB_NAME = "GROCERIES_db";
    //Database version
    public static final int DB_VERSION = 1;
    //id column for all tables
    public static final String ID_COLUMN = "_id";
    //price column for all tables
    public static final String PRICE_COLUMN = "price";
    //name column for all tables
    public static final String NAME_COLUMN = "name";
    //checked state column
    public static final String CHECKED_COLUMN = "checked";
    //measure column
    public static final String MEASURE_COLUMN = "measure";


    /*ITEMS table*/
    public static final String ITEMS_TABLE_NAME = "ITEMS_table";
    //measure column
    //table create command
    public static final String ITEMS_TABLE_CREATE_COMMAND = "CREATE TABLE " + ITEMS_TABLE_NAME + " (" +
            ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NAME_COLUMN + " TEXT NOT NULL UNIQUE, " +
            PRICE_COLUMN + " REAL NOT NULL DEFAULT 0, " +
            MEASURE_COLUMN + " INTEGER NOT NULL, " +
            CHECKED_COLUMN + " INTEGER DEFAULT 0);";
    //table drop command
    public static final String ITEMS_TABLE_DROP_COMMAND = "DROP TABLE " + ITEMS_TABLE_NAME + ";";


    /*MEASURE table*/
    static final String MEASURE_TABLE_NAME = "MEASURE_table";
    //table create command
    static final String MEASURE_TABLE_CREATE_COMMAND = "CREATE TABLE " + MEASURE_TABLE_NAME + " (" +
            ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MEASURE_COLUMN + " TEXT NOT NULL UNIQUE);";


    /*LOG table*/
    public static final String LOG_TABLE_NAME = "LOG_table";
    //code column
    static public final String LOG_CODE_COLUMN = "code";
    //code column
    static public final String LOG_ACTIVE_COLUMN = "active";
    //creation date column
    static public final String LOG_DATE_CREATED_COLUMN = "created";
    //completion date column
    static public final String LOG_DATE_COMPLETE_COLUMN = "complete";
    //completion date column
    static public final String LOG_TOTAL_COLUMN = "total";
    //table create command
    static public final String LOG_TABLE_CREATE_COMMAND = "CREATE TABLE " + LOG_TABLE_NAME + " (" +
            ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NAME_COLUMN + " TEXT NOT NULL UNIQUE, " +
            LOG_CODE_COLUMN + " INTEGER UNIQUE, " +
            LOG_ACTIVE_COLUMN + " INTEGER DEFAULT 0, " +
            LOG_TOTAL_COLUMN + " REAL DEFAULT 0, " +
            LOG_DATE_CREATED_COLUMN + " INTEGER NOT NULL UNIQUE, " +
            LOG_DATE_COMPLETE_COLUMN + " INTEGER DEFAULT 0);";
    //Create string for DROP TABLE command
    public static final String LOG_TABLE_DROP_COMMAND = "DROP TABLE " + LOG_TABLE_NAME + ";";


    /*LIST table*/
    //List table name 1st part
    private static final String LIST_TABLE_NAME_part_1 = "List_";
    //List table item column
    static final String LIST_ITEM_COLUMN = "item";
    //List table amount column
    static final String LIST_AMOUNT_COLUMN = "amount";
    //Create string for CREATE TABLE command
    private String LIST_INIT_TABLE_CREATE_COMMAND = "CREATE TABLE " + LIST_TABLE_NAME_part_1 + "0" +
            " (" +
            ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            LIST_ITEM_COLUMN + " INTEGER NOT NULL UNIQUE, " +
            LIST_AMOUNT_COLUMN + " REAL, " +
            PRICE_COLUMN + " REAL NOT NULL DEFAULT 0, " +
            CHECKED_COLUMN + " INTEGER DEFAULT 0);";


    public GroceriesDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                             int version) {
        super(context, name, factory, version);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create ITEMS_table
        db.execSQL(ITEMS_TABLE_CREATE_COMMAND);
        Log.e("WARNING: ", "Created ITEMS TABLE");

        //Create LOG_table
        db.execSQL(LOG_TABLE_CREATE_COMMAND);
        Log.e("WARNING: ", "Created LOG TABLE");

        //Create MEASURE_table
        db.execSQL(MEASURE_TABLE_CREATE_COMMAND);
        //Get the array with measurement values
        String[] measures = context.getResources().getStringArray(R.array.array_measurement_options);

        //Insert measurement values to MEASURE_table
        for (String i : measures) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MEASURE_COLUMN, i);
            db.insert(MEASURE_TABLE_NAME, null, contentValues);
        }
        Log.e("WARNING: ", "Created MEASURE TABLE");

        //Create init list table for the cursor
        db.execSQL(LIST_INIT_TABLE_CREATE_COMMAND);
        Log.e("WARNING: ", "Created LIST INIT TABLE");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Db stays version 1, nothing to do here
    }


    /*Returns latest list version #*/
    public int getLatestListVersion() {

        //Var for version
        int latestVersion;

        //Get cursor with code column
        Cursor cursor = db.query(LOG_TABLE_NAME,
                new String[]{LOG_CODE_COLUMN},
                null, null, null, null, null);

        //Check if there are lists at all
        if (cursor.getCount() > 0) {
            //There are lists

            //Move cursor to last row
            cursor.moveToLast();
            //Get version
            latestVersion = cursor.getInt(cursor.getColumnIndexOrThrow(LOG_CODE_COLUMN));
            //Close cursor
            cursor.close();

            Log.e("WARNING: ", "RETURNING LATEST VERSION: " + latestVersion);
            //Return version
            return latestVersion;

        } else {
            //No lists

            //Close cursor
            cursor.close();

            Log.e("WARNING: ", "RETURNING LATEST VERSION: " + 0);
            //Return 0
            return 0;
        }
    }

    /*Returns latest list name*/
    public String getLatestListName() {

        //Var for latest list name
        String latestVersionName;

        //Get cursor with code column
        Cursor cursor = db.query(LOG_TABLE_NAME,
                new String[]{NAME_COLUMN},
                null, null, null, null, null);

        //Check if there are lists at all
        if (cursor.getCount() > 0) {
            //There are lists

            //Move cursor to last row
            cursor.moveToLast();
            //Get value
            latestVersionName = cursor.getString(cursor.getColumnIndexOrThrow(NAME_COLUMN));
            //Close cursor
            cursor.close();

            Log.e("WARNING: ", "RETURNING LATEST NAME: " + latestVersionName);
            //Return value
            return latestVersionName;

        } else {
            //No lists

            //Close cursor
            cursor.close();

            Log.e("WARNING: ", "RETURNING LATEST NAME: " + "List_0");
            //Return default list
            return "List_0";
        }
    }


    /*Returns version of the active list*/
    public int getActiveListVersion() {

        //Var for active version
        int activeVersion;

        //Get cursor with code column
        Cursor cursor = db.query(LOG_TABLE_NAME,
                new String[]{LOG_CODE_COLUMN},
                LOG_ACTIVE_COLUMN + "=?", new String[]{"1"},
                null, null, null);

        //Must be only 1 active list
        if (cursor.getCount() == 1) {
            //There are lists

            //Move to first row
            cursor.moveToFirst();
            //Get value
            activeVersion = cursor.getInt(cursor.getColumnIndexOrThrow(LOG_CODE_COLUMN));
            //Close cursor
            cursor.close();

            Log.e("WARNING: ", "RETURNING ACTIVE VERSION: " + activeVersion);
            //return value
            return activeVersion;

        } else {
            //No lists

            //Close cursor
            cursor.close();

            Log.e("WARNING: ", "RETURNING ACTIVE VERSION: " + 0);
            //Return 0
            return 0;
        }
    }


    /*Returns name of the active list*/
    public String getActiveListName() {

        //Var for value
        String latestVersionName;

        //Get cursor with code column
        Cursor cursor = db.query(LOG_TABLE_NAME,
                new String[]{NAME_COLUMN},
                LOG_ACTIVE_COLUMN + "=?", new String[]{"1"},
                null, null, null);

        Log.e("WARNING: ", "ROWS IN CURSOR FOR ACTIVE IN LOG: " + cursor.getCount());

        //Check if there are lists at all
        if (cursor.getCount() > 0) {
            //There are lists

            //Move cursor to last row
            cursor.moveToLast();
            //Get value
            latestVersionName = cursor.getString(cursor.getColumnIndexOrThrow(NAME_COLUMN));
            //Close cursor
            cursor.close();

            Log.e("WARNING: ", "RETURNING ACTIVE NAME: " + latestVersionName);
            //Return value
            return latestVersionName;

        } else {
            //No lists

            //Close cursor
            cursor.close();

            Log.e("WARNING: ", "NO ACTIVE LISTS. RETURNING ACTIVE NAME: " + "List_0");
            //Return default
            return "List_0";
        }
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
            Log.e("WARNING: ", "UPDATING LOG TABLE ACTIVE COLUMN TO 0");

        } else {
            //Version is not 0

            //Get cursor with code column
            Cursor cursor = db.query(LOG_TABLE_NAME,
                    new String[]{LOG_CODE_COLUMN},
                    LOG_CODE_COLUMN + "=?", new String[]{Integer.toString(version)},
                    null, null, null);

            //Version must be real
            if (cursor.getCount() == 1) {
                //List found

                //Set all lists to inactive state
                ContentValues contentValues = new ContentValues();
                contentValues.put(LOG_ACTIVE_COLUMN, 0);
                db.update(LOG_TABLE_NAME, contentValues,
                        LOG_ACTIVE_COLUMN + "=?", new String[]{"1"});
                Log.e("WARNING: ", "UPDATING LOG TABLE ACTIVE COLUMN TO 0");

                //Set list required list to active state
                ContentValues contentValues2 = new ContentValues();
                contentValues2.put(LOG_ACTIVE_COLUMN, 1);
                db.update(LOG_TABLE_NAME, contentValues2,
                        LOG_CODE_COLUMN + "=?", new String[]{Integer.toString(version)});
                Log.e("WARNING: ", "UPDATING LOG TABLE ACTIVE COLUMN TO 1 FOR VERSION: " + version);


                //Close cursor
                cursor.close();

            } else {

                //List not found
                Log.e("WARNING: ", "setActiveListVersion(): No such version: " + version);

                //Close cursor
                cursor.close();
            }
        }

    }


    /*Creates new List table*/
    void createListTable() {

        //Create int for new version
        int newVersion = getLatestListVersion() + 1;

        //Create string for CREATE TABLE command
        String LIST_TABLE_CREATE_COMMAND = "CREATE TABLE " + LIST_TABLE_NAME_part_1 + newVersion +
                " (" +
                ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LIST_ITEM_COLUMN + " INTEGER NOT NULL UNIQUE, " +
                LIST_AMOUNT_COLUMN + " REAL, " +
                PRICE_COLUMN + " REAL NOT NULL DEFAULT 0, " +
                CHECKED_COLUMN + " INTEGER DEFAULT 0);";

        //Create new table
        db.execSQL(LIST_TABLE_CREATE_COMMAND);

        Log.e("WARNING: ", "CREATED NEW LIST TABLE WITH VERSION: " + newVersion);

        //Update LOG_table
        //Create contentValues var to store values of new list record of the LOG_TABLE
        ContentValues contentValues = new ContentValues();
        //Put name value of new list table
        contentValues.put(NAME_COLUMN, LIST_TABLE_NAME_part_1 + newVersion);
        //Put code value of new list table
        contentValues.put(LOG_CODE_COLUMN, newVersion);
        //Put creation date of new list table in ms
        contentValues.put(LOG_DATE_CREATED_COLUMN, System.currentTimeMillis());
        //Update LOG_TABLE with new list record
        db.insert(LOG_TABLE_NAME, null, contentValues);//add new record to LOG_table
        Log.e("WARNING: ", "ADD NEW ROW TO LOG TABLE WITH VERSION: " + newVersion);


        //Set latest list table to active state
        setActiveListVersion(newVersion);

        //Update cursors
        ListFragment.refreshListCursor();
        ItemsFragment.refreshItemsCursor();
    }


    /*Deletes List table*/
    public void deleteListTable(int version) {

        //Get cursor with code column
        Cursor cursor = db.query(LOG_TABLE_NAME,
                new String[]{LOG_CODE_COLUMN},
                LOG_CODE_COLUMN + "=?", new String[]{Integer.toString(version)},
                null, null, null);

        //Version must be real
        if (cursor.getCount() == 1) {
            //List found

            //Close cursor
            cursor.close();

            //Create string for DROP TABLE command
            String LIST_TABLE_DROP_COMMAND = "DROP TABLE " + "List_" + version + ";";

            //Drop current table
            db.execSQL(LIST_TABLE_DROP_COMMAND);
            Log.e("WARNING: ", "DROPPED TABLE : " + "List_" + version);


            //Check if list was active
            if (getActiveListVersion() == version) {
                //List was active

                //Uncheck all items in Item table
                //Create contentValues var
                ContentValues contentValues = new ContentValues();
                //Put new value to it
                contentValues.put(CHECKED_COLUMN, 0);
                //Update table
                db.update(ITEMS_TABLE_NAME, contentValues,
                        CHECKED_COLUMN + "=?",
                        new String[]{"1"});
                Log.e("WARNING: ", "REMOVE CHECKING FROM ALL ITEMS IN ITEMS TABLE");

            }

            //Delete current list from LOG_table
            db.delete(LOG_TABLE_NAME, LOG_CODE_COLUMN + "=?",
                    new String[]{Integer.toString(version)});

            Log.e("WARNING: ", "DELETE ROW FROM LOG TABLE WITH VERSION: " + version);

            //Update cursors
            ListFragment.refreshListCursor();
            LogFragment.refreshLogCursor();
            ItemsFragment.refreshItemsCursor();


        } else {
            //List not found
            Log.e("WARNING: ", "deleteListTable(): No such version: " + version);

            //Close cursor
            cursor.close();

        }
    }


    /*Mark current list as complete:*/
    public boolean approveCurrentList() {

        //Check if latest list is active
        if (getLatestListVersion() == getActiveListVersion()) {
            //List is active

            //Uncheck all items in Item table
            //Create contentValues var
            ContentValues contentValuesItems = new ContentValues();
            //Put new value to it
            contentValuesItems.put(CHECKED_COLUMN, 0);
            //Update table
            db.update(ITEMS_TABLE_NAME, contentValuesItems,
                    CHECKED_COLUMN + "=?",
                    new String[]{"1"});
            Log.e("WARNING: ", "REMOVE CHECKING FROM ALL ITEMS IN ITEMS TABLE");
            //Refresh items cursor
            ItemsFragment.refreshItemsCursor();


            //Check all items in List_? table
            //Create contentValues var
            ContentValues contentValuesList = new ContentValues();
            //Put new value to it
            contentValuesList.put(CHECKED_COLUMN, 1);
            //Update table
            db.update(getLatestListName(), contentValuesList,
                    CHECKED_COLUMN + "=?",
                    new String[]{"0"});
            Log.e("WARNING: ", "CHECK ALL ITEMS IN ITEMS IN LIST: " + getLatestListName());
            //Refresh list cursor
            ListFragment.refreshListCursor();

            //Update log table:
            //Create contentValuesLog
            ContentValues contentValuesLog = new ContentValues();
            //New state of the table
            contentValuesLog.put(LOG_ACTIVE_COLUMN, 0);
            //Put there complete date of current list in ms
            contentValuesLog.put(LOG_DATE_COMPLETE_COLUMN, System.currentTimeMillis());
            //update table
            db.update(LOG_TABLE_NAME, contentValuesLog,
                    NAME_COLUMN + "=?",
                    new String[]{getLatestListName()});
            Log.e("WARNING: ", "UPDADE LOG TABLE: " + getLatestListName() + " SET ACTIVE 0");
            //Refresh log cursor
            LogFragment.refreshLogCursor();

        }

        return true;
    }


    /*Deletes all items and lists*/
    public void deleteAllItemsAndLists() {

        //Resetting active version to 0
        setActiveListVersion(0);
        Log.e("WARNING: ", "SET ACTIVE VERSION: 0, SET TO:" + getActiveListVersion());

        //Drop items table
        db.execSQL(ITEMS_TABLE_DROP_COMMAND);
        Log.e("WARNING: ", "DROP ITEMS TABLE");
        //Create items table
        db.execSQL(ITEMS_TABLE_CREATE_COMMAND);
        Log.e("WARNING: ", "CREATE ITEMS TABLE");
        //Refresh items cursor
        ItemsFragment.refreshItemsCursor();


        //Delete all lists, except list_0
        //Get current number of lists
        int count = getLatestListVersion();
        //Delete all
        for (int i = 1; i <= count; i++) {
            db.execSQL("DROP TABLE " + "List_" + i + ";");
            Log.e("WARNING: ", "DROP TABLE LIST_" + i);
        }
        //Refresh list cursor
        ListFragment.refreshListCursor();

        //Drop log table
        db.execSQL(LOG_TABLE_DROP_COMMAND);
        Log.e("WARNING: ", "DROP LOG TABLE");
        //Create log table
        db.execSQL(LOG_TABLE_CREATE_COMMAND);
        Log.e("WARNING: ", "DROP LOG TABLE");
        //Refresh log cursor
        LogFragment.refreshLogCursor();


    }
}
