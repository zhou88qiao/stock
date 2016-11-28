package excel.all_v1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.point.stock.PointClass;

import common.ConstantsInfo;
import dao.DbConn;
import dao.StockBaseDao;
import dao.StockDataDao;
import dao.StockInformationDao;
import dao.StockMarket;
import dao.StockPointDao;
import dao.StockSingle;
import date.timer.stockDateTimer;

public class ExcelCommon {
		
	final static int stockLabelNum=5;
	final static int stockAnalyiseNum=8;
	final static int stockStatNum=8; //ͳ��
	final static int FirstRowFirstColNum=27; //��һ�� �� �� �·�������
	final static int FirstRowSecondColNum=15; //��һ�� �� �� �¶Աȷ�������
	final static int stockUpNum=8; //��
	final static int stockDownNum=8; //��
	final static int stockCurNum=3;
	final static int stockDesireNum=2;
	final static int stockOperateNum=3;
	
	final static int stockGapNum=15; //
	
	final static int stockMaxPointNum=300;
	final static int largeColWidth=4500;
	final static int middleColWidth=3500;
	final static int middleColWidth2=2800;
	final static int smallColWidth=2000;
	
	public ExcelCommon()
	{
		
	}
	
	//С��תΪ�ٷֱ�
	public static  String  changePercent(float value)
	{
		  //��ȡ��ʽ������
		   NumberFormat nt = NumberFormat.getPercentInstance();
		   //���ðٷ�����ȷ��2��������λС��
		   nt.setMinimumFractionDigits(2);
	
		   return nt.format(value);
	}
	
	 //excel07 
    public static void create07Cell(Workbook wb, Row row, int column, String value, int color) {  
        
    	XSSFColor xfcolor = null;
    	Cell cell = row.createCell(column); 
        
        CellStyle cellStyle = wb.createCellStyle();  
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);  
        cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_BOTTOM);  
        
        switch(color) {
        case ConstantsInfo.REDColor:
        	xfcolor=new XSSFColor(java.awt.Color.RED);
        	break;
        case ConstantsInfo.CYANColor:
        	xfcolor=new XSSFColor(java.awt.Color.CYAN);
        	break;
        case ConstantsInfo.GRAYColor:
        	xfcolor=new XSSFColor(java.awt.Color.GRAY);
        	break;
        case ConstantsInfo.BLUEColor:
        	xfcolor=new XSSFColor(java.awt.Color.BLUE);
        	break;
        case ConstantsInfo.ORANGEColor:
        	xfcolor=new XSSFColor(java.awt.Color.ORANGE);
        	break;
        case ConstantsInfo.GREENColor:
        	xfcolor=new XSSFColor(java.awt.Color.GREEN);
        	break;
        default:
        	break;
        }
       
        if (color >0){
	       // �ļ�ͷ����       
	        XSSFFont font = (XSSFFont) wb.createFont();  
	        font.setFontName("����");  
	      //  font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);  
	      //  font.setItalic(false);  
	      //  font.setFontHeight((short) 250);  
	        font.setColor(xfcolor); 
	        cellStyle.setFont(font);  
        }
        
        
      //  cellStyle.setFillBackgroundColor(HSSFColor.BLUE.index);
       // IndexedColors.YELLOW.getIndex()
        /*
        if(color>0) {
        	cellStyle.setFillForegroundColor((short) color);            
        	cellStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);  
        }
       
    
        // ## �����ұ߱߿���ɫΪ��ɫ ##//  
        if (column == 2) {
        	cellStyle.setRightBorderColor(new HSSFColor.RED().getIndex());  
        } */
        
        cell.setCellStyle(cellStyle);  
        cell.setCellValue(value);  
    } 
    
    public static XSSFFont create07Fonts(Workbook wb, short bold, String fontName, boolean isItalic, short hight,XSSFColor color) {  
    	XSSFFont font = (XSSFFont) wb.createFont();  
        font.setFontName(fontName);  
        font.setBoldweight(bold);  
        font.setItalic(isItalic);  
        font.setFontHeight(hight);  
        font.setColor(color);             
        return font;  
    }  
    
    
  //����excel
    public static void createRecentExcel(String filePath, String fileName,StockDataDao sdDao,HashMap<String, Integer> stockDateColumnmap,int type) throws IOException, SQLException
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
		
		//��������Ŀ¼
		file = new File(filePath+fileName);  
		System.out.println(fileName);
		if (!file.exists())
		{   
			 file.mkdir();   
		}
		
		String excleFileName=null;
		if (type == 0)
			excleFileName="Stock_Concept_Simple_"+fileName+".xlsx";
		else
			excleFileName="Stock_Industry_Simple_"+fileName+".xlsx";
		
		 os = new FileOutputStream(filePath +fileName+ "\\"+excleFileName);  
		 // ������
		 XSSFWorkbook wb = new XSSFWorkbook();   
		 // ������һ��sheet     
		 XSSFSheet sheet= wb.createSheet("allstock");
		
    
         //������һ��  
         Row rowHead = sheet.createRow(0);  

         int col=stockLabelNum;
         //����һ������ı�                
         create07Cell(wb, rowHead, 0, "���", 0);  
         create07Cell(wb, rowHead, 1, "����", 0);  
         create07Cell(wb, rowHead, 2, "����", 0); 	            
         create07Cell(wb, rowHead, 3, "����", 0);   
         
        List<String> listStockDate = new ArrayList<String>();
       //���������ʱ��
        Date dt=new Date();
        stockDateTimer dateTimer = new stockDateTimer();
       String  cDate  = dateTimer.getAddDate(dt,-90);
       System.out.println("������ǰ��"+cDate);		
     
		listStockDate=sdDao.getDatesFromSH000001RecentDate(cDate);
 		System.out.println("date size:"+listStockDate.size());	
 		//�п�
        for(i=0;i<stockLabelNum;i++)
        {	
        	sheet.setColumnWidth(i, 3500);         
        }
         //�п�
         for(i=stockLabelNum;i<=stockLabelNum+listStockDate.size()+1;i++)
         {	
         	sheet.setColumnWidth(i, 2000);    
         }
         
         /*
         create07Cell(wb, rowHead, 4, "��ǰ�ռ���", cellStyleTitle);
         create07Cell(wb, rowHead, 5, "��ǰ�ܼ���", cellStyleTitle);
         create07Cell(wb, rowHead, 6, "��ǰ�¼���", cellStyleTitle);
        */ 
         int  stockColumn=stockLabelNum;//Ԥ����������ǰ����
             
       
        for(Iterator it = listStockDate.iterator();it.hasNext();)
 		{
        	//2015-08-20
         	String stockDate=(String)it.next();  
         	stockDateColumnmap.put(stockDate, stockColumn);
         	stockDate=stockDate.substring(5);
         	//ȡ���£��� 
         	create07Cell(wb, rowHead, stockColumn, stockDate, 0); 
         	
         	stockColumn++;
 		}  
         

		 wb.write(os);
		 os.close();
    }
    
   
	//����excel
    public  static void createExcel(Workbook wb,Sheet sheet,String filePath, String fileName) 
    {
		int i=0;
	 
		 //�п�
         for(i=0;i<3;i++)
         	sheet.setColumnWidth(i, middleColWidth);
         
         for(i=3;i<stockLabelNum+2*FirstRowFirstColNum;i++)
          	sheet.setColumnWidth(i, middleColWidth2);
             
         
         //������һ��  
         Row rowHead = sheet.createRow(0);  
         //����һ������ı�  
         create07Cell(wb, rowHead, 0, "���", 0);  
         create07Cell(wb, rowHead, 1, "����", 0);  
         create07Cell(wb, rowHead, 2, "����", 0); 	            
         create07Cell(wb, rowHead, 3, "����", 0);    
         sheet.setColumnWidth(4, largeColWidth);
         create07Cell(wb, rowHead, 4, "����Ԥ��", 0); 
       //  create07Cell(wb, rowHead, 5, "����", 0); 
        
         //��һ�кϲ���Ԫ��
         CellRangeAddress craStat=new CellRangeAddress(0, 0, stockLabelNum, stockLabelNum+8-1);     
         //��sheet�����Ӻϲ���Ԫ��  
         sheet.addMergedRegion(craStat);         
         create07Cell(wb, rowHead, stockLabelNum, "��ʷͳ��", 0); 
       
         CellRangeAddress craDay=new CellRangeAddress(0, 0, stockLabelNum+8, stockLabelNum+8+FirstRowFirstColNum-1);        
         sheet.addMergedRegion(craDay);         
         create07Cell(wb, rowHead, stockLabelNum+8, "��", 0); 
         
         CellRangeAddress craWeek=new CellRangeAddress(0, 0, stockLabelNum+8+FirstRowFirstColNum, stockLabelNum+8+2*FirstRowFirstColNum-1);     
         sheet.addMergedRegion(craWeek);         
         create07Cell(wb, rowHead, stockLabelNum+8+FirstRowFirstColNum, "��", 0); 
       
         CellRangeAddress craMon=new CellRangeAddress(0, 0, stockLabelNum+8+2*FirstRowFirstColNum, stockLabelNum+8+3*FirstRowFirstColNum-1);     
         sheet.addMergedRegion(craMon);
         create07Cell(wb, rowHead, stockLabelNum+8+2*FirstRowFirstColNum, "��", 0); 
         
         
         
         CellRangeAddress craCurDayDesc=new CellRangeAddress(0, 0, stockLabelNum+8+3*FirstRowFirstColNum, stockLabelNum+8+3*FirstRowFirstColNum+2-1);     
         sheet.addMergedRegion(craCurDayDesc);//
         create07Cell(wb, rowHead, stockLabelNum+8+3*FirstRowFirstColNum, "��ǰ", 0); 

         CellRangeAddress craDayDesc=new CellRangeAddress(0, 0, stockLabelNum+8+3*FirstRowFirstColNum+2, stockLabelNum+8+3*FirstRowFirstColNum+2+FirstRowSecondColNum-1);     
         sheet.addMergedRegion(craDayDesc);//���
         create07Cell(wb, rowHead, stockLabelNum+8+3*FirstRowFirstColNum+2, "*��*", 0); 
         
         CellRangeAddress craWeekDesc=new CellRangeAddress(0, 0, stockLabelNum+8+3*FirstRowFirstColNum+2+FirstRowSecondColNum, stockLabelNum+8+3*FirstRowFirstColNum+2+2*FirstRowSecondColNum-1);     
         sheet.addMergedRegion(craWeekDesc);
         create07Cell(wb, rowHead, stockLabelNum+8+3*FirstRowFirstColNum+2+FirstRowSecondColNum, "*��*", 0); 
         
         CellRangeAddress craMonDesc=new CellRangeAddress(0, 0, stockLabelNum+8+3*FirstRowFirstColNum+2+2*FirstRowSecondColNum, stockLabelNum+8+3*FirstRowFirstColNum+2+3*FirstRowSecondColNum-1);     
         sheet.addMergedRegion(craMonDesc);
         create07Cell(wb, rowHead, stockLabelNum+8+3*FirstRowFirstColNum+2+2*FirstRowSecondColNum, "*��*", 0); 
        
      
         //�ڶ��кϲ���Ԫ��
         Row rowHead2 = sheet.createRow(1);  
         //��sheet�����Ӻϲ���Ԫ��  
         
         CellRangeAddress craDayStat=new CellRangeAddress(1, 1, stockLabelNum, stockLabelNum+8-1);     
         sheet.addMergedRegion(craDayStat);
         create07Cell(wb, rowHead2, stockLabelNum, "ͳ��", 0);
         
         CellRangeAddress craCurDay=new CellRangeAddress(1, 1, stockLabelNum+8, stockLabelNum+8+FirstRowFirstColNum-1);      
         sheet.addMergedRegion(craCurDay);         
         create07Cell(wb, rowHead2, stockLabelNum+8, "(��)", 0); 
         CellRangeAddress craCurWeek=new CellRangeAddress(1, 1, stockLabelNum+8+FirstRowFirstColNum, stockLabelNum+8+2*FirstRowFirstColNum-1);     
         sheet.addMergedRegion(craCurWeek);
         create07Cell(wb, rowHead2, stockLabelNum+8+FirstRowFirstColNum, "(��)", 0); 
         CellRangeAddress craCurMon=new CellRangeAddress(1, 1, stockLabelNum+8+2*FirstRowFirstColNum, stockLabelNum+8+3*FirstRowFirstColNum-1);     
         sheet.addMergedRegion(craCurMon);
         create07Cell(wb, rowHead2, stockLabelNum+8+2*FirstRowFirstColNum, "(��)", 0); 
        
         CellRangeAddress craCurDayDesc2=new CellRangeAddress(1, 1, stockLabelNum+8+3*FirstRowFirstColNum, stockLabelNum+8+3*FirstRowFirstColNum+2-1);     
         sheet.addMergedRegion(craCurDayDesc2);//
         create07Cell(wb, rowHead2, stockLabelNum+8+3*FirstRowFirstColNum, "(��ǰ)", 0); 
                 
         //2 ��
         CellRangeAddress craDayPointDel=new CellRangeAddress(1, 1, stockLabelNum+8+3*FirstRowFirstColNum+2, stockLabelNum+8+3*FirstRowFirstColNum+2+4-1);     
         sheet.addMergedRegion(craDayPointDel);//���
         create07Cell(wb, rowHead2, stockLabelNum+8+3*FirstRowFirstColNum+2, "��ֵ���", 0); 
         
         CellRangeAddress craDayDesireDel=new CellRangeAddress(1, 1, stockLabelNum+8+3*FirstRowFirstColNum+2+4, stockLabelNum+8+3*FirstRowFirstColNum+2+11-1);     
         sheet.addMergedRegion(craDayDesireDel);
         create07Cell(wb, rowHead2, stockLabelNum+8+3*FirstRowFirstColNum+2+4,"Ԥ�ڲ��", 0); 
         
         CellRangeAddress craDayMarketDel=new CellRangeAddress(1, 1, stockLabelNum+8+3*FirstRowFirstColNum+2+11, stockLabelNum+8+3*FirstRowFirstColNum+2+14-1);     
         sheet.addMergedRegion(craDayMarketDel);
         create07Cell(wb, rowHead2, stockLabelNum+8+3*FirstRowFirstColNum+2+11, "���̶Ա�", 0); 
        
         CellRangeAddress craDayTurnDel=new CellRangeAddress(1, 1, stockLabelNum+8+3*FirstRowFirstColNum+2+14, stockLabelNum+8+3*FirstRowFirstColNum+2+FirstRowSecondColNum-1);     
         sheet.addMergedRegion(craDayTurnDel);
         create07Cell(wb, rowHead2, stockLabelNum+8+3*FirstRowFirstColNum+2+14, "�յ�����", 0); 
        
         //2 ��
         CellRangeAddress craWeekPointDel=new CellRangeAddress(1, 1, stockLabelNum+8+3*FirstRowFirstColNum+2+FirstRowSecondColNum, stockLabelNum+8+3*FirstRowFirstColNum+2+FirstRowSecondColNum+4-1);     
         sheet.addMergedRegion(craWeekPointDel);//���
         create07Cell(wb, rowHead2, stockLabelNum+8+3*FirstRowFirstColNum+2+FirstRowSecondColNum, "��ֵ���", 0); 
         
         CellRangeAddress craWeekDesireDel=new CellRangeAddress(1, 1, stockLabelNum+8+3*FirstRowFirstColNum+2+FirstRowSecondColNum+4, stockLabelNum+8+3*FirstRowFirstColNum+2+FirstRowSecondColNum+11-1);     
         sheet.addMergedRegion(craWeekDesireDel);
         create07Cell(wb, rowHead2, stockLabelNum+8+3*FirstRowFirstColNum+2+FirstRowSecondColNum+4,"Ԥ�ڲ��", 0); 
         
         CellRangeAddress craWeekMarketDel=new CellRangeAddress(1, 1, stockLabelNum+8+3*FirstRowFirstColNum+2+FirstRowSecondColNum+11, stockLabelNum+8+3*FirstRowFirstColNum+2+FirstRowSecondColNum+14-1);     
         sheet.addMergedRegion(craWeekMarketDel);
         create07Cell(wb, rowHead2, stockLabelNum+8+3*FirstRowFirstColNum+2+FirstRowSecondColNum+11, "���̶Ա�", 0); 
        
         CellRangeAddress craWeekTurnDel=new CellRangeAddress(1, 1, stockLabelNum+8+3*FirstRowFirstColNum+2+FirstRowSecondColNum+14, stockLabelNum+8+3*FirstRowFirstColNum+2+2*FirstRowSecondColNum-1);     
         sheet.addMergedRegion(craWeekTurnDel);
         create07Cell(wb, rowHead2, stockLabelNum+8+3*FirstRowFirstColNum+2+FirstRowSecondColNum+14, "�յ�����", 0); 
         
         //3 ��
         CellRangeAddress craMonthPointDel=new CellRangeAddress(1, 1, stockLabelNum+8+3*FirstRowFirstColNum+2+2*FirstRowSecondColNum, stockLabelNum+8+3*FirstRowFirstColNum+2+2*FirstRowSecondColNum+4-1);     
         sheet.addMergedRegion(craMonthPointDel);//���
         create07Cell(wb, rowHead2, stockLabelNum+8+3*FirstRowFirstColNum+2+2*FirstRowSecondColNum, "��ֵ���", 0); 
         
         CellRangeAddress craMonthDesireDel=new CellRangeAddress(1, 1, stockLabelNum+8+3*FirstRowFirstColNum+2+2*FirstRowSecondColNum+4, stockLabelNum+8+3*FirstRowFirstColNum+2+2*FirstRowSecondColNum+11-1);     
         sheet.addMergedRegion(craMonthDesireDel);
         create07Cell(wb, rowHead2, stockLabelNum+8+3*FirstRowFirstColNum+2+2*FirstRowSecondColNum+4,"Ԥ�ڲ��", 0); 
         
         CellRangeAddress craMonthMarketDel=new CellRangeAddress(1, 1, stockLabelNum+8+3*FirstRowFirstColNum+2+2*FirstRowSecondColNum+11, stockLabelNum+8+3*FirstRowFirstColNum+2+2*FirstRowSecondColNum+14-1);     
         sheet.addMergedRegion(craMonthMarketDel);
         create07Cell(wb, rowHead2, stockLabelNum+8+3*FirstRowFirstColNum+2+2*FirstRowSecondColNum+11, "���̶Ա�", 0); 
        
         CellRangeAddress craMonthTurnDel=new CellRangeAddress(1, 1, stockLabelNum+8+3*FirstRowFirstColNum+2+2*FirstRowSecondColNum+14, stockLabelNum+8+3*FirstRowFirstColNum+2+3*FirstRowSecondColNum-1);     
         sheet.addMergedRegion(craMonthTurnDel);
         create07Cell(wb, rowHead2, stockLabelNum+8+3*FirstRowFirstColNum+2+2*FirstRowSecondColNum+14, "�յ�����", 0); 

         
         //�����кϲ���Ԫ��
         Row rowHead3 = sheet.createRow(2);           
         //��sheet�����Ӻϲ���Ԫ��   �ղ������
         CellRangeAddress upStatCur=new CellRangeAddress(2, 2, stockLabelNum, stockLabelNum+4-1);     
         sheet.addMergedRegion(upStatCur);
         create07Cell(wb, rowHead3, stockLabelNum, "��ǰ", 0);
         
         CellRangeAddress upStatDirserd=new CellRangeAddress(2, 2, stockLabelNum+4, stockLabelNum+4+4-1);     
         sheet.addMergedRegion(upStatDirserd);
         create07Cell(wb, rowHead3, stockLabelNum+4, "Ԥ��", 0);
         
         CellRangeAddress upDayCur=new CellRangeAddress(2, 2, stockLabelNum+4+4, stockLabelNum+4+4+5-1);     
         sheet.addMergedRegion(upDayCur);
         create07Cell(wb, rowHead3, stockLabelNum+4+4, "(��)��ǰ", 0);
         
         CellRangeAddress upDayDirserd=new CellRangeAddress(2, 2, stockLabelNum+4+4+5, stockLabelNum+4+4+5+19-1);     
         sheet.addMergedRegion(upDayDirserd);
         create07Cell(wb, rowHead3, stockLabelNum+4+4+5, "(��)Ԥ��", 0);
         
       
         CellRangeAddress downDayOper=new CellRangeAddress(2, 2, stockLabelNum+4+4+5+19, stockLabelNum+8+5+19+3-1);     
         sheet.addMergedRegion(downDayOper);
         create07Cell(wb, rowHead3, stockLabelNum+8+5+19, "(��)����", ConstantsInfo.NoColor);
        
     
      
         //��sheet�����Ӻϲ���Ԫ��   �ܲ������
         CellRangeAddress upWeekCur=new CellRangeAddress(2, 2, stockLabelNum+8+FirstRowFirstColNum, stockLabelNum+8+FirstRowFirstColNum+5-1);     
         sheet.addMergedRegion(upWeekCur);
         create07Cell(wb, rowHead3, stockLabelNum+8+FirstRowFirstColNum, "(��)��ǰ", ConstantsInfo.NoColor);
         
         CellRangeAddress upWeekDirserd=new CellRangeAddress(2, 2, stockLabelNum+8+FirstRowFirstColNum+5, stockLabelNum+8+FirstRowFirstColNum+5+19-1);     
         sheet.addMergedRegion(upWeekDirserd);
         create07Cell(wb, rowHead3, stockLabelNum+8+FirstRowFirstColNum+5, "(��)Ԥ��", ConstantsInfo.NoColor);
         
         CellRangeAddress downWeekOper=new CellRangeAddress(2, 2, stockLabelNum+8+FirstRowFirstColNum+5+19, stockLabelNum+8+FirstRowFirstColNum+5+19+3-1);     
         sheet.addMergedRegion(downWeekOper);
         create07Cell(wb, rowHead3, stockLabelNum+8+FirstRowFirstColNum+5+19, "(��)����", ConstantsInfo.NoColor);
         
     
         //��sheet�����Ӻϲ���Ԫ��   �²������
         CellRangeAddress upMonthCur=new CellRangeAddress(2, 2, stockLabelNum+8+2*FirstRowFirstColNum, stockLabelNum+8+2*FirstRowFirstColNum+5-1);     
         sheet.addMergedRegion(upMonthCur);
         create07Cell(wb, rowHead3, stockLabelNum+8+2*FirstRowFirstColNum, "(��)��ǰ", 0);
         
         CellRangeAddress upMonthDirserd=new CellRangeAddress(2, 2, stockLabelNum+8+2*FirstRowFirstColNum+5, stockLabelNum+8+2*FirstRowFirstColNum+5+19-1);     
         sheet.addMergedRegion(upMonthDirserd);
         create07Cell(wb, rowHead3, stockLabelNum+8+2*FirstRowFirstColNum+5, "(��)Ԥ��", 0);
                
         CellRangeAddress downMonthOper=new CellRangeAddress(2, 2, stockLabelNum+8+2*FirstRowFirstColNum+5+19, stockLabelNum+8+2*FirstRowFirstColNum+5+19+3-1);     
         sheet.addMergedRegion(downMonthOper);
         create07Cell(wb, rowHead3, stockLabelNum+8+2*FirstRowFirstColNum+5+19, "(��)����", 0);
        
         CellRangeAddress downDayCur=new CellRangeAddress(2, 2, stockLabelNum+8+3*FirstRowFirstColNum, stockLabelNum+8+3*FirstRowFirstColNum+2-1);  
         sheet.addMergedRegion(downDayCur);
         create07Cell(wb, rowHead3, stockLabelNum+8+3*FirstRowFirstColNum, "(��ǰ)", 0);
        
       
         CellRangeAddress downDayStart=new CellRangeAddress(2, 2, stockLabelNum+8+3*FirstRowFirstColNum+2, stockLabelNum+8+3*FirstRowFirstColNum+4-1);     
         sheet.addMergedRegion(downDayStart);
         create07Cell(wb, rowHead3, stockLabelNum+8+3*FirstRowFirstColNum+2, "��ʼ", 0);

         CellRangeAddress downDayEnd=new CellRangeAddress(2, 2, stockLabelNum+8+3*FirstRowFirstColNum+4, stockLabelNum+8+3*FirstRowFirstColNum+6-1);     
         sheet.addMergedRegion(downDayEnd);
         create07Cell(wb, rowHead3, stockLabelNum+8+3*FirstRowFirstColNum+4, "����", 0);
      
         CellRangeAddress downDayDel=new CellRangeAddress(2, 2, stockLabelNum+8+3*FirstRowFirstColNum+6, stockLabelNum+8+3*FirstRowFirstColNum+13-1);     
         sheet.addMergedRegion(downDayDel);
         create07Cell(wb, rowHead3, stockLabelNum+8+3*FirstRowFirstColNum+6, "���", 0);
      
         create07Cell(wb, rowHead3, stockLabelNum+8+3*FirstRowFirstColNum+13, "��ʼ", 0); 
         create07Cell(wb, rowHead3, stockLabelNum+8+3*FirstRowFirstColNum+14, "����", 0); 
         create07Cell(wb, rowHead3, stockLabelNum+8+3*FirstRowFirstColNum+15, "�ռ�", 0);  
         create07Cell(wb, rowHead3, stockLabelNum+8+3*FirstRowFirstColNum+2+FirstRowSecondColNum-1, "����", 0); 
         
         CellRangeAddress downWeekStart=new CellRangeAddress(2, 2, stockLabelNum+8+3*FirstRowFirstColNum+2+FirstRowSecondColNum, stockLabelNum+8+3*FirstRowFirstColNum+4+FirstRowSecondColNum-1);     
         sheet.addMergedRegion(downWeekStart);
         create07Cell(wb, rowHead3, stockLabelNum+8+3*FirstRowFirstColNum+2+FirstRowSecondColNum, "��ʼ", 0);

         CellRangeAddress downWeekEnd=new CellRangeAddress(2, 2, stockLabelNum+8+3*FirstRowFirstColNum+4+FirstRowSecondColNum, stockLabelNum+8+3*FirstRowFirstColNum+6+FirstRowSecondColNum-1);     
         sheet.addMergedRegion(downWeekEnd);
         create07Cell(wb, rowHead3, stockLabelNum+8+3*FirstRowFirstColNum+4+FirstRowSecondColNum, "����", 0);
      
         CellRangeAddress downWeekDel=new CellRangeAddress(2, 2, stockLabelNum+8+3*FirstRowFirstColNum+6+FirstRowSecondColNum, stockLabelNum+8+3*FirstRowFirstColNum+13+FirstRowSecondColNum-1);     
         sheet.addMergedRegion(downWeekDel);
         create07Cell(wb, rowHead3, stockLabelNum+8+3*FirstRowFirstColNum+6+FirstRowSecondColNum, "���", 0);
      
         create07Cell(wb, rowHead3, stockLabelNum+8+3*FirstRowFirstColNum+13+FirstRowSecondColNum, "��ʼ", 0); 
         create07Cell(wb, rowHead3, stockLabelNum+8+3*FirstRowFirstColNum+14+FirstRowSecondColNum, "����", 0); 
         create07Cell(wb, rowHead3, stockLabelNum+8+3*FirstRowFirstColNum+15+FirstRowSecondColNum, "�ռ�", 0);  
         create07Cell(wb, rowHead3, stockLabelNum+8+3*FirstRowFirstColNum+2+2*FirstRowSecondColNum-1, "����", 0); 
         
         
         CellRangeAddress downMonthStart=new CellRangeAddress(2, 2, stockLabelNum+8+3*FirstRowFirstColNum+2+2*FirstRowSecondColNum, stockLabelNum+8+3*FirstRowFirstColNum+4+2*FirstRowSecondColNum-1);     
         sheet.addMergedRegion(downMonthStart);
         create07Cell(wb, rowHead3, stockLabelNum+8+3*FirstRowFirstColNum+2+2*FirstRowSecondColNum, "��ʼ", 0);

         CellRangeAddress downMonthEnd=new CellRangeAddress(2, 2, stockLabelNum+8+3*FirstRowFirstColNum+4+2*FirstRowSecondColNum, stockLabelNum+8+3*FirstRowFirstColNum+6+2*FirstRowSecondColNum-1);     
         sheet.addMergedRegion(downMonthEnd);
         create07Cell(wb, rowHead3, stockLabelNum+8+3*FirstRowFirstColNum+4+2*FirstRowSecondColNum, "����", 0);
      
         CellRangeAddress downMonthDel=new CellRangeAddress(2, 2, stockLabelNum+8+3*FirstRowFirstColNum+6+2*FirstRowSecondColNum, stockLabelNum+8+3*FirstRowFirstColNum+13+2*FirstRowSecondColNum-1);     
         sheet.addMergedRegion(downMonthDel);
         create07Cell(wb, rowHead3, stockLabelNum+8+3*FirstRowFirstColNum+6+2*FirstRowSecondColNum, "���", 0);
      
         create07Cell(wb, rowHead3, stockLabelNum+8+3*FirstRowFirstColNum+13+2*FirstRowSecondColNum, "��ʼ", 0); 
         create07Cell(wb, rowHead3, stockLabelNum+8+3*FirstRowFirstColNum+14+2*FirstRowSecondColNum, "����", 0); 
         create07Cell(wb, rowHead3, stockLabelNum+8+3*FirstRowFirstColNum+15+2*FirstRowSecondColNum, "�ռ�", 0);  
         create07Cell(wb, rowHead3, stockLabelNum+8+3*FirstRowFirstColNum+2+3*FirstRowSecondColNum-1, "����", 0); 
         
         
         
         Row rowHead4 = sheet.createRow(3);          
         int col=stockLabelNum;  
         //ͳ��
         create07Cell(wb, rowHead4, col++, "��ǰ", 0); 
         create07Cell(wb, rowHead4, col++, "���", 0); 
        // sheet.setColumnWidth(col, middleColWidth);
         create07Cell(wb, rowHead4, col++, "����", 0); 
        // sheet.setColumnWidth(col, middleColWidth);
         create07Cell(wb, rowHead4, col++, "����", 0);  
         
         create07Cell(wb, rowHead4, col++, "��ǰ", 0); 
         create07Cell(wb, rowHead4, col++, "���", 0); 
        // sheet.setColumnWidth(col, middleColWidth);
         create07Cell(wb, rowHead4, col++, "����", 0); 
        // sheet.setColumnWidth(col, middleColWidth);
         create07Cell(wb, rowHead4, col++, "����", 0);  
         //���ϵ�ǰ 5
         sheet.setColumnWidth(col, middleColWidth);
         create07Cell(wb, rowHead4, col++, "��ʼʱ��", 0); 
         create07Cell(wb, rowHead4, col++, "��ʼ��λ", 0); 
         sheet.setColumnWidth(col, middleColWidth);
         create07Cell(wb, rowHead4, col++, "����ʱ��", 0);
         create07Cell(wb, rowHead4, col++, "������λ", 0); 
         create07Cell(wb, rowHead4, col++, "��ǰ��", 0); 
         //��Ԥ�� 8
         sheet.setColumnWidth(col, middleColWidth);
         create07Cell(wb, rowHead4, col++, "ʱ��", 0);
         
         create07Cell(wb, rowHead4, col++, "Ԥ��0.382", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
        
         create07Cell(wb, rowHead4, col++, "Ԥ��0.5", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
        
         create07Cell(wb, rowHead4, col++, "Ԥ��0.618", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
         
         create07Cell(wb, rowHead4, col++, "Ԥ��0.75", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
         
         create07Cell(wb, rowHead4, col++, "Ԥ��1", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
      
         create07Cell(wb, rowHead4, col++, "Ԥ��1.08", 0);          
         create07Cell(wb, rowHead4, col++, "����", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
        
         create07Cell(wb, rowHead4, col++, "��", 0); 
         create07Cell(wb, rowHead4, col++, "Ӯ", 0); 
         create07Cell(wb, rowHead4, col++, "��", 0); 
      
         
         //��
         sheet.setColumnWidth(col, middleColWidth);
         create07Cell(wb, rowHead4, col++, "��ʼʱ��", 0); 
         create07Cell(wb, rowHead4, col++, "��ʼ��λ", 0); 
         sheet.setColumnWidth(col, middleColWidth);
         create07Cell(wb, rowHead4, col++, "����ʱ��", 0); 
         create07Cell(wb, rowHead4, col++, "������λ", 0); 
         create07Cell(wb, rowHead4, col++, "��", 0); 
         sheet.setColumnWidth(col, middleColWidth);
         create07Cell(wb, rowHead4, col++, "ʱ��", 0);          
        
         create07Cell(wb, rowHead4, col++, "Ԥ��0.382", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
        
         create07Cell(wb, rowHead4, col++, "Ԥ��0.5", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
         
         create07Cell(wb, rowHead4, col++, "Ԥ��0.618", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
         
         create07Cell(wb, rowHead4, col++, "Ԥ��0.75", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
         
         create07Cell(wb, rowHead4, col++, "Ԥ��1", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
        
         create07Cell(wb, rowHead4, col++, "Ԥ��1.08", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0);     
         create07Cell(wb, rowHead4, col++, "����", 0); 
         create07Cell(wb, rowHead4, col++, "��", 0); 
         create07Cell(wb, rowHead4, col++, "Ӯ", 0); 
         create07Cell(wb, rowHead4, col++, "��", 0); 
         
         //��
         sheet.setColumnWidth(col, middleColWidth);       
         create07Cell(wb, rowHead4, col++, "��ʼʱ��", 0); 
         create07Cell(wb, rowHead4, col++, "��ʼ��λ", 0);   
         sheet.setColumnWidth(col, middleColWidth);
         create07Cell(wb, rowHead4, col++, "����ʱ��", 0); 
         create07Cell(wb, rowHead4, col++, "������λ", 0); 
         create07Cell(wb, rowHead4, col++, "��", 0); 
         sheet.setColumnWidth(col, middleColWidth);
         create07Cell(wb, rowHead4, col++, "ʱ��", 0);  

         create07Cell(wb, rowHead4, col++, "Ԥ��0.382", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
     
         create07Cell(wb, rowHead4, col++, "Ԥ��0.5", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
         
         create07Cell(wb, rowHead4, col++, "Ԥ��0.618", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
     
         create07Cell(wb, rowHead4, col++, "Ԥ��0.75", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
  
         create07Cell(wb, rowHead4, col++, "Ԥ��1", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0); 
       
         create07Cell(wb, rowHead4, col++, "Ԥ��1.08", 0); 
         create07Cell(wb, rowHead4, col++, "����", 0);  
         create07Cell(wb, rowHead4, col++, "����", 0); 
         create07Cell(wb, rowHead4, col++, "��", 0); 
         create07Cell(wb, rowHead4, col++, "Ӯ", 0); 
         create07Cell(wb, rowHead4, col++, "��", 0); 
         
         sheet.setColumnWidth(col, middleColWidth);
         create07Cell(wb, rowHead4, col++, "ʱ��", 0); 

         create07Cell(wb, rowHead4, col++, "��λ", 0); 
         ///�ղ�ֵ 
         create07Cell(wb, rowHead4, col++, "ʱ���", 0);          
         create07Cell(wb, rowHead4, col++, "��λ��", 0); 
         create07Cell(wb, rowHead4, col++, "ʱ���", 0); 
         create07Cell(wb, rowHead4, col++, "��λ��", 0); 
         
         create07Cell(wb, rowHead4, col++, "ʱ���", 0); 
         create07Cell(wb, rowHead4, col++, "0.382", 0); 
         create07Cell(wb, rowHead4, col++, "0.5", 0);          
         create07Cell(wb, rowHead4, col++, "0.618", 0); 
         create07Cell(wb, rowHead4, col++, "0.75", 0); 
         create07Cell(wb, rowHead4, col++, "1", 0); 
         create07Cell(wb, rowHead4, col++, "1.08", 0); 
         create07Cell(wb, rowHead4, col++, "ʱ���", 0); 
         create07Cell(wb, rowHead4, col++, "ʱ���", 0); 
         create07Cell(wb, rowHead4, col++, "�ռ�", 0);  
         create07Cell(wb, rowHead4, col++, "����", 0); 
         
         //��
         create07Cell(wb, rowHead4, col++, "ʱ���", 0);          
         create07Cell(wb, rowHead4, col++, "��λ��", 0); 
         create07Cell(wb, rowHead4, col++, "ʱ���", 0); 
         create07Cell(wb, rowHead4, col++, "��λ��", 0); 
         
         create07Cell(wb, rowHead4, col++, "ʱ���", 0); 
         create07Cell(wb, rowHead4, col++, "0.382", 0); 
         create07Cell(wb, rowHead4, col++, "0.5", 0);          
         create07Cell(wb, rowHead4, col++, "0.618", 0); 
         create07Cell(wb, rowHead4, col++, "0.75", 0); 
         create07Cell(wb, rowHead4, col++, "1", 0); 
         create07Cell(wb, rowHead4, col++, "1.08", 0); 
         create07Cell(wb, rowHead4, col++, "ʱ���", 0); 
         create07Cell(wb, rowHead4, col++, "ʱ���", 0); 
         create07Cell(wb, rowHead4, col++, "�ռ�", 0);  
         create07Cell(wb, rowHead4, col++, "����", 0); 
            
         //��
         create07Cell(wb, rowHead4, col++, "ʱ���", 0);          
         create07Cell(wb, rowHead4, col++, "��λ��", 0); 
         create07Cell(wb, rowHead4, col++, "ʱ���", 0); 
         create07Cell(wb, rowHead4, col++, "��λ��", 0); 
         
         create07Cell(wb, rowHead4, col++, "ʱ���", 0); 
         create07Cell(wb, rowHead4, col++, "0.382", 0); 
         create07Cell(wb, rowHead4, col++, "0.5", 0);          
         create07Cell(wb, rowHead4, col++, "0.618", 0); 
         create07Cell(wb, rowHead4, col++, "0.75", 0); 
         create07Cell(wb, rowHead4, col++, "1", 0); 
         create07Cell(wb, rowHead4, col++, "1.08", 0); 
         create07Cell(wb, rowHead4, col++, "ʱ���", 0); 
         create07Cell(wb, rowHead4, col++, "ʱ���", 0); 
         create07Cell(wb, rowHead4, col++, "�ռ�", 0);  
         create07Cell(wb, rowHead4, col++, "����", 0); 
       
         col++;
    }
    
    //���ÿ��ҵ����̱���
    public static void writeExcelItemTitle(Workbook wb,Sheet sheet,String induName,int rowIndex)
    {
    	Row rowIndustry = sheet.createRow(rowIndex);  
    	create07Cell(wb, rowIndustry, 0, induName, ConstantsInfo.NoColor);  
    }
    
    
    //���ͳ��������
    public static void writeExcelStatItem(Workbook wb,Sheet sheet, StockExcelStatItem esItem,int stockRow)
    {
    	//Row rowStock;
 
		Row rowStock =  sheet.getRow(stockRow);
    	int colNum = stockLabelNum;
    	
    	//���
    	String curNumber = esItem.getDayFlag()+"-"+esItem.getWeekFlag()+"-"+esItem.getMonthFlag();  	
    	String desireNumber = "";
    	//time����
    	String curTimes = esItem.getDayPointTimes()+"-"+esItem.getWeekPointTimes()+"-"+esItem.getMonthPointTimes();
    	int disireDayPointTime = esItem.getDayPointTimes()+1;
    	int disireWeekPointTime = esItem.getWeekPointTimes()+1;
    	int disireMonthPointTime = esItem.getMonthPointTimes()+1;
    	String desireTimes = disireDayPointTime+"-"+disireWeekPointTime+"-"+disireMonthPointTime;
    	String curCount =null;
    	String desireCount =null;
    	//���� count
    	if (esItem.getDayFlag() ==1) {//���� 
    		desireNumber = "0-"; //��һ�ε�
    		curCount = esItem.getDayCurUpTimes()+"-";
    		desireCount = esItem.getDayCurDownTimes()+"-";
    	}  else {
    		desireNumber = "1-";
    		curCount = esItem.getDayCurDownTimes()+"-";
    		desireCount = esItem.getDayCurUpTimes()+"-";
    	}
    	if (esItem.getWeekFlag() ==1){//����
    		desireNumber = desireNumber+"0-";
    		curCount = curCount+esItem.getWeekCurUpTimes()+"-";
    		desireCount = desireCount+esItem.getWeekCurDownTimes()+"-";
    	}   else {
    		desireNumber = desireNumber+"1-";
    		curCount = curCount+esItem.getWeekCurDownTimes()+"-";
    		desireCount = desireCount+esItem.getWeekCurUpTimes()+"-";
    	}
    	if (esItem.getMonthFlag() ==1){//����
    		desireNumber = desireNumber+"0";
    		curCount = curCount+esItem.getMonthCurUpTimes();
    		desireCount = desireCount+esItem.getWeekCurDownTimes();
    	} else {
    		desireNumber = desireNumber+"1";
    		curCount = curCount+esItem.getMonthCurDownTimes();
    		desireCount = desireCount+esItem.getWeekCurUpTimes();
    	}
    	//��ǰ
    	colNum++;
    	//���
    	create07Cell(wb, rowStock, colNum++,curNumber, 0); 	
    	create07Cell(wb, rowStock, colNum++, curCount, 0); 
    	create07Cell(wb, rowStock, colNum++, curTimes, 0); 	    	
    	colNum++;
    	create07Cell(wb, rowStock, colNum++, desireNumber, 0); 
    	create07Cell(wb, rowStock, colNum++, desireCount, 0);
    	create07Cell(wb, rowStock, colNum++, desireTimes, 0);    	
    	
    }
    
    //�����Ʊ����value
    public static void writeExcelStockOtherInfo(Workbook wb,Sheet sheet, StockOtherInfoValue soiValue,int stockRow)
    {
    	Row rowStock;
		rowStock = sheet.createRow(stockRow);  
		create07Cell(wb, rowStock, 0, Integer.toString(stockRow), 0);  
        create07Cell(wb, rowStock, 1, soiValue.getFullId(), 0);  
        create07Cell(wb, rowStock, 2, soiValue.getName(), 0); 
        if(soiValue.getEnableTingPai() == 1)
        	create07Cell(wb, rowStock, 3, Integer.toString(soiValue.getMarginTrading())+":ͣ��", 0); 
        else
        	create07Cell(wb, rowStock, 3, Integer.toString(soiValue.getMarginTrading()), 0); 
        
        if (soiValue.getBaseFace()!= null)
        	create07Cell(wb, rowStock, 4, soiValue.getBaseFace().getBaseExpect(), 0); 
    	
    }
    //�����Ʊ����
    public static void writeExcelItem(Workbook wb,Sheet sheet, StockExcelItem eItem,int stockRow,int dataType)
    {
    	
    	//ÿ����Ʊ
    	Row rowStock;
    	
    	if(eItem==null)
    		return; 
    	
    	int colNum=stockLabelNum + stockStatNum ;
    	int colGapNum=stockLabelNum + stockStatNum +3*FirstRowFirstColNum;
    	
    	String valuePer=null;//�ٷֱ�
    	   	
    	rowStock = sheet.getRow(stockRow);
    	switch (dataType)
    	{
    	case ConstantsInfo.DayDataType:  	
	        create07Cell(wb, rowStock, colGapNum, eItem.getScValue().getCurDate(), 0); 
	        create07Cell(wb, rowStock, colGapNum+1, Float.toString(eItem.getScValue().getCurValue()), 0); 
	        colGapNum = colGapNum+2;
	        break;
    	case ConstantsInfo.WeekDataType:
    		colNum = stockLabelNum+stockStatNum+FirstRowFirstColNum;  //��
    		colGapNum = colGapNum +2+ stockGapNum;
    		break;
    	case ConstantsInfo.MonthDataType:	
    		colNum = stockLabelNum+stockStatNum+2*FirstRowFirstColNum;  //��
    		colGapNum = colGapNum +2+ 2*stockGapNum;
    		break;
    	}
       	
    	//��ǰ
    	create07Cell(wb, rowStock, colNum++, eItem.getScValue().getStartDate(), 0);
    	create07Cell(wb, rowStock, colNum++, Float.toString(eItem.getScValue().getStartValue()), 0);
    	create07Cell(wb, rowStock, colNum++, eItem.getScValue().getEndDate(), 0); 	
    	create07Cell(wb, rowStock, colNum++, Float.toString(eItem.getScValue().getEndValue()), 0);
    	valuePer=changePercent(eItem.getScValue().getCurRange());
    	create07Cell(wb, rowStock, colNum++, valuePer, 0); 
    	
    	//Ԥ��
    	create07Cell(wb, rowStock, colNum++, eItem.getSdValue().getDesireDate(), 0);     	
    	create07Cell(wb, rowStock, colNum++, Float.toString(eItem.getSdValue().getDesireValue1()), 0);
    	valuePer=changePercent(eItem.getSdValue().getDesireRange1());
    	create07Cell(wb, rowStock, colNum++, valuePer, 0); 
    	create07Cell(wb, rowStock, colNum++, Float.toString(eItem.getSdValue().getDesireRate1()), 0);
    	create07Cell(wb, rowStock, colNum++, Float.toString(eItem.getSdValue().getDesireValue2()), 0);
    	valuePer=changePercent(eItem.getSdValue().getDesireRange2());
    	create07Cell(wb, rowStock, colNum++, valuePer, 0); 
    	create07Cell(wb, rowStock, colNum++, Float.toString(eItem.getSdValue().getDesireRate2()), 0);
    	create07Cell(wb, rowStock, colNum++, Float.toString(eItem.getSdValue().getDesireValue3()), 0);
    	valuePer=changePercent(eItem.getSdValue().getDesireRange3());
    	create07Cell(wb, rowStock, colNum++, valuePer, 0); 
    	create07Cell(wb, rowStock, colNum++, Float.toString(eItem.getSdValue().getDesireRate3()), 0);
    	create07Cell(wb, rowStock, colNum++, Float.toString(eItem.getSdValue().getDesireValue4()), 0);
    	valuePer=changePercent(eItem.getSdValue().getDesireRange4());
    	create07Cell(wb, rowStock, colNum++, valuePer, 0); 
    	create07Cell(wb, rowStock, colNum++, Float.toString(eItem.getSdValue().getDesireRate4()), 0);
    	create07Cell(wb, rowStock, colNum++, Float.toString(eItem.getSdValue().getDesireValue5()), 0);
    	valuePer=changePercent(eItem.getSdValue().getDesireRange5());
    	create07Cell(wb, rowStock, colNum++, valuePer, 0); 
    	create07Cell(wb, rowStock, colNum++, Float.toString(eItem.getSdValue().getDesireRate5()), 0);
    	create07Cell(wb, rowStock, colNum++, Float.toString(eItem.getSdValue().getDesireValue6()), 0);
    	valuePer=changePercent(eItem.getSdValue().getDesireRange6());
    	create07Cell(wb, rowStock, colNum++, valuePer, 0); 
    	create07Cell(wb, rowStock, colNum++, Float.toString(eItem.getSdValue().getDesireRate6()), 0);
    

    	if (eItem.getFlag() == 0) {//�µ� 	
    		
    		create07Cell(wb, rowStock, colNum++, Float.toString(eItem.getScValue().getBugValue()), 0); 
    		valuePer=changePercent(eItem.getScValue().getWinValue());
	    	create07Cell(wb, rowStock, colNum++, valuePer, 0); 
	    	valuePer=changePercent(eItem.getScValue().getLoseVaule());
	    	create07Cell(wb, rowStock, colNum++, valuePer, 0); 	
    	}
    	
    	//�Ա�
    	colNum = colGapNum;
    	
    	create07Cell(wb, rowStock, colNum++, Integer.toString(eItem.getScValue().getStartDateGap()), 0); 	
    	valuePer=changePercent(eItem.getScValue().getStartValueGap());
    	create07Cell(wb, rowStock, colNum++, valuePer, 0); 	
    	create07Cell(wb, rowStock, colNum++, Integer.toString(eItem.getScValue().getEndDateGap()), 0); 	
    	valuePer=changePercent(eItem.getScValue().getEndValueGap());
    	create07Cell(wb, rowStock, colNum++, valuePer, 0); 

    	create07Cell(wb, rowStock, colNum++, Integer.toString(eItem.getSdValue().getDesireDateGap()), 0); 
    	//create07Cell(wb, rowStock, colNum++, Integer.toString(eItem.getSdValue().getDesireDateGap()), 0); 
    	
    	//Ԥ�ڲ��
    	valuePer=changePercent(eItem.getSdValue().getDesireValue1Gap());
    	create07Cell(wb, rowStock, colNum++, valuePer, 0); 	
    	valuePer=changePercent(eItem.getSdValue().getDesireValue2Gap());
    	create07Cell(wb, rowStock, colNum++, valuePer, 0); 	
    	valuePer=changePercent(eItem.getSdValue().getDesireValue3Gap());
    	create07Cell(wb, rowStock, colNum++, valuePer, 0); 	
    	valuePer=changePercent(eItem.getSdValue().getDesireValue4Gap());
    	create07Cell(wb, rowStock, colNum++, valuePer, 0); 	
    	valuePer=changePercent(eItem.getSdValue().getDesireValue5Gap());
    	create07Cell(wb, rowStock, colNum++, valuePer, 0); 	
    	valuePer=changePercent(eItem.getSdValue().getDesireValue6Gap());
    	create07Cell(wb, rowStock, colNum++, valuePer, 0); 	
    
    	create07Cell(wb, rowStock, colNum++, Integer.toString(eItem.getScValue().getStartMarketDateGap()), 0); 
    	create07Cell(wb, rowStock, colNum++, Integer.toString(eItem.getScValue().getEndMarketDateGap()), 0); 	
    
    	valuePer=changePercent(eItem.getScValue().getMarketSpace());
    	create07Cell(wb, rowStock, colNum++, valuePer, 0); 	    
    	//valuePer=changePercent(eItem.getRatio());
    	create07Cell(wb, rowStock, colNum++, Float.toString(eItem.getScValue().getRatio()), 0); 	
    	    	       	
    }
    
    //���ÿ����Ʊһ������
    public void writeExcelStockItem(Workbook wb,XSSFSheet sheet, StockExcelItem eItem,StockSingle ss,int stockRow,int dataType)
    {
    	
    	//ÿ����Ʊ
    	Row rowStock = null;
    	int colNum=stockLabelNum + stockStatNum ;
    	int colGapNum=stockLabelNum + stockStatNum +3*FirstRowFirstColNum;
 
    	String valuePer=null;//�ٷֱ�
    	switch (dataType)
    	{
    	case ConstantsInfo.DayDataType:
    		rowStock = sheet.createRow(stockRow);  
			create07Cell(wb, rowStock, 0, Integer.toString(stockRow), 0);  
	        create07Cell(wb, rowStock, 1, ss.getStockFullId(), 0);  
	        create07Cell(wb, rowStock, 2, ss.getStockName(), 0); 
	        create07Cell(wb, rowStock, 3, Integer.toString(ss.getEnableMarginTrading()), 0); 
	        create07Cell(wb, rowStock, colGapNum, eItem.getScValue().getCurDate(), 0); 
	        create07Cell(wb, rowStock, colGapNum+1, Float.toString(eItem.getScValue().getCurValue()), 0); 
	        colGapNum = colGapNum+2;
	        break;
    	case ConstantsInfo.WeekDataType:
    		colNum = stockLabelNum+stockStatNum+FirstRowFirstColNum;  //��
    		colGapNum = colGapNum +2+ stockGapNum;		
    		break;
    	case ConstantsInfo.MonthDataType:	
    		colNum = stockLabelNum+stockStatNum+2*FirstRowFirstColNum;  //��
    		colGapNum = colGapNum +2+ 2*stockGapNum;
    		break;
    	}
    	
    	if (eItem == null)
    		return;
    	rowStock = sheet.getRow(stockRow);
    	//��ǰ
    	create07Cell(wb, rowStock, colNum++, eItem.getScValue().getStartDate(), 0);
    	create07Cell(wb, rowStock, colNum++, Float.toString(eItem.getScValue().getStartValue()), 0);
    	create07Cell(wb, rowStock, colNum++, eItem.getScValue().getEndDate(), 0); 	
    	create07Cell(wb, rowStock, colNum++, Float.toString(eItem.getScValue().getEndValue()), 0);
    	valuePer=changePercent(eItem.getScValue().getCurRange());
    	create07Cell(wb, rowStock, colNum++, valuePer, 0); 
    	
    	//Ԥ��
    	create07Cell(wb, rowStock, colNum++, eItem.getSdValue().getDesireDate(), 0);     	
    	create07Cell(wb, rowStock, colNum++, Float.toString(eItem.getSdValue().getDesireValue1()), 0);
    	valuePer=changePercent(eItem.getSdValue().getDesireRange1());
    	create07Cell(wb, rowStock, colNum++, valuePer, 0); 
    	create07Cell(wb, rowStock, colNum++, Float.toString(eItem.getSdValue().getDesireRate1()), 0);
    	create07Cell(wb, rowStock, colNum++, Float.toString(eItem.getSdValue().getDesireValue2()), 0);
    	valuePer=changePercent(eItem.getSdValue().getDesireRange2());
    	create07Cell(wb, rowStock, colNum++, valuePer, 0); 
    	create07Cell(wb, rowStock, colNum++, Float.toString(eItem.getSdValue().getDesireRate2()), 0);
    	create07Cell(wb, rowStock, colNum++, Float.toString(eItem.getSdValue().getDesireValue3()), 0);
    	valuePer=changePercent(eItem.getSdValue().getDesireRange3());
    	create07Cell(wb, rowStock, colNum++, valuePer, 0); 
    	create07Cell(wb, rowStock, colNum++, Float.toString(eItem.getSdValue().getDesireRate3()), 0);
    	create07Cell(wb, rowStock, colNum++, Float.toString(eItem.getSdValue().getDesireValue4()), 0);
    	valuePer=changePercent(eItem.getSdValue().getDesireRange4());
    	create07Cell(wb, rowStock, colNum++, valuePer, 0); 
    	create07Cell(wb, rowStock, colNum++, Float.toString(eItem.getSdValue().getDesireRate4()), 0);
    	create07Cell(wb, rowStock, colNum++, Float.toString(eItem.getSdValue().getDesireValue5()), 0);
    	valuePer=changePercent(eItem.getSdValue().getDesireRange5());
    	create07Cell(wb, rowStock, colNum++, valuePer, 0); 
    	create07Cell(wb, rowStock, colNum++, Float.toString(eItem.getSdValue().getDesireRate5()), 0);
    	create07Cell(wb, rowStock, colNum++, Float.toString(eItem.getSdValue().getDesireValue6()), 0);
    	valuePer=changePercent(eItem.getSdValue().getDesireRange6());
    	create07Cell(wb, rowStock, colNum++, valuePer, 0); 
    	create07Cell(wb, rowStock, colNum++, Float.toString(eItem.getSdValue().getDesireRate6()), 0);
    

    	if (eItem.getFlag() == 0) {//�µ� 	
    		
    		create07Cell(wb, rowStock, colNum++, Float.toString(eItem.getScValue().getBugValue()), 0); 
    		valuePer=changePercent(eItem.getScValue().getWinValue());
	    	create07Cell(wb, rowStock, colNum++, valuePer, 0); 
	    	valuePer=changePercent(eItem.getScValue().getLoseVaule());
	    	create07Cell(wb, rowStock, colNum++, valuePer, 0); 	
    	}
    	
    	//�Ա�
    	colNum = colGapNum;
    	
    	create07Cell(wb, rowStock, colNum++, Integer.toString(eItem.getScValue().getStartDateGap()), 0); 	
    	valuePer=changePercent(eItem.getScValue().getStartValueGap());
    	create07Cell(wb, rowStock, colNum++, valuePer, 0); 	
    	create07Cell(wb, rowStock, colNum++, Integer.toString(eItem.getScValue().getEndDateGap()), 0); 	
    	valuePer=changePercent(eItem.getScValue().getEndValueGap());
    	create07Cell(wb, rowStock, colNum++, valuePer, 0); 

    	create07Cell(wb, rowStock, colNum++, Integer.toString(eItem.getSdValue().getDesireDateGap()), 0); 
    	//create07Cell(wb, rowStock, colNum++, Integer.toString(eItem.getSdValue().getDesireDateGap()), 0); 
    	
    	//Ԥ�ڲ��
    	valuePer=changePercent(eItem.getSdValue().getDesireValue1Gap());
    	create07Cell(wb, rowStock, colNum++, valuePer, 0); 	
    	valuePer=changePercent(eItem.getSdValue().getDesireValue2Gap());
    	create07Cell(wb, rowStock, colNum++, valuePer, 0); 	
    	valuePer=changePercent(eItem.getSdValue().getDesireValue3Gap());
    	create07Cell(wb, rowStock, colNum++, valuePer, 0); 	
    	valuePer=changePercent(eItem.getSdValue().getDesireValue4Gap());
    	create07Cell(wb, rowStock, colNum++, valuePer, 0); 	
    	valuePer=changePercent(eItem.getSdValue().getDesireValue5Gap());
    	create07Cell(wb, rowStock, colNum++, valuePer, 0); 	
    	valuePer=changePercent(eItem.getSdValue().getDesireValue6Gap());
    	create07Cell(wb, rowStock, colNum++, valuePer, 0); 	
    
    	create07Cell(wb, rowStock, colNum++, Integer.toString(eItem.getScValue().getStartMarketDateGap()), 0); 
    	create07Cell(wb, rowStock, colNum++, Integer.toString(eItem.getScValue().getEndMarketDateGap()), 0); 	
    
    	valuePer=changePercent(eItem.getScValue().getMarketSpace());
    	create07Cell(wb, rowStock, colNum++, valuePer, 0);     	
    	//valuePer=changePercent(eItem.getRatio());
    	create07Cell(wb, rowStock, colNum++, Float.toString(eItem.getScValue().getRatio()), 0); 	
    	    	  
    }  
    
   
}
