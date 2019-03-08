package com.dgcheshang.cheji.Activity.Lukao;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dgcheshang.cheji.Activity.BaseInitActivity;
import com.dgcheshang.cheji.Adapter.LukaoAdapter;
import com.dgcheshang.cheji.R;
import com.dgcheshang.cheji.Tools.IsMediaPlayer;

/**
 * 路考list
 * */
public class LukaoListActivity extends BaseInitActivity implements View.OnClickListener{
    Context context=LukaoListActivity.this;
    CheckBox checkbox;
    SharedPreferences.Editor coachedit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lukao_list);
        initView();
    }

    private void initView() {
        final SharedPreferences coachsp = getSharedPreferences("coach", Context.MODE_PRIVATE); //私有数据
        coachedit = coachsp.edit();
        boolean isshowdetail = coachsp.getBoolean("isshowdetail", false);
        Bundle extras = getIntent().getExtras();
        final String title = extras.getString("title");
        String[] list = (String[]) extras.get("list");//名字
        int[] imagelist = (int[]) extras.get("imagelist");
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(title);
        View layout_back = findViewById(R.id.layout_back);
        checkbox = (CheckBox) findViewById(R.id.checkbox);//是否显示详情
        if(isshowdetail){
            checkbox.setChecked(true);
        }
        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkbox.isChecked()==true){
                    coachedit.putBoolean("isshowdetail",true).commit();
                }else {
                    coachedit.putBoolean("isshowdetail",false).commit();

                }
            }
        });
        RecyclerView recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new GridLayoutManager(context,5));
        if(title.equals("灯光训练")){
            LukaoAdapter lukaoAdapter = new LukaoAdapter(this,list,imagelist,"denguang",coachsp.getBoolean("isshowdetail", false));
            recyclerview.setAdapter(lukaoAdapter);
        }else if(title.equals("日常训练")){
            LukaoAdapter lukaoAdapter = new LukaoAdapter(this,list,imagelist,"richang",coachsp.getBoolean("isshowdetail", false));
            recyclerview.setAdapter(lukaoAdapter);
        }layout_back.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_back://返回
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭页面停止播放
        IsMediaPlayer.isRelease();
    }
}
