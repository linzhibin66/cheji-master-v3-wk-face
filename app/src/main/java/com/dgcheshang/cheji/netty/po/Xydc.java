package com.dgcheshang.cheji.netty.po;

import com.dgcheshang.cheji.netty.util.ByteUtil;

/**
 * 学员登出
 * @author Administrator
 *
 */
public class Xydc implements java.io.Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String xybh;//学员编号
	private String dcsj;//登出时间
	private String dlzsj;//学员该次登录总时间
	private String dlzlc;//学员该次登录总里程
	private String ktid;//课堂Id
	private String gnss;


	public String getXybh() {
		return xybh;
	}

	public void setXybh(String xybh) {
		this.xybh = xybh;
	}

	public String getDcsj() {
		return dcsj;
	}

	public void setDcsj(String dcsj) {
		this.dcsj = dcsj;
	}

	public String getDlzsj() {
		return dlzsj;
	}

	public void setDlzsj(String dlzsj) {
		this.dlzsj = dlzsj;
	}

	public String getDlzlc() {
		return dlzlc;
	}

	public void setDlzlc(String dlzlc) {
		this.dlzlc = dlzlc;
	}

	public String getKtid() {
		return ktid;
	}

	public void setKtid(String ktid) {
		this.ktid = ktid;
	}

	public String getGnss() {
		return gnss;
	}

	public void setGnss(String gnss) {
		this.gnss = gnss;
	}

	public byte[] getXydcBytes(){
		byte[] b=new byte[0];
		byte[] temp=xybh.getBytes();
		b=ByteUtil.byteMerger(b, temp);

		temp= ByteUtil.str2Bcd(dcsj);
		b=ByteUtil.byteMerger(b, temp);

		temp=ByteUtil.shortToByteArray(Short.valueOf(dlzsj));
		b=ByteUtil.byteMerger(b, temp);

		temp=ByteUtil.shortToByteArray((short)(Double.valueOf(dlzlc)*10));
		b=ByteUtil.byteMerger(b, temp);

		temp=ByteUtil.intToByteArray(Integer.valueOf(ktid));
		b=ByteUtil.byteMerger(b, temp);

		temp=ByteUtil.hexStringToByte(gnss);
		b=ByteUtil.byteMerger(b, temp);
		return b;
	}

}
