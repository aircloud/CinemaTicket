package com.example.xhz636.cinematicket;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class SuggestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_cinema_all);
        Button button = (Button)findViewById(R.id.button);
        final GlobalData globalData = (GlobalData)getApplication();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("arrangeid", 92);
                intent.putExtra("movie", "摔跤吧！爸爸");
                intent.putExtra("cinema", "横店电影城(杭州下沙店)");
                intent.putExtra("begintime", "12:00");
                intent.putExtra("hall", "5号厅");
                intent.putExtra("dimension", "3D");
                intent.putExtra("price", (float)19.9);
                intent.putExtra("userid", globalData.getUserid());
                intent.putExtra("cookie", globalData.getCookie());
                intent.setClass(SuggestActivity.this, ChooseTicketActivity.class);
                startActivity(intent);
            }
        });
    }

}
