package com.dgcheshang.cheji.Camera;

import android.hardware.Camera;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.dgcheshang.cheji.CjApplication;
import com.dgcheshang.cheji.Tools.FilesUtil;
import com.dgcheshang.cheji.netty.conf.NettyConf;
import com.dgcheshang.cheji.netty.util.ZdUtil;
import com.haoxueche.cameralib.exception.FaceVerifyEngineInitException;
import com.haoxueche.cameralib.faceverify.ArcFaceVerifyManager;
import com.haoxueche.cameralib.util.CameraInfo;
import com.haoxueche.cameralib.util.CameraWindowSize;
import com.haoxueche.cameralib.util.FaceVerifyHelper;
import com.haoxueche.cameralib.util.FaceVerifyResult;
import com.haoxueche.cameralib.util.ImageStatus;
import com.haoxueche.cameralib.util.TakePictureParam;
import com.haoxueche.winterlog.L;

import java.io.File;
import java.util.concurrent.TimeoutException;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

/**
 * @author 李昱辰 987424501@qq.com
 * @date 2019/2/18
 */
public class Test {
    public static void testTakePhoto(CameraWindowSize cameraWindowSize, boolean
            forceTakeWhenTimeOut, boolean aliveDect) {
        int cameraId = SharePreferUtil.isTimingPhotoUseCamera0() ? CameraInfo
                .CAMERA_FACING_INSIDE : CameraInfo.CAMERA_FACING_OUT;
        boolean previewMirrored = cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT;
        boolean cameraRotate;
        if (cameraId == CameraInfo.CAMERA_FACING_OUT) {
            cameraRotate = SharePreferUtil.isOutCameraRotate();
        } else {
            cameraRotate = SharePreferUtil.isInsideCameraRotate();
        }
        //用来人脸验证的基准图片
        byte[] data = FileUtil.readFile(FileUtil.DEFAULT_IMG_DIR + File.separator +
                "1550561982.jpg");

        TakePictureParam takePictureParam = new TakePictureParam.Builder()
                .setCameraId(cameraId) //设置相机id
                .setPreviewRotate(cameraRotate) //预览是否旋转
                .setPreviewMirrored(previewMirrored) //预览画面是否是镜像的
                .setCameraWindowSize(cameraWindowSize)
                .setOutTime(30) //超时时间
                .setDelayTimeBeforeDect(3) //人脸检测前延迟几秒
                .setForceTakeWhenTimeOut(forceTakeWhenTimeOut) //超时时间到后是否强制拍一张
                .setNeedFaceDect(true) //是否需要人脸检测
                .setNeedFaceVerify(true) //是否需要人脸验证
                .setVerifyManager(new ArcFaceVerifyManager(CjApplication.getInstance(), data)) //设置人脸验证的接口
                .setNeedAliveDect(aliveDect) //是否活体检测
                .setForceTakeWhenVerifyError(true) //人脸验证是出错状态时是否返回数据
                .setForceTakeWhenVerifyFail(true)  //人脸验证是不通过状态时是否返回数据
                .build();

        FaceVerifyHelper.takePicture(takePictureParam).map(new Function<FaceVerifyResult,
                FaceVerifyAndPhotoSaveResult>() {

            @Override
            public FaceVerifyAndPhotoSaveResult apply(FaceVerifyResult faceVerifyResult) throws
                    Exception {
                return new FaceVerifyAndPhotoSaveResult(faceVerifyResult, PhotoUtil.savePhoto
                        (faceVerifyResult, ""));
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<FaceVerifyAndPhotoSaveResult>() {


            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(FaceVerifyAndPhotoSaveResult result) {
                switch (result.getFaceVerifyResult().getStatus()) {
                    case ImageStatus.STATUS_SUCC:
                        //人脸验证通过
                        break;
                    case ImageStatus.STATUS_OUT_CAMERA_NOT_CONNECTED:
                        Toast.makeText(CjApplication.getInstance(), "外置摄像头故障，请检查相机线连接或通知维护人员修理设备", Toast
                                .LENGTH_SHORT).show();
                        break;
                    case ImageStatus.STATUS_VERIFY_FAIL:
                        Toast.makeText(CjApplication.getInstance(), result.getFaceVerifyResult().getMessage(), Toast
                                .LENGTH_SHORT).show();
                        break;
                    case ImageStatus.STATUS_VERIFY_ERROR:
                        Toast.makeText(CjApplication.getInstance(), result.getFaceVerifyResult().getMessage(), Toast
                                .LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                Toast.makeText(CjApplication.getInstance(), "照片保存成功：" + result.getPhotoSaveInfo().toString
                        (), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                L.e(e);
                if (e instanceof TimeoutException) {
                    Toast.makeText(CjApplication.getInstance(), "人脸验证未通过：超时", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CjApplication.getInstance(), "拍照出错: " + (e.getMessage() == null ? "" : e
                            .getMessage()), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /**
     * 小屏，超时时间到后不强制拍照，开启活体检测、人脸验证
     * @param view
     */
    public static void floatTakePicture(View view, String pic, final String type, final String handlername) {
        //拍照状态改为正在拍照
        NettyConf.ispz=true;
        int cameraId = SharePreferUtil.isTimingPhotoUseCamera0() ? CameraInfo
                .CAMERA_FACING_INSIDE : CameraInfo.CAMERA_FACING_OUT;
        boolean previewMirrored = cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT;
        boolean cameraRotate;
        if (cameraId == CameraInfo.CAMERA_FACING_OUT) {
            cameraRotate = SharePreferUtil.isOutCameraRotate();
        } else {
            cameraRotate = SharePreferUtil.isInsideCameraRotate();
        }
        //用来人脸验证的基准图片
        byte[] data = FileUtil.readFile(FileUtil.DEFAULT_IMG_DIR2 + File.separator + pic+".jpg");

        TakePictureParam takePictureParam = new TakePictureParam.Builder()
                .setCameraId(cameraId) //设置相机id
                .setPreviewRotate(cameraRotate) //预览是否旋转
                .setPreviewMirrored(previewMirrored) //预览画面是否是镜像的
                .setCameraWindowSize(CameraWindowSize.WINDOW_SIZE_SMALL)
                .setOutTime(30) //超时时间
                .setDelayTimeBeforeDect(3) //人脸检测前延迟几秒
                .setForceTakeWhenTimeOut(false) //超时时间到后是否强制拍一张
                .setNeedFaceDect(true) //是否需要人脸检测
                .setNeedFaceVerify(true) //是否需要人脸验证
                .setVerifyManager(new ArcFaceVerifyManager(CjApplication.getInstance(), data)) //设置人脸验证的接口
                .setNeedAliveDect(true) //是否活体检测
                .setForceTakeWhenVerifyError(false) //人脸验证是出错状态时是否返回数据
                .setForceTakeWhenVerifyFail(false)  //人脸验证是不通过状态时是否返回数据
                .build();

        FaceVerifyHelper.takePicture(takePictureParam).map(new Function<FaceVerifyResult,
                FaceVerifyAndPhotoSaveResult>() {

            @Override
            public FaceVerifyAndPhotoSaveResult apply(FaceVerifyResult faceVerifyResult) throws
                    Exception {
                return new FaceVerifyAndPhotoSaveResult(faceVerifyResult, PhotoUtil.savePhoto
                        (faceVerifyResult, ""));
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<FaceVerifyAndPhotoSaveResult>() {


            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(FaceVerifyAndPhotoSaveResult result) {
                switch (result.getFaceVerifyResult().getStatus()) {
                    case ImageStatus.STATUS_SUCC:
                        NettyConf.camerastate=true;
                        //人脸验证通过
                        if(NettyConf.handlersmap.get(handlername)!=null){
                            if(type.equals("jllogin")){

                                //教练登录拍照
                                Message msg = new Message();
                                msg.arg1=15;
                                Bundle bundle = new Bundle();
                                bundle.putString("pic",result.getPhotoSaveInfo().getPath());
                                msg.setData(bundle);
                                android.os.Handler handler = (android.os.Handler) NettyConf.handlersmap.get(handlername);
                                handler.sendMessage(msg);
                            }else if(type.equals("jlout")){
                                //教练登出拍照
                                Message msg = new Message();
                                msg.arg1=16;
                                Bundle bundle = new Bundle();
                                bundle.putString("pic",result.getPhotoSaveInfo().getPath());
                                msg.setData(bundle);
                                android.os.Handler handler = (android.os.Handler) NettyConf.handlersmap.get(handlername);
                                handler.sendMessage(msg);
                            }else if(type.equals("stulogin")){
                                //学员登录拍照
                                Message msg = new Message();
                                msg.arg1=15;
                                Bundle bundle = new Bundle();
                                bundle.putString("pic",result.getPhotoSaveInfo().getPath());
                                msg.setData(bundle);
                                android.os.Handler handler = (android.os.Handler) NettyConf.handlersmap.get(handlername);
                                handler.sendMessage(msg);
                            }else if(type.equals("stuout")){
                                //学员登出拍照
                                Message msg = new Message();
                                msg.arg1=16;
                                Bundle bundle = new Bundle();
                                bundle.putString("pic",result.getPhotoSaveInfo().getPath());
                                msg.setData(bundle);
                                android.os.Handler handler = (android.os.Handler) NettyConf.handlersmap.get(handlername);
                                handler.sendMessage(msg);
                            }
                        }

                        break;
                    case ImageStatus.STATUS_OUT_CAMERA_NOT_CONNECTED:
                        NettyConf.camerastate=false;
                        NettyConf.isback=true;
                        Toast.makeText(CjApplication.getInstance(), "外置摄像头故障，请检查相机线连接或通知维护人员修理设备", Toast
                                .LENGTH_SHORT).show();

                        break;
                    case ImageStatus.STATUS_VERIFY_FAIL:
                        Toast.makeText(CjApplication.getInstance(), result.getFaceVerifyResult().getMessage(), Toast
                                .LENGTH_SHORT).show();
                        break;
                    case ImageStatus.STATUS_VERIFY_ERROR:
                        Toast.makeText(CjApplication.getInstance(), result.getFaceVerifyResult().getMessage(), Toast
                                .LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
              //  Toast.makeText(CjApplication.getInstance(), "照片保存成功：" + result.getPhotoSaveInfo().toString(), Toast.LENGTH_SHORT).show();
                //拍照状态改为未拍照
                NettyConf.ispz=false;

            }

            @Override
            public void onError(Throwable e) {
                //拍照状态改为未拍照
                NettyConf.ispz=false;
                NettyConf.isback=true;
                L.e(e);
                if (e instanceof TimeoutException) {
                    Toast.makeText(CjApplication.getInstance(), "人脸验证未通过：超时", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CjApplication.getInstance(), "拍照出错: " + (e.getMessage() == null ? "" : e
                            .getMessage()), Toast.LENGTH_SHORT).show();
                }

                if(e instanceof FaceVerifyEngineInitException) {
                    //TODO 本地人脸验证服务不可用，之后的检测需要替换为网络人脸验证等其他方式,这个错误应该很少出现，可以暂时不处理
                }
            }
        });

    }


    /**
     * 隐藏 检测人脸 不验证人脸  超时后拍一张
     * @param view
     * @param scms
     * @param tdh
     * @param lx
     * @param gnss
     */
    public static void floatTakePicture4(View view, final String scms, final String tdh, final String lx, final String gnss) {
        //拍照状态改为未拍照
        NettyConf.ispz=true;
        int cameraId = SharePreferUtil.isTimingPhotoUseCamera0() ? CameraInfo
                .CAMERA_FACING_INSIDE : CameraInfo.CAMERA_FACING_OUT;
        boolean previewMirrored = cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT;
        boolean cameraRotate;
        if (cameraId == CameraInfo.CAMERA_FACING_OUT) {
            cameraRotate = SharePreferUtil.isOutCameraRotate();
        } else {
            cameraRotate = SharePreferUtil.isInsideCameraRotate();
        }
        //用来人脸验证的基准图片
        byte[] data = FileUtil.readFile(FileUtil.DEFAULT_IMG_DIR + File.separator +
                "1550561982.jpg");

        TakePictureParam takePictureParam = new TakePictureParam.Builder()
                .setCameraId(cameraId) //设置相机id
                .setPreviewRotate(cameraRotate) //预览是否旋转
                .setPreviewMirrored(previewMirrored) //预览画面是否是镜像的
                .setCameraWindowSize(CameraWindowSize.WINDOW_SIZE_HIDDEN)
                .setOutTime(30) //超时时间
                .setDelayTimeBeforeDect(3) //人脸检测前延迟几秒
                .setForceTakeWhenTimeOut(true) //超时时间到后是否强制拍一张
                .setNeedFaceDect(true) //是否需要人脸检测
                .setNeedFaceVerify(false) //是否需要人脸验证
                .setNeedAliveDect(true)
                .build();

        FaceVerifyHelper.takePicture(takePictureParam).map(new Function<FaceVerifyResult,
                FaceVerifyAndPhotoSaveResult>() {

            @Override
            public FaceVerifyAndPhotoSaveResult apply(FaceVerifyResult faceVerifyResult) throws
                    Exception {
                return new FaceVerifyAndPhotoSaveResult(faceVerifyResult, PhotoUtil.savePhoto
                        (faceVerifyResult, ""));
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<FaceVerifyAndPhotoSaveResult>() {


            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(FaceVerifyAndPhotoSaveResult result) {
                switch (result.getFaceVerifyResult().getStatus()) {
                    case ImageStatus.STATUS_SUCC:
                        NettyConf.camerastate=true;
                        //人脸验证通过
                        break;
                    case ImageStatus.STATUS_OUT_CAMERA_NOT_CONNECTED:
                        Toast.makeText(CjApplication.getInstance(), "外置摄像头故障，请检查相机线连接或通知维护人员修理设备", Toast
                                .LENGTH_SHORT).show();
                        NettyConf.camerastate=false;
                        break;
                    case ImageStatus.STATUS_VERIFY_FAIL:
                        Toast.makeText(CjApplication.getInstance(), result.getFaceVerifyResult().getMessage(), Toast
                                .LENGTH_SHORT).show();
                        break;
                    case ImageStatus.STATUS_VERIFY_ERROR:
                        Toast.makeText(CjApplication.getInstance(), result.getFaceVerifyResult().getMessage(), Toast
                                .LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
//                Toast.makeText(CjApplication.getInstance(), "照片保存成功：" + result.getPhotoSaveInfo().toString
//                        (), Toast.LENGTH_SHORT).show();
                //拍照状态改为未拍照
                NettyConf.ispz=false;
                ZdUtil.sendZpsc2(scms,tdh,lx,gnss,result.getPhotoSaveInfo().getPath());
            }

            @Override
            public void onError(Throwable e) {
                //拍照状态改为未拍照
                NettyConf.ispz=false;
                L.e(e);
                if (e instanceof TimeoutException) {
                    Toast.makeText(CjApplication.getInstance(), "人脸验证未通过：超时", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CjApplication.getInstance(), "拍照出错: " + (e.getMessage() == null ? "" : e
                            .getMessage()), Toast.LENGTH_SHORT).show();
                }

                if(e instanceof FaceVerifyEngineInitException) {
                    //TODO 本地人脸验证服务不可用，之后的检测需要替换为网络人脸验证等其他方式,这个错误应该很少出现，可以暂时不处理
                }

            }
        });

    }


    /**
     * 小窗 检测人脸 不验证人脸  超时后拍一张
     * @param view
     */
    public void floatTakePicture4(View view) {
        int cameraId = SharePreferUtil.isTimingPhotoUseCamera0() ? CameraInfo
                .CAMERA_FACING_INSIDE : CameraInfo.CAMERA_FACING_OUT;
        boolean previewMirrored = cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT;
        boolean cameraRotate;
        if (cameraId == CameraInfo.CAMERA_FACING_OUT) {
            cameraRotate = SharePreferUtil.isOutCameraRotate();
        } else {
            cameraRotate = SharePreferUtil.isInsideCameraRotate();
        }
        //用来人脸验证的基准图片
        byte[] data = FileUtil.readFile(FileUtil.DEFAULT_IMG_DIR + File.separator +
                "1550561982.jpg");

        TakePictureParam takePictureParam = new TakePictureParam.Builder()
                .setCameraId(cameraId) //设置相机id
                .setPreviewRotate(cameraRotate) //预览是否旋转
                .setPreviewMirrored(previewMirrored) //预览画面是否是镜像的
                .setCameraWindowSize(CameraWindowSize.WINDOW_SIZE_SMALL)
                .setOutTime(30) //超时时间
                .setDelayTimeBeforeDect(3) //人脸检测前延迟几秒
                .setForceTakeWhenTimeOut(true) //超时时间到后是否强制拍一张
                .setNeedFaceDect(true) //是否需要人脸检测
                .setNeedFaceVerify(false) //是否需要人脸验证
                .setNeedAliveDect(true)
                .build();

        FaceVerifyHelper.takePicture(takePictureParam).map(new Function<FaceVerifyResult,
                FaceVerifyAndPhotoSaveResult>() {

            @Override
            public FaceVerifyAndPhotoSaveResult apply(FaceVerifyResult faceVerifyResult) throws
                    Exception {
                return new FaceVerifyAndPhotoSaveResult(faceVerifyResult, PhotoUtil.savePhoto
                        (faceVerifyResult, ""));
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<FaceVerifyAndPhotoSaveResult>() {


            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(FaceVerifyAndPhotoSaveResult result) {
                switch (result.getFaceVerifyResult().getStatus()) {
                    case ImageStatus.STATUS_SUCC:
                        //人脸验证通过
                        break;
                    case ImageStatus.STATUS_OUT_CAMERA_NOT_CONNECTED:
                        Toast.makeText(CjApplication.getInstance(), "外置摄像头故障，请检查相机线连接或通知维护人员修理设备", Toast
                                .LENGTH_SHORT).show();
                        break;
                    case ImageStatus.STATUS_VERIFY_FAIL:
                        Toast.makeText(CjApplication.getInstance(), result.getFaceVerifyResult().getMessage(), Toast
                                .LENGTH_SHORT).show();
                        break;
                    case ImageStatus.STATUS_VERIFY_ERROR:
                        Toast.makeText(CjApplication.getInstance(), result.getFaceVerifyResult().getMessage(), Toast
                                .LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                Toast.makeText(CjApplication.getInstance(), "照片保存成功：" + result.getPhotoSaveInfo().toString
                        (), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                L.e(e);
                if (e instanceof TimeoutException) {
                    Toast.makeText(CjApplication.getInstance(), "人脸验证未通过：超时", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CjApplication.getInstance(), "拍照出错: " + (e.getMessage() == null ? "" : e
                            .getMessage()), Toast.LENGTH_SHORT).show();
                }

                if(e instanceof FaceVerifyEngineInitException) {
                    //TODO 本地人脸验证服务不可用，之后的检测需要替换为网络人脸验证等其他方式,这个错误应该很少出现，可以暂时不处理
                }

            }
        });

    }

    /**
     * 隐藏、立即拍照，不检测人脸、不验证
     * @param view
     */
    public static void floatTakePicture3(View view,final String scms, final String tdh, final String lx, final String gnss) {
        NettyConf.ispz=true;
        int cameraId = SharePreferUtil.isTimingPhotoUseCamera0() ? CameraInfo
                .CAMERA_FACING_INSIDE : CameraInfo.CAMERA_FACING_OUT;
        boolean previewMirrored = cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT;
        boolean cameraRotate;
        if (cameraId == CameraInfo.CAMERA_FACING_OUT) {
            cameraRotate = SharePreferUtil.isOutCameraRotate();
        } else {
            cameraRotate = SharePreferUtil.isInsideCameraRotate();
        }

        TakePictureParam takePictureParam = new TakePictureParam.Builder()
                .setCameraId(cameraId) //设置相机id
                .setPreviewRotate(cameraRotate) //预览是否旋转
                .setPreviewMirrored(previewMirrored) //预览画面是否是镜像的
                .setCameraWindowSize(CameraWindowSize.WINDOW_SIZE_HIDDEN)
                .setOutTime(30) //超时时间
                .setDelayTimeBeforeDect(3) //人脸检测前延迟几秒
                .setForceTakeWhenTimeOut(true) //超时时间到后是否强制拍一张
                .setNeedFaceDect(false) //是否需要人脸检测
                .build();

        FaceVerifyHelper.takePicture(takePictureParam).map(new Function<FaceVerifyResult,
                FaceVerifyAndPhotoSaveResult>() {

            @Override
            public FaceVerifyAndPhotoSaveResult apply(FaceVerifyResult faceVerifyResult) throws
                    Exception {
                return new FaceVerifyAndPhotoSaveResult(faceVerifyResult, PhotoUtil.savePhoto
                        (faceVerifyResult, ""));
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<FaceVerifyAndPhotoSaveResult>() {


            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(FaceVerifyAndPhotoSaveResult result) {
                switch (result.getFaceVerifyResult().getStatus()) {
                    case ImageStatus.STATUS_SUCC:
                        NettyConf.camerastate=true;
                        //人脸验证通过
                        break;
                    case ImageStatus.STATUS_OUT_CAMERA_NOT_CONNECTED:
                        NettyConf.camerastate=false;
                        Toast.makeText(CjApplication.getInstance(), "外置摄像头故障，请检查相机线连接或通知维护人员修理设备", Toast
                                .LENGTH_SHORT).show();

                        break;
                    case ImageStatus.STATUS_VERIFY_FAIL:
                        Toast.makeText(CjApplication.getInstance(), result.getFaceVerifyResult().getMessage(), Toast
                                .LENGTH_SHORT).show();
                        break;
                    case ImageStatus.STATUS_VERIFY_ERROR:
                        Toast.makeText(CjApplication.getInstance(), result.getFaceVerifyResult().getMessage(), Toast
                                .LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
//                Toast.makeText(CjApplication.getInstance(), "照片保存成功：" + result.getPhotoSaveInfo().toString
//                        (), Toast.LENGTH_SHORT).show();
                //拍照状态改为未拍照
                NettyConf.ispz=false;
                ZdUtil.sendZpsc2(scms,tdh,lx,gnss,result.getPhotoSaveInfo().getPath());
            }

            @Override
            public void onError(Throwable e) {
                NettyConf.ispz=false;
                L.e(e);
                if (e instanceof TimeoutException) {
                    Toast.makeText(CjApplication.getInstance(), "人脸验证未通过：超时", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CjApplication.getInstance(), "拍照出错: " + (e.getMessage() == null ? "" : e
                            .getMessage()), Toast.LENGTH_SHORT).show();
                }

                if(e instanceof FaceVerifyEngineInitException) {
                    //TODO 本地人脸验证服务不可用，之后的检测需要替换为网络人脸验证等其他方式,这个错误应该很少出现，可以暂时不处理
                }
            }
        });
    }

    /**
     * 隐藏、立即拍照，不检测人脸、不验证、拍完删除照片
     * @param view
     */
    public static void floatTakePicture5(View view) {
        NettyConf.ispz=true;
        int cameraId = SharePreferUtil.isTimingPhotoUseCamera0() ? CameraInfo
                .CAMERA_FACING_INSIDE : CameraInfo.CAMERA_FACING_OUT;
        boolean previewMirrored = cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT;
        boolean cameraRotate;
        if (cameraId == CameraInfo.CAMERA_FACING_OUT) {
            cameraRotate = SharePreferUtil.isOutCameraRotate();
        } else {
            cameraRotate = SharePreferUtil.isInsideCameraRotate();
        }

        TakePictureParam takePictureParam = new TakePictureParam.Builder()
                .setCameraId(cameraId) //设置相机id
                .setPreviewRotate(cameraRotate) //预览是否旋转
                .setPreviewMirrored(previewMirrored) //预览画面是否是镜像的
                .setCameraWindowSize(CameraWindowSize.WINDOW_SIZE_HIDDEN)
                .setOutTime(30) //超时时间
                .setDelayTimeBeforeDect(3) //人脸检测前延迟几秒
                .setForceTakeWhenTimeOut(true) //超时时间到后是否强制拍一张
                .setNeedFaceDect(false) //是否需要人脸检测
                .build();

        FaceVerifyHelper.takePicture(takePictureParam).map(new Function<FaceVerifyResult,
                FaceVerifyAndPhotoSaveResult>() {

            @Override
            public FaceVerifyAndPhotoSaveResult apply(FaceVerifyResult faceVerifyResult) throws
                    Exception {
                return new FaceVerifyAndPhotoSaveResult(faceVerifyResult, PhotoUtil.savePhoto
                        (faceVerifyResult, ""));
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<FaceVerifyAndPhotoSaveResult>() {


            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(FaceVerifyAndPhotoSaveResult result) {
                switch (result.getFaceVerifyResult().getStatus()) {
                    case ImageStatus.STATUS_SUCC:
                        //人脸验证通过
                        NettyConf.camerastate=true;
                        break;
                    case ImageStatus.STATUS_OUT_CAMERA_NOT_CONNECTED:
                        NettyConf.camerastate=false;
                        Toast.makeText(CjApplication.getInstance(), "外置摄像头故障，请检查相机线连接或通知维护人员修理设备", Toast
                                .LENGTH_SHORT).show();

                        break;
                    case ImageStatus.STATUS_VERIFY_FAIL:
                        Toast.makeText(CjApplication.getInstance(), result.getFaceVerifyResult().getMessage(), Toast
                                .LENGTH_SHORT).show();
                        break;
                    case ImageStatus.STATUS_VERIFY_ERROR:
                        Toast.makeText(CjApplication.getInstance(), result.getFaceVerifyResult().getMessage(), Toast
                                .LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
//                Toast.makeText(CjApplication.getInstance(), "照片保存成功：" + result.getPhotoSaveInfo().toString
//                        (), Toast.LENGTH_SHORT).show();
                //拍照状态改为未拍照
                NettyConf.ispz=false;
                //删除照片
                FilesUtil.deleteOneFile(result.getPhotoSaveInfo().getPath());
            }

            @Override
            public void onError(Throwable e) {
                NettyConf.ispz=false;
                L.e(e);
                if (e instanceof TimeoutException) {
                    Toast.makeText(CjApplication.getInstance(), "人脸验证未通过：超时", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CjApplication.getInstance(), "拍照出错: " + (e.getMessage() == null ? "" : e
                            .getMessage()), Toast.LENGTH_SHORT).show();
                }

                if(e instanceof FaceVerifyEngineInitException) {
                    //TODO 本地人脸验证服务不可用，之后的检测需要替换为网络人脸验证等其他方式,这个错误应该很少出现，可以暂时不处理
                }
            }
        });
    }


    public static boolean mockHttpFaceVerify(byte[] data, byte[] data1) {
        return false;
    }


}
