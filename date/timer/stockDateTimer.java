package date.timer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import common.ConstantsInfo;

public class stockDateTimer {
	
	// ����ȫ�ֿ��� ��һ�ܣ����ܣ���һ�ܵ������仯
	 private int weeks = 0;
	 private int MaxDate;// һ���������
	 private int MaxYear;// һ���������
	 
	 public static String DEFAULT_FORMAT = "yyyy-MM-dd"; 
	 static SimpleDateFormat format = new   SimpleDateFormat(DEFAULT_FORMAT); 
	   /** 
	     * ��ʽ������ 
	     * @param date ���ڶ��� 
	     * @return String �����ַ��� 
	     */  
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
	    
	    //�жϵ�ǰ�ǵڼ���
		public int getWeekofYear() {
			Calendar calendar = Calendar.getInstance();  
			int week = calendar.get(Calendar.WEEK_OF_MONTH);    //��ȡ��
			return week;	       
		 }
	      
	    /** 
	     * ��ȡĳ���һ������ 
	     * @param year ��� 
	     * @return Date 
	     */  
	    public static Date getCurrYearFirst(int year){  
	        Calendar calendar = Calendar.getInstance();  
	        calendar.clear();  
	        calendar.set(Calendar.YEAR, year);  
	        Date currYearFirst = calendar.getTime();  
	        return currYearFirst;  
	    }  
	      
	    /** 
	     * ��ȡĳ�����һ������ 
	     * @param year ��� 
	     * @return Date 
	     */  
	    public static Date getCurrYearLast(int year){  
	        Calendar calendar = Calendar.getInstance();  
	        calendar.clear();  
	        calendar.set(Calendar.YEAR, year);  
	        calendar.roll(Calendar.DAY_OF_YEAR, -1);  
	        Date currYearLast = calendar.getTime();  
	          
	        return currYearLast;  
	    }  
	 
	 public static String getTwoDay(String sj1, String sj2) 
	 {
		  SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
		  long day = 0;
		  try {
			  Date date = myFormatter.parse(sj1);
			  Date mydate = myFormatter.parse(sj2);
			  day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
		  } catch (Exception e) {
		   return "";
		  }
		  return day + "";
	 }
	 
	 public static boolean isWorkDay()
	 {
		 Calendar cd = Calendar.getInstance();
		  // ��ý�����һ�ܵĵڼ��죬�������ǵ�һ�죬���ڶ��ǵڶ���......
		 int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK)-1; // ��Ϊ���й����һ��Ϊ��һ�����������1
		 System.out.print(dayOfWeek);
		 switch(dayOfWeek)
		 {
		 case 1:
		 case 2:
		 case 3:
		 case 4:
		 case 5:
			 System.out.print("wrokDay");
			 return true;
		 case 0:
		 case 6:
		 default:
			 System.out.print("not wrokDay");
			 return false;		 
		 }
		  
		 
	 }
	 //�ж� 2014-5-24���ܼ�
	 public static String getWeek(String sdate) {
		  // ��ת��Ϊʱ��
		  Date date = strToDate(sdate);
		  Calendar c = Calendar.getInstance();
		  c.setTime(date);
		  // int hour=c.get(Calendar.DAY_OF_WEEK);
		  // hour�д�ľ������ڼ��ˣ��䷶Χ 1~7
		  // 1=������ 7=����������������
		  return new SimpleDateFormat("EEEE").format(c.getTime());
	 }
	 
	
	 
	 public static long getDays(String date1, String date2) {
		  if (date1 == null || date1.equals(""))
			  return 0;
		  if (date2 == null || date2.equals(""))
			  return 0;
		  // ת��Ϊ��׼ʱ��
		  SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
		  Date date = null;
		  Date mydate = null;
		  try {
			  date = myFormatter.parse(date1);
			  mydate = myFormatter.parse(date2);
		  } catch (Exception e) {
		  }
		  long day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
		  return day;
	 }
	 
	 // ���㵱�����һ��,�����ַ���
	 public String getDefaultDay() {
		  String str = "";
		  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		  Calendar lastDate = Calendar.getInstance();
		  lastDate.set(Calendar.DATE, 1);// ��Ϊ��ǰ�µ�1��
		  lastDate.add(Calendar.MONTH, 1);// ��һ���£���Ϊ���µ�1��
		  lastDate.add(Calendar.DATE, -1);// ��ȥһ�죬��Ϊ�������һ��
		  str = sdf.format(lastDate.getTime());
		  return str;
	 }
	 // ���µ�һ��
	 public String getPreviousMonthFirst() {
		  String str = "";
		  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		  Calendar lastDate = Calendar.getInstance();
		  lastDate.set(Calendar.DATE, 1);// ��Ϊ��ǰ�µ�1��
		  lastDate.add(Calendar.MONTH, -1);// ��һ���£���Ϊ���µ�1��
		  // lastDate.add(Calendar.DATE,-1);//��ȥһ�죬��Ϊ�������һ��
		  str = sdf.format(lastDate.getTime());
		  return str;
	 }
	 // ��ȡ���µ�һ��
	 public String getFirstDayOfMonth() {
		  String str = "";
		  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		  Calendar lastDate = Calendar.getInstance();
		  lastDate.set(Calendar.DATE, 1);// ��Ϊ��ǰ�µ�1��
		  str = sdf.format(lastDate.getTime());
		  return str;
	 }
	 // ��ñ��������յ�����
	 public String getCurrentWeekday() {
		  weeks = 0;
		  int mondayPlus = this.getMondayPlus();
		  GregorianCalendar currentDate = new GregorianCalendar();
		  currentDate.add(GregorianCalendar.DATE, mondayPlus + 6);
		  Date monday = currentDate.getTime();
		  DateFormat df = DateFormat.getDateInstance();
		  String preMonday = df.format(monday);
		  return preMonday;
	 }
	 // ��ȡ����ʱ��
	 public String getNowTime(String dateformat) {
		  Date now = new Date();
		  SimpleDateFormat dateFormat = new SimpleDateFormat(dateformat);// ���Է�����޸����ڸ�ʽ
		  String curTime = dateFormat.format(now);
		  return curTime;
	 }
	 
	 // ��õ�ǰ�����뱾������������
	 private int getMondayPlus() {
		  Calendar cd = Calendar.getInstance();
		  // ��ý�����һ�ܵĵڼ��죬�������ǵ�һ�죬���ڶ��ǵڶ���......
		  int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK) - 1; // ��Ϊ���й����һ��Ϊ��һ�����������1
		  
		  System.out.println("week:"+dayOfWeek);
		  if (dayOfWeek == 1) {
			  return 0;
		  } else {
			  return 1 - dayOfWeek;
		  }
	 }
	 
	 // ��ñ���һ������
	 public String getMondayOFWeek() {
		  weeks = 0;
		  int mondayPlus = this.getMondayPlus();
		  GregorianCalendar currentDate = new GregorianCalendar();
		  currentDate.add(GregorianCalendar.DATE, mondayPlus);
		  Date monday = currentDate.getTime();
		  DateFormat df = DateFormat.getDateInstance();
		  String preMonday = df.format(monday);
		  return preMonday;
	 }
	 // �����Ӧ�ܵ�����������
	 public String getSaturday() {
		  int mondayPlus = this.getMondayPlus();
		  GregorianCalendar currentDate = new GregorianCalendar();
		  currentDate.add(GregorianCalendar.DATE, mondayPlus + 7 * weeks + 6);
		  Date monday = currentDate.getTime();
		  DateFormat df = DateFormat.getDateInstance();
		  String preMonday = df.format(monday);
		  return preMonday;
	 }
	 // ������������յ�����
	 public String getPreviousWeekSunday() {
		  weeks = 0;
		  weeks--;
		  int mondayPlus = this.getMondayPlus();
		  GregorianCalendar currentDate = new GregorianCalendar();
		  currentDate.add(GregorianCalendar.DATE, mondayPlus + weeks);
		  Date monday = currentDate.getTime();
		  DateFormat df = DateFormat.getDateInstance();
		  String preMonday = df.format(monday);
		  return preMonday;
	 }
	 // �����������һ������
	 public String getPreviousWeekday() {
		  weeks--;
		  int mondayPlus = this.getMondayPlus();
		  GregorianCalendar currentDate = new GregorianCalendar();
		  currentDate.add(GregorianCalendar.DATE, mondayPlus + 7 * weeks);
		  Date monday = currentDate.getTime();
		  DateFormat df = DateFormat.getDateInstance();
		  String preMonday = df.format(monday);
		  return preMonday;
	 }
	 // �����������һ������
	 public String getNextMonday() {
		  weeks++;
		  int mondayPlus = this.getMondayPlus();
		  GregorianCalendar currentDate = new GregorianCalendar();
		  currentDate.add(GregorianCalendar.DATE, mondayPlus + 7);
		  Date monday = currentDate.getTime();
		  DateFormat df = DateFormat.getDateInstance();
		  String preMonday = df.format(monday);
		  return preMonday;
	 }
	 // ������������յ�����
	 public String getNextSunday() {
		  int mondayPlus = this.getMondayPlus();
		  GregorianCalendar currentDate = new GregorianCalendar();
		  currentDate.add(GregorianCalendar.DATE, mondayPlus + 7 + 6);
		  Date monday = currentDate.getTime();
		  DateFormat df = DateFormat.getDateInstance();
		  String preMonday = df.format(monday);
		  return preMonday;
	 }
	 private int getMonthPlus() {
		  Calendar cd = Calendar.getInstance();
		  int monthOfNumber = cd.get(Calendar.DAY_OF_MONTH);
		  cd.set(Calendar.DATE, 1);// ����������Ϊ���µ�һ��
		  cd.roll(Calendar.DATE, -1);// ���ڻع�һ�죬Ҳ�������һ��
		  MaxDate = cd.get(Calendar.DATE);
		  if (monthOfNumber == 1) {
		   return -MaxDate;
		  } else {
		   return 1 - monthOfNumber;
		  }
	 }
	 // ����������һ�������
	 public String getPreviousMonthEnd() {
		  String str = "";
		  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		  Calendar lastDate = Calendar.getInstance();
		  lastDate.add(Calendar.MONTH, -1);// ��һ����
		  lastDate.set(Calendar.DATE, 1);// ����������Ϊ���µ�һ��
		  lastDate.roll(Calendar.DATE, -1);// ���ڻع�һ�죬Ҳ���Ǳ������һ��
		  str = sdf.format(lastDate.getTime());
		  return str;
	 }
	 // ����¸��µ�һ�������
	 public String getNextMonthFirst() {
		  String str = "";
		  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		  Calendar lastDate = Calendar.getInstance();
		  lastDate.add(Calendar.MONTH, 1);// ��һ����
		  lastDate.set(Calendar.DATE, 1);// ����������Ϊ���µ�һ��
		  str = sdf.format(lastDate.getTime());
		  return str;
	 }
	 // ����¸������һ�������
	 public String getNextMonthEnd() {
		  String str = "";
		  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		  Calendar lastDate = Calendar.getInstance();
		  lastDate.add(Calendar.MONTH, 1);// ��һ����
		  lastDate.set(Calendar.DATE, 1);// ����������Ϊ���µ�һ��
		  lastDate.roll(Calendar.DATE, -1);// ���ڻع�һ�죬Ҳ���Ǳ������һ��
		  str = sdf.format(lastDate.getTime());
		  return str;
	 }
	 // ����������һ�������
	 public String getNextYearEnd() {
		  String str = "";
		  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		  Calendar lastDate = Calendar.getInstance();
		  lastDate.add(Calendar.YEAR, 1);// ��һ����
		  lastDate.set(Calendar.DAY_OF_YEAR, 1);
		  lastDate.roll(Calendar.DAY_OF_YEAR, -1);
		  str = sdf.format(lastDate.getTime());
		  return str;
	 }
	 // ��������һ�������
	 public String getNextYearFirst() {
		  String str = "";
		  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		  Calendar lastDate = Calendar.getInstance();
		  lastDate.add(Calendar.YEAR, 1);// ��һ����
		  lastDate.set(Calendar.DAY_OF_YEAR, 1);
		  str = sdf.format(lastDate.getTime());
		  return str;
	 }
	 // ��ñ����ж�����
	 private int getMaxYear() {
		  Calendar cd = Calendar.getInstance();
		  cd.set(Calendar.DAY_OF_YEAR, 1);// ��������Ϊ�����һ��
		  cd.roll(Calendar.DAY_OF_YEAR, -1);// �����ڻع�һ�졣
		  int MaxYear = cd.get(Calendar.DAY_OF_YEAR);
		  return MaxYear;
	 }
	 private int getYearPlus() {
		  Calendar cd = Calendar.getInstance();
		  int yearOfNumber = cd.get(Calendar.DAY_OF_YEAR);// ��õ�����һ���еĵڼ���
		  cd.set(Calendar.DAY_OF_YEAR, 1);// ��������Ϊ�����һ��
		  cd.roll(Calendar.DAY_OF_YEAR, -1);// �����ڻع�һ�졣
		  int MaxYear = cd.get(Calendar.DAY_OF_YEAR);
		  if (yearOfNumber == 1) {
		   return -MaxYear;
		  } else {
		   return 1 - yearOfNumber;
		  }
	 }
	 // ��ñ����һ�������
	 public String getCurrentYearFirst() {
		  int yearPlus = this.getYearPlus();
		  GregorianCalendar currentDate = new GregorianCalendar();
		  currentDate.add(GregorianCalendar.DATE, yearPlus);
		  Date yearDay = currentDate.getTime();
		  DateFormat df = DateFormat.getDateInstance();
		  String preYearDay = df.format(yearDay);
		  return preYearDay;
	 }
	 // ��ñ������һ������� *
	 public String getCurrentYearEnd() {
		  Date date = new Date();
		  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");// ���Է�����޸����ڸ�ʽ
		  String years = dateFormat.format(date);
		  return years + "-12-31";
	 }
	 // ��������һ������� *
	 public String getPreviousYearFirst() {
		  Date date = new Date();
		  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");// ���Է�����޸����ڸ�ʽ
		  String years = dateFormat.format(date);
		  int years_value = Integer.parseInt(years);
		  years_value--;
		  return years_value + "-1-1";
	 }
	 // ����������һ�������
	 public String getPreviousYearEnd() {
		  weeks--;
		  int yearPlus = this.getYearPlus();
		  GregorianCalendar currentDate = new GregorianCalendar();
		  currentDate.add(GregorianCalendar.DATE, yearPlus + MaxYear * weeks
		    + (MaxYear - 1));
		  Date yearDay = currentDate.getTime();
		  DateFormat df = DateFormat.getDateInstance();
		  String preYearDay = df.format(yearDay);
		  getThisSeasonTime(11);
		  return preYearDay;
	 }
	 // ��ñ�����
	 public String getThisSeasonTime(int month) {
		  int array[][] = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 }, { 10, 11, 12 } };
		  int season = 1;
		  if (month >= 1 && month <= 3) {
		   season = 1;
		  }
		  if (month >= 4 && month <= 6) {
		   season = 2;
		  }
		  if (month >= 7 && month <= 9) {
		   season = 3;
		  }
		  if (month >= 10 && month <= 12) {
		   season = 4;
		  }
		  int start_month = array[season - 1][0];
		  int end_month = array[season - 1][2];
		  Date date = new Date();
		  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");// ���Է�����޸����ڸ�ʽ
		  String years = dateFormat.format(date);
		  int years_value = Integer.parseInt(years);
		  int start_days = 1;// years+"-"+String.valueOf(start_month)+"-1";//getLastDayOfMonth(years_value,start_month);
		  int end_days = getLastDayOfMonth(years_value, end_month);
		  String seasonDate = years_value + "-" + start_month + "-" + start_days
		    + ";" + years_value + "-" + end_month + "-" + end_days;
		  return seasonDate;
	 }
	 
	 //��ȡĳ��ĳ���ж�����
	 private int getLastDayOfMonth(int year, int month) {
		  if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8
		    || month == 10 || month == 12) {
		   return 31;
		  }
		  if (month == 4 || month == 6 || month == 9 || month == 11) {
		   return 30;
		  }
		  if (month == 2) {
		   if (isLeapYear(year)) {
		    return 29;
		   } else {
		    return 28;
		   }
		  }
		  return 0;
	 }
	 
	 //�ж�����
	 public boolean isLeapYear(int year) {
	  return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
	 }
	 
	 
	 
	 public static int getMonthSpace(String date1, String date2) throws ParseException
	 {
		 int result = 0;		
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		 Calendar c1 = Calendar.getInstance();
		 Calendar c2 = Calendar.getInstance();
		
		 c1.setTime(sdf.parse(date1));
		 c2.setTime(sdf.parse(date2));
		
		 result = c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);
		
		 return result == 0 ? 1 : Math.abs(result);

	 }
	 
	 //����ʱ������
	 public static int daysBetween(String smdate,String bdate,int type) throws ParseException{  
	        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
	        Calendar cal = Calendar.getInstance();    
	        cal.setTime(sdf.parse(smdate));    
	        long time1 = cal.getTimeInMillis();                 
	        cal.setTime(sdf.parse(bdate));    
	        long time2 = cal.getTimeInMillis();         
	        long between_days=(time2-time1)/(1000*3600*24);  
	      //  System.out.println("between_days:"+between_days);
	      //  if(between_days<0)
	    //    	between_days = -between_days;
	       int dayNums=Integer.parseInt(String.valueOf(between_days));    
	       int nums=0;
	       if (type == ConstantsInfo.DayDataType)
	    	   nums =dayNums;
	       else if (type == ConstantsInfo.WeekDataType)
	    	   nums= dayNums/7;    
	       else if (type == ConstantsInfo.MonthDataType)
	    	   nums=  dayNums/30;
	       return nums;
	   }  

	 
	 //�����add��ʱ��
	 public static String getAddDate(Date date,int add) {
		
		 Calendar calendar = Calendar.getInstance();
		 calendar.setTime(date);
		 calendar.add(Calendar.DATE,add); 
		 String addDate = format.format(calendar.getTime()) ;
		 return addDate;
	 }
	 
	 //��ȡ��ǰ����
	 public static String getCurDate() {
		Date dt=new Date();//�������Ҫ��ʽ,��ֱ����dt,dt���ǵ�ǰϵͳʱ��
	 	String nowTime="";
	 	nowTime= format.format(dt);//��DateFormat��format()������dt�л�ȡ����yyyy/MM/dd HH:mm:ss��ʽ��ʾ
	 	return nowTime;
	 }
	 
	 
	 public static  int compareDate(String DATE1, String DATE2) throws ParseException {     
      
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
 
        Date dt1 = sdf.parse(DATE1);
        Date dt2 = sdf.parse(DATE2);
        if (dt1.getTime() > dt2.getTime()) {            
            return 1;
        } else if (dt1.getTime() < dt2.getTime()) {      
            return -1;
        } else {
            return 0;
        }
       
	 }

	public static void main(String[] args) throws ParseException {
		stockDateTimer tt = new stockDateTimer();
		 int i= compareDate("1995-11-12", "1999-12-16");
	       System.out.println("i=="+i);
	       /*
	       for(int i = 1999;i < 2013;i++){  
	            System.out.println(formatDate(getCurrYearFirst(i)));  
	            System.out.println(formatDate(getCurrYearLast(i)));  
	        }  
		
		System.out.println("��ȡ��ǰ�������ܼ�:" + tt.isWorkDay());
		System.out.println("��ȡ��ǰ�������ܼ�:" + tt.getWeek("2014-4-29"));
		System.out.println("��ȡ��������:" + tt.getNowTime("yyyy-MM-dd"));
		System.out.println("��ȡ����һ����:" + tt.getMondayOFWeek());
		System.out.println("��ȡ�����յ�����:" + tt.getCurrentWeekday());
		System.out.println("��ȡ����һ����:" + tt.getPreviousWeekday());
		System.out.println("��ȡ����������:" + tt.getPreviousWeekSunday());
		System.out.println("��ȡ����һ����:" + tt.getNextMonday());
		System.out.println("��ȡ����������:" + tt.getNextSunday());
		System.out.println("�����Ӧ�ܵ�����������:" + tt.getNowTime("yyyy-MM-dd"));
		System.out.println("��ȡ���µ�һ������:" + tt.getFirstDayOfMonth());
		System.out.println("��ȡ�������һ������:" + tt.getDefaultDay());
		System.out.println("��ȡ���µ�һ������:" + tt.getPreviousMonthFirst());
		System.out.println("��ȡ�������һ�������:" + tt.getPreviousMonthEnd());
		System.out.println("��ȡ���µ�һ������:" + tt.getNextMonthFirst());
		System.out.println("��ȡ�������һ������:" + tt.getNextMonthEnd());
		System.out.println("��ȡ����ĵ�һ������:" + tt.getCurrentYearFirst());
		System.out.println("��ȡ�������һ������:" + tt.getCurrentYearEnd());
		System.out.println("��ȡȥ��ĵ�һ������:" + tt.getPreviousYearFirst());
		System.out.println("��ȡȥ������һ������:" + tt.getPreviousYearEnd());
		System.out.println("��ȡ�����һ������:" + tt.getNextYearFirst());
		System.out.println("��ȡ�������һ������:" + tt.getNextYearEnd());
		System.out.println("��ȡ�����ȵ�һ�쵽���һ��:" + tt.getThisSeasonTime(11));
		System.out.println("��ȡ��������֮��������2008-12-1~2008-9.29:"
	    + stockDateTimer.getTwoDay("2008-12-1", "2008-9-29"));
		*/
	}

}
