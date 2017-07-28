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

import com.hust.scdx.model.params.ExtfileQueryCondition;
import com.hust.scdx.util.AttrUtil;
import com.hust.scdx.util.ExcelUtil;
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
	 * @param origfile
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
	public Object upload(@RequestParam(value = "origfile", required = true) MultipartFile origfile,
			@RequestParam(value = "topicId", required = true) String topicId,
			@RequestParam(value = "sourceType", required = false) String sourceType, HttpServletRequest request) {
		if (origfile.isEmpty()) {
			logger.info("文件为空。");
			return ResultUtil.errorWithMsg("文件为空。");
		}
		ExtfileQueryCondition con = new ExtfileQueryCondition();
		con.setFile(origfile);
		con.setSourceType(sourceType);
		con.setTopicId(topicId);
		if (extfileService.insert(con, request) == 0) {
			logger.info("文件上传失败。");
			return ResultUtil.errorWithMsg("上传失败。");
		}
		return ResultUtil.success("上传成功");
	}

	/**
	 * 读取上传文件的第一行（属性行），检查是否满足存在 时间、标题、链接
	 * 
	 * @param origfile
	 *            上传的原始文件
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getAttrs")
	public Object getAttrs(@RequestParam(value = "origfile", required = true) MultipartFile origfile) {
		if (origfile.isEmpty()) {
			return ResultUtil.errorWithMsg("文件是空的");
		}
		try {
			String[] attrs = ExcelUtil.readOrigfileAttrs(origfile.getOriginalFilename(), origfile.getInputStream());
			if (AttrUtil.findIndexOfTime(attrs) != -1 && AttrUtil.findIndexOfTitle(attrs) != -1
					&& AttrUtil.findIndexOfUrl(attrs) != -1) {
				return ResultUtil.successWithoutMsg();
			}
		} catch (Exception e) {
			logger.warn("读取属性行失败。" + e.toString());
		}
		return ResultUtil.errorWithMsg("获取文件属性失败");
	}
}
