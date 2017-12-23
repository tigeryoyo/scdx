package com.hust.scdx.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.hust.scdx.constant.Config;
import com.hust.scdx.constant.Constant;
import com.hust.scdx.dao.DomainOneDao;
import com.hust.scdx.dao.DomainTwoDao;
import com.hust.scdx.dao.SourceTypeDao;
import com.hust.scdx.model.Domain;
import com.hust.scdx.model.DomainOne;
import com.hust.scdx.model.DomainTwo;
import com.hust.scdx.model.SourceType;
import com.hust.scdx.model.params.DomainOneQueryCondition;
import com.hust.scdx.model.params.DomainTwoQueryCondition;

/**
 * 域名分级清洗相关操作
 * 
 * @author Jack
 *
 */
public class UrlUtil {
	private static final Logger logger = LoggerFactory.getLogger(UrlUtil.class);

	private static final String RE_IP = "((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))(:[0-9]{1,})?$";
	private static final String RE = "(com\\.cn|cn\\.com|com\\.hk|net\\.cn|gov\\.cn|org\\.nz|org\\.cn|com\\.tw|"
			+ "com|cn|net|edu|xyz|xin|club|vip|top|red|org|wang|gov|edu|mil|co|biz|name|info|mobi|pro|travel|museum|int|aero|post|rec|asia|arts|firm|nom|store|web|cc|tv|coop|arpa|wiki|games|science|gift|help|hk|news|me|la"
			+ ")(:[0-9]{1,})?$";
	// 一级域名提取
	private static final String RE_TOP = "([\\w-]*\\.){1}" + RE;

	// 二级域名提取
	private static final String RE_SEC = "([\\w-]*\\.){2,}";

	// 三级域名提取
	// private static final String RE_THI = "(\\w*\\.){3,}";

	/**
	 * 初始化全局域名对象，添加未知URL时减少数据库查询操作
	 */
	static{
		// 初始化域名对象
				ApplicationContext applicationContext = new FileSystemXmlApplicationContext("classpath:spring-config.xml");
				DomainOneDao domainOneDao = applicationContext.getBean(DomainOneDao.class);
				DomainOneQueryCondition oneCondition = new DomainOneQueryCondition();
				oneCondition.setLimit(0);
				oneCondition.setStart(0);
				for (DomainOne domainOne : domainOneDao.getDomainOneOrderByTime(oneCondition)) {
					Domain domain = new Domain();
					domain.setDomainFormOne(domainOne);
					if (domain.getMaintenanceStatus()) {
						Constant.markedDomain.put(domainOne.getUrl(), domain);
					} else {
						Constant.unmarkedDomain.put(domainOne.getUrl(), domain);
					}
				}
				DomainTwoDao domaintwoDao = applicationContext.getBean(DomainTwoDao.class);
				DomainTwoQueryCondition twoCondition = new DomainTwoQueryCondition();
				for (DomainTwo domainTwo : domaintwoDao.getDomainTwoByCondition(twoCondition)) {
					Domain domain = new Domain();
					domain.setDomainFormTwo(domainTwo);
					if (domain.getMaintenanceStatus()) {
						Constant.markedDomain.put(domainTwo.getUrl(), domain);
					} else {
						Constant.unmarkedDomain.put(domainTwo.getUrl(), domain);
					}
				}
				/**
				 * 初始化类型
				 */
				SourceTypeDao sourceTypeDao = applicationContext.getBean(SourceTypeDao.class);
				List<SourceType> types = sourceTypeDao.selectSourceType(0, 0);
				for (SourceType sourceType : types) {
					Constant.typeMap.add(sourceType.getName());
				}
	}
	public static void initialDomainAndType() {
		if(Constant.markedDomain.size()>0){
			Constant.markedDomain.clear();
		}
		if(Constant.unmarkedDomain.size()>0){
			Constant.unmarkedDomain.clear();
		}
		if(Constant.typeMap.size()>0){
			Constant.typeMap.clear();
		}
		// 初始化域名对象
		ApplicationContext applicationContext = new FileSystemXmlApplicationContext("classpath:spring-config.xml");
		DomainOneDao domainOneDao = applicationContext.getBean(DomainOneDao.class);
		DomainOneQueryCondition oneCondition = new DomainOneQueryCondition();
		oneCondition.setLimit(0);
		oneCondition.setStart(0);
		for (DomainOne domainOne : domainOneDao.getDomainOneOrderByTime(oneCondition)) {
			Domain domain = new Domain();
			domain.setDomainFormOne(domainOne);
			if (domain.getMaintenanceStatus()) {
				Constant.markedDomain.put(domainOne.getUrl(), domain);
			} else {
				Constant.unmarkedDomain.put(domainOne.getUrl(), domain);
			}
		}
		DomainTwoDao domaintwoDao = applicationContext.getBean(DomainTwoDao.class);
		DomainTwoQueryCondition twoCondition = new DomainTwoQueryCondition();
		for (DomainTwo domainTwo : domaintwoDao.getDomainTwoByCondition(twoCondition)) {
			Domain domain = new Domain();
			domain.setDomainFormTwo(domainTwo);
			if (domain.getMaintenanceStatus()) {
				Constant.markedDomain.put(domainTwo.getUrl(), domain);
			} else {
				Constant.unmarkedDomain.put(domainTwo.getUrl(), domain);
			}
		}
		/**
		 * 初始化类型
		 */
		SourceTypeDao sourceTypeDao = applicationContext.getBean(SourceTypeDao.class);
		List<SourceType> types = sourceTypeDao.selectSourceType(0, 0);
		for (SourceType sourceType : types) {
			Constant.typeMap.add(sourceType.getName());
		}
	}

	/**
	 * 给定网址提取出完整域名（包含非80端口的端口）,同时清洗掉www.
	 * 
	 * @param url
	 *            网址，不符合格式的网址返回null
	 * @return 若输入有误则返回null
	 */
	public static String getUrl(String url) {
		String result = null;
		try {
			URL tool = new URL(url);
			result = tool.getHost();
			int port = tool.getPort();
			if (!(port == -1 || port == 80)) {
				result += ":" + port;
			}
			if (result.startsWith("www.")) {
				result = result.substring(4);
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			logger.info(e.getMessage());
			if (url.startsWith("www.")) {
				url = url.substring(4);
			}
			if (isDomain(url) || isIp(url)) {
				logger.info("不带http协议的域名url：" + url);
				return url;
			} else {
				return null;
			}
		}
		return result;
	}

	/**
	 * 批量提取网址中的完整域名，同时清洗掉www.
	 * 
	 * @param list
	 * @return
	 */
	public static List<String> getUrl(List<String> list) {
		List<String> result = new ArrayList<>();
		for (String string : list) {
			result.add(getUrl(string));
		}
		return result;
	}

	/**
	 * 获取清洗过后的完整域名中的一级域名
	 * 
	 * @param url
	 *            清洗过后的完整域名 形如（baidu.com）,如为ip地址则返回null
	 * @return
	 */
	public static String getDomainOne(String url) {
		if (url == null) {
			return null;
		}
		Pattern pattern = Pattern.compile(RE_TOP);
		Matcher matcher = pattern.matcher(url);
		if (matcher.find()) {
			return matcher.group();
		}
		return null;
	}

	/**
	 * 批量获取清洗过后的完整域名中的一级域名
	 * 
	 * @param list
	 *            清洗过后的完整域名 形如（baidu.com）
	 * @return
	 */
	public static List<String> getDomainOne(List<String> list) {
		List<String> oneList = new ArrayList<>();
		for (String string : list) {
			oneList.add(getDomainOne(string));
		}
		return oneList;
	}

	/**
	 * 获取清洗过后的完整域名中的二级域名
	 * 
	 * @param url
	 *            清洗过后的完整域名 形如（baidu.com）,没有二级域名的其对应String为null
	 * @return
	 */
	public static String getDomainTwo(String url) {
		/*
		 * String result = ""; Pattern pattern = Pattern.compile(RE_SEC);
		 * Matcher matcher = pattern.matcher(url); if (matcher.find()) { if
		 * (!matcher.group().equals(getDomainOne(url))) { return
		 * matcher.group(); } } return null;
		 */
		if (url == null) {
			return null;
		}
		String head = "";
		String end = "";
		Pattern pattern1 = Pattern.compile("\\." + RE);
		Matcher matcher1 = pattern1.matcher(url);
		if (matcher1.find()) {
			end = matcher1.group();
			url = matcher1.replaceFirst("");
		} else {
			return null;
		}
		Pattern pattern2 = Pattern.compile(RE_SEC);
		Matcher matcher2 = pattern2.matcher(url + ".");
		if (matcher2.find()) {
			head = matcher2.group();
		} else
			return null;
		return (head + end).replace("..", ".");
	}

	/**
	 * 批量获取清洗过后的完整域名中的二级域名
	 * 
	 * @param list
	 *            清洗过后的完整域名 形如（baidu.com）,没有二级域名的其对应String为null
	 * @return
	 */
	public static List<String> getDomainTwo(List<String> list) {
		List<String> twoList = new ArrayList<>();
		for (String string : list) {
			twoList.add(getDomainTwo(string));
		}
		return twoList;
	}

	/**
	 * 获取清洗过后的完整域名中的三级及以上域名
	 * 
	 * @param url
	 *            清洗过后的完整域名 形如（baidu.com）,没有三级及以上域名的其对应String为null
	 * @return
	 */
	/*
	 * public static String getDomainThr(String url){ Pattern pattern =
	 * Pattern.compile(RE_THI); Matcher matcher = pattern.matcher(url);
	 * if(matcher.find()){ return matcher.group(); } return null; }
	 */
	/*
	 * public static String getDomainThr(String url) { String head = ""; String
	 * end = ""; Pattern pattern1 = Pattern.compile(RE); Matcher matcher1 =
	 * pattern1.matcher(url); if (matcher1.find()) { end = matcher1.group(); url
	 * = matcher1.replaceFirst(""); } else { return null; } Pattern pattern2 =
	 * Pattern.compile(RE_THI); Matcher matcher2 = pattern2.matcher(url); if
	 * (matcher2.find()) { head = matcher2.group(); } else return null; return
	 * head + end; }
	 */

	/**
	 * 批量获取清洗过后的完整域名中的三级及以上域名
	 * 
	 * @param list
	 *            清洗过后的完整域名 形如（baidu.com）,没有三级及以上域名的其对应String为null
	 * @return
	 */
	/*
	 * public static List<String> getDomainThr(List<String> list) { List<String>
	 * thrList = new ArrayList<>(); for (String string : list) {
	 * thrList.add(getDomainThr(string)); } return thrList; }
	 */

	private static boolean isIp(String url) {
		Pattern pattern = Pattern.compile(RE_IP);
		Matcher matcher = pattern.matcher(url);
		if (matcher.find()) {
			return true;
		} else
			return false;
	}

	private static boolean isDomain(String url) {
		Pattern pattern = Pattern.compile("(\\w*\\.){1,}" + RE);
		Matcher matcher = pattern.matcher(url);
		if (matcher.find()) {
			return true;
		} else
			return false;
	}

	/**
	 * 获取url前缀
	 * 
	 * @param url
	 * @return
	 */
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
