/**
 * 
 */
package org.axisframework.axis.model;

/**
 * @author yuantengkai
 *
 */
public enum ResponseStatus {

	OK(200, "OK"),
	CLIENT_ERROR(500, "client error"),
	SERVER_ERROR(501, "server error"),
	ARG_ILLEGAL(502, "argument Illegal"),
	METHOD_NOT_FOUND(400, "method not found");
	
	private String message;
	private byte code;

	private ResponseStatus(int code, String message) {
		this.code = (byte) code;
		this.message = message;
	}

	public String getMessage() {
		return this.message;
	}

	public int getCode() {
		return code;
	}
}
