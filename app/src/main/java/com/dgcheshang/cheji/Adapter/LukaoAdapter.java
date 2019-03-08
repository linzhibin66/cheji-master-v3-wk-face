package com.dgcheshang.cheji.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.dgcheshang.cheji.Activity.Lukao.LightingDetailActivity;
import com.dgcheshang.cheji.Activity.Lukao.LukaoListActivity;
import com.dgcheshang.cheji.Activity.Lukao.LukaoListDetailActivity;
import com.dgcheshang.cheji.R;
import com.dgcheshang.cheji.Tools.IsMediaPlayer;


/**
 *路考adapter
 */

public class LukaoAdapter extends RecyclerView.Adapter<LukaoAdapter.ViewHolder> {
    MyItemClickListener myItemClickListener;
    MyItemLongClickListener myItemLongClickListener;
    Context mContent;
    String[] list;
    int[] imagelist;
    String who;
    boolean ischeckbox;
    int[] richanglist={R.raw.lukao13,R.raw.lukao14,R.raw.lukao15,R.raw.lukao16,R.raw.lukao17,R.raw.lukao18,R.raw.lukao19,R.raw.lukao20,R.raw.lukao21,R.raw.lukao22,R.raw.lukao23,R.raw.lukao24,R.raw.lukao25,R.raw.lukao26,R.raw.lukao27,R.raw.lukao28,R.raw.lukao29,R.raw.lukao30,R.raw.lukao31,R.raw.lukao32};
    public LukaoAdapter(LukaoListActivity lukaoListActivity, String[] list, int[] image, String biaoji, boolean ischeckbox) {
        this.mContent=lukaoListActivity;
        this.list=list;
        this.imagelist=image;
        this.who=biaoji;
        this.ischeckbox=ischeckbox;
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

        holder.tv_list_name.setText(list[position]);
        if(who.equals("denguang")) {
            holder.icon.setBackgroundResource(imagelist[0]);
        }else {
            holder.icon.setBackgroundResource(imagelist[position]);
        }
        holder.layout_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ischeckbox){
                    Intent intent = new Intent();
//                intent.putExtra("position",position);
                    if(who.equals("denguang")){
                        intent.setClass(mContent, LightingDetailActivity.class);
                        intent.putExtra("position",position);
                        intent.putExtra("title",list[position]);
                    }else {
                        intent.setClass(mContent, LukaoListDetailActivity.class);
                        intent.putExtra("position",position);
                        intent.putExtra("title",list[position]);
                    }

                    intent.putExtra("who",who);//richang,denguang
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mContent.startActivity(intent);
                }else {
                    //日常才报读。灯光必须进入详情
                    if(who.equals("richang")) {
//                        IsMediaPlayer.isRelease();
//                        String url="/mnt/sdcard/chejidoal/lukao"+(position+13)+ ".ogg";
//                        IsMediaPlayer.isplay(url);

                        IsMediaPlayer.isRelease();
                        int i1 = richanglist[position];
                        Uri setDataSourceuri = Uri.parse("android.resource://com.dgcheshang.cheji/"+i1);
                        IsMediaPlayer.isplay1(mContent,setDataSourceuri);
                    }else {
                        //
                        Intent intent = new Intent();
                        intent.setClass(mContent, LightingDetailActivity.class);
                        intent.putExtra("position",position);
                        intent.putExtra("title",list[position]);
                        intent.putExtra("who",who);//richang,denguang
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        mContent.startActivity(intent);
                    }

                }

            }
        });

    }

    /**
     * 获取控件
     * */
    @Override
    public int getItemCount() {
        return list.length;

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        ImageView icon;
        TextView tv_list_name;
        View layout_list;
        MyItemClickListener myItemClick;
        MyItemLongClickListener myItemLongClick;
        public ViewHolder(View view ,MyItemClickListener myItemClickListener, MyItemLongClickListener myItemLongClickListener) {
            super(view);
            icon = (ImageView) view.findViewById(R.id.image_list_icon);//图标
            tv_list_name = (TextView) view.findViewById(R.id.tv_list_name);//名称
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
        void onItemClick(View V,int Position);
    }

    public interface MyItemLongClickListener{
        void onItemLongClick(View V,int Position);
    }

    public void setMyItemClickListener(MyItemClickListener myItemClickListener) {
        this.myItemClickListener = myItemClickListener;
    }

    public void setMyItemLongClickListener(MyItemLongClickListener myItemLongClickListener) {
        this.myItemLongClickListener = myItemLongClickListener;
    }

}
