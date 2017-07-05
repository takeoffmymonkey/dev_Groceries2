package com.example.android.groceries2.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.android.groceries2.App;

import static com.example.android.groceries2.fragments.ItemsFragment.progressBar;

/**
 * Created by takeoff on 005 05 Jul 17.
 */




/*
* 1 - delete all items
* 2 - select item
* */
public class ItemsDialogFragment extends DialogFragment {

    int mode;


    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
    }

    public static ItemsDialogFragment newInstance(int mode) {
        ItemsDialogFragment fragment = new ItemsDialogFragment();
        Bundle bundle = new Bundle(1);
        bundle.putInt("mode", mode);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mode = getArguments().getInt("mode");

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Context context = App.getContext();
        // Use the Builder class for convenient dialog construction

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if (mode == 1) {
            //need to delete all items

            builder.setMessage("Are you sure you want to delete ALL items?")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            progressBar.setVisibility(View.VISIBLE);
                            new ItemsFragment().new ItemsBackgroundTasks(context,
                                    "All items successfully deleted!")
                                    .execute(1);

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });


        } else if (mode == 2) {

        }


        return builder.create();

        // Create the AlertDialog object and return it

    }


    private EditText findInput(ViewGroup np) {
        int count = np.getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = np.getChildAt(i);
            if (child instanceof ViewGroup) {
                findInput((ViewGroup) child);
            } else if (child instanceof EditText) {
                return (EditText) child;
            }
        }
        return null;
    }

}