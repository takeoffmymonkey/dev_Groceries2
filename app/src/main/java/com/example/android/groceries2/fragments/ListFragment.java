package com.example.android.groceries2.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.groceries2.R;
import com.example.android.groceries2.activities.MainActivity;
import com.example.android.groceries2.adapters.ListCursorAdapter;

import static com.example.android.groceries2.activities.MainActivity.db;
import static com.example.android.groceries2.activities.MainActivity.dbHelper;
import static com.example.android.groceries2.db.GroceriesDbHelper.LIST_AMOUNT_COLUMN;
import static com.example.android.groceries2.db.GroceriesDbHelper.LIST_ITEM_COLUMN;
import static com.example.android.groceries2.db.GroceriesDbHelper.PRICE_COLUMN;


/**
 * Created by takeoff on 002 02 Jun 17.
 */

public class ListFragment extends Fragment {

    View listView;

    static ListCursorAdapter listCursorAdapter;

    public static TextView listTotalTextView;

    public static FloatingActionButton fabCompleteList;

    public static FloatingActionButton fabSendList;

    public ListFragment() {

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        listView = inflater.inflate(R.layout.tab_list, container, false);

        //int with active version
        int activeListVersion = dbHelper.getActiveListVersion();

        //Total text view
        listTotalTextView = (TextView) listView.findViewById(R.id.list_total);
        //Get total value
        float total = dbHelper.getTotal(activeListVersion);
        //Set text
        listTotalTextView.setText("Total: " + MainActivity.formatPrice(total));


        TextView listEmptyText = (TextView) listView.findViewById(R.id.list_empty_text);
        listEmptyText.setText("No selected items");


        TextView listEmptyTextSub = (TextView) listView.findViewById(R.id.list_empty_text_sub);
        listEmptyTextSub.setText("Please form a list in ITEMS");

        fabCompleteList =
                (FloatingActionButton) listView.findViewById(R.id.fab_complete_list);

        //Set approve list action to fab
        fabCompleteList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.approveCurrentList();
                ListFragment.refreshListCursor(null, null, 0);
                LogFragment.refreshLogCursor(null, null, 0);
                ItemsFragment.refreshItemsCursor(null, null, 0);
                //Inform user
                Toast.makeText(listView.getContext(), "List marked as complete", Toast.LENGTH_SHORT).show();

                MainActivity.selectTab(2);
            }
        });


        fabSendList =
                (FloatingActionButton) listView.findViewById(R.id.fab_send_list);

        //Set approve list action to fab
        fabSendList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Cursor cursorActiveList = db.query(dbHelper.getActiveListName(), null, null,
                        null, null, null, null);

                int rows = cursorActiveList.getCount();
                int lines = rows + 2;

                if (rows > 0) {

                    cursorActiveList.moveToFirst();

                    StringBuilder sb = new StringBuilder();

                    float total = 0f;

                    for (int i = 0; i < rows; i++) {


                        int name = cursorActiveList.getInt(cursorActiveList
                                .getColumnIndex(LIST_ITEM_COLUMN));

                        float amount = cursorActiveList.getFloat(cursorActiveList
                                .getColumnIndex(LIST_AMOUNT_COLUMN));


                        String amountString;

                        if (amount == Math.round(amount)) {
                            amountString = Integer.toString(Math.round(amount));
                        } else {
                            amountString = Float.toString(amount);
                        }

                        float price = cursorActiveList.getFloat(cursorActiveList
                                .getColumnIndex(PRICE_COLUMN));

                        total += price;

                        sb.append(name + " (" + amountString + ")" + " = " +
                                MainActivity.formatPrice(price) + "\n");

                        cursorActiveList.moveToNext();
                    }

                    sb.append("= = = =" + "\n");
                    sb.append("Total: " + MainActivity.formatPrice(total));

                    String sendMessage = sb.toString();

                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                    sendIntent.setType("text/plain");
                    sendIntent.putExtra(Intent.EXTRA_TEXT, sendMessage);
                    sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Groceries app: You've got new list!");
                    startActivity(sendIntent);
                }


            }
        });


        // Find the ListView which will be populated with the pet data
        ListView listListView = (ListView) listView.findViewById(R.id.list_list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.

        View emptyView = listView.findViewById(R.id.list_empty_view);

        listListView.setEmptyView(emptyView);

        Cursor cursor = db.query("List_" + activeListVersion, null,
                null, null, null, null, null);

        if (cursor.getCount() == 0)

        {
            fabCompleteList.setVisibility(View.GONE);
            fabSendList.setVisibility(View.GONE);
            listTotalTextView.setVisibility(View.GONE);
        } else

        {
            fabCompleteList.setVisibility(View.VISIBLE);
            fabSendList.setVisibility(View.VISIBLE);
            listTotalTextView.setVisibility(View.VISIBLE);
        }

        listCursorAdapter = new

                ListCursorAdapter(getContext(), cursor, 0);

        listListView.setAdapter(listCursorAdapter);

        setHasOptionsMenu(true);

        return listView;

    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.settings_list_mark_as_complete:
                dbHelper.approveCurrentList();
                ListFragment.refreshListCursor(null, null, 0);
                LogFragment.refreshLogCursor(null, null, 0);
                ItemsFragment.refreshItemsCursor(null, null, 0);
                //Inform user
                Toast.makeText(getContext(), "List marked as complete", Toast.LENGTH_SHORT).show();

                MainActivity.selectTab(2);

                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.settings_list_delete_list:
                dbHelper.deleteListTable(dbHelper.getActiveListVersion());
                ListFragment.refreshListCursor(null, null, 0);
                LogFragment.refreshLogCursor(null, null, 0);
                Toast.makeText(getContext(), "List deleted", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public static void refreshListCursor(@Nullable Context context,
                                         @Nullable String toast, @Nullable final int length) {

        class NewListCursor extends AsyncTask<Integer, Void, Cursor> {

            Context context;
            String toast;
            int length;

            public NewListCursor() {
                super();
            }


            public NewListCursor(Context context, String toast, int length) {
                super();
                this.context = context;
                this.toast = toast;
                this.length = length;
            }

            //Actions to perform in main thread before background execusion
            @Override
            protected void onPreExecute() {
            }

            //Actions to perform on background thread
            @Override
            protected Cursor doInBackground(Integer... params) {
                Cursor cursor = db.query(dbHelper.getActiveListName(), null, null, null, null, null, null);
                return cursor;
            }

            @Override
            protected void onPostExecute(Cursor cursor) {
                if (cursor.getCount() == 0) {
                    fabCompleteList.setVisibility(View.GONE);
                    fabSendList.setVisibility(View.GONE);
                    listTotalTextView.setVisibility(View.GONE);
                } else {
                    fabCompleteList.setVisibility(View.VISIBLE);
                    fabSendList.setVisibility(View.VISIBLE);
                    listTotalTextView.setVisibility(View.VISIBLE);
                }

                listCursorAdapter.changeCursor(cursor);

                if (toast != null) {
                    Toast.makeText(context, toast, length).show();
                }
            }
        }

        new NewListCursor(context, toast, length).execute(0);

    }

}
