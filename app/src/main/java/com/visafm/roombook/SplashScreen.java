package com.visafm.roombook;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.visafm.roombook.common.Common;

public class SplashScreen extends AppCompatActivity {
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_splashscreen);
        getSupportActionBar().hide();

        handler.postDelayed(new Runnable() {
            public void run() {
                openNextActivity();
            }
        }, 3000);
    }

    private void openNextActivity() {
        Intent i;
        if (!Common.getSharedPreferences(getApplicationContext(), "userSession").equals("NA")) {
            Common.USER_SESSION = Common.getSharedPreferences(getApplicationContext(), "userSession");
            Common.SERVER_URL = Common.getSharedPreferences(getApplicationContext(), "serverUrl");
            i = new Intent(SplashScreen.this, Dashboard.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        } else {
            i = new Intent(SplashScreen.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        startActivity(i);
    }
}
