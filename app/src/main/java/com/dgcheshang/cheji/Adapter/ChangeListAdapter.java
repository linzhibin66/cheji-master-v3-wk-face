package com.dgcheshang.cheji.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.dgcheshang.cheji.R;
import com.dgcheshang.cheji.Tools.IsMediaPlayer;

import java.util.ArrayList;

/**
 *路考adapter
 */

public class ChangeListAdapter extends RecyclerView.Adapter<ChangeListAdapter.ViewHolder> {
    MyItemClickListener myItemClickListener;
    MyItemLongClickListener myItemLongClickListener;
    Context mContent;
    ArrayList tvcontent;
    String[] namelist;
    int[] imagelist;
    int[] richanglist={R.raw.lukao13,R.raw.lukao14,R.raw.lukao15,R.raw.lukao16,R.raw.lukao17,R.raw.lukao18,R.raw.lukao19,R.raw.lukao20,R.raw.lukao21,R.raw.lukao22,R.raw.lukao23,R.raw.lukao24,R.raw.lukao25,R.raw.lukao26,R.raw.lukao27,R.raw.lukao28,R.raw.lukao29,R.raw.lukao30,R.raw.lukao31,R.raw.lukao32};

    public ChangeListAdapter(ArrayList list, Context mContent, String[] namelist, int[] imagerichang) {
        this.tvcontent=list;
        this.mContent=mContent;
        this.namelist=namelist;
        this.imagelist=imagerichang;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContent).inflate(R.layout.lukao_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view,myItemClickListener,myItemLongClickListener);
        return holder;
    }
    /**
     * 操作
     * */
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        String  str = (String) tvcontent.get(position);
        String[] split = str.split(",");
        String s2 = split[1];//下标
        final int i1 = Integer.parseInt(s2);
        s2 = namelist[i1];//名字
        String s3 = "纬度："+split[2];
        String s4 = "经度："+split[3];
        final String location=s3+"    "+s4;
        holder.tv_list_name.setText(s2);
        holder.image_list_icon.setBackgroundResource(imagelist[i1]);
        //图标点击监听
        holder.layout_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContent,location,Toast.LENGTH_SHORT).show();//显示经纬度
//                String musicurl="/sdcard/chejidoal/lukao"+(i1+1)+".ogg";

                IsMediaPlayer.isRelease();
//                IsMediaPlayer.isplay(musicurl);
                int i2 = richanglist[i1];
                Uri setDataSourceuri = Uri.parse("android.resource://com.dgcheshang.cheji/"+i2);
                IsMediaPlayer.isplay1(mContent,setDataSourceuri);
            }
        });
    }

    /**
     * 获取控件
     * */
    @Override
    public int getItemCount() {
        return tvcontent.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        TextView tv_list_name;
        View layout_list;
        ImageView image_list_icon;
        MyItemClickListener myItemClick;
        MyItemLongClickListener myItemLongClick;
        public ViewHolder(View view ,MyItemClickListener myItemClickListener, MyItemLongClickListener myItemLongClickListener) {
            super(view);
            tv_list_name = (TextView) view.findViewById(R.id.tv_list_name);
            image_list_icon = (ImageView) view.findViewById(R.id.image_list_icon);
            layout_list = view.findViewById(R.id.layout_list);
            this.myItemClick = myItemClickListener;
            this.myItemLongClick = myItemLongClickListener;
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (myItemClick != null) {
                myItemClick.onItemClick(v, getPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (myItemLongClick != null) {
                myItemLongClick.onItemLongClick(v, getPosition());
            }
            return false;
        }
    }

    public interface MyItemClickListener{
        void onItemClick(View V, int Position);
    }

    public interface MyItemLongClickListener{
        void onItemLongClick(View V, int Position);
    }

    public void setMyItemClickListener(MyItemClickListener myItemClickListener) {
        this.myItemClickListener = myItemClickListener;
    }

    public void setMyItemLongClickListener(MyItemLongClickListener myItemLongClickListener) {
        this.myItemLongClickListener = myItemLongClickListener;
    }

}
