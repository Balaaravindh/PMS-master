package com.election.poll_monitor_system.Activity;

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
import com.election.poll_monitor_system.Common.Constant;
import com.election.poll_monitor_system.Common.Session;
import com.election.poll_monitor_system.R;

import org.json.JSONObject;

import java.util.HashMap;

public class PrePollDayActivity extends AppCompatActivity {

    ActionBar actionBar;

    RelativeLayout check_yes_1, check_no_1, check_yes_2, check_no_2;
    ImageView check_image_yes_1, check_image_no_1, check_image_yes_2, check_image_no_2;

    Button submit_btn;

    String id, isPollingPartyReached, isPollingMaterialReached;

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
        } else {
            if (isPollingPartyReached.equals("true")) {
                check_image_yes_1.setVisibility(View.VISIBLE);
                check_image_no_1.setVisibility(View.GONE);
            } else {
                check_image_yes_1.setVisibility(View.GONE);
                check_image_no_1.setVisibility(View.VISIBLE);
            }

            if (isPollingMaterialReached.equals("true")) {
                check_image_yes_2.setVisibility(View.VISIBLE);
                check_image_no_2.setVisibility(View.GONE);
            } else {
                check_image_yes_2.setVisibility(View.GONE);
                check_image_no_2.setVisibility(View.VISIBLE);
            }
        }

        check_yes_1.setOnClickListener(v -> {
            if (check_image_yes_1.getVisibility() == View.VISIBLE) {
                check_image_yes_1.setVisibility(View.GONE);
            } else {
                if (check_image_no_1.getVisibility() == View.VISIBLE) {
                    check_image_yes_1.setVisibility(View.GONE);
                } else {
                    check_image_yes_1.setVisibility(View.VISIBLE);
                    party_value = true;
                }
            }
        });

        check_no_1.setOnClickListener(v -> {
            if (check_image_no_1.getVisibility() == View.VISIBLE) {
                check_image_no_1.setVisibility(View.GONE);
            } else {
                if (check_image_yes_1.getVisibility() == View.VISIBLE) {
                    check_image_no_1.setVisibility(View.GONE);
                } else {
                    check_image_no_1.setVisibility(View.VISIBLE);
                    party_value = false;
                }
            }
        });

        check_yes_2.setOnClickListener(v -> {
            if (check_image_yes_2.getVisibility() == View.VISIBLE) {
                check_image_yes_2.setVisibility(View.GONE);
            } else {
                if (check_image_no_2.getVisibility() == View.VISIBLE) {
                    check_image_yes_2.setVisibility(View.GONE);
                } else {
                    check_image_yes_2.setVisibility(View.VISIBLE);
                    material_value = true;
                }
            }
        });

        check_no_2.setOnClickListener(v -> {
            if (check_image_no_2.getVisibility() == View.VISIBLE) {
                check_image_no_2.setVisibility(View.GONE);
            } else {
                if (check_image_yes_2.getVisibility() == View.VISIBLE) {
                    check_image_no_2.setVisibility(View.GONE);
                } else {
                    check_image_no_2.setVisibility(View.VISIBLE);
                    material_value = false;
                }
            }
        });

        back.setOnClickListener(v -> finish());

        home.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
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

        HashMap<String, String> params = new HashMap<>();
        params.put("PresidingOfficerID", user.get("presidingOfficierId"));
        params.put("AssemblyID", user.get("assemblyConstituencyId"));
        params.put("PollingStationId", id);
        params.put("IsPollingPartyReached", String.valueOf(party_value));
        params.put("IsPollingMaterialReached", String.valueOf(material_value));

        mRequestQueue = Volley.newRequestQueue(this);
        mJsonRequest = new JsonObjectRequest(Request.Method.POST, Constant.API_CONSTANT + SAVEURL, new JSONObject(params), response -> {
            Log.e("response", response.toString());
            try {
                String result = response.getString("result");
                String message = response.getString("message");
                if (result.equals("0")) {
                    pDialog.dismiss();
                    Toast.makeText(PrePollDayActivity.this, message, Toast.LENGTH_SHORT).show();
                } else {
                    pDialog.dismiss();
                    Toast.makeText(PrePollDayActivity.this, message, Toast.LENGTH_SHORT).show();
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
