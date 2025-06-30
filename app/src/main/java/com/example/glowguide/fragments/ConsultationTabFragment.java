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
import com.example.glowguide.adapters.ConsultationPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ConsultationTabFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_consultation_tab, container, false);

        ViewPager2 viewPager = view.findViewById(R.id.consultationViewPager);
        TabLayout tabLayout = view.findViewById(R.id.consultationTabLayout);

        viewPager.setAdapter(new ConsultationPagerAdapter(this));

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("UPCOMING");
                    break;
                case 1:
                    tab.setText("COMPLETED");
                    break;
                case 2:
                    tab.setText("CANCELLED");
                    break;
            }
        }).attach();

        return view;
    }
}
