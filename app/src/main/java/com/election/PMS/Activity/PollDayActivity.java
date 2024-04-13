package com.election.PMS.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.election.PMS.Adapter.BoothAdapter;
import com.election.PMS.Common.Constant;
import com.election.PMS.Common.NotificationService;
import com.election.PMS.Common.NukeSSLCerts;
import com.election.PMS.Common.PollStartedSession;
import com.election.PMS.Common.Session;
import com.election.PMS.R;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class PollDayActivity extends AppCompatActivity {

    ActionBar actionBar;

    Button mock_poll, poll_started, poll_completed, final_btn;

    String ids, assemblyId, isMockPollConducted = "null", isPollingStarted = "null";
    String serverTime;
    String noOfVotesAt9AM = "0", noOfVotesAt10AM = "0", noOfVotesAt11AM = "0", noOfVotesAt12PM = "0", noOfVotesAt1PM = "0",
            noOfVotesAt2PM = "0",
            noOfVotesAt3PM = "0", noOfVotesAt4PM = "0", noOfVotesAt5PM = "0", noOfVotesAt6PM = "0", noOfVotesAt7PM = "0";

    String noOfVotesPercentAt9AM = "0", noOfVotesPercentAt10AM = "0", noOfVotesPercentAt11AM = "0", noOfVotesPercentAt12PM = "0",
            noOfVotesPercentAt1PM = "0", noOfVotesPercentAt2PM = "0", noOfVotesPercentAt3PM = "0",
            noOfVotesPercentAt4PM = "0", noOfVotesPercentAt5PM = "0", noOfVotesPercentAt6PM = "0", noOfVotesPercentAt7PM = "0";

    String isPollingCompleted = "false",
            totalElectors = "100",
            totalMaleVotesPolled  = "0",
            totalFemaleVotesPolled = "0",
            totalTGVotesPolled = "0",
            totalVotesPolled = "0";
    private RequestQueue mRequestQueue;
    private JsonObjectRequest mJsonRequest;
    Session session;
    HashMap<String, String> user;
    Dialog dialog = null;
    GridView dialog_ListView;
    RelativeLayout close_alert;
    TextView select_time;
    String URL;
    RelativeLayout dropdown;
    ArrayList<String> timeArray = new ArrayList<>();
    ArrayList<String> timeArray_new = new ArrayList<>();
    RelativeLayout back, home;
    PollStartedSession pollStartedSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_day);

        //Hide Action Bar
        actionBar = getSupportActionBar();
        actionBar.hide();

        ids = getIntent().getStringExtra("id");
        assemblyId = getIntent().getStringExtra("assemblyId");
        init();
        new NukeSSLCerts().nuke();
        timeArray = new ArrayList<>();
        timeArray.add("09:00 AM");
        timeArray.add("11:00 AM");
        timeArray.add("01:00 PM");
        timeArray.add("03:00 PM");
        timeArray.add("05:00 PM");
        timeArray.add("07:00 PM");

        timeArray_new = new ArrayList<>();
        timeArray_new.add("09:00 AM");
        timeArray_new.add("11:00 AM");
         timeArray_new.add("01:00 PM");
        timeArray_new.add("03:00 PM");
        timeArray_new.add("05:00 PM");
        timeArray_new.add("07:00 PM");

        poll_Datas();

        mock_poll.setOnClickListener(v -> {
            if (isMockPollConducted.equals("true")) {
                Toast.makeText(this, "Already Submited", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(this, MockPollActivity.class);
                intent.putExtra("isMockPollConducted", isMockPollConducted);
                intent.putExtra("id", ids);
                intent.putExtra("assemblyId", assemblyId);
                startActivity(intent);
            }
        });

        poll_started.setOnClickListener(v -> {
            if (isPollingStarted.equals("true")) {
                Toast.makeText(this, "Already Submited", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(this, PollStartedActivity.class);
                intent.putExtra("id", ids);
                intent.putExtra("assemblyId", assemblyId);
                intent.putExtra("isPollingStarted", isPollingStarted);
                startActivity(intent);
            }
        });

        poll_completed.setOnClickListener(v -> {
            if (isPollingStarted.equals("true")) {
                if (isPollingCompleted.equals("true")) {
                    Toast.makeText(this, "Already Submited", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(this, PollCompletedActivity.class);
                    intent.putExtra("id", ids);
                    intent.putExtra("assemblyId", assemblyId);
                    intent.putExtra("isPollingCompleted", isPollingCompleted);
                    startActivity(intent);
                }
            } else {
                Toast.makeText(this, "Yet to Start the Poll. So, Please Start the Poll", Toast.LENGTH_SHORT).show();
            }
        });

        final_btn.setOnClickListener(v -> {
            if (isPollingStarted.equals("true")) {
                if (totalTGVotesPolled.equals("0")) {
                    Intent intent = new Intent(this, FinalVotePolledActivity.class);
                    intent.putExtra("id", ids);
                    intent.putExtra("assemblyId", assemblyId);
                    intent.putExtra("totalElectors", totalElectors);
                    intent.putExtra("totalMaleVotesPolled", totalMaleVotesPolled);
                    intent.putExtra("totalFemaleVotesPolled", totalFemaleVotesPolled);
                    intent.putExtra("totalTGVotesPolled", totalTGVotesPolled);
                    intent.putExtra("totalVotesPolled", totalVotesPolled);

                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Already Submited", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Yet to Start the Poll. So, Please Start the Poll", Toast.LENGTH_SHORT).show();
            }
        });

        dropdown.setOnClickListener(v -> {
            if (isPollingStarted.equals("true")) {
                if (isPollingCompleted.equals("false")) {
//                    session = new Session(this);
//                    user = session.getProfileManagerDetails();
//                    if (user.get("is8PMEnabled").equals("true")) {
//                        ShowDialog(timeArray);
//                    } else {
//                        ShowDialog(timeArray_new);
//                    }
                    ShowDialog(timeArray_new);
                } else {
                    Toast.makeText(this, "Poll Completed", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Yet to Start the Poll. So, Please Start the Poll", Toast.LENGTH_SHORT).show();
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

    }

    private void ShowDialog(ArrayList<String> timeList) {
        dialog = new Dialog(PollDayActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_layout);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog_ListView = dialog.findViewById(R.id.listview);
        close_alert = dialog.findViewById(R.id.close_alert);
        session = new Session(this);
        user = session.getProfileManagerDetails();

        ArrayList<String> new_time_List = new ArrayList<>();

        if (Constant.TimeValue == 0) {
            new_time_List = timeList;
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            Date dt_Time = null;
            int arr_Count = 0;
            try {
                dt_Time = sdf.parse(serverTime);

                if (sdf.parse("08:45 AM").getTime() <= dt_Time.getTime()) {
                    arr_Count = arr_Count + 1;
                }

                if (sdf.parse("10:45 AM").getTime() <= dt_Time.getTime()) {
                    arr_Count = arr_Count + 1;
                }

                if (sdf.parse("12:45 PM").getTime() <= dt_Time.getTime()) {
                    arr_Count = arr_Count + 1;
                }

                if (sdf.parse("02:45 PM").getTime() <= dt_Time.getTime()) {
                    arr_Count = arr_Count + 1;
                }

                if (sdf.parse("04:45 PM").getTime() <= dt_Time.getTime()) {
                    arr_Count = arr_Count + 1;
                }

                if (sdf.parse("06:45 PM").getTime() <= dt_Time.getTime()) {
                    arr_Count = arr_Count + 1;
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < arr_Count; i++) {
                new_time_List.add(timeList.get(i));
            }

        }

        BoothAdapter adapter = new BoothAdapter(PollDayActivity.this, timeList);
        dialog_ListView.setAdapter(adapter);

        dialog_ListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, TimeActivity.class);
            intent.putExtra("time", timeList.get(position));
            intent.putExtra("id", ids);
            intent.putExtra("assemblyId", assemblyId);
            intent.putExtra("totalElectors", totalElectors);
            switch (timeList.get(position)) {
                case "09:00 AM":
                    if (noOfVotesAt9AM.equals("0")) {
                        dialog.dismiss();
                        intent.putExtra("count", noOfVotesAt9AM);
                        intent.putExtra("percentage", noOfVotesPercentAt9AM);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Already Submitted", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case "10:00 AM":
                    if (noOfVotesAt10AM.equals("0")) {
                        dialog.dismiss();
                        intent.putExtra("count", noOfVotesAt10AM);
                        intent.putExtra("percentage", noOfVotesPercentAt10AM);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Already Submmited", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case "11:00 AM":
                    if (noOfVotesAt11AM.equals("0")) {
                        dialog.dismiss();
                        intent.putExtra("count", noOfVotesAt11AM);
                        intent.putExtra("percentage", noOfVotesPercentAt11AM);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Already Submmited", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case "12:00 PM":
                    if (noOfVotesAt12PM.equals("0")) {
                        dialog.dismiss();
                        intent.putExtra("count", noOfVotesAt12PM);
                        intent.putExtra("percentage", noOfVotesPercentAt12PM);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Already Submmited", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case "01:00 PM":
                    if (noOfVotesAt1PM.equals("0")) {
                        dialog.dismiss();
                        intent.putExtra("count", noOfVotesAt1PM);
                        intent.putExtra("percentage", noOfVotesPercentAt1PM);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Already Submmited", Toast.LENGTH_SHORT).show();
                    }

                    break;
                case "02:00 PM":
                    if (noOfVotesAt2PM.equals("0")) {
                        dialog.dismiss();
                        intent.putExtra("count", noOfVotesAt2PM);
                        intent.putExtra("percentage", noOfVotesPercentAt2PM);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Already Submmited", Toast.LENGTH_SHORT).show();
                    }

                    break;
                case "03:00 PM":
                    if (noOfVotesAt3PM.equals("0")) {
                        dialog.dismiss();
                        intent.putExtra("count", noOfVotesAt3PM);
                        intent.putExtra("percentage", noOfVotesPercentAt3PM);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Already Submmited", Toast.LENGTH_SHORT).show();
                    }

                    break;
                case "04:00 PM":
                    if (noOfVotesAt4PM.equals("0")) {
                        dialog.dismiss();
                        intent.putExtra("count", noOfVotesAt4PM);
                        intent.putExtra("percentage", noOfVotesPercentAt4PM);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Already Submmited", Toast.LENGTH_SHORT).show();
                    }

                    break;
                case "05:00 PM":
                    if (noOfVotesAt5PM.equals("0")) {
                        dialog.dismiss();
                        intent.putExtra("count", noOfVotesAt5PM);
                        intent.putExtra("percentage", noOfVotesPercentAt5PM);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Already Submmited", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case "06:00 PM":
                    if (noOfVotesAt6PM.equals("0")) {
                        dialog.dismiss();
                        intent.putExtra("count", noOfVotesAt6PM);
                        intent.putExtra("percentage", noOfVotesPercentAt6PM);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Already Submmited", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case "07:00 PM":
                    if (noOfVotesAt7PM.equals("0")) {
                        dialog.dismiss();
                        intent.putExtra("count", noOfVotesAt7PM);
                        intent.putExtra("percentage", noOfVotesPercentAt7PM);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Already Submmited", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        });

        close_alert.setOnClickListener(v -> dialog.dismiss());


        dialog.show();

    }

    @Override
    public void onRestart() {
        super.onRestart();
        startActivity(getIntent());
        finish();
    }

    private void poll_Datas() {
        session = new Session(this);
        user = session.getProfileManagerDetails();
        URL = "GetPollDayData/" + user.get("presidingOfficierId") + "/" + assemblyId;

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

                if (!result.equals("false")) {
                    serverTime = response.getString("serverTime");
                    JSONObject data = response.getJSONObject("data");
                    isMockPollConducted = data.getString("isMockPollConducted");
                    isPollingStarted = data.getString("isPollingStarted");

                    noOfVotesAt9AM = data.getString("noOfVotesAt9AM");
                    noOfVotesAt10AM = data.getString("noOfVotesAt10AM");
                    noOfVotesAt11AM = data.getString("noOfVotesAt11AM");
                    noOfVotesAt12PM = data.getString("noOfVotesAt12PM");
                    noOfVotesAt1PM = data.getString("noOfVotesAt1PM");
                    noOfVotesAt2PM = data.getString("noOfVotesAt2PM");
                    noOfVotesAt3PM = data.getString("noOfVotesAt3PM");
                    noOfVotesAt4PM = data.getString("noOfVotesAt4PM");
                    noOfVotesAt5PM = data.getString("noOfVotesAt5PM");

                    noOfVotesPercentAt9AM = data.getString("noOfVotesPercentAt9AM");
                    noOfVotesPercentAt10AM = data.getString("noOfVotesPercentAt10AM");
                    noOfVotesPercentAt11AM = data.getString("noOfVotesPercentAt11AM");
                    noOfVotesPercentAt12PM = data.getString("noOfVotesPercentAt12PM");
                    noOfVotesPercentAt1PM = data.getString("noOfVotesPercentAt1PM");
                    noOfVotesPercentAt2PM = data.getString("noOfVotesPercentAt2PM");
                    noOfVotesPercentAt3PM = data.getString("noOfVotesPercentAt3PM");
                    noOfVotesPercentAt4PM = data.getString("noOfVotesPercentAt4PM");
                    noOfVotesPercentAt5PM = data.getString("noOfVotesPercentAt5PM");

                    isPollingCompleted = data.getString("isPollingCompleted");

                    totalElectors = data.getString("totalElectors");
                    totalMaleVotesPolled = data.getString("totalMaleVotesPolled");
                    totalFemaleVotesPolled = data.getString("totalFemaleVotesPolled");
                    totalTGVotesPolled = data.getString("totalTGVotesPolled");

//                    noOfVotesAt6PM = data.getString("noofVotesAt6PM");
//                    noOfVotesAt7PM = data.getString("noofVotesAt7PM");
//                    noOfVotesPercentAt6PM = data.getString("noofVotesPercentAt6PM");
//                    noOfVotesPercentAt7PM = data.getString("noofVotesPercentAt7PM");

                    int total_value = Integer.parseInt(totalMaleVotesPolled) +
                            Integer.parseInt(totalFemaleVotesPolled) +
                            Integer.parseInt(totalTGVotesPolled);

                    totalVotesPolled = String.valueOf(total_value);

                    if (isMockPollConducted.equals("true") || isMockPollConducted.equals("false")) {
                        mock_poll.setVisibility(View.GONE);
                    } else {
                        mock_poll.setVisibility(View.VISIBLE);
                    }

                    pollStartedSession = new PollStartedSession(this);

                    if (isPollingStarted.equals("true")) {
                        pollStartedSession.createPollStartedSession(isPollingStarted, isPollingCompleted);
                        Intent intent = new Intent(this, NotificationService.class);
                        startService(intent);
                    }

                    if (isPollingCompleted.equals("true")) {
                        final_btn.setVisibility(View.VISIBLE);
                        Intent intent = new Intent(this, NotificationService.class);
                        stopService(intent);
                    } else {
                        final_btn.setVisibility(View.GONE);
                    }
                }else{
                    serverTime = response.getString("serverTime");
                }
                pDialog.dismiss();
            } catch (Exception ignored) { }
        }, error -> Log.e("MAIN_ACTIVITY", "Error :" + error.getMessage()));
        mRequestQueue.add(mJsonRequest);
    }

    private void init() {
        mock_poll = findViewById(R.id.mock_poll);
        poll_started = findViewById(R.id.poll_started);

        dropdown = findViewById(R.id.dropdown);
        select_time = findViewById(R.id.select_time);

        back = findViewById(R.id.back);
        home = findViewById(R.id.home);

        poll_completed = findViewById(R.id.poll_completed);
        final_btn = findViewById(R.id.final_btn);
    }

}
