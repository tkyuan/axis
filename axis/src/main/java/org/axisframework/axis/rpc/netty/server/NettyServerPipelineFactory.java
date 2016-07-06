/**
 * 
 */
package org.axisframework.axis.rpc.netty.server;

import org.axisframework.axis.model.AXSRequest;
import org.axisframework.axis.model.AXSResponse;
import org.axisframework.axis.rpc.coder.RpcDecoder;
import org.axisframework.axis.rpc.coder.RpcEncoder;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * @author yuantengkai
 *
 */
public class NettyServerPipelineFactory extends ChannelInitializer<SocketChannel>{

	private NettyServerHandler handler;
	
	public NettyServerPipelineFactory(NettyServerHandler handler){
		this.handler = handler;
	}
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast(new RpcDecoder(AXSRequest.class));
        pipeline.addLast(new RpcEncoder(AXSResponse.class));
        pipeline.addLast(handler);
		
	}

}
