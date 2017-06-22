package com.example.xhz636.cinematicket;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {

    private ListView listView;

    private List<ScheduleInfo> schedules;
    private GlobalData globalData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_cinema_all);
        globalData = (GlobalData)getApplication();
        Intent intent = getIntent();
        String movieid = intent.getStringExtra("movieid");
        String cinemaid = intent.getStringExtra("cinemaid");
        final String movie = intent.getStringExtra("movie");
        final String cinema = intent.getStringExtra("cinema");
        listView = (ListView)findViewById(R.id.all_list);
        schedules = getSchedule(cinemaid, movieid);
        listView.setAdapter(new ScheduleListAdapter(this, schedules));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("arrangeid", schedules.get(position).getId());
                intent.putExtra("movie", movie);
                intent.putExtra("cinema", cinema);
                intent.putExtra("begintime", schedules.get(position).getBegintime());
                intent.putExtra("hall", schedules.get(position).getHall());
                intent.putExtra("dimension", schedules.get(position).getDimension());
                intent.putExtra("price", schedules.get(position).getPrice());
                intent.putExtra("userid", globalData.getUserid());
                intent.putExtra("cookie", globalData.getCookie());
                intent.setClass(ScheduleActivity.this, ChooseTicketActivity.class);
                startActivity(intent);
            }
        });
    }

    private List<ScheduleInfo> getSchedule(final String cinemaid, final String movieid) {
        final List<ScheduleInfo> lists = new ArrayList<>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String geturl = "https://c.10000h.top/main/schedule/" + cinemaid + "/" + movieid;
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
                        Log.d("cinema", jsonString);
                        byteArrayOutputStream.close();
                        JSONObject jsonObject = new JSONObject(jsonString);
                        int success = jsonObject.optInt("success");
                        if (success == 2000) {
                            JSONArray value = jsonObject.optJSONArray("value");
                            for (int i = 0; i < value.length(); i++) {
                                JSONObject schedule = value.getJSONObject(i);
                                ScheduleInfo info = new ScheduleInfo();
                                info.setId(schedule.optInt("id"));
                                info.setBegintime(schedule.optString("begintime"));
                                info.setEndtime(schedule.optString("endtime"));
                                info.setDimension(schedule.optString("dimension"));
                                info.setHall(schedule.optString("hall"));
                                String price = schedule.optString("price");
                                info.setPrice(Float.valueOf(price.replaceAll("[^0-9\\.]", "")));
                                info.setSurplus(schedule.optInt("surplus"));
                                info.setCinemaid(schedule.optString("cinemaid"));
                                info.setMovieid(schedule.optString("movieid"));
                                lists.add(info);
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
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return lists;
    }

}
