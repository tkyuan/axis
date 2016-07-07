/**
 * 
 */
package org.axisframework.axis.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author yuantengkai
 *
 */
public class UniqueUtils {
	
	private static final AtomicLong atomicIds = new AtomicLong(1);

	private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	private static String uniqueTime = encodeForPositiveLong(System
			.currentTimeMillis() / 1000);

	private static String uniqueName;

	static {
		Enumeration<NetworkInterface> netInterfaces = null;
		try {
			netInterfaces = NetworkInterface.getNetworkInterfaces();
			while (netInterfaces.hasMoreElements() && uniqueName == null) {
				NetworkInterface ni = netInterfaces.nextElement();
				Enumeration<InetAddress> ips = ni.getInetAddresses();
				while (ips.hasMoreElements() && uniqueName == null) {
					InetAddress add = ips.nextElement();
					if (!add.isLoopbackAddress() && !add.isLinkLocalAddress()) {
						uniqueName = encodeForInt(add.hashCode());
						break;
					}
				}
			}
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}

	}

	/**
	 * 得到唯一号
	 * @return
	 */
	public static String getUniqueId() {

		long id = atomicIds.getAndIncrement();
		if (id >= (Long.MAX_VALUE - 100000)) {
			atomicIds.set(1);
			uniqueTime = encodeForPositiveLong(System.currentTimeMillis() / 1000);
		}

		String uniqueId = uniqueName + "-" + uniqueTime + "-"
				+ encodeForPositiveLong(id);
		return uniqueId;
	}

	private static String encodeForPositiveLong(long num) {
		if (num < 1) {
			throw new RuntimeException("num must be greater than 0.");
		}
		StringBuilder sb = new StringBuilder();
		for (; num > 0; num /= ALPHABET.length()) {
			sb.append(ALPHABET.charAt((int) (num % ALPHABET.length())));
		}
		return sb.toString();
	}

	static String encodeForInt(int intNumber) {
		long num = Math.abs((long) Integer.MIN_VALUE) + intNumber + 1;
		return encodeForPositiveLong(num);
	}
	
	public static void main(String[] args) {
		System.out.println(getUniqueId());
		System.out.println(getUniqueId());
		System.out.println(getUniqueId());
		System.out.println(getUniqueId());
		System.out.println(getUniqueId());
		System.out.println(getUniqueId());
		System.out.println(getUniqueId());
		System.out.println(getUniqueId());
		//Q9HG8C-F63H9O-1  873H9O
	}
}
