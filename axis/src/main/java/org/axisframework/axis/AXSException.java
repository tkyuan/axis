/**
 * 
 */
package org.axisframework.axis;

/**
 * @author yuantengkai
 *
 */
public class AXSException extends RuntimeException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6648103723331277457L;
	
	
	public static final int ERROR_CONNECTED = 101;
	
	public static final int ERROR_RESPONSE = 102;
	
	public static final int ERROR_CLIENT = 103;
	
	public static final int ERROR_SERVER = 104;
	
	public static final int NOT_RESPONSE = 105;
	
	private int errorCode;

	public AXSException(int errorCode) {
        super();
        this.errorCode = errorCode;
    }
	
	public AXSException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;

    }

    public AXSException(int errorCode, Throwable t) {
        super(t);
        this.errorCode = errorCode;

    }

    public AXSException(int errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

	public int getErrorCode() {
		return errorCode;
	}
    
}
