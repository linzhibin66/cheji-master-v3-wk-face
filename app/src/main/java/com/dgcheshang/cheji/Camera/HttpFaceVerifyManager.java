package com.dgcheshang.cheji.Camera;

import com.haoxueche.cameralib.util.IFaceVerifyManager;
import com.haoxueche.cameralib.util.ImageStatus;
import com.haoxueche.cameralib.util.VerifyResult;
import com.haoxueche.winterlog.L;

/**
 * @author 李昱辰 987424501@qq.com
 * @date 2019/2/12
 */
public class HttpFaceVerifyManager implements IFaceVerifyManager {
    private byte[] mainVerifyData;

    public HttpFaceVerifyManager(byte[] mainVerifyData) {
        this.mainVerifyData = mainVerifyData;
    }


    @Override
    public VerifyResult faceVerify(byte[] data) {
        VerifyResult verifyResult;
        try {
            boolean verifySucc;
            //TODO 把这里替换为自己的http请求，现在是一个模拟测试的
            verifySucc = Test.mockHttpFaceVerify(mainVerifyData, data);


            if (verifySucc) {
                verifyResult = new VerifyResult(ImageStatus.STATUS_SUCC, "");
            } else {
                verifyResult = new VerifyResult(ImageStatus.STATUS_VERIFY_FAIL, "人脸验证未通过");
            }
        } catch (Exception e) {
            if((e.getCause() instanceof InterruptedException)) {
                verifyResult = new VerifyResult(ImageStatus.STATUS_VERIFY_FAIL, "人脸验证未通过");
            } else {
                verifyResult = new VerifyResult(ImageStatus.STATUS_VERIFY_ERROR, "人脸验证服务不可用");
                L.e(e);
            }

        }
        return verifyResult;
    }
}
