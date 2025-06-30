package com.example.glowguide.fragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class OrderPagerAdapter extends FragmentStateAdapter {

    public OrderPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return OrderStatusFragment.newInstance("ALL"); // optional if you handle it
            case 1:
                return OrderStatusFragment.newInstance("TO SHIP");
            case 2:
                return OrderStatusFragment.newInstance("SHIPPED");
            case 3:
                return OrderStatusFragment.newInstance("TO PICKUP");
            case 4:
                return OrderStatusFragment.newInstance("CANCELLED");
            default:
                return new OrderStatusFragment(); // fallback
        }
    }

    @Override
    public int getItemCount() {
        return 5; // total number of tabs
    }
}