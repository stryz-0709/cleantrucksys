package com.aasolution.cleantrucksys;

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
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.github.anastr.speedviewlib.SpeedView;
import com.github.anastr.speedviewlib.components.note.Note;
import com.github.angads25.toggle.widget.LabeledSwitch;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ManualFragment extends Fragment {
    View mView;
    MainActivity mainActivity;
    RelativeLayout homeButton;
    Button vacuumInButton, vacuumOutButton, vacuumStopButton,
            tankInButton, tankOutButton, tankStopButton,
            waterInButton, waterOutButton, waterStopButton,
            robotForwardButton, robotBackwardButton, robotStopButton,
            armUpButton, armDownButton,
            pipeUpButton, pipeDownButton;

    SpeedView vacuumPressure, waterPressure;

    LabeledSwitch vacuum_pumpButton, water_pumpButton;

    private Handler handler = new Handler();
    private Runnable dataFetchRunnable;
    private final Map<String, Boolean> buttonStates = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        mView = inflater.inflate(R.layout.fragment_manual, container, false);
        mainActivity = (MainActivity) getActivity();

        homeButton = mView.findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.openFragment(new HomeFragment());
            }
        });

        vacuumPressure = mView.findViewById(R.id.vacuum_pressure_gauge);
        waterPressure = mView.findViewById(R.id.water_pressure_gauge);

        vacuumPressure.getSections().get(0).setColor(Color.parseColor("#CCCCCC"));
        vacuumPressure.getSections().get(1).setColor(Color.parseColor("#CCCCCC"));
        vacuumPressure.getSections().get(2).setColor(Color.parseColor("#CCCCCC"));

        vacuumPressure.speedTo(0);
        waterPressure.speedTo(0);

        waterPressure.getSections().get(0).setColor(Color.parseColor("#CCCCCC"));
        waterPressure.getSections().get(1).setColor(Color.parseColor("#CCCCCC"));
        waterPressure.getSections().get(2).setColor(Color.parseColor("#CCCCCC"));

        // Initialize buttons
        robotForwardButton = mView.findViewById(R.id.robot_forward_button);
        robotBackwardButton = mView.findViewById(R.id.robot_backward_button);
        robotStopButton = mView.findViewById(R.id.robot_stop_button);

        vacuumInButton = mView.findViewById(R.id.vacuum_in_button);
        vacuumOutButton = mView.findViewById(R.id.vacuum_out_button);
        vacuumStopButton = mView.findViewById(R.id.vacuum_stop_button);

        waterInButton = mView.findViewById(R.id.water_in_button);
        waterOutButton = mView.findViewById(R.id.water_out_button);
        waterStopButton = mView.findViewById(R.id.water_stop_button);

        tankInButton = mView.findViewById(R.id.tank_in_button);
        tankOutButton = mView.findViewById(R.id.tank_out_button);
        tankStopButton = mView.findViewById(R.id.tank_stop_button);

        armUpButton = mView.findViewById(R.id.arm_up_button);
        armDownButton = mView.findViewById(R.id.arm_down_button);

        pipeUpButton = mView.findViewById(R.id.pipe_up_button);
        pipeDownButton = mView.findViewById(R.id.pipe_down_button);

        vacuum_pumpButton = mView.findViewById(R.id.vacuum_button);
        water_pumpButton = mView.findViewById(R.id.water_button);

        Button[] group1 = {vacuumInButton, vacuumOutButton, vacuumStopButton};
        String[] group1Keys = {"vacuum_in", "vacuum_out", "vacuum_stop"};

        Button[] group2 = {waterInButton, waterOutButton, waterStopButton};
        String[] group2Keys = {"water_in", "water_out", "water_stop"};

        Button[] group3 = {tankInButton, tankOutButton, tankStopButton};
        String[] group3Keys = {"tank_in", "tank_out", "tank_stop"};

        Button[] group4 = {robotForwardButton, robotBackwardButton, robotStopButton};
        String[] group4Keys = {"robot_forward", "robot_backward", "robot_stop"};

        Button[] group5 = {armUpButton, armDownButton};
        String[] group5Keys = {"arm_up", "arm_down"};

        Button[] group6 = {pipeUpButton, pipeDownButton};
        String[] group6Keys = {"pipe_up", "pipe_down"};

        // Set up button logic
        for (Button button : group1) setupButton(button, group1, group1Keys);
        for (Button button : group2) setupButton(button, group2, group2Keys);
        for (Button button : group3) setupButton(button, group3, group3Keys);
        for (Button button : group4) setupButton(button, group4, group4Keys);
        for (Button button : group5) setupButton(button, group5, group5Keys);
        for (Button button : group6) setupButton(button, group6, group6Keys);

        setupLabeledSwitch(vacuum_pumpButton, "vacuum_power");
        setupLabeledSwitch(water_pumpButton, "water_power");

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
                handler.postDelayed(this, 1000); // Run every 1 seconds
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

    private void setupLabeledSwitch(LabeledSwitch switchButton, String key) {
        switchButton.setOnClickListener(v -> {
            switchButton.setEnabled(false);
            sendStateChangeToServer(switchButton, key);
        });
    }

    private void sendStateChangeToServer(LabeledSwitch switchButton, String key) {
        try {
            // Prepare the JSON data for the server request
            boolean newState = !buttonStates.getOrDefault(key, false); // Invert the local state for the request
            buttonStates.put(key, newState); // Save the intended state locally

            JSONObject jsonData = new JSONObject();
            jsonData.put(key, newState ? 1 : 0);

            // Send the request to the server (simulated here)
            mainActivity.postOKHTTP(jsonData.toString());
        } catch (JSONException e) {
            Log.e("ManualFragment", "Error preparing JSON for key: " + key, e);
            // Re-enable the switch in case of error
            switchButton.setEnabled(true);
        }
    }

    private void updateLabeledSwitch(LabeledSwitch switchButton, String key, JSONObject jsonObject) {
        try {
            if (jsonObject != null) {
                boolean newState = jsonObject.has(key) && jsonObject.getInt(key) == 1;

                switchButton.setOn(newState);
                switchButton.setColorOn(Color.parseColor(newState ? "#4CAF50" : "#F00000"));

                buttonStates.put(key, newState);
                switchButton.setEnabled(true);
            }
        } catch (JSONException e) {
            Log.e("ManualFragment", "Error updating LabeledSwitch state for key: " + key, e);
            switchButton.setEnabled(true);
        }
    }

    private void updateButtonLogic(Button clickedButton, Button[] groupButtons, String[] groupKeys, JSONObject jsonObject) {
        try {
            JSONObject jsonData = new JSONObject();
            boolean isStopButton = true;

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
            }

            if (jsonObject == null) {
                // If this is a button click, prepare JSON to send to the server
                for (String groupKey : groupKeys) {
                    if (!groupKey.endsWith("_stop")) {
                        jsonData.put(groupKey, Boolean.TRUE.equals(buttonStates.get(groupKey)) ? 1 : 0);
                    }
                }

                // Send updated state to the server
                mainActivity.postOKHTTP(jsonData.toString());
            }
        } catch (JSONException e) {
            Log.e("ManualFragment", "Error updating button states", e);
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
                        Log.d("ManualFragment", "Received JSON: " + jsonObject.toString());

                        // Update button states for each group
                        updateButtonLogic(null, new Button[]{vacuumInButton, vacuumOutButton, vacuumStopButton},
                                new String[]{"vacuum_in", "vacuum_out", "vacuum_stop"}, jsonObject);

                        updateButtonLogic(null, new Button[]{waterInButton, waterOutButton, waterStopButton},
                                new String[]{"water_in", "water_out", "water_stop"}, jsonObject);

                        updateButtonLogic(null, new Button[]{tankInButton, tankOutButton, tankStopButton},
                                new String[]{"tank_in", "tank_out", "tank_stop"}, jsonObject);

                        updateButtonLogic(null, new Button[]{robotForwardButton, robotBackwardButton, robotStopButton},
                                new String[]{"robot_forward", "robot_backward", "robot_stop"}, jsonObject);

                        updateButtonLogic(null, new Button[]{armUpButton, armDownButton},
                                new String[]{"arm_up", "arm_down"}, jsonObject);

                        // Update labeled switches
                        updateLabeledSwitch(vacuum_pumpButton, "vacuum_power", jsonObject);
                        updateLabeledSwitch(water_pumpButton, "water_power", jsonObject);

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
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Failed to fetch data. Check connection.", Toast.LENGTH_LONG).show()
                );
            }
        });
    }
}
