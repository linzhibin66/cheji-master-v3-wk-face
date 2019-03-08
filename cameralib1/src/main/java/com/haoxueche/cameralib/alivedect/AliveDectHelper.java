package com.haoxueche.cameralib.alivedect;

import android.content.Context;
import android.graphics.Rect;

import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.LivenessInfo;
import com.arcsoft.face.VersionInfo;
import com.haoxueche.winterlog.L;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyc(987424501@qq.com) on 2018/11/30.
 */
public class AliveDectHelper {
    public static final String TAG = "AliveDectHelper";
    private Context context;

    private int imageWidth;
    private int imageHeight;
    private boolean released;

    private FaceEngine faceEngine = new FaceEngine();
    private int processMask = FaceEngine.ASF_LIVENESS;
    private int afCode;

    public AliveDectHelper(Context context, int imageWidth, int imageHeight) {
        this.context = context;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    public String initEngine() {
        return initEngine(true);
    }

    /**
     * @return errorMsg
     */
    public String initEngine(boolean retry) {
        String errorMsg = "";
        afCode = faceEngine.init(context, FaceEngine.ASF_DETECT_MODE_VIDEO, FaceEngine
                        .ASF_OP_0_HIGHER_EXT,
                16, 1, FaceEngine.ASF_FACE_DETECT | FaceEngine.ASF_LIVENESS);
        VersionInfo versionInfo = new VersionInfo();
        faceEngine.getVersion(versionInfo);
        L.i("initEngine:  init: " + afCode + "  version:" + versionInfo);
        if (afCode != ErrorInfo.MOK) {
            if (retry && afCode == ErrorInfo.MERR_ASF_NOT_ACTIVATED) {
                AliveDectUtil.activeEngine(context);
                return initEngine(false);
            } else {
                errorMsg = "人脸引擎初始化失败，errorcode：" + afCode;
            }
        }

        return errorMsg;
    }

    public AliveInfo aliveDect(byte[] nv21) {
        List<FaceInfo> faceInfoList = new ArrayList<>();
        Rect faceRect = null;
        AliveInfo aliveInfo = null;
        int code = faceEngine.detectFaces(nv21, imageWidth, imageHeight, FaceEngine.CP_PAF_NV21,
                faceInfoList);
        if (code == ErrorInfo.MOK) {
            if (faceInfoList.size() > 0) {
                code = faceEngine.process(nv21, imageWidth, imageHeight, FaceEngine.CP_PAF_NV21,
                        faceInfoList,
                        processMask);
                if (code == ErrorInfo.MOK) {
                    List<LivenessInfo> faceLivenessInfoList = new ArrayList<>();
                    int livenessCode = faceEngine.getLiveness(faceLivenessInfoList);
                    L.i("startLiveness: errorcode " + livenessCode);

                    if (livenessCode == ErrorInfo.MOK) {
                        final int liveness = faceLivenessInfoList.get(0).getLiveness();
                        faceRect = faceInfoList.get(0).getRect();
                        L.i("faceRect==" + faceRect.toShortString());
                        L.i("getLivenessScore: liveness " + liveness);
                        if (liveness == LivenessInfo.NOT_ALIVE) {
                            L.i("非活体");
                            aliveInfo = new AliveInfo(AliveInfo.NOT_ALIVE, null, faceRect);
                        } else if (liveness == LivenessInfo.ALIVE) {
                            L.i("活体");
                            if (isRectInBounds(faceRect, imageWidth, imageHeight)) {
//                                Bitmap imageBitmap = ImageUtil.nv21toBitmap(context, nv21,
//                                        imageWidth, imageHeight);
//                                Bitmap faceBitmap = Bitmap.createBitmap(imageBitmap, faceRect
//                                        .left, faceRect.top, faceRect.width(), faceRect.height());
//                                byte[] data = ImageUtil.compressImage(faceBitmap, 35);
                                aliveInfo = new AliveInfo(AliveInfo.ALIVE, nv21, faceRect);
                            } else {
                                L.i("isRectInBounds false");
                            }
                        } else {
                            L.i("未知");
                            aliveInfo = new AliveInfo(AliveInfo.UNKNOW, null, faceRect);
                        }
                    }
                }
            } else {
                L.i("无人脸");
                aliveInfo = new AliveInfo(AliveInfo.NO_FACE, null, faceRect);
            }
        }
        if (aliveInfo == null) {
            aliveInfo = new AliveInfo(AliveInfo.UNKNOW, null, faceRect);
        }
        return aliveInfo;
    }


    private boolean isRectInBounds(Rect rect, int boundWidth, int boundHeight) {
        return rect.top >= 0 && rect.left >= 0 && rect.right <= boundWidth && rect.bottom <=
                boundHeight;
    }

    public void release() {
        if (released) {
            return;
        }
        released = true;
        //销毁引擎
        if (afCode == 0 && faceEngine != null) {
            afCode = faceEngine.unInit();
            L.i("unInitEngine: " + afCode);
        }
    }

}
