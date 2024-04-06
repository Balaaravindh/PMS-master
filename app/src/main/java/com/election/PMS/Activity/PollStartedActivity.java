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

public class PollStartedActivity extends AppCompatActivity {

    ActionBar actionBar;

    RelativeLayout check_yes, check_no;
    ImageView check_image_yes, check_image_no;

    Button submit_btn_mock_poll;
    boolean poll_start = false;

    private RequestQueue mRequestQueue;
    private JsonObjectRequest mJsonRequest;
    Session session;
    HashMap<String, String> user;
    String URL = "SavePollDayStarted";
    String id, assemblyId, isPollingStarted;

    RelativeLayout back, home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_started);

        //Hide Action Bar
        actionBar = getSupportActionBar();
        actionBar.hide();

        id = getIntent().getStringExtra("id");
        assemblyId = getIntent().getStringExtra("assemblyId");
        isPollingStarted = getIntent().getStringExtra("isPollingStarted");

        init();

        session = new Session(this);
        user = session.getProfileManagerDetails();

        if (isPollingStarted.equals("true")) {
            check_image_yes.setVisibility(View.VISIBLE);
            check_image_no.setVisibility(View.GONE);
            poll_start = true;
        } else if (isPollingStarted.equals("null") || isPollingStarted.equals(null)) {
            check_image_yes.setVisibility(View.GONE);
            check_image_no.setVisibility(View.GONE);
            poll_start = false;
        } else {
            check_image_yes.setVisibility(View.GONE);
            check_image_no.setVisibility(View.VISIBLE);
            poll_start = false;
        }

        check_yes.setOnClickListener(v -> {
            if (check_image_yes.getVisibility() == View.VISIBLE) {
                check_image_yes.setVisibility(View.GONE);
            } else {
                if (check_image_no.getVisibility() == View.VISIBLE) {
                    check_image_yes.setVisibility(View.GONE);
                } else {
                    check_image_yes.setVisibility(View.VISIBLE);
                    poll_start = true;
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
                    poll_start = false;
                }
            }
        });

        back.setOnClickListener(v -> finish());

        home.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("assemblyId", assemblyId);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        submit_btn_mock_poll.setOnClickListener(v -> {
            if (check_image_yes.getVisibility() != View.VISIBLE && check_image_no.getVisibility() != View.VISIBLE) {
                Toast.makeText(this, "Please check value for poll start!!!", Toast.LENGTH_SHORT).show();
            } else {
                poll_started();
            }
        });

    }

    private void poll_started() {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.show();

        session = new Session(this);
        user = session.getProfileManagerDetails();
        JSONObject params = new JSONObject();
        try {
            params.put("presidingOfficerID", id);
            params.put("constituencyID", assemblyId);
            params.put("boothID", user.get("boothId"));
            params.put("isPollingStarted", poll_start);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mRequestQueue = Volley.newRequestQueue(this);
        mJsonRequest = new JsonObjectRequest(Request.Method.POST, Constant.API_CONSTANT + URL, params, response -> {
            Log.e("response", response.toString());
            try {
                String result = response.getString("isSuccess");
                String message = response.getString("message");
                pDialog.dismiss();
                if (result.equals("false")) {
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } catch (Exception ex) {

            }
        }, error -> Log.e("MAIN_ACTIVITY", "Error :" + error.getMessage()));

        mRequestQueue.add(mJsonRequest);

    }

    private void init() {

        check_yes = findViewById(R.id.check_yes);
        check_no = findViewById(R.id.check_no);

        check_image_yes = findViewById(R.id.check_image_yes);
        check_image_no = findViewById(R.id.check_image_no);

        back = findViewById(R.id.back);
        home = findViewById(R.id.home);

        submit_btn_mock_poll = findViewById(R.id.submit_btn_mock_poll);

    }
}
