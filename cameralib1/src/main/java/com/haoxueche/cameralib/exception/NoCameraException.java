package com.haoxueche.cameralib.exception;

/**
 * 无法检测到相机
 * @author 李昱辰 987424501@qq.com
 * @date 2019/1/22
 */
public class NoCameraException extends BaseCameraException {

    public NoCameraException() {
        super("无法检测到相机");
    }
}
