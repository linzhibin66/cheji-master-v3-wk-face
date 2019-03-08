package com.haoxueche.cameralib.manager;

import com.haoxueche.cameralib.alivedect.AliveInfo;
import com.haoxueche.cameralib.exception.AliveDectEngineInitException;
import com.haoxueche.cameralib.util.CameraWindowSize;

import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * @author 李昱辰 987424501@qq.com
 * @date 2019/1/22
 */
public interface ICameraManager {

    /**
     * 初始化
     * @param windowSize 预览窗口大小和位置
     * @param cameraId  相机id(前置、后置、uvc等，可以根据业务拓展)
     * @param mirrored  是否镜像
     * @param rotate 是否旋转180度
     * @return
     */
    Single<ICameraManager> init(CameraWindowSize windowSize, int cameraId, boolean mirrored,
                                boolean rotate);

    /**
     * 人脸检测
     *  @param faceDectNum 检测到几张人脸才传数据
     * @return 人脸对象数组
     */
    Observable<?> faceDect(int faceDectNum);


    /**
     * 活体检测
     * @return 活体信息
     * @throws AliveDectEngineInitException 人脸引擎初始化出错
     */
    Observable<AliveInfo> aliveDect() throws AliveDectEngineInitException;

    /**
     * 拍照
     * @return rgbg数组
     */
    Single<byte[]> takePicture();

    /**
     * 获取预览帧数据
     * @return 预览帧数据
     */
    Observable<byte[]> preview();

    /**
     * 释放相机等资源
     */
    void release();
}
