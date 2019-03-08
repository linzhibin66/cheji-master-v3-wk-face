package com.dgcheshang.cheji.Activity.Lukao;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dgcheshang.cheji.Activity.BaseInitActivity;
import com.dgcheshang.cheji.Adapter.StartExamAdapter;
import com.dgcheshang.cheji.R;
import com.dgcheshang.cheji.netty.conf.NettyConf;
import com.dgcheshang.cheji.netty.po.Line;
import com.dgcheshang.cheji.netty.timer.LineTimerTask;

import java.util.ArrayList;
import java.util.Timer;

public class StartExamActivity extends BaseInitActivity implements View.OnClickListener{

    Context context =StartExamActivity.this;
    ArrayList poslist=new ArrayList();
    ArrayList arrayList;
    String[] namelist;
    int[] imagelist;
    Button bt_start;
    int isstart=0;
    StartExamAdapter startExamAdapter;
    RecyclerView recyclerview;
    EditText edt_bdjl;
    SharedPreferences lukaosp;
    Line line;
    Chronometer time;
    TextView tv_exam_state;
     Handler handler=new  Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == 1) {
                Bundle data = msg.getData();
                String  type = data.getString("type");
                int i = Integer.parseInt(type);
                poslist.add(i);
                if(poslist.size()==arrayList.size()){
                    time.stop();
                }
                startExamAdapter = new StartExamAdapter(context,namelist,arrayList,poslist,isstart);
                recyclerview.setAdapter(startExamAdapter);
                startExamAdapter.notifyDataSetChanged();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_exam);
        NettyConf.handlersmap.put("startexam",handler);
        initView();
    }

    private void initView() {
        lukaosp = getSharedPreferences("lukao", Context.MODE_PRIVATE);
        SharedPreferences studentsp = getSharedPreferences("student", Context.MODE_PRIVATE);
        Bundle extras = getIntent().getExtras();
        line = (Line) extras.get("list");
        namelist = (String[]) extras.get("namelist");//名字list
        imagelist = (int[]) extras.get("imagelist");//图标list
//        Line line=NettyConf.selectedLine;
        arrayList = splitLine(line);
        Button bt_out = (Button) findViewById(R.id.bt_out);
        bt_start = (Button) findViewById(R.id.bt_start);
        TextView tv_linename = (TextView) findViewById(R.id.tv_linename);//线路名称
        time = (Chronometer) findViewById(R.id.time);//时间计时器
        tv_exam_state = (TextView) findViewById(R.id.tv_exam_state);//考试状态
        TextView tv_stu_name = (TextView) findViewById(R.id.tv_stu_name);//姓名
        edt_bdjl = (EditText) findViewById(R.id.edt_bdjl);//报读距离
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        int bdjl = lukaosp.getInt("bdjl", NettyConf.bdjl);
        edt_bdjl.setText(bdjl+"");
        tv_linename.setText(line.getMc());
        if(NettyConf.xystate==1){
            //有学员登录
            String string = studentsp.getString("xyxm", "练习学员");//获取学员姓名
            tv_stu_name.setText(string);
        }
        recyclerview.setLayoutManager(new GridLayoutManager(this,3));
        startExamAdapter = new StartExamAdapter(context,namelist,arrayList,poslist,isstart);
        recyclerview.setAdapter(startExamAdapter);
        bt_out.setOnClickListener(this);
        bt_start.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_out://退出考试
                if(isstart==1){
                    Toast.makeText(context,"请先结束考试",Toast.LENGTH_SHORT).show();
                }else {
                    finish();
                }

                break;
            case R.id.bt_start://开始考试
                if(isstart==0){
                    String trim = edt_bdjl.getText().toString().trim();
                    int i = Integer.parseInt(trim);
                    NettyConf.bdjl=i;//赋值给报读距离
                    SharedPreferences.Editor edit = lukaosp.edit();
                    edit.putInt("bdjl",i);//报读距离
                    edit.commit();
                    //开始考试
                    bt_start.setText("结束考试");
                    isstart=1;
                    tv_exam_state.setText("正在考试");
                    //启动时间计时器
                    time.setBase(SystemClock.elapsedRealtime());//计时器清零
                    time.start();
                    //开启报读定时器
                    if(NettyConf.xltimer!=null){
                        NettyConf.xltimer.cancel();
                        NettyConf.xltimer=null;
                    }
                    NettyConf.line=line;
                    NettyConf.xltimer = new Timer();
                    LineTimerTask lineTask = new LineTimerTask(true,context);
                    NettyConf.xltimer.schedule(lineTask,0,1000);
                }else {
                    //结束考试
                    bt_start.setText("开始考试");
                    isstart=0;
                    time.stop();
                    if(NettyConf.xltimer!=null){
                        NettyConf.xltimer.cancel();
                        NettyConf.xltimer=null;
                    }
                    startExamAdapter = new StartExamAdapter(context,namelist,arrayList,poslist,isstart);
                    recyclerview.setAdapter(startExamAdapter);
                    startExamAdapter.notifyDataSetChanged();
                    finish();
                }

                break;
        }
    }

    /**
     * 分割arlist
     * */
    public ArrayList splitLine(Line line){
        ArrayList arlist = new ArrayList();
        String xlzb = line.getXlzb();
        String[] s = xlzb.split(";");
        for(int i=0;i<s.length;i++){
            String s1 = s[i];
            arlist.add(s1);
        }
        return arlist;
    }

    /**
     * 禁用返回键
     * */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }

}
