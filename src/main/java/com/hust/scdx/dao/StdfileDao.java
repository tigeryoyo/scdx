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
import com.hust.scdx.dao.mapper.StdfileMapper;
import com.hust.scdx.model.Extfile;
import com.hust.scdx.model.ExtfileExample;
import com.hust.scdx.model.Stdfile;
import com.hust.scdx.model.StdfileExample;
import com.hust.scdx.model.StdfileExample.Criteria;
import com.hust.scdx.model.params.ExtfileQueryCondition;
import com.hust.scdx.model.params.StdfileQueryCondition;
import com.hust.scdx.util.DateConverter;
import com.hust.scdx.util.FileUtil;

@Repository
public class StdfileDao {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(StdfileDao.class);

	@Autowired
	private StdfileMapper stdfileMapper;

	public int insert(Stdfile stdfile, List<String[]> list) {
		// 存储规则 DIRECTORY.stdfile/xxxx年/xx月/stdfileId
		Date uploadTime = stdfile.getUploadTime();
		String dir = DIRECTORY.STDFILE + DateConverter.convertToPath(uploadTime);
		if (!new File(dir).exists()) {
			new File(dir).mkdirs();
		}

		String filename = dir + stdfile.getStdfileId();
		try {
			// 将list按照特定格式写入txt中.
			if (FileUtil.write(filename, list)) {
				return stdfileMapper.insert(stdfile);
			}
		} catch (Exception e) {
			logger.error("写标准数据文件失败:{}", e.toString());
		}
		return 0;
	}

	/**
	 * 根据标准文件id查找对象
	 * 
	 * @param stdfileId
	 * @return
	 */
	public Stdfile queryStdfileById(String stdfileId) {
		return stdfileMapper.selectByPrimaryKey(stdfileId);
	}
	
	/**
	 * 根据条件（时间范围、专题id）查找符合范围的标准数据对象
	 * 
	 * @param con
	 * @return
	 */
	public List<Stdfile> queryStdfilesByCondtion(StdfileQueryCondition con) {
		StdfileExample example = new StdfileExample();
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
		return stdfileMapper.selectByExample(example);
	}

	/**
	 * 根据标准数据id删除数据库记录与文件。
	 * 
	 * @param stdfileId
	 * @return
	 */
	public int deleteStdfileById(String stdfileId) {
		Stdfile stdfile = queryStdfileById(stdfileId);
		if (FileUtil.delete(DIRECTORY.STDFILE + DateConverter.convertToPath(stdfile.getUploadTime()))) {
			return stdfileMapper.deleteByPrimaryKey(stdfileId);
		}
		return -1;
	}

	/**
	 * 根据专题id删除该专题下的所有标准数据:数据库与文件系统内的数据。
	 * 
	 * @param topicId
	 * @return
	 */
	public int deleteStdfileByTopicId(String topicId) {
		int del = -1;
		StdfileExample example = new StdfileExample();
		Criteria criteria = example.createCriteria();
		if (!StringUtils.isEmpty(topicId)) {
			criteria.andTopicIdEqualTo(topicId);
		} else {
			return del;
		}
		StdfileQueryCondition con = new StdfileQueryCondition();
		con.setTopicId(topicId);
		List<Stdfile> list = queryStdfilesByCondtion(con);
		for (Stdfile stdfile : list) {
			del = deleteStdfileById(stdfile.getStdfileId());
		}
		return del;
	}
}
