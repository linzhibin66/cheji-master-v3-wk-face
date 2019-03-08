package com.dgcheshang.cheji.netty.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.dgcheshang.cheji.CjApplication;
import com.dgcheshang.cheji.Database.MyDatabase;
import com.dgcheshang.cheji.Tools.GetSystemInfoUtil;
import com.dgcheshang.cheji.netty.certificate.Certificate;
import com.dgcheshang.cheji.netty.conf.NettyConf;
import com.dgcheshang.cheji.netty.conf.SetZdcs;
import com.dgcheshang.cheji.netty.timer.InitTimer;
import com.dgcheshang.cheji.netty.timer.XsjlTimer;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Timer;

/**
 * Created by Administrator on 2017/7/19.
 */

public class InitUtil {

    //初始化
    public static void initSystem(){
        getDeviceId();
        setCS();
        setNum();

        new LocationUtil().getGPS();
        //点火熄火监听
        new AccUtil();

        new Timer().schedule(new InitTimer(),10000);
    }

    /**
     * 获取设备基本信息
     * */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void getDeviceId(){
        TelephonyManager tm = (TelephonyManager) CjApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
//        String IMEI = tm.getDeviceId();
        Map<String, String> map = GetSystemInfoUtil.getImeiAndMeid(CjApplication.getInstance());
        String IMEI=map.get("imei2");
        String line1Number = tm.getLine1Number();//电话号码
        String model= android.os.Build.MODEL;//型号
        String xlh=android.os.Build.SERIAL;
        String mf = Build.MANUFACTURER;//制造商
        String simSn = tm.getSimSerialNumber();//sim卡序列号
        Log.e("TAG","型号"+model);
        if(NettyConf.debug){
            Log.e("TAG","IMEI="+IMEI+",电话号码="+line1Number+",model ="+model+",zzsid ="+mf+",序列号="+xlh+"---"+simSn);
        }

        if(StringUtils.isNotEmpty(model)) {
            NettyConf.model = model;
            if(NettyConf.debug){
                Log.e("TAG","修改后的终端型号："+NettyConf.model);
            }
        }
        if(StringUtils.isNotEmpty(simSn)){
            NettyConf.SIMxlh=simSn;
        }

        if(StringUtils.isNotEmpty(line1Number)&&StringUtils.isEmpty(NettyConf.mobile)){
            NettyConf.mobile=line1Number;
        }
        if(StringUtils.isNotEmpty(xlh)){
            if(xlh.length()>7){
                NettyConf.zdxlh=xlh.substring(xlh.length()-7,xlh.length());
            }else{
                NettyConf.zdxlh=xlh;
            }
        }
        NettyConf.imei=IMEI;
        String versionName = getVersionName(CjApplication.getInstance());//获取版本号
        NettyConf.version=versionName;

    }

    /**
     * 获取app的VersionName
     * */
    public  static String getVersionName(Context context){
        PackageManager packageManager=context.getPackageManager();
        PackageInfo packageInfo;
        String versionName="";
        try {
            packageInfo=packageManager.getPackageInfo(context.getPackageName(),0);
            versionName=packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 赋值给静态
     * */

    public static void setNum(){
        //获取鉴权成功后的sp。
        SharedPreferences sp = CjApplication.getInstance().getSharedPreferences("jianquan", Context.MODE_PRIVATE);
        String ptbh = sp.getString("ptbh", "");//平台编号
        String pxjgbh = sp.getString("pxjgbh", "");//培训机构编号
        String jszdbh = sp.getString("jszdbh", "");//终端统一编号
        String zs = sp.getString("zs", "");//证书
        String zskl = sp.getString("zskl", "");//证书口令
        int zcstate = sp.getInt("zcstate", 0);//注册状态

        NettyConf.ptbh=ptbh;
        NettyConf.pxjgbh=pxjgbh;
        NettyConf.jszdbh=jszdbh;
        NettyConf.zs=zs;
        NettyConf.zskl=zskl;
        if(StringUtils.isNotEmpty(zs)&&StringUtils.isNotEmpty(zskl)){
            NettyConf.key = Certificate.getPrivateKey(zs, zskl.toCharArray());
        }
        NettyConf.zcstate=zcstate;

        //终端参数赋值
        SharedPreferences zdcssp = CjApplication.getInstance().getSharedPreferences("zdcs", Context.MODE_PRIVATE);
        String host = zdcssp.getString("0013", "");//ip
        String port = zdcssp.getString("0018", "");//tcp端口
        String cp = zdcssp.getString("0083", "");//车牌
        String shengID = zdcssp.getString("0081", "44");//省域
        String shiID = zdcssp.getString("0082", "1900");//市域
        String cpys = zdcssp.getString("0084", "2");//车牌颜色
        String phone = zdcssp.getString("0048", "");//手机号码
        String xtjg = zdcssp.getString("0001", "30");
        String cfjg = zdcssp.getString("0002", "30");
        String cfcs=zdcssp.getString("0003", "3");
        String dwfsjg=zdcssp.getString("0029", "20");
        String dwfsjg2=zdcssp.getString("0022", "600");
        String mintime=zdcssp.getString("0004", "050000");//最早登陆时间
        String maxtime=zdcssp.getString("0005", "235500");//最晚登陆时间
        String jrtime=zdcssp.getString("0058", "14400");//今日最长培训时长
        String dzwlcl=zdcssp.getString("0006", "0");
        String sfjm=zdcssp.getString("0007", "0");
        String dzwllxsj=zdcssp.getString("0010", "5");

        NettyConf.host=host;
        NettyConf.port=Integer.valueOf(port);
        NettyConf.cp=cp;
        NettyConf.cpys=cpys;
        NettyConf.shengID=shengID;
        NettyConf.shiID=shiID;
        NettyConf.mobile=phone;

        NettyConf.xtjg=Integer.valueOf(xtjg);//心跳间隔
        NettyConf.cfjg=Integer.valueOf(cfjg);//重发间隔
        NettyConf.cfcs=Integer.valueOf(cfcs);//重传次数
        NettyConf.dwfsjg=Integer.valueOf(dwfsjg);//定位信息发送间隔
        NettyConf.dwfsjg2=Integer.valueOf(dwfsjg2);//教练未登陆发送时间间隔

        NettyConf.minTime=Integer.valueOf(mintime);
        NettyConf.maxTime=Integer.valueOf(maxtime);
        NettyConf.dtlxlong=Integer.valueOf(jrtime)/60;
        NettyConf.dzwlcl=dzwlcl;//电子围栏策略
        NettyConf.dzwllxsj=Integer.parseInt(dzwllxsj);
        if(sfjm.equals("0")){
            NettyConf.sfjm1="0";
            NettyConf.sfjm2="0";
        }else{
            NettyConf.sfjm1="1";
            NettyConf.sfjm2="2";
        }

        //应用参数设置
        SharedPreferences yycssp = CjApplication.getInstance().getSharedPreferences("yycs", Context.MODE_PRIVATE);
        String makephotojg=yycssp.getString("1","15");
        NettyConf.makephotojg=Integer.parseInt(makephotojg)*60;//拍照发送间隔时间
        String cxyzsj=yycssp.getString("7","30");
        NettyConf.cxyzsj=Integer.parseInt(cxyzsj);
        NettyConf.dwfsjg3=Integer.valueOf(yycssp.getString("5","3600"));

        //教练赋值
        SharedPreferences coachsp = CjApplication.getInstance().getSharedPreferences("coach", Context.MODE_PRIVATE);
        int jlstate = coachsp.getInt("jlstate", 0);
        String jlbh = coachsp.getString("jlbh", "");
        String cx = coachsp.getString("cx", "");
        String jzjhm = coachsp.getString("jzjhm", "");
        if(!jlbh.equals("")){
            NettyConf.jbh=jlbh;
            NettyConf.jlstate=jlstate;
            NettyConf.cx=cx;
            NettyConf.jzjhm=jzjhm;
        }

        //学员赋值
        SharedPreferences stusp = CjApplication.getInstance().getSharedPreferences("student", Context.MODE_PRIVATE);
        String xybh = stusp.getString("xybh", "");
        int xystate = stusp.getInt("xystate", 0);
        String ktid = stusp.getString("ktid", "0");
        String xydltime = stusp.getString("xydltime", "");//学员登录时间
        String jrxs=stusp.getString("jrxs","0");
        int fzpxjlsc=stusp.getInt("fzpxjlsc",0);
        boolean sendState = stusp.getBoolean("sendState", true);
        NettyConf.sendState=sendState;
        if(!xybh.equals("")){
            NettyConf.xbh=xybh;
            NettyConf.xystate=xystate;
            NettyConf.ktid=ktid;
            NettyConf.xydltime=xydltime;
            NettyConf.jrxxsc=Integer.valueOf(jrxs);
            XsjlTimer.fzpxjlsc=fzpxjlsc;
        }

        //禁训状态
        SharedPreferences jxsp = CjApplication.getInstance().getSharedPreferences("jxzt", Context.MODE_PRIVATE);
        String jxzt = jxsp.getString("jxzt", "1");
        NettyConf.jxzt=Integer.valueOf(jxzt);

        SharedPreferences restartsp = CjApplication.getInstance().getSharedPreferences("restartspinner", Context.MODE_PRIVATE);
        NettyConf.autoroot=restartsp.getInt("restart",2);
    }

    /**
     * 设置参数
     * */
    public static void setCS(){
        SetZdcs setZdcs = new SetZdcs();
        SharedPreferences zdcssp = CjApplication.getInstance().getSharedPreferences("zdcs", Context.MODE_PRIVATE);
        String isset = zdcssp.getString("isset", "");
        if(!isset.equals("true")){
            setZdcs.setZdcs(CjApplication.getInstance());
        }
        SharedPreferences yycssp = CjApplication.getInstance().getSharedPreferences("yycs", Context.MODE_PRIVATE);
        String isset1 = yycssp.getString("isset", "");
        if(!isset1.equals("true")){
            setZdcs.setYycs(CjApplication.getInstance());
        }

        SharedPreferences jxzt = CjApplication.getInstance().getSharedPreferences("jxzt", Context.MODE_PRIVATE);
        String isset2 = jxzt.getString("isset", "");
        if(!isset2.equals("true")){
            setZdcs.setJxzt(CjApplication.getInstance());
        }
    }

}
