package com.dgcheshang.cheji.netty.timer;

import android.util.Log;

import com.dgcheshang.cheji.Tools.Speaking;
import com.dgcheshang.cheji.netty.conf.NettyConf;
import com.dgcheshang.cheji.netty.util.DzwlUtil;
import com.dgcheshang.cheji.netty.util.LocationUtil;
import com.dgcheshang.cheji.netty.util.ZdUtil;

import java.util.TimerTask;

/***********************************************
 * @项目名称：cheji-master-3.5
 * @文件名称：DzwlTimerTask
 * @文件描述：
 * @文件作者：joxhome
 * @创建时间：2017/11/27 11:33
 ***********************************************/
public class DzwlTimerTask extends TimerTask {
    public static String dzwlstate=null;//0，无电子围栏  1，教学区域内  2教学区域外
    public static String jcqy="0";//进出区域标志
    @Override
    public void run() {
        if("1".equals(NettyConf.dzwlcl)) {
            //if (LocationUtil.state && "2".equals(NettyConf.pxkc.substring(3, 4))) {
            boolean flag= ZdUtil.pdDwState();
            if (flag) {
                String rs = DzwlUtil.getDzwlByLocation();
                if(!rs.equals("0")){
                    if (dzwlstate == null) {
                        dzwlstate = rs;
                        if (rs.equals("2")) {
                            Speaking.in("请注意,你已经离开教学区域");
                        } else {
                            Speaking.in("你已经进入教学区域");
                        }
                    } else if (!rs.equals(dzwlstate)) {
                        dzwlstate = rs;
                        if (rs.equals("1")) {
                            Speaking.in("你已经进入教学区域");
                            jcqy="1";
                        } else {
                            Speaking.in("请注意,你已经离开教学区域");
                            jcqy="1";
                        }
                    }
                }

            }else{
                DzwlTimerTask.dzwlstate=null;
            }
        }else{
            this.cancel();
        }
    }
}
