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

public class MockPollActivity extends AppCompatActivity {

    ActionBar actionBar;

    RelativeLayout check_yes, check_no;
    ImageView check_image_yes, check_image_no;
    Button submit_btn_mock_poll;

    private RequestQueue mRequestQueue;
    private JsonObjectRequest mJsonRequest;
    String URL = "SavePollDayMockConducted";
    Session session;
    HashMap<String, String> user;
    String id, isMockPollConducted = "";
    boolean IsMockPollConducted = false;

    RelativeLayout back, home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock_poll);

        //Hide Action Bar
        actionBar = getSupportActionBar();
        actionBar.hide();

        id = getIntent().getStringExtra("id");
        isMockPollConducted = getIntent().getStringExtra("isMockPollConducted");

        init();

        session = new Session(this);
        user = session.getProfileManagerDetails();

        if (isMockPollConducted.equals("true")) {
            check_image_yes.setVisibility(View.VISIBLE);
            check_image_no.setVisibility(View.GONE);
        } else if (isMockPollConducted.equals("null") || isMockPollConducted.equals(null)) {
            check_image_yes.setVisibility(View.GONE);
            check_image_no.setVisibility(View.GONE);
        } else {
            check_image_yes.setVisibility(View.GONE);
            check_image_no.setVisibility(View.VISIBLE);
        }

        check_yes.setOnClickListener(v -> {
            if (check_image_yes.getVisibility() == View.VISIBLE) {
                check_image_yes.setVisibility(View.GONE);
            } else {
                if (check_image_no.getVisibility() == View.VISIBLE) {
                    check_image_yes.setVisibility(View.GONE);
                } else {
                    check_image_yes.setVisibility(View.VISIBLE);
                    IsMockPollConducted = true;
                }

            }
        });

        check_no.setOnClickListener(v -> {
            if (check_image_no.getVisibility() == View.VISIBLE) {
                check_image_no.setVisibility(View.GONE);
            } else {
                if (check_image_yes.getVisibility() == View.VISIBLE) {
                    check_image_no.setVisibility(View.GONE);
                } else {
                    check_image_no.setVisibility(View.VISIBLE);
                    IsMockPollConducted = false;
                }
            }
        });

        submit_btn_mock_poll.setOnClickListener(v -> {
            if (check_image_yes.getVisibility() != View.VISIBLE && check_image_no.getVisibility() != View.VISIBLE) {
                Toast.makeText(this, "Please check value for mock poll!!!", Toast.LENGTH_SHORT).show();
            } else {
                mock_completed();
            }
        });

        back.setOnClickListener(v -> finish());

        home.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });


    }

    private void mock_completed() {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.show();

        session = new Session(this);
        user = session.getProfileManagerDetails();

        HashMap<String, String> params = new HashMap<>();
        params.put("PresidingOfficerID", user.get("presidingOfficierId"));
        params.put("AssemblyID", user.get("assemblyConstituencyId"));
        params.put("PollingStationId", id);
        params.put("Status", String.valueOf(IsMockPollConducted));

        mRequestQueue = Volley.newRequestQueue(this);
        mJsonRequest = new JsonObjectRequest(Request.Method.POST, Constant.API_CONSTANT + URL, new JSONObject(params), response -> {

            try {
                String result = response.getString("result");
                String message = response.getString("message");

                if (result.equals("0")) {
                    pDialog.dismiss();
                    Toast.makeText(MockPollActivity.this, message, Toast.LENGTH_SHORT).show();
                } else {
                    pDialog.dismiss();
                    Toast.makeText(MockPollActivity.this, message, Toast.LENGTH_SHORT).show();
                    finish();
                }

            } catch (Exception ex) {
            }

        }, error -> Log.e("LOGIN_ACTIVITY", "Error :" + error.getMessage()));

        mRequestQueue.add(mJsonRequest);

    }

    private void init() {

        check_yes = findViewById(R.id.check_yes);
        check_no = findViewById(R.id.check_no);

        back = findViewById(R.id.back);
        home = findViewById(R.id.home);

        check_image_yes = findViewById(R.id.check_image_yes);
        check_image_no = findViewById(R.id.check_image_no);

        submit_btn_mock_poll = findViewById(R.id.submit_btn_mock_poll);

    }
}
