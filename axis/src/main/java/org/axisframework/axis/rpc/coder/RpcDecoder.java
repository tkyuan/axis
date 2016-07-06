package org.axisframework.axis.rpc.coder;

import java.util.List;

import org.axisframework.axis.util.SerializationUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class RpcDecoder extends ByteToMessageDecoder {

	private Class<?> ifClazz;

	public RpcDecoder(Class<?> ifClazz) {
		this.ifClazz = ifClazz;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		if (in.readableBytes() < 4) {
			return;
		}
		in.markReaderIndex();
		int dataLength = in.readInt();
		if (dataLength < 0) {
			ctx.close();
		}
		if (in.readableBytes() < dataLength) {
			in.resetReaderIndex();
		}
		byte[] data = new byte[dataLength];
		in.readBytes(data);

		Object obj = SerializationUtil.decode(data, ifClazz);
		out.add(obj);
	}

}
