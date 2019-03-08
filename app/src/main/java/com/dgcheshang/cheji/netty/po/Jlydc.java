package com.dgcheshang.cheji.netty.po;


import com.dgcheshang.cheji.netty.util.ByteUtil;

/**
 * 教练员登出
 * @author Administrator
 *
 */
public class Jlydc implements java.io.Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String sxx;//时效性
	private String jszdbh;//计时终端编号
	private String jlybh;//教练员编号
	private String zjcx;//准教车型
	private String gnss;

	public String getSxx() {
		return sxx;
	}
	public void setSxx(String sxx) {
		this.sxx = sxx;
	}
	public String getJszdbh() {
		return jszdbh;
	}
	public void setJszdbh(String jszdbh) {
		this.jszdbh = jszdbh;
	}
	public String getJlybh() {
		return jlybh;
	}
	public void setJlybh(String jlybh) {
		this.jlybh = jlybh;
	}
	public String getZjcx() {
		return zjcx;
	}
	public void setZjcx(String zjcx) {
		this.zjcx = zjcx;
	}
	public String getGnss() {
		return gnss;
	}
	public void setGnss(String gnss) {
		this.gnss = gnss;
	}

	public byte[] getJlydcBytes(){
		byte[] b=new byte[0];
		byte[] temp=jlybh.getBytes();
		b=ByteUtil.byteMerger(b, temp);

		temp= ByteUtil.hexStringToByte(gnss);
		b=ByteUtil.byteMerger(b, temp);
		return b;
	}
}
