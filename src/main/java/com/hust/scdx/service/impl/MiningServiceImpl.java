package com.hust.scdx.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hust.datamining.algorithm.cluster.Canopy;
import com.hust.datamining.algorithm.cluster.DBScan;
import com.hust.datamining.algorithm.cluster.KMeans;
import com.hust.datamining.convertor.Convertor;
import com.hust.datamining.convertor.DigitalConvertor;
import com.hust.datamining.convertor.TFIDFConvertor;
import com.hust.datamining.simcal.AcrossSimilarity;
import com.hust.datamining.simcal.CosSimilarity;
import com.hust.scdx.constant.Constant;
import com.hust.scdx.constant.Constant.ALGORIRHMTYPE;
import com.hust.scdx.constant.Constant.GRANULARITY;
import com.hust.scdx.constant.Constant.Index;
import com.hust.scdx.constant.Constant.KEY;
import com.hust.scdx.service.MiningService;
import com.hust.scdx.service.SegmentService;
import com.hust.scdx.util.AttrUtil;
import com.hust.scdx.util.ConvertUtil;

@Service
public class MiningServiceImpl implements MiningService {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(MiningServiceImpl.class);

	@Autowired
	private SegmentService segmentService;

	// 目的是聚类。第一个参数是原始文本，第二个为向量模型的选择，第三个为算法的选择（已经写死了，为canopy）。
	@Override
	public List<List<Integer>> cluster(List<String[]> list, int converterType, int algorithmType, int granularity) {
		// 用于存放结果
		List<List<Integer>> resultIndexSetList = new ArrayList<List<Integer>>();
		// 移除属性行
		String[] attrs = list.remove(0);
		int indexOfTitle = AttrUtil.findIndexOfTitle(attrs);
		int indexOfTime = AttrUtil.findIndexOfTime(attrs);
		List<String[]> segmentList = segmentService.getSegresult(list, indexOfTitle, 0);
		Convertor convertor = null;
		// 判断选择的向量模型的类型
		if (converterType == Constant.DIGITAL) {
			convertor = new DigitalConvertor();
		} else if (converterType == Constant.TFIDF) {
			convertor = new TFIDFConvertor();
		}
		convertor.setList(segmentList);
		List<double[]> vectors = convertor.getVector();
		// 向量转换完成

		// 如果选择的是canopy算法
		if (algorithmType == ALGORIRHMTYPE.CANOPY) {
			// System.out.println("使用的是CANOPY");
			Canopy canopy = new Canopy();
			canopy.setVectors(vectors);
			// 相似度方式的选择
			if (granularity == GRANULARITY.AcrossSimilarity) {
				canopy.setSimi(new AcrossSimilarity(vectors));
				// System.out.println("选择的是粗粒度AcrossSimilarity");

			} else if (granularity == GRANULARITY.CosSimilarity) {
				canopy.setSimi(new CosSimilarity(vectors));
				// System.out.println("选择的是细粒度CosSimilarity");
			}
			// 设置阀值
			canopy.setThreshold(Constant.SIMILARITYTHRESHOLD);
			// 开启线程池
			ExecutorService exec = Executors.newSingleThreadExecutor();
			Future<List<List<Integer>>> future = exec.submit(canopy);
			try {
				// 得到聚类结果
				resultIndexSetList = future.get();
			} catch (Exception e) {
				logger.error("error occur during clustering by canopy" + e.toString());
				return null;
			}
		} else if (algorithmType == ALGORIRHMTYPE.KMEANS) {
			// System.out.println("使用的是KMEANS");
			Canopy canopy = new Canopy();
			canopy.setVectors(vectors);
			// 相似度方式的选择
			canopy.setSimi(new AcrossSimilarity(vectors));
			// 设置阀值
			canopy.setThreshold(Constant.SIMILARITYTHRESHOLD);
			// 开启线程池
			ExecutorService exec = Executors.newSingleThreadExecutor();
			Future<List<List<Integer>>> future = exec.submit(canopy);
			try {
				// 得到聚类结果
				resultIndexSetList = future.get();
				int k = resultIndexSetList.size();
				KMeans kmeans = new KMeans();
				kmeans.setVectors(vectors);
				kmeans.setIterationTimes(20);
				// 相似度方式的选择
				if (granularity == GRANULARITY.AcrossSimilarity) {
					kmeans.setSimi(new AcrossSimilarity(vectors));
					// System.out.println("选择的是粗粒度AcrossSimilarity");

				} else if (granularity == GRANULARITY.CosSimilarity) {
					kmeans.setSimi(new CosSimilarity(vectors));
					// System.out.println("选择的是细粒度CosSimilarity");
				}
				kmeans.setK(k);

				Future<List<List<Integer>>> future1 = exec.submit(kmeans);
				try {
					// 得到聚类结果
					resultIndexSetList = future1.get();
				} catch (Exception e) {
					logger.error("error occur during clustering by canopy" + e.toString());
					return null;
				}

			} catch (Exception e) {
				logger.error("error occur during clustering by canopy" + e.toString());
				return null;
			}
		} else if (algorithmType == ALGORIRHMTYPE.DBSCAN) {
			// System.out.println("使用的是DBSCAN");
			DBScan dbscan = new DBScan();
			dbscan.setVectors(vectors);
			if (granularity == GRANULARITY.AcrossSimilarity) {
				dbscan.setSimi(new AcrossSimilarity(vectors));
				// System.out.println("选择的是粗粒度AcrossSimilarity");

			} else if (granularity == GRANULARITY.CosSimilarity) {
				dbscan.setSimi(new CosSimilarity(vectors));
				// System.out.println("选择的是细粒度CosSimilarity");
			}
			// 设置阀值
			dbscan.setMinPts(Constant.MinPts);
			dbscan.setEps(Constant.Eps);
			// 开启线程池
			ExecutorService exec = Executors.newSingleThreadExecutor();
			Future<List<List<Integer>>> future = exec.submit(dbscan);
			try {
				// 得到聚类结果
				resultIndexSetList = future.get();
			} catch (Exception e) {
				logger.error("error occur during clustering by canopy" + e.toString());
				return null;
			}
		} else {
			logger.error("没有选择任何算法");
			return null;
		}

		// 重载排序的方法，按照降序。类中数量多的排在前面。
		Collections.sort(resultIndexSetList, new Comparator<List<Integer>>() {

			@Override
			public int compare(List<Integer> o1, List<Integer> o2) {
				// TODO Auto-generated method stub
				return o2.size() - o1.size();
			}
		});
		for (List<Integer> set : resultIndexSetList) {
			Collections.sort(set, new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {
					// TODO Auto-generated method stub
					// 判断他们的标题是否相同
					int compare = list.get(o1)[indexOfTitle].compareTo(list.get(o2)[indexOfTitle]);
					// 若不相同，使用时间进行排序。
					if (compare == 0) {
						compare = list.get(o1)[indexOfTime].compareTo(list.get(o2)[indexOfTime]);
					}
					return compare;
				}
			});
		}
		return resultIndexSetList;
	}

	// 用于得出orig_count的数据
	@Override
	public List<int[]> count(List<String[]> content, List<String[]> cluster) {
		// TODO Auto-generated method stub
		List<int[]> clusterInt = ConvertUtil.toIntList(cluster); // 变成
		List<int[]> reList = new ArrayList<int[]>();
		for (int i = 0; i < clusterInt.size(); i++) {
			int[] tmpList = clusterInt.get(i); // 取第i条数组
			int origIndex = -1;
			String origTime = "9999-12-12 23:59:59";
			for (int j = 0; j < tmpList.length; j++) {
				String[] row = content.get(tmpList[j]); // 取它的真实内容
				try {
					if (origTime.compareTo(row[Index.TIME_INDEX]) > 0) {
						origTime = row[Index.TIME_INDEX];
						origIndex = tmpList[j];
					}
				} catch (Exception e) {
					logger.error("sth error when count:{}", tmpList[j]);
				}
			}
			if (origIndex == -1) {
				origIndex = tmpList[0];
			}
			int[] item = new int[2];
			item[Index.COUNT_ITEM_INDEX] = origIndex;
			item[Index.COUNT_ITEM_AMOUNT] = tmpList.length;
			reList.add(item);
		}
		return reList;
	}

	@Override
	public Map<String, Object> getAmount(Map<String, Map<String, Map<String, Integer>>> map) {
		// TODO Auto-generated method stub
		if (map == null) {
			return null;
		}
		Map<String, Integer> typeAmountMap = new HashMap<String, Integer>();
		for (Map<String, Map<String, Integer>> values : map.values()) {
			Map<String, Integer> typeMap = values.get(Constant.INFOTYPE_EN);
			for (Entry<String, Integer> entry : typeMap.entrySet()) {
				Integer oldValue = typeAmountMap.get(entry.getKey());
				if (null == oldValue) {
					oldValue = 0;
				}
				typeAmountMap.put(entry.getKey(), entry.getValue() + oldValue);
			}
		}
		Map<String, Integer> mediaAmountMap = new HashMap<String, Integer>();
		for (Map<String, Map<String, Integer>> values : map.values()) {
			Map<String, Integer> mediaMap = values.get(Constant.MEDIA_EN);
			for (Entry<String, Integer> entry : mediaMap.entrySet()) {
				Integer oldValue = mediaAmountMap.get(entry.getKey());
				if (null == oldValue) {
					oldValue = 0;
				}
				mediaAmountMap.put(entry.getKey(), entry.getValue() + oldValue);
			}
		}
		Map<String, Object> reMap = new HashMap<>();
		reMap.put(KEY.MINING_AMOUNT_MEDIA, mediaAmountMap);
		reMap.put(KEY.MINING_AMOUNT_TYPE, typeAmountMap);
		return reMap;
	}

}
