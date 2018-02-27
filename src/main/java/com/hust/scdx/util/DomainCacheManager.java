package com.hust.scdx.util;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.hust.scdx.constant.Constant;
import com.hust.scdx.dao.DomainOneDao;
import com.hust.scdx.dao.DomainTwoDao;
import com.hust.scdx.model.Domain;
import com.hust.scdx.model.DomainOne;
import com.hust.scdx.model.DomainTwo;
import com.hust.scdx.model.params.DomainOneQueryCondition;
import com.hust.scdx.model.params.DomainTwoQueryCondition;

public class DomainCacheManager {
	/**
	 * 初始化全局变量Domain作为缓存
	 */
	static{
		ApplicationContext applicationContext = new FileSystemXmlApplicationContext("classpath:spring-config.xml");
		DomainOneDao domainOneDao = applicationContext.getBean(DomainOneDao.class);
		DomainOneQueryCondition oneCondition = new DomainOneQueryCondition();
		oneCondition.setLimit(0);
		oneCondition.setStart(0);
		List<DomainOne> oneList = domainOneDao.getDomainOneOrderByTime(oneCondition);
		DomainTwoDao domaintwoDao = applicationContext.getBean(DomainTwoDao.class);
		DomainTwoQueryCondition twoCondition = new DomainTwoQueryCondition();
		List<DomainTwo> twoList = domaintwoDao.getDomainTwoByCondition(twoCondition);
		Constant.DomainCache = new ConcurrentHashMap<>((int) ((oneList.size()+twoList.size())/0.55f), 0.6f);
		for (DomainOne domainOne : oneList) {
			Domain domain = new Domain();
			domain.setDomainFormOne(domainOne);
			Constant.DomainCache.put(domain.getUrl(), domain);
		}
		
		for (DomainTwo domainTwo : twoList) {
			Domain domain = new Domain();
			domain.setDomainFormTwo(domainTwo);
			Constant.DomainCache.put(domain.getUrl(), domain);
		}
	}
	
	public static void initDomainCache(){
		ApplicationContext applicationContext = new FileSystemXmlApplicationContext("classpath:spring-config.xml");
		DomainOneDao domainOneDao = applicationContext.getBean(DomainOneDao.class);
		DomainOneQueryCondition oneCondition = new DomainOneQueryCondition();
		oneCondition.setLimit(0);
		oneCondition.setStart(0);
		List<DomainOne> oneList = domainOneDao.getDomainOneOrderByTime(oneCondition);
		DomainTwoDao domaintwoDao = applicationContext.getBean(DomainTwoDao.class);
		DomainTwoQueryCondition twoCondition = new DomainTwoQueryCondition();
		List<DomainTwo> twoList = domaintwoDao.getDomainTwoByCondition(twoCondition);
		Constant.DomainCache = new ConcurrentHashMap<>((int) ((oneList.size()+twoList.size())/0.55f), 0.6f);
		for (DomainOne domainOne : oneList) {
			Domain domain = new Domain();
			domain.setDomainFormOne(domainOne);
			Constant.DomainCache.put(domain.getUrl(), domain);
		}
		
		for (DomainTwo domainTwo : twoList) {
			Domain domain = new Domain();
			domain.setDomainFormTwo(domainTwo);
			Constant.DomainCache.put(domain.getUrl(), domain);
		}
	}
	
	public static Domain deleteByUrl(String url){
		if(null != Constant.DomainCache && null != url){
			return Constant.DomainCache.remove(url);
		}
		return null;
	}
	
	public static boolean addDomain(Domain domain){
		if(null != domain){
			String url = domain.getUrl();
			if(null != url){
				Constant.DomainCache.put(url, domain);
				return true;
			}
		}
		return false;
	}
	
	public static Domain getByUrl(String url){
		if(null != Constant.DomainCache && null != url)
			return Constant.DomainCache.get(url);
		return null;
	}
	
	public static boolean isMaintained(String url){
		if(null != Constant.DomainCache && null != url && Constant.DomainCache.containsKey(url)){
			return Constant.DomainCache.get(url).getMaintenanceStatus();
		}
		return false;
	}
}
