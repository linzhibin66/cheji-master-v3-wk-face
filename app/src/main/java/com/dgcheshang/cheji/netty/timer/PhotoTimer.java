package com.dgcheshang.cheji.netty.timer;

import com.dgcheshang.cheji.netty.conf.NettyConf;
import com.dgcheshang.cheji.netty.util.ZdUtil;

import java.util.TimerTask;

public class PhotoTimer extends TimerTask{
	public String TAG="PhotoTimer";

	@Override
	public void run() {
		if(ZdUtil.pdGps()&& NettyConf.xystate==1) {
			ZdUtil.sendZpsc("129", "0", "19");
		}else{
			this.cancel();
		}
	}
}


