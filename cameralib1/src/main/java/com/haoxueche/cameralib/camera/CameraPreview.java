package com.haoxueche.cameralib.camera;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.haoxueche.cameralib.common.CameraStateCallback;
import com.haoxueche.winterlog.L;

import java.io.IOException;

/** A basic Camera preview class */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    public static final String TAG = "CameraPreview";
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private CameraStateCallback cameraStateCallback;


    public CameraPreview(Context context, Camera camera, CameraStateCallback cameraStateCallback) {
        super(context);
        mCamera = camera;

        // Install a SurfaceHolder.CameraStateCallback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        this.cameraStateCallback = cameraStateCallback;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        L.i("surfaceCreated");
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        L.i("surfaceDestroyed");
        // empty. Take care of releasing the Camera preview in your activity.
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        L.i("surfaceChanged");
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
          // preview surface does not exist
          return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
          // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        cameraStateCallback.onBeforeStartPreview(mCamera);

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
            cameraStateCallback.onStartPreview(mCamera);
        } catch (Exception e){
            L.e(e, "Error starting camera preview: ");
        }
    }
}