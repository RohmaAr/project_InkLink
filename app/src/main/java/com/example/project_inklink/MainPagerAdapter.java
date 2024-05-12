package com.example.project_inklink;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MainPagerAdapter extends FragmentStateAdapter {

    private Bundle bundle;
    public void setBundle(Bundle bundle)
    {
        this.bundle=bundle;
    }
    public MainPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position)
        {
            case 0:
            {
                HomeFrag frag=new HomeFrag();
                frag.setBundle(bundle);
                return frag;

            }
            case 1:
            {
                SearchFrag frag=new SearchFrag();
                frag.setBundle(bundle);
                return frag;
            }
            case 2:
            {

                LibraryFrag frag=new LibraryFrag();
                frag.setBundle(bundle);
                return frag;
            }
            default:
            {
                ProfileFrag frag=new ProfileFrag();
                frag.setBundle(bundle);
                return frag;
            }
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
