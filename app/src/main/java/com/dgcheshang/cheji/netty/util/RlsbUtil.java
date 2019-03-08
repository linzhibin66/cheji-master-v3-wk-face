package com.dgcheshang.cheji.netty.util;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

/**
 *人脸识别工具
 */

public class RlsbUtil {
    static List<Timer> timers=new ArrayList<>();
    /**
     * 判断文件是否存在
     * */
    public static boolean isFileExist(String path) {
        File file = new File(path);
        if (file.isFile()) {
            return (file.length() > 0);
        }
        return false;
    }
    /**
     * 判断文件夹是否存在,不存在则创建
     * */
    public static void isexistAndBuild(String path){
        //判断文件夹是否存在
        File f=new File(path);
        if(!f.exists()){
            //不存在
            f.mkdirs();
        }
    }
    /**
     * 删除比对失败的照片
     * */

    public static boolean delete(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            return file.delete();

        } else {
            return false ;
        }
    }

    public static void addtimer (Timer timer){
        timers.add(timer);
    }

    public static void deltimers (){
        if(timers.size()>0){
            for(int i=0;i<timers.size();i++){
                timers.get(i).cancel();
            }
        }

    }

    public static String getAssetsCacheFile(Context context, String fileName) {
        File cacheFile = new File(context.getFilesDir(), fileName);
        if (!cacheFile.exists()) {
            //printLog("*******cacheFile not exists");
            try {
                InputStream inputStream = context.getAssets().open(fileName);
                try {
                    FileOutputStream outputStream = new FileOutputStream(cacheFile);
                    try {
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = inputStream.read(buf)) > 0) {
                            outputStream.write(buf, 0, len);
                        }
                    } finally {
                        outputStream.close();
                    }
                } finally {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //printLog("*******cacheFile=" + cacheFile.getAbsolutePath());
        }
        return cacheFile.getAbsolutePath();
    }



    /**
     *  获得编码格式
     * */
    public static String codetype(byte[] head) {
        String type = "";
        byte[] codehead = new byte[3];
        System.arraycopy(head, 0, codehead, 0, 3);
        if(codehead[0] == -1 && codehead[1] == -2) {
            type = "UTF-16";
        }
        else if(codehead[0] == -2 && codehead[1] == -1) {
            type = "UNICODE";
        }
        else if(codehead[0] == -17 && codehead[1] == -69 && codehead[2] == -65) {
            type = "UTF-8";
        }
        else {
            type = "GB2312";
        }
        return type;
    }

    /**
     * 删除文件夹和文件夹里面的文件
     * */
    public static void deleteDir(final String pPath) {
        File dir = new File(pPath);
        deleteDirWihtFile(dir);
    }

    public static void deleteDirWihtFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirWihtFile(file); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }

    /**
     * 清除文件下的某文件
     * */
    public static boolean deleteOneFile(String filePath){
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                return true;
            } else {
                return false;
            }
        }else {
            return false;
        }
    }

}
