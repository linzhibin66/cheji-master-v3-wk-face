package com.haoxueche.cameralib.alivedect;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.media.Image;

import com.arcsoft.face.FaceEngine;
import com.haoxueche.winterlog.L;

import java.nio.ByteBuffer;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Lyc(987424501@qq.com) on 2018/11/23.
 */
public class AliveDectUtil {
    public static final String TAG = "AliveDectUtil";
    public static final int YUV420P = 1;
    public static final int YUV420SP = 2;
    public static final int NV21 = 3;

    public static int activeEngine(final Context context) {
        FaceEngine faceEngine = new FaceEngine();
        int activeCode = faceEngine.active(context, Constant.APP_ID, Constant.SDK_KEY);
        return activeCode;
    }

    public static Observable<Integer> activeEngine1(final Context context) {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(activeEngine(context));
            }
        }).subscribeOn(Schedulers.newThread());
    }

    public static byte[] convertYUV420ToNV21(byte[] data, int width, int height) {
        byte[] ret = new byte[data.length];
        int total = width * height;

        ByteBuffer bufferY = ByteBuffer.wrap(ret, 0, total);
        ByteBuffer bufferV = ByteBuffer.wrap(ret, total, total / 4);
        ByteBuffer bufferU = ByteBuffer.wrap(ret, total + total / 4, total / 4);

        bufferY.put(data, 0, total);
        for (int i = 0; i < total / 4; i += 1) {
            bufferV.put(data[total + i]);
            bufferU.put(data[i + total + total / 4]);
        }

        return ret;
    }


    public static byte[] getBytesFromImageAsType(Image image, int type) {
        try {
            //获取源数据，如果是YUV格式的数据planes.length = 3
            //plane[i]里面的实际数据可能存在byte[].length <= capacity (缓冲区总大小)
            final Image.Plane[] planes = image.getPlanes();

            //数据有效宽度，一般的，图片width <= rowStride，这也是导致byte[].length <= capacity的原因
            // 所以我们只取width部分
            int width = image.getWidth();
            int height = image.getHeight();

            //此处用来装填最终的YUV数据，需要1.5倍的图片大小，因为Y U V 比例为 4:1:1
            byte[] yuvBytes = new byte[width * height * ImageFormat.getBitsPerPixel(ImageFormat
                    .YUV_420_888) / 8];
            //目标数组的装填到的位置
            int dstIndex = 0;

            //临时存储uv数据的
            byte uBytes[] = new byte[width * height / 4];
            byte vBytes[] = new byte[width * height / 4];
            int uIndex = 0;
            int vIndex = 0;

            int pixelsStride, rowStride;
            for (int i = 0; i < planes.length; i++) {
                pixelsStride = planes[i].getPixelStride();
                rowStride = planes[i].getRowStride();

                ByteBuffer buffer = planes[i].getBuffer();

                //如果pixelsStride==2，一般的Y的buffer长度=640*480，UV的长度=640*480/2-1
                //源数据的索引，y的数据是byte中连续的，u的数据是v向左移以为生成的，两者都是偶数位为有效数据
                byte[] bytes = new byte[buffer.capacity()];
                buffer.get(bytes);

                int srcIndex = 0;
                if (i == 0) {
                    //直接取出来所有Y的有效区域，也可以存储成一个临时的bytes，到下一步再copy
                    for (int j = 0; j < height; j++) {
                        System.arraycopy(bytes, srcIndex, yuvBytes, dstIndex, width);
                        srcIndex += rowStride;
                        dstIndex += width;
                    }
                } else if (i == 1) {
                    //根据pixelsStride取相应的数据
                    for (int j = 0; j < height / 2; j++) {
                        for (int k = 0; k < width / 2; k++) {
                            uBytes[uIndex++] = bytes[srcIndex];
                            srcIndex += pixelsStride;
                        }
                        if (pixelsStride == 2) {
                            srcIndex += rowStride - width;
                        } else if (pixelsStride == 1) {
                            srcIndex += rowStride - width / 2;
                        }
                    }
                } else if (i == 2) {
                    //根据pixelsStride取相应的数据
                    for (int j = 0; j < height / 2; j++) {
                        for (int k = 0; k < width / 2; k++) {
                            vBytes[vIndex++] = bytes[srcIndex];
                            srcIndex += pixelsStride;
                        }
                        if (pixelsStride == 2) {
                            srcIndex += rowStride - width;
                        } else if (pixelsStride == 1) {
                            srcIndex += rowStride - width / 2;
                        }
                    }
                }
            }

            image.close();

            //根据要求的结果类型进行填充
            switch (type) {
                case YUV420P:
                    System.arraycopy(uBytes, 0, yuvBytes, dstIndex, uBytes.length);
                    System.arraycopy(vBytes, 0, yuvBytes, dstIndex + uBytes.length, vBytes.length);
                    break;
                case YUV420SP:
                    for (int i = 0; i < vBytes.length; i++) {
                        yuvBytes[dstIndex++] = uBytes[i];
                        yuvBytes[dstIndex++] = vBytes[i];
                    }
                    break;
                case NV21:
                    for (int i = 0; i < vBytes.length; i++) {
                        yuvBytes[dstIndex++] = vBytes[i];
                        yuvBytes[dstIndex++] = uBytes[i];
                    }
                    break;
            }
            return yuvBytes;
        } catch (final Exception e) {
            if (image != null) {
                image.close();
            }
            L.i( e.toString());
        }
        return null;
    }

    private static boolean isImageFormatSupported(Image image) {
        int format = image.getFormat();
        switch (format) {
            case ImageFormat.YUV_420_888:
            case ImageFormat.NV21:
            case ImageFormat.YV12:
                return true;
        }
        return false;
    }


    public static byte[] getDataFromImage(Image image, int colorFormat) {
        if (colorFormat != YUV420P && colorFormat != NV21) {
            throw new IllegalArgumentException("only support COLOR_FormatI420 " + "and " +
                    "COLOR_FormatNV21");
        }
        if (!isImageFormatSupported(image)) {
            throw new RuntimeException("can't convert Image to byte array, format " + image
                    .getFormat());
        }
        Rect crop = image.getCropRect();
        int format = image.getFormat();
        int width = crop.width();
        int height = crop.height();
        Image.Plane[] planes = image.getPlanes();
        byte[] data = new byte[width * height * ImageFormat.getBitsPerPixel(format) / 8];
        byte[] rowData = new byte[planes[0].getRowStride()];
        L.i( "get data from " + planes.length + " planes");
        int channelOffset = 0;
        int outputStride = 1;
        for (int i = 0; i < planes.length; i++) {
            switch (i) {
                case 0:
                    channelOffset = 0;
                    outputStride = 1;
                    break;
                case 1:
                    if (colorFormat == YUV420P) {
                        channelOffset = width * height;
                        outputStride = 1;
                    } else if (colorFormat == NV21) {
                        channelOffset = width * height + 1;
                        outputStride = 2;
                    }
                    break;
                case 2:
                    if (colorFormat == YUV420P) {
                        channelOffset = (int) (width * height * 1.25);
                        outputStride = 1;
                    } else if (colorFormat == NV21) {
                        channelOffset = width * height;
                        outputStride = 2;
                    }
                    break;
            }
            ByteBuffer buffer = planes[i].getBuffer();
            int rowStride = planes[i].getRowStride();
            int pixelStride = planes[i].getPixelStride();
            L.i( "pixelStride " + pixelStride);
            L.i( "rowStride " + rowStride);
            L.i( "width " + width);
            L.i( "height " + height);
            L.i( "buffer size " + buffer.remaining());
            int shift = (i == 0) ? 0 : 1;
            int w = width >> shift;
            int h = height >> shift;
            buffer.position(rowStride * (crop.top >> shift) + pixelStride * (crop.left >> shift));
            for (int row = 0; row < h; row++) {
                int length;
                if (pixelStride == 1 && outputStride == 1) {
                    length = w;
                    buffer.get(data, channelOffset, length);
                    channelOffset += length;
                } else {
                    length = (w - 1) * pixelStride + 1;
                    buffer.get(rowData, 0, length);
                    for (int col = 0; col < w; col++) {
                        data[channelOffset] = rowData[col * pixelStride];
                        channelOffset += outputStride;
                    }
                }
                if (row < h - 1) {
                    buffer.position(buffer.position() + rowStride - length);
                }
            }
            L.i( "Finished reading data from plane " + i);
        }
        return data;
    }

    public static byte[] yuvImageToByteArray(Image image) {

        if (image.getFormat() != ImageFormat.YUV_420_888) {
            throw new UnsupportedOperationException();
        }

        int width = image.getWidth();
        int height = image.getHeight();

        Image.Plane[] planes = image.getPlanes();
        byte[] result = new byte[width * height * 3 / 2];

        int stride = planes[0].getRowStride();
        if (stride == width) {
            planes[0].getBuffer().get(result, 0, width);
        } else {
            for (int row = 0; row < height; row++) {
                planes[0].getBuffer().position(row * stride);
                planes[0].getBuffer().get(result, row * width, width);
            }
        }

        stride = planes[1].getRowStride();
        if (stride != planes[2].getRowStride()) {
            throw new AssertionError();
        }
        byte[] rowBytesCb = new byte[stride];
        byte[] rowBytesCr = new byte[stride];

        for (int row = 0; row < height / 2; row++) {
            int rowOffset = width * height + width / 2 * row;
            planes[1].getBuffer().position(row * stride);
            planes[1].getBuffer().get(rowBytesCb, 0, width / 2);
            planes[2].getBuffer().position(row * stride);
            planes[2].getBuffer().get(rowBytesCr, 0, width / 2);

            for (int col = 0; col < width / 2; col++) {
                result[rowOffset + col * 2] = rowBytesCr[col];
                result[rowOffset + col * 2 + 1] = rowBytesCb[col];
            }
        }
        return result;
    }

//    public static int findFTMaxAreaFace(List<AFT_FSDKFace> ftFaceList) {
//        if (ftFaceList.size() == 0) {
//            return -1;
//        }
//        int index = 0;
//        int maxArea = 0;
//        int area;
//        for (int i = 0; i < ftFaceList.size(); i++) {
//            area = ftFaceList.get(i).getRect().width() * ftFaceList.get(i).getRect().height();
//            if (area > maxArea) {
//                maxArea = area;
//                index = i;
//            }
//        }
//        return index;
//    }

    public static String getStatusMessage(int aliveStatus) {
        String message;
        switch (aliveStatus) {
            case AliveInfo.NO_FACE:
                message = "检测中";
                break;
            case AliveInfo.NOT_ALIVE:
                message = "认证中";
                break;
            case AliveInfo.UNKNOW:
                message = "未知状态";
                break;
            case AliveInfo.MORE_THAN_ONE_FACE:
                message = "画面内只允许有一个人";
                break;
            case AliveInfo.ALIVE:
                message = "通过";
                break;
            default:
                message = "异常状态";
                break;
        }
        return message;
    }


}
