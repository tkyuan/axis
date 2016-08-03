/**
 * 
 */
package org.axisframework.axis.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.axisframework.axis.AXSException;
import org.axisframework.axis.model.AXSRequest;
import org.axisframework.axis.model.ServiceMetadata;
import org.axisframework.axis.rpc.netty.client.NettyClient;
import org.axisframework.axis.rpc.netty.client.NettyClientFactory;
import org.axisframework.axis.rpc.netty.server.NettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yuantengkai
 *
 */
public class AXSServiceContainer {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AXSServiceContainer.class);

	private static final AXSServiceContainer instance = new AXSServiceContainer();

	// key: uniqueServiceName value: target
	private final ConcurrentHashMap<String, Object> cachedServices = new ConcurrentHashMap<String, Object>();

	// -----------------provider below---------------------
	private final int port = 9201;

	// key: uniqueServiceName value: service
	private final Map<String, Object> servicesMap = new ConcurrentHashMap<String, Object>();
	
	private final Map<String,ConcurrentHashMap<String,Method>> serviceMethodMap = new ConcurrentHashMap<String,ConcurrentHashMap<String,Method>>();

	private final AtomicBoolean serverStartFlag = new AtomicBoolean(false);

	private final AtomicBoolean serverStopFlag = new AtomicBoolean(false);

	private volatile NettyServer server;

	private AXSServiceContainer() {

	}

	public static AXSServiceContainer getInstance() {
		return instance;
	}

	public void publish(final ServiceMetadata metadata) {
		String uniqueServiceName = metadata.getUniqueServiceName();
		Object servicePojo = metadata.getObj();
		servicesMap.put(uniqueServiceName, servicePojo);

		ConcurrentHashMap<String,Method> methodMap = new ConcurrentHashMap<String,Method>();
		for (Method m : servicePojo.getClass().getMethods()) {
	         StringBuilder sbParamType = new StringBuilder();
	         sbParamType.append(m.getName());
	         sbParamType.append("_");
	         for (Class<?> paramType : m.getParameterTypes()) {
	        	 sbParamType.append(paramType.getName());
	         }
	         methodMap.put(sbParamType.toString(), m);
	    }
		serviceMethodMap.put(uniqueServiceName, methodMap);
		
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("axis service published," + metadata);
		}
		if (serverStartFlag.compareAndSet(false, true)) {
			server = new NettyServer(port, servicesMap,serviceMethodMap);
			try {
				server.start();
			} catch (Exception e) {
				LOGGER.error("netty server start exception," + metadata, e);
				throw new RuntimeException(e);
			}
		}
	}

	public void stopServer() {
		if (serverStopFlag.compareAndSet(false, true)) {
			try {
				server.refuseAndCloseConnect();
				Thread.sleep(1000);
				server.stop();
			} catch (Exception e) {
				LOGGER.error("netty server stop exception.", e);
			}
		}

	}

	public Object consume(final ServiceMetadata metadata) {
		if (cachedServices.containsKey(metadata.getUniqueServiceName())) {
			return cachedServices.get(metadata.getUniqueServiceName());
		}
		Object proxyObj = Proxy.newProxyInstance(metadata.getIfClazz()
				.getClassLoader(), new Class[] { metadata.getIfClazz() },
				new AXSServiceProxy(metadata));

		cachedServices.putIfAbsent(metadata.getUniqueServiceName(), proxyObj);
		return proxyObj;
	}

	private class AXSServiceProxy implements InvocationHandler {

		private ServiceMetadata metadata;

		public AXSServiceProxy(ServiceMetadata metadata) {
			this.metadata = metadata;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			return trueInvoke(metadata, method.getName(),
					transform2ParamTypeStr(method.getParameterTypes()), args);
		}


	}

	/**
	 * 远程调用方法
	 * 
	 * @param metadata
	 * @param methodName
	 * @param parameterTypeStr
	 * @param args
	 * @return
	 * @throws AXSException
	 * @throws Throwable
	 */
	public Object trueInvoke(ServiceMetadata metadata, String methodName,
			String[] parameterTypeStr, Object[] args) throws AXSException,
			Exception {
		AXSRequest request = new AXSRequest();
		request.setRequestId(UniqueUtils.getUniqueId());
		request.setTargetServiceUniqueName(metadata.getUniqueServiceName());
		request.setMethodName(methodName);
		request.setParameters(args);
		request.setParameterTypeStr(parameterTypeStr);
		request.setTimeout(metadata.getTimeout());
		// 1.得到链接 2.发送请求 3.返回结果
		String targetAddr = metadata.getProperty("target");
		String[] ip_port = targetAddr.split(":");
		String ip = ip_port[0];
		int port = Integer.valueOf(ip_port[1]);
		NettyClient client = NettyClientFactory.getInstance().getClient(ip,
				port);

		Object rawResult = client.send(request);

		if (rawResult instanceof Exception) {// 如果是server端抛出的异常
			throw (Exception) rawResult;
		}
		return rawResult;
	}

	private String[] transform2ParamTypeStr(Class<?>[] args) {
		if (args == null || args.length == 0) {
			return new String[] {};
		}
		String[] paramTypeStr = new String[args.length];
		for (int i = 0; i < args.length; i++) {
			paramTypeStr[i] = args[i].getName();
		}
		return paramTypeStr;
	}

}
