package com.haoxueche.cameralib.cameraoperator;

import com.haoxueche.cameralib.manager.ICameraManager;

import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.functions.Function;

/**
 * @author 李昱辰 987424501@qq.com
 * @date 2019/1/31
 */
public class TakePictureOperator implements SingleTransformer<ICameraManager,
        byte[]> {
    @Override
    public SingleSource<byte[]> apply(Single<ICameraManager> upstream) {
        return upstream.delay(1, TimeUnit.SECONDS).flatMap(new Function<ICameraManager, SingleSource<? extends byte[]>>() {
            @Override
            public SingleSource<? extends byte[]> apply(ICameraManager iCameraManager) throws
                    Exception {
                return iCameraManager.takePicture();
            }
        });
    }
}
