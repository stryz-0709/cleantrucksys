package com.aasolution.cleantrucksys;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import okhttp3.*;

import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {
    FragmentManager fragmentManager;
    private Toast currentToast;
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS) // Increase connection timeout
            .readTimeout(60, TimeUnit.SECONDS)    // Increase read timeout
            .writeTimeout(60, TimeUnit.SECONDS)   // Increase write timeout
            .build();
    private Handler handler = new Handler();
    private Runnable dataFetchRunnable;
    public int valve2, valve3, valve4, valve6, valve8, valve9, valve11, vacuum_pump, oil_pump, water_pump, process;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        valve2 = 0;
        valve3 = 0;
        valve4 = 0;
        valve6 = 0;
        valve8 = 0;
        valve9 = 0;
        valve11 = 0;
        vacuum_pump = 0;
        oil_pump = 0;
        water_pump = 0;
        process = 0;


        permissionRequest();
//        connectToWifi("AAS");
        statusRequest();

        fragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null)
            fragmentManager.beginTransaction().add(R.id.fragment_layout, new HomeFragment(), "HomeFragment").commit();

        fetchComponentStates(); // Fetch initial states
        // Start fetching data every second
        startPeriodicDataFetch();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPeriodicDataFetch(); // Stop handler when activity is destroyed
    }

    public void openFragment(Fragment fragment){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_layout, fragment);
        transaction.commit();
    }

    public void startPeriodicDataFetch() {
        dataFetchRunnable = new Runnable() {
            @Override
            public void run() {
                fetchComponentStates();
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

    public void fetchComponentStates() {
        String url = "http://192.168.1.143/hmi"; // Replace with your ESP32 IP
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("MainActivity", "Request failed: " + e.getMessage());
                // Optionally retry
                handler.postDelayed(() -> fetchComponentStates(), 2000); // Retry after 2 seconds
            }


            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    parseComponentStates(jsonResponse);
                } else {
                    Log.e("MainActivity", "Unexpected response code: " + response.code());
                }
            }
        });
    }

    private void parseComponentStates(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            valve2 = jsonObject.getInt("v2");
            valve3 = jsonObject.getInt("v3");
            valve4 = jsonObject.getInt("v4");
            valve6 = jsonObject.getInt("v6");
            valve8 = jsonObject.getInt("v8");
            valve9 = jsonObject.getInt("v9");
            valve11 = jsonObject.getInt("v11");
            vacuum_pump = jsonObject.getInt("VACUUM_PUMP");
            oil_pump = jsonObject.getInt("OIL_PUMP");
            water_pump = jsonObject.getInt("WATER_PUMP");

            runOnUiThread(() -> {
                Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_layout);
                if (currentFragment instanceof ProcessA1) {
                    ((ProcessA1) currentFragment).updateLights();
                }
                else if (currentFragment instanceof ProcessB1) {
                    ((ProcessB1) currentFragment).updateLights();
                }
            });

        } catch (JSONException e) {
            Log.e("MainActivity", "Failed to parse JSON response", e);
        }
    }

    public static class ESP32Connection {

        // Base URL of ESP32
        private static final String ESP32_BASE_URL = "http://192.168.1.143";
        private final OkHttpClient client;

        // Constructor
        public ESP32Connection() {
            client = new OkHttpClient();
        }

        // Send a POST request with JSON data
        public void sendPostRequest(String endpoint, JSONObject jsonData) {
            String url = ESP32_BASE_URL + endpoint;

            RequestBody body = RequestBody.create(
                    jsonData.toString(),
                    MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    System.out.println("Failed to send POST request: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) {
                    try (ResponseBody responseBody = response.body()) {
                        if (!response.isSuccessful()) {
                            System.err.println("Unexpected response code: " + response.code());
                        } else if (responseBody != null) {
                            String responseString = responseBody.string();
                            System.out.println("POST Response: " + responseString);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    // Send a GET request
    public void sendGetRequest(String endpoint) {
        String url = ESP32_BASE_URL + endpoint;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                System.out.println("Failed to send GET request: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        System.err.println("Unexpected response code: " + response.code());
                    } else if (responseBody != null) {
                        String responseString = responseBody.string();
                        System.out.println("GET Response: " + responseString);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Main method for testing
    public static void main(String[] args) {
        ESP32Connection esp32Connection = new ESP32Connection();

        // Example: Sending a GET request
        esp32Connection.sendGetRequest("/hmi");

        // Example: Sending a POST request
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("command", "start_process");
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("Error creating JSON object: " + e.getMessage());
        }

    }
}
    private void permissionRequest() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }
    }

    public void showToast(String message, int duration) {
        if (currentToast != null) currentToast.cancel();

        final int delay = Toast.LENGTH_SHORT;
        final int iterations = duration / 3500;

        currentToast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);

        final Handler handler = new Handler();
        for (int i = 0; i < iterations; i++) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    currentToast.show();
                }
            }, i * delay);
        }
    }

    public void postOKHTTP(String status) {
        String url = "http://192.168.1.143/post"; // Ensure the IP matches the ESP32

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                status
        );

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (response.isSuccessful() && responseBody != null) {
                        String responseData = responseBody.string();
                        Log.d("HTTP Response", responseData);
                    } else {
                        Log.e("HTTP Error", "Response Code: " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("HTTP Error", "Request failed", e);
            }
        });
    }

    public void getOKHTTP(ResponseCallback callback) {
        String url = "http://192.168.1.143/hmi"; // Ensure the IP matches the ESP32
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    callback.onResponse(responseData);
                } else {
                    callback.onError("Failed with code: " + response.code());
                }
            }
        });
    }

    public interface ResponseCallback {
        void onResponse(String response);
        void onError(String error);
    }

    private void statusRequest() {
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
//                requestInfo();
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(runnable);
    }


    private Fragment getCurrentFragment() {
        fragmentManager = getSupportFragmentManager();
        return fragmentManager.findFragmentById(R.id.fragment_layout);
    }


    public void handleInfo(String jsonResponse){
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            valve2 = jsonObject.getInt("v2");
            valve3 = jsonObject.getInt("v3");
            valve4 = jsonObject.getInt("v4");
            valve6 = jsonObject.getInt("v6");
            valve8 = jsonObject.getInt("v8");
            valve9 = jsonObject.getInt("v9");
            valve11 = jsonObject.getInt("v11");
            vacuum_pump = jsonObject.getInt("VACUUM_PUMP");
            oil_pump = jsonObject.getInt("OIL_PUMP");
            water_pump = jsonObject.getInt("WATER_PUMP");
            process = jsonObject.getInt("process");

            Log.d("JSON","v2: " + String.valueOf(valve2));
            Log.d("JSON", "v3: " + String.valueOf(valve3));
            Log.d("JSON","v4: " + String.valueOf(valve4));
            Log.d("JSON","v6: " + String.valueOf(valve6));
            Log.d("JSON","v8: " + String.valueOf(valve8));
            Log.d("JSON", "v9: " + String.valueOf(valve9));
            Log.d("JSON", "v11: " + String.valueOf(valve11));
            Log.d("JSON","VACUUM_PUMP: " + String.valueOf(vacuum_pump));
            Log.d("JSON", "OIL_PUMP: " + String.valueOf(oil_pump));
            Log.d("JSON","WATER_PUMP: " + String.valueOf(water_pump));

            Fragment currentFragment = getCurrentFragment();
            if (currentFragment instanceof ProcessA1) ((ProcessA1) currentFragment).updateLights();
            else if (currentFragment instanceof ProcessB1) ((ProcessB1) currentFragment).updateLights();
//            else if (currentFragment instanceof ManualFragment) ((ManualFragment) currentFragment).updateLights();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}