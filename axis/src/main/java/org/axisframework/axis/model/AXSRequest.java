/**
 * 
 */
package org.axisframework.axis.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.axisframework.axis.util.UniqueUtils;

/**
 * @author yuantengkai
 * 
 */
public class AXSRequest implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7738679022334786773L;
    
	private String requestId;
	
	private String targetServiceUniqueName;
	
    private String methodName;
    
    private Class<?>[] parameterTypes;
    
    private Object[] parameters;
    
    private int timeout = 3000;
    
    public AXSRequest(){
    	this.requestId = UniqueUtils.getUniqueId();
    }
    
	public String getRequestId() {
		return requestId;
	}

	public String getTargetServiceUniqueName() {
		return targetServiceUniqueName;
	}

	public void setTargetServiceUniqueName(String targetServiceUniqueName) {
		this.targetServiceUniqueName = targetServiceUniqueName;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public Object[] getParameters() {
		return parameters;
	}

	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}
    
	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String toString() {
        return ToStringBuilder.reflectionToString(this,ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
