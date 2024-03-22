package com.election.poll_monitor_system.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.election.poll_monitor_system.R;
import java.util.ArrayList;
import java.util.List;

public class BoothAdapter extends BaseAdapter {

    List<String> mainListDataList;
    ViewHolder holder;

    private Context context;

    public BoothAdapter(Context context, ArrayList<String> mainListDataList) {
        this.context = context;
        this.mainListDataList = mainListDataList;
    }

    @Override
    public int getCount() {
        return mainListDataList.size();
    }

    @Override
    public String getItem(int position) {
        return mainListDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.alert_list_single, null);

            holder = new ViewHolder();

            holder.alert_title = convertView.findViewById(R.id.alert_title);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.alert_title.setText(mainListDataList.get(position));

        return convertView;
    }

    private class ViewHolder {
        TextView alert_title;
    }

}
