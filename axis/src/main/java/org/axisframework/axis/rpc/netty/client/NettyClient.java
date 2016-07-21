/**
 * 
 */
package org.axisframework.axis.rpc.netty.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.axisframework.axis.AXSException;
import org.axisframework.axis.model.AXSRequest;
import org.axisframework.axis.model.AXSResponse;
import org.axisframework.axis.model.AXSResponseFuture;
import org.axisframework.axis.model.ResponseStatus;
import org.axisframework.axis.rpc.ResponseCallBackFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * @author yuantengkai
 *
 */
public class NettyClient {
	
	private static Logger logger = LoggerFactory.getLogger(NettyClient.class);
	
	//当前连接client所持有的请求 key-requestid
	private final ConcurrentMap<String, ResponseCallBackFuture> responses = new ConcurrentHashMap<String, ResponseCallBackFuture>();
	
	private final Channel channel;
	
	private final String remoteAddress;
	
	private final int remotePort;
	
	public NettyClient(Channel channel){
		this.channel = channel;
		InetSocketAddress isaRemote = (InetSocketAddress) this.channel.remoteAddress();
	    this.remoteAddress = isaRemote.getAddress().toString().substring(1);
	    this.remotePort = isaRemote.getPort();
	}
	
	public Object send(final AXSRequest request) throws AXSException, Exception{
		
		final long beginTime = System.currentTimeMillis();
		final String requestId = request.getRequestId();
		ResponseCallBackFuture rcbFuture = new ResponseCallBackFuture();
		
		responses.put(requestId, rcbFuture);
		ChannelFuture writeFuture = channel.writeAndFlush(request);
		writeFuture.addListener(new ChannelFutureListener() {
			
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if (future.isSuccess()) {
                    return;
                }
				Throwable ft = future.cause();
				String errorMsg = null;
				if(ft instanceof ClosedChannelException){
					errorMsg = "Send request but channel closed,"+ft.getMessage();
					if(channel.isActive()){
						channel.close();
					}
					NettyClientFactory.getInstance().removeClient(channel);
				}else if (System.currentTimeMillis() - beginTime >= request.getTimeout()) {
					errorMsg = "Send request consume too long time(" + (System.currentTimeMillis() - beginTime)
	                            + "),requestId:" + requestId;
	            }else{
	            	errorMsg = "Send request error,requestId:"+requestId + future.cause();
	            }
				AXSResponse resp = new AXSResponse();
				resp.setStatus(ResponseStatus.CLIENT_ERROR);
				resp.setErrorMsg(errorMsg);
				resp.setRequestId(requestId);
				NettyClient.this.putResponse(resp);
			}
		});
		
		AXSResponseFuture.setFuture(rcbFuture);
		
		return AXSResponseFuture.getResponseResult(request.getTimeout());
	}
	
	
	public void putResponse(AXSResponse response){
		ResponseCallBackFuture responseCallBack = responses.remove(response.getRequestId());
        if (responseCallBack == null) {
        	logger.warn("responseCallBack is null, give up the response,requestId:" + response.getRequestId()+response);
        } else {
            responseCallBack.onResponse(response);
        }
	}
	
    public String getRemoteAddress() {
        return remoteAddress;
    }
    
    public int getRemotePort() {
		return remotePort;
	}

	public String toString() {
        return channel.toString();
    }
	
}
