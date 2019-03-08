package com.haoxueche.cameralib.util;

/**
 * @author 李昱辰 987424501@qq.com
 * @date 2019/2/11
 */
public class FaceVerifyResult extends VerifyResult {

    private byte[] data;

    public FaceVerifyResult(int result, String message) {
        super(result, message);
    }

    public FaceVerifyResult(VerifyResult verifyResult, byte[] data) {
        super(verifyResult.getStatus(), verifyResult.getMessage());
        this.data = data;
    }

    public FaceVerifyResult(byte[] data) {
        this(VerifyResult.SUCC, data);
    }

    public FaceVerifyResult(int result, String message, byte[] data) {
        super(result, message);
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
