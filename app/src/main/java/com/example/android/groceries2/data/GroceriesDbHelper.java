package com.example.android.groceries2.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.android.groceries2.R;

import static android.R.attr.version;
import static com.example.android.groceries2.MainActivity.dbHelper;
import static java.security.AccessController.getContext;

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

    private boolean activeList = false;

    //Database name
    public static final String DB_NAME = "GROCERIES_db";
    //Database version
    public static final int DB_VERSION = 1;
    //id column for all tables
    static final String ID_COLUMN = "_id";
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
            MEASURE_COLUMN + " INTEGER NOT NULL DEFAULT 0, " +
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
    private static final String LOG_TABLE_CREATE_COMMAND = "CREATE TABLE " + LOG_TABLE_NAME + " (" +
            ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NAME_COLUMN + " TEXT NOT NULL UNIQUE, " +
            LOG_DATE_CREATED_COLUMN + " INTEGER NOT NULL UNIQUE, " +
            LOG_DATE_COMPLETE_COLUMN + " INTEGER UNIQUE);";


    /*LIST table*/
    //List table name 1st part
    private static final String LIST_TABLE_NAME_part_1 = "List_";
    //List table item column
    static final String LIST_ITEM_COLUMN = "item";
    //List table amount column
    static final String LIST_AMOUNT_COLUMN = "amount";


    /**
     * Reqired implementation of a constructor.
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @param context to use to open or create the database
     * @param name    of the database file, or null for an in-memory database
     * @param factory to use for creating cursor objects, or null for the default
     * @param version number of the database (starting at 1); if the database is older,
     *                {@link #onUpgrade} will be used to upgrade the database; if the database is
     *                newer, {@link #onDowngrade} will be used to downgrade the database
     */
    public GroceriesDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                             int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    /**
     * Requred implementation of an abstract method.
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(ITEMS_TABLE_CREATE_COMMAND);
        db.execSQL(LOG_TABLE_CREATE_COMMAND);

        db.execSQL(MEASURE_TABLE_CREATE_COMMAND);
        //Get the array with measurement values
        String[] measures = context.getResources().getStringArray(R.array.array_measurement_options);

        //Insert measurement values to MEASURE_table
        for (String i : measures) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MEASURE_COLUMN, i);
            db.insert(MEASURE_TABLE_NAME, null, contentValues);
        }

        db.execSQL(VALUES_TABLE_CREATE_COMMAND);
        //Add 1st row with default values
        ContentValues contentValues = new ContentValues();
        contentValues.put(VALUES_LIST_VERSION_COLUMN, "0");
        contentValues.put(VALUES_IS_ACTIVE_COLUMN, "0");
        db.insert(VALUES_TABLE_NAME, null, contentValues);
    }

    /**
     * Requred implementation of an abstract method.
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Db stays version 1, nothing to do here
    }

    /*Returns latest list version #*/
    public int getListsCount(SQLiteDatabase db) {
        int listCount;
        Cursor cursor = db.query(VALUES_TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToFirst();
        listCount = cursor.getInt(cursor.getColumnIndexOrThrow(VALUES_LIST_VERSION_COLUMN));
        cursor.close();
        return listCount;
    }

    /*Update latest list version #*/
    private void setListsCount(SQLiteDatabase db, int newCount) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(VALUES_LIST_VERSION_COLUMN, newCount);
        db.update(VALUES_TABLE_NAME, contentValues,
                ID_COLUMN + "=?", new String[]{"1"});
    }

    /*Returns latest list version active state*/
    boolean getListActiveState(SQLiteDatabase db) {
        int stateInt;
        Cursor cursor = db.query(VALUES_TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToFirst();
        stateInt = cursor.getInt(cursor.getColumnIndexOrThrow(VALUES_IS_ACTIVE_COLUMN));
        cursor.close();
        if (stateInt == 0) return false;
        else return true;
    }

    /*Sets latest list version active state*/
    private void setListActiveState(SQLiteDatabase db, boolean newState) {
        ContentValues contentValues = new ContentValues();
        int stateInt;
        if (newState) stateInt = 1;
        else stateInt = 0;
        contentValues.put(VALUES_IS_ACTIVE_COLUMN, stateInt);
        db.update(VALUES_TABLE_NAME, contentValues,
                ID_COLUMN + "=?", new String[]{"1"});
    }

    /*Get currentListName*/
    public String getCurrentListTableName(SQLiteDatabase db) {
        return LIST_TABLE_NAME_part_1 + getListsCount(db);
    }


    /*Creates new List table
    updates count of lists,
    sets it as active,
    updates LOG_table */
    void createListTable(SQLiteDatabase db) {
        //Create int for new version
        int newVersion = getListsCount(db) + 1;

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
        setListsCount(db, newVersion);

        //Set latest list table to active state
        setListActiveState(db, true);

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
    public String deleteListTable(SQLiteDatabase db) {
        //Get current list version
        int currentVersion = getListsCount(db);

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
            setListsCount(db, currentVersion - 1);

            //Set latest list table to active state
            setListActiveState(db, false);

            //Uncheck all items in Item table
            //Create contentValues var
            ContentValues contentValues = new ContentValues();
            //Put new value to it
            contentValues.put(CHECKED_COLUMN, 0);
            //Update table
            // TODO: 016 16 Jun 17 what if none is checked?
            db.update(ITEMS_TABLE_NAME, contentValues,
                    CHECKED_COLUMN + "=?",
                    new String[]{"1"});

            //Return success message
            return currentListTableName + "deleted";

        } else {
            //Return failure message
            return "No list to delete";
        }


    }

}
