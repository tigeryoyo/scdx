package com.hust.scdx.service.impl;

import java.io.IOException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;

import org.apache.tools.ant.taskdefs.optional.jlink.jlink;
import org.apache.xmlbeans.impl.xb.xsdownload.DownloadedSchemaEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hust.datamining.algorithm.cluster.Canopy;
import com.hust.datamining.algorithm.cluster.DBScan;
import com.hust.datamining.algorithm.cluster.KMeans;
import com.hust.datamining.convertor.Convertor;
import com.hust.datamining.convertor.DigitalConvertor;
import com.hust.datamining.convertor.TFIDFConvertor;
import com.hust.datamining.simcal.AcrossSimilarity;
import com.hust.datamining.simcal.CosSimilarity;
import com.hust.scdx.constant.Constant.Algorithm;
import com.hust.scdx.service.AlgorithmContainerService;
import com.hust.scdx.service.MiningService;
import com.hust.scdx.service.SegmentService;
import com.hust.scdx.util.AttrUtil;
import com.hust.scdx.util.ConvertUtil;

@Service
public class AlgorithmContainerServiceImpl implements AlgorithmContainerService {

	 @Autowired
	 private SegmentService segmentService;
	 
	 @Autowired
	 private MiningService miningService;
	 
	 
	 //存放返回给前台显示的聚类结果
	 private static List<String[]> resultContent = new ArrayList<String[]>();
	 //存放聚类结果中没给类的数量信息
	 private static List<int[]> countResult = new ArrayList<int[]>();
	 //存放聚类结果的详细信息
	 private static List<String[]> clusterResult = new ArrayList<String[]>();
	 /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(MiningServiceImpl.class);
	
    /**
     * 下载聚类结果(带属性行的文件内容)
     */
    @Override
	public List<String[]> Downloade(List<String[]> content,HttpServletRequest request) {    	
		//用于存储结果
        List<String[]> list = new ArrayList<String[]>();
        String[] attrs = content.remove(0);
        list.add(attrs);
        for (String[] strings : clusterResult) {
        	for (String string : strings) {
				int i = Integer.parseInt(string);
				String[] s = content.get(i);
				list.add(s);
			}
			list.add(new String[0]);
		}
        return list;
	}

    
    /**
     * 第一个参数为：去除属性后的文本
     * 聚类的结果
     * 属性列
     */
    @Override
    public List<String[]> storeResult(List<String[]> list,List<List<Integer>> result1, String[] attrs,HttpServletRequest request) 
    {
    	int indexOfTitle = AttrUtil.findIndexOfTitle(attrs);
		int indexOfUrl = AttrUtil.findIndexOfUrl(attrs);
		int indexOfTime = AttrUtil.findIndexOfTime(attrs);
		
    	clusterResult = ConvertUtil.toStringArrayListB(result1);
		countResult = miningService.getOrigCounts(attrs,list, clusterResult);
		List<String[]> displayResult = new ArrayList<String[]>();
		for (int[] row : countResult) {
			String[] old = list.get(row[0]);
			String[] sub = new String[] { old[indexOfTitle], old[indexOfUrl], old[indexOfTime],
					String.valueOf(row[1]) };
			displayResult.add(sub);
		}
		return displayResult;
    }
    /**
     * 获取聚类显示结果
     */
   /* @Override
    public List<String[]> getResult(List<String[]> content,String attrs,HttpServletRequest request) {
        // TODO Auto-generated method stub
    	List<String[]> list = new ArrayList<String[]>();
        try {
            for (int[] item : count) {
                String[] old = content.get(item[Index.COUNT_ITEM_INDEX]+1);
                String[] ne = new String[old.length + 1];
                System.arraycopy(old, 0, ne, 1, old.length);
                ne[0] = item[Index.COUNT_ITEM_AMOUNT] + "";
                list.add(ne);
            }
            list.add(0, AttrUtil.findEssentialIndex(content.get(0)));
        } catch (Exception e) {
            logger.error("get count result failed:{}", e.toString());
            return null;
        }
        return list;
    }*/

    
    /**
     * 对待聚类的数据进行分词和向量模型的转换
     * @param list
     * @param attrs
     * @param converterType
     * @return
     */
	@Override
	public List<double[]> Converter(List<String[]> list,String[] attrs ,int converterType) {
		int titleIndex = AttrUtil.findIndexOfTitle(attrs);
		//对标题列进行分词。
        List<String[]> segmentList = segmentService.getSegresult(list, titleIndex, 0);
        Convertor convertor = null;
        //判断选择的向量模型的类型
        if (converterType == Algorithm.DIGITAL) {
            convertor = new DigitalConvertor();
        } else if (converterType == Algorithm.TFIDF) {
            convertor = new TFIDFConvertor();
        }
        convertor.setList(segmentList);
        List<double[]> vectors = convertor.getVector();
    	return vectors;
	}

	 /**
     * 对聚类结构进行排序
     * @param list  带聚类文本
     * @param resultIndexSetList   未排序的聚类结果
     * @return
     */
	@Override
	public List<List<Integer>> Sort(List<String[]> list, List<List<Integer>> resultIndexSetList, int indexOfTitle, int indexOfTime) {
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
                	//判断他们的标题是否相同
                    int compare = list.get(o1)[indexOfTitle].compareTo(list.get(o2)[indexOfTitle]);
                    //若不相同，使用时间进行排序。
                    if (compare == 0) {
                        compare = list.get(o1)[indexOfTime].compareTo(list.get(o2)[indexOfTime]);
                    }
                    return compare;
                }
            });
        }
        return resultIndexSetList;
	}

	 /**
     * 使用Kmeans算法
     * @param list  待聚类字符串
     * @param k     聚类中心个数
     * @param granularity    聚类粒度
     * @return
     */
	@Override
	public List<String[]> UseKmeans(List<String[]> list, int k, int granularity ,HttpServletRequest request) {
		//存放最后用于显示的聚类结果
		List<String[]> displayResult = new ArrayList<String[]>();
		//用于存放结果
    	List<List<Integer>> resultIndexSetList = new ArrayList<List<Integer>>();
    	//移除属性行
    	String[] attrs = list.remove(0);
    	
    	int indexOfTitle = AttrUtil.findIndexOfTitle(attrs);
    	int indexOfTime = AttrUtil.findIndexOfTime(attrs);
    	//向量转换完成
        List<double[]> vectors = Converter(list,attrs,1);
        //System.out.println("使用的是KMEANS");
        KMeans kmeans = new KMeans();
        kmeans.setVectors(vectors);
        System.out.println(vectors.size());
        kmeans.setIterationTimes(20);
        //相似度方式的选择
        if (granularity == Algorithm.AcrossSimilarity) {
        kmeans.setSimi(new AcrossSimilarity(vectors)); 
      	  //System.out.println("选择的是粗粒度AcrossSimilarity");
		
        }if(granularity == Algorithm.CosSimilarity){
       	  kmeans.setSimi(new CosSimilarity(vectors));
		  //System.out.println("选择的是细粒度CosSimilarity");
        }
        kmeans.setK(k);
        ExecutorService exec = Executors.newSingleThreadExecutor();
        Future<List<List<Integer>>> future1 = exec.submit(kmeans);
        try {
        	//得到聚类结果     
            resultIndexSetList = future1.get();
        } catch (Exception e) {
            logger.error("error occur during clustering by canopy" + e.toString());
            return null;
        }        
       List<List<Integer>> result= Sort(list, resultIndexSetList,indexOfTitle,indexOfTime);
      //去除长度为0 的类簇
      int start = 0;
       
      for(int i = 0; i < result.size();i++)
      {
    	  List<Integer> list2 = result.get(i);
    	  if (list2.size()==0) {
    		start = i;
    		break;
		 } 	  
      }
  	  List<List<Integer>> result1 = new ArrayList<List<Integer>>();
  	  if (start==0) 
  	  {
  		displayResult = storeResult(list, result, attrs, request);
	  }
  	  else{
  		  
  		 for(int i = 0; i < start; i++)
  	      {
  	    	 result1.add(result.get(i));
  	      }
  		displayResult = storeResult(list, result1, attrs, request);
  		  
  	  }
      return displayResult;
	}

	/**
	 * 使用Canopy聚类，阈值可设置
	 */
	@Override
	public List<String[]> UseCanopy(List<String[]> list, float Threshold, int granularity, HttpServletRequest request) {
		//存放最后用于显示的聚类结果
				List<String[]> displayResult = new ArrayList<String[]>();
		//用于存放结果
    	List<List<Integer>> resultIndexSetList = new ArrayList<List<Integer>>();
    	//移除属性行
    	String[] attrs = list.remove(0);
    	
    	int indexOfTitle = AttrUtil.findIndexOfTitle(attrs);
    	int indexOfTime = AttrUtil.findIndexOfTime(attrs);
    	//向量转换完成
        List<double[]> vectors = Converter(list,attrs, 1);
        //System.out.println("使用的是CANOPY");
   	    Canopy canopy = new Canopy();
        canopy.setVectors(vectors);
        //相似度方式的选择
        if (granularity == Algorithm.AcrossSimilarity) {
      	  canopy.setSimi(new AcrossSimilarity(vectors)); 
      	  //System.out.println("选择的是粗粒度AcrossSimilarity");
			
        } else if(granularity == Algorithm.CosSimilarity){
			  canopy.setSimi(new CosSimilarity(vectors));
			  //System.out.println("选择的是细粒度CosSimilarity");
        }
        //设置阀值
        canopy.setThreshold(Threshold);
        //开启线程池
        ExecutorService exec = Executors.newSingleThreadExecutor();
        Future<List<List<Integer>>> future = exec.submit(canopy);
        try {
        	//得到聚类结果
            resultIndexSetList = future.get();
        } catch (Exception e) {
            logger.error("error occur during clustering by canopy" + e.toString());
            return null;
        }
        List<List<Integer>> result= Sort(list, resultIndexSetList,indexOfTitle,indexOfTime);
        //System.out.println("result:");
        displayResult = storeResult(list, result, attrs, request);
        return displayResult;
	}

	/**
     * 使用DBScan算法
     * @param list  带聚类文本
     * @param Eps   半径
     * @param MinPts  最少的个数
     * @param granularity  粒度
     * @return
     */
	@Override
	public List<String[]> UseDBScan(List<String[]> list, float Eps, int MinPts, int granularity,HttpServletRequest request) {
		//存放最后用于显示的聚类结果
				List<String[]> displayResult = new ArrayList<String[]>();
		//用于存放结果
    	List<List<Integer>> resultIndexSetList = new ArrayList<List<Integer>>();
    	//移除属性行
    	String[] attrs = list.remove(0);
    	
    	int indexOfTitle = AttrUtil.findIndexOfTitle(attrs);
    	int indexOfTime = AttrUtil.findIndexOfTime(attrs);
    	//向量转换完成
        List<double[]> vectors = Converter(list, attrs,1);
        //System.out.println("使用的是DBSCAN");
   	    DBScan dbscan = new DBScan();
   	    dbscan.setVectors(vectors);
        if (granularity == Algorithm.AcrossSimilarity) {
       	  dbscan.setSimi(new AcrossSimilarity(vectors)); 
         	  //System.out.println("选择的是粗粒度AcrossSimilarity");
 			
           } else if(granularity == Algorithm.CosSimilarity){
             dbscan.setSimi(new CosSimilarity(vectors));
 			  //System.out.println("选择的是细粒度CosSimilarity");
           }
        //设置阀值
        dbscan.setMinPts(MinPts);
        dbscan.setEps(Eps);
        //开启线程池
        ExecutorService exec = Executors.newSingleThreadExecutor();
        Future<List<List<Integer>>> future = exec.submit(dbscan);
        try {
        	//得到聚类结果
            resultIndexSetList = future.get();
        } catch (Exception e) {
            logger.error("error occur during clustering by canopy" + e.toString());
            return null;
        }
        List<List<Integer>> result= Sort(list, resultIndexSetList,indexOfTitle,indexOfTime);
        displayResult = storeResult(list, result, attrs, request);
        	return displayResult;
	}

}
