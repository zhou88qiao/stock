package excel.simple;

import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.log4j.PropertyConfigurator;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.point.stock.PointClass;
import com.timer.stock.DateStock;

import common.ConstantsInfo;
import common.stockLogger;

import stockGUI.stockDialog;
import stockGUI.stocktree.StockObjectType;
import stockGUI.stocktree.StockTreeNodeData;

import dao.DbConn;
import dao.StockBaseDao;
import dao.StockConcept;
import dao.StockData;
import dao.StockDataDao;
import dao.StockIndustry;
import dao.StockInformation;
import dao.StockInformationDao;
import dao.StockMarket;
import dao.StockPoint;
import dao.StockPointDao;
import dao.StockToIndustry;
import date.timer.stockDateTimer;
import excel.all_v1.StockExcelItem;
import excel.all_v1.StockExcelStatItem;
import excel.all_v1.StockOtherInfoValue;

public class StockRecentWriterV1 {
	
	static StockInformationDao siDao;
	static StockDataDao sdDao;
	static StockPointDao spDao;
	static StockBaseDao sbDao;
	static PointClass pClass=new PointClass();
	private POIFSFileSystem fs;
    private static HSSFWorkbook wb;
    private XSSFWorkbook xb;
    private HSSFSheet sheet;
    private Sheet xsheet;
    private HSSFRow row;
    private Row xrow;
	static int stockLabelNum=4;
	static int stockRow = 1;
	static stockDateTimer dateTimer = new stockDateTimer();
	//ʱ������ hash��
    static HashMap<String, Integer> stockDateColumnmap = new HashMap<String, Integer>(); 
	
	  static XSSFColor hfNormal;
      static XSSFColor hfCday;
      static XSSFColor hfCweek;
      static XSSFColor hfCmonth;
      static XSSFColor hfCDayWeek;
      static XSSFColor hfCDayWeekMonth;
      static XSSFColor hfYuce;
      
      // �ļ�ͷ����  
      static XSSFFont font0;  
      static XSSFFont fontDay; 
      static XSSFFont fontWeek;
      static XSSFFont fontMonth;
      static XSSFFont fontDayWeek;      
      static XSSFFont fontDayWeekMonth;  
      static XSSFFont fontYuce;
      
      static CellStyle cellStyleTitle;//��һ�б���
      static CellStyle cellStyleGroup;//�������ҵ ����
      static CellStyle cellStyleNormal;
      static CellStyle cellStyleDay;
      static CellStyle cellStyleDayWeek;
      static CellStyle cellStyleDayWeekMonth;
      static CellStyle cellStyleWeek;
      static CellStyle cellStyleMonth;
      static CellStyle cellStyleYuce;
	
      
      public static CellStyle create07CellStyle(Workbook wb, XSSFFont font,int color) {    
          CellStyle cellStyle = wb.createCellStyle();  
          cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);  
          cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_BOTTOM);  
          cellStyle.setFont(font);  
        //  cellStyle.setFillBackgroundColor(HSSFColor.BLUE.index);
         // IndexedColors.YELLOW.getIndex()
          //ǰ��ɫ
          if((short)color>0)
          {	
          	cellStyle.setFillForegroundColor((short) color);            
          	cellStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);  
          }
         return cellStyle;
      }  
    
    public static void initFont(XSSFWorkbook wb)
    {
    	
    	hfNormal=new XSSFColor(java.awt.Color.BLACK);
        hfCday=new XSSFColor(java.awt.Color.RED);
        hfCweek=new XSSFColor(java.awt.Color.CYAN);
        hfCmonth=new XSSFColor(java.awt.Color.GRAY);
        hfCDayWeek=new XSSFColor(java.awt.Color.BLUE);
        hfCDayWeekMonth=new XSSFColor(java.awt.Color.ORANGE);
        hfYuce=new XSSFColor(java.awt.Color.GREEN);
        
        
    	 // �ļ�ͷ����  
        font0 = create07Fonts(wb, XSSFFont.BOLDWEIGHT_BOLD, "����", false,  
                (short) 400,hfNormal);  
        fontDay = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "����", false,  
                (short) 300,hfCday);  
        fontDayWeek = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "����", false,  
                (short) 300,hfCDayWeek);  
        fontDayWeekMonth = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "����", false,  
                (short) 300,hfCDayWeekMonth); 
        fontWeek = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "����", false,  
                (short) 300,hfCweek);  
        fontMonth = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "����", false,  
                (short) 300,hfCmonth);  
        fontYuce = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "����", false,  
                (short) 300,hfYuce);  
        
        //cell��ʽ����
        cellStyleTitle=create07CellStyle(wb,font0,IndexedColors.YELLOW.getIndex());
        cellStyleGroup=create07CellStyle(wb,font0,IndexedColors.BLUE.getIndex());
        cellStyleNormal=create07CellStyle(wb,font0,ConstantsInfo.XSSFNORMAL);
        cellStyleDay=create07CellStyle(wb,fontDay,ConstantsInfo.XSSFNORMAL);
        cellStyleWeek=create07CellStyle(wb,fontWeek,ConstantsInfo.XSSFNORMAL);
        cellStyleMonth=create07CellStyle(wb,fontMonth,ConstantsInfo.XSSFNORMAL);
        cellStyleDayWeek=create07CellStyle(wb,fontDayWeek,ConstantsInfo.XSSFNORMAL);
        cellStyleDayWeekMonth=create07CellStyle(wb,fontDayWeekMonth,ConstantsInfo.XSSFNORMAL);
        cellStyleYuce=create07CellStyle(wb,fontYuce,ConstantsInfo.XSSFNORMAL);

    }
   
    
    //����excel
    public static void createExcel(String filePath, String fileName) throws IOException, SQLException
    {
    	  // �����  
		OutputStream os = null;  
		int i=0;
		File file = new File(filePath);  
		System.out.println(filePath);
		if (!file.exists())
		{   
			 file.mkdir();   
		}    
		
		 os = new FileOutputStream(filePath + fileName);    
		 // ������
		 XSSFWorkbook wb = new XSSFWorkbook();   
		 // ������һ��sheet     
		 XSSFSheet sheet= wb.createSheet("allstock");
		
      
         // �ļ�ͷ����  
         initFont(wb); 
         
         
         //������һ��  
         Row rowHead = sheet.createRow(0);  
         
         int col=stockLabelNum;
         //����һ������ı�  
         create07Cell(wb, rowHead, 0, "���", cellStyleTitle);  
         create07Cell(wb, rowHead, 1, "����", cellStyleTitle);  
         create07Cell(wb, rowHead, 2, "����", cellStyleTitle); 	            
         create07Cell(wb, rowHead, 3, "����", cellStyleTitle);
         
        List<String> listStockDate = new ArrayList<String>();
       //���������ʱ��
        Date dt=new Date();
       String  cDate  = dateTimer.getAddDate(dt,-90);
       System.out.println("������ǰ��"+cDate);
		
 		listStockDate=sdDao.getDatesFromSH000001RecentDate(cDate);
 		System.out.println("date size:"+listStockDate.size());
 		
 		//�п�
        for(i=0;i<stockLabelNum;i++)
        {	
        	sheet.setColumnWidth(i, 5000);         
        }
         //�п�
         for(i=stockLabelNum;i<=stockLabelNum+listStockDate.size()+1;i++)
         {	
         	sheet.setColumnWidth(i, 4000);         
         }
         
         /*
         create07Cell(wb, rowHead, 4, "��ǰ�ռ���", cellStyleTitle);
         create07Cell(wb, rowHead, 5, "��ǰ�ܼ���", cellStyleTitle);
         create07Cell(wb, rowHead, 6, "��ǰ�¼���", cellStyleTitle);
        */ 
         int   stockColumn=stockLabelNum;//Ԥ����������ǰ����
             
         
        for(Iterator it = listStockDate.iterator();it.hasNext();)
 		{
        	//2015-08-20
         	String stockDate=(String)it.next();  
         	stockDateColumnmap.put(stockDate, stockColumn);
         	stockDate=stockDate.substring(5);
         	//ȡ���£��� 
         	create07Cell(wb, rowHead, stockColumn, stockDate, font0,IndexedColors.YELLOW.getIndex()); 	
         	
         	stockColumn++;
 		}  
         

		 wb.write(os);
		 os.close();
    }
    
    //�õ��������ֵ
    public static StockData getCurExtremeData(String stockFullId,int dataType) throws ClassNotFoundException, IOException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException
    {
    	StockData sCurData=null;
    	//����
 		String nowTime=dateTimer.getCurDate(); 
    	//1 ���һ����ֵ��
 		//System.out.println(dataType);
		StockPoint sp = spDao.getLastPointStock(stockFullId,dataType);		
		if(sp == null) {
			stockLogger.logger.fatal("stockFullId��"+stockFullId+"�����������");
			System.out.println(stockFullId+"�����������****");
			return null;  
		}
		String crossLastDate=sp.getToDate().toString();
		
		if (sp.getWillFlag()==1) { //ǰһ������������ǰΪ�½�����
							
			//3 �����͵�
			sCurData=sdDao.getMinStockDataPoint(stockFullId,crossLastDate,nowTime,dataType);
			if ( sCurData == null){
				stockLogger.logger.fatal("****stockFullId��"+stockFullId+"������͵�****");
				return null; 
			}
		} else {
			sCurData=sdDao.getMaxStockDataPoint(stockFullId,crossLastDate,nowTime,dataType);
			if ( sCurData == null){
				stockLogger.logger.fatal("****stockFullId��"+stockFullId+"������ߵ�****");
				stockLogger.logger.fatal("****����㣺"+crossLastDate+"��ǰ�յ㣺"+nowTime+"�����ڼ��޽�������****");
				return null; 
			}
		}
		
		return sCurData;
    }
    
    //������ҵ����
    public static void exportExcelAllStockForIndustry(String filePath, String fileName) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException 
    {    
            FileOutputStream fileOStream=null;

        	List<StockIndustry> listIndustry = new ArrayList<StockIndustry>();         	
        	
        	 //�õ���ǰ������ҵ
        	listIndustry=sbDao.getStockIndustry();        	
        	System.out.println("��ҵ������"+listIndustry.size());        	
        	
        	NumberFormat ddfDay=null;
        	//����������λС����
        	ddfDay=NumberFormat.getNumberInstance() ; 
        	ddfDay.setMaximumFractionDigits(2);
            //�ڶ��п�ʼչʾ  
                 
            Iterator itIndustry,ie;
	   		for(itIndustry = listIndustry.iterator();itIndustry.hasNext();)
	   		{
	   			// ������   
				 FileInputStream fileIStream = new FileInputStream(filePath + fileName);  
				 XSSFWorkbook wb = new XSSFWorkbook(fileIStream);   
				 XSSFSheet sheet = wb.getSheetAt(0);  
				// System.out.println("���һ�У�"+sheet.getLastRowNum());   
				 //������   
				 fileOStream = new FileOutputStream(filePath + fileName);
				
				 initFont(wb);
				 
				 //��ǰ��ҵ
				 StockIndustry indu= (StockIndustry)itIndustry.next();	
				 String induName=indu.getThirdname();
				 String induCode = indu.getThirdcode();
				 if(induName==null)
					 continue;				
				stockLogger.logger.debug("��ҵ��"+induName);
	   			   			
	   			//��ҵ
	   			Row rowIndustry = sheet.createRow(stockRow);  
	   			create07Cell(wb, rowIndustry, 0, induName, cellStyleGroup);  
	   			stockRow++;
	   			
	   			DecimalFormat decimalFormat=new DecimalFormat(".00");
	   			   	   			
	   			//������ҵ���й�Ʊ
	   			List<StockToIndustry> listIndustryStock = new ArrayList<StockToIndustry>();   
	   			listIndustryStock=sbDao.getIndustryToStock(indu.getThirdcode());		   		
	   			stockLogger.logger.debug("��ҵ��Ʊ����"+listIndustryStock.size());
	   			for(ie=listIndustryStock.iterator();ie.hasNext();)
	   			{
	   				//��������	   			
	   				//if(stockRow>20)
	   				//	break;
	   				
	   				StockToIndustry toInduStock = (StockToIndustry) ie.next();
	   				String stockFullId = toInduStock.getStockFullId();	
	   				System.out.println("stockFullId:"+stockFullId);
	   			 				

	   				String stockName=sbDao.getStockNameFromIndustryTable(stockFullId);			
	   			
	   				System.out.println("fullId��"+stockFullId+" ���֣�"+stockName);
	   				stockLogger.logger.debug("fullId��"+stockFullId+" ���֣�"+stockName);
	   				//ÿ����Ʊ
	   				Row rowDayData = sheet.createRow(stockRow);  
	    			create07Cell(wb, rowDayData, 0,  Integer.toString(stockRow), cellStyleNormal);  
		            create07Cell(wb, rowDayData, 1, stockFullId, cellStyleNormal);  
		            create07Cell(wb, rowDayData, 2, stockName, cellStyleNormal); 	            
		            stockRow++;  
		 
		            
		            //��ǰ��ֵ��
		            StockData sday=getCurExtremeData(stockFullId,ConstantsInfo.DayDataType);
		            if(sday!=null)
		            	create07Cell(wb, rowDayData, 4, sday.getDate().toString(), cellStyleNormal); 	
		            StockData sweek=getCurExtremeData(stockFullId,ConstantsInfo.WeekDataType);
		            if(sweek!=null)
		               	create07Cell(wb, rowDayData, 5, sday.getDate().toString(), cellStyleNormal);
		            StockData smonth=getCurExtremeData(stockFullId,ConstantsInfo.MonthDataType);
		            if(smonth!=null)
		            	create07Cell(wb, rowDayData, 6, sday.getDate().toString(), cellStyleNormal);
		                    
		            
	    			List<StockPoint> stockDayPoint=new ArrayList<StockPoint>();
	    			List<StockPoint> stockWeekPoint=new ArrayList<StockPoint>();
	    			List<StockPoint> stockMonthPoint=new ArrayList<StockPoint>();
	    			List<String> stockExtremeDate=new ArrayList<String>();
	    						
	    			//���������ʱ��
	    	        Date dt=new Date();
	    	        String  cDate  = dateTimer.getAddDate(dt,-90);
	    			//ȡ��������м�������
	    			stockDayPoint=spDao.getRecentPointStock(stockFullId,ConstantsInfo.DayDataType,cDate);
	    			stockWeekPoint=spDao.getRecentPointStock(stockFullId, ConstantsInfo.WeekDataType,cDate);
	    			stockMonthPoint=spDao.getRecentPointStock(stockFullId,  ConstantsInfo.MonthDataType,cDate);
	    			if(stockDayPoint==null) {
	    				continue;
	    			}
	    			
	    			//ȡ��������м���ʱ��
	    			stockExtremeDate=spDao.getRecentExtremeDate(stockFullId,cDate);
	    			
	    		  	String showtext="";
	    			int extremeCol=stockLabelNum;	
	    		
	    			//��ʱ���뼫�� hash 
	                HashMap<String, StockPoint> stockDayDateExist = new HashMap<String, StockPoint>(); 	
	    			//��ʱ���뼫�� hash 
	                HashMap<String, StockPoint> stockWeekDateExist = new HashMap<String, StockPoint>(); 
	               //��ʱ���뼫�� hash 
	                HashMap<String, StockPoint> stockMonthDateExist = new HashMap<String, StockPoint>(); 
	                for(Iterator itDay = stockDayPoint.iterator();itDay.hasNext();)
		 	    	{
		 	           StockPoint sPointDay=(StockPoint)itDay.next();	 	            
		 	           stockDayDateExist.put(sPointDay.getExtremeDate().toString(), sPointDay);
		 	    	}
	                
	    			for(Iterator itWeek = stockWeekPoint.iterator();itWeek.hasNext();)
		 	    	{
		 	           StockPoint sPointWeek=(StockPoint)itWeek.next();	 	            
		 	           stockWeekDateExist.put(sPointWeek.getExtremeDate().toString(), sPointWeek);
		 	    	}
	    		
	    			for(Iterator itMonth = stockMonthPoint.iterator();itMonth.hasNext();)
		 	    	{	
		 	            StockPoint sPointMonth=(StockPoint)itMonth.next();	 	            
		 	            stockMonthDateExist.put(sPointMonth.getExtremeDate().toString(), sPointMonth);
		 	    	}
	    			
	    			int type=0;  			
	    			////�ж��գ��ܣ��¼�ֵ���Ƿ��ظ�
	    			int day=0;
	    			int week=0;
	    			int month=0;
	    			int dayweek=0;
	    			int dayweekmonth=0;
	    			int daymonth=0;
	    			int weekmonth=0;
	    			int pointdefault=0;
	    			StockPoint sPointDay,sPointWeek,sPointMonth=null;
	    			for(Iterator itDate = stockExtremeDate.iterator();itDate.hasNext();)
	    			{
	    				int dayType=0;
	    				int weekType=0;
	    				int monthType=0;
	    				String extremeDate = (String) itDate.next();
	    				 sPointDay=stockDayDateExist.get(extremeDate);
		            	if(sPointDay != null)
		            	{
		            		dayType=1;//ConstantsInfo.ExtremeDateDay;
		            	}
	    				sPointWeek=stockWeekDateExist.get(extremeDate);
		            	if(sPointWeek!=null)
		            	{
		            		weekType=1;//ConstantsInfo.ExtremeDateWeek;
		            	}
		            	sPointMonth=stockMonthDateExist.get(extremeDate);
		            	if(sPointMonth!=null)
		            	{
		            		monthType=1;//ConstantsInfo.ExtremeDateDayWeekMonth;
		            	}
		            	
		            	if(dayType==1)//��
		            	{
		            		if(weekType==1)//��
		            		{
		            			if(monthType==1)		            			
		            				type=ConstantsInfo.ExtremeDateDayWeekMonth;	//������	         
		            			else
		            				type=ConstantsInfo.ExtremeDateDayWeek;//����
		            		}
		            		else
		            		{
		            			if(monthType==1)		            			
		            				type=ConstantsInfo.ExtremeDateDayMonth;	//����         
		            			else
		            				type=ConstantsInfo.ExtremeDateDay;//��
		            		}
		            	}
		            	else
		            	{
		            		if(weekType==1)//��
		            		{
		            			if(monthType==1)		            			
		            				type=ConstantsInfo.ExtremeDateWeekMonth;	//����	         
		            			else
		            				type=ConstantsInfo.ExtremeDateWeek;//��
		            		}
		            		else
		            		{
		            			if(monthType==1)		            			
		            				type=ConstantsInfo.ExtremeDateMonth;//��         
		            			
		            		}
		            		
		            	}
		            	String sDayRatio,sWeekRatio,sMonthRatio=null;	
		            	
		            	/*
		            	if(sPointDay!=null)
		            		System.out.println("day:"+sPointDay.getExtremeDate());
		            	if(sPointWeek!=null)
		            		System.out.println("week:"+sPointWeek.getExtremeDate());
		            	if(sPointMonth!=null)
		            		System.out.println("month"+sPointMonth.getExtremeDate());
		            	*/
		            	switch(type)
		            	{
		            	case ConstantsInfo.ExtremeDateDay:
		            		 
			            	sDayRatio= ddfDay.format(sPointDay.getRatio());
			            	//excel��ʾ��ֵ������
			            	showtext="�գ�"+sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%)"; 
			            	if(ConstantsInfo.DEBUG)
				        	{
				            	System.out.println("��ֵ��ʱ�䣺"+sPointDay.getExtremeDate());
				            	System.out.println("��ֵ�۸�"+sPointDay.getExtremePrice()); 
				            	System.out.println("������"+sPointDay.getRatio()); 
				            	System.out.println(showtext);	
				        	}
			            	
			            	extremeCol = stockDateColumnmap.get(sPointDay.getExtremeDate().toString());
			            	//System.out.println("Day��ֵ��ʱ�䣺"+extremeDate);
					         create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleDay); 
					       
					         day++;
		            		break;
		            	case ConstantsInfo.ExtremeDateDayWeek:
		            		 
			            	sDayRatio = ddfDay.format(sPointDay.getRatio());
			            	sWeekRatio = ddfDay.format(sPointWeek.getRatio());
			            	//System.out.println("DayWeek��ֵ��ʱ�䣺"+extremeDate);
			            	//excel��ʾ��ֵ������
			            	showtext="���ܣ�"+sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sWeekRatio+"%)"; 
			            	extremeCol = stockDateColumnmap.get(sPointDay.getExtremeDate().toString());
			            	create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleDayWeek); 
			            	dayweek++;
		            		break;
		            	case ConstantsInfo.ExtremeDateDayWeekMonth:
		            		
			            	sDayRatio= ddfDay.format(sPointDay.getRatio());
			            	sWeekRatio= ddfDay.format(sPointWeek.getRatio());
			            	sMonthRatio= ddfDay.format(sPointMonth.getRatio());
			            	//System.out.println("DayWeekMonth��ֵ��ʱ�䣺"+extremeDate);
			            	//excel��ʾ��ֵ������
			            	showtext="�����£�"+sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sWeekRatio+"%,"+sMonthRatio+"%)"; 
			            	extremeCol = stockDateColumnmap.get(sPointDay.getExtremeDate().toString());
			            	create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleDayWeekMonth); 
			            	
			            	dayweekmonth++;
			            	break;
		            	case ConstantsInfo.ExtremeDateWeek:		            		 
			            	sWeekRatio= ddfDay.format(sPointWeek.getRatio()) ;
			            	//excel��ʾ��ֵ������
			            	showtext="�ܣ�"+sPointWeek.getExtremeDate()+":"+Float.toString(sPointWeek.getExtremePrice())+"("+sWeekRatio+"%)"; 
			            	//System.out.println("Week��ֵ��ʱ�䣺"+extremeDate);
			            	extremeCol = stockDateColumnmap.get(sPointWeek.getExtremeDate().toString());
					         create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleWeek); 
					         
					         week++;
					         break;
		            	case ConstantsInfo.ExtremeDateMonth:		            		 
		            		sMonthRatio= ddfDay.format(sPointMonth.getRatio()) ;
			            	//excel��ʾ��ֵ������
		            		//System.out.println("Month��ֵ��ʱ�䣺"+extremeDate);
			            	showtext="�£�"+sPointMonth.getExtremeDate()+":"+Float.toString(sPointMonth.getExtremePrice())+"("+sMonthRatio+"%)"; 
			            	extremeCol = stockDateColumnmap.get(sPointMonth.getExtremeDate().toString());
					         create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleMonth); 
					       	
					         month++;
					         break;
		            	case ConstantsInfo.ExtremeDateDayMonth:
		            		
		            		sDayRatio = ddfDay.format(sPointDay.getRatio());
			            	sMonthRatio = ddfDay.format(sPointMonth.getRatio());
			            
			            	//excel��ʾ��ֵ������
			            	showtext="���£�"+sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sMonthRatio+"%)"; 
			            	extremeCol = stockDateColumnmap.get(sPointDay.getExtremeDate().toString());
			            	create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleMonth); //cellStyleDayMonth
			          
		            		daymonth++;		 
		            		//System.out.println("DayMonth��ֵ��ʱ�䣺"+extremeDate);
		            		break;
		            	case ConstantsInfo.ExtremeDateWeekMonth:
		            		sWeekRatio = ddfDay.format(sPointWeek.getRatio());
			            	sMonthRatio = ddfDay.format(sPointMonth.getRatio());
			            
			            	//excel��ʾ��ֵ������
			            	showtext="���£�"+sPointWeek.getExtremeDate()+":"+Float.toString(sPointWeek.getExtremePrice())+"("+sWeekRatio+"%,"+sMonthRatio+"%)"; 
			            	extremeCol = stockDateColumnmap.get(sPointWeek.getExtremeDate().toString());
			            	create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleMonth); //cellStyleWeekMonth
		            		
		            		weekmonth++;	
		            		//System.out.println("WeekMonth��ֵ��ʱ�䣺"+extremeDate);
		            		break;
		            	default:
		            		pointdefault++;	
		            		//System.out.println("default��ֵ��ʱ�䣺"+extremeDate);
		            		break;	            		
		            	}	
		            	
	    			}	
	    			
	    			if(ConstantsInfo.DEBUG)
		        	{
		    			System.out.println("fullId�� "+stockFullId+" Day points��"+day);
		    			System.out.println("fullId�� "+stockFullId+" Week points��"+week);
		    			System.out.println("fullId�� "+stockFullId+" Month points��"+month);
		    			System.out.println("fullId�� "+stockFullId+" DayWeek��"+dayweek);
		    			System.out.println("fullId�� "+stockFullId+" DayWeekMonth��"+dayweekmonth);
		    			System.out.println("fullId�� "+stockFullId+" DayMonth��"+daymonth);
		    			System.out.println("fullId�� "+stockFullId+" WeekMonth��"+weekmonth);
		    			System.out.println("fullId�� "+stockFullId+" pointdefault��"+pointdefault);
		    			System.out.println("extremeCol:"+extremeCol);
		        	}

	   			}

	            wb.write(fileOStream);
	            fileOStream.flush();
	            fileIStream.close();
	            fileOStream.close();   
	   		 } 
    }   
    
    
   
    
    
  //������ҵ����
    public static void exportExcelAllStockForMarket(String filePath, String fileName) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException 
    {    
         FileOutputStream fileOStream=null;

         List<String> listIndustryStock = new ArrayList<String>();        
        List<StockIndustry> listIndustry = new ArrayList<StockIndustry>(); 
        	
        	 // ������   
       	 FileInputStream fileIStream = new FileInputStream(filePath + fileName);  
       	 XSSFWorkbook wb = new XSSFWorkbook(fileIStream);   
       	 XSSFSheet sheet = wb.getSheetAt(0);    
       	 //�����   
       	fileOStream = new FileOutputStream(filePath + fileName);
       	List<StockMarket> listStockMarket = new ArrayList<StockMarket>(); 
       	initFont(wb);
       	 listStockMarket=sbDao.getStockMarket(ConstantsInfo.StockMarket);
     	NumberFormat ddfDay=null;
    	//����������λС����
    	ddfDay=NumberFormat.getNumberInstance() ; 
    	ddfDay.setMaximumFractionDigits(2);
       	//�ȷ�������
         //����ָ�� һ��        
       	Row rowIndustry = sheet.createRow(stockRow);  
       	stockRow++;
       	create07Cell(wb, rowIndustry, 0, "����ָ��", cellStyleGroup);  
       	int stockType=0;
       	System.out.println(listStockMarket.size());
         	for(Iterator itMarket = listStockMarket.iterator();itMarket.hasNext();)
         	{
         		StockMarket sMarket = (StockMarket)itMarket.next();	
         		
   				String stockFullId = (String) sMarket.getCode().toString();	
   		   			 				

   				String stockName=sbDao.getStockNameFromIndustryTable(stockFullId);			
   			
   				System.out.println("fullId��"+stockFullId+" ���֣�"+stockName);
   				stockLogger.logger.debug("fullId��"+stockFullId+" ���֣�"+stockName);
   				//ÿ����Ʊ
   				Row rowDayData = sheet.createRow(stockRow);  
   				stockRow++;
    			create07Cell(wb, rowDayData, 0,  Integer.toString(stockRow), cellStyleNormal);  
	            create07Cell(wb, rowDayData, 1, stockFullId, cellStyleNormal);  
	            create07Cell(wb, rowDayData, 2, stockName, cellStyleNormal); 	            
	    
		        //��ǰ��ֵ��
	            StockData sday=getCurExtremeData(stockFullId,ConstantsInfo.DayDataType);
	            if(sday!=null)
	            	create07Cell(wb, rowDayData, 4, sday.getDate().toString(), cellStyleNormal); 	
	            StockData sweek=getCurExtremeData(stockFullId,ConstantsInfo.WeekDataType);
	            if(sweek!=null)
	               	create07Cell(wb, rowDayData, 5, sday.getDate().toString(), cellStyleNormal);
	            StockData smonth=getCurExtremeData(stockFullId,ConstantsInfo.MonthDataType);
	            if(smonth!=null)
	            	create07Cell(wb, rowDayData, 6, sday.getDate().toString(), cellStyleNormal);
	                    
	            
    			List<StockPoint> stockDayPoint=new ArrayList<StockPoint>();
    			List<StockPoint> stockWeekPoint=new ArrayList<StockPoint>();
    			List<StockPoint> stockMonthPoint=new ArrayList<StockPoint>();
    			List<String> stockExtremeDate=new ArrayList<String>();
    						
    			//���������ʱ��
    	        Date dt=new Date();
    	        String  cDate  = dateTimer.getAddDate(dt,-90);
    			//ȡ��������м�������
    			stockDayPoint=spDao.getRecentPointStock(stockFullId,ConstantsInfo.DayDataType,cDate);
    			stockWeekPoint=spDao.getRecentPointStock(stockFullId, ConstantsInfo.WeekDataType,cDate);
    			stockMonthPoint=spDao.getRecentPointStock(stockFullId,  ConstantsInfo.MonthDataType,cDate);
    			if(stockDayPoint==null) {
    				continue;
    			}
    			
    			//ȡ��������м���ʱ��
    			stockExtremeDate=spDao.getRecentExtremeDate(stockFullId,cDate);
    			
	        	
    		  	String showtext="";
    			int extremeCol=stockLabelNum;	
    		
    			//��ʱ���뼫�� hash 
                HashMap<String, StockPoint> stockDayDateExist = new HashMap<String, StockPoint>(); 	
    			//��ʱ���뼫�� hash 
                HashMap<String, StockPoint> stockWeekDateExist = new HashMap<String, StockPoint>(); 
               //��ʱ���뼫�� hash 
                HashMap<String, StockPoint> stockMonthDateExist = new HashMap<String, StockPoint>(); 
                for(Iterator itDay = stockDayPoint.iterator();itDay.hasNext();)
	 	    	{
	 	           StockPoint sPointDay=(StockPoint)itDay.next();	 	            
	 	           stockDayDateExist.put(sPointDay.getExtremeDate().toString(), sPointDay);
	 	    	}
                
    			for(Iterator itWeek = stockWeekPoint.iterator();itWeek.hasNext();)
	 	    	{
	 	           StockPoint sPointWeek=(StockPoint)itWeek.next();	 	            
	 	           stockWeekDateExist.put(sPointWeek.getExtremeDate().toString(), sPointWeek);
	 	    	}
    		
    			for(Iterator itMonth = stockMonthPoint.iterator();itMonth.hasNext();)
	 	    	{	
	 	            StockPoint sPointMonth=(StockPoint)itMonth.next();	 	            
	 	            stockMonthDateExist.put(sPointMonth.getExtremeDate().toString(), sPointMonth);
	 	    	}
    			
    			int type=0;  			
    			////�ж��գ��ܣ��¼�ֵ���Ƿ��ظ�
    			int day=0;
    			int week=0;
    			int month=0;
    			int dayweek=0;
    			int dayweekmonth=0;
    			int daymonth=0;
    			int weekmonth=0;
    			int pointdefault=0;
    			StockPoint sPointDay,sPointWeek,sPointMonth=null;
    			for(Iterator itDate = stockExtremeDate.iterator();itDate.hasNext();)
    			{
    				int dayType=0;
    				int weekType=0;
    				int monthType=0;
    				String extremeDate = (String) itDate.next();
    				 sPointDay=stockDayDateExist.get(extremeDate);
	            	if(sPointDay != null)
	            	{
	            		dayType=1;//ConstantsInfo.ExtremeDateDay;
	            	}
    				sPointWeek=stockWeekDateExist.get(extremeDate);
	            	if(sPointWeek!=null)
	            	{
	            		weekType=1;//ConstantsInfo.ExtremeDateWeek;
	            	}
	            	sPointMonth=stockMonthDateExist.get(extremeDate);
	            	if(sPointMonth!=null)
	            	{
	            		monthType=1;//ConstantsInfo.ExtremeDateDayWeekMonth;
	            	}
	            	
	            	if(dayType==1)//��
	            	{
	            		if(weekType==1)//��
	            		{
	            			if(monthType==1)		            			
	            				type=ConstantsInfo.ExtremeDateDayWeekMonth;	//������	         
	            			else
	            				type=ConstantsInfo.ExtremeDateDayWeek;//����
	            		}
	            		else
	            		{
	            			if(monthType==1)		            			
	            				type=ConstantsInfo.ExtremeDateDayMonth;	//����         
	            			else
	            				type=ConstantsInfo.ExtremeDateDay;//��
	            		}
	            	}
	            	else
	            	{
	            		if(weekType==1)//��
	            		{
	            			if(monthType==1)		            			
	            				type=ConstantsInfo.ExtremeDateWeekMonth;	//����	         
	            			else
	            				type=ConstantsInfo.ExtremeDateWeek;//��
	            		}
	            		else
	            		{
	            			if(monthType==1)		            			
	            				type=ConstantsInfo.ExtremeDateMonth;//��         
	            			
	            		}
	            		
	            	}
	            	String sDayRatio,sWeekRatio,sMonthRatio=null;	
	            	
	            	/*
	            	if(sPointDay!=null)
	            		System.out.println("day:"+sPointDay.getExtremeDate());
	            	if(sPointWeek!=null)
	            		System.out.println("week:"+sPointWeek.getExtremeDate());
	            	if(sPointMonth!=null)
	            		System.out.println("month"+sPointMonth.getExtremeDate());
	            	*/
	            	switch(type)
	            	{
	            	case ConstantsInfo.ExtremeDateDay:
	            		 
		            	sDayRatio= ddfDay.format(sPointDay.getRatio());
		            	//excel��ʾ��ֵ������
		            	showtext="�գ�"+sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%)"; 
		            	if(ConstantsInfo.DEBUG)
			        	{
			            	System.out.println("��ֵ��ʱ�䣺"+sPointDay.getExtremeDate());
			            	System.out.println("��ֵ�۸�"+sPointDay.getExtremePrice()); 
			            	System.out.println("������"+sPointDay.getRatio()); 
			            	System.out.println(showtext);	
			        	}
		            	
		            	extremeCol = stockDateColumnmap.get(sPointDay.getExtremeDate().toString());
		            	//System.out.println("Day��ֵ��ʱ�䣺"+extremeDate);
		            	//�� 
		            	if(sPointDay.getWillFlag() == 1) //����	
		            		create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleDay); 
		            	else 
		            		create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleDayWeek);
				       
				         day++;
	            		break;
	            	case ConstantsInfo.ExtremeDateDayWeek:
	            		 
		            	sDayRatio = ddfDay.format(sPointDay.getRatio());
		            	sWeekRatio = ddfDay.format(sPointWeek.getRatio());
		            	
		            	//excel��ʾ��ֵ������
		            	showtext="���ܣ�"+sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sWeekRatio+"%)"; 
		            	extremeCol = stockDateColumnmap.get(sPointDay.getExtremeDate().toString());
		            	
		            	if(sPointDay.getWillFlag() == 1) //����	
		            		create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleDay); 
		            	else 
		            		create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleDayWeek);
				       
		            	dayweek++;
	            		break;
	            	case ConstantsInfo.ExtremeDateDayWeekMonth:
	            		
		            	sDayRatio= ddfDay.format(sPointDay.getRatio());
		            	sWeekRatio= ddfDay.format(sPointWeek.getRatio());
		            	sMonthRatio= ddfDay.format(sPointMonth.getRatio());
		            	//System.out.println("DayWeekMonth��ֵ��ʱ�䣺"+extremeDate);
		            	//excel��ʾ��ֵ������
		            	showtext="�����£�"+sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sWeekRatio+"%,"+sMonthRatio+"%)"; 
		            	extremeCol = stockDateColumnmap.get(sPointDay.getExtremeDate().toString());
		            
		            	if(sPointDay.getWillFlag() == 1) //����	
		            		create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleDay); 
		            	else 
		            		create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleDayWeek);
				       
		            	//create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleDayWeekMonth); 
		            	
		            	dayweekmonth++;
		            	break;
	            	case ConstantsInfo.ExtremeDateWeek:		            		 
		            	sWeekRatio= ddfDay.format(sPointWeek.getRatio()) ;
		            	//excel��ʾ��ֵ������
		            	showtext="�ܣ�"+sPointWeek.getExtremeDate()+":"+Float.toString(sPointWeek.getExtremePrice())+"("+sWeekRatio+"%)"; 
		            	//System.out.println("Week��ֵ��ʱ�䣺"+extremeDate);
		            	extremeCol = stockDateColumnmap.get(sPointWeek.getExtremeDate().toString());
				         create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleWeek); 
				         
				         week++;
				         break;
	            	case ConstantsInfo.ExtremeDateMonth:		            		 
	            		sMonthRatio= ddfDay.format(sPointMonth.getRatio()) ;
		            	//excel��ʾ��ֵ������
	            		//System.out.println("Month��ֵ��ʱ�䣺"+extremeDate);
		            	showtext="�£�"+sPointMonth.getExtremeDate()+":"+Float.toString(sPointMonth.getExtremePrice())+"("+sMonthRatio+"%)"; 
		            	extremeCol = stockDateColumnmap.get(sPointMonth.getExtremeDate().toString());
				         create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleMonth); 
				       	
				         month++;
				         break;
	            	case ConstantsInfo.ExtremeDateDayMonth:
	            		
	            		sDayRatio = ddfDay.format(sPointDay.getRatio());
		            	sMonthRatio = ddfDay.format(sPointMonth.getRatio());
		            
		            	//excel��ʾ��ֵ������
		            	showtext="���£�"+sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sMonthRatio+"%)"; 
		            	extremeCol = stockDateColumnmap.get(sPointDay.getExtremeDate().toString());
		            	create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleMonth); //cellStyleDayMonth
		          
	            		daymonth++;		 
	            		//System.out.println("DayMonth��ֵ��ʱ�䣺"+extremeDate);
	            		break;
	            	case ConstantsInfo.ExtremeDateWeekMonth:
	            		sWeekRatio = ddfDay.format(sPointWeek.getRatio());
		            	sMonthRatio = ddfDay.format(sPointMonth.getRatio());
		            
		            	//excel��ʾ��ֵ������
		            	showtext="���£�"+sPointWeek.getExtremeDate()+":"+Float.toString(sPointWeek.getExtremePrice())+"("+sWeekRatio+"%,"+sMonthRatio+"%)"; 
		            	extremeCol = stockDateColumnmap.get(sPointWeek.getExtremeDate().toString());
		            	create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleMonth); //cellStyleWeekMonth
	            		
	            		weekmonth++;	
	            		//System.out.println("WeekMonth��ֵ��ʱ�䣺"+extremeDate);
	            		break;
	            	default:
	            		pointdefault++;	
	            		//System.out.println("default��ֵ��ʱ�䣺"+extremeDate);
	            		break;	            		
	            	}	
	            	
    			}	
    			
    			if(ConstantsInfo.DEBUG)
	        	{
	    			System.out.println("fullId�� "+stockFullId+" Day points��"+day);
	    			System.out.println("fullId�� "+stockFullId+" Week points��"+week);
	    			System.out.println("fullId�� "+stockFullId+" Month points��"+month);
	    			System.out.println("fullId�� "+stockFullId+" DayWeek��"+dayweek);
	    			System.out.println("fullId�� "+stockFullId+" DayWeekMonth��"+dayweekmonth);
	    			System.out.println("fullId�� "+stockFullId+" DayMonth��"+daymonth);
	    			System.out.println("fullId�� "+stockFullId+" WeekMonth��"+weekmonth);
	    			System.out.println("fullId�� "+stockFullId+" pointdefault��"+pointdefault);
	    			System.out.println("extremeCol:"+extremeCol);
	        	}

   			}

            wb.write(fileOStream);
            fileOStream.flush();
            fileIStream.close();
            fileOStream.close();   
	   		 
    }   
    
    public static void exportStockInfo(String stockFullId,XSSFSheet sheet) throws SQLException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, IOException
    {
    	NumberFormat ddfDay=null;
    	//����������λС����
    	ddfDay=NumberFormat.getNumberInstance() ; 
    	ddfDay.setMaximumFractionDigits(2);
    
    	String stockName=sbDao.getStockNameFromIndustryTable(stockFullId);			
		
		System.out.println("fullId��"+stockFullId+" ���֣�"+stockName);
		stockLogger.logger.debug("fullId��"+stockFullId+" ���֣�"+stockName);
		//ÿ����Ʊ
		Row rowDayData = sheet.createRow(stockRow);  
		stockRow++;
	create07Cell(wb, rowDayData, 0,  Integer.toString(stockRow), cellStyleNormal);  
    create07Cell(wb, rowDayData, 1, stockFullId, cellStyleNormal);  
    create07Cell(wb, rowDayData, 2, stockName, cellStyleNormal); 	            

    /*
    //��ǰ��ֵ��
    StockData sday=getCurExtremeData(stockFullId,ConstantsInfo.DayDataType);
    if(sday!=null)
    	create07Cell(wb, rowDayData, 4, sday.getDate().toString(), cellStyleNormal); 	
    StockData sweek=getCurExtremeData(stockFullId,ConstantsInfo.WeekDataType);
    if(sweek!=null)
       	create07Cell(wb, rowDayData, 5, sday.getDate().toString(), cellStyleNormal);
    StockData smonth=getCurExtremeData(stockFullId,ConstantsInfo.MonthDataType);
    if(smonth!=null)
    	create07Cell(wb, rowDayData, 6, sday.getDate().toString(), cellStyleNormal);
     */      
    
	List<StockPoint> stockDayPoint=new ArrayList<StockPoint>();
	List<StockPoint> stockWeekPoint=new ArrayList<StockPoint>();
	List<StockPoint> stockMonthPoint=new ArrayList<StockPoint>();
	List<String> stockExtremeDate=new ArrayList<String>();
				
	//���������ʱ��
    Date dt=new Date();
    String  cDate  = dateTimer.getAddDate(dt,-90);
	//ȡ��������м�������
	stockDayPoint=spDao.getRecentPointStock(stockFullId,ConstantsInfo.DayDataType,cDate);
	if(stockDayPoint==null) {
		return;
	}
	stockWeekPoint=spDao.getRecentPointStock(stockFullId, ConstantsInfo.WeekDataType,cDate);
	stockMonthPoint=spDao.getRecentPointStock(stockFullId,  ConstantsInfo.MonthDataType,cDate);
	
	
	//ȡ��������м���ʱ��
	stockExtremeDate=spDao.getRecentExtremeDate(stockFullId,cDate);
	
	
  	String showtext="";
	int extremeCol=stockLabelNum;	

	//��ʱ���뼫�� hash 
    HashMap<String, StockPoint> stockDayDateExist = new HashMap<String, StockPoint>(); 	
	//��ʱ���뼫�� hash 
    HashMap<String, StockPoint> stockWeekDateExist = new HashMap<String, StockPoint>(); 
   //��ʱ���뼫�� hash 
    HashMap<String, StockPoint> stockMonthDateExist = new HashMap<String, StockPoint>(); 
    for(Iterator itDay = stockDayPoint.iterator();itDay.hasNext();)
 	{
        StockPoint sPointDay=(StockPoint)itDay.next();	 	            
        stockDayDateExist.put(sPointDay.getExtremeDate().toString(), sPointDay);
 	}
    
	for(Iterator itWeek = stockWeekPoint.iterator();itWeek.hasNext();)
 	{
        StockPoint sPointWeek=(StockPoint)itWeek.next();	 	            
        stockWeekDateExist.put(sPointWeek.getExtremeDate().toString(), sPointWeek);
 	}

	for(Iterator itMonth = stockMonthPoint.iterator();itMonth.hasNext();)
 	{	
         StockPoint sPointMonth=(StockPoint)itMonth.next();	 	            
         stockMonthDateExist.put(sPointMonth.getExtremeDate().toString(), sPointMonth);
 	}
	
	int type=0;  			
	////�ж��գ��ܣ��¼�ֵ���Ƿ��ظ�
	int day=0;
	int week=0;
	int month=0;
	int dayweek=0;
	int dayweekmonth=0;
	int daymonth=0;
	int weekmonth=0;
	int pointdefault=0;
	StockPoint sPointDay,sPointWeek,sPointMonth=null;
	for(Iterator itDate = stockExtremeDate.iterator();itDate.hasNext();)
	{
		int dayType=0;
		int weekType=0;
		int monthType=0;
		String extremeDate = (String) itDate.next();
		 sPointDay=stockDayDateExist.get(extremeDate);
    	if(sPointDay != null)
    	{
    		dayType=1;//ConstantsInfo.ExtremeDateDay;
    	}
		sPointWeek=stockWeekDateExist.get(extremeDate);
    	if(sPointWeek!=null)
    	{
    		weekType=1;//ConstantsInfo.ExtremeDateWeek;
    	}
    	sPointMonth=stockMonthDateExist.get(extremeDate);
    	if(sPointMonth!=null)
    	{
    		monthType=1;//ConstantsInfo.ExtremeDateDayWeekMonth;
    	}
    	
    	if(dayType==1)//��
    	{
    		if(weekType==1)//��
    		{
    			if(monthType==1)		            			
    				type=ConstantsInfo.ExtremeDateDayWeekMonth;	//������	         
    			else
    				type=ConstantsInfo.ExtremeDateDayWeek;//����
    		}
    		else
    		{
    			if(monthType==1)		            			
    				type=ConstantsInfo.ExtremeDateDayMonth;	//����         
    			else
    				type=ConstantsInfo.ExtremeDateDay;//��
    		}
    	}
    	else
    	{
    		if(weekType==1)//��
    		{
    			if(monthType==1)		            			
    				type=ConstantsInfo.ExtremeDateWeekMonth;	//����	         
    			else
    				type=ConstantsInfo.ExtremeDateWeek;//��
    		}
    		else
    		{
    			if(monthType==1)		            			
    				type=ConstantsInfo.ExtremeDateMonth;//��         
    			
    		}
    		
    	}
    	String sDayRatio,sWeekRatio,sMonthRatio=null;	
    	
    	/*
    	if(sPointDay!=null)
    		System.out.println("day:"+sPointDay.getExtremeDate());
    	if(sPointWeek!=null)
    		System.out.println("week:"+sPointWeek.getExtremeDate());
    	if(sPointMonth!=null)
    		System.out.println("month"+sPointMonth.getExtremeDate());
    	*/
    	switch(type)
    	{
    	case ConstantsInfo.ExtremeDateDay:
    		    	
        	if(ConstantsInfo.DEBUG)
        	{
            	System.out.println("��ֵ��ʱ�䣺"+sPointDay.getExtremeDate());
            	System.out.println("��ֵ�۸�"+sPointDay.getExtremePrice()); 
            	System.out.println("������"+sPointDay.getRatio()); 
            	System.out.println(showtext);	
        	}
        	
        	extremeCol = stockDateColumnmap.get(sPointDay.getExtremeDate().toString());
        	//System.out.println("Day��ֵ��ʱ�䣺"+extremeDate);
        	//�� 
        	if(sPointDay.getWillFlag() == 1) //����	
        		create07Cell(wb, rowDayData, extremeCol, "����ת��", cellStyleDay); 
        	else 
        		create07Cell(wb, rowDayData, extremeCol, "����ת��", cellStyleDayWeek);
	       
	         day++;
    		break;
    	case ConstantsInfo.ExtremeDateDayWeek:
     	
        	extremeCol = stockDateColumnmap.get(sPointDay.getExtremeDate().toString());
        	
        	if(sPointDay.getWillFlag() == 1) //����	
        		create07Cell(wb, rowDayData, extremeCol, "������ת��", cellStyleDay); 
        	else 
        		create07Cell(wb, rowDayData, extremeCol, "������ת��", cellStyleDayWeek);
	       
        	dayweek++;
    		break;
    	case ConstantsInfo.ExtremeDateDayWeekMonth:
        	extremeCol = stockDateColumnmap.get(sPointDay.getExtremeDate().toString());
        
        	if(sPointDay.getWillFlag() == 1) //����	
        		create07Cell(wb, rowDayData, extremeCol,"��������ת��", cellStyleDay); 
        	else 
        		create07Cell(wb, rowDayData, extremeCol, "��������ת��", cellStyleDayWeek);
	       
        	dayweekmonth++;
        	break;
    	case ConstantsInfo.ExtremeDateWeek:		            		 
        	
        	extremeCol = stockDateColumnmap.get(sPointWeek.getExtremeDate().toString());
	         if(sPointWeek.getWillFlag() == 1) //����	
	        		create07Cell(wb, rowDayData, extremeCol,"����ת��", cellStyleDay); 
	        	else 
	        		create07Cell(wb, rowDayData, extremeCol, "����ת��", cellStyleDayWeek);
	         week++;
	         break;
    	case ConstantsInfo.ExtremeDateMonth:		            		 
    		extremeCol = stockDateColumnmap.get(sPointMonth.getExtremeDate().toString());      
        	 if(sPointMonth.getWillFlag() == 1) //����	
	        		create07Cell(wb, rowDayData, extremeCol,"����ת��", cellStyleDay); 
	        	else 
	        		create07Cell(wb, rowDayData, extremeCol, "����ת��", cellStyleDayWeek);
	       	
	         month++;
	         break;
    	case ConstantsInfo.ExtremeDateDayMonth:  		
        	extremeCol = stockDateColumnmap.get(sPointDay.getExtremeDate().toString());
        	 if(sPointDay.getWillFlag() == 1) //����	
	        		create07Cell(wb, rowDayData, extremeCol,"������ת��", cellStyleDay); 
	        	else 
	        		create07Cell(wb, rowDayData, extremeCol, "������ת��", cellStyleDayWeek);
    			 
    		//System.out.println("DayMonth��ֵ��ʱ�䣺"+extremeDate);
    		break;
    	case ConstantsInfo.ExtremeDateWeekMonth:
    
    		extremeCol = stockDateColumnmap.get(sPointWeek.getExtremeDate().toString());
    		if(sPointWeek.getWillFlag() == 1) //����	
	        		create07Cell(wb, rowDayData, extremeCol,"������ת��", cellStyleDay); 
	        	else 
	        		create07Cell(wb, rowDayData, extremeCol, "������ת��", cellStyleDayWeek);
 	
    		//System.out.println("WeekMonth��ֵ��ʱ�䣺"+extremeDate);
    		break;
    	default:
    		pointdefault++;	
    		//System.out.println("default��ֵ��ʱ�䣺"+extremeDate);
    		break;	            		
    	}	
    	
	}	
	
	
    }
    
  //������ҵ����
    public static void exportStockRecentlyForMarket(String filePath, String fileName) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException 
    {    
         FileOutputStream fileOStream=null;

         List<String> listIndustryStock = new ArrayList<String>();        
        List<StockIndustry> listIndustry = new ArrayList<StockIndustry>(); 
        	
        	 // ������   
       	 FileInputStream fileIStream = new FileInputStream(filePath + fileName);  
       	 XSSFWorkbook wb = new XSSFWorkbook(fileIStream);   
       	 XSSFSheet sheet = wb.getSheetAt(0);    
       	 //�����   
       	fileOStream = new FileOutputStream(filePath + fileName);
       	List<StockMarket> listStockMarket = new ArrayList<StockMarket>(); 
       	initFont(wb);
       	listStockMarket=sbDao.getStockMarket(ConstantsInfo.StockMarket);
     
       	//�ȷ�������
         //����ָ�� һ��        
       	Row rowIndustry = sheet.createRow(stockRow);  
       	stockRow++;
       	create07Cell(wb, rowIndustry, 0, "����ָ��", cellStyleGroup);  
       
       	System.out.println(listStockMarket.size());
     	for(Iterator itMarket = listStockMarket.iterator();itMarket.hasNext();)
     	{
     		StockMarket sMarket = (StockMarket)itMarket.next();	
     		
			String stockFullId = (String) sMarket.getCode().toString();	
	   			 				
			exportStockInfo(stockFullId,sheet);
			
		}

            wb.write(fileOStream);
            fileOStream.flush();
            fileIStream.close();
            fileOStream.close();   
	   		 
    }   
    
    //������ҵ����
    public static void exportStockRecentlyForIndustry(String filePath, String fileName) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException 
    {    
            FileOutputStream fileOStream=null;
      
        	List<StockIndustry> listIndustry = new ArrayList<StockIndustry>();         	
        	
        	 //�õ���ǰ������ҵ
        	listIndustry=sbDao.getStockIndustry();        	
        	System.out.println("��ҵ������"+listIndustry.size());        	
 
            //�ڶ��п�ʼչʾ              
            Iterator itIndustry,ie;
	   		for(itIndustry = listIndustry.iterator();itIndustry.hasNext();)
	   		{
	   			// ������   
				 FileInputStream fileIStream = new FileInputStream(filePath + fileName);  
				 XSSFWorkbook wb = new XSSFWorkbook(fileIStream);   
				 XSSFSheet sheet = wb.getSheetAt(0);  
				// System.out.println("���һ�У�"+sheet.getLastRowNum());   
				 //������   
				 fileOStream = new FileOutputStream(filePath + fileName);
				
				 initFont(wb);
				 
				 //��ǰ��ҵ
				 StockIndustry indu= (StockIndustry)itIndustry.next();	
				 String induName=indu.getThirdname();
				 if(induName==null)
					 continue;				
				stockLogger.logger.debug("��ҵ��"+induName);
	   			   			
	   			//��ҵ
	   			Row rowIndustry = sheet.createRow(stockRow);  
	   			create07Cell(wb, rowIndustry, 0, induName, cellStyleGroup);  
	   			stockRow++;
	   			   			   	   			
	   			//������ҵ���й�Ʊ
	   			List<StockToIndustry> listIndustryStock = new ArrayList<StockToIndustry>();   
	   			listIndustryStock=sbDao.getIndustryToStock(indu.getThirdcode());  		
	   			stockLogger.logger.debug("��ҵ��Ʊ����"+listIndustryStock.size());
	   			for(ie=listIndustryStock.iterator();ie.hasNext();)
	   			{
	   				//��������	   			
	   			//	if(stockRow>20)
	   			//		break;
	   				StockToIndustry toInduStock = (StockToIndustry) ie.next();
	   				String stockFullId = toInduStock.getStockFullId();	
	   				System.out.println("stockFullId:"+stockFullId);			
	   				exportStockInfo(stockFullId,sheet);
	   			}

	            wb.write(fileOStream);
	            fileOStream.flush();
	            fileIStream.close();
	            fileOStream.close();   
	   		 } 
    }   
    
        
        //public static void create07Cell(Workbook wb, Row row, int column, String value, XSSFFont font,short color) {  
        public static void create07Cell(Workbook wb, Row row, int column, String value, CellStyle cellStyle) {  
            Cell cell = row.createCell(column);
            cell.setCellValue(value);  
            cell.setCellStyle(cellStyle);  
        }  
        
       
        //public static void create07Cell(Workbook wb, Row row, int column, String value, XSSFFont font,short color) {  
        public static void create07Cell(Workbook wb, Row row, int column, String value, XSSFFont font,int color) {  
            Cell cell = row.createCell(column);  
            cell.setCellValue(value);  
            CellStyle cellStyle = wb.createCellStyle();  
            cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);  
            cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_BOTTOM);  
            cellStyle.setFont(font);  
          //  cellStyle.setFillBackgroundColor(HSSFColor.BLUE.index);
           // IndexedColors.YELLOW.getIndex()
            if(color>0)
            {	
            	cellStyle.setFillForegroundColor((short) color);            
            	cellStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);  
            }
            cell.setCellStyle(cellStyle);  
        } 
        
        /** 
         * 07excel �������� 
         */   
        public static XSSFFont create07Fonts(Workbook wb, short bold, String fontName, boolean isItalic, short hight,XSSFColor color) {  
        	XSSFFont font = (XSSFFont) wb.createFont();  
            font.setFontName(fontName);  
            font.setBoldweight(bold);  
            font.setItalic(isItalic);  
            font.setFontHeight(hight);  
            font.setColor(color);             
            return font;  
        }  
      
        /** 
         * �ж��Ƿ�Ϊ���� 
         *  
         * @param str 
         * @return 
         */  
        public static boolean isNumeric(String str) {  
            if (str == null || "".equals(str.trim()) || str.length() > 10)  
                return false;  
            Pattern pattern = Pattern.compile("^0|[1-9]\\d*(\\.\\d+)?$");  
            return pattern.matcher(str).matches();  
        } 
        
        
        
        public static void exportRecentlyStock(String filePath, String fileName) throws IOException, SQLException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException
        {
        	 createExcel(filePath,fileName);
             exportStockRecentlyForMarket(filePath,fileName);
             exportStockRecentlyForIndustry(filePath,fileName);//��Ʊ��ֵ����ʾһ�� ��ȡpointStock��
        }
        
        public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException {
    		
        	PropertyConfigurator.configure("stockConf/log4j_excelWriter.properties");
        	
    		StockRecentWriterV1 ew=new StockRecentWriterV1();
    		
    		HSSFWorkbook wb;
    		Date startDate = new Date();
    	//	wb=ew.exportExcelForStock();
    		
    		//XSSFWorkbook xwb;
    		//xwb=ew.exportExcelForAllStock();
    		
    	
   	     Connection stockBaseConn = DbConn.getConnDB("stockConf/conn_base_db.ini"); 
   	     Connection stockDataConn = DbConn.getConnDB("stockConf/conn_data_db.ini"); 
   	     Connection stockPointConn = DbConn.getConnDB("stockConf/conn_point_db.ini");  
   	     spDao =new StockPointDao(stockPointConn);
   			sdDao =new StockDataDao(stockDataConn);
   			sbDao =new StockBaseDao(stockBaseConn);   		
   		
    		//��ǰ·��
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
            String dateNowStr = sdf.format(startDate);  
            String excleFileName="stockIndustryPoint_R_"+dateNowStr+".xlsx";
            
            ew.exportRecentlyStock("export//",excleFileName);
          
    		//��ǰ·��
    	//	ew.exportExcelAllStockForConcept("load\\","stockPointConcept.xlsx");//��Ʊ��ֵ����ʾһ�� ��ȡpointStock��
    		
    		//wb=ew.exportExcelForStock("sh60002",null,null,null);
    		
    		
    		stockBaseConn.close();
    		stockPointConn.close();
    		stockDataConn.close();
    		
    		Date endDate = new Date();
    		// ��������ʱ�����������
    		long seconds =(endDate.getTime() - startDate.getTime())/1000;
    		System.out.println("�ܹ���ʱ��"+seconds+"��");
    		System.out.println("end");
    		stockLogger.logger.debug("end");
    			
    	}
}
