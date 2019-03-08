package com.dgcheshang.cheji.netty.timer;

import com.dgcheshang.cheji.netty.conf.NettyConf;
import com.dgcheshang.cheji.netty.util.CountDistance;

import java.util.TimerTask;

/**
 * Created by Administrator on 2017/6/30.
 */

public class DistanceTimer extends TimerTask {
    @Override
    public void run() {
        if(NettyConf.xystate==1) {
            CountDistance.addDistanceByLocation(NettyConf.location);
        }else{
            this.cancel();
        }
    }
}
