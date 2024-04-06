package com.election.PMS.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.election.PMS.Common.Constant;
import com.election.PMS.Common.Session;
import com.election.PMS.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ChangePasswordActivity extends AppCompatActivity {
    ActionBar actionBar;
    TextView welcome_text;
    Session session;
    HashMap<String, String> user;
    EditText new_password, repeat_password;
    Button btn_Submit;
    private RequestQueue mRequestQueue;
    private JsonObjectRequest mJsonRequest;
    private static String URL = "changemobilepin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        actionBar = getSupportActionBar();
        actionBar.hide();
        inti();
        session = new Session(this);
        user = session.getProfileManagerDetails();
        if(user.get("type").equals("BLO")){
            welcome_text.setText(Html.fromHtml("Welcome, <b> " + user.get("title") + " " + user.get("officierName").toUpperCase() + "</b>, Booth Level Officer"));
        }else{
            welcome_text.setText(Html.fromHtml("Welcome <b> " + user.get("titleName") + " " + user.get("officierName").toUpperCase() + "</b> Presiding Officer"));
        }
        btn_Submit.setOnClickListener(v -> {
            if (new_password.getText().toString().equals(repeat_password.getText().toString())) {
                change_pin();
            } else {
                Toast.makeText(this, "Mis-Matched password", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void change_pin() {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.show();

        user = session.getProfileManagerDetails();
        HashMap<String, String> params = new HashMap<>();
        params.put("presidingOfficerID", user.get("presidingOfficierId"));
        params.put("MobilePin", repeat_password.getText().toString());

        mRequestQueue = Volley.newRequestQueue(this);
        mJsonRequest = new JsonObjectRequest(Request.Method.POST, Constant.API_CONSTANT + URL, new JSONObject(params), response -> {
            Log.e("response", response.toString());
            try {
                Boolean result = response.getBoolean("isSuccess");
                String message = response.getString("message");
                if (!result) {
                    pDialog.dismiss();
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(this, AC_PC_Activity.class);
                    startActivity(i);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("ChangePassword_ACTIVITY", "Error :" + error.getMessage()));
        mRequestQueue.add(mJsonRequest);
    }

    private void inti() {
        welcome_text = findViewById(R.id.welcome_text);
        new_password = findViewById(R.id.new_password);
        repeat_password = findViewById(R.id.repeat_password);
        btn_Submit = findViewById(R.id.change_pin_btn);
    }
}
