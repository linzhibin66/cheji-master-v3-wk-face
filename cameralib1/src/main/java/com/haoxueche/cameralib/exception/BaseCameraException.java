package com.haoxueche.cameralib.exception;

/**
 * 相机异常基类
 *
 * @author 李昱辰 987424501@qq.com
 * @date 2019/1/17.
 */
public abstract class BaseCameraException extends Exception {
    public BaseCameraException() {
    }

    public BaseCameraException(String message) {
        super(message);
    }

    public BaseCameraException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseCameraException(Throwable cause) {
        super(cause);
    }

}
