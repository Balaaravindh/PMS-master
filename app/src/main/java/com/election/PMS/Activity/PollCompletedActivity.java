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

public class PollCompletedActivity extends AppCompatActivity {

    ActionBar actionBar;
    Button submit_btn_mock_poll;

    RelativeLayout check_yes, check_no;
    ImageView check_image_yes, check_image_no;

    private RequestQueue mRequestQueue;
    private JsonObjectRequest mJsonRequest;
    Session session;
    HashMap<String, String> user;

    boolean poll_complete = false;
    String id, isPollingCompleted;

    RelativeLayout back, home;
    String assemblyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_completed);

        //Hide Action Bar
        actionBar = getSupportActionBar();
        actionBar.hide();

        id = getIntent().getStringExtra("id");
        assemblyId = getIntent().getStringExtra("assemblyId");
        isPollingCompleted = getIntent().getStringExtra("isPollingCompleted");

        init();

        session = new Session(this);
        user = session.getProfileManagerDetails();

        if(isPollingCompleted.equals("true")){
            check_image_yes.setVisibility(View.VISIBLE);
            check_image_no.setVisibility(View.GONE);
            poll_complete = true;
        } else if (isPollingCompleted.equals("null") || isPollingCompleted.equals(null)) {
            check_image_yes.setVisibility(View.GONE);
            check_image_no.setVisibility(View.GONE);
            poll_complete = false;
        }else{
            check_image_yes.setVisibility(View.GONE);
            check_image_no.setVisibility(View.VISIBLE);
            poll_complete = false;
        }

        check_yes.setOnClickListener(v -> {
            poll_complete = true;
            check_image_yes.setVisibility(View.VISIBLE);
            check_image_no.setVisibility(View.GONE);
        });

        check_no.setOnClickListener(v -> {
            poll_complete = false;
            check_image_no.setVisibility(View.VISIBLE);
            check_image_yes.setVisibility(View.GONE);
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
                poll_completed();
            }
        });
    }

    private void poll_completed() {

        String URL = "SavePollDayCompleted";
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
            params.put("isPollingCompleted", poll_complete);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mRequestQueue = Volley.newRequestQueue(this);
        mJsonRequest = new JsonObjectRequest(Request.Method.POST, Constant.API_CONSTANT + URL, params, response -> {
            Log.e("response", response.toString());
            try {
                String result = response.getString("isSuccess");
                String message = response.getString("message");

                if (result.equals("false")) {
                    pDialog.dismiss();
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                } else {
                    pDialog.dismiss();
                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } catch (Exception ex) {

            }
        }, error -> Log.e("MAIN_ACTIVITY", "Error :" + error.getMessage()));

        mRequestQueue.add(mJsonRequest);


    }

    private void init() {
        submit_btn_mock_poll = findViewById(R.id.submit_btn_mock_poll);

        check_yes = findViewById(R.id.check_yes);
        check_no = findViewById(R.id.check_no);

        back = findViewById(R.id.back);
        home = findViewById(R.id.home);

        check_image_yes = findViewById(R.id.check_image_yes);
        check_image_no = findViewById(R.id.check_image_no);
    }
}
