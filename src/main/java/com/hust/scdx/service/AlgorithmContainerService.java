package com.hust.scdx.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

public interface AlgorithmContainerService {

	  public List<double[]> Converter(List<String[]> list,String[] attrs , int converterType);
	  
	  public List<List<Integer>> Sort(List<String[]> list, List<List<Integer>> resultIndexSetList,int indexOfTitle, int indexOfTime);
	  
	  public List<String[]> UseKmeans(List<String[]> list, int k,int granularity, HttpServletRequest request);
	  
	  public List<String[]> Downloade(List<String[]> content,HttpServletRequest request);

	  List<String[]> storeResult(List<String[]> list, List<List<Integer>> result1, String[] attrs, HttpServletRequest request);

//	  List<String[]> getResult(List<String[]> content,String attrs,HttpServletRequest request);

	  List<String[]> UseDBScan(List<String[]> list, float Eps, int MinPts, int granularity, HttpServletRequest request);

	  List<String[]> UseCanopy(List<String[]> list, float Threshold, int granularity, HttpServletRequest request);
}
