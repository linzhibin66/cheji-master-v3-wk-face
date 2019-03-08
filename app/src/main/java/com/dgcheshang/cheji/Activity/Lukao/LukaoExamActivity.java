package com.dgcheshang.cheji.Activity.Lukao;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.dgcheshang.cheji.Activity.BaseInitActivity;
import com.dgcheshang.cheji.Adapter.LukaoExamAdapter;
import com.dgcheshang.cheji.Database.DbHandle;
import com.dgcheshang.cheji.R;
import com.dgcheshang.cheji.netty.conf.NettyConf;
import com.dgcheshang.cheji.netty.po.Line;


import java.util.ArrayList;

/**
 * 路考模拟考试
 * */
public class LukaoExamActivity extends BaseInitActivity implements View.OnClickListener{
    Context context=LukaoExamActivity.this;
    LukaoExamAdapter lukaoExamAdapter;
    Handler handler=new  Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == 1) {

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lukao_exam);
        NettyConf.handlersmap.put("lukaoExam",handler);
        initView();
    }

    private void initView() {
        try {
            Bundle extras = getIntent().getExtras();
            final String[] namelist = (String[]) extras.get("list");
            final int[] imagelist = (int[]) extras.get("imagelist");
            String sql = "select * from line";
            final String[] params = null;
            final ArrayList<Line> list = DbHandle.queryline(sql, params);
            SharedPreferences lukaosp = getSharedPreferences("lukao", Context.MODE_PRIVATE);
            View layout_back = findViewById(R.id.layout_back);
            TextView tv_nocontent = (TextView) findViewById(R.id.tv_nocontent);//暂无路线
            RecyclerView recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
            if (list!=null&&list.size() > 0) {
                recyclerview.setVisibility(View.VISIBLE);
                tv_nocontent.setVisibility(View.GONE);
            } else {
                tv_nocontent.setVisibility(View.VISIBLE);
                recyclerview.setVisibility(View.GONE);
            }
            recyclerview.setLayoutManager(new LinearLayoutManager(context));
            lukaoExamAdapter = new LukaoExamAdapter(context, list, namelist, lukaosp, imagelist);
            recyclerview.setAdapter(lukaoExamAdapter);
            layout_back.setOnClickListener(this);
            //行点击
            lukaoExamAdapter.setMyItemClickListener(new LukaoExamAdapter.MyItemClickListener() {
                @Override
                public void onItemClick(View V, int position) {
                    Intent intent = new Intent();
                    intent.setClass(context, LineDetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("list", list.get(position));
                    intent.putExtra("namelist", namelist);//图标名字
                    intent.putExtra("imagelist", imagelist);//图标
                    startActivity(intent);
                }
            });
        }catch(Exception e){e.printStackTrace();}
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.layout_back:
                finish();
                break;
        }
    }

}
