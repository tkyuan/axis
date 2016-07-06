/**
 * 
 */
package org.axisframework.axis.demo.consumer.service;

import javax.annotation.Resource;

import org.axisframework.axis.api.AXSSpringConsumerBean;
import org.axisframework.axis.demo.api.HelloService;
import org.axisframework.axis.demo.api.UserDto;
import org.axisframework.axis.demo.api.UserService;

/**
 * @author yuantengkai
 *
 */
public class HelloConsumer {
	
	@Resource
	private HelloService helloService;
	
	@Resource
	private UserService userService;
	
	public void init() {
		startNewThread2Consume();
	}

	private void startNewThread2Consume() {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				int i = 0;
				while (true) {
					try {
						Thread.sleep(1000);
						i++;
						
						UserDto dto = userService.query("香蕉"+i);
						System.out.println("receive server response:"+dto);
						Thread.sleep(2000);
						String s = helloService.sayHello("tkyuan" + i);
						System.out.println("receive server response:"+s);
						Thread.sleep(1000);
						dto.setName(dto.getName()+i);
						int r = userService.addUser(dto);
						System.out.println("receive server response:"+r);
						Thread.sleep(1000);
						if(i>3){
							break;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (Throwable t) {
						t.printStackTrace();
					}

				}
			}
		});
		t.setDaemon(false);
		t.setName("hello-consume-thread");
		t.start();
	}

	public void setHelloService(HelloService helloService) {
		this.helloService = helloService;
	}
	
	public static void main(String[] args) throws Exception {
		AXSSpringConsumerBean bean = new AXSSpringConsumerBean();
		bean.setInterfaceName("org.axisframework.axis.demo.api.HelloService");
		bean.setTarget("172.24.122.126:9201");
		bean.setVersion("1.0.0");
		bean.setTimeout(8000);
		bean.init();
		
	}

}
