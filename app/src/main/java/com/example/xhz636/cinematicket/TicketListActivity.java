package com.example.xhz636.cinematicket;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.zxing.WriterException;

import java.util.ArrayList;
import java.util.List;

public class TicketListActivity extends AppCompatActivity {

    private String userid;
    private ListView listView;
    private List<TicketInfo> tickets;

    private TicketDatabaseHelper tickethelper;
    private SQLiteDatabase ticketdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticketlist);
        Intent intent = getIntent();
        userid = intent.getStringExtra("userid");
        listView = (ListView)findViewById(R.id.list_ticket_item);
        tickets = getTickets();
        listView.setAdapter(new TicketListAdapter(this, tickets));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(TicketListActivity.this, QRCodeActivity.class);
                intent.putExtra("movie", tickets.get(position).getMovie());
                intent.putExtra("ordernumber", tickets.get(position).getOrdernumber());
                startActivity(intent);
            }
        });
    }

    private List<TicketInfo> getTickets() {
        List<TicketInfo> lists = new ArrayList<>();
        tickethelper = new TicketDatabaseHelper(this, "ticket.db", null, 1);
        ticketdb = tickethelper.getWritableDatabase();
        Cursor cursor = ticketdb.query(
                "ticket",
                null,
                "userid = ?",
                new String[] { userid },
                null,
                null,
                null);
        if(cursor != null) {
            if(cursor.moveToFirst()) {
                do {
                    String movie = cursor.getString(cursor.getColumnIndex("movie"));
                    String cinema = cursor.getString(cursor.getColumnIndex("cinema"));
                    String begintime = cursor.getString(cursor.getColumnIndex("begintime"));
                    String hall = cursor.getString(cursor.getColumnIndex("hall"));
                    String dimension = cursor.getString(cursor.getColumnIndex("dimension"));
                    int row = cursor.getInt(cursor.getColumnIndex("row"));
                    int column = cursor.getInt(cursor.getColumnIndex("column"));
                    String ordernumber = cursor.getString(cursor.getColumnIndex("ordernumber"));
                    TicketInfo ticket = new TicketInfo(movie, cinema, begintime, hall, dimension, row, column, ordernumber);
                    lists.add(ticket);
                } while(cursor.moveToNext());
            }
            cursor.close();
        }
        return lists;
    }

}
