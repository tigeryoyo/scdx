package com.hust.scdx.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hust.scdx.model.Attr;
import com.hust.scdx.service.AttrService;
import com.hust.scdx.util.ResultUtil;

@Controller
@RequestMapping("/attr")
public class AttrController {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(AttrController.class);
	@Autowired
	private AttrService attrService;

	/**
	 * 查询所有属性
	 */
	@ResponseBody
	@RequestMapping(value = "/queryAttrById", method = RequestMethod.POST)
	public Object queryAttrById(@RequestParam(value = "attrId", required = true) int attrId, HttpServletRequest request) {
		Attr attr = attrService.queryAttrById(attrId);
		if (attr == null) {
			logger.error("查询属性失败。");
			return ResultUtil.errorWithMsg("查询属性失败。");
		}
		return ResultUtil.success(attr);
	}

	/**
	 * 查询所有属性
	 */
	@ResponseBody
	@RequestMapping(value = "/queryAllAttr", method = RequestMethod.POST)
	public Object queryAllAttr(HttpServletRequest request) {
		List<Attr> attrs = attrService.queryAllAttr();
		if (attrs == null || attrs.isEmpty()) {
			logger.error("查询所有属性失败。");
			return ResultUtil.errorWithMsg("查询所有属性失败。");
		}
		return ResultUtil.success(attrs);
	}

	/**
	 * 插入新属性
	 */
	@ResponseBody
	@RequestMapping(value = "/insertAttr", method = RequestMethod.POST)
	public Object insertAttr(@RequestParam(value = "attrMainname", required = true) String attrMainname,
			@RequestParam(value = "attrAlias", required = true) String attrAlias, HttpServletRequest request) {
		Attr attr = new Attr();
		if (attrService.insertAttr(attrMainname, attrAlias) < 0) {
			logger.error("插入属性失败。");
			return ResultUtil.errorWithMsg("插入属性失败。");
		}
		return ResultUtil.successWithoutMsg();
	}

	/**
	 * 删除属性
	 */
	@ResponseBody
	@RequestMapping(value = "/deleteAttr", method = RequestMethod.POST)
	public Object deleteAttr(@RequestParam(value = "attrId", required = true) int attrId, HttpServletRequest request) {
		if (attrService.deleteAttr(attrId) < 0) {
			logger.error("删除属性失败。");
			return ResultUtil.errorWithMsg("删除属性失败。");
		}
		return ResultUtil.successWithoutMsg();
	}

	/**
	 * 更新属性
	 */
	@ResponseBody
	@RequestMapping(value = "/updateAttr", method = RequestMethod.POST)
	public Object updateAttr(@RequestParam(value = "attrId", required = true) int attrId,
			@RequestParam(value = "attrMainname", required = true) String attrMainname,
			@RequestParam(value = "attrAlias", required = true) String attrAlias, HttpServletRequest request) {
		Attr attr = new Attr();
		attr.setAttrId(attrId);
		attr.setAttrMainname(attrMainname);
		attr.setAttrAlias(attrAlias);
		if (attrService.updateAttr(attr) < 0) {
			logger.error("更新属性失败。");
			return ResultUtil.errorWithMsg("更新属性失败。");
		}
		return ResultUtil.successWithoutMsg();
	}
}