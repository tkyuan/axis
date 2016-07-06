/**
 * 
 */
package org.axisframework.axis.model;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.axisframework.axis.AXSException;





/**
 * @author yuantengkai
 *
 */
public class AXSResponseFuture {
	
	public static ThreadLocal<Future<AXSResponse>> future = new ThreadLocal<Future<AXSResponse>>();

	
	public static Object getResponseResult(long timeout) throws AXSException, Exception {
        if (null == future.get()) {
            throw new AXSException(AXSException.ERROR_RESPONSE, "Thread [" + Thread.currentThread() + "] have not set the response future!");
        }
        try {
            AXSResponse resp = future.get().get(timeout, TimeUnit.MILLISECONDS);
            if (resp.isSuccess()) {
            	return resp.getResult();
            }
            throw new AXSException(AXSException.ERROR_SERVER, resp.getErrorMsg());
        } catch(AXSException e){
        	throw e;
        } catch (Exception e) {
        	throw e;
        } 
	}
	
	public static void setFuture(Future<AXSResponse> future) {
		AXSResponseFuture.future.set(future);
    }
}
