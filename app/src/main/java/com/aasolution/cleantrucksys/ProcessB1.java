package com.aasolution.cleantrucksys;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.github.angads25.toggle.widget.LabeledSwitch;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ProcessB1 extends Fragment {
    View mView;
    MainActivity mainActivity;
    ToggleButton startProcessB1, stopProcessB1;
    RelativeLayout backButton;
    RelativeLayout zoomIn, zoomOut;
    ConstraintLayout constraintLayout;
    ToggleButton valve2Light, valve3Light, valve4Light, valve6Light, valve8Light,
            valve9Light, vacuumLight, waterLight;

    ImageView startProcessGradient, stopProcessGradient,
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
        mView = inflater.inflate(R.layout.fragment_process_b1, container, false);
        mainActivity = (MainActivity) getActivity();

        startProcessB1 = mView.findViewById(R.id.start_process_button);
        stopProcessB1 = mView.findViewById(R.id.stop_process_button);
        state = mView.findViewById(R.id.state);

        startProcessGradient = mView.findViewById(R.id.start_process_gradient);
        stopProcessGradient = mView.findViewById(R.id.stop_process_gradient);
        valve2Gradient  = mView.findViewById(R.id.valve2_gradient);
        valve3Gradient  = mView.findViewById(R.id.valve3_gradient);
        valve4Gradient  = mView.findViewById(R.id.valve4_gradient);
        valve6Gradient  = mView.findViewById(R.id.valve6_gradient);
        valve8Gradient  = mView.findViewById(R.id.valve8_gradient);
        valve9Gradient  = mView.findViewById(R.id.valve9_gradient);
        vacuumGradient  = mView.findViewById(R.id.vacuum_gradient);
        waterGradient = mView.findViewById(R.id.water_gradient);

        startProcessGradient.setVisibility(GONE);
        stopProcessGradient.setVisibility(GONE);

        backButton = mView.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.openFragment(new ProcessB());
            }
        });

        startProcessB1.setOnClickListener(v -> updateProcess(startProcessB1, stopProcessB1, "process", null));
        stopProcessB1.setOnClickListener(v -> updateProcess(startProcessB1, stopProcessB1, "process", null));

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

    private void updateProcess(ToggleButton startButton, ToggleButton stopButton, String key, JSONObject jsonObject) {
        try {
            boolean isProcessActive;

            if (jsonObject != null) {
                // Update state based on JSON response
                isProcessActive = jsonObject.has(key) && jsonObject.getInt(key) == 3; // 3 indicates "active"
            } else {
                // Check which button was clicked
                if (startButton.isPressed()) {
                    isProcessActive = true;
                } else if (stopButton.isPressed()) {
                    isProcessActive = false;
                } else {
                    return; // If neither button was pressed, exit
                }

                // Prepare and send JSON to the server
                JSONObject jsonData = new JSONObject();
                jsonData.put(key, isProcessActive ? 3 : 0); // 3 for start, 0 for stop
                mainActivity.postOKHTTP(jsonData.toString());
            }

            // Update the UI state
            startButton.setChecked(isProcessActive);
            stopButton.setChecked(!isProcessActive);
            state.setText(isProcessActive ? "Đang hoạt động" : "Không hoạt động");
            state.setTextColor(isProcessActive ? Color.parseColor("#00CC66") : Color.parseColor("#FF0000"));
            if (isProcessActive){
                startProcessGradient.setVisibility(VISIBLE);
                stopProcessGradient.setVisibility(GONE);
            }
            else{
                startProcessGradient.setVisibility(GONE);
                stopProcessGradient.setVisibility(VISIBLE);
            }

        } catch (JSONException e) {
            Log.e("ProcessB1", "Error handling process state for key: " + key, e);
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
            valve2Gradient.setVisibility(jsonObject.getInt("v2") == 1? VISIBLE: GONE);
            valve3Light.setChecked(jsonObject.getInt("v3") == 1);
            valve3Gradient.setVisibility(jsonObject.getInt("v3") == 1? VISIBLE: GONE);
            valve4Light.setChecked(jsonObject.getInt("v4") == 1);
            valve4Gradient.setVisibility(jsonObject.getInt("v4") == 1? VISIBLE: GONE);
            valve6Light.setChecked(jsonObject.getInt("v6") == 1);
            valve6Gradient.setVisibility(jsonObject.getInt("v6") == 1? VISIBLE: GONE);
            valve9Light.setChecked(jsonObject.getInt("v9") == 1);
            valve9Gradient.setVisibility(jsonObject.getInt("v9") == 1? VISIBLE: GONE);
            valve8Light.setChecked(jsonObject.getInt("v8") == 1);
            valve8Gradient.setVisibility(jsonObject.getInt("v8") == 1? VISIBLE: GONE);
            vacuumLight.setChecked(jsonObject.getInt("vacuum_power") == 1);
            vacuumGradient.setVisibility(jsonObject.getInt("vacuum_power") == 1? VISIBLE: GONE);
            waterLight.setChecked(jsonObject.getInt("water_power") == 1);
            waterGradient.setVisibility(jsonObject.getInt("water_power") == 1? VISIBLE: GONE);

        } catch (JSONException e) {
            Log.e("ProcessB1", "Error updating lights", e);
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
                        Log.d("ProcessB1", "Received JSON: " + jsonObject.toString());

                        updateLights(jsonObject);
                        updateProcess(startProcessB1, stopProcessB1, "process", jsonObject);

                    } catch (JSONException e) {
                        Log.e("ProcessB1", "Error parsing JSON response", e);
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