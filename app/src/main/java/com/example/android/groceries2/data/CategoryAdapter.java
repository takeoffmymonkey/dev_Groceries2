package com.example.android.groceries2.data;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.android.groceries2.ItemsFragment;
import com.example.android.groceries2.ListFragment;
import com.example.android.groceries2.LogFragment;


/**
 * Created by takeoff on 002 02 Jun 17.
 */

public class CategoryAdapter extends FragmentPagerAdapter {


    public CategoryAdapter(FragmentManager fm) {
        super(fm);
    }

    //Pages' mapping
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new ItemsFragment();
        } else if (position == 1) {
            return new ListFragment();
        } else {
            return new LogFragment();
        }
    }


    //Number of pages
    @Override
    public int getCount() {
        return 3;
    }

    //Pages' titles
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "Items";
        } else if (position == 1) {
            return "List";
        } else {
            return "Log";
        }
    }
}
