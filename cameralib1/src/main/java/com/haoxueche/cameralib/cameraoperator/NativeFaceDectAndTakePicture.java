package com.haoxueche.cameralib.cameraoperator;//package com.haoxueche.camerademo.cameraoperator;
//
//import com.haoxueche.cameralib.manager.ICameraManager;
//
//import io.reactivex.ObservableSource;
//import io.reactivex.Single;
//import io.reactivex.SingleSource;
//import io.reactivex.SingleTransformer;
//import io.reactivex.functions.Function;
//
///**
// * @author 李昱辰 987424501@qq.com
// * @date 2019/1/23
// * 检测到人脸后拍张照
// */
//public class NativeFaceDectAndTakePicture implements SingleTransformer<ICameraManager, byte[]> {
//    private ICameraManager cameraManager;
//
//
//    @Override
//    public SingleSource<byte[]> apply(Single<ICameraManager> upstream) {
//        return upstream
//                .flatMapObservable(new Function<ICameraManager, ObservableSource<?>>() {
//                    @Override
//                    public ObservableSource<?> apply(ICameraManager iCameraManager) throws
//                            Exception {
//                        NativeFaceDectAndTakePicture.this.cameraManager = iCameraManager;
//                        return cameraManager.faceDect();
//                    }
//                }).take(1).singleOrError().flatMap(new Function<Object, SingleSource<byte[]>>() {
//
//                    @Override
//                    public SingleSource<byte[]> apply(Object o) throws Exception {
//                        return cameraManager.takePicture();
//                    }
//                });
//    }
//}
