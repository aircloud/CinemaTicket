package com.example.xhz636.cinematicket;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ChooseTicketActivity extends AppCompatActivity {

    private TextView textView_TicketInfo;
    private SeatView seatView_Seat;
    private Button button_Buy;

    private String movie;
    private String cinema;
    private String begintime;
    private String hall;
    private String dimension;
    private int arrangeid;
    private float price;
    private int window_width;
    private GlobalData globalData;

    private TicketDatabaseHelper tickethelper;
    private SQLiteDatabase ticketdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_ticket);
        globalData = (GlobalData)getApplication();
        Intent intent = getIntent();
        movie = intent.getStringExtra("movie");
        setTitle(movie);
        textView_TicketInfo = (TextView)findViewById(R.id.ticket_info);
        seatView_Seat = (SeatView)findViewById(R.id.seat_choose_view);
        button_Buy = (Button)findViewById(R.id.ticket_buy);
        cinema = intent.getStringExtra("cinema");
        begintime = intent.getStringExtra("begintime");
        String html = "<h4>" + cinema + "</h4><h6><font color='#ff0000'>开始时间：" + begintime + "</font><h6>";
        textView_TicketInfo.setText(Html.fromHtml(html));
        hall = intent.getStringExtra("hall");
        dimension = intent.getStringExtra("dimension");
        seatView_Seat.setScreenName(hall + "-" + dimension);
        arrangeid = intent.getIntExtra("arrangeid", 0);
        price = intent.getFloatExtra("price", 0);
        WindowManager windowManager = getWindowManager();
        window_width = windowManager.getDefaultDisplay().getWidth();
        seatView_Seat.setSeatClicker(new SeatView.SeatClicker() {

            @Override
            public void checked(int row, int column) {

            }

            @Override
            public void unCheck(int row, int column) {

            }

            @Override
            public String[] checkedSeatTxt(int row, int column) {
                return null;
            }

        });
        seatView_Seat.setData(8,10);
        initTicket();
        button_Buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<Integer> selects = seatView_Seat.getSelectedSeat();
                if (selects.size() == 0) {
                    Toast.makeText(getApplicationContext(), "您尚未选座！", Toast.LENGTH_LONG).show();
                    return;
                }
                else if (globalData.getUserid() == null || globalData.getUserid().equals("")) {
                    Toast.makeText(getApplicationContext(), "请先登录！", Toast.LENGTH_LONG).show();
                    Intent loginIntent = new Intent();
                    loginIntent.putExtra("userid", "");
                    loginIntent.putExtra("password", "");
                    loginIntent.setClass(ChooseTicketActivity.this, LoginActivity.class);
                    startActivityForResult(loginIntent, 1);
                    return;
                }
                final PayDialog payDialog = new PayDialog(getWindow().getContext());
                payDialog.show();
                payDialog.setMoney(price * selects.size());
                payDialog.setDialogWidth((int)(window_width * 0.8));
                payDialog.setPay(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!checkBank("test", "test", price * selects.size())) {
                            Toast.makeText(getApplicationContext(), "密码错误！", Toast.LENGTH_LONG).show();
                        }
                        else {
                            for (int i = 0; i < selects.size(); i++) {
                                buyTicket(selects.get(i));
                                seatView_Seat.setSeat(selects.get(i));
                            }
                            seatView_Seat.clearSelect();
                            payDialog.cancel();
                            Toast.makeText(getApplicationContext(), "购票成功！", Toast.LENGTH_LONG).show();
                            reloadUserActivity();
                            finish();
                        }
                    }
                });
            }
        });
        tickethelper = new TicketDatabaseHelper(this, "ticket.db", null, 1);
        ticketdb = tickethelper.getWritableDatabase();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d("test", "choose");
        Activity userActivity = globalData.getManager().getActivity("UserActivity");
        if (userActivity instanceof ActivityListener) {
            ActivityListener listener = (ActivityListener)userActivity;
            listener.activityListener(requestCode, resultCode, intent);
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    private void reloadUserActivity() {
        Activity userActivity = globalData.getManager().getActivity("UserActivity");
        if (userActivity instanceof ActivityListener) {
            Log.d("test", "choosereload");
            ActivityListener listener = (ActivityListener)userActivity;
            listener.reloadData();
        }
    }

    private void initTicket() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String geturl = "https://c.10000h.top/main/getorderseats/" + arrangeid;
                    Log.d("url", geturl);
                    URL url = new URL(geturl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Cookie", globalData.getCookie());
                    connection.setConnectTimeout(1000);
                    connection.connect();
                    int responsecode = connection.getResponseCode();
                    Log.d("response", String.valueOf(responsecode));
                    if (responsecode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = inputStream.read(buffer)) != -1) {
                            byteArrayOutputStream.write(buffer, 0, len);
                        }
                        String jsonString = byteArrayOutputStream.toString();
                        Log.d("responsedata", jsonString);
                        byteArrayOutputStream.close();
                        JSONObject jsonObject = new JSONObject(jsonString);
                        int success = jsonObject.optInt("success");
                        if (success == 2000) {
                            JSONArray value = jsonObject.optJSONArray("value");
                            for (int i = 0; i < value.length(); i++) {
                                JSONObject temp = value.getJSONObject(i);
                                int seatid = temp.optInt("seatid");
                                seatView_Seat.setSeat(seatid);
                            }
                        }
                    }
                    connection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private boolean checkBank(String id, String password, float price) {
        // Virtual Bank API to check id and password
        // If it's right, pay the price and return true
        return true;
    }

    private void buyTicket(final int seatid) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String geturl = "https://c.10000h.top/main/order/" + arrangeid + "/" + globalData.getUserid() + "/" + seatid;
                    Log.d("url", geturl);
                    URL url = new URL(geturl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Cookie", globalData.getCookie());
                    connection.setConnectTimeout(1000);
                    connection.connect();
                    int responsecode = connection.getResponseCode();
                    Log.d("response", String.valueOf(responsecode));
                    if (responsecode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = inputStream.read(buffer)) != -1) {
                            byteArrayOutputStream.write(buffer, 0, len);
                        }
                        String jsonString = byteArrayOutputStream.toString();
                        Log.d("responsedata", jsonString);
                        byteArrayOutputStream.close();
                        JSONObject jsonObject = new JSONObject(jsonString);
                        int success = jsonObject.optInt("success");
                        if (success == 2000)
                        {
                            String ordernumber = jsonObject.optString("value");
                            ContentValues values = new ContentValues();
                            values.put("userid", globalData.getUserid());
                            values.put("movie", movie);
                            values.put("cinema", cinema);
                            values.put("begintime", begintime);
                            values.put("hall", hall);
                            values.put("dimension", dimension);
                            values.put("row", seatid / 10 + 1);
                            values.put("column", seatid % 10 + 1);
                            values.put("ordernumber", ordernumber);
                            ticketdb.insert("ticket", null, values);
                        }
                    }
                    connection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
