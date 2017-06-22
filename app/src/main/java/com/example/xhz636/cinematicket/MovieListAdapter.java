package com.example.xhz636.cinematicket;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieListAdapter extends BaseAdapter {

    private Context context;
    private List<MovieInfo> movies;
    private LayoutInflater layoutInflater;
    private Map<String, Bitmap> maps = new HashMap<>();

    private ImageView imageView_Photo;
    private TextView textView_Name;
    private TextView textView_Score_Type;
    private TextView textView_Abstract;
    private TextView textView_Time;

    MovieListAdapter(Context context, List<MovieInfo> movies) {
        this.context = context;
        this.movies = movies;
        layoutInflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Object getItem(int position) {
        return movies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.listview_movie, null);
        }
        imageView_Photo = (ImageView)convertView.findViewById(R.id.movie_item_photo);
        textView_Name = (TextView)convertView.findViewById(R.id.movie_item_name);
        textView_Score_Type = (TextView)convertView.findViewById(R.id.movie_item_score_type);
        textView_Abstract = (TextView)convertView.findViewById(R.id.movie_item_abstract);
        textView_Time = (TextView)convertView.findViewById(R.id.movie_item_time);
        imageView_Photo.setImageBitmap(getHttpBitmap(movies.get(position).getPhoto()));
        textView_Name.setText(movies.get(position).getName());
        String score = "评分：" + movies.get(position).getScore();
        String type = "类型：" + movies.get(position).getType();
        textView_Score_Type.setText(score + "   " + type);
        String abstra = "简介：" + movies.get(position).getAbstra();
        textView_Abstract.setText(abstra);
        String duration = "时长：" + movies.get(position).getDuration();
        String showtime = "上映时间：" + movies.get(position).getShowtime();
        textView_Time.setText(duration + "   " + showtime);
        return convertView;
    }

    private Bitmap getHttpBitmap(final String url){
        if (maps.containsKey(url))
            return maps.get(url);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    URL urltemp = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection)urltemp.openConnection();
                    connection.setConnectTimeout(6000);
                    connection.setDoInput(true);
                    connection.setUseCaches(false);
                    connection.connect();
                    InputStream inputStream = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    maps.put(url, bitmap);
                    inputStream.close();
                }catch(Exception e){
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
        return maps.get(url);
    }

}
