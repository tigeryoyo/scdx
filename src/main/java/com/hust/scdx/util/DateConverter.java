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

	/**
	 * 存储路径 例如 2017/07/
	 * 
	 * @param date
	 * @return
	 */
	public static String convertToPath(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMM");
		try {
			String res = simpleDateFormat.format(date);
			return res.substring(0, 4) + "/" + res.substring(4, 6) + "/";
		} catch (Exception e) {
			logger.error("转换日期错误。");
		}
		return Constant.UNKNOWN;
	}

	/**
	 * 用于生成聚类后结果文件名
	 * 
	 * @param date
	 * @return
	 */
	public static String convert(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHH");
		try {
			return simpleDateFormat.format(date);
		} catch (Exception e) {
			logger.error("转换日期错误。");
		}
		return Constant.UNKNOWN;
	}
}
