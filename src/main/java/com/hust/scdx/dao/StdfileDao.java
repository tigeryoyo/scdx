package com.hust.scdx.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hust.scdx.constant.Config.DIRECTORY;
import com.hust.scdx.constant.Constant.Interval;
import com.hust.scdx.dao.mapper.StdfileMapper;
import com.hust.scdx.model.Stdfile;
import com.hust.scdx.model.StdfileExample;
import com.hust.scdx.model.StdfileExample.Criteria;
import com.hust.scdx.model.params.StdfileQueryCondition;
import com.hust.scdx.util.AttrUtil;
import com.hust.scdx.util.CommonUtil;
import com.hust.scdx.util.ConvertUtil;
import com.hust.scdx.util.FileUtil;
import com.hust.scdx.util.TimeUtil;

@Repository
public class StdfileDao {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(StdfileDao.class);

	@Autowired
	private StdfileMapper stdfileMapper;

	// /**
	// * 插入标准数据文件至文件系统、插入记录至mysql数据库。文件存放在文件系统中的位置是以上传文件的时间为基准
	// *
	// * @param stdfile
	// * 标准数据文件对象
	// * @param list
	// * 文件内容每一个String[]数组为一行
	// * @return
	// */
	// public int insert(Stdfile stdfile, List<String[]> list) {
	// // 存储规则 DIRECTORY.stdfile/xxxx年/xx月/stdfileId
	// Date uploadTime = stdfile.getUploadTime();
	// String dir = DIRECTORY.STDFILE +
	// ConvertUtil.convertDateToPath(uploadTime);
	// if (!new File(dir).exists()) {
	// new File(dir).mkdirs();
	// }
	//
	// String filename = dir + stdfile.getStdfileId();
	// try {
	// // 将list按照特定格式写入txt中.
	// if (FileUtil.write(filename, list)) {
	// return stdfileMapper.insert(stdfile);
	// }
	// } catch (Exception e) {
	// logger.error("写标准数据文件失败:{}", e.toString());
	// }
	// return -1;
	// }

	/**
	 * 插入标准数据文件至文件系统、插入记录至mysql数据库。文件存放在文件系统中的位置是以文件内每条数据的时间为基准
	 * 
	 * @param stdfile
	 *            标准数据文件对象
	 * @param list
	 *            文件内容每一个String[]数组为一行
	 * @return
	 */
	public int insert(Stdfile stdfile, List<String[]> list) {
		// 存储规则 DIRECTORY.stdfile/xxxx年/xx月/xx日/stdfileId
		String[] attrs = list.get(0);
		int size = list.size();
		AttrUtil attrUtil = AttrUtil.getSingleton();
		int indexOfTime = attrUtil.findIndexOf(attrs, attrUtil.getTime_alias());
		TreeMap<String, List<String[]>> map = new TreeMap<String, List<String[]>>();
		for (int i = 1; i < size; i++) {
			String[] line = list.get(i);
			if (CommonUtil.isEmptyArray(line)) {
				continue;
			}
			String time = TimeUtil.getTimeKey(line[indexOfTime], Interval.DAY);
			List<String[]> r = map.get(time);
			if (r != null) {
				r.add(line);
			} else {
				r = new ArrayList<String[]>();
				r.add(line);
			}
			map.put(time, r);
		}
		String datatime = "";
		for (Map.Entry<String, List<String[]>> entry : map.entrySet()) {
			String timeKey = entry.getKey();
			datatime += timeKey + ";";
			String dir = DIRECTORY.STDFILE + ConvertUtil.convertDateToStdPath(timeKey);
			if (!new File(dir).exists()) {
				new File(dir).mkdirs();
			}
			String filename = dir + stdfile.getStdfileId();
			try {
				List<String[]> cl = entry.getValue();
				cl.add(0, attrs);
				// 将list按照特定格式写入txt中.
				FileUtil.write(filename, cl);
			} catch (Exception e) {
				logger.error("写标准数据文件失败:{}", e.toString());
				return -1;
			}
		}
		stdfile.setDatatime(datatime);
		return stdfileMapper.insert(stdfile);
	}

	/**
	 * 将聚类后的数据作为标准文件插入至文件系统中，目录为 stdfile下的一级子目录
	 * 
	 * @param stdfile
	 * @param res
	 */
	public void insertTop(Stdfile stdfile, List<String[]> res, String userName) {
		FileUtil.write(DIRECTORY.STDFILE + userName + "/" + stdfile.getStdfileId(), res);
		stdfileMapper.deleteByPrimaryKey(stdfile.getStdfileId());
		stdfileMapper.insert(stdfile);
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

	public Stdfile queryLastedStdfile(String topicId) {
		StdfileExample example = new StdfileExample();
		Criteria criteria = example.createCriteria();
		criteria.andTopicIdEqualTo(topicId);
		example.setOrderByClause("upload_time desc");
		example.setLimit(1);
		List<Stdfile> files = stdfileMapper.selectByExample(example);
		if (files.size() != 1) {
			return null;
		}
		return files.get(0);
	}

	/**
	 * 根据专题id删除该专题下的所有标准数据:数据库与文件系统内的数据。
	 * 
	 * @param topicId
	 * @return
	 */
	public int deleteStdfileByTopicId(String topicId) {
		int del = -1;
		StdfileQueryCondition con = new StdfileQueryCondition();
		con.setTopicId(topicId);
		List<Stdfile> list = queryStdfilesByCondtion(con);
		if (list == null || list.size() == 0) {
			del = 0;
		}
		for (Stdfile stdfile : list) {
			del = deleteStdfileById(stdfile.getStdfileId());
		}
		return del;
	}

	/**
	 * 根据标准数据id删除数据库记录与文件。
	 * 
	 * @param stdfileId
	 * @return
	 */
	public int deleteStdfileById(String stdfileId) {
		int del = -1;
		if (stdfileId.equals("stdfile_cluster_result")) {
			return 0;
		}
		Stdfile stdfile = queryStdfileById(stdfileId);
		String datatime = stdfile.getDatatime();
		String[] datatimes = datatime.split(";");
		for (String d : datatimes) {
			if (FileUtil.delete(DIRECTORY.STDFILE + d.replaceAll("-", "/") + "/" + stdfileId)) {
				del = stdfileMapper.deleteByPrimaryKey(stdfileId);
			}
		}
		return del;
	}

}
