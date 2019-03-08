package com.dgcheshang.cheji.Activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dgcheshang.cheji.R;
import com.dgcheshang.cheji.netty.conf.NettyConf;
import com.dgcheshang.cheji.netty.util.LocationUtil;
import com.dgcheshang.cheji.netty.util.ZdUtil;

/**
 * 车辆信息
 * */
public class CarDetailActivity extends BaseInitActivity implements View.OnClickListener{
    Context context=CarDetailActivity.this;

    TextView tv_net_state,tv_con_state,tv_jq_state,tv_gps_state,tv_camera_state,tv_self_ip,tv_SIMxlh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_detail);
        init();
    }

    /**
     * 初始化布局
     * */
    private void init() {
        View layout_back = findViewById(R.id.layout_back);//返回按键
        TextView tv_phonenum = (TextView) findViewById(R.id.tv_phonenum);//手机号码
        TextView tv_SIMxlh = (TextView) findViewById(R.id.tv_SIMxlh);//sim卡序列号
        TextView tv_imei = (TextView) findViewById(R.id.tv_imei);//IMEI
        TextView tv_model = (TextView) findViewById(R.id.tv_model);//型号
        tv_net_state = (TextView) findViewById(R.id.tv_net_state);//网络状态
        tv_con_state = (TextView) findViewById(R.id.tv_con_state);//连接状态
        tv_jq_state = (TextView) findViewById(R.id.tv_jq_state);//鉴权状态
        tv_gps_state = (TextView) findViewById(R.id.tv_gps_state);//gps状态
        tv_self_ip = (TextView) findViewById(R.id.tv_self_ip);//本机ip
        tv_camera_state = (TextView) findViewById(R.id.tv_camera_state);//摄像头状态

        String ipAddress = ZdUtil.getIPAddress(context);
        if(ipAddress!=null){
            tv_self_ip.setText(ipAddress);
        }

        int lianjie = NettyConf.constate;
        int jianquan =NettyConf.jqstate;
        //boolean network = NettyConf.netstate;
        boolean gpsstate = LocationUtil.state;
        boolean camerastate = NettyConf.camerastate;

        if(NettyConf.netstate!=null){
            if(NettyConf.netstate){
                tv_net_state.setText("连接");
            }else {
                tv_net_state.setText("断开");
            }
        }else{
            tv_net_state.setText("失败");
        }

        if(gpsstate){
            tv_gps_state.setText("打开");
        }else {
            tv_gps_state.setText("关闭");
        }
        if(lianjie==1){
            tv_con_state.setText("成功");
        }else {
            tv_con_state.setText("失败");
        }
        if(jianquan==1){
            tv_jq_state.setText("成功");
        }else {
            tv_jq_state.setText("失败");
        }

        if(camerastate){
            tv_camera_state.setText("连接");
        }else {
            tv_camera_state.setText("断开");
        }
        tv_imei.setText(NettyConf.imei);
        tv_phonenum.setText(NettyConf.mobile);
        tv_model.setText(NettyConf.model);
        tv_SIMxlh.setText(NettyConf.SIMxlh);
        layout_back.setOnClickListener(this);
    }

    /**
     * 点击监听
     * */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_back://返回按钮
                finish();
                break;
        }
    }
}
