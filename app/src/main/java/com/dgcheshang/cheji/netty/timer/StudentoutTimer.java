package com.dgcheshang.cheji.netty.timer;

import android.os.Handler;
import android.os.Message;

import com.dgcheshang.cheji.CjApplication;
import com.dgcheshang.cheji.Database.DbHandle;
import com.dgcheshang.cheji.Database.MyDatabase;
import com.dgcheshang.cheji.netty.conf.NettyConf;
import com.dgcheshang.cheji.netty.po.Tdata;
import com.dgcheshang.cheji.netty.serverreply.XydcR;
import com.dgcheshang.cheji.netty.util.ForwardUtil;
import com.dgcheshang.cheji.netty.util.ZdUtil;

import java.util.List;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/7/8.
 */

public class StudentoutTimer extends TimerTask {
    @Override
    public void run() {
        if(NettyConf.handlersmap.get("loginstudent")!=null) {
            Message msg = new Message();
            msg.arg1 = 8;
            Handler handler = (Handler) NettyConf.handlersmap.get("loginstudent");
            handler.sendMessage(msg);
        }else{
            List<Tdata> list=ZdUtil.studentOut2();
            if(NettyConf.sendState&&NettyConf.constate==1&&NettyConf.jqstate==1){
                ForwardUtil.sendData(list, 1,6);
            }else{
                DbHandle.insertTdatas(list,6);
                //改变学员登出状态
                ZdUtil.handleStudentOut();
            }
        }
    }
}
