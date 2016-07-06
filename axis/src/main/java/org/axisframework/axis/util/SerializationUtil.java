/**
 * 
 */
package org.axisframework.axis.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.caucho.hessian.io.SerializerFactory;

/**
 * @author yuantengkai
 *
 */
public class SerializationUtil {

	private static final SerializerFactory serializerFactory = new SerializerFactory();

	public static byte[] encode(Object obj) throws IOException {
		ByteArrayOutputStream binary = new ByteArrayOutputStream();
		HessianOutput hout = new HessianOutput(binary);
		hout.setSerializerFactory(serializerFactory);
		hout.writeObject(obj);
		return binary.toByteArray();
	}

	public static <T> Object decode(byte[] bytes, Class<T> clazz) throws IOException {
		HessianInput hin = new HessianInput(new ByteArrayInputStream(bytes));
		hin.setSerializerFactory(serializerFactory);
		return hin.readObject();
	}
}
