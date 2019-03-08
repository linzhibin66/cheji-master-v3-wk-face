package com.haoxueche.cameralib.camera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.FaceDetectionListener;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;

import com.haoxueche.cameralib.exception.CameraUnAvailableException;
import com.haoxueche.cameralib.exception.EmptyByteArrayException;
import com.haoxueche.winterlog.L;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

/**
 * Created by Lyc(987424501@qq.com) on 2019/1/17.
 */
public class CameraUtil {
    /**
     * @param context
     * @return 是否有相机
     */
    public static boolean isCameraSupport(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * @return 相机数量
     */
    public static int getCameraCount() {
        return Camera.getNumberOfCameras();
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance (int cameraId) throws CameraUnAvailableException {
        Camera c = null;
        try {
            c = Camera.open(cameraId);
        } catch (Exception e) {
            throw new CameraUnAvailableException(e.getCause());
        }
        if(c == null) {
            throw new CameraUnAvailableException("camera open return null");
        }
        return c;
    }

    public static void startFaceDetection(Camera camera, FaceDetectionListener listener){
        // Try starting Face Detection
        Camera.Parameters params = camera.getParameters();

        // start face detection only *after* preview has started
        if (params.getMaxNumDetectedFaces() > 0){
            // camera supports face detection, so can start it:
            camera.setFaceDetectionListener(listener);
            camera.startFaceDetection();
        }
    }

    public static void releaseCamera(Camera camera) {
        if(camera != null) {
            try {
                camera.setPreviewCallback(null);
                camera.stopPreview();
                camera.release();
            } catch (Exception e) {
                L.e(e);
            }
        }
    }

    public static void setAutoFocus(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        final List<String> modes = parameters.getSupportedFocusModes();
        if (modes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        } else if (modes.contains(Camera.Parameters.FOCUS_MODE_FIXED)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
        } else if (modes.contains(Camera.Parameters.FOCUS_MODE_INFINITY)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
        } else {
            parameters.setFocusMode(modes.get(0));
        }
        camera.setParameters(parameters);
    }

    public static Single<byte[]> takePicture(final ShutterCallback shutterCallback, final Camera camera) {
        return Single.create(new SingleOnSubscribe<byte[]>() {
            @Override
            public void subscribe(final SingleEmitter<byte[]> emitter) throws Exception {
                camera.takePicture(shutterCallback, null, new PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        if(data != null) {
                            emitter.onSuccess(data);
                        } else {
                            emitter.tryOnError(new EmptyByteArrayException());
                        }
                    }
                });
            }
        });
    }

}
