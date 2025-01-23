package com.aasolution.cleantrucksysbeta;

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

import java.util.Collections;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {
    FragmentManager fragmentManager;
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS) // Increase connection timeout
            .readTimeout(60, TimeUnit.SECONDS)    // Increase read timeout
            .writeTimeout(60, TimeUnit.SECONDS)   // Increase write timeout
            .build();

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

        permissionRequest();

        fragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null)
            fragmentManager.beginTransaction().add(R.id.fragment_layout, new HomeFragment(), "HomeFragment").commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void openFragment(Fragment fragment){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_layout, fragment);
        transaction.commit();
    }

    private void permissionRequest() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }
    }


    public void postOKHTTP(String status) {
        String url = "http://192.168.1.149/post"; // Ensure the IP matches the ESP32
//        String url = "http://172.20.10.3/post"; // Ensure the IP matches the ESP32


        Log.d("HTTP Request", "Payload: " + status);

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
        String url = "http://192.168.1.149/hmi"; // Replace with your server IP and endpoint
//        String url = "http://172.20.10.3/hmi"; // Replace with your server IP and endpoint


        // Build OkHttpClient with timeout settings and HTTP/1.1 support
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS) // Connection timeout
                .readTimeout(30, TimeUnit.SECONDS)    // Read timeout
                .writeTimeout(30, TimeUnit.SECONDS)   // Write timeout
                .protocols(Collections.singletonList(Protocol.HTTP_1_1)) // Force HTTP/1.1
                .build();

        // Create the request
        Request request = new Request.Builder()
                .url(url)
                .build();

        // Enqueue the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Log the error and pass it to the callback
                Log.e("getOKHTTP", "Request failed: " + e.getMessage(), e);
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Check if the response is successful
                if (response.isSuccessful()) {
                    try (ResponseBody body = response.body()) {
                        if (body != null) {
                            String responseData = body.string();
                            Log.d("getOKHTTP", "Response body: " + responseData);
                            callback.onResponse(responseData);
                        } else {
                            Log.e("getOKHTTP", "Response body is null");
                            callback.onError("Response body is null");
                        }
                    }
                } else {
                    // Log the response code and message
                    Log.e("getOKHTTP", "Failed with code: " + response.code() +
                            ", message: " + response.message());
                    callback.onError("Failed with code: " + response.code() +
                            ", message: " + response.message());
                }
            }
        });
    }

    public interface ResponseCallback {
        void onResponse(String response);
        void onError(String error);
    }


    private Fragment getCurrentFragment() {
        fragmentManager = getSupportFragmentManager();
        return fragmentManager.findFragmentById(R.id.fragment_layout);
    }

}