package com.hust.scdx.controller;

import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.hust.scdx.constant.Constant.StdfileMap;
import com.hust.scdx.model.Stdfile;
import com.hust.scdx.model.params.StdfileQueryCondition;
import com.hust.scdx.service.DomainService;
import com.hust.scdx.service.StdfileService;
import com.hust.scdx.util.ExcelUtil;
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
	@Autowired
	private DomainService domainService;

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
		// 添加标准文件中未知的url基本属性
		if (domainService.addUnknowUrlFromFile(stdfile))
			return ResultUtil.success("准数据文件上传成功，并且未知url已添加到数据仓库中！");
		else
			return ResultUtil.success("准数据文件上传成功，但未知url添加失败！");
	}

	/**
	 * 根据标准数据id下载标准数据
	 * 
	 * @param StdfileId
	 *            标准数据id
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/downloadStdfileByStdfileId", method = RequestMethod.POST)
	public Object downloadStdfileByStdfileId(@RequestParam(value = "stdfileId", required = true) String stdfileId,
			HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> map = stdfileService.getStdfileById(stdfileId);
		if (map == null || map.size() == 0) {
			return ResultUtil.errorWithMsg("下载结果失败。");
		}

		try (OutputStream outputStream = response.getOutputStream();) {
			response.setCharacterEncoding("utf-8");
			response.setContentType("multipart/form-data");

			String resultName = new String(((String) map.get(StdfileMap.NAME)).getBytes(), "ISO8859-1");
			response.setHeader("Content-Disposition", "attachment;filename=" + resultName + ".xls");
			HSSFWorkbook workbook = ExcelUtil.exportToExcelMarked((List<String[]>) map.get(StdfileMap.CONTENT),
					(List<Integer>) map.get(StdfileMap.MARKED));
			workbook = ExcelUtil.exportStatToExcel(workbook,
					(Map<String, TreeMap<String, Integer>>) map.get(StdfileMap.STAT));
			workbook.write(outputStream);
		} catch (Exception e) {
			logger.error("下载结果失败。");
			return ResultUtil.errorWithMsg("下载结果失败。");
		}

		return ResultUtil.success("下载成功。");
	}

	/**
	 * 根据标准数据id下载标准数据
	 * 
	 * @param StdfileId
	 *            标准数据id
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/downloadAbstractByStdfileId", method = RequestMethod.POST)
	public Object downloadAbstractByStdfileId(@RequestParam(value = "topicId", required = true) String topicId,
			@RequestParam(value = "stdfileId", required = true) String stdfileId, HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> map = stdfileService.getAbstractById(topicId, stdfileId);
		if (map == null || map.size() == 0) {
			return ResultUtil.errorWithMsg("下载结果失败。");
		}

		try (OutputStream outputStream = response.getOutputStream();) {
			response.setCharacterEncoding("utf-8");
			response.setContentType("multipart/form-data");
			String resultName = new String(((String) map.get(StdfileMap.NAME)).getBytes(), "ISO8859-1");
			response.setHeader("Content-Disposition", "attachment;filename=" + resultName + ".doc");
			((XWPFDocument) map.get(StdfileMap.REPORT)).write(outputStream);
		} catch (Exception e) {
			logger.error("下载结果失败。");
			return ResultUtil.errorWithMsg("下载结果失败。");
		}

		return ResultUtil.success("下载成功。");
	}

	/**
	 * 根据时间范围查找标准数据文件。
	 * 
	 * @param topicId
	 *            专题id
	 * @param startTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryStdfilesByTimeRange")
	public Object queryStdfilesByTimeRange(@RequestParam(value = "topicId", required = true) String topicId,
			@RequestParam(value = "startTime", required = true) Date startTime,
			@RequestParam(value = "endTime", required = true) Date endTime, HttpServletRequest request) {
		List<Stdfile> list = stdfileService.queryExtfilesByTimeRange(topicId, startTime, endTime);
		if (list == null) {
			return ResultUtil.unknowError();
		}
		return ResultUtil.success(list);
	}

	/**
	 * 分析标准数据文件
	 * 
	 * @param stdfileId
	 *            标准数据id
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/analyzeByStdfileId", method = RequestMethod.POST)
	public Object analyzeByStdfileId(@RequestParam(value = "stdfileId", required = true) String stdfileId,
			HttpServletRequest request) {
		// title、url、time、amount
		List<String[]> list = stdfileService.analyzeByStdfileId(stdfileId);
		if (list == null || list.isEmpty()) {
			return ResultUtil.errorWithMsg("分析标准数据出错。");
		}
		return ResultUtil.success(list);
	}

	/**
	 * 对准数据一个类统计出图
	 * 
	 * @param params
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/statisticSingleSet")
	public Object statistic(@RequestParam(value = "stdfileId", required = true) String stdfileId,
			@RequestParam(value = "interval", required = true) Integer interval,
			@RequestParam(value = "targetIndex", required = true) String targetIndex, HttpServletRequest request) {
		if (StringUtils.isBlank(stdfileId)) {
			return ResultUtil.errorWithMsg("请重新选择准数据任务");
		}
		Map<String, Object> map = stdfileService.statistic(stdfileId, interval, Integer.parseInt(targetIndex), request);
		if (null == map || map.isEmpty()) {
			return ResultUtil.errorWithMsg("统计失败");
		}
		return ResultUtil.success(map);
	}

	@InitBinder
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		CustomDateEditor editor = new CustomDateEditor(df, false);
		binder.registerCustomEditor(Date.class, editor);
	}
}
