package com.hust.scdx.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;

import com.hust.scdx.constant.Constant.Interval;
import com.hust.scdx.constant.Constant.KEY;

public class TimeUtil {

	public static String convert(Cell cell) {
		if (cell == null)
			return "1900-01-01 00:00:00";
		if (DateUtil.isCellDateFormatted(cell)) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return sdf.format(DateUtil.getJavaDate(cell.getNumericCellValue())).toString();
		}
		return new DecimalFormat("#").format(cell.getNumericCellValue());
	}

	public static boolean isvalidate(String time) {
		String regex = "^\\d{4}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D*$";
		return Pattern.matches(regex, time);
	}

	public static String DateToStr(Date date) {
		if (null == date) {
			return StringUtils.EMPTY;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
		String dateStr = sdf.format(date);
		return dateStr;
	}

	public static Date getSystemDate() throws ParseException {
		Date now = new Date();
		return now;
	}
	/**
	 * 
	 * @param DATE1
	 * @param DATE2
	 * @return 1 if DATE1(后) > DATE2(前)
	 * @return -1 if DATE1(前) < DATE2(后)
	 * @return 0 if DATE1 = DATE2 
	 */
	public static int compare_date(String DATE1, String DATE2) {        
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if (dt1.getTime() > dt2.getTime()) {                
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {              
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

	/**
	 * 获取出图需要的时间维度
	 * @param time 时间 yyyy-mm-dd hh:MM:ss
	 * @param interval
	 * @return
	 */
	public static String getTimeKey(String time, int interval) {
		if (StringUtils.isBlank(time) || !isvalidate(time)) {
			return KEY.INVALID_TIME;
		}
		switch (interval) {
		case Interval.HOUR: {
			return time.substring(0, 13);
		}
		case Interval.DAY: {
			return time.substring(0, 10);
		}
		case Interval.MONTH: {
			return time.substring(0, 7);
		}
		default: {
			return KEY.INVALID_TIME;
		}
		}
	}
}
