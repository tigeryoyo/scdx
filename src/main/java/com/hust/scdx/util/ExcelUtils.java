package com.hust.scdx.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.hust.scdx.constant.Config.DIRECTORY;

public class ExcelUtils {

	public static void main(String[] args) {
		try {
			Date uploadTime = new Date();
			String dir = "C:/Users/Chan/Desktop/" + DateConverter.parseYear(uploadTime) + "/"
					+ DateConverter.parseMonth(uploadTime) + "/";
			if (!new File(dir).exists()) {
				new File(dir).mkdirs();
			} else {
				System.out.println("true");
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
		List<String[]> resList = new ArrayList<String[]>();
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
		resList.add(attrRow);
		// url所在列位置
		int indexOfUrl = AttrUtil.findIndexOfUrl(attrRow);
		// 文件中已存在的url
		HashSet<String> existUrlSet = new HashSet<String>();
		for (int i = 1; i <= rowNum; i++) {
			String[] row = convert(sheet.getRow(i), colNum);
			if (!existUrlSet.contains(row[indexOfUrl])) {
				existUrlSet.add(row[indexOfUrl]);
				resList.add(row);
			}
		}
		inputStream.close();
		return resList;
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
			res[i] = res[i].replaceAll("\n", "");
			res[i] = res[i].replaceAll("\t", "");
			res[i] = res[i].replaceAll("\r", "");
			res[i] = res[i].replaceAll("\b", "");
			res[i] = res[i].replaceAll("\f", "");
		}
		return res;
	}
}
