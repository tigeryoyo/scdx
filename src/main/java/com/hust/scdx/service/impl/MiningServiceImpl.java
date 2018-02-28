package com.hust.scdx.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
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
import com.hust.scdx.constant.Constant.Algorithm;
import com.hust.scdx.constant.Constant.Index;
import com.hust.scdx.constant.Constant.KEY;
import com.hust.scdx.dao.WeightDao;
import com.hust.scdx.model.Domain;
import com.hust.scdx.service.DomainService;
import com.hust.scdx.service.MiningService;
import com.hust.scdx.service.SegmentService;
import com.hust.scdx.util.AttrUtil;
import com.hust.scdx.util.CommonUtil;
import com.hust.scdx.util.ConvertUtil;
import com.hust.scdx.util.TimeUtil;
import com.hust.scdx.util.UrlUtil;

@Service
public class MiningServiceImpl implements MiningService {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(MiningServiceImpl.class);

	@Autowired
	private SegmentService segmentService;
	@Autowired
	private DomainService domainService;
	@Autowired
	private WeightDao weightDao;

	/**
	 * 目的是聚类。第一个参数是原始文本，第二个为向量模型的选择，第三个为算法的选择（已经写死了，为canopy）。
	 */
	@Override
	public List<List<Integer>> getOrigClusters(String[] attrs, List<String[]> content, int converterType,
			int algorithmType, int granularity) {
		// 用于存放结果
		List<List<Integer>> resultIndexSetList = new ArrayList<List<Integer>>();
		AttrUtil attrUtil = AttrUtil.getSingleton();
    	int indexOfTitle = attrUtil.findIndexOf(attrs, attrUtil.getTitle_alias());
		int indexOfTime = attrUtil.findIndexOf(attrs, attrUtil.getTime_alias());
		List<String[]> segmentList = segmentService.getSegresult(content, indexOfTitle, 0);
		Convertor convertor = null;
		// 判断选择的向量模型的类型
		if (converterType == Algorithm.DIGITAL) {
			convertor = new DigitalConvertor();
		} else if (converterType == Algorithm.TFIDF) {
			convertor = new TFIDFConvertor();
		}
		convertor.setList(segmentList);
		List<double[]> vectors = convertor.getVector();
		// 向量转换完成

		// 如果选择的是canopy算法
		if (algorithmType == Algorithm.CANOPY) {
			// System.out.println("使用的是CANOPY");
			Canopy canopy = new Canopy();
			canopy.setVectors(vectors);
			// 相似度方式的选择
			if (granularity == Algorithm.AcrossSimilarity) {
				canopy.setSimi(new AcrossSimilarity(vectors));
				// System.out.println("选择的是粗粒度AcrossSimilarity");

			} else if (granularity == Algorithm.CosSimilarity) {
				canopy.setSimi(new CosSimilarity(vectors));
				// System.out.println("选择的是细粒度CosSimilarity");
			}
			// 设置阀值
			canopy.setThreshold(Algorithm.SIMILARITYTHRESHOLD);
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
		} else if (algorithmType == Algorithm.KMEANS) {
			// System.out.println("使用的是KMEANS");
			Canopy canopy = new Canopy();
			canopy.setVectors(vectors);
			// 相似度方式的选择
			canopy.setSimi(new AcrossSimilarity(vectors));
			// 设置阀值
			canopy.setThreshold(Algorithm.SIMILARITYTHRESHOLD);
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
				if (granularity == Algorithm.AcrossSimilarity) {
					kmeans.setSimi(new AcrossSimilarity(vectors));
					// System.out.println("选择的是粗粒度AcrossSimilarity");

				} else if (granularity == Algorithm.CosSimilarity) {
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
		} else if (algorithmType == Algorithm.DBSCAN) {
			// System.out.println("使用的是DBSCAN");
			DBScan dbscan = new DBScan();
			dbscan.setVectors(vectors);
			if (granularity == Algorithm.AcrossSimilarity) {
				dbscan.setSimi(new AcrossSimilarity(vectors));
				// System.out.println("选择的是粗粒度AcrossSimilarity");

			} else if (granularity == Algorithm.CosSimilarity) {
				dbscan.setSimi(new CosSimilarity(vectors));
				// System.out.println("选择的是细粒度CosSimilarity");
			}
			// 设置阀值
			dbscan.setMinPts(Algorithm.MINPTS);
			dbscan.setEps(Algorithm.EPS);
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

		return resultIndexSetList;
	}

	/**
	 * 用于得出origCounts的数据
	 */
	@Override
	public List<int[]> getOrigCounts(String[] attrs, List<String[]> content, List<String[]> origClusters) {
		List<int[]> origCounts = new ArrayList<int[]>();
		AttrUtil attrUtil = AttrUtil.getSingleton();
		int indexOfTime = attrUtil.findIndexOf(attrs, attrUtil.getTime_alias());
		List<int[]> clusters = ConvertUtil.toIntArrayList(origClusters); // 变成
		for (int i = 0; i < clusters.size(); i++) {
			int[] cluster = clusters.get(i); // 取第i条数组
			int origIndex = -1;
			String origTime = "9999-12-12 23:59:59";
			for (int j = 0; j < cluster.length; j++) {
				String[] row = content.get(cluster[j]); // 取它的真实内容
				try {
					if (origTime.compareTo(row[indexOfTime]) > 0) {
						origTime = row[indexOfTime];
						origIndex = cluster[j];
					}
				} catch (Exception e) {
					logger.error("计算origCount错误{}", cluster[j]);
				}
			}
			if (origIndex == -1) {
				origIndex = cluster[0];
			}
			int[] item = new int[2];
			item[Index.COUNT_ITEM_INDEX] = origIndex;
			item[Index.COUNT_ITEM_AMOUNT] = cluster.length;
			origCounts.add(item);
		}
		return origCounts;
	}
	
	
	
	/**
	 * 获取出图需要的统计数据信息
	 * @param map
	 * @return
	 */
	@Override
    public Map<String, Object> getAmount(Map<String, Map<String, Map<String, Integer>>> map) {
        // TODO Auto-generated method stub
        if (map == null) {
            return null;
        }
        Map<String, Integer> typeAmountMap = new HashMap<String, Integer>();
        for (Map<String, Map<String, Integer>> values : map.values()) {
            Map<String, Integer> typeMap = values.get(KEY.INFOTYPE_EN);
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
            Map<String, Integer> mediaMap = values.get(KEY.MEDIA_EN);
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

    /**
     * 统计准数据某个类各个维度信息
     * content 类的所有记录
     */
    @Override
	public Map<String, Map<String, Map<String, Integer>>> statisticStdfile(List<String[]> content, int interval) {
		Map<String, Map<String, Map<String, Integer>>> map =
                new TreeMap<String, Map<String, Map<String, Integer>>>(new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return o1.compareTo(o2);
                    }
                });
        //属性行
        String[] attrs = content.remove(0);
        AttrUtil attrUtil = AttrUtil.getSingleton();
		int indexOfUrl = attrUtil.findIndexOf(attrs, attrUtil.getUrl_alias());
		int indexOfTime = attrUtil.findIndexOf(attrs, attrUtil.getTime_alias());
        int indexOfType = attrUtil.findIndexOf(attrs, attrUtil.getType_alias());
        int indexOfLevel = attrUtil.findIndexOf(attrs, attrUtil.getRank_alias());
        for (String[] row : content) {
           
            if (CommonUtil.isEmptyArray(row)) {
                continue;
            }
           
           /* Domain domain = domainService.getDomainByUrl(row[indexOfUrl]);
            if(domain == null){
            	continue;
            }
            String level = domain.getRank();
            String type = domain.getType();*/
            String level = row[indexOfLevel];
            String type = row[indexOfType];
            String timeKey = TimeUtil.getTimeKey(row[indexOfTime], interval);
            Map<String, Map<String, Integer>> timeMap = map.get(timeKey);
            if (timeMap == null) {
                timeMap = new HashMap<String, Map<String, Integer>>();
                Map<String, Integer> typeMap = new HashMap<String, Integer>();
                Map<String, Integer> levelMap = new HashMap<String, Integer>();
                typeMap.put(type, 1);
                levelMap.put(level, 1);
                timeMap.put(KEY.MEDIA_EN, levelMap);
                timeMap.put(KEY.INFOTYPE_EN, typeMap);
                map.put(timeKey, timeMap);
            } else {
                Map<String, Integer> typeMap = timeMap.get(KEY.INFOTYPE_EN);
                if (null == typeMap) {
                    typeMap = new HashMap<String, Integer>();
                    typeMap.put(type, 1);
                } else {
                    if (typeMap.get(type) == null) {
                        typeMap.put(type, 1);
                    } else {
                        typeMap.put(type, typeMap.get(type) + 1);
                    }
                }

                Map<String, Integer> levelMap = timeMap.get(KEY.MEDIA_EN);
                if (null == levelMap) {
                    levelMap = new HashMap<String, Integer>();
                    levelMap.put(level, 1);
                } else {
                    if (levelMap.get(level) == null) {
                        levelMap.put(level, 1);
                    } else {
                        levelMap.put(level, levelMap.get(level) + 1);
                    }
                }
                timeMap.put(KEY.MEDIA_EN, levelMap);
                timeMap.put(KEY.INFOTYPE_EN, typeMap);
                map.put(timeKey, timeMap);
            }
        }
        for (String time : map.keySet()) {
            Map<String, Map<String, Integer>> timeMap = map.get(time);
            Map<String, Integer> mediaAttention = calAttention(timeMap.get(KEY.MEDIA_EN));
            Map<String, Integer> netizenAttention = calAttention(timeMap.get(KEY.INFOTYPE_EN));
            timeMap.put(KEY.NETIZENATTENTION_EN, netizenAttention);
            timeMap.put(KEY.MEDIAATTENTION_EN, mediaAttention);
        }
        return map;
	}
    
    private Map<String, Integer> calAttention(Map<String, Integer> map) {
        // TODO Auto-generated method stub
        Map<String, Integer> attention = new HashMap<String, Integer>();
        if (null == map) {
            return attention;
        }
        for (Entry<String, Integer> entry : map.entrySet()) {
        	if(null == entry || null == entry.getKey()){
        		continue;
        	}
            int weight = weightDao.queryWeightByName(entry.getKey().toString());
            int atten = weight * entry.getValue();
            attention.put(entry.getKey(), atten);
        }
        return attention;
    }

}
