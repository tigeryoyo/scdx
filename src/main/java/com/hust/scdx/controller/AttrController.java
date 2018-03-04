package com.hust.scdx.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hust.scdx.model.Attr;
import com.hust.scdx.model.params.AttrQueryCondition;
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
	 * 查询符合条件的所有属性
	 */
	@ResponseBody
	@RequestMapping(value = "/queryAttr", method = RequestMethod.POST)
	public Object queryAttr(@RequestParam(value = "mainName", required = false) String mainName,
			@RequestParam(value = "alias", required = false) String alias,
			@RequestParam(value = "start", required = false) Integer start,
			@RequestParam(value = "limit", required = false) Integer limit, HttpServletRequest request) {
		AttrQueryCondition condition = new AttrQueryCondition();
		if(null != mainName)
			condition.setMainName(mainName);
		if(null != alias)
			condition.setAlias(alias);
		if(null != start)
			condition.setStart(start);
		if(null != limit)
			condition.setLimit(limit);
		List<Attr> attrs = attrService.queryAttrByCondition(condition);
		if (attrs == null || attrs.isEmpty()) {
			logger.error("未找到符合条件的属性。");
			return ResultUtil.errorWithMsg("未找到符合条件的属性。");
		}
		return ResultUtil.success(attrs);
	}
	
	/**
	 * 查询符合条件的属性的个数
	 */
	@ResponseBody
	@RequestMapping(value = "/queryAttrCount", method = RequestMethod.POST)
	public Object queryAttrCount(@RequestParam(value = "mainName", required = false) String mainName,
			@RequestParam(value = "alias", required = false) String alias,
			@RequestParam(value = "start", required = false) Integer start,
			@RequestParam(value = "limit", required = false) Integer limit, HttpServletRequest request) {
		AttrQueryCondition condition = new AttrQueryCondition();
		if(null != mainName)
			condition.setMainName(mainName);
		if(null != alias)
			condition.setAlias(alias);
		if(null != start)
			condition.setStart(start);
		if(null != limit)
			condition.setLimit(limit);
		Long count = attrService.queryAttrCountByCondition(condition);
		if (count == null || count == 0) {
			logger.error("未找到符合条件的属性。");
			return ResultUtil.errorWithMsg("未找到符合条件的属性。");
		}
		return ResultUtil.success(count);
	}

	/**
	 * 插入新属性
	 */
	@ResponseBody
	@RequestMapping(value = "/insertAttr", method = RequestMethod.POST)
	public Object insertAttr(@RequestParam(value = "mainName", required = true) String attrMainname,
			@RequestParam(value = "alias", required = true) String attrAlias, HttpServletRequest request) {
		if(StringUtils.isBlank(attrAlias)||StringUtils.isBlank(attrMainname))
			return ResultUtil.errorWithMsg("插入属性失败!属性名或属性别名不能为空"); 
		attrMainname = attrMainname.trim();
		attrAlias = attrAlias.trim();
		if(!attrAlias.contains(attrMainname)){
			if(attrAlias.isEmpty()){
				attrAlias = attrMainname;
			}else{
				attrAlias = attrMainname+"|"+attrAlias;
			}
		}
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
		return ResultUtil.success("删除成功！");
	}

	/**
	 * 更新属性
	 */
	@ResponseBody
	@RequestMapping(value = "/updateAttr", method = RequestMethod.POST)
	public Object updateAttr(@RequestParam(value = "id", required = true) Integer attrId,
			@RequestParam(value = "mainName", required = true) String attrMainname,
			@RequestParam(value = "alias", required = true) String attrAlias, HttpServletRequest request) {
		Attr attr = new Attr();
		if(StringUtils.isBlank(attrAlias)||StringUtils.isBlank(attrMainname))
			return ResultUtil.errorWithMsg("更新属性失败!属性名或属性别名不能为空"); 
		attrMainname = attrMainname.trim();
		attrAlias = attrAlias.trim();
		if(!attrAlias.contains(attrMainname)){
			if(attrAlias.isEmpty()){
				attrAlias = attrMainname;
			}else{
				attrAlias = attrMainname+"|"+attrAlias;
			}
		}
		attr.setAttrId(attrId);
		attr.setAttrMainname(attrMainname);
		attr.setAttrAlias(attrAlias);
		if (attrService.updateAttr(attr) < 0) {
			logger.error("更新属性失败。");
			return ResultUtil.errorWithMsg("更新属性失败。");
		}
		return ResultUtil.success("更新成功！");
	}
}