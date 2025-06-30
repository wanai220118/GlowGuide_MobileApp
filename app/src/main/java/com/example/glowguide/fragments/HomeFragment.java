package com.example.glowguide.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
//import com.example.glowguide.NotificationActivity;
//import com.example.glowguide.QuizActivity;
import com.example.glowguide.R;
import com.example.glowguide.activities.NotificationActivity;
import com.example.glowguide.activities.QuizActivity;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // === Image Slider ===
        ImageSlider imageSlider = view.findViewById(R.id.image_slider);
        List<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.slider1, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.slider2, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.slider3, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.slider4, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.slider5, ScaleTypes.FIT));
        imageSlider.setImageList(slideModels);

        // === Quiz Button ===
        Button quizButton = view.findViewById(R.id.btnTakeQuiz);
        quizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), QuizActivity.class);
                startActivity(intent);
            }
        });

        // === Notification Bell ===
        ImageView notificationBell = view.findViewById(R.id.notificationBell);
        notificationBell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NotificationActivity.class);
                startActivity(intent);
            }
        });

        // === YouTube Skincare Tips ===
        ImageView video1 = view.findViewById(R.id.video1);
        ImageView video2 = view.findViewById(R.id.video2);
        ImageView video3 = view.findViewById(R.id.video3);
        ImageView video4 = view.findViewById(R.id.video4);
        ImageView video5 = view.findViewById(R.id.video5);


        video1.setOnClickListener(v -> openYoutube("https://www.youtube.com/watch?v=_WYRX02WjAw"));
        video2.setOnClickListener(v -> openYoutube("https://www.youtube.com/watch?v=ekdDFYFKRCs"));
        video3.setOnClickListener(v -> openYoutube("https://www.youtube.com/watch?v=vxxv-F7362E"));
        video4.setOnClickListener(v -> openYoutube("https://www.youtube.com/watch?v=AAveYddVE8s"));
        video5.setOnClickListener(v -> openYoutube("https://www.youtube.com/watch?v=4j9_9aDxIzI"));

        return view;
    }

    private void openYoutube(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setPackage("com.google.android.youtube"); // opens in YouTube app
        startActivity(intent);
    }
}
