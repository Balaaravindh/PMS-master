package com.election.PMS.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.election.PMS.Common.PollStartedSession;
import com.election.PMS.Common.Session;
import com.election.PMS.R;

import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    ActionBar actionBar;
    Button btn_pre_poll, btn_poll, btn_exit;
    RelativeLayout logo_layout;
    Session session;
    PollStartedSession pollStartedSession;
    HashMap<String, String> user;
    String pollingStationID;
    private RequestQueue mRequestQueue;
    private JsonObjectRequest mJsonRequest;
    String URL = "";
    TextView welcome_text, title_text;
    RelativeLayout back, header_layout;
    View view_white;
    String assemblyId, title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Hide Action Bar
        actionBar = getSupportActionBar();
        actionBar.hide();
        title = getIntent().getStringExtra("title");
        assemblyId = getIntent().getStringExtra("assemblyId");
        init();
        title_text.setText(title);
        session = new Session(this);
        user = session.getProfileManagerDetails();
        pollStartedSession = new PollStartedSession(this);
        if(user.get("type").equals("BLO")){
            welcome_text.setText(Html.fromHtml("Welcome, <b> " + user.get("title") + " " + user.get("officierName").toUpperCase() + "</b>, Booth Level Officer"));
        }else{
            welcome_text.setText(Html.fromHtml("Welcome, <b> " + user.get("title") + " " + user.get("officierName").toUpperCase() + "</b>, Presiding Officer"));
        }
        btn_pre_poll.setOnClickListener(v -> data_values());
        btn_poll.setOnClickListener(v -> {
            Intent intent = new Intent(this, PollDayActivity.class);
            intent.putExtra("id", user.get("presidingOfficierId"));
            intent.putExtra("assemblyId", assemblyId);
            startActivity(intent);
        });
        btn_exit.setOnClickListener(v -> {
            session.clear_Session();
            pollStartedSession.clear_Session();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        back.setOnClickListener(v -> finish());
    }

    @Override
    public void onRestart() {
        super.onRestart();
        startActivity(getIntent());
        finish();
    }

//    private  void getGetPollsByPresidingOfficerID(){
//        session = new Session(this);
//        electorsValueSave = new ElectorsValueSave(this);
//        user = session.getProfileManagerDetails();
//        String presidingOfficierId = user.get("presidingOfficierId");
//        URL = "GetPollsByPresidingOfficerID/" + presidingOfficierId;
//
//        final ProgressDialog pDialog = new ProgressDialog(this);
//        pDialog.setMessage("Loading...");
//        pDialog.setCancelable(false);
//        pDialog.setCanceledOnTouchOutside(false);
//        pDialog.show();
//
//        mRequestQueue = Volley.newRequestQueue(this);
//        mJsonRequest = new JsonObjectRequest(Request.Method.GET, Constant.API_CONSTANT + URL, null, response -> {
//            Log.e("response", response.toString());
//            try {
//                String result = response.getString("result");
//                String message = response.getString("message");
//                if(result.equals("0")) {
//                    pDialog.dismiss();
//                }else{
//                    JSONArray data = response.getJSONArray("data");
//                    for(int i= 0; i < data.length(); i++){
//                        pollingStationID = data.getJSONObject(i).getString("boothID");
////                        String districtID = data.getJSONObject(i).getString("districtID");
////                        String assemblyConstituencyID = data.getJSONObject(i).getString("assemblyConstituencyID");
////                        String pollingStationCode = data.getJSONObject(i).getString("pollingStationCode");
////                        String pollingStationNameLanguage1 = data.getJSONObject(i).getString("pollingStationNameLanguage1");
////                        String pollingStationAddressLanguage1 = data.getJSONObject(i).getString("pollingStationAddressLanguage1");
////                        String pollingStationNameLanguage2 = data.getJSONObject(i).getString("pollingStationNameLanguage2");
////                        String pollingStationAddressLanguage2 = data.getJSONObject(i).getString("pollingStationAddressLanguage2");
////                        String pollingStationCoordinates = data.getJSONObject(i).getString("pollingStationCoordinates");
////                        String isActive = data.getJSONObject(i).getString("isActive");
////                        String addressLanguage1 = data.getJSONObject(i).getString("addressLanguage1");
////                        String addressLanguage2 = data.getJSONObject(i).getString("addressLanguage2");
//                        String electors = data.getJSONObject(i).getString("electors");
//                        String is8PMEnabled = data.getJSONObject(i).getString("is8PMEnabled");
//
//                        electorsValueSave.createProfileManageSession(electors, is8PMEnabled);
//                    }
//                    pDialog.dismiss();
//                }
//            } catch (Exception ex) { }
//        }, error -> Log.e("MAIN_ACTIVITY", "Error :" + error.getMessage()));
//        mRequestQueue.add(mJsonRequest);
//    }

    private void data_values() {
        session = new Session(this);
        user = session.getProfileManagerDetails();
        pollingStationID = user.get("presidingOfficierId");
        URL = "GetPrePollDayData/" + pollingStationID + "/" + assemblyId;

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.show();

        mRequestQueue = Volley.newRequestQueue(this);
        mJsonRequest = new JsonObjectRequest(Request.Method.GET, Constant.API_CONSTANT + URL, null, response -> {
            Log.e("response", response.toString());
            try {
                String result = response.getString("isSuccess");
                String message = response.getString("message");
                if (result.equals("false")) {
                    pDialog.dismiss();
                    Intent intent = new Intent(this, PrePollDayActivity.class);
                    intent.putExtra("id", pollingStationID);
                    intent.putExtra("assemblyId", assemblyId);
                    intent.putExtra("prePollDayId", "0");
                    intent.putExtra("isPollingPartyReached", "0");
                    intent.putExtra("isPollingMaterialReached", "0");
                    startActivity(intent);
                } else {
                    JSONObject data = response.getJSONObject("data");
                    String prePollDayId = data.getString("prePollDayID");
                    String boothId = data.getString("boothID");
                    String presidingOfficerID = data.getString("presidingOfficerID");
                    String isPollingPartyReached = data.getString("isPollingPartyReached");
                    String isPollingMaterialReached = data.getString("isPollingMaterialReached");
                    String isActive = data.getString("isActive");
                    String constituencyID = data.getString("constituencyID");
                    assemblyId = data.getString("constituencyID");
                     pDialog.dismiss();
                    if(isPollingPartyReached.equals("false") || isPollingMaterialReached.equals("false")){
                        Intent intent = new Intent(this, PrePollDayActivity.class);
                        intent.putExtra("id", presidingOfficerID);
                        intent.putExtra("assemblyId", constituencyID);
                        intent.putExtra("prePollDayId", prePollDayId);
                        intent.putExtra("isPollingPartyReached", isPollingPartyReached);
                        intent.putExtra("isPollingMaterialReached", isPollingMaterialReached);
                        startActivity(intent);
                    }else{
                        Toast.makeText(MainActivity.this, "Already Submited", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception ex) { }
        }, error -> Log.e("MAIN_ACTIVITY", "Error :" + error.getMessage()));
        mRequestQueue.add(mJsonRequest);
    }

    private void init() {
        btn_pre_poll = findViewById(R.id.pre_polled_day);
        btn_poll = findViewById(R.id.poll_day);
        btn_exit = findViewById(R.id.btn_exit);

        title_text = findViewById(R.id.title_text);
        back = findViewById(R.id.back);
        logo_layout = findViewById(R.id.logo_layout);
        welcome_text = findViewById(R.id.welcome_text);

        view_white = findViewById(R.id.view_white);
        header_layout = findViewById(R.id.header_layout);
    }
}
