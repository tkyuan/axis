/**
 * 
 */
package org.axisframework.axis.rpc.netty.server;

import java.lang.reflect.Method;
import java.util.Map;

import org.axisframework.axis.AXSException;
import org.axisframework.axis.model.AXSRequest;
import org.axisframework.axis.model.AXSResponse;
import org.axisframework.axis.model.ResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author yuantengkai
 *
 */
@Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<AXSRequest> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(NettyServerHandler.class);

	private  Map<String, Object> rpcServiceMap;
	
	public NettyServerHandler(Map<String, Object> rpcServiceMap){
		this.rpcServiceMap = rpcServiceMap;
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, AXSRequest request)
			throws Exception {
		if(LOGGER.isInfoEnabled()){
			LOGGER.info("AXIS netty-server received request:"+request);
		}
		AXSResponse response = new AXSResponse();  
        response.setRequestId(request.getRequestId()); 
        try {  
            response.setResult(handle(request));
        } catch (NoSuchMethodException e) {
        	LOGGER.warn("AXS nettyserver deal NoSuchMethodException,"+request, e);
            response.setStatus(ResponseStatus.METHOD_NOT_FOUND); 
            response.setErrorMsg(e.getMessage());
        } catch (IllegalArgumentException e) {
        	LOGGER.warn("AXS nettyserver deal IllegalArgumentException,"+request, e);
            response.setStatus(ResponseStatus.ARG_ILLEGAL); 
            response.setErrorMsg(e.getMessage());
        }  catch (Exception e) {
        	LOGGER.warn("AXS nettyserver deal exception,"+request, e);
            response.setStatus(ResponseStatus.SERVER_ERROR); 
            response.setErrorMsg(e.getMessage());
        }  
        ctx.writeAndFlush(response);  
    }  

	private Object handle(AXSRequest request) throws NoSuchMethodException, IllegalArgumentException, Exception {
		Object service = rpcServiceMap.get(request.getTargetServiceUniqueName());
		if(service == null){
			throw new AXSException(AXSException.ERROR_SERVER,"service not fount,uniqueName="+request.getTargetServiceUniqueName());
		}
		Method method = service.getClass().getMethod(request.getMethodName(),  
	            request.getParameterTypes());
		return method.invoke(service, request.getParameters());
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		LOGGER.warn("##########AXIS netty-server connected,"+ctx.channel());
		super.channelActive(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		LOGGER.warn("##########AXIS netty-server connection closed,"+ctx.channel());
		super.channelInactive(ctx);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		LOGGER.warn("AXIS netty-server exception,"+ctx.channel(), cause);
		ctx.channel().close();
	}

}
