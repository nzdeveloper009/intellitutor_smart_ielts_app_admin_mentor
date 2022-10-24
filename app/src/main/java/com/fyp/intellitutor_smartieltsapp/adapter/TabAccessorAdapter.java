package com.fyp.intellitutor_smartieltsapp.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class TabAccessorAdapter extends FragmentPagerAdapter {

    private final ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    private final ArrayList<String> fragmentTitle = new ArrayList<>();


    public TabAccessorAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }


    // https://www.youtube.com/watch?v=3Am-iad_Gkg&list=PLxefhmF0pcPmtdoud8f64EpgapkclCllj&index=5
    // 8:50

    @NonNull
    @Override
    public Fragment getItem(int position) {

        return fragmentArrayList.get(position);
    }


    @Override
    public int getCount() {
        return fragmentArrayList.size();
    }

    public void addFragment(Fragment fragment,String title){
        fragmentArrayList.add(fragment);
        fragmentTitle.add(title);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        return fragmentTitle.get(position);
    }
}
