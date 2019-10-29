package me.dm7.barcodescanner.core.texture;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import androidx.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import me.dm7.barcodescanner.core.CameraUtils;
import me.dm7.barcodescanner.core.CameraWrapper;
import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.core.R;
import me.dm7.barcodescanner.core.R.styleable;
import me.dm7.barcodescanner.core.ViewFinderView;

/**
 * Created by lijian on 2017/11/22.
 */

public abstract class BarcodeScannerTextureView extends FrameLayout implements Camera.PreviewCallback {
    private CameraTexturePreview mPreview;
    private CameraTextureHandlerThread mCameraHandlerThread;
    private CameraWrapper mCameraWrapper;
    private IViewFinder mViewFinderView;
    private Rect mFramingRectInPreview;
    private Boolean mFlashState;
    private boolean mAutofocusState = true;
    private boolean mShouldScaleToFill = true;
    private boolean mIsLaserEnabled = true;
    @ColorInt
    private int mLaserColor;
    @ColorInt
    private int mBorderColor;
    @ColorInt
    private int mBorderLineColor;
    private int mMaskColor;
    private int mBorderWidth;
    private int mBorderLength;
    private boolean mRoundedCorner;
    private int mCornerRadius;
    private boolean mSquaredFinder;
    private float mBorderAlpha;
    private int mViewFinderOffset;
    private float mAspectTolerance;

    public BarcodeScannerTextureView(Context context) {
        super(context);
        this.mLaserColor = this.getResources().getColor(me.dm7.barcodescanner.core.R.color.viewfinder_laser);
        this.mBorderColor = this.getResources().getColor(me.dm7.barcodescanner.core.R.color.viewfinder_border);
        this.mBorderLineColor = this.getResources().getColor(R.color.viewfinder_border_line);
        this.mMaskColor = this.getResources().getColor(me.dm7.barcodescanner.core.R.color.viewfinder_mask);
        this.mBorderWidth = this.getResources().getInteger(me.dm7.barcodescanner.core.R.integer.viewfinder_border_width);
        this.mBorderLength = this.getResources().getInteger(me.dm7.barcodescanner.core.R.integer.viewfinder_border_length);
        this.mRoundedCorner = false;
        this.mCornerRadius = 0;
        this.mSquaredFinder = false;
        this.mBorderAlpha = 1.0F;
        this.mViewFinderOffset = 0;
        this.mAspectTolerance = 0.1F;
        this.init();
    }

    public BarcodeScannerTextureView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mLaserColor = this.getResources().getColor(me.dm7.barcodescanner.core.R.color.viewfinder_laser);
        this.mBorderColor = this.getResources().getColor(me.dm7.barcodescanner.core.R.color.viewfinder_border);
        this.mBorderLineColor = this.getResources().getColor(R.color.viewfinder_border_line);
        this.mMaskColor = this.getResources().getColor(me.dm7.barcodescanner.core.R.color.viewfinder_mask);
        this.mBorderWidth = this.getResources().getInteger(me.dm7.barcodescanner.core.R.integer.viewfinder_border_width);
        this.mBorderLength = this.getResources().getInteger(me.dm7.barcodescanner.core.R.integer.viewfinder_border_length);
        this.mRoundedCorner = false;
        this.mCornerRadius = 0;
        this.mSquaredFinder = false;
        this.mBorderAlpha = 1.0F;
        this.mViewFinderOffset = 0;
        this.mAspectTolerance = 0.1F;
        TypedArray a = context.getTheme().obtainStyledAttributes(attributeSet, styleable.BarcodeScannerView, 0, 0);

        try {
            this.setShouldScaleToFill(a.getBoolean(styleable.BarcodeScannerView_shouldScaleToFill, true));
            this.mIsLaserEnabled = a.getBoolean(styleable.BarcodeScannerView_laserEnabled, this.mIsLaserEnabled);
            this.mLaserColor = a.getColor(styleable.BarcodeScannerView_laserColor, this.mLaserColor);
            this.mBorderColor = a.getColor(styleable.BarcodeScannerView_borderColor, this.mBorderColor);
            this.mBorderLineColor = a.getColor(styleable.BarcodeScannerView_borderLineColor, this.mBorderLineColor);
            this.mMaskColor = a.getColor(styleable.BarcodeScannerView_maskColor, this.mMaskColor);
            this.mBorderWidth = a.getDimensionPixelSize(styleable.BarcodeScannerView_borderWidth, this.mBorderWidth);
            this.mBorderLength = a.getDimensionPixelSize(styleable.BarcodeScannerView_borderLength, this.mBorderLength);
            this.mRoundedCorner = a.getBoolean(styleable.BarcodeScannerView_roundedCorner, this.mRoundedCorner);
            this.mCornerRadius = a.getDimensionPixelSize(styleable.BarcodeScannerView_cornerRadius, this.mCornerRadius);
            this.mSquaredFinder = a.getBoolean(styleable.BarcodeScannerView_squaredFinder, this.mSquaredFinder);
            this.mBorderAlpha = a.getFloat(styleable.BarcodeScannerView_borderAlpha, this.mBorderAlpha);
            this.mViewFinderOffset = a.getDimensionPixelSize(styleable.BarcodeScannerView_finderOffset, this.mViewFinderOffset);
        } finally {
            a.recycle();
        }

        this.init();

    }

    private void init() {
        this.mViewFinderView = this.createViewFinderView(this.getContext());
    }

    public final void setupLayout(CameraWrapper cameraWrapper) {
        this.removeAllViews();
        this.mPreview = new CameraTexturePreview(this.getContext(), cameraWrapper, this);
        this.mPreview.setAspectTolerance(this.mAspectTolerance);
        this.mPreview.setShouldScaleToFill(this.mShouldScaleToFill);
        if (!this.mShouldScaleToFill) {
            RelativeLayout relativeLayout = new RelativeLayout(this.getContext());
            relativeLayout.setGravity(17);
            relativeLayout.setBackgroundColor(-16777216);
            relativeLayout.addView(this.mPreview);
            this.addView(relativeLayout);
        } else {
            this.addView(this.mPreview);
        }

        if (this.mViewFinderView instanceof View) {
            this.addView((View) this.mViewFinderView);
        } else {
            throw new IllegalArgumentException("IViewFinder object returned by 'createViewFinderView()' should be instance of android.view.View");
        }
    }

    protected IViewFinder createViewFinderView(Context context) {
        ViewFinderView viewFinderView = new ViewFinderView(context);
        viewFinderView.setBorderColor(this.mBorderColor);
        viewFinderView.setBorderLineColor(this.mBorderLineColor);
        viewFinderView.setLaserColor(this.mLaserColor);
        viewFinderView.setLaserEnabled(this.mIsLaserEnabled);
        viewFinderView.setBorderStrokeWidth(this.mBorderWidth);
        viewFinderView.setBorderLineLength(this.mBorderLength);
        viewFinderView.setMaskColor(this.mMaskColor);
        viewFinderView.setBorderCornerRounded(this.mRoundedCorner);
        viewFinderView.setBorderCornerRadius(this.mCornerRadius);
        viewFinderView.setSquareViewFinder(this.mSquaredFinder);
        viewFinderView.setViewFinderOffset(this.mViewFinderOffset);
        return viewFinderView;
    }

    public void setLaserColor(int laserColor) {
        this.mLaserColor = laserColor;
        this.mViewFinderView.setLaserColor(this.mLaserColor);
        this.mViewFinderView.setupViewFinder();
    }

    public void setMaskColor(int maskColor) {
        this.mMaskColor = maskColor;
        this.mViewFinderView.setMaskColor(this.mMaskColor);
        this.mViewFinderView.setupViewFinder();
    }

    public void setMask(int left, int top, int right, int bottom) {
        this.mViewFinderView.setMaskColor(this.mMaskColor);
        this.mViewFinderView.setupViewFinder(new Rect(left, top, right, bottom));
    }

    public void setBorderColor(int borderColor) {
        this.mBorderColor = borderColor;
        this.mViewFinderView.setBorderColor(this.mBorderColor);
        this.mViewFinderView.setupViewFinder();
    }

    public void setBorderLineColor(int borderLineColor) {
        this.mBorderLineColor = borderLineColor;
        this.mViewFinderView.setBorderLineColor(this.mBorderLineColor);
        this.mViewFinderView.setupViewFinder();
    }

    public void setBorderStrokeWidth(int borderStrokeWidth) {
        this.mBorderWidth = borderStrokeWidth;
        this.mViewFinderView.setBorderStrokeWidth(this.mBorderWidth);
        this.mViewFinderView.setupViewFinder();
    }

    public void setBorderLineLength(int borderLineLength) {
        this.mBorderLength = borderLineLength;
        this.mViewFinderView.setBorderLineLength(this.mBorderLength);
        this.mViewFinderView.setupViewFinder();
    }

    public void setLaserEnabled(boolean isLaserEnabled) {
        this.mIsLaserEnabled = isLaserEnabled;
        this.mViewFinderView.setLaserEnabled(this.mIsLaserEnabled);
        this.mViewFinderView.setupViewFinder();
    }

    public void setIsBorderCornerRounded(boolean isBorderCornerRounded) {
        this.mRoundedCorner = isBorderCornerRounded;
        this.mViewFinderView.setBorderCornerRounded(this.mRoundedCorner);
        this.mViewFinderView.setupViewFinder();
    }

    public void setBorderCornerRadius(int borderCornerRadius) {
        this.mCornerRadius = borderCornerRadius;
        this.mViewFinderView.setBorderCornerRadius(this.mCornerRadius);
        this.mViewFinderView.setupViewFinder();
    }

    public void setSquareViewFinder(boolean isSquareViewFinder) {
        this.mSquaredFinder = isSquareViewFinder;
        this.mViewFinderView.setSquareViewFinder(this.mSquaredFinder);
        this.mViewFinderView.setupViewFinder();
    }

    public void setBorderAlpha(float borderAlpha) {
        this.mBorderAlpha = borderAlpha;
        this.mViewFinderView.setBorderAlpha(this.mBorderAlpha);
        this.mViewFinderView.setupViewFinder();
    }

    public void startCamera(int cameraId) {
        if (this.mCameraHandlerThread == null) {
            this.mCameraHandlerThread = new CameraTextureHandlerThread(this);
        }

        this.mCameraHandlerThread.startCamera(cameraId);
    }

    public void setupCameraPreview(CameraWrapper cameraWrapper) {
        this.mCameraWrapper = cameraWrapper;
        if (this.mCameraWrapper != null) {
            this.setupLayout(this.mCameraWrapper);
            this.mViewFinderView.setupViewFinder();
            if (this.mFlashState != null) {
                this.setFlash(this.mFlashState.booleanValue());
            }

            this.setAutoFocus(this.mAutofocusState);
        }

    }

    public void startCamera() {
        this.startCamera(CameraUtils.getDefaultCameraId());
    }

    public void stopCamera() {
        if (this.mCameraWrapper != null) {
            this.mPreview.stopCameraPreview();
            this.mPreview.setCamera((CameraWrapper) null, (PreviewCallback) null);
            this.mCameraWrapper.mCamera.release();
            this.mCameraWrapper = null;
        }

        if (this.mCameraHandlerThread != null) {
            this.mCameraHandlerThread.quit();
            this.mCameraHandlerThread = null;
        }

    }

    public void stopCameraPreview() {
        if (this.mPreview != null) {
            this.mPreview.stopCameraPreview();
        }

    }

    protected void resumeCameraPreview() {
        if (this.mPreview != null) {
            this.mPreview.showCameraPreview();
        }

    }

    public synchronized Rect getFramingRectInPreview(int previewWidth, int previewHeight) {
        if (this.mFramingRectInPreview == null) {
            Rect framingRect = this.mViewFinderView.getFramingRect();
            int viewFinderViewWidth = this.mViewFinderView.getWidth();
            int viewFinderViewHeight = this.mViewFinderView.getHeight();
            if (framingRect == null || viewFinderViewWidth == 0 || viewFinderViewHeight == 0) {
                return null;
            }

            Rect rect = new Rect(framingRect);
            if (previewWidth < viewFinderViewWidth) {
                rect.left = rect.left * previewWidth / viewFinderViewWidth;
                rect.right = rect.right * previewWidth / viewFinderViewWidth;
            }

            if (previewHeight < viewFinderViewHeight) {
                rect.top = rect.top * previewHeight / viewFinderViewHeight;
                rect.bottom = rect.bottom * previewHeight / viewFinderViewHeight;
            }

            this.mFramingRectInPreview = rect;
        }

        return this.mFramingRectInPreview;
    }

    public void setFlash(boolean flag) {
        this.mFlashState = Boolean.valueOf(flag);
        if (this.mCameraWrapper != null && CameraUtils.isFlashSupported(this.mCameraWrapper.mCamera)) {
            Parameters parameters = this.mCameraWrapper.mCamera.getParameters();
            if (flag) {
                if (parameters.getFlashMode().equals("torch")) {
                    return;
                }

                parameters.setFlashMode("torch");
            } else {
                if (parameters.getFlashMode().equals("off")) {
                    return;
                }

                parameters.setFlashMode("off");
            }

            this.mCameraWrapper.mCamera.setParameters(parameters);
        }

    }

    public boolean getFlash() {
        if (this.mCameraWrapper != null && CameraUtils.isFlashSupported(this.mCameraWrapper.mCamera)) {
            Parameters parameters = this.mCameraWrapper.mCamera.getParameters();
            return parameters.getFlashMode().equals("torch");
        } else {
            return false;
        }
    }

    public void toggleFlash() {
        if (this.mCameraWrapper != null && CameraUtils.isFlashSupported(this.mCameraWrapper.mCamera)) {
            Parameters parameters = this.mCameraWrapper.mCamera.getParameters();
            if (parameters.getFlashMode().equals("torch")) {
                parameters.setFlashMode("off");
            } else {
                parameters.setFlashMode("torch");
            }

            this.mCameraWrapper.mCamera.setParameters(parameters);
        }

    }

    public void setAutoFocus(boolean state) {
        this.mAutofocusState = state;
        if (this.mPreview != null) {
            this.mPreview.setAutoFocus(state);
        }

    }

    public void setShouldScaleToFill(boolean shouldScaleToFill) {
        this.mShouldScaleToFill = shouldScaleToFill;
    }

    public void setAspectTolerance(float aspectTolerance) {
        this.mAspectTolerance = aspectTolerance;
    }

    public byte[] getRotatedData(byte[] data, Camera camera) {
        Parameters parameters = camera.getParameters();
        Camera.Size size = parameters.getPreviewSize();
        int width = size.width;
        int height = size.height;
        int rotationCount = this.getRotationCount();
        if (rotationCount == 1 || rotationCount == 3) {
            for (int i = 0; i < rotationCount; ++i) {
                byte[] rotatedData = new byte[data.length];

                int y;
                for (y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        rotatedData[x * height + height - y - 1] = data[x + y * width];
                    }
                }

                data = rotatedData;
                y = width;
                width = height;
                height = y;
            }
        }

        return data;
    }

    public int getRotationCount() {
        int displayOrientation = this.mPreview.getDisplayOrientation();
        return displayOrientation / 90;
    }
}
