package com.dgcheshang.cheji.netty.timer;

import android.os.Handler;
import android.os.Message;

import com.dgcheshang.cheji.netty.conf.NettyConf;
import com.dgcheshang.cheji.netty.util.ZdUtil;

import java.util.TimerTask;

/**
 * Created by Administrator on 2017/7/17.
 */

public class LoginoutTimer extends TimerTask {
    @Override
    public void run() {
        try {
            if(NettyConf.xystate==1){
                //学员登出
                ZdUtil.studentOut1();
            }
            Thread.sleep(5000);
            if(NettyConf.jlstate==1){
                //教练登出
                ZdUtil.coachOut1();
            }
        }catch(Exception e){}
    }
}
