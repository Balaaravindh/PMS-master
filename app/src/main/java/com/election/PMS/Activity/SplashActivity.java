package com.election.PMS.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.election.PMS.Common.NotificationService;
import com.election.PMS.Common.PollStartedSession;
import com.election.PMS.Common.Session;
import com.election.PMS.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class SplashActivity extends AppCompatActivity {


    ActionBar actionBar;
    private static int SPLASH_TIME_OUT = 3000;

    public static Session session;
    public static PollStartedSession pollStartedSession;
    public static HashMap<String, String> pollvalue;
    public static HashMap<String, String> user;
    Date currentDate = Calendar.getInstance().getTime();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Hide Status Bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Hide Action Bar
        actionBar = getSupportActionBar();
        actionBar.hide();

        pollStartedSession = new PollStartedSession(this);
        pollvalue = pollStartedSession.getPollStartedSession();

        session = new Session(this);
        user = session.getProfileManagerDetails();

        new Handler().postDelayed(() -> {
            String User_ID = user.get("presidingOfficierId");
            String User_Type = user.get("type");
            if (User_ID == null || User_ID == "null") {
                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
                finish();
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String sessionDate = sdf.format(currentDate);
                if(!user.get("sessionDate").equals(sessionDate)){
                    session.clear_Session();
                }else {
                    String isPollingStarted = pollvalue.get("isPollingStarted");
                    String isPollingCompleted = pollvalue.get("isPollingCompleted");

                    if (isPollingStarted != null &&
                            !isPollingStarted.equals(null) &&
                            !isPollingStarted.equals("null") &&
                            !isPollingStarted.equals("null") &&
                            isPollingCompleted != null &&
                            !isPollingCompleted.equals(null) &&
                            !isPollingCompleted.equals("null") &&
                            !isPollingCompleted.equals("null")) {

                        if(isPollingStarted.equals("true") && !isPollingCompleted.equals("true")){
                            startService(new Intent(this, NotificationService.class));
                        }else{
                            stopService(new Intent(this, NotificationService.class));
                        }
                    }
                    Intent i = new Intent(this, AC_PC_Activity.class);
                    startActivity(i);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);

    }
}
