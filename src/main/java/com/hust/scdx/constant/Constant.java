package com.hust.scdx.constant;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.util.ConcurrentHashSet;

import com.hust.scdx.model.Domain;

public class Constant {

	public final static String UESR = "user";
	public final static String USERPOWER = "userpower";

	public final static String UNKNOWN = "未知";
	public final static String INVALID_TIME = "1970-01-01";

	public final static String topicAttrOrder = "5;7;1;4;3;2;6;8;10;9;";
	
	/**
	 * 全局域名变量
	 */
	public static ConcurrentHashMap<String, Domain> DomainCache;
	
	/**
	 * 类型-权重
	 */
	public static ConcurrentHashSet<String> typeMap = new ConcurrentHashSet<String>();
	
	// 切片
	public final static int slices = 1250;
	// 线程数
	public final static int threads = 10;
	// 错误码
	public static final int ERROR_CODE = -1;
	// 错误返回内容
	public static final String UNKNOW_ERROR = "未知错误。";

	public static class Resutt {
		public final static String RESULT = "result";
		public final static String RESULTNAME = "resultName";
		public final static String MARKED = "marked";
		public final static String STAT = "stat";
	}

	public static class StdfileMap {
		public final static String NAME = "name";
		public final static String CONTENT = "content";
		public final static String MARKED = "marked";
		public final static String STAT = "stat";
		public final static String REPORT = "report";
	}

	public static class AttrID {
		public final static String TITLE = "TITLE";
		public final static String URL = "URL";
		public final static String TIME = "TIME";
		public final static String TYPE = "TYPE";
	}

	public static class Index {
		// 不能大于2
		public static final int TITLE = 0;
		public static final int URL = 1;
		public static final int TIME = 2;

		public static final int COUNT_ITEM_INDEX = 0;
		public static final int COUNT_ITEM_AMOUNT = 1;
	}

	public static class Cluster {
		public static final String CONTENT = "content";
		public static final String ORIGCLUSTERS = "origClusters";
		public static final String ORIGCOUNTS = "origCounts";
		public static final String MODIFYCLUSTERS = "modifyClusters";
		public static final String MODIFYCOUNTS = "modifyCounts";
		public static final String DISPLAYRESULT = "displayResult";

		public static final String RESULTID = "resultId";

		public static final String REDIS_CONTENT = "redis_content";
		public static final String REDIS_ORIGCLUSTER = "redis_OrigClusters";
		public static final String REDIS_ORIGCOUNT = "redis_OrigCounts";
	}

	/**
	 * 域名文件的基本属性列下标
	 * 
	 * @author Jack
	 *
	 */
	public static class DomainExcelAttr {
		public static final int URL_INDEX = 0;
		public static final int NAME_INDEX = 1;
		public static final int COLUMN_INDEX = 2;
		public static final int TYPE_INDEX = 3;
		public static final int RANK_INDEX = 4;
		public static final int INCIDENCE_INDEX = 5;
		public static final int WEIGHT_INDEX = 6;
		public static final int MAINTENANCE_STATUS_INDEX = 7;
	}

	public static class Algorithm {
		public static final int DIGITAL = 1;
		public static final int TFIDF = 2;

		public static final int AcrossSimilarity = 1; // 粗粒度
		public static final int CosSimilarity = 2; // 细粒度

		public static float SIMILARITYTHRESHOLD = 0.4f;
		public static int MINPTS = 2;
		public static float EPS = 0.6f;

		public static final int CANOPY = 1;
		public static final int KMEANS = 2;
		public static final int DBSCAN = 3;
	}

	public static class WordFont {
		public static final String HEITI = "黑体";
		public static final String XINSONGTI = "新宋体";
		public static final String KAITI = "楷体_GB2312";
		public static final String SONGTI = "宋体";
		public static final String FANGSONG = "华文仿宋";

		public static final String RED = "FF0000";
		public static final String GREEN = "008B00";
	}

	// 出图用到的参数
	public static class KEY {
		public static final String MINING_AMOUNT_TYPE = "typeAmount";
		public static final String MINING_AMOUNT_MEDIA = "mediaAmount";
		public final static String NETIZENATTENTION_EN = "netizenAttention";
		public final static String MEDIAATTENTION_EN = "mediaAttention";
		public final static String MEDIA_EN = "media";
		public final static String INFOTYPE_EN = "infoType";
		public final static String INVALID_TIME = "1970-01-01";
	}

	// 出图用到的 统计时间间隔
	public static class Interval {
		public static final int HOUR = 1;
		public static final int DAY = 2;
		public static final int MONTH = 3;
	}
}
