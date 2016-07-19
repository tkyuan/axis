/**
 * 
 */
package org.axisframework.axis.rpc.netty.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

import org.axisframework.axis.model.AXSResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yuantengkai
 *
 */
@Sharable
public class NettyClientHandler extends SimpleChannelInboundHandler<AXSResponse>{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(NettyClientHandler.class);
	
	private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(
            Unpooled.copiedBuffer("HEARTBEAT", CharsetUtil.ISO_8859_1));

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
		LOGGER.warn("##########AXIS netty-client connected:"+ctx.channel());
		super.channelActive(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		LOGGER.warn("##########AXIS netty-client connection closed,"+factory.getCientByChannel(ctx.channel()));
		factory.removeClient(ctx.channel());
		super.channelInactive(ctx);
	}



	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
		if (evt instanceof IdleStateEvent) {
			if(LOGGER.isInfoEnabled()){
				LOGGER.info("netty-client send heartbeat...");
			}
            ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate())
                    .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
       } else {
           super.userEventTriggered(ctx, evt);
       }
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		LOGGER.warn("AXIS netty-client exception,"+ctx.channel(), cause);
		ctx.channel().close();
	}

}
