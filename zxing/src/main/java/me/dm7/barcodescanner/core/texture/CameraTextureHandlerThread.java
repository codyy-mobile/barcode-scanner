package me.dm7.barcodescanner.core.texture;

import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import me.dm7.barcodescanner.core.CameraUtils;
import me.dm7.barcodescanner.core.CameraWrapper;

/**
 * Created by lijian on 2017/11/22.
 */

public class CameraTextureHandlerThread extends HandlerThread {
    private BarcodeScannerTextureView mTextureView;
    public CameraTextureHandlerThread(BarcodeScannerTextureView view) {
        super("CameraTextureHandlerThread");
        this.mTextureView = view;
        this.start();
    }
    public void startCamera(final int cameraId) {
        Handler localHandler = new Handler(this.getLooper());
        localHandler.post(new Runnable() {
            public void run() {
                final Camera camera = CameraUtils.getCameraInstance(cameraId);
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(new Runnable() {
                    public void run() {
                        CameraTextureHandlerThread.this.mTextureView.setupCameraPreview(CameraWrapper.getWrapper(camera, cameraId));
                    }
                });
            }
        });
    }
}
