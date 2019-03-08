package com.dgcheshang.cheji.Activity.Lukao;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dgcheshang.cheji.Activity.BaseInitActivity;
import com.dgcheshang.cheji.Adapter.LukaoGatherAdapter;
import com.dgcheshang.cheji.Database.DbHandle;
import com.dgcheshang.cheji.R;
import com.dgcheshang.cheji.netty.po.Line;
import com.dgcheshang.cheji.netty.util.LocationUtil;

/**
 * 路线采集
 * */
public class LukaoGatherActivity extends BaseInitActivity implements View.OnClickListener{
    Context context =LukaoGatherActivity.this;
    SoundPool soundPool;
    EditText edt_name;
    LukaoGatherAdapter lukaoGatherAdapter;
    int[] imagerichang;
    String[] lukaoname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lukao_gather);
        initView();
    }

    private void initView() {
        Bundle extras = getIntent().getExtras();
        lukaoname = (String[]) extras.get("list");
        imagerichang  = (int[]) extras.get("imagerichang");
        soundPool= new SoundPool(10, AudioManager.STREAM_SYSTEM,5);
        soundPool.load(context, R.raw.choose,1);
        View layout_back = findViewById(R.id.layout_back);
        View layout_change = findViewById(R.id.layout_change);//线路管理
        Button bt_save = (Button) findViewById(R.id.bt_save);//保存
        Button bt_clear = (Button) findViewById(R.id.bt_clear);//清除
        edt_name = (EditText) findViewById(R.id.edt_name);//线路名称
        TextView tv_gps_state = (TextView) findViewById(R.id.tv_gps_state);//gps状态
        TextView tv_lon = (TextView) findViewById(R.id.tv_lon);//经度
        TextView tv_lat = (TextView) findViewById(R.id.tv_lat);//维度
        RecyclerView recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new GridLayoutManager(context,4));

        lukaoGatherAdapter = new LukaoGatherAdapter((LukaoGatherActivity) context, lukaoname,imagerichang,soundPool,tv_lon,tv_lat);
        recyclerview.setAdapter(lukaoGatherAdapter);
        //gps状态
        boolean state = LocationUtil.state;
        if(state){
            tv_gps_state.setText("正常");
        }else {
            tv_gps_state.setText("断开");
        }
        layout_back.setOnClickListener(this);
        bt_save.setOnClickListener(this);
        bt_clear.setOnClickListener(this);
        layout_change.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_back://返回
                finish();
                break;

            case R.id.bt_save://保存
                String name = edt_name.getText().toString().trim();
                if(name.equals("")){
                    Toast.makeText(context,"请输入线路名称",Toast.LENGTH_SHORT).show();
                }else {
                    int getarlistsize = lukaoGatherAdapter.getarlistsize();
                    if(getarlistsize>0){
                        String savelist = lukaoGatherAdapter.savelist();
                        Line line = new Line();
                        line.setMc(name);
                        line.setXlzb(savelist);
                        DbHandle.insertLine(line);
                        Toast.makeText(context,"保存成功",Toast.LENGTH_SHORT).show();
                        finish();
                    }else {
                        Toast.makeText(context,"无保存线路",Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.bt_clear://清除
                lukaoGatherAdapter.clearList();
                lukaoGatherAdapter.notifyDataSetChanged();
                Toast.makeText(context,"清除成功",Toast.LENGTH_SHORT).show();
                break;

            case R.id.layout_change://线路管理
                Intent intent = new Intent();
                intent.setClass(context,LukaoChangeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("list",lukaoname);
                intent.putExtra("imagelist",imagerichang);
                startActivity(intent);
                break;

        }
    }

}
