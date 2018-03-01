package com.hust.scdx.util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
public class HanlpTest {
	public static String getAddr(List<Term> termList) {
		HashSet<Nature> notEnding = new HashSet<Nature>();
		notEnding.add(Nature.ag);
		notEnding.add(Nature.q);
		notEnding.add(Nature.b);
		notEnding.add(Nature.s);
		notEnding.add(Nature.m);
		notEnding.add(Nature.n);
		notEnding.add(Nature.nis);
		notEnding.add(Nature.ng);
		notEnding.add(Nature.ni);
		notEnding.add(Nature.nic);
		notEnding.add(Nature.nis);
		notEnding.add(Nature.nit);
		notEnding.add(Nature.ntc);
		notEnding.add(Nature.ntcb);
		notEnding.add(Nature.ntcf);
		notEnding.add(Nature.ntch);
		notEnding.add(Nature.nth);
		notEnding.add(Nature.nto);
		notEnding.add(Nature.nts);
		notEnding.add(Nature.ntu);
		notEnding.add(Nature.ns);
		notEnding.add(Nature.nz);
		notEnding.add(Nature.nr);
		String sb = getAddr(termList, notEnding, Nature.ns);
		if (sb.isEmpty()) {
			sb = getAddr(termList, notEnding, Nature.nz);
		}
		if (sb.isEmpty()) {
			sb = getAddr(termList, notEnding, Nature.nr);
		}
		if (sb.isEmpty()) {
			sb = getAddr(termList, notEnding, Nature.s);
		}
		return sb.isEmpty() ? "未识别地名" : sb;
	}

	public static String getAddr(List<Term> termList, HashSet<Nature> notEnding, Nature sflag) {
		StringBuilder sb = new StringBuilder();
		int size = termList.size();
		boolean flag = false;
		for (int i = 0; i < size; i++) {
			Term term = termList.get(i);
			if (!flag) {
				if (term.nature == sflag) {
					sb.append(term.word);
					flag = true;
				}
			} else {
				if (notEnding.contains(term.nature)) {
					sb.append(term.word);
				} else {
					break;
				}
			}
		}
		return sb.toString();
	}

	public static void main(String[] args) throws Exception {
		List<String[]> list = new ArrayList<String[]>();

		Segment segment = HanLP.newSegment().enablePlaceRecognize(true);
		String filename = "C:/Users/Chan/Desktop/area.xlsx";
		InputStream inputStream = new FileInputStream(new File(filename));
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
		for (int i = 1; i <= rowNum; i++) {
			Row row = sheet.getRow(i);
			List<Term> termList = segment.seg(row.getCell(2).toString());
			String tmp = getAddr(termList);
			String are = "";
//			Thread.sleep(1000);
//			if (i % 10 == 0) {
//				Thread.sleep(5000);
//			}
			if (tmp.equals("未识别地名")) {
				are = "未识别";
			} else {
				are = SendGET(tmp, "成都");
			}
			System.out.println(i + 1 + ":" + tmp);

			String[] strs = new String[6];
			strs[0] = row.getCell(0).toString();
			strs[1] = are;
			strs[2] = tmp;
			strs[3] = row.getCell(1).toString();
			strs[4] = row.getCell(2).toString();
			if (strs[0].equals(strs[1])) {
				strs[5] = "same";
			} else {
				strs[5] = "";
			}
			list.add(strs);
		}
		inputStream.close();
		XSSFWorkbook hssf = exportToExcel(list);
		OutputStream out = new FileOutputStream(new File("C:/Users/Chan/Desktop/new_area2.xlsx"));
		hssf.write(out);
		out.close();
		// String str =
		// "南湖逸家楼盘2号门每天臭气熏天，垃圾房建在小区内，每天早上都在小区里和小区门口的空地收拾垃圾，严重影响我的生活健康，物业不做为，每天都是默认这样的行为！我该找谁？？";
		// List<Term> termList = segment.seg(str);
		// System.out.println(termList);
		// String addrr = getAddr(termList);
		// System.out.println("从内容提取出来的地址："+addrr);
		// System.out.println(SendGET(addrr,"成都"));
	}

	public static XSSFWorkbook exportToExcel(@SuppressWarnings("unchecked") List<String[]>... lists) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		// 生成单元格样式
		XSSFCellStyle markedrowStyle = workbook.createCellStyle();
		// 新建font实体
		XSSFFont hssfFont = workbook.createFont();
		// 设置字体颜色 ----红色标红
		hssfFont.setColor(HSSFColor.RED.index);
		markedrowStyle.setFont(hssfFont);
		for (int k = 0; k < lists.length; k++) {
			List<String[]> list = lists[k];
			Sheet sheet = workbook.createSheet("sheet" + (k + 1));
			for (int i = 0; i < list.size(); i++) {
				String[] rowList = list.get(i);
				Row row = sheet.createRow(i);
				for (int j = 0; j < rowList.length - 1; j++) {
					Cell cell = row.createCell(j);
					cell.setCellValue(rowList[j]);
					// if (j == 1 && !rowList[4].equals("same")) {
					// cell.setCellStyle(markedrowStyle);
					// }
				}
			}
		}
		return workbook;
	}

	public static String SendGET(String query, String region) {
		String url = "http://api.map.baidu.com/place/v2/search?";
		String ak = "&output=json&ak=cM0Wtkumyeyx71SBrxKQU9Grq3nLFO9k";
		//String ak = "&output=json&ak=hI8GnhbPC9G58DEqTmLmF82oBqoRsQkW";
		String result = "";// 访问返回结果
		BufferedReader read = null;// 读取访问结果
		query = "query=" + query;
		region = "&region=" + region;
		try {
			// 创建url
			URL realurl = new URL(url + query + region + ak);
			// 打开连接
			URLConnection connection = realurl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 建立连接
			connection.connect();
			// 获取所有响应头字段
			Map<String, List<String>> map = connection.getHeaderFields();
			// 遍历所有的响应头字段，获取到cookies等
			// for (String key : map.keySet()) {
			// System.out.println(key + "--->" + map.get(key));
			// }
			// 定义 BufferedReader输入流来读取URL的响应
			read = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
			String line;// 循环读取
			while ((line = read.readLine()) != null) {
				result += line;
			}
		} catch (IOException e) {
			return "未识别";
		} finally {
			if (read != null) {// 关闭流
				try {
					read.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		JSONObject j1 = JSONObject.fromObject(result);
		if (j1.getString("status").equals("0")) {
			System.out.println(j1.toString());
			JSONArray array = j1.getJSONArray("results");
			if (!array.isEmpty()) {
				JSONObject j2 = (JSONObject) array.get(0);
				String res = "";
				try {
					res = j2.getString("area");
				} catch (Exception e) {
					try {
						res = j2.getString("address");
					} catch (Exception e1) {
						try {
							res = j2.getString("name");
						} catch (Exception e2) {
							res = query;
						}
					}
				}
				return res;
			} else {
				return "成都";
			}
		} else {
			System.out.println("查询错误。。。");
			System.out.println(j1.toString());
			return "成都";
		}
	}
}
