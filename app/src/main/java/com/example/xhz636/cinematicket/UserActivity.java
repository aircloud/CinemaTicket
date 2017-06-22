package com.example.xhz636.cinematicket;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity implements ActivityListener {

    private ListView listView;
    private TextView textView;
    private Button button;

    private List<TicketInfo> tickets;
    private TicketDatabaseHelper tickethelper;
    private SQLiteDatabase ticketdb;
    private View.OnClickListener clickerLogin;
    private View.OnClickListener clickerLogout;

    private GlobalData globalData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        setTitle("个人中心");
        globalData = (GlobalData)getApplication();
        Log.d("datauser", globalData.getUserid());
        listView = (ListView)findViewById(R.id.list_ticket_item);
        textView = (TextView)findViewById(R.id.user_info) ;
        button = (Button)findViewById(R.id.user_log);
        initClicker();
        reloadData();
    }

    @Override
    public void activityListener(int requestCode, int resultCode, Intent intent) {
        Log.d("test", "listen");
        if (requestCode == 1 && resultCode == RESULT_OK) {
            setUserData();
        }
    }

    @Override
    public void reloadData() {
        if (globalData.getUserid() == null || globalData.getUserid().equals("")) {
            clearUserData();
        }
        else {
            Log.d("test", "userreload");
            setUserData();
        }
    }

    private void initClicker() {
        clickerLogin = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("userid", "");
                intent.putExtra("password", "");
                intent.setClass(UserActivity.this, LoginActivity.class);
                getParent().startActivityForResult(intent, 1);
            }
        };
        clickerLogout = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                globalData.setUserid("");
                SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userid", "");
                editor.commit();
                globalData.setUserid("");
                clearUserData();
            }
        };
    }

    private void setUserData() {
        String welcome = "欢迎！" + globalData.getUserid();
        textView.setText(welcome);
        button.setText(R.string.user_logout_button);
        button.setOnClickListener(clickerLogout);
        tickets = getTickets();
        listView.setAdapter(new TicketListAdapter(this, tickets));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(UserActivity.this, QRCodeActivity.class);
                intent.putExtra("movie", tickets.get(position).getMovie());
                intent.putExtra("ordernumber", tickets.get(position).getOrdernumber());
                startActivity(intent);
            }
        });
    }

    private void clearUserData() {
        textView.setText(R.string.prompt_user_log);
        button.setText(R.string.user_login_button);
        button.setOnClickListener(clickerLogin);
        if (tickets != null)
            tickets.clear();
        else
            tickets = new ArrayList<>();
        listView.setAdapter(new TicketListAdapter(UserActivity.this, tickets));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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
                new String[] { globalData.getUserid() },
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
