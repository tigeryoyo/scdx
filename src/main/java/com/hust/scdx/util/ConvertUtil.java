package com.hust.scdx.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

import com.hust.scdx.constant.Constant;

public class ConvertUtil implements Converter<String, Date> {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(ConvertUtil.class);

	@Override
	public Date convert(String source) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return simpleDateFormat.parse(source);
		} catch (Exception e) {
			logger.error("convert str to date error");
		}
		return null;
	}

	/**
	 * 存储路径 例如 2017/07/
	 * 
	 * @param date
	 * @return
	 */
	public static String convertDateToPath(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMM");
		try {
			String res = simpleDateFormat.format(date);
			return res.substring(0, 4) + "/" + res.substring(4, 6) + "/";
		} catch (Exception e) {
			logger.error("转换日期错误。");
		}
		return Constant.UNKNOWN;
	}

	/**
	 * 存储路径 例如 2017/07/07
	 * 
	 * @param date
	 * @return
	 */
	public static String convertDateToStdPath(String date) {
		try {
			return date.substring(0, 4) + "/" + date.substring(5, 7) + "/" + date.substring(8, 10) + "/";
		} catch (Exception e) {
			logger.error("转换日期错误。");
		}
		return Constant.UNKNOWN;
	}

	/**
	 * 用于生成聚类后结果文件名
	 * 
	 * @param date
	 * @return
	 */
	public static String convertDateToName(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHH");
		try {
			return simpleDateFormat.format(date);
		} catch (Exception e) {
			logger.error("转换日期错误。");
		}
		return Constant.UNKNOWN;
	}

	/**
	 * 将字符串数组转换为整型数组
	 * 
	 * @param array
	 * @return
	 */
	public static int[] toIntArray(String[] array) {
		if (array == null || array.length == 0) {
			return null;
		}
		int[] newArray = new int[array.length];
		for (int i = 0; i < array.length; i++) {
			newArray[i] = Integer.parseInt(array[i]);
		}
		return newArray;
	}

	/**
	 * 将整型数组转换为字符串数组
	 * 
	 * @param array
	 * @return
	 */
	public static String[] toStringArray(int[] array) {
		if (array == null) {
			return null;
		}
		String[] newArray = new String[array.length];
		for (int i = 0; i < array.length; i++) {
			newArray[i] = String.valueOf(array[i]);
		}
		return newArray;
	}

	/**
	 * 将字符串数组list转换为整型数组list
	 * 
	 * @param list
	 * @return
	 */
	public static List<int[]> toIntArrayList(List<String[]> list) {
		if (list == null || list.size() == 0) {
			return null;
		}
		List<int[]> newList = new ArrayList<>();
		for (String[] array : list) {
			newList.add(toIntArray(array));
		}
		return newList;
	}

	/**
	 * 将整型数组list转换为字符串数组list
	 * 
	 * @param list
	 * @return
	 */
	public static List<String[]> toStringArrayList(List<int[]> list) {
		if (list == null || list.size() == 0) {
			return null;
		}
		List<String[]> newList = new ArrayList<String[]>();
		for (int[] array : list) {
			newList.add(toStringArray(array));
		}
		return newList;
	}

	/**
	 * 将字符串数组list转换为...
	 * 
	 * @param list
	 * @return
	 */
	public static List<List<Integer>> toIntListList(List<String[]> list) {
		if (list == null || list.size() == 0) {
			return null;
		}
		List<List<Integer>> newList = new ArrayList<List<Integer>>();
		for (String[] strs : list) {
			List<Integer> sub = new ArrayList<Integer>();
			for (String str : strs) {
				sub.add(Integer.valueOf(str));
			}
			newList.add(sub);
		}

		return newList;
	}

	/**
	 * 将(所有类（单个类（类内元素的index））)变为（所有类（单个类））
	 * 
	 * @param list
	 *            聚类结果集（index）
	 * @return
	 */
	public static List<String[]> toStringArrayListB(List<List<Integer>> list) {
		if (list == null || list.size() == 0) {
			return null;
		}
		List<String[]> newList = new ArrayList<String[]>();
		for (List<Integer> set : list) {
			String[] array = new String[set.size()];
			for (int i = 0; i < set.size(); i++) {
				array[i] = set.get(i) + "";
			}
			newList.add(array);
		}
		return newList;
	}
}
