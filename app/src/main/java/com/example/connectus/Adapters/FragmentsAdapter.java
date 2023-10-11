package com.example.connectus.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.connectus.Fragments.CallsFragment;
import com.example.connectus.Fragments.ChatsFragment;
import com.example.connectus.Fragments.PeopleFragment;
import com.example.connectus.Fragments.StoryFragment;

public class FragmentsAdapter extends FragmentPagerAdapter {
    public FragmentsAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }



    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0 : return new ChatsFragment();
            case 1 : return new CallsFragment();
            case 2 : return new PeopleFragment();
            case 3 : return new StoryFragment();
            default: return  new ChatsFragment();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title =null;
        if(position==0){
            title="Chats";

        }
        if(position==1){
            title="Calls";
        }
        if(position==2){
            title="People";
        }
        if(position==3){
            title="stories";
        }
        return super.getPageTitle(position);
    }
}
