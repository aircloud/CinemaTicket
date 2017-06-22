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

public class CinemaActivity extends AppCompatActivity {

    private ListView listView;

    private List<CinemaInfo> cinemas;
    private GlobalData globalData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_cinema_all);
        globalData = (GlobalData)getApplication();
        Intent intent = getIntent();
        final String movieid = intent.getStringExtra("movieid");
        final String movie = intent.getStringExtra("movie");
        if (movie == null || movie.equals(""))
            setTitle("附近影院");
        else
            setTitle(movie);
        listView = (ListView)findViewById(R.id.all_list);
        cinemas = getCinemas(movieid);
        listView.setAdapter(new CinemaListAdapter(this, cinemas));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("cinemaid", cinemas.get(position).getCinemaid());
                intent.putExtra("cinema", cinemas.get(position).getName());
                if (movieid == null || movieid.equals("")) {
                    intent.setClass(CinemaActivity.this, MovieActivity.class);
                    startActivity(intent);
                }
                else {
                    intent.putExtra("movieid", movieid);
                    intent.putExtra("movie", movie);
                    intent.setClass(CinemaActivity.this, ScheduleActivity.class);
                    startActivityForResult(intent, 12);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 12 && resultCode == RESULT_OK) {
            finish();
        }
    }

    private List<CinemaInfo> getCinemas(final String movieid) {
        final List<CinemaInfo> lists = new ArrayList<>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String geturl;
                    if (movieid == null || movieid.equals(""))
                        geturl = "https://c.10000h.top/main/cinemas";
                    else
                        geturl = "https://c.10000h.top/main/cinemasformovie/" + movieid;
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
                                JSONObject cinema = value.getJSONObject(i);
                                CinemaInfo info = new CinemaInfo();
                                info.setId(cinema.optInt("id"));
                                info.setName(cinema.optString("name"));
                                info.setAddress(cinema.optString("address"));
                                info.setBeginprice((float)cinema.optDouble("beginprice"));
                                info.setCinemaid(cinema.optString("nm_cinema"));
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
