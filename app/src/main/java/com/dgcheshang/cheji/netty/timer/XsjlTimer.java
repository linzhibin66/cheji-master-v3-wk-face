package com.dgcheshang.cheji.netty.timer;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

import com.dgcheshang.cheji.CjApplication;
import com.dgcheshang.cheji.Tools.Speaking;
import com.dgcheshang.cheji.netty.conf.NettyConf;
import com.dgcheshang.cheji.netty.util.LocationUtil;
import com.dgcheshang.cheji.netty.util.ZdUtil;

import java.util.TimerTask;

public class XsjlTimer extends TimerTask{
	public static boolean isSpeakState=true;
	public static boolean dwstate=false;
	public static int fzpxjlsc=0;
	public String TAG="XsjlTimer";

	@Override
	public void run() {
		try {
			if(NettyConf.xystate==1) {
				SharedPreferences sp = CjApplication.getInstance().getSharedPreferences("student", Context.MODE_PRIVATE); //私有数据
				SharedPreferences.Editor editor = sp.edit();//获取编辑器
				int zpxsj = sp.getInt("zpxsj", 0);
				editor.putInt("zpxsj", zpxsj + 1);
				editor.commit();

				if (NettyConf.debug) {
					Log.e("TAG", "学时记录发送");
				}
				ZdUtil.sendXsjl();

				NettyConf.jrxxsc++;
				editor.putString("jrxs", NettyConf.jrxxsc + "");

				fzpxjlsc++;
				editor.putInt("fzpxjlsc", fzpxjlsc);
				editor.commit();

				if (dwstate) {
					dwstate = false;
					//启动异常时报读
					isSpeakState = true;

				} else {
					if (NettyConf.debug) {
						Log.e("TAG", "学时记录发送失败,获取定位失败");
					}
					if (isSpeakState) {
						Speaking.in("GPS异常,学时可能无效");

						//连续异常不再报读
						isSpeakState = false;
					}

					if (LocationUtil.state) {
						LocationUtil.state = false;
					}
				}
			}else{
				this.cancel();
			}
		}catch(Exception e){}
	}
}


