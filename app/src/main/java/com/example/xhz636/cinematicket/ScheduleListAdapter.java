package com.example.xhz636.cinematicket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ScheduleListAdapter extends BaseAdapter {

    private Context context;
    private List<ScheduleInfo> schedules;
    private LayoutInflater layoutInflater;

    private TextView textView_Time;
    private TextView textView_Hall;
    private TextView textView_Price;

    ScheduleListAdapter(Context context, List<ScheduleInfo> schedules) {
        this.context = context;
        this.schedules = schedules;
        layoutInflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return schedules.size();
    }

    @Override
    public Object getItem(int position) {
        return schedules.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.listview_schedule, null);
        }
        textView_Time = (TextView)convertView.findViewById(R.id.schedule_item_time);
        textView_Hall = (TextView)convertView.findViewById(R.id.schedule_item_hall);
        textView_Price = (TextView)convertView.findViewById(R.id.schedule_item_price);
        String time = "时间：" + schedules.get(position).getBegintime() + "-" + schedules.get(position).getEndtime();
        textView_Time.setText(time);
        String hall = schedules.get(position).getHall() + "-" + schedules.get(position).getDimension();
        textView_Hall.setText(hall);
        String ticket = "价格：" + schedules.get(position).getPrice() + "   剩余：" + schedules.get(position).getSurplus() + "张";
        textView_Price.setText(ticket);
        return convertView;
    }

}
