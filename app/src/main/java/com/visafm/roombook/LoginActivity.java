package com.visafm.roombook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.visafm.roombook.common.Common;
import com.visafm.roombook.data.factory.RepoFactory;
import com.visafm.roombook.data.remote.network.RetrofitClient;
import com.visafm.roombook.data.repository.SharedPreferencesRepository;
import com.visafm.roombook.data.repository.UserRepository;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    SharedPreferencesRepository sharedPref;
    UserRepository userRepository;
    Button btnLogin;
    EditText etUsername;
    EditText etPassword;
    EditText etServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etServer = findViewById(R.id.etServer);
        btnLogin = findViewById(R.id.btnLogin);

        // initialize repositories
        sharedPref = RepoFactory.INSTANCE.createSharedPref(this);



        if (!Common.getSharedPreferences(getApplicationContext(), "previousUsername").equals("NA")) {
            etUsername.setText(sharedPref.getString(Common.KEY_USERNAME, ""));
            etPassword.setText(sharedPref.getString(Common.KEY_PASSWORD, ""));
            etServer.setText(sharedPref.getString(Common.KEY_BASE_URL, ""));
        }

        btnLogin.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (compositeDisposable == null || compositeDisposable.isDisposed())
            compositeDisposable = new CompositeDisposable();
    }

    @Override
    protected void onStop() {
        if (!compositeDisposable.isDisposed())
            compositeDisposable.dispose();

        super.onStop();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                checkValidation();
                break;
        }
    }

    private void checkValidation() {
        if (Common.isValidEdittext(etUsername, 0) && Common.isValidEdittext(etPassword, 0) && Common.isValidEdittext(etServer, 0)) {
            // save the url
            String baseUrl = etServer.getText().toString().trim() + "/api/";
            sharedPref.putString(Common.KEY_BASE_URL, baseUrl);

            // initialized retrofit
            RetrofitClient.INSTANCE.init(baseUrl, "");
            userRepository = RepoFactory.INSTANCE.getUserRemoteRepository();

            // login
            login();
        }
    }

    private void login() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        Common.startProgressDialouge(this, "");
        compositeDisposable.add(
                userRepository.login(username, password)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(response -> {
                            Common.stopProgressDialouge();

                            if (response.getResultCode().equals("SUCCESS")) {
                                String sessionUserID = response.getResultObject().getSessionUserID();
                                String sessionApplicationID = response.getResultObject().getSessionApplicationID();

                                // Save session user ID and application ID to SharedPreferences
                                sharedPref.putString(Common.KEY_USER_SESSION, sessionUserID);
                                sharedPref.putString(Common.KEY_APPLICATION_ID, sessionApplicationID);
                                sharedPref.putString(Common.KEY_USERNAME, username);
                                sharedPref.putString(Common.KEY_PASSWORD, password);


                                // Start Dashboard activity
                                Intent intent = new Intent(this, Dashboard.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                // Show alert with result message
                                Common.showAlert(this, response.getResultMessage());
                            }
                        }, error -> {
                            Common.stopProgressDialouge();
                        })
        );
    }
}

