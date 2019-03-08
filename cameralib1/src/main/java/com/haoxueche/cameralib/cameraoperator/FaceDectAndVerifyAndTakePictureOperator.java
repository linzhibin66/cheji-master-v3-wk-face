package com.haoxueche.cameralib.cameraoperator;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.haoxueche.cameralib.alivedect.AliveInfo;
import com.haoxueche.cameralib.exception.AliveDectEngineInitException;
import com.haoxueche.cameralib.manager.ICameraManager;
import com.haoxueche.cameralib.util.App;
import com.haoxueche.cameralib.util.CameraInfo;
import com.haoxueche.cameralib.util.IFaceVerifyManager;
import com.haoxueche.cameralib.util.ImageUtil;
import com.haoxueche.cameralib.util.VerifyResult;
import com.haoxueche.winterlog.L;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * @author 李昱辰 987424501@qq.com
 * @date 2019/1/23
 * 检测到人脸后拍张照
 */
public class FaceDectAndVerifyAndTakePictureOperator implements SingleTransformer<ICameraManager,
        byte[]> {
    private ICameraManager cameraManager;
    private IFaceVerifyManager faceVerifyManager;
    private int delayTimeBeforeDect;

    private boolean needFaceVerify;
    private int faceDectNum;

    private boolean aliveDect;
    private List<VerifyResult> verifyResultList = new ArrayList<>();

    public byte[] getLatestWatingVerifyData() {
        return latestWatingVerifyData;
    }

    private byte[] latestWatingVerifyData;

    private byte[] aliveDectData;

    public FaceDectAndVerifyAndTakePictureOperator(boolean needFaceVerify, int faceDectNum, final IFaceVerifyManager
            faceVerifyManager, boolean aliveDect, int delayTimeBeforeDect) {
        this.needFaceVerify = needFaceVerify;
        this.aliveDect = aliveDect;
        this.delayTimeBeforeDect = delayTimeBeforeDect;
        this.faceDectNum = faceDectNum;
        if(faceVerifyManager != null) {
            this.faceVerifyManager = new IFaceVerifyManager() {
                @Override
                public VerifyResult faceVerify(byte[] data) {
                    VerifyResult verifyResult =  faceVerifyManager.faceVerify(data);
                    L.i(verifyResult.toString());
                    verifyResultList.add(verifyResult);
                    return verifyResult;
                }
            };
        }
    }

    @Override
    public SingleSource<byte[]> apply(Single<ICameraManager> upstream) {
        Observable<byte[]> observable = upstream.delay(delayTimeBeforeDect, TimeUnit.SECONDS).flatMapObservable(new Function<ICameraManager,
                Observable<?>>() {

            @Override
            public Observable<?> apply(ICameraManager cameraManager) throws Exception {
                FaceDectAndVerifyAndTakePictureOperator.this.cameraManager = cameraManager;
                if (aliveDect) {
                    Observable<?> observable1;
                    try {
                        observable1 = cameraManager.aliveDect();
                        //当活体检测初始化失败后，替换为人脸检测
                    } catch (AliveDectEngineInitException e) {
                        aliveDect = false;
                        observable1 = cameraManager.faceDect(faceDectNum);
                    }
                    return observable1;
                } else {
                    return cameraManager.faceDect(faceDectNum);
                }

            }
        }).toFlowable(BackpressureStrategy.DROP).observeOn(Schedulers.io(), false, 1)
                .toObservable().throttleFirst(5, TimeUnit.SECONDS).flatMap(new Function<Object,
                        ObservableSource<byte[]>>() {
                    @Override
                    public ObservableSource<byte[]> apply(Object object) throws Exception {
                        if (aliveDect) {
                            AliveInfo aliveInfo = (AliveInfo) object;
                            Bitmap imageBitmap = ImageUtil.nv21toBitmap(App.getInstance(),
                                    aliveInfo.getAliveImageData(),
                                    CameraInfo.PREVIEW_SIZE.getWidth(), CameraInfo.PREVIEW_SIZE
                                            .getHeight());
                            Rect faceRect = aliveInfo.getFaceRect();
                            Bitmap faceBitmap = Bitmap.createBitmap(imageBitmap, faceRect
                                    .left, faceRect.top, faceRect.width(), faceRect.height());
                            aliveDectData = ImageUtil.compressImage(faceBitmap, 35);
                        }
                        L.i("pos1");
                        return cameraManager.takePicture().toObservable();
                    }
                });
        if (needFaceVerify) {
            observable = observable.doOnNext(new Consumer<byte[]>() {
                @Override
                public void accept(byte[] bytes) throws Exception {
                    latestWatingVerifyData = bytes;
                }
            }).observeOn(Schedulers.io()).filter(new Predicate<byte[]>() {
                @Override
                public boolean test(byte[] bytes) throws Exception {
                    L.i("pos2");
                    if (aliveDectData != null) {
                        return Single.zip(Single.just(faceVerifyManager.faceVerify(aliveDectData).isSucc()),
                                Single.just(faceVerifyManager.faceVerify(bytes).isSucc()).subscribeOn
                                        (Schedulers.io()),

                                new BiFunction<Boolean, Boolean, Boolean>() {
                                    @Override
                                    public Boolean apply(Boolean aBoolean, Boolean aBoolean2)
                                            throws Exception {
                                        return aBoolean && aBoolean2;
                                    }
                                }).blockingGet();
                    } else {
                        return faceVerifyManager.faceVerify(bytes).isSucc();
                    }
                }
            });
        }
        return observable.take(1).singleOrError();
    }

    public List<VerifyResult> getVerifyResultList() {
        return verifyResultList;
    }

}
