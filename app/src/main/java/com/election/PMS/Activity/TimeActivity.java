package com.election.PMS.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
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

import java.text.DecimalFormat;
import java.util.HashMap;

public class TimeActivity extends AppCompatActivity {

    ActionBar actionBar;

    public static String time = "" , id = "", count = "0", percentage = "0", totalElectors = "0";
    public TextView time_text, count_Percentage;
    public Button submit_btn;

    Session session;
    HashMap<String, String> user;

    EditText count_editext;

    private RequestQueue mRequestQueue;
    private JsonObjectRequest mJsonRequest;
    RelativeLayout back, home;
    String assemblyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);

        //Hide Action Bar
        actionBar = getSupportActionBar();
        actionBar.hide();

        time = getIntent().getStringExtra("time");
        id = getIntent().getStringExtra("id");
        assemblyId = getIntent().getStringExtra("assemblyId");
        count = getIntent().getStringExtra("count");
        percentage = getIntent().getStringExtra("percentage");
        totalElectors = getIntent().getStringExtra("totalElectors");

        init();

        session = new Session(this);
        user = session.getProfileManagerDetails();
        if(count.equals("0")){

        }else{
            count_editext.setText(count);
        }

        if(percentage.equals("0")){
            count_Percentage.setText("Percentage : 0.00 %");
        }else{
            count_Percentage.setText("Percentage : "+ percentage+ " %");
        }

        count_editext.addTextChangedListener(mTextEditorWatcher);
        time_text.setText(Html.fromHtml("<b>" + time + " Status.</b><br><br>Please enter the No. of votes polled till " + time));

        back.setOnClickListener(v -> finish());

        home.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("assemblyId", assemblyId);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        submit_btn.setOnClickListener(v -> {
            time_count();
        });
    }

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(count_editext.getText().toString().isEmpty()){
                count_Percentage.setText("Percentage : 0.00 %");
            }else{
                int value = Integer.parseInt(count_editext.getText().toString());
                user = session.getProfileManagerDetails();
                String getElectors = user.get("electors");
                int total_Count = Integer.parseInt(getElectors);
                double percentage_value = (value * 1.0 / total_Count) * 100;
                count_Percentage.setText("Percentage : "+ new DecimalFormat("##.##").format(percentage_value) + " %");
            }
        }

        public void afterTextChanged(Editable s) {  }

    };

    private void time_count() {
        String URL = "SavePollDayEachTime";
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.show();
        String value = count_editext.getText().toString();
        if(value.isEmpty()){
            value = "0";
        }
        JSONObject params = new JSONObject();
        try {
            params.put("presidingOfficerID", id);
            params.put("constituencyID", assemblyId);
            params.put("boothID", user.get("boothId"));
            params.put("timeKey", time);
            params.put("noOfVotes", value);
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
        time_text = findViewById(R.id.time_text);
        count_Percentage = findViewById(R.id.count_Percentage);

        back = findViewById(R.id.back);
        home = findViewById(R.id.home);

        count_editext = findViewById(R.id.count_editext);
        submit_btn = findViewById(R.id.submit_btn);
    }
}
