package com.haoxueche.cameralib.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.hardware.Camera.FaceDetectionListener;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.text.TextUtils;

import com.haoxueche.cameralib.alivedect.AliveDectHelper;
import com.haoxueche.cameralib.alivedect.AliveInfo;
import com.haoxueche.cameralib.camera.CameraUtil;
import com.haoxueche.cameralib.common.CameraStateCallback;
import com.haoxueche.cameralib.exception.EmptyByteArrayException;
import com.haoxueche.cameralib.exception.AliveDectEngineInitException;
import com.haoxueche.cameralib.exception.NoCameraException;
import com.haoxueche.cameralib.ui.FloatCameraWindow;
import com.haoxueche.cameralib.util.App;
import com.haoxueche.cameralib.util.CameraInfo;
import com.haoxueche.cameralib.util.CameraWindowSize;
import com.haoxueche.cameralib.util.ImageUtil;
import com.haoxueche.winterlog.L;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.reactivex.Emitter;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiPredicate;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * @author 李昱辰 987424501@qq.com
 * @date 2019/1/21
 */
public class NativeCameraManager implements CameraStateCallback, FaceDetectionListener,
        PreviewCallback {
    private Context context = App.getInstance();
    private FloatCameraWindow floatCameraWindow;
    private Emitter<Face[]> faceEmitter;
    private Camera camera;
    //TODO CopyOnWriteArrayList的使用不确定是否合适
    private List<Emitter<byte[]>> previewEmitters = new CopyOnWriteArrayList<>();
    private int width = CameraInfo.PREVIEW_SIZE.getWidth();
    private int height = CameraInfo.PREVIEW_SIZE.getHeight();
    private AliveDectHelper aliveDectHelper;
    private SingleEmitter<NativeCameraManager> cameraSingleEmitter;
    private int faceDectNum;

    private boolean nativeFaceVerify;
    private boolean mirrored;
    private boolean rotate;

    public NativeCameraManager() {

    }


    public Single<NativeCameraManager> initCamera(final CameraWindowSize viewSize, final int
            cameraId, final boolean mirrored, final boolean rotate) {
        this.mirrored = mirrored;
        this.rotate = rotate;
        return Single.create(new SingleOnSubscribe<Camera>() {
            @Override
            public void subscribe(SingleEmitter<Camera> emitter) throws Exception {
                //是否有相机
                boolean cameraSupported = CameraUtil.isCameraSupport(context);
                if (cameraSupported) {
                    //获取相机对象
                    camera = CameraUtil.getCameraInstance(cameraId);
                    if (rotate) {
                        camera.setDisplayOrientation(180);
                    }
                    emitter.onSuccess(camera);
                } else {
                    emitter.tryOnError(new NoCameraException());
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnSuccess
                (new Consumer<Camera>() {
                    @Override
                    public void accept(Camera camera) throws Exception {
                        floatCameraWindow = new FloatCameraWindow(context, viewSize, mirrored,
                                rotate);
                        floatCameraWindow.setCamera(camera, NativeCameraManager.this);
                    }
                }).flatMap(new Function<Camera, SingleSource<NativeCameraManager>>() {
            @Override
            public SingleSource<NativeCameraManager> apply(Camera camera) throws
                    Exception {
                return Single.create(new SingleOnSubscribe<NativeCameraManager>() {
                    @Override
                    public void subscribe(SingleEmitter<NativeCameraManager> emitter) throws
                            Exception {
                        cameraSingleEmitter = emitter;
                    }
                });
            }
        }).observeOn(Schedulers.io());
    }

    public Single<byte[]> takePicture() {
        return Single.create(new SingleOnSubscribe<byte[]>() {
            @Override
            public void subscribe(final SingleEmitter<byte[]> emitter) throws Exception {
                camera.takePicture(null, null, new PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        if (data != null) {
                            if (rotate || mirrored) {
                                Bitmap bitmap = ImageUtil.convert(BitmapFactory.decodeByteArray
                                        (data, 0, data.length), rotate, mirrored);
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bitmap.compress(CompressFormat.JPEG, 100, baos);
                                bitmap.recycle();
                                data = baos.toByteArray();
                            }
                            emitter.onSuccess(data);
                        } else {
                            emitter.tryOnError(new EmptyByteArrayException());
                        }
                    }
                });
            }
        }).doOnSuccess(new Consumer<byte[]>() {
            @Override
            public void accept(byte[] bytes) throws Exception {
                camera.startPreview();
                if (nativeFaceVerify) {
                    //开始人脸检测
                    CameraUtil.startFaceDetection(camera, NativeCameraManager.this);
                }
            }
        });
    }


    @Override
    public void onStartPreview(Camera camera) {
        cameraSingleEmitter.onSuccess(this);
    }

    @Override
    public void onBeforeStartPreview(Camera camera) {
        L.i("onBeforeStartPreview");
        try {
            Camera.Parameters parameters = camera.getParameters();
            //设置预览画面大小
            parameters.setPreviewSize(CameraInfo.PREVIEW_SIZE.getWidth(), CameraInfo.PREVIEW_SIZE
                    .getHeight());

            //设置拍照大小
            parameters.setPictureSize(CameraInfo.PREVIEW_SIZE.getWidth(), CameraInfo.PREVIEW_SIZE
                    .getHeight());
//        requestLayout();
            camera.setParameters(parameters);
        } catch (Exception e) {
            RuntimeException exception = new RuntimeException("相机设置参数出错", e);
            cameraSingleEmitter.tryOnError(exception);
        }
    }

    @Override
    public void onFaceDetection(Face[] faces, Camera camera) {
        Rect[] rects = ImageUtil.faces2Rects(faces);
        L.i("rects==" + (rects.length > 0 ? rects[0].toShortString() : 0));
        floatCameraWindow.setFace(ImageUtil.adaptCoordinate(rects, -1000, -1000));
        if (faceEmitter != null && faces != null && faces.length >= faceDectNum) {
            faceEmitter.onNext(faces);
        }

    }

    public Observable<Face[]> faceDect(final int faceDectNum) {
        return Observable.create(new ObservableOnSubscribe<Face[]>() {
            @Override
            public void subscribe(ObservableEmitter<Face[]> emitter) throws Exception {
                nativeFaceVerify = true;
                floatCameraWindow.setFaceCoodinateSize(2000, 2000);
                faceEmitter = emitter;
                NativeCameraManager.this.faceDectNum = faceDectNum;
                //开始人脸检测
                CameraUtil.startFaceDetection(camera, NativeCameraManager.this);
            }
        }).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {

            }
        }).observeOn(Schedulers.io());
    }

    public Observable<AliveInfo> aliveDect() throws AliveDectEngineInitException {
        aliveDectHelper = new AliveDectHelper(context, width, height);
        final String errorMsg = aliveDectHelper.initEngine();
        if (!TextUtils.isEmpty(errorMsg)) {
            //活体引擎激活失败
            throw new AliveDectEngineInitException("活体引擎激活失败:" + errorMsg);
        }
        return preview().observeOn(Schedulers.io()).map(new Function<byte[], AliveInfo>() {
            @Override
            public AliveInfo apply(byte[] bytes) throws Exception {
                return aliveDectHelper.aliveDect(bytes);
            }
        }).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
                floatCameraWindow.setFaceCoodinateSize(width, height);
            }
        }).doOnNext(new Consumer<AliveInfo>() {
            @Override
            public void accept(AliveInfo aliveInfo) throws Exception {
                Rect[] rects;
                if (aliveInfo.getFaceRect() != null) {
                    rects = new Rect[]{aliveInfo.getFaceRect()};
                } else {
                    rects = new Rect[0];
                }
                floatCameraWindow.setFace(rects);
            }
        }).observeOn(AndroidSchedulers.mainThread()).distinctUntilChanged(new BiPredicate<AliveInfo, AliveInfo>() {
            @Override
            public boolean test(AliveInfo aliveInfo, AliveInfo aliveInfo2) throws Exception {
                //如果两次活体状态不一样
                if (aliveInfo.getStatus() != aliveInfo2.getStatus()) {
                    return false;
                    //如果都是活体
                } else if (aliveInfo.getStatus() == AliveInfo.ALIVE) {
                    return false;
                }
                return true;
            }
        }).doOnNext(new Consumer<AliveInfo>() {
            @Override
            public void accept(AliveInfo aliveInfo) throws Exception {
                floatCameraWindow.setHint(aliveInfo.getDescription());
            }
        }).filter(new Predicate<AliveInfo>() {
            @Override
            public boolean test(AliveInfo aliveInfo) throws Exception {
                return aliveInfo.getAliveImageData() != null;
            }
        });
    }

    public void release() {
        if (previewEmitters.size() > 0) {
            for (Emitter<byte[]> emitter : previewEmitters) {
                emitter.onComplete();
            }
            previewEmitters.clear();
        }
        CameraUtil.releaseCamera(camera);
        if (floatCameraWindow != null) {
            floatCameraWindow.release();
        }
        if (aliveDectHelper != null) {
            aliveDectHelper.release();
        }
    }

    public Observable<byte[]> preview() {
        return Observable.create(new ObservableOnSubscribe<byte[]>() {
            @Override
            public void subscribe(ObservableEmitter<byte[]> emitter) throws Exception {
                //设置相机预览数据回调
                camera.setPreviewCallback(NativeCameraManager.this);
                NativeCameraManager.this.previewEmitters.add(emitter);
            }
        });
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (previewEmitters.size() > 0 && data != null) {
            for (Emitter<byte[]> emitter: previewEmitters) {
                emitter.onNext(data);
            }
        }
    }

}
