package com.dgcheshang.cheji.netty.timer;

import android.os.Handler;
import android.os.Message;
import com.dgcheshang.cheji.broadcastReceiver.TrainReceiver;
import com.dgcheshang.cheji.netty.conf.NettyConf;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/8/1.
 */

public class InitTimer extends TimerTask {
    @Override
    public void run() {
        if(TrainReceiver.wzhbT==null) {
            NettyConf.dwfsjg4=NettyConf.dwfsjg;
            Message msg = new Message();
            msg.arg1 = 11;
            Handler handler = (Handler) NettyConf.handlersmap.get("login");
            handler.sendMessage(msg);
        }
    }
}
