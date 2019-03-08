package com.dgcheshang.cheji.Activity.Lukao;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dgcheshang.cheji.Activity.BaseInitActivity;
import com.dgcheshang.cheji.Database.DbHandle;
import com.dgcheshang.cheji.R;
import com.dgcheshang.cheji.Tools.IsMediaPlayer;
import com.dgcheshang.cheji.Tools.LoadingDialogUtils;
import com.dgcheshang.cheji.netty.conf.NettyConf;
import com.dgcheshang.cheji.netty.po.Line;
import com.dgcheshang.cheji.netty.timer.LineTimerTask;
import com.dgcheshang.cheji.netty.util.LocationUtil;
import com.dgcheshang.cheji.networkUrl.NetworkUrl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.zip.ZipInputStream;

/**
 * 模拟路考
 * */
public class LukaoActivity extends BaseInitActivity implements View.OnClickListener{
    Context context=LukaoActivity.this;
    //名字数组
    String[] lukaoname=new String[]{"上车准备","起步","变更车道","直线行驶","通过公交车站","通过学校区域","通过路口","通过人行横道","会车","超车","掉头","靠边停车","左转","右转","减速让行","禁止鸣笛","通过拱桥","通过急弯坡路","加减档","考试完成"};
    String[] lightingname=new String[]{"夜间灯光操作一","夜间灯光操作二","夜间灯光操作三","夜间灯光操作四","夜间灯光操作五","单项练习"};

   //夜间灯光训练照片
    int[] imageDenguang=new int[]{R.mipmap.lukao_light1,R.mipmap.lukao_light2,R.mipmap.lukao_light3,R.mipmap.lukao_light4,R.mipmap.lukao_light5,R.mipmap.lukao_light6,R.mipmap.lukao_light7};
    //日常训练照片
    int[] imagerichang=new int[]{R.mipmap.lukao1,R.mipmap.lukao2,R.mipmap.lukao3,R.mipmap.lukao4,R.mipmap.lukao5,R.mipmap.lukao6,R.mipmap.lukao7,R.mipmap.lukao8,R.mipmap.lukao9,R.mipmap.lukao10,R.mipmap.lukao11,R.mipmap.lukao12,R.mipmap.lukao13,R.mipmap.lukao14,R.mipmap.lukao15,R.mipmap.lukao16,R.mipmap.lukao17,R.mipmap.lukao18,R.mipmap.lukao19,R.mipmap.lukao20};

    Dialog loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lukao);
        initView();
    }

    private void initView() {
        final SharedPreferences lukaosp = getSharedPreferences("lukao", Context.MODE_PRIVATE);
        View layout_light = findViewById(R.id.layout_light);//灯光训练
        View layout_sum = findViewById(R.id.layout_sum);//日常训练
        View layout_gather = findViewById(R.id.layout_gather);//路线采集
        View layout_change = findViewById(R.id.layout_change);//路线管理
        View layout_moni = findViewById(R.id.layout_moni);//模拟考试
        View layout_back = findViewById(R.id.layout_back);//返回
        final CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);//开启模拟路考
        boolean isstart = lukaosp.getBoolean("isstart", false);//是否开启路考
        checkBox.setChecked(isstart);
        if(isstart==true){
            Line line = getLine();
            if(line!=null){
                startExam(line);
            }
        }

        //开启路考选项监听
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked==true){
                    Line line = getLine();
                    if(line!=null){
                        startExam(line);
                    }else {
                        checkBox.setChecked(false);
                        isChecked=false;
                    }
                }else {
                    if(NettyConf.xltimer!=null){
                        IsMediaPlayer.isRelease();
                        NettyConf.xltimer.cancel();
                        NettyConf.xltimer=null;
                    }
                }
                SharedPreferences.Editor edit = lukaosp.edit();
                edit.putBoolean("isstart",isChecked);
                edit.commit();

            }
        });
        layout_light.setOnClickListener(this);
        layout_sum.setOnClickListener(this);
        layout_back.setOnClickListener(this);
        layout_gather.setOnClickListener(this);
        layout_change.setOnClickListener(this);
        layout_moni.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){

            case R.id.layout_back://返回
                finish();
                break;

            case R.id.layout_moni://模拟考试
                intent.setClass(context,LukaoExamActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("list", lukaoname);
                intent.putExtra("imagelist",imagerichang);
                startActivity(intent);
                break;

            case R.id.layout_sum://日常训练
                intent.setClass(context,LukaoListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("title","日常训练");
                intent.putExtra("list", lukaoname);
                intent.putExtra("imagelist",imagerichang);
                startActivity(intent);
                break;

            case R.id.layout_light://灯光训练
                intent.setClass(context,LukaoListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("title","灯光训练");
                intent.putExtra("list", lightingname);
                intent.putExtra("imagelist",imageDenguang);
                startActivity(intent);

                break;
            case R.id.layout_gather://路线采集
                intent.setClass(context,LukaoGatherActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("list",lukaoname);
                intent.putExtra("imagerichang",imagerichang);
                startActivity(intent);
                break;
            case R.id.layout_change://路线管理
                intent.setClass(context,LukaoChangeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("list",lukaoname);
                intent.putExtra("imagelist",imagerichang);
                startActivity(intent);
                break;
        }
    }


    /**
     * 获取保存选择的第几条线路
     * */
    public Line getLine(){
        String sql="select * from line";
        final String[] params=null;
        final ArrayList<Line> list = DbHandle.queryline(sql, params);
        SharedPreferences lukaosp = getSharedPreferences("lukao", Context.MODE_PRIVATE);
        String linename = lukaosp.getString("linename", "");
        if(!linename.isEmpty()&&list.size()>0){
            Line line=null;
            for(int i =0;i<list.size();i++){
                String mc = list.get(i).getMc();
                if(mc.equals(linename)){
                    return list.get(i);
                }
            }
            return line;
        }else {
            Toast.makeText(context,"请先选择模拟考试路线",Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    /**
     * 开启路考
     * */
    public void startExam(Line line){
        if(NettyConf.xltimer!=null){
            NettyConf.xltimer.cancel();
            NettyConf.xltimer=null;
        }
        NettyConf.line=line;
        NettyConf.xltimer = new Timer();
        LineTimerTask lineTask = new LineTimerTask(false,context);
        NettyConf.xltimer.schedule(lineTask,0,1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭页面停止播放
        IsMediaPlayer.isRelease();
    }

}
