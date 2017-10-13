package com.hust.scdx.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtil {

	public static void main(String[] args) {
		try {
			String[] a = new String[0];
			String[] b = new String[] { "1", "2" };
			List<String[]> list = new ArrayList<String[]>();
			list.add(a);
			list.add(b);
			System.out.println(list.size());
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).length == 0) {
					System.out.println("yes");
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 读取原始excel文件的第一个sheet,且有去重功能。
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static List<String[]> readOrigfile(String filename) throws IOException {
		InputStream inputStream = new FileInputStream(new File(filename));
		return readOrigfile(filename, inputStream);
	}

	/**
	 * 读取原始excel文件的第一个sheet,且有去重功能。
	 * 
	 * @param filename
	 *            文件名
	 * @param inputStream
	 *            文件流
	 * @param startRow
	 *            开始行
	 * @return
	 * @throws IOException
	 */
	public static List<String[]> readOrigfile(String filename, InputStream inputStream) throws IOException {
		if (inputStream == null) {
			throw new IllegalArgumentException("inputStream is null.");
		}
		List<String[]> content = new ArrayList<String[]>();
		Workbook workbook = null;
		if (filename.endsWith("xls")) {
			workbook = new HSSFWorkbook(inputStream);
		} else {
			workbook = new XSSFWorkbook(inputStream);
		}
		Sheet sheet = workbook.getSheetAt(0);
		// 行数
		int rowNum = sheet.getLastRowNum();
		// 列数
		int colNum = sheet.getRow(0).getLastCellNum();
		// excel首行为属性行
		String[] attrRow = convert(sheet.getRow(0), colNum);
		// url、time所在列位置
		int indexOfUrl = AttrUtil.findIndexOfUrl(attrRow);
		int indexOfTime = AttrUtil.findIndexOfTime(attrRow);
		// 文件中已存在的url,key未url、Integer为当前url所在行的行数（从0开始）
		HashMap<String, Integer> urlMap = new HashMap<String, Integer>();
		for (int i = 1; i <= rowNum; i++) {
			String[] row = convert(sheet.getRow(i), colNum);
			if (urlMap.containsKey(row[indexOfUrl])) {
				int col = urlMap.get(row[indexOfUrl]);
				String[] oldNews = content.get(col);
				if (oldNews[indexOfTime].compareTo(row[indexOfTime]) > 0) {
					content.set(col, row);
				}
			} else {
				urlMap.put(row[indexOfUrl], content.size());
				content.add(row);
			}
		}
		inputStream.close();
		content.add(0, attrRow);
		return content;
	}

	/**
	 * 读取基础数据文件属性行
	 * 
	 * @param filename
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	public static String[] readOrigfileAttrs(String filename, InputStream inputStream) throws IOException {
		if (inputStream == null) {
			throw new IllegalArgumentException("inputStream is null.");
		}
		Workbook workbook = null;
		if (filename.endsWith("xls")) {
			workbook = new HSSFWorkbook(inputStream);
		} else {
			workbook = new XSSFWorkbook(inputStream);
		}
		Sheet sheet = workbook.getSheetAt(0);
		// 列数
		int colNum = sheet.getRow(0).getLastCellNum();
		// excel首行为属性行
		return convert(sheet.getRow(0), colNum);
	}

	/**
	 * 读取标准数据文件，标准数据文件首行为属性行类簇与类簇之间以空格区分
	 * 
	 * @param StdfileFilename
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	public static List<String[]> readStdfile(String StdfileFilename, InputStream inputStream) throws IOException {
		if (inputStream == null) {
			throw new IllegalArgumentException("inputStream is null.");
		}
		List<String[]> content = new ArrayList<String[]>();
		Workbook workbook = null;
		if (StdfileFilename.endsWith("xls")) {
			workbook = new HSSFWorkbook(inputStream);
		} else {
			workbook = new XSSFWorkbook(inputStream);
		}
		Sheet sheet = workbook.getSheetAt(0);
		// 行数
		int rowNum = sheet.getLastRowNum();
		// 列数
		int colNum = sheet.getRow(0).getLastCellNum();
		// excel首行为属性行
		String[] attrRow = convert(sheet.getRow(0), colNum);
		content.add(attrRow);
		// url所在列位置
		int indexOfUrl = AttrUtil.findIndexOfUrl(attrRow);
		for (int i = 1; i <= rowNum; i++) {
			String[] row = convert(sheet.getRow(i), colNum);
			if (StringUtils.isBlank(row[indexOfUrl])) {
				content.add(new String[0]);
			} else {
				content.add(row);
			}
		}
		inputStream.close();
		return content;
	}

	/**
	 * 将list导出为excel
	 * 
	 * @param lists
	 * @return
	 */
	public static HSSFWorkbook exportToExcel(@SuppressWarnings("unchecked") List<String[]>... lists) {
		HSSFWorkbook workbook = new HSSFWorkbook();
		for (int k = 0; k < lists.length; k++) {
			List<String[]> list = lists[k];
			Sheet sheet = workbook.createSheet("sheet" + (k + 1));
			for (int i = 0; i < list.size(); i++) {
				String[] rowList = list.get(i);
				Row row = sheet.createRow(i);
				for (int j = 0; j < rowList.length; j++) {
					Cell cell = row.createCell(j);
					cell.setCellValue(rowList[j]);
				}
			}
		}
		return workbook;
	}

	/**
	 * 将带标记的信息导出到Excel文件中 带标记的行字体样式为红色
	 * 
	 * @param content
	 *            要导出的内容
	 * @param marked
	 *            待标记的id集合（每个类中的下标）
	 * @return
	 */
	public static HSSFWorkbook exportToExcelMarked(List<String[]> content, List<Integer> marked) {
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 生成单元格样式
		HSSFCellStyle markedrowStyle = workbook.createCellStyle();
		// 新建font实体
		HSSFFont hssfFont = workbook.createFont();
		// 设置字体颜色 ----红色标红
		hssfFont.setColor(HSSFColor.RED.index);
		markedrowStyle.setFont(hssfFont);

		Sheet sheet = workbook.createSheet();
		String[] rowList = content.get(0);
		Row row = sheet.createRow(0);
		for (int j = 0; j < rowList.length; j++) {
			Cell cell = row.createCell(j);
			cell.setCellValue(rowList[j]);
		}

		int index = 0;
		int count = 0;
		int maxCount = marked.size();
		for (int i = 1; i < content.size(); i++) {
			rowList = content.get(i);
			if (CommonUtil.isEmptyArray(rowList)) {
				index = 0;
				count++;
				continue;
			}
			// 从下标1开始，否则第一行会被覆盖
			row = sheet.createRow(i);
			if (maxCount > count && index == marked.get(count)) {
				for (int j = 0; j < rowList.length; j++) {
					Cell cell = row.createCell(j);
					cell.setCellValue(rowList[j]);
					cell.setCellStyle(markedrowStyle);
				}
			} else {
				for (int j = 0; j < rowList.length; j++) {
					Cell cell = row.createCell(j);
					cell.setCellValue(rowList[j]);
				}
			}
			++index;
		}
		return workbook;
	}

	/**
	 * 转换Row为String数组。
	 * 
	 * @return
	 */
	static String[] convert(Row row, int length) {
		String[] res = new String[length];
		for (int i = 0; i < length; i++) {
			try {
				Cell cell = row.getCell(i);
				if (cell.getCellType() == 0) {
					res[i] = TimeUtil.convert(cell);
				} else {
					res[i] = cell.toString();
				}
			} catch (Exception e) {
				res[i] = "";
			}
			res[i] = res[i].replaceAll("\n", "").trim();
			res[i] = res[i].replaceAll("\t", "").trim();
			res[i] = res[i].replaceAll("\r", "").trim();
			res[i] = res[i].replaceAll("\b", "").trim();
			res[i] = res[i].replaceAll("\f", "").trim();
		}
		return res;
	}
	
	/**
	 * 读取excel文件第一个sheet，并会过滤到url为空的数据，同时具有url去重功能
	 * @param filename 文件名
	 * @param inputStream 输入流，不能为null
	 * @return 返回excel文件内容 一行为一个String[]
	 * @throws IOException
	 */
	public static List<String[]> readExcel(String filename, InputStream inputStream) throws IOException {
		if (inputStream == null) {
			throw new IllegalArgumentException("inputStream is null.");
		}
		List<String[]> content = new ArrayList<String[]>();
		Workbook workbook = null;
		if (filename.endsWith("xls")) {
			workbook = new HSSFWorkbook(inputStream);
		} else {
			workbook = new XSSFWorkbook(inputStream);
		}
		Sheet sheet = workbook.getSheetAt(0);
		// 行数
		int rowNum = sheet.getLastRowNum();
		// 列数
		int colNum = sheet.getRow(0).getLastCellNum();
		// excel首行为属性行
		String[] attrRow = convert(sheet.getRow(0), colNum);
		// url所在列位置
		int indexOfUrl = AttrUtil.findIndexOfUrl(attrRow);
		List<String> exitUrls = new ArrayList<String>();
		for (int i = 1; i <= rowNum; i++) {
			String[] row = convert(sheet.getRow(i), colNum);
			//如果url为空则过滤该行数据
			if(StringUtils.isBlank(row[indexOfUrl])){
				continue;
			}
			//url去重
			if (!exitUrls.contains(row[indexOfUrl])) {
				exitUrls.add(row[indexOfUrl]);
				content.add(row);
			}
		}
		inputStream.close();
		content.add(0, attrRow);
		return content;
	}
}
