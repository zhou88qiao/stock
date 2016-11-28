package com.timer.stock;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class StockDateTimer {
	
	public static String DEFAULT_FORMAT = "yyyy-MM-dd"; 
	
	 public static String formatDate(Date date){  
	        SimpleDateFormat f = new SimpleDateFormat(DEFAULT_FORMAT);  
	        String sDate = f.format(date);  
	        return sDate;  
	    }  
	 
	  public static Date strToDate(String strDate) {
		  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		  ParsePosition pos = new ParsePosition(0);
		  Date strtodate = formatter.parse(strDate, pos);
		  return strtodate;
	 }
    
		 
	 public static String getBeforeDay(String dateStr, int type,int num) {
		GregorianCalendar gregorianCal = new GregorianCalendar();  
		Date  date=strToDate(dateStr);	         
		gregorianCal.setTime(date); 
		switch(type)
		{
		case 1:
			gregorianCal.set(Calendar.DAY_OF_YEAR, gregorianCal.get(Calendar.DAY_OF_YEAR) - num);
			break;
		case 2:
			gregorianCal.set(Calendar.WEEK_OF_YEAR, gregorianCal.get(Calendar.WEEK_OF_YEAR) - num);  
			break;	
		case 3:
			gregorianCal.set(Calendar.MONTH, gregorianCal.get(Calendar.MONTH) - num);  
			break;			
		}
		
		Date beforeDate= gregorianCal.getTime();
		String before=formatDate(beforeDate);
		return before;
	 }
	 
	 //�жϵ�ǰ�ǵڼ���
	 public int getDayofYear() {
			Calendar calendar = Calendar.getInstance();  
			int day = calendar.get(Calendar.DAY_OF_YEAR);    //��ȡ��
			return day;	       
		}
	 
	  //�жϵ�ǰ�ǵڼ���
	 public int getWeekofYear() {
			Calendar calendar = Calendar.getInstance();  
			int week = calendar.get(Calendar.WEEK_OF_YEAR);    //��ȡ��
			return week;	       
		}
	 //�жϵ�ǰ�ǵڼ���
	 public int getMonthofYear() {
			Calendar calendar = Calendar.getInstance();  
			int month = calendar.get(Calendar.MONTH);    //��ȡ��
			return month;	       
		}
	 
	 
	 //�ж� 2014-5-24�ǵڼ���
	 public static int getDayInYear(String sdate) {
		  // ��ת��Ϊʱ��
		  Date date = strToDate(sdate);
		  Calendar calendar = Calendar.getInstance();
		  calendar.setTime(date);		
		  return calendar.get(Calendar.DAY_OF_YEAR); 
	 }
	 
	 //�ж� 2014-5-24�ǵڼ���
	 public static int getWeekInYear(String sdate) {
		  // ��ת��Ϊʱ��
		  Date date = strToDate(sdate);
		  Calendar calendar = Calendar.getInstance();
		  calendar.setTime(date);		
		  return calendar.get(Calendar.WEEK_OF_YEAR); 
	 }
	 
	//�ж� 2014-5-24�ǵڼ���
	 public static int getMonthInYear(String sdate) {
		  // ��ת��Ϊʱ��
		  Date date = strToDate(sdate);
		  Calendar calendar = Calendar.getInstance();
		  calendar.setTime(date);		
		  return calendar.get(Calendar.MONTH); 
	 }
	 
	 public static void main(String[] args) {
		 
		 
		 Calendar aTime=Calendar.getInstance();
			int year = aTime.get(Calendar.YEAR);//�õ���
			System.out.println(year);
		 
		 StockDateTimer st=new StockDateTimer();
		 int day=st.getDayofYear();
		 int day1=st.getDayInYear("2014-6-24");
		 int week=st.getWeekInYear("2015-12-11");
		 int week1=st.getWeekInYear("2015-12-12");
		 int month=st.getMonthInYear("2014-12-24");
		 System.out.println("day:"+day+"week:"+week+"month:"+month);
		 System.out.println("week1:"+week1);
		 System.out.println("day1:"+day1);
		 

	 }
	
	
	    
	

}
