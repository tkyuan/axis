/**
 * 
 */
package org.axisframework.axis.rpc.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.axisframework.axis.model.AXSResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yuantengkai
 *
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<AXSResponse>{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(NettyClientHandler.class);

	private NettyClientFactory factory;

	public NettyClientHandler(NettyClientFactory factory) {
		this.factory = factory;
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, AXSResponse response)
			throws Exception {
		if(LOGGER.isInfoEnabled()){
			LOGGER.info("netty-client received response:"+response);
		}
		factory.getCientByChannel(ctx.channel()).putResponse(response);
	}
	
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		if(LOGGER.isInfoEnabled()){
			LOGGER.info("netty-client connected:"+ctx.channel());
		}
		super.channelActive(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		LOGGER.warn("netty-client connection closed,"+factory.getCientByChannel(ctx.channel()));
		factory.removeClient(ctx.channel());
		super.channelInactive(ctx);
	}



	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		LOGGER.warn("AXIS nettyclient exception,"+ctx.channel(), cause);
		ctx.channel().close();
	}

}
