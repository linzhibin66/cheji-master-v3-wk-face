package com.haoxueche.cameralib.managerProxy;

import com.haoxueche.cameralib.alivedect.AliveInfo;
import com.haoxueche.cameralib.exception.AliveDectEngineInitException;
import com.haoxueche.cameralib.manager.ICameraManager;
import com.haoxueche.cameralib.manager.NativeCameraManager;
import com.haoxueche.cameralib.util.CameraWindowSize;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Function;

/**
 * @author 李昱辰 987424501@qq.com
 * @date 2019/1/24
 */
public class NativeCameraManagerProxy implements ICameraManager {
    protected NativeCameraManager nativeCameraManager;

    public NativeCameraManagerProxy() {
        nativeCameraManager = new NativeCameraManager();
    }


    @Override
    public Single<ICameraManager> init(CameraWindowSize windowSize, int cameraId, boolean
            mirrored, boolean rotate) {
        return nativeCameraManager.initCamera(windowSize, cameraId, mirrored, rotate).map(new Function<NativeCameraManager, ICameraManager>() {

            @Override
            public NativeCameraManagerProxy apply(NativeCameraManager nativeCameraManager) throws
                    Exception {
                return NativeCameraManagerProxy.this;
            }
        });
    }

    @Override
    public Observable<?> faceDect(int faceDectNum) {
        return nativeCameraManager.faceDect(faceDectNum);
    }

    @Override
    public Observable<AliveInfo> aliveDect() throws AliveDectEngineInitException {
        return nativeCameraManager.aliveDect();
    }

    @Override
    public Single<byte[]> takePicture() {
        return nativeCameraManager.takePicture();
    }

    @Override
    public Observable<byte[]> preview() {
        return nativeCameraManager.preview();
    }


    @Override
    public void release() {
        nativeCameraManager.release();
    }

}
