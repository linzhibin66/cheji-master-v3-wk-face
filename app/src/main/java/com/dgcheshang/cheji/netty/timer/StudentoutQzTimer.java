package com.dgcheshang.cheji.netty.timer;

import android.os.Handler;
import android.os.Message;

import com.dgcheshang.cheji.CjApplication;
import com.dgcheshang.cheji.Database.MyDatabase;
import com.dgcheshang.cheji.netty.conf.NettyConf;
import com.dgcheshang.cheji.netty.po.Tdata;
import com.dgcheshang.cheji.netty.util.ForwardUtil;
import com.dgcheshang.cheji.netty.util.ZdUtil;

import java.util.List;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/7/8.
 */

public class StudentoutQzTimer extends TimerTask {
    @Override
    public void run() {
        if(NettyConf.xystate==1){
            //学员登出
            ZdUtil.studentOut1();
        }
    }
}
