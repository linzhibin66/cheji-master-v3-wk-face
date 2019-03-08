package com.haoxueche.cameralib.util;

import com.haoxueche.cameralib.cameraoperator.FaceDectAndVerifyAndTakePictureOperator;
import com.haoxueche.cameralib.cameraoperator.TakePictureOperator;
import com.haoxueche.cameralib.manager.ICameraManager;
import com.haoxueche.cameralib.managerProxy.NativeCameraManagerProxy;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

import static com.haoxueche.cameralib.util.ImageStatus.STATUS_OUT_CAMERA_NOT_CONNECTED;

/**
 * @author 李昱辰 987424501@qq.com
 * @date 2019/1/22
 */
public class FaceVerifyHelper {

    public static Single<FaceVerifyResult> takePicture(final TakePictureParam param) {
        final ICameraManager cameraManager = new NativeCameraManagerProxy();

        SingleTransformer<ICameraManager,
                byte[]> transformer;
        SingleSource<FaceVerifyResult> timeOutSource = null;

        if (param.isNeedFaceDect()) {
            final FaceDectAndVerifyAndTakePictureOperator transformerFinal
                    = new FaceDectAndVerifyAndTakePictureOperator(param.isNeedFaceVerify(), param.getFaceDectNum(),
                    param.getVerifyManager(), param.isNeedAliveDect(), param.getDelayTimeBeforeDect());
            timeOutSource = Single.create(new SingleOnSubscribe<FaceVerifyResult>
                    () {
                @Override
                public void subscribe(SingleEmitter<FaceVerifyResult> emitter) throws Exception {
                    if(param.isForceTakeWhenTimeOut()) {
                        if (transformerFinal.getLatestWatingVerifyData() != null) {
                            emitter.onSuccess(new FaceVerifyResult(transformerFinal.getLatestWatingVerifyData()));
                        } else {
                            try {
                                byte[] data= cameraManager.takePicture().blockingGet();
                                emitter.onSuccess(new FaceVerifyResult(data));
                            } catch (Exception e) {
                                emitter.tryOnError(new RuntimeException(e));
                            }
                        }
                        return;
                    }
                    if(param.isForceTakeWhenVerifyError()) {
                        boolean isVerifyError = isVerifyError(transformerFinal.getVerifyResultList());
                        if (transformerFinal.getLatestWatingVerifyData() != null && isVerifyError) {
                            emitter.onSuccess(new FaceVerifyResult(findVerifyResult(transformerFinal.getVerifyResultList(), ImageStatus.STATUS_VERIFY_ERROR)
                                    , transformerFinal.getLatestWatingVerifyData()));
                            return;
                        }
                    }
                    if(param.isForceTakeWhenVerifyFail()) {
                        boolean isVerifyFail = isVerifyFail(transformerFinal.getVerifyResultList());
                        if (transformerFinal.getLatestWatingVerifyData() != null && isVerifyFail) {
                            emitter.onSuccess(new FaceVerifyResult(findVerifyResult(transformerFinal.getVerifyResultList(), ImageStatus.STATUS_VERIFY_FAIL)
                                    , transformerFinal.getLatestWatingVerifyData()));
                            return;
                        }
                    }
                    emitter.tryOnError(new TimeoutException());

                }

            });

            transformer = transformerFinal;
        } else {
            transformer = new TakePictureOperator();

            timeOutSource = Single.create(new SingleOnSubscribe<FaceVerifyResult>
                    () {
                @Override
                public void subscribe(SingleEmitter<FaceVerifyResult> emitter) throws Exception {
                   try {
                       byte[] data = cameraManager.takePicture().blockingGet();
                       emitter.onSuccess( new FaceVerifyResult(data));
                   } catch (Exception e) {
                       emitter.tryOnError(new RuntimeException(e));
                   }
                }
            });
        }


        Single<FaceVerifyResult> single = cameraManager.init(param.getCameraWindowSize(),
                param.getCameraId(), param.isPreviewMirrored(), param.isPreviewRotate()).compose
                (transformer).map(new Function<byte[], FaceVerifyResult>() {
            @Override
            public FaceVerifyResult apply(byte[] bytes) throws Exception {
                return new FaceVerifyResult(bytes);
            }
        });
        if (param.isForceTakeWhenTimeOut() || param.isForceTakeWhenVerifyError() || param.isForceTakeWhenVerifyFail()) {
            single = single.timeout(param.getOutTime(), TimeUnit.SECONDS, timeOutSource);
        } else {
            single = single.timeout(param.getOutTime(), TimeUnit.SECONDS);
        }
        return single.doOnSuccess(new Consumer<FaceVerifyResult>() {
            @Override
            public void accept(FaceVerifyResult faceVerifyResult) throws Exception {
                if (ImageUtil.isErrorImage(faceVerifyResult.getData())) {
                    faceVerifyResult.setStatus(STATUS_OUT_CAMERA_NOT_CONNECTED);
                }
            }
        }).doFinally(new Action() {
            @Override
            public void run() throws Exception {
                cameraManager.release();
                if(param.getVerifyManager() instanceof AutoCloseable) {
                    ((AutoCloseable) param.getVerifyManager()).close();
                }
            }
        });
    }


    public static boolean isVerifyError(List<VerifyResult> verifyResultList) {
        for(VerifyResult verifyResult : verifyResultList) {
           if(verifyResult.getStatus() != ImageStatus.STATUS_VERIFY_ERROR) {
               return false;
           }
        }
        return true;
    }

    public static boolean isVerifyFail(List<VerifyResult> verifyResultList) {
        for(VerifyResult verifyResult : verifyResultList) {
            if(verifyResult.getStatus() == ImageStatus.STATUS_VERIFY_FAIL) {
                return true;
            }
        }
        return false;
    }

    public static VerifyResult findVerifyResult(List<VerifyResult> verifyResultList, int status) {
        for(VerifyResult verifyResult : verifyResultList) {
            if(verifyResult.getStatus() == status) {
                return verifyResult;
            }
        }
        return null;
    }

}
