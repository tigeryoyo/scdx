package com.hust.scdx.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import com.hust.scdx.constant.Constant;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class AreaUtil {
	Segment segment;
	HashSet<Nature> notEnding;
	String url;
	String ak;
	HashSet<String> centerLayerSet;

	AreaUtil() {
		try {
			segment = HanLP.newSegment().enablePlaceRecognize(true);
			url = "http://api.map.baidu.com/place/v2/search?";
			ak = "&output=json&ak=cM0Wtkumyeyx71SBrxKQU9Grq3nLFO9k";
			notEnding = new HashSet<Nature>();
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
			centerLayerSet = new HashSet<String>();
			centerLayerSet.add("青羊");
			centerLayerSet.add("青羊区");
			centerLayerSet.add("锦江");
			centerLayerSet.add("锦江区");
			centerLayerSet.add("金牛");
			centerLayerSet.add("金牛区");
			centerLayerSet.add("武侯");
			centerLayerSet.add("武侯区");
			centerLayerSet.add("成华");
			centerLayerSet.add("成华区");
			centerLayerSet.add("龙泉驿");
			centerLayerSet.add("龙泉驿区");
			centerLayerSet.add("双流");
			centerLayerSet.add("双流县");
			centerLayerSet.add("双流区");
			centerLayerSet.add("温江");
			centerLayerSet.add("温江区");
			centerLayerSet.add("郫都");
			centerLayerSet.add("郫都区");
			centerLayerSet.add("新都");
			centerLayerSet.add("新都区");
			centerLayerSet.add("青白江");
			centerLayerSet.add("青白江区");
		} catch (Exception e) {
			
		}
	}

	private String getAddr(List<Term> termList, HashSet<Nature> notEnding, Nature sflag) {
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

	/**
	 * 从字符串中抽取地名
	 * 
	 * @param str
	 * @return
	 */
	private String getAddr(String str) {
		List<Term> termList = segment.seg(str);
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
		return sb.isEmpty() ? "未识别" : sb;
	}

	/**
	 * 借助百度地图api根据地名查询区域
	 */
	public String getArea(String str, String region) {
		try {
			// String ak = "&output=json&ak=hI8GnhbPC9G58DEqTmLmF82oBqoRsQkW";
			String result = "";// 访问返回结果
			BufferedReader read = null;// 读取访问结果
			String addr = getAddr(str);
			if (addr.equals("未识别")) {
				return region;
			}
			String query = "query=" + addr;
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
//				System.out.println(j1.toString());
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
				// System.out.println("查询错误。。。");
				// System.out.println(j1.toString());
				return "成都";
			}
		} catch (Exception e) {
			return "未识别";
		}
	}

	public String isCenterLayer(String str) {
		if (centerLayerSet.contains(str)) {
			return "中心城区";
		}
		if (str.equals(Constant.REGION) || str.equals(Constant.REGION1)) {
			return "未识别";
		}
		for (String s : centerLayerSet) {
			if (str.contains(s)) {
				return "中心城区";
			}
		}
		return "郊区新城";
	}
	
	public static void main(String[] args) {
		System.out.println(new AreaUtil().getArea("双流县", "成都"));
	}
}
