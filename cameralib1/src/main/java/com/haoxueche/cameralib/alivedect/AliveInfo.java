package com.haoxueche.cameralib.alivedect;

import android.graphics.Rect;

import java.util.Arrays;

/**
 * Created by Lyc(987424501@qq.com) on 2018/11/30.
 */
public class AliveInfo {
    public static final int NO_FACE = 1;
    public static final int ALIVE = 2;
    public static final int NOT_ALIVE = 3;
    public static final int UNKNOW = 4;
    public static final int MORE_THAN_ONE_FACE = 5;

    private int status;
    private String description;
    private Rect faceRect;
    private byte[] aliveImageData;

    public AliveInfo() {
    }

    public AliveInfo(int status, byte[] aliveImageData, Rect faceRect) {
        this.status = status;
        this.aliveImageData = aliveImageData;
        this.faceRect = faceRect;
        this.description = AliveDectUtil.getStatusMessage(status);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public byte[] getAliveImageData() {
        return aliveImageData;
    }

    public void setAliveImageData(byte[] aliveImageData) {
        this.aliveImageData = aliveImageData;
    }

    public Rect getFaceRect() {
        return faceRect;
    }

    public void setFaceRect(Rect faceRect) {
        this.faceRect = faceRect;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "AliveInfo{" +
                "status=" + status +
                ", description='" + description + '\'' +
                ", faceRect=" + faceRect +
                ", aliveImageData=" + Arrays.toString(aliveImageData) +
                '}';
    }
}
