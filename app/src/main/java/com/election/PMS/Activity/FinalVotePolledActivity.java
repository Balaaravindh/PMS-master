package com.election.PMS.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
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

public class FinalVotePolledActivity extends AppCompatActivity {
    ActionBar actionBar;
    Button submit_btn;
    private RequestQueue mRequestQueue;
    private JsonObjectRequest mJsonRequest;
    Session session;
    HashMap<String, String> user;
    String totalMaleVotesPolled, totalFemaleVotesPolled, totalTGVotesPolled, totalVotesPolled, id, totalElectors;
    EditText count, male_count, female_count, total_count, total_count_votes;
    Dialog dialog = null;
    String male, female, total;
    RelativeLayout back, home;
    String assemblyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_vote_polled);

        actionBar = getSupportActionBar();
        actionBar.hide();

        id = getIntent().getStringExtra("id");
        assemblyId = getIntent().getStringExtra("assemblyId");
        totalMaleVotesPolled = getIntent().getStringExtra("totalMaleVotesPolled");
        totalFemaleVotesPolled = getIntent().getStringExtra("totalFemaleVotesPolled");
        totalTGVotesPolled = getIntent().getStringExtra("totalTGVotesPolled");
        totalVotesPolled = getIntent().getStringExtra("totalVotesPolled");
        totalElectors  = getIntent().getStringExtra("totalElectors");

        init();

        session = new Session(this);
        user = session.getProfileManagerDetails();
        count.setText(user.get("electors"));

        if (!totalMaleVotesPolled.equals("0") &&
                !totalFemaleVotesPolled.equals("0") &&
                !totalTGVotesPolled.equals("0") &&
                !totalVotesPolled.equals("0")) {
            male_count.setText(totalMaleVotesPolled);
            female_count.setText(totalFemaleVotesPolled);
            total_count.setText(totalTGVotesPolled);
            total_count_votes.setText(totalVotesPolled);
        }

        male_count.addTextChangedListener(mTextEditorWatcher_Male);
        female_count.addTextChangedListener(mTextEditorWatcher_Female);
        total_count.addTextChangedListener(mTextEditorWatcher_TG);

        back.setOnClickListener(v -> finish());

        home.setOnClickListener(v -> {
            Intent intent = new Intent(FinalVotePolledActivity.this, MainActivity.class);
            intent.putExtra("assemblyId", assemblyId);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });


        submit_btn.setOnClickListener(v -> {
            String total_polled = total_count_votes.getText().toString();
            int total_polled_count;
            int electors =  Integer.parseInt(user.get("electors"));
            if(total_polled.isEmpty()){
                total_polled_count = 0;
            }else{
                total_polled_count = Integer.parseInt(total_polled);
            }
            if (total_polled_count > 0 && total_polled_count <= electors) {
                final_vote_api();
            } else {
                Toast.makeText(this, "Please enter the valid count", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void final_vote_api() {

        String URL = "SavePollDayFinalVote";

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.show();
        session = new Session(this);
        user = session.getProfileManagerDetails();
        male = male_count.getText().toString();
        female = female_count.getText().toString();
        total = total_count.getText().toString();
        if (male.isEmpty()) {
            male = "0";
        }
        if (female.isEmpty()) {
            female = "0";
        }
        if (total.isEmpty()) {
            total = "0";
        }
        JSONObject params = new JSONObject();
        try {
            params.put("presidingOfficerID", id);
            params.put("constituencyID", assemblyId);
            params.put("boothID", user.get("boothId"));
            params.put("totalElectors", count.getText().toString());
            params.put("totalMaleVotesPolled", male);
            params.put("totalFemaleVotesPolled", female);
            params.put("totalTGVotesPolled", total);
            params.put("totalVotesPolled", total_count_votes.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mRequestQueue = Volley.newRequestQueue(this);
        mJsonRequest = new JsonObjectRequest(Request.Method.POST, Constant.API_CONSTANT + URL, params, response -> {

            try {
                String result = response.getString("isSuccess");
                String message = response.getString("message");

                if (result.equals("false")) {
                    pDialog.dismiss();
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                } else {
                    pDialog.dismiss();
                    ShowDialog();
                }
            } catch (Exception ex) {

            }

        }, error -> Log.e("MAIN_ACTIVITY", "Error :" + error.getMessage()));

        mRequestQueue.add(mJsonRequest);

    }

    private void ShowDialog() {

        dialog = new Dialog(FinalVotePolledActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.completed_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        TextView cong_text = dialog.findViewById(R.id.cong_text);
        Button exit_btn = dialog.findViewById(R.id.exit_btn);

        session = new Session(this);
        user = session.getProfileManagerDetails();

        cong_text.setText(Html.fromHtml("Congratulations, <b> " + user.get("titleName") + " " +
                user.get("officierName").toUpperCase() + "</b>, You have successfully uploaded all the data."));

       //cong_text.setText(Html.fromHtml("Congratulations, <b>Mr./Ms. " + user.get("officierName")+ "</b>, You have successfully uploaded all the data."));

        exit_btn.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(FinalVotePolledActivity.this, MainActivity.class);
            intent.putExtra("assemblyId", assemblyId);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });


        dialog.show();

    }

    private final TextWatcher mTextEditorWatcher_Male = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

            male = male_count.getText().toString();
            female = female_count.getText().toString();
            total = total_count.getText().toString();

            if (male.isEmpty()) {
                male = "0";
            }

            if (female.isEmpty()) {
                female = "0";
            }

            if (total.isEmpty()) {
                total = "0";
            }

            int value1 = Integer.parseInt(male);
            int value2 = Integer.parseInt(female);
            int value3 = Integer.parseInt(total);

            int total_value = value1 + value2 + value3;
            total_count_votes.setText(String.valueOf(total_value));


        }

        public void afterTextChanged(Editable s) {
        }
    };

    private final TextWatcher mTextEditorWatcher_Female = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

            male = male_count.getText().toString();
            female = female_count.getText().toString();
            total = total_count.getText().toString();

            if (male.isEmpty()) {
                male = "0";
            }

            if (female.isEmpty()) {
                female = "0";
            }

            if (total.isEmpty()) {
                total = "0";
            }

            int value1 = Integer.parseInt(male);
            int value2 = Integer.parseInt(female);
            int value3 = Integer.parseInt(total);

            int total_value = value1 + value2 + value3;
            total_count_votes.setText(String.valueOf(total_value));

        }

        public void afterTextChanged(Editable s) {
        }
    };

    private final TextWatcher mTextEditorWatcher_TG = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

            male = male_count.getText().toString();
            female = female_count.getText().toString();
            total = total_count.getText().toString();

            if (male.isEmpty()) {
                male = "0";
            }

            if (female.isEmpty()) {
                female = "0";
            }

            if (total.isEmpty()) {
                total = "0";
            }

            int value1 = Integer.parseInt(male);
            int value2 = Integer.parseInt(female);
            int value3 = Integer.parseInt(total);

            int total_value = value1 + value2 + value3;
            total_count_votes.setText(String.valueOf(total_value));

        }

        public void afterTextChanged(Editable s) {
        }
    };

    public void init() {
        submit_btn = findViewById(R.id.submit_btn);

        back = findViewById(R.id.back);
        home = findViewById(R.id.home);

        count = findViewById(R.id.count);
        male_count = findViewById(R.id.male_count);
        female_count = findViewById(R.id.female_count);
        total_count = findViewById(R.id.total_count);
        total_count_votes = findViewById(R.id.total_count_votes);

    }
}
