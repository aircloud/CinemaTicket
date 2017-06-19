package com.example.xhz636.cinematicket;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class QRCodeActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        imageView = (ImageView)findViewById(R.id.qrcode_image);
        Bitmap bitmap;
        try {
            Intent intent = getIntent();
            bitmap = Create2DCode(intent.getStringExtra("ordernumber"));
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    public Bitmap Create2DCode(String str) throws WriterException {
        WindowManager windowManager = getWindowManager();
        int qrsize = windowManager.getDefaultDisplay().getWidth();
        BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, qrsize, qrsize);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

}
