package com.haoxueche.cameralib.util;

import com.haoxueche.winterlog.L;

/**
 * @author 李昱辰 987424501@qq.com
 * @date 2019/1/29
 */
public class TakePictureParam {
    public static final int OUT_TIME_SHORT = 30;
    public static final int OUT_TIME_LONG = 55;

    private int cameraId;
    private boolean previewRotate;
    private boolean previewMirrored;
    private CameraWindowSize cameraWindowSize;
    private int outTime;
    private boolean forceTakeWhenTimeOut;
    private int delayTimeBeforeDect;
    private boolean needFaceDect;
    private int faceDectNum;
    private boolean needAliveDect;
    private boolean needFaceVerify;
    private boolean forceTakeWhenVerifyFail;
    private boolean forceTakeWhenVerifyError;
    private IFaceVerifyManager verifyManager;

    private TakePictureParam(Builder builder) {
        setCameraId(builder.cameraId);
        setPreviewRotate(builder.previewRotate);
        setPreviewMirrored(builder.previewMirrored);
        setCameraWindowSize(builder.cameraWindowSize);
        setOutTime(builder.outTime);
        setForceTakeWhenTimeOut(builder.forceTakeWhenTimeOut);
        setNeedFaceDect(builder.needFaceDect);

        setNeedAliveDect(builder.needAliveDect);
        setNeedFaceVerify(builder.needFaceVerify);
        setVerifyManager(builder.verifyManager);
        setForceTakeWhenVerifyFail(builder.forceTakeWhenVerifyFail);
        setForceTakeWhenVerifyError(builder.forceTakeWhenVerifyError);
        setDelayTimeBeforeDect(builder.delayTimeBeforeDect);
        setFaceDectNum(builder.faceDectNum);
    }

    public int getCameraId() {
        return cameraId;
    }

    public void setCameraId(int cameraId) {
        this.cameraId = cameraId;
    }

    public boolean isPreviewRotate() {
        return previewRotate;
    }

    public void setPreviewRotate(boolean previewRotate) {
        this.previewRotate = previewRotate;
    }

    public boolean isPreviewMirrored() {
        return previewMirrored;
    }

    public void setPreviewMirrored(boolean previewMirrored) {
        this.previewMirrored = previewMirrored;
    }

    public CameraWindowSize getCameraWindowSize() {
        return cameraWindowSize;
    }

    public void setCameraWindowSize(CameraWindowSize cameraWindowSize) {
        this.cameraWindowSize = cameraWindowSize;
    }

    public int getOutTime() {
        return outTime;
    }

    public void setOutTime(int outTime) {
        this.outTime = outTime;
    }

    public boolean isForceTakeWhenTimeOut() {
        return forceTakeWhenTimeOut;
    }

    public void setForceTakeWhenTimeOut(boolean forceTakeWhenTimeOut) {
        this.forceTakeWhenTimeOut = forceTakeWhenTimeOut;
    }

    public boolean isNeedFaceDect() {
        return needFaceDect;
    }

    public void setNeedFaceDect(boolean needFaceDect) {
        this.needFaceDect = needFaceDect;
    }

    public boolean isNeedAliveDect() {
        return needAliveDect;
    }

    public void setNeedAliveDect(boolean needAliveDect) {
        this.needAliveDect = needAliveDect;
    }

    public boolean isNeedFaceVerify() {
        return needFaceVerify;
    }

    public void setNeedFaceVerify(boolean needFaceVerify) {
        this.needFaceVerify = needFaceVerify;
    }

    public IFaceVerifyManager getVerifyManager() {
        return verifyManager;
    }

    public void setVerifyManager(IFaceVerifyManager verifyManager) {
        this.verifyManager = verifyManager;
    }

    public boolean isForceTakeWhenVerifyError() {
        return forceTakeWhenVerifyError;
    }

    public void setForceTakeWhenVerifyError(boolean forceTakeWhenVerifyError) {
        this.forceTakeWhenVerifyError = forceTakeWhenVerifyError;
    }

    public boolean isForceTakeWhenVerifyFail() {
        return forceTakeWhenVerifyFail;
    }

    public void setForceTakeWhenVerifyFail(boolean forceTakeWhenVerifyFail) {
        this.forceTakeWhenVerifyFail = forceTakeWhenVerifyFail;
    }

    public int getDelayTimeBeforeDect() {
        return delayTimeBeforeDect;
    }

    public void setDelayTimeBeforeDect(int delayTimeBeforeDect) {
        this.delayTimeBeforeDect = delayTimeBeforeDect;
    }

    public int getFaceDectNum() {
        return faceDectNum;
    }

    public void setFaceDectNum(int faceDectNum) {
        this.faceDectNum = faceDectNum;
    }

    public static final class Builder {
        private int cameraId;
        private boolean previewRotate;
        private boolean previewMirrored;
        private CameraWindowSize cameraWindowSize;
        private int outTime;
        private boolean forceTakeWhenTimeOut;
        private boolean needFaceDect;
        private int faceDectNum = 1;
        private boolean needAliveDect;
        private boolean needFaceVerify;
        private IFaceVerifyManager verifyManager;
        private boolean forceTakeWhenVerifyError;
        private boolean forceTakeWhenVerifyFail;
        private int delayTimeBeforeDect;


        public Builder() {
        }

        public Builder setCameraId(int val) {
            cameraId = val;
            return this;
        }

        public Builder setPreviewRotate(boolean val) {
            previewRotate = val;
            return this;
        }

        public Builder setPreviewMirrored(boolean val) {
            previewMirrored = val;
            return this;
        }

        public Builder setCameraWindowSize(CameraWindowSize val) {
            cameraWindowSize = val;
            return this;
        }

        public Builder setOutTime(int val) {
            outTime = val;
            return this;
        }

        public Builder setForceTakeWhenTimeOut(boolean val) {
            forceTakeWhenTimeOut = val;
            return this;
        }

        public Builder setNeedFaceDect(boolean val) {
            needFaceDect = val;
            return this;
        }

        public Builder setNeedAliveDect(boolean val) {
            needAliveDect = val;
            return this;
        }

        public Builder setNeedFaceVerify(boolean val) {
            needFaceVerify = val;
            return this;
        }

        public Builder setVerifyManager(IFaceVerifyManager val) {
            verifyManager = val;
            return this;
        }

        public Builder setForceTakeWhenVerifyError(boolean val) {
            forceTakeWhenVerifyError = val;
            return this;
        }

        public Builder setForceTakeWhenVerifyFail(boolean val) {
            forceTakeWhenVerifyFail = val;
            return this;
        }

        public Builder setDelayTimeBeforeDect(int val) {
            delayTimeBeforeDect = val;
            return this;
        }

        public Builder setFaceDectNum(int val) {
            faceDectNum = val;
            return this;
        }

        public TakePictureParam build() {
            if(needFaceVerify && verifyManager == null) {
                throw new IllegalArgumentException("VerifyManager could't be null when needFaceVerify is true,you need to set a non-null verifyManager");
            }
            if(!needFaceDect && needFaceVerify) {
                throw new IllegalArgumentException("If you need faceVerify, please setNeedFaceDect true at the same time");
            }

            if(!needFaceDect && needAliveDect) {
                throw new IllegalArgumentException("If you need aliveDect, please setNeedFaceDect true at the same time");
            }

            if(needFaceDect && delayTimeBeforeDect < 0) {
                    throw new IllegalArgumentException("delayTimeBeforeDect should be equal or greater than 0");
            }

            if(needFaceDect && faceDectNum < 1) {
                throw new IllegalArgumentException("If you needFaceDect, faceDectNum should be greater than zero");
            }

            if(needAliveDect && faceDectNum > 1) {
                throw new IllegalArgumentException("aliveDect only supports faceDectNum is 1, unless you modify the code" +
                        "（这个框架活体检测只支持1张人脸，如果要多张，请自己修改框架的代码）");
            }

            if(!needFaceVerify && forceTakeWhenVerifyError) {
                L.w("needFaceVerify is false, forceTakeWhenVerifyError will not work");
            }

            if(forceTakeWhenTimeOut && forceTakeWhenVerifyError && forceTakeWhenVerifyFail) {
                L.w("You set  forceTakeWhenTimeOut and forceTakeWhenVerifyError and forceTakeWhenVerifyFail to true at the same time, only forceTakeWhenTimeOut will work");
            } else  if(forceTakeWhenTimeOut && forceTakeWhenVerifyError) {
                L.w("You set  forceTakeWhenTimeOut and forceTakeWhenVerifyError to true at the same time, only forceTakeWhenTimeOut will work");
            } else  if(forceTakeWhenTimeOut && forceTakeWhenVerifyFail) {
                L.w("You set  forceTakeWhenTimeOut and forceTakeWhenVerifyFail to true at the same time, only forceTakeWhenTimeOut will work");
            }

            return new TakePictureParam(this);
        }
    }

    @Override
    public String toString() {
        return "TakePictureParam{" +
                "cameraId=" + cameraId +
                ", previewRotate=" + previewRotate +
                ", previewMirrored=" + previewMirrored +
                ", cameraWindowSize=" + cameraWindowSize +
                ", outTime=" + outTime +
                ", forceTakeWhenTimeOut=" + forceTakeWhenTimeOut +
                ", delayTimeBeforeDect=" + delayTimeBeforeDect +
                ", needFaceDect=" + needFaceDect +
                ", faceDectNum=" + faceDectNum +
                ", needAliveDect=" + needAliveDect +
                ", needFaceVerify=" + needFaceVerify +
                ", forceTakeWhenVerifyFail=" + forceTakeWhenVerifyFail +
                ", forceTakeWhenVerifyError=" + forceTakeWhenVerifyError +
                ", verifyManager=" + verifyManager +
                '}';
    }
}
