package com.dgcheshang.cheji.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.dgcheshang.cheji.Adapter.DzwlAdapter;
import com.dgcheshang.cheji.Database.DbHandle;
import com.dgcheshang.cheji.R;
import com.dgcheshang.cheji.netty.conf.NettyConf;
import com.dgcheshang.cheji.netty.po.Dzwl;

import java.util.ArrayList;

/**
 *电子围栏列表
 */

public class DzwlActivity extends BaseInitActivity implements View.OnClickListener{
    Context context=DzwlActivity.this;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dzwl);
        init();

    }

    /**
     * 初始化布局
     * */
    private void init() {
        String sql="select * from dzwl";
        String[] params=null;
        ArrayList<Dzwl> querydzwl = DbHandle.querydzwl(sql, params);
        RecyclerView recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        View layout_back = findViewById(R.id.layout_back);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        DzwlAdapter dzwlAdapter = new DzwlAdapter(this,querydzwl);
        recyclerview.setAdapter(dzwlAdapter);
        layout_back.setOnClickListener(this);
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
        }
    }
}
