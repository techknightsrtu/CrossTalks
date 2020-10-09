package com.techknightsrtu.crosstalks.activity.chat.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.techknightsrtu.crosstalks.activity.chat.ChatListFragment;
import com.techknightsrtu.crosstalks.activity.chat.UsersListFragment;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {


    public MyFragmentPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){

            case 0 : return ChatListFragment.newInstance();
            case 1 : return UsersListFragment.newInstance();

        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
