package com.dgcheshang.cheji.Activity;

import com.haoxueche.cameralib.util.ImageStatus;

/**
 * @author 李昱辰 987424501@qq.com
 * @date 2019/2/19
 */
class ImageSaveInfo {
    private int status = ImageStatus.STATUS_SUCC;
    private String path;
    private byte[] data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ImageSaveInfo{" +
                "status=" + status +
                ", path='" + path + '\'' +
                '}';
    }
}
