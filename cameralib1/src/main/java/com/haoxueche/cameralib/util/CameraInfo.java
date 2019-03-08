package com.haoxueche.cameralib.util;

import android.hardware.Camera;
import android.util.Size;

import com.haoxueche.cameralib.camera.CameraUtil;

/**
 * Created by Lyc(987424501@qq.com) on 2019/1/17.
 */
public class CameraInfo {
    public static final int UNKNOW = -1;

    //外置相机
    public static final int CAMERA_FACING_OUT;
    //内置相机
    public static final int CAMERA_FACING_INSIDE;

    public static final Size PREVIEW_SIZE = new Size(320, 240);

    static {
        int cameraCount = CameraUtil.getCameraCount();
        //有两个相机的情况下，内置相机index为0，外置为1
        if(cameraCount == 2) {
            CAMERA_FACING_INSIDE = Camera.CameraInfo.CAMERA_FACING_BACK;
            CAMERA_FACING_OUT = Camera.CameraInfo.CAMERA_FACING_FRONT;

            //有只个1相机的情况下，内置相机index都设置为0，保证代码不出错，更好适配
        } else if(cameraCount == 1) {
            CAMERA_FACING_INSIDE = Camera.CameraInfo.CAMERA_FACING_BACK;
            CAMERA_FACING_OUT = Camera.CameraInfo.CAMERA_FACING_BACK;
        } else {
            //0相机的情况下，不允许使用相机
            CAMERA_FACING_INSIDE = UNKNOW;
            CAMERA_FACING_OUT = UNKNOW;
        }
    }

}
