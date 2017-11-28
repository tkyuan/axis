/**
 * 
 */
package org.axisframework.axis.rpc.netty.server;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.axisframework.axis.AXSException;
import org.axisframework.axis.model.AXSRequest;
import org.axisframework.axis.model.AXSResponse;
import org.axisframework.axis.model.ResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author yuantengkai
 * netty4.0 不允许channelHandler贴加多次，除非被标记为Sharable
 */
@Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<AXSRequest> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(NettyServerHandler.class);

	//key:serviceUniqueName value: service pojo
	private  Map<String, Object> rpcServiceMap;
	
	//key:key:serviceUniqueName value:key-parmetertype
	private Map<String,ConcurrentHashMap<String,Method>> serviceMethodMap;
	
	private final List<Channel> channels = new CopyOnWriteArrayList<Channel>();
	
	public NettyServerHandler(Map<String, Object> rpcServiceMap, Map<String,ConcurrentHashMap<String,Method>> serviceMethodMap){
		this.rpcServiceMap = rpcServiceMap;
		this.serviceMethodMap = serviceMethodMap;
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

	//TODO fix me, now i/o thread, should user thread to improve concurrency
	private Object handle(AXSRequest request) throws NoSuchMethodException, IllegalArgumentException, Exception {
		//1. service
		Object service = rpcServiceMap.get(request.getTargetServiceUniqueName());
		if(service == null){
			throw new AXSException(AXSException.ERROR_SERVER,"service not fount,uniqueName="+request.getTargetServiceUniqueName());
		}
		
		//2. method
		StringBuilder methodKeyBuilder = new StringBuilder();
		methodKeyBuilder.append(request.getMethodName());
		methodKeyBuilder.append("_");
		
		String[] paramTypeStr = request.getParameterTypeStr();
        for (int i = 0; i < paramTypeStr.length; i++) {
            methodKeyBuilder.append(paramTypeStr[i]);
        }
        String methodKey = methodKeyBuilder.toString();
        
		Method method = serviceMethodMap.get(request.getTargetServiceUniqueName()).get(methodKey);
		if(method == null){
			throw new AXSException(AXSException.ERROR_SERVER,"method not fount,uniqueName="+request.getTargetServiceUniqueName()+",methodKey="+methodKey);
		}
//		Method method = service.getClass().getMethod(request.getMethodName(),  
//	            request.getParameterTypes());
		//3. invoke
		return method.invoke(service, request.getParameters());
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		channels.add(ctx.channel());
		LOGGER.warn("##########AXIS netty-server connected,"+ctx.channel());
		super.channelActive(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		channels.remove(ctx.channel());
		LOGGER.warn("##########AXIS netty-server connection closed,"+ctx.channel());
		super.channelInactive(ctx);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		LOGGER.warn("AXIS netty-server exception,"+ctx.channel(), cause);
		ctx.channel().close();
	}
	
	public List<Channel> getChannels() {
        return channels;
    }

}
