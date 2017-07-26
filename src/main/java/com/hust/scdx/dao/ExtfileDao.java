package com.hust.scdx.dao;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hust.scdx.constant.Config.DIRECTORY;
import com.hust.scdx.dao.mapper.ExtfileMapper;
import com.hust.scdx.model.Extfile;
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

	public int insert(Extfile extfile, List<String[]> list) {
		// 存储规则 DIRECTORY.EXTFILE/xxxx年/xx月/extfileId
		Date uploadTime = extfile.getUploadTime();
		String dir = DIRECTORY.EXTFILE + DateConverter.parseYear(uploadTime) + "/"
				+ DateConverter.parseMonth(uploadTime) + "/";
		if (!new File(dir).exists()) {
			new File(dir).mkdirs();
		}

		String filename = dir + extfile.getExtfileId();
		try {
			// 将excel文件按照特定格式写入txt中.
			if (FileUtil.write(filename, list)) {
				return extfileMapper.insert(extfile);
			}
		} catch (Exception e) {
			logger.error("write file error:{}", e.toString());
		}
		return 0;
	}

}
