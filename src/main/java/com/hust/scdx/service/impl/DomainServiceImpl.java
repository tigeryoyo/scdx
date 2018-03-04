package com.hust.scdx.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
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
import com.hust.scdx.dao.DomainStoreDao;
import com.hust.scdx.dao.DomainTwoDao;
import com.hust.scdx.dao.RankWeightDao;
import com.hust.scdx.dao.SourceTypeDao;
import com.hust.scdx.dao.WeightDao;
import com.hust.scdx.model.Domain;
import com.hust.scdx.model.DomainOne;
import com.hust.scdx.model.DomainStore;
import com.hust.scdx.model.DomainTwo;
import com.hust.scdx.model.RankWeight;
import com.hust.scdx.model.Weight;
import com.hust.scdx.model.params.DomainOneQueryCondition;
import com.hust.scdx.model.params.DomainTwoQueryCondition;
import com.hust.scdx.service.DomainService;
import com.hust.scdx.util.AttrUtil;
import com.hust.scdx.util.DomainCacheManager;
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
	private DomainStoreDao domainStoreDao;

	@Autowired
	private WeightDao weightDao;

	@Autowired
	private RankWeightDao rankWeightDao;
	
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

	/**
	 * 处理原始数据，提取原始数据中存在域名信息库但未被标记为已维护的域名信息，并将其作为预备资源存入数据库
	 * 
	 * @param file
	 * @return
	 */
	@Override
	public boolean addUnMaintainedFromOrigFile(MultipartFile file) {
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
			AttrUtil attrUtil = AttrUtil.getSingleton();
			boolean nameFlag = true, columnFlag = true, typeFlag = true, rankFlag = true, weightFlag = true, incidenceFlag = true;
			int urlIndex = attrUtil.findIndexOf(attr, attrUtil.getUrl_alias());
			// 获取其他属性列的下标
			int nameIndex = attrUtil.findIndexOf(attr, attrUtil.getWebname_alias());// 网站名
			if (nameIndex == -1) {
				nameFlag = false;
			}
			int columnIndex = attrUtil.findIndexOf(attr, attrUtil.getColumn_alias());// 栏目
			if (columnIndex == -1) {
				columnFlag = false;
			}
			int typeIndex = attrUtil.findIndexOf(attr, attrUtil.getType_alias());// 类型
			if (typeIndex == -1) {
				typeFlag = false;
			}
			int rankIndex = attrUtil.findIndexOf(attr, attrUtil.getRank_alias());// 级别
			if (rankIndex == -1) {
				rankFlag = false;
			}
			int weightIndex = attrUtil.findIndexOf(attr, attrUtil.getWeight_alias());// 权重
			if (weightIndex == -1) {
				weightFlag = false;
			}
			int incidenceIndex = attrUtil.findIndexOf(attr, attrUtil.getIncidence_alias());// 影响类型
			if (incidenceIndex == -1) {
				incidenceFlag = false;
			}
			// 存放存在但未被标记为已维护的域名信息
			List<Domain> unMaintainedList = new ArrayList<Domain>();
			for (String[] string : content) {
				if (string.length <= 0 || null == string) {
					continue;
				}
				String url = UrlUtil.getUrl(string[urlIndex]);
				if (DomainCacheManager.isMaintained(url)) {
					// 已存在域名信息库，且被标记为已维护的
					continue;
				} else if (null != DomainCacheManager.getByUrl(url)) {
					// 存在但未被标记为已维护的域名信息，以原始信息为准统计信息
					Domain d = new Domain();
					d.setUrl(url);
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
						if (StringUtils.isBlank(string[weightIndex]) && !StringUtils.isNumeric(string[weightIndex])) {
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
					unMaintainedList.add(d);
				}
			}

			/**
			 * 处理存在但未被维护的域名信息,添加到数据库中
			 */
			domainStoreDao.insertDomainStore(DomainStore.getDomainStoreFromDomain(unMaintainedList));
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.info(e.toString());
			return false;
		}
	}

	/**
	 * 处理标准数据，将标准数据中未知的域名信息添加到域名信息库
	 * 
	 * @param file
	 * @return
	 */
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
			boolean nameFlag = true, columnFlag = true, typeFlag = true, rankFlag = true, weightFlag = true, incidenceFlag = true;
			AttrUtil attrUtil = AttrUtil.getSingleton();
			int urlIndex = attrUtil.findIndexOf(attr, attrUtil.getUrl_alias());
			// 获取其他属性列的下标
			int nameIndex = attrUtil.findIndexOf(attr, attrUtil.getWebname_alias());// 网站名
			if (nameIndex == -1) {
				nameFlag = false;
			}
			int columnIndex = attrUtil.findIndexOf(attr, attrUtil.getColumn_alias());// 栏目
			if (columnIndex == -1) {
				columnFlag = false;
			}
			int typeIndex = attrUtil.findIndexOf(attr, attrUtil.getType_alias());// 类型
			if (typeIndex == -1) {
				typeFlag = false;
			}
			int rankIndex = attrUtil.findIndexOf(attr, attrUtil.getRank_alias());// 级别
			if (rankIndex == -1) {
				rankFlag = false;
			}
			int weightIndex = attrUtil.findIndexOf(attr, attrUtil.getWeight_alias());// 权重
			if (weightIndex == -1) {
				weightFlag = false;
			}
			int incidenceIndex = attrUtil.findIndexOf(attr, attrUtil.getIncidence_alias());// 影响类型
			if (incidenceIndex == -1) {
				incidenceFlag = false;
			}
			// 存放存在但未被标记为已维护的域名信息
			List<Domain> unknowList = new ArrayList<Domain>();
			for (String[] string : content) {
				if (string.length <= 0 || null == string) {
					continue;
				}
				String url = UrlUtil.getUrl(string[urlIndex]);
				if (DomainCacheManager.isMaintained(url)) {
					// 已存在域名信息库，且被标记为已维护的
					continue;
				} else if (null == DomainCacheManager.getByUrl(url)) {
					// 未知的域名信息,并根据已有信息初始化域名信息
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
						if (!(StringUtils.isBlank(string[weightIndex]) || string[weightIndex].equals("0"))) {
							try{
								d.setWeight(Integer.parseInt(string[weightIndex]));
							}catch(Exception e){
								logger.info(e.getMessage());
							}
						}
					}
					if (incidenceFlag) {
						if (StringUtils.isBlank(string[incidenceIndex])) {
							d.setIncidence("");
						} else {
							d.setIncidence(string[incidenceIndex]);
						}
					}
					unknowList.add(d);
				}
			}
			return addUnknowDomain(unknowList);
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
				if (null != DomainCacheManager.getByUrl(domain.getUrl()))
					list.add(domain);
				else
					unexist.add(domain);
			}

			System.out.println("共" + list.size() + "条已知域名需要更新！");
			for (Domain d : list) {
				updateDomainFromFile(d);
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

	/**
	 * 给据给定域名信息，添加新的域名信息到域名信息库
	 * 
	 * @param domain
	 * @return
	 */
	@Override
	public boolean addUnknowDomain(Domain domain) {
		String url = UrlUtil.getUrl(domain.getUrl());
		// 判断域名中的类型属性是否存在，若存在，且不存在类型表中则插入类型表中
		if (!StringUtils.isBlank(domain.getType()) && !Constant.typeMap.contains(domain.getType())) {
			if (typeDao.insertSourceType(domain.getType()) > 0) {
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
					if (!insertDomainOne(father)) {
						return false;
					} else {
						domainOne = father;
					}
				} else {
					fatherUuid = domainOne.getUuid();
				}
				DomainTwo domainTwo = domainTwoDao.getDomainTwoByUrl(two);
				// 不存在则插入，存在则不做处理
				if (null == domainTwo) {
					DomainTwo dt = domain.getDomainTwoBaseProperty();
					dt.setUuid(UUID.randomUUID().toString());
					dt.setFatherUuid(fatherUuid);
					dt.setUrl(two);
					dt.setUpdateTime(new Date());
					if (insertDomainTwo(dt)) {
						// 插入成功则判断其父的isFather是否为真，若为否则更新
						// DomainOne dm = domainOneDao.getDomainOneByUrl(one);
						if (null != domainOne) {
							if (!domainOne.getIsFather()) {
								domainOne.setIsFather(true);
							}
							// 若父域名的网站名为其他或者为空，则按照子域名的名字填充
							if (StringUtils.isBlank(domainOne.getName()) || domainOne.getName().equals("其他")) {
								if (!StringUtils.isBlank(dt.getName()) && !dt.getName().equals("其他"))
									domainOne.setName(dt.getName());
							}
							domainOne.setUpdateTime(new Date());
							updateDomainOne(domainOne);
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
					if (!insertDomainOne(domainOne))
						return false;
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

	/**
	 * 根据给定域名信息，更新域名信息库
	 * 
	 * @param domain
	 * @return
	 */
	@Override
	public boolean updateDomainFromFile(Domain domain) {
		String url = UrlUtil.getUrl(domain.getUrl());
		// 判断域名中的类型属性是否存在，若存在则插入类型表中
		if (!StringUtils.isBlank(domain.getType()) && !Constant.typeMap.contains(domain.getType())) {
			if (typeDao.insertSourceType(domain.getType()) > 0) {
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
				if (!updateDomainTwo(dt))
					return false;
			} else {
				// 不是二级域名，就一定是一级域名
				DomainOne domainOne = domainOneDao.getDomainOneByUrl(one);
				if (null == domainOne) {
					logger.error(one + "该一级域名不存在，更新失败！");
					return false;
				}
				// 更新一级域名信息
				DomainOne dm = domain.getDomainOneBaseProperty();
				dm.setUuid(domainOne.getUuid());
				dm.setUrl(one);
				dm.setUpdateTime(new Date());
				if (!updateDomainOne(dm))
					return false;
			}
		} else
			return false;
		return true;
	}

	public boolean insertDomainOne(DomainOne domainOne) {
		if (null != domainOne && null != domainOne.getUuid() && null != domainOne.getUrl()) {
			initDomainWeightByTypeAndRank(domainOne);
			if(domainOneDao.insertDomain(domainOne)){
				domainOne = domainOneDao.getDomainOneById(domainOne.getUuid());
				Domain domain = new Domain();
				domain.setDomainFormOne(domainOne);
				DomainCacheManager.addDomain(domain);
				return true;
			}
		}
		return false;
	}

	public boolean insertDomainTwo(DomainTwo domainTwo) {
		if (null != domainTwo && null != domainTwo.getUuid() && null != domainTwo.getUrl()) {
			initDomainWeightByTypeAndRank(domainTwo);
			if(domainTwoDao.insertDomainTwo(domainTwo)){
				domainTwo = domainTwoDao.getDomainTwoById(domainTwo.getUuid());
				Domain domain = new Domain();
				domain.setDomainFormTwo(domainTwo);
				DomainCacheManager.addDomain(domain);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean deleteDomainOneById(String uuid) {
		// TODO Auto-generated method stub
		String url = domainOneDao.getDomainOneById(uuid).getUrl();
		List<DomainTwo> list = domainTwoDao.getDomainTwoByFatherId(uuid);
		if (domainOneDao.delelteDomainOneById(uuid)) {
			try {
				if (null == DomainCacheManager.deleteByUrl(url))
					logger.error(url + "删除缓存中的域名信息失败！");
				// 同时级联删除内存中的二级域名信息
				for (DomainTwo domainTwo : list) {
					if (null == DomainCacheManager.deleteByUrl(domainTwo.getUrl()))
						logger.error(domainTwo.getUrl() + "删除缓存中的域名信息失败！");
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
	public boolean deleteDomainOneById(List<String> uuids) {
		// TODO Auto-generated method stub
		if(null== uuids||uuids.size()==0)return true;
		List<DomainOne> dos = domainOneDao.getDomainOneById(uuids);
		if (domainOneDao.delelteDomainOneById(uuids)) {
			for (DomainOne domainOne : dos) {
				String url = domainOne.getUrl();
				List<DomainTwo> list = domainTwoDao.getDomainTwoByFatherId(domainOne.getUuid());
				try {
					if (null == DomainCacheManager.deleteByUrl(url))
						logger.error(url + "删除缓存中的域名信息失败！");
					// 同时级联删除内存中的二级域名信息
					for (DomainTwo domainTwo : list) {
						if (null == DomainCacheManager.deleteByUrl(domainTwo.getUrl()))
							logger.error(domainTwo.getUrl() + "删除缓存中的域名信息失败！");
					}
				} catch (Exception e) {
					logger.error("删除内存中的域名信息失败！");
					logger.error(e.getMessage());
				}
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
		DomainTwo dt = domainTwoDao.getDomainTwoById(uuid);
		String url = dt.getUrl();
		String fatherId = dt.getFatherUuid();
		if (domainTwoDao.deleteDomainById(uuid)) {
			if(domainTwoDao.getDomainTwoByFatherId(fatherId).size()==0){
				DomainOne d= new DomainOne();
				d.setUuid(fatherId);
				d.setIsFather(false);
				d.setUpdateTime(new Date());
				updateDomainOne(d);
			}
			try {
				if (null == DomainCacheManager.deleteByUrl(url))
					logger.error(url + "删除缓存中的域名信息失败！");
			} catch (Exception e) {
				logger.error("删除缓存中的域名信息失败！");
				logger.error(e.getMessage());
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean deleteDomainTwoById(List<String> uuids) {
		// TODO Auto-generated method stub
		if(null== uuids||uuids.size()==0)return true;
		List<DomainTwo> dts = domainTwoDao.getDomainTwoById(uuids);
		HashSet<String> hs = new HashSet<String>();
		if (domainTwoDao.deleteDomainById(uuids)) {
			for (DomainTwo dt : dts) {
				String fatherUuid = dt.getFatherUuid();
				String url = dt.getUrl();
				try {
					if (null == DomainCacheManager.deleteByUrl(url))
						logger.error(url + "删除缓存中的域名信息失败！");
				} catch (Exception e) {
					logger.error("删除缓存中的域名信息失败！");
					logger.error(e.getMessage());
				}
				if(!hs.contains(fatherUuid)){
					hs.add(fatherUuid);
					if(domainTwoDao.getDomainTwoByFatherId(fatherUuid).size()==0){
						DomainOne d= new DomainOne();
						d.setUuid(fatherUuid);
						d.setIsFather(false);
						d.setUpdateTime(new Date());
						updateDomainOne(d);
					}
				}
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
		if (StringUtils.isBlank(rank) || rank.equals("null"))
			rank = "无";
		String incidence = baseInfo[DomainExcelAttr.INCIDENCE_INDEX].trim();
		if (StringUtils.isBlank(incidence))
			incidence = "";
		String weight = baseInfo[DomainExcelAttr.WEIGHT_INDEX].trim();
		// 如果weight为0或者为空，则从权重表中学习初始权重
		if (StringUtils.isBlank(weight) || !StringUtils.isNumeric(weight) || weight.equals("0")) {
			/*
			 * if (!StringUtils.isBlank(type)) { List<Weight> weights =
			 * weightDao.selectWeightByName(type); if (weights.size() > 0) {
			 * weight = weights.get(0).getWeight()+""; } else { weight = "0"; }
			 * }
			 */
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
		/*
		 * //判断域名中的类型属性是否存在，若存在且存存在类型表中则插入类型表中
		 * if(!StringUtils.isBlank(domain.getType()) &&
		 * !Constant.typeMap.contains(domain.getType())){
		 * if(typeDao.insertSourceType(domain.getType())> 0){
		 * Constant.typeMap.add(domain.getType()); } }
		 */
		return domain;
	}

	@Override
	public boolean updateDomainOne(DomainOne one) {
		// TODO Auto-generated method stub
		if (domainOneDao.updateDomainOneInfo(one)) {
			one = domainOneDao.getDomainOneById(one.getUuid());
			Domain domain = new Domain();
			domain.setDomainFormOne(one);
			if (!DomainCacheManager.addDomain(domain))
				logger.info(one.getUrl() + "更新到缓存失败！");
			return true;
		}
		return false;
	}
	
	@Override
	public boolean updateDomainOne(DomainOne one,List<String> uuids) {
		// TODO Auto-generated method stub
		if(null== uuids||uuids.size()==0)return true;
		List<DomainOne> dos = domainOneDao.getDomainOneById(uuids);
		if (domainOneDao.updateDomainOneInfo(one,uuids)) {
			for (DomainOne domainOne : dos) {
				Domain domain = new Domain();
				domain.setDomainFormOne(domainOne);
				if (!DomainCacheManager.addDomain(domain))
					logger.info(one.getUrl() + "更新到缓存失败！");
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean updateDomainTwo(DomainTwo two) {
		// TODO Auto-generated method stub
		if (domainTwoDao.updateDomainTwo(two)) {
			two = domainTwoDao.getDomainTwoById(two.getUuid());
			Domain domain = new Domain();
			domain.setDomainFormTwo(two);
			if (!DomainCacheManager.addDomain(domain))
				logger.info(two.getUrl() + "更新到缓存失败！");
			DomainOne one = domainOneDao.getDomainOneById(two.getFatherUuid());
			if (null != one) {
				one.setUpdateTime(two.getUpdateTime());
				updateDomainOne(one);
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean updateDomainTwo(DomainTwo two,List<String> uuids) {
		// TODO Auto-generated method stub
		if(null== uuids||uuids.size()==0)return true;
		if (domainTwoDao.updateDomainTwo(two,uuids)) {
			List<DomainTwo> dts = domainTwoDao.getDomainTwoById(uuids);
			for (DomainTwo domainTwo : dts) {
				Domain domain = new Domain();
				domain.setDomainFormTwo(domainTwo);
				if (!DomainCacheManager.addDomain(domain))
					logger.info(domainTwo.getUrl() + "更新到缓存失败！");
				DomainOne one = domainOneDao.getDomainOneById(domainTwo.getFatherUuid());
				if (null != one) {
					one.setUpdateTime(domainTwo.getUpdateTime());
					updateDomainOne(one);
				}
			}
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
	
	private void initDomainWeightByTypeAndRank(DomainOne domain){
		if (null == domain.getWeight()) {
			// 从权重表中根据类型的权重赋予初始值
			int typeWeight = 0;
			int rankWeight = 0;
			if (!StringUtils.isBlank(domain.getType())) {
				List<Weight> weights = weightDao.selectWeightByName(domain.getType());
				if (weights.size() > 0) {
					typeWeight = weights.get(0).getWeight();
				}
			}
			if (!StringUtils.isBlank(domain.getRank())) {
				List<RankWeight> weights = rankWeightDao.selectWeightByName(domain.getRank());
				if (weights.size() > 0) {
					rankWeight = weights.get(0).getWeight();
				}
			}
			if (typeWeight == 0 && rankWeight == 0) {
				domain.setWeight(0);
			} else {
				domain.setWeight(typeWeight > rankWeight ? typeWeight : rankWeight);
			}
		}
	}
	
	private void initDomainWeightByTypeAndRank(DomainTwo domain){
		if (null == domain.getWeight()) {
			// 从权重表中根据类型的权重赋予初始值
			int typeWeight = 0;
			int rankWeight = 0;
			if (!StringUtils.isBlank(domain.getType())) {
				List<Weight> weights = weightDao.selectWeightByName(domain.getType());
				if (weights.size() > 0) {
					typeWeight = weights.get(0).getWeight();
				}
			}
			if (!StringUtils.isBlank(domain.getRank())) {
				List<RankWeight> weights = rankWeightDao.selectWeightByName(domain.getRank());
				if (weights.size() > 0) {
					rankWeight = weights.get(0).getWeight();
				}
			}
			if (typeWeight == 0 && rankWeight == 0) {
				domain.setWeight(0);
			} else {
				domain.setWeight(typeWeight > rankWeight ? typeWeight : rankWeight);
			}
		}
	}
}
