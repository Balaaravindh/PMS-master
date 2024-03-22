package com.election.poll_monitor_system.Common;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.election.poll_monitor_system.R;

import java.util.HashMap;

import static android.content.Context.NOTIFICATION_SERVICE;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class PollStartedSession {

    private static final String KEy_isPollingStarted = "isPollingStarted";
    private static final String KEy_isPollingCompleted = "isPollingCompleted";

    private static String PREF_NAME = "PollStartedValue";
    private static String IS_LOGIN = "IsPollStartedValue";

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    HashMap<String, String> user;

    public PollStartedSession(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createPollStartedSession(String isPollingStarted, String isPollingCompleted) {
        editor.putBoolean(IS_LOGIN, true);

        editor.putString(KEy_isPollingStarted, isPollingStarted);
        editor.putString(KEy_isPollingCompleted, isPollingCompleted);

        editor.commit();
    }

    public HashMap<String, String> getPollStartedSession() {
        user = new HashMap<String, String>();

        user.put(KEy_isPollingStarted, pref.getString(KEy_isPollingStarted, null));
        user.put(KEy_isPollingCompleted, pref.getString(KEy_isPollingCompleted, null));

        return user;
    }

    public void clear_Session() {
        editor.clear();
        editor.commit();
    }

    public void issueNotification()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            makeNotificationChannel("CHANNEL_1", "Example channel", NotificationManager.IMPORTANCE_DEFAULT);
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(_context, "CHANNEL_1");

        notification
                .setSmallIcon(R.mipmap.logo)
                .setContentTitle("Alert!")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("You need to enter the value for time slot."))
                .setContentText("You need to enter the value for time slot.")
                .setNumber(3);

        NotificationManager notificationManager = (NotificationManager) _context.getSystemService(NOTIFICATION_SERVICE);

        assert notificationManager != null;
        notificationManager.notify(1, notification.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void makeNotificationChannel(String id, String name, int importance)
    {
        NotificationChannel channel = new NotificationChannel(id, name, importance);
        channel.setShowBadge(true);

        NotificationManager notificationManager = (NotificationManager) _context.getSystemService(NOTIFICATION_SERVICE);

        assert notificationManager != null;
        notificationManager.createNotificationChannel(channel);
    }

}
