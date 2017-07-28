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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

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
	@SuppressWarnings("null")
	public static List<String[]> readExtfiles(String... filenames) {
		List<String[]> content = null;
		if (CommonUtil.hasEmptyArray(filenames)) {
			return null;
		}
		// 属性list
		List<String> globalAttrs = new ArrayList<String>();
		for (int i = 0; i < filenames.length; i++) {
			try (BufferedReader br = new BufferedReader(new FileReader(filenames[i]))) {
				String line = br.readLine();
				// 当前文件url、time所在列
				int indexOfUrl = AttrUtil.findIndexOfUrl(line.split("\t"));
				int indexOfTime = AttrUtil.findIndexOfTime(line.split("t"));
				// 当前文件的所有属性在全局文件的索引位置
				int[] indexs = getIndexOfExtfile(line, globalAttrs);
				// 全局文件url、time所在列
				int globalIndexOfUrl = AttrUtil.findIndexOfUrl(globalAttrs);
				int globalIndexOfTime = AttrUtil.findIndexOfTime(globalAttrs);
				// 全局文件中已存在的url,key未url、Integer为当前url所在行的行数（从0开始）
				HashMap<String, Integer> urlMap = new HashMap<String, Integer>();
				while (true) {
					line = br.readLine();
					if (!StringUtils.isBlank(line)) {
						String[] row = line.split("\t");
						String[] imp = new String[globalAttrs.size()];
						for (int j = 0; j < row.length; j++) {
							imp[indexs[j]] = row[j];
						}
						// 如果检测到url重复
						if (urlMap.containsKey(row[indexOfUrl])) {
							// 比较两条url所属新闻数据的时间先后s
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
					}
				}
			} catch (Exception e) {
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

		String[] tmp = new String[globalAttrs.size()];
		globalAttrs.toArray(tmp);
		content.add(0, tmp);
		return content;
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
	private static int[] getIndexOfExtfile(String rowOne, List<String> globalAttrs) {
		int[] indexs = null;
		String[] attrs = rowOne.split("\t");
		indexs = new int[attrs.length];
		for (int i = 0; i < attrs.length; i++) {
			int index = globalAttrs.indexOf(attrs[i]);
			if (index != -1) { // 防止相似属性
				indexs[i] = index;
			} else {
				if (AttrUtil.isTitle(attrs[i])) {
					indexs[i] = AttrUtil.findIndexOfTitle(attrs);
				} else if (AttrUtil.isUrl(attrs[i])) {
					indexs[i] = AttrUtil.findIndexOfUrl(attrs);
				} else if (AttrUtil.isTime(attrs[i])) {
					indexs[i] = AttrUtil.findIndexOfTime(attrs);
				} else {
					globalAttrs.add(attrs[i]);
					indexs[i] = globalAttrs.size() - 1;
				}
			}
		}
		return indexs;
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
}
