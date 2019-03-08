package com.dgcheshang.cheji.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dgcheshang.cheji.R;

import java.util.ArrayList;

/**
 *开始路考页面adapter
 */

public class StartExamAdapter extends RecyclerView.Adapter<StartExamAdapter.ViewHolder> {
    MyItemClickListener myItemClickListener;
    MyItemLongClickListener myItemLongClickListener;
    Context context;
    String[] namelist;
    ArrayList arrayList;
    ArrayList posList;
    int isstart;
    public StartExamAdapter(Context context, String[] namelist, ArrayList list, ArrayList posList, int isstart) {
        this.context=context;
        this.namelist=namelist;
        this.arrayList=list;
        this.posList=posList;
        this.isstart=isstart;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.startexam_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view,myItemClickListener,myItemLongClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String  str = (String) arrayList.get(position);
        String[] split = str.split(",");
        String s2 = split[1];
        int i1 = Integer.parseInt(s2);
        s2 = namelist[i1];
        holder.tv_content.setText(s2);
        if(isstart==0){
            holder.layout_content.setBackgroundResource(R.color.green);
        }
        if(posList.size()>0){
            for(int i=0;i<posList.size();i++){
                int bgpos=(int)posList.get(i);
                if(bgpos == i1){
                    holder.layout_content.setBackgroundResource(R.color.orange);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
       return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        MyItemClickListener myItemClick;
        MyItemLongClickListener myItemLongClick;
        TextView tv_content;
        View layout_content;
        public ViewHolder(View view, MyItemClickListener myItemClickListener, MyItemLongClickListener myItemLongClickListener) {
            super(view);
            tv_content = (TextView) view.findViewById(R.id.tv_content);//名称显示
            layout_content = view.findViewById(R.id.layout_content);//名称布局

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
