package com.visafm.roombook;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.visafm.roombook.common.BaseClass;
import com.visafm.roombook.common.Common;
import com.visafm.roombook.common.HttpConnection;

import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, BaseClass {

    Button btnLogin;
    BaseClass delegate = this;
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

        if (Common.getSharedPreferences(getApplicationContext(), "previousUsername").equals("NA")) {
            //Local
//          etUsername.setText("Wolfgang Kaiser");
//            etPassword.setText("P:M3n4VUm(r^>Z");
//            etServer.setText("http://aab8259f.ngrok.io");

//            etUsername.setText("Hemali Parekh");
//            etServer.setText("http://8c786713.ngrok.io");
//            etPassword.setText("5OpH+SyWn5CFg3");

//            Live
//            etUsername.setText("Wolfgang Kaiser");
//            etPassword.setText("Nrn{_zgY1@3siSZUv");
//            etServer.setText(" https://mobile.visa-fm-raumbuch.de/");
        } else {
            etUsername.setText(Common.getSharedPreferences(getApplicationContext(), "previousUsername"));
            etPassword.setText(Common.getSharedPreferences(getApplicationContext(), "previousPassword"));
            etServer.setText(Common.getSharedPreferences(getApplicationContext(), "previousServer"));
        }

        btnLogin.setOnClickListener(this);
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
            Common.SERVER_URL = etServer.getText().toString().trim() + "/api/";
            getLogin();
        }
        /*Common.SERVER_URL = "http://5.175.13.128:90/api/";
        getLogin();*/
    }

    private void getLogin() {
        try {
            HashMap<String, String> postDataParams = new HashMap<>();
            postDataParams.put("username", etUsername.getText().toString().trim());
            postDataParams.put("password", etPassword.getText().toString().trim());
            HttpConnection httpConnection = new HttpConnection(delegate, LoginActivity.this);
            httpConnection.setRequestedfor("login");
            httpConnection.setIsloading(true);
            httpConnection.setPostDataParams(postDataParams);
            httpConnection.setUrl("Account/ApplicantLogin");
            httpConnection.execute("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void httpResponse(String response, String requestedFor) throws Exception {
        Common.stopProgressDialouge(requestedFor);
        if (requestedFor.equals("login")) {
            JSONObject jObj = new JSONObject(response);
            if (jObj.getString("ResultCode").equals("SUCCESS")) {
                String strTypes = jObj.getString("ResultObject");
                JSONObject jObjt = new JSONObject(strTypes);
                Common.USER_SESSION = jObjt.getString("SessionUserID");
                Common.APPLICATIONID = jObjt.getString("SessionApplicationID");
                Common.setSharedPreferences(getApplicationContext(), "userSession", Common.USER_SESSION);
                Common.setSharedPreferences(getApplicationContext(), "serverUrl", Common.SERVER_URL);
                Common.setSharedPreferences(getApplicationContext(), "previousUsername", etUsername.getText().toString().trim());
                Common.setSharedPreferences(getApplicationContext(), "previousPassword", etPassword.getText().toString().trim());
                Common.setSharedPreferences(getApplicationContext(), "previousServer", etServer.getText().toString().trim());
                Intent i = new Intent(LoginActivity.this, Dashboard.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            } else {
                Common.showAlert(LoginActivity.this, jObj.getString("ResultMessage"));
            }
        }
    }

    @Override
    public void httpFailure(String response, String requestedFor) throws Exception {
    }

}

