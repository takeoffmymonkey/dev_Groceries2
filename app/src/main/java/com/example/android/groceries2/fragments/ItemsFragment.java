package com.example.android.groceries2.fragments;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.groceries2.R;
import com.example.android.groceries2.activities.EditorActivity;
import com.example.android.groceries2.activities.MainActivity;
import com.example.android.groceries2.adapters.ItemsCursorAdapter;

import static com.example.android.groceries2.activities.MainActivity.db;
import static com.example.android.groceries2.activities.MainActivity.dbHelper;
import static com.example.android.groceries2.db.GroceriesDbHelper.ID_COLUMN;
import static com.example.android.groceries2.db.GroceriesDbHelper.ITEMS_TABLE_NAME;
import static com.example.android.groceries2.db.GroceriesDbHelper.MEASURE_COLUMN;
import static com.example.android.groceries2.db.GroceriesDbHelper.NAME_COLUMN;
import static com.example.android.groceries2.db.GroceriesDbHelper.PRICE_COLUMN;
import static com.example.android.groceries2.fragments.ListFragment.fabCompleteList;
import static com.example.android.groceries2.fragments.ListFragment.fabSendList;
import static com.example.android.groceries2.fragments.ListFragment.listTotalTextView;


/**
 * Created by takeoff on 002 02 Jun 17.
 */

public class ItemsFragment extends Fragment {

    //Create ItemsCursorAdapter link
    static ItemsCursorAdapter itemsCursorAdapter;

    public static ProgressBar progressBar;

    public static TextView itemsTotalTextView;

    public static FloatingActionButton fabAddItem;

    public String snackText;

    public int lines;

    GridView itemsGridView;

    public static boolean snackOn = false;

    public static Snackbar snackBar;

    public View itemsView;

    //Required empty constructor
    public ItemsFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        if (savedInstanceState != null) {

            snackText = savedInstanceState.getString("snackText");

            snackOn = savedInstanceState.getBoolean("snackOn");

            lines = savedInstanceState.getInt("lines");
        }


        //Create the view object and inflate in with tab_items layout
        itemsView = inflater.inflate(R.layout.tab_items, container, false);


        //int with active version
        int activeListVersion = dbHelper.getActiveListVersion();

        //Total text view
        itemsTotalTextView = (TextView) itemsView.findViewById(R.id.items_total);
        //Get total value
        float total = dbHelper.getTotal(activeListVersion);
        //Set text
        itemsTotalTextView.setText("Total: " + MainActivity.formatPrice(total));


        progressBar = (ProgressBar) itemsView.findViewById(R.id.items_progress_bar);
        progressBar.setVisibility(View.GONE);


        TextView itemsEmptyText = (TextView) itemsView.findViewById(R.id.items_empty_text);
        itemsEmptyText.setText("No items added");

        TextView itemsEmptyTextSub = (TextView) itemsView.findViewById(R.id.items_empty_text_sub);
        itemsEmptyTextSub.setText("Please add an item");

        //Create floating action button for adding 1 item
        fabAddItem =
                (FloatingActionButton) itemsView.findViewById(R.id.fab_add_item_to_db);
        //Set click listener on it
        fabAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create object for intent
                Intent intent = new Intent(getActivity(), EditorActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                //Create editor activity
                startActivity(intent);
            }
        });

        //Find the gridView to hold items
        itemsGridView = (GridView) itemsView.findViewById(R.id.items_list);

        //Find empty view when nothing to show
        View emptyView = itemsView.findViewById(R.id.items_empty_view);
        //Set it to gridView
        itemsGridView.setEmptyView(emptyView);

        Cursor cursor = db.query(ITEMS_TABLE_NAME, null, null, null, null, null, null);
        if (cursor.getCount() == 0) {
            itemsTotalTextView.setVisibility(View.GONE);
        } else {
            itemsTotalTextView.setVisibility(View.VISIBLE);
            fabAddItem.setVisibility(View.VISIBLE);
        }
        //Create cursor adapter object and pass cursor there
        itemsCursorAdapter = new ItemsCursorAdapter(getContext(), cursor, 0);

        itemsGridView.setAdapter(itemsCursorAdapter);


        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            itemsGridView.setNumColumns(3);
        } else {
            itemsGridView.setNumColumns(5);
        }


        itemsTotalTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Cursor cursorActiveList = db.query(dbHelper.getActiveListName(), null, null,
                        null, null, null, null);

                int rows = cursorActiveList.getCount();
                lines = rows + 2;

                cursorActiveList.close();

                snackText = MainActivity.getActiveListAsString();

                showSnackBar(lines, snackText);

            }

        });


        if (snackOn) {
            Log.e("WARNING: ", "NEED TO SHOW SNACK");
            showSnackBar(lines, snackText);
        }

        //This fragment has options menu
        setHasOptionsMenu(true);

        //Return fragment's view
        return itemsView;

    }


    //Set menu layout
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_items, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    //When option from menu is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.settings_items_populate_list:

                progressBar.setVisibility(View.VISIBLE);

                new ItemsBackgroundTasks(getContext(), "New items added").execute(0);

                return true;


            case R.id.settings_items_add_item:
                Intent intent = new Intent(getActivity(), EditorActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                return true;

            // Respond to a click on the "Delete all entries" menu option
            case R.id.settings_items_delete_all_items:

                if (ItemsFragment.snackOn && ItemsFragment.snackBar != null) {
                    ItemsFragment.snackBar.dismiss();
                    ItemsFragment.setSnackOnState(false, null);
                }

                //Check if there are items to delete
                //Create a cursor and ask for ID 1
                Cursor cursorCheckItemsTable = db.query(ITEMS_TABLE_NAME,
                        new String[]{ID_COLUMN},
                        ID_COLUMN + "=?", new String[]{Integer.toString(1)},
                        null, null, null);

                if (cursorCheckItemsTable.getCount() > 0) {
                    //table has at least 1 item
                    cursorCheckItemsTable.close();

                    //Create alert dialog object
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    //Set title of the dialog
                    builder.setTitle("WARNING!")
                            //Set custom view of the dialog
                            .setMessage("This will delete all lists as well!")
                            //Set ability to press back
                            .setCancelable(true)
                            //Set Ok button with click listener
                            .setPositiveButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            progressBar.setVisibility(View.VISIBLE);
                                            new ItemsBackgroundTasks(getContext(), "All items successfully deleted!")
                                                    .execute(1);
                                            //Close the dialog window
                                            dialog.cancel();

                                        }
                                    })

                            //Set cancel button with click listener
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //Close the dialog window
                                            dialog.cancel();
                                        }
                                    });

                    AlertDialog alert = builder.create();
                    alert.show();


                } else {
                    //close cursor
                    cursorCheckItemsTable.close();
                    //Inform user
                    Toast.makeText(getContext(), "No items to delete!", Toast.LENGTH_LONG).show();
                }


                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    class ItemsBackgroundTasks extends AsyncTask<Integer, Void, Boolean> {

        Context context;

        String toast;

        public ItemsBackgroundTasks() {
            super();
        }

        public ItemsBackgroundTasks(Context context, String toast) {
            super();

            this.context = context;
            this.toast = toast;
        }

        //Actions to perform on background thread
        @Override
        protected Boolean doInBackground(Integer... params) {
            switch (params[0]) {

                case 0: // Populate list
                    //Get array of items' names from resources
                    String[] names = getResources().getStringArray(R.array.array_auto_name_list);
                    //Get array of items' measures from resources
                    int[] measures = getResources().getIntArray(R.array.array_auto_measure_list);
                    //Create array of items' prices
                    String[] prices = getResources().getStringArray(R.array.array_auto_price_list);

                    for (int i = 0; i < prices.length; i++) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(NAME_COLUMN, names[i]);
                        contentValues.put(PRICE_COLUMN, Float.parseFloat(prices[i]));
                        contentValues.put(MEASURE_COLUMN, measures[i] + 1);
                        db.insert(ITEMS_TABLE_NAME, null, contentValues);
                    }


                    refreshItemsCursor(null, null, 0);

                    break;

                case 1: // Delete list
                    dbHelper.deleteAll(1);
                    ItemsFragment.refreshItemsCursor(null, null, 0);
                    ListFragment.refreshListCursor(null, null, 0);
                    break;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            itemsTotalTextView.setText("Total: " + MainActivity.formatPrice(0.00f));

            progressBar.setVisibility(View.GONE);

            if (toast != null) {
                Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
            }


        }

    }


    /*Refresh cursor using inner class*/
    public static void refreshItemsCursor(@Nullable Context context,
                                          @Nullable String toast, @Nullable final int length) {


        class NewItemsCursor extends AsyncTask<Integer, Void, Cursor> {

            Context context;
            String toast;
            int length;

            public NewItemsCursor() {
                super();
            }


            public NewItemsCursor(Context context, String toast, int length) {
                super();
                this.context = context;
                this.toast = toast;
                this.length = length;
            }


            //Actions to perform on background thread
            @Override
            protected Cursor doInBackground(Integer... params) {
                Cursor cursor = db.query(ITEMS_TABLE_NAME, null, null, null, null, null, null);
                return cursor;

            }

            @Override
            protected void onPostExecute(Cursor cursor) {
                if (cursor.getCount() == 0) {

                    itemsTotalTextView.setVisibility(View.GONE);

                    listTotalTextView.setVisibility(View.GONE);
                    fabCompleteList.setVisibility(View.GONE);
                    fabSendList.setVisibility(View.GONE);

                } else {

                    itemsTotalTextView.setVisibility(View.VISIBLE);

                }

                itemsCursorAdapter.changeCursor(cursor);


                if (toast != null) {

                    if (length == 2) {

                        final Toast toastObj = Toast.makeText(context, toast, Toast.LENGTH_SHORT);
                        toastObj.show();

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                toastObj.cancel();
                            }
                        }, 1000);

                    } else {
                        Toast.makeText(context, toast, length).show();
                    }


                }

            }
        }

        new NewItemsCursor(context, toast, length).execute(1);

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        itemsGridView.setNumColumns(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ? 3 : 2);
        super.onConfigurationChanged(newConfig);
    }


    public static void setSnackOnState(boolean state, Snackbar snackbar) {
        snackOn = state;
        snackBar = snackbar;
    }

    public boolean getSnackOnState() {
        return snackOn;
    }


    void showSnackBar(int lines, String snackText) {
        final Snackbar snackBar = Snackbar.make(itemsView,
                snackText,
                Snackbar.LENGTH_INDEFINITE);


        View snackbarView = snackBar.getView();
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setMaxLines(lines);

        //snackbarView.setBackgroundColor(MainActivity.primaryTextColor);
        snackbarView.setBackgroundColor(Color.DKGRAY);

        snackBar.setAction("Close", new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                snackBar.dismiss();
                setSnackOnState(false, null);

                Log.e("WARNING: ", "KILLING SNACK");
            }


        });

        snackBar.show();

        Log.e("WARNING: ", "SHOWING SNACK");

        setSnackOnState(true, snackBar);

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("snackText", snackText);
        outState.putBoolean("snackOn", getSnackOnState());
        outState.putInt("lines", lines);

    }


}
