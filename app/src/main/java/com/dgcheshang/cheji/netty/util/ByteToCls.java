package com.dgcheshang.cheji.netty.util;

import android.util.Log;

import com.dgcheshang.cheji.netty.clientreply.CxcsR;
import com.dgcheshang.cheji.netty.clientreply.CxxsjlR;
import com.dgcheshang.cheji.netty.clientreply.CxyycsR;
import com.dgcheshang.cheji.netty.clientreply.CxzpR;
import com.dgcheshang.cheji.netty.clientreply.JxztR;
import com.dgcheshang.cheji.netty.clientreply.LjpzR;
import com.dgcheshang.cheji.netty.clientreply.SczdzpR;
import com.dgcheshang.cheji.netty.clientreply.YycsR;
import com.dgcheshang.cheji.netty.conf.NettyConf;
import com.dgcheshang.cheji.netty.po.Bcfb;
import com.dgcheshang.cheji.netty.po.ClearJlcd;
import com.dgcheshang.cheji.netty.po.Cxcs;
import com.dgcheshang.cheji.netty.po.Cxxsjl;
import com.dgcheshang.cheji.netty.po.Cxzp;
import com.dgcheshang.cheji.netty.po.Dzwl;
import com.dgcheshang.cheji.netty.po.Gzkz;
import com.dgcheshang.cheji.netty.po.Header;
import com.dgcheshang.cheji.netty.po.Jxzt;
import com.dgcheshang.cheji.netty.po.Ljpz;
import com.dgcheshang.cheji.netty.po.MsgExtend;
import com.dgcheshang.cheji.netty.po.Parameter;
import com.dgcheshang.cheji.netty.po.ParamsSz;
import com.dgcheshang.cheji.netty.po.ParamsSzAll;
import com.dgcheshang.cheji.netty.po.Qqtybh;
import com.dgcheshang.cheji.netty.po.Sbzpjg;
import com.dgcheshang.cheji.netty.po.Sfrz;
import com.dgcheshang.cheji.netty.po.Upgrade;
import com.dgcheshang.cheji.netty.po.Yycs;
import com.dgcheshang.cheji.netty.po.Zdjq;
import com.dgcheshang.cheji.netty.po.Zdkz;
import com.dgcheshang.cheji.netty.po.Zdzc;
import com.dgcheshang.cheji.netty.po.ZdzcR;
import com.dgcheshang.cheji.netty.po.Zdzp;
import com.dgcheshang.cheji.netty.proputil.ParamType;
import com.dgcheshang.cheji.netty.serverreply.CommonR;
import com.dgcheshang.cheji.netty.serverreply.DeviceInfo;
import com.dgcheshang.cheji.netty.serverreply.ImeiPasswordR;
import com.dgcheshang.cheji.netty.serverreply.JlydcR;
import com.dgcheshang.cheji.netty.serverreply.JlydlR;
import com.dgcheshang.cheji.netty.serverreply.QqtybhR;
import com.dgcheshang.cheji.netty.serverreply.SbzpjgR;
import com.dgcheshang.cheji.netty.serverreply.SfrzR;
import com.dgcheshang.cheji.netty.serverreply.XydcR;
import com.dgcheshang.cheji.netty.serverreply.XydlR;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class ByteToCls {

	/**
	 * 获取原版消息字节数组
	 * @param msg
	 * @return
	 */
	public static byte[] hexStringTobyte(String msg){
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<msg.length();i=i+2){
			sb.append(msg.substring(i, i+2)+",");
		}
		msg=sb.toString().replaceAll("7D,02", "7E");
		msg=msg.replaceAll("7D,01", "7D");
		msg=msg.replaceAll(",", "");
		byte[] ss= ByteUtil.hexStringToByte(msg);
		return ByteUtil.subBytes(ss, 1, ss.length-2);
	}

	/**
	 * 获取消息头
	 * @param b
	 * @return
	 */
	public static Header getHeader(byte[] b){
		try{
			Header header=new Header();
			//协议号
			header.setProtver(b[0] & 0xFF);
			//消息ID
			byte[] temp= ByteUtil.subBytes(b, 1, 2);
			header.setMsgid(ByteUtil.bytesToHexString(temp));
			//消息体属性
			String s1= Integer.toBinaryString((b[3] & 0xFF) + 0x100).substring(1);
			String s2= Integer.toBinaryString((b[4] & 0xFF) + 0x100).substring(1);
			String s12=s1+s2;
			header.setBodyprop(s12);

			//是否分包
			if(s12.substring(2,3).equals("0")){
				header.setSffb("0");
			}
			if(s12.substring(2,3).equals("1")){
				header.setSffb("1");
				temp= ByteUtil.subBytes(b, 16, 2);
				header.setMsgpackcnt(ByteUtil.byte2ToUnsignedShort(temp, 0));
				temp= ByteUtil.subBytes(b, 18, 2);
				header.setPacksortno(ByteUtil.byte2ToUnsignedShort(temp, 0));
			}

			//加密方式
			if(s12.substring(5,6).equals("1")){
				header.setJmfs("1");
			}

			if(s12.substring(5,6).equals("0")){
				header.setJmfs("0");
			}

			//数据长度
			int num=Integer.parseInt(s12.substring(6,s12.length()),2);
			header.setSjcd(num);

			//终端手机号
			temp= ByteUtil.subBytes(b, 5, 8);
			String hs= ByteUtil.bytesToHexString(temp);
			hs= ByteUtil.clearHexStringfront(hs);
			temp= ByteUtil.hexStringToByte(hs);
			header.setMobileno(ByteUtil.bcd2Str(temp));

			//消息流水号
			temp= ByteUtil.subBytes(b, 13, 2);
			header.setMsgserno(ByteUtil.byte2ToUnsignedShort(temp, 0));

			if(header.getSffb().equals("1")){
				temp= ByteUtil.subBytes(b, 16, 2);
				header.setMsgpackcnt(ByteUtil.byte2ToUnsignedShort(temp, 0));
				temp= ByteUtil.subBytes(b, 18, 2);
				header.setPacksortno(ByteUtil.byte2ToUnsignedShort(temp, 0));
			}

			return header;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * byte[]转终端注册类
	 */

	public static Zdzc getZdzc(byte[] sbody){
		Zdzc zdzc=new Zdzc();
		//消息体解析
		byte[] temp= ByteUtil.subBytes(sbody, 0, 2);
		String syid=String.valueOf(ByteUtil.byte2ToUnsignedShort(temp, 0));
		if(syid.length()<2){
			syid="0"+syid;
		}
		zdzc.setSyid(syid);

		temp= ByteUtil.subBytes(sbody, 2, 2);
		String sxyid=String.valueOf(ByteUtil.byte2ToUnsignedShort(temp, 0));
		int num=4-sxyid.length();
		for(int k=0;k<num;k++){
			sxyid="0"+sxyid;
		}
		zdzc.setSxyid(sxyid);

		temp= ByteUtil.subBytes(sbody, 4, 5);
		zdzc.setZzsid(new String(temp));

		temp= ByteUtil.byteClearback(ByteUtil.subBytes(sbody, 9, 20));
		zdzc.setZdxh(new String(temp));

		temp= ByteUtil.byteClearback(ByteUtil.subBytes(sbody, 29, 7));
		zdzc.setXlh(new String(temp));

		temp= ByteUtil.subBytes(sbody, 36, 15);
		zdzc.setImei(new String(temp));

		zdzc.setZdcpys(String.valueOf((sbody[51] & 0xFF)));

		if(sbody.length>52){
			temp= ByteUtil.byteClearback(ByteUtil.subBytes(sbody, 52, sbody.length-52));
			try {
				zdzc.setZdcpbz(new String(temp,"GBK"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return zdzc;
	}

	public static ClearJlcd getClearJlcd(byte[] sbody){

		ClearJlcd clearJlcd=new ClearJlcd();

		clearJlcd.setNum(sbody[0]);
		try {
			List<String> slist = new ArrayList<>();
			if (sbody[0] != 0) {
				for (int i = 1; i < sbody.length; i = i + 4) {
					byte[] temp = ByteUtil.subBytes(sbody, i, 4);
					slist.add(String.format("%04d", ByteUtil.byte4ToInt(ByteUtil.subBytes(sbody, i, 4), 0)));
				}
			}
		}catch(Exception e){}

		return clearJlcd;
	}

	/**
	 * 电子围栏解析
	 * @param sbody
	 * @return
	 */
	public static Dzwl getDzwl(byte[] sbody){
		Dzwl dzwl=new Dzwl();
		byte[] temp=new byte[0];

		temp=ByteUtil.subBytes(sbody, 0, 4);
		dzwl.setCdbh(String.format("%04d", ByteUtil.byte4ToInt(temp, 0)));

		String s1= Integer.toBinaryString((sbody[4] & 0xFF) + 0x100).substring(1);
		String s2= Integer.toBinaryString((sbody[5] & 0xFF) + 0x100).substring(1);
		String s12=s1+s2;

		int t=0;
		if(s12.substring(s12.length()-1,s12.length()).equals("1")){
			temp=ByteUtil.subBytes(sbody, 6, 6);
			//dzwl.setStartTime(ByteUtil.bcd2Str(temp));

			temp=ByteUtil.subBytes(sbody, 12, 6);
			//dzwl.setEndTime(ByteUtil.bcd2Str(temp));

			t=12;
		}

		temp=ByteUtil.subBytes(sbody, 6+t, 2);
		int num=ByteUtil.byte2ToUnsignedShort(temp, 0);
		//dzwl.setQynum(ByteUtil.byte2ToUnsignedShort(temp, 0));

		//dzwl.setPakenum(sbody[8+t]);

		byte[] bytes=ByteUtil.subBytes(sbody,8+t,8*num);


		StringBuffer sb=new StringBuffer();
		for(int i=0;i<bytes.length;i=i+8){
			temp=ByteUtil.subBytes(bytes, i, 4);
			int k=ByteUtil.byte4ToInt(temp, 0);

			temp=ByteUtil.subBytes(bytes, i+4, 4);
			int j=ByteUtil.byte4ToInt(temp, 0);
			sb.append(j+","+k+";");
		}
		String ts=sb.toString();
		if(StringUtils.isNotEmpty(ts)){
			dzwl.setZbd(ts.substring(0,ts.length()-1));
		}

		temp=ByteUtil.subBytes(sbody,8+t+8*num,sbody.length-(8+t+8*num));
		try {
			String s=new String(temp,"GBK");
			String[] ss=s.split(";");
			if(ss.length>=4){
				dzwl.setCdmc(ss[0]);
				dzwl.setCddz(ss[1]);
				dzwl.setPxcx(ss[2]);
				dzwl.setCdlx(ss[3]);
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return dzwl;
	}

	public static DeviceInfo getDeviceInfo(byte[] sbody){
		DeviceInfo deviceInfo=new DeviceInfo();
		byte[] temp=ByteUtil.subBytes(sbody,0,1);
		deviceInfo.setRs(temp[0]);
		if(deviceInfo.getRs()==0){
			temp= ByteUtil.subBytes(sbody, 1, 8);
			String hs= ByteUtil.bytesToHexString(temp);
			hs= ByteUtil.clearHexStringfront(hs);
			temp= ByteUtil.hexStringToByte(hs);
			deviceInfo.setMobile(ByteUtil.bcd2Str(temp));

			temp= ByteUtil.subBytes(sbody, 9, 12);
			hs= ByteUtil.bytesToHexString(temp);
			hs= ByteUtil.clearHexStringfront(hs);
			temp= ByteUtil.hexStringToByte(hs);
			try {
				deviceInfo.setCp(new String(temp,"GBK"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			temp= ByteUtil.subBytes(sbody, 21, 1);
			try {
				deviceInfo.setCpys(new String(temp,"GBK"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return deviceInfo;
	}

	/**
	 * byte[]转终端注册回复
	 * @param sbody
	 * @return
	 */
	public static ZdzcR getZdzcR(byte[] sbody){
		ZdzcR zr=new ZdzcR();
		byte[] temp= ByteUtil.subBytes(sbody, 0, 2);
		zr.setLsh(ByteUtil.byte2ToUnsignedShort(temp, 0));

		temp= ByteUtil.subBytes(sbody, 2, 1);
		zr.setJg(temp[0]);

		if(zr.getJg()==0){
			temp= ByteUtil.subBytes(sbody, 3, 5);
			zr.setPtbh(new String(temp));

			temp= ByteUtil.subBytes(sbody,8,16);
			zr.setPxjgbh(new String(temp));

			temp= ByteUtil.subBytes(sbody,24,16);
			zr.setJszdbh(new String(temp));

			temp= ByteUtil.subBytes(sbody,40,12);
			String hs= ByteUtil.clearHexStringback(ByteUtil.bytesToHexString(temp));
			zr.setZskl(new String(ByteUtil.hexStringToByte(hs)));

			temp= ByteUtil.subBytes(sbody,52,sbody.length-53);
			zr.setZs(new String(temp));
		}
		return zr;
	}

	/*public static void main(String[] args) {
		String ptbh="18575320215";
		byte[] temp=ptbh.getBytes();
		System.out.println(new String(temp));

		temp=ByteUtil.str2Bcd(ptbh);
		if(temp.length<8){
			String hex=ByteUtil.bytesToHexString(temp);
			for(int i=temp.length;i<8;i++){
				hex="00"+hex;
			}
			temp=ByteUtil.hexStringToByte(hex);
		}
		System.out.println(temp.length);
		System.out.println(ByteUtil.bytesToHexString(temp));

		String hs=ByteUtil.bytesToHexString(temp);
    	hs=ByteUtil.clearHexStringfront(hs);
    	System.out.println(hs);
    	temp=ByteUtil.hexStringToByte(hs);
    	System.out.println(ByteUtil.bcd2Str(temp));
	}*/

	/**
	 * 获取终端鉴权
	 */
	public static Zdjq getZdjq(byte[] sbody){
		Zdjq zdjq=new Zdjq();
		byte[] temp= ByteUtil.subBytes(sbody, 0, 4);
		zdjq.setSjc(String.valueOf(ByteUtil.byte4ToInt(temp, 0)));

		if(sbody.length>4){
			temp= ByteUtil.subBytes(sbody, 4, 256);

			zdjq.setJqmw(ByteUtil.bytesToHexString(temp));
		}

		return zdjq;
	}

	/**
	 * B.4.1.2　扩展计时培训消息内容
	 */
	public static MsgExtend getMsgExtend(byte[] body){
		try {
			MsgExtend me = new MsgExtend();
			byte[] bs = ByteUtil.subBytes(body, 0, 1);
			me.setYwlx(ByteUtil.bytesToHexString(bs));

			byte[] sbody = ByteUtil.subBytes(body, 1, body.length - 1);
			byte[] temp = ByteUtil.subBytes(sbody, 0, 2);
			me.setMsgid(ByteUtil.bytesToHexString(temp));

			String s1 = Integer.toBinaryString((sbody[2] & 0xFF) + 0x100).substring(1);
			String s2 = Integer.toBinaryString((sbody[3] & 0xFF) + 0x100).substring(1);
			String s12 = s1 + s2;
			me.setMsgsx(s12);

			me.setSxx(s12.substring(15, 16));
			me.setYdsx(s12.substring(14, 15));

			String s = s12.substring(8, 12);
			me.setSfjm(String.valueOf(Integer.parseInt(s, 2)));

			temp = ByteUtil.subBytes(sbody, 4, 2);
			me.setJpbxh(ByteUtil.byte2ToUnsignedShort(temp, 0));

			temp = ByteUtil.subBytes(sbody, 6, 16);
			me.setJszdbh(new String(temp));

			temp = ByteUtil.subBytes(sbody, 22, 4);
			me.setSjcd(ByteUtil.byte4ToInt(temp, 0));

			temp = ByteUtil.subBytes(sbody, 26, me.getSjcd());
			me.setNr(temp);

			temp = ByteUtil.subBytes(sbody, 26 + me.getSjcd(), sbody.length - 26 - me.getSjcd());
			me.setSign(ByteUtil.bytesToHexString(temp));

			temp = ByteUtil.subBytes(sbody, 0, 26 + me.getSjcd());
			me.setContent(temp);

    	/*temp=ByteUtil.subBytes(sbody, 22, 2);
    	me.setSjcd(ByteUtil.byte2ToUnsignedShort(temp, 0));

    	temp=ByteUtil.subBytes(sbody, 24, me.getSjcd());
    	me.setNr(temp);

    	temp=ByteUtil.subBytes(sbody, 24+me.getSjcd(), sbody.length-24-me.getSjcd());
    	me.setSign(ByteUtil.bytesToHexString(temp));

    	temp=ByteUtil.subBytes(sbody, 0, 24+me.getSjcd());
    	me.setContent(temp);*/

		//解析消息体
		Object o = getObject(me.getMsgid(), me.getNr());
		me.setO(o);
		return me;
		}catch(Exception e){return null;}
	}

	//扩展消息体内容解析
	public static Object getObject(String msgid,byte[] sbody){
		 if(msgid.equals("8101")){
			//教练员登陆应答
			return ByteToCls.getJlydlR(sbody);
		}else if(msgid.equals("8102")){
			//教练员登出应答
			return ByteToCls.getJlydcR(sbody);
		}else if(msgid.equals("8201")){
			//学员登录应答
			return ByteToCls.getXydlR(sbody);
		}else if(msgid.equals("8202")){
			//学员登出应答
			return ByteToCls.getXydcR(sbody);
		}else if(msgid.equals("8205")){
			//命令上报学时记录
			return ByteToCls.getCxxsjl(sbody);
		}else if(msgid.equals("0205")){
			//命令上报学时记录应答
			return ByteToCls.getCxxsjlR(sbody);
		}else if(msgid.equals("8301")){
			//立即拍照
			return ByteToCls.getLjpz(sbody);
		}else if(msgid.equals("0301")){
			//立即拍照应答
			return ByteToCls.getLjpzR(sbody);
		}else if(msgid.equals("8302")){
			//查询照片
			return ByteToCls.getCxzp(sbody);
		}else if(msgid.equals("0302")){
			//查询照片应答
			return ByteToCls.getCxzpR(sbody);
		}else if(msgid.equals("0303")){
			//查询照片应答
			return ByteToCls.getSbzpjg(sbody);
		}else if(msgid.equals("8303")){
			//上报照片结果应答
			return ByteToCls.getSbzpjgR(sbody);
		}else if(msgid.equals("8304")){
			//上传指定照片
			return ByteToCls.getZdzp(sbody);
		}else if(msgid.equals("0304")||msgid.equals("8305")){
			//上传指定照片应答
			return ByteToCls.getSczdzpR(sbody);
		}else if(msgid.equals("8501")){
			//设置计时终端应用参数
			return ByteToCls.getYycs(sbody);
		}else if(msgid.equals("0501")){
			//设置应用参数回复
			return ByteToCls.getYycsR(sbody);
		}else if(msgid.equals("8502")){
			//设置禁训状态
			return ByteToCls.getJxzt(sbody);
		}else if(msgid.equals("0502")){
			//设置禁训状态回复
			return ByteToCls.getJxztR(sbody);
		}else if(msgid.equals("0503")){
			//查询应用参数回复
			return ByteToCls.getCxyycsR(sbody);
		}else if(msgid.equals("0401")){
			//请求身份认证
			return ByteToCls.getSfrz(sbody);
		}else if(msgid.equals("8401")){
			//请求身份认证回复
			return ByteToCls.getSfrzR(sbody);
		}else if(msgid.equals("0402")){
			//请求统一编号
			return ByteToCls.getQqtybh(sbody);
		}else if(msgid.equals("8402")){
			//请求统一编号应答
			return ByteToCls.getQqtybhR(sbody);
		}else if(msgid.equals("7001")){
			return ByteToCls.getImeiPasswordR(sbody);
		 }
		return null;
	}

	/**
	 * 教练员登陆应答解析
	 * @return
	 */
	public static JlydlR getJlydlR(byte[] sbody){
		try {
			JlydlR jr = new JlydlR();
			byte[] temp = ByteUtil.subBytes(sbody, 0, 1);
			jr.setJg(temp[0]);

			temp = ByteUtil.subBytes(sbody, 1, 16);
			jr.setJlbh(new String(temp));

			temp = ByteUtil.subBytes(sbody, 17, 1);
			jr.setSfbd(temp[0]);

			temp = ByteUtil.subBytes(sbody, 18, 1);
			jr.setFjxxcd(temp[0]);

			if (temp[0] > 0) {
				temp = ByteUtil.subBytes(sbody, 19, temp[0]);
				temp = ByteUtil.byteClearback(temp);
				try {
					jr.setFjxx(new String(temp, "GBK"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			return jr;
		}catch(Exception e){
			if(NettyConf.debug){
				Log.e("TAG",e.getMessage());
			}
			return null;
		}
	}


	/**
	 * 时间转化
	 * @param sj
	 * @return
	 */
	public static String sjzm(String sj){
		sj="20"+sj;
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			sj=sdf2.format(sdf.parse(sj));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return sj;
	}

	/**
	 * 教练员登出应答
	 * @param sbody
	 * @return
	 */
	public static JlydcR getJlydcR(byte[] sbody){
		try {
			JlydcR jr = new JlydcR();
			byte[] temp = ByteUtil.subBytes(sbody, 0, 1);
			jr.setJg(temp[0]);

			temp = ByteUtil.subBytes(sbody, 1, 16);
			jr.setJlbh(new String(temp));
			return jr;
		}catch(Exception e){
			if(NettyConf.debug){
				Log.e("TAG",e.getMessage());
			}
			return null;
		}
	}


	/**
	 * 学员登录应答解析
	 * @param sbody
	 * @return
	 */
	public static XydlR getXydlR(byte[] sbody){
		try {
			XydlR xr = new XydlR();
			byte[] temp = ByteUtil.subBytes(sbody, 0, 1);
			xr.setJg(temp[0]);

			temp = ByteUtil.subBytes(sbody, 1, 16);
			xr.setXybh(new String(temp));

			temp = ByteUtil.subBytes(sbody, 17, 2);
			xr.setZpxxs(ByteUtil.byte2ToUnsignedShort(temp, 0));

			temp = ByteUtil.subBytes(sbody, 19, 2);
			xr.setWcxs(ByteUtil.byte2ToUnsignedShort(temp, 0));

			temp = ByteUtil.subBytes(sbody, 21, 2);
			xr.setZpxlc(ByteUtil.byte2ToUnsignedShort(temp, 0));

			temp = ByteUtil.subBytes(sbody, 23, 2);
			xr.setWclc(ByteUtil.byte2ToUnsignedShort(temp, 0));

			temp = ByteUtil.subBytes(sbody, 25, 1);
			xr.setSfbd(temp[0]);

			temp = ByteUtil.subBytes(sbody, 26, 1);
			xr.setFjxxcd(temp[0]);

			if (temp[0] > 0) {
				temp = ByteUtil.subBytes(sbody, 27, temp[0]);
				temp = ByteUtil.byteClearback(temp);
				try {
					xr.setFjxx(new String(temp, "GBK"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			return xr;
		}catch(Exception e){
			if(NettyConf.debug) {
				Log.e("TAG", e.getMessage());
			}
			return null;
		}
	}

	/**
	 * 学员登出应答
	 * @return
	 */
	public static XydcR getXydcR(byte[] sbody){
		try {
			XydcR xr = new XydcR();
			byte[] temp = ByteUtil.subBytes(sbody, 0, 1);
			xr.setJg(temp[0]);

			temp = ByteUtil.subBytes(sbody, 1, 16);
			xr.setXybh(new String(temp));
			return xr;
		}catch(Exception e){return null;}
	}
	/**
	 * 获取身份认证消息
	 * @param
	 */
	public static Sfrz getSfrz(byte[] sbody){
		try {
			Sfrz sfrz = new Sfrz();
			sfrz.setXxlx(String.valueOf(sbody[0] & 0xFF));
			sfrz.setRylx(String.valueOf(sbody[1] & 0xFF));
			byte[] temp = ByteUtil.subBytes(sbody, 2, 18);
			String hs = ByteUtil.bytesToHexString(temp);
			hs = ByteUtil.clearHexStringfront(hs);
			temp = ByteUtil.hexStringToByte(hs);
			sfrz.setZjhm(new String(temp));
			return sfrz;
		}catch(Exception e){return null;}
	}

	/**
	 * 身份认证回复
	 * @return
	 */
	public static SfrzR getSfrzR(byte[] sbody){
		try {
			SfrzR sr = new SfrzR();

			byte[] temp = ByteUtil.subBytes(sbody, 0, 2);
			sr.setJpbxh(ByteUtil.byte2ToUnsignedShort(temp, 0));

			sr.setJg(sbody[2]);

			temp = ByteUtil.subBytes(sbody, 3, 4);
			sr.setXxcd(ByteUtil.byte4ToInt(temp, 0));

			temp = ByteUtil.subBytes(sbody, 7, sr.getXxcd());
			sr.setXx(ByteUtil.bytesToHexString(temp));


			int len = 7 + sr.getXxcd();

			if (sbody.length > len) {
				temp = ByteUtil.subBytes(sbody, len, 1);
				sr.setLx(temp[0]);

				temp = ByteUtil.subBytes(sbody, len + 1, 8);
				sr.setUuid(new String(temp));

				if (sbody.length > 9 + len) {
					temp = ByteUtil.subBytes(sbody, 9 + len, 16);
					sr.setTybh(new String(temp));
				}

				if (sbody.length > 25 + len) {
					temp = ByteUtil.subBytes(sbody, 25 + len, 18);
					String hs = ByteUtil.bytesToHexString(temp);
					hs = ByteUtil.clearHexStringfront(hs);
					temp = ByteUtil.hexStringToByte(hs);
					sr.setSfzh(new String(temp));

					if (sbody.length > 43 + len) {
						temp = ByteUtil.subBytes(sbody, 43 + len, 2);
						sr.setCx(new String(temp));
					}

					if (sbody.length > 45 + len) {
						int xmcd=sbody[45+len];
						temp = ByteUtil.subBytes(sbody, 46 + len, xmcd);
						try {
							sr.setXm(new String(temp, "GBK"));
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}

						if(sbody.length>46+len+xmcd){
							temp = ByteUtil.subBytes(sbody, 46+len+xmcd, 4);
							int zpcd=ByteUtil.byte4ToInt(temp, 0);

							temp = ByteUtil.subBytes(sbody, 50+len+xmcd, zpcd);
							sr.setZp(ByteUtil.bytesToHexString(temp));
						}
					}
				}
			}

			return sr;
		}catch(Exception e){return null;}
	}

	/**
	 * 请求统一编号
	 * @param
	 */
	public static Qqtybh getQqtybh(byte[] sbody){
		try {
			Qqtybh qqtybh = new Qqtybh();
			qqtybh.setBhlx(String.valueOf(sbody[0] & 0xFF));
			if (qqtybh.getBhlx().equals("2") || qqtybh.getBhlx().equals("3") || qqtybh.getBhlx().equals("4") || qqtybh.getBhlx().equals("6")) {
				byte[] temp = ByteUtil.subBytes(sbody, 1, 18);
				String hs = ByteUtil.bytesToHexString(temp);
				hs = ByteUtil.clearHexStringfront(hs);
				temp = ByteUtil.hexStringToByte(hs);
				qqtybh.setJszd(new String(temp));
			}

			return qqtybh;
		}catch(Exception e){
			return null;
		}
	}

	/**
	 * 请求统一编号应答
	 * @return
	 */
	public static QqtybhR getQqtybhR(byte[] sbody){
		try {
			QqtybhR qr = new QqtybhR();

			byte[] temp = ByteUtil.subBytes(sbody, 0, 2);
			qr.setJpbxh(ByteUtil.byte2ToUnsignedShort(temp, 0));

			qr.setJg(sbody[2]);

			temp = ByteUtil.subBytes(sbody, 3, 16);
			qr.setTybh(new String(temp));

			temp = ByteUtil.subBytes(sbody, 19, 2);
			String hs = ByteUtil.bytesToHexString(temp);
			qr.setZjcx(ByteUtil.clearHexStringfront(hs));
			return qr;
		}catch(Exception e){return null;}
	}

	public static ImeiPasswordR getImeiPasswordR(byte[] sbody){
		try {
			ImeiPasswordR ir = new ImeiPasswordR();
			ir.setJg(sbody[0]);
			ir.setType(sbody[1]);
			return ir;
		}catch(Exception e){return null;}
	}

	/**
	 * 获取客户端回复的通用信息
	 * @param sbody
	 * @return
	 */
	public static CommonR getCommonR(byte[] sbody){
		try {
			CommonR cr = new CommonR();

			byte[] temp = ByteUtil.subBytes(sbody, 0, 2);
			cr.setLsh(ByteUtil.byte2ToUnsignedShort(temp, 0));

			temp = ByteUtil.subBytes(sbody, 2, 2);
			cr.setMsgid(ByteUtil.bytesToHexString(temp));

			cr.setJg(sbody[4] & 0xFF);
			return cr;
		}catch(Exception e){return null;}
	}

	/**
	 * 终端参数设置解析
	 * @param sbody
	 */
	public static ParamsSz getParamsSz(byte[] sbody){
		try {
			ParamsSz pz = new ParamsSz();
			byte[] temp = ByteUtil.subBytes(sbody, 0, 1);
			pz.setCount(temp[0]);

			temp = ByteUtil.subBytes(sbody, 1, 1);
			pz.setFsnum(temp[0]);

			//循环获取参数设置数据
			List<Parameter> paramList = new ArrayList<Parameter>();
			int start = 2;
			for (int i = 1; i <= pz.getFsnum(); i++) {
				Parameter p = new Parameter();
				temp = ByteUtil.subBytes(sbody, start, 4);
				String hs = ByteUtil.bytesToHexString(temp);
				hs = hs.substring(hs.length() - 4, hs.length());
				p.setId(hs);
				temp = ByteUtil.subBytes(sbody, start + 4, 1);
				p.setCd(temp[0]);
				temp = ByteUtil.subBytes(sbody, start + 5, p.getCd());
				//获取数据类型
				String type = ParamType.getValue(p.getId());
				if (type.equals("int")) {
					p.setValue(String.valueOf(ByteUtil.byte4ToInt(temp, 0)));
				} else if (type.equals("short")) {
					p.setValue(String.valueOf(ByteUtil.byte2ToUnsignedShort(temp, 0)));
				} else if (type.equals("byte")) {
					p.setValue(String.valueOf(temp[0]));
				} else if (type.equals("string")) {
					//去除零位
					temp = ByteUtil.subBytes(temp, 0, temp.length - 1);
					p.setValue(new String(temp,"GBK"));
				}
				paramList.add(p);
				//起始位置重置
				start = start + 5 + p.getCd();
			}
			pz.setParamList(paramList);
			return pz;
		}catch(Exception e){return null;}
	}

	/**
	 * 终端参数设置解析
	 * @param sbody
	 */
	public static ParamsSzAll getParamsSzAll(byte[] sbody){
		try {
			ParamsSzAll pz = new ParamsSzAll();
			byte[] temp = ByteUtil.subBytes(sbody, 0, 4);
			pz.setVersion(ByteUtil.byte4ToInt(temp, 0));

			temp = ByteUtil.subBytes(sbody, 4, 1);
			pz.setCount(temp[0]);

			temp = ByteUtil.subBytes(sbody, 5, 1);
			pz.setFsnum(temp[0]);

			//循环获取参数设置数据
			List<Parameter> paramList = new ArrayList<Parameter>();
			int start = 6;
			for (int i = 1; i <= pz.getFsnum(); i++) {
				Parameter p = new Parameter();
				temp = ByteUtil.subBytes(sbody, start, 4);
				String hs = ByteUtil.bytesToHexString(temp);
				hs = hs.substring(hs.length() - 4, hs.length());
				p.setId(hs);
				temp = ByteUtil.subBytes(sbody, start + 4, 1);
				p.setCd(temp[0]);
				temp = ByteUtil.subBytes(sbody, start + 5, p.getCd());
				//获取数据类型
				String type = ParamType.getValue(p.getId());
				if (type.equals("int")) {
					p.setValue(String.valueOf(ByteUtil.byte4ToInt(temp, 0)));
				} else if (type.equals("short")) {
					p.setValue(String.valueOf(ByteUtil.byte2ToUnsignedShort(temp, 0)));
				} else if (type.equals("byte")) {
					p.setValue(String.valueOf(temp[0]));
				} else if (type.equals("string")) {
					//去除零位
					temp = ByteUtil.subBytes(temp, 0, temp.length - 1);
					p.setValue(new String(temp));
				}
				paramList.add(p);
				//起始位置重置
				start = start + 5 + p.getCd();
			}
			pz.setParamList(paramList);
			return pz;
		}catch(Exception e){return null;}
	}

	/**
	 * 查询参数解析
	 * @return
	 */
	public static Cxcs getCxcs(byte[] sbody){
		try {
			Cxcs cxcs = new Cxcs();
			byte[] temp = ByteUtil.subBytes(sbody, 0, 1);
			cxcs.setCount(temp[0]);
			String ids = "";
			for (int i = 0; i < cxcs.getCount(); i++) {
				temp = ByteUtil.subBytes(sbody, 1 + i * 4, 4);
				String hs = ByteUtil.bytesToHexString(temp);
				ids = ids + hs.substring(hs.length() - 4, hs.length()) + ",";
			}
			if (ids.length() > 1) {
				ids = ids.substring(0, ids.length() - 1);
			}
			cxcs.setIds(ids);
			return cxcs;
		}catch(Exception e){return null;}
	}

	/**
	 * 获取查询终端参数回复的结果
	 * @param
	 */
	public static CxcsR getCxcsR(byte[] sbody){
		try {
			CxcsR cr = new CxcsR();
			byte[] temp = ByteUtil.subBytes(sbody, 0, 2);
			cr.setLsh(ByteUtil.byte2ToUnsignedShort(temp, 0));

			cr.setCscount(sbody[2] & 0xFF);

			cr.setCsnum(sbody[3] & 0xFF);

			List<Parameter> pList = new ArrayList<Parameter>();
			int k = 4;//起始字节下标
			for (int i = 0; i < cr.getCsnum(); i++) {
				Parameter p = new Parameter();
				temp = ByteUtil.subBytes(sbody, k, 4);
				String hs = ByteUtil.bytesToHexString(temp);
				//获取参数ID
				hs = hs.substring(hs.length() - 4, hs.length());

				p.setId(hs);

				//对应的字节数组长度
				int cd = (sbody[k + 4] & 0xFF);
				p.setCd(cd);
				temp = ByteUtil.subBytes(sbody, k + 5, cd);

				String type = ParamType.getValue(hs);
				if (type.equals("int")) {
					p.setValue(String.valueOf(ByteUtil.byte4ToInt(temp, 0)));
				} else if (type.equals("short")) {
					p.setValue(String.valueOf(ByteUtil.byte2ToUnsignedShort(temp, 0)));
				} else if (type.equals("byte")) {
					p.setValue(String.valueOf(temp[0] & 0xFF));
				} else {
					temp = ByteUtil.byteClearback(temp);
					p.setValue(new String(temp));
				}
				pList.add(p);

				k = k + 5 + cd;
			}
			cr.setpList(pList);
			return cr;
		}catch(Exception e){return null;}
	}


	/**
	 * 解析终端控制
	 * @param sbody
	 * @return
	 */
	public static Zdkz getZdkz(byte[] sbody){
		try {
			Zdkz zdkz = new Zdkz();

			byte[] temp = ByteUtil.subBytes(sbody, 0, 1);
			zdkz.setMlz(String.valueOf(temp[0]));

			String type = "";
			if (zdkz.getMlz().equals("1")) {
				type = "string,string,string,string,string,short,short,str,string,string,short";
			} else if (zdkz.getMlz().equals("2")) {
				type = "byte,string,string,string,string,string,short,short,short";
			}
			temp = ByteUtil.subBytes(sbody, 1, sbody.length - 1);
			String sep = ByteUtil.bytesToHexString(";".getBytes());
			String hs = ByteUtil.bytesToHexString(temp);
			String[] ss = hs.split(sep);

			String[] types = type.split(",");
			String mlcs = "";
			int num = 0;
			for (int i = 0; i < ss.length; i++) {
				String tempstr = "";
				if (StringUtils.isNotEmpty(ss[i])) {
					temp = ByteUtil.hexStringToByte(ss[i]);
					if (types[i].equals("string")) {
						try {
							tempstr = new String(temp, "GBK");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					} else if (types[i].equals("str")) {
						tempstr = new String(temp);
					} else if (types[i].equals("short")) {
						tempstr = String.valueOf(ByteUtil.byte2ToUnsignedShort(temp, 0));
					} else if (types[i].equals("byte")) {
						tempstr = String.valueOf(temp[0]);
					}
				}
				mlcs = mlcs + tempstr + ";";
			}
			if (StringUtils.isNotEmpty(mlcs)) {
				mlcs = mlcs.substring(0, mlcs.length() - 1);
				zdkz.setMlcs(mlcs);
			}

			return zdkz;
		}catch(Exception e){return null;}
	}


	/**
	 * 跟踪控制解析
	 * @return
	 */
	public static Gzkz getGzkz(byte[] sbody){
		try {
			Gzkz gzkz = new Gzkz();
			byte[] temp = ByteUtil.subBytes(sbody, 0, 2);
			int jg = ByteUtil.byte2ToUnsignedShort(temp, 0);
			gzkz.setSjjg(String.valueOf(jg));

			if (jg > 0) {
				temp = ByteUtil.subBytes(sbody, 2, 4);
				gzkz.setYxq(String.valueOf(ByteUtil.byte4ToInt(temp, 0)));
			}
			return gzkz;
		}catch(Exception e){return null;}
	}

	/**
	 * 立即拍照解析
	 * @return
	 */
	public static Ljpz getLjpz(byte[] sbody){
		try {
			Ljpz ljpz = new Ljpz();
			ljpz.setScms(String.valueOf(sbody[0]));


			ljpz.setTdh(String.valueOf(sbody[1]));

			ljpz.setTpcc(String.valueOf(sbody[2]));
			return ljpz;
		}catch(Exception e){
			return null;
		}
	}

	/**
	 * 立即拍照应答解析
	 * @param
	 */
	public static LjpzR getLjpzR(byte[] sbody){
		try {
			LjpzR lr = new LjpzR();
			lr.setJg(sbody[0] & 0xFF);
			lr.setScms(sbody[1] & 0xFF);
			lr.setTdh(sbody[2] & 0xFF);

			byte[] temp = new byte[1];
			temp[0] = sbody[3];
			lr.setSjcc(ByteUtil.bytesToHexString(temp));
			return lr;
		}catch(Exception e){return null;}
	}


	/**
	 * 查询学时记录解析
	 * @return
	 */
	public static Cxxsjl getCxxsjl(byte[] sbody){
		try {
			Cxxsjl c = new Cxxsjl();
			byte[] temp = ByteUtil.subBytes(sbody, 0, 1);
			c.setCxfs(String.valueOf(temp[0]));

			temp = ByteUtil.subBytes(sbody, 1, 6);
			c.setKssj(ByteUtil.bcd2Str(temp));

			temp = ByteUtil.subBytes(sbody, 7, 6);
			c.setJssj(ByteUtil.bcd2Str(temp));

			temp = ByteUtil.subBytes(sbody, 13, 2);
			c.setCxts(String.valueOf(ByteUtil.byte2ToUnsignedShort(temp, 0)));
			return c;
		}catch(Exception e){
			return null;
		}
	}

	/**
	 * 获取上报学时记录查询结果
	 * @param
	 */
	public static CxxsjlR getCxxsjlR(byte[] sbody){
		try {
			CxxsjlR cr = new CxxsjlR();
			cr.setJg(sbody[0] & 0xFF);
			return cr;
		}catch(Exception e){return null;}
	}

	/**
	 * 查询照片解析
	 * @param sbody
	 * @return
	 */
	public static Cxzp getCxzp(byte[] sbody){
		try {
			Cxzp cxzp = new Cxzp();

			byte[] temp = ByteUtil.subBytes(sbody, 0, 1);
			cxzp.setCxfs(String.valueOf(temp[0]));

			temp = ByteUtil.subBytes(sbody, 1, 6);
			cxzp.setKssj(ByteUtil.bcd2Str(temp));

			temp = ByteUtil.subBytes(sbody, 7, 6);
			cxzp.setJssj(ByteUtil.bcd2Str(temp));
			return cxzp;
		}catch(Exception e){return null;}
	}

	/**
	 * 查询照片回复
	 * @param sbody
	 * @return
	 */
	public static CxzpR getCxzpR(byte[] sbody){
		try {
			CxzpR cr = new CxzpR();
			cr.setJg(sbody[0] & 0xFF);
			return cr;
		}catch(Exception e){return null;}
	}


	/**
	 * 获取上报照片结果数据
	 * @param sbody
	 * @return
	 */
	public static Sbzpjg getSbzpjg(byte[] sbody){
		try {
			Sbzpjg sg = new Sbzpjg();
			sg.setSfjs(sbody[0] & 0xFF);

			sg.setZnum(sbody[1] & 0xFF);
			sg.setNum(sbody[2] & 0xFF);
			List<String> sList = new ArrayList<String>();
			byte[] bs = ByteUtil.subBytes(sbody, 3, sbody.length - 3);
			byte[] temp = new byte[0];
			for (int i = 0; i < bs.length; i = i + 10) {
				temp = ByteUtil.subBytes(bs, i, 10);
				String bh = new String(temp);
				sList.add(bh);
			}
			sg.setsList(sList);
			return sg;
		}catch(Exception e){return null;}

	}

	/**
	 * 上报照片结果应答
	 * @return
	 */
	public static SbzpjgR getSbzpjgR(byte[] sbody){
		try {
			SbzpjgR sr = new SbzpjgR();
			sr.setJg(sbody[0]);
			return sr;
		}catch(Exception e){return null;}
	}

	/**
	 * 指定照片应答
	 * @param sbody
	 * @return
	 */
	public static Zdzp getZdzp(byte[] sbody){
		try {
			Zdzp z = new Zdzp();
			z.setZpbh(new String(sbody));
			return z;
		}catch(Exception e){return null;}
	}

	/**
	 * 上传指定照片应答
	 * @param sbody
	 * @return
	 */
	public static SczdzpR getSczdzpR(byte[] sbody){
		try {
			SczdzpR sr = new SczdzpR();
			sr.setJg(sbody[0] & 0xFF);
			return sr;
		}catch(Exception e){return null;}
	}

	/**
	 * 应用参数解析
	 * @return
	 */
	public static Yycs getYycs(byte[] sbody){
		try {
			Yycs yycs = new Yycs();
			yycs.setCsbh(String.valueOf(sbody[0]));

			yycs.setPzsjjg(String.valueOf(sbody[1]));

			yycs.setZpscsz(String.valueOf(sbody[2]));

			yycs.setSfbdfjxx(String.valueOf(sbody[3]));

			yycs.setXhyssj(String.valueOf(sbody[4]));

			byte[] temp = ByteUtil.subBytes(sbody, 5, 2);
			yycs.setScjg(String.valueOf(ByteUtil.byte2ToUnsignedShort(temp, 0)));

			temp = ByteUtil.subBytes(sbody, 7, 2);
			yycs.setDcyssj(String.valueOf(ByteUtil.byte2ToUnsignedShort(temp, 0)));

			temp = ByteUtil.subBytes(sbody, 9, 2);
			yycs.setCxyzsj(String.valueOf(ByteUtil.byte2ToUnsignedShort(temp, 0)));

			yycs.setJlkxxx(String.valueOf(sbody[11]));

			yycs.setXykxxx(String.valueOf(sbody[12]));

			temp = ByteUtil.subBytes(sbody, 13, 2);
			yycs.setTlptsj(String.valueOf(ByteUtil.byte2ToUnsignedShort(temp, 0)));
			return yycs;
		}catch(Exception e){return null;}
	}

	/**
	 * 应用参数回复
	 * @param sbody
	 * @return
	 */
	public static YycsR getYycsR(byte[] sbody){
		try {
			YycsR yr = new YycsR();
			yr.setJg(sbody[0] & 0xFF);
			return yr;
		}catch(Exception e){return null;}
	}

	/**
	 * 禁训状态解析
	 * @return
	 */
	public static Jxzt getJxzt(byte[] sbody){
		try {
			Jxzt j = new Jxzt();

			j.setZt(String.valueOf(sbody[0]));

			int len = sbody[1];

			if (len > 0) {
				byte[] temp = ByteUtil.subBytes(sbody, 2, len);
				try {
					j.setTsxx(new String(temp, "GBK"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

			return j;
		}catch(Exception e){return null;}
	}

	/**
	 * 禁训状态恢复
	 * @return
	 */
	public static JxztR getJxztR(byte[] sbody){
		try {
			JxztR jr = new JxztR();
			jr.setJg(sbody[0] & 0xFF);
			jr.setZt(sbody[1] & 0xFF);
			jr.setCd(sbody[2] & 0xFF);
			byte[] temp = ByteUtil.subBytes(sbody, 3, jr.getCd());
			jr.setTsxx(new String(temp));
			return jr;
		}catch(Exception e){return null;}
	}

	/**
	 * 获取查询应用参数回复结果
	 * @param sbody
	 * @return
	 */
	public static CxyycsR getCxyycsR(byte[] sbody){
		try {
			CxyycsR cr = new CxyycsR();
			cr.setJg(sbody[0] & 0xFF);
			cr.setPzsjjg(String.valueOf(sbody[1] & 0xFF));
			cr.setZpscsz(String.valueOf(sbody[2] & 0xFF));
			cr.setSfbdfjxx(String.valueOf(sbody[3] & 0xFF));
			cr.setXhyssj(String.valueOf(sbody[4] & 0xFF));

			byte[] temp = ByteUtil.subBytes(sbody, 5, 2);
			cr.setScjg(String.valueOf(ByteUtil.byte2ToUnsignedShort(temp, 0)));

			temp = ByteUtil.subBytes(sbody, 7, 2);
			cr.setDcyssj(String.valueOf(ByteUtil.byte2ToUnsignedShort(temp, 0)));

			temp = ByteUtil.subBytes(sbody, 9, 2);
			cr.setCxyzsj(String.valueOf(ByteUtil.byte2ToUnsignedShort(temp, 0)));

			cr.setJlkxxx(String.valueOf(sbody[11] & 0xFF));

			cr.setXykxxx(String.valueOf(sbody[12] & 0xFF));

			temp = ByteUtil.subBytes(sbody, 13, 2);
			cr.setTlptsj(String.valueOf(ByteUtil.byte2ToUnsignedShort(temp, 0)));
			return cr;
		}catch(Exception e){return null;}
	}


	/**
	 * 补传分包解析
	 * @param sbody
	 * @return
	 */
	public static Bcfb getBcfb(byte[] sbody){
		try {
			Bcfb bcfb = new Bcfb();
			byte[] temp = ByteUtil.subBytes(sbody, 0, 2);
			bcfb.setYslsh(ByteUtil.byte2ToUnsignedShort(temp, 0));

			bcfb.setGs(sbody[2]);

			byte[] bs = ByteUtil.subBytes(sbody, 3, sbody.length - 3);
			System.out.println(bs.length);
			String xhs = "";
			for (int i = 0; i < bs.length; i = i + 2) {
				temp = ByteUtil.subBytes(bs, i, 2);
				xhs = xhs + ByteUtil.byte2ToUnsignedShort(temp, 0) + ";";
			}
			xhs = xhs.substring(0, xhs.length() - 1);
			bcfb.setXhs(xhs);
			return bcfb;
		}catch(Exception e){return null;}
	}

	/**
	 * 升级指令
	 * @param sbody
	 * @return
     */
	public static Upgrade getUpgrade(byte[] sbody){
		try {
			Upgrade upgrade = new Upgrade();
			try {
				upgrade.setMsg(new String(sbody, "GBK"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return upgrade;
		}catch(Exception e){return null;}
	}

}
