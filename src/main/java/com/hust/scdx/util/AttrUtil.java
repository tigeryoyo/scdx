package com.hust.scdx.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.apache.poi.ss.formula.eval.IntersectionEval;

import com.hust.scdx.constant.Constant.AttrID;
import com.hust.scdx.constant.Constant.Interval;
import com.hust.scdx.model.Domain;

public class AttrUtil {

	/**
	 * title、url、time
	 * 
	 * @param attrs
	 * @return
	 */
	public static int[] findEssentialIndex(String[] attrs) {
		int indexOfTitle = findIndexOfTitle(attrs);
		int indexOfUrl = findIndexOfUrl(attrs);
		int indexOfTime = findIndexOfTime(attrs);

		return new int[] { indexOfTitle, indexOfUrl, indexOfTime };
	}

	public static int findIndexOfSth(String[] attrs, String sth) {
		for (int i = 0; i < attrs.length; i++) {
			if (Pattern.matches(sth, attrs[i])) {
				return i;
			}
		}

		return -1;
	}

	public static int findIndexOfTitle(String[] attrs) {
		for (int i = 0; i < attrs.length; i++) {
			if (Pattern.matches("标题|内容", attrs[i])) {
				return i;
			}
		}

		return -1;
	}

	public static int findIndexOfTitle(List<String> attrs) {
		for (int i = 0; i < attrs.size(); i++) {
			if (Pattern.matches("标题|内容", attrs.get(i))) {
				return i;
			}
		}

		return -1;
	}

	public static int findIndexOfUrl(String[] attrs) {
		for (int i = 0; i < attrs.length; i++) {
			if (Pattern.matches("链接|网址|域名|微博链接|[Uu][Rr][Ll]", attrs[i])) {
				return i;
			}
		}

		return -1;
	}

	public static int findIndexOfWebName(String[] attrs) {
		for (int i = 0; i < attrs.length; i++) {
			if (Pattern.matches("媒体名称|网站|来源", attrs[i])) {
				return i;
			}
		}

		return -1;
	}

	public static int findIndexOfUrl(List<String> attrs) {
		for (int i = 0; i < attrs.size(); i++) {
			if (Pattern.matches("链接|网址|微博链接|[Uu][Rr][Ll]", attrs.get(i))) {
				return i;
			}
		}

		return -1;
	}

	public static int findIndexOfTime(String[] attrs) {
		for (int i = 0; i < attrs.length; i++) {
			if (Pattern.matches("发布时间|发贴时间|时间", attrs[i])) {
				return i;
			}
		}

		return -1;
	}

	public static int findIndexOfTime(List<String> attrs) {
		for (int i = 0; i < attrs.size(); i++) {
			if (Pattern.matches("发布时间|发贴时间|时间", attrs.get(i))) {
				return i;
			}
		}

		return -1;
	}

	public static boolean isTitle(String attr) {
		if (Pattern.matches("标题|内容", attr)) {
			return true;
		}

		return false;
	}

	public static boolean isUrl(String attr) {
		if (Pattern.matches("链接|网址|微博链接|[Uu][Rr][Ll]", attr)) {
			return true;
		}

		return false;
	}

	public static boolean isTime(String attr) {
		if (Pattern.matches("发布时间|发贴时间|时间", attr)) {
			return true;
		}

		return false;
	}

	/**
	 * 统计日期-数量与来源-数量
	 * 
	 * @param content
	 * @return
	 */
	public static Map<String, TreeMap<String, Integer>> statistics(List<String[]> content,
			HashMap<String, Domain> domains) {
		HashMap<String, TreeMap<String, Integer>> map = new HashMap<String, TreeMap<String, Integer>>();
		int indexOfUrl = findIndexOfUrl(content.get(0));
		int indexOfTime = findIndexOfTime(content.get(0));
		int indexOfWebName = findIndexOfWebName(content.get(0));
		TreeMap<String, Integer> timeMap = new TreeMap<String, Integer>();
		TreeMap<String, Integer> webMap = new TreeMap<String, Integer>();

		int len = content.size();
		for (int i = 1; i < len; i++) {
			String[] row = content.get(i);
			// 如果不为空行
			if (row.length != 0) {
				String time = TimeUtil.getTimeKey(row[indexOfTime], Interval.DAY);
				// 统计日期-数量
				Integer timeCount = timeMap.get(time);
				if (timeCount != null) {
					timeMap.put(time, timeCount + 1);
				} else {
					timeMap.put(time, 1);
				}

				// 统计类型-数量
				// 先根据url查询域名表是否包含此条url
				Domain domain = domains.get(row[indexOfUrl]);
				String webName = row[indexOfWebName].trim();
				if (domain != null) {
					String type = domain.getType();
					if (!type.equals("其他") && !type.equals("")) {
						webName = type;
					}
				}

				Integer webCount = webMap.get(webName);
				if (webCount != null) {
					webMap.put(webName, webCount + 1);
				} else {
					webMap.put(webName, 1);
				}
			}
		}
		map.put(AttrID.TIME, timeMap);
		map.put(AttrID.TYPE, webMap);
		return map;
	}
}
