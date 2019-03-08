package com.haoxueche.cameralib.util;

import io.reactivex.Single;

/**
 * @author 李昱辰 987424501@qq.com
 * @date 2019/1/23
 */
public class MockFaceVerifyManager {
    public static Single<Boolean> faceVerify(byte[] data) {
        try {
            Thread.sleep(30000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Single.just(false);
    }
}
