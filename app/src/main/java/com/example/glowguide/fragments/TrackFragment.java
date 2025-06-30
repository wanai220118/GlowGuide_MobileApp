package com.example.glowguide.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.glowguide.R;
import com.example.glowguide.adapters.TrackPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TrackFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    public TrackFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_track, container, false);
        tabLayout = view.findViewById(R.id.tabLayoutMain);
        viewPager = view.findViewById(R.id.viewPagerMain);

        viewPager.setAdapter(new TrackPagerAdapter((FragmentActivity) requireActivity()));

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) tab.setText("ORDER");
            else tab.setText("CONSULTATION");
        }).attach();

        return view;
    }
}
