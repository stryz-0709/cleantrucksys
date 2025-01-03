package com.aasolution.cleantrucksys;

import androidx.fragment.app.Fragment;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class HomeFragment extends Fragment {
    View mView;
    Button manualButton, process;

    MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        super.onCreate(savedInstanceState);
        mView = inflater.inflate(R.layout.fragment_home, container, false);
        mainActivity = (MainActivity) getActivity();

        buttons();

        return mView;

    }

    private void buttons(){
        manualButton = mView.findViewById(R.id.manualButton);
        process = mView.findViewById(R.id.processButton);


        manualButton.setOnClickListener(v -> openManualFragment());
        process.setOnClickListener(v -> openProcessFragment());
    }

    private void openManualFragment() {
        mainActivity.openFragment(new ManualFragment());
    }

    private void openProcessFragment() {
        mainActivity.openFragment(new ProcessFragment());
    }

}