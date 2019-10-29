package com.example.lijian;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import me.dm7.barcodescanner.zxing.CustomZXingScannerView;

public class MainActivity extends AppCompatActivity implements CustomZXingScannerView.ResultHandler {
    private static final String TAG = "MainActivity";
    private CustomZXingScannerView mCustomZXingScannerView;
    private ImageView mIvBorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCustomZXingScannerView = findViewById(R.id.zxing_scanner_view);
        mIvBorder = findViewById(R.id.iv_border);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mIvBorder.post(new Runnable() {
            @Override
            public void run() {
                mCustomZXingScannerView.setMask(mIvBorder.getLeft(), mIvBorder.getTop(), mIvBorder.getRight(), mIvBorder.getBottom());
                Log.d(TAG, "left:" + mIvBorder.getLeft() + " top:" + mIvBorder.getTop() + " right:" + mIvBorder.getRight() + " bottom:" + mIvBorder.getBottom());
            }
        });

        mCustomZXingScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mCustomZXingScannerView.startCamera();          // Start camera on resume
        // If you would like to resume scanning, call this method below:
        resumeCameraPreview();
    }

    @Override
    public void onPause() {
        super.onPause();
        mCustomZXingScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(com.google.zxing.Result rawResult) {
        // Do something with the result here
        Log.d(TAG, rawResult.getText()); // Prints scan results
        Log.d(TAG, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)
        Toast.makeText(MainActivity.this, rawResult.getText(), Toast.LENGTH_SHORT).show();
        mCustomZXingScannerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                resumeCameraPreview();
            }
        }, 1000L);

    }

    private void resumeCameraPreview() {
        // If you would like to resume scanning, call this method below:
        mCustomZXingScannerView.resumeCameraPreview(this);
    }

}
