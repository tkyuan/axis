/**
 * 
 */
package org.axisframework.axis.demo.consumer.service;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Resource;
import org.axisframework.axis.demo.api.HelloService;
import org.axisframework.axis.demo.api.UserDto;
import org.axisframework.axis.demo.api.UserService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author yuantengkai
 *
 */
public class HelloConsumer implements ApplicationContextAware{
	
	@Resource
	private HelloService helloService;
	
	@Resource
	private UserService userService;
	
	private AtomicBoolean init = new AtomicBoolean(false);
	
	public void init() {
		startNewThread2Consume();
	}

	private void startNewThread2Consume() {
		for(int i=1;i<=3;i++){
			Thread t = new Thread(new TaskTest());
			t.setDaemon(true);
			t.setName("hello-consume-thread"+i);
			t.start();
		}
	}

	public void setHelloService(HelloService helloService) {
		this.helloService = helloService;
	}
	

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		init.set(true);
	}
	
	
	private class TaskTest implements Runnable{

		@Override
		public void run() {
			int i = 0;
			while (true) {
				try {
					
					if(!init.get()){
						continue;
					}
					if(i==0){
						Thread.sleep(5000);
					}
					
					i++;
					UserDto dto = userService.query("香蕉"+i);
					System.out.println("receive server response:"+dto);
//					Thread.sleep(200);
					String s = helloService.sayHello("tkyuan" + i);
					System.out.println("receive server response:"+s);
//					Thread.sleep(1000);
					dto.setName(dto.getName());
					int r = userService.addUser(dto);
					System.out.println("receive server response:"+r);
					if(i>1000){
						break;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (Throwable t) {
					t.printStackTrace();
				}

			}
		}
		
	}

}
