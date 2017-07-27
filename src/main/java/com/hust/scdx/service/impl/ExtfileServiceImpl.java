package com.hust.scdx.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hust.scdx.dao.ExtfileDao;
import com.hust.scdx.model.Extfile;
import com.hust.scdx.model.User;
import com.hust.scdx.model.params.ExtfileQueryCondition;
import com.hust.scdx.service.ExtfileService;
import com.hust.scdx.service.UserService;
import com.hust.scdx.util.ExcelUtils;

@Service
public class ExtfileServiceImpl implements ExtfileService {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(ExtfileServiceImpl.class);

	@Autowired
	private ExtfileDao extfileDao;
	@Autowired
	private UserService userService;
	
	@Override
	public int insert(ExtfileQueryCondition con, HttpServletRequest request) {
		User user = userService.selectCurrentUser(request);
		MultipartFile file = con.getFile();
		List<String[]> list = null;
		try {
			list = ExcelUtils.readOrigfile(file.getOriginalFilename(), file.getInputStream());
		} catch (IOException e) {
			logger.error("读取原始文件出现异常\t" + e.toString());
			return 0;
		}
		Extfile extfile = new Extfile();
		extfile.setCreator(user.getTrueName());
		extfile.setUploadTime(new Date());
		extfile.setExtfileName(file.getOriginalFilename());
		extfile.setLineNumber(list.size());
		extfile.setSize((int) (file.getSize() / 1024));
		extfile.setTopicId(con.getTopicId());
		extfile.setExtfileId(UUID.randomUUID().toString());
		extfile.setSourceType(con.getSourceType());
		return extfileDao.insert(extfile, list);
	}

}
