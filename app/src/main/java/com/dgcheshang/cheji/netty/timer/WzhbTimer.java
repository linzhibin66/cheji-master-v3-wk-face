package com.dgcheshang.cheji.netty.timer;


import android.util.Log;

import com.dgcheshang.cheji.CjApplication;
import com.dgcheshang.cheji.Database.DbHandle;
import com.dgcheshang.cheji.Database.MyDatabase;
import com.dgcheshang.cheji.netty.conf.NettyConf;
import com.dgcheshang.cheji.netty.po.Tdata;
import com.dgcheshang.cheji.netty.util.ByteUtil;
import com.dgcheshang.cheji.netty.util.ForwardUtil;
import com.dgcheshang.cheji.netty.util.LocationUtil;
import com.dgcheshang.cheji.netty.util.MsgUtilClient;
import com.dgcheshang.cheji.netty.util.ZdUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.TimerTask;

public class WzhbTimer extends TimerTask{
	public String TAG="WzhbTimer";

	@Override
	public void run() {
		if(LocationUtil.state&&StringUtils.isNotEmpty(NettyConf.mobile)) {
			if(NettyConf.sendState&&NettyConf.constate==1&&NettyConf.jqstate==1) {
				ForwardUtil.sendData(generateWzhb(), 1, 3);
			}else if(NettyConf.constate==0&&NettyConf.xystate==0){

			}else{
				DbHandle.insertTdatas(generateWzhb(),3);
			}
		}else{
			if(NettyConf.debug){
				Log.e("TAG", "位置发送失败,获取定位失败");
			}
		}
	}

	public List<Tdata> generateWzhb(){
		String gnss3 = ZdUtil.getGnss3();
		byte[] b2 = ByteUtil.hexStringToByte(gnss3);
		return MsgUtilClient.generateMsg(b2, "0200", MsgUtilClient.generateLsh(), NettyConf.mobile, "0");
	}
}


