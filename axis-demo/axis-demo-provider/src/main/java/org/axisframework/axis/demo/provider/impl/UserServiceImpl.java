/**
 * 
 */
package org.axisframework.axis.demo.provider.impl;

import org.axisframework.axis.demo.api.UserDto;
import org.axisframework.axis.demo.api.UserService;

/**
 * @author yuantengkai
 *
 */
public class UserServiceImpl implements UserService {

	@Override
	public UserDto query(String name) {
		System.out.println("receive from consumer, query name:"+name);
		UserDto dto = new UserDto();
		dto.setName("西瓜-"+name);
		dto.setAge(20);
		return dto;
	}

	@Override
	public int addUser(UserDto user) {
		System.out.println("receive from consumer add user:"+user);
		return 10+user.getAge();
	}

}
