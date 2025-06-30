package com.example.glowguide.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.glowguide.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class OrderTabFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    public OrderTabFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_tab, container, false);

        tabLayout = view.findViewById(R.id.orderTabLayout);
        viewPager = view.findViewById(R.id.orderViewPager);

        viewPager.setAdapter(new com.example.glowguide.fragments.OrderPagerAdapter(this));

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("ALL"); break;
                case 1: tab.setText("TO SHIP"); break;
                case 2: tab.setText("SHIPPED"); break;
                case 3: tab.setText("TO PICKUP"); break;
                case 4: tab.setText("CANCEL"); break;
            }
        }).attach();

        return view;
    }
}
