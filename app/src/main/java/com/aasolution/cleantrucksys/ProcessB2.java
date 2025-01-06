package com.aasolution.cleantrucksys;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
public class ProcessB2 extends Fragment {
    View mView;
    MainActivity mainActivity;
    ToggleButton startProcessB2, stopProcessB2, waterStopButton;
    RelativeLayout backButton;
    RelativeLayout zoomIn, zoomOut;
    ConstraintLayout constraintLayout;
    ToggleButton valve2Light, valve3Light, valve4Light, valve6Light, valve8Light,
            valve9Light, vacuumLight, oilLight, waterLight;
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
        mView = inflater.inflate(R.layout.fragment_process_b2, container, false);
        mainActivity = (MainActivity) getActivity();

        startProcessB2 = mView.findViewById(R.id.start_process_button);
        stopProcessB2 = mView.findViewById(R.id.stop_process_button);
        waterStopButton = mView.findViewById(R.id.stop_water_button);

        state = mView.findViewById(R.id.state);

        backButton = mView.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.openFragment(new ProcessB());
            }
        });

        startProcessB2.setOnClickListener(v -> updateButton(startProcessB2, stopProcessB2, "process", null));
        stopProcessB2.setOnClickListener(v -> updateButton(startProcessB2, stopProcessB2, "process", null));
        waterStopButton.setOnClickListener(v -> updateButton(waterStopButton, null, "water_power", null));

        zoomIn = mView.findViewById(R.id.zoom_in_button);
        zoomOut = mView.findViewById(R.id.zoom_out_button);
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

        dataFetch();
        startPeriodicDataFetch();

        return mView;
    }

    private void updateButton(ToggleButton startButton, ToggleButton stopButton, String key, JSONObject jsonObject) {
        try {
            boolean isActive = false;

            if (jsonObject != null) {
                // Update state based on JSON response
                int keyValue = jsonObject.has(key) ? jsonObject.getInt(key) : -1;
                if (key.equals("process")) {
                    isActive = keyValue == 4;
                }
                else if (key.equals("water_power")){
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
                    jsonData.put(key, isActive ? 4 : 0); // 4 for start, 0 for stop
                } else {
                    jsonData.put(key, isActive ? 0 : 1); // 0 for active, 1 for inactive
                }
                mainActivity.postOKHTTP(jsonData.toString());
            }

            // Update the UI state
            if (startButton != null) {
                startButton.setChecked(isActive);
            }
            if (stopButton != null) {
                stopButton.setChecked(!isActive);
            }

            // Additional UI updates for "process"
            if (key.equals("process")) {
                state.setText(isActive ? "Đang hoạt động" : "Không hoạt động");
                state.setTextColor(isActive ? Color.parseColor("#00CC66") : Color.parseColor("#FF0000"));
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
        stopPeriodicDataFetch();
    }

    private void updateLights(JSONObject jsonObject) {
        try {
            valve2Light.setChecked(jsonObject.getInt("v2") == 1);
            valve3Light.setChecked(jsonObject.getInt("v3") == 1);
            valve4Light.setChecked(jsonObject.getInt("v4") == 1);
            valve6Light.setChecked(jsonObject.getInt("v6") == 1);
            valve8Light.setChecked(jsonObject.getInt("v8") == 1);
            valve9Light.setChecked(jsonObject.getInt("v9") == 1);
            waterLight.setChecked(jsonObject.getInt("water_power") == 1);
            vacuumLight.setChecked(jsonObject.getInt("vacuum_power") == 1);
            oilLight.setChecked(jsonObject.getInt("oil_power") == 1);

        } catch (JSONException e) {
            Log.e("ProcessB2", "Error updating lights", e);
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
                        updateButton(startProcessB2, stopProcessB2, "process", jsonObject);
                        updateButton(waterStopButton, null, "water_power", jsonObject);

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