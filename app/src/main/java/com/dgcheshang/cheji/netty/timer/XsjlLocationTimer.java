package com.dgcheshang.cheji.netty.timer;

import android.location.Location;

import com.dgcheshang.cheji.netty.conf.NettyConf;
import com.dgcheshang.cheji.netty.util.ZdUtil;

import java.util.Date;
import java.util.TimerTask;

public class XsjlLocationTimer extends TimerTask{


	@Override
	public void run() {
		try {
			if(NettyConf.xystate==1) {
				NettyConf.xsgnss = ZdUtil.getGnss5();
			}else{
				this.cancel();
			}
		}catch(Exception e){}
	}
}


