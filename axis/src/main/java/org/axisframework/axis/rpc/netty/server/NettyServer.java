/**
 * 
 */
package org.axisframework.axis.rpc.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.util.Map;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.axisframework.axis.util.AXSServiceContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author yuantengkai
 *
 */
public class NettyServer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(NettyServer.class);
	
	private final int port;
	
	private final Map<String, Object> servicesMap;
	private final AtomicBoolean startFlag = new AtomicBoolean(false);
	
	private final ServerBootstrap serverBootstrap;
	private ChannelFuture future;
	private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    
    private final NettyServerHandler serverHandler;
	
	public NettyServer(int port, Map<String, Object> servicesMap){
		this.port = port;
		this.servicesMap = servicesMap;
		serverBootstrap = new ServerBootstrap(); 
		serverHandler = new NettyServerHandler(servicesMap);
		
		bossGroup = new NioEventLoopGroup(0,new ThreadFactory() {
			AtomicInteger index1 = new AtomicInteger();

			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				thread.setDaemon(true);
				thread.setName("NettyServer-BossGroup#"
						+ (index1.incrementAndGet()));
				return thread;
			}
		});
//		bossGroup = new NioEventLoopGroup();
//		workerGroup = new NioEventLoopGroup();
		workerGroup = new NioEventLoopGroup(0,new ThreadFactory() {
			AtomicInteger index2 = new AtomicInteger();

			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				thread.setDaemon(true);
				thread.setName("NettyServer-WorkerGroup#"
						+ (index2.incrementAndGet()));
				return thread;
			}
		});
	}
	
	public void start() throws Exception{
		if(!startFlag.compareAndSet(false, true)){
			return;
		}
		
        serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)  
        .childHandler(new NettyServerPipelineFactory(serverHandler));  
        
		serverBootstrap.option(ChannelOption.SO_BACKLOG, 128);//
        serverBootstrap.option(ChannelOption.TCP_NODELAY, true);
        serverBootstrap.option(ChannelOption.SO_REUSEADDR, true);
        serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            
        future = serverBootstrap.bind(port);
        Thread closeHookThread = new Thread( new Runnable() {
			
			@Override
			public void run() {
				try{
//					NettyServer.this.refuseAndCloseConnect();
					Thread.sleep(3000);
					NettyServer.this.stop();
				}catch(Exception e){
					LOGGER.error("stop server happens exception.",e);
				}
				
			}
		});
        closeHookThread.setDaemon(true);
        Runtime.getRuntime().addShutdownHook(closeHookThread);
        LOGGER.warn("AXIS netty-server started,bind port:"+port);
        // 等待服务监听端口关闭  
//		future.channel().closeFuture().sync();
        
	}
	
	
	public void refuseAndCloseConnect() throws Exception{
		for(Channel channel:serverHandler.getChannels()){
			if(channel.isActive()){
				channel.close();
			}
		}
	}
	
	
	public void stop() throws Exception{
		if(future.isDone() && future.channel().isActive()){
			future.channel().closeFuture().await(1500);
		}
		if(!bossGroup.isShutdown()){
			bossGroup.shutdownGracefully().await(1500);
		}
		if(!workerGroup.isShutdown()){
			workerGroup.shutdownGracefully().await(1500);
		}
		
	}
	
	
	public static void main(String[] args) {
		Thread shutdownHookOne = new Thread() {  
            public void run() {  
                System.out.println("shutdownHook one...");  
            }  
        };  
        Runtime.getRuntime().addShutdownHook(shutdownHookOne);  
  
        Runnable threadOne = new Runnable() {  
            public void run() {  
                try {  
                    Thread.sleep(1000);  
                } catch (InterruptedException e) {  
                    e.printStackTrace();  
                }  
                System.out.println("thread one doing something...");  
            }  
        };  
  
        Runnable threadTwo = new Thread() {  
            public void run() {  
                try {  
                    Thread.sleep(2000);  
                } catch (InterruptedException e) {  
                    e.printStackTrace();  
                }  
                System.out.println("thread two doing something...");  
            }  
        };  
  
        threadOne.run();  
        threadTwo.run();  
    }  

}
