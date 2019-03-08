package com.dgcheshang.cheji.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dgcheshang.cheji.Activity.DzwlActivity;
import com.dgcheshang.cheji.R;
import com.dgcheshang.cheji.netty.po.Dzwl;

import java.util.ArrayList;

/**
 *电子围栏adapter
 */

public class DzwlAdapter extends RecyclerView.Adapter<DzwlAdapter.ViewHolder> {
    MyItemClickListener myItemClickListener;
    MyItemLongClickListener myItemLongClickListener;
    Context mContent;
    ArrayList<Dzwl> dzwllist;

    public DzwlAdapter(DzwlActivity dzwlActivity, ArrayList<Dzwl> querydzwl) {
        this.mContent=dzwlActivity;
        this.dzwllist=querydzwl;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContent).inflate(R.layout.dzwl_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view,myItemClickListener,myItemLongClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Dzwl dzwl = dzwllist.get(position);
        holder.tv_bianhao.setText(dzwl.getCdbh());
        holder.tv_content.setText(dzwl.getCdmc());
        holder.tv_address.setText(dzwl.getCddz());
    }

    @Override
    public int getItemCount() {
        if(dzwllist!=null){
            return dzwllist.size();
        }else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        MyItemClickListener myItemClick;
        MyItemLongClickListener myItemLongClick;
        TextView tv_content;
        TextView tv_bianhao;
        TextView tv_address;
        public ViewHolder(View view, MyItemClickListener myItemClickListener, MyItemLongClickListener myItemLongClickListener) {
            super(view);
            tv_content = (TextView) view.findViewById(R.id.tv_content);//名称
            tv_bianhao = (TextView) view.findViewById(R.id.tv_bianhao);//编号
            tv_address = (TextView) view.findViewById(R.id.tv_address);//地址
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
