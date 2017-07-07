package com.example.android.groceries2.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.android.groceries2.fragments.ItemsFragment;
import com.example.android.groceries2.fragments.ListFragment;


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
        if (position == 0) return new ItemsFragment();
        else return new ListFragment();

    }


    //Number of pages
    @Override
    public int getCount() {
        return 2;
    }

    //Pages' titles
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) return "Items";
        else return "List";

    }


}
