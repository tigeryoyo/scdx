package com.hust.scdx.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.hust.scdx.model.Domain;
import com.hust.scdx.model.DomainOne;
import com.hust.scdx.model.DomainTwo;
import com.hust.scdx.model.params.DomainOneQueryCondition;
import com.hust.scdx.model.params.DomainTwoQueryCondition;
import com.hust.scdx.service.DomainService;
import com.hust.scdx.util.ExcelUtil;
import com.hust.scdx.util.ResultUtil;
import com.hust.scdx.util.UrlUtil;

import net.sf.json.JSONObject;

@RequestMapping("/domain")
public class DomainController {
	private static final Logger logger = LoggerFactory.getLogger(DomainController.class);
	@Autowired
	private DomainService domainService;

	/**
	 * 分页查询所有一级域名以及其对应的二级域名
	 * 
	 * @param start
	 * @param limit
	 * @return List<一级域名> <-----> List<List<二级域名>> 用json封装 （"one":List
	 *         <DomainOne>;"two":List<List<DomainTwo>>）
	 */
	@ResponseBody
	@RequestMapping(value = "/selectDomain", method = RequestMethod.POST)
	public Object selectDomain(@RequestParam(value = "start", required = true) int start,
			@RequestParam(value = "limit", required = true) int limit, HttpServletRequest request) {
		DomainOneQueryCondition condition = new DomainOneQueryCondition();
		condition.setStart(start);
		condition.setLimit(limit);
		List<DomainOne> one = domainService.getDomainOneByCondition(condition);
		if (one == null) {
			return ResultUtil.errorWithMsg("没有域名被找到！");
		}
		List<List<DomainTwo>> two = domainService.getDomainTwoByOne(one);

		JSONObject json = new JSONObject();
		json.put("one", one);
		json.put("two", two);
		return ResultUtil.success(json);
	}

	/**
	 * 分页查询计算总共有多少页
	 * 
	 * @param request
	 * @return 总一级域名数 count
	 */
	@ResponseBody
	@RequestMapping(value = "/selectDomainCount", method = RequestMethod.POST)
	public Object SelectDomainCount(HttpServletRequest request) {
		Long count = domainService.getDomainOneCount();
		if (null == count) {
			return ResultUtil.errorWithMsg("分页失败！");
		} else {
			return ResultUtil.success(count);
		}
	}

	/**
	 * 根据一级域名id查询一级域名以及其对应的二级域名信息
	 * 
	 * @param uuid
	 *            一级域名uuid
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/selectDomainOneById")
	public Object selectDomainOneById(@RequestParam(value = "uuid", required = true) String uuid,
			HttpServletRequest request) {
		DomainOne one = domainService.getDomainOneById(uuid);
		if (null == one) {
			return ResultUtil.errorWithMsg("未找到相关域名信息！");
		}
		DomainTwoQueryCondition condition = new DomainTwoQueryCondition();
		condition.setFatherId(uuid);
		List<DomainTwo> two = domainService.getDomainTwoByCondition(condition);
		JSONObject json = new JSONObject();
		json.put("one", one);
		json.put("two", two);
		return ResultUtil.success(json);
	}

	/**
	 * 添加域名信息，
	 * 
	 * @param url
	 *            url
	 * @param name
	 *            名称
	 * @param column
	 *            栏目
	 * @param type
	 *            类型
	 * @param rank
	 *            等级
	 * @param incidence
	 *            影响范围
	 * @param weight
	 *            权重
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/addDomain")
	public Object addDomain(@RequestParam(value = "url", required = true) String url,
			@RequestParam(value = "name", required = true) String name,
			@RequestParam(value = "column", required = true) String column,
			@RequestParam(value = "type", required = true) String type,
			@RequestParam(value = "rank", required = true) String rank,
			@RequestParam(value = "incidence", required = true) String incidence,
			@RequestParam(value = "weight", required = true) Integer weight, HttpServletRequest request) {
		if (StringUtils.isBlank(url)) {
			url = null;
		}
		if (StringUtils.isBlank(name)) {
			name = null;
		}
		if (StringUtils.isBlank(column)) {
			column = null;
		}
		if (StringUtils.isBlank(type)) {
			type = null;
		}
		if (StringUtils.isBlank(rank)) {
			rank = null;
		}
		if (StringUtils.isBlank(incidence)) {
			incidence = null;
		}
		Domain domain = new Domain();
		domain.setUrl(url);
		domain.setName(name);
		domain.setColumn(column);
		domain.setType(type);
		domain.setRank(rank);
		domain.setIncidence(incidence);
		domain.setWeight(weight);
		if (domainService.addDomain(domain))
			return ResultUtil.success("添加成功！");
		return ResultUtil.errorWithMsg("添加失败！");
	}

	/**
	 * 根据二级域名id查询二级域名以及其对应的一级域名信息
	 * 
	 * @param uuid
	 *            二级域名uuid
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/selectDomainTwoById")
	public Object selectDomainTwoById(@RequestParam(value = "uuid", required = true) String uuid,
			HttpServletRequest request) {
		DomainTwo two = domainService.getDomainTwoById(uuid);
		if (null == two) {
			return ResultUtil.errorWithMsg("未找到相关域名信息！");
		}
		DomainOne one = domainService.getDomainOneById(two.getFatherUuid());
		JSONObject json = new JSONObject();
		json.put("one", one);
		json.put("two", two);
		return ResultUtil.success(json);
	}

	/**
	 * 根据输入条件搜索满足条件的一级域名以及其对应的二级域名
	 * 
	 * @param url
	 * @param name
	 * @param column
	 * @param type
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/searchDomainOne")
	public Object searchDomainOne(@RequestParam(value = "url", required = true) String url,
			@RequestParam(value = "name", required = true) String name,
			@RequestParam(value = "rank", required = true) String rank,
			@RequestParam(value = "weight", required = true) String weight,
			@RequestParam(value = "start", required = true) int start,
			@RequestParam(value = "limit", required = true) int limit, HttpServletRequest request) {
		DomainOneQueryCondition condition = new DomainOneQueryCondition();
		if (StringUtils.isNotBlank(url)) {
			url = UrlUtil.getDomainOne(UrlUtil.getUrl(url));
			condition.setUrl(url);
		}
		if (StringUtils.isNotBlank(name))
			condition.setName(name);
		if (StringUtils.isNotBlank(rank))
			condition.setRank(rank);
		if (StringUtils.isNotBlank(weight))
			condition.setWeight(Integer.parseInt(weight));
		condition.setStart(start);
		condition.setLimit(limit);
		List<DomainOne> list = domainService.getDomainOneByCondition(condition);
		if (null == list || list.size() == 0)
			return ResultUtil.errorWithMsg("无相关域名信息！");
		List<List<DomainTwo>> two = domainService.getDomainTwoByOne(list);
		JSONObject json = new JSONObject();
		json.put("one", list);
		json.put("two", two);
		return ResultUtil.success(json);
	}

	/**
	 * 根据输入条件搜索满足条件的一级域名的个数 用于分页
	 * 
	 * @param url
	 * @param name
	 * @param column
	 * @param type
	 * @param request
	 * @return 满足条件的一级域名数
	 */
	@ResponseBody
	@RequestMapping("/searchDomainOneCount")
	public Object searchDomainOneCount(@RequestParam(value = "url", required = true) String url,
			@RequestParam(value = "name", required = true) String name,
			@RequestParam(value = "rank", required = true) String rank,
			@RequestParam(value = "weight", required = true) String weight, HttpServletRequest request) {
		DomainOneQueryCondition condition = new DomainOneQueryCondition();
		if (StringUtils.isNotBlank(url))
			condition.setUrl(url);
		if (StringUtils.isNotBlank(name))
			condition.setName(name);
		if (StringUtils.isNotBlank(rank))
			condition.setRank(rank);
		if (StringUtils.isNotBlank(weight))
			condition.setWeight(Integer.parseInt(weight));
		condition.setStart(0);
		condition.setLimit(0);
		List<DomainOne> list = domainService.getDomainOneByCondition(condition);
		if (null == list || list.size() == 0)
			return ResultUtil.errorWithMsg("无相关域名信息！");
		return ResultUtil.success(list.size());
	}

	/**
	 * 根据uuid删除一级域名 会有级联删除的效果，删除其对应的二级域名
	 * 
	 * @param uuid
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteDomainOne")
	public Object deleteDomainOne(@RequestParam(value = "uuid", required = true) String uuid,
			HttpServletRequest request) {
		if (domainService.deleteDomainOneById(uuid)) {
			return ResultUtil.success("删除成功！");
		} else {
			return ResultUtil.errorWithMsg("删除失败！");
		}
	}

	/**
	 * 根据uuid删除二级域名
	 * 
	 * @param uuid
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteDomainTwo")
	public Object deleteDomainTwo(@RequestParam(value = "uuid", required = true) String uuid,
			HttpServletRequest request) {
		if (domainService.deleteDomainTwoById(uuid)) {
			return ResultUtil.success("删除成功！");
		} else {
			return ResultUtil.errorWithMsg("删除失败！");
		}
	}

	/**
	 * 根据所给定信息更新一句域名信息
	 * 
	 * @param uuid
	 * @param url
	 * @param name
	 * @param column
	 * @param type
	 * @param rank
	 * @param incidence
	 * @param weight
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateDomainOne")
	public Object updateDomainOne(@RequestParam(value = "uuid", required = true) String uuid,
			@RequestParam(value = "url", required = true) String url,
			@RequestParam(value = "name", required = true) String name,
			@RequestParam(value = "column", required = true) String column,
			@RequestParam(value = "type", required = true) String type,
			@RequestParam(value = "rank", required = true) String rank,
			@RequestParam(value = "incidence", required = true) String incidence,
			@RequestParam(value = "weight", required = true) Integer weight, HttpServletRequest request) {
		DomainOne one = new DomainOne();
		System.out.println(uuid);
		one.setUuid(uuid);
		one.setUrl(url);
		one.setName(name);
		one.setColumn(column);
		one.setType(type);
		one.setRank(rank);
		one.setIncidence(incidence);
		one.setWeight(weight);
		one.setUpdateTime(new Date());
		if (domainService.updateDomainOne(one))
			return ResultUtil.success("修改成功！");
		return ResultUtil.errorWithMsg("修改失败！");
	}

	/**
	 * 根据所给定二级域名信息更新二级域名信息
	 * 
	 * @param uuid
	 * @param url
	 * @param name
	 * @param column
	 * @param type
	 * @param rank
	 * @param incidence
	 * @param weight
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateDomainTwo")
	public Object updateDomainTwo(@RequestParam(value = "uuid", required = true) String uuid,
			@RequestParam(value = "url", required = true) String url,
			@RequestParam(value = "name", required = true) String name,
			@RequestParam(value = "column", required = true) String column,
			@RequestParam(value = "type", required = true) String type,
			@RequestParam(value = "rank", required = true) String rank,
			@RequestParam(value = "incidence", required = true) String incidence,
			@RequestParam(value = "weight", required = true) Integer weight, HttpServletRequest request) {
		DomainTwo two = new DomainTwo();
		two.setUuid(uuid);
		two.setUrl(url);
		two.setName(name);
		two.setColumn(column);
		two.setType(type);
		two.setRank(rank);
		two.setIncidence(incidence);
		two.setWeight(weight);
		two.setUpdateTime(new Date());
		if (domainService.updateDomainTwo(two))
			return ResultUtil.success("修改成功！");
		return ResultUtil.errorWithMsg("修改失败！");
	}

	/**
	 * 上传域名信息excel文件 并根据文件内容更新或插入域名信息
	 * 
	 * @param file
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/uploadDomainExcel")
	public Object uploadDomainExcel(@RequestParam(value = "file", required = true) MultipartFile file) {
		if (file.isEmpty()) {
			return ResultUtil.errorWithMsg("文件为空");
		}
		if (!domainService.addUrlFromFile(file)) {
			return ResultUtil.errorWithMsg("文件格式错误导致添加域名信息失败！");
		}
		return ResultUtil.success("添加信息成功！");
	}

	/**
	 * 导出domain信息
	 * 
	 * @param flag
	 *            0：导出全部domain信息；1：导出未知domain信息
	 */
	@RequestMapping("/exportDomain")
	public void exportDomain(@RequestParam(value = "flag", required = true) Integer flag, HttpServletRequest request,
			HttpServletResponse response) {
		List<String[]> result = new ArrayList<>();
		String fileName = "error.xls";
		String[] attr = { "域名", "网站名", "栏目", "类型", "级别", "影响范围", "权重" };
		result.add(attr);
		switch (flag) {
		case 0:
			fileName = "allDomainInfo.xls";
			DomainOneQueryCondition condition = new DomainOneQueryCondition();
			condition.setStart(0);
			condition.setLimit(0);
			List<DomainOne> oneList = domainService.getDomainOneByCondition(condition);
			List<List<DomainTwo>> twoList = domainService.getDomainTwoByOne(oneList);
			for (int i = 0; i < oneList.size(); i++) {
				DomainOne one = oneList.get(i);
				result.add(getDomainInfo(one));
				for (DomainTwo two : twoList.get(i)) {
					result.add(getDomainInfo(two));
				}
			}
			break;
		case 1:
			fileName = "unknowDomainInfo.xls";
			DomainOneQueryCondition condition2 = new DomainOneQueryCondition();
			condition2.setName("其他");
			condition2.setStart(0);
			condition2.setLimit(0);
			List<DomainOne> oneList2 = domainService.getDomainOneByCondition(condition2);
			for (DomainOne domainOne : oneList2) {
				result.add(getDomainInfo(domainOne));
			}
			DomainTwoQueryCondition condition3 = new DomainTwoQueryCondition();
			condition3.setName("其他");
			List<DomainTwo> twoList2 = domainService.getDomainTwoByCondition(condition3);
			for (DomainTwo domainTwo : twoList2) {
				result.add(getDomainInfo(domainTwo));
			}
			break;

		default:
			logger.info("输入参数有误！");
			break;
		}
		OutputStream outputStream = null;

		try {
			outputStream = response.getOutputStream();
			response.setCharacterEncoding("utf-8");
			response.setContentType("multipart/form-data");
			response.setHeader("Content-Disposition", "attachment;fileName=" + fileName);
			HSSFWorkbook workbook = ExcelUtil.exportToExcel(result);
			workbook.write(outputStream);
		} catch (IOException e) {
			logger.info("excel 导出失败\t" + e.toString());
		} finally {
			try {
				outputStream.close();
			} catch (Exception e) {
				logger.info("导出excel时，关闭outputstream失败");
			}
		}
	}

	/**
	 * 将域名信息转化为字符串数组方便生成excel文件
	 * @param one
	 * @return
	 */
	private String[] getDomainInfo(DomainOne one) {
		String url = one.getUrl();
		String name = one.getName();
		if (null == name || name.equals("其他")) {
			name = "";
		}
		String column = one.getColumn();
		if (null == column || column.equals("其他")) {
			column = "";
		}
		String type = one.getType();
		if (null == type || type.equals("其他")) {
			type = "";
		}
		String rank = one.getRank();
		if (null == rank || rank.equals("其他")) {
			rank = "";
		}
		String incidence = one.getIncidence();
		if (null == incidence || incidence.equals("其他")) {
			incidence = "";
		}
		String weight = one.getWeight().toString();
		String[] str = { url, name, column, type, rank, incidence, weight };
		return str;
	}

	/**
	 * 将域名信息转化为字符串数组方便生成excel文件
	 * @param two
	 * @return
	 */
	private String[] getDomainInfo(DomainTwo two) {
		String url = two.getUrl();
		String name = two.getName();
		if (null == name || name.equals("其他")) {
			name = "";
		}
		String column = two.getColumn();
		if (null == column || column.equals("其他")) {
			column = "";
		}
		String type = two.getType();
		if (null == type || type.equals("其他")) {
			type = "";
		}
		String rank = two.getRank();
		if (null == rank || rank.equals("其他")) {
			rank = "";
		}
		String incidence = two.getIncidence();
		if (null == incidence || incidence.equals("其他")) {
			incidence = "";
		}
		String weight = two.getWeight().toString();
		String[] str = { url, name, column, type, rank, incidence, weight };
		return str;
	}
}
