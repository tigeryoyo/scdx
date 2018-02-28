package com.hust.scdx.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import com.hust.scdx.constant.Constant.AttrID;
import com.hust.scdx.constant.Constant.Index;
import com.hust.scdx.constant.Constant.Interval;
import com.hust.scdx.model.Domain;

public class AttrUtil {

	private volatile static AttrUtil singleton;

	private AttrUtil() {

	}

	public static AttrUtil getSingleton() {
		if (singleton == null) {
			synchronized (AttrUtil.class) {
				if (singleton == null) {
					singleton = new AttrUtil();
				}
			}
		}
		return singleton;
	}

	// 最核心冗余属性
	String title_mainName; // 标题
	String title_alias; // 标题|内容
	String url_mainName; // 链接
	String url_alias; // 链接|网址|域名|微博链接|[Uu][Rr][Ll]
	String time_mainName; // 时间
	String time_alias; // 发布时间|发帖时间|发贴时间|时间
	// 次要冗余属性
	String posting_mainName; // 发帖人
	String posting_alias; // 发帖人|发贴人|作者|发布人|来源/发布人|发布者昵称
	String webname_mainName; // 网站
	String webname_alias; // 网站|媒体名称
	String type_mainName; // 来源
	String type_alias; // 来源|类型|资源类型|媒体类型
	String column_mainName; // 板块
	String column_alias; // 板块|频道|频道分类
	String rank_mainName; // 媒体级别
	String rank_alias;// 媒体级别
	String weight_mainName;// 权重
	String weight_alias; // 权重
	String incidence_mainName;// 影响范围
	String incidence_alias;// 影响范围

	// 存储的是自定义的属性名称
	List<String> attrs_mainName;
	// 对应的自定义属性别名
	List<String> attrs_alias;

	public String getTitle_mainName() {
		return title_mainName;
	}

	public void setTitle_mainName(String title_mainName) {
		this.title_mainName = title_mainName;
	}

	public String getTitle_alias() {
		return title_alias;
	}

	public void setTitle_alias(String title_alias) {
		this.title_alias = title_alias;
	}

	public String getUrl_mainName() {
		return url_mainName;
	}

	public void setUrl_mainName(String url_mainName) {
		this.url_mainName = url_mainName;
	}

	public String getUrl_alias() {
		return url_alias;
	}

	public void setUrl_alias(String url_alias) {
		this.url_alias = url_alias;
	}

	public String getTime_mainName() {
		return time_mainName;
	}

	public void setTime_mainName(String time_mainName) {
		this.time_mainName = time_mainName;
	}

	public String getTime_alias() {
		return time_alias;
	}

	public void setTime_alias(String time_alias) {
		this.time_alias = time_alias;
	}

	public List<String> getAttrs_mainName() {
		return attrs_mainName;
	}

	public void setAttrs_mainName(List<String> attrs_mainName) {
		this.attrs_mainName = attrs_mainName;
	}

	public List<String> getAttrs_alias() {
		return attrs_alias;
	}

	public void setAttrs_alias(List<String> attrs_alias) {
		this.attrs_alias = attrs_alias;
	}

	public String getType_mainName() {
		return type_mainName;
	}

	public void setType_mainName(String type_mainName) {
		this.type_mainName = type_mainName;
	}

	public String getType_alias() {
		return type_alias;
	}

	public void setType_alias(String type_alias) {
		this.type_alias = type_alias;
	}

	public String getRank_mainName() {
		return rank_mainName;
	}

	public void setRank_mainName(String rank_mainName) {
		this.rank_mainName = rank_mainName;
	}

	public String getRank_alias() {
		return rank_alias;
	}

	public void setRank_alias(String rank_alias) {
		this.rank_alias = rank_alias;
	}

	public String getPosting_mainName() {
		return posting_mainName;
	}

	public void setPosting_mainName(String posting_mainName) {
		this.posting_mainName = posting_mainName;
	}

	public String getPosting_alias() {
		return posting_alias;
	}

	public void setPosting_alias(String posting_alias) {
		this.posting_alias = posting_alias;
	}

	public String getWebname_mainName() {
		return webname_mainName;
	}

	public void setWebname_mainName(String webname_mainName) {
		this.webname_mainName = webname_mainName;
	}

	public String getWebname_alias() {
		return webname_alias;
	}

	public void setWebname_alias(String webname_alias) {
		this.webname_alias = webname_alias;
	}

	public String getColumn_mainName() {
		return column_mainName;
	}

	public void setColumn_mainName(String column_mainName) {
		this.column_mainName = column_mainName;
	}

	public String getColumn_alias() {
		return column_alias;
	}

	public void setColumn_alias(String column_alias) {
		this.column_alias = column_alias;
	}

	public String getWeight_mainName() {
		return weight_mainName;
	}

	public void setWeight_mainName(String weight_mainName) {
		this.weight_mainName = weight_mainName;
	}

	public String getWeight_alias() {
		return weight_alias;
	}

	public void setWeight_alias(String weight_alias) {
		this.weight_alias = weight_alias;
	}

	public String getIncidence_mainName() {
		return incidence_mainName;
	}

	public void setIncidence_mainName(String incidence_mainName) {
		this.incidence_mainName = incidence_mainName;
	}

	public String getIncidence_alias() {
		return incidence_alias;
	}

	public void setIncidence_alias(String incidence_alias) {
		this.incidence_alias = incidence_alias;
	}

	public int findIndexOf(String[] attrs, String pattern) {
		for (int i = 0; i < attrs.length; i++) {
			if (Pattern.matches(pattern, attrs[i])) {
				return i;
			}
		}
		return -1;
	}

	public int findIndexOf(List<String> attrs, String pattern) {
		int size = attrs.size();
		for (int i = 0; i < size; i++) {
			if (Pattern.matches(pattern, attrs.get(i))) {
				return i;
			}
		}
		return -1;
	}

	// title/url/time
	public int[] findEssentialIndex(String[] attrs) {
		int[] res = new int[3];
		res[Index.TITLE] = findIndexOf(attrs, title_alias);
		res[Index.URL] = findIndexOf(attrs, url_alias);
		res[Index.TIME] = findIndexOf(attrs, time_alias);
		return res;
	}

	/**
	 * 判断attr是否在自定义属性里
	 * 
	 * @param attr
	 * @return
	 */
	public int isIndexOf(String attr) {
		int size = attrs_alias.size();
		for (int i = 0; i < size; i++) {
			if (Pattern.matches(attrs_alias.get(i), attr)) {
				return i;
			}
		}
		return -1;
	}

	// /**
	// * 标题 "标题|内容"
	// */
	// public static final String TITLE_PATTERN = "标题|内容";
	// /**
	// * url "链接|网址|域名|微博链接|[Uu][Rr][Ll]"
	// */
	// public static final String URL_PATTERN = "链接|网址|域名|微博链接|[Uu][Rr][Ll]";
	// /**
	// * 时间 "发布时间|发贴时间|时间"
	// */
	// public static final String TIME_PATTERN = "发布时间|发帖时间|发贴时间|时间";
	// /**
	// * 发帖人 "发帖人|发贴人"
	// */
	// public static final String POSTING = "发帖人|发贴人|作者|发布人|来源/发布人|发布者昵称";
	// /**
	// * 网站名称 "网站|媒体名称"
	// */
	// public static final String WEBNAME_PATTERN = "网站|媒体名称";
	// /**
	// * 网站类型 "来源|类型|资源类型"
	// */
	// public static final String TYPE_PATTERN = "来源|类型|资源类型|媒体类型";
	// /**
	// * 网站所属模块 "板块|频道"
	// */
	// public static final String COLUMN_PATTERN = "板块|频道|频道分类";
	// /**
	// * 网站级别 "媒体级别"
	// */
	// public static final String RANK_PATTERN = "媒体级别";
	// /**
	// * 网站权重 "权重"
	// */
	// public static final String WEIGHT_PATTERN = "权重";
	// /**
	// * 网站影响范围 "影响范围"
	// */
	// public static final String INCIDENCE_PATTERN = "影响范围";

	/**
	 * 统计日期-数量与来源-数量
	 * 
	 * @param content
	 * @return
	 */
	public static Map<String, TreeMap<String, Integer>> statistics(List<String[]> content) {
		AttrUtil attrUtil = AttrUtil.getSingleton();
		HashMap<String, TreeMap<String, Integer>> map = new HashMap<String, TreeMap<String, Integer>>();
		int indexOfUrl = attrUtil.findIndexOf(content.get(0), attrUtil.getUrl_alias());
		int indexOfTime = attrUtil.findIndexOf(content.get(0), attrUtil.getTime_alias());
		int indexOfType = attrUtil.findIndexOf(content.get(0), attrUtil.getType_alias());
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
				Domain domain = DomainCacheManager.getByUrl(row[indexOfUrl]);
				String type = row[indexOfType].trim();
				if (domain != null) {
					String tmp = domain.getType();
					if (!tmp.equals("其他") && !tmp.equals("")) {
						type = tmp;
					}
				}

				Integer webCount = webMap.get(type);
				if (webCount != null) {
					webMap.put(type, webCount + 1);
				} else {
					webMap.put(type, 1);
				}
			}
		}
		map.put(AttrID.TIME, timeMap);
		map.put(AttrID.TYPE, webMap);
		return map;
	}
}
