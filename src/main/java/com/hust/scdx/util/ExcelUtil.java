package com.hust.scdx.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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
			String[] c = new String[] { "3", "4", "5" };
			a = StringUtil.concat(a, b);
			System.out.println(a.length);
			for (String i : a) {
				System.out.print(i);
			}
			System.out.println();
			a = StringUtil.concat(a, c);
			System.out.println(a.length);
			for (String i : a) {
				System.out.print(i);
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
}
