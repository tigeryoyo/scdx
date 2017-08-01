package com.hust.scdx.util;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonUtil {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(CommonUtil.class);

	public static boolean hasEmptyArray(String[] array) {
		if (null == array || array.length == 0) {
			return true;
		}
		for (int i = 0; i < array.length; i++) {
			if (StringUtils.isBlank(array[i])) {
				return true;
			}
		}
		return false;
	}

	public static boolean isEmptyArray(String[] array) {
		if (null == array || array.length == 0) {
			return true;
		}
		for (int i = 0; i < array.length; i++) {
			if (!StringUtils.isBlank(array[i])) {
				return false;
			}
		}
		return true;
	}

	public static String getPrefixUrl(String url) {
		if (StringUtils.isEmpty(url)) {
			return StringUtils.EMPTY;
		}
		try {
			int pure = url.indexOf("/", url.indexOf("://") + 3);
			if (pure == -1) {
				return url;
			} else {
				String prefix = url.substring(0, pure);
				return prefix;
			}
		} catch (Exception e) {
			logger.error("get prefix of url failed, url :{}, exception:{}", url, e.toString());
			return StringUtils.EMPTY;
		}
	}

}
