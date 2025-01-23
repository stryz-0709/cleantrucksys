package com.aasolution.cleantrucksysbeta;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ProcessFragment extends Fragment {
    View mView;
    com.aasolution.cleantrucksysbeta.MainActivity mainActivity;
    ToggleButton startProcessA1, stopProcessA1,
            startProcessA2, stopProcessA2,
            startProcessB1, stopProcessB1,
            startProcessB2, stopProcessB2, waterStopButton;
    RelativeLayout homeButton;
    RelativeLayout zoomIn, zoomOut;
    ConstraintLayout constraintLayout;
    ToggleButton valve2Light, valve3Light, valve4Light, valve6Light, valve8Light,
            valve9Light, vacuumLight, waterLight;

    ImageView valve2Glow1, valve2Glow2, valve2Glow3, valve2Glow4,
            valve3Glow1, valve3Glow2, valve3Glow3, valve3Glow4,
            valve4Glow1, valve4Glow2, valve4Glow3, valve4Glow4,
            valve6Glow1, valve6Glow2, valve6Glow3, valve6Glow4,
            valve8Glow1, valve8Glow2, valve8Glow3, valve8Glow4,
            valve9Glow1, valve9Glow2, valve9Glow3, valve9Glow4;

    ImageView startProcessA1Gradient, stopProcessA1Gradient,
            startProcessA2Gradient, stopProcessA2Gradient,
            startProcessB1Gradient, stopProcessB1Gradient,
            startProcessB2Gradient, stopProcessB2Gradient, stopWaterGradient,
            valve2Gradient, valve3Gradient, valve4Gradient,
            valve6Gradient, valve9Gradient, valve8Gradient,
            vacuumGradient, waterGradient;
    TextView state;

    private float currentScale = 1.0f;
    private final float scaleStep = 0.1f;
    private final float maxScale = 3.0f;
    private final float minScale = 1.0f;

    private Handler handler = new Handler();
    private Runnable dataFetchRunnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        mView = inflater.inflate(R.layout.fragment_process, container, false);
        mainActivity = (com.aasolution.cleantrucksysbeta.MainActivity) getActivity();

        startProcessA1 = mView.findViewById(R.id.start_processA1_button);
        stopProcessA1 = mView.findViewById(R.id.stop_processA1_button);
        startProcessA2 = mView.findViewById(R.id.start_processA2_button);
        stopProcessA2 = mView.findViewById(R.id.stop_processA2_button);
        startProcessB1 = mView.findViewById(R.id.start_processB1_button);
        stopProcessB1 = mView.findViewById(R.id.stop_processB1_button);
        startProcessB2 = mView.findViewById(R.id.start_processB2_button);
        stopProcessB2 = mView.findViewById(R.id.stop_processB2_button);
        waterStopButton = mView.findViewById(R.id.stop_water_button);

        state = mView.findViewById(R.id.state);

        startProcessA1Gradient = mView.findViewById(R.id.start_processA1_gradient);
        stopProcessA1Gradient = mView.findViewById(R.id.stop_processA1_gradient);
        startProcessA2Gradient = mView.findViewById(R.id.start_processA2_gradient);
        stopProcessA2Gradient = mView.findViewById(R.id.stop_processA2_gradient);
        startProcessB1Gradient = mView.findViewById(R.id.start_processB1_gradient);
        stopProcessB1Gradient = mView.findViewById(R.id.stop_processB1_gradient);
        startProcessB2Gradient = mView.findViewById(R.id.start_processB2_gradient);
        stopProcessB2Gradient = mView.findViewById(R.id.stop_processB2_gradient);
        stopWaterGradient = mView.findViewById(R.id.stop_water_gradient);
        valve2Gradient = mView.findViewById(R.id.valve2_gradient);
        valve3Gradient = mView.findViewById(R.id.valve3_gradient);
        valve4Gradient = mView.findViewById(R.id.valve4_gradient);
        valve6Gradient = mView.findViewById(R.id.valve6_gradient);
        valve8Gradient = mView.findViewById(R.id.valve8_gradient);
        valve9Gradient = mView.findViewById(R.id.valve9_gradient);
        vacuumGradient = mView.findViewById(R.id.vacuum_gradient);
        waterGradient = mView.findViewById(R.id.water_gradient);

        valve2Glow1 = mView.findViewById(R.id.valve2_glow1);
        valve2Glow2 = mView.findViewById(R.id.valve2_glow2);
        valve2Glow3 = mView.findViewById(R.id.valve2_glow3);
        valve2Glow4 = mView.findViewById(R.id.valve2_glow4);

        valve3Glow1 = mView.findViewById(R.id.valve3_glow1);
        valve3Glow2 = mView.findViewById(R.id.valve3_glow2);
        valve3Glow3 = mView.findViewById(R.id.valve3_glow3);
        valve3Glow4 = mView.findViewById(R.id.valve3_glow4);

        valve4Glow1 = mView.findViewById(R.id.valve4_glow1);
        valve4Glow2 = mView.findViewById(R.id.valve4_glow2);
        valve4Glow3 = mView.findViewById(R.id.valve4_glow3);
        valve4Glow4 = mView.findViewById(R.id.valve4_glow4);

        valve6Glow1 = mView.findViewById(R.id.valve6_glow1);
        valve6Glow2 = mView.findViewById(R.id.valve6_glow2);
        valve6Glow3 = mView.findViewById(R.id.valve6_glow3);
        valve6Glow4 = mView.findViewById(R.id.valve6_glow4);

        valve8Glow1 = mView.findViewById(R.id.valve8_glow1);
        valve8Glow2 = mView.findViewById(R.id.valve8_glow2);
        valve8Glow3 = mView.findViewById(R.id.valve8_glow3);
        valve8Glow4 = mView.findViewById(R.id.valve8_glow4);

        valve9Glow1 = mView.findViewById(R.id.valve9_glow1);
        valve9Glow2 = mView.findViewById(R.id.valve9_glow2);
        valve9Glow3 = mView.findViewById(R.id.valve9_glow3);
        valve9Glow4 = mView.findViewById(R.id.valve9_glow4);

        startProcessA1Gradient.setVisibility(GONE);
        stopProcessA1Gradient.setVisibility(GONE);
        startProcessA2Gradient.setVisibility(GONE);
        stopProcessA2Gradient.setVisibility(GONE);
        startProcessB1Gradient.setVisibility(GONE);
        stopProcessB1Gradient.setVisibility(GONE);
        startProcessB2Gradient.setVisibility(GONE);
        stopProcessB2Gradient.setVisibility(GONE);
        stopWaterGradient.setVisibility(GONE);

        valve2Gradient.setVisibility(GONE);
        valve3Gradient.setVisibility(GONE);
        valve4Gradient.setVisibility(GONE);
        valve6Gradient.setVisibility(GONE);
        valve8Gradient.setVisibility(GONE);
        valve9Gradient.setVisibility(GONE);
        vacuumGradient.setVisibility(GONE);
        waterGradient.setVisibility(GONE);

        valve2Glow1.setVisibility(GONE);
        valve2Glow2.setVisibility(GONE);
        valve2Glow3.setVisibility(GONE);
        valve2Glow4.setVisibility(GONE);

        valve3Glow1.setVisibility(GONE);
        valve3Glow2.setVisibility(GONE);
        valve3Glow3.setVisibility(GONE);
        valve3Glow4.setVisibility(GONE);

        valve4Glow1.setVisibility(GONE);
        valve4Glow2.setVisibility(GONE);
        valve4Glow3.setVisibility(GONE);
        valve4Glow4.setVisibility(GONE);

        valve6Glow1.setVisibility(GONE);
        valve6Glow2.setVisibility(GONE);
        valve6Glow3.setVisibility(GONE);
        valve6Glow4.setVisibility(GONE);

        valve8Glow1.setVisibility(GONE);
        valve8Glow2.setVisibility(GONE);
        valve8Glow3.setVisibility(GONE);
        valve8Glow4.setVisibility(GONE);

        valve9Glow1.setVisibility(GONE);
        valve9Glow2.setVisibility(GONE);
        valve9Glow3.setVisibility(GONE);
        valve9Glow4.setVisibility(GONE);


        homeButton = mView.findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.openFragment(new HomeFragment());
            }
        });


        startProcessA1.setOnClickListener(v -> updateButton(startProcessA1, startProcessA1Gradient, stopProcessA1, stopProcessA1Gradient, "process", 1, null));
        stopProcessA1.setOnClickListener(v -> updateButton(startProcessA1, startProcessA1Gradient, stopProcessA1, stopProcessA1Gradient, "process", 1, null));
        startProcessA2.setOnClickListener(v -> updateButton(startProcessA2, startProcessA2Gradient, stopProcessA2, stopProcessA2Gradient, "process", 2, null));
        stopProcessA2.setOnClickListener(v -> updateButton(startProcessA2, startProcessA2Gradient, stopProcessA2, stopProcessA2Gradient, "process", 2, null));
        startProcessB1.setOnClickListener(v -> updateButton(startProcessB1, startProcessB1Gradient, stopProcessB1, stopProcessB1Gradient, "process", 3, null));
        stopProcessB1.setOnClickListener(v -> updateButton(startProcessB1, startProcessB1Gradient, stopProcessB1, stopProcessB1Gradient, "process", 3, null));
        startProcessB2.setOnClickListener(v -> updateButton(startProcessB2, startProcessB2Gradient, stopProcessB2, stopProcessB2Gradient, "process", 4, null));
        stopProcessB2.setOnClickListener(v -> updateButton(startProcessB2, startProcessB2Gradient, stopProcessB2, stopProcessB2Gradient, "process", 4, null));
        waterStopButton.setOnClickListener(v -> updateButton(waterStopButton, stopWaterGradient, null, null, "water_power", 0, null));

        constraintLayout = mView.findViewById(R.id.ConstraintLayout);

        valve2Light = mView.findViewById(R.id.valve2_light);
        valve3Light = mView.findViewById(R.id.valve3_light);
        valve4Light = mView.findViewById(R.id.valve4_light);
        valve6Light = mView.findViewById(R.id.valve6_light);
        valve8Light = mView.findViewById(R.id.valve8_light);
        valve9Light = mView.findViewById(R.id.valve9_light);
        vacuumLight = mView.findViewById(R.id.vacuum_light);
        waterLight = mView.findViewById(R.id.water_light);

        valve2Light.setEnabled(false);
        valve3Light.setEnabled(false);
        valve4Light.setEnabled(false);
        valve6Light.setEnabled(false);
        valve8Light.setEnabled(false);
        valve9Light.setEnabled(false);
        vacuumLight.setEnabled(false);
        waterLight.setEnabled(false);

        dataFetch();
        startPeriodicDataFetch();

        return mView;
    }

    private void updateButton(ToggleButton startButton, ImageView startGradient, ToggleButton stopButton, ImageView stopGradient, String key, int type, JSONObject jsonObject) {
        try {
            boolean isActive = false;

            if (jsonObject != null) {
                // Update state based on JSON response
                int keyValue = jsonObject.has(key) ? jsonObject.getInt(key) : -1;
                if (key.equals("process")) {
                    isActive = keyValue == type;
                } else if (key.equals("water_power")) {
                    isActive = keyValue == 0;
                }
            } else {
                // Check which button was clicked
                if (startButton != null && startButton.isPressed()) {
                    isActive = true;
                } else if (stopButton != null && stopButton.isPressed()) {
                    isActive = false;
                } else {
                    return; // If neither button was pressed, exit
                }

                // Prepare and send JSON to the server
                JSONObject jsonData = new JSONObject();
                if (key.equals("process")) {
                    jsonData.put(key, isActive ? type : 0); // 4 for start, 0 for stop
                } else {
                    jsonData.put(key, isActive ? 0 : 1); // 0 for active, 1 for inactive
                }
                mainActivity.postOKHTTP(jsonData.toString());
            }

            // Update the UI state
            if (startButton != null && startGradient != null) {
                startButton.setChecked(isActive);
                startGradient.setVisibility(isActive ? VISIBLE : GONE);
            }
            if (stopButton != null && stopGradient != null) {
                stopButton.setChecked(!isActive);
                stopGradient.setVisibility(!isActive ? VISIBLE : GONE);
            }


        } catch (JSONException e) {
            Log.e("ProcessB2", "Error handling state for key: " + key, e);
        }
    }


    private void startPeriodicDataFetch() {
        dataFetchRunnable = new Runnable() {
            @Override
            public void run() {
                dataFetch();
                handler.postDelayed(this, 1000); // Update every second
            }
        };
        handler.post(dataFetchRunnable);
    }

    public void stopPeriodicDataFetch() {
        if (dataFetchRunnable != null) {
            handler.removeCallbacks(dataFetchRunnable);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopTogglingLights();
        stopPeriodicDataFetch();
    }

    private boolean toggleState = true;
    private int flowState = 0;
    private Handler toggleHandler = new Handler(); // Handler for scheduling tasks

    // Starts the toggling process
    private void startTogglingLights() {
        toggleHandler.removeCallbacks(toggleRunnable); // Remove any existing tasks
        toggleHandler.post(toggleRunnable); // Start the toggling process
    }

    // Stops the toggling process
    private void stopTogglingLights() {
        toggleHandler.removeCallbacks(toggleRunnable); // Stop any ongoing tasks
    }

    private void updateLights(JSONObject jsonObject) {
        try {
            valve2Light.setChecked(jsonObject.getInt("v2") == 1);
            valve3Light.setChecked(jsonObject.getInt("v3") == 1);
            valve4Light.setChecked(jsonObject.getInt("v4") == 1);
            valve6Light.setChecked(jsonObject.getInt("v6") == 1);
            valve9Light.setChecked(jsonObject.getInt("v9") == 1);
            valve8Light.setChecked(jsonObject.getInt("v8") == 1);
            vacuumLight.setChecked(jsonObject.getInt("vacuum_power") == 1);
            waterLight.setChecked(jsonObject.getInt("water_power") == 1);

            startTogglingLights();

        } catch (JSONException e) {
            Log.e("ProcessB2", "Error updating lights", e);
        }
    }

    private int toggleCounter = 0; // Counter to track when to toggle state

    private Runnable toggleRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                // Toggle only activated lights
                toggleLight(valve2Light, valve2Gradient, valve2Glow1, valve2Glow2, valve2Glow3, valve2Glow4, 0);
                toggleLight(valve3Light, valve3Gradient, valve3Glow1, valve3Glow2, valve3Glow3, valve3Glow4, 0);
                toggleLight(valve4Light, valve4Gradient, valve4Glow1, valve4Glow2, valve4Glow3, valve4Glow4, 1);
                toggleLight(valve6Light, valve6Gradient, valve6Glow1, valve6Glow2, valve6Glow3, valve6Glow4, 1);
                toggleLight(valve8Light, valve8Gradient, valve8Glow1, valve8Glow2, valve8Glow3, valve8Glow4, 0);
                toggleLight(valve9Light, valve9Gradient, valve9Glow1, valve9Glow2, valve9Glow3, valve9Glow4, 1);
                toggleLight(vacuumLight, vacuumGradient, null, null, null, null, 0);
                toggleLight(waterLight, waterGradient, null, null, null, null, 0);

                // Increment the toggleCounter
                toggleCounter++;

                // Flip the toggle state every 1 second (5 cycles of 200 ms)
                if (toggleCounter >= 10) {
                    toggleState = !toggleState;
                    toggleCounter = 0; // Reset the counter
                }

                // Update flowState for glow animation
                flowState = (flowState == 3) ? 0 : flowState + 1;

                // Schedule the next toggle
                toggleHandler.postDelayed(this, 100); // Run every 200 ms
            } catch (Exception e) {
                Log.e("ToggleLights", "Error during light toggling", e);
            }
        }
    };

    // Helper method to toggle only activated lights
    private void toggleLight(ToggleButton lightButton, View gradient, View glow1, View glow2, View glow3, View glow4, int type) {
        if (lightButton.isChecked()) {
            gradient.setVisibility(toggleState ? GONE : VISIBLE);
            if (glow1 != null && glow2 != null && glow3 != null && glow4 != null){
                lineFlow(glow1, glow2, glow3, glow4, type);
            }
        }
        else {
            gradient.setVisibility(GONE);
            if (glow1 != null) glow1.setVisibility(GONE);
            if (glow2 != null) glow2.setVisibility(GONE);
            if (glow3 != null) glow3.setVisibility(GONE);
            if (glow4 != null) glow4.setVisibility(GONE);
        }
    }

    private void lineFlow(View glow1, View glow2, View glow3, View glow4, int type) {
        // Array to hold the glow views
        View[] glowViews;

        if (type == 0) {
            glowViews = new View[]{glow1, glow2, glow3, glow4};
        } else { // Reverse order for type == 2
            glowViews = new View[]{glow4, glow3, glow2, glow1};
        }

        for (int i = 0; i < glowViews.length; i++) {
            if (glowViews[i] != null) {
                glowViews[i].setVisibility(i == flowState ? VISIBLE : GONE); // Only set VISIBLE for the matching flowState
            }
        }
        Log.d("flowState", String.valueOf(flowState));
    }



    private void dataFetch() {
        mainActivity.getOKHTTP(new MainActivity.ResponseCallback() {
            @Override
            public void onResponse(String response) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Log.d("ProcessB2", "Received JSON: " + jsonObject.toString());

                        updateLights(jsonObject);
                        updateButton(startProcessA1, startProcessA1Gradient, stopProcessA1, stopProcessA1Gradient, "process", 1, jsonObject);
                        updateButton(startProcessA2, startProcessA2Gradient, stopProcessA2, stopProcessA2Gradient, "process", 2, jsonObject);
                        updateButton(startProcessB1, startProcessB1Gradient, stopProcessB1, stopProcessB1Gradient, "process", 3, jsonObject);
                        updateButton(startProcessB2, startProcessB2Gradient, stopProcessB2, stopProcessB2Gradient, "process", 4, jsonObject);
                        updateButton(waterStopButton, stopWaterGradient, null, null, "water_power", 0, jsonObject);

                        int process = jsonObject.getInt("process");

                        state.setText(process == 1 ? "Quy trình A1 đang diễn ra" : process == 2 ? "Quy trình A2 đang diễn ra" : process == 3 ? "Quy trình B1 đang diễn ra" : process == 4 ? "Quy trình B2 đang diễn ra" : "Không hoạt động");
                        state.setTextColor(process != 0 ? Color.parseColor("#00CC66") : Color.parseColor("#FF0000"));

                    } catch (JSONException e) {
                        Log.e("ProcessB2", "Error parsing JSON response", e);
                    }
                });
            }

            @Override
            public void onError(String error) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Failed to fetch data. Check connection.", Toast.LENGTH_LONG).show()
                );
            }
        });
    }
}