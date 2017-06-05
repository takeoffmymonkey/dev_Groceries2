package com.example.android.groceries2;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


/**
 * Created by takeoff on 002 02 Jun 17.
 */

public class CategoryAdapter extends FragmentPagerAdapter {


    public CategoryAdapter(FragmentManager fm) {
        super(fm);

    }


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


    //Return number of pages
    @Override
    public int getCount() {
        return 3;
    }


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
