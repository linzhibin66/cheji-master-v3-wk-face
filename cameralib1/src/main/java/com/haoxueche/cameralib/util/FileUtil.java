package com.haoxueche.cameralib.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author 李昱辰 987424501@qq.com
 * @date 2019/1/31
 */
public class FileUtil {
    /**
     * 通过输入流获取字节数组
     *
     * @param inStream
     * @return
     * @throws IOException
     */
    public static final byte[] input2byte(InputStream inStream)
            throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = inStream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] in2b = swapStream.toByteArray();
        return in2b;
    }

    public static float getSimilarity(byte[] data1, byte[] data2) {
        int sameCount = 0;
        int totalCount;
        int caculateCount;
        if (data1.length > data2.length) {
            totalCount = data1.length;
            caculateCount = data2.length;
        } else {
            totalCount = data2.length;
            caculateCount = data1.length;
        }
        for (int i = 0; i < caculateCount; i++) {
            if (data1[i] == data2[i]) {
                sameCount++;
            }
        }
        return sameCount * 1.0f / totalCount;
    }


}
