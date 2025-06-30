package com.example.glowguide.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.glowguide.fragments.ConsultationTabFragment;
import com.example.glowguide.fragments.OrderTabFragment;

public class TrackPagerAdapter extends FragmentStateAdapter {

    public TrackPagerAdapter(@NonNull FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0)
            return new OrderTabFragment();  // You need to create this
        else
            return new ConsultationTabFragment();  // And this
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
