package com.dgcheshang.cheji.netty.timer;

import com.dgcheshang.cheji.netty.util.ZdUtil;

import java.util.TimerTask;

/**
 * Created by Administrator on 2017/10/9.
 */

public class XydcStateTimer extends TimerTask{
    @Override
    public void run() {
        ZdUtil.xydcstate=false;
    }
}
