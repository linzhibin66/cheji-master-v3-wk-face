package com.haoxueche.cameralib.util;

import android.app.Application;
import android.content.Context;

/**
 * App
 * AndroidContextHolder <com.vilyever.vdcontextholder>
 * Created by vilyever on 2015/9/15.
 * Feature:
 */
public class App {
    private final App self = this;

    static Context ApplicationContext;

    /* Public Methods */

    /**
     * 初始化context，如果由于不同机型导致反射获取context失败可以在Application调用此方法
     * @param context
     */
    public static void init(Context context) {
        ApplicationContext = context;
    }

    public static Context getInstance() {
        if (ApplicationContext == null) {
            try {
                Application application = (Application) Class.forName("android.app.ActivityThread")
                        .getMethod("currentApplication").invoke(null, (Object[]) null);
                if (application != null) {
                    ApplicationContext = application;
                    return application;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Application application = (Application) Class.forName("android.app.AppGlobals")
                        .getMethod("getInitialApplication").invoke(null, (Object[]) null);
                if (application != null) {
                    ApplicationContext = application;
                    return application;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            throw new IllegalStateException("App is not initialed, it is recommend to init with application context.");
        }
        return ApplicationContext;
    }
}