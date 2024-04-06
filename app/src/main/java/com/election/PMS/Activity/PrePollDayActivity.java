package com.election.PMS.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

public class PrePollDayActivity extends AppCompatActivity {
    ActionBar actionBar;
    RelativeLayout check_yes_1, check_no_1, check_yes_2, check_no_2;
    ImageView check_image_yes_1, check_image_no_1, check_image_yes_2, check_image_no_2;
    Button submit_btn;
    String id, assemblyId, prePollDayId, isPollingPartyReached, isPollingMaterialReached;
    boolean party_value = false;
    boolean material_value = false;
    private RequestQueue mRequestQueue;
    private JsonObjectRequest mJsonRequest;
    String SAVEURL = "SavePrePollDay";
    Session session;
    HashMap<String, String> user;
    RelativeLayout back, home;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_poll_day);

        //Hide Action Bar
        actionBar = getSupportActionBar();
        actionBar.hide();

        id = getIntent().getStringExtra("id");
        assemblyId = getIntent().getStringExtra("assemblyId");
        prePollDayId = getIntent().getStringExtra("prePollDayId");
        isPollingPartyReached = getIntent().getStringExtra("isPollingPartyReached");
        isPollingMaterialReached = getIntent().getStringExtra("isPollingMaterialReached");

        init();

        session = new Session(this);
        user = session.getProfileManagerDetails();

        if (isPollingPartyReached.equals("0") && isPollingMaterialReached.equals("0")) {
            check_image_yes_1.setVisibility(View.GONE);
            check_image_no_1.setVisibility(View.GONE);
            check_image_yes_2.setVisibility(View.GONE);
            check_image_no_2.setVisibility(View.GONE);
            party_value = false;
            material_value = false;
        } else {
            if (isPollingPartyReached.equals("true")) {
                party_value = true;
                check_image_yes_1.setVisibility(View.VISIBLE);
                check_image_no_1.setVisibility(View.GONE);
            } else {
                party_value = false;
                check_image_yes_1.setVisibility(View.GONE);
                check_image_no_1.setVisibility(View.VISIBLE);
            }

            if (isPollingMaterialReached.equals("true")) {
                material_value = true;
                check_image_yes_2.setVisibility(View.VISIBLE);
                check_image_no_2.setVisibility(View.GONE);
            } else {
                material_value = false;
                check_image_yes_2.setVisibility(View.GONE);
                check_image_no_2.setVisibility(View.VISIBLE);
            }
        }

        check_yes_1.setOnClickListener(v -> {
            party_value = true;
            check_image_yes_1.setVisibility(View.VISIBLE);
            check_image_no_1.setVisibility(View.GONE);
        });

        check_no_1.setOnClickListener(v -> {
            party_value = false;
            check_image_yes_1.setVisibility(View.GONE);
            check_image_no_1.setVisibility(View.VISIBLE);
        });

        check_yes_2.setOnClickListener(v -> {
            material_value = true;
            check_image_yes_2.setVisibility(View.VISIBLE);
            check_image_no_2.setVisibility(View.GONE);
        });

        check_no_2.setOnClickListener(v -> {
            material_value = false;
            check_image_no_2.setVisibility(View.VISIBLE);
            check_image_yes_2.setVisibility(View.GONE);
        });

        back.setOnClickListener(v -> finish());

        home.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("assemblyId", assemblyId);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        submit_btn.setOnClickListener(v -> {
            if (check_image_yes_1.getVisibility() != View.VISIBLE && check_image_no_1.getVisibility() != View.VISIBLE) {
                Toast.makeText(this, "Please check value for party reached!!!", Toast.LENGTH_SHORT).show();
            } else if (check_image_yes_2.getVisibility() != View.VISIBLE && check_image_no_2.getVisibility() != View.VISIBLE) {
                Toast.makeText(this, "Please check value for material reached!!!", Toast.LENGTH_SHORT).show();
            } else {
                submitValue();
            }
        });
    }
    private void submitValue() {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.show();
        JSONObject params = new JSONObject();
        try {
            params.put("prePollDayId", prePollDayId);
            params.put("presidingOfficerID", id);
            params.put("constituencyID", assemblyId);
            params.put("boothID", user.get("boothId"));
            params.put("isPollingPartyReached", party_value);
            params.put("isPollingMaterialReached", material_value);
            params.put("presidingOfficer", null);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mRequestQueue = Volley.newRequestQueue(this);
        mJsonRequest = new JsonObjectRequest(Request.Method.POST, Constant.API_CONSTANT + SAVEURL, params, response -> {
            Log.e("response", response.toString());
            try {
                String result = response.getString("isSuccess");
                String message = response.getString("message");
                if (result.equals("false")) {
                    pDialog.dismiss();
                    Toast.makeText(PrePollDayActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                } else {
                    pDialog.dismiss();
                    Toast.makeText(PrePollDayActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } catch (Exception ex) {
            }
        }, error -> Log.e("LOGIN_ACTIVITY", "Error :" + error.getMessage()));
        mRequestQueue.add(mJsonRequest);
    }
    private void init() {

        check_yes_1 = findViewById(R.id.check_yes_1);
        check_no_1 = findViewById(R.id.check_no_1);
        check_yes_2 = findViewById(R.id.check_yes_2);
        check_no_2 = findViewById(R.id.check_no_2);

        back = findViewById(R.id.back);
        home = findViewById(R.id.home);

        check_image_yes_1 = findViewById(R.id.check_image_yes_1);
        check_image_no_1 = findViewById(R.id.check_image_no_1);
        check_image_yes_2 = findViewById(R.id.check_image_yes_2);
        check_image_no_2 = findViewById(R.id.check_image_no_2);

        submit_btn = findViewById(R.id.submit_btn);
    }
}
