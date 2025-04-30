package com.aasolution.cleantrucksysbeta;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.github.anastr.speedviewlib.SpeedView;
import com.github.angads25.toggle.widget.LabeledSwitch;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ProcessFragment extends Fragment {
    View mView;
    com.aasolution.cleantrucksysbeta.MainActivity mainActivity;
    ToggleButton startProcessA1, stopProcess,
            startProcessA2,
            startProcessB1,
            startProcessB2;
    RelativeLayout homeButton;
    RelativeLayout zoomIn, zoomOut;
    ConstraintLayout constraintLayout;
    SpeedView vacuumPressure, waterPressure;
    ToggleButton valve2Light, valve3Light, valve4Light, valve6Light, valve8Light,
            valve9Light, vacuumLight, waterLight;
    LabeledSwitch waterButton, armButton;

    ImageView valve2Glow1, valve2Glow2, valve2Glow3, valve2Glow4,
            valve3Glow1, valve3Glow2, valve3Glow3, valve3Glow4,
            valve4Glow1, valve4Glow2, valve4Glow3, valve4Glow4,
            valve6Glow1, valve6Glow2, valve6Glow3, valve6Glow4,
            valve8Glow1, valve8Glow2, valve8Glow3, valve8Glow4,
            valve9Glow1, valve9Glow2, valve9Glow3, valve9Glow4;

    ImageView startProcessA1Gradient, stopProcessGradient,
            startProcessA2Gradient,
            startProcessB1Gradient,
            startProcessB2Gradient,
            valve2Gradient, valve3Gradient, valve4Gradient,
            valve6Gradient, valve9Gradient, valve8Gradient,
            vacuumGradient, waterGradient;
    TextView state;
    EditText phun_a_value, phun_b_value, hut_a_value, hut_b_value, so_lan_a_value, so_lan_b_value;

    private float currentScale = 1.0f;
    private final float scaleStep = 0.1f;
    private final float maxScale = 3.0f;
    private final float minScale = 1.0f;

    private Handler handler = new Handler();
    private final Map<String, Boolean> pendingUpdates = new HashMap<>();
    private Runnable dataFetchRunnable;

    private final Map<String, Boolean> buttonStates = new HashMap<>();
    private Handler wifiHandler = new Handler();
    private Runnable wifiRunnable;
    private ProgressDialog progressDialog;

    public void setProgressDialog(ProgressDialog dialog) {
        this.progressDialog = dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        mView = inflater.inflate(R.layout.fragment_process, container, false);
        mainActivity = (com.aasolution.cleantrucksysbeta.MainActivity) getActivity();



        startProcessA1 = mView.findViewById(R.id.start_processA1_button);
        startProcessB1 = mView.findViewById(R.id.start_processB1_button);
        stopProcess = mView.findViewById(R.id.stop_process_button);
        waterButton = mView.findViewById(R.id.water_button);
        armButton = mView.findViewById(R.id.arm_button);

        state = mView.findViewById(R.id.state);

        startProcessA1Gradient = mView.findViewById(R.id.start_processA1_gradient);
        startProcessB1Gradient = mView.findViewById(R.id.start_processB1_gradient);
        stopProcessGradient = mView.findViewById(R.id.stop_process_gradient);
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

        startProcessA1Gradient.setVisibility(View.INVISIBLE);
        startProcessB1Gradient.setVisibility(View.INVISIBLE);
        stopProcessGradient.setVisibility(View.INVISIBLE);

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

        vacuumPressure = mView.findViewById(R.id.vacuum_pressure_gauge);
        waterPressure = mView.findViewById(R.id.water_pressure_gauge);

        vacuumPressure.getSections().get(0).setColor(Color.parseColor("#CCCCCC"));
        vacuumPressure.getSections().get(1).setColor(Color.parseColor("#CCCCCC"));
        vacuumPressure.getSections().get(2).setColor(Color.parseColor("#CCCCCC"));
        vacuumPressure.setOnPrintTickLabel(new Function2<Integer, Float, String>() {
            @Override
            public String invoke(Integer tickPosition, Float tick) {
                return String.format("%.2f", tick);
            }
        });

        vacuumPressure.setSpeedTextListener(new Function1<Float, String>() {
            @Override
            public String invoke(Float speed) {
                return String.format("%.2f", speed);
            }
        });

        vacuumPressure.speedTo(0);
        waterPressure.speedTo(0);

        waterPressure.getSections().get(0).setColor(Color.parseColor("#CCCCCC"));
        waterPressure.getSections().get(1).setColor(Color.parseColor("#CCCCCC"));
        waterPressure.getSections().get(2).setColor(Color.parseColor("#CCCCCC"));


        homeButton = mView.findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.openFragment(new com.aasolution.cleantrucksysbeta.HomeFragment());
            }
        });


        startProcessA1.setOnClickListener(v -> updateButton(startProcessA1, startProcessA1Gradient, "process", 1, null));
        startProcessB1.setOnClickListener(v -> updateButton(startProcessB1, startProcessB1Gradient, "process", 2, null));
        stopProcess.setOnClickListener(v -> handleStopButtonClick());

        setupLabeledSwitch(waterButton, "water_power", null, null);
        setupLabeledSwitch(armButton, "arm_up", "arm_down", null);

        constraintLayout = mView.findViewById(R.id.ConstraintLayout);

        valve2Light = mView.findViewById(R.id.valve2_light);
        valve3Light = mView.findViewById(R.id.valve3_light);
        valve4Light = mView.findViewById(R.id.valve4_light);
        valve6Light = mView.findViewById(R.id.valve6_light);
        valve8Light = mView.findViewById(R.id.valve8_light);
        valve9Light = mView.findViewById(R.id.valve9_light);
        vacuumLight = mView.findViewById(R.id.vacuum_light);
        waterLight = mView.findViewById(R.id.water_light);

        phun_a_value = mView.findViewById(R.id.phun_a_value);
        phun_b_value = mView.findViewById(R.id.phun_b_value);
        hut_a_value = mView.findViewById(R.id.hut_a_value);
        hut_b_value = mView.findViewById(R.id.hut_b_value);
        so_lan_a_value = mView.findViewById(R.id.so_lan_a_value);
        so_lan_b_value = mView.findViewById(R.id.so_lan_b_value);

        View.OnClickListener numericClickListener = v -> {
            ((EditText) v).setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
            ((EditText) v).setSelection(((EditText) v).getText().length());
        };

        phun_a_value.setOnClickListener(numericClickListener);
        phun_b_value.setOnClickListener(numericClickListener);
        hut_a_value.setOnClickListener(numericClickListener);
        hut_b_value.setOnClickListener(numericClickListener);
        so_lan_a_value.setOnClickListener(numericClickListener);
        so_lan_b_value.setOnClickListener(numericClickListener);


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


    private void setupLabeledSwitch(LabeledSwitch switchButton, String key1, String key2, String key3) {
        switchButton.setOnClickListener(v -> {
            switchButton.setEnabled(false);
            try {
                boolean newState = Boolean.FALSE.equals(buttonStates.getOrDefault(key1, false));
                buttonStates.put(key1, newState);
                pendingUpdates.put(key1, newState);  // Track intent

                // Prepare the JSON data for the server request
                JSONObject jsonData = new JSONObject();

                if (key2 == null && key3 == null) {
                    // Single key: Send (1) or (0)
                    jsonData.put(key1, newState ? 1 : 0);
                } else if (key2 != null && key3 == null) {
                    // Two keys: Send (1;0) or (0;1)
                    jsonData.put(key1, newState ? 1 : 0);
                    jsonData.put(key2, newState ? 0 : 1);
                } else if (key2 != null && key3 != null) {
                    // Three keys: Send (1;0;0) or (0;0;0)
                    jsonData.put(key1, newState ? 1 : 0);
                    jsonData.put(key2, 0);
                    jsonData.put(key3, 0);
                }

                mainActivity.postOKHTTP(jsonData.toString());

            } catch (JSONException e) {
                Log.e("ManualFragment", "Error preparing JSON", e);
                switchButton.setEnabled(true);
            }
        });
    }

    private void updateLabeledSwitch(LabeledSwitch switchButton, String key1, String key2, JSONObject jsonObject) {
        try {
            if (jsonObject != null) {
                boolean newState;

                if (key2 == null) {
                    newState = jsonObject.has(key1) && jsonObject.getInt(key1) == 1;
                } else {
                    boolean key1State = jsonObject.has(key1) && jsonObject.getInt(key1) == 1;
                    boolean key2State = jsonObject.has(key2) && jsonObject.getInt(key2) == 1;
                    newState = key1State && !key2State;
                }

                Boolean pending = pendingUpdates.get(key1);
                if (pending != null && pending == newState) {
                    // Matched pending request, clear it
                    pendingUpdates.remove(key1);
                } else if (pending != null) {
                    // Ignore outdated or conflicting update
                    Log.d("ManualFragment", "Ignoring outdated response for " + key1);
                    return;
                }

                switchButton.setOn(newState);
                switchButton.setColorOn(Color.parseColor(newState ? "#4CAF50" : "#F00000"));
                buttonStates.put(key1, newState);
                switchButton.setEnabled(true);
            }
        } catch (JSONException e) {
            Log.e("ManualFragment", "Error updating state for keys: " + key1 + ", " + key2, e);
            switchButton.setEnabled(true);
        }
    }

    private void updateButton(ToggleButton toggleButton, ImageView toggleGradient, String key, int type, JSONObject jsonObject) {
        try {
            boolean isActive;

            String pendingKey = key + "_" + type;

            if (jsonObject != null) {
                int keyValue = jsonObject.has(key) ? jsonObject.getInt(key) : -1;
                isActive = keyValue == type;

                // Inserted logic to turn off stopProcess button if process is active (not 0)
                if (key.equals("process") && stopProcess != null && keyValue != 0) {
                    stopProcess.setChecked(false);
                    stopProcess.setEnabled(true);
                    stopProcessGradient.setVisibility(View.INVISIBLE);
                }

                Boolean pending = pendingUpdates.get(pendingKey);
                if (pending != null && pending == isActive) {
                    pendingUpdates.remove(pendingKey);
                } else if (pending != null) {
                    Log.d("ProcessFragment", "Ignoring outdated response for " + pendingKey);
                    return;
                }
            } else {
                isActive = toggleButton.isChecked();
                pendingUpdates.put(pendingKey, isActive);

                JSONObject jsonData = new JSONObject();
                if (key.equals("process")) {
                    boolean hasError = false;

                    if (type == 1) {
                        if (phun_a_value.getText().toString().trim().isEmpty()) {
                            phun_a_value.setError("Required");
                            hasError = true;
                        }
                        if (hut_a_value.getText().toString().trim().isEmpty()) {
                            hut_a_value.setError("Required");
                            hasError = true;
                        }
                        if (so_lan_a_value.getText().toString().trim().isEmpty()) {
                            so_lan_a_value.setError("Required");
                            hasError = true;
                        }
                        if (hasError) {
                            toggleButton.setChecked(false);
                            if (toggleGradient != null) {
                                toggleGradient.setVisibility(View.INVISIBLE);
                            }
                            return;
                        }

                        jsonData.put(key, isActive ? type : 0);
                        jsonData.put("pa", Integer.parseInt(phun_a_value.getText().toString()));
                        jsonData.put("ha", Integer.parseInt(hut_a_value.getText().toString()));
                        jsonData.put("la", Integer.parseInt(so_lan_a_value.getText().toString()));
                    } else if (type == 2) {
                        if (phun_b_value.getText().toString().trim().isEmpty()) {
                            phun_b_value.setError("Required");
                            hasError = true;
                        }
                        if (hut_b_value.getText().toString().trim().isEmpty()) {
                            hut_b_value.setError("Required");
                            hasError = true;
                        }
                        if (so_lan_b_value.getText().toString().trim().isEmpty()) {
                            so_lan_b_value.setError("Required");
                            hasError = true;
                        }
                        if (hasError) {
                            toggleButton.setChecked(false);
                            if (toggleGradient != null) {
                                toggleGradient.setVisibility(View.INVISIBLE);
                            }
                            return;
                        }

                        jsonData.put(key, isActive ? type : 0);
                        jsonData.put("pb", Integer.parseInt(phun_b_value.getText().toString()));
                        jsonData.put("hb", Integer.parseInt(hut_b_value.getText().toString()));
                        jsonData.put("lb", Integer.parseInt(so_lan_b_value.getText().toString()));
                    }
                } else {
                    jsonData.put(key, isActive ? 0 : 1);
                }
                mainActivity.postOKHTTP(jsonData.toString());
            }

            toggleButton.setChecked(isActive);
            if (toggleGradient != null) {
                toggleGradient.setVisibility(isActive ? View.VISIBLE : View.INVISIBLE);
            }

            // Disable or enable EditText inputs based on isActive
            if (type == 1) {
                if (phun_a_value != null) phun_a_value.setEnabled(!isActive);
                if (hut_a_value != null) hut_a_value.setEnabled(!isActive);
                if (so_lan_a_value != null) so_lan_a_value.setEnabled(!isActive);
            } else if (type == 2) {
                if (phun_b_value != null) phun_b_value.setEnabled(!isActive);
                if (hut_b_value != null) hut_b_value.setEnabled(!isActive);
                if (so_lan_b_value != null) so_lan_b_value.setEnabled(!isActive);
            }

        } catch (JSONException e) {
            Log.e("ProcessFragment", "Error handling state for key: " + key, e);
        }
    }


    private void startPeriodicDataFetch() {
        dataFetchRunnable = new Runnable() {
            @Override
            public void run() {
                dataFetch();
                handler.postDelayed(this, 2000); // Update every 2 second
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Now the view is fully created, dismiss progressDialog after a short delay to allow fragment to render
        if (progressDialog != null && progressDialog.isShowing()) {
            new Handler().postDelayed(() -> progressDialog.dismiss(), 300);
        }
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
                toggleLight(valve2Light, valve2Gradient, valve2Glow1, valve2Glow2, valve2Glow3, valve2Glow4, 1);
                toggleLight(valve3Light, valve3Gradient, valve3Glow1, valve3Glow2, valve3Glow3, valve3Glow4, 0);
                toggleLight(valve4Light, valve4Gradient, valve4Glow1, valve4Glow2, valve4Glow3, valve4Glow4, 0);
                toggleLight(valve6Light, valve6Gradient, valve6Glow1, valve6Glow2, valve6Glow3, valve6Glow4, 0);
                toggleLight(valve8Light, valve8Gradient, valve8Glow1, valve8Glow2, valve8Glow3, valve8Glow4, 0);
                toggleLight(valve9Light, valve9Gradient, valve9Glow1, valve9Glow2, valve9Glow3, valve9Glow4, 0);
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
            if (glow1 != null && glow2 != null && glow3 != null && glow4 != null) {
                lineFlow(glow1, glow2, glow3, glow4, type);
            }
        } else {
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
//        Log.d("flowState", String.valueOf(flowState));
    }


    private void dataFetch() {
        mainActivity.getOKHTTP(new com.aasolution.cleantrucksysbeta.MainActivity.ResponseCallback() {
            @Override
            public void onResponse(String response) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Log.d("ProcessB2", "Received JSON: " + jsonObject.toString());

                        updateLights(jsonObject);
                        updateButton(startProcessA1, startProcessA1Gradient, "process", 1, jsonObject);
                        updateButton(startProcessB1, startProcessB1Gradient, "process", 2, jsonObject);

                        updateLabeledSwitch(waterButton, "water_power", null, jsonObject);
                        updateLabeledSwitch(armButton, "arm_up", "arm_down", jsonObject);

                        int process = jsonObject.getInt("process");

                        state.setText(process == 1 ? "Quy trình Tráng bồn đang diễn ra" : process == 2 ? "Quy trình Rửa bồn đang diễn ra" : "Không hoạt động");
                        state.setTextColor(process != 0 ? Color.parseColor("#00CC66") : Color.parseColor("#FF0000"));

                        float vacuumPressureValue = (float) jsonObject.getDouble("vacuum_pressure");
                        float waterPressureValue = (float) jsonObject.getDouble("water_pressure");

                        vacuumPressure.speedTo(vacuumPressureValue);
                        waterPressure.speedTo(waterPressureValue);

                        try {
                            if (!phun_a_value.hasFocus() && isEditTextNaN(phun_a_value) && jsonObject.has("pa") && !Double.isNaN(jsonObject.getDouble("pa")))
                                phun_a_value.setText(String.valueOf(jsonObject.getInt("pa")));
                            if (!hut_a_value.hasFocus() && isEditTextNaN(hut_a_value) && jsonObject.has("ha") && !Double.isNaN(jsonObject.getDouble("ha")))
                                hut_a_value.setText(String.valueOf(jsonObject.getInt("ha")));
                            if (!so_lan_a_value.hasFocus() && isEditTextNaN(so_lan_a_value) && jsonObject.has("la") && !Double.isNaN(jsonObject.getDouble("la")))
                                so_lan_a_value.setText(String.valueOf(jsonObject.getInt("la")));

                            if (!phun_b_value.hasFocus() && isEditTextNaN(phun_b_value) && jsonObject.has("pb") && !Double.isNaN(jsonObject.getDouble("pb")))
                                phun_b_value.setText(String.valueOf(jsonObject.getInt("pb")));
                            if (!hut_b_value.hasFocus() && isEditTextNaN(hut_b_value) && jsonObject.has("hb") && !Double.isNaN(jsonObject.getDouble("hb")))
                                hut_b_value.setText(String.valueOf(jsonObject.getInt("hb")));
                            if (!so_lan_b_value.hasFocus() && isEditTextNaN(so_lan_b_value) && jsonObject.has("lb") && !Double.isNaN(jsonObject.getDouble("lb")))
                                so_lan_b_value.setText(String.valueOf(jsonObject.getInt("lb")));
                        } catch (JSONException ignored) {
                        }

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
        if (wifiRunnable == null) {
            wifiRunnable = new Runnable() {
                @Override
                public void run() {
                    if (!Objects.equals(mainActivity.getWifi(), "HMI2")) {
                        android.widget.Toast.makeText(requireContext(), "Kết nối bị gián đoạn, vui lòng kiểm tra lại đường truyền", android.widget.Toast.LENGTH_SHORT).show();
                        if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                            getParentFragmentManager().popBackStack();  // Pop the current fragment
                        } else {
                            requireActivity().onBackPressed();  // Go back in the activity's back stack
                        }
                    }
                    wifiHandler.postDelayed(this, 2000);  // Repeat every 2 seconds
                }
            };
            wifiHandler.post(wifiRunnable);
        }
    }


    // Helper method to check if EditText contains NaN or invalid number
    private boolean isEditTextNaN(EditText editText) {
        try {
            String value = editText.getText().toString().trim();
            return value.isEmpty() || Double.isNaN(Double.parseDouble(value));
        } catch (NumberFormatException e) {
            return true;
        }
    }

    private void handleStopButtonClick() {
        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận dừng khẩn cấp")
                .setMessage("Bạn có chắc chắn muốn dừng toàn bộ quy trình ngay lập tức?")
                .setPositiveButton("Dừng", (dialog, which) -> {
                    try {
                        JSONObject jsonData = new JSONObject();
                        jsonData.put("process", 0);
                        mainActivity.postOKHTTP(jsonData.toString());

                        stopProcess.setChecked(true);
                        stopProcess.setEnabled(false);
                        if (stopProcessGradient != null) {
                            stopProcessGradient.setVisibility(View.VISIBLE);
                        }

                    } catch (JSONException e) {
                        Log.e("ProcessFragment", "Error sending emergency stop", e);
                    }
                })
                .setNegativeButton("Huỷ", (dialog, which) -> dialog.dismiss())
                .show();
    }
}