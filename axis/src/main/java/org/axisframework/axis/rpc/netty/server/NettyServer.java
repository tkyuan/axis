/**
 * 
 */
package org.axisframework.axis.rpc.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author yuantengkai
 *
 */
public class NettyServer {
	
	private final int port;
	
	private final Map<String, Object> servicesMap;
	private final AtomicBoolean startFlag = new AtomicBoolean(false);
	
	private ServerBootstrap serverBootstrap;
	private ChannelFuture future;
	private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
	
	public NettyServer(int port, Map<String, Object> servicesMap){
		this.port = port;
		this.servicesMap = servicesMap;
		bossGroup = new NioEventLoopGroup();
		workerGroup = new NioEventLoopGroup();
	}
	
	public void start() throws Exception{
		if(!startFlag.compareAndSet(false, true)){
			return;
		}
		
        serverBootstrap = new ServerBootstrap();  
        serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)  
        .childHandler(new NettyServerPipelineFactory(new NettyServerHandler(servicesMap)));  
        
		serverBootstrap.option(ChannelOption.SO_BACKLOG, 128);//
        serverBootstrap.option(ChannelOption.TCP_NODELAY, true);
        serverBootstrap.option(ChannelOption.SO_REUSEADDR, true);
        serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            
        future = serverBootstrap.bind(port);
        // 等待服务监听端口关闭  
//		future.channel().closeFuture().sync();
        
	}
	
	
	public void stop() throws Exception{
		future.channel().closeFuture().sync();
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
	}

}
