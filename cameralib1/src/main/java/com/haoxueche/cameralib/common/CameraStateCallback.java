package com.haoxueche.cameralib.common;

import android.hardware.Camera;

/**
 * 相机回调
 * @author 李昱辰 987424501@qq.com
 * @date 2019/1/19
 */
public interface CameraStateCallback {
    /**
     * 开始预览后的回调方法
     * @param camera
     */
    void onStartPreview(Camera camera);
    /**
     * 开始预览前的回调
     * @param camera
     */
    void onBeforeStartPreview(Camera camera);
}
