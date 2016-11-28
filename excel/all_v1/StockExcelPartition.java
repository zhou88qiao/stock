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

//分片处理excel 分成多个excel

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
 	
 	String SHDate=null; //记录上证大盘当前时间，方便与其他股票时间作对比，是不有停牌现象
 	
 	//记录market 方便后面股票对比计算
 	StockMaretInfoValue MarketStockTimeInfo[][] = new StockMaretInfoValue[4][3];
  
 	//比例保留两位小数点
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
 	
 	//保留几位小数
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
 	 	
 	//获取两值比率
 	public float getGAP(float value1,float value2)
 	{
 		float tmpValue = (float) (value2 - value1)/value1;
		float gap = getDec(tmpValue,4);
		return gap;
 	} 	 
 	                
 	//计算 分析趋势，上，下转等
 	public ExcelItem getExcelItem1(String stockFullId,int dataType) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException
 	{
 		int stockPointTimes=0;
 		ExcelItem eItem;
 		float ratioHistory=0;	//历史涨幅比
 		float ratioCur=0;//当前涨幅比 		

    	String nowTime=stockDateTimer.getCurDate();
    	
    	int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_POINT_STOCK);
    	if(isTableExist == 0){//不存在
			stockLogger.logger.fatal("****stockFullId："+stockFullId+"不存在极值表****");
			System.out.println(stockFullId+"极值表不存在****");
			return null;  
		}
    	
		//1 轮次W
		stockPointTimes = spDao.getStockPointTimes(stockFullId,dataType);
		stockLogger.logger.debug("轮次："+stockPointTimes);
		
		if(stockPointTimes == 0){
			stockLogger.logger.fatal("****stockFullId："+stockFullId+"无极值点数据****");
			System.out.println(stockFullId+"无极值点数据****");
			return null;  
		}
		
		//2 最后一个极值点
		StockPoint sp = spDao.getLastPointStock(stockFullId,dataType);
		float lastExtrmePrice = sp.getExtremePrice();
		String crossLastDate=sp.getToDate().toString();
		stockLogger.logger.debug("最后一个交叉点："+crossLastDate);
		
		//历史涨幅只计算一次，在计算日类型时
		if (dataType == ConstantsInfo.DayDataType) {
			float minPrice = sdDao.getStockMinORMaxPriceData(stockFullId,0,ConstantsInfo.SeasonDataType);
			if ( minPrice < 0.1) {
				minPrice = sdDao.getStockMinORMaxPriceData(stockFullId,0,ConstantsInfo.DayDataType);
			}
			float maxPrice = sdDao.getStockMinORMaxPriceData(stockFullId,1,ConstantsInfo.SeasonDataType);
			if ( maxPrice < 0.1) {
				maxPrice = sdDao.getStockMinORMaxPriceData(stockFullId,1,ConstantsInfo.DayDataType);
			}
			//历史涨幅比,保留两位有效数字
			float ratioTmp1 = (maxPrice - minPrice)/minPrice*100;
		//	System.out.println("maxPrice:"+maxPrice+"minPrice:"+minPrice);
			ratioHistory = (float)(Math.round(ratioTmp1*100))/100;
		}
		
		String extremeDate=null;
		int haveTurn=0; //是否出现反转
		int tread=0; //上升或下跌
		
		float latelyPrice=0;
		StockData sMinData=null;
		StockData sMaxData=null;
		if	(sp.getWillFlag()==1) { //前一个是上升，当前为下降趋势
			//3 最近最低点
			sMinData=sdDao.getMinStockDataPoint(stockFullId,crossLastDate,nowTime,dataType);
			if ( sMinData == null){
				stockLogger.logger.fatal("****stockFullId："+stockFullId+"无最近低点****");
				return null; 
			}
				
			extremeDate=sMinData.getDate().toString();	
			latelyPrice = sMinData.getLowestPrice();
			stockLogger.logger.debug("最近最低点时间："+extremeDate+"最近最低点点位："+sMinData.getLowestPrice());
		} else {
			//3 最近最高点
			sMaxData=sdDao.getMaxStockDataPoint(stockFullId,crossLastDate,nowTime,dataType);
			if ( sMaxData == null){
				stockLogger.logger.fatal("****stockFullId："+stockFullId+"无最近高点****");
				stockLogger.logger.fatal("****交叉点："+crossLastDate+"当前日点："+nowTime+"两日期间无交易数据****");
				return null; 
			}
			extremeDate=sMaxData.getDate().toString();	
			latelyPrice = sMaxData.getHighestPrice();
			stockLogger.logger.debug("最近最高点时间："+extremeDate+"最近最高点点位："+sMaxData.getHighestPrice());
		}
		
		// upTimes downTime至少为1，计算小于等于两结束时间
		int upTimes=sdDao.getStockTradeTimes(stockFullId,crossLastDate,extremeDate,dataType);
		int downTimes=sdDao.getStockTradeTimes(stockFullId,extremeDate,nowTime,dataType);
		
		//最近涨幅比
		float ratioTmp2 = (latelyPrice - lastExtrmePrice)/lastExtrmePrice*100;
		ratioCur = (float)(Math.round(ratioTmp2*100))/100;
		
		
		if	(sp.getWillFlag()==1) {
			stockLogger.logger.debug("下："+upTimes+"上："+downTimes);
			tread = 0; //趋势反转
			if (upTimes > 1)
				haveTurn = 1;
			else
				upTimes =0 ;//无反转，并反转天数置为0
			eItem=new ExcelItem(stockFullId,ratioHistory,downTimes,upTimes,haveTurn,stockPointTimes,ratioCur,latelyPrice,extremeDate,crossLastDate,tread);
		} else {
			tread = 1;
			stockLogger.logger.debug("上："+upTimes+"下："+downTimes);
			if (downTimes >1)
				haveTurn = 1;
			else
				downTimes = 0; //无反转，并反转天数置为0
			eItem=new ExcelItem(stockFullId,ratioHistory,downTimes,upTimes,haveTurn,stockPointTimes,ratioCur,latelyPrice,extremeDate,crossLastDate,tread);
		}
			
		//构造对象
		
		return eItem;
 	}
 	
 	
 	//计算date当天 分析趋势，上，下转等
 	public StockExcelItem getExcelItem(String stockFullId,int dataType,int stockType) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
 	{
 	
 		StockExcelItem eItem=null;
 		String curExtremeDate=null;//最近极值时间
 		String lastExtremeDate =null; //前一个极点时间
 		//轮次
 		int stockPointTimes=0;
 		//当天
 		String nowTime=stockDateTimer.getCurDate(); 

 		String curStartDate = null,curEndDate = null,desireDate = null;
 		float curRange,curStartValue = 0,curEndValue=0;//开始，结束点位
 		float desireValue1 = 0,desireValue2,desireValue3,desireValue4,desireValue5,desireValue6=0;
 		float desireValue1Gap,desireValue2Gap,desireValue3Gap,desireValue4Gap,desireValue5Gap,desireValue6Gap=0;
 		float desireRange1,desireRange2,desireRange3,desireRange4,desireRange5,desireRange6=0;
 		float desireRate1,desireRate2,desireRate3,desireRate4,desireRate5,desireRate6=0;
 		float bugValue = 0,winValue = 0,loseValue=0; 		
 		float tmpValue=0;
 		float rate=0;
 		int startDateGAP,endDateGAP,desireDateGap=0;
 		//与大盘对比
 		int toMarketStartDateGAP,toMarketEndDateGAP;
 		float toMaretRangeGAP=0;
 		int marketIndex=0;
 		StockStatValue ssValue;//统计
 		StockCurValue scValue;//当前
 		StockDesireValue sdValue;//预期
 		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 
 		
    	int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_DATA_STOCK);
    	if(isTableExist == 0){//不存在
			stockLogger.logger.fatal("****stockFullId："+stockFullId+"不存在极值表****");
			System.out.println(stockFullId+"极值表不存在****");
			return null;  
		}
    	
    	isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_POINT_STOCK);
    	if(isTableExist == 0){//不存在
			stockLogger.logger.fatal("****stockFullId："+stockFullId+"不存在交易表****");
			System.out.println(stockFullId+"交易表不存在****");
			return null;  
		}    	
       	
		//1 最后一个极值点
		StockPoint sp = spDao.getLastPointStock(stockFullId,dataType);
		if(sp == null) {
			stockLogger.logger.fatal("stockFullId："+stockFullId+"极点表无数据");
			System.out.println(stockFullId+"极点表无数据****");
			return null;  
		}
		
		float lastExtrmePrice = sp.getExtremePrice();
		lastExtremeDate = sdf.format(sp.getExtremeDate());
		String crossLastDate=sp.getToDate().toString();
	//	stockLogger.logger.debug("极值时间："+extremeDate);
	//	stockLogger.logger.debug("最后一个交叉点："+crossLastDate);		
		
		//1 轮次W
		stockPointTimes = spDao.getStockPointTimes(stockFullId,dataType);
		if(stockPointTimes == 0){
			stockLogger.logger.fatal("stockFullId："+stockFullId+"无极值点数据");
			System.out.println(stockFullId+"无极值点数据****");
			return null;
		}
		
		//取当天日数据作为计算，不拿周，月数据
		StockData sdata = sdDao.getLastDataStock(stockFullId,ConstantsInfo.DayDataType);
		String curDate= sdata.getDate().toString();
		float curPrice=0;
	
		StockData sMinData=null;
		StockData sMaxData=null;
		if (sp.getWillFlag()==1) { //前一个是上升，当前为下降趋势
			
			//新的交叉点 趋势反转
		//	if (crossLastDate.equals(nowTime)){
				
		//	}else {			
				//3 最近最低点
				//sMinData=sdDao.getMinStockDataPoint(stockFullId,crossLastDate,nowTime,ConstantsInfo.DayDataType);
				sMinData=sdDao.getMinStockDataPoint(stockFullId,lastExtremeDate,nowTime,ConstantsInfo.DayDataType);
				if ( sMinData == null){
					stockLogger.logger.fatal("****stockFullId："+stockFullId+"无最近低点****");
					return null; 
				}
				//float minPrice = sMinData.getLowestPrice();
				//开始时间
				curStartDate = 	lastExtremeDate;
				//结束时间
				curEndDate = sMinData.getDate().toString();	
				curExtremeDate = curEndDate;//最近极点时间
				//开始点位
				curStartValue = lastExtrmePrice;//sdDao.getStockLowestPrice(stockFullId,curDownStartDate,dataType);
				//结束点位
				curEndValue = sMinData.getLowestPrice();
				//当前幅
				//curDownRange = sdDao.getStockCurHigestRange(stockFullId,curDownEndDate,dataType,0);		
				//预期时间
				desireDate = stockDateTimer.getAddDate(sMinData.getDate(),stockDesiredDate);
				//当前收盘价格
				curPrice = sdata.getClosingPrice();
		//	}			
		} else {
			
			//新的交叉点 趋势反转
		//	if (crossLastDate.equals(nowTime)){
				
		//	}else {	
				//3 最近最高点
				sMaxData=sdDao.getMaxStockDataPoint(stockFullId,lastExtremeDate,nowTime,ConstantsInfo.DayDataType);
				if ( sMaxData == null){
					stockLogger.logger.fatal("****stockFullId："+stockFullId+"无最近高点****");
					stockLogger.logger.fatal("****交叉点："+crossLastDate+"当前日点："+nowTime+"两日期间无交易数据****");
					return null; 
				}
				//开始时间
				curStartDate = lastExtremeDate;
				//开始点位
				curStartValue = lastExtrmePrice;//sdDao.getStockHighestPrice(stockFullId,curUpStartDate,dataType);//开始点位
				//结束时间
				curEndDate = sMaxData.getDate().toString();	
				//结束点位
				curEndValue = sMaxData.getHighestPrice();
				//最近极点时间
				curExtremeDate = curEndDate;
				
				desireDate = stockDateTimer.getAddDate(sMaxData.getDate(),stockDesiredDate);
				//幅
				//curUpRange = sdDao.getStockCurHigestRange(stockFullId,curUpEndDate,dataType,1);
				
				
				//当前最高价格
				curPrice = sdata.getClosingPrice();
				//stockLogger.logger.debug("up开始时间："+curUpStartDate+",up结束时间："+curUpEndDate);
				//stockLogger.logger.debug("up幅："+curUpRange);
				//stockLogger.logger.debug("up预期时间："+desireUpDate+",up预期值："+desireUpValue);
		//	}
		}
		
		//当前幅度	
		curRange=getGAP(curStartValue,curEndValue);
		
		//开始时间差 点位比
		startDateGAP = stockDateTimer.daysBetween(curStartDate,curDate,dataType);
		float startValueGAP = getGAP(curStartValue,curPrice);
		/*
		System.out.println("curStartDate:"+curStartDate);
		System.out.println("curStartValue:"+curStartValue);
		System.out.println("curPrice:"+curPrice);
		System.out.println("startValueGAP:"+startValueGAP);
		*/
		//结束时间差 点位比
		endDateGAP = stockDateTimer.daysBetween(curDate,curEndDate,dataType);
		float endValueGAP =getGAP(curEndValue,curPrice);
		/*
		System.out.println("curDate:"+curDate);
		System.out.println("curEndValue:"+curEndValue);
		System.out.println("curPrice:"+curPrice);
		System.out.println("endValueGAP:"+endValueGAP);
		*/
		
		//比率
		if(endDateGAP ==0) {
			rate=0;
		}else {
			tmpValue= endValueGAP*100/endDateGAP;			
			rate= getDec(tmpValue,2);
		}
		
		
		//预期时间差
		desireDateGap = stockDateTimer.daysBetween(curDate,desireDate,dataType);
		
		//0.382预期点位 onstantsInfo.STOCK_DESIRE1 
		tmpValue = (float) (curEndValue - 0.382 * (curEndValue - curStartValue));
		desireValue1= getDec(tmpValue,2);
		//预期幅度
		tmpValue = desireValue1/curEndValue -1;
		desireRange1 = getDec(tmpValue,4);		
		//比率
		tmpValue= desireRange1*100/stockDesiredDate;			
		desireRate1= getDec(tmpValue,2);		
		//预期差
		tmpValue = (float)(curPrice - desireValue1)/desireValue1;
		desireValue1Gap= getDec(tmpValue,4);
		
		//0.5预期点位
		tmpValue = (float) (curEndValue - 0.5 * (curEndValue - curStartValue));
		desireValue2 =  getDec(tmpValue,2);
		//预期幅度
		tmpValue = desireValue2/curEndValue -1;
		desireRange2 =  getDec(tmpValue,4);
		//比率
		tmpValue= desireRange2*100/stockDesiredDate;			
		desireRate2= getDec(tmpValue,2);	
		//预期差
		tmpValue = (float)(curPrice - desireValue2)/desireValue2;
		desireValue2Gap =  getDec(tmpValue,4);
		
		//0.618预期点位
		tmpValue = (float) (curEndValue - 0.618 * (curEndValue - curStartValue));
		desireValue3 =  getDec(tmpValue,2);	
		//预期幅度
		tmpValue = desireValue3/curEndValue -1;
		desireRange3 = getDec(tmpValue,4);
		//比率
		tmpValue= desireRange3*100/stockDesiredDate;			
		desireRate3= getDec(tmpValue,2);	
		//预期差
		tmpValue = (float)(curPrice - desireValue3)/desireValue3;
		desireValue3Gap =  getDec(tmpValue,4);
		
		//0.75预期点位
		tmpValue = (float) (curEndValue - 0.75 * (curEndValue - curStartValue));
		desireValue4 =  getDec(tmpValue,2);
		//预期幅度
		tmpValue = desireValue4/curEndValue -1;
		desireRange4 = getDec(tmpValue,4);
		//比率
		tmpValue= desireRange4*100/stockDesiredDate;			
		desireRate4= getDec(tmpValue,2);
		//预期差
		tmpValue = (float)(curPrice - desireValue4)/desireValue4;
		desireValue4Gap =  getDec(tmpValue,4);
		
		//1 预期点位
		tmpValue = (float) (curEndValue - 1 * (curEndValue - curStartValue));
		desireValue5 = getDec(tmpValue,2);
		//预期幅度
		tmpValue = desireValue5/curEndValue -1;
		desireRange5 = getDec(tmpValue,4);
		//比率
		tmpValue= desireRange5*100/stockDesiredDate;			
		desireRate5= getDec(tmpValue,2);	
		//预期差
		tmpValue = (float)(curPrice - desireValue5)/desireValue5;
		desireValue5Gap =  getDec(tmpValue,4);
		
		//1.08预期点位
		tmpValue = (float) (curEndValue - 1.08 * (curEndValue - curStartValue));
		desireValue6 = getDec(tmpValue,2);
		//预期幅度
		tmpValue = desireValue6/curEndValue -1;
		desireRange6 = getDec(tmpValue,4);
		//比率
		tmpValue= desireRange6*100/stockDesiredDate;			
		desireRate6= getDec(tmpValue,2);
		//预期差
		tmpValue = (float)(curPrice - desireValue6)/desireValue6;
		desireValue6Gap = getDec(tmpValue,4);
		
		sdValue=new StockDesireValue(desireDate,desireDateGap,desireValue1,desireRange1,desireRate1,desireValue1Gap,desireValue2,desireRange2,desireRate2,desireValue2Gap,desireValue3,desireRange3,desireRate3,desireValue3Gap,desireValue4,desireRange4,desireRate4,desireValue4Gap,desireValue5,desireRange5,desireRate5,desireValue5Gap,desireValue6,desireRange6,desireRate6,desireValue6Gap);
		 		
		if (sp.getWillFlag()==1) { //前一个是上升，当前为下降趋势
			//买
			tmpValue = (float) (1 + curRange/100)* curEndValue;
			bugValue = getDec(tmpValue,2);			
			
			//赢 0.618
			tmpValue = (float) (desireValue3/bugValue-1);
			winValue = getDec(tmpValue,4);	
			
			//亏
			tmpValue = (float) (curEndValue/bugValue -1);	
			loseValue = getDec(tmpValue,4);	
		}
		
		// upTimes downTime至少为1，计算小于等于两结束时间
		int upTimes=sdDao.getStockTradeTimes(stockFullId,crossLastDate,curExtremeDate,dataType);
		int downTimes=sdDao.getStockTradeTimes(stockFullId,curExtremeDate,nowTime,dataType);
			
		//大盘指数
		if (stockType == ConstantsInfo.DPMarket){
			StockMaretInfoValue smiValue=new StockMaretInfoValue(stockFullId,curStartDate,curEndDate,curRange);
			marketIndex = sbDao.getMarketNum(stockFullId);
			//保留大盘时间值			
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
		
		if(sp.getWillFlag()==1) {	 //当前上涨	下一个是下跌	
			ssValue=new StockStatValue(0,0,sp.getWillFlag(),stockPointTimes,upTimes,downTimes);
		} else {	
			ssValue=new StockStatValue(0,1,sp.getWillFlag(),stockPointTimes,upTimes,downTimes);
		}
		
		eItem=new StockExcelItem(stockFullId,ssValue,scValue,sdValue);
	
		return eItem;
 	}
 	
 	//是否出现停牌现像
 	int getEnableTingPai(String stockFullId) throws IOException, ClassNotFoundException, SQLException
 	{
 		//取当天取大盘时间对比，是否有停牌
 		StockData sdata = sdDao.getLastDataStock(stockFullId,ConstantsInfo.DayDataType);
 		if(sdata == null){
 			System.out.println("last data null");
 			return 0;
 		}
 			
 		if(SHDate.equals(sdata.getDate().toString()))
 			return 0;
 		else
 			return 1;//停牌
 	}
 
 	
 	//分析大盘
 	public void writeExcelFromMarket(Workbook wb,Sheet sheet,String filePath, String fileName) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
 		
		 List<StockMarket> listStockMarket = new ArrayList<StockMarket>(); 
		 listStockMarket=sbDao.getStockMarket(ConstantsInfo.StockMarket);
		 
		//先分析大盘
	    //大盘指数 一行 
		stockRow++;
		ExcelCommon.writeExcelItemTitle(wb,sheet,"大盘指数",stockRow);
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
	   		
	   		//其他值
	   		StockOtherInfoValue soiValue=new StockOtherInfoValue(sMarket.getCode().toString(),sMarket.getName().toString(),0,0,null);
	   		ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow);
	   		//分析日
	   	//	stockLogger.logger.debug("*****分析日*****");
	   		StockExcelItem dayItem = getExcelItem(sMarket.getCode().toString(),ConstantsInfo.DayDataType,stockType); 
			ExcelCommon.writeExcelItem(wb,sheet,dayItem, stockRow, ConstantsInfo.DayDataType);
			if (dayItem == null)
				continue;
			//分析周预测值
	//		stockLogger.logger.debug("*****分析周*****");
			StockExcelItem weekItem = getExcelItem(sMarket.getCode().toString(),ConstantsInfo.WeekDataType,stockType);		
			ExcelCommon.writeExcelItem(wb,sheet,weekItem, stockRow, ConstantsInfo.WeekDataType);
			if (weekItem == null)
				continue;			
			//分析月预测值
	//		stockLogger.logger.debug("*****分析月*****");
			StockExcelItem monthItem = getExcelItem(sMarket.getCode().toString(),ConstantsInfo.MonthDataType,stockType);		
			ExcelCommon.writeExcelItem(wb,sheet,monthItem, stockRow, ConstantsInfo.MonthDataType);
			if (monthItem == null)
				continue;
			//统计
			StockExcelStatItem  statItem = new StockExcelStatItem(0,0,0,dayItem.getSsValue().getFlag(),weekItem.getSsValue().getFlag(),monthItem.getSsValue().getFlag(),
					dayItem.getSsValue().getPointTimes(),weekItem.getSsValue().getPointTimes(),monthItem.getSsValue().getPointTimes(),dayItem.getSsValue().getCurUpTimes(),
					weekItem.getSsValue().getCurUpTimes(),monthItem.getSsValue().getCurUpTimes(),dayItem.getSsValue().getCurDownTimes(),weekItem.getSsValue().getCurDownTimes(),monthItem.getSsValue().getCurDownTimes());
			ExcelCommon.writeExcelStatItem(wb,sheet,statItem, stockRow);
	   	}

	}
 	
 	//按行业生成excel 按周当前幅度排序
	public void writeExcelFormIndustryOrderBy(String filePath, String fileTime) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
		     
	 	List<StockIndustry> listIndustry = new ArrayList<StockIndustry>(); 
		 //得到当前所有行业
    	listIndustry=sbDao.getStockIndustry();
    	System.out.println("行业个数："+listIndustry.size());
    	XSSFWorkbook wb=null;
    	XSSFSheet sheet =null;
    	String excleFileName=null;
        //每个行业输出到excel一次
        for (int i=0;i<listIndustry.size();i++)	
   		{
        	
   			if(i==0 || stockRow >= 510) {
   				
   				//创建日期目录
   				File file = new File(filePath+fileTime);  
   				System.out.println(fileTime);
   				if (!file.exists())
   				{   
   					 file.mkdir();   
   				} 
   				
	   			// 工作区		
	   			wb = new XSSFWorkbook(); 
	   			// 创建第一个sheet     
	   			sheet=  wb.createSheet("allstock");		
	   			sheetCount++;
	   			excleFileName="Stock_Industry_"+fileTime+"_All_"+sheetCount+".xlsx";	   		    
	   		    stockRow = 3;
	   			//创建excel
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
			// 输入流   
			FileInputStream fileIStream = new FileInputStream(file);  
			
			 wb = new XSSFWorkbook(fileIStream);   
			 sheet = wb.getSheetAt(0);  
			// System.out.println("最后一行："+sheet.getLastRowNum());   
			
			StockExcelStatItem  statItem;   		
			
			//当前行业
			StockIndustry indu = listIndustry.get(i);	
			String induCode = indu.getThirdcode();
			String induName = indu.getThirdname();
			if(induCode == null || induName == null)
				continue;				
			stockLogger.logger.fatal("行业："+induName);   		
			System.out.println("行业："+induName);			
			//行业标题 
			stockRow++;
			ExcelCommon.writeExcelItemTitle(wb,sheet,i+":"+induName,stockRow);
   					 	
   			//所属行业所有股票
   			List<String> listIndustryStock = new ArrayList<String>();   
   			//listIndustryStock=sbDao.getIndustryStock(induName);	   	
   			listIndustryStock=sbDao.getIndustryStockFromAllInfo(induCode);
   			stockLogger.logger.debug("行业股票数："+listIndustryStock.size());
   			int stockType=0;
   			
   			//按周涨幅比排序
   			List<StockExcelTotalInfo> listStockTotalInfoOrderBy = new ArrayList<StockExcelTotalInfo>(); 
   			for(Iterator ie=listIndustryStock.iterator();ie.hasNext();)
   			{
   				//stockRow++;
   			
   				String stockFullId = (String) ie.next();	
   				System.out.println("stockFullId:"+stockFullId);
   				stockType=sbDao.getMarketType(stockFullId);
   				//if(!stockFullId.equals("SH601268"))
   				//	continue;
   				stockLogger.logger.fatal("****stockFullId："+stockFullId+"****");
   				
   				StockSingle ss= sbDao.lookUpStockSingle(stockFullId);
   				StockExcelItem dayItem;
				StockExcelItem weekItem;
				StockExcelItem monthItem;				
				
				int enableTingPai = getEnableTingPai(stockFullId);
				//基本面
				StockBaseFace baseFace = sbDao.lookUpStockBaseFace(stockFullId);
				//其他值
		   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,ss.getStockName(),ss.getEnableMarginTrading(),enableTingPai,baseFace);
		   		//ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow);
				
   				//分析日预测值
   				dayItem = getExcelItem(stockFullId,ConstantsInfo.DayDataType,stockType);   
   				if (dayItem == null){
   					stockLogger.logger.fatal("day point is null");
   					//continue;
   				} else {
   				//	ExcelCommon.writeExcelItem(wb,sheet,dayItem, stockRow, ConstantsInfo.DayDataType);
   				}
   				//分析周预测值
   				weekItem = getExcelItem(stockFullId,ConstantsInfo.WeekDataType,stockType);
   				if (weekItem == null){
   					stockLogger.logger.fatal("week point is null");
   					//continue; 
   				} else {
   				//	ExcelCommon.writeExcelItem(wb,sheet,weekItem, stockRow, ConstantsInfo.WeekDataType);
   				}
   				//分析月预测值
   				monthItem = getExcelItem(stockFullId,ConstantsInfo.MonthDataType,stockType);
   				if (monthItem == null){
   					stockLogger.logger.fatal("month point is null");
   					//continue; 
   				} else {
   				//	ExcelCommon.writeExcelItem(wb,sheet,monthItem,stockRow, ConstantsInfo.MonthDataType);
   				}
   				
   				//统计
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
   		
   			//排序
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
          //测试作用
			//if(stockRow>10)
			//	break;
   		}
   		
   		listIndustry = null;
	}
	
 
	//按行业生成excel 
	public void writeExcelFormIndustry(String filePath, String fileTime) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
		     
	 	List<StockIndustry> listIndustry = new ArrayList<StockIndustry>(); 
		 //得到当前所有行业
    	listIndustry=sbDao.getStockIndustry();
    	System.out.println("行业个数："+listIndustry.size());
    	XSSFWorkbook wb=null;
    	XSSFSheet sheet =null;
    	String excleFileName=null;
        //每个行业输出到excel一次
        for (int i=0;i<listIndustry.size();i++)	
   		{
        	
   			if(i==0 || stockRow >= 510) {
   				
   				//创建日期目录
   				File file = new File(filePath+fileTime);  
   				System.out.println(fileTime);
   				if (!file.exists())
   				{   
   					 file.mkdir();   
   				} 
   				
	   			// 工作区		
	   			wb = new XSSFWorkbook(); 
	   			// 创建第一个sheet     
	   			sheet=  wb.createSheet("allstock");		
	   			sheetCount++;
	   			excleFileName="Stock_Industry_"+fileTime+"_All_"+sheetCount+".xlsx";	   		    
	   		    stockRow = 3;
	   			//创建excel
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
			// 输入流   
			FileInputStream fileIStream = new FileInputStream(file);  
			
			 wb = new XSSFWorkbook(fileIStream);   
			 sheet = wb.getSheetAt(0);  
			// System.out.println("最后一行："+sheet.getLastRowNum());   
			
			StockExcelStatItem  statItem;   		
			
			//当前行业
			StockIndustry indu = listIndustry.get(i);	
			String induCode = indu.getThirdcode();
			String induName = indu.getThirdname();
			if(induCode == null || induName == null)
				continue;				
			stockLogger.logger.fatal("行业："+induName);   		
			System.out.println("行业："+induName);			
			//行业标题 
			stockRow++;
			ExcelCommon.writeExcelItemTitle(wb,sheet,i+":"+induName,stockRow);
   					 	
   			//所属行业所有股票
   			List<String> listIndustryStock = new ArrayList<String>();   
   			//listIndustryStock=sbDao.getIndustryStock(induName);	   	
   			listIndustryStock=sbDao.getIndustryStockFromAllInfo(induCode);
   			stockLogger.logger.debug("行业股票数："+listIndustryStock.size());
   			int stockType=0;
   			for(Iterator ie=listIndustryStock.iterator();ie.hasNext();)
   			{
   				stockRow++;
   			
   				String stockFullId = (String) ie.next();	
   				System.out.println("stockFullId:"+stockFullId);
   				stockType=sbDao.getMarketType(stockFullId);
   				//if(!stockFullId.equals("SH601268"))
   				//	continue;
   				stockLogger.logger.fatal("****stockFullId："+stockFullId+"****");
   				
   				StockSingle ss= sbDao.lookUpStockSingle(stockFullId);
   				StockExcelItem dayItem;
				StockExcelItem weekItem;
				StockExcelItem monthItem;
				
				
				int enableTingPai = getEnableTingPai(stockFullId);
				
				StockBaseFace baseFace = sbDao.lookUpStockBaseFace(stockFullId);
				//其他值
		   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,ss.getStockName(),ss.getEnableMarginTrading(),enableTingPai,baseFace);
		   		ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow);
				
   				//分析日预测值
   				dayItem = getExcelItem(stockFullId,ConstantsInfo.DayDataType,stockType);   
   				if (dayItem == null){
   					stockLogger.logger.fatal("day point is null");
   					//continue;
   				} else {
   					ExcelCommon.writeExcelItem(wb,sheet,dayItem, stockRow, ConstantsInfo.DayDataType);
   				}
   				//分析周预测值
   				weekItem = getExcelItem(stockFullId,ConstantsInfo.WeekDataType,stockType);
   				if (weekItem == null){
   					stockLogger.logger.fatal("week point is null");
   					//continue; 
   				} else {
   					ExcelCommon.writeExcelItem(wb,sheet,weekItem, stockRow, ConstantsInfo.WeekDataType);
   				}
   				//分析月预测值
   				monthItem = getExcelItem(stockFullId,ConstantsInfo.MonthDataType,stockType);
   				if (monthItem == null){
   					stockLogger.logger.fatal("month point is null");
   					//continue; 
   				} else {
   					ExcelCommon.writeExcelItem(wb,sheet,monthItem,stockRow, ConstantsInfo.MonthDataType);
   				}
   				
   				//统计
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
          //测试作用
		//	if(stockRow>20)
		//		break;
   		}
   		
   		listIndustry = null;
	}
	
	
	//按概念生成excel 
	public void writeExcelFormConcept(String filePath, String fileTime) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
		     
	 	List<StockConcept> listConcept = new ArrayList<StockConcept>(); 
    	//得到当前所有概念
    	listConcept=sbDao.getStockConcept();
    	System.out.println("概念个数："+listConcept.size());
    	XSSFWorkbook wb=null;
    	XSSFSheet sheet =null;
    	String excleFileName=null;
        //每个行业输出到excel一次
        for (int i=0;i<listConcept.size();i++)	
   		{
        	
   			if(i==0 || stockRow >= 510) {
   				
   				//创建日期目录
   				File file = new File(filePath+fileTime);  
   				System.out.println(fileTime);
   				if (!file.exists())
   				{   
   					 file.mkdir();   
   				} 
   				
	   			// 工作区		
	   			wb = new XSSFWorkbook(); 
	   			// 创建第一个sheet     
	   			sheet=  wb.createSheet("allstock");		
	   			sheetCount++;
	   			excleFileName="Stock_Concept_"+fileTime+"_All_"+sheetCount+".xlsx";	   		    
	   		    stockRow = 3;
	   			//创建excel
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
			// 输入流   
			FileInputStream fileIStream = new FileInputStream(file);  
			
			 wb = new XSSFWorkbook(fileIStream);   
			 sheet = wb.getSheetAt(0);  
			// System.out.println("最后一行："+sheet.getLastRowNum());   
			
			StockExcelStatItem  statItem;   		
			
			//当前概念
			 StockConcept concept= listConcept.get(i);	
			 String conceptName=concept.getName();
			 if(conceptName==null)
				 continue;
			 
			stockLogger.logger.fatal("概念："+conceptName);
			System.out.println("概念："+conceptName);	 			
  	
			//概念标题 
			stockRow++;
			ExcelCommon.writeExcelItemTitle(wb,sheet,i+":"+conceptName,stockRow);
   					 	
   			//所属概念所有股票
   			List<String> listConceptStock = new ArrayList<String>();     	
   			listConceptStock=sbDao.getConceptStock(conceptName);
   			stockLogger.logger.debug("概念股票数："+listConceptStock.size());
   			
   			//按周涨幅比排序
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
   				stockLogger.logger.fatal("****stockFullId："+stockFullId+"****");
   				
   				StockSingle ss= sbDao.lookUpStockSingle(stockFullId);
   				if(ss == null)
   					continue;
   				StockExcelItem dayItem;
				StockExcelItem weekItem;
				StockExcelItem monthItem;
				
				int enableTingPai = getEnableTingPai(stockFullId);
				StockBaseFace baseFace = sbDao.lookUpStockBaseFace(stockFullId);
				//其他值
		   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,ss.getStockName(),ss.getEnableMarginTrading(),enableTingPai,baseFace);
		   		ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow);
				
   				//分析日预测值
   				dayItem = getExcelItem(stockFullId,ConstantsInfo.DayDataType,stockType);   
   				if (dayItem == null){
   					stockLogger.logger.fatal("day point is null");
   					//continue;
   				} else {
   					ExcelCommon.writeExcelItem(wb,sheet,dayItem, stockRow, ConstantsInfo.DayDataType);
   				}
   				//分析周预测值
   				weekItem = getExcelItem(stockFullId,ConstantsInfo.WeekDataType,stockType);
   				if (weekItem == null){
   					stockLogger.logger.fatal("week point is null");
   					//continue; 
   				} else {
   					ExcelCommon.writeExcelItem(wb,sheet,weekItem, stockRow, ConstantsInfo.WeekDataType);
   				}
   				//分析月预测值
   				monthItem = getExcelItem(stockFullId,ConstantsInfo.MonthDataType,stockType);
   				if (monthItem == null){
   					stockLogger.logger.fatal("month point is null");
   					//continue; 
   				} else {
   					ExcelCommon.writeExcelItem(wb,sheet,monthItem,stockRow, ConstantsInfo.MonthDataType);
   				}
   				
   				//统计
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
          //测试作用
			//if(stockRow>20)
			//	break;
   		}
   		
        listConcept = null;
	}
	
	
	//按概念生成excel orderby
	public void writeExcelFormConceptOrderBy(String filePath, String fileTime) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
		     
	 	List<StockConcept> listConcept = new ArrayList<StockConcept>(); 
    	//得到当前所有概念
    	listConcept=sbDao.getStockConcept();
    	System.out.println("概念个数："+listConcept.size());
    	XSSFWorkbook wb=null;
    	XSSFSheet sheet =null;
    	String excleFileName=null;
        //每个行业输出到excel一次
        for (int i=0;i<listConcept.size();i++)	
   		{
        	
   			if(i==0 || stockRow >= 510) {
   				
   				//创建日期目录
   				File file = new File(filePath+fileTime);  
   				System.out.println(fileTime);
   				if (!file.exists())
   				{   
   					 file.mkdir();   
   				} 
   				
	   			// 工作区		
	   			wb = new XSSFWorkbook(); 
	   			// 创建第一个sheet     
	   			sheet=  wb.createSheet("allstock");		
	   			sheetCount++;
	   			excleFileName="Stock_Concept_"+fileTime+"_All_"+sheetCount+".xlsx";	   		    
	   		    stockRow = 3;
	   			//创建excel
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
			// 输入流   
			FileInputStream fileIStream = new FileInputStream(file);  
			
			 wb = new XSSFWorkbook(fileIStream);   
			 sheet = wb.getSheetAt(0);  
			// System.out.println("最后一行："+sheet.getLastRowNum());   
			
			StockExcelStatItem  statItem;   		
			
			//当前概念
			 StockConcept concept= listConcept.get(i);	
			 String conceptName=concept.getName();
			 if(conceptName==null)
				 continue;
			 
			stockLogger.logger.fatal("概念："+conceptName);
			System.out.println("概念："+conceptName);	 			
  	
			//概念标题 
			stockRow++;
			ExcelCommon.writeExcelItemTitle(wb,sheet,i+":"+conceptName,stockRow);
   					 	
   			//所属概念所有股票
   			List<String> listConceptStock = new ArrayList<String>();     	
   			listConceptStock=sbDao.getConceptStock(conceptName);
   			stockLogger.logger.debug("概念股票数："+listConceptStock.size());
   			
   		//按周涨幅比排序
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
   				stockLogger.logger.fatal("****stockFullId："+stockFullId+"****");
   				
   				StockSingle ss= sbDao.lookUpStockSingle(stockFullId);
   				if(ss == null)
   					continue;
   				StockExcelItem dayItem;
				StockExcelItem weekItem;
				StockExcelItem monthItem;
				
				int enableTingPai = getEnableTingPai(stockFullId);
				
				//基本面
				StockBaseFace baseFace = sbDao.lookUpStockBaseFace(stockFullId);
				
				//其他值
		   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,ss.getStockName(),ss.getEnableMarginTrading(),enableTingPai,baseFace);
		   		//ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow);
				
   				//分析日预测值
   				dayItem = getExcelItem(stockFullId,ConstantsInfo.DayDataType,stockType);   
   				if (dayItem == null){
   					stockLogger.logger.fatal("day point is null");
   					//continue;
   				} else {
   					//ExcelCommon.writeExcelItem(wb,sheet,dayItem, stockRow, ConstantsInfo.DayDataType);
   				}
   				//分析周预测值
   				weekItem = getExcelItem(stockFullId,ConstantsInfo.WeekDataType,stockType);
   				if (weekItem == null){
   					stockLogger.logger.fatal("week point is null");
   					//continue; 
   				} else {
   					//ExcelCommon.writeExcelItem(wb,sheet,weekItem, stockRow, ConstantsInfo.WeekDataType);
   				}
   				//分析月预测值
   				monthItem = getExcelItem(stockFullId,ConstantsInfo.MonthDataType,stockType);
   				if (monthItem == null){
   					stockLogger.logger.fatal("month point is null");
   					//continue; 
   				} else {
   					//ExcelCommon.writeExcelItem(wb,sheet,monthItem,stockRow, ConstantsInfo.MonthDataType);
   				}
   				
   				//统计
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
   			
   			//排序
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
          //测试作用
		//	if(stockRow>30)
		//		break;
   		}
   		
        listConcept = null;
	}
	
	
	//按概念在一级行业下生成excel orderby
	public void writeExcelFormConceptInFirstIndustryOrderBy(String filePath, String fileTime) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
		     
	 	List<String> listConcept = new ArrayList<String>(); 
	 	List<String> listFirstIndustry = new ArrayList<String>(); 
    	//得到当前一级行业code
	 	listFirstIndustry=sbDao.getStockFirstIndustry();    	
    	
    	XSSFWorkbook wb=null;
    	XSSFSheet sheet =null;
    	String excleFileName=null;
        int flag_first=0;
    	//先输出到一级行业
        for (int countI=0; countI<listFirstIndustry.size();countI++)	
   		{
    	
        	//得到当前一级行业下概念code
    	 	listFirstIndustry=sbDao.getStockFirstIndustry();
        	String firstIndustryId = listFirstIndustry.get(countI);
        	
        	//一级行业名称
        	String firstIndustryName = sbDao.getStockFirstIndustryName(firstIndustryId);        	
        	System.out.println(firstIndustryName);        	
        	
        	//得到概念id
        	listConcept = sbDao.getStockFirstIndustryConceptCode1(firstIndustryId);
        	
	    	//每个行业输出到excel一次
	        for (int i=0;i<listConcept.size();i++)	
	   		{
	        	//countI第一次	        	
	   			if(flag_first == 0 || stockRow >= 510) {
	   				
	   				flag_first =1;
	   				//创建日期目录
	   				File file = new File(filePath+fileTime);  
	   				System.out.println(fileTime);
	   				if (!file.exists())
	   				{   
	   					 file.mkdir();   
	   				} 
	   				
		   			// 工作区		
		   			wb = new XSSFWorkbook(); 
		   			// 创建第一个sheet     
		   			sheet=  wb.createSheet("allstock");		
		   			sheetCount++;
		   			excleFileName="Stock_Concept_"+fileTime+"_All_"+sheetCount+".xlsx";	   		    
		   		    stockRow = 3;
		   			//创建excel
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
				// 输入流   
				FileInputStream fileIStream = new FileInputStream(file);  
				
				 wb = new XSSFWorkbook(fileIStream);   
				 sheet = wb.getSheetAt(0);  
				// System.out.println("最后一行："+sheet.getLastRowNum());   
				
				StockExcelStatItem  statItem;   		
				
				//当前一级行业
				 String conceptCode= listConcept.get(i);	
				 String conceptName=sbDao.getConceptNameFromFirstIndeustyToConceptTable(conceptCode);
				 if(conceptName==null)
					 continue;
				 
				stockLogger.logger.fatal("概念："+conceptName);
				System.out.println("概念："+conceptName);	 			
	  	
				//概念标题 
				stockRow++;
				ExcelCommon.writeExcelItemTitle(wb,sheet,i+":"+firstIndustryName+"-"+conceptName,stockRow);
	   					 	
	   			//所属概念所有股票
				//所属概念所有股票
	   			List<StockToConcept> listConceptStock = new ArrayList<StockToConcept>();
	   			listConceptStock=sbDao.getStockToConcept(conceptCode);
	   			stockLogger.logger.debug("概念股票数："+listConceptStock.size());
	   			
	   			//按周涨幅比排序
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
	   				stockLogger.logger.fatal("****stockFullId："+stockFullId+"****");
	   				
	   				StockSingle ss= sbDao.lookUpStockSingle(stockFullId);
	   				if(ss == null)
	   					continue;
	   				StockExcelItem dayItem;
					StockExcelItem weekItem;
					StockExcelItem monthItem;
					
					int enableTingPai = getEnableTingPai(stockFullId);
					
					//基本面
					StockBaseFace baseFace = sbDao.lookUpStockBaseFace(stockFullId);
					
					//其他值
			   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,ss.getStockName(),ss.getEnableMarginTrading(),enableTingPai,baseFace);
			   		//ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow);
					
	   				//分析日预测值
	   				dayItem = getExcelItem(stockFullId,ConstantsInfo.DayDataType,stockType);   
	   				if (dayItem == null){
	   					stockLogger.logger.fatal("day point is null");
	   					//continue;
	   				} else {
	   					//ExcelCommon.writeExcelItem(wb,sheet,dayItem, stockRow, ConstantsInfo.DayDataType);
	   				}
	   				//分析周预测值
	   				weekItem = getExcelItem(stockFullId,ConstantsInfo.WeekDataType,stockType);
	   				if (weekItem == null){
	   					stockLogger.logger.fatal("week point is null");
	   					//continue; 
	   				} else {
	   					//ExcelCommon.writeExcelItem(wb,sheet,weekItem, stockRow, ConstantsInfo.WeekDataType);
	   				}
	   				//分析月预测值
	   				monthItem = getExcelItem(stockFullId,ConstantsInfo.MonthDataType,stockType);
	   				if (monthItem == null){
	   					stockLogger.logger.fatal("month point is null");
	   					//continue; 
	   				} else {
	   					//ExcelCommon.writeExcelItem(wb,sheet,monthItem,stockRow, ConstantsInfo.MonthDataType);
	   				}
	   				
	   				//统计
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
	   			
	   			//排序
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
	          
				 //测试作用
				if(stockRow>10)
					break;				
	   		}
	        
	      //测试作用
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
		
		//排序
		//se.writeExcelFormIndustryOrderBy("export\\",dateNowStr);
		se.writeExcelFormConceptInFirstIndustryOrderBy("export\\",dateNowStr);
		
		//se.writeExcelFormIndustry("export\\",dateNowStr);
		//se.writeExcelFormConcept("export\\",dateNowStr);
		
		stockBaseConn.close();
	    stockDataConn.close();
	    stockPointConn.close();
		
		Date endDate = new Date();
		// 计算两个时间点相差的秒数
		long seconds =(endDate.getTime() - startDate.getTime())/1000;
		System.out.println("总共耗时："+seconds+"秒");
		System.out.println("end");
		stockLogger.logger.fatal("excel stock export end");		
	}

}
