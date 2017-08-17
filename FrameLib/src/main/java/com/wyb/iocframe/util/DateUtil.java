package com.wyb.iocframe.util;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 閺冦儲婀￠幙宥勭稊瀹搞儱鍙跨猾锟�.
 * @author Liuj
 */

@SuppressLint("SimpleDateFormat")
public class DateUtil {

	private static final String FORMAT = "yyyy-MM-dd HH:mm:ss";

	public static Date str2Date(String str) {
		return str2Date(str, null);
	}

	public static Date str2Date(String str, String format) {
		if (str == null || str.length() == 0) {
			return null;
		}
		if (format == null || format.length() == 0) {
			format = FORMAT;
		}
		Date date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			date = sdf.parse(str);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;

	}

	public static Calendar str2Calendar(String str) {
		return str2Calendar(str, null);

	}

	public static Calendar str2Calendar(String str, String format) {

		Date date = str2Date(str, format);
		if (date == null) {
			return null;
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);

		return c;

	}

	public static String date2Str(Calendar c) {// yyyy-MM-dd HH:mm:ss
		return date2Str(c, null);
	}

	public static String date2Str(Calendar c, String format) {
		if (c == null) {
			return null;
		}
		return date2Str(c.getTime(), format);
	}

	public static String date2Str(Date d) {// yyyy-MM-dd HH:mm:ss
		return date2Str(d, null);
	}

	public static String date2Str(Date d, String format) {// yyyy-MM-dd HH:mm:ss
		if (d == null) {
			return null;
		}
		if (format == null || format.length() == 0) {
			format = FORMAT;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String s = sdf.format(d);
		return s;
	}

	public static String getCurDateStr() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		return c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-"
				+ c.get(Calendar.DAY_OF_MONTH) + "-"
				+ c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE)
				+ ":" + c.get(Calendar.SECOND);
	}

	/**
	 * 閼惧嘲绶辫ぐ鎾冲閺冦儲婀￠惃鍕摟缁楋缚瑕嗛弽鐓庣础
	 * @param format
	 * @return
	 */
	public static String getCurDateStr(String format) {
		Calendar c = Calendar.getInstance();
		return date2Str(c, format);
	}

	// 閺嶇厧绱￠崚鎵潡
	public static String getMillon(long time) {

		return new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(time);

	}

	// 閺嶇厧绱￠崚鏉裤亯
	public static String getDay(long time) {

		return new SimpleDateFormat("yyyy-MM-dd").format(time);

	}

	// 閺嶇厧绱￠崚鐗堫嚑缁夛拷
	public static String getSMillon(long time) {
		return new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS").format(time);
	}
	
	/**
	 * 鐎涙顑佹稉鑼舵祮閺冦儲婀�
	 * @param	timeStr	闂囷拷鐟曚浇娴嗛幑銏㈡畱閺冨爼妫跨�涙顑佹稉锟�
	 * @param	format	闂囷拷鐟曚浇娴嗛幑銏″灇閻ㄥ嫭鐗稿锟�
	 * @return	閺冦儲婀�
	 */
	public static Date strToDate(String timeStr,String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = sdf.parse(timeStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 鐏忓棛鏁撻弮銉ㄦ祮閹广垺鍨氶獮鎾窞
	 * @return
	 */
	public static String getAge(String birthdayStr){
		Date birthday = strToDate(birthdayStr,"yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		if(cal.before(birthday)){
			return "";
		}
		int yearNow = cal.get(Calendar.YEAR);
		int monthNow = cal.get(Calendar.MONTH)+1;
		int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
		cal.setTime(birthday);
		int yearBirth = cal.get(Calendar.YEAR);
		int monthBirth = cal.get(Calendar.MONTH)+1;
		int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
		
		int age = yearNow - yearBirth;
		if(monthNow <= monthBirth){
			if(monthNow == monthBirth){
				if(dayOfMonthNow < dayOfMonthBirth){
					age--;
				}
			}else {
				age--;
			}
		}
		return (++age)+"";
	}

	
	public static String longToDateTime(Long mm,String format){
		
		Date date=new Date(mm*1000);
		String strs = "";
		SimpleDateFormat sdf=new SimpleDateFormat(format);
		strs=sdf.format(date);
		return strs ; 
		
//		
//		SimpleDateFormat sdf=new SimpleDateFormat(format);
//		String dateString = mm+"";
//		Date date  = null; 
//		try {
//			 date = sdf.parse(dateString);
//			
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	public static long DateTime2Long(String timeStr,String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = sdf.parse(timeStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date.getTime();
	}
	
	/**
	 * 妤犲矁鐦夐弻鎰嚋閺冨爼妫块弰顖氭儊濮ｆ棁绶濋弮鍫曟？閻拷24鐏忓繑妞傞崥锟�
	 * @param time
	 * @return
	 */
	public static boolean after24Hours(String time,String format,long compileTime){
		Date date = str2Date(time,format);
		long mtime = date.getTime();
		long curTime = compileTime;
		long time24 = 24*60*60*1000;
		if(mtime - curTime < time24){
			return false;
		}
		return true;
	}
	
	/**
	 * 閸掋倖鏌囬弮鍫曟？閺勵垰鎯佹径褌绨ぐ鎾冲閺冨爼妫�
	 * @param time
	 * @param format
	 * @param compileTime
	 * @return
	 */
	public static boolean afterCurTime(String time,String format,long compileTime){
		Date date = str2Date(time,format);
		long mtime = date.getTime();
		long curTime = compileTime;
//		long time24 = 24*60*60*1000;
		if(mtime < curTime){
			return false;
		}
		return true;
	}
	
	/**
	 * 鐏忓棛绮扮�规碍妞傞梻纾嬫祮閹诡澀璐� XX閸掑棝鎸撻崜宥忕礉XX鐏忓繑妞傞崜宥忕礉XX婢垛晛澧犻敍瀛筙閺堝牆澧犻敍瀛筙楠炴潙澧�
	 * @param timeStr	閸掑棝鎸撻弫锟�
	 * @return
	 */
	public static String getBeforeTime(String timeStr){
		int minutes = Integer.valueOf(timeStr);
		String backTime = "";
		if(minutes <= 0){
			backTime = "閸掓艾鍨�";
		}else if(minutes > 0 && minutes < 60){
			backTime = minutes + "閸掑棝鎸撻崜锟�";
		}else if(minutes >= 60 && minutes < 60*24){
			backTime = ((int)minutes/60) + "鐏忓繑妞傞崜锟�";
		}else if(minutes >= 60*24 &&  minutes < 24*60*30){
			backTime = ((int)minutes/(24*60)) + "婢垛晛澧�";
		}else if(minutes >= 60*24*30 && minutes < 60*24*30*12){
			backTime = ((int)minutes/(60*24*30)) + "閺堝牆澧�";
		}else {
			backTime = ((int)minutes/(60*24*30*12)) + "楠炴潙澧�";
		}
		return backTime;
	}
	/**
	 * 闁稓瀚� 鐠ч绨℃径姘毌閺冨爼妫�
	 * */
	public static String getHowLongTime(String timeStr){
		int minutes = Integer.valueOf(timeStr);
		String backTime = "";
		if(minutes <= 0){
			backTime = "閸掓艾鍨�";
		}else if(minutes > 0 && minutes < 60){
			backTime = minutes + "閸掑棝鎸�";
		}else if(minutes >= 60 && minutes < 60*24){
			backTime = ((int)minutes/60) + "鐏忓繑妞�";
		}else if(minutes >= 60*24 &&  minutes < 24*60*30){
			backTime = ((int)minutes/(24*60)) + "婢讹拷";
		}else if(minutes >= 60*24*30 && minutes < 60*24*30*12){
			backTime = ((int)minutes/(60*24*30)) + "閺堬拷";
		}else {
			backTime = ((int)minutes/(60*24*30*12)) + "楠烇拷";
		}
		return backTime;
	}
	
	/**
	 * 闁俺绻冮弮銉︽埂閺嶇厧绱￠惃鍕摟缁楋缚瑕嗛懢宄板絿鐠烘繄顬囪ぐ鎾冲閺冨爼妫�
	 * @param dateTime	yyyy-MM-dd HH:mm
	 * @return
	 */
	public static String getBeforeTimeFromDateTime(String dateTime){
		String beforeTime = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			//TODO
			Date date = sdf.parse(dateTime);
			long hm = date.getTime();
			//鐏忓棙顕犵粔鎺曟祮娑撳搫鍨庨柦锟�
			int oneMinute = 1000*60;
			long curhm = System.currentTimeMillis();
			
			long minutes = curhm/oneMinute - hm/oneMinute;
			
			beforeTime = getBeforeTime(minutes+"");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return beforeTime;
	}
}
