package com.election.poll_monitor_system.Common;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.election.poll_monitor_system.Activity.LoginActivity;

import java.util.HashMap;

public class Session {

    private static final String KEy_presidingOfficierId = "presidingOfficierId";
    private static final String KEy_officierName = "officierName";
    private static final String KEy_title = "title";
    private static final String KEy_titleName = "titleName";
    private static final String KEy_mobileNo = "mobileNo";
    private static final String KEy_email = "email";
    private static final String KEy_isActive = "isActive";
    private static final String KEy_isFirstLoggedIn = "isFirstLoggedIn";
    private static final String KEy_districtId = "districtId";
    private static final String KEy_boothId = "boothId";
    private static final String KEy_assemblyConstituencyId = "assemblyConstituencyId";

    private static final String KEy_SessionDate = "sessionDate";

    private static String PREF_NAME = "ProfileManage";
    private static String IS_LOGIN = "IsProfileManageIn";

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    HashMap<String, String> user;

    public Session(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public void createProfileManageSession(String presidingOfficierId,
                                           String officierName,
                                           String title,
                                           String titleName,
                                           String mobileNo,
                                           String email,
                                           String isActive,
                                           String isFirstLoggedIn,
                                           String districtId,
                                           String boothId,
                                           String assemblyConstituencyId,
                                           String sessionDate) {
        editor.putBoolean(IS_LOGIN, true);

        editor.putString(KEy_presidingOfficierId, presidingOfficierId);
        editor.putString(KEy_officierName, officierName);
        editor.putString(KEy_title, title);
        editor.putString(KEy_titleName, titleName);
        editor.putString(KEy_mobileNo, mobileNo);
        editor.putString(KEy_email, email);
        editor.putString(KEy_boothId, email);
        editor.putString(KEy_isActive, isActive);
        editor.putString(KEy_isFirstLoggedIn, isFirstLoggedIn);
        editor.putString(KEy_districtId, districtId);
        editor.putString(KEy_boothId, boothId);
        editor.putString(KEy_assemblyConstituencyId, assemblyConstituencyId);

        editor.putString(KEy_SessionDate, sessionDate);

        editor.commit();
    }

    public HashMap<String, String> getProfileManagerDetails() {
        user = new HashMap<>();

        user.put(KEy_presidingOfficierId, pref.getString(KEy_presidingOfficierId, null));
        user.put(KEy_officierName, pref.getString(KEy_officierName, null));
        user.put(KEy_title, pref.getString(KEy_title, null));
        user.put(KEy_titleName, pref.getString(KEy_titleName, null));
        user.put(KEy_mobileNo, pref.getString(KEy_mobileNo, null));
        user.put(KEy_email, pref.getString(KEy_email, null));
        user.put(KEy_isActive, pref.getString(KEy_isActive, null));
        user.put(KEy_isFirstLoggedIn, pref.getString(KEy_isFirstLoggedIn, null));
        user.put(KEy_districtId, pref.getString(KEy_districtId, null));
        user.put(KEy_boothId, pref.getString(KEy_boothId, null));
        user.put(KEy_assemblyConstituencyId, pref.getString(KEy_assemblyConstituencyId, null));

        user.put(KEy_SessionDate, pref.getString(KEy_SessionDate, null));

        return user;
    }

    public void clear_Session() {
        editor.clear();
        editor.commit();

        Intent intent = new Intent(_context, LoginActivity.class);
        _context.startActivity(intent);
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
}
