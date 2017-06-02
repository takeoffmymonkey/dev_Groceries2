package com.example.android.groceries2.data;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.android.groceries2.R;

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
        } else {
            return new ItemsFragment();
        }
    }


    //Return number of pages
    @Override
    public int getCount() {
        return 2;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "Items";
        } else {
            return "Items2";
        }
    }
}
