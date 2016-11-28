package excel.all_v1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import com.point.stock.PointClass;

import common.ConstantsInfo;
import common.stockLogger;

import dao.DbConn;
import dao.StockBaseDao;
import dao.StockConcept;
import dao.StockData;
import dao.StockDataDao;
import dao.StockBaseFace;
import dao.StockIndustry;
import dao.StockInformationDao;
import dao.StockMarket;
import dao.StockPoint;
import dao.StockPointDao;
import dao.StockSingle;
import dao.StockToConcept;
import date.timer.stockDateTimer;

//��Ƭ����excel �ֳɶ��excel

public class StockExcelPartition {

	private StockDataDao sdDao;
	private StockPointDao spDao;
	private StockBaseDao sbDao;

	static PointClass pClass=new PointClass();

	
 	//static ExcelCommon eCommon=new ExcelCommon();
 	static int stockRow = 3;
 	static int stockTotalRow = 3;
 	static int stockDesiredDate = 10;	
 	static int stockMaxRow = 10;	
 	static int sheetCount=0;
 	
 	String SHDate=null; //��¼��֤���̵�ǰʱ�䣬������������Ʊʱ�����Աȣ��ǲ���ͣ������
 	
 	//��¼market ��������Ʊ�Աȼ���
 	StockMaretInfoValue MarketStockTimeInfo[][] = new StockMaretInfoValue[4][3];
  
 	//����������λС����
 	static DecimalFormat decimalFormat=new DecimalFormat(".00");
 	
 	public StockExcelPartition(Connection stockBaseConn,Connection stockDataConn,Connection stockPointConn)
	{
		   this.sbDao = new StockBaseDao(stockBaseConn);
		   this.sdDao =new StockDataDao(stockDataConn);
		   this.spDao =new StockPointDao(stockPointConn);
	}
    
    public StockExcelPartition(StockBaseDao sbDao,StockDataDao sdDao,StockPointDao spDao)
	{
		this.sbDao = sbDao;
		this.sdDao = sdDao;
		this.spDao = spDao;
	}
 	
 	//������λС��
 	public float getDec(float value,int type)
 	{
 		 DecimalFormat df = null;
 		if (type ==2){
 			df = new DecimalFormat("#.##");
 		}else if(type==4){
 			df = new DecimalFormat("#.####");
 		}
 		  
         float f=Float.valueOf(df.format(value));  
         return f; 
 		//float valueDec = (float)(Math.round(value*10000))/10000.0;
 		//return valueDec;
 	}
 	 	
 	//��ȡ��ֵ����
 	public float getGAP(float value1,float value2)
 	{
 		float tmpValue = (float) (value2 - value1)/value1;
		float gap = getDec(tmpValue,4);
		return gap;
 	} 	 
 	                
 	//���� �������ƣ��ϣ���ת��
 	public ExcelItem getExcelItem1(String stockFullId,int dataType) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException
 	{
 		int stockPointTimes=0;
 		ExcelItem eItem;
 		float ratioHistory=0;	//��ʷ�Ƿ���
 		float ratioCur=0;//��ǰ�Ƿ��� 		

    	String nowTime=stockDateTimer.getCurDate();
    	
    	int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_POINT_STOCK);
    	if(isTableExist == 0){//������
			stockLogger.logger.fatal("****stockFullId��"+stockFullId+"�����ڼ�ֵ��****");
			System.out.println(stockFullId+"��ֵ������****");
			return null;  
		}
    	
		//1 �ִ�W
		stockPointTimes = spDao.getStockPointTimes(stockFullId,dataType);
		stockLogger.logger.debug("�ִΣ�"+stockPointTimes);
		
		if(stockPointTimes == 0){
			stockLogger.logger.fatal("****stockFullId��"+stockFullId+"�޼�ֵ������****");
			System.out.println(stockFullId+"�޼�ֵ������****");
			return null;  
		}
		
		//2 ���һ����ֵ��
		StockPoint sp = spDao.getLastPointStock(stockFullId,dataType);
		float lastExtrmePrice = sp.getExtremePrice();
		String crossLastDate=sp.getToDate().toString();
		stockLogger.logger.debug("���һ������㣺"+crossLastDate);
		
		//��ʷ�Ƿ�ֻ����һ�Σ��ڼ���������ʱ
		if (dataType == ConstantsInfo.DayDataType) {
			float minPrice = sdDao.getStockMinORMaxPriceData(stockFullId,0,ConstantsInfo.SeasonDataType);
			if ( minPrice < 0.1) {
				minPrice = sdDao.getStockMinORMaxPriceData(stockFullId,0,ConstantsInfo.DayDataType);
			}
			float maxPrice = sdDao.getStockMinORMaxPriceData(stockFullId,1,ConstantsInfo.SeasonDataType);
			if ( maxPrice < 0.1) {
				maxPrice = sdDao.getStockMinORMaxPriceData(stockFullId,1,ConstantsInfo.DayDataType);
			}
			//��ʷ�Ƿ���,������λ��Ч����
			float ratioTmp1 = (maxPrice - minPrice)/minPrice*100;
		//	System.out.println("maxPrice:"+maxPrice+"minPrice:"+minPrice);
			ratioHistory = (float)(Math.round(ratioTmp1*100))/100;
		}
		
		String extremeDate=null;
		int haveTurn=0; //�Ƿ���ַ�ת
		int tread=0; //�������µ�
		
		float latelyPrice=0;
		StockData sMinData=null;
		StockData sMaxData=null;
		if	(sp.getWillFlag()==1) { //ǰһ������������ǰΪ�½�����
			//3 �����͵�
			sMinData=sdDao.getMinStockDataPoint(stockFullId,crossLastDate,nowTime,dataType);
			if ( sMinData == null){
				stockLogger.logger.fatal("****stockFullId��"+stockFullId+"������͵�****");
				return null; 
			}
				
			extremeDate=sMinData.getDate().toString();	
			latelyPrice = sMinData.getLowestPrice();
			stockLogger.logger.debug("�����͵�ʱ�䣺"+extremeDate+"�����͵��λ��"+sMinData.getLowestPrice());
		} else {
			//3 �����ߵ�
			sMaxData=sdDao.getMaxStockDataPoint(stockFullId,crossLastDate,nowTime,dataType);
			if ( sMaxData == null){
				stockLogger.logger.fatal("****stockFullId��"+stockFullId+"������ߵ�****");
				stockLogger.logger.fatal("****����㣺"+crossLastDate+"��ǰ�յ㣺"+nowTime+"�����ڼ��޽�������****");
				return null; 
			}
			extremeDate=sMaxData.getDate().toString();	
			latelyPrice = sMaxData.getHighestPrice();
			stockLogger.logger.debug("�����ߵ�ʱ�䣺"+extremeDate+"�����ߵ��λ��"+sMaxData.getHighestPrice());
		}
		
		// upTimes downTime����Ϊ1������С�ڵ���������ʱ��
		int upTimes=sdDao.getStockTradeTimes(stockFullId,crossLastDate,extremeDate,dataType);
		int downTimes=sdDao.getStockTradeTimes(stockFullId,extremeDate,nowTime,dataType);
		
		//����Ƿ���
		float ratioTmp2 = (latelyPrice - lastExtrmePrice)/lastExtrmePrice*100;
		ratioCur = (float)(Math.round(ratioTmp2*100))/100;
		
		
		if	(sp.getWillFlag()==1) {
			stockLogger.logger.debug("�£�"+upTimes+"�ϣ�"+downTimes);
			tread = 0; //���Ʒ�ת
			if (upTimes > 1)
				haveTurn = 1;
			else
				upTimes =0 ;//�޷�ת������ת������Ϊ0
			eItem=new ExcelItem(stockFullId,ratioHistory,downTimes,upTimes,haveTurn,stockPointTimes,ratioCur,latelyPrice,extremeDate,crossLastDate,tread);
		} else {
			tread = 1;
			stockLogger.logger.debug("�ϣ�"+upTimes+"�£�"+downTimes);
			if (downTimes >1)
				haveTurn = 1;
			else
				downTimes = 0; //�޷�ת������ת������Ϊ0
			eItem=new ExcelItem(stockFullId,ratioHistory,downTimes,upTimes,haveTurn,stockPointTimes,ratioCur,latelyPrice,extremeDate,crossLastDate,tread);
		}
			
		//�������
		
		return eItem;
 	}
 	
 	
 	//����date���� �������ƣ��ϣ���ת��
 	public StockExcelItem getExcelItem(String stockFullId,int dataType,int stockType) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
 	{
 	
 		StockExcelItem eItem=null;
 		String curExtremeDate=null;//�����ֵʱ��
 		String lastExtremeDate =null; //ǰһ������ʱ��
 		//�ִ�
 		int stockPointTimes=0;
 		//����
 		String nowTime=stockDateTimer.getCurDate(); 

 		String curStartDate = null,curEndDate = null,desireDate = null;
 		float curRange,curStartValue = 0,curEndValue=0;//��ʼ��������λ
 		float desireValue1 = 0,desireValue2,desireValue3,desireValue4,desireValue5,desireValue6=0;
 		float desireValue1Gap,desireValue2Gap,desireValue3Gap,desireValue4Gap,desireValue5Gap,desireValue6Gap=0;
 		float desireRange1,desireRange2,desireRange3,desireRange4,desireRange5,desireRange6=0;
 		float desireRate1,desireRate2,desireRate3,desireRate4,desireRate5,desireRate6=0;
 		float bugValue = 0,winValue = 0,loseValue=0; 		
 		float tmpValue=0;
 		float rate=0;
 		int startDateGAP,endDateGAP,desireDateGap=0;
 		//����̶Ա�
 		int toMarketStartDateGAP,toMarketEndDateGAP;
 		float toMaretRangeGAP=0;
 		int marketIndex=0;
 		StockStatValue ssValue;//ͳ��
 		StockCurValue scValue;//��ǰ
 		StockDesireValue sdValue;//Ԥ��
 		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 
 		
    	int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_DATA_STOCK);
    	if(isTableExist == 0){//������
			stockLogger.logger.fatal("****stockFullId��"+stockFullId+"�����ڼ�ֵ��****");
			System.out.println(stockFullId+"��ֵ������****");
			return null;  
		}
    	
    	isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_POINT_STOCK);
    	if(isTableExist == 0){//������
			stockLogger.logger.fatal("****stockFullId��"+stockFullId+"�����ڽ��ױ�****");
			System.out.println(stockFullId+"���ױ�����****");
			return null;  
		}    	
       	
		//1 ���һ����ֵ��
		StockPoint sp = spDao.getLastPointStock(stockFullId,dataType);
		if(sp == null) {
			stockLogger.logger.fatal("stockFullId��"+stockFullId+"�����������");
			System.out.println(stockFullId+"�����������****");
			return null;  
		}
		
		float lastExtrmePrice = sp.getExtremePrice();
		lastExtremeDate = sdf.format(sp.getExtremeDate());
		String crossLastDate=sp.getToDate().toString();
	//	stockLogger.logger.debug("��ֵʱ�䣺"+extremeDate);
	//	stockLogger.logger.debug("���һ������㣺"+crossLastDate);		
		
		//1 �ִ�W
		stockPointTimes = spDao.getStockPointTimes(stockFullId,dataType);
		if(stockPointTimes == 0){
			stockLogger.logger.fatal("stockFullId��"+stockFullId+"�޼�ֵ������");
			System.out.println(stockFullId+"�޼�ֵ������****");
			return null;
		}
		
		//ȡ������������Ϊ���㣬�����ܣ�������
		StockData sdata = sdDao.getLastDataStock(stockFullId,ConstantsInfo.DayDataType);
		String curDate= sdata.getDate().toString();
		float curPrice=0;
	
		StockData sMinData=null;
		StockData sMaxData=null;
		if (sp.getWillFlag()==1) { //ǰһ������������ǰΪ�½�����
			
			//�µĽ���� ���Ʒ�ת
		//	if (crossLastDate.equals(nowTime)){
				
		//	}else {			
				//3 �����͵�
				//sMinData=sdDao.getMinStockDataPoint(stockFullId,crossLastDate,nowTime,ConstantsInfo.DayDataType);
				sMinData=sdDao.getMinStockDataPoint(stockFullId,lastExtremeDate,nowTime,ConstantsInfo.DayDataType);
				if ( sMinData == null){
					stockLogger.logger.fatal("****stockFullId��"+stockFullId+"������͵�****");
					return null; 
				}
				//float minPrice = sMinData.getLowestPrice();
				//��ʼʱ��
				curStartDate = 	lastExtremeDate;
				//����ʱ��
				curEndDate = sMinData.getDate().toString();	
				curExtremeDate = curEndDate;//�������ʱ��
				//��ʼ��λ
				curStartValue = lastExtrmePrice;//sdDao.getStockLowestPrice(stockFullId,curDownStartDate,dataType);
				//������λ
				curEndValue = sMinData.getLowestPrice();
				//��ǰ��
				//curDownRange = sdDao.getStockCurHigestRange(stockFullId,curDownEndDate,dataType,0);		
				//Ԥ��ʱ��
				desireDate = stockDateTimer.getAddDate(sMinData.getDate(),stockDesiredDate);
				//��ǰ���̼۸�
				curPrice = sdata.getClosingPrice();
		//	}			
		} else {
			
			//�µĽ���� ���Ʒ�ת
		//	if (crossLastDate.equals(nowTime)){
				
		//	}else {	
				//3 �����ߵ�
				sMaxData=sdDao.getMaxStockDataPoint(stockFullId,lastExtremeDate,nowTime,ConstantsInfo.DayDataType);
				if ( sMaxData == null){
					stockLogger.logger.fatal("****stockFullId��"+stockFullId+"������ߵ�****");
					stockLogger.logger.fatal("****����㣺"+crossLastDate+"��ǰ�յ㣺"+nowTime+"�����ڼ��޽�������****");
					return null; 
				}
				//��ʼʱ��
				curStartDate = lastExtremeDate;
				//��ʼ��λ
				curStartValue = lastExtrmePrice;//sdDao.getStockHighestPrice(stockFullId,curUpStartDate,dataType);//��ʼ��λ
				//����ʱ��
				curEndDate = sMaxData.getDate().toString();	
				//������λ
				curEndValue = sMaxData.getHighestPrice();
				//�������ʱ��
				curExtremeDate = curEndDate;
				
				desireDate = stockDateTimer.getAddDate(sMaxData.getDate(),stockDesiredDate);
				//��
				//curUpRange = sdDao.getStockCurHigestRange(stockFullId,curUpEndDate,dataType,1);
				
				
				//��ǰ��߼۸�
				curPrice = sdata.getClosingPrice();
				//stockLogger.logger.debug("up��ʼʱ�䣺"+curUpStartDate+",up����ʱ�䣺"+curUpEndDate);
				//stockLogger.logger.debug("up����"+curUpRange);
				//stockLogger.logger.debug("upԤ��ʱ�䣺"+desireUpDate+",upԤ��ֵ��"+desireUpValue);
		//	}
		}
		
		//��ǰ����	
		curRange=getGAP(curStartValue,curEndValue);
		
		//��ʼʱ��� ��λ��
		startDateGAP = stockDateTimer.daysBetween(curStartDate,curDate,dataType);
		float startValueGAP = getGAP(curStartValue,curPrice);
		/*
		System.out.println("curStartDate:"+curStartDate);
		System.out.println("curStartValue:"+curStartValue);
		System.out.println("curPrice:"+curPrice);
		System.out.println("startValueGAP:"+startValueGAP);
		*/
		//����ʱ��� ��λ��
		endDateGAP = stockDateTimer.daysBetween(curDate,curEndDate,dataType);
		float endValueGAP =getGAP(curEndValue,curPrice);
		/*
		System.out.println("curDate:"+curDate);
		System.out.println("curEndValue:"+curEndValue);
		System.out.println("curPrice:"+curPrice);
		System.out.println("endValueGAP:"+endValueGAP);
		*/
		
		//����
		if(endDateGAP ==0) {
			rate=0;
		}else {
			tmpValue= endValueGAP*100/endDateGAP;			
			rate= getDec(tmpValue,2);
		}
		
		
		//Ԥ��ʱ���
		desireDateGap = stockDateTimer.daysBetween(curDate,desireDate,dataType);
		
		//0.382Ԥ�ڵ�λ onstantsInfo.STOCK_DESIRE1 
		tmpValue = (float) (curEndValue - 0.382 * (curEndValue - curStartValue));
		desireValue1= getDec(tmpValue,2);
		//Ԥ�ڷ���
		tmpValue = desireValue1/curEndValue -1;
		desireRange1 = getDec(tmpValue,4);		
		//����
		tmpValue= desireRange1*100/stockDesiredDate;			
		desireRate1= getDec(tmpValue,2);		
		//Ԥ�ڲ�
		tmpValue = (float)(curPrice - desireValue1)/desireValue1;
		desireValue1Gap= getDec(tmpValue,4);
		
		//0.5Ԥ�ڵ�λ
		tmpValue = (float) (curEndValue - 0.5 * (curEndValue - curStartValue));
		desireValue2 =  getDec(tmpValue,2);
		//Ԥ�ڷ���
		tmpValue = desireValue2/curEndValue -1;
		desireRange2 =  getDec(tmpValue,4);
		//����
		tmpValue= desireRange2*100/stockDesiredDate;			
		desireRate2= getDec(tmpValue,2);	
		//Ԥ�ڲ�
		tmpValue = (float)(curPrice - desireValue2)/desireValue2;
		desireValue2Gap =  getDec(tmpValue,4);
		
		//0.618Ԥ�ڵ�λ
		tmpValue = (float) (curEndValue - 0.618 * (curEndValue - curStartValue));
		desireValue3 =  getDec(tmpValue,2);	
		//Ԥ�ڷ���
		tmpValue = desireValue3/curEndValue -1;
		desireRange3 = getDec(tmpValue,4);
		//����
		tmpValue= desireRange3*100/stockDesiredDate;			
		desireRate3= getDec(tmpValue,2);	
		//Ԥ�ڲ�
		tmpValue = (float)(curPrice - desireValue3)/desireValue3;
		desireValue3Gap =  getDec(tmpValue,4);
		
		//0.75Ԥ�ڵ�λ
		tmpValue = (float) (curEndValue - 0.75 * (curEndValue - curStartValue));
		desireValue4 =  getDec(tmpValue,2);
		//Ԥ�ڷ���
		tmpValue = desireValue4/curEndValue -1;
		desireRange4 = getDec(tmpValue,4);
		//����
		tmpValue= desireRange4*100/stockDesiredDate;			
		desireRate4= getDec(tmpValue,2);
		//Ԥ�ڲ�
		tmpValue = (float)(curPrice - desireValue4)/desireValue4;
		desireValue4Gap =  getDec(tmpValue,4);
		
		//1 Ԥ�ڵ�λ
		tmpValue = (float) (curEndValue - 1 * (curEndValue - curStartValue));
		desireValue5 = getDec(tmpValue,2);
		//Ԥ�ڷ���
		tmpValue = desireValue5/curEndValue -1;
		desireRange5 = getDec(tmpValue,4);
		//����
		tmpValue= desireRange5*100/stockDesiredDate;			
		desireRate5= getDec(tmpValue,2);	
		//Ԥ�ڲ�
		tmpValue = (float)(curPrice - desireValue5)/desireValue5;
		desireValue5Gap =  getDec(tmpValue,4);
		
		//1.08Ԥ�ڵ�λ
		tmpValue = (float) (curEndValue - 1.08 * (curEndValue - curStartValue));
		desireValue6 = getDec(tmpValue,2);
		//Ԥ�ڷ���
		tmpValue = desireValue6/curEndValue -1;
		desireRange6 = getDec(tmpValue,4);
		//����
		tmpValue= desireRange6*100/stockDesiredDate;			
		desireRate6= getDec(tmpValue,2);
		//Ԥ�ڲ�
		tmpValue = (float)(curPrice - desireValue6)/desireValue6;
		desireValue6Gap = getDec(tmpValue,4);
		
		sdValue=new StockDesireValue(desireDate,desireDateGap,desireValue1,desireRange1,desireRate1,desireValue1Gap,desireValue2,desireRange2,desireRate2,desireValue2Gap,desireValue3,desireRange3,desireRate3,desireValue3Gap,desireValue4,desireRange4,desireRate4,desireValue4Gap,desireValue5,desireRange5,desireRate5,desireValue5Gap,desireValue6,desireRange6,desireRate6,desireValue6Gap);
		 		
		if (sp.getWillFlag()==1) { //ǰһ������������ǰΪ�½�����
			//��
			tmpValue = (float) (1 + curRange/100)* curEndValue;
			bugValue = getDec(tmpValue,2);			
			
			//Ӯ 0.618
			tmpValue = (float) (desireValue3/bugValue-1);
			winValue = getDec(tmpValue,4);	
			
			//��
			tmpValue = (float) (curEndValue/bugValue -1);	
			loseValue = getDec(tmpValue,4);	
		}
		
		// upTimes downTime����Ϊ1������С�ڵ���������ʱ��
		int upTimes=sdDao.getStockTradeTimes(stockFullId,crossLastDate,curExtremeDate,dataType);
		int downTimes=sdDao.getStockTradeTimes(stockFullId,curExtremeDate,nowTime,dataType);
			
		//����ָ��
		if (stockType == ConstantsInfo.DPMarket){
			StockMaretInfoValue smiValue=new StockMaretInfoValue(stockFullId,curStartDate,curEndDate,curRange);
			marketIndex = sbDao.getMarketNum(stockFullId);
			//��������ʱ��ֵ			
			MarketStockTimeInfo[marketIndex][dataType-1]=smiValue;
			scValue=new StockCurValue(curStartDate,curStartValue,curEndDate,curEndValue,curRange,bugValue,winValue,loseValue,
					curDate,curPrice,startDateGAP,startValueGAP,endDateGAP,endValueGAP,
					0,0,0,rate);
		}else {
			marketIndex = stockType-2;
		//	System.out.println("curStartDate:"+curStartDate);			
			toMarketStartDateGAP = stockDateTimer.daysBetween(curStartDate,MarketStockTimeInfo[marketIndex][dataType-1].getStartDate(),dataType);
			toMarketEndDateGAP = stockDateTimer.daysBetween(curEndDate,MarketStockTimeInfo[marketIndex][dataType-1].getEndDate(),dataType);
			toMaretRangeGAP = curRange-MarketStockTimeInfo[marketIndex][dataType-1].getCurRange();
			
			scValue=new StockCurValue(curStartDate,curStartValue,curEndDate,curEndValue,curRange, bugValue,winValue,loseValue,
					curDate,curPrice,startDateGAP,startValueGAP,endDateGAP,endValueGAP,
					toMarketStartDateGAP,toMarketEndDateGAP,toMaretRangeGAP,rate);
		}		
		
		if(sp.getWillFlag()==1) {	 //��ǰ����	��һ�����µ�	
			ssValue=new StockStatValue(0,0,sp.getWillFlag(),stockPointTimes,upTimes,downTimes);
		} else {	
			ssValue=new StockStatValue(0,1,sp.getWillFlag(),stockPointTimes,upTimes,downTimes);
		}
		
		eItem=new StockExcelItem(stockFullId,ssValue,scValue,sdValue);
	
		return eItem;
 	}
 	
 	//�Ƿ����ͣ������
 	int getEnableTingPai(String stockFullId) throws IOException, ClassNotFoundException, SQLException
 	{
 		//ȡ����ȡ����ʱ��Աȣ��Ƿ���ͣ��
 		StockData sdata = sdDao.getLastDataStock(stockFullId,ConstantsInfo.DayDataType);
 		if(sdata == null){
 			System.out.println("last data null");
 			return 0;
 		}
 			
 		if(SHDate.equals(sdata.getDate().toString()))
 			return 0;
 		else
 			return 1;//ͣ��
 	}
 
 	
 	//��������
 	public void writeExcelFromMarket(Workbook wb,Sheet sheet,String filePath, String fileName) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
 		
		 List<StockMarket> listStockMarket = new ArrayList<StockMarket>(); 
		 listStockMarket=sbDao.getStockMarket(ConstantsInfo.StockMarket);
		 
		//�ȷ�������
	    //����ָ�� һ�� 
		stockRow++;
		ExcelCommon.writeExcelItemTitle(wb,sheet,"����ָ��",stockRow);
		int stockType=0;
	   	for(Iterator itMarket = listStockMarket.iterator();itMarket.hasNext();)
	   	{
	   		StockMarket sMarket = (StockMarket)itMarket.next();	
	   		stockRow++;
	   		System.out.println(sMarket.getCode().toString());
	   		stockType=sbDao.getMarketType(sMarket.getCode().toString());
	  
	   		StockData sdata = sdDao.getLastDataStock("SH000001",ConstantsInfo.DayDataType);
	   		SHDate = sdata.getDate().toString();
	   		//if(!sMarket.getCode().toString().equals("sh000001"))
	   		//	continue;
	   		
	   		//����ֵ
	   		StockOtherInfoValue soiValue=new StockOtherInfoValue(sMarket.getCode().toString(),sMarket.getName().toString(),0,0,null);
	   		ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow);
	   		//������
	   	//	stockLogger.logger.debug("*****������*****");
	   		StockExcelItem dayItem = getExcelItem(sMarket.getCode().toString(),ConstantsInfo.DayDataType,stockType); 
			ExcelCommon.writeExcelItem(wb,sheet,dayItem, stockRow, ConstantsInfo.DayDataType);
			if (dayItem == null)
				continue;
			//������Ԥ��ֵ
	//		stockLogger.logger.debug("*****������*****");
			StockExcelItem weekItem = getExcelItem(sMarket.getCode().toString(),ConstantsInfo.WeekDataType,stockType);		
			ExcelCommon.writeExcelItem(wb,sheet,weekItem, stockRow, ConstantsInfo.WeekDataType);
			if (weekItem == null)
				continue;			
			//������Ԥ��ֵ
	//		stockLogger.logger.debug("*****������*****");
			StockExcelItem monthItem = getExcelItem(sMarket.getCode().toString(),ConstantsInfo.MonthDataType,stockType);		
			ExcelCommon.writeExcelItem(wb,sheet,monthItem, stockRow, ConstantsInfo.MonthDataType);
			if (monthItem == null)
				continue;
			//ͳ��
			StockExcelStatItem  statItem = new StockExcelStatItem(0,0,0,dayItem.getSsValue().getFlag(),weekItem.getSsValue().getFlag(),monthItem.getSsValue().getFlag(),
					dayItem.getSsValue().getPointTimes(),weekItem.getSsValue().getPointTimes(),monthItem.getSsValue().getPointTimes(),dayItem.getSsValue().getCurUpTimes(),
					weekItem.getSsValue().getCurUpTimes(),monthItem.getSsValue().getCurUpTimes(),dayItem.getSsValue().getCurDownTimes(),weekItem.getSsValue().getCurDownTimes(),monthItem.getSsValue().getCurDownTimes());
			ExcelCommon.writeExcelStatItem(wb,sheet,statItem, stockRow);
	   	}

	}
 	
 	//����ҵ����excel ���ܵ�ǰ��������
	public void writeExcelFormIndustryOrderBy(String filePath, String fileTime) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
		     
	 	List<StockIndustry> listIndustry = new ArrayList<StockIndustry>(); 
		 //�õ���ǰ������ҵ
    	listIndustry=sbDao.getStockIndustry();
    	System.out.println("��ҵ������"+listIndustry.size());
    	XSSFWorkbook wb=null;
    	XSSFSheet sheet =null;
    	String excleFileName=null;
        //ÿ����ҵ�����excelһ��
        for (int i=0;i<listIndustry.size();i++)	
   		{
        	
   			if(i==0 || stockRow >= 510) {
   				
   				//��������Ŀ¼
   				File file = new File(filePath+fileTime);  
   				System.out.println(fileTime);
   				if (!file.exists())
   				{   
   					 file.mkdir();   
   				} 
   				
	   			// ������		
	   			wb = new XSSFWorkbook(); 
	   			// ������һ��sheet     
	   			sheet=  wb.createSheet("allstock");		
	   			sheetCount++;
	   			excleFileName="Stock_Industry_"+fileTime+"_All_"+sheetCount+".xlsx";	   		    
	   		    stockRow = 3;
	   			//����excel
	   	 		ExcelCommon.createExcel(wb,sheet,filePath,excleFileName);
	   			writeExcelFromMarket(wb,sheet,filePath,excleFileName);
	   			FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);
	   			wb.write(fileOStream);		
	   	        fileOStream.close(); 
	   	        wb=null;
	   	        sheet=null;
	   	        
	   	    //    if(sheetCount ==2)
	   	     //   	break;
   			}
   			
   			excleFileName="Stock_Industry_"+fileTime+"_All_"+sheetCount+".xlsx";
 			
   			File file = new File(filePath +fileTime+ "\\"+excleFileName);
			// ������   
			FileInputStream fileIStream = new FileInputStream(file);  
			
			 wb = new XSSFWorkbook(fileIStream);   
			 sheet = wb.getSheetAt(0);  
			// System.out.println("���һ�У�"+sheet.getLastRowNum());   
			
			StockExcelStatItem  statItem;   		
			
			//��ǰ��ҵ
			StockIndustry indu = listIndustry.get(i);	
			String induCode = indu.getThirdcode();
			String induName = indu.getThirdname();
			if(induCode == null || induName == null)
				continue;				
			stockLogger.logger.fatal("��ҵ��"+induName);   		
			System.out.println("��ҵ��"+induName);			
			//��ҵ���� 
			stockRow++;
			ExcelCommon.writeExcelItemTitle(wb,sheet,i+":"+induName,stockRow);
   					 	
   			//������ҵ���й�Ʊ
   			List<String> listIndustryStock = new ArrayList<String>();   
   			//listIndustryStock=sbDao.getIndustryStock(induName);	   	
   			listIndustryStock=sbDao.getIndustryStockFromAllInfo(induCode);
   			stockLogger.logger.debug("��ҵ��Ʊ����"+listIndustryStock.size());
   			int stockType=0;
   			
   			//�����Ƿ�������
   			List<StockExcelTotalInfo> listStockTotalInfoOrderBy = new ArrayList<StockExcelTotalInfo>(); 
   			for(Iterator ie=listIndustryStock.iterator();ie.hasNext();)
   			{
   				//stockRow++;
   			
   				String stockFullId = (String) ie.next();	
   				System.out.println("stockFullId:"+stockFullId);
   				stockType=sbDao.getMarketType(stockFullId);
   				//if(!stockFullId.equals("SH601268"))
   				//	continue;
   				stockLogger.logger.fatal("****stockFullId��"+stockFullId+"****");
   				
   				StockSingle ss= sbDao.lookUpStockSingle(stockFullId);
   				StockExcelItem dayItem;
				StockExcelItem weekItem;
				StockExcelItem monthItem;				
				
				int enableTingPai = getEnableTingPai(stockFullId);
				//������
				StockBaseFace baseFace = sbDao.lookUpStockBaseFace(stockFullId);
				//����ֵ
		   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,ss.getStockName(),ss.getEnableMarginTrading(),enableTingPai,baseFace);
		   		//ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow);
				
   				//������Ԥ��ֵ
   				dayItem = getExcelItem(stockFullId,ConstantsInfo.DayDataType,stockType);   
   				if (dayItem == null){
   					stockLogger.logger.fatal("day point is null");
   					//continue;
   				} else {
   				//	ExcelCommon.writeExcelItem(wb,sheet,dayItem, stockRow, ConstantsInfo.DayDataType);
   				}
   				//������Ԥ��ֵ
   				weekItem = getExcelItem(stockFullId,ConstantsInfo.WeekDataType,stockType);
   				if (weekItem == null){
   					stockLogger.logger.fatal("week point is null");
   					//continue; 
   				} else {
   				//	ExcelCommon.writeExcelItem(wb,sheet,weekItem, stockRow, ConstantsInfo.WeekDataType);
   				}
   				//������Ԥ��ֵ
   				monthItem = getExcelItem(stockFullId,ConstantsInfo.MonthDataType,stockType);
   				if (monthItem == null){
   					stockLogger.logger.fatal("month point is null");
   					//continue; 
   				} else {
   				//	ExcelCommon.writeExcelItem(wb,sheet,monthItem,stockRow, ConstantsInfo.MonthDataType);
   				}
   				
   				//ͳ��
   				if (dayItem == null){
   					statItem = new StockExcelStatItem(0,0,0,1,1,1,
   	   						0,0,0,0,
   	   						0,0,0,0,0);   					
   				}else if (weekItem == null){
   					statItem = new StockExcelStatItem(0,0,0,dayItem.getSsValue().getFlag(),1,1,
   	   						dayItem.getSsValue().getPointTimes(),0,0,dayItem.getSsValue().getCurUpTimes(),
   	   						0,0,dayItem.getSsValue().getCurDownTimes(),0,0);
   					
   				} else if (monthItem == null) {
   					statItem = new StockExcelStatItem(0,0,0,dayItem.getSsValue().getFlag(),weekItem.getSsValue().getFlag(),1,
	   						dayItem.getSsValue().getPointTimes(),weekItem.getPointTimes(),0,dayItem.getSsValue().getCurUpTimes(),
	   						weekItem.getSsValue().getCurUpTimes(),0,dayItem.getSsValue().getCurDownTimes(),weekItem.getSsValue().getCurDownTimes(),0);
   				} else {	   			
	   				statItem = new StockExcelStatItem(0,0,0,dayItem.getSsValue().getFlag(),weekItem.getSsValue().getFlag(),monthItem.getSsValue().getFlag(),
	   						dayItem.getSsValue().getPointTimes(),weekItem.getSsValue().getPointTimes(),monthItem.getSsValue().getPointTimes(),dayItem.getSsValue().getCurUpTimes(),
	   						weekItem.getSsValue().getCurUpTimes(),monthItem.getSsValue().getCurUpTimes(),dayItem.getSsValue().getCurDownTimes(),weekItem.getSsValue().getCurDownTimes(),monthItem.getSsValue().getCurDownTimes());
   				}
   				
   			//	ExcelCommon.writeExcelStatItem(wb,sheet,statItem, stockRow);
   			
   				StockExcelTotalInfo setInfo =new StockExcelTotalInfo(soiValue,dayItem,weekItem,monthItem,statItem);
   				listStockTotalInfoOrderBy.add(setInfo);
   			}
   			
   			System.out.println(listStockTotalInfoOrderBy.size());
   		
   			//����
   			Collections.sort(listStockTotalInfoOrderBy); 
   		           
   			for (int j=0;j<listStockTotalInfoOrderBy.size();j++)	
   			{
   				StockExcelTotalInfo setInfo = (StockExcelTotalInfo) listStockTotalInfoOrderBy.get(j);
   				stockRow++;
   				ExcelCommon.writeExcelStockOtherInfo(wb, sheet, setInfo.getSoiValue(), stockRow);
   				if (setInfo.getDayItem() != null)
   					ExcelCommon.writeExcelItem(wb,sheet,setInfo.getDayItem(), stockRow, ConstantsInfo.DayDataType);
   				if (setInfo.getWeekItem() != null)
   					ExcelCommon.writeExcelItem(wb,sheet,setInfo.getWeekItem(), stockRow, ConstantsInfo.WeekDataType);
   				if (setInfo.getMonthItem() != null)
   					ExcelCommon.writeExcelItem(wb,sheet,setInfo.getMonthItem(),stockRow, ConstantsInfo.MonthDataType);
   				ExcelCommon.writeExcelStatItem(wb,sheet,setInfo.getStatItem(), stockRow);
   			}
   			
   			listStockTotalInfoOrderBy =null;
   			
			FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);
			wb.write(fileOStream);
			fileOStream.flush();
			fileIStream.close();
			fileOStream.close();              
			  
			listIndustryStock = null;
          //��������
			//if(stockRow>10)
			//	break;
   		}
   		
   		listIndustry = null;
	}
	
 
	//����ҵ����excel 
	public void writeExcelFormIndustry(String filePath, String fileTime) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
		     
	 	List<StockIndustry> listIndustry = new ArrayList<StockIndustry>(); 
		 //�õ���ǰ������ҵ
    	listIndustry=sbDao.getStockIndustry();
    	System.out.println("��ҵ������"+listIndustry.size());
    	XSSFWorkbook wb=null;
    	XSSFSheet sheet =null;
    	String excleFileName=null;
        //ÿ����ҵ�����excelһ��
        for (int i=0;i<listIndustry.size();i++)	
   		{
        	
   			if(i==0 || stockRow >= 510) {
   				
   				//��������Ŀ¼
   				File file = new File(filePath+fileTime);  
   				System.out.println(fileTime);
   				if (!file.exists())
   				{   
   					 file.mkdir();   
   				} 
   				
	   			// ������		
	   			wb = new XSSFWorkbook(); 
	   			// ������һ��sheet     
	   			sheet=  wb.createSheet("allstock");		
	   			sheetCount++;
	   			excleFileName="Stock_Industry_"+fileTime+"_All_"+sheetCount+".xlsx";	   		    
	   		    stockRow = 3;
	   			//����excel
	   	 		ExcelCommon.createExcel(wb,sheet,filePath,excleFileName);
	   			writeExcelFromMarket(wb,sheet,filePath,excleFileName);
	   			FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);
	   			wb.write(fileOStream);		
	   	        fileOStream.close(); 
	   	        wb=null;
	   	        sheet=null;
	   	        
	   	    //    if(sheetCount ==2)
	   	     //   	break;
   			}
   			
   			excleFileName="Stock_Industry_"+fileTime+"_All_"+sheetCount+".xlsx";
 			
   			File file = new File(filePath +fileTime+ "\\"+excleFileName);
			// ������   
			FileInputStream fileIStream = new FileInputStream(file);  
			
			 wb = new XSSFWorkbook(fileIStream);   
			 sheet = wb.getSheetAt(0);  
			// System.out.println("���һ�У�"+sheet.getLastRowNum());   
			
			StockExcelStatItem  statItem;   		
			
			//��ǰ��ҵ
			StockIndustry indu = listIndustry.get(i);	
			String induCode = indu.getThirdcode();
			String induName = indu.getThirdname();
			if(induCode == null || induName == null)
				continue;				
			stockLogger.logger.fatal("��ҵ��"+induName);   		
			System.out.println("��ҵ��"+induName);			
			//��ҵ���� 
			stockRow++;
			ExcelCommon.writeExcelItemTitle(wb,sheet,i+":"+induName,stockRow);
   					 	
   			//������ҵ���й�Ʊ
   			List<String> listIndustryStock = new ArrayList<String>();   
   			//listIndustryStock=sbDao.getIndustryStock(induName);	   	
   			listIndustryStock=sbDao.getIndustryStockFromAllInfo(induCode);
   			stockLogger.logger.debug("��ҵ��Ʊ����"+listIndustryStock.size());
   			int stockType=0;
   			for(Iterator ie=listIndustryStock.iterator();ie.hasNext();)
   			{
   				stockRow++;
   			
   				String stockFullId = (String) ie.next();	
   				System.out.println("stockFullId:"+stockFullId);
   				stockType=sbDao.getMarketType(stockFullId);
   				//if(!stockFullId.equals("SH601268"))
   				//	continue;
   				stockLogger.logger.fatal("****stockFullId��"+stockFullId+"****");
   				
   				StockSingle ss= sbDao.lookUpStockSingle(stockFullId);
   				StockExcelItem dayItem;
				StockExcelItem weekItem;
				StockExcelItem monthItem;
				
				
				int enableTingPai = getEnableTingPai(stockFullId);
				
				StockBaseFace baseFace = sbDao.lookUpStockBaseFace(stockFullId);
				//����ֵ
		   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,ss.getStockName(),ss.getEnableMarginTrading(),enableTingPai,baseFace);
		   		ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow);
				
   				//������Ԥ��ֵ
   				dayItem = getExcelItem(stockFullId,ConstantsInfo.DayDataType,stockType);   
   				if (dayItem == null){
   					stockLogger.logger.fatal("day point is null");
   					//continue;
   				} else {
   					ExcelCommon.writeExcelItem(wb,sheet,dayItem, stockRow, ConstantsInfo.DayDataType);
   				}
   				//������Ԥ��ֵ
   				weekItem = getExcelItem(stockFullId,ConstantsInfo.WeekDataType,stockType);
   				if (weekItem == null){
   					stockLogger.logger.fatal("week point is null");
   					//continue; 
   				} else {
   					ExcelCommon.writeExcelItem(wb,sheet,weekItem, stockRow, ConstantsInfo.WeekDataType);
   				}
   				//������Ԥ��ֵ
   				monthItem = getExcelItem(stockFullId,ConstantsInfo.MonthDataType,stockType);
   				if (monthItem == null){
   					stockLogger.logger.fatal("month point is null");
   					//continue; 
   				} else {
   					ExcelCommon.writeExcelItem(wb,sheet,monthItem,stockRow, ConstantsInfo.MonthDataType);
   				}
   				
   				//ͳ��
   				if (dayItem == null){
   					statItem = new StockExcelStatItem(0,0,0,1,1,1,
   	   						0,0,0,0,
   	   						0,0,0,0,0);   					
   				}else if (weekItem == null){
   					statItem = new StockExcelStatItem(0,0,0,dayItem.getSsValue().getFlag(),1,1,
   	   						dayItem.getSsValue().getPointTimes(),0,0,dayItem.getSsValue().getCurUpTimes(),
   	   						0,0,dayItem.getSsValue().getCurDownTimes(),0,0);
   					
   				} else if (monthItem == null) {
   					statItem = new StockExcelStatItem(0,0,0,dayItem.getSsValue().getFlag(),weekItem.getSsValue().getFlag(),1,
	   						dayItem.getSsValue().getPointTimes(),weekItem.getPointTimes(),0,dayItem.getSsValue().getCurUpTimes(),
	   						weekItem.getSsValue().getCurUpTimes(),0,dayItem.getSsValue().getCurDownTimes(),weekItem.getSsValue().getCurDownTimes(),0);
   				} else {	   			
	   				statItem = new StockExcelStatItem(0,0,0,dayItem.getSsValue().getFlag(),weekItem.getSsValue().getFlag(),monthItem.getSsValue().getFlag(),
	   						dayItem.getSsValue().getPointTimes(),weekItem.getSsValue().getPointTimes(),monthItem.getSsValue().getPointTimes(),dayItem.getSsValue().getCurUpTimes(),
	   						weekItem.getSsValue().getCurUpTimes(),monthItem.getSsValue().getCurUpTimes(),dayItem.getSsValue().getCurDownTimes(),weekItem.getSsValue().getCurDownTimes(),monthItem.getSsValue().getCurDownTimes());
   				}
   				
   				ExcelCommon.writeExcelStatItem(wb,sheet,statItem, stockRow);
   				
   				/*
	   			 if(stockRow % stockMaxRow == 0) {
	                 ((SXSSFSheet)sheet).flushRows(stockMaxRow); // retain 100 last rows and flush all others
	           
	   			 }
	   			 */
   			}
   			
			FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);
			wb.write(fileOStream);
			fileOStream.flush();
			fileIStream.close();
			fileOStream.close();              
			  
			listIndustryStock = null;
          //��������
		//	if(stockRow>20)
		//		break;
   		}
   		
   		listIndustry = null;
	}
	
	
	//����������excel 
	public void writeExcelFormConcept(String filePath, String fileTime) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
		     
	 	List<StockConcept> listConcept = new ArrayList<StockConcept>(); 
    	//�õ���ǰ���и���
    	listConcept=sbDao.getStockConcept();
    	System.out.println("���������"+listConcept.size());
    	XSSFWorkbook wb=null;
    	XSSFSheet sheet =null;
    	String excleFileName=null;
        //ÿ����ҵ�����excelһ��
        for (int i=0;i<listConcept.size();i++)	
   		{
        	
   			if(i==0 || stockRow >= 510) {
   				
   				//��������Ŀ¼
   				File file = new File(filePath+fileTime);  
   				System.out.println(fileTime);
   				if (!file.exists())
   				{   
   					 file.mkdir();   
   				} 
   				
	   			// ������		
	   			wb = new XSSFWorkbook(); 
	   			// ������һ��sheet     
	   			sheet=  wb.createSheet("allstock");		
	   			sheetCount++;
	   			excleFileName="Stock_Concept_"+fileTime+"_All_"+sheetCount+".xlsx";	   		    
	   		    stockRow = 3;
	   			//����excel
	   	 		ExcelCommon.createExcel(wb,sheet,filePath,excleFileName);
	   			writeExcelFromMarket(wb,sheet,filePath,excleFileName);
	   			FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
	   			wb.write(fileOStream);		
	   	        fileOStream.close(); 
	   	        wb=null;
	   	        sheet=null;
   			}
   			
   			excleFileName="Stock_Concept_"+fileTime+"_All_"+sheetCount+".xlsx";
 			
   			File file = new File(filePath +fileTime+ "\\"+excleFileName);
			// ������   
			FileInputStream fileIStream = new FileInputStream(file);  
			
			 wb = new XSSFWorkbook(fileIStream);   
			 sheet = wb.getSheetAt(0);  
			// System.out.println("���һ�У�"+sheet.getLastRowNum());   
			
			StockExcelStatItem  statItem;   		
			
			//��ǰ����
			 StockConcept concept= listConcept.get(i);	
			 String conceptName=concept.getName();
			 if(conceptName==null)
				 continue;
			 
			stockLogger.logger.fatal("���"+conceptName);
			System.out.println("���"+conceptName);	 			
  	
			//������� 
			stockRow++;
			ExcelCommon.writeExcelItemTitle(wb,sheet,i+":"+conceptName,stockRow);
   					 	
   			//�����������й�Ʊ
   			List<String> listConceptStock = new ArrayList<String>();     	
   			listConceptStock=sbDao.getConceptStock(conceptName);
   			stockLogger.logger.debug("�����Ʊ����"+listConceptStock.size());
   			
   			//�����Ƿ�������
   			List<StockExcelTotalInfo> listStockTotalInfoOrderBy = new ArrayList<StockExcelTotalInfo>(); 
   			
   			int stockType=0;
   			for(Iterator ie=listConceptStock.iterator();ie.hasNext();)
   			{
   				stockRow++;
   				String stockFullId = (String) ie.next();
   				System.out.println("stockFullId:"+stockFullId);
   				stockType=sbDao.getMarketType(stockFullId);
   			//	if(!stockFullId.equals("SZ300488"))
   			//		continue;
   				stockLogger.logger.fatal("****stockFullId��"+stockFullId+"****");
   				
   				StockSingle ss= sbDao.lookUpStockSingle(stockFullId);
   				if(ss == null)
   					continue;
   				StockExcelItem dayItem;
				StockExcelItem weekItem;
				StockExcelItem monthItem;
				
				int enableTingPai = getEnableTingPai(stockFullId);
				StockBaseFace baseFace = sbDao.lookUpStockBaseFace(stockFullId);
				//����ֵ
		   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,ss.getStockName(),ss.getEnableMarginTrading(),enableTingPai,baseFace);
		   		ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow);
				
   				//������Ԥ��ֵ
   				dayItem = getExcelItem(stockFullId,ConstantsInfo.DayDataType,stockType);   
   				if (dayItem == null){
   					stockLogger.logger.fatal("day point is null");
   					//continue;
   				} else {
   					ExcelCommon.writeExcelItem(wb,sheet,dayItem, stockRow, ConstantsInfo.DayDataType);
   				}
   				//������Ԥ��ֵ
   				weekItem = getExcelItem(stockFullId,ConstantsInfo.WeekDataType,stockType);
   				if (weekItem == null){
   					stockLogger.logger.fatal("week point is null");
   					//continue; 
   				} else {
   					ExcelCommon.writeExcelItem(wb,sheet,weekItem, stockRow, ConstantsInfo.WeekDataType);
   				}
   				//������Ԥ��ֵ
   				monthItem = getExcelItem(stockFullId,ConstantsInfo.MonthDataType,stockType);
   				if (monthItem == null){
   					stockLogger.logger.fatal("month point is null");
   					//continue; 
   				} else {
   					ExcelCommon.writeExcelItem(wb,sheet,monthItem,stockRow, ConstantsInfo.MonthDataType);
   				}
   				
   				//ͳ��
   				if (dayItem == null){
   					statItem = new StockExcelStatItem(0,0,0,1,1,1,
   	   						0,0,0,0,
   	   						0,0,0,0,0);   					
   				}else if (weekItem == null){
   					statItem = new StockExcelStatItem(0,0,0,dayItem.getSsValue().getFlag(),1,1,
   	   						dayItem.getSsValue().getPointTimes(),0,0,dayItem.getSsValue().getCurUpTimes(),
   	   						0,0,dayItem.getSsValue().getCurDownTimes(),0,0);
   					
   				} else if (monthItem == null) {
   					statItem = new StockExcelStatItem(0,0,0,dayItem.getSsValue().getFlag(),weekItem.getSsValue().getFlag(),1,
	   						dayItem.getSsValue().getPointTimes(),weekItem.getPointTimes(),0,dayItem.getSsValue().getCurUpTimes(),
	   						weekItem.getSsValue().getCurUpTimes(),0,dayItem.getSsValue().getCurDownTimes(),weekItem.getSsValue().getCurDownTimes(),0);
   				} else {	   			
	   				statItem = new StockExcelStatItem(0,0,0,dayItem.getSsValue().getFlag(),weekItem.getSsValue().getFlag(),monthItem.getSsValue().getFlag(),
	   						dayItem.getSsValue().getPointTimes(),weekItem.getSsValue().getPointTimes(),monthItem.getSsValue().getPointTimes(),dayItem.getSsValue().getCurUpTimes(),
	   						weekItem.getSsValue().getCurUpTimes(),monthItem.getSsValue().getCurUpTimes(),dayItem.getSsValue().getCurDownTimes(),weekItem.getSsValue().getCurDownTimes(),monthItem.getSsValue().getCurDownTimes());
   				}
   				
   				ExcelCommon.writeExcelStatItem(wb,sheet,statItem, stockRow);
   				
   				/*
	   			 if(stockRow % stockMaxRow == 0) {
	                 ((SXSSFSheet)sheet).flushRows(stockMaxRow); // retain 100 last rows and flush all others
	           
	   			 }
	   			 */
   			}
   			
			FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
			wb.write(fileOStream);
			fileOStream.flush();
			fileIStream.close();
			fileOStream.close();              
			  
			listConceptStock = null;
          //��������
			//if(stockRow>20)
			//	break;
   		}
   		
        listConcept = null;
	}
	
	
	//����������excel orderby
	public void writeExcelFormConceptOrderBy(String filePath, String fileTime) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
		     
	 	List<StockConcept> listConcept = new ArrayList<StockConcept>(); 
    	//�õ���ǰ���и���
    	listConcept=sbDao.getStockConcept();
    	System.out.println("���������"+listConcept.size());
    	XSSFWorkbook wb=null;
    	XSSFSheet sheet =null;
    	String excleFileName=null;
        //ÿ����ҵ�����excelһ��
        for (int i=0;i<listConcept.size();i++)	
   		{
        	
   			if(i==0 || stockRow >= 510) {
   				
   				//��������Ŀ¼
   				File file = new File(filePath+fileTime);  
   				System.out.println(fileTime);
   				if (!file.exists())
   				{   
   					 file.mkdir();   
   				} 
   				
	   			// ������		
	   			wb = new XSSFWorkbook(); 
	   			// ������һ��sheet     
	   			sheet=  wb.createSheet("allstock");		
	   			sheetCount++;
	   			excleFileName="Stock_Concept_"+fileTime+"_All_"+sheetCount+".xlsx";	   		    
	   		    stockRow = 3;
	   			//����excel
	   	 		ExcelCommon.createExcel(wb,sheet,filePath,excleFileName);
	   			writeExcelFromMarket(wb,sheet,filePath,excleFileName);
	   			FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
	   			wb.write(fileOStream);		
	   	        fileOStream.close(); 
	   	        wb=null;
	   	        sheet=null;
   			}
   			
   			excleFileName="Stock_Concept_"+fileTime+"_All_"+sheetCount+".xlsx";
 			
   			File file = new File(filePath +fileTime+ "\\"+excleFileName);
			// ������   
			FileInputStream fileIStream = new FileInputStream(file);  
			
			 wb = new XSSFWorkbook(fileIStream);   
			 sheet = wb.getSheetAt(0);  
			// System.out.println("���һ�У�"+sheet.getLastRowNum());   
			
			StockExcelStatItem  statItem;   		
			
			//��ǰ����
			 StockConcept concept= listConcept.get(i);	
			 String conceptName=concept.getName();
			 if(conceptName==null)
				 continue;
			 
			stockLogger.logger.fatal("���"+conceptName);
			System.out.println("���"+conceptName);	 			
  	
			//������� 
			stockRow++;
			ExcelCommon.writeExcelItemTitle(wb,sheet,i+":"+conceptName,stockRow);
   					 	
   			//�����������й�Ʊ
   			List<String> listConceptStock = new ArrayList<String>();     	
   			listConceptStock=sbDao.getConceptStock(conceptName);
   			stockLogger.logger.debug("�����Ʊ����"+listConceptStock.size());
   			
   		//�����Ƿ�������
   			List<StockExcelTotalInfo> listStockTotalInfoOrderBy = new ArrayList<StockExcelTotalInfo>(); 
   			
   			int stockType=0;
   			for(Iterator ie=listConceptStock.iterator();ie.hasNext();)
   			{
   				//stockRow++;
   				String stockFullId = (String) ie.next();
   				System.out.println("stockFullId:"+stockFullId);
   				stockType=sbDao.getMarketType(stockFullId);
   			//	if(!stockFullId.equals("SZ300488"))
   			//		continue;
   				stockLogger.logger.fatal("****stockFullId��"+stockFullId+"****");
   				
   				StockSingle ss= sbDao.lookUpStockSingle(stockFullId);
   				if(ss == null)
   					continue;
   				StockExcelItem dayItem;
				StockExcelItem weekItem;
				StockExcelItem monthItem;
				
				int enableTingPai = getEnableTingPai(stockFullId);
				
				//������
				StockBaseFace baseFace = sbDao.lookUpStockBaseFace(stockFullId);
				
				//����ֵ
		   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,ss.getStockName(),ss.getEnableMarginTrading(),enableTingPai,baseFace);
		   		//ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow);
				
   				//������Ԥ��ֵ
   				dayItem = getExcelItem(stockFullId,ConstantsInfo.DayDataType,stockType);   
   				if (dayItem == null){
   					stockLogger.logger.fatal("day point is null");
   					//continue;
   				} else {
   					//ExcelCommon.writeExcelItem(wb,sheet,dayItem, stockRow, ConstantsInfo.DayDataType);
   				}
   				//������Ԥ��ֵ
   				weekItem = getExcelItem(stockFullId,ConstantsInfo.WeekDataType,stockType);
   				if (weekItem == null){
   					stockLogger.logger.fatal("week point is null");
   					//continue; 
   				} else {
   					//ExcelCommon.writeExcelItem(wb,sheet,weekItem, stockRow, ConstantsInfo.WeekDataType);
   				}
   				//������Ԥ��ֵ
   				monthItem = getExcelItem(stockFullId,ConstantsInfo.MonthDataType,stockType);
   				if (monthItem == null){
   					stockLogger.logger.fatal("month point is null");
   					//continue; 
   				} else {
   					//ExcelCommon.writeExcelItem(wb,sheet,monthItem,stockRow, ConstantsInfo.MonthDataType);
   				}
   				
   				//ͳ��
   				if (dayItem == null){
   					statItem = new StockExcelStatItem(0,0,0,1,1,1,
   	   						0,0,0,0,
   	   						0,0,0,0,0);   					
   				}else if (weekItem == null){
   					statItem = new StockExcelStatItem(0,0,0,dayItem.getSsValue().getFlag(),1,1,
   	   						dayItem.getSsValue().getPointTimes(),0,0,dayItem.getSsValue().getCurUpTimes(),
   	   						0,0,dayItem.getSsValue().getCurDownTimes(),0,0);
   					
   				} else if (monthItem == null) {
   					statItem = new StockExcelStatItem(0,0,0,dayItem.getSsValue().getFlag(),weekItem.getSsValue().getFlag(),1,
	   						dayItem.getSsValue().getPointTimes(),weekItem.getPointTimes(),0,dayItem.getSsValue().getCurUpTimes(),
	   						weekItem.getSsValue().getCurUpTimes(),0,dayItem.getSsValue().getCurDownTimes(),weekItem.getSsValue().getCurDownTimes(),0);
   				} else {	   			
	   				statItem = new StockExcelStatItem(0,0,0,dayItem.getSsValue().getFlag(),weekItem.getSsValue().getFlag(),monthItem.getSsValue().getFlag(),
	   						dayItem.getSsValue().getPointTimes(),weekItem.getSsValue().getPointTimes(),monthItem.getSsValue().getPointTimes(),dayItem.getSsValue().getCurUpTimes(),
	   						weekItem.getSsValue().getCurUpTimes(),monthItem.getSsValue().getCurUpTimes(),dayItem.getSsValue().getCurDownTimes(),weekItem.getSsValue().getCurDownTimes(),monthItem.getSsValue().getCurDownTimes());
   				}
   				
   				//ExcelCommon.writeExcelStatItem(wb,sheet,statItem, stockRow);
   				
   				StockExcelTotalInfo setInfo =new StockExcelTotalInfo(soiValue,dayItem,weekItem,monthItem,statItem);
   				listStockTotalInfoOrderBy.add(setInfo);
   				
   			}
   			
   			//����
   			Collections.sort(listStockTotalInfoOrderBy); 
   		           
   			for (int j=0;j<listStockTotalInfoOrderBy.size();j++)	
   			{
   				StockExcelTotalInfo setInfo = (StockExcelTotalInfo) listStockTotalInfoOrderBy.get(j);
   				stockRow++;
   				ExcelCommon.writeExcelStockOtherInfo(wb, sheet, setInfo.getSoiValue(), stockRow);
   				if (setInfo.getDayItem() != null)
   					ExcelCommon.writeExcelItem(wb,sheet,setInfo.getDayItem(), stockRow, ConstantsInfo.DayDataType);
   				if (setInfo.getWeekItem() != null)
   					ExcelCommon.writeExcelItem(wb,sheet,setInfo.getWeekItem(), stockRow, ConstantsInfo.WeekDataType);
   				if (setInfo.getMonthItem() != null)
   					ExcelCommon.writeExcelItem(wb,sheet,setInfo.getMonthItem(),stockRow, ConstantsInfo.MonthDataType);
   				ExcelCommon.writeExcelStatItem(wb,sheet,setInfo.getStatItem(), stockRow);
   			}
   			
   			listStockTotalInfoOrderBy =null;
   			
			FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
			wb.write(fileOStream);
			fileOStream.flush();
			fileIStream.close();
			fileOStream.close();              
			  
			listConceptStock = null;
          //��������
		//	if(stockRow>30)
		//		break;
   		}
   		
        listConcept = null;
	}
	
	
	//��������һ����ҵ������excel orderby
	public void writeExcelFormConceptInFirstIndustryOrderBy(String filePath, String fileTime) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
		     
	 	List<String> listConcept = new ArrayList<String>(); 
	 	List<String> listFirstIndustry = new ArrayList<String>(); 
    	//�õ���ǰһ����ҵcode
	 	listFirstIndustry=sbDao.getStockFirstIndustry();    	
    	
    	XSSFWorkbook wb=null;
    	XSSFSheet sheet =null;
    	String excleFileName=null;
        int flag_first=0;
    	//�������һ����ҵ
        for (int countI=0; countI<listFirstIndustry.size();countI++)	
   		{
    	
        	//�õ���ǰһ����ҵ�¸���code
    	 	listFirstIndustry=sbDao.getStockFirstIndustry();
        	String firstIndustryId = listFirstIndustry.get(countI);
        	
        	//һ����ҵ����
        	String firstIndustryName = sbDao.getStockFirstIndustryName(firstIndustryId);        	
        	System.out.println(firstIndustryName);        	
        	
        	//�õ�����id
        	listConcept = sbDao.getStockFirstIndustryConceptCode1(firstIndustryId);
        	
	    	//ÿ����ҵ�����excelһ��
	        for (int i=0;i<listConcept.size();i++)	
	   		{
	        	//countI��һ��	        	
	   			if(flag_first == 0 || stockRow >= 510) {
	   				
	   				flag_first =1;
	   				//��������Ŀ¼
	   				File file = new File(filePath+fileTime);  
	   				System.out.println(fileTime);
	   				if (!file.exists())
	   				{   
	   					 file.mkdir();   
	   				} 
	   				
		   			// ������		
		   			wb = new XSSFWorkbook(); 
		   			// ������һ��sheet     
		   			sheet=  wb.createSheet("allstock");		
		   			sheetCount++;
		   			excleFileName="Stock_Concept_"+fileTime+"_All_"+sheetCount+".xlsx";	   		    
		   		    stockRow = 3;
		   			//����excel
		   	 		ExcelCommon.createExcel(wb,sheet,filePath,excleFileName);
		   			writeExcelFromMarket(wb,sheet,filePath,excleFileName);
		   			FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
		   			wb.write(fileOStream);		
		   	        fileOStream.close(); 
		   	        wb=null;
		   	        sheet=null;
	   			}
	   			
	   			excleFileName="Stock_Concept_"+fileTime+"_All_"+sheetCount+".xlsx";
	 			
	   			File file = new File(filePath +fileTime+ "\\"+excleFileName);
				// ������   
				FileInputStream fileIStream = new FileInputStream(file);  
				
				 wb = new XSSFWorkbook(fileIStream);   
				 sheet = wb.getSheetAt(0);  
				// System.out.println("���һ�У�"+sheet.getLastRowNum());   
				
				StockExcelStatItem  statItem;   		
				
				//��ǰһ����ҵ
				 String conceptCode= listConcept.get(i);	
				 String conceptName=sbDao.getConceptNameFromFirstIndeustyToConceptTable(conceptCode);
				 if(conceptName==null)
					 continue;
				 
				stockLogger.logger.fatal("���"+conceptName);
				System.out.println("���"+conceptName);	 			
	  	
				//������� 
				stockRow++;
				ExcelCommon.writeExcelItemTitle(wb,sheet,i+":"+firstIndustryName+"-"+conceptName,stockRow);
	   					 	
	   			//�����������й�Ʊ
				//�����������й�Ʊ
	   			List<StockToConcept> listConceptStock = new ArrayList<StockToConcept>();
	   			listConceptStock=sbDao.getStockToConcept(conceptCode);
	   			stockLogger.logger.debug("�����Ʊ����"+listConceptStock.size());
	   			
	   			//�����Ƿ�������
	   			List<StockExcelTotalInfo> listStockTotalInfoOrderBy = new ArrayList<StockExcelTotalInfo>(); 
	   			
	   			int stockType=0;
	   			for(Iterator ie=listConceptStock.iterator();ie.hasNext();)
	   			{
	   				//stockRow++;
	   				String stockFullId = (String) ie.next();
	   				System.out.println("stockFullId:"+stockFullId);
	   				stockType=sbDao.getMarketType(stockFullId);
	   			//	if(!stockFullId.equals("SZ300488"))
	   			//		continue;
	   				stockLogger.logger.fatal("****stockFullId��"+stockFullId+"****");
	   				
	   				StockSingle ss= sbDao.lookUpStockSingle(stockFullId);
	   				if(ss == null)
	   					continue;
	   				StockExcelItem dayItem;
					StockExcelItem weekItem;
					StockExcelItem monthItem;
					
					int enableTingPai = getEnableTingPai(stockFullId);
					
					//������
					StockBaseFace baseFace = sbDao.lookUpStockBaseFace(stockFullId);
					
					//����ֵ
			   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,ss.getStockName(),ss.getEnableMarginTrading(),enableTingPai,baseFace);
			   		//ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow);
					
	   				//������Ԥ��ֵ
	   				dayItem = getExcelItem(stockFullId,ConstantsInfo.DayDataType,stockType);   
	   				if (dayItem == null){
	   					stockLogger.logger.fatal("day point is null");
	   					//continue;
	   				} else {
	   					//ExcelCommon.writeExcelItem(wb,sheet,dayItem, stockRow, ConstantsInfo.DayDataType);
	   				}
	   				//������Ԥ��ֵ
	   				weekItem = getExcelItem(stockFullId,ConstantsInfo.WeekDataType,stockType);
	   				if (weekItem == null){
	   					stockLogger.logger.fatal("week point is null");
	   					//continue; 
	   				} else {
	   					//ExcelCommon.writeExcelItem(wb,sheet,weekItem, stockRow, ConstantsInfo.WeekDataType);
	   				}
	   				//������Ԥ��ֵ
	   				monthItem = getExcelItem(stockFullId,ConstantsInfo.MonthDataType,stockType);
	   				if (monthItem == null){
	   					stockLogger.logger.fatal("month point is null");
	   					//continue; 
	   				} else {
	   					//ExcelCommon.writeExcelItem(wb,sheet,monthItem,stockRow, ConstantsInfo.MonthDataType);
	   				}
	   				
	   				//ͳ��
	   				if (dayItem == null){
	   					statItem = new StockExcelStatItem(0,0,0,1,1,1,
	   	   						0,0,0,0,
	   	   						0,0,0,0,0);   					
	   				}else if (weekItem == null){
	   					statItem = new StockExcelStatItem(0,0,0,dayItem.getSsValue().getFlag(),1,1,
	   	   						dayItem.getSsValue().getPointTimes(),0,0,dayItem.getSsValue().getCurUpTimes(),
	   	   						0,0,dayItem.getSsValue().getCurDownTimes(),0,0);
	   					
	   				} else if (monthItem == null) {
	   					statItem = new StockExcelStatItem(0,0,0,dayItem.getSsValue().getFlag(),weekItem.getSsValue().getFlag(),1,
		   						dayItem.getSsValue().getPointTimes(),weekItem.getPointTimes(),0,dayItem.getSsValue().getCurUpTimes(),
		   						weekItem.getSsValue().getCurUpTimes(),0,dayItem.getSsValue().getCurDownTimes(),weekItem.getSsValue().getCurDownTimes(),0);
	   				} else {	   			
		   				statItem = new StockExcelStatItem(0,0,0,dayItem.getSsValue().getFlag(),weekItem.getSsValue().getFlag(),monthItem.getSsValue().getFlag(),
		   						dayItem.getSsValue().getPointTimes(),weekItem.getSsValue().getPointTimes(),monthItem.getSsValue().getPointTimes(),dayItem.getSsValue().getCurUpTimes(),
		   						weekItem.getSsValue().getCurUpTimes(),monthItem.getSsValue().getCurUpTimes(),dayItem.getSsValue().getCurDownTimes(),weekItem.getSsValue().getCurDownTimes(),monthItem.getSsValue().getCurDownTimes());
	   				}
	   				
	   				//ExcelCommon.writeExcelStatItem(wb,sheet,statItem, stockRow);
	   				
	   				StockExcelTotalInfo setInfo =new StockExcelTotalInfo(soiValue,dayItem,weekItem,monthItem,statItem);
	   				listStockTotalInfoOrderBy.add(setInfo);
	   				
	   			}
	   			
	   			//����
	   			Collections.sort(listStockTotalInfoOrderBy); 
	   		           
	   			for (int j=0;j<listStockTotalInfoOrderBy.size();j++)	
	   			{
	   				StockExcelTotalInfo setInfo = (StockExcelTotalInfo) listStockTotalInfoOrderBy.get(j);
	   				stockRow++;
	   				ExcelCommon.writeExcelStockOtherInfo(wb, sheet, setInfo.getSoiValue(), stockRow);
	   				if (setInfo.getDayItem() != null)
	   					ExcelCommon.writeExcelItem(wb,sheet,setInfo.getDayItem(), stockRow, ConstantsInfo.DayDataType);
	   				if (setInfo.getWeekItem() != null)
	   					ExcelCommon.writeExcelItem(wb,sheet,setInfo.getWeekItem(), stockRow, ConstantsInfo.WeekDataType);
	   				if (setInfo.getMonthItem() != null)
	   					ExcelCommon.writeExcelItem(wb,sheet,setInfo.getMonthItem(),stockRow, ConstantsInfo.MonthDataType);
	   				ExcelCommon.writeExcelStatItem(wb,sheet,setInfo.getStatItem(), stockRow);
	   			}
	   			
	   			listStockTotalInfoOrderBy =null;
	   			
				FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
				wb.write(fileOStream);
				fileOStream.flush();
				fileIStream.close();
				fileOStream.close();              
				  
				listConceptStock = null;
	          
				 //��������
				if(stockRow>10)
					break;				
	   		}
	        
	      //��������
			if(stockRow>10)
				break;
	        
   		}
   		
        listConcept = null;
	}
	
	
	public static void main(String[] args) throws SecurityException, SQLException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException {
		PropertyConfigurator.configure("stockConf/log4j_excelWriter.properties");

		stockLogger.logger.fatal("excel stock export start");	
		
		Date startDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
        String dateNowStr = sdf.format(startDate);   
        Connection stockBaseConn = DbConn.getConnDB("stockConf/conn_base_db.ini"); 
        Connection stockDataConn = DbConn.getConnDB("stockConf/conn_data_db.ini"); 
        Connection stockPointConn = DbConn.getConnDB("stockConf/conn_point_db.ini");
	   
       //Str/ng stockBaseConn = ""
        
		//siDao =new StockInformationDao(stockBaseConn);
        
        String excleFileName="stock_ALL_"+dateNowStr+".xlsx";
		StockExcelPartition se = new StockExcelPartition(stockBaseConn,stockDataConn,stockPointConn);
		
		//����
		//se.writeExcelFormIndustryOrderBy("export\\",dateNowStr);
		se.writeExcelFormConceptInFirstIndustryOrderBy("export\\",dateNowStr);
		
		//se.writeExcelFormIndustry("export\\",dateNowStr);
		//se.writeExcelFormConcept("export\\",dateNowStr);
		
		stockBaseConn.close();
	    stockDataConn.close();
	    stockPointConn.close();
		
		Date endDate = new Date();
		// ��������ʱ�����������
		long seconds =(endDate.getTime() - startDate.getTime())/1000;
		System.out.println("�ܹ���ʱ��"+seconds+"��");
		System.out.println("end");
		stockLogger.logger.fatal("excel stock export end");		
	}

}
