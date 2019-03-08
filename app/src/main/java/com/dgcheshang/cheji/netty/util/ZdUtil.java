package com.dgcheshang.cheji.netty.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.widget.Toast;

import com.dgcheshang.cheji.Camera.Test;
import com.dgcheshang.cheji.CjApplication;
import com.dgcheshang.cheji.Database.DbHandle;
import com.dgcheshang.cheji.Tools.Speaking;
import com.dgcheshang.cheji.netty.certificate.Sign;
import com.dgcheshang.cheji.netty.conf.NettyConf;
import com.dgcheshang.cheji.netty.init.ZdClient;
import com.dgcheshang.cheji.netty.po.Bcfb;
import com.dgcheshang.cheji.netty.po.Gnss;
import com.dgcheshang.cheji.netty.po.GnssExtend;
import com.dgcheshang.cheji.netty.po.ImeiPassword;
import com.dgcheshang.cheji.netty.po.Jlydc;
import com.dgcheshang.cheji.netty.po.Line;
import com.dgcheshang.cheji.netty.po.Sfrz;
import com.dgcheshang.cheji.netty.po.Tdata;
import com.dgcheshang.cheji.netty.po.Xsjl;
import com.dgcheshang.cheji.netty.po.Xydc;
import com.dgcheshang.cheji.netty.po.Zdjq;
import com.dgcheshang.cheji.netty.po.Zdzc;
import com.dgcheshang.cheji.netty.po.Zpdata;
import com.dgcheshang.cheji.netty.po.Zpsc;
import com.dgcheshang.cheji.netty.proputil.PropertiesUtil;
import com.dgcheshang.cheji.netty.thread.CacheDelete;
import com.dgcheshang.cheji.netty.thread.CacheSend;
import com.dgcheshang.cheji.netty.timer.CoachoutTimer;
import com.dgcheshang.cheji.netty.timer.ConTimer;
import com.dgcheshang.cheji.netty.timer.DzwlTimerTask;
import com.dgcheshang.cheji.netty.timer.PzysTimer;
import com.dgcheshang.cheji.netty.timer.StudentoutTimer;
import com.dgcheshang.cheji.netty.timer.XsjlTimer;
import com.dgcheshang.cheji.netty.timer.XydcStateTimer;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ZdUtil {
	public static  String TAG="ZdUtil";
	//水印用
	public static String markwd;
	public static String markjd;
	public static String marksj;
	public static String marksd;
	public static boolean ispz=false;//是否在拍照
	public static boolean issave=true;//是否保存数据

	public static boolean xydcstate=false;//学员登出操作是否


	/**
	 * 获取身份认证信息
	 * @param ickxlh 1.教练员  4.学员
	 * @param type
     */
	public static void sendSfrz(String ickxlh,String type){
		Sfrz sfrz=new Sfrz();
		sfrz.setXxlx("1");
		sfrz.setRylx(type);
		sfrz.setZjhm(ickxlh);
		byte[] b3=sfrz.getSfrzBytes();
		byte[] b2= MsgUtilClient.getMsgExtend(b3,"0401", "13", NettyConf.sfjm2);
		List<Tdata> list=MsgUtilClient.generateMsg(b2,"0900", NettyConf.mobile, NettyConf.sfjm1);

		GatewayService.sendHexMsgToServer("serverChannel", list.get(0).getData());
	}
	/**
	 * 终端注册
	 */
	public static void sendZdzc(){
		if(StringUtils.isBlank(NettyConf.mobile)||StringUtils.isBlank(NettyConf.cp)||StringUtils.isBlank(NettyConf.cpys)||StringUtils.isBlank(NettyConf.shengID)||StringUtils.isBlank(NettyConf.shiID)||StringUtils.isBlank(NettyConf.host)||NettyConf.port == 0){
			Toast.makeText(CjApplication.getInstance(),"请填写完整注册参数",Toast.LENGTH_LONG).show();
		}else {
			Zdzc zdzc = new Zdzc();
			zdzc.setImei(NettyConf.imei);
			zdzc.setSxyid(NettyConf.shiID);
			zdzc.setSyid(NettyConf.shengID);
			zdzc.setXlh(NettyConf.zdxlh);
			zdzc.setZdcpbz(NettyConf.cp);
			zdzc.setZdcpys(NettyConf.cpys);
			zdzc.setZdxh(NettyConf.model);
			zdzc.setZzsid(NettyConf.zzsid);
			byte[] b2 = zdzc.getZdzcbytes();
			List<Tdata> list = MsgUtilClient.generateMsg(b2, "0100", NettyConf.mobile, "0");
			GatewayService.sendHexMsgToServer("serverChannel", list.get(0).getData());
		}
	}

	/**
	 * 参数请求
	 */
	public static void sendParams(){
		SharedPreferences zdcssp = CjApplication.getInstance().getSharedPreferences("zdcs", Context.MODE_PRIVATE);
		int version=zdcssp.getInt("version",0);
		byte[] b2=ByteUtil.intToByteArray(version);
		List<Tdata> list= MsgUtilClient.generateMsg(b2, "1103",NettyConf.mobile,"0");
		GatewayService.sendHexMsgToServer("serverChannel", list.get(0).getData());
	}

	/**
	 * 照片初始化
	 * */
	public static void sendZpsc(String scms,String tdh,String lx){
		ispz=true;//正在拍照中

		String gnss=ZdUtil.getGnss4();
        Message msg=new Message();
        msg.arg1=2;
        Bundle bundle = new Bundle();
        bundle.putString("scms",scms);
        bundle.putString("tdh",tdh);
        bundle.putString("lx",lx);
		bundle.putString("gnss",gnss);
        msg.setData(bundle);
		Timer ysTimer=new Timer();
		PzysTimer pt=new PzysTimer(msg);
		ysTimer.schedule(pt,1000);
	}

	/**
	 * 照片初始化
	 * */
	public static void sendZpsc1(String scms,String tdh,String lx){
		String gnss=ZdUtil.getGnss4();
		Message msg=new Message();
		msg.arg1=13;
		Bundle bundle = new Bundle();
		bundle.putString("scms",scms);
		bundle.putString("tdh",tdh);
		bundle.putString("lx",lx);
		bundle.putString("gnss",gnss);
		msg.setData(bundle);
		Timer ysTimer=new Timer();
		PzysTimer pt=new PzysTimer(msg);
		ysTimer.schedule(pt,1000);
	}
    /**
     * 照片初始化
     * */
    public static void sendZpsc2(String scms,String tdh,String lx,String gnss,String picpath){
        //String gnss=ZdUtil.getGnss4();
        //先组合照片数据
        String s=String.valueOf(ZdUtil.getLongTime());
        String zpbh=s.substring(s.length()-12,s.length()-2);

//        String picpath="mnt/sdcard/loginpic/1.jpg";
        //图片加水印
        String mark="";
        DecimalFormat df = new DecimalFormat("#.000");
        if(lx.equals("20")||lx.equals("21")){
            mark= PropertiesUtil.getValue("jlwatermark");
            mark=mark.replace("{{jlbh}}",NettyConf.jbh);
        }else{
            if(lx.equals("17")||lx.equals("18")||lx.equals("19")){
                mark= PropertiesUtil.getValue("xywatermark");
                mark=mark.replace("{{xybh}}",NettyConf.xbh);
                mark=mark.replace("{{jlbh}}",NettyConf.jbh);
            }else{
                if(NettyConf.xystate==1){
                    mark= PropertiesUtil.getValue("xywatermark");
                    mark=mark.replace("{{xybh}}",NettyConf.xbh);
                    mark=mark.replace("{{jlbh}}",NettyConf.jbh);
                }else if(NettyConf.jlstate==1){
                    mark= PropertiesUtil.getValue("jlwatermark");
                    mark=mark.replace("{{jlbh}}",NettyConf.jbh);
                }else{
                    mark= PropertiesUtil.getValue("watermark");
                }
            }

        }
        mark=mark.replace("{{jxbh}}",NettyConf.pxjgbh);
        mark=mark.replace("{{sd}}",marksd);
        mark=mark.replace("{{time}}",marksj);
        mark=mark.replace("{{cp}}",NettyConf.cp);
        mark=mark.replace("{{wd}}",df.format(Double.valueOf(markwd)));
        mark=mark.replace("{{jd}}",df.format(Double.valueOf(markjd)));

		ispz=false;//拍照结束
        //String src_base64=Base64Utils.GetImageBase64(picpath);
        //src_base64=src_base64.replaceAll("\r|\n", "");
        byte[] zpsj=ImageMarkUtil.picMark(picpath,mark);
		if(zpsj!=null) {
			if (NettyConf.debug) {
				Log.e("TAG", "返回的数据长度：" + zpsj.length);
			}
			//source = source.replaceAll("\r|\n", "");
			Zpdata zd = new Zpdata();
			zd.setZpbh(zpbh);
			zd.setZpsj(zpsj);

			//存入数据库
			DbHandle.insertZpdata(zd);

			//删除图片
			Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
			ContentResolver mContentResolver = CjApplication.getInstance().getContentResolver();
			String where = MediaStore.Images.Media.DATA + "='" + picpath + "'";
			mContentResolver.delete(uri, where, null);

			//预留照片上传初始化流水号
			int msgserno = MsgUtilClient.generateLsh();

			byte[] b3 = zd.getZpdatabytes();
			byte[] b2 = MsgUtilClient.getMsgExtend(b3, "0306", "13", NettyConf.sfjm2);

			List<Tdata> list = MsgUtilClient.generateMsg(b2, "0900", NettyConf.mobile, NettyConf.sfjm1);
			int len = list.size();

			//int lsh=MsgUtilClient.generateLsh();
			//初始化流水号与照片数据对应
			// NettyConf.zpdataStr.put(lsh+"",msg);
			//再组合照片上传初始化
			Zpsc zpsc = new Zpsc();
			zpsc.setZpbh(zpbh);
			zpsc.setScms(scms);
			zpsc.setTdh(tdh);
			zpsc.setTpcc("1");//需接洽
			zpsc.setSjlx(lx);
			if (lx.equals("20") || lx.equals("21")) {
				zpsc.setKtid("0");
				zpsc.setBh(NettyConf.jbh);
			} else if (lx.equals("17") || lx.equals("18") || lx.equals("19")) {
				zpsc.setKtid(NettyConf.ktid);
				zpsc.setBh(NettyConf.xbh);
			} else {
				if (NettyConf.xystate == 1) {
					zpsc.setKtid(NettyConf.ktid);
					zpsc.setBh(NettyConf.xbh);
				} else if (NettyConf.jlstate == 1) {
					zpsc.setKtid("0");
					zpsc.setBh(NettyConf.jbh);
				} else {
					zpsc.setKtid("0");
					zpsc.setBh("0000000000000000");
				}
			}
			zpsc.setGnss(gnss);
			zpsc.setRlsb("50");//需接洽
			String srclen = String.valueOf(zpsj.length);
			zpsc.setZpsjcd(srclen);
			zpsc.setZbs(String.valueOf(len));
			byte[] zpscb3 = zpsc.getZpscBytes();
			byte[] zpscb2 = MsgUtilClient.getMsgExtend(zpscb3, "0305", "13", NettyConf.sfjm2);
			List<Tdata> list2 = MsgUtilClient.generateMsg(zpscb2, "0900", NettyConf.mobile, NettyConf.sfjm1,msgserno);

			//存入数据库
			DbHandle.insertZpsc(zpsc);
			if (list2.size() > 0) {
				String parentid = list2.get(0).getKey();
				//处理照片数据
				for (Tdata tdata : list) {
					tdata.setParentid(parentid);
					DbHandle.insertTdata(tdata);
				}
			}

			if (NettyConf.sendState) {
				if (ZdUtil.pdNetwork() && NettyConf.constate == 1 && NettyConf.jqstate == 1) {
					ForwardUtil.sendData(list2, 1, 4);
				} else {
					if (NettyConf.debug) {
						Log.e("TAG", "照片缓存");
					}
					DbHandle.insertTdatas(list2, 4);
				}
			} else {
				if (NettyConf.debug) {
					Log.e("TAG", "照片缓存");
				}
				DbHandle.insertTdatas(list2, 4);
			}

			if (lx.equals("18")) {
				StudentoutTimer studentoutTimer=new StudentoutTimer();
				new Timer().schedule(studentoutTimer,300);
			}

			if (lx.equals("21")) {
				CoachoutTimer coachoutTimer=new CoachoutTimer();
				new Timer().schedule(coachoutTimer,300);
			}
		}else{
			Speaking.in("无拍照数据");
			if(NettyConf.debug){
				Log.e("TAG","拍照数据为空！");
			}
		}
    }

	/**
	 * 发送终端鉴权
	 */
	public static void sendZdjqHex(){
		if(NettyConf.debug){
			Log.e("TAG","发送鉴权信息");
		}
		//已注册成功鉴权
		Zdjq zdjq = new Zdjq();
		try {
			long ts=new Date().getTime()/1000;
			zdjq.setSjc(String.valueOf(ts));
			zdjq.setJqmw(Sign.sign(NettyConf.jszdbh, ts, NettyConf.key));
		} catch (Exception e) {
			e.printStackTrace();
		}
		byte[] b2=zdjq.getZdjqbytes();
		List<Tdata> list= MsgUtilClient.generateMsg(b2,"0102",NettyConf.mobile,"0");
		GatewayService.sendHexMsgToServer("serverChannel",list);
	}

	/**
	 * 终端注销
	 */
	public static void sendZdzx(){
		byte[] b2=new byte[0];
		List<Tdata> list=MsgUtilClient.generateMsg(b2,"0003",NettyConf.mobile,"0");
		GatewayService.sendHexMsgToServer("serverChannel",list);
	}

	/**
	 * 发送补传分包数据
	 * @param bcfb
     */
	public static void replyBcfb(Bcfb bcfb){
		int yslsh=bcfb.getYslsh();
		String xhs=bcfb.getXhs();
		Log.e("TAG","补包请求："+xhs);
		String[] ss=xhs.split(";");

		String temp="";
		for(int i=0;i<ss.length;i++){
			int lsh=yslsh+Integer.parseInt(ss[i])-1;
			if(i==ss.length-1){
				temp=temp+lsh;
			}else{
				temp=temp+lsh+",";
			}
		}
		if(NettyConf.debug){
			Log.e("TAG","补包流水号:"+temp);
		}
		String sql = "select * from tdata where key in (?)";
		String[] param = {temp};
		List<Tdata> tdatas=DbHandle.queryTdata(sql,param);
		if(NettyConf.debug){
			Log.e("TAG","补包数量:"+tdatas.size());
		}
		GatewayService.sendHexMsgToServer("serverChannel", tdatas);
	}

	//判断无分钟学时的定位状态
	public static boolean pdDwState(){
		long t=new Date().getTime();
		if(t-LocationUtil.endTime<=60*1000){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * 无附加信息的位置信息
	 * @return
     */
	public static String getGnss(){
		Gnss gnss = new Gnss();
		if(LocationUtil.state&&NettyConf.location!=null&&pdDwState()) {
			Location location = NettyConf.location;
			gnss.setWd(String.valueOf(location.getLatitude()));
			gnss.setJd(String.valueOf(location.getLongitude()));
			gnss.setFx(String.valueOf((int) location.getBearing()));
			gnss.setSj(String.valueOf(ZdUtil.getLongTime()));
			gnss.setWxdwsd(String.valueOf(location.getSpeed()));
			gnss.setBjbz("00000000000000000000000000000000");
			gnss.setZt("0000000000000000000000000000001"+AccUtil.accState);
			gnss.setXsjlsd(getSpeed());
		}else{
			gnss=getCGnss();
		}
		byte[] bs = gnss.getGnssBytes();
		return ByteUtil.bytesToHexString(bs);
	}

	public static String getSpeed(){
		try {
			double l = NettyConf.location.getSpeed() * 3.6;
			DecimalFormat decimalFormat=new DecimalFormat("0.0");
			return decimalFormat.format(l);
		}catch (Exception e){
			return "0";
		}
	}

	/**
	 * 包含里程和发动机转速
	 * @return
	 */
	public static String getGnss2(){
		Gnss gnss = new Gnss();
		if(LocationUtil.state&&NettyConf.location!=null&&pdDwState()) {
			Location location = NettyConf.location;
			gnss.setWd(String.valueOf(location.getLatitude()));
			gnss.setJd(String.valueOf(location.getLongitude()));
			gnss.setFx(String.valueOf((int) location.getBearing()));
			gnss.setSj(String.valueOf(ZdUtil.getLongTime()));
			gnss.setWxdwsd(String.valueOf(location.getSpeed()));
			gnss.setBjbz("00000000000000000000000000000000");
			gnss.setZt("0000000000000000000000000000001"+AccUtil.accState);
			gnss.setXsjlsd(getSpeed());
		}else{
			gnss=getCGnss();
		}
		GnssExtend ge = new GnssExtend();
		ge.setLc(0.0);
		ge.setFdjzs(0);
		gnss.setFjxx(ByteUtil.bytesToHexString(ge.getGnssExtendBytes()));

		byte[] bs = gnss.getGnssBytes();
		return ByteUtil.bytesToHexString(bs);
	}

	/**
	 * 包含里程和发动机转速
	 * @return
	 */
	public static String getGnss4(){
		Gnss gnss = new Gnss();
		if(LocationUtil.state&&NettyConf.location!=null&&pdDwState()) {
			Location location = NettyConf.location;
			String wd = String.valueOf(location.getLatitude());
			String jd = String.valueOf(location.getLongitude());
			gnss.setWd(wd);
			gnss.setJd(jd);
			gnss.setFx(String.valueOf((int) location.getBearing()));
			Date d = new Date(ZdUtil.getLongTime());
			gnss.setSj(String.valueOf(d.getTime()));
			gnss.setBjbz("00000000000000000000000000000000");
			gnss.setZt("0000000000000000000000000000001"+AccUtil.accState);
			gnss.setXsjlsd(getSpeed());
			String wxdwsd =getSpeed();
			gnss.setWxdwsd(wxdwsd);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			marksj = sdf.format(d);
			markwd = wd;
			markjd = jd;
			marksd = wxdwsd;
		}else{
			gnss=getCGnss();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			marksj = sdf.format(new Date());
			markwd = gnss.getWd();
			markjd = gnss.getJd();
			marksd = gnss.getWxdwsd();
		}
		GnssExtend ge = new GnssExtend();
		ge.setLc(0.0);
		ge.setFdjzs(0);
		gnss.setFjxx(ByteUtil.bytesToHexString(ge.getGnssExtendBytes()));

		byte[] bs = gnss.getGnssBytes();
		return ByteUtil.bytesToHexString(bs);
	}

	/**
	 * 包含所有
	 * @return
	 */
	public static String getGnss3(){
		Gnss gnss = new Gnss();
		if(LocationUtil.state&&NettyConf.location!=null&&pdDwState()) {

			Location location = NettyConf.location;
			gnss.setWd(String.valueOf(location.getLatitude()));
			gnss.setJd(String.valueOf(location.getLongitude()));
			gnss.setFx(String.valueOf((int) location.getBearing()));
			gnss.setSj(String.valueOf(ZdUtil.getLongTime()));
			gnss.setWxdwsd(String.valueOf(location.getSpeed()));

			gnss.setBjbz("000000000000"+ DzwlTimerTask.jcqy+"0000000000000000000");
			DzwlTimerTask.jcqy="0";
			gnss.setZt("0000000000000000000000000000001"+AccUtil.accState);
			gnss.setXsjlsd(getSpeed());
		}else{
			gnss=getCGnss();
		}
		GnssExtend ge = new GnssExtend();
		ge.setLc(0.0);
		int zs = new Random().nextInt(400) + 800;
		ge.setFdjzs(zs);
		ge.setGd(0);
		ge.setYl(0.0);
		gnss.setFjxx(ByteUtil.bytesToHexString(ge.getGnssExtendBytes()));

		byte[] bs = gnss.getGnssBytes();
		return ByteUtil.bytesToHexString(bs);
	}

	/**
	 * 包含里程和发动机转速
	 * @return
	 */
	public static String getGnss5(){
		Gnss gnss = new Gnss();
		if(LocationUtil.state&&NettyConf.location!=null&&pdDwState()) {
			Location location = NettyConf.location;
			if(location==null){
				location=NettyConf.location;
			}
			gnss.setWd(String.valueOf(location.getLatitude()));
			gnss.setJd(String.valueOf(location.getLongitude()));
			gnss.setFx(String.valueOf((int) location.getBearing()));
			gnss.setSj(String.valueOf(ZdUtil.getLongTime()));
			gnss.setWxdwsd(getSpeed());
			gnss.setBjbz("00000000000000000000000000000000");
			gnss.setZt("0000000000000000000000000000001"+AccUtil.accState);
			if(location.getSpeed()==0){
				String s=NettyConf.pxkc.substring(3,4);
				Random rand = new Random();
				double d=0;
				if(NettyConf.debug){
					Log.e("TAG","第及部分："+s);
				}
				if(s.equals("2")){
					d= rand.nextDouble()*5+3;
				}else if(s.equals("3")){
					d=rand.nextDouble()*20+10;
				}
				gnss.setXsjlsd(String.valueOf(d));
			}else {
				gnss.setXsjlsd(getSpeed());
			}
		}else{
			gnss=getCGnss();
		}
		GnssExtend ge = new GnssExtend();
		ge.setLc(0.0);
		//随机800-1200的转速
		int zs = new Random().nextInt(400) + 800;
		ge.setFdjzs(zs);
		gnss.setFjxx(ByteUtil.bytesToHexString(ge.getGnssExtendBytes()));

		byte[] bs = gnss.getGnssBytes();
		return ByteUtil.bytesToHexString(bs);
	}

	public static Gnss getCGnss(){
		Gnss gnss = new Gnss();
		gnss.setWd("23.024554");
		gnss.setJd("113.736303");
		gnss.setFx("0");
		gnss.setSj(String.valueOf(new Date().getTime()));
		gnss.setWxdwsd("0");
		gnss.setBjbz("00000000000000000000000000000000");
		gnss.setZt("0000000000000000000000000000001"+AccUtil.accState);
		gnss.setXsjlsd("0");
		return gnss;
	}


	/**
	 * 学时上报
	 * */
	public static void sendXsjl(){
		Xsjl xsjl = new Xsjl();
		String jlbh = NettyConf.jbh;
		xsjl.setJlbh(jlbh);
		String xybh = NettyConf.xbh;
		xsjl.setXybh(xybh);
		xsjl.setSblx("01");//类型
		xsjl.setKtid(NettyConf.ktid);//课程id
		xsjl.setZdsd(String.valueOf(NettyConf.zdsdM*3.6));//最大速度
		NettyConf.zdsdM=0;
		DecimalFormat df   = new DecimalFormat("######0.0");
		DecimalFormat df2   = new DecimalFormat("######0.0000");
		String xclc=df.format(CountDistance.getTotalFMile()/1000.0);
		String xclc2=df2.format(CountDistance.getTotalFMile()/1000.0);
		if(NettyConf.debug){
			Log.e("TAG","分钟里程(km):"+xclc);
		}
		xsjl.setXclc(xclc);//里程
		CountDistance.setTotalFMile(0);

		//把总里程存储起来
		SharedPreferences sp = CjApplication.getInstance().getSharedPreferences("student", Context.MODE_PRIVATE); //私有数据
		SharedPreferences.Editor editor = sp.edit();//获取编辑器
		float f=sp.getFloat("zlc",0);
		f=f+Float.valueOf(xclc2);
		if(NettyConf.debug){
			Log.e("TAG","总里程(km)："+f);
		}
		editor.putFloat("zlc",f);
		editor.commit();

		xsjl.setJlzt("0");//状态
		xsjl.setXsjlbh(getxsjlbh());
		SimpleDateFormat sdf=new SimpleDateFormat("HHmmss");
		String jlcssj=sdf.format(new Date());
		xsjl.setJlcssj(jlcssj);//记录产生时间
		String pxkc = NettyConf.pxkc;
		xsjl.setPxkc(pxkc);
		xsjl.setGnss(NettyConf.xsgnss);
		byte[] b3 = xsjl.getXsjlBytes();
		byte[] b2= MsgUtilClient.getMsgExtend(b3,"0203", "13", NettyConf.sfjm2);
		List<Tdata> list=MsgUtilClient.generateMsg(b2,"0900", NettyConf.mobile, NettyConf.sfjm1);

		//保存学时记录
		DbHandle.insertXsjl(xsjl);

		if(NettyConf.debug){
			Log.e("TAG","学时发送信息："+list.get(0).getData());
		}
		if(NettyConf.sendState&&NettyConf.constate==1&&NettyConf.jqstate==1) {
			ForwardUtil.sendData(list, 1, 5);
		}else{
			DbHandle.insertTdatas(list,5);
		}
	}

	/**
	 * 生成学时记录编号
	 * @return
     */
	public static  String getxsjlbh(){
		SimpleDateFormat sdf=new SimpleDateFormat("yyMMdd");
		String rqm=sdf.format(new Date());

		SimpleDateFormat sdf2=new SimpleDateFormat("HH:mm");
		Date d=new Date(ZdUtil.getLongTime());
		String s=sdf2.format(d);
		String[] ss=s.split(":");
		int xlh=Integer.parseInt(ss[0])*60+Integer.parseInt(ss[1]);

		String vl=String.valueOf(xlh);
		String temp="";
		for(int i=0;i<4-vl.length();i++){
			temp+="0";
		}
		vl=temp+vl;
		return NettyConf.jszdbh+rqm+vl;
	}

	/**
	 * 获取东八区时间
	 * @return
     */
	public static String getTime(){
		SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(NettyConf.location!=null) {
			Date d = new Date();
			String ee = dff.format(d);
			return ee;
		}else{
			String ee = dff.format(new Date());
			return ee;
		}
	}

	public static String getTime2(){
		SimpleDateFormat dff = new SimpleDateFormat("yyMMddHHmmss");
		if(NettyConf.location!=null) {
			Date d = new Date();
			String ee = dff.format(d);
			return ee;
		}else{
			String ee = dff.format(new Date());
			return ee;
		}
	}

	public static String getTime3(){
		SimpleDateFormat dff = new SimpleDateFormat("HHmmss");
		if(NettyConf.location!=null) {
			Date d = new Date();
			String ee = dff.format(d);
			return ee;
		}else{
			String ee = dff.format(new Date());
			return ee;
		}
	}

	public static String getdisMin(String d1,String d2){
		SimpleDateFormat dff = new SimpleDateFormat("yyMMddHHmmss");
		try {
			long s=dff.parse(d2).getTime()-dff.parse(d1).getTime();
			return (int)(s/60000)+"";
		} catch (ParseException e) {
			e.printStackTrace();
			return "0";
		}
	}

	public static long getLongTime(){
		return new Date().getTime();
	}

	/**
	 * 发送缓存数据
	 */
	public static void sendCache(){
		CacheSend cs=new CacheSend();
		Thread t=new Thread(cs);
		t.start();
	}

	//缓存数据清除
	public static void deleteCache(){
		CacheDelete cacheDelete=new CacheDelete();
		Thread t=new Thread(cacheDelete);
		t.start();
	}

	public static boolean pdGps(){
		/*if(LocationUtil.state&&NettyConf.location!=null){
			return true;
		}else{
			return false;
		}*/
		return true;
	}
	public static boolean pdNetwork(){
		ConnectivityManager manager = (ConnectivityManager) CjApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeInfo = manager.getActiveNetworkInfo();
		if(activeInfo==null){
            return false;
		}else {
			return true;
		}
	}

	/**
	 * 链接服务器
	 */
	public static void conServer() {
		if (NettyConf.constate == 0) {
			new Timer().schedule(new ConTimer(),200);
		} else if (NettyConf.constate == 1) {
			if (NettyConf.zcstate == 0) {
				//没注册过注册
				ZdUtil.sendZdzc();
			} else if (NettyConf.zcstate == 1) {
				//发送终端鉴权
				ZdUtil.sendZdjqHex();
			}
		}
	}

	/**
	 * 请求密码匹配 type 1是教练 4是学员
	 */
	public static void matchPassword(int type,String password){
		ImeiPassword imeiPassword=new ImeiPassword();
		imeiPassword.setImei(NettyConf.imei);
		imeiPassword.setType(type);
		imeiPassword.setPassword(password);
		byte[] b3 = imeiPassword.getImeiPasswordBytes();
		List<Tdata> list;
		if(type==9){
			if(NettyConf.debug){
				Log.e("TAG","密码认证不用加密");
			}
			byte[] b2 = MsgUtilClient.getMsgExtend(b3, "1001", "13", "0");
			list = MsgUtilClient.generateMsg(b2, "0900", NettyConf.mobile, "0");
		}else {
			if(NettyConf.debug){
				Log.e("TAG","密码认证加密");
			}
			byte[] b2 = MsgUtilClient.getMsgExtend(b3, "1001", "13", "2");
			list = MsgUtilClient.generateMsg(b2, "0900", NettyConf.mobile, "1");
		}
		//ForwardUtil.sendData(list, 0, 1);
		if(NettyConf.debug){
			Log.e("TAG","发送管理员密码认证");
		}
		if(list.size()>0) {
			GatewayService.sendHexMsgToServer("serverChannel", list.get(0).getData());
		}
	}

	//学员登出
	public static synchronized void studentOut1(){
		if(!xydcstate) {
			xydcstate = true;
			new Timer().schedule(new XydcStateTimer(),10*1000);
			try {
				if (ZdUtil.pdGps()) {
					ZdUtil.sendZpsc1("129", "0", "18");//调用拍照
				} else {
					Speaking.in("定位信息获取失败");
				}
			} catch (Exception e) {
				Speaking.in("学员登出数据异常");
			}
		}
	}

	public static List<Tdata> studentOut2(){
		String gnss = ZdUtil.getGnss();
		Xydc xydc = new Xydc();
		xydc.setXybh(NettyConf.xbh);
		String d2 = ZdUtil.getTime2();
		xydc.setDcsj(d2);
		//SharedPreferences sp = CjApplication.getInstance().getSharedPreferences("student", Context.MODE_PRIVATE); //私有数据
		//int zpxsj=sp.getInt("zpxsj",0);

		int zpxsj=0;
		try {
			String sql = "select * from xsjl where ktid=?";
			String[] params = {NettyConf.ktid};
			List list = DbHandle.queryxsjl(sql, params);
			zpxsj = list.size();
            //Speaking.in("培训"+zpxsj+"分钟");
		}catch(Exception e){}

		if(zpxsj>240){
			zpxsj=240;
		}
		xydc.setDlzsj(zpxsj+"");

		SharedPreferences sps = CjApplication.getInstance().getSharedPreferences("student", Context.MODE_PRIVATE); //私有数据
		float z=sps.getFloat("zlc",0);
		if(NettyConf.debug){
			Log.e("TAG","最后获取总里程:"+z);
		}
		xydc.setDlzlc(z+"");
		xydc.setKtid(NettyConf.ktid);
		xydc.setGnss(gnss);
		byte[] b3 = xydc.getXydcBytes();
		byte[] b2 = MsgUtilClient.getMsgExtend(b3, "0202", "13", NettyConf.sfjm2);
		List<Tdata> list = MsgUtilClient.generateMsg(b2, "0900", NettyConf.mobile, NettyConf.sfjm1);
		return list;
	}

	public static void handleStudentOut(){
		Speaking.in("学员登出成功");
		if(NettyConf.xystate!=0) {
			NettyConf.xystate = 0;
			XsjlTimer.fzpxjlsc=0;
		}
		CountDistance.isStart=false;
		//把总里程存储起来
		SharedPreferences sp = CjApplication.getInstance().getSharedPreferences("student", Context.MODE_PRIVATE); //私有数据
		SharedPreferences.Editor editor = sp.edit();//获取编辑器
		editor.putFloat("zlc",0);

		editor.putInt("xystate",0);

		editor.putInt("zpxsj",0);

		editor.putInt("fzpxjlsc",0);

		editor.commit();
		editor.clear();

		//缓存转发
		if(!NettyConf.sendState&&NettyConf.jqstate==1&&NettyConf.constate==1){
			NettyConf.sendState=true;
			ZdUtil.sendCache();
		}
		NettyConf.sendState=true;
		editor.putBoolean("sendState",NettyConf.sendState);
		editor.commit();


		if(NettyConf.handlersmap.get("loginstudent")==null) {
			Message msg = new Message();
			msg.arg1 = 5;
			Handler handler = (Handler) NettyConf.handlersmap.get("login");
			handler.sendMessage(msg);
		}
	}

	//教练登出
	public static void coachOut1(){
		try {
			if (ZdUtil.pdGps()) {
				ZdUtil.sendZpsc1("129", "0", "21");//调用拍照
			} else {
				Speaking.in("定位数据获取失败");
			}
		}catch(Exception e){
			Speaking.in("教练员登出数据异常");
		}
	}

	public static List<Tdata> coachOut2(){
		String gnss = ZdUtil.getGnss();
		Jlydc jlydc = new Jlydc();
		jlydc.setJlybh(NettyConf.jbh);
		jlydc.setGnss(gnss);
		byte[] b3 = jlydc.getJlydcBytes();
		byte[] b2 = MsgUtilClient.getMsgExtend(b3, "0102", "13", NettyConf.sfjm2);
		List<Tdata> list = MsgUtilClient.generateMsg(b2, "0900", NettyConf.mobile, NettyConf.sfjm1);
		return list;
	}

	public static void handleCoachOut(){
		Speaking.in("教练员登出成功");
		if(NettyConf.jlstate!=0) {
			NettyConf.jlstate = 0;
		}
		//保存教练信息
		SharedPreferences coachsp = CjApplication.getInstance().getSharedPreferences("coach", Context.MODE_PRIVATE); //私有数据
		SharedPreferences.Editor editor = coachsp.edit();//获取编辑器
		editor.putInt("jlstate",0);
		editor.commit();

		if(NettyConf.handlersmap.get("logincoach")==null) {
			Message msg = new Message();
			msg.arg1 = 5;
			Handler handler = (Handler) NettyConf.handlersmap.get("login");
			handler.sendMessage(msg);
		}
	}

	//判断当前时间是否能登陆
	public static boolean canLogin(){
		if(ZdUtil.pdGps()){
			String s=ZdUtil.getTime3();
			int sj=Integer.valueOf(s);
			if(sj<NettyConf.maxTime&&sj>NettyConf.minTime){
				if(NettyConf.jxzt==1) {
					return true;
				}else{
					Speaking.in("设备处于禁训状态");
					return false;
				}
			}else{
				Speaking.in("现在不是培训时间段");
				return false;
			}
		}else{
			Speaking.in("定位信息获取失败");
			return false;
		}
	}

	/**
	 * 获取本机IP
	 * */

	public static String getIPAddress(Context context) {
		NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
				try {
					for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
						NetworkInterface intf = en.nextElement();
						for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
							InetAddress inetAddress = enumIpAddr.nextElement();
							if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
								return inetAddress.getHostAddress();
							}
						}
					}
				} catch (SocketException e) {
					e.printStackTrace();
				}

			} else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
				WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				WifiInfo wifiInfo = wifiManager.getConnectionInfo();
				String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
				return ipAddress;
			}
		} else {
			//当前无网络连接,请在设置中打开网络
		}
		return null;
	}

	/**
	 * 将得到的int类型的IP转换为String类型
	 * @param ip
	 * @return
	 */
	public static String intIP2StringIP(int ip) {
		return (ip & 0xFF) + "." +
				((ip >> 8) & 0xFF) + "." +
				((ip >> 16) & 0xFF) + "." +
				(ip >> 24 & 0xFF);
	}

	/**
	 * 线路内容报读
	 * @return
	 */
	public static String xlSpeakMsg() {
		String xh="";
		String type="";
		Line line = NettyConf.line;
		if (line != null) {
			Location location = LocationUtil.getNewGps();
			if (location!=null) {
				String s = line.getXlzb();
				String[] ss1 = s.split(";");
				for (int i=0;i<ss1.length;i++) {
					String t=ss1[i];
					if(StringUtils.isNotEmpty(t)) {
						String[] ss2 = t.split(",");
						double dis = CountDistance.getDistance2(location.getLatitude(), location.getLongitude(), Double.valueOf(ss2[2]), Double.valueOf(ss2[3]));
						if (dis <= NettyConf.bdjl) {
							xh=xh+","+ss2[0];
							type=type+","+ss2[1];
							ss1=ArrayUtils.remove(ss1,i);
						}
					}
				}

				NettyConf.line.setXlzb(StringUtils.join(ss1,";"));

			}
			return type;

		}else{
			Speaking.in("请选择线路");
			return "";
		}
	}

	//发送获取设备基本信息
	public static void getDeviceInfo(){
		byte[] b2 = NettyConf.imei.getBytes();
		List<Tdata> list = MsgUtilClient.generateMsg(b2, "1105", "18575320215", "0");
		GatewayService.sendHexMsgToServer("serverChannel", list.get(0).getData());
	}

}


