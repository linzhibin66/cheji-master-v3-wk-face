package com.dgcheshang.cheji.Camera;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.dgcheshang.cheji.CjApplication;


public class T {
    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

    public static void show(final String msg) {

//        Toast.makeText(TerminalApp.getInstance(), msg, Toast.LENGTH_SHORT).show();
		MAIN_HANDLER.post(new Runnable() {
            @Override
            public void run() {
                showToast(CjApplication.getInstance(), msg);
            }
        });
    }

    public static void showSpeak(String msg) {
        T.show(msg);
        SpeechUtils.getInstance().speak(msg);
    }

    public static void showSpeak(String msg, SpeechUtils.SpeechCallback callback) {
        T.show(msg);
        SpeechUtils.getInstance().speak(msg, callback);
    }

    public static void show(String msg, int time) {
        Toast.makeText(CjApplication.getInstance(), msg, time).show();
    }

    public static void show(int resId) {
        Toast.makeText(CjApplication.getInstance(), resId, Toast.LENGTH_SHORT).show();
    }

    public static void show(int resId, int duration) {
        Toast.makeText(CjApplication.getInstance(), resId, duration).show();
    }

    public static void show(Context context, String msg) {
        if (context == null) {
            return;
        }
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void show(Context context, int resId) {
        if (context == null) {
            return;
        }
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }

    public static void show(Context context, String msg, int duration) {
        if (context == null) {
            return;
        }
        Toast.makeText(context, msg, duration).show();
    }

    public static void show(Context context, int resId, int duration) {
        if (context == null) {
            return;
        }
        Toast.makeText(context, resId, duration).show();
    }

    private static String oldMsg;
    /**
     * Toast对象
     */
    private static Toast toast = null;
    /**
     * 第一次时间
     */
    private static long oneTime = 0;
    /**
     * 第二次时间
     */
    private static long twoTime = 0;

    /**
     * 显示Toast
     *
     * @param context
     * @param message
     */
    public static void showToast(Context context, String message) {
        if (toast == null) {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.show();
            oneTime = System.currentTimeMillis();
        } else if(message != null){
            twoTime = System.currentTimeMillis();
            if (message.equals(oldMsg)) {
                if (twoTime - oneTime > Toast.LENGTH_SHORT) {
                    toast.show();
                }
            } else {
                oldMsg = message;
                toast.setText(message);
                toast.show();
            }
        }
        oneTime = twoTime;
    }

}
