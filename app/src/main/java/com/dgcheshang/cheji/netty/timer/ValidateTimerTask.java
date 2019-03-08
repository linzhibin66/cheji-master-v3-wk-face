package com.dgcheshang.cheji.netty.timer;

import com.dgcheshang.cheji.netty.conf.NettyConf;
import com.dgcheshang.cheji.netty.util.ZdUtil;

import java.util.TimerTask;

/**
 * Created by Administrator on 2017/9/13.
 */

public class ValidateTimerTask extends TimerTask {
    @Override
    public void run() {
        if(NettyConf.jqstate==1) {
            ZdUtil.sendZdjqHex();
        }
    }
}
