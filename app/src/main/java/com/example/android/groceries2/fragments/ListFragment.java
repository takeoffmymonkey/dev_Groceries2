package com.example.android.groceries2.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import static com.example.android.groceries2.activities.MainActivity.showSnackBar;
import static com.example.android.groceries2.activities.MainActivity.snackLines;


/**
 * Created by takeoff on 002 02 Jun 17.
 */

public class ListFragment extends Fragment {

    View listView;

    static ListCursorAdapter listCursorAdapter;

    public static TextView listTotalTextView;

    public static FloatingActionButton fabCompleteList;

    public static FloatingActionButton fabSendList;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.w("WARNING: ", "IN ONATTACH OF LIST FRAGMENT");
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w("WARNING: ", "IN ONCREATE OF LIST FRAGMENT");
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.w("WARNING: ", "IN ONACTIVITYCREATE OF LIST FRAGMENT");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.w("WARNING: ", "IN ONSTART OF LIST FRAGMENT");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.w("WARNING: ", "IN ONRESUME OF LIST FRAGMENT");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.w("WARNING: ", "IN ONPAUSE OF LIST FRAGMENT");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.w("WARNING: ", "IN ONSTOP OF LIST FRAGMENT");
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.w("WARNING: ", "IN ONDESTROYVIEW OF LIST FRAGMENT");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.w("WARNING: ", "IN ONDETACH OF LIST FRAGMENT");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.w("WARNING: ", "IN ONDESTROY OF LIST FRAGMENT");
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Log.w("WARNING: ", "IN ONCREATEVIEW OF LIST FRAGMENT");

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

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, MainActivity.getActiveListAsString());
                intent.putExtra(Intent.EXTRA_SUBJECT, "Groceries app: You've got new list!");

                //For ability to always choose intent receiver
                Intent sendIntent = Intent.createChooser(intent, "Send list");

                startActivity(sendIntent);
            }

        });


        // Find the ListView which will be populated with the pet data
        ListView listListView = (ListView) listView.findViewById(R.id.list_list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.

        View emptyView = listView.findViewById(R.id.list_empty_view);

        listListView.setEmptyView(emptyView);

        Cursor cursor = db.query("LIST_" + activeListVersion, null,
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

        listCursorAdapter = new ListCursorAdapter(getContext(), cursor, 0);

        listListView.setAdapter(listCursorAdapter);

        View footerView = ((LayoutInflater) getContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_layout, null, false);
        listListView.addFooterView(footerView);

        listTotalTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String snackText = MainActivity.getActiveListAsString();

                if (snackLines > 0)
                    showSnackBar(listView, snackText);

            }

        });


        setHasOptionsMenu(true);

        return listView;

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

            case R.id.settings_list_share_list:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, MainActivity.getActiveListAsString());
                intent.putExtra(Intent.EXTRA_SUBJECT, "Groceries app: You've got new list!");

                //For ability to always choose intent receiver
                Intent sendIntent = Intent.createChooser(intent, "Send list");

                startActivity(sendIntent);

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
