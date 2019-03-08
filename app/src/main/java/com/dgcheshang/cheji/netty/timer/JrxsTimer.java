package com.dgcheshang.cheji.netty.timer;

import android.util.Log;

import com.dgcheshang.cheji.Tools.Speaking;
import com.dgcheshang.cheji.broadcastReceiver.TrainReceiver;
import com.dgcheshang.cheji.netty.conf.NettyConf;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/7/13.
 */

public class JrxsTimer extends TimerTask{
    public static Timer xydcQzTimer;

    @Override
    public void run() {
        if(NettyConf.debug){
            Log.e("TAG","今日时长："+NettyConf.jrxxsc);
            Log.e("TAG","最大时长："+NettyConf.dtlxlong);
        }
        if(NettyConf.jrxxsc>=NettyConf.dtlxlong){
            Speaking.in("今日学习已满四小时,三十秒后将自动登出");
            xydcQzTimer=new Timer();
            xydcQzTimer.schedule(new StudentoutQzTimer(),30*1000);
            /*this.cancel();
            TrainReceiver.jrxsT=null;*/
        }
    }
}
