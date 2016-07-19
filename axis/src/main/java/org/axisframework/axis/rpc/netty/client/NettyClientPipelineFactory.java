/**
 * 
 */
package org.axisframework.axis.rpc.netty.client;


import java.util.concurrent.TimeUnit;

import org.axisframework.axis.model.AXSRequest;
import org.axisframework.axis.model.AXSResponse;
import org.axisframework.axis.rpc.coder.RpcDecoder;
import org.axisframework.axis.rpc.coder.RpcEncoder;

import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;


/**
 * @author yuantengkai
 *
 */
public class NettyClientPipelineFactory extends ChannelInitializer<SocketChannel>{
	
	private ChannelInboundHandler handler;
	
	public NettyClientPipelineFactory(ChannelInboundHandler handler){
		this.handler = handler;
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast(new RpcEncoder(AXSRequest.class));
        pipeline.addLast(new RpcDecoder(AXSResponse.class));
        pipeline.addLast(new IdleStateHandler(0, 0, 27, TimeUnit.SECONDS));
        pipeline.addLast(handler);
	}

}
