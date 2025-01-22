package com.aasolution.cleantrucksys;

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
    MainActivity mainActivity;
    ToggleButton startProcessA1, stopProcessA1,
            startProcessA2, stopProcessA2,
            startProcessB1, stopProcessB1,
            startProcessB2, stopProcessB2, waterStopButton;
    RelativeLayout homeButton;
    RelativeLayout zoomIn, zoomOut;
    ConstraintLayout constraintLayout;
    ToggleButton valve2Light, valve3Light, valve4Light, valve6Light, valve8Light,
            valve9Light, vacuumLight, waterLight;

    ImageView valve2Glow, valve3Glow, valve4Glow, valve6Glow, valve8Glow, valve9Glow;

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
        mainActivity = (MainActivity) getActivity();

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

        valve2Glow = mView.findViewById(R.id.valve2_glow);
        valve3Glow = mView.findViewById(R.id.valve3_glow);
        valve4Glow = mView.findViewById(R.id.valve4_glow);
        valve6Glow = mView.findViewById(R.id.valve6_glow);
        valve8Glow = mView.findViewById(R.id.valve8_glow);
        valve9Glow = mView.findViewById(R.id.valve9_glow);

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

        valve2Glow.setVisibility(GONE);
        valve3Glow.setVisibility(GONE);
        valve4Glow.setVisibility(GONE);
        valve6Glow.setVisibility(GONE);
        valve8Glow.setVisibility(GONE);
        valve9Glow.setVisibility(GONE);


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
            valve2Gradient.setVisibility(jsonObject.getInt("v2") == 1 ? VISIBLE : GONE);
            valve2Glow.setVisibility(jsonObject.getInt("v2") == 1 ? VISIBLE : GONE);
            valve3Light.setChecked(jsonObject.getInt("v3") == 1);
            valve3Gradient.setVisibility(jsonObject.getInt("v3") == 1 ? VISIBLE : GONE);
            valve3Glow.setVisibility(jsonObject.getInt("v3") == 1 ? VISIBLE : GONE);
            valve4Light.setChecked(jsonObject.getInt("v4") == 1);
            valve4Gradient.setVisibility(jsonObject.getInt("v4") == 1 ? VISIBLE : GONE);
            valve4Glow.setVisibility(jsonObject.getInt("v4") == 1 ? VISIBLE : GONE);
            valve6Light.setChecked(jsonObject.getInt("v6") == 1);
            valve6Gradient.setVisibility(jsonObject.getInt("v6") == 1 ? VISIBLE : GONE);
            valve6Glow.setVisibility(jsonObject.getInt("v6") == 1 ? VISIBLE : GONE);
            valve9Light.setChecked(jsonObject.getInt("v9") == 1);
            valve9Gradient.setVisibility(jsonObject.getInt("v9") == 1 ? VISIBLE : GONE);
            valve9Glow.setVisibility(jsonObject.getInt("v9") == 1 ? VISIBLE : GONE);
            valve8Light.setChecked(jsonObject.getInt("v8") == 1);
            valve8Gradient.setVisibility(jsonObject.getInt("v8") == 1 ? VISIBLE : GONE);
            valve8Glow.setVisibility(jsonObject.getInt("v8") == 1 ? VISIBLE : GONE);
            vacuumLight.setChecked(jsonObject.getInt("vacuum_power") == 1);
            vacuumGradient.setVisibility(jsonObject.getInt("vacuum_power") == 1 ? VISIBLE : GONE);
            waterLight.setChecked(jsonObject.getInt("water_power") == 1);
            waterGradient.setVisibility(jsonObject.getInt("water_power") == 1 ? VISIBLE : GONE);

            startTogglingLights();

        } catch (JSONException e) {
            Log.e("ProcessB2", "Error updating lights", e);
        }
    }

    private Runnable toggleRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                // Toggle only activated lights
                toggleLightIfActivated(valve2Light, valve2Gradient, valve2Glow);
                toggleLightIfActivated(valve3Light, valve3Gradient, valve3Glow);
                toggleLightIfActivated(valve4Light, valve4Gradient, valve4Glow);
                toggleLightIfActivated(valve6Light, valve6Gradient, valve6Glow);
                toggleLightIfActivated(valve8Light, valve8Gradient, valve8Glow);
                toggleLightIfActivated(valve9Light, valve9Gradient, valve9Glow);
                toggleLightIfActivated(vacuumLight, vacuumGradient, null);
                toggleLightIfActivated(waterLight, waterGradient, null);

                toggleState = !toggleState; // Flip the toggle state

                // Schedule the next toggle
                toggleHandler.postDelayed(this, 2000);
            } catch (Exception e) {
                Log.e("ToggleLights", "Error during light toggling", e);
            }
        }
    };

    // Helper method to toggle only activated lights
    private void toggleLightIfActivated(ToggleButton lightButton, View gradient, View glow) {
        if (lightButton.isChecked()) {
            gradient.setVisibility(toggleState ? GONE : VISIBLE);
            if (glow != null) glow.setVisibility(toggleState? VISIBLE : GONE);
        }
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