package com.hust.scdx.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

	/**
	 * 将得到的类簇集排序
	 * 
	 * @param content
	 * @param resultIndexSetList
	 */
	public static void sort(List<String[]> content, List<List<Integer>> resultIndexSetList) {
		List<String[]> tmp = new ArrayList<String[]>(content);
		String[] attrs = tmp.remove(0);
		int indexOfTitle = AttrUtil.findIndexOfTitle(attrs);
		int indexOfTime = AttrUtil.findIndexOfTime(attrs);
		// 重载排序的方法，按照降序。类中数量多的排在前面。
		Collections.sort(resultIndexSetList, new Comparator<List<Integer>>() {
			@Override
			public int compare(List<Integer> o1, List<Integer> o2) {
				// TODO Auto-generated method stub
				return o2.size() - o1.size();
			}
		});
		for (List<Integer> set : resultIndexSetList) {
			Collections.sort(set, new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {
					// TODO Auto-generated method stub
					// 判断他们的标题是否相同
					int compare = tmp.get(o1)[indexOfTitle].compareTo(tmp.get(o2)[indexOfTitle]);
					// 若不相同，使用时间进行排序。
					if (compare == 0) {
						compare = tmp.get(o1)[indexOfTime].compareTo(tmp.get(o2)[indexOfTime]);
					}
					return compare;
				}
			});
		}
	}
}
