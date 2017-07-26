package com.hust.scdx.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.hust.scdx.model.params.ExtfileCondition;
import com.hust.scdx.util.ResultUtil;
import com.hust.scdx.service.ExtfileService;

@Controller
@RequestMapping("/extfile")
public class ExtfileController {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(ExtfileController.class);

	@Autowired
	private ExtfileService extfileService;

	/**
	 * 上传原始文件，经过去重成为泛数据文件存储在文件系统中。
	 * 
	 * @param file
	 *            原始文件
	 * @param topicId
	 *            话题id
	 * @param sourceType
	 *            文件类型：新闻or微博
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public Object upload(@RequestParam(value = "file", required = true) MultipartFile file,
			@RequestParam(value = "topicId", required = true) String topicId,
			@RequestParam(value = "sourceType", required = false) String sourceType, HttpServletRequest request) {
		if (file.isEmpty()) {
			logger.info(file.getName() + "为空。");
			return ResultUtil.errorWithMsg("文件为空。");
		}
		ExtfileCondition con = new ExtfileCondition();
		con.setFile(file);
		con.setSourceType(sourceType);
		con.setTopicId(topicId);
		if (extfileService.insert(con, request) == 0) {
			logger.info("文件上传失败。");
			return ResultUtil.errorWithMsg("上传失败。");
		}
		return ResultUtil.success("上传成功");
	}
}
