package com.election.poll_monitor_system.Common;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class ElectorsValueSave {

    private static final String KEy_electors = "electors";
    private static final String KEy_is8PMEnabled = "is8PMEnabled";

    private static String PREF_NAME = "ElectorsValueSave";
    private static String IS_LOGIN = "ISelectors";

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    HashMap<String, String> user;


    public ElectorsValueSave(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public void createProfileManageSession(String electors,
                                           String is8PMEnabled) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEy_electors, electors);
        editor.putString(KEy_is8PMEnabled, is8PMEnabled);
        editor.commit();
    }

    public HashMap<String, String> getElectorsData() {
        user = new HashMap<String, String>();
        user.put(KEy_electors, pref.getString(KEy_electors, null));
        user.put(KEy_is8PMEnabled, pref.getString(KEy_is8PMEnabled, null));
        return user;
    }

    public void clear_Session() {
        editor.clear();
        editor.commit();
    }
}
