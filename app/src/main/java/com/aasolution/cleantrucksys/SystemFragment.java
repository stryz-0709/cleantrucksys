package com.aasolution.cleantrucksys;

import static android.view.View.GONE;

import androidx.fragment.app.Fragment;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SystemFragment extends Fragment {
    View mView;
    MainActivity mainActivity;
    RelativeLayout homeButton;
    ToggleButton sys2_light;

    ToggleButton valve2Light, valve3Light, valve4Light, valve6Light, valve8Light, valve9Light,
            valve11Light, vacuumLight, oilLight, waterLight;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        mView = inflater.inflate(R.layout.fragment_system, container, false);
        mainActivity = (MainActivity) getActivity();

        buttons();
        updateLights();

        return mView;

    }

    private void buttons(){
        valve2Light = mView.findViewById(R.id.valve2_light);
        valve3Light = mView.findViewById(R.id.valve3_light);
        valve4Light = mView.findViewById(R.id.valve4_light);
        valve6Light = mView.findViewById(R.id.valve6_light);
        valve8Light = mView.findViewById(R.id.valve8_light);
        valve9Light = mView.findViewById(R.id.valve9_light);
        valve9Light = mView.findViewById(R.id.valve9_light);
        vacuumLight = mView.findViewById(R.id.vacuum_light);
        waterLight = mView.findViewById(R.id.water_light);

        valve2Light.setEnabled(false);
        valve3Light.setEnabled(false);
        valve4Light.setEnabled(false);
        valve6Light.setEnabled(false);
        valve8Light.setEnabled(false);
        valve9Light.setEnabled(false);
        valve9Light.setEnabled(false);
        vacuumLight.setEnabled(false);
        waterLight.setEnabled(false);

        homeButton = mView.findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.openFragment(new HomeFragment());
            }
        });

        sys2_light = mView.findViewById(R.id.sys2_light);
        sys2_light.setVisibility(GONE);
    }

    public void updateLights() {
//        valve2Light.setChecked(mainActivity.valve2 == 1);
//        valve3Light.setChecked(mainActivity.valve3 == 1);
//        valve4Light.setChecked(mainActivity.valve4 == 1);
//        valve6Light.setChecked(mainActivity.valve6 == 1);
//        valve8Light.setChecked(mainActivity.valve8 == 1);
//        valve9Light.setChecked(mainActivity.valve9 == 1);
//        vacuumLight.setChecked(mainActivity.vacuum_pump == 1);
//        oilLight.setChecked(mainActivity.oil_pump == 1);
//        waterLight.setChecked(mainActivity.water_pump == 1);
    }
}