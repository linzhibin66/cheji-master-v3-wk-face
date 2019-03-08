package com.dgcheshang.cheji.netty.serverreply;

import com.dgcheshang.cheji.netty.util.ByteUtil;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.StringUtils;

public class DeviceInfo implements Serializable{
	private int rs;//结果 0，成功 1失败(没后续字段)
	private String mobile;
	private String cp;
	private String cpys;

	public int getRs() {
		return rs;
	}

	public void setRs(int rs) {
		this.rs = rs;
	}

	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getCp() {
		return cp;
	}
	public void setCp(String cp) {
		this.cp = cp;
	}
	public String getCpys() {
		return cpys;
	}
	public void setCpys(String cpys) {
		this.cpys = cpys;
	}

	public byte[] getDeviceInfoBytes(){
		byte[] bs=new byte[0];

		//终端手机号
		byte[] temp= ByteUtil.str2Bcd(this.mobile);
		if(temp.length<8){
			String hex=ByteUtil.bytesToHexString(temp);
			for(int i=temp.length;i<8;i++){
				hex="00"+hex;
			}
			temp=ByteUtil.hexStringToByte(hex);
		}

		bs=ByteUtil.byteMerger(bs, temp);

		try {
			temp=cp.getBytes("GBK");
			temp=ByteUtil.hexStringTOFinalbyte(ByteUtil.bytesToHexString(temp), 12, 1);
			bs=ByteUtil.byteMerger(bs, temp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		if(StringUtils.isEmpty(cpys)){
			cpys="2";
		}
		temp=cpys.getBytes();
		bs=ByteUtil.byteMerger(bs, temp);

		return bs;
	}

}
