package com.aasolution.cleantrucksys;

import androidx.fragment.app.Fragment;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ProcessFragment extends Fragment {
    View mView;
    Button processA, processB;
    RelativeLayout homeButton, backButton;

    MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        super.onCreate(savedInstanceState);
        mView = inflater.inflate(R.layout.fragment_process, container, false);
        mainActivity = (MainActivity) getActivity();
        buttons();

        return mView;

    }

    private void buttons(){
        processA = mView.findViewById(R.id.processA);
        processB = mView.findViewById(R.id.processB);
        homeButton = mView.findViewById(R.id.homeButton);
        backButton = mView.findViewById(R.id.backButton);


        processA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.openFragment(new ProcessA());
            }
        });

        processB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.openFragment(new ProcessB());
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
                mainActivity.openFragment(new HomeFragment());
            }
        });
    }

}