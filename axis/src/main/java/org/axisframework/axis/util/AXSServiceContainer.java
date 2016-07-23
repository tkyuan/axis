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
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AXSServiceContainer.class);


	private static final AXSServiceContainer instance = new AXSServiceContainer();

	// key: uniqueServiceName value: target
	private final ConcurrentHashMap<String, Object> cachedServices = new ConcurrentHashMap<String, Object>();

	//-----------------provider below---------------------
	private final int port = 9201;
	
	//key: uniqueServiceName value: service
	private final Map<String,Object> servicesMap = new ConcurrentHashMap<String, Object>();

	private final AtomicBoolean serverStartFlag = new AtomicBoolean(false);
	
	private final AtomicBoolean serverStopFlag = new AtomicBoolean(false);
	
	private volatile NettyServer server;
	
	private AXSServiceContainer() {

	}

	public static AXSServiceContainer getInstance() {
		return instance;
	}
	
	public void publish(final ServiceMetadata metadata){
		String uniqueServiceName = metadata.getUniqueServiceName();
		servicesMap.put(uniqueServiceName, metadata.getObj());
		if(LOGGER.isInfoEnabled()){
			LOGGER.info("axis service published,"+metadata);
		}
		if(serverStartFlag.compareAndSet(false, true)){
			server = new NettyServer(port, servicesMap);
			try {
				server.start();
			} catch (Exception e) {
				LOGGER.error("netty server start exception,"+metadata,e);
				throw new RuntimeException(e);
			}
		}
	}

	
	public void stopServer(){
		if(serverStopFlag.compareAndSet(false, true)){
			try{
				server.refuseAndCloseConnect();
				Thread.sleep(1000);
				server.stop();
			} catch(Exception e){
				LOGGER.error("netty server stop exception.",e);
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
			AXSRequest request = new AXSRequest();
			request.setRequestId(UniqueUtils.getUniqueId());
			request.setTargetServiceUniqueName(metadata.getUniqueServiceName());
			request.setMethodName(method.getName());
			request.setParameters(args);
			request.setParameterTypes(method.getParameterTypes());
			request.setTimeout(metadata.getTimeout());
			// 1.得到链接 2.发送请求 3.返回结果
			String targetAddr = metadata.getProperty("target");
			String[] ip_port = targetAddr.split(":");
			String ip = ip_port[0];
			int port = Integer.valueOf(ip_port[1]);
			NettyClient client = NettyClientFactory.getInstance().getClient(ip,
					port);

			Object rawResult = client.send(request);

			if (rawResult instanceof Throwable) {//如果是server端抛出的异常
				throw (Throwable) rawResult;
			}
			return rawResult;
		}

	}

}
