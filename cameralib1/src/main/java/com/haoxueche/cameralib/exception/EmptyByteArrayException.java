package com.haoxueche.cameralib.exception;

/**
 * @author 李昱辰 987424501@qq.com
 * @date 2019/1/22
 */
public class EmptyByteArrayException extends RuntimeException {
    public EmptyByteArrayException() {
        super("拍照得到的byte[]为null");
    }
}
