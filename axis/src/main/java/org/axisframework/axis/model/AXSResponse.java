/**
 * 
 */
package org.axisframework.axis.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;


/**
 * @author yuantengkai
 *
 */
public class AXSResponse implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5007325967532801456L;

	private ResponseStatus status = ResponseStatus.OK;
	
	private String requestId;
	
	private String errorMsg;
    
    private Object result;

	public ResponseStatus getStatus() {
		return status;
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}
	
	public boolean isSuccess(){
		return this.status == ResponseStatus.OK;
		
	}
    
	public String toString() {
        return ToStringBuilder.reflectionToString(this,ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
