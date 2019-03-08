package com.dgcheshang.cheji.netty.timer;

import android.os.Handler;
import android.os.Message;

import com.dgcheshang.cheji.netty.conf.NettyConf;

import java.util.TimerTask;

/**
 * Created by Administrator on 2017/9/11.
 */

public class RebootTimerTask extends TimerTask {

    @Override
    public void run() {
        try {
            if (NettyConf.handlersmap.get("login") != null) {
                if(NettyConf.autoroot==1||NettyConf.autoroot==2) {
                    Message msg = new Message();
                    msg.arg1 = 12;
                    Handler handler = (Handler) NettyConf.handlersmap.get("login");
                    handler.sendMessage(msg);
                }
            }
        }catch(Exception e){e.printStackTrace();}
    }

}
