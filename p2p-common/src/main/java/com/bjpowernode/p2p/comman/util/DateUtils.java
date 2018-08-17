package com.bjpowernode.p2p.comman.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author 宋艾衡 on 2018/8/14 下午11:38
 */
public class DateUtils {

	public static Date getDateByAddDays(Date date, Integer days) {
		Calendar calendar = Calendar.getInstance ();
		calendar.setTime ( date );
		calendar.add ( Calendar.DAY_OF_MONTH, days );
		return calendar.getTime ();
	}


	public static Date getDateByAddMonth(Date date, Integer mouth) {
		Calendar calendar = Calendar.getInstance ();
		calendar.setTime ( date );
		calendar.add ( Calendar.MONTH, mouth );
		return calendar.getTime ();
	}

	public static int getDaysByYear(int year){
		int days = 365;
		if (year % 4 == 0 && year % 100 !=0 || year % 400 == 0){
			days = 366;
		}

		return days;
	}

	public static int getDistanceBetweenDate(Date endDate, Date startDate) {
		int distance = (int) ((endDate.getTime () - startDate.getTime ()) / (24 * 60 * 60 * 1000));
		int mod = (int) ((endDate.getTime () - startDate.getTime ()) % (24 * 60 * 60 * 1000));
		if (mod > 0){
			distance += 1;
		}

		return distance;
	}

	public static String getTimeStamp(){
		return new SimpleDateFormat ( "yyyyMMddHHmmssSSS" ).format ( new Date () );
	}


}
