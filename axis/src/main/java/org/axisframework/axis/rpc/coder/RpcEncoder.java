/**
 * 
 */
package org.axisframework.axis.rpc.coder;

import org.axisframework.axis.util.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author yuantengkai
 *
 */
@SuppressWarnings("rawtypes")
public class RpcEncoder extends MessageToByteEncoder{

	private Class<?> ifClazz;
	
	public RpcEncoder(Class<?> ifClazz){
		this.ifClazz = ifClazz;
	}
	
	@Override
	protected void encode(ChannelHandlerContext ctx, Object in, ByteBuf out)
			throws Exception {
		
		if (ifClazz.isInstance(in)) {  
            byte[] data = SerializationUtil.encode(in);
            int dataLength = data.length;
            out.writeInt(dataLength);  
            out.writeBytes(data);  
        }  
		
	}

}
