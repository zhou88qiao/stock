package excel.rw;

import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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

import dao.StockBaseDao;
import dao.StockConcept;
import dao.StockDataDao;
import dao.StockIndustry;
import dao.StockInformation;
import dao.StockInformationDao;
import dao.StockPoint;
import dao.StockPointDao;
import dao.StockToIndustry;

public class ExcelWriterV2 {
	
	static StockInformationDao siDao =new StockInformationDao();
	static StockDataDao sdDao=new StockDataDao();
	static StockPointDao spDao=new StockPointDao();
	static StockBaseDao sbDao=new StockBaseDao();
	static PointClass pClass=new PointClass();
	private POIFSFileSystem fs;
    private static HSSFWorkbook wb;
    private XSSFWorkbook xb;
    private HSSFSheet sheet;
    private Sheet xsheet;
    private HSSFRow row;
    private Row xrow;
	final static int stockLabelNum=3;
	final static int stockMaxPointNum=300;
	
	  static XSSFColor hfNormal;
      static XSSFColor hfCday;
      static XSSFColor hfCweek;
      static XSSFColor hfCmonth;
      static XSSFColor hfYuce;
      
      // �ļ�ͷ����  
      static XSSFFont font0;  
      static XSSFFont fontDay;  
      static XSSFFont fontDayWeek;  
      static XSSFFont fontDayWeekMonth;  
      static XSSFFont fontYuce;
      static CellStyle cellStyleNormal;
      static CellStyle cellStyleDay;
      static CellStyle cellStyleWeek;
      static CellStyle cellStyleWeekMonth;
      static CellStyle cellStyleYuce;
	
      
      public static CellStyle create07CellStyle(Workbook wb, XSSFFont font,int color) {    
          CellStyle cellStyle = wb.createCellStyle();  
          cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);  
          cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_BOTTOM);  
          cellStyle.setFont(font);  
        //  cellStyle.setFillBackgroundColor(HSSFColor.BLUE.index);
         // IndexedColors.YELLOW.getIndex()
          if((short)color>0)
          {	
          	cellStyle.setFillForegroundColor((short) color);            
          	cellStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);  
          }
         return cellStyle;
      }  
      
    public static HSSFWorkbook exportExcelForStock() throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException 
    {    
      
    	//����excel�ļ�����  
            HSSFWorkbook wb = new HSSFWorkbook();  
            //����һ���ű�  
            int lt=wb.getNumberOfSheets();
            FileOutputStream os=null;
            System.out.println(lt);
            
            List<String> listStockFullId = new ArrayList<String>();
    		listStockFullId=siDao.getAllFullId();
    		int sheetCount=0;
    		int i=0;
            
    		for(Iterator it = listStockFullId.iterator();it.hasNext();)
    		{
    			String stockFullId=(String)it.next();
    			if(!stockFullId.equals("sh000001"))
					continue;
    			//if(!stockFullId.equals("sh600000") && !stockFullId.equals("sh600015") && !stockFullId.equals("sz000001"))
    			//		continue;
    			List<StockPoint> stockDayPoint=new ArrayList<StockPoint>();
    			List<StockPoint> stockWeekPoint=new ArrayList<StockPoint>();
    			List<StockPoint> stockMonthPoint=new ArrayList<StockPoint>();
    			
    			DateStock dStock=sdDao.getDateStock(stockFullId);
    			
    			stockDayPoint=pClass.getStockDayExtremePoint(stockFullId, dStock,ConstantsInfo.StockCalAllData);
    			stockWeekPoint=pClass.getStockWeekExtremePoint(stockFullId, dStock,ConstantsInfo.StockCalAllData);
    			stockMonthPoint=pClass.getStockMonthExtremePoint(stockFullId, dStock,ConstantsInfo.StockCalAllData);
    			System.out.println("day size:"+stockDayPoint.size());
    			System.out.println("week size:"+stockWeekPoint.size());
    			System.out.println("month size:"+stockMonthPoint.size());    			
    			
    			
            	System.out.println("sheet count:"+sheetCount);
	            HSSFSheet  sheet = wb.createSheet(); 
	            wb.setSheetName(sheetCount,stockFullId);
	            
	            //�п� 0,1,2,3
	            for(i=0;i<stockLabelNum;i++)
	            	sheet.setColumnWidth(i, 6000);
	            //�п� 3,4,5
	            for(i=stockLabelNum-1;i<stockMaxPointNum;i++)
	            	sheet.setColumnWidth(i, 10000);
	       
	            sheetCount++;
	          
	            //������һ��  
	            Row row = sheet.createRow(0);  
	            //�����ڶ���  
	            Row row1 = sheet.createRow(1);  
	            //����������  
	            Row row2 = sheet.createRow(2);  
	        
	            
	            // �ļ�ͷ����  
	            HSSFFont font0 = create03Fonts(wb, HSSFFont.BOLDWEIGHT_BOLD, "����", false,  
	                    (short) 300);  
	            HSSFFont font1 = create03Fonts(wb, HSSFFont.BOLDWEIGHT_NORMAL, "����", false,  
	                    (short) 300);  
	            
 
	            // �ϲ�0-1�еĵ�Ԫ��  
	            sheet.addMergedRegion(new CellRangeAddress(0, 2, 0, 0));  
	            //���õ�һ�е�����  
	            create03Cell(wb, row, 0, "����", font0);  
	            //�ϲ���һ�е�2���Ժ�8�У��������ڶ��У�  
	            sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 4));  
	            //���õڶ��е�����  
	            create03Cell(wb, row, 1, "����", font0);  
	            //�ϲ�ʱ��
	            sheet.addMergedRegion(new CellRangeAddress(1, 2, 1, 1)); 
	            //�ϲ���λ
	            sheet.addMergedRegion(new CellRangeAddress(1, 2, 2, 2));
	            //�ϲ������
	            sheet.addMergedRegion(new CellRangeAddress(1, 1, 3, 4));
	            //������������ı�  
	            create03Cell(wb, row1, 1, "ʱ��", font0);  
	            create03Cell(wb, row1, 2, "��λ", font0);  
	            create03Cell(wb, row1, 3, "�����", font0);  
	            
	            create03Cell(wb, row2, 3, "ǰ", font0);  
	            create03Cell(wb, row2, 4, "��", font0);  
	            //�����б�ʾ  
	            int lhead = 3;  
	            //���ｫѧԱ�����Ĵ��뵽�����  
	         
	            //�ռ���
	            for(Iterator itDay = stockDayPoint.iterator();itDay.hasNext();)
	    		{
	            	Row rowData = sheet.createRow(lhead++);  
	            	StockPoint sPoint=(StockPoint)itDay.next();
	               // createCell(wb, rowData, 0, String.valueOf(i + 1), font1);
	                create03Cell(wb, rowData, 0, "��", font1); 
	                create03Cell(wb, rowData, 1, sPoint.getExtremeDate().toString(), font1);  
	                create03Cell(wb, rowData, 2,((Float)sPoint.getExtremePrice()).toString(), font1);  
	                create03Cell(wb, rowData, 3,sPoint.getFromDate().toString(), font1);  
	                create03Cell(wb, rowData, 4,sPoint.getToDate().toString(), font1);  	            	
	    		}
	            
	            //�ܼ���
	            for(Iterator itWeek = stockWeekPoint.iterator();itWeek.hasNext();)
	    		{
	            	Row rowData = sheet.createRow(lhead++);  
	            	StockPoint sPoint=(StockPoint)itWeek.next();
	               // createCell(wb, rowData, 0, String.valueOf(i + 1), font1);
	                create03Cell(wb, rowData, 0, "��", font1); 
	                create03Cell(wb, rowData, 1, sPoint.getExtremeDate().toString(), font1);  
	                create03Cell(wb, rowData, 2,((Float)sPoint.getExtremePrice()).toString(), font1);  
	                create03Cell(wb, rowData, 3,sPoint.getFromDate().toString(), font1);  
	                create03Cell(wb, rowData, 4,sPoint.getToDate().toString(), font1);              	
	    		}
	            
	            //�¼���
	            for(Iterator itMonth = stockMonthPoint.iterator();itMonth.hasNext();)
	    		{
	            	Row rowData = sheet.createRow(lhead++);  
	            	StockPoint sPoint=(StockPoint)itMonth.next();
	               // createCell(wb, rowData, 0, String.valueOf(i + 1), font1);
	                create03Cell(wb, rowData, 0, "��", font1); 
	                create03Cell(wb, rowData, 1, sPoint.getExtremeDate().toString(), font1);  
	                create03Cell(wb, rowData, 2,((Float)sPoint.getExtremePrice()).toString(), font1);  
	                create03Cell(wb, rowData, 3,sPoint.getFromDate().toString(), font1);  
	                create03Cell(wb, rowData, 4,sPoint.getToDate().toString(), font1);              	
	    		}
 
	             os = new FileOutputStream("stockExcel.xls");
	         
		         wb.write(os);
		          //  os.flush();
		         os.close();
            
            }
    
            return wb;  
        }     
    
    //2007   day week���һ��
    public static XSSFWorkbook exportExcelForAllStockSeparate() throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException 
    {    
      
    	//����excel�ļ�����  
    		XSSFWorkbook wb = new XSSFWorkbook();  
            //����һ���ű�  
            int lt=wb.getNumberOfSheets();
            FileOutputStream os=null;
            System.out.println(lt);
            
            List<String> listStockFullId = new ArrayList<String>();
            List<StockInformation> listStockInfo = new ArrayList<StockInformation>(); 
   
            listStockInfo=siDao.getStockDaoList();
    		listStockFullId=siDao.getAllFullId();
    		int sheetCount=0;
    		
    		List<String> listStockDate = new ArrayList<String>();
    		listStockDate=sdDao.getDatesFromSH000001("sh000001");
    		System.out.println("date size:"+listStockDate.size());
    		
        	XSSFSheet  sheet = wb.createSheet(); 
            wb.setSheetName(sheetCount,"allStock");
            
            //�п�
            for(int i=0;i<=4+listStockDate.size();i++)
            {	
            	sheet.setColumnWidth(i, 6000);
            
            }
                
            //������һ��  
            Row rowHead = sheet.createRow(0);  
            
            XSSFColor hfNormal=new XSSFColor(java.awt.Color.BLACK);
            XSSFColor hfCday=new XSSFColor(java.awt.Color.RED);
            XSSFColor hfCweek=new XSSFColor(java.awt.Color.BLUE);
            
            /*
            // �ļ�ͷ����  
            XSSFFont font0 = create07Fonts(wb, XSSFFont.BOLDWEIGHT_BOLD, "����", false,  
                    (short) 400,XSSFFont.COLOR_NORMAL);  
            XSSFFont fontDay = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "����", false,  
                    (short) 400,XSSFFont.COLOR_RED);  
            */
            
            // �ļ�ͷ����  
            XSSFFont font0 = create07Fonts(wb, XSSFFont.BOLDWEIGHT_BOLD, "����", false,  
                    (short) 400,hfNormal);  
            XSSFFont fontDay = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "����", false,  
                    (short) 400,hfCday);  
            XSSFFont fontWeek = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "����", false,  
                    (short) 400,hfCweek);  
             

            //����һ������ı�  
            create07Cell(wb, rowHead, 0, "���", font0,IndexedColors.YELLOW.getIndex());  
            create07Cell(wb, rowHead, 1, "����", font0,IndexedColors.YELLOW.getIndex());  
            create07Cell(wb, rowHead, 2, "����", font0,IndexedColors.YELLOW.getIndex()); 	            
            create07Cell(wb, rowHead, 3, "����", font0,IndexedColors.YELLOW.getIndex());  
            create07Cell(wb, rowHead, 4, "����", font0,IndexedColors.YELLOW.getIndex());  
            int stockColumn=5;
            //�п�
            
            //ʱ������ hash��
            HashMap<String, Integer> stockDateColumnmap = new HashMap<String, Integer>(); 
            for(Iterator it = listStockDate.iterator();it.hasNext();)
    		{
            	String stockDate=(String)it.next();            	
            	create07Cell(wb, rowHead, stockColumn, stockDate, font0,IndexedColors.YELLOW.getIndex()); 	
            	stockDateColumnmap.put(stockDate, stockColumn);
            	stockColumn++;
    		}
       
            //�ڶ��б�ʾ  
            int stockRow = 1;  
            
    		for(Iterator it = listStockInfo.iterator();it.hasNext();)
    		{
    			
    		//	String stockFullId=(String)it.next();
    			StockInformation si = (StockInformation) it.next();
    			String stockFullId=si.getStockFullId();
    		//	if(!stockFullId.equals("sh000001"))
			//		continue;
    			
    			if(!stockFullId.equals("sh600000") && !stockFullId.equals("sh600015") && !stockFullId.equals("sz000001"))
    					continue;
    			
    			Row rowDayData = sheet.createRow(stockRow);  
    			
    			create07Cell(wb, rowDayData, 0,  Integer.toString(stockRow), font0,ConstantsInfo.XSSFNORMAL);  
	            create07Cell(wb, rowDayData, 1, stockFullId, font0,ConstantsInfo.XSSFNORMAL);  
	            create07Cell(wb, rowDayData, 2, si.getStockName(), font0,ConstantsInfo.XSSFNORMAL); 	            
	            create07Cell(wb, rowDayData, 3, si.getStockRegional(), font0,ConstantsInfo.XSSFNORMAL); 
	            create07Cell(wb, rowDayData, 4, "��", font0,ConstantsInfo.XSSFNORMAL);  
	            stockColumn=5;
	           // stockRow++;
    			List<StockPoint> stockDayPoint=new ArrayList<StockPoint>();
    			List<StockPoint> stockWeekPoint=new ArrayList<StockPoint>();
    			List<StockPoint> stockMonthPoint=new ArrayList<StockPoint>();
    			
    			
    			DateStock dStock=sdDao.getDateStock(stockFullId);
    			
    			stockDayPoint=pClass.getStockDayExtremePoint(stockFullId, dStock,ConstantsInfo.StockCalAllData);
    			stockWeekPoint=pClass.getStockWeekExtremePoint(stockFullId, dStock,ConstantsInfo.StockCalAllData);
    			//stockMonthPoint=pClass.getStockMonthExtremePoint(stockFullId, dStock);
    			System.out.println("day size:"+stockDayPoint.size());
    			System.out.println("week size:"+stockWeekPoint.size());
    			System.out.println("month size:"+stockMonthPoint.size());    			
    				
	            //�ռ���
    			System.out.println("��stockRow��"+stockRow);
    			System.out.println("******day point*******");
	            for(Iterator itDay = stockDayPoint.iterator();itDay.hasNext();)
	    		{
	            	StockPoint sPoint=(StockPoint)itDay.next();
	            		
	            	//����������λС����
	            	NumberFormat ddf=NumberFormat.getNumberInstance() ; 
	            	ddf.setMaximumFractionDigits(2); 
	            	String sRatio= ddf.format(sPoint.getRatio()) ;
	            	
	            	//excel��ʾ��ֵ������
	            	String showtext=Float.toString(sPoint.getExtremePrice())+"("+sRatio+"%)";       
	            	
	            	int extremeCol = stockDateColumnmap.get(sPoint.getExtremeDate());
	            	
	            //	if(ConstantsInfo.DEBUG)
	        	//	{
		            	System.out.println("��ֵ��ʱ�䣺"+sPoint.getExtremeDate());
		            	System.out.println("��ֵ�۸�"+sPoint.getExtremePrice()); 
		            	System.out.println("������"+sPoint.getRatio()); 
		            	System.out.println(showtext);
		            	System.out.println("��ֵ��"+extremeCol);  
	        	//	}
	            	
	            	create07Cell(wb, rowDayData, extremeCol, showtext, fontDay,ConstantsInfo.XSSFNORMAL);  	            	
	    		}
	            
	          
	            stockRow++;
	           
	            //�ܼ���
	            System.out.println("��stockRow��"+stockRow);
	            System.out.println("******week point*******");
	            Row rowWeekData = sheet.createRow(stockRow);  
	            create07Cell(wb, rowWeekData, 0,  Integer.toString(stockRow), font0,-1); 
	            create07Cell(wb, rowWeekData, 4, "��", font0,-1); 
	            for(Iterator itWeek = stockWeekPoint.iterator();itWeek.hasNext();)
	    		{
	            	
	            	StockPoint sPoint=(StockPoint)itWeek.next();
	                          		
	            	//����������λС����
	            	NumberFormat ddf=NumberFormat.getNumberInstance() ; 
	            	ddf.setMaximumFractionDigits(2); 
	            	String sRatio= ddf.format(sPoint.getRatio()) ;
	            	
	            	//excel��ʾ��ֵ������
	            	String showtext=Float.toString(sPoint.getExtremePrice())+"("+sRatio+"%)";       
	            	
	            	int extremeCol = stockDateColumnmap.get(sPoint.getExtremeDate());
	            //	if(ConstantsInfo.DEBUG)
	        	//	{
		            	System.out.println("��ֵ��ʱ�䣺"+sPoint.getExtremeDate());
		            	System.out.println("��ֵ�۸�"+sPoint.getExtremePrice()); 
		            	System.out.println("������"+sPoint.getRatio()); 
		            	System.out.println(showtext);
		            	System.out.println("��ֵ��"+extremeCol);  
	        	//	}
		       
	            	create07Cell(wb, rowWeekData, extremeCol, showtext,fontWeek,ConstantsInfo.XSSFNORMAL);  
	    		}
	          
	            stockRow++;
	            
	            /*
	            //�¼���
	            for(Iterator itMonth = stockMonthPoint.iterator();itMonth.hasNext();)
	    		{
	            	Row rowMonthData = sheet.createRow(stockRow);  
	            	StockPoint sPoint=(StockPoint)itMonth.next();
	            	//����������λС����
	            	NumberFormat ddf=NumberFormat.getNumberInstance() ; 
	            	ddf.setMaximumFractionDigits(2); 
	            	String sRatio= ddf.format(sPoint.getRatio()) ;
	            	
	            	//excel��ʾ��ֵ������
	            	String showtext=Float.toString(sPoint.getExtremePrice())+"("+sRatio+")";       
	            	System.out.println("��ֵ��ʱ�䣺"+sPoint.getExtremeDate());//��ֵ��ʱ�䣺2012-05-04 ����13��14����
	            	int extremeCol = stockDateColumnmap.get(sPoint.getExtremeDate());
	            	if(ConstantsInfo.DEBUG)
	        		{
		            	System.out.println("��ֵ��ʱ�䣺"+sPoint.getExtremeDate());
		            	System.out.println("��ֵ�۸�"+sPoint.getExtremePrice()); 
		            	System.out.println("������"+sPoint.getRatio()); 
		            	System.out.println(showtext);
		            	System.out.println("��ֵ��"+extremeCol);  
	        		}
	            	
	            	create07Cell(wb, rowMonthData, extremeCol, showtext, font1);              	
	    		}
	    		*/       
	             
            
            }
    		
    		os = new FileOutputStream("stockExcel.xlsx");
	         
	         wb.write(os);
	          //  os.flush();
	         os.close();
    
            return wb;  
        }  
    
    
    
    public static XSSFWorkbook exportExcelForAllStockFromTable() throws IOException, ClassNotFoundException, SQLException
    {    
    		      
	//����excel�ļ�����  
		XSSFWorkbook wb = new XSSFWorkbook();  
        //����һ���ű�  
        int lt=wb.getNumberOfSheets();
        FileOutputStream os=null;
        System.out.println(lt);
        int i=0;
        
    //    List<String> listStockFullId = new ArrayList<String>();
            List<StockInformation> listStockInfo = new ArrayList<StockInformation>(); 
            List<String> listIndustry = new ArrayList<String>(); 
        	List<String> listStockIndustry = new ArrayList<String>(); 
   
            listStockInfo=siDao.getStockDaoList();
    	//	listStockFullId=siDao.getAllFullId();
		int sheetCount=0;
		
		List<String> listStockDate = new ArrayList<String>();
		listStockDate=sdDao.getDatesFromSH000001("sh000001");
		System.out.println("date size:"+listStockDate.size());
		
    	XSSFSheet  sheet = wb.createSheet(); 
        wb.setSheetName(sheetCount,"allStock");
                    
        //�п�
        for(i=0;i<stockLabelNum;i++)
        	sheet.setColumnWidth(i, 6000);
        //�п�
        for(i=stockLabelNum;i<=stockMaxPointNum;i++)
        	sheet.setColumnWidth(i, 10000);
   
            
        //������һ��  
        Row rowHead = sheet.createRow(0);  
        
        XSSFColor hfNormal=new XSSFColor(java.awt.Color.BLACK);
        XSSFColor hfCday=new XSSFColor(java.awt.Color.RED);
        XSSFColor hfCweek=new XSSFColor(java.awt.Color.BLUE);
        XSSFColor hfCmonth=new XSSFColor(java.awt.Color.GREEN);
        
        /*
        // �ļ�ͷ����  
        XSSFFont font0 = create07Fonts(wb, XSSFFont.BOLDWEIGHT_BOLD, "����", false,  
                (short) 400,XSSFFont.COLOR_NORMAL);  
        XSSFFont fontDay = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "����", false,  
                (short) 400,XSSFFont.COLOR_RED);  
        */
        
        // �ļ�ͷ����  
        XSSFFont font0 = create07Fonts(wb, XSSFFont.BOLDWEIGHT_BOLD, "����", false,  
                (short) 400,hfNormal);  
        XSSFFont fontDay = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "����", false,  
                (short) 300,hfCday);  
        XSSFFont fontDayWeek = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "����", false,  
                (short) 300,hfCweek);  
        XSSFFont fontDayWeekMonth = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "����", false,  
                (short) 300,hfCmonth);  
         

        //����һ������ı�  
        create07Cell(wb, rowHead, 0, "���", font0,IndexedColors.YELLOW.getIndex());  
        create07Cell(wb, rowHead, 1, "����", font0,IndexedColors.YELLOW.getIndex());  
        create07Cell(wb, rowHead, 2, "����", font0,IndexedColors.YELLOW.getIndex()); 	            
        create07Cell(wb, rowHead, 3, "����", font0,IndexedColors.YELLOW.getIndex());            
        int stockColumn=4;
        //�п�
        
        //ʱ������ hash��
        for(;stockColumn<100;stockColumn++)
		{
        	String colName=""+stockColumn+"";            	
        	create07Cell(wb, rowHead, stockColumn, colName, font0,IndexedColors.YELLOW.getIndex()); 	
		}
   
        //�ڶ��п�ʼչʾ  
        int stockRow = 1;  
        
        listIndustry=siDao.getAllIndustry(listStockInfo);	
        System.out.println("��������"+listIndustry.size());
        Iterator itIndu,ie;
        int stockNum=0;
   		 for(itIndu = listIndustry.iterator();itIndu.hasNext();)
   		 {
   			String stockIndu = (String) itIndu.next();			
   			//System.out.println(stockIndu);
   			
   		//	if(!stockIndu.equals("����ҵ")&& !stockIndu.equals("ҽ����е����ҵ"))
   		//		continue;
   			
   			//��ҵ
   			Row rowIndustry = sheet.createRow(stockRow);  
   			create07Cell(wb, rowIndustry, 0, stockIndu, font0,IndexedColors.BLUE.getIndex());  
   			stockRow++;
   			
   			listStockIndustry=siDao.getStockIndustry(stockIndu);
   			System.out.println("�ð���Ʊ������"+listStockIndustry.size());
   			for(ie=listStockIndustry.iterator();ie.hasNext();)
   			{
   				String stockFullId = (String) ie.next();	
   				int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_DATA_STOCK);
   				if(isTableExist==0)//������
   					continue;   				

   				StockInformation si=siDao.getStockInformation(stockFullId);			
   				
    		//	String stockFullId=si.getStockFullId();
    		
			
   			//��Ʊ
    			Row rowDayData = sheet.createRow(stockRow);  
    			
    			create07Cell(wb, rowDayData, 0,  Integer.toString(stockRow), font0,ConstantsInfo.XSSFNORMAL);  
	            create07Cell(wb, rowDayData, 1, stockFullId, font0,ConstantsInfo.XSSFNORMAL);  
	            create07Cell(wb, rowDayData, 2, si.getStockName(), font0,ConstantsInfo.XSSFNORMAL); 	            
	            create07Cell(wb, rowDayData, 3, si.getStockRegional(), font0,ConstantsInfo.XSSFNORMAL); 

	            
	            //�����п�ʼ��ʾ��ֵ
	            stockColumn=4;
	            
	            stockRow++;
	       
	            
	        //   if(!stockFullId.equals("sh600000"))
			//		continue;
    			
    			if(!(stockFullId.equals("sh600000") || stockFullId.equals("sh601169") || stockFullId.equals("sz000045")))
    				continue;
	            
    			List<StockPoint> stockDayPoint=new ArrayList<StockPoint>();
    			List<StockPoint> stockWeekPoint=new ArrayList<StockPoint>();
    			List<StockPoint> stockMonthPoint=new ArrayList<StockPoint>();
    			
    			
    			DateStock dStock=sdDao.getDateStock(stockFullId);
    			
    			stockDayPoint=spDao.getAllPointStock(stockFullId,ConstantsInfo.DayDataType);
    			stockWeekPoint=spDao.getAllPointStock(stockFullId, ConstantsInfo.WeekDataType);
    			stockMonthPoint=spDao.getAllPointStock(stockFullId,  ConstantsInfo.MonthDataType);
    			if(stockDayPoint==null)
    				continue;
    			System.out.println("day size:"+stockDayPoint.size());
    			System.out.println("week size:"+stockWeekPoint.size());
    			System.out.println("month size:"+stockMonthPoint.size());    			
    				
	            //�ռ���
    			System.out.println("��stockRow��"+stockRow);
    			System.out.println("******all point*******");
    			
    			  //��ʱ������� 
                HashMap<String, StockPoint> stockWeekDateExist = new HashMap<String, StockPoint>(); 
                HashMap<String, StockPoint> stockMonthDateExist = new HashMap<String, StockPoint>(); 
                
    			for(Iterator itWeek = stockWeekPoint.iterator();itWeek.hasNext();)
	 	    	{
	 	            StockPoint sPointWeek=(StockPoint)itWeek.next();	 	            
	 	           stockWeekDateExist.put(sPointWeek.getExtremeDate().toString(), sPointWeek);
	 	    	}
    			//��ʱ������� 
    			for(Iterator itMonth = stockMonthPoint.iterator();itMonth.hasNext();)
	 	    	{	
	 	            StockPoint sPointMonth=(StockPoint)itMonth.next();	 	            
	 	           stockMonthDateExist.put(sPointMonth.getExtremeDate().toString(), sPointMonth);
	 	    	}
    			int type=0;
    			int extremeCol=stockLabelNum;
	            for(Iterator itDay = stockDayPoint.iterator();itDay.hasNext();)
	    		{
	            	StockPoint sPointDay=(StockPoint)itDay.next();
	            	String extremeDate=sPointDay.getExtremeDate().toString();//ʱ��
	            	type=ConstantsInfo.ExtremeDateDay;
	            	StockPoint sPointWeek=stockWeekDateExist.get(extremeDate);
	            	if(sPointWeek!=null)
	            	{
	            		type=ConstantsInfo.ExtremeDateDayWeek;
	            	}
	            	
	            	StockPoint sPointMonth=stockMonthDateExist.get(extremeDate);
	            	if(sPointMonth!=null && type==ConstantsInfo.ExtremeDateDayWeek)
	            	{
	            		type=ConstantsInfo.ExtremeDateDayWeekMonth;
	            	}
	            	
	            	NumberFormat ddfDay=null;
	            	NumberFormat ddfWeek=null;
	            	NumberFormat ddfMonth=null;
	            	String sDayRatio,sWeekRatio,sMonthRatio=null;
	            	String showtext=null;
	            	
	            	switch(type)
	            	{
	            	case ConstantsInfo.ExtremeDateDay:
	            		//����������λС����
		            	ddfDay=NumberFormat.getNumberInstance() ; 
		            	ddfDay.setMaximumFractionDigits(2); 
		            	sDayRatio= ddfDay.format(sPointDay.getRatio()) ;
		            	//excel��ʾ��ֵ������
		            	showtext=sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%)"; 
		            	 //	if(ConstantsInfo.DEBUG)
			        	//	{
				            	System.out.println("��ֵ��ʱ�䣺"+sPointDay.getExtremeDate());
				            	System.out.println("��ֵ�۸�"+sPointDay.getExtremePrice()); 
				            	System.out.println("������"+sPointDay.getRatio()); 
				            	System.out.println(showtext);
				            	
			        	//	}
				         
				       
				         create07Cell(wb, rowDayData, extremeCol, showtext, fontDay,ConstantsInfo.XSSFNORMAL); 
				         extremeCol++;
	            		break;
	            	case ConstantsInfo.ExtremeDateDayWeek:
	            		//����������λС����
	            		ddfDay=NumberFormat.getNumberInstance() ; 
		            	ddfDay.setMaximumFractionDigits(2); 
		            	sDayRatio= ddfDay.format(sPointDay.getRatio()) ;
		            	
		            	ddfWeek=NumberFormat.getNumberInstance() ; 
		            	ddfWeek.setMaximumFractionDigits(2); 
		            	sWeekRatio= ddfWeek.format(sPointWeek.getRatio()) ;
		            	
		            	//excel��ʾ��ֵ������
		            	showtext=sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sWeekRatio+"%)"; 
		            	
		            	create07Cell(wb, rowDayData, extremeCol, showtext, fontDayWeek,ConstantsInfo.XSSFNORMAL); 
		            	extremeCol++;
	            		break;
	            	case ConstantsInfo.ExtremeDateDayWeekMonth:
	            		//����������λС����
	            		ddfDay=NumberFormat.getNumberInstance() ; 
		            	ddfDay.setMaximumFractionDigits(2); 
		            	sDayRatio= ddfDay.format(sPointDay.getRatio()) ;
		            	
		            	ddfWeek=NumberFormat.getNumberInstance() ; 
		            	ddfWeek.setMaximumFractionDigits(2); 
		            	sWeekRatio= ddfWeek.format(sPointWeek.getRatio()) ;
		            	
		            	ddfMonth=NumberFormat.getNumberInstance() ; 
		            	ddfMonth.setMaximumFractionDigits(2); 
		            	sMonthRatio= ddfMonth.format(sPointMonth.getRatio()) ;
		            	
		            	//excel��ʾ��ֵ������
		            	showtext=sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sWeekRatio+"%,"+sMonthRatio+"%)"; 
		            	
		            	create07Cell(wb, rowDayData, extremeCol, showtext, fontDayWeekMonth,ConstantsInfo.XSSFNORMAL); 
		            	extremeCol++;
		            	break;
	            	default:
	            		break;	            		
	            	}	
	    		}

   			}
   		 }
    	    		
		os = new FileOutputStream("stockExcel.xlsx");
         
         wb.write(os);
          //  os.flush();
         os.close();

        return wb;  
     }     
    
    public static void initFont(XSSFWorkbook wb)
    {
    	hfNormal=new XSSFColor(java.awt.Color.BLACK);
        hfCday=new XSSFColor(java.awt.Color.RED);
        hfCweek=new XSSFColor(java.awt.Color.BLUE);
        hfCmonth=new XSSFColor(java.awt.Color.ORANGE);
        hfYuce=new XSSFColor(java.awt.Color.GREEN);
    	 // �ļ�ͷ����  
        font0 = create07Fonts(wb, XSSFFont.BOLDWEIGHT_BOLD, "����", false,  
                (short) 400,hfNormal);  
        fontDay = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "����", false,  
                (short) 300,hfCday);  
        fontDayWeek = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "����", false,  
                (short) 300,hfCweek);  
        fontDayWeekMonth = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "����", false,  
                (short) 300,hfCmonth); 
        fontYuce = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "����", false,  
                (short) 300,hfYuce);  
        
        //cell��ʽ����
        cellStyleNormal=create07CellStyle(wb,font0,ConstantsInfo.XSSFNORMAL);
        cellStyleDay=create07CellStyle(wb,fontDay,ConstantsInfo.XSSFNORMAL);
        cellStyleWeek=create07CellStyle(wb,fontDayWeek,ConstantsInfo.XSSFNORMAL);
        cellStyleWeekMonth=create07CellStyle(wb,fontDayWeekMonth,ConstantsInfo.XSSFNORMAL);
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
         for(i=0;i<stockLabelNum;i++)
         	sheet.setColumnWidth(i, 6000);
         //�� �� ��Ԥ��
         for(i=stockLabelNum;i<stockLabelNum+3;i++)
         	sheet.setColumnWidth(i, 12000);
         //�п�
         for(i=stockLabelNum+3;i<=stockMaxPointNum;i++)
         	sheet.setColumnWidth(i, 10000);
      
         // �ļ�ͷ����  
         initFont(wb); 
         
         
         //������һ��  
         Row rowHead = sheet.createRow(0);  
         
         //����һ������ı�  
         create07Cell(wb, rowHead, 0, "���", font0,IndexedColors.YELLOW.getIndex());  
         create07Cell(wb, rowHead, 1, "����", font0,IndexedColors.YELLOW.getIndex());  
         create07Cell(wb, rowHead, 2, "����", font0,IndexedColors.YELLOW.getIndex()); 	            
         create07Cell(wb, rowHead, 3, "��Ԥ��ֵ", font0,IndexedColors.YELLOW.getIndex()); 
         create07Cell(wb, rowHead, 4, "��Ԥ��ֵ", font0,IndexedColors.YELLOW.getIndex()); 
         create07Cell(wb, rowHead, 5, "��Ԥ��ֵ", font0,IndexedColors.YELLOW.getIndex()); 
         int stockColumn=stockLabelNum+3;
         //�п�
         
         //ʱ������ hash��
         for(;stockColumn<=stockMaxPointNum;stockColumn++)
 		{
         	String colName=""+stockColumn+"";            	
         	create07Cell(wb, rowHead, stockColumn, colName, font0,IndexedColors.YELLOW.getIndex()); 	
 		}
         
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
				 
			//	 initFont(wb);
				 
				 //��ǰ����
				 StockConcept concept= (StockConcept)itConcept.next();	
				 String conceptName=concept.getName();
				 if(conceptName==null)
					 continue;
				 
				stockLogger.logger.fatal("���"+conceptName);
				   			
	   			//����
	   			Row rowConcept = sheet.createRow(stockRow);  
	   			create07Cell(wb, rowConcept, 0, conceptName, font0,IndexedColors.BLUE.getIndex());  
	   			stockRow++;
	   			
	   			DecimalFormat decimalFormat=new DecimalFormat(".00");
	   			//�����������й�Ʊ
	   			listConceptStock=sbDao.getConceptStock(conceptName);
	   			
	   			stockLogger.logger.debug("�����Ʊ����"+listConceptStock.size());
	   			for(ie=listConceptStock.iterator();ie.hasNext();)
	   			{
	   				
	   			//��������
		   			//if(stockRow>10)
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
	    			create07Cell(wb, rowDayData, 0,  Integer.toString(stockRow), font0,ConstantsInfo.XSSFNORMAL);  
		            create07Cell(wb, rowDayData, 1, stockFullId, font0,ConstantsInfo.XSSFNORMAL);  
		            create07Cell(wb, rowDayData, 2, stockName, font0,ConstantsInfo.XSSFNORMAL); 	            
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
		            	
		            	create07Cell(wb, rowDayData, extremeCol, showtext, fontYuce,ConstantsInfo.XSSFNORMAL); 
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
		            	create07Cell(wb, rowDayData, extremeCol, showtext, fontYuce,ConstantsInfo.XSSFNORMAL); 
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
		            	create07Cell(wb, rowDayData, extremeCol, showtext, fontYuce,ConstantsInfo.XSSFNORMAL); 
		            	extremeCol++;
	    			}
	    			
	    			extremeCol=stockLabelNum+3;
	    			
	    			//��ʱ������� 
	                HashMap<String, StockPoint> stockWeekDateExist = new HashMap<String, StockPoint>(); 
	                HashMap<String, StockPoint> stockMonthDateExist = new HashMap<String, StockPoint>(); 
	                
	    			for(Iterator itWeek = stockWeekPoint.iterator();itWeek.hasNext();)
		 	    	{
		 	            StockPoint sPointWeek=(StockPoint)itWeek.next();	 	            
		 	           stockWeekDateExist.put(sPointWeek.getExtremeDate().toString(), sPointWeek);
		 	    	}
	    			//��ʱ������� 
	    			for(Iterator itMonth = stockMonthPoint.iterator();itMonth.hasNext();)
		 	    	{	
		 	            StockPoint sPointMonth=(StockPoint)itMonth.next();	 	            
		 	           stockMonthDateExist.put(sPointMonth.getExtremeDate().toString(), sPointMonth);
		 	    	}
	    			int type=0;	    	
	    			////�ж��գ��ܣ��¼�ֵ���Ƿ��ظ�
		          //  for(Iterator itDay = stockDayPoint.iterator();itDay.hasNext();)
		            for(int  itDay = stockDayPoint.size()-1;itDay>=0;itDay--)
		    		{
		            	//StockPoint sPointDay=(StockPoint)itDay.next();
		            	StockPoint sPointDay=(StockPoint)stockDayPoint.get(itDay);
		            	String extremeDate=sPointDay.getExtremeDate().toString();//ʱ��
		            	type=ConstantsInfo.ExtremeDateDay;
		            	StockPoint sPointWeek=stockWeekDateExist.get(extremeDate);
		            	if(sPointWeek!=null)
		            	{
		            		type=ConstantsInfo.ExtremeDateDayWeek;
		            	}
		            	
		            	StockPoint sPointMonth=stockMonthDateExist.get(extremeDate);
		            	if(sPointMonth!=null && type==ConstantsInfo.ExtremeDateDayWeek)
		            	{
		            		type=ConstantsInfo.ExtremeDateDayWeekMonth;
		            	}
		            	
		            	//NumberFormat ddfDay=null;
		            	//NumberFormat ddfWeek=null;
		            	//NumberFormat ddfMonth=null;
		            	String sDayRatio,sWeekRatio,sMonthRatio=null;		            	
		            	
		            	switch(type)
		            	{
		            	case ConstantsInfo.ExtremeDateDay:
		            		
			            	sDayRatio= ddfDay.format(sPointDay.getRatio()) ;
			            	//excel��ʾ��ֵ������
			            	showtext=sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%)"; 
		            	 	if(ConstantsInfo.DEBUG)
			        		{
				            	System.out.println("��ֵ��ʱ�䣺"+sPointDay.getExtremeDate());
				            	System.out.println("��ֵ�۸�"+sPointDay.getExtremePrice()); 
				            	System.out.println("������"+sPointDay.getRatio()); 
				            	System.out.println(showtext);
			        		}					         
					       
					         create07Cell(wb, rowDayData, extremeCol, showtext, fontDay,ConstantsInfo.XSSFNORMAL); 
					         extremeCol++;
		            		break;
		            	case ConstantsInfo.ExtremeDateDayWeek:
		            		
			            	sDayRatio= ddfDay.format(sPointDay.getRatio()) ;
			            	sWeekRatio= ddfDay.format(sPointWeek.getRatio()) ;
			            	
			            	//excel��ʾ��ֵ������
			            	showtext=sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sWeekRatio+"%)"; 
			            	
			            	create07Cell(wb, rowDayData, extremeCol, showtext, fontDayWeek,ConstantsInfo.XSSFNORMAL); 
			            	extremeCol++;
		            		break;
		            	case ConstantsInfo.ExtremeDateDayWeekMonth:
		            	
			            	sDayRatio= ddfDay.format(sPointDay.getRatio()) ;
			            	sWeekRatio= ddfDay.format(sPointWeek.getRatio()) ;
			            	sMonthRatio= ddfDay.format(sPointMonth.getRatio()) ;
			            	
			            	//excel��ʾ��ֵ������
			            	showtext=sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sWeekRatio+"%,"+sMonthRatio+"%)"; 
			            	
			            	create07Cell(wb, rowDayData, extremeCol, showtext, fontDayWeekMonth,ConstantsInfo.XSSFNORMAL); 
			            	extremeCol++;
			            	break;
		            	default:
		            		break;	            		
		            	}	
		            
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
				
				// initFont(wb);
				 
				 //��ǰ��ҵ
				 StockIndustry indu= (StockIndustry)itIndustry.next();	
				 String induName=indu.getThirdname();
				 if(induName==null)
					 continue;				
				stockLogger.logger.debug("��ҵ��"+induName);
	   			   			
	   			//��ҵ
	   			Row rowIndustry = sheet.createRow(stockRow);  
	   			create07Cell(wb, rowIndustry, 0, induName, font0,IndexedColors.BLUE.getIndex());  
	   			stockRow++;
	   			
	   			DecimalFormat decimalFormat=new DecimalFormat(".00");
	   			   	   			
	   			//������ҵ���й�Ʊ
	   			List<StockToIndustry> listIndustryStock = new ArrayList<StockToIndustry>();   
	   			listIndustryStock=sbDao.getIndustryToStock(indu.getThirdcode());		   		
	   			stockLogger.logger.debug("��ҵ��Ʊ����"+listIndustryStock.size());
	   			for(ie=listIndustryStock.iterator();ie.hasNext();)
	   			{
	   				//��������
	   			//	if(stockRow>5)
	   		   	//		break;
	   				StockToIndustry toInduStock = (StockToIndustry) ie.next();
	   				String stockFullId = toInduStock.getStockFullId();	
	   				System.out.println("stockFullId:"+stockFullId);
	   			
	   				int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_DATA_STOCK);
	   				if(isTableExist==0)//������
	   					continue;   				

	   				String stockName=sbDao.getStockNameFromIndustryTable(stockFullId);			
	   			
	   				System.out.println("fullId��"+stockFullId+" ���֣�"+stockName);
	   				stockLogger.logger.debug("fullId��"+stockFullId+" ���֣�"+stockName);
	   				//ÿ����Ʊ
	   				Row rowDayData = sheet.createRow(stockRow);  
	    			create07Cell(wb, rowDayData, 0,  Integer.toString(stockRow), font0,ConstantsInfo.XSSFNORMAL);  
		            create07Cell(wb, rowDayData, 1, stockFullId, font0,ConstantsInfo.XSSFNORMAL);  
		            create07Cell(wb, rowDayData, 2, stockName, font0,ConstantsInfo.XSSFNORMAL); 	            
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
		            	
		            	create07Cell(wb, rowDayData, extremeCol, showtext, fontYuce,ConstantsInfo.XSSFNORMAL); 
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
		            	create07Cell(wb, rowDayData, extremeCol, showtext, fontYuce,ConstantsInfo.XSSFNORMAL); 
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
		            	create07Cell(wb, rowDayData, extremeCol, showtext, fontYuce,ConstantsInfo.XSSFNORMAL); 
		            	extremeCol++;
	    			}
	    			
	    			extremeCol=stockLabelNum+3;
	    			
	    			//��ʱ���뼫�� hash 
	                HashMap<String, StockPoint> stockWeekDateExist = new HashMap<String, StockPoint>(); 
	               //��ʱ���뼫�� hash 
	                HashMap<String, StockPoint> stockMonthDateExist = new HashMap<String, StockPoint>(); 
	                
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
		            //for(Iterator itDay = stockDayPoint.iterator();itDay.hasNext();)
		            for(int  itDay = stockDayPoint.size()-1;itDay>=0;itDay--)
		    		{
		            	StockPoint sPointDay=(StockPoint)stockDayPoint.get(itDay);
		            	String extremeDate=sPointDay.getExtremeDate().toString();//ʱ��
		            	type=ConstantsInfo.ExtremeDateDay;
		            	StockPoint sPointWeek=stockWeekDateExist.get(extremeDate);
		            	if(sPointWeek!=null)
		            	{
		            		type=ConstantsInfo.ExtremeDateDayWeek;
		            	}
		            	
		            	StockPoint sPointMonth=stockMonthDateExist.get(extremeDate);
		            	if(sPointMonth!=null && type==ConstantsInfo.ExtremeDateDayWeek)
		            	{
		            		type=ConstantsInfo.ExtremeDateDayWeekMonth;
		            	}
		            			            	
		            	String sDayRatio,sWeekRatio,sMonthRatio=null;		          
		            	
		            	switch(type)
		            	{
		            	case ConstantsInfo.ExtremeDateDay:
		            		 
			            	sDayRatio= ddfDay.format(sPointDay.getRatio()) ;
			            	//excel��ʾ��ֵ������
			            	showtext=sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%)"; 
			            	if(ConstantsInfo.DEBUG)
				        	{
				            	System.out.println("��ֵ��ʱ�䣺"+sPointDay.getExtremeDate());
				            	System.out.println("��ֵ�۸�"+sPointDay.getExtremePrice()); 
				            	System.out.println("������"+sPointDay.getRatio()); 
				            	System.out.println(showtext);	
				        	}
					         create07Cell(wb, rowDayData, extremeCol, showtext, fontDay,ConstantsInfo.XSSFNORMAL); 
					         extremeCol++;
		            		break;
		            	case ConstantsInfo.ExtremeDateDayWeek:
		            		 
			            	sDayRatio = ddfDay.format(sPointDay.getRatio());
			            	sWeekRatio = ddfDay.format(sPointWeek.getRatio());
			            	
			            	//excel��ʾ��ֵ������
			            	showtext=sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sWeekRatio+"%)"; 
			            	
			            	create07Cell(wb, rowDayData, extremeCol, showtext, fontDayWeek,ConstantsInfo.XSSFNORMAL); 
			            	extremeCol++;
		            		break;
		            	case ConstantsInfo.ExtremeDateDayWeekMonth:
		            		
			            	sDayRatio= ddfDay.format(sPointDay.getRatio());
			            	sWeekRatio= ddfDay.format(sPointWeek.getRatio());
			            	sMonthRatio= ddfDay.format(sPointMonth.getRatio());
			            	
			            	//excel��ʾ��ֵ������
			            	showtext=sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sWeekRatio+"%,"+sMonthRatio+"%)"; 
			            	
			            	create07Cell(wb, rowDayData, extremeCol, showtext, fontDayWeekMonth,ConstantsInfo.XSSFNORMAL); 
			            	extremeCol++;
			            	break;
		            	default:
		            		break;	            		
		            	}	
		            
		    		}

	   			}

	            wb.write(fileOStream);
	            fileOStream.flush();
	            fileIStream.close();
	            fileOStream.close();   
	   		 } 
    }   
    
    //2007 ֻ��ʾ��Ʊ��ǰ���ڵļ�ֵ��
    public static XSSFWorkbook exportExcelForAllStockSimple() throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException 
    {    
      
    	//����excel�ļ�����  
    		XSSFWorkbook wb = new XSSFWorkbook();  
            //����һ���ű�  
            int lt=wb.getNumberOfSheets();
            FileOutputStream os=null;
            System.out.println(lt);
            int i=0;
            
        //    List<String> listStockFullId = new ArrayList<String>();
            List<StockInformation> listStockInfo = new ArrayList<StockInformation>(); 
            List<String> listIndustry = new ArrayList<String>(); 
        	List<String> listStockIndustry = new ArrayList<String>(); 
   
            listStockInfo=siDao.getStockDaoList();
    	//	listStockFullId=siDao.getAllFullId();
    		int sheetCount=0;
    		
    		List<String> listStockDate = new ArrayList<String>();
    		listStockDate=sdDao.getDatesFromSH000001("sh000001");
    		System.out.println("date size:"+listStockDate.size());
    		
        	XSSFSheet  sheet = wb.createSheet(); 
            wb.setSheetName(sheetCount,"allStock");
                        
            //�п�
            for(i=0;i<stockLabelNum;i++)
            	sheet.setColumnWidth(i, 6000);
            //�п�
            for(i=stockLabelNum+1;i<=stockMaxPointNum;i++)
            	sheet.setColumnWidth(i, 10000);
       
                
            //������һ��  
            Row rowHead = sheet.createRow(0);  
            
            XSSFColor hfNormal=new XSSFColor(java.awt.Color.BLACK);
            XSSFColor hfCday=new XSSFColor(java.awt.Color.RED);
            XSSFColor hfCweek=new XSSFColor(java.awt.Color.BLUE);
            XSSFColor hfCmonth=new XSSFColor(java.awt.Color.GREEN);
            
            /*
            // �ļ�ͷ����  
            XSSFFont font0 = create07Fonts(wb, XSSFFont.BOLDWEIGHT_BOLD, "����", false,  
                    (short) 400,XSSFFont.COLOR_NORMAL);  
            XSSFFont fontDay = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "����", false,  
                    (short) 400,XSSFFont.COLOR_RED);  
            */
            
            // �ļ�ͷ����  
            XSSFFont font0 = create07Fonts(wb, XSSFFont.BOLDWEIGHT_BOLD, "����", false,  
                    (short) 400,hfNormal);  
            XSSFFont fontDay = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "����", false,  
                    (short) 300,hfCday);  
            XSSFFont fontDayWeek = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "����", false,  
                    (short) 300,hfCweek);  
            XSSFFont fontDayWeekMonth = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "����", false,  
                    (short) 300,hfCmonth);  
             

            //����һ������ı�  
            create07Cell(wb, rowHead, 0, "���", font0,IndexedColors.YELLOW.getIndex());  
            create07Cell(wb, rowHead, 1, "����", font0,IndexedColors.YELLOW.getIndex());  
            create07Cell(wb, rowHead, 2, "����", font0,IndexedColors.YELLOW.getIndex()); 	            
            create07Cell(wb, rowHead, 3, "����", font0,IndexedColors.YELLOW.getIndex());            
            int stockColumn=4;
            //�п�
            
            //ʱ������ hash��
            for(;stockColumn<100;stockColumn++)
    		{
            	String colName=""+stockColumn+"";            	
            	create07Cell(wb, rowHead, stockColumn, colName, font0,IndexedColors.YELLOW.getIndex()); 	
    		}
       
            //�ڶ��п�ʼչʾ  
            int stockRow = 1;  
            
            listIndustry=siDao.getAllIndustry(listStockInfo);	
            System.out.println("��������"+listIndustry.size());
            Iterator itIndu,ie;
            int stockNum=0;
	   		 for(itIndu = listIndustry.iterator();itIndu.hasNext();)
	   		 {
	   			String stockIndu = (String) itIndu.next();			
	   			//System.out.println(stockIndu);
	   			
	   			if(!(stockIndu.equals("����ҵ")|| stockIndu.equals("ʯ�ͺ���Ȼ������ҵ")))
	   				continue;
	   			
	   			//��ҵ
	   			Row rowIndustry = sheet.createRow(stockRow);  
	   			create07Cell(wb, rowIndustry, 0, stockIndu, font0,IndexedColors.BLUE.getIndex());  
	   			stockRow++;
	   			
	   			listStockIndustry=siDao.getStockIndustry(stockIndu);
	   			System.out.println("�ð���Ʊ������"+listStockIndustry.size());
	   			for(ie=listStockIndustry.iterator();ie.hasNext();)
	   			{
	   				String stockFullId = (String) ie.next();	
	   				int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_DATA_STOCK);
	   				if(isTableExist==0)//������
	   					continue;   				

	   				StockInformation si=siDao.getStockInformation(stockFullId);			
	   				
	    		//	String stockFullId=si.getStockFullId();

	   			//��Ʊ
	    			Row rowDayData = sheet.createRow(stockRow);  
	    			
	    			create07Cell(wb, rowDayData, 0,  Integer.toString(stockRow), font0,ConstantsInfo.XSSFNORMAL);  
		            create07Cell(wb, rowDayData, 1, stockFullId, font0,ConstantsInfo.XSSFNORMAL);  
		            create07Cell(wb, rowDayData, 2, si.getStockName(), font0,ConstantsInfo.XSSFNORMAL); 	            
		            create07Cell(wb, rowDayData, 3, si.getStockRegional(), font0,ConstantsInfo.XSSFNORMAL); 

		            stockColumn=4;
		            
		            stockRow++;
		        //   if(stockRow>100)
		   		//		break;
		            
		       //    if(!stockFullId.equals("sh600000"))
				//		continue;
	    			
	    			//if(!stockFullId.equals("sh600000") && !stockFullId.equals("sz002142") && !stockFullId.equals("sz000001"))
	    			//		continue;
	    			List<StockPoint> stockDayPoint=new ArrayList<StockPoint>();
	    			List<StockPoint> stockWeekPoint=new ArrayList<StockPoint>();
	    			List<StockPoint> stockMonthPoint=new ArrayList<StockPoint>();
	    			
	    			
	    			DateStock dStock=sdDao.getDateStock(stockFullId);
	    			
	    			stockDayPoint=pClass.getStockDayExtremePoint(stockFullId, dStock,ConstantsInfo.StockCalAllData);
	    			stockWeekPoint=pClass.getStockWeekExtremePoint(stockFullId, dStock,ConstantsInfo.StockCalAllData);
	    			stockMonthPoint=pClass.getStockMonthExtremePoint(stockFullId, dStock,ConstantsInfo.StockCalAllData);
	    			System.out.println("day size:"+stockDayPoint.size());
	    			System.out.println("week size:"+stockWeekPoint.size());
	    			System.out.println("month size:"+stockMonthPoint.size());    			
	    				
		            //�ռ���
	    			System.out.println("��stockRow��"+stockRow);
	    			System.out.println("******all point*******");
	    			
	    			  //��ʱ������� 
	                HashMap<String, StockPoint> stockWeekDateExist = new HashMap<String, StockPoint>(); 
	                HashMap<String, StockPoint> stockMonthDateExist = new HashMap<String, StockPoint>(); 
	                
	    			for(Iterator itWeek = stockWeekPoint.iterator();itWeek.hasNext();)
		 	    	{
		 	            StockPoint sPointWeek=(StockPoint)itWeek.next();	 	            
		 	           stockWeekDateExist.put(sPointWeek.getExtremeDate().toString(), sPointWeek);
		 	    	}
	    			//��ʱ������� 
	    			for(Iterator itMonth = stockMonthPoint.iterator();itMonth.hasNext();)
		 	    	{	
		 	            StockPoint sPointMonth=(StockPoint)itMonth.next();	 	            
		 	           stockMonthDateExist.put(sPointMonth.getExtremeDate().toString(), sPointMonth);
		 	    	}
	    			int type=0;
	    			int extremeCol=4;
		            for(Iterator itDay = stockDayPoint.iterator();itDay.hasNext();)
		    		{
		            	StockPoint sPointDay=(StockPoint)itDay.next();
		            	String extremeDate=sPointDay.getExtremeDate().toString();//ʱ��
		            	type=ConstantsInfo.ExtremeDateDay;
		            	StockPoint sPointWeek=stockWeekDateExist.get(extremeDate);
		            	if(sPointWeek!=null)
		            	{
		            		type=ConstantsInfo.ExtremeDateDayWeek;
		            	}
		            	
		            	StockPoint sPointMonth=stockMonthDateExist.get(extremeDate);
		            	if(sPointMonth!=null && type==ConstantsInfo.ExtremeDateDayWeek)
		            	{
		            		type=ConstantsInfo.ExtremeDateDayWeekMonth;
		            	}
		            	
		            	NumberFormat ddfDay=null;
		            	NumberFormat ddfWeek=null;
		            	NumberFormat ddfMonth=null;
		            	String sDayRatio,sWeekRatio,sMonthRatio=null;
		            	String showtext=null;
		            	
		            	switch(type)
		            	{
		            	case ConstantsInfo.ExtremeDateDay:
		            		//����������λС����
			            	ddfDay=NumberFormat.getNumberInstance() ; 
			            	ddfDay.setMaximumFractionDigits(2); 
			            	sDayRatio= ddfDay.format(sPointDay.getRatio()) ;
			            	//excel��ʾ��ֵ������
			            	showtext=sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%)"; 
			            	 //	if(ConstantsInfo.DEBUG)
				        	//	{
					            	System.out.println("��ֵ��ʱ�䣺"+sPointDay.getExtremeDate());
					            	System.out.println("��ֵ�۸�"+sPointDay.getExtremePrice()); 
					            	System.out.println("������"+sPointDay.getRatio()); 
					            	System.out.println(showtext);
					            	
				        	//	}
					         
					       
					         create07Cell(wb, rowDayData, extremeCol, showtext, fontDay,ConstantsInfo.XSSFNORMAL); 
					         extremeCol++;
		            		break;
		            	case ConstantsInfo.ExtremeDateDayWeek:
		            		//����������λС����
		            		ddfDay=NumberFormat.getNumberInstance() ; 
			            	ddfDay.setMaximumFractionDigits(2); 
			            	sDayRatio= ddfDay.format(sPointDay.getRatio()) ;
			            	
			            	ddfWeek=NumberFormat.getNumberInstance() ; 
			            	ddfWeek.setMaximumFractionDigits(2); 
			            	sWeekRatio= ddfWeek.format(sPointWeek.getRatio()) ;
			            	
			            	//excel��ʾ��ֵ������
			            	showtext=sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sWeekRatio+"%)"; 
			            	
			            	create07Cell(wb, rowDayData, extremeCol, showtext, fontDayWeek,ConstantsInfo.XSSFNORMAL); 
			            	extremeCol++;
		            		break;
		            	case ConstantsInfo.ExtremeDateDayWeekMonth:
		            		//����������λС����
		            		ddfDay=NumberFormat.getNumberInstance() ; 
			            	ddfDay.setMaximumFractionDigits(2); 
			            	sDayRatio= ddfDay.format(sPointDay.getRatio()) ;
			            	
			            	ddfWeek=NumberFormat.getNumberInstance() ; 
			            	ddfWeek.setMaximumFractionDigits(2); 
			            	sWeekRatio= ddfWeek.format(sPointWeek.getRatio()) ;
			            	
			            	ddfMonth=NumberFormat.getNumberInstance() ; 
			            	ddfMonth.setMaximumFractionDigits(2); 
			            	sMonthRatio= ddfMonth.format(sPointMonth.getRatio()) ;
			            	
			            	//excel��ʾ��ֵ������
			            	showtext=sPointDay.getExtremeDate()+":"+Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sWeekRatio+"%,"+sMonthRatio+"%)"; 
			            	
			            	create07Cell(wb, rowDayData, extremeCol, showtext, fontDayWeekMonth,ConstantsInfo.XSSFNORMAL); 
			            	extremeCol++;
			            	break;
		            	default:
		            		break;	            		
		            	}	
		    		}

	   			}
	   		 }
    		
		os = new FileOutputStream("stockExcel.xlsx",true);
         
         wb.write(os);
          os.flush();
         os.close();

        return wb;  
    }     
      
    
    //2007   day week monthһ��
    public static XSSFWorkbook exportExcelForAllStock() throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException 
    {    
      
    	//����excel�ļ�����  
    		XSSFWorkbook wb = new XSSFWorkbook();  
            //����һ���ű�  
            int lt=wb.getNumberOfSheets();
            FileOutputStream os=null;
            System.out.println(lt);
            
        //    List<String> listStockFullId = new ArrayList<String>();
            List<StockInformation> listStockInfo = new ArrayList<StockInformation>(); 
            List<String> listIndustry = new ArrayList<String>(); 
        	List<String> listStockIndustry = new ArrayList<String>(); 
   
            listStockInfo=siDao.getStockDaoList();
    	//	listStockFullId=siDao.getAllFullId();
    		int sheetCount=0;
    		
    		List<String> listStockDate = new ArrayList<String>();
    		listStockDate=sdDao.getDatesFromSH000001("sh000001");
    		System.out.println("date size:"+listStockDate.size());
    		
        	XSSFSheet  sheet = wb.createSheet(); 
            wb.setSheetName(sheetCount,"allStock");
            
            //�п�
            for(int i=0;i<=4+listStockDate.size();i++)
            {	
            	sheet.setColumnWidth(i, 6000);
            
            }
                
            //������һ��  
            Row rowHead = sheet.createRow(0);  
            
            XSSFColor hfNormal=new XSSFColor(java.awt.Color.BLACK);
            XSSFColor hfCday=new XSSFColor(java.awt.Color.RED);
            XSSFColor hfCweek=new XSSFColor(java.awt.Color.BLUE);
            XSSFColor hfCmonth=new XSSFColor(java.awt.Color.GREEN);
            
            /*
            // �ļ�ͷ����  
            XSSFFont font0 = create07Fonts(wb, XSSFFont.BOLDWEIGHT_BOLD, "����", false,  
                    (short) 400,XSSFFont.COLOR_NORMAL);  
            XSSFFont fontDay = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "����", false,  
                    (short) 400,XSSFFont.COLOR_RED);  
            */
            
            // �ļ�ͷ����  
            XSSFFont font0 = create07Fonts(wb, XSSFFont.BOLDWEIGHT_BOLD, "����", false,  
                    (short) 400,hfNormal);  
            XSSFFont fontDay = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "����", false,  
                    (short) 400,hfCday);  
            XSSFFont fontDayWeek = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "����", false,  
                    (short) 400,hfCweek);  
            XSSFFont fontDayWeekMonth = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "����", false,  
                    (short) 400,hfCmonth);  
             

            //����һ������ı�  
            create07Cell(wb, rowHead, 0, "���", font0,IndexedColors.YELLOW.getIndex());  
            create07Cell(wb, rowHead, 1, "����", font0,IndexedColors.YELLOW.getIndex());  
            create07Cell(wb, rowHead, 2, "����", font0,IndexedColors.YELLOW.getIndex()); 	            
            create07Cell(wb, rowHead, 3, "����", font0,IndexedColors.YELLOW.getIndex());            
            int stockColumn=4;
            //�п�
            
            //ʱ������ hash��
            HashMap<String, Integer> stockDateColumnmap = new HashMap<String, Integer>(); 
            for(Iterator it = listStockDate.iterator();it.hasNext();)
    		{
            	String stockDate=(String)it.next();            	
            	create07Cell(wb, rowHead, stockColumn, stockDate, font0,IndexedColors.YELLOW.getIndex()); 	
            	stockDateColumnmap.put(stockDate, stockColumn);
            	stockColumn++;
    		}           
           
       
            //�ڶ��п�ʼչʾ  
            int stockRow = 1;  
            
            listIndustry=siDao.getAllIndustry(listStockInfo);	
            System.out.println("��������"+listIndustry.size());
            Iterator itIndu,ie;
            int stockNum=0;
	   		 for(itIndu = listIndustry.iterator();itIndu.hasNext();)
	   		 {
	   			String stockIndu = (String) itIndu.next();			
	   			//System.out.println(stockIndu);   			
	   		
	   			
	   			//��ҵ
	   			Row rowIndustry = sheet.createRow(stockRow);  
	   			create07Cell(wb, rowIndustry, 0, stockIndu, font0,IndexedColors.BLUE.getIndex());  
	   			stockRow++;
	   			
	   			listStockIndustry=siDao.getStockIndustry(stockIndu);
	   			System.out.println("�ð���Ʊ������"+listStockIndustry.size());
	   			for(ie=listStockIndustry.iterator();ie.hasNext();)
	   			{
	   				String stockFullId = (String) ie.next();	
	   				int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_DATA_STOCK);
	   				if(isTableExist==0)//������
	   					continue;   				

	   				StockInformation si=siDao.getStockInformation(stockFullId);			
	   				
	    		//	String stockFullId=si.getStockFullId();
	    		
    			
	   			//��Ʊ
	    			Row rowDayData = sheet.createRow(stockRow);  
	    			
	    			create07Cell(wb, rowDayData, 0,  Integer.toString(stockRow), font0,ConstantsInfo.XSSFNORMAL);  
		            create07Cell(wb, rowDayData, 1, stockFullId, font0,ConstantsInfo.XSSFNORMAL);  
		            create07Cell(wb, rowDayData, 2, si.getStockName(), font0,ConstantsInfo.XSSFNORMAL); 	            
		            create07Cell(wb, rowDayData, 3, si.getStockRegional(), font0,ConstantsInfo.XSSFNORMAL); 

		            stockColumn=4;
		            
		            stockRow++;
		        //   if(stockRow>100)
		   		//		break;
		            
		            //if(!stockFullId.equals("sh000001"))
					//		continue;
	    			
	    			if(!stockFullId.equals("sh600000") && !stockFullId.equals("sz002142") && !stockFullId.equals("sz000001"))
	    					continue;
	    			List<StockPoint> stockDayPoint=new ArrayList<StockPoint>();
	    			List<StockPoint> stockWeekPoint=new ArrayList<StockPoint>();
	    			List<StockPoint> stockMonthPoint=new ArrayList<StockPoint>();
	    			
	    			
	    			DateStock dStock=sdDao.getDateStock(stockFullId);
	    			
	    			stockDayPoint=pClass.getStockDayExtremePoint(stockFullId, dStock,ConstantsInfo.StockCalAllData);
	    			stockWeekPoint=pClass.getStockWeekExtremePoint(stockFullId, dStock,ConstantsInfo.StockCalAllData);
	    			stockMonthPoint=pClass.getStockMonthExtremePoint(stockFullId, dStock,ConstantsInfo.StockCalAllData);
	    			System.out.println("day size:"+stockDayPoint.size());
	    			System.out.println("week size:"+stockWeekPoint.size());
	    			System.out.println("month size:"+stockMonthPoint.size());    			
	    				
		            //�ռ���
	    			System.out.println("��stockRow��"+stockRow);
	    			System.out.println("******all point*******");
	    			
	    			  //��ʱ������� 
	                HashMap<String, StockPoint> stockWeekDateExist = new HashMap<String, StockPoint>(); 
	                HashMap<String, StockPoint> stockMonthDateExist = new HashMap<String, StockPoint>(); 
	                
	    			for(Iterator itWeek = stockWeekPoint.iterator();itWeek.hasNext();)
		 	    	{
		 	            StockPoint sPointWeek=(StockPoint)itWeek.next();	 	            
		 	           stockWeekDateExist.put(sPointWeek.getExtremeDate().toString(), sPointWeek);
		 	    	}
	    			//��ʱ������� 
	    			for(Iterator itMonth = stockMonthPoint.iterator();itMonth.hasNext();)
		 	    	{	
		 	            StockPoint sPointMonth=(StockPoint)itMonth.next();	 	            
		 	           stockMonthDateExist.put(sPointMonth.getExtremeDate().toString(), sPointMonth);
		 	    	}
	    			int type=0;
		            for(Iterator itDay = stockDayPoint.iterator();itDay.hasNext();)
		    		{
		            	StockPoint sPointDay=(StockPoint)itDay.next();
		            	String extremeDate=sPointDay.getExtremeDate().toString();//ʱ��
		            	type=ConstantsInfo.ExtremeDateDay;
		            	StockPoint sPointWeek=stockWeekDateExist.get(extremeDate);
		            	if(sPointWeek!=null)
		            	{
		            		type=ConstantsInfo.ExtremeDateDayWeek;
		            	}
		            	
		            	StockPoint sPointMonth=stockMonthDateExist.get(extremeDate);
		            	if(sPointMonth!=null && type==ConstantsInfo.ExtremeDateDayWeek)
		            	{
		            		type=ConstantsInfo.ExtremeDateDayWeekMonth;
		            	}
		            	
		            	NumberFormat ddfDay=null;
		            	NumberFormat ddfWeek=null;
		            	NumberFormat ddfMonth=null;
		            	String sDayRatio,sWeekRatio,sMonthRatio=null;
		            	String showtext=null;
		            	int extremeCol=0;
		            	switch(type)
		            	{
		            	case ConstantsInfo.ExtremeDateDay:
		            		//����������λС����
			            	ddfDay=NumberFormat.getNumberInstance() ; 
			            	ddfDay.setMaximumFractionDigits(2); 
			            	sDayRatio= ddfDay.format(sPointDay.getRatio()) ;
			            	//excel��ʾ��ֵ������
			            	showtext=Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%)"; 
			            	 //	if(ConstantsInfo.DEBUG)
				        	//	{
					            	System.out.println("��ֵ��ʱ�䣺"+sPointDay.getExtremeDate());
					            	System.out.println("��ֵ�۸�"+sPointDay.getExtremePrice()); 
					            	System.out.println("������"+sPointDay.getRatio()); 
					            	System.out.println(showtext);
					            	
				        	//	}
					         extremeCol = stockDateColumnmap.get(extremeDate);
					         create07Cell(wb, rowDayData, extremeCol, showtext, fontDay,ConstantsInfo.XSSFNORMAL); 
				            	
		            		break;
		            	case ConstantsInfo.ExtremeDateDayWeek:
		            		//����������λС����
		            		ddfDay=NumberFormat.getNumberInstance() ; 
			            	ddfDay.setMaximumFractionDigits(2); 
			            	sDayRatio= ddfDay.format(sPointDay.getRatio()) ;
			            	
			            	ddfWeek=NumberFormat.getNumberInstance() ; 
			            	ddfWeek.setMaximumFractionDigits(2); 
			            	sWeekRatio= ddfWeek.format(sPointWeek.getRatio()) ;
			            	
			            	//excel��ʾ��ֵ������
			            	showtext=Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sWeekRatio+"%)"; 
			            	extremeCol = stockDateColumnmap.get(extremeDate);
			            	create07Cell(wb, rowDayData, extremeCol, showtext, fontDayWeek,ConstantsInfo.XSSFNORMAL); 
		            		
		            		break;
		            	case ConstantsInfo.ExtremeDateDayWeekMonth:
		            		//����������λС����
		            		ddfDay=NumberFormat.getNumberInstance() ; 
			            	ddfDay.setMaximumFractionDigits(2); 
			            	sDayRatio= ddfDay.format(sPointDay.getRatio()) ;
			            	
			            	ddfWeek=NumberFormat.getNumberInstance() ; 
			            	ddfWeek.setMaximumFractionDigits(2); 
			            	sWeekRatio= ddfWeek.format(sPointWeek.getRatio()) ;
			            	
			            	ddfMonth=NumberFormat.getNumberInstance() ; 
			            	ddfMonth.setMaximumFractionDigits(2); 
			            	sMonthRatio= ddfMonth.format(sPointMonth.getRatio()) ;
			            	
			            	//excel��ʾ��ֵ������
			            	showtext=Float.toString(sPointDay.getExtremePrice())+"("+sDayRatio+"%,"+sWeekRatio+"%,"+sMonthRatio+"%)"; 
			            	extremeCol = stockDateColumnmap.get(extremeDate);
			            	create07Cell(wb, rowDayData, extremeCol, showtext, fontDayWeekMonth,ConstantsInfo.XSSFNORMAL); 
		            		break;
		            	default:
		            		break;	            		
		            	}	
		    		}

	   			}
	   		 }
    		
    		os = new FileOutputStream("stockExcel.xlsx");
	         
	         wb.write(os);
	          //  os.flush();
	         os.close();
    
            return wb;  
        }     
      
      
    /** 
         * 03excel ������Ԫ��������ʽ,ֵ 
         *  
         * @param wb 
         * @param row 
         * @param column 
         * @param 
         * @param 
         * @param value 
         */  
        public static void create03Cell(Workbook wb, Row row, int column, String value, HSSFFont font) {  
            Cell cell = row.createCell(column);  
            cell.setCellValue(value);  
            CellStyle cellStyle = wb.createCellStyle();  
            cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);  
            cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_BOTTOM);  
            cellStyle.setFont(font);  
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
         * 03excel �������� 
         *  
         * @param wb 
         * @return 
         */  
        public static HSSFFont create03Fonts(Workbook wb, short bold, String fontName,  
                boolean isItalic, short hight) {  
        	HSSFFont font = (HSSFFont) wb.createFont();  
            font.setFontName(fontName);  
            font.setBoldweight(bold);  
            font.setItalic(isItalic);  
            font.setFontHeight(hight);  
            return font;  
        }  
        /** 
         * 07excel �������� 
         *  
         * @param wb 
         * @return 
         */ 
        /*
        public static XSSFFont create07Fonts(Workbook wb, short bold, String fontName, boolean isItalic, short hight,short color) {  
        	XSSFFont font = (XSSFFont) wb.createFont();  
            font.setFontName(fontName);  
            font.setBoldweight(bold);  
            font.setItalic(isItalic);  
            font.setFontHeight(hight);  
            font.setColor(color);             
            return font;  
        }  */
        
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
        
        
        //2007   day week monthһ��
        public static XSSFWorkbook exportExcelForStockIndustry() throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException 
        {    
          
        	//����excel�ļ�����  
        		XSSFWorkbook wb = new XSSFWorkbook();  
                //����һ���ű�  
                int lt=wb.getNumberOfSheets();
                FileOutputStream os=null;
                System.out.println(lt);
                
            //    List<String> listStockFullId = new ArrayList<String>();
                List<StockInformation> listStockInfo = new ArrayList<StockInformation>(); 
                List<String> listIndustry = new ArrayList<String>(); 
            	List<String> listStockIndustry = new ArrayList<String>(); 
       
                listStockInfo=siDao.getStockDaoList();
        	//	listStockFullId=siDao.getAllFullId();
        		int sheetCount=0;
        		
        		List<String> listStockDate = new ArrayList<String>();
        		listStockDate=sdDao.getDatesFromSH000001("sh000001");
        		System.out.println("date size:"+listStockDate.size());
        		
            	XSSFSheet  sheet = wb.createSheet(); 
                wb.setSheetName(sheetCount,"allStock");
                
               
                    
                //������һ��  
                Row rowHead = sheet.createRow(0);  
                
                XSSFColor hfNormal=new XSSFColor(java.awt.Color.BLACK);
                XSSFColor hfCday=new XSSFColor(java.awt.Color.RED);
                XSSFColor hfCweek=new XSSFColor(java.awt.Color.BLUE);
                XSSFColor hfCmonth=new XSSFColor(java.awt.Color.GREEN);
                
                /*
                // �ļ�ͷ����  
                XSSFFont font0 = create07Fonts(wb, XSSFFont.BOLDWEIGHT_BOLD, "����", false,  
                        (short) 400,XSSFFont.COLOR_NORMAL);  
                XSSFFont fontDay = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "����", false,  
                        (short) 400,XSSFFont.COLOR_RED);  
                */
                
                // �ļ�ͷ����  
                XSSFFont font0 = create07Fonts(wb, XSSFFont.BOLDWEIGHT_BOLD, "����", false,  
                        (short) 400,hfNormal);  
                XSSFFont fontDay = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "����", false,  
                        (short) 400,hfCday);  
                XSSFFont fontDayWeek = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "����", false,  
                        (short) 400,hfCweek);  
                XSSFFont fontDayWeekMonth = create07Fonts(wb, XSSFFont.BOLDWEIGHT_NORMAL, "����", false,  
                        (short) 400,hfCmonth);  
                 

                //����һ������ı�  
                create07Cell(wb, rowHead, 0, "���", font0,IndexedColors.YELLOW.getIndex());  
                create07Cell(wb, rowHead, 1, "����", font0,IndexedColors.YELLOW.getIndex());  
                create07Cell(wb, rowHead, 2, "����", font0,IndexedColors.YELLOW.getIndex()); 	            
                create07Cell(wb, rowHead, 3, "����", font0,IndexedColors.YELLOW.getIndex());            
                int stockColumn=4;
               
                //�ڶ��п�ʼչʾ  
                int stockRow = 1;  
                
                listIndustry=siDao.getAllIndustry(listStockInfo);	
                System.out.println("��������"+listIndustry.size());
                Iterator itIndu,ie;
                int stockNum=0;
    	   		 for(itIndu = listIndustry.iterator();itIndu.hasNext();)
    	   		 {
    	   			String stockIndu = (String) itIndu.next();			
    	   			//System.out.println(stockIndu);
    	   			
    	   		//	if(stockRow>100)
    	   		//		break;
    	   			
    	   			//��ҵ
    	   			Row rowIndustry = sheet.createRow(stockRow);  
    	   			create07Cell(wb, rowIndustry, 0, stockIndu, font0,IndexedColors.BLUE.getIndex());  
    	   			stockRow++;
    	   			
    	   			listStockIndustry=siDao.getStockIndustry(stockIndu);
    	   			System.out.println("�ð���Ʊ������"+listStockIndustry.size());
    	   			for(ie=listStockIndustry.iterator();ie.hasNext();)
    	   			{
    	   				String stockFullId = (String) ie.next();	
    	   				int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_DATA_STOCK);
    	   				if(isTableExist==0)//������
    	   					continue;   				

    	   				StockInformation si=siDao.getStockInformation(stockFullId);			
    	   				
    	    		//	String stockFullId=si.getStockFullId();
    	    		
        			
    	   			//��Ʊ
    	    			Row rowDayData = sheet.createRow(stockRow);  
    	    			
    	    			create07Cell(wb, rowDayData, 0,  Integer.toString(stockRow), font0,ConstantsInfo.XSSFNORMAL);  
    		            create07Cell(wb, rowDayData, 1, stockFullId, font0,ConstantsInfo.XSSFNORMAL);  
    		            create07Cell(wb, rowDayData, 2, si.getStockName(), font0,ConstantsInfo.XSSFNORMAL); 	            
    		            create07Cell(wb, rowDayData, 3, si.getStockRegional(), font0,ConstantsInfo.XSSFNORMAL); 

    		          stockColumn=4;
    		            
    		            stockRow++;
    		      		            
    		            //if(!stockFullId.equals("sh000001"))
    					//		continue;
    	    			
    	    			if(!stockFullId.equals("sh600000") && !stockFullId.equals("sz002142") && !stockFullId.equals("sz000001"))
    	    					continue;
    	    			List<StockPoint> stockDayPoint=new ArrayList<StockPoint>();
    	    			List<StockPoint> stockWeekPoint=new ArrayList<StockPoint>();
    	    			List<StockPoint> stockMonthPoint=new ArrayList<StockPoint>();
    	    			
    	    			
    	    			DateStock dStock=sdDao.getDateStock(stockFullId);
    	    			
    	    			stockDayPoint=pClass.getStockDayExtremePoint(stockFullId, dStock,ConstantsInfo.StockCalAllData);
    	    			stockWeekPoint=pClass.getStockWeekExtremePoint(stockFullId, dStock,ConstantsInfo.StockCalAllData);
    	    			stockMonthPoint=pClass.getStockMonthExtremePoint(stockFullId, dStock,ConstantsInfo.StockCalAllData);
    	    			System.out.println("day size:"+stockDayPoint.size());
    	    			System.out.println("week size:"+stockWeekPoint.size());
    	    			System.out.println("month size:"+stockMonthPoint.size());    			
    	    				
    		            //�ռ���
    	    			System.out.println("��stockRow��"+stockRow);
    	    			System.out.println("******all point*******");
    	    			
    	    			  //��ʱ������� 
    	                HashMap<String, StockPoint> stockWeekDateExist = new HashMap<String, StockPoint>(); 
    	                HashMap<String, StockPoint> stockMonthDateExist = new HashMap<String, StockPoint>(); 
    	                
    	    			for(Iterator itWeek = stockWeekPoint.iterator();itWeek.hasNext();)
    		 	    	{
    		 	            StockPoint sPointWeek=(StockPoint)itWeek.next();	 	            
    		 	           stockWeekDateExist.put(sPointWeek.getExtremeDate().toString(), sPointWeek);
    		 	    	}
    	    			
    	   			}
    	   		 }
        		
        		os = new FileOutputStream("stockExcel.xlsx");
    	         
    	         wb.write(os);
    	          //  os.flush();
    	         os.close();
        
                return wb;  
            }     
          
        
        public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException {
    		
        	PropertyConfigurator.configure("log4j.excelWriter.properties");
        	
    		ExcelWriterV2 ew=new ExcelWriterV2();
    		
    		HSSFWorkbook wb;
    		Date startDate = new Date();
    	//	wb=ew.exportExcelForStock();
    		
    		//XSSFWorkbook xwb;
    		//xwb=ew.exportExcelForAllStock();
    		
    		//	xwb=ew.exportExcelForAllStockSimple();//��Ʊ��ֵ����ʾһ�� ��ʱ�������
    		
    		//��ǰ·��
    		ew.exportExcelAllStockForIndustry("load\\","stockPointIndustry.xlsx");//��Ʊ��ֵ����ʾһ�� ��ȡpointStock��

    		//��ǰ·��
    		ew.exportExcelAllStockForConcept("load\\","stockPointConcept.xlsx");//��Ʊ��ֵ����ʾһ�� ��ȡpointStock��
    		
    		
    		Date endDate = new Date();
    		// ��������ʱ�����������
    		long seconds =(endDate.getTime() - startDate.getTime())/1000;
    		System.out.println("�ܹ���ʱ��"+seconds+"��");
    		System.out.println("end");
    		stockLogger.logger.debug("end");
    			
    	}
}
