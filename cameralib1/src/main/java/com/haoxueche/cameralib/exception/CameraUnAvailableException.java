package com.haoxueche.cameralib.exception;

/**
 * 相机打开失败，可能是在使用中
 *
 * @author 李昱辰 987424501@qq.com
 * @date 2019/1/17
 */
public class CameraUnAvailableException extends BaseCameraException {
    public CameraUnAvailableException() {
    }

    public CameraUnAvailableException(String message) {
        super(message);
    }

    public CameraUnAvailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public CameraUnAvailableException(Throwable cause) {
        super(cause);
    }
}
