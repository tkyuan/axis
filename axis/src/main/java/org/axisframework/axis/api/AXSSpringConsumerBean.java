/**
 * 
 */
package org.axisframework.axis.api;

import org.axisframework.axis.model.ServiceMetadata;
import org.axisframework.axis.util.AXSServiceContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;



/**
 * @author yuantengkai
 * <bean id="xxxService"
          class="org.axisframework.axis.api.AXSSpringConsumerBean" init-method="init">
        <property name="interfaceName" value="接口名"/>
        <property name="version" value="1.0.0"/>
        <property name="target" value="127.0.0.1:6800"/>
        <property name="timeout" value="3000"/>
    </bean>
 *
 */
public class AXSSpringConsumerBean implements FactoryBean{
	
	private static Logger logger = LoggerFactory.getLogger(AXSSpringConsumerBean.class);
	
//	private final AtomicBoolean inited = new AtomicBoolean(false);
	
	private final ServiceMetadata metadata = new ServiceMetadata(false);
	
	public void init() throws Exception{
		
//		if (!inited.compareAndSet(false, true)){
//			logger.warn("AXSSpringConsumerBean is already inited,"+metadata.getUniqueServiceName());
//			return;
//		}
		metadata.initUniqueName();
		
		try{
			Class<?> interfaceClass = Class.forName(metadata.getInterfaceName());
			metadata.setIfClazz(interfaceClass);
			metadata.setObj(AXSServiceContainer.getInstance().consume(metadata));
		}catch(Exception e){
			logger.error("AXSSpringConsumerBean init error,"+metadata.getUniqueServiceName(), e);
			throw e;
		}
	}
	
	@Override
	public Object getObject() throws Exception {
		return metadata.getObj();
	}

	@Override
	public Class<?> getObjectType() {
		if(metadata.getIfClazz() == null){
			return AXSSpringConsumerBean.class;
		}
		return metadata.getIfClazz();
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
	

	public void setInterfaceName(String interfaceName) {
		metadata.setInterfaceName(interfaceName);
	}

	public void setVersion(String version) {
		metadata.setVersion(version);
	}

	public void setTimeout(int timeout) {
		metadata.setTimeout(timeout);
	}
	
	public void setTarget(String target){
		metadata.addProperty("target", target);
	}

}
