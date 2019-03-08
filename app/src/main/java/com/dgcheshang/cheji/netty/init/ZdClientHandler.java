package com.dgcheshang.cheji.netty.init;

import android.util.Log;
import com.dgcheshang.cheji.netty.conf.NettyConf;
import com.dgcheshang.cheji.netty.timer.ConTimer;
import com.dgcheshang.cheji.netty.util.ByteUtil;
import com.dgcheshang.cheji.netty.util.GatewayService;
import com.dgcheshang.cheji.netty.util.MsgUtil;
import com.dgcheshang.cheji.netty.util.ZdUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class ZdClientHandler extends SimpleChannelInboundHandler{

	private static ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		Log.e("TAG","连接异常！");
		super.exceptionCaught(ctx, cause);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
        Log.e("TAG","终端连接服务器激活成功");
		GatewayService.addGatewayChannel("serverChannel",ctx);
		NettyConf.constate=1;

		if(NettyConf.zcstate==0){
			//没注册过注册
			ZdUtil.sendZdzc();
		}else if(NettyConf.zcstate==1&NettyConf.jqstate==0){
			//发送终端鉴权
			ZdUtil.sendZdjqHex();
		}

	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

		if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
			IdleStateEvent event = (IdleStateEvent) evt;
			if (event.state() == IdleState.READER_IDLE){
				if(NettyConf.debug){
					Log.e("TAG","90秒内没收到任何回复包括心跳回复");
				}
				ctx.close();

			}
			else if (event.state() == IdleState.WRITER_IDLE){
				String mobile="";
				if(StringUtils.isNotEmpty(NettyConf.mobile)){
					mobile=NettyConf.mobile;
				}else{
					mobile=NettyConf.imei;
				}
				String hexmsg = MsgUtil.getXthf(mobile);
				if (StringUtils.isNotEmpty(hexmsg)) {
					ctx.writeAndFlush(hexmsg);
				}
			}
			else if (event.state() == IdleState.ALL_IDLE){
				System.out.println("all idle");
			}

		}
	}


//	@Override
//	protected void channelRead0(ChannelHandlerContext ctx, String msg)
//			throws Exception {
//		// 收到消息直接打印输出
//		if(NettyConf.debug) {
//			Log.e("TAG", ctx.channel().remoteAddress() + " server say: " + msg);
//		}
//		if(StringUtils.isEmpty(msg)){
//			//消息为空直接丢弃
//		}else{
//			ZdHandleMsg zm=new ZdHandleMsg(msg);
//
//			cachedThreadPool.execute(zm);
//			/*Thread th=new Thread(zm);
//			th.start();*/
//		}
//
//	}


	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		GatewayService.removeGatewayChannel("serverChannel");
		NettyConf.constate=0;
		NettyConf.jqstate=0;
		Log.e("TAG","断开连接重连！");
		new Timer().schedule(new ConTimer(),3000);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf buf=(ByteBuf) msg;
		try{
			int buflen=buf.readableBytes();
			if(buflen>0){
				byte[] bs=new byte[buf.readableBytes()];
				buf.readBytes(bs);
				String hs= ByteUtil.bytesToHexString(bs);
				ZdHandleMsg zm=new ZdHandleMsg(hs);
				cachedThreadPool.execute(zm);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		buf.release();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {

	}
}
