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

import com.hust.scdx.model.params.StdfileQueryCondition;
import com.hust.scdx.service.StdfileService;
import com.hust.scdx.util.ResultUtil;

@Controller
@RequestMapping("/stdfile")
/**
 * 标准数据Controller
 * 
 * @author tigerto
 *
 */
public class StdfileController {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(StdfileController.class);

	@Autowired
	private StdfileService stdfileService;

	/**
	 * 上传标准数据文件
	 * 
	 * @param stdfile
	 *            标准数据文件句柄
	 * @param topicId
	 *            专题id
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public Object upload(@RequestParam(value = "stdfile", required = true) MultipartFile stdfile,
			@RequestParam(value = "topicId", required = true) String topicId, HttpServletRequest request) {
		if (stdfile.isEmpty()) {
			logger.info("文件为空。");
			return ResultUtil.errorWithMsg("文件为空。");
		}
		StdfileQueryCondition con = new StdfileQueryCondition();
		con.setFile(stdfile);
		con.setTopicId(topicId);
		if (stdfileService.insert(con, request) == 0) {
			logger.info("文件上传失败。");
			return ResultUtil.errorWithMsg("上传失败。");
		}
		return ResultUtil.success("上传成功。");
	}
}
