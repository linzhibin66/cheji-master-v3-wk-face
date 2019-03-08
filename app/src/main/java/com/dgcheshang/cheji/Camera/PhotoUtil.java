package com.dgcheshang.cheji.Camera;

import android.graphics.Bitmap;

import com.haoxueche.cameralib.util.FaceVerifyResult;
import com.haoxueche.cameralib.util.ImageUtil;

import java.io.File;


/**
 * @author 李昱辰 987424501@qq.com
 * @date 2019/2/11
 * 照片处理工具类
 */
public class PhotoUtil {

    public static PhotoSaveInfo savePhoto(FaceVerifyResult faceVerifyResult, String text) {
        String photoNo = String.valueOf(System.currentTimeMillis() / 1000);
        //照片保存路径
        String path = FileUtil.DEFAULT_IMG_DIR + File.separator + photoNo + ".jpg";
        //绘制文字
        Bitmap bitmap = ImageUtil.drawTextToByte(faceVerifyResult.getData(), text);
        //压缩图片
        byte[] data = ImageUtil.compressImage(bitmap, 50);
        //保存并返回是否成功
        boolean saved = FileUtil.saveBytesToFile(data, path);
        if(!saved) {
            throw new IllegalStateException("照片保存失败");
        }

        PhotoSaveInfo photoSaveInfo = new PhotoSaveInfo(photoNo, path);
        return photoSaveInfo;
    }

    public static byte[] savePhoto(String path, byte[] imageData, String text) {
        //绘制文字
        Bitmap bitmap = ImageUtil.drawTextToByte(imageData, text);
        //压缩图片
        byte[] data = ImageUtil.compressImage(bitmap, 50);
        //保存并返回是否成功
        boolean saved = FileUtil.saveBytesToFile(data, path);
        if(!saved) {
            throw new IllegalStateException("照片保存失败");
        }

        return data;
    }

}
