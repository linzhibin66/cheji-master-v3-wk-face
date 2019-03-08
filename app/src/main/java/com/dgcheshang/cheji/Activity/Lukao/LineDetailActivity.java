package com.dgcheshang.cheji.Activity.Lukao;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.dgcheshang.cheji.Activity.BaseInitActivity;
import com.dgcheshang.cheji.Adapter.ChangeListAdapter;
import com.dgcheshang.cheji.R;
import com.dgcheshang.cheji.Tools.IsMediaPlayer;
import com.dgcheshang.cheji.netty.conf.NettyConf;
import com.dgcheshang.cheji.netty.po.Line;

import java.util.ArrayList;

/**
 * 线路详情
 * */
public class LineDetailActivity extends BaseInitActivity implements View.OnClickListener{
    Context context=LineDetailActivity.this;
    ChangeListAdapter changeListAdapter;
    RecyclerView recyclerview;
    ArrayList arrayList;
    String[] namelist;
    int[] imagelist;
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
        setContentView(R.layout.activity_line_detail);
        NettyConf.handlersmap.put("linedetail",handler);
        initView();
    }

    private void initView() {
        Bundle extras = getIntent().getExtras();
        Line line = (Line) extras.get("list");
        namelist = (String[]) extras.get("namelist");//图标名称list
        imagelist = (int[]) extras.get("imagelist");//图标list
        arrayList = splitLine(line);
        View layout_back = findViewById(R.id.layout_back);
        TextView line_name = (TextView) findViewById(R.id.line_name);//路线名称
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new GridLayoutManager(this,5));
        line_name.setText(line.getMc());

        changeListAdapter = new ChangeListAdapter(arrayList, context, namelist,imagelist);
        recyclerview.setAdapter(changeListAdapter);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IsMediaPlayer.isRelease();//释放音乐资源
    }
}
