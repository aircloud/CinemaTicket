package com.example.xhz636.cinematicket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class CinemaListAdapter extends BaseAdapter {

    private Context context;
    private List<CinemaInfo> cinemas;
    private LayoutInflater layoutInflater;

    private TextView textView_Name;
    private TextView textView_Price;
    private TextView textView_Address;

    CinemaListAdapter(Context context, List<CinemaInfo> cinemas) {
        this.context = context;
        this.cinemas = cinemas;
        layoutInflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return cinemas.size();
    }

    @Override
    public Object getItem(int position) {
        return cinemas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.listview_cinema, null);
        }
        textView_Name = (TextView)convertView.findViewById(R.id.cinema_item_name);
        textView_Price = (TextView)convertView.findViewById(R.id.cinema_item_price);
        textView_Address = (TextView)convertView.findViewById(R.id.cinema_item_address);
        textView_Name.setText(cinemas.get(position).getName());
        String price = cinemas.get(position).getBeginprice() + "元起";
        textView_Price.setText(price);
        textView_Address.setText(cinemas.get(position).getAddress());
        return convertView;
    }

}
