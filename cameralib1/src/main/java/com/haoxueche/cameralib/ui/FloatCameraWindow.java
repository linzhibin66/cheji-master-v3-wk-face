package com.haoxueche.cameralib.ui;

import android.app.Application;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.haoxueche.cameralib.R;
import com.haoxueche.cameralib.camera.CameraPreview;
import com.haoxueche.cameralib.common.CameraStateCallback;
import com.haoxueche.cameralib.util.CameraWindowSize;
import com.haoxueche.winterlog.L;

/**
 * @author 李昱辰 987424501@qq.com
 * @date 2019/1/21
 */
public class FloatCameraWindow{

    private Context context;

    private WindowManager windowManager;
    private LayoutParams wmParams;

    private ViewGroup floatLayout;

    //相机预览View
    private CameraPreview cameraPreview;
    //人脸检测提示框
    private FaceView faceView;
    private TextView tvHint;

    public FloatCameraWindow(Context context, CameraWindowSize windowSize
            , boolean previewMirrored, boolean previewRotated) {
        wmParams = new LayoutParams();
        this.context = context;
        windowManager = (WindowManager) context.getSystemService
                (Application.WINDOW_SERVICE);
        //设置window type
        wmParams.type = LayoutParams.TYPE_TOAST;
        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        wmParams.x = windowSize.getLeft();
        wmParams.y = windowSize.getTop();
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        wmParams.width = windowSize.getWidth();
        wmParams.height = windowSize.getHeight();

        LayoutInflater inflater = LayoutInflater.from(context);
        //获取浮动窗口视图所在布局
        floatLayout = (ViewGroup) inflater.inflate(R.layout.layout_float_camera_window, null);
        initView(previewMirrored, previewRotated);
        windowManager.addView(floatLayout, wmParams);
    }

    public void setCamera(Camera camera, CameraStateCallback cameraStateCallback) {
        //创建预览View
        cameraPreview = new CameraPreview(context, camera,
                cameraStateCallback);
        //把预览View添加到界面中
        floatLayout.addView(cameraPreview, 0);
    }

    private void initView(boolean previewMirrored, boolean previewRotated) {
        faceView = floatLayout.findViewById(R.id.faceView);
        //外置相机的预览画面是镜像的，人脸检测的框也要镜像
        faceView.setFrontCamera(previewMirrored);
        faceView.setRotate(previewRotated);

        tvHint = floatLayout.findViewById(R.id.tvHint);
    }

    public void setFace(Rect[] rects) {
        faceView.setFaces(rects);
    }

    public void release() {
        dismissWindow();
    }

    /**
     * 移除
     */
    private void dismissWindow() {
        if (floatLayout != null) {
            try {
                windowManager.removeView(floatLayout);
                floatLayout = null;
            } catch (Exception e) {
                L.e(e);
            }
        }
    }

    public void setFaceCoodinateSize(int width, int height) {
        faceView.setCoordinatesWidth(width);
        faceView.setCoordinatesHeight(height);
    }

    public void setHint(String description) {
        tvHint.setText(description);
    }
}
