package com.example.android.groceries2.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.groceries2.R;

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
    //name column for all tables
    public static final String NAME_COLUMN = "name";
    //checked state column
    public static final String CHECKED_COLUMN = "checked";


    /*ITEMS table*/
    public static final String ITEMS_TABLE_NAME = "ITEMS_table";
    //price column
    public static final String ITEMS_PRICE_COLUMN = "price";
    //measure column
    public static final String ITEMS_MEASURE_COLUMN = "measure";

    //table create command
    public static final String ITEMS_TABLE_CREATE_COMMAND = "CREATE TABLE " + ITEMS_TABLE_NAME + " (" +
            ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NAME_COLUMN + " TEXT NOT NULL UNIQUE, " +
            ITEMS_PRICE_COLUMN + " REAL NOT NULL DEFAULT 0, " +
            ITEMS_MEASURE_COLUMN + " INTEGER NOT NULL DEFAULT 0, " +
            CHECKED_COLUMN + " INTEGER);";
    //table drop command
    public static final String ITEMS_TABLE_DROP_COMMAND = "DROP TABLE " + ITEMS_TABLE_NAME + ";";


    /*MEASURE table*/
    public static final String MEASURE_TABLE_NAME = "MEASURE_table";
    //measure column
    public static final String MEASURE_MEASURE_COLUMN = "measure";
    //table create command
    public static final String MEASURE_TABLE_CREATE_COMMAND = "CREATE TABLE " + MEASURE_TABLE_NAME + " (" +
            ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MEASURE_MEASURE_COLUMN + " TEXT NOT NULL UNIQUE);";
    //table drop command
    public static final String MEASURE_TABLE_DROP_COMMAND = "DROP TABLE " + MEASURE_TABLE_NAME + ";";


    /*LOG table*/
    public static final String LOG_TABLE_NAME = "LOG_table";
    //creation date column
    public static final String LOG_DATE_CREATED_COLUMN = "created";
    //completion date column
    public static final String LOG_DATE_COMPLETE_COLUMN = "complete";
    //table create command
    public static final String LOG_TABLE_CREATE_COMMAND = "CREATE TABLE " + LOG_TABLE_NAME + " (" +
            ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NAME_COLUMN + " TEXT NOT NULL UNIQUE, " +
            LOG_DATE_CREATED_COLUMN + " TEXT NOT NULL UNIQUE, " +
            LOG_DATE_COMPLETE_COLUMN + " TEXT NOT NULL UNIQUE);";
    //table drop command
    public static final String LOG_TABLE_DROP_COMMAND = "DROP TABLE " + LOG_TABLE_NAME + ";";


    /*LIST table*/
    // TODO: 013 13 Jun 17 auto increment list table's name dynamically
    public static final String LIST_TABLE_NAME = "LIST_table";
    //item column
    public static final String LIST_ITEM_COLUMN = "item";
    //checked column
    public static final String LIST_AMOUNT_COLUMN = "amount";
    //table create command
    public static final String LIST_TABLE_CREATE_COMMAND = "CREATE TABLE " + LIST_TABLE_NAME + " (" +
            ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            LIST_ITEM_COLUMN + " INTEGER NOT NULL UNIQUE, " +
            LIST_AMOUNT_COLUMN + " REAL NOT NULL, " +
            CHECKED_COLUMN + " INTEGER);";
    //table drop command
    public static final String LIST_TABLE_DROP_COMMAND = "DROP TABLE " + LIST_TABLE_NAME + ";";


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
        db.execSQL(LIST_TABLE_CREATE_COMMAND);
        db.execSQL(MEASURE_TABLE_CREATE_COMMAND);

        //Get the array with measurement values
        String[] measures = context.getResources().getStringArray(R.array.array_measurement_options);

        //Insert measurement values to MEASURE_table
        for (int i = 0; i < measures.length; i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MEASURE_MEASURE_COLUMN, measures[i]);
            db.insert(MEASURE_TABLE_NAME, null, contentValues);
        }


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
}
