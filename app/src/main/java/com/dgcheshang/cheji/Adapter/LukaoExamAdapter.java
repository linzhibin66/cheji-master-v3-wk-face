package com.dgcheshang.cheji.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.dgcheshang.cheji.Activity.Lukao.LineDetailActivity;
import com.dgcheshang.cheji.Activity.Lukao.StartExamActivity;
import com.dgcheshang.cheji.Database.DbHandle;
import com.dgcheshang.cheji.R;
import com.dgcheshang.cheji.netty.conf.NettyConf;
import com.dgcheshang.cheji.netty.po.Line;
import com.dgcheshang.cheji.netty.timer.LineTimerTask;
import com.dgcheshang.cheji.netty.util.ZdUtil;

import java.util.ArrayList;
import java.util.Timer;

/**
 *路考_模拟考试路线adapter
 */

public class LukaoExamAdapter extends RecyclerView.Adapter<LukaoExamAdapter.ViewHolder> {
    MyItemClickListener myItemClickListener;
    MyItemLongClickListener myItemLongClickListener;
    Context mContent;
    ArrayList<Line> list;
    String[] namelist;
    SharedPreferences lukaosp;
    int mSelectedItem=-1;
    int[] imagelist;

    public LukaoExamAdapter(Context context, ArrayList<Line> list, String[] namelist, SharedPreferences lukaosp, int[] imagelist) {
        this.mContent=context;
        this.list=list;
        this.namelist=namelist;
        this.lukaosp=lukaosp;
        this.imagelist=imagelist;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContent).inflate(R.layout.lukao_exam_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view,myItemClickListener,myItemLongClickListener);
        return holder;
    }
    /**
     * 操作
     * */
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Line line = list.get(position);
        final String mc = line.getMc();
        holder.tv_name.setText(mc);
        holder.tv_linenumber.setText("线路"+(position+1)+"：");
        String linename = lukaosp.getString("linename", "");
        if(!linename.isEmpty()&&linename.equals(mc)){
            mSelectedItem=position;
        }
        holder.rb_choose.setChecked(position==mSelectedItem);
        holder.bt_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContent,LineDetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("list", list.get(position));
                intent.putExtra("namelist",namelist);
                intent.putExtra("imagelist",imagelist);
                intent.putExtra("isexam",false);
                mContent.startActivity(intent);
            }
        });
    }



    /**
     * 获取控件
     * */
    @Override
    public int getItemCount() {
        if(list!=null){
            return list.size();
        }else {
            return 0;
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        TextView tv_name;
        MyItemClickListener myItemClick;
        MyItemLongClickListener myItemLongClick;
        RadioButton rb_choose;
        Button bt_detail;
        TextView tv_linenumber;
        public ViewHolder(View view ,MyItemClickListener myItemClickListener, MyItemLongClickListener myItemLongClickListener) {
            super(view);
            tv_name = (TextView) view.findViewById(R.id.tv_name);//路线名称
            rb_choose = (RadioButton) view.findViewById(R.id.rb_choose);
            bt_detail = (Button) view.findViewById(R.id.bt_detail);//查看详情
            tv_linenumber = (TextView) view.findViewById(R.id.tv_linenumber);//线路标志
            this.myItemClick = myItemClickListener;
            this.myItemLongClick = myItemLongClickListener;
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            rb_choose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectedItem = getAdapterPosition();
                    SharedPreferences.Editor edit = lukaosp.edit();
                    Line line = list.get(mSelectedItem);
                    String mc = line.getMc();
                    edit.putString("linename",mc);//保存线路名称
                    edit.commit();
                    notifyItemRangeChanged(0, list.size());
                    Intent intent = new Intent();
                    intent.setClass(mContent,StartExamActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("list", list.get(mSelectedItem));
//                    NettyConf.selectedLine=list.get(mSelectedItem);
                    intent.putExtra("namelist",namelist);
                    intent.putExtra("imagelist",imagelist);
                    mContent.startActivity(intent);
                }
            });
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

    /**
     * 选择路线Dialog
     * @param mc
     * @param line
     * @param position
     * @param bt_delete      */
    private void chooseDialog(final String mc, final Line line, final int position, final Button bt_delete) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(mContent);
        dialog.setTitle("提示");
        dialog.setMessage("确定要培训本条线路吗?");
        dialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if(NettyConf.xltimer!=null){
                            NettyConf.xltimer.cancel();
                            NettyConf.xltimer=null;
                        }
                        NettyConf.line=line;
                        NettyConf.xltimer = new Timer();
                        LineTimerTask lineTask = new LineTimerTask(true,mContent);
                        NettyConf.xltimer.schedule(lineTask,0,1000);
                        SharedPreferences.Editor edit = lukaosp.edit();
                        edit.putString("position",position+"");
                        edit.commit();
                    }
                });
        dialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        // 显示
        dialog.show();

    }

}
