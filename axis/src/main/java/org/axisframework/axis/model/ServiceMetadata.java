/**
 * 
 */
package org.axisframework.axis.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yuantengkai
 *
 */
public class ServiceMetadata {
	
	private boolean isProvider = false;
	
	private volatile String uniqueServiceName;
	
	/**
	 * 接口名
	 */
	private String interfaceName;
	
	private String version;
	
	/**
	 * 超时ms
	 */
	private int timeout;
	
	private Class<?> ifClazz;
	
	//consumer:proxy;provider:servicebean
	private Object obj;
	
	private Map<String, String> serviceProps = new HashMap<String, String>();
	
	public ServiceMetadata(boolean isProvider){
		this.isProvider = isProvider;
	}
	
	public void initUniqueName() {
        this.uniqueServiceName = interfaceName + ":" + version;
    }

	public String getUniqueServiceName() {
		return uniqueServiceName;
	}

	public void setUniqueServiceName(String uniqueServiceName) {
		this.uniqueServiceName = uniqueServiceName;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public Class<?> getIfClazz() {
		return ifClazz;
	}

	public void setIfClazz(Class<?> ifClazz) {
		this.ifClazz = ifClazz;
	}
	
	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

	public void addProperty(String name, String value){
		serviceProps.put(name, value);
	}
	
	public String getProperty(String name){
		return serviceProps.get(name);
	}
	
	@Override
    public String toString() {
        return getUniqueServiceName()+",isProvider:"+isProvider;
    }

}
