package com.example.glowguide.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.glowguide.fragments.ConsultationStatusFragment;

public class ConsultationPagerAdapter extends FragmentStateAdapter {

    public ConsultationPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        String status;
        switch (position) {
            case 0:
                status = "UPCOMING";
                break;
            case 1:
                status = "COMPLETED";
                break;
            case 2:
            default:
                status = "CANCELLED";
                break;
        }
        return ConsultationStatusFragment.newInstance(status);
    }

    @Override
    public int getItemCount() {
        return 3; // Number of tabs
    }
}
