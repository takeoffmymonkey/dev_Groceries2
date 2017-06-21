package com.example.android.groceries2.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.android.groceries2.ItemsFragment;
import com.example.android.groceries2.ListFragment;
import com.example.android.groceries2.LogFragment;
import com.example.android.groceries2.R;

import java.util.Set;

import static com.example.android.groceries2.ItemsFragment.refreshItemsCursor;
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


    /*VALUES table*/
    static final String VALUES_TABLE_NAME = "VALUES_table";
    //list version column
    static final String VALUES_LIST_VERSION_COLUMN = "list_version";
    //is active column
    static final String VALUES_IS_ACTIVE_COLUMN = "is_active";
    //table create command
    static final String VALUES_TABLE_CREATE_COMMAND = "CREATE TABLE " + VALUES_TABLE_NAME + " (" +
            ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            VALUES_LIST_VERSION_COLUMN + " INTEGER NOT NULL DEFAULT 0, " +
            VALUES_IS_ACTIVE_COLUMN + " INTEGER NOT NULL DEFAULT 0);";


    /*LOG table*/
    public static final String LOG_TABLE_NAME = "LOG_table";
    //creation date column
    static final String LOG_DATE_CREATED_COLUMN = "created";
    //completion date column
    static final String LOG_DATE_COMPLETE_COLUMN = "complete";
    //table create command
    public static final String LOG_TABLE_CREATE_COMMAND = "CREATE TABLE " + LOG_TABLE_NAME + " (" +
            ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NAME_COLUMN + " TEXT NOT NULL UNIQUE, " +
            LOG_DATE_CREATED_COLUMN + " INTEGER NOT NULL UNIQUE, " +
            LOG_DATE_COMPLETE_COLUMN + " INTEGER UNIQUE);";
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

        //Create LOG_table
        db.execSQL(LOG_TABLE_CREATE_COMMAND);

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

        //Create VALUES_table
        db.execSQL(VALUES_TABLE_CREATE_COMMAND);
        //Add 1st row with default values
        ContentValues contentValues = new ContentValues();
        contentValues.put(VALUES_LIST_VERSION_COLUMN, "0");
        contentValues.put(VALUES_IS_ACTIVE_COLUMN, "0");
        db.insert(VALUES_TABLE_NAME, null, contentValues);

        //Create init list table for the cursor
        db.execSQL(LIST_INIT_TABLE_CREATE_COMMAND);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Db stays version 1, nothing to do here
    }


    /*Returns latest list version #*/
    public int getListsCount() {
        int listCount;
        Cursor cursor = db.query(VALUES_TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToFirst();
        listCount = cursor.getInt(cursor.getColumnIndexOrThrow(VALUES_LIST_VERSION_COLUMN));
        cursor.close();
        return listCount;
    }


    /*Update latest list version #*/
    private void setListsCount(int newCount) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(VALUES_LIST_VERSION_COLUMN, newCount);
        db.update(VALUES_TABLE_NAME, contentValues,
                ID_COLUMN + "=?", new String[]{"1"});
    }


    /*Returns latest list version active state*/
    public boolean getListActiveState() {
        int stateInt;
        Cursor cursor = db.query(VALUES_TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToFirst();
        stateInt = cursor.getInt(cursor.getColumnIndexOrThrow(VALUES_IS_ACTIVE_COLUMN));
        cursor.close();
        if (stateInt == 0) return false;
        else return true;
    }


    /*Sets latest list version active state*/
    private void setListActiveState(boolean newState) {
        ContentValues contentValues = new ContentValues();
        int stateInt;
        if (newState) stateInt = 1;
        else stateInt = 0;
        contentValues.put(VALUES_IS_ACTIVE_COLUMN, stateInt);
        db.update(VALUES_TABLE_NAME, contentValues,
                ID_COLUMN + "=?", new String[]{"1"});
    }


    /*Get currentListName*/
    public String getCurrentListTableName() {
        return LIST_TABLE_NAME_part_1 + getListsCount();
    }


    /*Creates new List table
    updates count of lists,
    sets it as active,
    updates LOG_table */
    void createListTable() {
        //Create int for new version
        int newVersion = getListsCount() + 1;

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

        //Set new version of latest list table
        setListsCount(newVersion);

        //Set latest list table to active state
        setListActiveState(true);

        //Update LOG_table
        //Create contentValues var to store values of new list record of the LOG_TABLE
        ContentValues contentValues = new ContentValues();
        //Put name value of new list table
        contentValues.put(NAME_COLUMN, LIST_TABLE_NAME_part_1 + newVersion);
        //Put creation date of new list table in ms
        contentValues.put(LOG_DATE_CREATED_COLUMN, System.currentTimeMillis());
        //Update LOG_TABLE with new list record
        db.insert(LOG_TABLE_NAME, null, contentValues);//add new record to LOG_table
    }


    /*Deletes List table
    updates count of lists,
    deactivates active field in values,
    unchecks all items in Item table
    updates LOG_table */
    public String deleteListTable() {
        //Get current list version
        int currentVersion = getListsCount();

        String currentListTableName = LIST_TABLE_NAME_part_1 + currentVersion;

        //Should be at least 1 to delete
        if (currentVersion > 0) {

            //Create string for DROP TABLE command
            String LIST_TABLE_DROP_COMMAND = "DROP TABLE " + currentListTableName + ";";

            //Drop current table
            db.execSQL(LIST_TABLE_DROP_COMMAND);

            //Delete current list from LOG_table
            db.delete(LOG_TABLE_NAME, NAME_COLUMN + "=?",
                    new String[]{currentListTableName});

            //Set new version of latest list table
            setListsCount(currentVersion - 1);

            //Set latest list table to active state
            setListActiveState(false);

            //Uncheck all items in Item table
            //Create contentValues var
            ContentValues contentValues = new ContentValues();
            //Put new value to it
            contentValues.put(CHECKED_COLUMN, 0);
            //Update table
            db.update(ITEMS_TABLE_NAME, contentValues,
                    CHECKED_COLUMN + "=?",
                    new String[]{"1"});

            //Update cursor of ItemsFragment
            refreshItemsCursor();

            //Return success message
            return currentListTableName + "deleted";

        } else {
            //Return failure message
            return "No list to delete";
        }

    }


    /*Mark current list as complete:
    * Set status to inactive
    * Uncheck all checked in Items_table
    * Check all in list_?
    * Update log table
    * update all
    * */
    public boolean approveCurrentList() {

        //Check status of current list
        if (getListActiveState()) {
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

            //Check all items in List_? table
            //Create contentValues var
            ContentValues contentValuesList = new ContentValues();
            //Put new value to it
            contentValuesList.put(CHECKED_COLUMN, 1);
            //Update table
            db.update(getCurrentListTableName(), contentValuesList,
                    CHECKED_COLUMN + "=?",
                    new String[]{"0"});

            //Update log table:
            //Create contentValuesLog
            ContentValues contentValuesLog = new ContentValues();
            //Put there complete date of current list in ms
            contentValuesLog.put(LOG_DATE_COMPLETE_COLUMN, System.currentTimeMillis());
            //update table
            db.update(LOG_TABLE_NAME, contentValuesLog,
                    NAME_COLUMN + "=?",
                    new String[]{getCurrentListTableName()});

            //mark list as inactive
            setListActiveState(false);


            //Update cursors
            ListFragment.refreshListCursor();
            LogFragment.refreshLogCursor();
            refreshItemsCursor();

        }

        return true;
    }


    /*Deletes all items and lists:
    * Drop items table
    * Create items table
    * Refresh items adapter
    * Drop log table
    * Create log table
    * Delete all lists, except list_0
    * Set list active to false
    * Set current list to 0
    * Refresh list adapter
    *
    * */
    public void deleteAllItemsAndLists() {

        //Drop items table
        db.execSQL(ITEMS_TABLE_DROP_COMMAND);
        //Create items table
        db.execSQL(ITEMS_TABLE_CREATE_COMMAND);
        //Refresh items adapter
        ItemsFragment.refreshItemsCursor();

        //Drop log table
        db.execSQL(LOG_TABLE_DROP_COMMAND);
        //Create log table
        db.execSQL(LOG_TABLE_CREATE_COMMAND);


        //Delete all lists, except list_0
        //Get current number of lists
        int count = getListsCount();
        //Delete all
        for (int i = 1; i <= count; i++) {
            db.execSQL("DROP TABLE " + "List_" + i + ";");
        }

        //Set current list to 0
        setListsCount(0);
        //Set list active to false
        setListActiveState(false);
        //Refresh list adapter
        ListFragment.refreshListCursor();


    }
}
