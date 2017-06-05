package com.example.android.groceries2;

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

/**
 * Created by takeoff on 002 02 Jun 17.
 */

public class ListFragment extends Fragment {


    View listView;

    public ListFragment() {

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        listView = inflater.inflate(R.layout.tab_list, container, false);


        FloatingActionButton fabComplete = (FloatingActionButton) listView.findViewById(R.id.fab_complete_list);

        fabComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            case R.id.settings_option_1:
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.settings_option_2:

                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
