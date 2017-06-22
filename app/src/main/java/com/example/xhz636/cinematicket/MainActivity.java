package com.example.xhz636.cinematicket;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private ViewPager viewPager;
    private MenuItem menuItem;
    private BottomNavigationView bottomNavigationView;
    private LocalActivityManager manager;

    private GlobalData globalData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        globalData = (GlobalData)getApplication();
        manager = new LocalActivityManager(this, true);
        manager.dispatchCreate(savedInstanceState);
        globalData.setManager(manager);
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.navigation_menu);
        disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_suggest:
                                viewPager.setCurrentItem(0);
                                break;
                            case R.id.menu_movie:
                                viewPager.setCurrentItem(1);
                                break;
                            case R.id.menu_cinema:
                                viewPager.setCurrentItem(2);
                                break;
                            case R.id.menu_user:
                                viewPager.setCurrentItem(3);
                                break;
                        }
                        return false;
                    }
                });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (menuItem != null) {
                    menuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                menuItem = bottomNavigationView.getMenu().getItem(position);
                menuItem.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        initData();
        setupViewPager(viewPager);
        viewPager.setCurrentItem(0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d("test", "super");
        Activity subActivity = globalData.getManager().getActivity("UserActivity");
        if (subActivity instanceof ActivityListener) {
            ActivityListener listener = (ActivityListener)subActivity;
            listener.activityListener(requestCode, resultCode, intent);
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    private void initData() {
        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        globalData.setUserid(sharedPreferences.getString("userid", ""));
        while (!getCookie("https://c.10000h.top/user/captcha")) {
            Toast.makeText(getApplicationContext(), "正在连接服务器...", Toast.LENGTH_LONG).show();
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        List<View> views = new ArrayList<>();
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, SuggestActivity.class);
        views.add(getView("SuggestActivity", intent));
        intent.setClass(MainActivity.this, MovieActivity.class);
        views.add(getView("MovieActivity", intent));
        intent.setClass(MainActivity.this, CinemaActivity.class);
        views.add(getView("CinemaActivity", intent));
        intent.setClass(MainActivity.this, UserActivity.class);
        views.add(getView("UserActivity", intent));
        ViewPagerAdapter adapter = new ViewPagerAdapter(views);
        viewPager.setAdapter(adapter);
    }

    private View getView(String id, Intent intent) {
        return manager.startActivity(id, intent).getDecorView();
    }

    private void disableShiftMode(BottomNavigationView navigationView) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) navigationView.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(i);
                itemView.setShiftingMode(false);
                itemView.setChecked(itemView.getItemData().isChecked());
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private boolean getCookie(final String url) {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String geturl = url;
                    Log.d("url", geturl);
                    URL url = new URL(geturl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(1000);
                    connection.connect();
                    int responsecode = connection.getResponseCode();
                    Log.d("response", String.valueOf(responsecode));
                    if (responsecode == HttpURLConnection.HTTP_OK) {
                        globalData.setCookie(connection.getHeaderField("Set-Cookie"));
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
        if (globalData.getCookie() != null) {
            cookieManager.setCookie("https://c.10000h.top/", globalData.getCookie());
            return true;
        }
        else {
            return false;
        }
    }

}
