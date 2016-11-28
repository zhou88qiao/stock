package excel.rw;

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
import dao.StockDataDao;
import dao.StockIndustry;
import dao.StockInformation;
import dao.StockInformationDao;
import dao.StockPoint;
import dao.StockPointDao;

public class ExcelWriter {
	
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
	static int stockLabelNum=5;
	
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
    public static void createExcel(String filePath, String fileName) throws IOException
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
		 
		  //�п�
         for(i=0;i<stockLabelNum+20;i++)
         	sheet.setColumnWidth(i, 6000);
         
      
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
         create07Cell(wb, rowHead, 4, "����", cellStyleTitle); 
         
         create07Cell(wb, rowHead, col, "��", cellStyleTitle); 
         create07Cell(wb, rowHead, col++, "ǰ��������", cellStyleTitle);
         create07Cell(wb, rowHead, col++, "������ߵ�����", cellStyleTitle);
         create07Cell(wb, rowHead, col++, "�ߵ�۸�", cellStyleTitle); 
         create07Cell(wb, rowHead, col++, "��", cellStyleTitle); 
         create07Cell(wb, rowHead, col++, "��", cellStyleTitle); 
         create07Cell(wb, rowHead, col++, "ת", cellStyleTitle); 
         create07Cell(wb, rowHead, col++, "��", cellStyleTitle); 
         create07Cell(wb, rowHead, col++, "ǰ��������", cellStyleTitle);
         create07Cell(wb, rowHead, col++, "������ߵ�����", cellStyleTitle);
         create07Cell(wb, rowHead, col++, "�ߵ�۸�", cellStyleTitle); 
         create07Cell(wb, rowHead, col++, "��", cellStyleTitle); 
         create07Cell(wb, rowHead, col++, "��", cellStyleTitle); 
         create07Cell(wb, rowHead, col++, "ת", cellStyleTitle); 
         create07Cell(wb, rowHead, col++, "��", cellStyleTitle);      

		 wb.write(os);
		 os.close();
    }
    
    
    //���ݸ�������
    public static void exportExcelAllStockForConcept(String filePath, String fileName) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException 
    {    
            FileOutputStream fileOStream=null;

            List<String> listConceptStock = new ArrayList<String>();        
        	List<StockConcept> listConcept = new ArrayList<StockConcept>(); 
   
        	
        	//����excel
        	createExcel(filePath,fileName);
        	
        	 //�õ���ǰ���и���
        	listConcept=sbDao.getStockConcept();
        	System.out.println("���������"+listConcept.size());
        	
        	//����������λС����
        	NumberFormat ddfDay=null;
        	ddfDay=NumberFormat.getNumberInstance() ; 
        	ddfDay.setMaximumFractionDigits(2);
        	
            //�ڶ��п�ʼչʾ  
            int stockRow = 1;        
            Iterator itConcept,ie;
        	int monthpointcount=0;
        	int monthpoint1count=0;
	   		for(itConcept = listConcept.iterator();itConcept.hasNext();)
	   		{
	   			// ������   
				 FileInputStream  fileIStream = new FileInputStream(filePath + fileName);  
				 XSSFWorkbook wb = new XSSFWorkbook(fileIStream);   
				 XSSFSheet sheet = wb.getSheetAt(0);  
				//�����	       
				 fileOStream = new FileOutputStream(filePath + fileName);
				 
				 initFont(wb);
				 
				 //��ǰ����
				 StockConcept concept= (StockConcept)itConcept.next();	
				 String conceptName=concept.getName();
				 if(conceptName==null)
					 continue;
				 
				stockLogger.logger.fatal("���"+conceptName);
				   			
	   			//����
	   			Row rowConcept = sheet.createRow(stockRow);  
	   			create07Cell(wb, rowConcept, 0, conceptName, cellStyleGroup);  
	   			stockRow++;
	   			
	   			DecimalFormat decimalFormat=new DecimalFormat(".00");
	   			//�����������й�Ʊ
	   			listConceptStock=sbDao.getConceptStock(conceptName);
	   			
	   			stockLogger.logger.debug("�����Ʊ����"+listConceptStock.size());
	   			for(ie=listConceptStock.iterator();ie.hasNext();)
	   			{
	   				
	   			//��������
		   		//	if(stockRow>10)
		   		//		break;
	   				
	   				String stockFullId = (String) ie.next();	
	   				int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_DATA_STOCK);
	   				if(isTableExist==0)//������
	   					continue;   				

	   				String stockName=sbDao.getStockNameFromConceptTable(stockFullId);			
	   				System.out.println("fullId��"+stockFullId+" ���֣�"+stockName);
	   				stockLogger.logger.fatal("fullId��"+stockFullId+" ���֣�"+stockName);
	   				
	   				//ÿ����Ʊ
	   				Row rowDayData = sheet.createRow(stockRow);  
	    			create07Cell(wb, rowDayData, 0,  Integer.toString(stockRow), cellStyleNormal);  
		            create07Cell(wb, rowDayData, 1, stockFullId, cellStyleNormal);  
		            create07Cell(wb, rowDayData, 2, stockName, cellStyleNormal); 	            
		           // create07Cell(wb, rowDayData, 3, si.getStockRegional(), font0,ConstantsInfo.XSSFNORMAL); 
		            stockRow++;
  
		       //    if(!stockFullId.equals("sh600000"))
				//		continue;
	    			//if(!stockFullId.equals("sh600000") && !stockFullId.equals("sz002142") && !stockFullId.equals("sz000001"))
	    			//		continue;
		            
	    			List<StockPoint> stockDayPoint=new ArrayList<StockPoint>();
	    			List<StockPoint> stockWeekPoint=new ArrayList<StockPoint>();
	    			List<StockPoint> stockMonthPoint=new ArrayList<StockPoint>();
	    			
	    			//ȡ�����м�������
	    			stockDayPoint=spDao.getAllPointStock(stockFullId,ConstantsInfo.DayDataType);
	    			stockWeekPoint=spDao.getAllPointStock(stockFullId, ConstantsInfo.WeekDataType);
	    			stockMonthPoint=spDao.getAllPointStock(stockFullId,  ConstantsInfo.MonthDataType);
	    			if(stockDayPoint==null)
	    				continue;
	    			
	    			List<String> stockExtremeDate=new ArrayList<String>();					
	    			//ȡ�����м���ʱ��
	    			stockExtremeDate=spDao.getAllExtremeDate(stockFullId);
	    			
	    			if(ConstantsInfo.DEBUG)
		        	{
		    			System.out.println("day size:"+stockDayPoint.size());
		    			System.out.println("week size:"+stockWeekPoint.size());
		    			System.out.println("month size:"+stockMonthPoint.size());    			
		        	}
	    		
	    			if(stockMonthPoint.size()>=3)
	    				monthpointcount++;
	    			else
	    				monthpoint1count++;
	    		//	System.out.println("month size:"+stockMonthPoint.size()); 
	    			
	    			
	    			int extremeCol=stockLabelNum;	
	    			//��һ������Ԥ��ֵ
	    			int daySize=stockDayPoint.size();
	    			int weekSize=stockWeekPoint.size();
	    			int monthSize=stockMonthPoint.size();
	    			//����6��Ԥ��ֵ
	    			String[] nextPointPrice=new String[6];
	    			String showtext=null;
	    			
	    			//��Ԥ��
	    			if(daySize>=2)
	    			{
	    				String nextDate=spDao.getNextPointDate(stockDayPoint.get(daySize-1).getExtremeDate().toString(),stockDayPoint.get(daySize-2).getExtremeDate().toString());
	    				showtext=nextDate+"(";
	    				float lastPrice=stockDayPoint.get(daySize-1).getExtremePrice();
	    				float priPrice=stockDayPoint.get(daySize-2).getExtremePrice();
	    				nextPointPrice[0]=decimalFormat.format((lastPrice-0.382*(lastPrice-priPrice)));
	    				nextPointPrice[1]=decimalFormat.format((lastPrice-0.5*(lastPrice-priPrice)));
	    				nextPointPrice[2]=decimalFormat.format((lastPrice-0.618*(lastPrice-priPrice)));
	    				nextPointPrice[3]=decimalFormat.format((lastPrice-0.75*(lastPrice-priPrice)));
	    				nextPointPrice[4]=decimalFormat.format((priPrice));
	    				nextPointPrice[5]=decimalFormat.format((lastPrice-1.08*(lastPrice-priPrice)));	
	    				System.out.println("�ռ�ֵԤ���ʱ�䣺"+nextDate);	
	    				for(int i=0;i<6;i++)
	    				{
	    					if(ConstantsInfo.DEBUG)
				        	{
				            	System.out.println("�ռ�ֵԤ���λ"+i+":"+nextPointPrice[i]);	
				        	}
	    					showtext=showtext+nextPointPrice[i]+","; 
	    				}
	    				showtext=showtext+")";
		            	
		            	create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleYuce); 
		            	extremeCol++;
	    			}
	    			
	    			//��Ԥ��
	    			if(weekSize>=2)
	    			{
	    				String nextDate=spDao.getNextPointDate(stockWeekPoint.get(weekSize-1).getExtremeDate().toString(),stockWeekPoint.get(weekSize-2).getExtremeDate().toString());
	    				showtext=nextDate+"(";
	    				float lastPrice=stockWeekPoint.get(weekSize-1).getExtremePrice();
	    				float priPrice=stockWeekPoint.get(weekSize-2).getExtremePrice();
	    				nextPointPrice[0]=decimalFormat.format((lastPrice-0.382*(lastPrice-priPrice)));
	    				nextPointPrice[1]=decimalFormat.format((lastPrice-0.5*(lastPrice-priPrice)));
	    				nextPointPrice[2]=decimalFormat.format((lastPrice-0.618*(lastPrice-priPrice)));
	    				nextPointPrice[3]=decimalFormat.format((lastPrice-0.75*(lastPrice-priPrice)));
	    				nextPointPrice[4]=decimalFormat.format((priPrice));
	    				nextPointPrice[5]=decimalFormat.format((lastPrice-1.08*(lastPrice-priPrice)));	
	    				System.out.println("�ܼ�ֵԤ���ʱ�䣺"+nextDate);	
	    				for(int i=0;i<6;i++)
	    				{
	    					if(ConstantsInfo.DEBUG)
				        	{
				            	System.out.println("�ܼ�ֵԤ���λ"+i+":"+nextPointPrice[i]);	
				        	}
	    					showtext=showtext+nextPointPrice[i]+","; 
	    				}
	    				showtext=showtext+")";
		            	create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleYuce); 
		            	extremeCol++;
	    			}
	    			
	    			//��Ԥ��
	    			if(monthSize>=2)
	    			{
	    				String nextDate=spDao.getNextPointDate(stockMonthPoint.get(monthSize-1).getExtremeDate().toString(),stockMonthPoint.get(monthSize-2).getExtremeDate().toString());
	    				showtext=nextDate+"(";
	    				float lastPrice=stockDayPoint.get(monthSize-1).getExtremePrice();
	    				float priPrice=stockDayPoint.get(monthSize-2).getExtremePrice();
	    				nextPointPrice[0]=decimalFormat.format((lastPrice-0.382*(lastPrice-priPrice)));
	    				nextPointPrice[1]=decimalFormat.format((lastPrice-0.5*(lastPrice-priPrice)));
	    				nextPointPrice[2]=decimalFormat.format((lastPrice-0.618*(lastPrice-priPrice)));
	    				nextPointPrice[3]=decimalFormat.format((lastPrice-0.75*(lastPrice-priPrice)));
	    				nextPointPrice[4]=decimalFormat.format((priPrice));
	    				nextPointPrice[5]=decimalFormat.format((lastPrice-1.08*(lastPrice-priPrice)));	
	    				System.out.println("�¼�ֵԤ���ʱ�䣺"+nextDate);	
	    				for(int i=0;i<6;i++)
	    				{
	    					if(ConstantsInfo.DEBUG)
				        	{
				            	System.out.println("�¼�ֵԤ���λ"+i+":"+nextPointPrice[i]);	
				        	}
	    					showtext=showtext+nextPointPrice[i]+","; 
	    				}
	    				showtext=showtext+")";
		            	create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleYuce); 
		            	extremeCol++;
	    			}
	    			
	    			extremeCol=stockLabelNum+3;
	    			
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
	    			for(Iterator itDate = stockExtremeDate.iterator();itDate.hasNext();)
	    			{
	    				int dayType=0;
	    				int weekType=0;
	    				int monthType=0;
	    				String extremeDate = (String) itDate.next();
	    				StockPoint sPointDay=stockDayDateExist.get(extremeDate);
		            	if(sPointDay != null)
		            	{
		            		dayType=1;//ConstantsInfo.ExtremeDateDay;
		            	}
	    				StockPoint sPointWeek=stockWeekDateExist.get(extremeDate);
		            	if(sPointWeek!=null)
		            	{
		            		weekType=1;//ConstantsInfo.ExtremeDateWeek;
		            	}
		            	StockPoint sPointMonth=stockMonthDateExist.get(extremeDate);
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
		            	
		            	
		            	switch(type)
		            	{
		            	case ConstantsInfo.ExtremeDateDay:
		            		 
			            	sDayRatio= ddfDay.format(sPointDay.getRatio()) ;
			            	//excel��ʾ��ֵ������
			            	showtext="�գ�"+sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%)"; 
			            	if(ConstantsInfo.DEBUG)
				        	{
				            	System.out.println("��ֵ��ʱ�䣺"+sPointDay.getExtremeDate());
				            	System.out.println("��ֵ�۸�"+sPointDay.getExtremePrice()); 
				            	System.out.println("������"+sPointDay.getRatio()); 
				            	System.out.println(showtext);	
				        	}
			            	//System.out.println("Day��ֵ��ʱ�䣺"+extremeDate);
					         create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleDay); 
					         extremeCol++;
					         day++;
		            		break;
		            	case ConstantsInfo.ExtremeDateDayWeek:
		            		 
			            	sDayRatio = ddfDay.format(sPointDay.getRatio());
			            	sWeekRatio = ddfDay.format(sPointWeek.getRatio());
			            	//System.out.println("DayWeek��ֵ��ʱ�䣺"+extremeDate);
			            	//excel��ʾ��ֵ������
			            	showtext="���ܣ�"+sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sWeekRatio+"%)"; 
			            	
			            	create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleDayWeek); 
			            	extremeCol++;
			            	dayweek++;
		            		break;
		            	case ConstantsInfo.ExtremeDateDayWeekMonth:
		            		
			            	sDayRatio= ddfDay.format(sPointDay.getRatio());
			            	sWeekRatio= ddfDay.format(sPointWeek.getRatio());
			            	sMonthRatio= ddfDay.format(sPointMonth.getRatio());
			            	//System.out.println("DayWeekMonth��ֵ��ʱ�䣺"+extremeDate);
			            	//excel��ʾ��ֵ������
			            	showtext="�����£�"+sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sWeekRatio+"%,"+sMonthRatio+"%)"; 
			            	
			            	create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleDayWeekMonth); 
			            	extremeCol++;
			            	dayweekmonth++;
			            	break;
		            	case ConstantsInfo.ExtremeDateWeek:		            		 
			            	sWeekRatio= ddfDay.format(sPointWeek.getRatio()) ;
			            	//excel��ʾ��ֵ������
			            	showtext="�ܣ�"+sPointWeek.getExtremeDate()+":"+Float.toString(sPointWeek.getExtremePrice())+"("+sWeekRatio+"%)"; 
			            	//System.out.println("Week��ֵ��ʱ�䣺"+extremeDate);
					         create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleWeek); 
					         extremeCol++;
					         week++;
					         break;
		            	case ConstantsInfo.ExtremeDateMonth:		            		 
		            		sMonthRatio= ddfDay.format(sPointMonth.getRatio()) ;
			            	//excel��ʾ��ֵ������
		            		//System.out.println("Month��ֵ��ʱ�䣺"+extremeDate);
			            	showtext="�£�"+sPointMonth.getExtremeDate()+":"+Float.toString(sPointMonth.getExtremePrice())+"("+sMonthRatio+"%)"; 
			            	
					         create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleMonth); 
					         extremeCol++;	
					         month++;
					         break;
		            	case ConstantsInfo.ExtremeDateDayMonth:
		            		
		            		sDayRatio = ddfDay.format(sPointDay.getRatio());
			            	sMonthRatio = ddfDay.format(sPointMonth.getRatio());
			            
			            	//excel��ʾ��ֵ������
			            	showtext="���£�"+sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sMonthRatio+"%)"; 
			         
			            	create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleMonth); //cellStyleDayMonth
			            	
		            		 extremeCol++;	
		            		daymonth++;		 
		            		//System.out.println("DayMonth��ֵ��ʱ�䣺"+extremeDate);
		            		break;
		            	case ConstantsInfo.ExtremeDateWeekMonth:
		            		sWeekRatio = ddfDay.format(sPointWeek.getRatio());
			            	sMonthRatio = ddfDay.format(sPointMonth.getRatio());
			            
			            	//excel��ʾ��ֵ������
			            	showtext="���£�"+sPointWeek.getExtremeDate()+":"+Float.toString(sPointWeek.getExtremePrice())+"("+sWeekRatio+"%,"+sMonthRatio+"%)"; 
			            	create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleMonth); //cellStyleWeekMonth
		            		extremeCol++;	
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
	   		
	   		System.out.println("stock >3 count:"+monthpointcount);
	   		System.out.println("stock count:"+monthpoint1count);
	   		
    }   
    
    
    //������ҵ����
    public static void exportExcelAllStockForIndustry(String filePath, String fileName) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException 
    {    
            FileOutputStream fileOStream=null;

            List<String> listIndustryStock = new ArrayList<String>();        
        	List<StockIndustry> listIndustry = new ArrayList<StockIndustry>(); 
        	
        	//����excel
        	createExcel(filePath,fileName);
        	
        	 //�õ���ǰ������ҵ
        	listIndustry=sbDao.getStockIndustry();
        	System.out.println("��ҵ������"+listIndustry.size());
        	
        	NumberFormat ddfDay=null;
        	//����������λС����
        	ddfDay=NumberFormat.getNumberInstance() ; 
        	ddfDay.setMaximumFractionDigits(2);
            //�ڶ��п�ʼչʾ  
            int stockRow = 1;        
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
	   			
	   			DecimalFormat decimalFormat=new DecimalFormat(".00");
	   			   	   			
	   			//������ҵ���й�Ʊ
	   			listIndustryStock=sbDao.getIndustryStockFromAllInfo(induName);	   		
	   			stockLogger.logger.debug("��ҵ��Ʊ����"+listIndustryStock.size());
	   			for(ie=listIndustryStock.iterator();ie.hasNext();)
	   			{
	   				//��������
	   			
	   			//	if(stockRow>10)
	   			//		break;
	   				
	   				String stockFullId = (String) ie.next();	
	   			//	if(!stockFullId.equals("SH600354"))
	   			//		continue;
	   				int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_DATA_STOCK);
	   				if(isTableExist==0)//������
	   					continue;   				

	   				String stockName=sbDao.getStockNameFromIndustryTable(stockFullId);			
	   			
	   				System.out.println("fullId��"+stockFullId+" ���֣�"+stockName);
	   				stockLogger.logger.debug("fullId��"+stockFullId+" ���֣�"+stockName);
	   				//ÿ����Ʊ
	   				Row rowDayData = sheet.createRow(stockRow);  
	    			create07Cell(wb, rowDayData, 0,  Integer.toString(stockRow), cellStyleNormal);  
		            create07Cell(wb, rowDayData, 1, stockFullId, cellStyleNormal);  
		            create07Cell(wb, rowDayData, 2, stockName, cellStyleNormal); 	            
		    
		            stockRow++;
  
		       //    if(!stockFullId.equals("sh600000"))
				//		continue;
	    			//if(!stockFullId.equals("sh600000") && !stockFullId.equals("sz002142") && !stockFullId.equals("sz000001"))
	    			//		continue;
		            
	    			List<StockPoint> stockDayPoint=new ArrayList<StockPoint>();
	    			List<StockPoint> stockWeekPoint=new ArrayList<StockPoint>();
	    			List<StockPoint> stockMonthPoint=new ArrayList<StockPoint>();
	    			List<String> stockExtremeDate=new ArrayList<String>();
	    						
	    			//ȡ�����м�������
	    			stockDayPoint=spDao.getAllPointStock(stockFullId,ConstantsInfo.DayDataType);
	    			stockWeekPoint=spDao.getAllPointStock(stockFullId, ConstantsInfo.WeekDataType);
	    			stockMonthPoint=spDao.getAllPointStock(stockFullId,  ConstantsInfo.MonthDataType);
	    			if(stockDayPoint==null)
	    				continue;
	    			
	    			//ȡ�����м���ʱ��
	    			stockExtremeDate=spDao.getAllExtremeDate(stockFullId);
	    			
	    			if(ConstantsInfo.DEBUG)
		        	{
		    			System.out.println("day size:"+stockDayPoint.size());
		    			System.out.println("week size:"+stockWeekPoint.size());
		    			System.out.println("month size:"+stockMonthPoint.size()); 
		        	}
	    		  	String showtext="";
	    			int extremeCol=stockLabelNum;	
	    			//��һ������Ԥ��ֵ
	    			int daySize=stockDayPoint.size();
	    			int weekSize=stockWeekPoint.size();
	    			int monthSize=stockMonthPoint.size();
	    			//����6��Ԥ��ֵ
	    			String[] nextPointPrice=new String[6];
	    			
	    			//��Ԥ��
	    			if(daySize>=2)
	    			{
	    				String nextDate=spDao.getNextPointDate(stockDayPoint.get(daySize-1).getExtremeDate().toString(),stockDayPoint.get(daySize-2).getExtremeDate().toString());
	    				showtext=nextDate+"(";
	    				float lastPrice=stockDayPoint.get(daySize-1).getExtremePrice();
	    				float priPrice=stockDayPoint.get(daySize-2).getExtremePrice();
	    				nextPointPrice[0]=decimalFormat.format((lastPrice-0.382*(lastPrice-priPrice)));
	    				nextPointPrice[1]=decimalFormat.format((lastPrice-0.5*(lastPrice-priPrice)));
	    				nextPointPrice[2]=decimalFormat.format((lastPrice-0.618*(lastPrice-priPrice)));
	    				nextPointPrice[3]=decimalFormat.format((lastPrice-0.75*(lastPrice-priPrice)));
	    				nextPointPrice[4]=decimalFormat.format((priPrice));
	    				nextPointPrice[5]=decimalFormat.format((lastPrice-1.08*(lastPrice-priPrice)));	
	    				System.out.println("�ռ�ֵԤ���ʱ�䣺"+nextDate);	
	    				for(int i=0;i<6;i++)
	    				{
	    					if(ConstantsInfo.DEBUG)
				        	{
				            	System.out.println("�ռ�ֵԤ���λ"+i+":"+nextPointPrice[i]);	
				        	}
	    					showtext=showtext+nextPointPrice[i]+","; 
	    				}
	    				showtext=showtext+")";
		            	
		            	create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleYuce); 
		            	extremeCol++;
	    			}
	    			
	    			//��Ԥ��
	    			if(weekSize>=2)
	    			{
	    				String nextDate=spDao.getNextPointDate(stockWeekPoint.get(weekSize-1).getExtremeDate().toString(),stockWeekPoint.get(weekSize-2).getExtremeDate().toString());
	    				showtext=nextDate+"(";
	    				float lastPrice=stockWeekPoint.get(weekSize-1).getExtremePrice();
	    				float priPrice=stockWeekPoint.get(weekSize-2).getExtremePrice();
	    				nextPointPrice[0]=decimalFormat.format((lastPrice-0.382*(lastPrice-priPrice)));
	    				nextPointPrice[1]=decimalFormat.format((lastPrice-0.5*(lastPrice-priPrice)));
	    				nextPointPrice[2]=decimalFormat.format((lastPrice-0.618*(lastPrice-priPrice)));
	    				nextPointPrice[3]=decimalFormat.format((lastPrice-0.75*(lastPrice-priPrice)));
	    				nextPointPrice[4]=decimalFormat.format((priPrice));
	    				nextPointPrice[5]=decimalFormat.format((lastPrice-1.08*(lastPrice-priPrice)));	
	    				System.out.println("�ܼ�ֵԤ���ʱ�䣺"+nextDate);	
	    				for(int i=0;i<6;i++)
	    				{
	    					if(ConstantsInfo.DEBUG)
				        	{
				            	System.out.println("�ܼ�ֵԤ���λ"+i+":"+nextPointPrice[i]);	
				        	}
	    					showtext=showtext+nextPointPrice[i]+","; 
	    				}
	    				showtext=showtext+")";
		            	create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleYuce); 
		            	extremeCol++;
	    			}
	    			
	    			//��Ԥ��
	    			if(monthSize>=2)
	    			{
	    				String nextDate=spDao.getNextPointDate(stockMonthPoint.get(monthSize-1).getExtremeDate().toString(),stockMonthPoint.get(monthSize-2).getExtremeDate().toString());
	    				showtext=nextDate+"(";
	    				float lastPrice=stockDayPoint.get(monthSize-1).getExtremePrice();
	    				float priPrice=stockDayPoint.get(monthSize-2).getExtremePrice();
	    				nextPointPrice[0]=decimalFormat.format((lastPrice-0.382*(lastPrice-priPrice)));
	    				nextPointPrice[1]=decimalFormat.format((lastPrice-0.5*(lastPrice-priPrice)));
	    				nextPointPrice[2]=decimalFormat.format((lastPrice-0.618*(lastPrice-priPrice)));
	    				nextPointPrice[3]=decimalFormat.format((lastPrice-0.75*(lastPrice-priPrice)));
	    				nextPointPrice[4]=decimalFormat.format((priPrice));
	    				nextPointPrice[5]=decimalFormat.format((lastPrice-1.08*(lastPrice-priPrice)));	
	    				System.out.println("�¼�ֵԤ���ʱ�䣺"+nextDate);	
	    				for(int i=0;i<6;i++)
	    				{
	    					if(ConstantsInfo.DEBUG)
				        	{
				            	System.out.println("�¼�ֵԤ���λ"+i+":"+nextPointPrice[i]);	
				        	}
	    					showtext=showtext+nextPointPrice[i]+","; 
	    				}
	    				showtext=showtext+")";
		            	create07Cell(wb, rowDayData, extremeCol, showtext,cellStyleYuce); 
		            	extremeCol++;
	    			}
	    			
	    			extremeCol=stockLabelNum+3;
	    			
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
	    			for(Iterator itDate = stockExtremeDate.iterator();itDate.hasNext();)
	    			{
	    				int dayType=0;
	    				int weekType=0;
	    				int monthType=0;
	    				String extremeDate = (String) itDate.next();
	    				StockPoint sPointDay=stockDayDateExist.get(extremeDate);
		            	if(sPointDay != null)
		            	{
		            		dayType=1;//ConstantsInfo.ExtremeDateDay;
		            	}
	    				StockPoint sPointWeek=stockWeekDateExist.get(extremeDate);
		            	if(sPointWeek!=null)
		            	{
		            		weekType=1;//ConstantsInfo.ExtremeDateWeek;
		            	}
		            	StockPoint sPointMonth=stockMonthDateExist.get(extremeDate);
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
		            	
		            	
		            	switch(type)
		            	{
		            	case ConstantsInfo.ExtremeDateDay:
		            		 
			            	sDayRatio= ddfDay.format(sPointDay.getRatio()) ;
			            	//excel��ʾ��ֵ������
			            	showtext="�գ�"+sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%)"; 
			            	if(ConstantsInfo.DEBUG)
				        	{
				            	System.out.println("��ֵ��ʱ�䣺"+sPointDay.getExtremeDate());
				            	System.out.println("��ֵ�۸�"+sPointDay.getExtremePrice()); 
				            	System.out.println("������"+sPointDay.getRatio()); 
				            	System.out.println(showtext);	
				        	}
			            	//System.out.println("Day��ֵ��ʱ�䣺"+extremeDate);
					         create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleDay); 
					         extremeCol++;
					         day++;
		            		break;
		            	case ConstantsInfo.ExtremeDateDayWeek:
		            		 
			            	sDayRatio = ddfDay.format(sPointDay.getRatio());
			            	sWeekRatio = ddfDay.format(sPointWeek.getRatio());
			            	//System.out.println("DayWeek��ֵ��ʱ�䣺"+extremeDate);
			            	//excel��ʾ��ֵ������
			            	showtext="���ܣ�"+sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sWeekRatio+"%)"; 
			            	
			            	create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleDayWeek); 
			            	extremeCol++;
			            	dayweek++;
		            		break;
		            	case ConstantsInfo.ExtremeDateDayWeekMonth:
		            		
			            	sDayRatio= ddfDay.format(sPointDay.getRatio());
			            	sWeekRatio= ddfDay.format(sPointWeek.getRatio());
			            	sMonthRatio= ddfDay.format(sPointMonth.getRatio());
			            	//System.out.println("DayWeekMonth��ֵ��ʱ�䣺"+extremeDate);
			            	//excel��ʾ��ֵ������
			            	showtext="�����£�"+sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sWeekRatio+"%,"+sMonthRatio+"%)"; 
			            	
			            	create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleDayWeekMonth); 
			            	extremeCol++;
			            	dayweekmonth++;
			            	break;
		            	case ConstantsInfo.ExtremeDateWeek:		            		 
			            	sWeekRatio= ddfDay.format(sPointWeek.getRatio()) ;
			            	//excel��ʾ��ֵ������
			            	showtext="�ܣ�"+sPointWeek.getExtremeDate()+":"+Float.toString(sPointWeek.getExtremePrice())+"("+sWeekRatio+"%)"; 
			            	//System.out.println("Week��ֵ��ʱ�䣺"+extremeDate);
					         create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleWeek); 
					         extremeCol++;
					         week++;
					         break;
		            	case ConstantsInfo.ExtremeDateMonth:		            		 
		            		sMonthRatio= ddfDay.format(sPointMonth.getRatio()) ;
			            	//excel��ʾ��ֵ������
		            		//System.out.println("Month��ֵ��ʱ�䣺"+extremeDate);
			            	showtext="�£�"+sPointMonth.getExtremeDate()+":"+Float.toString(sPointMonth.getExtremePrice())+"("+sMonthRatio+"%)"; 
			            	
					         create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleMonth); 
					         extremeCol++;	
					         month++;
					         break;
		            	case ConstantsInfo.ExtremeDateDayMonth:
		            		
		            		sDayRatio = ddfDay.format(sPointDay.getRatio());
			            	sMonthRatio = ddfDay.format(sPointMonth.getRatio());
			            
			            	//excel��ʾ��ֵ������
			            	showtext="���£�"+sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sMonthRatio+"%)"; 
			         
			            	create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleMonth); //cellStyleDayMonth
			            	
		            		 extremeCol++;	
		            		daymonth++;		 
		            		//System.out.println("DayMonth��ֵ��ʱ�䣺"+extremeDate);
		            		break;
		            	case ConstantsInfo.ExtremeDateWeekMonth:
		            		sWeekRatio = ddfDay.format(sPointWeek.getRatio());
			            	sMonthRatio = ddfDay.format(sPointMonth.getRatio());
			            
			            	//excel��ʾ��ֵ������
			            	showtext="���£�"+sPointWeek.getExtremeDate()+":"+Float.toString(sPointWeek.getExtremePrice())+"("+sWeekRatio+"%,"+sMonthRatio+"%)"; 
			            	create07Cell(wb, rowDayData, extremeCol, showtext, cellStyleMonth); //cellStyleWeekMonth
		            		extremeCol++;	
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
        
        //public static void create07Cell(Workbook wb, Row row, int column, String value, XSSFFont font,short color) {  
        public static void create07Cell(Workbook wb, Row row, int column, String value, CellStyle cellStyle) {  
            Cell cell = row.createCell(column);  
            cell.setCellValue(value);  
            cell.setCellStyle(cellStyle);  
        }  
        
        /*
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
        */
        
       
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
        
        public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException {
    		
        	PropertyConfigurator.configure("stockConf/log4j_excelWriter.properties");
        	
    		ExcelWriter ew=new ExcelWriter();
    		
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
            String excleFileName="stockIndustryPoint_"+dateNowStr+".xlsx";
    		ew.exportExcelAllStockForIndustry("export\\",excleFileName);//��Ʊ��ֵ����ʾһ�� ��ȡpointStock��
    		
    		//��ǰ·��
    		ew.exportExcelAllStockForConcept("load\\","stockPointConcept.xlsx");//��Ʊ��ֵ����ʾһ�� ��ȡpointStock��
    		
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
