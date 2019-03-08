package com.haoxueche.cameralib.util;

/**
 * @author 李昱辰 987424501@qq.com
 * @date 2019/1/24
 */
public class CameraWindowSize {
    public static final CameraWindowSize WINDOW_SIZE_HIDDEN = new CameraWindowSize(1, 1, 442, 130);
    public static final CameraWindowSize WINDOW_SIZE_SMALL = new CameraWindowSize(400, 300, 442, 130 );
    //552 = 600 - 48，600是屏幕高度，48是状态栏高度，需要减去
    public static final CameraWindowSize WINDOW_SIZE_FULL = new CameraWindowSize(1024, 552, 0, 48);

    private final int width;
    private final int height;
    private final int left;
    private final int top;

    public CameraWindowSize(int width, int height, int left, int top) {
        this.width = width;
        this.height = height;
        this.left = left;
        this.top = top;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getTop() {
        return top;
    }

    public int getLeft() {
        return left;
    }
}
