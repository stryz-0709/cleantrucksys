package com.aasolution.cleantrucksys;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ProcessB extends Fragment {
    View mView;
    Button processB1, processB2;
    RelativeLayout homeButton, backButton;

    MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        mView = inflater.inflate(R.layout.fragment_process_b, container, false);
        mainActivity = (MainActivity) getActivity();
        buttons();

        return mView;

    }

    private void buttons(){
        processB1 = mView.findViewById(R.id.processB1);
        processB2 = mView.findViewById(R.id.processB2);
        homeButton = mView.findViewById(R.id.homeButton);
        backButton = mView.findViewById(R.id.backButton);


        processB1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.openFragment(new ProcessB1());
            }
        });

        processB2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.openFragment(new ProcessB2());
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.openFragment(new HomeFragment());
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.openFragment(new ProcessFragment());
            }
        });
    }

}