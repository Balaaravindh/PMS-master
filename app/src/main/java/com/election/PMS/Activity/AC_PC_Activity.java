package com.election.PMS.Activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.election.PMS.Common.PollStartedSession;
import com.election.PMS.Common.Session;
import com.election.PMS.R;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class AC_PC_Activity extends AppCompatActivity {
    ActionBar actionBar;
    LinearLayout ac_pc_btnLayout;
    Button ac_btn, pc_btn, btn_que_status;
    Button btn_exit;
    TextView welcome_text, boothNumber_Name, boothNumber_NameTitle;
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
        requestPermission();
        session = new Session(this);
        user = session.getProfileManagerDetails();
        pollStartedSession = new PollStartedSession(this);
        if(user.get("type").equals("BLO")){
            btn_que_status.setVisibility(View.VISIBLE);
            boothNumber_Name.setVisibility(View.VISIBLE);
            boothNumber_NameTitle.setVisibility(View.VISIBLE);
            ac_pc_btnLayout.setVisibility(View.GONE);
            boothNumber_Name.setText(user.get("boothName"));
            welcome_text.setText(Html.fromHtml("Welcome, <b> " + user.get("title") + " " + user.get("officierName").toUpperCase() + "</b>, Booth Level Officer"));
        }else{
            ac_pc_btnLayout.setVisibility(View.VISIBLE);
            boothNumber_Name.setVisibility(View.GONE);
            btn_que_status.setVisibility(View.GONE);
            boothNumber_NameTitle.setVisibility(View.GONE);
            welcome_text.setText(Html.fromHtml("Welcome, <b> " + user.get("title") + " " + user.get("officierName").toUpperCase() + "</b>, Presiding Officer"));
        }

        if(user.get("parliamentConstituencyID").equals("0")) {
            pc_btn.setVisibility(View.GONE);
        }
        else {
            pc_btn.setVisibility(View.VISIBLE);
        }

        if(user.get("assemblyConstituencyId").equals("0")) {
            ac_btn.setVisibility(View.GONE);
        }
        else {
            ac_btn.setVisibility(View.VISIBLE);
        }

        ac_btn.setOnClickListener(v -> {
            Intent intent = new Intent(AC_PC_Activity.this, MainActivity.class);
            intent.putExtra("title", "Assembly Constituency");
            intent.putExtra("assemblyId", user.get("assemblyConstituencyId"));
            startActivity(intent);
            finish();
        });
        pc_btn.setOnClickListener(v -> {
            Intent intent = new Intent(AC_PC_Activity.this, MainActivity.class);
            intent.putExtra("title", "Parliment Constituency");
            intent.putExtra("assemblyId", user.get("parliamentConstituencyID"));
            startActivity(intent);
            finish();
        });
        btn_exit.setOnClickListener(v -> {
            session.clear_Session();
            pollStartedSession.clear_Session();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        btn_que_status.setOnClickListener(v -> {
            Intent intent = new Intent(this, QueStatusEntryActivity.class);
            startActivity(intent);
        });
    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.POST_NOTIFICATIONS,
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, 1);
    }

    public void inti() {
        ac_btn = findViewById(R.id.ac_btn);
        pc_btn = findViewById(R.id.pc_btn);
        btn_que_status = findViewById(R.id.btn_que_status);
        boothNumber_Name = findViewById(R.id.boothNumber_Name);
        boothNumber_NameTitle = findViewById(R.id.boothNumber_NameTitle);
        btn_exit = findViewById(R.id.btn_exit);
        ac_pc_btnLayout = findViewById(R.id.ac_pc_btnLayout);
        welcome_text = findViewById(R.id.welcome_text);
    }
}
