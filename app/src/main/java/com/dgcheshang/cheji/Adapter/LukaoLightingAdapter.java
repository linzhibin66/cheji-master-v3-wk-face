package com.dgcheshang.cheji.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.dgcheshang.cheji.Activity.Lukao.LightingDetailActivity;
import com.dgcheshang.cheji.Bean.LightingBean;
import com.dgcheshang.cheji.R;
import com.dgcheshang.cheji.Tools.IsMediaPlayer;

import java.util.List;

/**
 *路考adapter
 */

public class LukaoLightingAdapter extends RecyclerView.Adapter<LukaoLightingAdapter.ViewHolder> {
    MyItemClickListener myItemClickListener;
    MyItemLongClickListener myItemLongClickListener;
    Context mContent;
    List<LightingBean> list;
    //图标list
    int[] iconlist;
    //报读list
    int[] voicelist;
    //选择哪个进来的
    int whoposition;
     public LukaoLightingAdapter(LightingDetailActivity lightingDetailActivity, List<LightingBean> list, int[] iconlist, int[] voicelist, int position) {
        this.mContent=lightingDetailActivity;
        this.list=list;
         this.iconlist=iconlist;
         this.voicelist=voicelist;
         this.whoposition=position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContent).inflate(R.layout.lukao_lighting_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view,myItemClickListener,myItemLongClickListener);
        return holder;
    }

    /**
     * 操作
     * */
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.tv_list_name.setText(list.get(position).getName());
        holder.tv_list_do.setText(list.get(position).getDoing());
        holder.tv_number.setText(String.valueOf(position+1));//编号
        holder.icon.setBackgroundResource(iconlist[Integer.parseInt(list.get(position).getIcon())]);
        holder.layout_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(whoposition==5){

                    IsMediaPlayer.isRelease();
//                    String url="";
//                    url="/mnt/sdcard/chejidoal/lukao"+(position+1)+ ".ogg";
                    int i1 = voicelist[Integer.parseInt(list.get(position).getVoice())];
                    Uri setDataSourceuri = Uri.parse("android.resource://com.dgcheshang.cheji/"+i1);
                    IsMediaPlayer.isplay1(mContent,setDataSourceuri);
                }

            }
        });
    }

    /**
     * 获取控件
     * */
    @Override
    public int getItemCount() {
        return list.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        ImageView icon;
        TextView tv_list_name;
        TextView tv_list_do;
        TextView tv_number;
        View layout_list;
        MyItemClickListener myItemClick;
        MyItemLongClickListener myItemLongClick;
        public ViewHolder(View view ,MyItemClickListener myItemClickListener, MyItemLongClickListener myItemLongClickListener) {
            super(view);
            icon = (ImageView) view.findViewById(R.id.image_list_icon);//图标
            tv_list_name = (TextView) view.findViewById(R.id.tv_list_name);//名称
            tv_list_do = (TextView) view.findViewById(R.id.tv_list_do);//如何操作
            tv_number = (TextView) view.findViewById(R.id.tv_number);//编号
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
