package com.dgcheshang.cheji.Activity;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;

import com.dgcheshang.cheji.Tools.CrashHandler;
import com.dgcheshang.cheji.netty.conf.NettyConf;
import com.haoxueche.mz200alib.activity.NfcActivity;

import java.util.List;

/**
 * Created by Administrator on 2017/10/10.
 */

public class BaseInitActivity extends NfcActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //异常上传
       try {
            CrashHandler crashHandler = CrashHandler.getInstance();
            crashHandler.init(getApplicationContext());
        }catch(Exception e){}

    }

    @Override
    protected void onStart() {
        super.onStart();
        //学员培训阶段，显示屏常亮
        if(NettyConf.xystate==1){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    /**
     * 判断app是否处于前台
     * @param context
     * @return true前台，flase后台
     */
    public static boolean isAppForeground(Context context){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Service.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList = activityManager.getRunningAppProcesses();
        if (runningAppProcessInfoList==null){
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo processInfo : runningAppProcessInfoList) {
            if (processInfo.processName.equals(context.getPackageName()) &&
                    processInfo.importance==ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(NettyConf.background==0){
            //不停止是否监听后台
            boolean appForeground = isAppForeground(BaseInitActivity.this);
            Log.e("TAG","前台状态"+appForeground);
            if(appForeground==false){

                Intent intent = new Intent();
                intent.setClass(BaseInitActivity.this,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onDataRead(String data) {

    }
}
