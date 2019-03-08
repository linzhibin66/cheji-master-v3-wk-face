package com.dgcheshang.cheji.netty.init;

import android.util.Log;

import com.dgcheshang.cheji.netty.conf.NettyConf;
import com.dgcheshang.cheji.netty.timer.ConTimer;
import com.dgcheshang.cheji.netty.util.CommonUtil;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.util.Timer;

public class ZdClient{

    private static Bootstrap b;
    public static Timer conTimer=null;//连接服务器的定时器

    public void run() throws Exception {
        if(NettyConf.debug) {
            Log.e("TAG", "启动连接！");
        }
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ZdClientInitializer());
            //自动调整下一次缓冲区建立时分配的空间大小，避免内存的浪费
            b.option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT);
            //使用内存池
            b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

            b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS,30*1000);
            // 连接服务端

            if(NettyConf.debug) {
                Log.e("TAG", NettyConf.host + ":" + NettyConf.port);
            }

            ChannelFuture f = b.connect(NettyConf.host, NettyConf.port).sync();

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } catch(Exception e){
            new Timer().schedule(new ConTimer(), CommonUtil.getRandStart()*1000);
        }finally {
            // The connection is closed automatically on shutdown.
            group.shutdownGracefully();
        }
    }
}

