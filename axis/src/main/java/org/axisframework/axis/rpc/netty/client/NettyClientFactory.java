/**
 * 
 */
package org.axisframework.axis.rpc.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.axisframework.axis.AXSException;




/**
 * @author yuantengkai
 * netty连接管理工厂
 */
public class NettyClientFactory {
	
	private static final NettyClientFactory instance = new NettyClientFactory();
	
	private final Object lock = new Object();
	
	//key-ip:port value-nettyclient 以后可以改成一个ipport对应多个client的形式
	private final ConcurrentMap<String, NettyClient> addr2clientMap = new ConcurrentHashMap<String, NettyClient>();
	
	private final ConcurrentMap<Channel,NettyClient> channel2clientMap = new ConcurrentHashMap<Channel, NettyClient>();
	
	private final Bootstrap bootstrap;
	private final EventLoopGroup group;
	
	private NettyClientFactory(){
		bootstrap = new Bootstrap();
		group = new NioEventLoopGroup();
//		group = new NioEventLoopGroup(0,new ThreadFactory() {
//			
//			AtomicInteger index = new AtomicInteger();
//
//			public Thread newThread(Runnable r) {
//				Thread thread = new Thread(r);
//				thread.setDaemon(true);
//				thread.setName("NettyClient-Group#"
//						+ (index.incrementAndGet()));
//				return thread;
//			}
//		});
		bootstrap.group(group).channel(NioSocketChannel.class);
		bootstrap.handler(new NettyClientPipelineFactory(new NettyClientHandler(this)));
	}

	public static NettyClientFactory getInstance() {
        return instance;
    }
	
	/**
	 * 拿到一个连接
	 * @param remoteHost
	 * @param port
	 * @return
	 * @throws AXSException 
	 */
	public NettyClient getClient(String remoteHost, int port) throws AXSException{
		String key = getClientKey(remoteHost, port);
		if(addr2clientMap.containsKey(key)){
			return addr2clientMap.get(key);
		}
		NettyClient client = null;
		synchronized(lock){
			if(addr2clientMap.containsKey(key)){
				return addr2clientMap.get(key);
			}
//	        bootstrap.group(group).channel(NioSocketChannel.class);
//			bootstrap.handler(new NettyClientPipelineFactory(new NettyClientHandler(this)));
			bootstrap.option(ChannelOption.TCP_NODELAY, true);
			bootstrap.option(ChannelOption.SO_REUSEADDR, true);
	        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = bootstrap.connect(new InetSocketAddress(remoteHost, port));
            future.awaitUninterruptibly(1500);
			if (!future.isDone() || !future.isSuccess() || !future.channel().isRegistered()) {
	            throw new AXSException(AXSException.ERROR_CONNECTED, "connected error,key="+key);
	        }
			client = new NettyClient(future.channel());
			addr2clientMap.put(key, client);
			channel2clientMap.put(future.channel(), client);
		}
		return client;
	}
	
	public NettyClient getCientByChannel(final Channel channel) {
        return channel2clientMap.get(channel);
    }
	
	public void removeClient(final Channel channel){
		synchronized(lock){
			NettyClient client = channel2clientMap.remove(channel);
			addr2clientMap.remove(getClientKey(client.getRemoteAddress(), client.getRemotePort()));
		}
	}

	private String getClientKey(String remoteHost, int port) {
		return remoteHost + ":" + port;
	}
	
	public static void main(String[] args) {
		InetSocketAddress isa = new InetSocketAddress("127.0.0.1", 7001);
		System.out.println(isa.getAddress().toString());
		System.out.println(isa.getAddress().toString().substring(1));
	}
}
