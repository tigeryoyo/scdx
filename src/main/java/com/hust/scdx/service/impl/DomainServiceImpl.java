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
			boolean nameFlag = true, columnFlag = true, typeFlag = true, rankFlag = true;
			int urlIndex = AttrUtil.findIndexOfUrl(attr);
			// 获取其他属性列的下标
			int nameIndex = AttrUtil.findIndexOfSth(attr, AttrUtil.WEBNAME_PATTERN);// 网站名
			if (nameIndex == -1) {
				nameFlag = false;
			}
			int columnIndex = AttrUtil.findIndexOfSth(attr, AttrUtil.COLUMN_PATTERN);// 栏目
			if (columnIndex == -1) {
				columnFlag = false;
			}
			int typeIndex = AttrUtil.findIndexOfSth(attr, AttrUtil.TYPE_PATTERN);// 类型
			if (typeIndex == -1) {
				typeFlag = false;
			}
			int rankIndex = AttrUtil.findIndexOfSth(attr, AttrUtil.RANK_PATTERN);// 级别
			if (rankIndex == -1) {
				rankFlag = false;
			}
			// 存放不存在域名信息库的未知域名信息
			List<Domain> list = new ArrayList<>();
			for (String[] string : content) {
				if (string.length <= 0 || null == string) {
					continue;
				}
				if (Constant.existDomain.containsKey(UrlUtil.getUrl(string[urlIndex]))) {
					// 存在的域名信息若信息不完整（网站名为其他，或者其他属性为空）则更新该域名信息
					Domain old = Constant.existDomain.get(UrlUtil.getUrl(string[urlIndex]));// 存放在域名信息库中的域名信息
					Domain d = new Domain();// 存放需要更新的域名信息
					d.setUrl(string[urlIndex]);
					boolean upflag = false;// 判断是否需要更新
					System.out.print(old.getUrl());
					System.out.println("---------"+old.getName());
					if (old.getName().equals("其他")) {
						if (nameFlag && !StringUtils.isBlank(string[nameIndex])
								&& !string[nameIndex].trim().equals("其他")) {
							d.setName(string[nameIndex]);
							upflag = true;
						}
					}
					if (StringUtils.isBlank(old.getColumn())) {
						if (columnFlag && !StringUtils.isBlank(string[columnIndex])) {
							if (string[columnIndex].length() > 32) {
								d.setColumn(string[columnIndex].substring(0, 31));
							} else {
								d.setColumn(string[columnIndex]);
							}
							upflag = true;
						}
					}
					if (StringUtils.isBlank(old.getType()) || old.getType().equals("其他")) {
						if (typeFlag && !StringUtils.isBlank(string[typeIndex])) {
							d.setType(string[typeIndex]);
							upflag = true;
						}
					}
					if (StringUtils.isBlank(old.getRank()) || old.getRank().equals("其他")) {
						if (rankFlag && !StringUtils.isBlank(string[rankIndex])) {
							d.setRank(string[rankIndex]);
							upflag = true;
						}
					}
					if (upflag) {
						addDomain(d);
					}
				} else {
					Domain d = new Domain();
					d.setUrl(string[urlIndex]);
					if (nameFlag) {
						if (StringUtils.isBlank(string[nameIndex])) {
							d.setName("其他");
						} else {
							d.setName(string[nameIndex]);
						}
					}
					if (columnFlag) {
						if (StringUtils.isBlank(string[columnIndex])) {
							d.setColumn("");
						} else {
							if (string[columnIndex].length() > 32) {
								d.setColumn(string[columnIndex].substring(0, 31));
							} else {
								d.setColumn(string[columnIndex]);
							}
						}
					}
					if (typeFlag) {
						if (StringUtils.isBlank(string[typeIndex])) {
							d.setType("其他");
						} else {
							d.setType(string[typeIndex]);
						}
					}
					if (rankFlag) {
						if (StringUtils.isBlank(string[rankIndex])) {
							d.setRank("其他");
						} else {
							d.setRank(string[rankIndex]);
						}
					}
					/**
					 * 添加其他属性 注意判null
					 */
					list.add(d);
				}
			}
			// System.out.println("---------这是一个测试---------需要添加的未知url有----"+list.size());
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
			// 存放已知域名信息，用来更新域名
			List<Domain> list = new ArrayList<>();
			List<Domain> unexist = new ArrayList<Domain>();

			for (int i = 1; i < content.size(); i++) {
				String[] info = content.get(i);
				Domain domain = Array2Domain(info);
				if (domain == null)
					continue;
				if (Constant.existDomain.containsKey(domain.getUrl()))
					list.add(domain);
				else
					unexist.add(domain);
			}

			int count = 0;
			// 更新已知url
			System.out.println("共" + list.size() + "条已知域名需要更新！");
			for (Domain d : list) {
				if (addDomain(d))
					count++;
			}
			// 添加未知url
			System.out.println("共" + unexist.size() + "条未知域名需要添加！");
			if (unexist.size() > 0) {
				addUnknowDomain(unexist);
			}
			if (count > 0)
				return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.info(e.toString());
			return false;
		}

		return true;
	}

	@Override
	public boolean addUnknowDomain(Domain domain) {
		// int flag = 0;
		String url = UrlUtil.getUrl(domain.getUrl());
		if (null != url) {
			String one = UrlUtil.getDomainOne(url);
			if (null == one) {
				// 此时url为ip地址
				one = url;
			}
			String two = UrlUtil.getDomainTwo(url);
			if (null != two) {
				// 该域名存在二级域名
				String fatherUuid = "";
				DomainOne domainOne = domainOneDao.getDomainOneByUrl(one);
				// 一级域名存在就获取uuid，不存在就插入数据库并获取uuid
				if (null == domainOne) {
					fatherUuid = UUID.randomUUID().toString();
					DomainOne father = domain.getDomainOneBaseProperty();
					father.setUuid(fatherUuid);
					father.setUrl(one);
					father.setUpdateTime(new Date());
					father.setIsFather(false);
					if (!domainOneDao.insertDomain(father)) {
						return false;
					} else {
						// 添加成功，写入全局域名存放在数据库中
						domainOne = father;
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
						// 添加成功，写入全局域名存放在数据库中
						updateExistDomain(dt);
						// 插入成功则判断其父的isFather是否为真，诺为否则更新
						// DomainOne dm = domainOneDao.getDomainOneByUrl(one);
						if (null != domainOne && !domainOne.getIsFather()) {
							domainOne.setIsFather(true);
							domainOne.setUpdateTime(new Date());
							if (domainOneDao.updateDomainOneInfo(domainOne)) {
								updateExistDomain(domainOne);
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
					else {
						// 添加成功，写入全局域名存放在数据库中
						updateExistDomain(domainOne);
					}
				}
			}
		} else {
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
		String url = UrlUtil.getUrl(domain.getUrl());
		if (null != url) {
			String one = UrlUtil.getDomainOne(url);
			if (null == one) {
				// 此时 url为ip地址
				one = url;
			}
			String two = UrlUtil.getDomainTwo(url);
			if (null != two) {
				DomainTwo domainTwo = domainTwoDao.getDomainTwoByUrl(two);
				if (null == domainTwo) {
					logger.error(two + "二级域名更新失败！");
					return false;
				}
				DomainTwo dt = domain.getDomainTwoBaseProperty();
				dt.setUuid(domainTwo.getUuid());
				dt.setUrl(two);
				dt.setUpdateTime(new Date());
				if (!domainTwoDao.updateDomainTwo(dt))
					return false;
				else {
					updateExistDomain(dt);
				}
			} else {
				// 不是二级域名，就一定是一级域名
				DomainOne domainOne = domainOneDao.getDomainOneByUrl(one);
				if (null == domainOne) {
					logger.error(one + "一级域名更新失败！");
					return false;
				}
				// 更新一级域名信息
				DomainOne dm = domain.getDomainOneBaseProperty();
				dm.setUuid(domainOne.getUuid());
				dm.setUrl(one);
				dm.setUpdateTime(new Date());
				if (!domainOneDao.updateDomainOneInfo(dm))
					return false;
				else {
					updateExistDomain(dm);
				}
			}
		} else
			return false;
		return true;
	}

	/*
	 * @Override public boolean addDomain(Domain domain) { // TODO
	 * Auto-generated method stub // int flag = 0; String url =
	 * UrlUtil.getUrl(domain.getUrl()); if (null != url) { String one =
	 * UrlUtil.getDomainOne(url); if(null == one){ //此时 url为ip地址 one = url; }
	 * String two = UrlUtil.getDomainTwo(url); if (null != two) { String
	 * fatherUuid = ""; DomainOne domainOne =
	 * domainOneDao.getDomainOneByUrl(one); // 一级域名存在就获取uuid，不存在就插入数据库并获取uuid if
	 * (null == domainOne) { //一级域名不存在 插入一级域名 fatherUuid =
	 * UUID.randomUUID().toString(); domainOne =
	 * domain.getDomainOneBaseProperty(); domainOne.setUuid(fatherUuid);
	 * domainOne.setUrl(one); domainOne.setIsFather(true);
	 * domainOne.setUpdateTime(new Date()); if
	 * (!domainOneDao.insertDomain(domainOne)){ return false; }else{
	 * //添加成功，写入全局域名存放在数据库中 updateExistDomain(domainOne); } } else { fatherUuid
	 * = domainOne.getUuid(); } DomainTwo domainTwo =
	 * domainTwoDao.getDomainTwoByUrl(two); // 不存在则插入，存在则更新 if (null ==
	 * domainTwo) { //插入二级域名 domainTwo = domain.getDomainTwoBaseProperty();
	 * domainTwo.setUuid(UUID.randomUUID().toString()); domainTwo.setUrl(two);
	 * domainTwo.setFatherUuid(fatherUuid); domainTwo.setUpdateTime(new Date());
	 * if (domainTwoDao.insertDomainTwo(domainTwo)) { //添加成功，写入全局域名存放在数据库中
	 * updateExistDomain(domainTwo); //插入二级域名成功后，更新对应一级域名的isFather属性 DomainOne
	 * dm = domainOneDao.getDomainOneByUrl(one); if (null != dm &&
	 * !dm.getIsFather()) { dm.setIsFather(true); dm.setUuid(fatherUuid);
	 * dm.setUpdateTime(new Date()); if(domainOneDao.updateDomainOneInfo(dm)){
	 * updateExistDomain(dm); } } } else { //插入二级域名失败 return false; } } else {
	 * //更新二级域名 DomainTwo dt = domain.getDomainTwoBaseProperty();
	 * dt.setUuid(domainTwo.getFatherUuid()); dt.setUrl(two);
	 * dt.setFatherUuid(domainTwo.getFatherUuid()); dt.setUpdateTime(new
	 * Date());
	 * System.out.println("---------------------------测试fatherUUid是否一样-------"+
	 * fatherUuid== domainTwo.getFatherUuid()); if
	 * (!domainTwoDao.updateDomainTwo(dt)) return false; else{
	 * updateExistDomain(dt); } } } else { // 不是二级域名，就一定是一级域名 DomainOne
	 * domainOne = domainOneDao.getDomainOneByUrl(one); //
	 * 一级域名存在就获取uuid，不存在就插入数据库并获取uuid if (null == domainOne) { //插入一级域名
	 * domainOne = domain.getDomainOneBaseProperty();
	 * domainOne.setUuid(UUID.randomUUID().toString()); domainOne.setUrl(one);
	 * domainOne.setIsFather(false); domainOne.setUpdateTime(new Date()); if
	 * (!domainOneDao.insertDomain(domainOne)) return false; else{
	 * //添加成功，写入全局域名存放在数据库中 updateExistDomain(domainOne); } } else { //更新一级域名信息
	 * DomainOne dm = domain.getDomainOneBaseProperty();
	 * dm.setUuid(domainOne.getUuid()); dm.setUrl(one);
	 * dm.setIsFather(domainOne.getIsFather()); dm.setUpdateTime(new Date()); if
	 * (!domainOneDao.updateDomainOneInfo(dm)) return false; else{
	 * updateExistDomain(dm); } } } }else{ return false; } return true; }
	 */

	@Override
	public boolean deleteDomainOneById(String uuid) {
		// TODO Auto-generated method stub
		String url = domainOneDao.getDomainOneById(uuid).getUrl();
		List<DomainTwo> list = domainTwoDao.getDomainTwoByFatherId(uuid);
		if (domainOneDao.delelteDomainOneById(uuid)) {
			try {
				// 删除内存中的域名信息
				Constant.existDomain.remove(url);
				// 同时级联删除内存中的二级域名信息
				for (DomainTwo domainTwo : list) {
					Constant.existDomain.remove(domainTwo.getUrl());
				}
			} catch (Exception e) {
				logger.error("删除内存中的域名信息失败！");
				logger.error(e.getMessage());
			}
			return true;
		}
		return false;
	}

	@Override
	public long getDomainOneCount() {
		// TODO Auto-generated method stub
		return domainOneDao.getDomainOneCount();
	}

	@Override
	public boolean deleteDomainTwoById(String uuid) {
		// TODO Auto-generated method stub
		String url = domainTwoDao.getDomainTwoById(uuid).getUrl();
		if (domainTwoDao.deleteDomainById(uuid)) {
			try {
				// 删除内存中的域名信息
				Constant.existDomain.remove(url);
			} catch (Exception e) {
				logger.error("删除内存中的域名信息失败！");
				logger.error(e.getMessage());
			}
			return true;
		}
		return false;
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

	private Domain Array2Domain(String[] baseInfo) {
		Domain domain = new Domain();
		String url = baseInfo[DomainExcelAttr.URL_INDEX].trim();
		if (StringUtils.isBlank(url))
			return null;
		String name = baseInfo[DomainExcelAttr.NAME_INDEX].trim();
		if (StringUtils.isBlank(name)) {
			name = null;
		}
		String column = baseInfo[DomainExcelAttr.COLUMN_INDEX].trim();
		if (StringUtils.isBlank(column))
			column = null;
		String type = baseInfo[DomainExcelAttr.TYPE_INDEX].trim();
		if (StringUtils.isBlank(type))
			type = null;
		String rank = baseInfo[DomainExcelAttr.RANK_INDEX].trim();
		if (StringUtils.isBlank(rank))
			rank = null;
		String incidence = baseInfo[DomainExcelAttr.INCIDENCE_INDEX].trim();
		if (StringUtils.isBlank(incidence))
			incidence = null;
		String weight = baseInfo[DomainExcelAttr.WEIGHT_INDEX].trim();
		if (StringUtils.isBlank(weight) || !StringUtils.isNumeric(weight))
			weight = "0";
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
		if (domainOneDao.updateDomainOneInfo(one)) {
			updateExistDomain(one);
			return true;
		}
		return false;
	}

	@Override
	public boolean updateDomainTwo(DomainTwo two) {
		// TODO Auto-generated method stub
		if (domainTwoDao.updateDomainTwo(two)) {
			updateExistDomain(two);
			return true;
		} else {
			return false;
		}
	}

	public Domain getDomainByUrl(String url) {
		String tmp = UrlUtil.getUrl(url);
		Domain domain = null;
		DomainOne domainOne = domainOneDao.getDomainOneByUrl(tmp);
		if (domainOne != null) {
			domain = new Domain();
			domain.setDomainFormOne(domainOne);
		} else {
			DomainTwo domainTwo = domainTwoDao.getDomainTwoByUrl(tmp);
			if (domainTwo != null) {
				domain = new Domain();
				domain.setDomainFormTwo(domainTwo);
			} else {
				domain = new Domain();
				logger.debug("-----------------------------" + url + "不存在" + tmp);
			}
		}
		return domain;
	}

	private void updateExistDomain(DomainTwo two) {
		Domain domain = new Domain();
		domain.setDomainFormTwo(two);
		if (Constant.existDomain.containsKey(two.getUrl())) {
			Domain old = Constant.existDomain.get(two.getUrl());
			if (domain.getName() != null) {
				old.setName(domain.getName());
			}else{
				System.out.println(two.getUrl()+"域名名为null，"+old.getName()+"-----------");
			}
			if (domain.getColumn() != null) {
				old.setColumn(domain.getColumn());
			}
			if (domain.getType() != null) {
				old.setType(domain.getType());
			}
			if (domain.getRank() != null) {
				old.setRank(domain.getRank());
			}
			if (domain.getWeight() != null) {
				old.setWeight(domain.getWeight());
			}
			if (domain.getIncidence() != null) {
				old.setIncidence(domain.getIncidence());
			}
			System.out.println("UP-------name:"+old.getName()+"column:"+old.getColumn()+"rank:"+old.getRank()+"type:"+old.getType()+"weight"+old.getWeight()+"incidence"+old.getIncidence());
			Constant.existDomain.put(two.getUrl(), old);
		} else {
			System.out.println("IN-----"+domain.getUrl()+"-----name:"+domain.getName()+"column:"+domain.getColumn()+"rank:"+domain.getRank()+"type:"+domain.getType()+"weight"+domain.getWeight()+"incidence"+domain.getIncidence());
			Constant.existDomain.put(two.getUrl(), domain);
		}
	}

	private void updateExistDomain(DomainOne one) {
		Domain domain = new Domain();
		domain.setDomainFormOne(one);
		if (Constant.existDomain.containsKey(one.getUrl())) {
			//域名信息存在，做更新操作，否则做添加操作
			Domain old = Constant.existDomain.get(one.getUrl());
			if (domain.getName() != null) {
				old.setName(domain.getName());
			}else{
				System.out.println(one.getUrl()+"域名名为null，"+old.getName()+"-----------");
			}
			if (domain.getColumn() != null) {
				old.setColumn(domain.getColumn());
			}
			if (domain.getType() != null) {
				old.setType(domain.getType());
			}
			if (domain.getRank() != null) {
				old.setRank(domain.getRank());
			}
			if (domain.getWeight() != null) {
				old.setWeight(domain.getWeight());
			}
			if (domain.getIncidence() != null) {
				old.setIncidence(domain.getIncidence());
			}
			System.out.println("UP-------name:"+old.getName()+"column:"+old.getColumn()+"rank:"+old.getRank()+"type:"+old.getType()+"weight"+old.getWeight()+"incidence"+old.getIncidence());
			Constant.existDomain.put(one.getUrl(), old);
		} else {
			System.out.println("IN---"+domain.getUrl()+"----name:"+domain.getName()+"column:"+domain.getColumn()+"rank:"+domain.getRank()+"type:"+domain.getType()+"weight"+domain.getWeight()+"incidence"+domain.getIncidence());
			Constant.existDomain.put(one.getUrl(), domain);
		}
	}
}
