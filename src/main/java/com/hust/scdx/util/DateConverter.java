package com.hust.scdx.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;

import com.hust.scdx.constant.Constant;

public class DateConverter implements Converter<String, Date> {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(DateConverter.class);

    @Override
    public Date convert(String source) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return simpleDateFormat.parse(source);
        } catch (Exception e) {
            logger.error("convert str to date error");
        }
        return null;
    }

    public static String parseYear(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        try {
            return simpleDateFormat.format(date);
        } catch (Exception e) {
            logger.error("转换日期错误。");
        }
        return Constant.UNKNOWN;
    }
    
    public static String parseMonth(Date date) {
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM");
    	try {
    		return simpleDateFormat.format(date);
    	} catch (Exception e) {
    		logger.error("转换日期错误。");
    	}
    	return Constant.UNKNOWN;
    }
    
    public static String parseMdH(Date date){
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMddHH");
    	try {
    		return simpleDateFormat.format(date);
    	} catch (Exception e) {
    		logger.error("转换日期错误。");
    	}
    	return Constant.UNKNOWN;
    }

}
