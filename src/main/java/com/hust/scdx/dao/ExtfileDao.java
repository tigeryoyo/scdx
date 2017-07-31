package com.hust.scdx.dao;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hust.scdx.constant.Config.DIRECTORY;
import com.hust.scdx.dao.mapper.ExtfileMapper;
import com.hust.scdx.model.Extfile;
import com.hust.scdx.model.ExtfileExample;
import com.hust.scdx.model.ExtfileExample.Criteria;
import com.hust.scdx.model.params.ExtfileQueryCondition;
import com.hust.scdx.util.DateConverter;
import com.hust.scdx.util.FileUtil;

@Repository
public class ExtfileDao {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(ExtfileDao.class);

	@Autowired
	private ExtfileMapper extfileMapper;

	/**
	 * 插入基础数据文件至文件系统、插入记录至mysql数据库。
	 * 
	 * @param extfile
	 *            基础数据文件对象
	 * @param list
	 *            文件内容每一个String[]数组为一行
	 * @return
	 */
	public int insert(Extfile extfile, List<String[]> list) {
		// 存储规则 DIRECTORY.EXTFILE/xxxx年/xx月/extfileId
		Date uploadTime = extfile.getUploadTime();
		String dir = DIRECTORY.EXTFILE + DateConverter.convertToPath(uploadTime);
		if (!new File(dir).exists()) {
			new File(dir).mkdirs();
		}

		String filename = dir + extfile.getExtfileId();
		try {
			// 将list按照特定格式写入txt中.
			if (FileUtil.write(filename, list)) {
				return extfileMapper.insert(extfile);
			}
		} catch (Exception e) {
			logger.error("写基础数据文件失败::{}", e.toString());
		}
		return 0;
	}

	/**
	 * 根据基础文件id查找基础文件
	 * 
	 * @param extfileId
	 * @return
	 */
	public Extfile queryExtfileById(String extfileId) {
		return extfileMapper.selectByPrimaryKey(extfileId);
	}

	/**
	 * 根据条件（时间范围、专题id）查找符合范围的基础数据对象
	 * 
	 * @param con
	 * @return
	 */
	public List<Extfile> queryExtfilesByCondtion(ExtfileQueryCondition con) {
		ExtfileExample example = new ExtfileExample();
		Criteria criteria = example.createCriteria();
		if (!StringUtils.isEmpty(con.getTopicId())) {
			criteria.andTopicIdEqualTo(con.getTopicId());
		}
		if (null != con.getStartTime()) {
			criteria.andUploadTimeGreaterThanOrEqualTo(con.getStartTime());
		}
		if (null != con.getEndTime()) {
			criteria.andUploadTimeLessThanOrEqualTo(con.getEndTime());
		}
		example.setOrderByClause("upload_time desc");
		return extfileMapper.selectByExample(example);
	}

	/**
	 * 根据基础数据id删除数据库记录与文件。
	 * 
	 * @param extfileId
	 * @return
	 */
	public int deleteExtfileById(String extfileId) {
		Extfile extfile = queryExtfileById(extfileId);
		if (FileUtil.delete(DIRECTORY.EXTFILE + DateConverter.convertToPath(extfile.getUploadTime()))) {
			return extfileMapper.deleteByPrimaryKey(extfileId);
		}
		return -1;
	}

	/**
	 * 根据专题id删除该专题下的所有基础数据:数据库与文件系统内的数据。
	 * 
	 * @param topicId
	 * @return
	 */
	public int deleteExtfileByTopicId(String topicId) {
		int del = -1;
		ExtfileExample example = new ExtfileExample();
		Criteria criteria = example.createCriteria();
		if (!StringUtils.isEmpty(topicId)) {
			criteria.andTopicIdEqualTo(topicId);
		} else {
			return del;
		}
		ExtfileQueryCondition con = new ExtfileQueryCondition();
		con.setTopicId(topicId);
		List<Extfile> list = queryExtfilesByCondtion(con);
		for (Extfile extfile : list) {
			del = deleteExtfileById(extfile.getExtfileId());
		}
		return del;
	}

	/**
	 * 根据基础数据文件名集合获取基础数据文件内容，整合、去重。
	 * 
	 * @param extfilePaths
	 * @return
	 */
	public List<String[]> getExtfilesContent(String[] extfilePaths) {
		return FileUtil.readExtfiles(extfilePaths);
	}

}
