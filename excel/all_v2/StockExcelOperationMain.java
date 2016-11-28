package excel.all_v2;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;

import common.ConstantsInfo;
import common.stockLogger;

import dao.DbConn;
import dao.StockBaseDao;
import dao.StockData;
import dao.StockDataDao;
import dao.StockOperation;
import dao.StockPoint;
import dao.StockPointDao;
import dao.StockSummary;
import dao.StockSummaryDao;

public class StockExcelOperationMain {
	private StockDataDao sdDao;
	private StockBaseDao sbDao;
	private StockPointDao spDao;
	private StockSummaryDao ssDao;
	
	public StockExcelOperationMain(Connection stockBaseConn,Connection stockDataConn,Connection stockPointConn,Connection stockSummaryConn)
	{
		   this.sbDao = new StockBaseDao(stockBaseConn);
		   this.sdDao =new StockDataDao(stockDataConn);
		   this.spDao =new StockPointDao(stockPointConn);
		   this.ssDao = new StockSummaryDao(stockSummaryConn);
	}
    
    public StockExcelOperationMain(StockBaseDao sbDao,StockDataDao sdDao,StockPointDao spDao,StockSummaryDao ssDao)
	{
		this.sbDao = sbDao;
		this.sdDao = sdDao;
		this.spDao = spDao;
		this.ssDao = ssDao;
	}

  //��Ʊ���쿪�����̼۹�ϵ ����� ����۹�ϵ
	public float getStockOpenCloseValueInfo(float openPrice,float closePrice)
	{
		return (openPrice - closePrice)/closePrice;
	}
    
	public void analyseStockOperation(int marketType,String anaylseDate) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException 
	{
		List<String> listStockFullId = new ArrayList<String>();
			
		if(marketType == ConstantsInfo.StockMarket )
			listStockFullId=sbDao.getAllStockFullId(marketType);
		else 
			listStockFullId=sbDao.getAllFuturesFullId(marketType);
		
		int isTableExist =0;
		
	
		for (int i=0;i<listStockFullId.size();i++)	
		{
			String fullId = listStockFullId.get(i);
			
			//if(!fullId.equals("SZ002548"))
			//	continue;
				
			stockLogger.logger.fatal("*****"+fullId+"*****");
			isTableExist=sdDao.isExistStockTable(fullId,ConstantsInfo.TABLE_OPERATION_STOCK);
			if(isTableExist==0){//������
				ssDao.createStockOperationTable(fullId);
			} 
			/*
			else 
				ssDao.addColStockOperationTable(fullId);
			*/
			isTableExist=sdDao.isExistStockTable(fullId,ConstantsInfo.TABLE_SUMMARY_STOCK);
			if(isTableExist==0){//������
				continue;
			}
			
			//���һ���������
			StockSummary lastSS;
			if(anaylseDate != null)
				lastSS = ssDao.getZhiDingSummaryFromSummaryTable(fullId,anaylseDate);
			else
				lastSS = ssDao.getLastSummaryFromSummaryTable(fullId);
			if(lastSS == null) {
				stockLogger.logger.fatal("summary no data");
				continue;
			}
			
			//���һ�콻������
			StockData lastSD;
			if(anaylseDate != null)
				lastSD = sdDao.getZhiDingDataStock(fullId,ConstantsInfo.DayDataType,anaylseDate);
			else 
				lastSD = sdDao.getLastDataStock(fullId,ConstantsInfo.DayDataType);
			
			if(lastSD == null) {
				stockLogger.logger.fatal("stock data no data");
				continue;
			}
			
			if(!lastSD.getDate().toString().equals(lastSS.getDayCurDate().toString())) {
				stockLogger.logger.fatal("stock no same date");
				continue;
			}
			
			int dayTrend = 0;
			if(lastSS.getDayPSValueGap().contains("-")) //��
				dayTrend = 1;
			else 
				dayTrend = 0;
		
			//ǰ����ʱ��
			String priPointDate = lastSS.getDayStartDate();
			//���Ƶ�ʱ��
			String PSDate = lastSS.getDayEndDate();
			
			//��ǰʱ��
			String curDate = lastSS.getDayCurDate();
			
			//ʱ���
			int dataGap = sdDao.getStockDataDateGap(fullId,PSDate,curDate,ConstantsInfo.DayDataType);
			if(dataGap>2) {
				stockLogger.logger.fatal("dataGap more than 2");	
				continue;
			}			
			
			//ʱ���
			dataGap = sdDao.getStockDataDateGap(fullId,priPointDate,PSDate,ConstantsInfo.DayDataType);
			
			//�������̼۹�ϵ
			float octype = getStockOpenCloseValueInfo(lastSD.getOpeningPrice(),lastSD.getClosingPrice());
			
			StockOperation sop = null;
			//���һ��������¼
			StockOperation lastOp;
			
			lastOp = ssDao.getLastOperation(fullId,anaylseDate);
			
			//�ظ�����
			if(lastOp!=null && lastOp.getOpDate().equals(curDate)){
				System.out.println("double anysle");
				continue;
			}				
			
			if(dayTrend == 1) {
				
				//���  // �� ֹ ��  Ӯ ֹ �� 
				if(dataGap >= 5 && octype <= 0){
					//��һ�������˵�
					if(lastOp != null && (lastOp.getOpType() == ConstantsInfo.BUG)){
						stockLogger.logger.fatal("cur optype"+ConstantsInfo.BUG+"-pri optype"+lastOp.getOpType());
						continue;
					}
					//sop = new StockOperation(fullId,0, curDate,lastSD.getOpeningPrice(),lastSD.getLowestPrice(),0,0,0,0,ConstantsInfo.BUG,1);
					 
					sop = new StockOperation(fullId,0, curDate,lastSD.getOpeningPrice(),Float.parseFloat(lastSS.getDayEndValue()),0,0,0,0,ConstantsInfo.BUG,1);
					 
				//}	else if(lastOp!=null && lastSD.getLowestPrice()<lastOp.getStopValue() && lastSD.getClosingPrice() < lastOp.getStopValue() ){
				} else if(lastOp!=null && lastSD.getLowestPrice()<lastOp.getStopValue() ){
					//ֹ���	 // �� ֹ �� Ӯ ֹ �� 
					
					//��һ�������ֹ�����˵�
					if(lastOp.getOpType() == ConstantsInfo.SALE|| lastOp.getOpType() == ConstantsInfo.STOP ) {
						stockLogger.logger.fatal("cur optype"+ConstantsInfo.STOP+"-pri optype"+lastOp.getOpType());					
						continue;
					}
					
					float stopRation= getStockOpenCloseValueInfo(lastOp.getStopValue(),lastOp.getBuyValue() );
					sop = new StockOperation(fullId, lastOp.getAssId(), curDate,0,lastOp.getStopValue(), 0,0,stopRation,0, ConstantsInfo.STOP,1);
				}
				
			} else {
				
				//����,ǰһ�����������
				if(lastOp!=null && dataGap >= 5 && octype >= 0){
					
					//��һ�������ֹ�����˵�
					if(lastOp.getOpType() == ConstantsInfo.SALE|| lastOp.getOpType() == ConstantsInfo.STOP ) {
						stockLogger.logger.fatal("cur optype"+ConstantsInfo.SALE+"-pri optype"+lastOp.getOpType());
						continue;
					}
					
					float earnOrlose = getStockOpenCloseValueInfo(lastSD.getOpeningPrice(),lastOp.getBuyValue());
					if(earnOrlose>0) // // �� ֹ �� Ӯ ֹ �� 
						sop =new StockOperation(fullId,lastOp.getAssId(),curDate,0,0,lastSD.getOpeningPrice(),earnOrlose,0, 0, ConstantsInfo.SALE,1);
					else 
						sop =new StockOperation(fullId,lastOp.getAssId(),curDate,0,0,lastSD.getOpeningPrice(),0,0,earnOrlose, ConstantsInfo.SALE,1);
				}
			}
			
			if(sop !=null) {
				stockLogger.logger.fatal("insert new operation");	
				ssDao.insertStockOperationTable(sop);
			}
			//else 
			//	 stockLogger.logger.fatal("sop null");	
			 
		
		}
	
	}
	
	
	 public void delete_data(int marketType) throws IOException, ClassNotFoundException, SQLException
	 {
		 List<String> listStockFullId = new ArrayList<String>();
			
			if(marketType == ConstantsInfo.StockMarket )
				listStockFullId=sbDao.getAllStockFullId(marketType);
			else 
				listStockFullId=sbDao.getAllFuturesFullId(marketType);
			String fullId;
			for (int i=0;i<listStockFullId.size();i++)	
			{
				fullId = listStockFullId.get(i);
				System.out.println(fullId);
				ssDao.deleteSummayData(fullId, "2016-07-25");
				
			}
	 		
	 }
	
	
	/**
	 * @param args
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 * @throws NoSuchFieldException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws SecurityException 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException {
		PropertyConfigurator.configure("stockConf/log4j_excelWriter.properties");
		Connection stockBaseConn = DbConn.getConnDB("stockConf/conn_base_db.ini"); 
        Connection stockDataConn = DbConn.getConnDB("stockConf/conn_data_db.ini"); 
        Connection stockPointConn = DbConn.getConnDB("stockConf/conn_point_db.ini");
        Connection stockSummaryConn = DbConn.getConnDB("stockConf/conn_summary_db.ini");
        
        stockLogger.logger.fatal("excel operation start");	
        StockExcelOperationMain seOp = new StockExcelOperationMain(stockBaseConn,stockDataConn,stockPointConn,stockSummaryConn);
		//����
        //"2016-04-11"
      //  seOp.analyseStockOperation(ConstantsInfo.StockMarket,"2016-07-19");
       seOp.analyseStockOperation(ConstantsInfo.StockMarket,"2016-07-29");
      // seOp.analyseStockOperation(ConstantsInfo.FuturesMarket,null);
        
      
      //  seOp.delete_data(ConstantsInfo.StockMarket);
        stockBaseConn.close();
	    stockDataConn.close();
	    stockPointConn.close();
	    stockSummaryConn.close();

	}

}
