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

public class MovieActivity extends AppCompatActivity {

    private ListView listView;

    private List<MovieInfo> movies;
    private GlobalData globalData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_cinema_all);
        globalData = (GlobalData)getApplication();
        Intent intent = getIntent();
        final String cinemaid = intent.getStringExtra("cinemaid");
        final String cinema = intent.getStringExtra("cinema");
        if (cinema == null || cinema.equals(""))
            setTitle("近期电影");
        else
            setTitle(cinema);
        listView = (ListView)findViewById(R.id.all_list);
        movies = getMovies(cinemaid);
        listView.setAdapter(new MovieListAdapter(this, movies));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("movieid", movies.get(position).getMovieid());
                intent.putExtra("movie", movies.get(position).getName());
                if (cinemaid == null || cinemaid.equals("")) {
                    intent.setClass(MovieActivity.this, CinemaActivity.class);
                    startActivity(intent);
                }
                else {
                    intent.putExtra("cinemaid", cinemaid);
                    intent.putExtra("cinema", cinema);
                    intent.setClass(MovieActivity.this, ScheduleActivity.class);
                    startActivityForResult(intent, 11);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 11 && resultCode == RESULT_OK) {
            finish();
        }
    }

    private List<MovieInfo> getMovies(final String cinemaid) {
        final List<MovieInfo> lists = new ArrayList<>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String geturl;
                    if (cinemaid == null || cinemaid.equals(""))
                        geturl = "https://c.10000h.top/main/movies";
                    else
                        geturl = "https://c.10000h.top/main/moviesforcinema/" + cinemaid;
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
                                JSONObject movie = value.getJSONObject(i);
                                MovieInfo info = new MovieInfo();
                                info.setId(movie.optInt("id"));
                                info.setName(movie.optString("name"));
                                info.setAbstra(movie.optString("abstract"));
                                info.setScore((float)movie.optDouble("score"));
                                info.setType(movie.optString("type"));
                                info.setDuration(movie.optString("duration"));
                                info.setShowtime(movie.optString("showtime"));
                                info.setPhoto(movie.optString("photo"));
                                info.setMovieid(movie.optString("nm_movieid"));
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
