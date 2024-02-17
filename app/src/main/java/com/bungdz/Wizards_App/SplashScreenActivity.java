package com.bungdz.Wizards_App;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.bungdz.Wizards_App.networking.ThingsBoardHandle;
import com.bungdz.Wizards_App.models.ThingsBoardInfo;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class SplashScreenActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 5000; // Thời gian trễ, 5000ms = 5 giây

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ThingsBoardHandle.getjwtToken("bungvu50@gmail.com", "123456", new Callback(){
            @Override
            public void onFailure(Request request, IOException e) {
                Log.d("Main Activity", "Error - " + e.getMessage());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    JsonElement rootElement = JsonParser.parseString(response.body().string());
                    if (rootElement.isJsonObject()) {
                        JsonObject jsonObject = rootElement.getAsJsonObject();
                        if (jsonObject.has("token")) {
                            ThingsBoardInfo.JWT_TOKEN = jsonObject.get("token").getAsString();
                        } else {
                            Log.d("Main Activity","Token not found in JSON data.");
                        }
                    }
                } else {
                    Log.d("Main Activity", "Not Success - code: " + response.code());
                }
            }
        });
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_DELAY);
    }
}