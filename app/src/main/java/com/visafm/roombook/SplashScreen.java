package com.visafm.roombook;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.visafm.roombook.common.Common;
import com.visafm.roombook.data.factory.RepoFactory;
import com.visafm.roombook.data.remote.network.RetrofitClient;
import com.visafm.roombook.data.repository.SharedPreferencesRepository;

public class SplashScreen extends AppCompatActivity {
    SharedPreferencesRepository sharedPref;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_splashscreen);
        getSupportActionBar().hide();

        // init repositories
        sharedPref = RepoFactory.INSTANCE.createSharedPref(this);

        // initialize retrofit
        RetrofitClient.INSTANCE.init(
                sharedPref.getString(Common.KEY_BASE_URL, ""),
                sharedPref.getString(Common.KEY_USER_SESSION, "")
        );

        handler.postDelayed(new Runnable() {
            public void run() {
                openNextActivity();
            }
        }, 3000);
    }

    private void openNextActivity() {
        Intent i;
        String userSession = sharedPref.getString(Common.KEY_USER_SESSION, "");
        if (!userSession.equals("NA") && !userSession.isEmpty()) {
            // user is logged in, go to dashboard
            i = new Intent(SplashScreen.this, Dashboard.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        } else {
            // user is logged out, go to login
            i = new Intent(SplashScreen.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        startActivity(i);
    }
}
