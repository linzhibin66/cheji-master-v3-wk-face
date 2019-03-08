package com.dgcheshang.cheji.Activity.Lukao;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.dgcheshang.cheji.Activity.BaseInitActivity;
import com.dgcheshang.cheji.Adapter.LukaoChangeAdapter;
import com.dgcheshang.cheji.Database.DbHandle;
import com.dgcheshang.cheji.R;
import com.dgcheshang.cheji.netty.po.Line;

import java.util.ArrayList;

/**
 * 路线管理
 * */
public class LukaoChangeActivity extends BaseInitActivity implements View.OnClickListener{

    Context context=LukaoChangeActivity.this;
    LukaoChangeAdapter lukaoChangeAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lukao_change);
        initView();
    }

    private void initView() {
        Bundle extras = getIntent().getExtras();
        final String[] namelist = (String[]) extras.get("list");
        final int[] imagelist = (int[]) extras.get("imagelist");
        String sql="select * from line";
        String[] params=null;
        final ArrayList<Line> list = DbHandle.queryline(sql, params);
        View layout_back = findViewById(R.id.layout_back);
        RecyclerView recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        TextView tv_nocontent = (TextView) findViewById(R.id.tv_nocontent);//暂无路线
        if(list!=null&&list.size()>0){
            recyclerview.setVisibility(View.VISIBLE);
            tv_nocontent.setVisibility(View.GONE);
        }else {
            recyclerview.setVisibility(View.GONE);
            tv_nocontent.setVisibility(View.VISIBLE);
        }
        recyclerview.setLayoutManager(new LinearLayoutManager(context));
        lukaoChangeAdapter = new LukaoChangeAdapter(context,list,namelist);
        recyclerview.setAdapter(lukaoChangeAdapter);
        //行点击
        lukaoChangeAdapter.setMyItemClickListener(new LukaoChangeAdapter.MyItemClickListener() {
            @Override
            public void onItemClick(View V, int position) {
                Intent intent = new Intent();
                intent.setClass(context,LineDetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("list", list.get(position));
                intent.putExtra("namelist",namelist);
                intent.putExtra("imagelist",imagelist);
                startActivity(intent);
            }
        });
        layout_back.setOnClickListener(this);
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
