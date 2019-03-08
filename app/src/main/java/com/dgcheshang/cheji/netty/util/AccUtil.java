package com.dgcheshang.cheji.netty.util;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.dgcheshang.cheji.CjApplication;
import com.dgcheshang.cheji.netty.conf.NettyConf;
import com.rscja.deviceapi.Vehicle_Listener;

/**
 * Created by Administrator on 2017/7/18.
 */

public class AccUtil {
    public static int accState=0;

    public AccUtil() {
        myVehicleListener.requestGetAccState();
    }

    Vehicle_Listener myVehicleListener = new Vehicle_Listener(CjApplication.getInstance()) {
        @Override
        public void onUEvent(String s, Object o) {
            if (s.contains("ACC")) {
                int state = (int) o;
                switch (state) {
                    case 0://"acc off";
                        accState=0;
                        NettyConf.dwfsjg4=NettyConf.dwfsjg3;
                        resetWzhb();
                        if(NettyConf.debug){
                            Log.e("TAG","ACC状态："+accState);
                        }
                        break;
                    case 1://"acc on";
                        accState=1;
                        NettyConf.dwfsjg4=NettyConf.dwfsjg;
                        resetWzhb();
                        if(NettyConf.debug){
                            Log.e("TAG","ACC状态："+accState);
                        }
                        break;
                }
            }
        }
    };

    //充值位置汇报服务
    public void resetWzhb(){
        //重启定位服务
        Message msg=new Message();
        msg.arg1=11;
        Handler handler= (Handler) NettyConf.handlersmap.get("login");
        handler.sendMessage(msg);
    }

}
