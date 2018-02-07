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
import com.hust.scdx.dao.SourceTypeDao;
import com.hust.scdx.dao.WeightDao;
import com.hust.scdx.model.Domain;
import com.hust.scdx.model.DomainOne;
import com.hust.scdx.model.DomainTwo;
import com.hust.scdx.model.Weight;
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

	@Autowired
	private WeightDao weightDao;
	
	@Autowired
	private SourceTypeDao typeDao;

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
			boolean nameFlag = true, columnFlag = true, typeFlag = true, rankFlag = true, weightFlag = true,
					incidenceFlag = true;
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
			int weightIndex = AttrUtil.findIndexOfSth(attr, AttrUtil.WEIGHT_PATTERN);// 权重
			if (weightIndex == -1) {
				weightFlag = false;
			}
			int incidenceIndex = AttrUtil.findIndexOfSth(attr, AttrUtil.INCIDENCE_PATTERN);// 影响类型
			if (incidenceIndex == -1) {
				incidenceFlag = false;
			}
			// 存放不存在域名信息库的未知域名信息
			List<Domain> list = new ArrayList<>();
			for (String[] string : content) {
				if (string.length <= 0 || null == string) {
					continue;
				}
				if (Constant.markedDomain.containsKey(UrlUtil.getUrl(string[urlIndex]))) {
					// 被标记为已维护的域名信息不做处理
					continue;
				} else if (Constant.unmarkedDomain.containsKey(UrlUtil.getUrl(string[urlIndex]))) {
					// 没有被标记为已维护的域名信息直接覆盖域名信息库(文件中没有的属性，设为null，即不更新)
					Domain d = new Domain();
					d.setUrl(string[urlIndex]);
					if (nameFlag) {
						if (StringUtils.isBlank(string[nameIndex])) {
							d.setName(null);
						} else {
							d.setName(string[nameIndex]);
						}
					}
					if (columnFlag) {
						if (StringUtils.isBlank(string[columnIndex])) {
							d.setColumn(null);
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
							d.setType(null);
						} else {
							d.setType(string[typeIndex]);
						}
					}
					if (rankFlag) {
						if (StringUtils.isBlank(string[rankIndex])) {
							d.setRank(null);
						} else {
							d.setRank(string[rankIndex]);
						}
					}
					if (weightFlag) {
						if (StringUtils.isBlank(string[weightIndex])) {
							d.setWeight(null);
						} else {
							d.setWeight(Integer.parseInt(string[weightIndex]));
						}
					}
					if (incidenceFlag) {
						if (StringUtils.isBlank(string[incidenceIndex])) {
							d.setIncidence(null);
						} else {
							d.setIncidence(string[incidenceIndex]);
						}
					}
					addDomain(d);
				} else {
					// 未知的域名信息
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
							d.setType("");
						} else {
							d.setType(string[typeIndex]);
						}
					}
					if (rankFlag) {
						if (StringUtils.isBlank(string[rankIndex])) {
							d.setRank("无");
						} else {
							d.setRank(string[rankIndex]);
						}
					}else{
						d.setRank("无");
					}
					if (weightFlag) {
						if (StringUtils.isBlank(string[weightIndex]) || string[weightIndex].equals("0")) {
							// 从权重表中根据类型的权重赋予初始值
							if (!StringUtils.isBlank(d.getType())) {
								List<Weight> weights = weightDao.selectWeightByName(d.getType());
								if (weights.size() > 0) {
									d.setWeight(weights.get(0).getWeight());
								} else {
									d.setWeight(0);
								}
							} else {
								d.setWeight(0);
							}
						} else {
							d.setWeight(Integer.parseInt(string[weightIndex]));
						}
					}else if(!StringUtils.isBlank(d.getType())) {
						List<Weight> weights = weightDao.selectWeightByName(d.getType());
						if (weights.size() > 0) {
							d.setWeight(weights.get(0).getWeight());
						} else {
							d.setWeight(0);
						}
					}
					if (incidenceFlag) {
						if (StringUtils.isBlank(string[incidenceIndex])) {
							d.setIncidence("");
						} else {
							d.setIncidence(string[incidenceIndex]);
						}
					}
					list.add(d);
				}

				/*
				 * if (Constant.existDomain.containsKey(UrlUtil.getUrl(string[
				 * urlIndex]))) { // 存在的域名信息若信息不完整（网站名为其他，或者其他属性为空）则更新该域名信息
				 * Domain old =
				 * Constant.existDomain.get(UrlUtil.getUrl(string[urlIndex]));//
				 * 存放在域名信息库中的域名信息 Domain d = new Domain();// 存放需要更新的域名信息
				 * d.setUrl(string[urlIndex]); boolean upflag = false;//
				 * 判断是否需要更新 System.out.print(old.getUrl());
				 * System.out.println("---------"+old.getName()); if
				 * (old.getName().equals("其他")) { if (nameFlag &&
				 * !StringUtils.isBlank(string[nameIndex]) &&
				 * !string[nameIndex].trim().equals("其他")) {
				 * d.setName(string[nameIndex]); upflag = true; } } if
				 * (StringUtils.isBlank(old.getColumn())) { if (columnFlag &&
				 * !StringUtils.isBlank(string[columnIndex])) { if
				 * (string[columnIndex].length() > 32) {
				 * d.setColumn(string[columnIndex].substring(0, 31)); } else {
				 * d.setColumn(string[columnIndex]); } upflag = true; } } if
				 * (StringUtils.isBlank(old.getType()) ||
				 * old.getType().equals("其他")) { if (typeFlag &&
				 * !StringUtils.isBlank(string[typeIndex])) {
				 * d.setType(string[typeIndex]); upflag = true; } } if
				 * (StringUtils.isBlank(old.getRank()) ||
				 * old.getRank().equals("其他")) { if (rankFlag &&
				 * !StringUtils.isBlank(string[rankIndex])) {
				 * d.setRank(string[rankIndex]); upflag = true; } } if (upflag)
				 * { addDomain(d); }
				 */
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
				if (Constant.markedDomain.containsKey(domain.getUrl())
						|| Constant.unmarkedDomain.containsKey(domain.getUrl()))
					list.add(domain);
				else
					unexist.add(domain);
			}

			System.out.println("共" + list.size() + "条已知域名需要更新！");
			for (Domain d : list) {
				addDomain(d);
			}
			// 添加未知url
			System.out.println("共" + unexist.size() + "条未知域名需要添加！");
			if (unexist.size() > 0) {
				addUnknowDomain(unexist);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.info(e.toString());
			return false;
		}

		return true;
	}

	@Override
	public boolean addUnknowDomain(Domain domain) {
		String url = UrlUtil.getUrl(domain.getUrl());
		//判断域名中的类型属性是否存在，若存在且存存在类型表中则插入类型表中
		if(!StringUtils.isBlank(domain.getType()) && !Constant.typeMap.contains(domain.getType())){
			if(typeDao.insertSourceType(domain.getType())> 0){
				Constant.typeMap.add(domain.getType());
			}
		}
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
						// 插入成功则判断其父的isFather是否为真，若为否则更新
						// DomainOne dm = domainOneDao.getDomainOneByUrl(one);
						if (null != domainOne) {
							if(!domainOne.getIsFather()){
								domainOne.setIsFather(true);
							}
							//若父域名的网站名为其他或者为空，则按照子域名的名字填充
							if(StringUtils.isBlank(domainOne.getName())||domainOne.getName().equals("其他")){
								if(!StringUtils.isBlank(dt.getName())&&!dt.getName().equals("其他"))
									domainOne.setName(dt.getName());
							}
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
		//判断域名中的类型属性是否存在，若存在则插入类型表中
		if(!StringUtils.isBlank(domain.getType()) && !Constant.typeMap.contains(domain.getType())){
			if(typeDao.insertSourceType(domain.getType())> 0){
				Constant.typeMap.add(domain.getType());
			}
		}
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
				Constant.unmarkedDomain.remove(url);
				Constant.markedDomain.remove(url);
				// 同时级联删除内存中的二级域名信息
				for (DomainTwo domainTwo : list) {
					Constant.unmarkedDomain.remove(domainTwo.getUrl());
					Constant.markedDomain.remove(domainTwo.getUrl());
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
				Constant.unmarkedDomain.remove(url);
				Constant.markedDomain.remove(url);
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
			name = "其他";
		}
		String column = baseInfo[DomainExcelAttr.COLUMN_INDEX].trim();
		if (StringUtils.isBlank(column))
			column = "";
		String type = baseInfo[DomainExcelAttr.TYPE_INDEX].trim();
		if (StringUtils.isBlank(type))
			type = null;
		String rank = baseInfo[DomainExcelAttr.RANK_INDEX].trim();
		if (StringUtils.isBlank(rank)|| rank.equals("null"))
			rank = "无";
		String incidence = baseInfo[DomainExcelAttr.INCIDENCE_INDEX].trim();
		if (StringUtils.isBlank(incidence))
			incidence = "";
		String weight = baseInfo[DomainExcelAttr.WEIGHT_INDEX].trim();
		//如果weight为0或者为空，则从权重表中学习初始权重
		if (StringUtils.isBlank(weight) || !StringUtils.isNumeric(weight) || weight.equals("0")){
			/*if (!StringUtils.isBlank(type)) {
				List<Weight> weights = weightDao.selectWeightByName(type);
				if (weights.size() > 0) {
					weight = weights.get(0).getWeight()+"";
				} else {
					weight = "0";
				}
			}*/
			weight = "0";
		}
		String maintenance_status = baseInfo[DomainExcelAttr.MAINTENANCE_STATUS_INDEX].trim();
		if (StringUtils.isBlank(maintenance_status) || !StringUtils.isNumeric(maintenance_status))
			maintenance_status = "0";
		domain.setUrl(url);
		domain.setName(name);
		domain.setColumn(column);
		domain.setType(type);
		domain.setRank(rank);
		domain.setIncidence(incidence);
		domain.setWeight(Integer.parseInt(weight));
		// 维护状态为0代表未维护，其他代表已维护
		if (Integer.parseInt(maintenance_status) == 0) {
			domain.setMaintenanceStatus(false);
		} else {
			domain.setMaintenanceStatus(true);
		}
		//判断域名中的类型属性是否存在，若存在且存存在类型表中则插入类型表中
				if(!StringUtils.isBlank(domain.getType()) && !Constant.typeMap.contains(domain.getType())){
					if(typeDao.insertSourceType(domain.getType())> 0){
						Constant.typeMap.add(domain.getType());
					}
				}
		return domain;
	}

	@Override
	public boolean updateDomainOne(DomainOne one) {
		// TODO Auto-generated method stub
		if (domainOneDao.updateDomainOneInfo(one)) {
			if(StringUtils.isBlank(one.getUrl())){
				one = domainOneDao.getDomainOneById(one.getUuid());
			}
			updateExistDomain(one);
			return true;
		}
		return false;
	}

	@Override
	public boolean updateDomainTwo(DomainTwo two) {
		// TODO Auto-generated method stub
		if (domainTwoDao.updateDomainTwo(two)) {
			two = domainTwoDao.getDomainTwoById(two.getUuid());
			DomainOne one = domainOneDao.getDomainOneById(two.getFatherUuid());
			if(null != one){
				one.setUpdateTime(two.getUpdateTime());
				updateDomainOne(one);
			}			
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
		if (domain.getMaintenanceStatus() != null && domain.getMaintenanceStatus() == true) {// 维护状态存在，且为已维护
			if (Constant.markedDomain.containsKey(two.getUrl())) {// 该已维护的域名存在内存中，则更新原内存中域名信息
				Domain old = Constant.markedDomain.get(two.getUrl());
				if (domain.getName() != null) {
					old.setName(domain.getName());
				} else {
					System.out.println(two.getUrl() + "域名名为null，" + old.getName() + "-----------");
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
				Constant.markedDomain.put(two.getUrl(), old);
			} else {// 该域名不存在markedDomain中，则添加到markedDomain中
				// 若该域名原来是未维护状态，则从unmarkedDomian中移除,并将更新后的信息添加到markedDomain中
				if (Constant.unmarkedDomain.containsKey(two.getUrl())) {
					Domain old = Constant.unmarkedDomain.remove(two.getUrl());
					if (domain.getName() != null) {
						old.setName(domain.getName());
					} else {
						System.out.println(two.getUrl() + "域名名为null，" + old.getName() + "-----------");
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
					if (domain.getMaintenanceStatus() != null) {
						old.setMaintenanceStatus(domain.getMaintenanceStatus());
					}					
					Constant.markedDomain.put(two.getUrl(), old);
				}else{
					Domain d = new Domain();
					d.setDomainFormTwo(domainTwoDao.getDomainTwoByUrl(two.getUrl()));
					Constant.markedDomain.put(two.getUrl(), d);
				}
			}
		} else {//该域名不是已维护的域名
			if (Constant.unmarkedDomain.containsKey(two.getUrl())) {// 该未维护的域名存在内存中，则更新原内存中域名信息
				Domain old = Constant.unmarkedDomain.get(two.getUrl());
				if (domain.getName() != null) {
					old.setName(domain.getName());
				} else {
					System.out.println(two.getUrl() + "域名名为null，" + old.getName() + "-----------");
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
				Constant.unmarkedDomain.put(two.getUrl(), old);
			} else {// 该域名不存在unmarkedDomain中，则添加到unmarkedDomain中
				// 若该域名原来是已维护状态，则从markedDomian中移除,并将原信息更新后copy到unmarkedDomain中
				if (Constant.markedDomain.containsKey(two.getUrl())) {
					Domain old = Constant.markedDomain.remove(two.getUrl());
					if (domain.getName() != null) {
						old.setName(domain.getName());
					} else {
						System.out.println(two.getUrl() + "域名名为null，" + old.getName() + "-----------");
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
					if (domain.getMaintenanceStatus() != null) {
						old.setMaintenanceStatus(domain.getMaintenanceStatus());
					}
					Constant.unmarkedDomain.put(two.getUrl(), old);
				}else{
					Domain d = new Domain();
					d.setDomainFormTwo(domainTwoDao.getDomainTwoByUrl(two.getUrl()));
					Constant.unmarkedDomain.put(two.getUrl(), d);
				}
			}
		}
	}

	private void updateExistDomain(DomainOne one) {
		Domain domain = new Domain();
		domain.setDomainFormOne(one);
		if (domain.getMaintenanceStatus() != null && domain.getMaintenanceStatus() == true) {// 维护状态存在，且为已维护
			if (Constant.markedDomain.containsKey(one.getUrl())) {// 该已维护的域名存在内存中，则更新原内存中域名信息
				Domain old = Constant.markedDomain.get(one.getUrl());
				if (domain.getName() != null) {
					old.setName(domain.getName());
				} else {
					System.out.println(one.getUrl() + "域名名为null，" + old.getName() + "-----------");
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
				Constant.markedDomain.put(one.getUrl(), old);
			} else {// 该域名不存在markedDomain中，则添加到markedDomain中
				// 若该域名原来是未维护状态，则从unmarkedDomian中移除
				if (Constant.unmarkedDomain.containsKey(one.getUrl())) {
					Domain old = Constant.unmarkedDomain.remove(one.getUrl());
					if (domain.getName() != null) {
						old.setName(domain.getName());
					} else {
						System.out.println(one.getUrl() + "域名名为null，" + old.getName() + "-----------");
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
					if (domain.getMaintenanceStatus() != null) {
						old.setMaintenanceStatus(domain.getMaintenanceStatus());
					}
					Constant.markedDomain.put(one.getUrl(), old);
				}else{
					Domain d = new Domain();
					d.setDomainFormOne(domainOneDao.getDomainOneByUrl(one.getUrl()));
					Constant.markedDomain.put(one.getUrl(), d);
				}
			}
		} else {//该域名不是已维护的域名
			if (Constant.unmarkedDomain.containsKey(one.getUrl())) {// 该未维护的域名存在内存中，则更新原内存中域名信息
				Domain old = Constant.unmarkedDomain.get(one.getUrl());
				if (domain.getName() != null) {
					old.setName(domain.getName());
				} else {
					System.out.println(one.getUrl() + "域名名为null，" + old.getName() + "-----------");
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
				Constant.unmarkedDomain.put(one.getUrl(), old);
			} else {// 该域名不存在unmarkedDomain中，则添加到unmarkedDomain中
				// 若该域名原来是已维护状态，则从markedDomian中移除
				if (Constant.markedDomain.containsKey(one.getUrl())) {
					Domain old = Constant.markedDomain.remove(one.getUrl());
					if (domain.getName() != null) {
						old.setName(domain.getName());
					} else {
						System.out.println(one.getUrl() + "域名名为null，" + old.getName() + "-----------");
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
					if (domain.getMaintenanceStatus() != null) {
						old.setMaintenanceStatus(domain.getMaintenanceStatus());
					}
					Constant.unmarkedDomain.put(one.getUrl(), old);
				}else{
					//未知域名
					Domain d = new Domain();
					d.setDomainFormOne(domainOneDao.getDomainOneByUrl(one.getUrl()));
					Constant.unmarkedDomain.put(one.getUrl(), d);
				}
			}
		}
	}
}
