package com.italia.ipos.utils;
/**
 * 
 * @author mark italia
 * @since 09/28/2016
 *
 */
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
/**
 * 
 * @author mark italia
 * @since 09/28/2016
 * @version 1.0
 *
 */
public class DateUtils {

	/**
	 * 	
	 * @return current date
	 * @format MMMM dd, yyyy (eg January 01, 2016)
	 */
	public static String getCurrentDateMMMMDDYYYY(){
		DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	/**
	 * 	
	 * @return current date
	 * @format MM-dd-yyyy
	 */
	public static String getCurrentDateMMDDYYYY(){//MMMM d, yyyy
		DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");//new SimpleDateFormat("MM/dd/yyyy");//new SimpleDateFormat("yyyy/MM/dd hh:mm: a");
		Date date = new Date();
		return dateFormat.format(date);
	}
	/**
	 * 	
	 * @return current date
	 * @format yyyy-MM-dd
	 */
	public static String getCurrentDateYYYYMMDD(){//MMMM d, yyyy
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		return dateFormat.format(date);
	}
	/**
	 * 	
	 * @return current date
	 * @format MMddyyyy
	 */
	public static String getCurrentDateMMDDYYYYPlain(){
		DateFormat dateFormat = new SimpleDateFormat("MMddyyyy");
		Date date = new Date();
		return dateFormat.format(date);
	}
	/**
	 * 	
	 * @return current date with time
	 * @format MM-dd-yyyy hh:mm:ss a
	 */
	public static String getCurrentDateMMDDYYYYTIME(){//MMMM d, yyyy
		DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss a");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	public static String getCurrentDateMMDDYYYYTIMEPlain(){//MMMM d, yyyy
		DateFormat dateFormat = new SimpleDateFormat("MMddyyyyhhmmss");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	/**
	 * 
	 * @param dateFormat (eg. mm-dd-yyyy)
	 * @param locale (eg. Locale.TAIWAN/Locale.US)
	 * @return last date of the month(eg. 31)
	 * date
	 */
	public static String getEndOfMonthDate(String dateFormat, Locale locale){
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat, locale);
		LocalDate now = LocalDate.parse(DateUtils.getCurrentDateMMDDYYYY(), dateTimeFormatter);
		LocalDate lastDay = now.with(TemporalAdjusters.lastDayOfMonth()); //2015-11-30
		
		String mm="",dd="",yyyy="",endOfMonth=lastDay.atStartOfDay().toString().split("T")[0];
		yyyy=endOfMonth.split("-")[0];
		mm=endOfMonth.split("-")[1];
		dd=endOfMonth.split("-")[2];
		
		return mm + "-" + dd + "-" + yyyy;
	}
	
	/**
	 * 
	 * @param dateFormat 1=MM-dd-yyyy 2=yyyy-MM-dd 3=dd-MM-yyyy
	 * @param locale
	 * @return dateFormat
	 */
	public static String getEndOfMonthDate(int dateFormat, Locale locale){
		String datePatern = "yyyy-MM-dd";
		if(dateFormat==1){
			datePatern = "MM-dd-yyyy";
		}else if(dateFormat==2){
			datePatern = "yyyy-MM-dd";
		}else if(dateFormat==3){
			datePatern = "dd-MM-yyyy";
		}	
		
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(datePatern, locale);
		LocalDate now = LocalDate.parse(DateUtils.getCurrentDateMMDDYYYY(), dateTimeFormatter);
		LocalDate lastDay = now.with(TemporalAdjusters.lastDayOfMonth()); //2015-11-30
		
		String mm="",dd="",yyyy="",endOfMonth=lastDay.atStartOfDay().toString().split("T")[0];
		yyyy=endOfMonth.split("-")[0];
		mm=endOfMonth.split("-")[1];
		dd=endOfMonth.split("-")[2];
		
		if(dateFormat==1){
			return mm + "-" + dd + "-" + yyyy;
		}else if(dateFormat==2){
			return yyyy + "-" + mm + "-" + dd;
		}else if(dateFormat==3){
			datePatern = "dd-MM-yyyy";
			return dd + "-" + mm + "-" + yyyy;
		}
		
		return yyyy + "-" + mm + "-" + dd;
	}
	
	public static String getLastDayOfTheMonth(String dateFormat,String dateInputed, Locale locale){
		String date="";
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat, locale);
		LocalDate now = LocalDate.parse(dateInputed, dateTimeFormatter);
		LocalDate initial = now.of(getCurrentYear(), getCurrentMonth(), 13);
		//LocalDate start = initial.withDayOfMonth(1);
		LocalDate end = initial.withDayOfMonth(initial.lengthOfMonth());
		return end.toString();
	}
	
	/**
	 * Convert 24 format hour to 12 format
	 * @param hour
	 * @return
	 */
	public static String timeTo12Format(String time, boolean isIncludePM){
		int hh=Integer.valueOf(time.split(":")[0]);
		String mm = time.split(":")[1];
		String ss = time.split(":")[2];
		String result = isIncludePM? "00:"+ mm +":"+ ss +" PM" : "00:"+ mm +":"+ ss;
		
		if(hh<=12){
			if(hh<=9){
				result = isIncludePM?  "0"+hh+":"+ mm +":"+ ss +" AM" : "00:"+ mm +":"+ ss;
			}else{
				result = isIncludePM?  hh+":"+ mm +":"+ ss +" AM" : "00:"+ mm +":"+ ss;
			}
		}
		switch(hh){
			case 13 : result = isIncludePM? "01:"+ mm +":"+ ss +" PM" : "01:"+ mm +":"+ ss;
			case 14 : result = isIncludePM? "02:"+ mm +":"+ ss +" PM" : "02:"+ mm +":"+ ss;
			case 15 : result = isIncludePM? "03:"+ mm +":"+ ss +" PM" : "03:"+ mm +":"+ ss;
			case 16 : result = isIncludePM? "04:"+ mm +":"+ ss +" PM" : "04:"+ mm +":"+ ss;
			case 17 : result = isIncludePM? "05:"+ mm +":"+ ss +" PM" : "05:"+ mm +":"+ ss;
			case 18 : result = isIncludePM? "06:"+ mm +":"+ ss +" PM" : "06:"+ mm +":"+ ss;
			case 19 : result = isIncludePM? "07:"+ mm +":"+ ss +" PM" : "07:"+ mm +":"+ ss;
			case 20 : result = isIncludePM? "08:"+ mm +":"+ ss +" PM" : "08:"+ mm +":"+ ss;
			case 21 : result = isIncludePM? "09:"+ mm +":"+ ss +" PM" : "09:"+ mm +":"+ ss;
			case 22 : result = isIncludePM? "10:"+ mm +":"+ ss +" PM" : "10:"+ mm +":"+ ss;
			case 23 : result = isIncludePM? "11:"+ mm +":"+ ss +" PM" : "11:"+ mm +":"+ ss;
			case 00 : result = isIncludePM? "12:"+ mm +":"+ ss +" PM" : "12:"+ mm +":"+ ss;
		}
		return result;
	}
	
	/**
	 * 
	 * @param dateVal YYYY-MM-DD
	 * @return Month day, Year
	 */
	public static String convertDateToMonthDayYear(String dateVal){
		//System.out.println("Date : " + dateVal);
		if(dateVal==null || dateVal.isEmpty()){
			dateVal = DateUtils.getCurrentDateYYYYMMDD();
		}
		int month = Integer.valueOf(dateVal.split("-")[1]); 
		String year = dateVal.split("-")[0];
		int day = Integer.valueOf(dateVal.split("-")[2]);
		
		if(day<10){
			dateVal = getMonthName(month) + " 0"+day + ", " + year;
		}else{
			dateVal = getMonthName(month) + " "+day + ", " + year;
		}
		//System.out.println("return date: "+ dateVal);
		return dateVal;
	}
	
	/**
	 * 
	 * @param dateVal Month day, Year 
	 * @return YYYY-MM-DD
	 */
	public static String convertDateToYearMontyDay(String dateVal){
		//System.out.println("Date : " + dateVal);
		if(dateVal==null || dateVal.isEmpty()){
			dateVal = DateUtils.getCurrentDateMMMMDDYYYY();
		}
		String tmp = dateVal.split(",")[0];
		String month = tmp.split(" ")[0];
		String day = tmp.split(" ")[1];
		String year = dateVal.split(",")[1].trim();
		dateVal = year + "-" + getMonthNumber(month) + "-" + day;
		return dateVal;
	}
	
	public static String getMonthNumber(String month){
		switch(month){
			case "January": return "01";
			case "February" : return "02";
			case "March" : return "03";
			case "April" : return "04";
			case "May" : return "05";
			case "June" : return "06";
			case "July" : return "07";
			case "August" : return "08";
			case "September" :  return "09";
			case "October" : return "10";
			case "November" : return "11";
			case "December" : return "12";
		}
		return "January";
	}
	
	public static String getMonthName(int month){
		switch(month){
			case 1: return "January";
			case 2 : return "February";
			case 3 : return "March";
			case 4 : return "April";
			case 5 : return "May";
			case 6 : return "June";
			case 7 : return "July";
			case 8 : return "August";
			case 9 : return "September";
			case 10 : return "October";
			case 11 : return "November";
			case 12 : return "December";
		}
		return "January";
	}
	
	public static int getCurrentMonth(){
		java.util.Date date= new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int month = cal.get(Calendar.MONTH) + 1;//i dont know the reason but you have to add 1 in order to get the accurate month
		return month;
	}
	
	public static int getCurrentYear(){
		Calendar now = Calendar.getInstance();   // Gets the current date and time
		int year = now.get(Calendar.YEAR);      // The current year as an int
		return year;
	}
	
	/**
	 * 
	 * @param formatStye
	 * @param 1 [yyyy-MM-dd]
	 * @param 2 [MM-dd-yyyy]
	 * @Note applicable only for current month dateValue inputed
	 */
	public static String getDateBaseOnCount(int count, String dateValue, int formatStye){
		int dd=0,mm=0,yy=0;
		int ddd=0,mmm=0,yyy=0;
		String lastDay = "", timeValue;
		System.out.println("getDateBaseOnCount " + dateValue + " format " + formatStye);
		//timeValue = dateValue.split(" ")[1];
		dateValue = dateValue.split(" ")[0];
		
		if(formatStye==1){
			dd = Integer.valueOf(dateValue.split("-")[2]);
			mm = Integer.valueOf(dateValue.split("-")[1]);
			yy = Integer.valueOf(dateValue.split("-")[0]);
			
			lastDay = getLastDayOfTheMonth("yyyy-MM-dd", dateValue, Locale.TAIWAN);
			ddd = Integer.valueOf(lastDay.split("-")[2]);
			mmm = Integer.valueOf(lastDay.split("-")[1]);
			yyy = Integer.valueOf(lastDay.split("-")[0]);
			
			count -=1; //to get the actual date count including today
			
			int additionalDate =0;
			if(mm==getCurrentMonth()){
				
				additionalDate = dd + count;
				
				if(additionalDate<=ddd){ // determine if the additional date is not greater than end date of the month
					dateValue = yy + "-" + (mm<=9? "0"+mm : mm) + "-" + (additionalDate<=9? "0"+ additionalDate : additionalDate);
				}else{
					
					dd = additionalDate - ddd;
					mm +=1; // add plus 1 to get the next month
					dateValue = yy + "-" + (mm<=9? "0"+mm : mm) + "-" + (dd<=9? "0"+ dd : dd);
				}
				
			}
			
		}else if(formatStye==2){
			mm = Integer.valueOf(dateValue.split("-")[0]);
			dd = Integer.valueOf(dateValue.split("-")[1]);
			yy = Integer.valueOf(dateValue.split("-")[2]);
			
			lastDay = getLastDayOfTheMonth("MM-dd-yyyy", dateValue, Locale.TAIWAN);
			
			mmm = Integer.valueOf(lastDay.split("-")[0]);
			ddd = Integer.valueOf(lastDay.split("-")[1]);
			yyy = Integer.valueOf(lastDay.split("-")[2]);
			
			int additionalDate =0;
			if(mm==getCurrentMonth()){
				
				additionalDate = dd + count;
				
				if(additionalDate<=ddd){ // determine if the additional date is not greater than end date of the month
					dateValue = yy + "-" + (mm<=9? "0"+mm : mm) + "-" + (additionalDate<=9? "0"+ additionalDate : additionalDate);
				}else{
					
					dd = additionalDate - ddd;
					mm +=1; // add pluse 1 to get the next month
					dateValue = yy + "-" + (mm<=9? "0"+mm : mm) + "-" + (dd<=9? "0"+ dd : dd);
				}
				
			}
			
		}
		
		
		return dateValue;
	}
	
	/**
	 * 	
	 * @return current date with time
	 * @format yyyy-MM-dd hh:mm:ss a
	 */
	public static String getCurrentDateYYYYMMDDTIME(){//MMMM d, yyyy
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	public static Date getDateFromString(String datevalue, String format){
		Date date = new Date();
		
		try{
		DateFormat dateFormat = new SimpleDateFormat(format);
		date = dateFormat.parse(datevalue);
		}catch(ParseException e){}
		
		return date;
	}
	
	/**
	 * Get a diff between two dates
	 * @param date1 the oldest date
	 * @param date2 the newest date
	 * @param timeUnit the unit in which you want the diff
	 * @return the diff value, in the provided unit
	 */
	public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
	    long diffInMillies = date2.getTime() - date1.getTime();
	    return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
	}

	public static long getRemainingDays(Date date1, Date date2, TimeUnit timeUnit) {
	    long diffInMillies = date2.getTime() - date1.getTime();
	    return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
	}
	
	public static Map<TimeUnit,Long> computeDiff(Date date1, Date date2) {
	    long diffInMillies = date2.getTime() - date1.getTime();
	    List<TimeUnit> units = new ArrayList<TimeUnit>(EnumSet.allOf(TimeUnit.class));
	    Collections.reverse(units);
	    Map<TimeUnit,Long> result = new LinkedHashMap<TimeUnit,Long>();
	    long milliesRest = diffInMillies;
	    for ( TimeUnit unit : units ) {
	        long diff = unit.convert(milliesRest,TimeUnit.MILLISECONDS);
	        long diffInMilliesForUnit = unit.toMillis(diff);
	        milliesRest = milliesRest - diffInMilliesForUnit;
	        result.put(unit,diff);
	    }
	    return result;
	}
	
	public static void main(String[] args) {
		//System.out.println(DateUtils.getCurrentYear());
		Date date1 = DateUtils.getDateFromString("2017-05-28", "yyyy-MM-dd");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date2 = new Date();
		System.out.println("Days Remaining: " + DateUtils.getDateDiff(date1, date2, TimeUnit.DAYS) +
				" Hours: " + DateUtils.getDateDiff(date1, date2, TimeUnit.HOURS) +
				" Minutes: " + DateUtils.getDateDiff(date1, date2, TimeUnit.MINUTES) +
				" Seconds: " + DateUtils.getDateDiff(date1, date2, TimeUnit.SECONDS));
		 Map<TimeUnit,Long> result = DateUtils.computeDiff(date1, date2);
		
		
	}
	
	/**
	 * 
	 * @param date
	 * @param format default(yyyy-MM-dd)
	 * @return string date format
	 */
	public static String convertDate(Date date, String format){
		try{
		if(format==null || format.isEmpty()) format ="yyyy-MM-dd";
		DateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * 
	 * @param datevalue
	 * @param format
	 * @return date
	 */
	public static Date convertDateString(String datevalue, String format){
		
		try{
		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat(format);
		date = dateFormat.parse(datevalue);
		return date;
		}catch(Exception e){}
		return null;
	}
	
	public static Date getDateToday(){
		return new Date();
	}
	
}

