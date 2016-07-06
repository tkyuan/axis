/**
 * 
 */
package org.axisframework.axis.demo.api;

/**
 * @author yuantengkai
 *
 */
public interface UserService {
	
	public UserDto query(String name);
	
	public int addUser(UserDto user);

}
