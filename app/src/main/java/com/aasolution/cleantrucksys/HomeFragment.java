package com.aasolution.cleantrucksysbeta;

import androidx.fragment.app.Fragment;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Objects;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class HomeFragment extends Fragment {
    View mView;
    Button manualButton, process;

    com.aasolution.cleantrucksysbeta.MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        super.onCreate(savedInstanceState);
        mView = inflater.inflate(R.layout.fragment_home, container, false);
        mainActivity = (com.aasolution.cleantrucksysbeta.MainActivity) getActivity();

        buttons();

        return mView;

    }

    private void buttons(){
        manualButton = mView.findViewById(R.id.manualButton);
        process = mView.findViewById(R.id.processButton);


        manualButton.setOnClickListener(v -> {
            if (!Objects.equals(mainActivity.getWifi(), "HMI2")){
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    startActivityForResult(new android.content.Intent(android.provider.Settings.Panel.ACTION_INTERNET_CONNECTIVITY), 0);
                } else {
                    startActivityForResult(new android.content.Intent(android.provider.Settings.ACTION_WIFI_SETTINGS), 0);
                }
            } else {
                openManualFragment();
            }
        });
        process.setOnClickListener(v -> {
            if (!Objects.equals(mainActivity.getWifi(), "HMI2")) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    startActivityForResult(new android.content.Intent(android.provider.Settings.Panel.ACTION_INTERNET_CONNECTIVITY), 0);
                } else {
                    startActivityForResult(new android.content.Intent(android.provider.Settings.ACTION_WIFI_SETTINGS), 0);
                }
            } else {
                // Show loading spinner dialog while opening ProcessFragment
                android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(getContext());
                progressDialog.setMessage("Đang tải...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                com.aasolution.cleantrucksysbeta.ProcessFragment fragment = new com.aasolution.cleantrucksysbeta.ProcessFragment();
                fragment.setProgressDialog(progressDialog);
                mainActivity.openFragment(fragment);
            }
        });
    }

    private void openManualFragment() {
        mainActivity.openFragment(new com.aasolution.cleantrucksysbeta.ManualFragment());
    }

    private void openProcessFragment() {
        mainActivity.openFragment(new com.aasolution.cleantrucksysbeta.ProcessFragment());
    }

}