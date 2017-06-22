package com.example.xhz636.cinematicket;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
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
    private Button button_Register;

    private boolean captcha_check = false;
    private boolean login_result = false;
    private GlobalData globalData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        globalData = (GlobalData)getApplication();
        Intent intent = getIntent();
        String userid = intent.getStringExtra("userid");
        String password = intent.getStringExtra("password");
        editText_UserPhone = (EditText)findViewById(R.id.login_user_phone);
        if (userid != null && !userid.equals(""))
            editText_UserPhone.setText(userid);
        editText_Password = (EditText)findViewById(R.id.login_password);
        if (password != null && !password.equals(""))
            editText_Password.setText(password);
        editText_Captcha = (EditText)findViewById(R.id.login_captcha_text);
        webView_Captcha = (WebView)findViewById(R.id.login_captcha_image);
        webView_Captcha.setVerticalScrollBarEnabled(false);
        webView_Captcha.setHorizontalScrollBarEnabled(false);
        imageButton_Captcha = (ImageButton)findViewById(R.id.login_captcha_refresh);
        button_Login = (Button)findViewById(R.id.login_button);
        button_Register = (Button)findViewById(R.id.login_indent);
        button_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String captcha = editText_Captcha.getText().toString();
                if (checkCaptcha(captcha)) {
                    String user_phone = editText_UserPhone.getText().toString();
                    String password = editText_Password.getText().toString();
                    if (login(user_phone, password, captcha)) {
                        Toast.makeText(getApplicationContext(), "登录成功！", Toast.LENGTH_LONG).show();
                        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("userid", user_phone);
                        editor.commit();
                        globalData.setUserid(user_phone);
                        setResult(RESULT_OK);
                        finish();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "用户名或密码错误", Toast.LENGTH_LONG).show();
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
        button_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("userid", editText_UserPhone.getText().toString());
                intent.putExtra("password", editText_Password.getText().toString());
                intent.setClass(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent, 2);
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
        initData();
        getCaptcha();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 2 && resultCode == RESULT_OK) {
            String userid = intent.getStringExtra("userid");
            String password = intent.getStringExtra("password");
            if (userid != null && !userid.equals(""))
                editText_UserPhone.setText(userid);
            if (password != null && !password.equals(""))
                editText_Password.setText(password);
            getCaptcha();
        }
    }

    private void getCaptcha() {
        webView_Captcha.loadUrl("https://c.10000h.top/user/captcha");
    }

    private void initData() {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setCookie("https://c.10000h.top/", globalData.getCookie());
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
                        connection.setRequestProperty("Cookie", globalData.getCookie());
                        Log.d("cookie", globalData.getCookie());
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
                        connection.setRequestProperty("Cookie", globalData.getCookie());
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
