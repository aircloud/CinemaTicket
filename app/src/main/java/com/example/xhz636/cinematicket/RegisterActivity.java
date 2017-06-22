package com.example.xhz636.cinematicket;

import android.content.Intent;
import android.os.CountDownTimer;
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

public class RegisterActivity extends AppCompatActivity {

    private EditText editText_User;
    private EditText editText_Phone;
    private EditText editText_Password;
    private EditText editText_PasswordConfirm;
    private EditText editText_Captcha;
    private WebView webView_Captcha;
    private ImageButton imageButton_Captcha;
    private EditText editText_Verify;
    private Button button_Verify;
    private Button button_Register;

    private boolean captcha_check = false;
    private boolean user_phone_check = false;
    private String exist_type;
    private boolean verify_request = false;
    private boolean register_result = false;
    private final long verify_delay = 30 * 1000;
    private int regid;
    private String regname, regphone, registertime;
    private GlobalData globalData;

    private CountDownTimer timer = new CountDownTimer(verify_delay, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            button_Verify.setEnabled(false);
            button_Verify.setText(String.valueOf(millisUntilFinished / 1000) + "秒后重新获取");
        }

        @Override
        public void onFinish() {
            button_Verify.setEnabled(true);
            button_Verify.setText("获取验证码");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("注册");
        globalData = (GlobalData)getApplication();
        Intent intent = getIntent();
        String userid = intent.getStringExtra("userid");
        String password = intent.getStringExtra("password");
        editText_User = (EditText)findViewById(R.id.register_user);
        if (userid != null && !userid.equals(""))
            editText_User.setText(userid);
        editText_Phone = (EditText)findViewById(R.id.register_phone);
        editText_Password = (EditText)findViewById(R.id.register_password);
        if (password != null && !password.equals(""))
            editText_Password.setText(password);
        editText_PasswordConfirm = (EditText)findViewById(R.id.register_password_confirm);
        editText_Captcha = (EditText)findViewById(R.id.register_captcha_text);
        webView_Captcha = (WebView)findViewById(R.id.register_captcha_image);
        webView_Captcha.setVerticalScrollBarEnabled(false);
        webView_Captcha.setHorizontalScrollBarEnabled(false);
        imageButton_Captcha = (ImageButton)findViewById(R.id.register_captcha_refresh);
        editText_Verify = (EditText)findViewById(R.id.register_verify_text);
        button_Verify = (Button)findViewById(R.id.register_verify_button);
        button_Register = (Button)findViewById(R.id.register_button);
        button_Verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String captcha = editText_Captcha.getText().toString();
                String phone = editText_Phone.getText().toString();
                if (checkCaptcha(captcha)) {
                    if (requestVerify(captcha, phone))
                        timer.start();
                }
                else {
                    editText_Captcha.setError("验证码错误");
                }
            }
        });
        button_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String captcha = editText_Captcha.getText().toString();
                String password = editText_Password.getText().toString();
                String password_confirm = editText_PasswordConfirm.getText().toString();
                if (!checkPasswordConfirm(password, password_confirm)) {
                    editText_PasswordConfirm.setError("两次密码不一致");
                    return;
                }
                if (!checkCaptcha(captcha)) {
                    editText_Captcha.setError("验证码错误");
                    return;
                }
                String user = editText_User.getText().toString();
                String phone = editText_Phone.getText().toString();
                if (!checkUserPhoneExist(user, phone)) {
                    String verify = editText_Verify.getText().toString();
                    if (register(verify, user, phone, password)) {
                        Toast.makeText(getApplicationContext(), "注册成功！", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent();
                        intent.putExtra("userid", user);
                        intent.putExtra("password", password);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                    else {
                        editText_Verify.setError("短信验证码错误");
                    }
                }
                else {
                    if (exist_type.equals("name")) {
                        editText_User.setError("用户已被注册");
                    } else if (exist_type.equals("phone")) {
                        editText_Phone.setError("手机已被注册");
                    }
                }
            }
        });
        imageButton_Captcha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCaptcha();
            }
        });
        editText_User.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editText_User.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editText_Phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editText_Phone.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editText_Password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editText_Password.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editText_PasswordConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editText_PasswordConfirm.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

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
        editText_Verify.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editText_Verify.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        initData();
        getCaptcha();
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
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String geturl = "https://c.10000h.top/user/captchatest/" + code;
                    Log.d("url", geturl);
                    URL url = new URL(geturl);
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Cookie", globalData.getCookie());
                    connection.setConnectTimeout(1000);
                    connection.connect();
                    int code = connection.getResponseCode();
                    StringBuffer str = new StringBuffer();
                    if (code == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        int len;
                        while((len = inputStream.read(buffer)) != -1){
                            byteArrayOutputStream.write(buffer, 0, len);
                        }
                        String jsonString = byteArrayOutputStream.toString();
                        Log.d("json", jsonString);
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
        return captcha_check;
    }

    private boolean checkUserPhoneExist(final String user, final String phone) {
        user_phone_check = false;
        if (checkUserFormat(user) && checkPhoneFormat(phone)) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String geturl = "https://c.10000h.top/user/finduser/" + phone + "/" + user;
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
                            boolean exist = value.optBoolean("result");
                            if (success == 2000 && exist) {
                                user_phone_check = true;
                                exist_type = value.optString("type");
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
        return user_phone_check;
    }

    private boolean checkUserFormat(String user) {
        if (!user.isEmpty() && !user.matches("^[0-9]{11}$"))
            return true;
        else
            return false;
    }

    private boolean checkPhoneFormat(String phone) {
        if (!phone.isEmpty() && phone.matches("^(13|14|15|16|17|18|19)[0-9]{9}$"))
            return true;
        else
            return false;
    }

    private boolean checkPasswordConfirm(String password, String password_confirm) {
        if (!password.equals(password_confirm))
            return false;
        else
            return true;
    }

    private boolean checkPasswordFormat(String password) {
        if (!password.isEmpty())
            return true;
        else
            return false;
    }

    private boolean checkVerifyFormat(String verify) {
        if (verify.length() == 6 && verify.matches("[0-9a-zA-Z]*"))
            return true;
        else
            return false;
    }

    private boolean requestVerify(final String captcha, final String phone) {
        verify_request = false;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String geturl = "https://c.10000h.top/user/send/" + captcha + "/" + phone;
                    Log.d("url", geturl);
                    URL url = new URL(geturl);
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Cookie", globalData.getCookie());
                    connection.setConnectTimeout(1000);
                    connection.connect();
                    int code = connection.getResponseCode();
                    StringBuffer str = new StringBuffer();
                    if (code == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        int len;
                        while((len = inputStream.read(buffer)) != -1){
                            byteArrayOutputStream.write(buffer, 0, len);
                        }
                        String jsonString = byteArrayOutputStream.toString();
                        Log.d("json", jsonString);
                        byteArrayOutputStream.close();
                        JSONObject jsonObject = new JSONObject(jsonString);
                        int success = jsonObject.optInt("success");
                        if (success == 2000) {
                            verify_request = true;
                        }
                        else {
                            String value = jsonObject.optString("value");
                            Log.d("verify", value);
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
        return verify_request;
    }

    private boolean register(final String verify, final String user, final String phone, final String password) {
        register_result = false;
        if (checkVerifyFormat(verify)) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        JSONObject jsonParam = new JSONObject();
                        jsonParam.put("phone", phone);
                        jsonParam.put("name", user);
                        jsonParam.put("password", password);
                        String posturl = "https://c.10000h.top/user/insert/" + verify;
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
                                JSONObject value = jsonObject.optJSONObject("user");
                                regid = value.optInt("id");
                                regname = value.optString("name");
                                regphone = value.optString("phone");
                                registertime = value.optString("registertime");
                                register_result = true;
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
        return register_result;
    }

}
