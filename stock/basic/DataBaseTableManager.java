package stock.basic;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import common.ConstantsInfo;

import dao.DbConn;
import dao.StockBaseDao;
import dao.StockDataDao;
import dao.StockInformationDao;
import dao.StockMarket;
import dao.StockPointDao;

/*���ݿ��ṹ����*/
/**   
 * This class is used for ...   
 * @author zhouqiao  
 * @version   
 *       1.0, 2015-7-18 ����03:12:13   
 */    
public class DataBaseTableManager {
	
	 static StockDataDao sdDao;
	 static StockBaseDao sbDao;
	 static StockPointDao spDao;
	DataBaseTableManager()
	{
		
	}
	
	/** ������Ʊ���ױ�
	 * @param tableType 0���ݱ� 1��ֵ��
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public void createDataTable(int tableType) throws IOException, ClassNotFoundException, SQLException
	{
		List<String> listStockFullId = new ArrayList<String>(); 
		
		listStockFullId=sbDao.getAllStockFullId(ConstantsInfo.StockMarket);	
	
		for(int i = 0; i < listStockFullId.size(); i++)  
        {  
			 String fullId = listStockFullId.get(i);  
			 System.out.println(fullId);
		//	 if(!fullId.equals("SH600000"))
			//	continue;
			 if (tableType == 0)
				 sdDao.createStockDataTable(fullId);
			 else
			 	spDao.createStockPointTable(fullId);
        }
           
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
		Connection stockBaseConn = DbConn.getConnDB("stockConf/conn_base.ini");  
		Connection stockDataConn = DbConn.getConnDB("stockConf/conn_data.ini");  
		 Connection stockPointConn = DbConn.getConnDB("stockConf/conn_point.ini");  
		sbDao=new StockBaseDao(stockBaseConn);
		sdDao =new StockDataDao(stockDataConn);
		spDao =new StockPointDao(stockPointConn);
		DataBaseTableManager dbtManager = new DataBaseTableManager();
		//0 ���ݱ� 1��ֵ��
		dbtManager.createDataTable(0);
		stockBaseConn.close();
		stockPointConn.close();
		stockDataConn.close();
	}

}
