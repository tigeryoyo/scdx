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
import com.hust.scdx.constant.Constant;
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
		// 属性list
		List<String> globalAttrs = new ArrayList<String>();
		// 全局文件中已存在的url,key未url、Integer为当前url所在行的行数（从0开始）
		HashMap<String, Integer> urlMap = new HashMap<String, Integer>();
		for (int i = 0; i < filenames.length; i++) {
			try (BufferedReader br = new BufferedReader(new FileReader(filenames[i]))) {
				String line = br.readLine();
				String[] attrs = line.split("\t");
				// 当前文件url、time所在列
				int indexOfUrl = AttrUtil.findIndexOfUrl(attrs);
				int indexOfTime = AttrUtil.findIndexOfTime(attrs);
				// 当前文件的所有属性在全局文件的索引位置
				int[] indexs = i == 0 ? initGlobalAttrs(attrs, globalAttrs) : getIndexOfExtfile(attrs, globalAttrs);
				// 全局文件time所在列
				int globalIndexOfTime = AttrUtil.findIndexOfTime(globalAttrs);
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
		int indexOfTite = AttrUtil.findIndexOfTitle(globalAttrs);
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
		return fillContentFromDomain(adjustPropertyLine(globalAttrs, content));
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
	 * 获取基础数据首行各个属性在公共属性集合中的索引位置,获取url所在列
	 * 
	 * @param rowOne
	 *            基础数据属性行
	 * @param gloableAttrs
	 *            所有属性集合
	 * @return
	 */
	private static int[] getIndexOfExtfile(String[] attrs, List<String> globalAttrs) {
		int[] indexs = null;
		indexs = new int[attrs.length];
		for (int i = 0; i < attrs.length; i++) {
			int index = globalAttrs.indexOf(attrs[i]);
			if (index != -1) { // 防止相似属性
				indexs[i] = index;
			} else {
				if (AttrUtil.isSth(attrs[i], AttrUtil.TITLE_PATTERN)) {
					indexs[i] = AttrUtil.findIndexOfTitle(globalAttrs);
					globalAttrs.set(indexs[i], "标题");
				} else if (AttrUtil.isSth(attrs[i], AttrUtil.URL_PATTERN)) {
					indexs[i] = AttrUtil.findIndexOfUrl(globalAttrs);
					globalAttrs.set(indexs[i], "链接");
				} else if (AttrUtil.isSth(attrs[i], AttrUtil.TIME_PATTERN)) {
					indexs[i] = AttrUtil.findIndexOfTime(globalAttrs);
					globalAttrs.set(indexs[i], "发帖时间");
				} else if (AttrUtil.isSth(attrs[i], AttrUtil.COLUMN_PATTERN)) {
					int j = AttrUtil.findIndexOfSth(globalAttrs, AttrUtil.COLUMN_PATTERN);
					if (j == -1) {
						indexs[i] = globalAttrs.size() - 1;
					} else {
						indexs[i] = j;
					}
					globalAttrs.set(indexs[i], "板块");
				} else if (AttrUtil.isSth(attrs[i], AttrUtil.TYPE_PATTERN)) {
					int j = AttrUtil.findIndexOfSth(globalAttrs, AttrUtil.TYPE_PATTERN);
					if (j == -1) {
						indexs[i] = globalAttrs.size() - 1;
					} else {
						indexs[i] = j;
					}
					globalAttrs.set(indexs[i], "来源");
				} else if (AttrUtil.isSth(attrs[i], AttrUtil.WEBNAME_PATTERN)) {
					int j = AttrUtil.findIndexOfSth(globalAttrs, AttrUtil.WEBNAME_PATTERN);
					if (j == -1) {
						indexs[i] = globalAttrs.size() - 1;
					} else {
						indexs[i] = j;
					}
					globalAttrs.set(indexs[i], "网站");
				} else if (AttrUtil.isSth(attrs[i], AttrUtil.POSTING)) {
					int j = AttrUtil.findIndexOfSth(globalAttrs, AttrUtil.POSTING);
					if (j == -1) {
						indexs[i] = globalAttrs.size() - 1;
					} else {
						indexs[i] = j;
					}
					globalAttrs.set(indexs[i], "发帖人");
				} else if (AttrUtil.isSth(attrs[i], AttrUtil.RANK_PATTERN)) {
					int j = AttrUtil.findIndexOfSth(globalAttrs, AttrUtil.RANK_PATTERN);
					if (j == -1) {
						indexs[i] = globalAttrs.size() - 1;
					} else {
						indexs[i] = j;
					}
					globalAttrs.set(indexs[i], "媒体级别");
				} else if (AttrUtil.isSth(attrs[i], AttrUtil.INCIDENCE_PATTERN)) {
					int j = AttrUtil.findIndexOfSth(globalAttrs, AttrUtil.INCIDENCE_PATTERN);
					if (j == -1) {
						indexs[i] = globalAttrs.size() - 1;
					} else {
						indexs[i] = j;
					}
					globalAttrs.set(indexs[i], "影响范围");
				} else if (AttrUtil.isSth(attrs[i], AttrUtil.WEIGHT_PATTERN)) {
					int j = AttrUtil.findIndexOfSth(globalAttrs, AttrUtil.WEIGHT_PATTERN);
					if (j == -1) {
						indexs[i] = globalAttrs.size() - 1;
					} else {
						indexs[i] = j;
					}
					globalAttrs.set(indexs[i], "权重");
				} else {
					globalAttrs.add(attrs[i]);
					indexs[i] = globalAttrs.size() - 1;
				}
			}
		}
		return indexs;
	}

	/**
	 * 调整属性行
	 * 
	 * @param attrs
	 * @param content
	 */
	private static List<String[]> adjustPropertyLine(List<String> attrs, List<String[]> content) {
		int attrSize = attrs.size();
		int[] orderAttrs = new int[attrSize + 6];
		List<String> newAttrs = new ArrayList<String>();
		int order = 0;
		
		int index = AttrUtil.findIndexOfSth(attrs, AttrUtil.WEIGHT_PATTERN);
		if (index != -1) {
			orderAttrs[order++] = index;
			newAttrs.add(attrs.get(index));
		} else {
			orderAttrs[order++] = index;
			newAttrs.add(AttrUtil.WEIGHT_PATTERN);
		}
		index = AttrUtil.findIndexOfSth(attrs, AttrUtil.WEBNAME_PATTERN);
		if (index != -1) {
			orderAttrs[order++] = index;
			newAttrs.add(attrs.get(index));
		} else {
			orderAttrs[order++] = index;
			newAttrs.add("网站");
		}

		index = AttrUtil.findIndexOfSth(attrs, AttrUtil.COLUMN_PATTERN);
		if (index != -1) {
			orderAttrs[order++] = index;
			newAttrs.add(attrs.get(index));
		} else {
			orderAttrs[order++] = index;
			newAttrs.add("板块");
		}

		index = AttrUtil.findIndexOfTitle(attrs);
		if (index != -1) {
			orderAttrs[order++] = index;
			newAttrs.add(attrs.get(index));
		}

		index = AttrUtil.findIndexOfSth(attrs, AttrUtil.POSTING);
		if (index != -1) {
			orderAttrs[order++] = index;
			newAttrs.add(attrs.get(index));
		}

		index = AttrUtil.findIndexOfTime(attrs);
		if (index != -1) {
			orderAttrs[order++] = index;
			newAttrs.add(attrs.get(index));
		}

		index = AttrUtil.findIndexOfUrl(attrs);
		if (index != -1) {
			orderAttrs[order++] = index;
			newAttrs.add(attrs.get(index));
		}

		index = AttrUtil.findIndexOfSth(attrs, AttrUtil.TYPE_PATTERN);
		if (index != -1) {
			orderAttrs[order++] = index;
			newAttrs.add(attrs.get(index));
		} else {
			orderAttrs[order++] = index;
			newAttrs.add("类型");
		}

		index = AttrUtil.findIndexOfSth(attrs, AttrUtil.RANK_PATTERN);
		if (index != -1) {
			orderAttrs[order++] = index;
			newAttrs.add(attrs.get(index));
		} else {
			orderAttrs[order++] = index;
			newAttrs.add(AttrUtil.RANK_PATTERN);
		}

		index = AttrUtil.findIndexOfSth(attrs, AttrUtil.INCIDENCE_PATTERN);
		if (index != -1) {
			orderAttrs[order++] = index;
			newAttrs.add(attrs.get(index));
		} else {
			orderAttrs[order++] = index;
			newAttrs.add(AttrUtil.INCIDENCE_PATTERN);
		}

		

		for (int i = 0; i < attrSize; i++) {
			if (!AttrUtil.isImp(attrs.get(i))) {
				orderAttrs[order++] = i;
				newAttrs.add(attrs.get(i));
			}
		}

		List<String[]> res = new ArrayList<String[]>();
		int contentSize = content.size();
		attrSize = newAttrs.size();
		for (int i = 0; i < contentSize; i++) {
			String[] cline = content.get(i);
			String[] line = new String[attrSize];
			for (int j = 0; j < line.length; j++) {
				if (orderAttrs[j] != -1 && orderAttrs[j] < cline.length) {
					line[j] = cline[orderAttrs[j]];
				} else {
					line[j] = "";
				}
			}
			res.add(line);
		}
		String[] tmp = new String[newAttrs.size()];
		newAttrs.toArray(tmp);
		res.add(0, tmp);
		return res;
	}

	private static List<String[]> fillContentFromDomain(List<String[]> content) {
		List<String[]> res = new ArrayList<String[]>();
		String[] attrs = content.remove(0);
		int urlIndex = AttrUtil.findIndexOfSth(attrs, AttrUtil.URL_PATTERN);
		int nameIndex = AttrUtil.findIndexOfSth(attrs, AttrUtil.WEBNAME_PATTERN);
		int columnIndex = AttrUtil.findIndexOfSth(attrs, AttrUtil.COLUMN_PATTERN);
		int typeIndex = AttrUtil.findIndexOfSth(attrs, AttrUtil.TYPE_PATTERN);
		int rankIndex = AttrUtil.findIndexOfSth(attrs, AttrUtil.RANK_PATTERN);
		int incidenceIndex = AttrUtil.findIndexOfSth(attrs, AttrUtil.INCIDENCE_PATTERN);
		int weightIndex = AttrUtil.findIndexOfSth(attrs, AttrUtil.WEIGHT_PATTERN);
		for (String[] strs : content) {
			String url = UrlUtil.getUrl(strs[urlIndex]);
			String tUrl = UrlUtil.getDomainTwo(url);
			if (tUrl == null) {
				// 如果二级域名不存在，则url为一级域名
				if (Constant.markedDomain.containsKey(url)) {
					// 如果该一级域名被标记为已维护，则覆盖网站名、栏目、类型、级别、影响范围、权重信息
					Domain domain = Constant.markedDomain.get(url);
					strs[nameIndex] = domain.getName();
					strs[columnIndex] = domain.getColumn();
					strs[typeIndex] = domain.getType();
					strs[rankIndex] = domain.getRank();
					strs[incidenceIndex] = domain.getIncidence();
					strs[weightIndex] = domain.getWeight() + "";
				} else if (Constant.unmarkedDomain.containsKey(url)) {
					// 若不是被标记为已维护域名，则判断该域名是否存在域名信息库中，若存在则填充为空的信息，其他信息不做修改，若不存在域名信息库中，则不做处理
					Domain domain = Constant.unmarkedDomain.get(url);
					if (StringUtils.isBlank(strs[nameIndex])) {
						strs[nameIndex] = domain.getName();
					}
					if (StringUtils.isBlank(strs[columnIndex]) && null != domain.getColumn()) {
						strs[columnIndex] = domain.getColumn();
					}
					if (StringUtils.isBlank(strs[typeIndex]) && null != domain.getType()) {
						strs[typeIndex] = domain.getType();
					}
					if (StringUtils.isBlank(strs[rankIndex]) && null != domain.getRank()) {
						strs[rankIndex] = domain.getRank();
					}
					if (StringUtils.isBlank(strs[incidenceIndex]) && null != domain.getIncidence()) {
						strs[incidenceIndex] = domain.getIncidence();
					}
					if (StringUtils.isBlank(strs[weightIndex])) {
						if(!StringUtils.isNumeric(strs[weightIndex])){
							Integer weight = domain.getWeight();
							if(weight == null){
								strs[weightIndex] = "0";
							}else{
								strs[weightIndex] = weight + "";
							}
						}else{
							strs[weightIndex] = "0";
						}
					}
				}
			} else {
				// tUrl不为null则，tUrl为二级域名，url为其一级域名
				if (Constant.markedDomain.containsKey(tUrl)) {
					// 如果该二级域名被标记为已维护，则覆盖网站名、栏目、类型、级别、影响范围、权重信息
					Domain domain = Constant.markedDomain.get(tUrl);
					strs[nameIndex] = domain.getName();
					strs[columnIndex] = domain.getColumn();
					strs[typeIndex] = domain.getType();
					strs[rankIndex] = domain.getRank();
					strs[incidenceIndex] = domain.getIncidence();
					strs[weightIndex] = domain.getWeight() + "";
				} else if (Constant.markedDomain.containsKey(url)) {
					// 如果二级域名不是已维护状态，但他的一级域名是已维护状态，这覆盖网站名，级别、影响范围、权重信息
					Domain domain = Constant.markedDomain.get(url);
					strs[nameIndex] = domain.getName();
					strs[rankIndex] = domain.getRank();
					strs[incidenceIndex] = domain.getIncidence();
					strs[weightIndex] = domain.getWeight() + "";
				} else if (Constant.unmarkedDomain.containsKey(tUrl)) {
					// 若都不是被标记为已维护域名，则判断该域名是否存在域名信息库中，若存在则填充为空的信息，其他信息不做修改，若不存在域名信息库中，则不做处理
					Domain domain = Constant.unmarkedDomain.get(tUrl);
					if (StringUtils.isBlank(strs[nameIndex])) {
						strs[nameIndex] = domain.getName();
					}
					if (StringUtils.isBlank(strs[columnIndex]) && null != domain.getColumn()) {
						strs[columnIndex] = domain.getColumn();
					}
					if (StringUtils.isBlank(strs[typeIndex]) && null != domain.getType()) {
						strs[typeIndex] = domain.getType();
					}
					if (StringUtils.isBlank(strs[rankIndex]) && null != domain.getRank()) {
						strs[rankIndex] = domain.getRank();
					}
					if (StringUtils.isBlank(strs[incidenceIndex]) && null != domain.getIncidence()) {
						strs[incidenceIndex] = domain.getIncidence();
					}
					if (StringUtils.isBlank(strs[weightIndex]) ) {
						if(!StringUtils.isNumeric(strs[weightIndex])){
							Integer weight = domain.getWeight();
							if(weight == null){
								strs[weightIndex] = "0";
							}else{
								strs[weightIndex] = weight + "";
							}
						}else{
							strs[weightIndex] = "0";
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
		try (BufferedReader br = new BufferedReader(new FileReader(stdfilePath))) {
			ArrayList<String[]> list = new ArrayList<String[]>();
			String line = br.readLine();
			String[] row = line.split("\t");
			int indexOfTitle = AttrUtil.findIndexOfTitle(row);
			int indexOfUrl = AttrUtil.findIndexOfUrl(row);
			int indexOfTime = AttrUtil.findIndexOfTime(row);
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
		String[] row = list.get(0);
		int indexOfTitle = AttrUtil.findIndexOfTitle(row);
		int indexOfUrl = AttrUtil.findIndexOfUrl(row);
		int indexOfTime = AttrUtil.findIndexOfTime(row);
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
		try (BufferedReader br = new BufferedReader(new FileReader(stdfilePath))) {
			List<String[]> content = new ArrayList<String[]>();
			List<Integer> marked = new ArrayList<Integer>();
			String line = br.readLine();
			String[] row = line.split("\t");
			content.add(row);
			int indexOfTime = AttrUtil.findIndexOfTime(row);
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
		List<String[]> content = new ArrayList<String[]>();
		List<Integer> marked = new ArrayList<Integer>();
		String[] row = list.get(0);
		content.add(row);
		int indexOfTime = AttrUtil.findIndexOfTime(row);
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
		try (BufferedReader br = new BufferedReader(new FileReader(stdfilePath))) {
			List<String[]> content = new ArrayList<String[]>();
			List<Integer> marked = new ArrayList<Integer>();
			String line = br.readLine();
			String[] row = line.split("\t");
			content.add(row);
			int indexOfTime = AttrUtil.findIndexOfTime(row);
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
