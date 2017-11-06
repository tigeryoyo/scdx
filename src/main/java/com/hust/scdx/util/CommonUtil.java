package com.hust.scdx.util;

import org.apache.commons.lang.StringUtils;

import com.hust.scdx.constant.Constant.Interval;
import com.hust.scdx.constant.Constant.KEY;

public class CommonUtil {

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
	/**
	 * 获取出图需要的时间维度
	 * @param time 时间 yyyy-mm-dd hh:MM:ss
	 * @param interval
	 * @return
	 */
	public static String getTimeKey(String time, int interval) {
		if (StringUtils.isBlank(time) || !TimeUtil.isvalidate(time)) {
			return KEY.INVALID_TIME;
		}
		switch (interval) {
		case Interval.HOUR: {
			return time.substring(0, 13);
		}
		case Interval.DAY: {
			return time.substring(0, 10);
		}
		case Interval.MONTH: {
			return time.substring(0, 7);
		}
		default: {
			return KEY.INVALID_TIME;
		}
		}
	}

}
