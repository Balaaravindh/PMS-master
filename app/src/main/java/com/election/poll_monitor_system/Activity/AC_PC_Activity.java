package com.election.poll_monitor_system.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.election.poll_monitor_system.Common.PollStartedSession;
import com.election.poll_monitor_system.Common.Session;
import com.election.poll_monitor_system.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class AC_PC_Activity extends AppCompatActivity {

    ActionBar actionBar;
    Button ac_btn, pc_btn;
    Button btn_exit;

    TextView welcome_text;

    Session session;
    HashMap<String, String> user;

    PollStartedSession pollStartedSession;

    Date currentTime = Calendar.getInstance().getTime();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ac__pc_);

        actionBar = getSupportActionBar();
        actionBar.hide();

        inti();

        session = new Session(this);
        user = session.getProfileManagerDetails();

        pollStartedSession = new PollStartedSession(this);

        welcome_text.setText(Html.fromHtml("Welcome, <b> " + user.get("title") + " " + user.get("officierName") + "</b>, Presiding Officer"));

        ac_btn.setOnClickListener(v -> {
            Intent intent = new Intent(AC_PC_Activity.this, MainActivity.class);
            intent.putExtra("title", "Assembly Constituency");
            intent.putExtra("electionACID", user.get("electionACID"));
            startActivity(intent);
        });

        pc_btn.setOnClickListener(v -> {
            Intent intent = new Intent(AC_PC_Activity.this, MainActivity.class);
            intent.putExtra("title", "Parliment Constituency");
            intent.putExtra("electionACID", user.get("electionPCID"));
            startActivity(intent);

        });

        btn_exit.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            session.clear_Session();
            pollStartedSession.clear_Session();
            finish();
        });

        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String strDate = dateFormat.format(currentTime);

        if(strDate.equals("05:10 PM")){
            Log.e("date", "Equalssss");
        }else{
            Log.e("date", "Else Equalssss");
        }

    }

    public void inti() {
        ac_btn = findViewById(R.id.ac_btn);
        pc_btn = findViewById(R.id.pc_btn);

        btn_exit = findViewById(R.id.btn_exit);

        welcome_text = findViewById(R.id.welcome_text);
    }

}
