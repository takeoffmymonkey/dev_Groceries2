package com.example.android.groceries2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

    //Database name
    public static final String DATABASE_NAME = "groceries_db";
    //Database version
    public static final int DBVERSION = 1;
    //Db table name
    public static final String TABLE_GROCERIES = "groceries";
    //Item's name
    public static final String ITEM_NAME = "name";
    //Item's price
    public static final String ITEM_PRICE = "price";
    //Item's weight
    //Item's measurement
    public static final String ITEM_MEASURE = "measure";
    //Item's check checkBoxState
    public static final String ITEM_CHECKED = "checked";


    public static final String GROCERIES_TABLE_CREATE = "CREATE TABLE " + TABLE_GROCERIES + " (" + "_id" +
            " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ITEM_NAME + " TEXT, " + ITEM_PRICE + " REAL, " +
            ITEM_MEASURE + " TEXT, " + ITEM_CHECKED + " INTEGER);";

    public static final String GROCERIES_TABLE_DROP = "DROP TABLE " + TABLE_GROCERIES + ";";

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

        db.execSQL(GROCERIES_TABLE_CREATE);
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
