package com.haoxueche.cameralib.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera.Face;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.haoxueche.winterlog.L;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author 李昱辰 987424501@qq.com
 * @date 2019/1/19
 */
public class ImageUtil {

    public static final byte[] ERROR_BLUE_IMAGE_DATA;

    static {
        byte[] data = null;
        try {
             data = FileUtil.input2byte(App.getInstance()
                        .getAssets().open("error_blue.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ERROR_BLUE_IMAGE_DATA = data;
    }

    /**
     * 压缩文件
     *
     * @param size  压缩到多大以内(kb)
     * @return
     */
    public static byte[] compressImageData(byte[] data, int size) {
        L.i("originalSize==" + data.length / 1024);
        Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 80;

        while (baos.toByteArray().length / 1024 > size && options > 0) { // 循环判断如果压缩后图片是否大于size,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            L.i("options==" + options);
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
            L.i("size==" + baos.toByteArray().length / 1024);
        }
        return baos.toByteArray();
    }

    public static Bitmap nv21toBitmap(Context context, byte[] nv21Data, int width, int height) {
        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element
                .U8_4(rs));

        Type.Builder yuvType = new Type.Builder(rs, Element.U8(rs)).setX(nv21Data.length);
        Allocation in = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);
        Type.Builder rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs)).setX(width).setY
                (height);
        Allocation out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT);

        in.copyFrom(nv21Data);

        yuvToRgbIntrinsic.setInput(in);
        yuvToRgbIntrinsic.forEach(out);

        Bitmap bmpout = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        out.copyTo(bmpout);
        in.destroy();
        out.destroy();
        yuvToRgbIntrinsic.destroy();
        rs.finish();
        rs.destroy();

        return bmpout;

    }

    /**
     * 压缩文件
     *
     * @param image
     * @param size  压缩大小(kb)
     * @return
     */
    public static byte[] compressImage(Bitmap image, int size) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;

        while (baos.toByteArray().length / 1024 > size
                && options > 0) { // 循环判断如果压缩后图片是否大于35kb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//
            // 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        image.recycle();
        return baos.toByteArray();
    }

    public static Rect[] faces2Rects(Face[] faces) {
        if(faces != null) {
            Rect[] rects = new Rect[faces.length];
            for(int i = 0; i < faces.length; i++) {
                rects[i] = faces[i].rect;
            }
            return rects;
        }
        return new Rect[0];
    }

    public static Rect[] adaptCoordinate(Rect[] rects, int left, int top) {
        for(Rect rect : rects) {
            rect.top -= top;
            rect.bottom -= top;
            rect.left -= left;
            rect.right -= left;
        }

        return rects;
    }


    /**
     * 图像旋转
     *
     * @param a
     * @return
     */
    public static Bitmap convert(Bitmap a, boolean rotate, boolean mirror) {
        if(!mirror && !rotate) {
            return a;
        }
        int w = a.getWidth();
        int h = a.getHeight();
        Bitmap newb = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
        Canvas cv = new Canvas(newb);
        Matrix m = new Matrix();
//        m.postScale(1, -1);   //镜像垂直翻转
        if(mirror) {
            m.postScale(-1, 1);   //镜像水平翻转
        }
        if(rotate) {
            m.postRotate(180);  //旋转180度
        }
        Bitmap new2 = Bitmap.createBitmap(a, 0, 0, w, h, m, true);
        cv.drawBitmap(new2, new Rect(0, 0, new2.getWidth(), new2.getHeight()), new Rect(0, 0, w,
                h), null);
        a.recycle();
        new2.recycle();
        return newb;
    }

    public static boolean isErrorImage(byte[] data) {
        return FileUtil.getSimilarity(data, ERROR_BLUE_IMAGE_DATA) > 0.98;
    }

    /**
     * 图片上绘制文字
     */
    public static Bitmap drawTextToByte(byte[] data, String text) {
        return drawTextToBitmap(BitmapFactory.decodeByteArray(data,
                0, data.length), text);
    }

    /**
     * 图片上绘制文字
     */
    public static Bitmap drawTextToBitmap(Bitmap bitmap, String text) {
        int paddingLeft = 0;
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.parseColor("#ffd600"));
        textPaint.setTextSize(12);
        Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        Bitmap.Config bitmapConfig = bitmap.getConfig();

        textPaint.setDither(true); // 获取跟清晰的图像采样
        textPaint.setFilterBitmap(true);// 过滤一些
        // 这里的参数1000，表示字符串的长度，当满1000时，就会换行，也可以使用“\r\n”来实现换行
        StaticLayout layout = new StaticLayout(text, textPaint, 1000, Layout.Alignment
                .ALIGN_NORMAL, 1.0F, 0.0F, true);
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        Bitmap bitmap1 = bitmap.copy(bitmapConfig, true);
        bitmap.recycle();
        Canvas canvas = new Canvas(bitmap1);

        canvas.translate(DipUtil.dp2px(paddingLeft),
                bitmap1.getHeight() - bounds.height() * 3);
        layout.draw(canvas);
        return bitmap1;

    }

}
