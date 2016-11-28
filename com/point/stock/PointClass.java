package com.point.stock;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;

import com.timer.stock.DateStock;
import com.timer.stock.StockDateTimer;

import common.ConstantsInfo;
import common.stockLogger;

import dao.DayStock;
import dao.DbConn;
import dao.StockBaseDao;
import dao.StockData;
import dao.StockDataDao;
import dao.StockInformationDao;
import dao.StockPoint;
import dao.StockPointDao;

public class PointClass {
	
	private StockDataDao sdDao;
	private StockBaseDao sbDao;
	private StockPointDao spDao;
	static StockInformationDao siDao =new StockInformationDao();
	
	
	public PointClass()
	{
		
	}
	public PointClass(Connection stockBaseConn,Connection stockDataConn,Connection stockPointConn)
	{
		this.sbDao = new StockBaseDao(stockBaseConn);
		this.sdDao =new StockDataDao(stockDataConn);
		this.spDao =new StockPointDao(stockPointConn);
	}
	 public PointClass(StockBaseDao sbDao,StockDataDao sdDao,StockPointDao spDao)
	{
		this.sbDao = sbDao;
		this.sdDao = sdDao;
		this.spDao = spDao;
	}
	
	
	/**
	 * @param fullId
	 * @param dStock
	 * @param stockType �����գ��ܣ�������
	 * @param calType �������ͣ���ʷ�͵�ǰ
	 * @return
	 * @throws SecurityException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 */
	public int getStockExtremePoint(String fullId,DateStock dStock,int stockType, int calType) throws SecurityException, IOException, ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException
	{

		float md5Pri = 0,md10Pri,md5Next,md10Next;	
		
		String strPriDate,strNextDate,curDate = null;
		String pointDate = null;//�����
		boolean priceWillFall,priceWillRise;
		String pointStartDate = null,pointEndDate = null;
		String pointTrueStartDate= null;
		String pointLastExtremeDate=null;
		int getStartflag=0;
		StockData sData;
		float extremePrice = 0;
		String extremeDate = null;
		int flagRiseFall=0;
		int index=0;
		StockPoint sp;		
		float extremeBeforePrice=0;
		float extremeCurPrice=0;
		StockPoint lastSp;
		float ratioTmp,ratio=0;
		List<String> dayDate=null;
		int timeStart=1;
		int daySize = 0;
		
		switch (stockType)
		{
		case ConstantsInfo.DayDataType:
			dayDate=dStock.getDayDate();
			break;
		case ConstantsInfo.WeekDataType:
			dayDate=dStock.getWeekDate();				
			break;
		case ConstantsInfo.MonthDataType:
			dayDate=dStock.getMonthDate();
			break;			
		}
		daySize=dayDate.size();
		
		if(daySize <=1) {
			stockLogger.logger.fatal("day size less than 1");
			return -1;		
		}

		String pointCurDateStart=null;//���㵱ǰ����ʱ��ʼʱ��
		switch(calType)
		{
		case ConstantsInfo.StockCalAllData:			
			
			//������ʷ����
			//for(int dayIndex=0;dayIndex<daySize;dayIndex++)
			/*
			for(index=0;index<daySize-1;index++)
			{
				if(dayDate.get(index).equals("2010-06-02"))
				{
					dayStart=index;
					break;
				}
			}
			*/
			timeStart = 1;
			extremeCurPrice=0;
			break;
		case ConstantsInfo.StockCalCurData:
			//��ѯ���һ����¼
			lastSp=spDao.getLastPointStock(fullId,stockType);
			if(lastSp==null) { //���¼��㣬�Ƿ�����µļ���
				stockLogger.logger.fatal("no point data");
				System.out.println("no point data");
				timeStart = 1;
				extremeCurPrice=0;
			} else {
				System.out.println("last start date:"+lastSp.getFromDate());
				System.out.println("last end date:"+lastSp.getToDate());
			//	dayStart=dayDate.indexOf(lastSp.getExtremeDate());
				System.out.println("extreme end date:"+lastSp.getExtremeDate());
				pointLastExtremeDate = lastSp.getExtremeDate().toString();//��ֵ
				timeStart=dayDate.indexOf(""+lastSp.getFromDate()+"");
				pointCurDateStart=dayDate.get(timeStart);//��ֵ
				
				extremeCurPrice=lastSp.getExtremePrice();
			}
			break;
		}
		
		pointDate=dayDate.get(timeStart);//��ֵ
		System.out.println("StartDate:"+pointDate);
		stockLogger.logger.debug("StartDate::"+pointDate);
		for(index=timeStart;index<daySize-1;index++)
		{
			strPriDate=dayDate.get(index-1);
			curDate=dayDate.get(index);
			strNextDate=dayDate.get(index+1);
			md5Pri=sdDao.getStockMaData(fullId,strPriDate,5,stockType);
			md10Pri=sdDao.getStockMaData(fullId,strPriDate,10,stockType);
			md5Next=sdDao.getStockMaData(fullId,strNextDate,5,stockType);
			md10Next=sdDao.getStockMaData(fullId,strNextDate,10,stockType);
			stockLogger.logger.debug("curDate:"+curDate);
			stockLogger.logger.debug("md5Pri:"+md5Pri+"md10Pri:"+md10Pri);
			stockLogger.logger.debug("md5Next:"+md5Next+"md10Next:"+md10Next);
		//	stockLogger.logger.debug("curDate:"+pointDate);
			if(md5Pri==0 || md10Pri==0 || md5Next==0 || md10Next==0)
			{
				continue;
			}
			priceWillFall = (md5Pri>=md10Pri) && (md5Next<=md10Next);
			priceWillRise = (md5Pri<=md10Pri) && (md5Next>=md10Next);
			
			if((md5Pri>=md10Pri && md5Next<=md10Next) || (md5Pri<=md10Pri && md5Next>=md10Next))
			{
				
				//ǰ����������ĵ� �����ǰ����֮ǰ��һ�£�ȡǰһ����
				if(pointDate.equals(strPriDate)) //
				{
					stockLogger.logger.debug("pointDate continue:"+pointDate);
					continue;
				}
				pointDate=curDate;	//���µ�point��
			
				
				//��һ����
				/*
				if(!pointDate.equals(strPriDate))
				{
					stockLogger.logger.debug("pointDate continue:"+pointDate);
					pointDate = curDate;	//����֮ǰpoint��	
					continue;
				}
				pointDate = curDate;	//���µ�point��	
				*/
					
				stockLogger.logger.debug("pointDate:"+pointDate);
				
				switch(getStartflag)
				{
					case 2:
					default:					
						pointStartDate=pointEndDate;
						pointEndDate=pointDate;
						break;
					case 1:
						pointEndDate=curDate;//�ڶ���
						getStartflag++;
						//System.out.println("pointEndDate:"+curDate);
						break;
					case 0:
						pointStartDate=curDate;//��һ��
						getStartflag++;	
						//System.out.println("pointStartDate:"+curDate);
						break;
				}						
				
				if(getStartflag==2)
				{
					//System.out.println("priceWillFall:"+priceWillFall+"priceWillRise:"+priceWillRise);
					//System.out.println("pointStartDate:"+pointStartDate+"pointEndDate:"+pointEndDate);
					
					switch (stockType)
					{
					case ConstantsInfo.DayDataType:
						pointTrueStartDate=pointStartDate;
						break;
					case ConstantsInfo.WeekDataType:
						pointTrueStartDate=sdDao.getPointStartDate(fullId,pointStartDate,ConstantsInfo.WeekDataType);				
						break;
					case ConstantsInfo.MonthDataType:
						pointTrueStartDate=sdDao.getPointStartDate(fullId,pointStartDate,ConstantsInfo.MonthDataType);
						break;
					}
					
					stockLogger.logger.debug("pointStartDate:"+pointTrueStartDate+"pointEndDate:"+pointEndDate);
					if(priceWillFall)
					{
						
						sData=sdDao.getMaxStockDataPoint(fullId,pointTrueStartDate,pointEndDate,stockType);					    		
						if(sData==null)
			    			continue;
						extremePrice=sData.getHighestPrice();
			    		extremeDate=sData.getDate().toString();		    		
			    		flagRiseFall=1;
					}							
					else if(priceWillRise)
					{
						sData=sdDao.getMinStockDataPoint(fullId,pointTrueStartDate,pointEndDate,stockType);				
			    		extremePrice=sData.getLowestPrice();
			    		extremeDate=sData.getDate().toString();			    				    		
			    		flagRiseFall=0;
					}	
				
					//ԭ����۸�
					extremeBeforePrice=extremeCurPrice;
					//��ǰ����۸�
					extremeCurPrice=extremePrice;
					ratioTmp=(extremeCurPrice-extremeBeforePrice)*100/extremeCurPrice;
					ratio = (float)(Math.round(ratioTmp*100))/100;
					
					//������㵱ǰ���ݣ���һ�β���Ҫ���
					if(calType == ConstantsInfo.StockCalCurData && extremeDate.equals(pointLastExtremeDate))
						continue;
					
					sp=new StockPoint(0,stockType,Date.valueOf(extremeDate),extremePrice,Date.valueOf(pointStartDate),Date.valueOf(pointEndDate),flagRiseFall,ratio,0);
					stockLogger.logger.debug("extremeDate:"+extremeDate);
					stockLogger.logger.debug("extremePrice:"+extremePrice);
					stockLogger.logger.debug("pointStartDate:"+pointStartDate);
					stockLogger.logger.debug("pointEndDate:"+pointEndDate);					
					stockLogger.logger.debug("ratio:"+ratio);
					System.out.println("insert day point date:"+extremeDate);
					spDao.insertPointStockTable(sp,fullId);
									
				}	
						
			}	
		}		
		return 0;
	}
	
	
	//�����㷨ȡ���㣬 ͬʱ����㿪ʼʱ��Ϊǰһ����ʱ��
	public int getStockExtremePointValue(String fullId,DateStock dStock,int stockType, int calType) throws SecurityException, IOException, ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException
	{

		float md5Pri = 0,md10Pri,md5Cur,md10Cur;	
		
		String strPriDate,curDate = null;
		String pointDate = null;//�����
		String firstDate = null;//��ʼֵ
		String pointStartDate = null,pointEndDate = null;
		String pointTrueStartDate= null;
		String pointLastExtremeDate=null;
		int getStartflag=0;
		StockData sData;
		float extremePrice = 0;
		String extremeDate = null;
		int flagRiseFall=0;
		int index=0;
		StockPoint sp;		
		float extremeBeforePrice=0;
		float extremeCurPrice=0;
		StockPoint lastSp;
		float ratioTmp,ratio=0;
		List<String> dayDate=null;
		int timeStart=1; //�����ʼλ��
		int daySize = 0;
		StockDateTimer sdateTimer=new StockDateTimer();
		
		switch (stockType)
		{
		case ConstantsInfo.DayDataType:
			dayDate=dStock.getDayDate();
			break;
		case ConstantsInfo.WeekDataType:
			dayDate=dStock.getWeekDate();				
			break;
		case ConstantsInfo.MonthDataType:
			dayDate=dStock.getMonthDate();
			break;			
		}
		daySize=dayDate.size();
		System.out.println("daySize:"+daySize);
		if(daySize <=1) {
			stockLogger.logger.fatal("day size less than 1");
			return -1;		
		}

		switch(calType)
		{
		case ConstantsInfo.StockCalAllData:			
			timeStart = 1;
			extremeCurPrice=0;
			break;
		case ConstantsInfo.StockCalCurData:
			//��ѯ���һ����¼
			lastSp=spDao.getLastPointStock(fullId,stockType);
			if(lastSp==null) { //���¼��㣬�Ƿ�����µļ���
				//stockLogger.logger.fatal("no point data");
				System.out.println("no point data");
				timeStart = 1;
				extremeCurPrice=0;
			} else {
				//System.out.println("last start date:"+lastSp.getFromDate());
			//	System.out.println("last end date:"+lastSp.getToDate());
				//dayStart=dayDate.indexOf(lastSp.getExtremeDate());
				System.out.println("extreme end date:"+lastSp.getExtremeDate());
				//pointLastExtremeDate = lastSp.getExtremeDate().toString();//��ֵ
				
				if(stockType == ConstantsInfo.DayDataType){
					timeStart=dayDate.indexOf(""+lastSp.getToDate()+""); //��ֵ	
					extremeCurPrice = lastSp.getExtremePrice();
				} else if(stockType == ConstantsInfo.WeekDataType) {
					
					//StockData sdata = sdDao.getLastDataStock(fullId,ConstantsInfo.WeekDataType);
					String spDate = lastSp.getToDate().toString();
					
					StockData sdata = sdDao.getZhiDingDataStock(fullId,ConstantsInfo.WeekDataType,lastSp.getToDate().toString());
					String sdDate = sdata.getDate().toString();
			       
			     //   System.out.println("spdata date:"+spDate);
			     //   System.out.println("sdata date:"+sdata.getDate().toString());
			        
			        //��ʱ���Ѿ�����
					if(!spDate.equals(sdDate)) {
						//ɾ�����������¼��㣬��һ�²��ǽ����
						spDao.delStockPointData(fullId,lastSp.getId());
						//��ȡ������һ��
						lastSp=spDao.getLastPointStock(fullId,stockType);
						if(lastSp==null) { //���¼��㣬�Ƿ�����µļ���						
							timeStart = 1;
							extremeCurPrice=0;
						} else {
							timeStart = dayDate.indexOf(""+lastSp.getToDate().toString()+""); //��ȡ�����ڶ�������ʱ���ֵ
							extremeCurPrice = lastSp.getExtremePrice();
						}
					} else {
						timeStart=dayDate.indexOf(""+lastSp.getToDate()+""); //��ֵ	
					}
					
				} else if(stockType == ConstantsInfo.MonthDataType) {
				
			        String spDate = lastSp.getToDate().toString();
					
					StockData sdata1 = sdDao.getZhiDingDataStock(fullId,ConstantsInfo.MonthDataType,lastSp.getToDate().toString());
					String sdDate = sdata1.getDate().toString();
			        
			        //�����ǵ���
					if(!spDate.equals(sdDate)) {
						//ɾ�����������¼��㣬��һ�²��ǽ����
						spDao.delStockPointData(fullId,lastSp.getId());
						//��ȡ������һ��
						lastSp=spDao.getLastPointStock(fullId,stockType);
						if(lastSp==null) { //���¼��㣬�Ƿ�����µļ���						
							timeStart = 1;
							extremeCurPrice=0;
						} else {
							timeStart = dayDate.indexOf(""+lastSp.getToDate().toString()+""); //��ȡ�����ڶ�������ʱ���ֵ
							extremeCurPrice = lastSp.getExtremePrice();
						}
					} else {
						timeStart = dayDate.indexOf(""+lastSp.getToDate()+""); //��ֵ	
						extremeCurPrice = lastSp.getExtremePrice();
					}
				}
							
			}
			break;
		}
		
		System.out.println(timeStart);
		
		firstDate=dayDate.get(timeStart);//���㵱ǰ����ʱ��ʼʱ��
	//	System.out.println("StartDate:"+firstDate);
		stockLogger.logger.debug("StartDate::"+firstDate);
		float pointFlag =0;
		float priFlag =0;
		int curFlag=0;
		int priceFlag=0;
		String dateZero="";//ma5=ma10�ȱ���ʱ��
		int pointZeroFlag=0;//ma5=ma10�ȱ���״̬�����ǻ����µ�
		float tmpValue=0;
		
		//timeStart+1   //��ǰһ������ ��һ����
		for(index=timeStart+1;index<daySize;index++)
		{
			strPriDate=dayDate.get(index-1);
			curDate=dayDate.get(index);
			
			md5Pri=sdDao.getStockMaData(fullId,strPriDate,5,stockType);
			md10Pri=sdDao.getStockMaData(fullId,strPriDate,10,stockType);
			md5Cur=sdDao.getStockMaData(fullId,curDate,5,stockType);
			md10Cur=sdDao.getStockMaData(fullId,curDate,10,stockType);
			stockLogger.logger.debug("curDate:"+curDate);
			stockLogger.logger.debug("md5Pri:"+md5Pri+"md10Pri:"+md10Pri);
			stockLogger.logger.debug("md5Cur:"+md5Cur+"md10Cur:"+md10Cur);
		//	stockLogger.logger.debug("curDate:"+pointDate);
			if(md5Pri==0 || md10Pri==0 || md5Cur==0 || md10Cur==0)
			{
				continue;
			}
			//md5Pri- md10Pri>0 ����md5Cur-md10Cur)/(md5Pri- md10Pri
			priFlag = md5Pri- md10Pri;
			//pointFlag = (md5Cur-md5Pri)/(md10Cur- md10Pri);
			if(priFlag == 0)
				pointFlag = 0;
			else 
				pointFlag = (md5Cur-md10Cur)/(md5Pri- md10Pri);
			//System.out.println("curDate:"+curDate);
			//System.out.println("priFlag��"+priFlag);
			//System.out.println("pointFlag��"+pointFlag);
			if(priFlag  == 0) {
				if(pointFlag == 0) {//ǰһ��ma5=ma10 ,����
					tmpValue = md5Cur - md10Cur;
					if(tmpValue > 0) //ȷ�ϵ�ǰ״̬ 
						curFlag = 1;  //����
					else if(tmpValue < 0)
						curFlag = 0;  //�µ�
					else
						continue; //�������
					
				//	System.out.println("curFlag��"+curFlag);
				//	System.out.println("flagRiseFall��"+flagRiseFall);
					if(curFlag != pointZeroFlag){ //���ϴ�״̬һ��
						continue;
					} else {
						priceFlag = curFlag;//���ϴ�״̬��һ��*/
						if(!dateZero.equals(""))
							curDate = dateZero;
						//System.out.println("333��"+dateZero);
					}
				} 
			} else if (priFlag>0) {   //�µ�����
				if(pointFlag==0){ //��ǰma5=ma10 ,����
					dateZero = curDate;
					pointZeroFlag = 0;//�µ�
					//System.out.println("1111��"+dateZero);
					continue;
				} else if(pointFlag<0)
					priceFlag = 0;//�µ�
				else
					priceFlag = 2;//
			} else {
				if(pointFlag==0){ //��ǰma5=ma10   //��������
					dateZero = curDate;//����ʱ��
					pointZeroFlag = 1;//��������
					//System.out.println("2222��"+dateZero);
					continue;
				}else if(pointFlag<0)
					priceFlag = 1;//����
				else
					priceFlag = 2;//
			}
			
			if(priceFlag==0 || priceFlag == 1)
			{
				stockLogger.logger.debug("priceWillFall:"+priceFlag);
				pointDate=curDate;	//���½����
			
				stockLogger.logger.debug("***pointDate**:"+pointDate);	
			//	System.out.println("***pointDate**:"+pointDate);
				switch(getStartflag)
				{
					case 2:
					default:					
						pointStartDate=pointEndDate;
						pointEndDate=pointDate;
						break;
						/*
					case 1:
						pointEndDate=curDate;//�ڶ���
						getStartflag++;
						//System.out.println("pointEndDate:"+curDate);
						break;
						*/
					case 0:
						
						//pointStartDate=curDate;//��һ��
						//getStartflag++;	
						
						pointStartDate=firstDate;
						pointEndDate=pointDate;
						getStartflag=getStartflag+2;	
						//System.out.println("pointStartDate:"+curDate);
						break;
				}						
				
				if(getStartflag==2)
				{
			
					//�������һ������
					lastSp=spDao.getLastPointStock(fullId,stockType);
					if(lastSp==null) { //���¼��㣬�Ƿ�����µļ���			
					//	System.out.println("no point data");	
						pointTrueStartDate=pointStartDate;
					} else {					
						pointLastExtremeDate = lastSp.getExtremeDate().toString();//��ֵ
						pointTrueStartDate = pointLastExtremeDate;
					}
	
					/*
					switch (stockType)
					{
					case ConstantsInfo.DayDataType:
						pointTrueStartDate=pointStartDate;
						break;
					case ConstantsInfo.WeekDataType:
						pointTrueStartDate=sdDao.getPointStartDate(fullId,pointStartDate,ConstantsInfo.WeekDataType);				
						break;
					case ConstantsInfo.MonthDataType:
						pointTrueStartDate=sdDao.getPointStartDate(fullId,pointStartDate,ConstantsInfo.MonthDataType);
						break;
					}
					*/
									
					stockLogger.logger.debug("*pointStartDate:"+pointTrueStartDate+"pointEndDate:"+pointEndDate);
					//if(priceWillFall)
					if(priceFlag == 0)//��ǰ���µ�������ǰһ��������
					{
						sData=sdDao.getMaxStockDataPoint(fullId,pointTrueStartDate,pointEndDate,stockType);					    		
						if(sData==null)
			    			continue;
						extremePrice=sData.getHighestPrice();
			    		extremeDate=sData.getDate().toString();		    		
			    		flagRiseFall=1;
					}							
					else //if(priceWillRise) //�µ�
					{
						sData=sdDao.getMinStockDataPoint(fullId,pointTrueStartDate,pointEndDate,stockType);				
						if(sData==null)
			    			continue;
						extremePrice=sData.getLowestPrice();
			    		extremeDate=sData.getDate().toString();			    				    		
			    		flagRiseFall=0;
					}	
				
					//ԭ����۸�
					extremeBeforePrice=extremeCurPrice;
					//��ǰ����۸�
					extremeCurPrice=extremePrice;
					ratioTmp=(extremeCurPrice-extremeBeforePrice)*100/extremeCurPrice;
					ratio = (float)(Math.round(ratioTmp*100))/100;
					
					//������㵱ǰ���ݣ���һ�β���Ҫ���
					if(calType == ConstantsInfo.StockCalCurData && extremeDate.equals(pointLastExtremeDate))
						continue;
					
					stockLogger.logger.debug("extremeDate:"+extremeDate);
					stockLogger.logger.debug("extremePrice:"+extremePrice);
					stockLogger.logger.debug("pointStartDate:"+pointStartDate);
					stockLogger.logger.debug("pointEndDate:"+pointEndDate);					
					stockLogger.logger.debug("ratio:"+ratio);
					//System.out.println("flag:"+flagRiseFall);
					System.out.println("insert day point date:"+extremeDate);
					//sp=new StockPoint(stockType,Date.valueOf(extremeDate),extremePrice,Date.valueOf(pointStartDate),Date.valueOf(pointEndDate),priceFlag,ratio,0);
					sp=new StockPoint(0,stockType,Date.valueOf(extremeDate),extremePrice,Date.valueOf(pointTrueStartDate),Date.valueOf(pointEndDate),flagRiseFall,ratio,0);
					
					spDao.insertPointStockTable(sp,fullId);
									
				}	
						
			}	
		}		
		return 0;
	}
	
	
	public List<StockPoint> getStockDayExtremePoint(String fullId,DateStock dStock,int type) throws SecurityException, IOException, ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException
	{
		List<StockPoint> listStockPoint = new ArrayList<StockPoint>();
		float md5Pri = 0,md10Pri,md5Next,md10Next;
		int daySize=dStock.getDayDate().size();
		
		String strPriDate,strNextDate,curDate,pointDate = null;
		boolean priceWillFall,priceWillRise;
		String pointStartDate = null,pointEndDate = null;
		int pointIndex=0;
		int getStartflag=0;
		StockData sData;
		float extremePrice = 0;
		String extremeDate = null;
		int flagRiseFall=0;
		int index=0;
		StockPoint sp;		
		float extremeBeforePrice=0;
		float extremeCurPrice=0;
		StockPoint lastSp;
		List<String> dayDate=null;
		int dayStart=1;
		dayDate=dStock.getDayDate();
		
	//	System.out.println("dayLast:"+dayDate.get(daySize-1));
		
		String pointCurDateStart=null;//���㵱ǰ����ʱ��ʼʱ��
		switch(type)
		{
		case ConstantsInfo.StockCalAllData:			
			
			//������ʷ����
			//for(int dayIndex=0;dayIndex<daySize;dayIndex++)
			/*
			for(index=0;index<daySize-1;index++)
			{
				if(dayDate.get(index).equals("2010-06-02"))
				{
					dayStart=index;
					break;
				}
			}
			*/
			dayStart = 1;
			extremeCurPrice=0;
			break;
		case ConstantsInfo.StockCalCurData:
			//��ǰ�������ݸ���
			lastSp=spDao.getLastPointStock(fullId,ConstantsInfo.DayDataType);
			if(lastSp==null)
				return listStockPoint;
			System.out.println("day last start date:"+lastSp.getFromDate());
			System.out.println("day last end date:"+lastSp.getToDate());
		//	dayStart=dayDate.indexOf(lastSp.getExtremeDate());
			dayStart=dayDate.indexOf(""+lastSp.getExtremeDate()+"");			
			pointCurDateStart=dayDate.get(dayStart);//��ֵ
			extremeCurPrice=lastSp.getExtremePrice();
			break;
		}
		
		pointDate=dayDate.get(dayStart);//��ֵ
		System.out.println("day StartDate::"+pointDate);
		for(index=dayStart;index<daySize-1;index++)
		{
			strPriDate=dayDate.get(index-1);
			curDate=dayDate.get(index);
			strNextDate=dayDate.get(index+1);
			md5Pri=sdDao.getStockMaData(fullId,strPriDate,5,ConstantsInfo.DayDataType);
			md10Pri=sdDao.getStockMaData(fullId,strPriDate,10,ConstantsInfo.DayDataType);
			md5Next=sdDao.getStockMaData(fullId,strNextDate,5,ConstantsInfo.DayDataType);
			md10Next=sdDao.getStockMaData(fullId,strNextDate,10,ConstantsInfo.DayDataType);
			
		//	System.out.println("md5Pri:"+md5Pri+"md10Pri:"+md10Pri);
		//	System.out.println("md5Next:"+md5Next+"md10Next:"+md10Next);
			//System.out.println("curDate:"+pointDate);
			if(md5Pri==0 || md10Pri==0 || md5Next==0 || md10Next==0)
			{
				continue;
			}
			priceWillFall = md5Pri>=md10Pri && md5Next<=md10Next;
			priceWillRise = md5Pri<=md10Pri && md5Next>=md10Next;
			
			
			if((md5Pri>=md10Pri && md5Next<=md10Next) || (md5Pri<=md10Pri && md5Next>=md10Next))
			{
				
				//System.out.println("strPriDate:"+strPriDate);
				//ǰ����������ĵ� �����ǰ����֮ǰ��һ�£�ȡǰһ����
				if(pointDate.equals(strPriDate)) //
				{
					//System.out.println("pointDate continue:"+pointDate);
					continue;
				}
			
				pointDate=curDate;	//���µ�point��

			//	System.out.println("day pointDate:"+pointDate);
				
				switch(getStartflag)
				{
					case 2:
					default:					
						pointStartDate=pointEndDate;
						pointEndDate=pointDate;
						break;
					case 1:
						pointEndDate=curDate;//�ڶ���
						getStartflag++;
						//System.out.println("pointEndDate:"+curDate);
						break;
					case 0:
						pointStartDate=curDate;//��һ��
						getStartflag++;	
						//System.out.println("pointStartDate:"+curDate);
						break;
				}						
				
				if(getStartflag==2)
				{
					//System.out.println("priceWillFall:"+priceWillFall+"priceWillRise:"+priceWillRise);
					//System.out.println("pointStartDate:"+pointStartDate+"pointEndDate:"+pointEndDate);
					if(priceWillFall)
					{
						sData=sdDao.getMaxStockDataPoint(fullId,pointStartDate,pointEndDate,ConstantsInfo.DayDataType);					    		
			    		extremePrice=sData.getHighestPrice();
			    		extremeDate=sData.getDate().toString();		    		
			    		flagRiseFall=1;
					}							
					else if(priceWillRise)
					{
						sData=sdDao.getMinStockDataPoint(fullId,pointStartDate,pointEndDate,ConstantsInfo.DayDataType);				
			    		extremePrice=sData.getLowestPrice();
			    		extremeDate=sData.getDate().toString();			    				    		
			    		flagRiseFall=0;
					}	
				
					//ԭ����۸�
					extremeBeforePrice=extremeCurPrice;
					//��ǰ����۸�
					extremeCurPrice=extremePrice;
					float ratio=(extremeCurPrice-extremeBeforePrice)*100/extremeCurPrice;
					sp=new StockPoint(0,ConstantsInfo.DayDataType,Date.valueOf(extremeDate),extremePrice,Date.valueOf(pointStartDate),Date.valueOf(pointEndDate),flagRiseFall,ratio,0);
					//������㵱ǰ���ݣ���һ�β���Ҫ���
					if(type==ConstantsInfo.StockCalCurData && extremeDate.equals(pointCurDateStart))
						continue;
					stockLogger.logger.debug("day extremeDate:"+extremeDate);
					stockLogger.logger.debug("day extremePrice:"+extremePrice);
					stockLogger.logger.debug("day pointStartDate:"+pointStartDate);
					stockLogger.logger.debug("day pointEndDate:"+pointEndDate);					
					stockLogger.logger.debug("ratio:"+ratio);
					System.out.println("insert day point date:"+extremeDate);
					spDao.insertPointStockTable(sp,fullId);
					listStockPoint.add(sp);
					
				}	
						
			}	
		
		}		
		
		return listStockPoint;
	}
	
	public List<StockPoint> getStockWeekExtremePoint(String fullId,DateStock dStock,int type) throws SecurityException, IOException, ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException
	{		
		List<StockPoint> listStockPoint = new ArrayList<StockPoint>();
		float md5Pri = 0,md10Pri,md5Next,md10Next;
		
		List<String> weekDate=dStock.getWeekDate();
		String strPriDate,strNextDate,curDate,pointDate = null;
		boolean priceWillFall,priceWillRise;
		String pointStartDate = null,pointEndDate = null;
		String pointTrueStartDate=null;
		int pointIndex=0;
		int getStartflag=0;
		StockData sData;
		float extremePrice = 0;
		String extremeDate = null;
		int flagRiseFall=0;
		StockPoint sp;		
		int weekSize=weekDate.size();
		if(weekSize==0)
			return listStockPoint;
		
		//System.out.println("weekLast:"+weekDate.get(weekSize-1));
		
		float extremeBeforePrice=0;
		float extremeCurPrice=0;
		
		int weekStart=1;
		int index=0;
		
		StockPoint lastSp = null;		
		String pointCurDateStart=null;//���㵱ǰ����ʱ��ʼʱ��
		switch(type)
		{
		case ConstantsInfo.StockCalAllData:
		
			//������ʷ����
			/*
			for(index=0;index<weekSize-1;index++)
			{
				if(weekDate.get(index).equals("2013-01-04"))
				//if(weekDate.get(index).equals("2014-01-03"))
				{
					weekStart=index;
					break;
				}
			}*/
			weekStart=1;
			extremeCurPrice=0;
			break;
		case ConstantsInfo.StockCalCurData:
			//��ǰ�������ݸ���
			lastSp=spDao.getLastPointStock(fullId,ConstantsInfo.WeekDataType);
			if(lastSp==null)
				return listStockPoint;
			System.out.println("week last start date:"+lastSp.getFromDate());
			System.out.println("week last end date:"+lastSp.getToDate());

			//�ܱ�ֵʱ�������ڵ�ʱ��
			weekStart=weekDate.indexOf(""+lastSp.getFromDate()+"");
			pointCurDateStart=lastSp.getExtremeDate().toString();//��ֵ
			extremeCurPrice=lastSp.getExtremePrice();
			break;
			
		}
		
		pointDate=weekDate.get(weekStart);//��ֵ
		System.out.println("week StartDate:"+pointDate);
		//for(int Index=0;index<weekSize-1;index++)
		
		for(index=weekStart;index<weekSize-1;index++)
		{
			strPriDate=weekDate.get(index-1);
			curDate=weekDate.get(index);
			strNextDate=weekDate.get(index+1);
			md5Pri=sdDao.getStockMaData(fullId,strPriDate,5,ConstantsInfo.WeekDataType);
			md10Pri=sdDao.getStockMaData(fullId,strPriDate,10,ConstantsInfo.WeekDataType);
			md5Next=sdDao.getStockMaData(fullId,strNextDate,5,ConstantsInfo.WeekDataType);
			md10Next=sdDao.getStockMaData(fullId,strNextDate,10,ConstantsInfo.WeekDataType);
			
		//	System.out.println("md5Pri:"+md5Pri+"md10Pri:"+md10Pri);
		//	System.out.println("md5Next:"+md5Next+"md10Next:"+md10Next);
			//System.out.println("curDate:"+pointDate);
			if(md5Pri==0 || md10Pri==0 || md5Next==0 || md10Next==0)
			{
				continue;
			}
			priceWillFall = md5Pri>=md10Pri && md5Next<=md10Next;
			priceWillRise = md5Pri<=md10Pri && md5Next>=md10Next;			
			
			if((md5Pri>=md10Pri && md5Next<=md10Next) || (md5Pri<=md10Pri && md5Next>=md10Next))
			{
				//System.out.println("strPriDate:"+strPriDate);
				//ǰ����������ĵ� �����ǰ����֮ǰ��һ�£�ȡǰһ����
				if(pointDate.equals(strPriDate)) //
				{
					//System.out.println("pointDate continue:"+pointDate);
					continue;
				}
			
				pointDate=curDate;	//���µ�point��
			
				//System.out.println("week pointDate:"+pointDate);
				
				switch(getStartflag)
				{
					case 2:
					default:					
						pointStartDate=pointEndDate;
						pointEndDate=pointDate;
						break;
					case 1:
						pointEndDate=curDate;//�ڶ���
						getStartflag++;
						//System.out.println("pointEndDate:"+curDate);
						break;
					case 0:
						pointStartDate=curDate;//��һ��
						getStartflag++;	
						//System.out.println("pointStartDate:"+curDate);
						break;
				}						
				
				if(getStartflag==2)
				{
					//System.out.println("priceWillFall:"+priceWillFall+"priceWillRise:"+priceWillRise);
					//System.out.println("pointStartDate:"+pointStartDate+"pointEndDate:"+pointEndDate);
					if(priceWillFall)
					{
					//	sData=sdDao.getMaxStockDataPoint(fullId,pointStartDate,pointEndDate,ConstantsInfo.WeekDataType);					    		
			    		//extremePrice=sData.getHighestPrice();
			    		//extremeDate=sData.getDate().toString();
						sData=null;
			    		//�ܱ�Ĭ��ʱ��Ϊ���壬�Ȼ�ȡ��ǰ��ʱ��
			    		pointTrueStartDate=sdDao.getPointStartDate(fullId,pointStartDate,ConstantsInfo.WeekDataType);
			    		sData=sdDao.getMaxStockDataPoint(fullId,pointTrueStartDate,pointEndDate,ConstantsInfo.DayDataType);
			    		//extremeDate=sdDao.getExtremeDate(fullId,pointTrueStartDate,pointEndDate,extremePrice,1);
			    		if(sData==null)
			    			continue;
			    		extremeDate=sData.getDate().toString();
			    		extremePrice=sData.getHighestPrice();			    		
			    		
			    		flagRiseFall=1;
					}							
					else if(priceWillRise)
					{
					//	sData=sdDao.getMinStockDataPoint(fullId,pointStartDate,pointEndDate,ConstantsInfo.WeekDataType);
					//	sData=sdDao.getMinStockDataPoint(fullId,pointStartDate,pointEndDate,ConstantsInfo.DayDataType);
			    	//	extremePrice=sData.getLowestPrice();
			    		//extremeDate=sData.getDate().toString();
						sData=null;
						pointTrueStartDate=sdDao.getPointStartDate(fullId,pointStartDate,ConstantsInfo.WeekDataType);
			    		sData=sdDao.getMinStockDataPoint(fullId,pointTrueStartDate,pointEndDate,ConstantsInfo.DayDataType);
			    		//extremeDate=sdDao.getExtremeDate(fullId,pointTrueStartDate,pointEndDate,extremePrice,0);
			    		if(sData==null)
			    			continue;
			    		extremePrice=sData.getLowestPrice();
			    		extremeDate=sData.getDate().toString();
			    			    		
			    		flagRiseFall=0;
					}	
					//
					/*
					if(type==ConstantsInfo.StockCalCurData && extremeDate.equals(pointCurDateStart))
						extremeBeforePrice=lastSp.getExtremePrice();
					else
						extremeBeforePrice=extremeCurPrice;
					*/
					extremeBeforePrice=extremeCurPrice;
					extremeCurPrice=extremePrice;
					float ratio=(extremeCurPrice-extremeBeforePrice)*100/extremeCurPrice;
					sp=new StockPoint(0,ConstantsInfo.WeekDataType,Date.valueOf(extremeDate),extremePrice,Date.valueOf(pointStartDate),Date.valueOf(pointEndDate),flagRiseFall,ratio,0);
					//������㵱ǰ���ݣ���һ�β���Ҫ���
					if(type==ConstantsInfo.StockCalCurData && extremeDate.equals(pointCurDateStart))
						continue;
					
					stockLogger.logger.debug("week extremeDate:"+extremeDate);
					stockLogger.logger.debug("week extremePrice:"+extremePrice);
					stockLogger.logger.debug("week pointStartDate:"+pointStartDate);
					stockLogger.logger.debug("week pointEndDate:"+pointEndDate);
					stockLogger.logger.debug("ratio:"+ratio);
					System.out.println("insert week point date:"+extremeDate);
					spDao.insertPointStockTable(sp,fullId);
					listStockPoint.add(sp);
				}
							
			}	
		
		}		
		
		return listStockPoint;
	}
	
	public  List<StockPoint> getStockMonthExtremePoint(String fullId,DateStock dStock,int type) throws SecurityException, IOException, ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException
	{
		List<StockPoint> listStockPoint = new ArrayList<StockPoint>();
		float md5Pri = 0,md10Pri,md5Next,md10Next;
		
		List<String> monthDate=dStock.getMonthDate();
		String strPriDate,strNextDate,curDate,pointDate = null;
		boolean priceWillFall,priceWillRise;
		String pointStartDate = null,pointEndDate = null;
		String pointTrueStartDate=null;
		int pointIndex=0;
		int getStartflag=0;
		StockData sData;
		float extremePrice = 0;
		String extremeDate = null;
		int flagRiseFall=0;
		StockPoint sp;		
		int monthSize=monthDate.size();
		if(monthSize==0)
			return listStockPoint;
		
	//	System.out.println("monthLast:"+monthDate.get(monthSize-1));
		int monthStart=1;
		int index=0;
		float extremeBeforePrice=0;
		float extremeCurPrice=0;
		
		
		StockPoint lastSp = null;		
		String pointCurDateStart=null;//���㵱ǰ����ʱ��ʼʱ��
		switch(type)
		{
		case ConstantsInfo.StockCalAllData:
		
			//������ʷ����
			/*
			for(index=0;index<monthSize-1;index++)
			{
				if(monthDate.get(index).contains("2013-01"))
				{
					monthStart=index;
					break;
				}
			}*/
			monthStart =1;
			extremeCurPrice=0;
			break;
		case ConstantsInfo.StockCalCurData:
			//��ǰ�������ݸ���
			lastSp=spDao.getLastPointStock(fullId,ConstantsInfo.MonthDataType);
			if(lastSp==null)
				return listStockPoint;
			System.out.println("month last start date:"+lastSp.getFromDate());
			System.out.println("month last end date:"+lastSp.getToDate());

			//�ܱ�ֵʱ�������ڵ�ʱ��
			monthStart=monthDate.indexOf(""+lastSp.getFromDate()+"");
			//System.out.println("week dayStart::"+monthStart);
			pointCurDateStart=lastSp.getExtremeDate().toString();//��ֵ
			extremeCurPrice=lastSp.getExtremePrice();
			break;
			
		}
		
		pointDate=monthDate.get(monthStart);//��ֵ
		System.out.println("month StartDate:"+pointDate);
		
		for(index=monthStart;index<monthSize-1;index++)
		{
			strPriDate=monthDate.get(index-1);
			curDate=monthDate.get(index);
			strNextDate=monthDate.get(index+1);
			md5Pri=sdDao.getStockMaData(fullId,strPriDate,5,ConstantsInfo.MonthDataType);
			md10Pri=sdDao.getStockMaData(fullId,strPriDate,10,ConstantsInfo.MonthDataType);
			md5Next=sdDao.getStockMaData(fullId,strNextDate,5,ConstantsInfo.MonthDataType);
			md10Next=sdDao.getStockMaData(fullId,strNextDate,10,ConstantsInfo.MonthDataType);
			
		//	System.out.println("md5Pri:"+md5Pri+"md10Pri:"+md10Pri);
		//	System.out.println("md5Next:"+md5Next+"md10Next:"+md10Next);
			//System.out.println("curDate:"+pointDate);
			if(md5Pri==0 || md10Pri==0 || md5Next==0 || md10Next==0)
			{
				continue;
			}
			priceWillFall = md5Pri>=md10Pri && md5Next<=md10Next;
			priceWillRise = md5Pri<=md10Pri && md5Next>=md10Next;			
			
			if((md5Pri>=md10Pri && md5Next<=md10Next) || (md5Pri<=md10Pri && md5Next>=md10Next))
			{
				//System.out.println("strPriDate:"+strPriDate);
				//ǰ����������ĵ� �����ǰ����֮ǰ��һ�£�ȡǰһ����
				if(pointDate.equals(strPriDate)) //
				{
					//System.out.println("pointDate continue:"+pointDate);
					continue;
				}
			
				pointDate=curDate;	//���µ�point��
					
				//System.out.println("month pointDate:"+pointDate);
				
				switch(getStartflag)
				{
					case 2:
					default:					
						pointStartDate=pointEndDate;
						pointEndDate=pointDate;
						break;
					case 1:
						pointEndDate=curDate;//�ڶ���
						getStartflag++;
						//System.out.println("pointEndDate:"+curDate);
						break;
					case 0:
						pointStartDate=curDate;//��һ��
						getStartflag++;	
						//System.out.println("pointStartDate:"+curDate);
						break;
				}						
				
				if(getStartflag==2)
				{
					if(priceWillFall)
					{
					
						sData=null;
			    		pointTrueStartDate=sdDao.getPointStartDate(fullId,pointStartDate,ConstantsInfo.MonthDataType);
			    		sData=sdDao.getMaxStockDataPoint(fullId,pointTrueStartDate,pointEndDate,ConstantsInfo.DayDataType);
			    		//extremeDate=sdDao.getExtremeDate(fullId,pointTrueStartDate,pointEndDate,extremePrice,1);
			    		if(sData==null)
			    			continue;
			    		extremeDate=sData.getDate().toString();
			    		extremePrice=sData.getHighestPrice();	   	
			    		
			    		flagRiseFall=1;
					}							
					else if(priceWillRise)
					{	
						sData=null;
			    		pointTrueStartDate=sdDao.getPointStartDate(fullId,pointStartDate,ConstantsInfo.MonthDataType);
			    		sData=sdDao.getMinStockDataPoint(fullId,pointTrueStartDate,pointEndDate,ConstantsInfo.DayDataType);
			    		//extremeDate=sdDao.getExtremeDate(fullId,pointTrueStartDate,pointEndDate,extremePrice,0);
			    		if(sData==null)
			    			continue;
			    		extremePrice=sData.getLowestPrice();
			    		extremeDate=sData.getDate().toString();			    	
			    		flagRiseFall=0;
					}		
					/*
					if(type==ConstantsInfo.StockCalCurData && extremeDate.equals(pointCurDateStart))
						extremeBeforePrice=lastSp.getExtremePrice();
					else
						extremeBeforePrice=extremeCurPrice;
					*/
					
					extremeBeforePrice=extremeCurPrice;
					extremeCurPrice=extremePrice;
					float ratio=(extremeCurPrice-extremeBeforePrice)*100/extremeCurPrice;
					sp=new StockPoint(0,ConstantsInfo.MonthDataType,Date.valueOf(extremeDate),extremePrice,Date.valueOf(pointStartDate),Date.valueOf(pointEndDate),flagRiseFall,ratio,0);
					//������㵱ǰ���ݣ���һ�β���Ҫ���
					if(type==ConstantsInfo.StockCalCurData && extremeDate.equals(pointCurDateStart))
						continue;
					System.out.println("insert month point date:"+extremeDate);
					spDao.insertPointStockTable(sp,fullId);
					stockLogger.logger.debug("month extremeDate:"+extremeDate);
					stockLogger.logger.debug("month extremePrice:"+extremePrice);
					stockLogger.logger.debug("month pointStartDate:"+pointStartDate);
					stockLogger.logger.debug("month pointEndDate:"+pointEndDate);
					stockLogger.logger.debug("ratio:"+ratio);
					listStockPoint.add(sp);
				}
							
			}	
		
		}		
		
		return listStockPoint;
	}
	
	
	public List<StockPoint> getStockSeasonExtremePoint(String fullId,DateStock dStock) throws SecurityException, IOException, ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException
	{
		List<StockPoint> listStockPoint = new ArrayList<StockPoint>();
		float md5Pri = 0,md10Pri,md5Next,md10Next;
		
		List<String> seasonDate=dStock.getSeasonDate();
		String strPriDate,strNextDate,curDate,pointDate = null;
		boolean priceWillFall,priceWillRise;
		String pointStartDate = null,pointEndDate = null;
		int pointIndex=0;
		int getStartflag=0;
		StockData sData;
		float extremePrice = 0;
		String extremeDate = null;
		int flagRiseFall=0;
		StockPoint sp;		
		int seasonSize=seasonDate.size();
		System.out.println("SeasonSize:"+seasonSize);
		System.out.println("seasonFirst:"+seasonDate.get(0));
		System.out.println("seasonLast:"+seasonDate.get(seasonSize-1));
		int seasonStart=1;
		
		int monthStart=1;
		int index=0;
		float extremeBeforePrice=0;
		float extremeCurPrice=0;
		
		for(index=0;index<seasonSize-1;index++)
		{
			if(seasonDate.get(index).contains("2012-03"))
			{
				seasonStart=index;
				break;
			}
		}
		
		pointDate=seasonDate.get(seasonStart);//��ֵ
		System.out.println("season StartDate:"+pointDate);
		
		for(index=1;index<seasonSize-1;index++)
		//for(index=seasonStart;index<seasonSize-1;index++)
		{
			strPriDate=seasonDate.get(index-1);
			curDate=seasonDate.get(index);
			strNextDate=seasonDate.get(index+1);
			md5Pri=sdDao.getStockMaData(fullId,strPriDate,5,ConstantsInfo.SeasonDataType);
			md10Pri=sdDao.getStockMaData(fullId,strPriDate,10,ConstantsInfo.SeasonDataType);
			md5Next=sdDao.getStockMaData(fullId,strNextDate,5,ConstantsInfo.SeasonDataType);
			md10Next=sdDao.getStockMaData(fullId,strNextDate,10,ConstantsInfo.SeasonDataType);
			
		//	System.out.println("md5Pri:"+md5Pri+"md10Pri:"+md10Pri);
		//	System.out.println("md5Next:"+md5Next+"md10Next:"+md10Next);
			//System.out.println("curDate:"+pointDate);
			if(md5Pri==0 || md10Pri== 0 || md5Next==0 || md10Next==0)
			{
				continue;
			}
			priceWillFall = md5Pri>=md10Pri && md5Next<=md10Next;
			priceWillRise = md5Pri<=md10Pri && md5Next>=md10Next;			
			
			if((md5Pri>=md10Pri && md5Next<=md10Next) || (md5Pri<=md10Pri && md5Next>=md10Next))
			{
				//System.out.println("strPriDate:"+strPriDate);
				//ǰ����������ĵ� �����ǰ����֮ǰ��һ�£�ȡǰһ����
				if(pointDate.equals(strPriDate)) //
				{
					//System.out.println("pointDate continue:"+pointDate);
					continue;
				}
			
				pointDate=curDate;	//���µ�point��
				System.out.println("-----------------");				
				System.out.println("pointDate:"+pointDate);
				
				switch(getStartflag)
				{
					case 2:
					default:					
						pointStartDate=pointEndDate;
						pointEndDate=pointDate;
						break;
					case 1:
						pointEndDate=curDate;//�ڶ���
						getStartflag++;
						System.out.println("pointEndDate:"+curDate);
						break;
					case 0:
						pointStartDate=curDate;//��һ��
						getStartflag++;	
						System.out.println("pointStartDate:"+curDate);
						break;
				}						
				
				if(getStartflag==2)
				{
					//System.out.println("priceWillFall:"+priceWillFall+"priceWillRise:"+priceWillRise);
					//System.out.println("pointStartDate:"+pointStartDate+"pointEndDate:"+pointEndDate);
					if(priceWillFall)
					{
						sData=null;
						sData=sdDao.getMaxStockDataPoint(fullId,pointStartDate,pointEndDate,ConstantsInfo.SeasonDataType);					    		
						if(sData==null)
			    			continue;
						extremePrice=sData.getHighestPrice();
			    		extremeDate=sData.getDate().toString();
			    		System.out.println("maxHighestDate:"+extremeDate);
			    		System.out.println("maxHighestPrice:"+extremePrice);
			    		
			    		flagRiseFall=1;
					}							
					else if(priceWillRise)
					{
						sData=null;
						sData=sdDao.getMinStockDataPoint(fullId,pointStartDate,pointEndDate,ConstantsInfo.SeasonDataType);				
						if(sData==null)
			    			continue;
						extremePrice=sData.getLowestPrice();
			    		extremeDate=sData.getDate().toString();
			    		System.out.println("minLowestDate:"+extremeDate);
			    		System.out.println("minLowestPrice:"+extremePrice);			    		
			    		flagRiseFall=0;
					}								
					
					extremeBeforePrice=extremeCurPrice;
					extremeCurPrice=extremePrice;
					float ratio=(extremeCurPrice-extremeBeforePrice)/extremeCurPrice;					
					sp=new StockPoint(0,ConstantsInfo.SeasonDataType,Date.valueOf(extremeDate),extremePrice,Date.valueOf(pointStartDate),Date.valueOf(pointEndDate),flagRiseFall,ratio,0);
					spDao.insertPointStockTable(sp,fullId);
					listStockPoint.add(sp);
				}
							
			}	
		
		}		
		
		return listStockPoint;
	}
	
	
	//����ĳ����Ʊ��������
	public void getPiontToTableForSingleStock(String fullId,int type) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException
	{
		int isTableExist=sdDao.isExistStockTable(fullId,ConstantsInfo.TABLE_DATA_STOCK);
		if(isTableExist==0)//������
		{
			stockLogger.logger.fatal("stock data table no exist");
			return;
		}
		
		List<String> listStockDays=new ArrayList<String>();
		List<String> listStockWeeks=new ArrayList<String>();
		List<String> listStockMonths=new ArrayList<String>();
		List<String> listStockSeasons=new ArrayList<String>();
		List<String> listStockYears=new ArrayList<String>();
	
		listStockDays=sdDao.getDates(fullId,ConstantsInfo.DayDataType);
		listStockWeeks=sdDao.getDates(fullId,ConstantsInfo.WeekDataType);
		listStockMonths=sdDao.getDates(fullId,ConstantsInfo.MonthDataType);
	//	listStockSeasons=sdDao.getDates(fullId,ConstantsInfo.SeasonDataType);
	//	listStockYears=sdDao.getDates(fullId,ConstantsInfo.YearDataType);
	//	listStockYears=sdDao.getDates("sh000001_copy",ConstantsInfo.YearDataType);
		DateStock dStock=new DateStock(listStockDays,listStockWeeks,listStockMonths,listStockSeasons,listStockYears);
	
		switch(type)
		{
			case ConstantsInfo.StockCalAllData:
				isTableExist=sdDao.isExistStockTable(fullId,ConstantsInfo.TABLE_POINT_STOCK);
		    	if(isTableExist == 0){//������
					stockLogger.logger.fatal("****stockFullId��"+fullId+"�����ڼ����****");
					System.out.println(fullId+"���������****");
					spDao.createStockPointTable(fullId);
				} else {
					//��ձ� 					
					spDao.truncatePointStockTable(fullId);	
				}
				//�������ݴ�2010-06-04 ���ݿ�������6�·ݿ�ʼ
				getStockExtremePointValue(fullId,dStock,ConstantsInfo.DayDataType,ConstantsInfo.StockCalAllData);
				getStockExtremePointValue(fullId,dStock,ConstantsInfo.WeekDataType,ConstantsInfo.StockCalAllData);
				getStockExtremePointValue(fullId,dStock,ConstantsInfo.MonthDataType,ConstantsInfo.StockCalAllData);

				break;
			case ConstantsInfo.StockCalCurData:
				System.out.println("------------");
				getStockExtremePointValue(fullId,dStock,ConstantsInfo.DayDataType,ConstantsInfo.StockCalCurData);
			//	getStockDayExtremePoint(fullId,dStock,ConstantsInfo.StockCalCurData); ///�ռ���
				System.out.println("-----------");
				getStockExtremePointValue(fullId,dStock,ConstantsInfo.WeekDataType,ConstantsInfo.StockCalCurData);
			//	getStockWeekExtremePoint(fullId,dStock,ConstantsInfo.StockCalCurData);//�ܼ���
				System.out.println("------------");
				getStockExtremePointValue(fullId,dStock,ConstantsInfo.MonthDataType,ConstantsInfo.StockCalCurData);
			//	getStockMonthExtremePoint(fullId,dStock,ConstantsInfo.StockCalCurData);//�����¼���
				break;
		}
		
	}
	
	/**
	 * ���㼫���������
	 * @param type ����ȫ�������ǵ�ǰ����
	 * @param listStockFullId
	 * @param stockMarket
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws SecurityException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 */

	public void getPointToTable(int type,int stockMarket,int marketType) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException
	{
		List<String> listStockFullId = new ArrayList<String>();
		
		if(marketType == ConstantsInfo.StockMarket )
			listStockFullId=sbDao.getAllStockFullId(marketType);
		else 
			listStockFullId=sbDao.getAllFuturesFullId(marketType);
		
		int count1=0,count2=0,count3=0,count4=0,count5=0;
	
		for (int i=0;i<listStockFullId.size();i++)	
		{
			String fullId = listStockFullId.get(i);

			stockLogger.logger.fatal("stock fullId:"+fullId);
			System.out.println("stock fullId:"+fullId);
			switch(stockMarket)
			{
			case ConstantsInfo.DPMarket:
				if(!(fullId.equals("sh000001")|| fullId.equals("sz399001")|| fullId.equals("sz399005")|| fullId.equals("sz399006")))
					continue;
				count1++;
				break;
			case ConstantsInfo.SHMarket:
				if(!fullId.contains("sh60"))	
			//	if(!(fullId.contains("sh60") &&	Integer.parseInt(fullId.substring(4).toString())>=3005))
					continue;
				count2++;
				break;
			case ConstantsInfo.SZMarket:
			//	if(!fullId.contains("sz000100"))
			//			continue;
				if(!fullId.contains("SZ001"))
			//	if(!(fullId.contains("sz000") && Integer.parseInt(fullId.substring(4).toString())>=768))
					continue;
				count3++;
				break;
			case ConstantsInfo.ZXMarket:
				//if(!fullId.contains("sz002"))
				if(!(fullId.contains("sz002") && Integer.parseInt(fullId.substring(4).toString())>=2451))
					continue;
				count4++;
				break;
			case ConstantsInfo.CYMarket:
				if(!fullId.contains("sz300"))
					continue;
				count5++;
				break;
			case ConstantsInfo.ALLMarket:
			default:
				break;
			}
			
			/*����*/
			// if(!fullId.equals("SZ002772"))
			//	continue;
			
			 /*����*/
			// if(!fullId.contains("SH"))
			//	continue;

			int isTableExist=sdDao.isExistStockTable(fullId,ConstantsInfo.TABLE_DATA_STOCK);
			if(isTableExist==0)//������
			{
				stockLogger.logger.fatal("stock data table no exist");
				continue;
			}

			//ɾ���ռ�¼
			//sdDao.deleteColumnNULL(fullId);
			
			List<String> listStockDays=new ArrayList<String>();
			List<String> listStockWeeks=new ArrayList<String>();
			List<String> listStockMonths=new ArrayList<String>();
			List<String> listStockSeasons=new ArrayList<String>();
			List<String> listStockYears=new ArrayList<String>();
		
			listStockDays=sdDao.getDates(fullId,ConstantsInfo.DayDataType);
			listStockWeeks=sdDao.getDates(fullId,ConstantsInfo.WeekDataType);
			listStockMonths=sdDao.getDates(fullId,ConstantsInfo.MonthDataType);
		//	listStockSeasons=sdDao.getDates(fullId,ConstantsInfo.SeasonDataType);
		//	listStockYears=sdDao.getDates(fullId,ConstantsInfo.YearDataType);
		//	listStockYears=sdDao.getDates("sh000001_copy",ConstantsInfo.YearDataType);
			DateStock dStock=new DateStock(listStockDays,listStockWeeks,listStockMonths,listStockSeasons,listStockYears);
		
			switch(type)
			{
				case ConstantsInfo.StockCalAllData:
					isTableExist=sdDao.isExistStockTable(fullId,ConstantsInfo.TABLE_POINT_STOCK);
			    	if(isTableExist == 0){//������
						stockLogger.logger.fatal("****stockFullId��"+fullId+"�����ڼ����****");
						System.out.println(fullId+"���������****");
						spDao.createStockPointTable(fullId);
					} else {
						//��ձ� 					
						spDao.truncatePointStockTable(fullId);	
					}
					//�������ݴ�2010-06-04 ���ݿ�������6�·ݿ�ʼ
			    	getStockExtremePointValue(fullId,dStock,ConstantsInfo.DayDataType,ConstantsInfo.StockCalAllData);
			    	getStockExtremePointValue(fullId,dStock,ConstantsInfo.WeekDataType,ConstantsInfo.StockCalAllData);
			    	getStockExtremePointValue(fullId,dStock,ConstantsInfo.MonthDataType,ConstantsInfo.StockCalAllData);

					break;
				case ConstantsInfo.StockCalCurData:
					System.out.println("------------");
					getStockExtremePointValue(fullId,dStock,ConstantsInfo.DayDataType,ConstantsInfo.StockCalCurData);
				//	getStockDayExtremePoint(fullId,dStock,ConstantsInfo.StockCalCurData); ///�ռ���
					System.out.println("-----------");
					getStockExtremePointValue(fullId,dStock,ConstantsInfo.WeekDataType,ConstantsInfo.StockCalCurData);
				//	getStockWeekExtremePoint(fullId,dStock,ConstantsInfo.StockCalCurData);//�ܼ���
					System.out.println("------------");
					getStockExtremePointValue(fullId,dStock,ConstantsInfo.MonthDataType,ConstantsInfo.StockCalCurData);
				//	getStockMonthExtremePoint(fullId,dStock,ConstantsInfo.StockCalCurData);//�����¼���
					break;
			}
			
		//	pc.getStockSeasonExtremePoint(fullId,dStock);//���ȼ���
		//	pc.getStockYearExtremePoint(fullId,dStock);//�꼫��
		
		}	
		
	}
	
	//��������
	public void addIndexForStockPoint() throws IOException, ClassNotFoundException, SQLException
	{
		List<String> listStockFullId = new ArrayList<String>();
		listStockFullId=siDao.getAllFullId();
		
		for(Iterator it = listStockFullId.iterator();it.hasNext();)
		{
			String fullId=(String)it.next();
			System.out.println(fullId);
			
			int isTableExist=sdDao.isExistStockTable(fullId,ConstantsInfo.TABLE_DATA_STOCK);
			if(isTableExist==0)//������
				continue;
			
		//	if(!fullId.equals("sz000001"))
		//		continue;
			
			spDao.addIndex(fullId);
		}
	}
	
	
	public void printfAllStockId() throws IOException, ClassNotFoundException, SQLException
	{
		List<String> listStockFullId = new ArrayList<String>();
		String fullId;
		
		listStockFullId=sbDao.getAllStockFullId(ConstantsInfo.StockMarket);
		System.out.println(listStockFullId.size());
		System.out.println("***");
		for (int i=2783;i<listStockFullId.size();i++)	
		{
			fullId = listStockFullId.get(i);
			System.out.println(fullId);
		}
	}

	
	public static void main(String[] args) throws SecurityException, IOException, ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException {

		PropertyConfigurator.configure("StockConf/log4j_point.properties");	
	
		stockLogger.logger.fatal("calculation stock point data start");
	
		Connection stockBaseConn = DbConn.getConnDB("stockConf/conn_base_db.ini"); 
	     Connection stockDataConn = DbConn.getConnDB("stockConf/conn_data_db.ini"); 
	     Connection stockPointConn = DbConn.getConnDB("stockConf/conn_point_db.ini");  

		//siDao =new StockInformationDao(stockData1Conn);
		
		//addIndexForStockPoint();//���� 1
		
		
		PointClass pc=new PointClass(stockBaseConn,stockDataConn,stockPointConn);
		Date startDate = new Date(0);
		
		//����ȫ����ֵ��
	//	pc.getPointToTable(ConstantsInfo.StockCalAllData,ConstantsInfo.ALLMarket);
	
		
		/*
		//ConstantsInfo.StockCalAllData ConstantsInfo.StockCalCurData
		//ConstantsInfo.DPMarket  ConstantsInfo.SHMarket ConstantsInfo.SZMarket ConstantsInfo.ZXMarket ConstantsInfo.CYMarket
		*/
		//���㲿�ּ�ֵ��
		//pc.getPointToTable(ConstantsInfo.StockCalCurData,ConstantsInfo.ALLMarket);
		
		//pc.getPiontToTableForSingleStock("SZ300420",ConstantsInfo.StockCalAllData);
		//pc.getPiontToTableForSingleStock("SZ002696",ConstantsInfo.StockCalCurData);
	//	pc.getPiontToTableForSingleStock("SH000001",ConstantsInfo.StockCalAllData);
		//pc.getPiontToTableForSingleStock("SH600030",ConstantsInfo.StockCalCurData);
	//	pc.printfAllStockId();
		

	
		Date endDate = new Date(0);
		long seconds =(endDate.getTime() - startDate.getTime())/1000;
		System.out.println("�ܹ���ʱ��"+seconds+"��");
		
		
		stockBaseConn.close();
		stockPointConn.close();
		stockDataConn.close();
			
		stockLogger.logger.fatal("�ܹ���ʱ��"+seconds+"��");
		stockLogger.logger.fatal("calculation stock point data end");
		System.out.println("get point end");
				
	}

}
