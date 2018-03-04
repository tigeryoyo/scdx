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

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

import com.hust.scdx.constant.Constant.Resutt;
import com.hust.scdx.model.Result;
import com.hust.scdx.service.ResultService;
import com.hust.scdx.util.ExcelUtil;
import com.hust.scdx.util.ResultUtil;

@Controller
@RequestMapping("/result")
/**
 * 聚类数据Controller
 * 
 * @author tigerto
 *
 */
public class ResultController {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(ResultController.class);

	@Autowired
	private ResultService resultService;

	/**
	 * 根据resultId查找结果，返回前台显示的list。
	 * 
	 * @param resultId
	 * @param request
	 * @return displayResult（title、url、time、amount）
	 */
	@ResponseBody
	@RequestMapping("/getDisplayResultById")
	public Object getDisplayResultById(@RequestParam(value = "resultId", required = true) String resultId,
			HttpServletRequest request) {
		List<String[]> list = resultService.getDisplayResultById(resultId, request);
		if (list == null) {
			logger.error("查找操作结果失败。");
			return ResultUtil.errorWithMsg("查找操作结果失败。");
		}
		return ResultUtil.success(list);
	}

	/**
	 * 根据时间范围查找操作结果。
	 * 
	 * @param topicId
	 *            专题id
	 * @param startTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @param request
	 * @return 结果记录对象list
	 */
	@ResponseBody
	@RequestMapping("/queryResultByTimeRange")
	public Object queryResultByTimeRange(@RequestParam(value = "topicId", required = true) String topicId,
			@RequestParam(value = "timeRangeType", required = true) String timeRangeType,
			@RequestParam(value = "startTime", required = true) Date startTime,
			@RequestParam(value = "endTime", required = true) Date endTime, HttpServletRequest request) {
		switch (timeRangeType) {
		case "1":
			endTime = new Date();
			startTime = new Date(endTime.getTime()-15*60*1000);
			endTime = null;
			break;
		case "2":
			endTime = new Date();
			startTime = new Date(endTime.getTime()-1*24*60*60*1000);
			endTime = null;
			break;
		case "3":
			endTime = new Date();
			startTime = new Date(endTime.getTime()-7*24*60*60*1000);
			endTime = null;
			break;
		default:
			break;
		}
		List<Result> list = resultService.queryResultByTimeRange(topicId, startTime, endTime);
		if (list == null) {
			logger.error("查找历史操作结果失败。");
			return ResultUtil.errorWithMsg("查找历史操作结果失败。");
		}
		return ResultUtil.success(list);
	}

	/**
	 * 根据索引合并聚类结果中的某些类
	 * 
	 * @param resultId
	 * @param indices
	 *            顺序的索引集合
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/combineResultItemsByIndices")
	public Object combineResultItemsByIndices(@RequestParam(value = "resultId", required = true) String resultId,
			@RequestParam(value = "indices", required = true) int[] indices, HttpServletRequest request) {
		if (indices.length == 0) {
			return ResultUtil.errorWithMsg("未选中任何index。");
		}
		if (resultService.combineResultItemsByIndices(resultId, indices, request) < 0) {
			return ResultUtil.errorWithMsg("合并失败。");
		}
		return ResultUtil.success("合并成功。");
	}

	/**
	 * 根据索引删除聚类结果中的某些类
	 * 
	 * @param resultId
	 * @param indices
	 *            顺序的索引集合
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteResultItemsByIndices")
	public Object deleteResultItemsByIndices(@RequestParam(value = "resultId", required = true) String resultId,
			@RequestParam(value = "indices", required = true) int[] indices, HttpServletRequest request) {
		if (indices.length == 0) {
			return ResultUtil.errorWithMsg("未选中任何index。");
		}
		if (resultService.deleteResultItemsByIndices(resultId, indices, request) < 0) {
			return ResultUtil.errorWithMsg("删除失败。");
		}
		return ResultUtil.success("删除成功。");
	}
	
	/**
	 * 根据关键词查找聚类结果中的某些类
	 * 
	 * @param resultId
	 * @param keyword
	 *            关键词
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/searchResultItemsByKeyword")
	public Object searchResultItemsByKeyword(@RequestParam(value = "resultId", required = true) String resultId,
			@RequestParam(value = "keyword", required = true) String keyword, HttpServletRequest request) {
		if (resultService.searchResultItemsByKeyword(resultId, keyword, request) < 0) {
			return ResultUtil.errorWithMsg("查找失败。");
		}
		return ResultUtil.success("查找成功。");
	}

	/**
	 * 重置结果，撤销对聚类结果的二次操作。
	 * 
	 * @param resultId
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/resetResultById")
	public Object resetResultById(@RequestParam(value = "resultId", required = true) String resultId,
			HttpServletRequest request) {
		List<String[]> list = resultService.resetResultById(resultId, request);
		if (list == null) {
			logger.error("查找操作结果失败。");
			return ResultUtil.errorWithMsg("查找操作结果失败。");
		}
		return ResultUtil.success(list);
	}

	/**
	 * 删除索引为index的类簇内的指定数据集。
	 * 
	 * @param resultId
	 *            结果id
	 * @param index
	 *            类簇索引
	 * @param indices
	 *            类簇内要删除的索引集合
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteClusterItemsByIndices")
	public Object deleteClusterItemsByIndices(@RequestParam(value = "resultId", required = true) String resultId,
			@RequestParam(value = "index", required = true) int index,
			@RequestParam(value = "indices", required = true) int[] indices, HttpServletRequest request) {
		if (indices.length == 0) {
			return ResultUtil.errorWithMsg("未选中任何index。");
		}
		if (resultService.deleteClusterItemsByIndices(resultId, index, indices, request) < 0) {
			return ResultUtil.errorWithMsg("删除失败。");
		}
		return ResultUtil.success("删除成功。");
	}

	/**
	 * 根据resultId下载结果
	 * 
	 * @param resultId
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "/downloadResultById", method = RequestMethod.POST)
	public Object downloadResultById(@RequestParam(value = "resultId", required = true) String resultId,
			HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> map = resultService.getResultContentById(resultId, request);
		if (map == null || map.size() == 0) {
			return ResultUtil.errorWithMsg("下载结果失败。");
		}

		try (OutputStream outputStream = response.getOutputStream();) {
			response.setCharacterEncoding("utf-8");
			response.setContentType("multipart/form-data");

			String resultName = new String(((String) map.get(Resutt.RESULTNAME)).getBytes(), "ISO8859-1");
			response.setHeader("Content-Disposition", "attachment;filename=" + resultName + ".xlsx");
			XSSFWorkbook workbook = ExcelUtil.exportToExcelMarked((List<String[]>) map.get(Resutt.RESULT),
					(List<Integer>) map.get(Resutt.MARKED));
			workbook = ExcelUtil.exportStatToExcel(workbook,
					(Map<String, TreeMap<String, Integer>>) map.get(Resutt.STAT));
			workbook.write(outputStream);
		} catch (Exception e) {
			logger.error("下载结果失败。");
			return ResultUtil.errorWithMsg("下载结果失败。");
		}

		return ResultUtil.success("下载成功。");
	}

	@InitBinder
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		CustomDateEditor editor = new CustomDateEditor(df, false);
		binder.registerCustomEditor(Date.class, editor);
	}
}
