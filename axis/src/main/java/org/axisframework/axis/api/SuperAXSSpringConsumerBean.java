/**
 * 
 */
package org.axisframework.axis.api;

import org.axisframework.axis.AXSException;
import org.axisframework.axis.model.ServiceMetadata;
import org.axisframework.axis.util.AXSServiceContainer;


/**
 * @author yuantengkai
 * <bean id="xxxService"
          class="org.axisframework.axis.api.SuperAXSSpringConsumerBean" init-method="init">
        <property name="interfaceName" value="接口名"/>
        <property name="version" value="1.0.0"/>
        <property name="target" value="127.0.0.1:6800"/>
        <property name="timeout" value="3000"/>
    </bean>
    xxxService.invoke();
 * 
 */
public class SuperAXSSpringConsumerBean {
	
	private final ServiceMetadata metadata = new ServiceMetadata(false);
	
	
	public void init(){
		metadata.initUniqueName();
	}
	
	
	/**
	 * 统一调用
	 * @param methodName 方法名
	 * @param parameterTypeStr 入参类型名string
	 * @param args 入参
	 * @return
	 * @throws AXSException
	 * @throws Exception
	 */
	public Object invoke(String methodName, String[] parameterTypeStr, Object[] args) throws AXSException, Exception {
		return AXSServiceContainer.getInstance().trueInvoke(metadata, methodName, parameterTypeStr, args);
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
