package com.dgcheshang.cheji.Activity;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;

import com.dgcheshang.cheji.Database.DbHandle;

import android.widget.Spinner;
import android.widget.Toast;
import com.dgcheshang.cheji.R;
import com.dgcheshang.cheji.Tools.Speaking;
import com.dgcheshang.cheji.netty.conf.NettyConf;
import com.dgcheshang.cheji.netty.util.RlsbUtil;
import com.dgcheshang.cheji.netty.util.ZdUtil;

/**
 *系统设置
 */

public class SystemSetActivity extends BaseInitActivity implements View.OnClickListener{

    Context context=SystemSetActivity.this;
    AudioManager mAudioManager;
    private EditText edt_idcard;
    SharedPreferences.Editor restart_edit;
    String[] text_restart=new String[]{"未选择","定时关机","定时重启"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_set);
        initView();
    }

    /**
     * 初始化布局
     * */
    private void initView() {
        final SharedPreferences restartsp = getSharedPreferences("restartspinner", Context.MODE_PRIVATE);
        restart_edit = restartsp.edit();
        edt_idcard = (EditText) findViewById(R.id.edt_idcard);//身份证号
        Button bt_clear = (Button) findViewById(R.id.bt_clear);//清除按钮
        Button bt_cacnel_all = (Button) findViewById(R.id.bt_cacnel_all);//清除所有
        Button bt_clear_data = (Button) findViewById(R.id.bt_clear_data);//清除过期数据缓存
        Button bt_show_dzwl = (Button) findViewById(R.id.bt_show_dzwl);//电子围栏列表
        CheckBox checkbox_show = (CheckBox) findViewById(R.id.cb_show_dzwl);//选择框
        Spinner spinner_restart = (Spinner) findViewById(R.id.spinner_restart);//开关机选择
        ArrayAdapter<String> restartAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, text_restart);
        restartAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_restart.setAdapter(restartAdapter);
        //获取开关机值
        int restart = restartsp.getInt("restart", 2);
        spinner_restart.setSelection(restart,true);

        if(NettyConf.dzwlcl.equals("0")){
            checkbox_show.setChecked(false);
        }else {
            checkbox_show.setChecked(true);
        }

        //关闭自动模式；
        Settings.System.putInt(getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        //音量控制,初始化定义
         mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //取得最大音量
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        // 取得当前亮度
        int normal = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 255);
        Log.e("TAG","屏幕亮度："+normal);
        //取得当前音量
        int syscurrenvolume= mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        View layout_back = findViewById(R.id.layout_back);
        SeekBar seekbarvolume = (SeekBar) findViewById(R.id.seekBar);//声音
        SeekBar seekbarlight = (SeekBar) findViewById(R.id.seekBar_light);//亮度
        // 进度条绑定当前亮度
        seekbarlight.setMax(255);
        seekbarlight.setProgress(normal);
        seekbarvolume.setMax(maxVolume);
        seekbarvolume.setProgress(syscurrenvolume);
        //声音监听
        seekbarvolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int tmpInt = seekBar.getProgress();
                // 当进度小于1时，设置成1，防止太小。
                if (tmpInt < 1) {
                    tmpInt = 1;
                }
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, tmpInt, 0);

            }
        });
        //亮度监听
        seekbarlight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // 取得当前进度
                int tmpInt = seekBar.getProgress();
                Log.e("TAG","屏幕亮度滑动后："+tmpInt);
                // 当进度小于80时，设置成80，防止太黑看不见的后果。
                if (tmpInt < 80) {
                    tmpInt = 80;
                }
                ContentResolver resolver = getContentResolver();
                // 根据当前进度改变亮度
                Uri uri = android.provider.Settings.System
                        .getUriFor("screen_brightness");
                android.provider.Settings.System.putInt(resolver, "screen_brightness",
                        tmpInt);
                resolver.notifyChange(uri, null);
//

            }
        });
        //自动重启设备
        spinner_restart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                restart_edit.putInt("restart",position);
                restart_edit.commit();

                NettyConf.autoroot=position;
                //position 默认选择2 :0未选择，1定时关机，2定时重启；

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        layout_back.setOnClickListener(this);
        bt_clear.setOnClickListener(this);
        bt_cacnel_all.setOnClickListener(this);
        bt_show_dzwl.setOnClickListener(this);
        bt_clear_data.setOnClickListener(this);
    }

    /**
     * 点击监听
     * */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_back://返回
                finish();
                break;

            case R.id.bt_clear://清除个人缓存
                String idcard = edt_idcard.getText().toString().trim();//身份证号
                clearData(idcard);
                break;

            case R.id.bt_cacnel_all://清除所有人缓存
                clearAllDialog();
                break;

            case R.id.bt_clear_data://清除过期数据缓存
                ZdUtil.deleteCache();
                Toast.makeText(context,"清除成功",Toast.LENGTH_SHORT).show();
                break;

            case R.id.bt_show_dzwl://电子围栏功能
                if(NettyConf.jqstate!=0){
                    Intent intent = new Intent();
                    intent.setClass(context,DzwlActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else {
                    Toast.makeText(context,"暂未鉴权",Toast.LENGTH_SHORT).show();
                }

                break;

        }
    }

    /**
     * 选择是否清除所有缓存
     * */

    private void clearAllDialog() {
        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(context);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("您确定要清除所有缓存吗？");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearAll();
                        dialog.dismiss();
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        // 显示
        normalDialog.setCancelable(false);
        normalDialog.show();
    }

    /**
     * 清除所有缓存
     * */
    private void clearAll() {
        if(NettyConf.xystate==1){
            Speaking.in("请先登出学员");
        }else if(NettyConf.jlstate==1){
            Speaking.in("请先登出教练");
        }else {
            int num = DbHandle.deleteData("tsfrz",null, null);
            if (NettyConf.debug) {
                Log.e("TAG", "清除身份信息数量：" + num);
            }
            //清除教练跟学员原始照片文件夹
            RlsbUtil.deleteDir(NettyConf.jlyxy_picurl);
            Speaking.in("清除成功");

        }
    }

    /**
     * 清除个人缓存
     *
     * @param idcard*/
    private void clearData(String idcard) {
        if(NettyConf.xystate==1){
            Speaking.in("请先登出学员");
        }else if(NettyConf.jlstate==1){
            Speaking.in("请先登出教练");
        }else {
            String[] params = {idcard};
            int num = DbHandle.deleteData("tsfrz", "sfzh = ?", params);
            if (NettyConf.debug) {
                Log.e("TAG", "清除身份信息数量：" + num);
            }
            if (num > 0) {
                //清除个人图片
                boolean deleteone=RlsbUtil.deleteOneFile(NettyConf.jlyxy_picurl+idcard+".jpg");
                Log.e("TAG","删除个人照片："+deleteone);
                Speaking.in("清除成功");
            } else {
                Speaking.in("未找到缓存");
            }
        }
    }
}
