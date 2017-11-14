package com.hust.scdx.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hust.scdx.constant.Constant;
import com.hust.scdx.constant.Constant.DomainExcelAttr;
import com.hust.scdx.dao.DomainOneDao;
import com.hust.scdx.dao.DomainTwoDao;
import com.hust.scdx.model.Domain;
import com.hust.scdx.model.DomainOne;
import com.hust.scdx.model.DomainTwo;
import com.hust.scdx.model.params.DomainOneQueryCondition;
import com.hust.scdx.model.params.DomainTwoQueryCondition;
import com.hust.scdx.service.DomainService;
import com.hust.scdx.util.AttrUtil;
import com.hust.scdx.util.ExcelUtil;
import com.hust.scdx.util.UrlUtil;

@Service
public class DomainServiceImpl implements DomainService {
	private static final Logger logger = LoggerFactory.getLogger(DomainServiceImpl.class);

	@Autowired
	private DomainOneDao domainOneDao;
	@Autowired
	private DomainTwoDao domainTwoDao;
	
	@Override
	public List<DomainOne> getDomainOneByCondition(DomainOneQueryCondition condition) {
		// TODO Auto-generated method stub
		return domainOneDao.getDomainOneOrderByTime(condition);
	}
	
	@Override
	public List<DomainTwo> getDomainTwoByCondition(DomainTwoQueryCondition condition) {
		// TODO Auto-generated method stub
		return domainTwoDao.getDomainTwoByCondition(condition);
	}
	
	@Override
	public List<List<DomainTwo>> getDomainTwoByOne(List<DomainOne> list) {
		// TODO Auto-generated method stub
		List<List<DomainTwo>> twoList = new ArrayList<>();
		for (DomainOne domainOne : list) {
			List<DomainTwo> two = new ArrayList<>();
			two = domainTwoDao.getDomainTwoByFatherId(domainOne.getUuid());
			twoList.add(two);
		}
		return twoList;
	}
	
	@Override
	public boolean addUnknowUrlFromFile(MultipartFile file) {
		// TODO Auto-generated method stub
		try {
			InputStream input = file.getInputStream();
			String fileName = file.getOriginalFilename();
			List<String[]> content = ExcelUtil.readExcel(fileName, input);
			if (content == null || content.size() <= 1) {
				logger.info("文件读取错误！");
				return false;
			}
			String[] attr = content.remove(0);
			boolean nameFlag=true,columnFlag=true,typeFlag = true;
			int urlIndex = AttrUtil.findIndexOfUrl(attr);
			int nameIndex = AttrUtil.findIndexOfSth(attr, AttrUtil.WEBNAME_PATTERN);//网站名
			if(nameIndex == -1){
				nameFlag = false;
			}
			int columnIndex = AttrUtil.findIndexOfSth(attr, AttrUtil.COLUMN_PATTERN);//栏目
			if(columnIndex == -1){
				columnFlag = false;
			}
			int typeIndex = AttrUtil.findIndexOfSth(attr, AttrUtil.COLUMN_PATTERN);//类型
			if(typeIndex == -1){
				typeFlag = false;
			}
			
			// 获取其他属性列的下标
			List<Domain> list = new ArrayList<>();
			for (String[] string : content) {
				if(string.length<=0 || null == string || Constant.existDomain.containsKey(UrlUtil.getUrl(string[urlIndex]))){
					continue;
				}
				Domain d = new Domain();
				d.setUrl(string[urlIndex]);
				if(nameFlag){
					if(StringUtils.isBlank(string[nameIndex])){
						d.setName("其他");
					}else{
						d.setName(string[nameIndex]);
					}					
				}
				if(columnFlag){
					if(StringUtils.isBlank(string[columnIndex])){
						d.setColumn("其他");
					}else{
						if(string[columnIndex].length()>32){
							d.setColumn(string[columnIndex].substring(0, 31));
						}else{
							d.setColumn(string[columnIndex]);
						}
					}
				}
				if(typeFlag){
					if(StringUtils.isBlank(string[typeIndex])){
						d.setType("其他");
					}else{
						d.setType(string[typeIndex]);
					}
				}
				/**
				 * 添加其他属性 注意判null
				 */
				list.add(d);
			}
			System.out.println("---------这是一个测试---------需要添加的未知url有----"+list.size());
			return addUnknowDomain(list);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.info(e.toString());
			return false;
		}
	}
	
	@Override
	public boolean addUrlFromFile(MultipartFile file) {
		// TODO Auto-generated method stub
		try {
			InputStream input = file.getInputStream();
			String fileName = file.getOriginalFilename();
			List<String[]> content = ExcelUtil.readExcel(fileName, input);
			if (content == null || content.size() <= 1) {
				logger.info("文件读取错误！");
				return false;
			}
			String[] attr = content.get(0);
			// 存放域名信息
			List<Domain> list = new ArrayList<>();
	
				for (int i = 1; i < content.size(); i++) {
					String[] info = content.get(i);
					Domain domain = Array2Domain(info);
					if(domain == null)
						continue;
					list.add(domain);
				}
			
			int count = 0;
			for (Domain d : list) {
				if (addDomain(d))
					count++;
			}
			if (count > 0)
				return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.info(e.toString());
			return false;
		}

		return false;
	}
	
	@Override
	public boolean addUnknowDomain(Domain domain) {
	//	int flag = 0;
		String url = UrlUtil.getUrl(domain.getUrl());
		if (null != url) {
			String one = UrlUtil.getDomainOne(url);
			if(null == one){
				//此时url为ip地址
				one = url;
			}
			String two = UrlUtil.getDomainTwo(url);
			if (null != two) {
				//该域名存在二级域名
				String fatherUuid = "";
				DomainOne domainOne = domainOneDao.getDomainOneByUrl(one);
				// 一级域名存在就获取uuid，不存在就插入数据库并获取uuid
				if (null == domainOne) {
					fatherUuid = UUID.randomUUID().toString();
					DomainOne father = domain.getDomainOneBaseProperty();
					father.setUuid(fatherUuid);
					father.setUrl(one);					
					father.setUpdateTime(new Date());
					father.setIsFather(true);
					if (!domainOneDao.insertDomain(father)){						
						return false;
					}else{
						//添加成功，写入全局域名存放在数据库中
						updateExistDomain(father);
					}
				} else {
					fatherUuid = domainOne.getUuid();
				}
				DomainTwo domainTwo = domainTwoDao.getDomainTwoByUrl(two);	
				// 不存在则插入，存在则更新
				if (null == domainTwo) {
					DomainTwo dt = domain.getDomainTwoBaseProperty();
					dt.setUuid(UUID.randomUUID().toString());
					dt.setFatherUuid(fatherUuid);
					dt.setUrl(two);
					dt.setUpdateTime(new Date());
					if (domainTwoDao.insertDomainTwo(dt)) {
						//添加成功，写入全局域名存放在数据库中
						updateExistDomain(dt);
						//插入成功则判断其父的isFather是否为真，诺为否则更新
						DomainOne dm = domainOneDao.getDomainOneByUrl(one);
						if (null != dm && !dm.getIsFather()) {
							dm.setIsFather(true);
							dm.setUuid(fatherUuid);
							dm.setUpdateTime(new Date());
							if(domainOneDao.updateDomainOneInfo(dm)){
								updateExistDomain(dm);
							}
						}
					} else {
						return false;
					}
				}
			} else {
				// 不是二级域名，就一定是一级域名
				DomainOne domainOne = domainOneDao.getDomainOneByUrl(one);
				// 一级域名存在就获取uuid，不存在就插入数据库并获取uuid
				if (null == domainOne) {
					domainOne = domain.getDomainOneBaseProperty();
					domainOne.setUuid(UUID.randomUUID().toString());
					domainOne.setIsFather(false);
					domainOne.setUrl(one);
					domainOne.setUpdateTime(new Date());
					if (!domainOneDao.insertDomain(domainOne))
						return false;
					else{
						//添加成功，写入全局域名存放在数据库中
						updateExistDomain(domainOne);
					}
				}
			}
		}else{
			return false;
		}
		return true;
	}
	
	@Override
	public boolean addUnknowDomain(List<Domain> domainList) {
		int count = 0;
		for (Domain domain : domainList) {
			if (addUnknowDomain(domain))
				count++;
		}
		if (count > 0)
			return true;
		return false;
	}
	
	@Override
	public boolean addDomain(Domain domain) {
		// TODO Auto-generated method stub
//		int flag = 0;
		String url = UrlUtil.getUrl(domain.getUrl());
		if (null != url) {
			String one = UrlUtil.getDomainOne(url);
			if(null == one){
				//此时 url为ip地址
				one = url;
			}
			String two = UrlUtil.getDomainTwo(url);
			if (null != two) {
				String fatherUuid = "";
				DomainOne domainOne = domainOneDao.getDomainOneByUrl(one);
				// 一级域名存在就获取uuid，不存在就插入数据库并获取uuid
				if (null == domainOne) {
					//一级域名不存在  插入一级域名
					fatherUuid = UUID.randomUUID().toString();
					domainOne = domain.getDomainOneBaseProperty();
					domainOne.setUuid(fatherUuid);
					domainOne.setUrl(one);
					domainOne.setIsFather(true);
					domainOne.setUpdateTime(new Date());
					if (!domainOneDao.insertDomain(domainOne)){
						return false;
					}else{
						//添加成功，写入全局域名存放在数据库中
						updateExistDomain(domainOne);
					}
				} else {
					fatherUuid = domainOne.getUuid();
				}
				DomainTwo domainTwo = domainTwoDao.getDomainTwoByUrl(two);	
				// 不存在则插入，存在则更新
				if (null == domainTwo) {
					//插入二级域名
					domainTwo = domain.getDomainTwoBaseProperty();
					domainTwo.setUuid(UUID.randomUUID().toString());
					domainTwo.setUrl(two);
					domainTwo.setFatherUuid(fatherUuid);
					domainTwo.setUpdateTime(new Date());
					if (domainTwoDao.insertDomainTwo(domainTwo)) {
						//添加成功，写入全局域名存放在数据库中
						updateExistDomain(domainTwo);
						//插入二级域名成功后，更新对应一级域名的isFather属性
						DomainOne dm = domainOneDao.getDomainOneByUrl(one);
						if (null != dm && !dm.getIsFather()) {
							dm.setIsFather(true);
							dm.setUuid(fatherUuid);
							dm.setUpdateTime(new Date());
							if(domainOneDao.updateDomainOneInfo(dm)){
								updateExistDomain(dm);
							}
						}
					} else {
						//插入二级域名失败
						return false;
					}
				} else {
					//更新二级域名
					DomainTwo dt = domain.getDomainTwoBaseProperty();
					dt.setUuid(domainTwo.getFatherUuid());
					dt.setUrl(two);
					dt.setFatherUuid(domainTwo.getFatherUuid());
					dt.setUpdateTime(new Date());
					System.out.println("---------------------------测试fatherUUid是否一样-------"+fatherUuid== domainTwo.getFatherUuid());
					if (!domainTwoDao.updateDomainTwo(dt))
						return false;
					else{
						updateExistDomain(dt);
					}
				}
			} else {
				// 不是二级域名，就一定是一级域名
				DomainOne domainOne = domainOneDao.getDomainOneByUrl(one);
				// 一级域名存在就获取uuid，不存在就插入数据库并获取uuid
				if (null == domainOne) {
					//插入一级域名
					domainOne = domain.getDomainOneBaseProperty();
					domainOne.setUuid(UUID.randomUUID().toString());
					domainOne.setUrl(one);
					domainOne.setIsFather(false);
					domainOne.setUpdateTime(new Date());
					if (!domainOneDao.insertDomain(domainOne))
						return false;
					else{
						//添加成功，写入全局域名存放在数据库中
						updateExistDomain(domainOne);
					}
				} else {
					//更新一级域名信息
					DomainOne dm = domain.getDomainOneBaseProperty();
					dm.setUuid(domainOne.getUuid());
					dm.setUrl(one);
					dm.setIsFather(domainOne.getIsFather());
					dm.setUpdateTime(new Date());
					if (!domainOneDao.updateDomainOneInfo(dm))
						return false;
					else{
						updateExistDomain(dm);
					}
				}
			}
		}else{
			return false;
		}		
		return true;

	}
	
	@Override
	public boolean deleteDomainOneById(String uuid) {
		// TODO Auto-generated method stub

		return domainOneDao.delelteDomainOneById(uuid);
	}
	
	@Override
	public long getDomainOneCount() {
		// TODO Auto-generated method stub
		return domainOneDao.getDomainOneCount();
	}

	@Override
	public boolean deleteDomainTwoById(String uuid) {
		// TODO Auto-generated method stub
		return domainTwoDao.deleteDomainById(uuid);
	}
	
	@Override
	public DomainOne getDomainOneById(String uuid) {
		// TODO Auto-generated method stub
		return domainOneDao.getDomainOneById(uuid);
	}
	
	@Override
	public DomainTwo getDomainTwoById(String uuid) {
		// TODO Auto-generated method stub
		return domainTwoDao.getDomainTwoById(uuid);
	}
	
	private Domain Array2Domain(String[] baseInfo){
		Domain domain = new Domain();
		String url  = baseInfo[DomainExcelAttr.URL_INDEX].trim();
		if(StringUtils.isBlank(url))
			return null;
		String name = baseInfo[DomainExcelAttr.NAME_INDEX].trim();
		if(StringUtils.isBlank(name))
			name = null;
		String column = baseInfo[DomainExcelAttr.COLUMN_INDEX].trim();
		if(StringUtils.isBlank(column))
			column = null;
		String type = baseInfo[DomainExcelAttr.TYPE_INDEX].trim();
		if(StringUtils.isBlank(type))
			type = null;
		String rank = baseInfo[DomainExcelAttr.RANK_INDEX].trim();
		if(StringUtils.isBlank(rank))
			rank = null;
		String incidence = baseInfo[DomainExcelAttr.INCIDENCE_INDEX].trim();
		if(StringUtils.isBlank(incidence))
			incidence = null;
		String weight = baseInfo[DomainExcelAttr.WEIGHT_INDEX].trim();
		if(StringUtils.isBlank(weight) || !StringUtils.isNumeric(weight))
			weight="0";
		domain.setUrl(url);
		domain.setName(name);
		domain.setColumn(column);
		domain.setType(type);
		domain.setRank(rank);
		domain.setIncidence(incidence);
		domain.setWeight(Integer.parseInt(weight));
		return domain;
	}

	@Override
	public boolean updateDomainOne(DomainOne one) {
		// TODO Auto-generated method stub
		if(domainOneDao.updateDomainOneInfo(one)){
			updateExistDomain(one);
			return true;
		}
		return false;
	}

	@Override
	public boolean updateDomainTwo(DomainTwo two) {
		// TODO Auto-generated method stub
		if(domainTwoDao.updateDomainTwo(two)){
			updateExistDomain(two);
			return true;
		}else{
			return false;
		}
	}
	
	public Domain getDomainByUrl(String url){
		String tmp = UrlUtil.getUrl(url);
		Domain domain = null;
		DomainOne domainOne = domainOneDao.getDomainOneByUrl(tmp);
		if(domainOne!= null){
			domain = new Domain();
			domain.setDomainFormOne(domainOne);
		}else{
			DomainTwo domainTwo = domainTwoDao.getDomainTwoByUrl(tmp);
			if(domainTwo!=null){
				domain = new Domain();
				domain.setDomainFormTwo(domainTwo);
			}else{
				domain = new Domain();
				logger.debug("-----------------------------"+url+"不存在"+tmp);
			}
		}		
		return domain;
	}
	
	private void updateExistDomain(DomainTwo two){
		Domain domain = new Domain();
		domain.setDomainFormTwo(two);
		Constant.existDomain.put(two.getUrl(), domain);
	}
	private void updateExistDomain(DomainOne one){
		Domain domain = new Domain();
		domain.setDomainFormOne(one);
		Constant.existDomain.put(one.getUrl(), domain);
	}
}
