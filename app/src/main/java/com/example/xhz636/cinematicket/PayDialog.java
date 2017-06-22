package com.example.xhz636.cinematicket;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PayDialog extends Dialog {

    private Context context;
    private TextView textView_Money;
    private EditText editText_Password;
    private Button button_Cancel;
    private Button button_Confirm;

    public PayDialog(Context context) {
        super(context);
        this.context = context;
    }

    public PayDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_pay);
        textView_Money = (TextView)findViewById(R.id.pay_money);
        editText_Password = (EditText)findViewById(R.id.pay_password);
        button_Cancel = (Button)findViewById(R.id.pay_cancel_button);
        button_Confirm = (Button)findViewById(R.id.pay_confirm_button);
        button_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        setCancelable(true);
    }

    public void setPay(View.OnClickListener clickListener) {
        button_Confirm.setOnClickListener(clickListener);
    }

    public void setMoney(float money) {
        textView_Money.setText("总共：" + money + "元");
    }

    public void setDialogWidth(int width) {
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.width = width;
        getWindow().setAttributes(layoutParams);
    }

}
