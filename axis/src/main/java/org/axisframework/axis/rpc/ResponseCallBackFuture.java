/**
 * 
 */
package org.axisframework.axis.rpc;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.axisframework.axis.AXSException;
import org.axisframework.axis.model.AXSResponse;




/**
 * @author yuantengkai
 *
 */
public class ResponseCallBackFuture implements Future<AXSResponse>{

	
	private final CountDownLatch countDownLatch = new CountDownLatch(1);
	
	private AXSResponse response;
	
	public ResponseCallBackFuture(){
	}
	
	/**
	 * 响应处理
	 * @param response
	 */
	public void onResponse(final AXSResponse response){
		synchronized (this) {
			if(this.response != null){
				return; //已经有响应了
			}
			this.response = response;
		}
		this.countDownLatch.countDown();
	}
	
	@Override
	public AXSResponse get() throws InterruptedException {
        this.countDownLatch.await();
        if(response == null){
			throw new AXSException(AXSException.ERROR_RESPONSE, "not responsed.");
		}
		return response;
	}

	@Override
	public AXSResponse get(long timeout, TimeUnit unit) throws InterruptedException {
		if (timeout > 0) {
            this.countDownLatch.await(timeout, unit);
        } else {
            this.countDownLatch.await();
        }
		if(response == null){
			throw new AXSException(AXSException.ERROR_RESPONSE, "not responsed.");
		}
		return response;
	}
	
	@Override
	public boolean isDone() {
		 synchronized (this) {
	         return this.response != null;
	     }
	}

	@Override
	public boolean isCancelled() {
		return false;
	}
	
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

}
