package com.election.poll_monitor_system.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.election.poll_monitor_system.Common.Constant;
import com.election.poll_monitor_system.Common.NetworkAvailable;
import com.election.poll_monitor_system.Common.Session;
import com.election.poll_monitor_system.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private static ActionBar actionBar;
    private static Button next_btn;
    private static EditText password;
    private static EditText phone_number;

    private RequestQueue mRequestQueue;
    private JsonObjectRequest mJsonRequest;
    private static String URL = "LogInByMobileno";
//    public static String result, message, otp,
//            presidingOfficierId,
//            officierName,
//            title,
//            electionACID,
//            electionPCID,
//            boothId,
//            electors,
//            IsFirstLogin,
//            is8PMEnabled;

    public static String result, message,
            presidingOfficierId,
            officierName,
            title,
            titleName,
            mobileNo,
            email,
            isActive,
            mobilePIN,
            isFirstLoggedIn,
            districtId,
            boothId,
            assemblyConstituencyId;

    private static Session session;

    Date loginDate = Calendar.getInstance().getTime();

    private static int[] networkTypes = {
            ConnectivityManager.TYPE_MOBILE,
            ConnectivityManager.TYPE_WIFI
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Hide Action Bar
        actionBar = getSupportActionBar();
        actionBar.hide();

        init();

        session = new Session(this);

        next_btn.setOnClickListener(v -> {
            if (phone_number.getText().toString().isEmpty() || phone_number.getText().toString().length() != 10) {
                Toast.makeText(this, "Enter your valid mobile number", Toast.LENGTH_SHORT).show();
            } else {
                if (NetworkAvailable.isNetworkAvailable(this, networkTypes)) {
                    otp_login();
                } else {
                    Toast.makeText(this, "Check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void otp_login() {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.show();

        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("MobileNo", phone_number.getText().toString());
        params.put("MobilePin", password.getText().toString());

        mRequestQueue = Volley.newRequestQueue(this);
        mJsonRequest = new JsonObjectRequest(Request.Method.POST, Constant.API_CONSTANT + URL, new JSONObject(params), response -> {
            Log.e("response", response.toString());
            try {
                result = response.getString("result");
                message = response.getString("message");
                if (result.equals("0")) {
                    pDialog.dismiss();
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                } else {
                    JSONObject data = response.getJSONObject("data");
                    presidingOfficierId = data.getString("presidingOfficierId");
                    officierName = data.getString("officierName");
                    title = data.getString("title");
                    titleName = data.getString("titleName");
                    mobileNo = data.getString("mobileNo");
                    email = data.getString("email");
                    isActive = data.getString("isActive");
                    mobilePIN = data.getString("mobilePIN");
                    isFirstLoggedIn = data.getString("isFirstLoggedIn");
                    districtId = data.getString("districtId");
                    boothId = data.getString("boothId");
                    assemblyConstituencyId = data.getString("assemblyConstituencyId");
                    pDialog.dismiss();

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    String sessionDate = sdf.format(loginDate);

                    session.createProfileManageSession(
                            presidingOfficierId, officierName,
                            title,
                            titleName,
                            mobileNo,
                            email,
                            isActive,
                            isFirstLoggedIn,
                            districtId,
                            boothId,
                            assemblyConstituencyId,
                            sessionDate);

                    Intent intent;
                    if (isFirstLoggedIn.equals("true")) {
                        intent = new Intent(this, ChangePasswordActivity.class);
                    } else {
                        intent = new Intent(this, MainActivity.class);
                    }
                    startActivity(intent);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("LOGIN_ACTIVITY", "Error :" + error.getMessage()));
        mRequestQueue.add(mJsonRequest);
    }

   /* @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case 1:
                if (grantResults.length > 0) {
                    boolean RECEIVE_SMS = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean READ_SMS = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                }
        }
    }*/

    private void init() {
        next_btn = findViewById(R.id.next_btn);
        password = findViewById(R.id.password);
        phone_number = findViewById(R.id.phone_number);
    }

}
