package com.dgcheshang.cheji.Tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dgcheshang.cheji.Bean.VersionBean;
import com.dgcheshang.cheji.CjApplication;
import com.dgcheshang.cheji.netty.conf.NettyConf;
import com.dgcheshang.cheji.networkUrl.NetworkUrl;
import com.google.gson.Gson;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * UncaughtException处理类,
 * 当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 *
 * @author user
 *
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    public static final String TAG = "CrashHandler";

    //系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    //CrashHandler实例
    private static CrashHandler INSTANCE = new CrashHandler();
    //程序的Context对象
    private Context mContext;
    //用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<String, String>();
//    //用于格式化日期,作为日志文件名的一部分
//    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss",Locale.getDefault());
    /** 保证只有一个CrashHandler实例 */
    private CrashHandler() {
    }

    /** 获取CrashHandler实例 ,单例模式 */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.e(TAG, "error : ", e);
            }  finally{

            }
            CjApplication.getInstance().onTerminate();
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        //使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                //Toast.makeText(mContext, "很抱歉,程序出现异常,即将退出.", Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
        //收集设备参数信息
        collectDeviceInfo(mContext);
        //保存日志文件
        saveCrashInfo2File(ex);
        return true;
    }

    /**
     * 收集设备参数信息
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            if(NettyConf.debug) {
                Log.e(TAG, "an error occured when collect package info", e);
            }
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return  返回文件名称,便于将文件传送到服务器
     */
    private String saveCrashInfo2File(Throwable ex) {

       /* String mobilemsg = "";
        //遍历HashMap
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            mobilemsg += key + "=" + value + "\r\n";
        }*/

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String errormsg = writer.toString();
        if(NettyConf.debug) {
            Log.e(TAG, errormsg);
        }
        infos.put("errorstr",errormsg);
        infos.put("imei",NettyConf.imei);
        infos.put("mobile",NettyConf.mobile);
        //上传异常
        uploadException();
        return null;
    }


    /**
     * 异常上传
     * */
    public void uploadException( ) {
        StringRequest request = new StringRequest(Request.Method.POST, NetworkUrl.uploadExceptionUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("TGA","volleyError="+volleyError);
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                return infos;
            }
        };
        CjApplication.getHttpQueue().add(request);
    }
}
