package com.aasolution.cleantrucksysbeta;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

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
import android.widget.ToggleButton;

import com.github.anastr.speedviewlib.SpeedView;
import com.github.angads25.toggle.widget.LabeledSwitch;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

public class ManualFragment extends Fragment {
    View mView;
    com.aasolution.cleantrucksysbeta.MainActivity mainActivity;
    RelativeLayout homeButton;
    ToggleButton vacuumPower, waterPower, tankPower,
            robotPower, robotForwardButton, robotBackwardButton, robotStopButton;

    SpeedView vacuumPressure, waterPressure, noPressure;

    LabeledSwitch vacuumButton, waterButton, armButton,
            tankInButton, vacuumOutButton, waterOutButton;

    ImageView robotPowerGradient, vacuumPowerGradient, waterPowerGradient, tankPowerGradient;

    private final Map<String, Boolean> pendingUpdates = new HashMap<>();

    private Handler handler = new Handler();
    private Runnable dataFetchRunnable;
    private final Map<String, Boolean> buttonStates = new HashMap<>();
    private Handler wifiHandler = new Handler();
    private Runnable wifiRunnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        mView = inflater.inflate(R.layout.fragment_manual, container, false);
        mainActivity = (com.aasolution.cleantrucksysbeta.MainActivity) getActivity();

        homeButton = mView.findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.openFragment(new com.aasolution.cleantrucksysbeta.HomeFragment());
            }
        });

        vacuumPressure = mView.findViewById(R.id.vacuum_pressure_gauge);
        waterPressure = mView.findViewById(R.id.water_pressure_gauge);
        noPressure = mView.findViewById(R.id.no_pressure_gauge);

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

        noPressure.getSections().get(0).setColor(Color.parseColor("#CCCCCC"));
        noPressure.getSections().get(1).setColor(Color.parseColor("#CCCCCC"));
        noPressure.getSections().get(2).setColor(Color.parseColor("#CCCCCC"));

        // Initialize buttons
        robotPower = mView.findViewById(R.id.robot_power);
        robotForwardButton = mView.findViewById(R.id.robot_forward_button);
        robotBackwardButton = mView.findViewById(R.id.robot_backward_button);
        robotStopButton = mView.findViewById(R.id.robot_stop_button);
        armButton = mView.findViewById(R.id.arm_button);

        vacuumButton = mView.findViewById(R.id.vacuum_button);
        vacuumPower = mView.findViewById(R.id.vacuum_power);
        waterButton = mView.findViewById(R.id.water_button);
        waterPower = mView.findViewById(R.id.water_power);

        tankInButton = mView.findViewById(R.id.tank_in_button);
        tankPower = mView.findViewById(R.id.tank_power);

        vacuumOutButton = mView.findViewById(R.id.vacuum_out_button);
        waterOutButton = mView.findViewById(R.id.water_out_button);

        robotPowerGradient = mView.findViewById(R.id.robot_power_gradient);
        vacuumPowerGradient = mView.findViewById(R.id.vacuum_power_gradient);
        waterPowerGradient = mView.findViewById(R.id.water_power_gradient);
        tankPowerGradient = mView.findViewById(R.id.tank_power_gradient);

        robotPowerGradient.setVisibility(GONE);
        vacuumPowerGradient.setVisibility(GONE);
        waterPowerGradient.setVisibility(GONE);
        tankPowerGradient.setVisibility(GONE);

        robotPower.setEnabled(false);
        vacuumPower.setEnabled(false);
        tankPower.setEnabled(false);
        waterPower.setEnabled(false);


        Button[] group = {robotForwardButton, robotBackwardButton, robotStopButton};
        String[] groupKeys = {"robot_forward", "robot_backward", "robot_stop"};

        // Set up button logic
        for (Button button : group) setupButton(button, group, groupKeys);


        ///vacuum_in for both vacuum_out and water_out

        setupLabeledSwitch(vacuumButton, "vacuum_power", null, null);
        setupLabeledSwitch(waterButton, "water_power", null, null);

        setupLabeledSwitch(armButton, "arm_up", "arm_down", null);
        setupLabeledSwitch(tankInButton, "vacuum_in", "vacuum_out", "water_out");
        setupLabeledSwitch(vacuumOutButton, "vacuum_out", "vacuum_in", "water_out");
        setupLabeledSwitch(waterOutButton, "water_out","vacuum_in", "vacuum_out");

        dataFetch();
        startPeriodicDataFetch();
        return mView;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPeriodicDataFetch();
    }

    public void startPeriodicDataFetch() {
        dataFetchRunnable = new Runnable() {
            @Override
            public void run() {
                dataFetch();
                handler.postDelayed(this, 2000); // Run every 2 seconds
            }
        };
        handler.post(dataFetchRunnable);
    }

    public void stopPeriodicDataFetch() {
        if (dataFetchRunnable != null) {
            handler.removeCallbacks(dataFetchRunnable);
        }
    }

    private void setupButton(Button button, Button[] groupButtons, String[] groupKeys) {
        button.setOnClickListener(v -> {
            updateButtonLogic(button, groupButtons, groupKeys, null);
        });
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

    private void updateButtonLogic(Button clickedButton, Button[] groupButtons, String[] groupKeys, JSONObject jsonObject) {
        try {
            JSONObject jsonData = new JSONObject();
            boolean isStopButton = true;
            String groupIntentKey = String.join("_", groupKeys);

            if (jsonObject == null) {
                pendingUpdates.put(groupIntentKey, true);
            } else if (pendingUpdates.containsKey(groupIntentKey)) {
                Log.d("ManualFragment", "Ignoring outdated group response for " + groupIntentKey);
                return;
            }

            for (int i = 0; i < groupButtons.length; i++) {
                Button groupButton = groupButtons[i];
                String groupKey = groupKeys[i];

                boolean state;
                if (jsonObject != null) {
                    // Update state based on JSON response
                    state = jsonObject.has(groupKey) && jsonObject.getInt(groupKey) == 1;
                } else {
                    // Update state based on button click
                    state = (groupButton == clickedButton);
                }

                if (state) {
                    // Activate this button
                    groupButton.setSelected(true);
                    ((ToggleButton) groupButton).setChecked(true);
                    buttonStates.put(groupKey, true);

                    if (!groupKey.endsWith("_stop")) {
                        isStopButton = false;
                    }

                    // If it's a stop button, deactivate others in the group
                    if (groupKey.endsWith("_stop")) {
                        for (int j = 0; j < groupButtons.length; j++) {
                            if (j != i) {
                                String otherKey = groupKeys[j];
                                Button otherButton = groupButtons[j];

                                // Deactivate the other buttons
                                otherButton.setSelected(false);
                                ((ToggleButton) otherButton).setChecked(false);
                                buttonStates.put(otherKey, false);
                            }
                        }
                    }
                } else {
                    // Deactivate this button
                    groupButton.setSelected(false);
                    ((ToggleButton) groupButton).setChecked(false);
                    buttonStates.put(groupKey, false);
                }
            }

            if (jsonObject != null && isStopButton) {
                for (int i = 0; i < groupButtons.length; i++) {
                    String groupKey = groupKeys[i];
                    if (groupKey.endsWith("_stop")) {
                        Button stopButton = groupButtons[i];
                        stopButton.setSelected(true);
                        ((ToggleButton) stopButton).setChecked(true);
                        buttonStates.put(groupKey, true);
                        Log.d("ManualFragment", "Activated stop button: " + groupKey);
                    }
                }
                pendingUpdates.remove(groupIntentKey);
            }

            if (jsonObject == null) {
                // If this is a button click, prepare JSON to send to the server
                for (String groupKey : groupKeys) {
                    if (!groupKey.endsWith("_stop")) {
                        jsonData.put(groupKey, Boolean.TRUE.equals(buttonStates.get(groupKey)) ? 1 : 0);
                    }
                }

                // Send updated state to the server
                pendingUpdates.remove(groupIntentKey); // remove after sending
                mainActivity.postOKHTTP(jsonData.toString());
            }
        } catch (JSONException e) {
            Log.e("ManualFragment", "Error updating button states", e);
        }
    }


    private void updateLights(JSONObject jsonObject) {
        try {
            robotPower.setChecked(jsonObject.getInt("robot_forward") == 1 || jsonObject.getInt("robot_backward") == 1 || jsonObject.getInt("arm_up") == 1);
            robotPowerGradient.setVisibility((jsonObject.getInt("robot_forward") == 1 || jsonObject.getInt("robot_backward") == 1 || jsonObject.getInt("arm_up") == 1)? VISIBLE: GONE);
            vacuumPower.setChecked(jsonObject.getInt("vacuum_power") == 1);
            vacuumPowerGradient.setVisibility(jsonObject.getInt("vacuum_power") == 1? VISIBLE: GONE);
            waterPower.setChecked(jsonObject.getInt("water_power") == 1);
            waterPowerGradient.setVisibility(jsonObject.getInt("water_power") == 1? VISIBLE: GONE);
            tankPower.setChecked(jsonObject.getInt("vacuum_in") == 1 || jsonObject.getInt("vacuum_out") == 1 || jsonObject.getInt("water_out") == 1);
            tankPowerGradient.setVisibility((jsonObject.getInt("vacuum_in") == 1 || jsonObject.getInt("vacuum_out") == 1 || jsonObject.getInt("water_out") == 1)? VISIBLE: GONE);
        } catch (JSONException e) {
            Log.e("ProcessB1", "Error updating lights", e);
        }
    }


    private void dataFetch() {
        mainActivity.getOKHTTP(new com.aasolution.cleantrucksysbeta.MainActivity.ResponseCallback() {
            @Override
            public void onResponse(String response) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Log.d("ManualFragment", "Received JSON: " + jsonObject.toString());

                        updateLights(jsonObject);

                        updateButtonLogic(null, new Button[]{robotForwardButton, robotBackwardButton, robotStopButton},
                                new String[]{"robot_forward", "robot_backward", "robot_stop"}, jsonObject);

                        // Update labeled switches
                        updateLabeledSwitch(vacuumButton, "vacuum_power", null, jsonObject);
                        updateLabeledSwitch(waterButton, "water_power", null, jsonObject);

                        updateLabeledSwitch(armButton, "arm_up", "arm_down", jsonObject);

                        updateLabeledSwitch(tankInButton, "vacuum_in", null, jsonObject);
                        updateLabeledSwitch(vacuumOutButton, "vacuum_out", null,  jsonObject);
                        updateLabeledSwitch(waterOutButton, "water_out", null, jsonObject);


                        // Update gauges
                        float vacuumPressureValue = (float) jsonObject.getDouble("vacuum_pressure");
                        float waterPressureValue = (float) jsonObject.getDouble("water_pressure");

                        vacuumPressure.speedTo(vacuumPressureValue);
                        waterPressure.speedTo(waterPressureValue);
                    } catch (JSONException e) {
                        Log.e("ManualFragment", "Error parsing JSON response", e);
                    }
                });
            }

            @Override
            public void onError(String error) {
                if (!isAdded()) return;
                Log.e("ManualFragment", "Network error: " + error);
//                requireActivity().runOnUiThread(() ->
//                        Toast.makeText(requireContext(), "Failed to fetch data. Check connection.", Toast.LENGTH_LONG).show()
//                );
            }
        });
        // Only declare and post wifiRunnable once
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
}
