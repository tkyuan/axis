/**
 * 
 */
package org.axisframework.axis.demo.provider.impl;

import org.axisframework.axis.demo.api.HelloService;

/**
 * @author yuantengkai
 *
 */
public class HelloServiceImpl implements HelloService {

	@Override
	public String sayHello(String name) {
		System.out.println("receive from consumer:"+name);
		return "Hello " + name;
	}

}
