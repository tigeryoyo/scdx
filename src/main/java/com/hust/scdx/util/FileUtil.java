package com.hust.scdx.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.hust.scdx.constant.Constant.StdfileMap;
import com.hust.scdx.model.Domain;

public class FileUtil {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

	public static void main(String[] args) {
		try {
			// List<String[]> list = readExtfiles("C:/Users/tigerto/Desktop/1",
			// "C:/Users/tigerto/Desktop/2");
			// for (String[] strs : list) {
			// for (String str : strs) {
			// System.out.print(str + "\t");
			// }
			// System.out.println();
			// }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static List<String[]> read(String... filenames) {
		if (CommonUtil.hasEmptyArray(filenames)) {
			return null;
		}
		int taskSize = filenames.length;
		ExecutorService pool = Executors.newFixedThreadPool(taskSize);
		List<String[]> content = new ArrayList<String[]>();
		FileUtil util = new FileUtil();
		for (int i = 0; i < taskSize; i++) {
			Callable<List<String[]>> thread = util.new ReadThread(filenames[i]);
			Future<List<String[]>> f = pool.submit(thread);
			try {
				content.addAll(f.get());
			} catch (InterruptedException | ExecutionException e) {
				logger.error("读取{}失败。", filenames[i]);
			}
		}
		pool.shutdown();
		return content;
	}

	/**
	 * 根据基础数据文件名集合获取泛数据文件内容，整合、去重。
	 * 
	 * @param filenames
	 * @return
	 */
	public static List<String[]> readExtfiles(String... filenames) {
		List<String[]> content = new ArrayList<String[]>();
		if (CommonUtil.hasEmptyArray(filenames)) {
			return null;
		}
		AttrUtil attrUtil = AttrUtil.getSingleton();

		// 全局属性list
		List<String> globalAttrs = new ArrayList<String>();
		// 全局文件中已存在的url,key未url、Integer为当前url所在行的行数（从0开始）
		HashMap<String, Integer> urlMap = new HashMap<String, Integer>();
		for (int i = 0; i < filenames.length; i++) {
			try (BufferedReader br = new BufferedReader(new FileReader(filenames[i]))) {
				String line = br.readLine();
				String[] attrs = line.split("\t");
				// 当前文件url、time所在列
				int indexOfUrl = attrUtil.findIndexOf(attrs, attrUtil.getUrl_alias());
				int indexOfTime = attrUtil.findIndexOf(attrs, attrUtil.getTime_alias());
				// 当前文件的所有属性在全局文件的索引位置
				int[] indexs = i == 0 ? initGlobalAttrs(attrs, globalAttrs)
						: getIndexOfExtfile(attrUtil, attrs, globalAttrs);
				// 全局文件time所在列
				int globalIndexOfTime = attrUtil.findIndexOf(globalAttrs, attrUtil.getTime_alias());
				while (true) {
					line = br.readLine();
					if (!StringUtils.isBlank(line)) {
						String[] row = line.split("\t");
						String[] imp = new String[globalAttrs.size()];
						Arrays.fill(imp, "");
						for (int j = 0; j < row.length; j++) {
							imp[indexs[j]] = row[j];
						}
						// 如果检测到url重复
						if (urlMap.containsKey(row[indexOfUrl])) {
							// 比较两条url所属新闻数据的时间先后
							int col = urlMap.get(row[indexOfUrl]);
							String[] oldNews = content.get(col);
							// 若全局新闻的时间大于当前新闻的时间那么替换该新闻
							if (oldNews[globalIndexOfTime].compareTo(row[indexOfTime]) > 0) {
								content.set(col, imp);
							}
						} else { // 否则将未重复的url加入到map当中
							urlMap.put(row[indexOfUrl], content.size());
							content.add(imp);
						}
					} else {
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("读取基础数据文件集合失败。");
				return null;
			}
		}
		// 对content进行排序
		int indexOfTite = attrUtil.findIndexOf(globalAttrs, attrUtil.getTitle_alias());
		Collections.sort(content, new Comparator<String[]>() {
			public int compare(String[] o1, String[] o2) {
				return (o1[indexOfTite]).compareTo(o2[indexOfTite]);
			}
		});

		// String[] tmp = new String[globalAttrs.size()];
		// globalAttrs.toArray(tmp);
		// content.add(0,tmp);
		// return content;

		// 调整属性顺序消耗比较多的运行时间
		return adjustPropertyLine(attrUtil, globalAttrs, content);
		// return fillContentFromDomain(adjustPropertyLine(attrUtil,
		// globalAttrs, content));
	}

	/**
	 * 对第一个读入的基础文件进行初始化全局属性list操作
	 * 
	 * @param attrs
	 * @param globalAttrs
	 * @return
	 */
	private static int[] initGlobalAttrs(String[] attrs, List<String> globalAttrs) {
		int[] indexs = null;
		indexs = new int[attrs.length];
		for (int i = 0; i < attrs.length; i++) {
			indexs[i] = i;
			globalAttrs.add(attrs[i]);
		}
		return indexs;
	}

	/**
	 * 获取基础数据首行各个属性在公共属性集合中的索引位置
	 * 
	 * @param gloableAttrs
	 *            所有属性集合
	 * @return
	 */
	private static int[] getIndexOfExtfile(AttrUtil attrUtil, String[] attrs, List<String> globalAttrs) {
		int[] indexs = new int[attrs.length];
		List<String> attrs_alias = attrUtil.getAttrs_alias();
		int m = -1, n = -1;
		for (int i = 0; i < attrs.length; i++) {
			m = attrUtil.isIndexOf(attrs[i]);
			// m!=-1,代表该属性在自定义属性中存在
			if (m != -1) {
				n = attrUtil.findIndexOf(globalAttrs, attrs_alias.get(m));
			} else {
				n = globalAttrs.indexOf(attrs[i]);
			}
			// n!=-1,代表该属性在全局属性中存在
			if (n != -1) {
				indexs[i] = n;
			} else {
				indexs[i] = globalAttrs.size();
				globalAttrs.add(attrs[i]);
			}
		}
		return indexs;
	}

	/**
	 * 调整属性行
	 * 
	 * @param globalAttrs
	 * @param content
	 * @return 调整属性行后的content
	 */
	private static List<String[]> adjustPropertyLine(AttrUtil attrUtil, List<String> globalAttrs,
			List<String[]> content) {
		List<String[]> res = new ArrayList<String[]>();
		List<String> attrs_mainName = attrUtil.getAttrs_mainName();
		List<String> nattrs = new ArrayList<String>(attrs_mainName);
		int size = globalAttrs.size();
		int m = -1;
		for (int i = 0; i < size; i++) {
			m = attrUtil.isIndexOf(globalAttrs.get(i));
			if (m != -1) {
				globalAttrs.set(i, attrUtil.getAttrs_mainName().get(m));
			}
		}
		// indices中元素为-1则说明自定义的属性在数据中并没有，需要填充...or
		List<Integer> indices = new ArrayList<Integer>();
		size = attrs_mainName.size();
		for (int i = 0; i < size; i++) {
			indices.add(globalAttrs.indexOf(attrs_mainName.get(i)));
		}

		size = globalAttrs.size();
		for (int i = 0; i < size; i++) {
			String attr = globalAttrs.get(i);
			if (attrs_mainName.indexOf(attr) == -1) {
				indices.add(i);
				nattrs.add(attr);
			}
		}
		// 一行新数据的长度
		int nsize = indices.size();
		// 数据条数（不包括属性行）
		int csize = content.size();
		for (int i = 0; i < csize; i++) {
			String[] cline = content.get(i);
			String[] nline = new String[nsize];
			for (int j = 0; j < nsize; j++) {
				int n = indices.get(j);
				if (n != -1 && n < cline.length) {
					nline[j] = cline[n];
				} else {
					nline[j] = "";
				}
			}
			res.add(nline);
		}
		String[] tmp = new String[nattrs.size()];
		nattrs.toArray(tmp);
		res.add(0, tmp);
		//
		return fillContentFromDomain(res);
	}

	private static List<String[]> fillContentFromDomain(List<String[]> content) {
		List<String[]> res = new ArrayList<String[]>();
		String[] attrs = content.remove(0);
		AttrUtil attrUtil = AttrUtil.getSingleton();
		int urlIndex = attrUtil.findIndexOf(attrs, attrUtil.getUrl_alias());
		if (urlIndex == -1)
			return null;
		int nameIndex = attrUtil.findIndexOf(attrs, attrUtil.getWebname_alias());
		int columnIndex = attrUtil.findIndexOf(attrs, attrUtil.getColumn_alias());
		int typeIndex = attrUtil.findIndexOf(attrs, attrUtil.getType_alias());
		int rankIndex = attrUtil.findIndexOf(attrs, attrUtil.getRank_alias());
		int incidenceIndex = attrUtil.findIndexOf(attrs, attrUtil.getIncidence_alias());
		int weightIndex = attrUtil.findIndexOf(attrs, attrUtil.getWeight_alias());
		for (String[] strs : content) {
			String url = UrlUtil.getUrl(strs[urlIndex]);
			if (url == null) {
				res.add(strs);
				continue;
			}
			String oUrl = UrlUtil.getDomainOne(url);
			String tUrl = UrlUtil.getDomainTwo(url);
			if (tUrl == null) {
				// 如果二级域名不存在，则url为一级域名
				Domain domain = DomainCacheManager.getByUrl(url);
				if (null != domain && domain.getMaintenanceStatus()) {
					// 如果该一级域名被标记为已维护，则覆盖网站名、栏目、类型、级别、影响范围、权重信息
					if (nameIndex != -1)
						strs[nameIndex] = domain.getName();
					if (columnIndex != -1)
						strs[columnIndex] = domain.getColumn();
					if (typeIndex != -1)
						strs[typeIndex] = domain.getType();
					if (rankIndex != -1)
						strs[rankIndex] = domain.getRank();
					if (incidenceIndex != -1)
						strs[incidenceIndex] = domain.getIncidence();
					if (weightIndex != -1)
						strs[weightIndex] = domain.getWeight() + "";
				} else if (null != domain) {
					// 若不是被标记为已维护域名，则判断该域名是否存在域名信息库中，若存在则填充为空的信息，其他信息不做修改，若不存在域名信息库中，则不做处理
					if (nameIndex != -1 && StringUtils.isBlank(strs[nameIndex])) {
						strs[nameIndex] = domain.getName();
					}
					if (columnIndex != -1 && StringUtils.isBlank(strs[columnIndex])
							&& !StringUtils.isBlank(domain.getColumn())) {
						strs[columnIndex] = domain.getColumn();
					}
					if (typeIndex != -1 && StringUtils.isBlank(strs[typeIndex])
							&& !StringUtils.isBlank(domain.getType())) {
						strs[typeIndex] = domain.getType();
					}
					if (rankIndex != -1 && StringUtils.isBlank(strs[rankIndex])
							&& !StringUtils.isBlank(domain.getRank())) {
						strs[rankIndex] = domain.getRank();
					}
					if (incidenceIndex != -1 && StringUtils.isBlank(strs[incidenceIndex])
							&& !StringUtils.isBlank(domain.getIncidence())) {
						strs[incidenceIndex] = domain.getIncidence();
					}
					if (weightIndex != -1 && StringUtils.isBlank(strs[weightIndex])) {
						Integer weight = domain.getWeight();
						if (weight == null) {
							strs[weightIndex] = "0";
						} else {
							strs[weightIndex] = weight + "";
						}
					} else if (weightIndex != -1) {
						if (!StringUtils.isNumeric(strs[weightIndex])) {
							Integer weight = domain.getWeight();
							if (weight == null) {
								strs[weightIndex] = "0";
							} else {
								strs[weightIndex] = weight + "";
							}
						}
					}
				}
			} else {
				// tUrl不为null则，tUrl为二级域名，oUrl为其一级域名
				Domain two = DomainCacheManager.getByUrl(tUrl);
				Domain one = DomainCacheManager.getByUrl(oUrl);
				if (null != two && two.getMaintenanceStatus()) {
					// 如果该二级域名被标记为已维护，则覆盖网站名、栏目、类型、级别、影响范围、权重信息
					if (nameIndex != -1)
						strs[nameIndex] = two.getName();
					if (columnIndex != -1)
						strs[columnIndex] = two.getColumn();
					if (typeIndex != -1)
						strs[typeIndex] = two.getType();
					if (rankIndex != -1)
						strs[rankIndex] = two.getRank();
					if (incidenceIndex != -1)
						strs[incidenceIndex] = two.getIncidence();
					if (weightIndex != -1)
						strs[weightIndex] = two.getWeight() + "";
				} else if (one != null && one.getMaintenanceStatus()) {
					// 如果二级域名不是已维护状态，但他的一级域名是已维护状态，这覆盖网站名，级别、影响范围、权重信息
					if (nameIndex != -1)
						strs[nameIndex] = one.getName();
					if (rankIndex != -1)
						strs[rankIndex] = one.getRank();
					if (incidenceIndex != -1)
						strs[incidenceIndex] = one.getIncidence();
					if (typeIndex != -1)
						strs[typeIndex] = one.getType();
					if (weightIndex != -1)
						strs[weightIndex] = one.getWeight() + "";
				} else if (null != two) {
					// 若都不是被标记为已维护域名，则判断该域名是否存在域名信息库中，若存在则填充为空的信息，其他信息不做修改，若不存在域名信息库中，则不做处理
					if (nameIndex != -1 && StringUtils.isBlank(strs[nameIndex])) {
						strs[nameIndex] = two.getName();
					}
					if (columnIndex != -1 && StringUtils.isBlank(strs[columnIndex])
							&& !StringUtils.isBlank(two.getColumn())) {
						strs[columnIndex] = two.getColumn();
					}
					if (typeIndex != -1 && StringUtils.isBlank(strs[typeIndex])
							&& !StringUtils.isBlank(two.getType())) {
						strs[typeIndex] = two.getType();
					}
					if (rankIndex != -1 && StringUtils.isBlank(strs[rankIndex])
							&& !StringUtils.isBlank(two.getRank())) {
						strs[rankIndex] = two.getRank();
					}
					if (incidenceIndex != -1 && StringUtils.isBlank(strs[incidenceIndex])
							&& !StringUtils.isBlank(two.getIncidence())) {
						strs[incidenceIndex] = two.getIncidence();
					}
					if (weightIndex != -1 && StringUtils.isBlank(strs[weightIndex])) {
						Integer weight = two.getWeight();
						if (weight == null) {
							strs[weightIndex] = "0";
						} else {
							strs[weightIndex] = weight + "";
						}
					} else if (weightIndex != -1) {
						if (!StringUtils.isNumeric(strs[weightIndex])) {
							Integer weight = two.getWeight();
							if (weight == null) {
								strs[weightIndex] = "0";
							} else {
								strs[weightIndex] = weight + "";
							}
						}
					}
				}
			}
			res.add(strs);
		}
		res.add(0, attrs);
		return res;
	}

	/**
	 * 从文件from拷贝至to,如果存在则覆盖。
	 * 
	 * @param from
	 * @param to
	 */
	public static void copy(String from, String to) {
		try {
			File fromf = new File(from);
			File tof = new File(to);
			Files.copy(fromf, tof);
		} catch (Exception e) {
			logger.error("拷贝文件出错。");
		}
	}

	/**
	 * 写入文件
	 * 
	 * @param filename
	 *            文件名
	 * @param content
	 *            文件内容 包含标题
	 * @return
	 */
	public static boolean write(String filename, List<String[]> content) {
		String parentDir = new File(filename).getParent();
		if (!new File(parentDir).exists()) {
			new File(parentDir).mkdirs();
		}
		if (StringUtils.isEmpty(filename)) {
			return false;
		}
		if (content == null || content.size() == 0) {
			return false;
		}
		ExecutorService pool = Executors.newSingleThreadExecutor();
		Callable<Boolean> thread = new FileUtil().new WriteThread(filename, content);
		Future<Boolean> f = pool.submit(thread);
		try {
			return f.get();
		} catch (Exception e) {
			logger.error("写文件失败:{}", e.toString());
		}
		return false;
	}

	public static boolean write(String filename, OutputStream outputStream) {
		try {
			InputStream is = new BufferedInputStream(new FileInputStream(filename));
			byte[] buffer = new byte[4096];
			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				outputStream.write(buffer, 0, len);
			}
			is.close();
		} catch (Exception e) {
			logger.error("导出{}失败,因为{}", filename, e.toString());
			return false;
		}
		return true;
	}

	public static boolean delete(String filename) {
		File file = new File(filename);
		if (file.exists() && file.isFile()) {
			file.delete();
			return true;
		}
		return false;
	}

	class ReadThread implements Callable<List<String[]>> {

		private String filename;

		protected ReadThread(String filename) {
			super();
			this.filename = filename;
		}

		@Override
		public List<String[]> call() throws Exception {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			List<String[]> content = new ArrayList<String[]>();
			synchronized (br) {
				String line;
				while (true) {
					line = br.readLine();
					if (StringUtils.isEmpty(line)) {
						break;
					}
					String[] row = line.split("\t");
					content.add(row);
				}
			}
			br.close();
			return content;
		}
	}

	class WriteThread implements Callable<Boolean> {
		private String filename;
		private List<String[]> content;

		protected WriteThread(String filename, List<String[]> content) {
			super();
			this.filename = filename;
			this.content = content;
		}

		@Override
		public Boolean call() throws Exception {
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
				for (String[] row : content) {
					String line = StringUtils.join(row, "\t");
					bw.write(line + "\r\n");
				}
				bw.close();
			} catch (Exception e) {
				logger.error("写文件{}失败,因为{}", filename, e.toString());
				return false;
			}
			return true;
		}

	}

	/**
	 * title、url、time、amount
	 * 
	 * @param stdfilePath
	 *            标准数据路径
	 * @return
	 */
	public static List<String[]> getStdfileDisplaylist(String stdfilePath) {
		AttrUtil attrUtil = AttrUtil.getSingleton();
		try (BufferedReader br = new BufferedReader(new FileReader(stdfilePath))) {
			ArrayList<String[]> list = new ArrayList<String[]>();
			String line = br.readLine();
			String[] row = line.split("\t");
			int indexOfTitle = attrUtil.findIndexOf(row, attrUtil.getTitle_alias());
			int indexOfUrl = attrUtil.findIndexOf(row, attrUtil.getUrl_alias());
			int indexOfTime = attrUtil.findIndexOf(row, attrUtil.getTime_alias());
			do {
				String title = "", url = "";
				int amount = 0;
				String latestTime = "9999-12-12 23:59:59";
				while (!StringUtils.isBlank((line = br.readLine()))) {
					amount++;
					row = line.split("\t");
					if (latestTime.compareTo(row[indexOfTime]) > 0) {
						latestTime = row[indexOfTime];
						title = row[indexOfTitle];
						url = row[indexOfUrl];
					}
				}
				if (amount != 0) {
					list.add(new String[] { title, url, latestTime, String.valueOf(amount) });
				}
			} while (line != null);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("读取标准数据文件出错。");
			return null;
		}
	}

	/**
	 * title、url、time、amount
	 * 
	 * @param stdfilePath
	 *            标准数据路径
	 * @return
	 */
	public static List<String[]> getStdfileDisplaylist2(List<String[]> list) {
		AttrUtil attrUtil = AttrUtil.getSingleton();
		String[] row = list.get(0);
		int indexOfTitle = attrUtil.findIndexOf(row, attrUtil.getTitle_alias());
		int indexOfUrl = attrUtil.findIndexOf(row, attrUtil.getUrl_alias());
		int indexOfTime = attrUtil.findIndexOf(row, attrUtil.getTime_alias());
		int size = list.size();
		int i = 1;
		List<String[]> res = new ArrayList<String[]>();
		while (i < size) {
			String title = "", url = "";
			int amount = 0;
			String latestTime = "9999-12-12 23:59:59";
			while ((row = list.get(i++)).length != 0) {
				amount++;
				if (latestTime.compareTo(row[indexOfTime]) > 0) {
					latestTime = row[indexOfTime];
					title = row[indexOfTitle];
					url = row[indexOfUrl];
				}
			}
			if (amount != 0) {
				res.add(new String[] { title, url, latestTime, String.valueOf(amount) });
			}
		}
		return res;
	}

	/**
	 * <content,List<String[]>>， <marked,List<Integer>>
	 * 获取stdfile内容与待标记的id集合（每个类中的下标）
	 * 
	 * @param stdfilePath
	 * @return
	 */
	public static Map<String, Object> getStdfileExcelcontent(String stdfilePath) {
		Map<String, Object> map = new HashMap<String, Object>();
		AttrUtil attrUtil = AttrUtil.getSingleton();
		try (BufferedReader br = new BufferedReader(new FileReader(stdfilePath))) {
			List<String[]> content = new ArrayList<String[]>();
			List<Integer> marked = new ArrayList<Integer>();
			String line = br.readLine();
			String[] row = line.split("\t");
			content.add(row);
			int indexOfTime = attrUtil.findIndexOf(row, attrUtil.getTime_alias());
			do {
				String latestTime = "9999-12-12 23:59:59";
				List<String[]> cluster = new ArrayList<String[]>();
				int markedIndex = 0;
				int i = 0;
				while (!StringUtils.isBlank((line = br.readLine()))) {
					row = line.split("\t");
					cluster.add(row);
					if (latestTime.compareTo(row[indexOfTime]) > 0) {
						latestTime = row[indexOfTime];
						markedIndex = i;
					}
					i++;
				}
				if (i != 0) {
					content.addAll(cluster);
					if (i != 1) {
						marked.add(markedIndex);
					} else {
						continue;
					}
				}
				if (StringUtils.isEmpty(line)) {
					content.add(new String[0]);
				}
			} while (line != null);
			map.put(StdfileMap.CONTENT, content);
			map.put(StdfileMap.MARKED, marked);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("获取标准数据map出错。");
			return null;
		}
		return map;
	}

	/**
	 * <content,List<String[]>>， <marked,List<Integer>>
	 * 获取stdfile内容与待标记的id集合（每个类中的下标）
	 * 
	 * @param stdfilePath
	 * @return
	 */
	public static Map<String, Object> getStdfileExcelcontent2(List<String[]> list) {
		Map<String, Object> map = new HashMap<String, Object>();
		AttrUtil attrUtil = AttrUtil.getSingleton();
		List<String[]> content = new ArrayList<String[]>();
		List<Integer> marked = new ArrayList<Integer>();
		String[] row = list.get(0);
		content.add(row);
		int indexOfTime = attrUtil.findIndexOf(row, attrUtil.getTime_alias());
		int size = list.size();
		int j = 1;
		while (j < size) {
			String latestTime = "9999-12-12 23:59:59";
			int markedIndex = 0;
			int i = 0;
			while ((row = list.get(i)).length != 0) {
				if (latestTime.compareTo(row[indexOfTime]) > 0) {
					latestTime = row[indexOfTime];
					markedIndex = i;
				}
				i++;
			}
			if (i != 0) {
				marked.add(markedIndex);
			}
			j++;
		}
		map.put(StdfileMap.CONTENT, list);
		map.put(StdfileMap.MARKED, marked);
		return map;
	}

	/**
	 * <content,List<String[]>>， <marked,List<Integer>>
	 * 获取stdfile内容与待标记的id集合（每个类中的下标）
	 * 
	 * @param stdfilePath
	 * @return
	 */
	public static Map<String, Object> getStdfileExcelcontent3(String stdfilePath) {
		Map<String, Object> map = new HashMap<String, Object>();
		AttrUtil attrUtil = AttrUtil.getSingleton();
		try (BufferedReader br = new BufferedReader(new FileReader(stdfilePath))) {
			List<String[]> content = new ArrayList<String[]>();
			List<Integer> marked = new ArrayList<Integer>();
			String line = br.readLine();
			String[] row = line.split("\t");
			content.add(row);
			int indexOfTime = attrUtil.findIndexOf(row, attrUtil.getTime_alias());
			do {
				String latestTime = "9999-12-12 23:59:59";
				List<String[]> cluster = new ArrayList<String[]>();
				int markedIndex = 0;
				int i = 0;
				while (!StringUtils.isBlank((line = br.readLine()))) {
					row = line.split("\t");
					cluster.add(row);
					if (latestTime.compareTo(row[indexOfTime]) > 0) {
						latestTime = row[indexOfTime];
						markedIndex = i;
					}
					i++;
				}
				if (i != 0) {
					content.addAll(cluster);
					if (i != 1) {
						marked.add(markedIndex);
					}
				}
				if (StringUtils.isEmpty(line)) {
					content.add(new String[0]);
				}
			} while (line != null);
			map.put(StdfileMap.CONTENT, content);
			map.put(StdfileMap.MARKED, marked);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("获取标准数据map出错。");
			return null;
		}
		return map;
	}

	/**
	 * 获取目标cluster
	 * 
	 * @param stdfilePath
	 * @param targetIndex
	 * @return
	 */
	public static List<String[]> getStdfileTargetCluster(String stdfilePath, int targetIndex) {
		try (BufferedReader br = new BufferedReader(new FileReader(stdfilePath))) {
			ArrayList<String[]> list = new ArrayList<String[]>();
			String line = br.readLine();
			String[] row = line.split("\t");
			list.add(row);
			// targetIndex为0时，统计所有类信息
			if (targetIndex == 0) {
				while (line != null) {
					while (!StringUtils.isBlank((line = br.readLine()))) {
						row = line.split("\t");
						list.add(row);
					}
				}
			} else {
				int i = 0;
				do {
					if (i == targetIndex) {
						while (!StringUtils.isBlank((line = br.readLine()))) {
							row = line.split("\t");
							list.add(row);
						}
						break;
					}
					while (!StringUtils.isBlank((line = br.readLine())))
						;
					i++;
				} while (line != null);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("读取标准数据文件出错。");
			return null;
		}
	}

}
