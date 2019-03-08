package com.dgcheshang.cheji.netty.timer;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.dgcheshang.cheji.R;
import com.dgcheshang.cheji.Tools.IsMediaPlayer;
import com.dgcheshang.cheji.netty.conf.NettyConf;
import com.dgcheshang.cheji.netty.util.ZdUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.TimerTask;

/**
 * Created by Administrator on 2017/9/19.
 */

public class LineTimerTask extends TimerTask {
    boolean isexam;
    Context context;
    int[] richanglist={R.raw.lukao13,R.raw.lukao14,R.raw.lukao15,R.raw.lukao16,R.raw.lukao17,R.raw.lukao18,R.raw.lukao19,R.raw.lukao20,R.raw.lukao21,R.raw.lukao22,R.raw.lukao23,R.raw.lukao24,R.raw.lukao25,R.raw.lukao26,R.raw.lukao27,R.raw.lukao28,R.raw.lukao29,R.raw.lukao30,R.raw.lukao31,R.raw.lukao32};

    public LineTimerTask(boolean isexam,Context context) {
        this.isexam=isexam;
        this.context=context;
    }

    @Override
    public void run() {
        String type=ZdUtil.xlSpeakMsg();
        String[] ss=type.split(",");
        for(String s:ss){
            if(StringUtils.isNotEmpty(s)){
                Log.e("TAG","返回类型："+type);
//                String musicurl="/sdcard/chejidoal/lukao"+(Integer.parseInt(s)+1)+".ogg";
                int i1 = richanglist[Integer.parseInt(s)];
                Uri setDataSourceuri = Uri.parse("android.resource://com.dgcheshang.cheji/"+i1);

                if(isexam==true){
                    Message msg = new Message();
                    msg.arg1=1;
                    Bundle bundle = new Bundle();
                    bundle.putString("type",s);
                    msg.setData(bundle);
                    Handler handler = (Handler) NettyConf.handlersmap.get("startexam");
                    if(handler!=null){
                        handler.sendMessage(msg);
                    }
                }
//                IsMediaPlayer.isplay(musicurl);
                IsMediaPlayer.isplay1(context,setDataSourceuri);
            }
        }

    }
}
