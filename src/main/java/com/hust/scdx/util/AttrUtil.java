package com.hust.scdx.util;

import java.util.List;
import java.util.regex.Pattern;

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
}
