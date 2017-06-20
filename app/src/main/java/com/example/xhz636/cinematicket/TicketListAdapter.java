package com.example.xhz636.cinematicket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class TicketListAdapter extends BaseAdapter {

    private Context context;
    private List<TicketInfo> tickets;
    private LayoutInflater layoutInflater;
    private TextView textView_Moive;
    private TextView textView_Time;
    private TextView textView_Ciname;
    private TextView textView_Hall_Seat;

    TicketListAdapter(Context context, List<TicketInfo> tickets) {
        this.context = context;
        this.tickets = tickets;
        layoutInflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return tickets.size();
    }

    @Override
    public Object getItem(int position) {
        return tickets.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.listview_ticket, null);
        }
        textView_Moive = (TextView)convertView.findViewById(R.id.ticket_item_movie);
        textView_Time = (TextView)convertView.findViewById(R.id.ticket_item_time);
        textView_Ciname = (TextView)convertView.findViewById(R.id.ticket_item_cinema);
        textView_Hall_Seat = (TextView)convertView.findViewById(R.id.ticket_item_hall_seat);
        textView_Moive.setText(tickets.get(position).getMovie());
        String time = "开始时间：" + tickets.get(position).getBegintime();
        textView_Time.setText(time);
        textView_Ciname.setText(tickets.get(position).getCinema());
        String hall = tickets.get(position).getHall() + "-" + tickets.get(position).getDimension();
        String seat = tickets.get(position).getRow() + "排" + tickets.get(position).getColumn() + "座";
        textView_Hall_Seat.setText(hall + "   " + seat);
        return convertView;
    }
}
