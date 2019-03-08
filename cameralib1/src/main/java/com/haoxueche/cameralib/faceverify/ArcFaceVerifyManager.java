package com.haoxueche.cameralib.faceverify;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;
import com.haoxueche.cameralib.alivedect.AliveDectUtil;
import com.haoxueche.cameralib.exception.FaceVerifyEngineInitException;
import com.haoxueche.cameralib.util.IFaceVerifyManager;
import com.haoxueche.cameralib.util.ImageStatus;
import com.haoxueche.cameralib.util.VerifyResult;
import com.haoxueche.winterlog.L;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 李昱辰 987424501@qq.com
 * @date 2019/2/22
 */
public class ArcFaceVerifyManager implements IFaceVerifyManager, AutoCloseable {
    public static final float VERIFY_SUCC_SCORE = 0.75f;

    private Context context;

    private FaceEngine faceEngine;

    /**
     * 主图的第0张人脸的特征数据
     */
    private FaceFeature mainFeature;

    private FaceVerifyEngineInitException exception;

    public ArcFaceVerifyManager(Context context, byte[] mainVerifyData) {
        this.context = context;
        try {
            initEngine(true);
            mainFeature = getFaceFeature(mainVerifyData);
        } catch (FaceVerifyEngineInitException e) {
            L.e(e);
            exception = e;
        }
    }

    private FaceFeature getFaceFeature(byte[] data) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

        bitmap = ImageUtil.alignBitmapForNv21(bitmap);
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        //bitmap转NV21
        data = ImageUtil.bitmapToNv21(bitmap, width, height);
        FaceFeature faceFeature = new FaceFeature();
        List<FaceInfo> faceInfoList = new ArrayList<>();
        //人脸检测
        int detectCode = faceEngine.detectFaces(data, width, height, FaceEngine
                .CP_PAF_NV21, faceInfoList);
        if (detectCode != 0 || faceInfoList.size() == 0) {
//            Toast.makeText(context, "face detection finished, code is " + detectCode + ", face " +
//                    "num is " + faceInfoList.size(), Toast.LENGTH_SHORT).show();
            faceFeature = null;
        } else {
            int res = faceEngine.extractFaceFeature(data, width, height, FaceEngine
                    .CP_PAF_NV21, faceInfoList.get(0), faceFeature);
            if (res != ErrorInfo.MOK) {
                faceFeature = null;
            }
        }
        return faceFeature;
    }

    @Override
    public VerifyResult faceVerify(byte[] data) {
        if (exception != null) {
            throw exception;
        }
        if (mainFeature == null) {
            return new VerifyResult(ImageStatus.STATUS_VERIFY_FAIL, "用来检测的基准图片中无人脸");
        } else {
            FaceFeature compareFeature = getFaceFeature(data);
            FaceSimilar faceSimilar = new FaceSimilar();
            int compareResult = faceEngine.compareFaceFeature(mainFeature, compareFeature,
                    faceSimilar);
            if (compareResult == ErrorInfo.MOK) {
                L.i("score==" + faceSimilar.getScore());
                if (faceSimilar.getScore() > VERIFY_SUCC_SCORE) {
                    return VerifyResult.SUCC;
                } else {
                    return new VerifyResult(ImageStatus.STATUS_VERIFY_FAIL, "相似度不够");
                }
            } else {
                return new VerifyResult(ImageStatus.STATUS_VERIFY_FAIL, "比对不通过，compareResult:" +
                        compareResult);
            }
        }
    }

    private void initEngine(boolean retry) throws FaceVerifyEngineInitException {

        faceEngine = new FaceEngine();
        int faceEngineCode = faceEngine.init(context, FaceEngine.ASF_DETECT_MODE_IMAGE,
                FaceEngine.ASF_OP_0_HIGHER_EXT,
                16, 6, FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_FACE_DETECT);

        L.i("initEngine: init " + faceEngineCode);

        if (faceEngineCode != ErrorInfo.MOK) {
            if (retry && faceEngineCode == ErrorInfo.MERR_ASF_NOT_ACTIVATED) {
                AliveDectUtil.activeEngine(context);
                initEngine(false);
            } else {
                throw new FaceVerifyEngineInitException("人脸引擎初始化失败，errorcode：" + faceEngineCode);
            }
        }
    }

    private void unInitEngine() {
        if (faceEngine != null) {
            int faceEngineCode = faceEngine.unInit();
            L.i("unInitEngine: " + faceEngineCode);
        }
    }

    @Override
    public void close() {
        unInitEngine();
    }
}
