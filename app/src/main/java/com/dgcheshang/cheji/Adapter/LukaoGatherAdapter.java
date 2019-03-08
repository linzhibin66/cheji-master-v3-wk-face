package com.dgcheshang.cheji.Adapter;


import android.content.Context;
import android.location.Location;
import android.media.SoundPool;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dgcheshang.cheji.Activity.Lukao.LukaoGatherActivity;
import com.dgcheshang.cheji.Bean.LineBean;
import com.dgcheshang.cheji.Bean.LukaoBean;
import com.dgcheshang.cheji.R;
import com.dgcheshang.cheji.Tools.Speaking;
import com.dgcheshang.cheji.netty.util.CountDistance;
import com.dgcheshang.cheji.netty.util.LocationUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;



/**
 *路线采集adapter
 */

public class LukaoGatherAdapter extends RecyclerView.Adapter<LukaoGatherAdapter.ViewHolder> {
    MyItemClickListener myItemClickListener;
    MyItemLongClickListener myItemLongClickListener;
    Context mContent;
    String[] lukaoname;
    int[] imagelist;
    int[] state= new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    SoundPool soundPool;
    ArrayList arlist =new ArrayList();
    TextView tv_lon,tv_lat;


    public LukaoGatherAdapter(LukaoGatherActivity lukaoGatherActivity, String[] lukaoname, int[] image, SoundPool soundPool, TextView tv_lon, TextView tv_lat) {
        this.mContent=lukaoGatherActivity;
        this.lukaoname=lukaoname;
        this.imagelist=image;
        this.soundPool=soundPool;
        this.tv_lat=tv_lat;
        this.tv_lon=tv_lon;
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
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.tv_list_name.setText(lukaoname[position]);
        holder.icon.setBackgroundResource(imagelist[position]);
        holder.layout_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(state[position]==0&&position!=0&&position!=19){
                    //未点击过
                    Location location = LocationUtil.getNewGps();
                    if(location!=null){
                        //定位成功
                        double longitude = location.getLongitude();
                        double latitude = location.getLatitude();

                        if(arlist.size()>0){
                            //有保存过采点
                            holder.layout_list.setBackgroundResource(R.color.orange);
                            caidian(position,longitude,latitude,lukaoname);
                        }else {
                            //没有保存过采点
                            holder.layout_list.setBackgroundResource(R.color.orange);
                            caidian(position,longitude,latitude,lukaoname);
                        }

                    }else {
                        //定位失败
                        Speaking.in("定位失败，暂时不能采点");
                        Toast.makeText(mContent,"定位失败，暂时不能采点",Toast.LENGTH_SHORT).show();
                    }

                }else {
                    if(position==0||position==19){
                        Toast.makeText(mContent,"该点不用采集",Toast.LENGTH_SHORT).show();
                    }else {
                        //已点击过
                        soundPool.play(1, 1, 1, 0, 0, 1);//嘀声音
                        //取消描点
                        holder.layout_list.setBackgroundResource(R.color.transparent);
                        state[position]=0;
                        for (int i=0;i<arlist.size();i++){
                            LineBean bean = (LineBean) arlist.get(i);
                            int type = bean.getType();
                            if(type==position){
                                arlist.remove(i);
                                return;
                            }
                        }
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
        return imagelist.length;
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
     * 将保存的list拼成String
     * */
    public String savelist(){
        StringBuffer buffer = new StringBuffer();
        for(int i=0;i<arlist.size();i++){
            LineBean bean = (LineBean) arlist.get(i);
             String type= String.valueOf(bean.getType());//序号
            String lat = bean.getLat();//维度
            String lon = bean.getLon();//经度
            String line=i+","+type+","+lat+","+lon+";";
            buffer.append(line);
        }
        return buffer.toString();
    }

    /**
     * 清除保存的list
     * */
    public void clearList(){
        arlist.clear();
        for(int i=0;i<state.length;i++){
          state[i]=0;
        }

    }
    /**
     * 获取arraylist长度
     * */
    public int getarlistsize(){
        return arlist.size();
    }

    /**
     * 采点保存
     * */
    public void caidian(int position, double longitude, double latitude, String[] lukaoname){
        soundPool.play(1, 1, 1, 0, 0, 1);//嘀声音
        state[position]=1;
        LineBean linebean = new LineBean();
        linebean.setType(position);
        linebean.setName(lukaoname[position]);
        DecimalFormat df = new DecimalFormat(".0000000");
        String lat=String.valueOf(df.format(latitude));
        String lon=String.valueOf(df.format(longitude));
        linebean.setLon(lon);
        linebean.setLat(lat);
        tv_lat.setText(lat);
        tv_lon.setText(lon);
        arlist.add(linebean);
    }
}
