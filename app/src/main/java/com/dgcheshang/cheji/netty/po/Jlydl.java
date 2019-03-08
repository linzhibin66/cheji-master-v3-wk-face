package com.dgcheshang.cheji.netty.po;

import com.dgcheshang.cheji.netty.util.ByteUtil;

/**
 * 教练员登陆
 * @author Administrator
 *
 */
public class Jlydl implements java.io.Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String jlybh;//教练员编号
	private String jlyzjhm;//教练员身份证号
	private String zjcx;//准教车型
	private String gnss;

	public String getJlybh() {
		return jlybh;
	}
	public void setJlybh(String jlybh) {
		this.jlybh = jlybh;
	}
	public String getJlyzjhm() {
		return jlyzjhm;
	}
	public void setJlyzjhm(String jlyzjhm) {
		this.jlyzjhm = jlyzjhm;
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

	public byte[] getJlydlbytes(){
		byte[] b=new byte[0];
		byte[] temp=jlybh.getBytes();
		b= ByteUtil.byteMerger(b, temp);

		temp=jlyzjhm.getBytes();
		temp=ByteUtil.hexStringTOFinalbyte(ByteUtil.bytesToHexString(temp), 18, 1);
		b=ByteUtil.byteMerger(b, temp);

		temp=ByteUtil.hexStringTOFinalbyte(ByteUtil.bytesToHexString(zjcx.getBytes()),2,1);
		b=ByteUtil.byteMerger(b, temp);

		temp=ByteUtil.hexStringToByte(gnss);
		b=ByteUtil.byteMerger(b, temp);

		return b;
	}

}
