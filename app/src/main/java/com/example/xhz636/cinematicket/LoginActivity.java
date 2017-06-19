package com.example.xhz636.cinematicket;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    private EditText editText_UserPhone;
    private EditText editText_Password;
    private EditText editText_Captcha;
    private WebView webView_Captcha;
    private ImageButton imageButton_Captcha;
    private Button button_Login;
    private Button button_Indent;

    private String cookie = null;
    private boolean captcha_check = false;
    private boolean login_result = false;
    private int id;
    private String name, phone, registertime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editText_UserPhone = (EditText)findViewById(R.id.login_user_phone);
        editText_Password = (EditText)findViewById(R.id.login_password);
        editText_Captcha = (EditText)findViewById(R.id.login_captcha_text);
        webView_Captcha = (WebView)findViewById(R.id.login_captcha_image);
        webView_Captcha.setVerticalScrollBarEnabled(false);
        webView_Captcha.setHorizontalScrollBarEnabled(false);
        getCaptcha();
        imageButton_Captcha = (ImageButton)findViewById(R.id.login_captcha_refresh);
        button_Login = (Button)findViewById(R.id.login_button);
        button_Indent = (Button)findViewById(R.id.login_indent);
        button_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String captcha = editText_Captcha.getText().toString();
                if (checkCaptcha(captcha)) {
                    String user_phone = editText_UserPhone.getText().toString();
                    String password = editText_Password.getText().toString();
                    if (login(user_phone, password, captcha)) {
                        Toast.makeText(getApplicationContext(), "登录成功！", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "登录失败！", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    editText_Captcha.setError("验证码错误");
                }
            }
        });
        imageButton_Captcha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCaptcha();
            }
        });
        button_Indent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
        editText_Captcha.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editText_Captcha.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        /*Intent intent = new Intent();
        intent.putExtra("arrangeid", 92);
        intent.putExtra("movie", "摔跤吧！爸爸");
        intent.putExtra("cinema", "横店电影城(杭州下沙店)");
        intent.putExtra("begintime", "12:00");
        intent.putExtra("hall", "5号厅");
        intent.putExtra("dimension", "3D");
        intent.putExtra("price", (float)19.9);
        intent.putExtra("userid", "xhz636");
        intent.putExtra("cookie", cookie);
        intent.setClass(LoginActivity.this, ChooseTicketActivity.class);
        startActivity(intent);
        finish();*/
    }

    private void getCaptcha() {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookie = cookieManager.getCookie("https://c.10000h.top/");
        Log.d("cookie", cookie);
        webView_Captcha.loadUrl("https://c.10000h.top/user/captcha");
    }

    private boolean checkCaptcha(final String code) {
        captcha_check = false;
        if (checkCaptchaFormat(code)) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String geturl = "https://c.10000h.top/user/captchatest/" + code;
                        Log.d("url", geturl);
                        URL url = new URL(geturl);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setRequestProperty("Cookie", cookie);
                        Log.d("cookie", cookie);
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
                            JSONObject value = jsonObject.optJSONObject("value");
                            boolean result = value.optBoolean("result");
                            if (success == 2000 && result)
                                captcha_check = true;
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
        return captcha_check;
    }

    private boolean login(final String user_phone, final String password, final String code) {
        login_result = false;
        if (checkUserPhoneFormat(user_phone) && checkPasswordFormat(password)) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject jsonParam = new JSONObject();
                        jsonParam.put("phone_name", user_phone);
                        jsonParam.put("password", password);
                        String posturl = "https://c.10000h.top/user/login/" + code;
                        Log.d("url", posturl);
                        URL url = new URL(posturl);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.setDoOutput(true);
                        connection.setRequestMethod("POST");
                        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        connection.setRequestProperty("Cookie", cookie);
                        connection.setConnectTimeout(1000);
                        connection.connect();
                        OutputStream output = connection.getOutputStream();
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, "UTF-8"));
                        writer.write(jsonParam.toString());
                        Log.d("postdata", jsonParam.toString());
                        writer.flush();
                        writer.close();
                        output.close();
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
                            int success = jsonObject.optInt("result");
                            if (success == 2000) {
                                JSONObject value = jsonObject.optJSONObject("user");
                                id = value.optInt("id");
                                name = value.optString("name");
                                phone = value.optString("phone");
                                registertime = value.optString("registertime");
                                login_result = true;
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
        return login_result;
    }

    private boolean checkCaptchaFormat(String code) {
        if (code.length() == 4 && code.matches("[0-9a-zA-Z]*"))
            return true;
        else
            return false;
    }

    private boolean checkUserPhoneFormat(String user_phone) {
        if (!user_phone.isEmpty())
            return true;
        else
            return false;
    }

    private boolean checkPasswordFormat(String password) {
        if (!password.isEmpty())
            return true;
        else
            return false;
    }

}
