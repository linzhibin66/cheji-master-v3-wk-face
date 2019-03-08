package com.dgcheshang.cheji.Camera;

import com.haoxueche.cameralib.util.FaceVerifyResult;

/**
 * @author 李昱辰 987424501@qq.com
 * @date 2019/2/12
 */
public class FaceVerifyAndPhotoSaveResult {
    private FaceVerifyResult faceVerifyResult;
    private PhotoSaveInfo photoSaveInfo;

    public FaceVerifyAndPhotoSaveResult(FaceVerifyResult faceVerifyResult, PhotoSaveInfo
            photoSaveInfo) {
        this.faceVerifyResult = faceVerifyResult;
        this.photoSaveInfo = photoSaveInfo;
    }

    public FaceVerifyResult getFaceVerifyResult() {
        return faceVerifyResult;
    }

    public void setFaceVerifyResult(FaceVerifyResult faceVerifyResult) {
        this.faceVerifyResult = faceVerifyResult;
    }

    public PhotoSaveInfo getPhotoSaveInfo() {
        return photoSaveInfo;
    }

    public void setPhotoSaveInfo(PhotoSaveInfo photoSaveInfo) {
        this.photoSaveInfo = photoSaveInfo;
    }
}
