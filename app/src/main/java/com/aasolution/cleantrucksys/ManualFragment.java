package com.aasolution.cleantrucksys;

import androidx.fragment.app.Fragment;

import android.content.pm.ActivityInfo;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ManualFragment extends Fragment {
    View mView;
    MainActivity mainActivity;
    RelativeLayout homeButton;
    Button valve1Button, valve3Button, valve4Button, valve6Button, valve8Button, valve9Button,
            valve11Button, vacuum_pumpButton, water_pumpButton, oil_pumpButton;

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
        // Initialize buttons
        valve1Button = mView.findViewById(R.id.valve1_button);
        valve3Button = mView.findViewById(R.id.valve3_button);
        valve4Button = mView.findViewById(R.id.valve4_button);
        valve6Button = mView.findViewById(R.id.valve6_button);
        valve8Button = mView.findViewById(R.id.valve8_button);
        valve9Button = mView.findViewById(R.id.valve9_button);
        valve11Button = mView.findViewById(R.id.valve11_button);
        vacuum_pumpButton = mView.findViewById(R.id.vacuum_button);
        oil_pumpButton = mView.findViewById(R.id.oil_button);
        water_pumpButton = mView.findViewById(R.id.water_button);

        // Set up button logic
        setupButton(valve1Button, "v1");
        setupButton(valve3Button, "v3");
        setupButton(valve4Button, "v4");
        setupButton(valve6Button, "v6");
        setupButton(valve8Button, "v8");
        setupButton(valve9Button, "v9");
        setupButton(valve11Button, "v11");
        setupButton(vacuum_pumpButton, "VACUUM_PUMP");
        setupButton(water_pumpButton, "WATER_PUMP");
        setupButton(oil_pumpButton, "OIL_PUMP");

        // Fetch initial and current states
        fetchInitialState();
        fetchCurrentState();
        startPeriodicDataFetch();
        return mView;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPeriodicDataFetch(); // Stop handler when activity is destroyed
    }

    public void startPeriodicDataFetch() {
        dataFetchRunnable = new Runnable() {
            @Override
            public void run() {
                fetchCurrentState();
                handler.postDelayed(this, 5000); // Run every 5 seconds
            }
        };
        handler.post(dataFetchRunnable);
    }

    public void stopPeriodicDataFetch() {
        if (dataFetchRunnable != null) {
            handler.removeCallbacks(dataFetchRunnable);
        }
    }

    private void setupButton(Button button, String key) {
        button.setOnClickListener(v -> {
            try {
                // Toggle the state locally
                boolean newState = !button.isSelected();

                // Update the local state map
                buttonStates.put(key, newState);

                // Update UI immediately
                button.setSelected(newState);
                ((ToggleButton) button).setChecked(newState);

                // Send updated state to ESP32
                JSONObject jsonData = new JSONObject();
                jsonData.put(key, newState ? 1 : 0);
                mainActivity.postOKHTTP(jsonData.toString());
            } catch (JSONException e) {
                Log.e("ManualFragment", "Failed to create JSON object for key: " + key, e);
            }
        });
    }


    private void updateButtonStatesFromESP(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);

            // Iterate over the keys in the JSON object
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                if (jsonObject.has(key)) {
                    boolean newState = jsonObject.getInt(key) == 1;

                    // Update local state map
                    buttonStates.put(key, newState);

                    // Find the corresponding button and update its state
                    Button button = findButtonByKey(key);
                    if (button != null) {
                        button.setSelected(newState);
                        ((ToggleButton) button).setChecked(newState);
                    }
                }
            }
        } catch (JSONException e) {
            Log.e("ManualFragment", "Error parsing JSON response", e);
            Toast.makeText(requireContext(), "Invalid response from ESP32", Toast.LENGTH_SHORT).show();
        }
    }

    // Helper method to find a button by its key
    private Button findButtonByKey(String key) {
        switch (key) {
            case "v1":
                return valve1Button;
            case "v3":
                return valve3Button;
            case "v4":
                return valve4Button;
            case "v6":
                return valve6Button;
            case "v8":
                return valve8Button;
            case "v9":
                return valve9Button;
            case "v11":
                return valve11Button;
            case "VACUUM_PUMP":
                return vacuum_pumpButton;
            case "WATER_PUMP":
                return water_pumpButton;
            case "OIL_PUMP":
                return oil_pumpButton;
            default:
                return null;
        }
    }

    private void fetchInitialState() {
        mainActivity.getOKHTTP(new MainActivity.ResponseCallback() {
            @Override
            public void onResponse(String response) {
                if (!isAdded()) return; // Ensure fragment is still attached
                requireActivity().runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        // Update each button independently
                        for (String key : buttonStates.keySet()) {
                            if (jsonObject.has(key)) {
                                boolean state = jsonObject.optInt(key, 0) == 1; // Default to 0 if key is missing
                                buttonStates.put(key, state);

                                // Find the corresponding button and update its state
                                Button button = findButtonByKey(key);
                                if (button != null) {
                                    button.setSelected(state);
                                    ((ToggleButton) button).setChecked(state);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        Log.e("ManualFragment", "Error parsing JSON response", e);
                    }
                });
            }

            @Override
            public void onError(String error) {
                if (!isAdded()) return; // Ensure fragment is still attached
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Failed to fetch initial state. Check connection.", Toast.LENGTH_LONG).show()
                );
            }
        });
    }


    private void fetchCurrentState() {
        mainActivity.getOKHTTP(new MainActivity.ResponseCallback() {
            @Override
            public void onResponse(String response) {
                if (!isAdded()) return; // Ensure fragment is still attached
                requireActivity().runOnUiThread(() -> {
                    try {
                        updateButtonStatesFromESP(response);
                    } catch (Exception e) {
                        Log.e("ManualFragment", "Error updating states from ESP32 response", e);
                        Toast.makeText(requireContext(), "Error syncing with ESP32", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                if (!isAdded()) return; // Ensure fragment is still attached
//                requireActivity().runOnUiThread(() ->
//                        Toast.makeText(requireContext(), "Failed to fetch data. Check connection.", Toast.LENGTH_LONG).show()
//                );
            }
        });
    }


    private void updateButtonState(Button button, String key, JSONObject jsonObject) {
        try {
            if (jsonObject.has(key)) {
                boolean newState = jsonObject.getInt(key) == 1;

                // Update the button state only if it has changed
                if (button.isSelected() != newState) {
                    button.setSelected(newState);
                    ((ToggleButton) button).setChecked(newState);
                }
            }
        } catch (JSONException e) {
            Log.e("ManualFragment", "Error updating button state for key: " + key, e);
        }
    }
}