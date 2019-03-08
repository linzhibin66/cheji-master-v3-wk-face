package com.dgcheshang.cheji.Tools;


import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;

/**
 *播放音乐
 */

public class IsMediaPlayer {
    public   static boolean stopvoice=true;

    static MediaPlayer mp;
    public static void isplay(String url){
//        if(mp!=null){
//            mp.release();
//            mp=new MediaPlayer();
//        }
        mp=new MediaPlayer();
        try {
            mp.setDataSource(url);
            mp.prepare();
            mp.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
    }

    public static void isplay1(Context context,Uri url){
        stopvoice=false;
//        if(mp!=null){
//            mp.release();
//            mp=new MediaPlayer();
//        }
        mp=new MediaPlayer();
        try {
            mp.setDataSource(context,url);
            mp.prepare();
            mp.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
                //已经停止播放
                stopvoice=true;
            }
        });
    }

    /**
     * 暂停播放
     * */
    public static void isPause(){
        if(mp!=null){
            mp.pause();
        }
    }
    /**
     * 停止播放
     * */
    public static void isStop(){
        if(mp!=null){
            mp.stop();
        }
    }

    /**
     * 释放资源
     * */
    public static void isRelease(){
        if(mp!=null){
            mp.release();
        }
    }

}
