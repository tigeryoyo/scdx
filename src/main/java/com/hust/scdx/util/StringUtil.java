package com.hust.scdx.util;

public class StringUtil {
	/**
	 * 字符串连接工具
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static String[] concat(String[] a, String[] b) {
		String[] c = new String[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}
}
