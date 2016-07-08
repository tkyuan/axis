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
		if (in.readableBytes() < 4) { //由于encode我们传的是一个int类型的值，所以这里的值为4
			return;
		}
		in.markReaderIndex();//标记一下当前的readIndex的位置
		int dataLength = in.readInt();//读取传送过来的消息的长度。ByteBuf 的readInt()方法会让他的readIndex增加4
		if (dataLength < 0) {//读到的消息体长度为0，这是不应该出现的情况，这里出现这情况，关闭连接。
			ctx.close();
		}
		if (in.readableBytes() < dataLength) {//读到的消息体长度如果小于我们传送过来的消息长度，则resetReaderIndex. 这个配合markReaderIndex使用的。把readIndex重置到mark的地方
			in.resetReaderIndex();
			return;
		}
		byte[] data = new byte[dataLength];
		in.readBytes(data);

		Object obj = SerializationUtil.decode(data, ifClazz);
		out.add(obj);
	}

}
