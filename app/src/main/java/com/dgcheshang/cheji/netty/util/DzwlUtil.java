package com.dgcheshang.cheji.netty.util;

import android.location.Location;
import android.util.Log;

import com.dgcheshang.cheji.Database.DbHandle;
import com.dgcheshang.cheji.Tools.Speaking;
import com.dgcheshang.cheji.netty.conf.NettyConf;
import com.dgcheshang.cheji.netty.po.Dzwl;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/8/14.
 */

public class DzwlUtil {
    public static List<Dzwl> dlist=null;
    public static Dzwl item=null;//电子围栏

    //实例化电子围栏
    private static List<Dzwl> getInstance(){
        if(dlist==null){
            dlist=DbHandle.querydzwl("select * from dzwl",null);
        }
        return dlist;
    }

    /**
     *  0:无电子围栏  1区域内  2  区域外
     * @param latitude
     * @param longtitude
     * @return
     */
    public static String getDzwl(long latitude, long longtitude){
        item=null;
        String rs="0";
        if(NettyConf.debug) {
            Log.e("TAG", "获取场地");
        }
        getInstance();

        if(dlist.size()>0){
            for(Dzwl dzwl:dlist){
                String zbd=dzwl.getZbd();
                String[] zbds=zbd.split(";");
                int pnum=zbds.length;
                long[] lat = new long[pnum];
                long[] longt = new long[pnum];
                for(int i=0;i<pnum;i++){
                    if(StringUtils.isNotEmpty(zbds[i])){
                        String[] ss=zbds[i].split(",");
                        lat[i]=(long)(Double.valueOf(ss[1])*1);
                        longt[i]=(long)(Double.valueOf(ss[0])*1);
                    }
                }

                int sum = 0;
                long x0;
                long y0;
                long x1;
                long y1;
                long x;
                long y;
                y = latitude;
                for (int index = 0; index < pnum; index++) {
                    if (index == pnum - 1) {
                        x0 = longt[index];
                        y0 = lat[index];
                        x1 = longt[0];
                        y1 = lat[0];
                    } else {
                        x0 = longt[index];
                        y0 = lat[index];
                        x1 = longt[index + 1];
                        y1 = lat[index + 1];
                    }
                    if (((y >= y0) && (y < y1)) || ((y >= y1) && (y < y0))) {
                        if (Math.abs(y0 - y1) > 0) {
                            //得到 A点向左射线与边的交点的x坐标：
                            x = x0 - ((x0 - x1) * (y0 - latitude)) / (y0 - y1);
                            if (x < longtitude)
                                sum++;
                        }
                    }
                }


                if (sum % 2 != 0) {
                    item=dzwl;
                    break;
                }

            }

            if(item==null){
                rs="2";
            }else{
                rs="1";
            }
        }

        return rs;

    }

    /**
     * 判断坐标点是否在电子围栏里
     * @return
     */
    public static boolean pdZbd(long latitude, long longtitude){
        if(item!=null) {
            String zbd = item.getZbd();
            String[] zbds = zbd.split(";");
            int pnum = zbds.length;
            long[] lat = new long[pnum];
            long[] longt = new long[pnum];
            for (int i = 0; i < pnum; i++) {
                if (StringUtils.isNotEmpty(zbds[i])) {
                    String[] ss = zbds[i].split(",");
                    lat[i] = (long)(Double.valueOf(ss[1])*1000000);
                    longt[i] =(long)(Double.valueOf(ss[0])*1000000);
                }
            }

            int sum = 0;
            long x0;
            long y0;
            long x1;
            long y1;
            long x;
            long y;
            y = latitude;
            for (int index = 0; index < pnum; index++) {
                if (index == pnum - 1) {
                    x0 = longt[index];
                    y0 = lat[index];
                    x1 = longt[0];
                    y1 = lat[0];
                } else {
                    x0 = longt[index];
                    y0 = lat[index];
                    x1 = longt[index + 1];
                    y1 = lat[index + 1];
                }
                if (((y >= y0) && (y < y1)) || ((y >= y1) && (y < y0))) {
                    if (Math.abs(y0 - y1) > 0) {
                        //得到 A点向左射线与边的交点的x坐标：
                        x = x0 - ((x0 - x1) * (y0 - latitude)) / (y0 - y1);
                        if (x < longtitude)
                            sum++;
                    }
                }
            }


            if (sum % 2 != 0) {
                return true;
            }else{
                return false;
            }
        }else{
            return true;
        }
    }

    public static String getDzwlByLocation(){
        Location location=NettyConf.location;
       return getDzwl((long)(location.getLatitude()*1000000),(long)(location.getLongitude()*1000000));
    }

    /*public static String pdDzwlByLocation(){
        if("0".equals(NettyConf.dzwlcl)||!LocationUtil.state||"3".equals(NettyConf.pxkc.substring(3,4))){
            jcqy="0";
            return "0";
        }else{
            Location location=NettyConf.location;
            boolean fg=getDzwl((long)(location.getLatitude()*1000000),(long)(location.getLongitude()*1000000));
            if((fg&&"1".equals(jcqy))||(!fg&&"0".equals(jcqy))){
                if(fg){
                    jcqy="0";
                    Speaking.in("进入培训场地");
                }else{
                    jcqy="1";
                    Speaking.in("离开培训场地");
                }
                return "1";
            }else{
                return "0";
            }
        }
    }*/
}
