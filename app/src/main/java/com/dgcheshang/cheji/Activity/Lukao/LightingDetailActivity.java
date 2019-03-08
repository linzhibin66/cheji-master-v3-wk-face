package com.dgcheshang.cheji.Activity.Lukao;


import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dgcheshang.cheji.Activity.BaseInitActivity;
import com.dgcheshang.cheji.Adapter.LukaoLightingAdapter;
import com.dgcheshang.cheji.Bean.LightingBean;
import com.dgcheshang.cheji.R;
import com.dgcheshang.cheji.Tools.IsMediaPlayer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LightingDetailActivity extends BaseInitActivity implements View.OnClickListener{

    Context context = LightingDetailActivity.this;

    String lightlist5="{\"lightinglist\":[{\"name\":\"下面将进行模拟夜间行驶灯光的考试，请在5秒内做出相应的灯光操作。请开启前照灯\",\"doing\":\"#开始\",\"voice\":\"0\",\"icon\":\"2\"},{\"name\":\"夜间在照明良好的道路上行驶\",\"doing\":\"#近光灯\",\"voice\":\"1\",\"icon\":\"4\"},{\"name\":\"夜间同方向近距离跟车行驶\",\"doing\":\"#近光灯\",\"voice\":\"2\",\"icon\":\"4\"},{\"name\":\"夜间在窄路与非机动车会车\",\"doing\":\"#近光灯\",\"voice\":\"3\",\"icon\":\"4\"},{\"name\":\"夜间与机动车会车\",\"doing\":\"#近光灯\",\"voice\":\"4\",\"icon\":\"4\"},{\"name\":\"夜间在窄桥与非机动车会车\",\"doing\":\"#近光灯\",\"voice\":\"5\",\"icon\":\"4\"},{\"name\":\"夜间在没有路灯，照明不良的条件下行驶\",\"doing\":\"#远光灯\",\"voice\":\"6\",\"icon\":\"6\"},{\"name\":\"夜间直行通过路口\",\"doing\":\"#近光灯\",\"voice\":\"7\",\"icon\":\"4\"},{\"name\":\"夜间通过坡路，拱桥\",\"doing\":\"#远近光交替\",\"voice\":\"8\",\"icon\":\"5\"},{\"name\":\"夜间通过没有交通信号灯控制的路口\",\"doing\":\"#远近光交替\",\"voice\":\"9\",\"icon\":\"5\"},{\"name\":\"夜间通过拱桥，人行横道\",\"doing\":\"#远近光交替\",\"voice\":\"10\",\"icon\":\"5\"},{\"name\":\"夜间超越前方车辆\",\"doing\":\"#远近光交替\",\"voice\":\"11\",\"icon\":\"5\"},{\"name\":\"夜间通过急弯，坡路\",\"doing\":\"#远近光交替\",\"voice\":\"12\",\"icon\":\"5\"},{\"name\":\"夜间在道路上发生故障，妨碍交通有难以移动\",\"doing\":\"#示廓灯/危险警示灯\",\"voice\":\"13\",\"icon\":\"3\"},{\"name\":\"夜间在道路上发生交通事故，妨碍交通有难以移动\",\"doing\":\"#示廓灯+危险警示灯\",\"voice\":\"14\",\"icon\":\"3\"},{\"name\":\"路边临时停车\",\"doing\":\"#示廓灯/危险警示灯\",\"voice\":\"15\",\"icon\":\"3\"},{\"name\":\"夜间在雾天行驶\",\"doing\":\"#近光灯+雾灯+危险警示灯\",\"voice\":\"16\",\"icon\":\"3\"},{\"name\":\"模拟夜间考试完成。请关闭所有灯光\",\"doing\":\"#结束\",\"voice\":\"17\",\"icon\":\"1\"}]}";
    String lightlist0="{\"lightinglist\":[{\"name\":\"下面将进行模拟夜间行驶灯光的考试，请在5秒内做出相应的灯光操作。请开启前照灯\",\"doing\":\"#开始\",\"voice\":\"0\",\"icon\":\"2\"},{\"name\":\"夜间通过坡路，拱桥\",\"doing\":\"#远近光交替\",\"voice\":\"8\",\"icon\":\"5\"},{\"name\":\"夜间超越前方车辆\",\"doing\":\"#远近光交替\",\"voice\":\"11\",\"icon\":\"5\"},{\"name\":\"夜间在没有路灯，照明不良的条件下行驶\",\"doing\":\"#远光灯\",\"voice\":\"6\",\"icon\":\"6\"},{\"name\":\"夜间与机动车会车\",\"doing\":\"#近光灯\",\"voice\":\"4\",\"icon\":\"4\"},{\"name\":\"路边临时停车\",\"doing\":\"#示廓灯/危险警示灯\",\"voice\":\"15\",\"icon\":\"3\"},{\"name\":\"模拟夜间考试完成。请关闭所有灯光\",\"doing\":\"#结束\",\"voice\":\"17\",\"icon\":\"1\"}]}";
    String lightlist1="{\"lightinglist\":[{\"name\":\"下面将进行模拟夜间行驶灯光的考试，请在5秒内做出相应的灯光操作。请开启前照灯\",\"doing\":\"#开始\",\"voice\":\"0\",\"icon\":\"2\"},{\"name\":\"夜间通过急弯，坡路\",\"doing\":\"#远近光交替\",\"voice\":\"12\",\"icon\":\"5\"},{\"name\":\"夜间超越前方车辆\",\"doing\":\"#远近光交替\",\"voice\":\"11\",\"icon\":\"5\"},{\"name\":\"夜间在没有路灯，照明不良的条件下行驶\",\"doing\":\"#远光灯\",\"voice\":\"6\",\"icon\":\"6\"},{\"name\":\"夜间同方向近距离跟车行驶\",\"doing\":\"#近光灯\",\"voice\":\"2\",\"icon\":\"4\"},{\"name\":\"路边临时停车\",\"doing\":\"#示廓灯/危险警示灯\",\"voice\":\"15\",\"icon\":\"3\"},{\"name\":\"模拟夜间考试完成。请关闭所有灯光\",\"doing\":\"#结束\",\"voice\":\"17\",\"icon\":\"1\"}]}";
    String lightlist2="{\"lightinglist\":[{\"name\":\"下面将进行模拟夜间行驶灯光的考试，请在5秒内做出相应的灯光操作。请开启前照灯\",\"doing\":\"#开始\",\"voice\":\"0\",\"icon\":\"2\"},{\"name\":\"夜间在道路上发生交通事故，妨碍交通有难以移动\",\"doing\":\"#示廓灯+危险警示灯\",\"voice\":\"14\",\"icon\":\"3\"},{\"name\":\"夜间超越前方车辆\",\"doing\":\"#远近光交替\",\"voice\":\"11\",\"icon\":\"5\"},{\"name\":\"夜间通过拱桥，人行横道\",\"doing\":\"#远近光交替\",\"voice\":\"10\",\"icon\":\"5\"},{\"name\":\"夜间在没有路灯，照明不良的条件下行驶\",\"doing\":\"#近光灯\",\"voice\":\"6\",\"icon\":\"4\"},{\"name\":\"夜间在照明良好的道路上行驶\",\"doing\":\"#近光灯\",\"voice\":\"1\",\"icon\":\"4\"},{\"name\":\"模拟夜间考试完成。请关闭所有灯光\",\"doing\":\"#结束\",\"voice\":\"17\",\"icon\":\"1\"}]}";
    String lightlist3="{\"lightinglist\":[{\"name\":\"下面将进行模拟夜间行驶灯光的考试，请在5秒内做出相应的灯光操作。请开启前照灯\",\"doing\":\"#开始\",\"voice\":\"0\",\"icon\":\"2\"},{\"name\":\"夜间在没有路灯，照明不良的条件下行驶\",\"doing\":\"#远光灯\",\"voice\":\"6\",\"icon\":\"6\"},{\"name\":\"夜间通过没有交通信号灯控制的路口\",\"doing\":\"#远近光交替\",\"voice\":\"9\",\"icon\":\"5\"},{\"name\":\"夜间在道路上发生交通事故，妨碍交通有难以移动\",\"do\":\"#示廓灯+危险警示灯\",\"voice\":\"14\",\"icon\":\"3\"},{\"name\":\"夜间在窄桥与非机动车会车\",\"doing\":\"#近光灯\",\"voice\":\"5\",\"icon\":\"4\"},{\"name\":\"路边临时停车\",\"doing\":\"#示廓灯/危险警示灯\",\"voice\":\"15\",\"icon\":\"3\"},{\"name\":\"夜间直行通过路口\",\"doing\":\"#近光灯\",\"voice\":\"7\",\"icon\":\"4\"},{\"name\":\"模拟夜间考试完成。请关闭所有灯光\",\"doing\":\"#结束\",\"voice\":\"17\",\"icon\":\"1\"}]}";
    String lightlist4="{\"lightinglist\":[{\"name\":\"下面将进行模拟夜间行驶灯光的考试，请在5秒内做出相应的灯光操作。请开启前照灯\",\"doing\":\"#开始\",\"voice\":\"0\",\"icon\":\"2\"},{\"name\":\"夜间超越前方车辆\",\"doing\":\"#远近光交替\",\"voice\":\"11\",\"icon\":\"5\"},{\"name\":\"路边临时停车\",\"doing\":\"#示廓灯/危险警示灯\",\"voice\":\"15\",\"icon\":\"3\"},{\"name\":\"夜间在窄路与非机动车会车\",\"doing\":\"#近光灯\",\"voice\":\"3\",\"icon\":\"4\"},{\"name\":\"夜间在道路上发生故障，妨碍交通有难以移动\",\"doing\":\"#示廓灯/危险警示灯\",\"voice\":\"13\",\"icon\":\"3\"},{\"name\":\"模拟夜间考试完成。请关闭所有灯光\",\"doing\":\"#结束\",\"voice\":\"17\",\"icon\":\"2\"}]}";
    //图标
    int[] iconlist= {R.mipmap.lukao_light1,R.mipmap.lukao_light2,R.mipmap.lukao_light3,R.mipmap.lukao_light4,R.mipmap.lukao_light5,R.mipmap.lukao_light6,R.mipmap.lukao_light7};
    //报读
    int[] voicelist={R.raw.lighting0,R.raw.lighting1,R.raw.lighting2,R.raw.lighting3,R.raw.lighting4,R.raw.lighting5,R.raw.lighting6,R.raw.lighting7,R.raw.lighting8,R.raw.lighting9,R.raw.lighting10,R.raw.lighting11,R.raw.lighting12,R.raw.lighting13,R.raw.lighting14,R.raw.lighting15,R.raw.lighting16,R.raw.lighting17};

    int i=0;
    RecyclerView recyclerview;
    //记录目标项位置
    private int mToPosition;
    //目标项是否在最后一个可见项之后
    private boolean mShouldScroll;

    RecyclerView.LayoutManager manager;
    //上级页面哪个图标点击进来的
    int position;
    Timer playtimer;
    Timer lasttimer;
    Timer gbtbtimer;
    Timer bdtimer;
    List<LightingBean> list;
    Handler handler=new  Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.arg1==1){
                try {

                i++;
                int position=i-1;
                View view = manager.findViewByPosition(position);
                ImageView icon = (ImageView) view.findViewById(R.id.image_list_icon);
                icon.setBackgroundResource(R.mipmap.lukao_light8);
                if(position>0){
                    View view2 = manager.findViewByPosition(position-1);
                    ImageView  icon2 = (ImageView) view2.findViewById(R.id.image_list_icon);
                     icon2.setBackgroundResource(iconlist[Integer.parseInt(list.get(position-1).getIcon())]);
                }

                }catch (Exception e){
                    Log.e("TAG","报读语音变换图标出错");
                }

            }else if(msg.arg1==2){
                int position=i-1;
                View view = manager.findViewByPosition(position);
                ImageView icon = (ImageView) view.findViewById(R.id.image_list_icon);
                icon.setBackgroundResource(iconlist[Integer.parseInt(list.get(position).getIcon())]);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lighting_detail);
        initView();
    }

    /**
     * 初始化布局
     * */
    private void initView() {
        Bundle extras = getIntent().getExtras();
        String title = extras.getString("title");
        position = extras.getInt("position");
        String data="";
        if(position==0){
            data=lightlist0;
        }else if(position==1){
            data=lightlist1;
        }else if(position==2){
            data=lightlist2;
        }else if(position==3){
            data=lightlist3;
        }else if(position==4){
            data=lightlist4;
        }else if(position==5){
            data=lightlist5;
        }
        View layout_back = findViewById(R.id.layout_back);
        layout_back.setOnClickListener(this);
        TextView tv_title = (TextView) findViewById(R.id.tv_title);//标题
        tv_title.setText(title);
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(context));
        Gson gson = new Gson();
        try {
            JSONObject jsonObject = new JSONObject(data);
            String str = jsonObject.getString("lightinglist");
            list = gson.fromJson(str, new TypeToken<List<LightingBean>>(){}.getType());
            LukaoLightingAdapter lukaoAdapter = new LukaoLightingAdapter(this,list,iconlist,voicelist,position);
            recyclerview.setAdapter(lukaoAdapter);
            manager = recyclerview.getLayoutManager();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 点击监听
     * */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_back:
                finish();
                break;
        }
    }

    /**
    /**
     * 滑动到指定位置
     *
     * @param mRecyclerView
     * @param position
     */
    private void smoothMoveToPosition(RecyclerView mRecyclerView, final int position) {
        // 第一个可见位置
        int firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        // 最后一个可见位置
        int lastItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));

        if (position < firstItem) {
            // 如果跳转位置在第一个可见位置之前，就smoothScrollToPosition可以直接跳转
            mRecyclerView.smoothScrollToPosition(position);
        } else if (position <= lastItem) {
            // 跳转位置在第一个可见项之后，最后一个可见项之前
            // smoothScrollToPosition根本不会动，此时调用smoothScrollBy来滑动到指定位置
            int movePosition = position - firstItem;
            if (movePosition >= 0 && movePosition < mRecyclerView.getChildCount()) {
                int top = mRecyclerView.getChildAt(movePosition).getTop();
                mRecyclerView.smoothScrollBy(0, top);
            }
        } else {
            // 如果要跳转的位置在最后可见项之后，则先调用smoothScrollToPosition将要跳转的位置滚动到可见位置
            // 再通过onScrollStateChanged控制再次调用smoothMoveToPosition，执行上一个判断中的方法
            mRecyclerView.smoothScrollToPosition(position);
            mToPosition = position;
            mShouldScroll = true;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        //只要不是单项练习，都连续报读
        if(position!=5){
            playtimer = new Timer();
            TimerTask playtask = new TimerTask() {
                @Override
                public void run() {
                    if(IsMediaPlayer.stopvoice==true&&i<list.size()){
                        IsMediaPlayer.stopvoice=false;
                        IsMediaPlayer.isRelease();
                        int i1 = voicelist[Integer.parseInt(list.get(i).getVoice())];
                        final Uri setDataSourceuri = Uri.parse("android.resource://com.dgcheshang.cheji/"+i1);
                        //延时报读
                        if(i==0){
                            smoothMoveToPosition(recyclerview,i);
                            //改变图标延时
                            gbtbtimer = new Timer();
                            TimerTask task=new TimerTask() {
                                @Override
                                public void run() {
                                    //语音报读
                                    IsMediaPlayer.isplay1(context,setDataSourceuri);
                                    //发送更改图标
                                    Message msg = new Message();
                                    msg.arg1=1;
                                    handler.sendMessage(msg);
                                    gbtbtimer.cancel();
                                }
                            };
                            gbtbtimer.schedule(task,100);

                        }else {
                            //报读延时
                            bdtimer = new Timer();
                            TimerTask bdtask=new TimerTask() {
                                @Override
                                public void run() {
                                    Log.e("TAG","报读坐标="+i);
                                    bdtimer.cancel();
                                    smoothMoveToPosition(recyclerview,i-1);

                                    //改变图标延时
                                    gbtbtimer = new Timer();
                                    TimerTask task=new TimerTask() {
                                        @Override
                                        public void run() {
                                            //语音报读
                                            IsMediaPlayer.isplay1(context,setDataSourceuri);
                                            //发送改变图标
                                            Message msg = new Message();
                                            msg.arg1=1;
                                            handler.sendMessage(msg);
                                            gbtbtimer.cancel();
                                        }
                                    };
                                    gbtbtimer.schedule(task,100);
                                }
                            };
                            bdtimer.schedule(bdtask,3000);
                        }

                        //最后一个则变换图标
                        if(i==list.size()-1){
                            lasttimer = new Timer();
                            TimerTask ystask=new TimerTask() {
                                @Override
                                public void run() {
                                    lasttimer.cancel();
                                    playtimer.cancel();
                                    Message msg = new Message();
                                    msg.arg1=2;
                                    handler.sendMessage(msg);

                                }
                            };
                            lasttimer.schedule(ystask,9000);
                        }

                    }
                }
            };
            playtimer.schedule(playtask,200,500);

        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        //报读延时器
        if(bdtimer!=null){
            bdtimer.cancel();
        }
        //最后一行图标延时器
        if(lasttimer!=null){
            lasttimer.cancel();
        }
        //改变图标定时器
        if(gbtbtimer!=null){
            gbtbtimer.cancel();
        }
        //循环判断是否读完定时器
        if(playtimer!=null){
            playtimer.cancel();
        }
        IsMediaPlayer.stopvoice=true;
        //关闭页面停止播放
        IsMediaPlayer.isRelease();
    }

}
