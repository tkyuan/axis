/**
 * 
 */
package org.axisframework.axis.api;

import org.axisframework.axis.model.ServiceMetadata;
import org.axisframework.axis.util.AXSServiceContainer;

/**
 * @author yuantengkai
 * <bean class="org.axisframework.axis.api.AXSSpringProviderBean" init-method="init">
        <property name="interfaceName" value="接口名"/>
        <property name="version" value="1.0.0"/>
        <property name="serviceTarget" ref="xxxService"/>
    </bean>
 */
public class AXSSpringProviderBean{

	private final ServiceMetadata metadata = new ServiceMetadata(true);
	
	public void init(){
		metadata.initUniqueName();
		AXSServiceContainer.getInstance().publish(metadata);
	}
	
	
	public void setInterfaceName(String interfaceName) {
		metadata.setInterfaceName(interfaceName);
	}

	public void setVersion(String version) {
		metadata.setVersion(version);
	}
	
	public void setServiceTarget(Object serviceTarget){
		metadata.setObj(serviceTarget);
	}
	
}
