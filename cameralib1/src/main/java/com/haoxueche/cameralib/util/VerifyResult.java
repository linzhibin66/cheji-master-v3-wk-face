package com.haoxueche.cameralib.util;


import static com.haoxueche.cameralib.util.ImageStatus.STATUS_SUCC;

/**
 * @author 李昱辰 987424501@qq.com
 * @date 2019/2/11
 */
public class VerifyResult {

    public static final VerifyResult SUCC = new VerifyResult(STATUS_SUCC, "");

    private int status;
    private String message;

    public VerifyResult(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSucc() {
        return status == STATUS_SUCC;
    }

    @Override
    public String toString() {
        return "VerifyResult{" +
                "status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
