package com.hust.scdx.util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class crawler {
    
  //爬取法制网、、司法部
	/**
	 * 爬取法制网、、司法部
	 * @param url
	 * @return
	 * @throws IOException
	 */
    private static List<String> getContent(String url){  
    	
    	List<String> list = new ArrayList<>();
        // TODO Auto-generated method stub          
    	 Document doc;  
         try {
			doc=Jsoup.connect(url).get();
			//正文
	         Elements pElements=doc.select("p");
	         for (Element element : pElements) {
	        	String str = trim(element.text());
	         	if(StringUtils.isNotBlank(str)){
	         		list.add(str); 
	         	} 
	 			//System.out.println(element.text());
	 		}
		} catch (IOException e) {
			return null;
		} 
 		
 		return list;          
    }  
    
  //爬取四川法制网，四川法制报（ http://dzb.scfzbs.com ）
    /**
     * 爬取四川法制网，四川法制报（ http://dzb.scfzbs.com ）
     * @param url
     * @return
     * @throws IOException
     */
    private static List<String> getContent1(String url){ 
    	
    	List<String> list = new ArrayList<>();
        // TODO Auto-generated method stub          
    	 Document doc;
         try {
			doc=Jsoup.connect(url).get();
			Elements pElements=doc.select("p");
			for (Element element : pElements) {
	 			String pString = element.text();
	 			String[] ss = pString.split(" ");
	 			for (String string : ss) {
	 				String str = trim(string);
	 	        	if(StringUtils.isNotBlank(str)){
	 	        		list.add(str); 
	 	        	} 
					//System.out.println(string);
	 			}
			}
				
		} catch (IOException e) {
			return null;
		} 
 		//正文
 		return list;          
    }  
    
    //爬取人民网
    /**
     * 爬取人民网
     * @param url
     * @return
     * @throws IOException
     */
    private static List<String> getContent2(String url){  
    	
    	List<String> list = new ArrayList<>();
        // TODO Auto-generated method stub          
        Document doc;  
        try {
			doc=Jsoup.connect(url).get();
			Elements pElements=doc.select("div.box_con>p");
	        for (Element element : pElements) {
	        	String str = trim(element.text());
	        	if(StringUtils.isNotBlank(str)){
	        		list.add(str); 
	        	} 
				//System.out.println(element.text());
			}
		} catch (IOException e) {
			return null;
		} 
		return list;          
    }  
    
  //爬取四川长安网
    /**
     * 爬取四川长安网
     * @param url
     * @return
     * @throws IOException
     */
    private static List<String> getContent3(String url){
    	
    	List<String> list = new ArrayList<>();
        // TODO Auto-generated method stub          
        Document doc;  
        try {
			doc=Jsoup.connect(url).get();
			//正文
	        Elements pElements=doc.select("td.black14>div");
	        for (Element element : pElements) {
	        	String str = trim(element.text());
	        	if(StringUtils.isNotBlank(str)){
	        		list.add(str); 
	        	}
				//System.out.println(element.text());
			}
		} catch (IOException e) {
			return null;
		} 
		return list;          
    }  

  //爬取大众日报  paper.dzwww.com
    /**
     * 爬取大众日报
     * @param url
     * @return
     * @throws IOException
     */
    private static List<String> getContent4(String url){
    	
    	List<String> list = new ArrayList<>();
        // TODO Auto-generated method stub          
        Document doc;  
        try {
			doc=Jsoup.connect(url).get();
			//正文
	        Elements pElements=doc.select("span[id=contenttext]");
	        for (Element element : pElements) {
	        	String str = trim(element.toString());
	        	for (String s : str.split("<br>")) {
	        		if(!StringUtils.isBlank(s) && s.contains("。")){
		        		list.add(trim(s)); 
		        	}
				}	        	
				//System.out.println(element.text());
			}
	        list.remove(0);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} 
		return list;          
    }
    
    //爬取齐鲁网  paper.dzwww.com
    /**
     * 爬取齐鲁网
     * @param url
     * @return
     * @throws IOException
     */
    private static List<String> getContent5(String url){
    	
    	List<String> list = new ArrayList<>();
        Document doc;  
        try {
			doc=Jsoup.connect(url).get();
			//正文
	        Elements pElements=doc.select("div.article-main>p");
	        for (Element element : pElements) {
	        	String str = trim(element.text());
	        	if(!StringUtils.isBlank(str)&& str.contains("。")){
		        	list.add(str); 
				}	        	
				//System.out.println(element.text());
			}
	        list.remove(list.size()-1);
		} catch (IOException e) {
			return null;
		} 
		return list;          
    }
  //爬取泸州网  news.lzep.cn
    /**
     * 爬取泸州网
     * @param url
     * @return
     * @throws IOException
     */
    private static List<String> getContent6(String url){
    	
    	List<String> list = new ArrayList<>();
        // TODO Auto-generated method stub          
        Document doc;  
        try {
			doc=Jsoup.connect(url).get();
			//正文
	        Elements pElements=doc.select("div.article-content>p");
	        for (Element element : pElements) {
	        	String str = trim(element.text());
	        	if(!StringUtils.isBlank(str) && str.contains("。")){
		        	list.add(str); 
				}	        	
				//System.out.println(element.text());
			}
		} catch (IOException e) {
			return null;
		} 
		return list;          
    }
    
  //爬取司法网  moj.gov.cn
    /**
     * 爬取司法部
     * @param url
     * @return
     * @throws IOException
     */
    private static List<String> getContent7(String url){
    	
    	List<String> list = new ArrayList<>();
        // TODO Auto-generated method stub          
        Document doc;  
        try {
			doc=Jsoup.connect(url).get();
			//正文
	        Elements pElements=doc.select("div.con>span>p");
	        for (Element element : pElements) {
	        	String str = trim(element.text());
	        	if(!StringUtils.isBlank(str) && str.contains("。")){
		        	list.add(str); 
				}	        	
				//System.out.println(element.text());
			}
		} catch (IOException e) {
			return null;
		} 
		return list;          
    }
    
    public static List<String> getSummary(String url){
    	List<String> list = new ArrayList<>();
    	switch (UrlUtil.getUrl(url)) {
		case "legaldaily.com.cn":		
				list = getContent(url);
			break;
		case "dzb.scfzbs.com":
			list = getContent1(url);
			break;
		case "scfz.org":
			list = getContent1(url);
			break;
		case "politics.people.com.cn":
			list = getContent2(url);
			break;
		case "sichuanpeace.gov.cn":
			list = getContent3(url);
			break;
		case "paper.dzwww.com":
			list = getContent4(url);
			break;
		case "news.iqilu.com":
			list = getContent5(url);
			break;
		case "news.lzep.cn":
			list = getContent6(url);
			break;
		case "moj.gov.cn":
			list = getContent7(url);
			break;
		default:
			return null;
		}		
    	return list;
    }
    
  /*  public static void main(String[] args) throws Exception {

    	//法制网
    	getContent("http://www.legaldaily.com.cn/index_article/content/2017-06/11/content_7200047.htm?node=5955");
    	//四川法制报
    	getContent1("http://dzb.scfzbs.com/shtml/scfzb/20170609/50659.shtml");
    	//四川法制网
     	getContent1("http://www.scfz.org/news/html/71-30/30048.htm");
    	//人民网
    	getContent2("http://politics.people.com.cn/n1/2017/0610/c1001-29331177.html");       
    	//四川长安网
    	getContent3("http://www.sichuanpeace.gov.cn/system/20170605/000451520.html");
    }*/
    
    private static String trim(String str){
    	str = str.replace((char) 12288, ' ');
    	str = str.trim();
		if (!str.isEmpty() && !str.equals("") && !str.equals(' ')
				&& !str.equals("	") && !str.equals("  ")
				&& !str.equals("   ") && !str.equals("    ")
				&& !str.equals("     ")) {
			return str;
		}    	
    	return null;
    }

}