package com.calculation.stock;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;

import com.timer.stock.TimeStock;
import common.ConstantsInfo;
import common.stockLogger;

import dao.DayStockDao;
import dao.DbConn;
import dao.StockBaseDao;
import dao.StockDataDao;
import dao.StockInformationDao;
import dao.StockPointDao;
import date.timer.stockDateTimer;

public class CalculationStock {
	
	private StockDataDao sdDao;
	private StockBaseDao sbDao;
	static StockInformationDao siDao =new StockInformationDao();
	static stockDateTimer sdTime=new stockDateTimer();
	public CalculationStock()
	{
		
	}
	public CalculationStock(Connection stockBaseConn,Connection stockDataConn,Connection stockPointConn)
	{
		sbDao = new StockBaseDao(stockBaseConn);
		sdDao =new StockDataDao(stockDataConn);
	}
	
	 public CalculationStock(StockBaseDao sbDao,StockDataDao sdDao)
	{
		this.sbDao = sbDao;
		this.sdDao = sdDao;
		///	this.spDao = spDao;
	}	
	 
	 //����ĳ����Ʊ
	public void calculStockAllDataForSingleStock(String fullId,int type) throws IOException, ClassNotFoundException, SQLException
	{
		if(type == ConstantsInfo.StockCalAllData)//��������
		{
			List<TimeStock> listTimeStock = new ArrayList<TimeStock>();
			//���յ���������±�dataType��0��Ϊ1
			sdDao.updateDayTypeForNewStock(fullId);
			
			listTimeStock=sdDao.getStockTimeStockOfYear(fullId);
			
			sdDao.calAllHistoryDayStockData(fullId, listTimeStock);//������ma5 ma10
			sdDao.calAllHistoryWeekStockData(fullId, listTimeStock);//����������ֵ				
			sdDao.calAllHistoryMonthStockData(fullId, listTimeStock);//����������ֵ
			
		//	sdDao.calAllHistorySeasonStockData(fullId, listTimeStock);//���㼾��
		//	sdDao.calAllHistoryYearStockData(fullId, listTimeStock);//������
			listTimeStock.clear();
		}
		else
		{
			List<String> listDay = new ArrayList<String>();
			//�������
			List<Integer> listWeekPri = new ArrayList<Integer>();//
			List<Integer> listWeekCur = new ArrayList<Integer>();
			List<Integer> listMonthPri = new ArrayList<Integer>();
			List<Integer> listMonthCur = new ArrayList<Integer>();
			List<Integer> listYear = new ArrayList<Integer>();
				
			//������ ������������һ�죬��������е��������
		//	listDay=sdDao.getUnCalculationDay(fullId,"2015-07-20");	
			
			listDay=sdDao.getUnCalculationDay(fullId);
			
			listYear=sdDao.getUnCalculationYear(fullId);
			
			if(listDay.size()< 1 || listYear.size()<1){
				stockLogger.logger.fatal(fullId+" have no data");
				System.out.println(fullId+" have no data");
				return;
			}
			
			listWeekPri=sdDao.getUnCalculationWeek(fullId, listYear.get(0));
			listMonthPri =sdDao.getUnCalculationMonth(fullId, listYear.get(0));
			
			if (listYear.size()==2) {
				stockLogger.logger.fatal(fullId+" have compute more than 1 year");
				listWeekCur=sdDao.getUnCalculationWeek(fullId, listYear.get(1));
				listMonthCur =sdDao.getUnCalculationMonth(fullId, listYear.get(1));
			}
			
		
			
			//��ʼ��������һ�� 
			System.out.println(listDay.get(0));
			System.out.println(listDay.get(listDay.size()-1));
			
			sdDao.callDayStockDataFromDate(fullId, listDay);//������ma5 ma10 �Ƿ���
			
			//������				
			//���ܿ���  ��β�������һ��
		//	listWeek=sdDao.getUnCalculationWeek(fullId,"2014-10-13","2014-12-28");
		//	sdDao.calWeekStockDataFromDate(fullId, listWeek,2014);//������

			//listWeek=sdDao.getUnCalculationWeek(fullId,"2015-07-20","2015-07-31");
	

			sdDao.calWeekStockDataFromDate(fullId, listWeekPri,listYear.get(0));//������ ma5 ma10 �Ƿ���
			sdDao.calMonthStockDataFromDate(fullId, listMonthPri,listYear.get(0));//������ ma5 ma10 �Ƿ���
			
			//����
			if (listYear.size()==2) {
				
				sdDao.calWeekStockDataFromDate(fullId, listWeekCur,listYear.get(1));//������ ma5 ma10 �Ƿ���
				sdDao.calMonthStockDataFromDate(fullId, listMonthCur,listYear.get(1));//������ ma5 ma10 �Ƿ���
				
			}
			
			
			//sdDao.calWeekStockDataFromDate(fullId, listWeek,listYear.get(0));//������ ma5 ma10 �Ƿ���
			 //������
			
			//����2014����
		//	listMonth.add(10);//��10��
		//	sdDao.calMonthStockDataFromDate(fullId, listMonth,2014); //������ ma5 ma10 �Ƿ��� 	
			//2015��
			//List<Integer> listMonth2015 = new ArrayList<Integer>();
			//listMonth2015.add(7);
	
		//	sdDao.calMonthStockDataFromDate(fullId, listMonth,listYear.get(0)); //������ ma5 ma10 �Ƿ���
			
			
		
		}
	}
	 
	public void calculStockAllData(int type) throws IOException, ClassNotFoundException, SQLException
	{
			
		List<String> listStockFullId = new ArrayList<String>();
//		listStockFullId=siDao.getAllFullId();
		
		listStockFullId=sbDao.getAllStockFullId(ConstantsInfo.StockMarket);		
		
	//	2624
		for (int i=0;i<listStockFullId.size();i++)	
		{
			String fullId = listStockFullId.get(i);
			
			//sh600000
		//	if(!fullId.equals("SH600007"))
		//		continue;
					
			
			System.out.println(fullId);
			stockLogger.logger.fatal(i+":"+fullId);
			int isTableExist=sdDao.isExistStockTable(fullId,ConstantsInfo.TABLE_DATA_STOCK);
			if(isTableExist==0)//������
			{
				stockLogger.logger.fatal(fullId+"not found data stock");
				continue;
			}

			if(type==0)//��������
			{
				List<TimeStock> listTimeStock = new ArrayList<TimeStock>();
				
				//���յ���������±�dataType��0��Ϊ1
				sdDao.updateDayTypeForNewStock(fullId);
				listTimeStock=sdDao.getStockTimeStockOfYear(fullId);
				
				sdDao.calAllHistoryDayStockData(fullId, listTimeStock);//������ma5 ma10
				sdDao.calAllHistoryWeekStockData(fullId, listTimeStock);//����������ֵ				
				sdDao.calAllHistoryMonthStockData(fullId, listTimeStock);//����������ֵ
				
			//	sdDao.calAllHistorySeasonStockData(fullId, listTimeStock);//���㼾��
			//	sdDao.calAllHistoryYearStockData(fullId, listTimeStock);//������
				listTimeStock.clear();
			}
			else
			{
				List<String> listDay = new ArrayList<String>();
				List<Integer> listWeek = new ArrayList<Integer>();
				List<Integer> listMonth = new ArrayList<Integer>();
				List<Integer> listYear = new ArrayList<Integer>();
					
				//������ ������������һ�죬��������е��������
			//	listDay=sdDao.getUnCalculationDay(fullId,"2015-07-20");	
				
				listDay=sdDao.getUnCalculationDay(fullId);
			
				listYear=sdDao.getUnCalculationYear(fullId);
				
				if (listYear.size()>1) {
					stockLogger.logger.fatal(fullId+" have compute more than 1 year");
				}
				//��ʼ��������һ�� 
				System.out.println(listDay.get(0));
				System.out.println(listDay.get(listDay.size()-1));
				
				sdDao.callDayStockDataFromDate(fullId, listDay);//������ma5 ma10 �Ƿ���
				
				//������				
				//���ܿ���  ��β�������һ��
			//	listWeek=sdDao.getUnCalculationWeek(fullId,"2014-10-13","2014-12-28");
			//	sdDao.calWeekStockDataFromDate(fullId, listWeek,2014);//������
	
				//listWeek=sdDao.getUnCalculationWeek(fullId,"2015-07-20","2015-07-31");
				
				
				for (i=0;i<listYear.size();i++)	
				{
					int year = listYear.get(i);
					listWeek=sdDao.getUnCalculationWeek(fullId, year);
					listMonth =sdDao.getUnCalculationMonth(fullId, year);
					System.out.println(year);
					sdDao.calWeekStockDataFromDate(fullId, listWeek,year);//������ ma5 ma10 �Ƿ���
					sdDao.calMonthStockDataFromDate(fullId, listMonth,year);//������ ma5 ma10 �Ƿ���
				}
				
				
				
		
				
				 //������
				
				//����2014����
			//	listMonth.add(10);//��10��
			//	sdDao.calMonthStockDataFromDate(fullId, listMonth,2014); //������ ma5 ma10 �Ƿ��� 	
				//2015��
				//List<Integer> listMonth2015 = new ArrayList<Integer>();
				//listMonth2015.add(7);
		
				
			
			}
			
			
		}
		
		
	}
	
	//��������
	public void addIndexForStock() throws IOException, ClassNotFoundException, SQLException
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
			
			//if(!fullId.equals("sh000001"))
			//		continue;
			
			sdDao.addIndex(fullId);
		}
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
		
		System.out.println("cal data start");
		Date startDate = new Date();
		PropertyConfigurator.configure("StockConf/log4j_append.properties");
		stockLogger.logger.fatal("calculation stock append data start");
		 
	     Connection stockBaseConn = DbConn.getConnDB("stockConf/conn_base_db.ini");  
	     Connection stockDataConn = DbConn.getConnDB("stockConf/conn_data_db.ini"); 
	     Connection stockPointConn = DbConn.getConnDB("stockConf/conn_point_db.ini");  
	     CalculationStock cas =new CalculationStock(stockBaseConn,stockDataConn,stockPointConn);

		
		/*1 �������*/
		//addIndexForStock(); //����
		
		/*2 ����ȫ������*/
	//	cas.calculStockAllData(0); //����ȫ��
		
		//cas.calculStockAllData(1);//���㲿������
		
		cas.calculStockAllDataForSingleStock("sh000001",ConstantsInfo.StockCalCurData);

		Date endDate = new Date();
		// ��������ʱ�����������
		long seconds =(endDate.getTime() - startDate.getTime())/1000;
		System.out.println("�ܹ���ʱ��"+seconds+"��");
		System.out.println("cal data end");
		
		stockBaseConn.close();
		stockDataConn.close();
		stockPointConn.close();
		stockLogger.logger.fatal("�ܹ���ʱ��"+seconds+"��");
		stockLogger.logger.fatal("calculation stock append data end");
		/*
		//����"sh000001_copy"
		List<TimeStock> listTimeStock=new ArrayList<TimeStock>();
		listTimeStock=sdDao.getStockTimeStockOfYear("sh000001_copy");			
		sdDao.calAllHistoryWeekStockData("sh000001_copy", listTimeStock);
		sdDao.calAllHistoryMonthStockData("sh000001_copy", listTimeStock);
		sdDao.calAllHistorySeasonStockData("sh000001_copy", listTimeStock);
		sdDao.calAllHistoryYearStockData("sh000001_copy", listTimeStock);
		*/			
	}

}
