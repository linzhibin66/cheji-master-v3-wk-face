package com.dgcheshang.cheji.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dgcheshang.cheji.Tools.Speaking;
import com.dgcheshang.cheji.netty.conf.NettyConf;
import com.dgcheshang.cheji.netty.timer.DistanceTimer;
import com.dgcheshang.cheji.netty.timer.JrxsTimer;
import com.dgcheshang.cheji.netty.timer.LoginoutTimer;
import com.dgcheshang.cheji.netty.timer.LoginoutWarnTimer;
import com.dgcheshang.cheji.netty.timer.PhotoTimer;
import com.dgcheshang.cheji.netty.timer.RebootTimerTask;
import com.dgcheshang.cheji.netty.timer.WzhbTimer;
import com.dgcheshang.cheji.netty.timer.XsjlLocationTimer;
import com.dgcheshang.cheji.netty.timer.XsjlTimer;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/8/4.
 */

public class TrainReceiver extends BroadcastReceiver {
    public static Timer wzhbT=null;
    public static Timer distanceT=null;
    public static Timer photoT=null;
    public static Timer xsjlLT=null;
    public static Timer xsjlT=null;
    public static Timer jrxsT=null;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();
        if(action.equals("wzhb")){
            if(NettyConf.debug){
                Log.e("TAG","收到位置广播");
            }
            handleWzhb();
        }else if(action.equals("xydl")){
            if(NettyConf.debug){
                Log.e("TAG","收到学员登陆广播");
            }
            handleXydl();
        }else if(action.equals("xydc")){
            if(NettyConf.debug){
                Log.e("TAG","收到学员登出广播");
            }
            handleXydc();
        }else if(action.equals("autoLoginout")){
            Log.e("TAG","自动登出");
            handleAutoLoginout();
        }else if(action.equals("autoLoginwarn")){
            Log.e("TAG","自动登出报警");
            handleAutoLoginwarn();
        }else if(action.equals("reboot")){
            handleReroot();
        }
    }

    public void handleReroot(){
        try{
            new Timer().schedule(new RebootTimerTask(),0);
        }catch(Exception e){}
    }

    public void handleAutoLoginwarn(){
        try{
            LoginoutWarnTimer lwt=new LoginoutWarnTimer();
            new Timer().schedule(lwt,0);
        }catch(Exception e){}
    }

    public void handleAutoLoginout(){
        try {
            //Speaking.in("自动登出中");
            new Timer().schedule(new LoginoutTimer(), 0);
        }catch(Exception e){}
    }

    public void handleWzhb(){
        if(wzhbT!=null){
            wzhbT.cancel();
            wzhbT=null;
        }
        TimerTask timerTask=new WzhbTimer();
        wzhbT=new Timer();
        if(NettyConf.debug){
            Log.e("TAGWZHB","定位发送间隔："+NettyConf.dwfsjg4);
        }
        wzhbT.schedule(timerTask,5*1000,NettyConf.dwfsjg4*1000);
    }

    public void handleXydl(){
        if(distanceT!=null){
            distanceT.cancel();
            distanceT=null;
        }
        DistanceTimer distanceTimer = new DistanceTimer();
        distanceT = new Timer();
        distanceT.schedule(distanceTimer, 20, 3000);
        //启动后台拍照
        if(photoT!=null){
            photoT.cancel();
            photoT=null;
        }
        TimerTask timerTask=new PhotoTimer();
        photoT=new Timer();
        photoT.schedule(timerTask,NettyConf.makephotojg*1000,NettyConf.makephotojg*1000);
        //启动学时记录上报
        if(xsjlLT!=null){
            xsjlLT.cancel();
            xsjlLT=null;
        }
        TimerTask task=new XsjlLocationTimer();
        xsjlLT=new Timer();
        xsjlLT.schedule(task,30*1000,30*1000);

        //学时记录定时器
        if(xsjlT!=null){
            xsjlT.cancel();
            xsjlT=null;
        }
        TimerTask task2=new XsjlTimer();
        xsjlT=new Timer();
        xsjlT.schedule(task2,NettyConf.xsjljg*1000,NettyConf.xsjljg*1000);

        //今日学时计时
        if(jrxsT!=null){
            jrxsT.cancel();
            jrxsT=null;
        }
        JrxsTimer jrxsTimer = new JrxsTimer();
        jrxsT = new Timer();
        jrxsT.schedule(jrxsTimer, 5 * 1000, 60 * 1000);
    }

    public void handleXydc(){
        if(distanceT!=null){
            if(NettyConf.debug){
                Log.e("TAG","清除距离定时器");
            }
            distanceT.cancel();
            distanceT=null;
        }
        if(photoT!=null){
            if(NettyConf.debug){
                Log.e("TAG","清除照片定时器");
            }
            photoT.cancel();
            photoT=null;
        }
        if(xsjlLT!=null){
            if(NettyConf.debug){
                Log.e("TAG","清除学时记录位置定时器");
            }
            xsjlLT.cancel();
            xsjlLT=null;
        }
        if(xsjlT!=null){
            if(NettyConf.debug){
                Log.e("TAG","清除学时记录时器");
            }
            xsjlT.cancel();
            xsjlT=null;
        }
        if(jrxsT!=null){
            if(NettyConf.debug){
                Log.e("TAG","今日学时计时器清除");
            }
            jrxsT.cancel();
            jrxsT=null;
        }

        //停止强制登出
        if(JrxsTimer.xydcQzTimer!=null){
            JrxsTimer.xydcQzTimer.cancel();
            JrxsTimer.xydcQzTimer=null;
        }

        //DzwlUtil.item=null;
    }
}
