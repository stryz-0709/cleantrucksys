package com.aasolution.cleantrucksys;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ProcessA2 extends Fragment {
    View mView;
    MainActivity mainActivity;

    Button StartProcessA, StopProcessA;
    RelativeLayout backButton;
    RelativeLayout zoomIn, zoomOut;
    ConstraintLayout constraintLayout;
    ToggleButton valve2Light, valve3Light, valve4Light, valve6Light, valve9Light,
            vacuumLight, oilLight;
    private float currentScale = 1.0f;
    private final float scaleStep = 0.1f;
    private final float maxScale = 3.0f;
    private final float minScale = 1.0f;

    private Handler handler = new Handler();
    private Runnable updateRunnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        mView = inflater.inflate(R.layout.fragment_process_a2, container, false);
        mainActivity = (MainActivity) getActivity();

        buttons();
        initializeLights();
        startUpdatingLights();
        updateLights();

        return mView;

    }

    private void buttons(){
        StartProcessA = mView.findViewById(R.id.start_process);

        StopProcessA = mView.findViewById(R.id.stop_process);

        backButton = mView.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.openFragment(new ProcessA());
            }
        });

        StartProcessA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("StartProcessA", "Button startA tapped");
                try {
                    // Create JSON object with processA data
                    JSONObject jsonData = new JSONObject();
                    jsonData.put("processA", 1);

                    // Send JSON data
                    mainActivity.postOKHTTP(jsonData.toString());
                } catch (JSONException e) {
                    Log.e("StartProcessA", "Failed to create JSON for StartProcessA", e);
                }
            }
        });

        StopProcessA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("StopProcessA", "Button stopA tapped");
                try {
                    // Create JSON object with processA data
                    JSONObject jsonData = new JSONObject();
                    jsonData.put("processA", 0);

                    // Send JSON data
                    mainActivity.postOKHTTP(jsonData.toString());
                } catch (JSONException e) {
                    Log.e("StopProcessA", "Failed to create JSON for StopProcessA", e);
                }
            }
        });

        zoomIn = mView.findViewById(R.id.zoom_in_button);
        zoomOut = mView.findViewById(R.id.zoom_out_button);
        constraintLayout = mView.findViewById(R.id.ConstraintLayout);

        valve2Light = mView.findViewById(R.id.valve2_light);
        valve3Light = mView.findViewById(R.id.valve3_light);
        valve4Light = mView.findViewById(R.id.valve4_light);
        valve6Light = mView.findViewById(R.id.valve6_light);
        valve9Light = mView.findViewById(R.id.valve9_light);
        vacuumLight = mView.findViewById(R.id.vacuum_light);
        oilLight = mView.findViewById(R.id.oil_light);

        valve2Light.setEnabled(false);
        valve3Light.setEnabled(false);
        valve4Light.setEnabled(false);
        valve6Light.setEnabled(false);
        valve9Light.setEnabled(false);
        vacuumLight.setEnabled(false);
        oilLight.setEnabled(false);

        zoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentScale < maxScale) {
                    currentScale += scaleStep;
                    updateZoom();
                }
            }
        });

        zoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentScale > minScale) {
                    currentScale -= scaleStep;
                    updateZoom();
                }
            }
        });
    }

    private void initializeLights() {
        valve2Light = mView.findViewById(R.id.valve2_light);
        valve3Light = mView.findViewById(R.id.valve3_light);
        valve4Light = mView.findViewById(R.id.valve4_light);
        valve6Light = mView.findViewById(R.id.valve6_light);
        valve9Light = mView.findViewById(R.id.valve9_light);
        vacuumLight = mView.findViewById(R.id.vacuum_light);
        oilLight = mView.findViewById(R.id.oil_light);

        // Disable interaction with the lights (they're indicators, not controls)
        valve2Light.setEnabled(false);
        valve3Light.setEnabled(false);
        valve4Light.setEnabled(false);
        valve6Light.setEnabled(false);
        valve9Light.setEnabled(false);
        vacuumLight.setEnabled(false);
        oilLight.setEnabled(false);
    }

    private void startUpdatingLights() {
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                updateLights();
                handler.postDelayed(this, 1000); // Update every second
            }
        };
        handler.post(updateRunnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(updateRunnable); // Stop updates when fragment is destroyed
    }

    public void updateLights(){
        if (mainActivity != null) {
            valve2Light.setChecked(mainActivity.valve2 == 1);
            valve3Light.setChecked(mainActivity.valve3 == 1);
            valve4Light.setChecked(mainActivity.valve4 == 1);
            valve6Light.setChecked(mainActivity.valve6 == 1);
            valve9Light.setChecked(mainActivity.valve9 == 1);
            vacuumLight.setChecked(mainActivity.vacuum_pump == 1);
            oilLight.setChecked(mainActivity.oil_pump == 1);
        }
    }


    private void updateZoom() {
        constraintLayout.setPivotX(0);
        constraintLayout.setPivotY(0);
        constraintLayout.setScaleX(currentScale);
        constraintLayout.setScaleY(currentScale);

        int originalWidth = constraintLayout.getMeasuredWidth();
        int originalHeight = constraintLayout.getMeasuredHeight();
        int newWidth = (int) (originalWidth * currentScale);
        int newHeight = (int) (originalHeight * currentScale);

        ViewGroup.LayoutParams params = constraintLayout.getLayoutParams();
        params.width = newWidth;
        params.height = newHeight;
        constraintLayout.setLayoutParams(params);

        constraintLayout.requestLayout();
    }
}