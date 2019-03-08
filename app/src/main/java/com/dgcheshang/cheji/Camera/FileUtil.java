package com.dgcheshang.cheji.Camera;

import android.os.Environment;
import android.text.TextUtils;

import com.haoxueche.winterlog.L;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author 李昱辰 987424501@qq.com
 * @date 2019/2/18
 */
public class FileUtil {
    // 根目录
    public static final String ROOT1 = Environment.getExternalStorageDirectory().getAbsolutePath();
    // tcp图片文件夹
    public static final String IMAGE_DIR = "ChejiCamera";
    public static final String DEFAULT_IMG_DIR = getRoot() + IMAGE_DIR;

    // 原始图片文件夹
    public static final String IMAGE_DIR2 = "jlyxypic";
    public static final String DEFAULT_IMG_DIR2 = getRoot() + IMAGE_DIR2;

    public static String getRoot() {
        String path = FileUtil.ROOT1;
        if (TextUtils.isEmpty(path) || path.contains("null")) {
            throw new IllegalStateException("设备存储路径未初始化:" + path);
        }
        L.d("root==" + path);
        return path + File.separator;
    }

    /**
     * 保存byte[] 到指定路径
     *
     * @param data
     * @param path
     * @return
     */
    public static boolean saveBytesToFile(byte[] data, String path) {

        if (data == null || TextUtils.isEmpty(path)) {
            return false;
        }

        boolean saved = false;

        System.out.println("File Path==" + path);

        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }

        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
        } catch (IOException e) {
            L.e(e);
        }

        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(file);
            fos.write(data);
            fos.flush();
            saved = true;

        } catch (FileNotFoundException e) {
            L.e(e);
        } catch (IOException e) {
            L.e(e);
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                L.e(e);
            }
        }
        return saved;
    }

    /**
     * 读取文件
     *
     * @param path
     * @return
     */
    public static byte[] readFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }

        File file = new File(path);
        if (!file.exists()) {
            return null;
        }

        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            return buffer;
        } catch (FileNotFoundException e) {
            L.e(e);
        } catch (IOException e) {
            L.e(e);
        }

        return null;

    }



}
