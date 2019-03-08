package com.dgcheshang.cheji.netty.util;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUtil {

	/**
	 * 北京时间
	 */
	public static String getBjtime(){
		String bjTime="";
		try{
			//java.util.Locale locale=java.util.Locale.CHINA; //这是获得本地中国时区

			String pattern = "yyyy-MM-dd kk:mm:ss";//这是日期格式
			SimpleDateFormat df = new SimpleDateFormat(pattern);//设定日期格式
			Date date = new Date();
			URL url=new URL("http://www.beijing-time.org/");//取得资源对象
			java.net.URLConnection uc=url.openConnection();//生成连接对象
			uc.connect(); //发出连接
			long ld=uc.getDate(); //取得网站日期时间
			date=new Date(ld); //转换为标准时间对象
			bjTime = df.format(date);
		}catch(Exception e){
			e.printStackTrace();
		}
		return bjTime;
	}

	/**
	 * 北京时间
	 */
	public static String getBdtime(){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}

	public static void main(String[] args) {
		System.out.println(CommonUtil.getBdtime());
	}

	/**
	 * 获取时间差
	 * @return
	 */
	public static long getsjc(String kssj,String jssj){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long sjc=0;
		try {
			Date d1=sdf.parse(kssj);
			Date d2=sdf.parse(jssj);
			sjc=((d2.getTime()-d1.getTime())/1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sjc;
	}

	/**
	 * 获取该部分应学时长
	 * @return
	 */
	public static String getPartsc(String pxcx,String pxbf){
		String key="part"+pxbf+"rkxs";
		if(pxbf.equals("2")){
			key+=pxcx;
		}
		return key.toLowerCase();
	}

	/**
	 * 考核结果转码
	 */
	public static String zmkhjg(String jg){
		if(jg.equals("0")){
			return "未考核";
		}else if(jg.equals("1")){
			return "合格";
		}else{
			return "不合格";
		}
	}

	/**
	 * 证件类型转码
	 */
	public static String zmzjlx(String zjlx){
		return "身份证";
	}

	/**
	 * 第及部分转码
	 */
	public static String zmsubject(String subject){
		return "B"+subject;
	}

	public static String zmsubjectF(String subject){
		if(subject.equals("1")){
			return "";
		}else{
			return "B"+(Integer.parseInt(subject)-1);
		}
	}

	/**
	 * 随机重连时间
	 * @return
	 */
	public static int getRandStart(){
		return (int) Math.round(Math.random()*30+120);
	}
}


