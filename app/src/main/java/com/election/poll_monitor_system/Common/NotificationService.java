package com.election.poll_monitor_system.Common;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends Service {

    public static final long NOTIFY_INTERVAL = 3600000;
    private Handler mHandler = new Handler();
    private Timer mTimer = null;

    PollStartedSession pollStartedSession;
    HashMap<String, String> pollvalue;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {

        if (mTimer != null) {
            mTimer.cancel();
        } else {
            mTimer = new Timer();
        }

        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);
    }

    class TimeDisplayTimerTask extends TimerTask {
        @Override
        public void run() {
            mHandler.post(() -> getPollValue());
        }
    }

    private void getPollValue() {

        pollStartedSession = new PollStartedSession(this);
        pollvalue = pollStartedSession.getPollStartedSession();

        if (pollvalue.get("isPollingStarted").equals("true")) {
            pollStartedSession.issueNotification();
        } else {

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        getPollValue();
    }
}
