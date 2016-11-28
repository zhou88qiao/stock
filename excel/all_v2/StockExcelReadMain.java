package excel.all_v2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import common.ConstantsInfo;
import common.stockLogger;

import dao.DbConn;
import dao.StockBaseDao;
import dao.StockDataDao;
import dao.StockPointDao;
import dao.StockSummary;
import dao.StockSummaryDao;


//��ȡ�����������뵽���ݿ���
public class StockExcelReadMain {
	
	private StockDataDao sdDao;
	private StockPointDao spDao;
	private StockBaseDao sbDao;
	private StockSummaryDao ssDao;
	
	public StockExcelReadMain(Connection stockBaseConn,Connection stockDataConn,Connection stockPointConn,Connection stockSummaryConn)
	{
		   this.sbDao = new StockBaseDao(stockBaseConn);
		   this.sdDao =new StockDataDao(stockDataConn);
		   this.spDao =new StockPointDao(stockPointConn);
		   this.ssDao = new StockSummaryDao(stockSummaryConn);
	}
    
    public StockExcelReadMain(StockBaseDao sbDao,StockDataDao sdDao,StockPointDao spDao,StockSummaryDao ssDao)
	{
		this.sbDao = sbDao;
		this.sdDao = sdDao;
		this.spDao = spDao;
		this.ssDao = ssDao;
	}
	
	 //��ȡĿ¼�ļ���
	  private static List<String> getListExcelFiles(String path,String date,int type)
	  {
		 List<String> lstFileNames=new ArrayList<String>();
		 File file=new File(path);
		 int fileNums=0;
		 int flag = 0;
			
		  if(file.isDirectory())
		  {
			  File []tmpList=file.listFiles();
			  for(int i=0;i<tmpList.length;i++)
			  {
				  flag = 0; //
				  if(tmpList[i].isFile())
				  {
					  if(type == ConstantsInfo.StockMarket) 
					  {
						  if(tmpList[i].getPath().contains("Stock_Industry_"+date) && tmpList[i].getPath().contains("All"))
							  flag = 1;	  			
					  }  else {  
						  if(tmpList[i].getPath().contains("Stock_Futures_"+date) && tmpList[i].getPath().contains("All"))
								flag = 1;
					  }
					  
					  if(flag == 1) {
						  fileNums++;
						  lstFileNames.add(tmpList[i].getPath());
						  System.out.println(tmpList[i].getPath());
					  }
					  
				  }
			  }
		  }
	 
		  System.out.println("fileNums:"+fileNums);
		  return lstFileNames;
	  }
	
	 public int  readStockAnsyleExcelData(int type) throws IOException, ClassNotFoundException, SQLException
	 {

		String [] stock= new String[250];
		HSSFCell cell;     	
		XSSFWorkbook xb = null;
		Sheet xsheet;

		Date fileDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
        String dateNowStr = sdf.format(fileDate); 
		 
		String curPath=System.getProperty("user.dir");
		
		String excelPath = curPath+"\\export\\"+dateNowStr;
		System.out.println(excelPath);
		
		//�õ� ��Ӧ�г��ļ���
		List<String> lstStockFileNames=null;
		lstStockFileNames = getListExcelFiles(excelPath,dateNowStr,type);
		System.out.println(lstStockFileNames.size());
		
		int flag =0;//����ֻ����һ��
	
		 for (int filecount=0;filecount<lstStockFileNames.size();filecount++)		 
		 {
			 String fileName = lstStockFileNames.get(filecount);	
			 fileName = fileName.replaceAll("\\\\", "/"); //ת�彫\ תΪ/
			 System.out.println("filePATH:"+fileName);			 
			 
			 File file = new File(fileName);    
			 if(file==null || !file.exists())  {   
				System.out.println("no xlsx");
			    return -1;  
			 }  
			 
			 InputStream is = new FileInputStream(fileName);  
			 if(is == null) {
				 return -1;
			 }

			try {
			    xb = new XSSFWorkbook(is);
			} catch (IOException e) {
			    e.printStackTrace();
			}
			
			xsheet = xb.getSheetAt(0);
		  
			// �õ�������
			int rowNum = xsheet.getLastRowNum();
			System.out.println("������"+rowNum);
			Row xrow = xsheet.getRow(1);
			int colNum = xrow.getPhysicalNumberOfCells();//����
			System.out.println("������"+colNum);
			// ��������Ӧ�ôӵڶ��п�ʼ,��һ��Ϊ��ͷ�ı���
		    
			//�ų���ǩǰ����
			String fullId="";
				
			//���� ֻ����һ�� 
			
			//��Ʒ��������֤��Ҳ��Ҫȥ��
			
			int i = 0;
			 if(type == ConstantsInfo.StockMarket) 
			 {
				  i = (flag==0)?2:7;	  			
			 }  else {  
				 i = 7;//ȥ����֤
			 }
		    for (; i <= rowNum; i++) {
		        xrow = xsheet.getRow(i);
		   
		        int j=0;//��ȡȫ���� ��һ�����ֲ���Ҫ         
				for (j = 1; j < colNum; j++) {   
				  stock[j] = ""; 
				  if(xrow.getCell(j)==null) {
					  stock[j] ="";
					  //	  break;
				  } else {
				  	  xrow.getCell(j).setCellType(Cell.CELL_TYPE_STRING);
				  	//  stock[j]=getCellFormatValue(xrow.getCell((short) j))
				  	  stock[j] = xrow.getCell(j).getStringCellValue();
				  }
				  
				}
		    
				//fullId
				 if (stock[1] == null || stock[1].equals("")|| stock[2] == null || stock[2].equals("")) {
				 	  continue;
				 }
				 fullId = stock[1];
				 int isTableExist=sdDao.isExistStockTable(fullId,ConstantsInfo.TABLE_SUMMARY_STOCK);
   		    	 if(isTableExist == 0){//������
   					ssDao.createStockSummaryTable(fullId);
   				 }
		    
				  String wfPath=curPath+"\\StockLoadExcel\\"+fullId+".sql"; 
				  FileWriter fw = new FileWriter(wfPath);   
				 for (j = 1; j < colNum; j++) {  
					  fw.write(stock[j]+",");
				 }
				 fw.write("\\r\\n");
				 fw.close();
				 //����·��
				 String  loadfilePath = wfPath.replaceAll("\\\\", "/"); //ת�彫\ תΪ/
				
				 ssDao.loadSummaryFiletoDB(loadfilePath,stock[1]);
		    
		  }
		    
		  //���ƴ���
			if(flag ==0)
				flag =1;		    
		   
		   is.close();
		
		}
		return 0;
	 }
	
	
	 
	 public  void create_summarY(String date) throws IOException, ClassNotFoundException, SQLException
	{
		
			ssDao.createStockSummaryTable("sh000001");
			ssDao.createStockSummaryTable("sz399001");
			ssDao.createStockSummaryTable("sz399005");
			ssDao.createStockSummaryTable("sz399006");
			
			//ssDao.insertStockSummaryTable("sh000001",ssu);
	}
	 
	
	 
	 
	/**
	 * @param args
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
		// TODO Auto-generated method stub
		
	   Connection stockBaseConn = DbConn.getConnDB("stockConf/conn_base_db.ini"); 
        Connection stockDataConn = DbConn.getConnDB("stockConf/conn_data_db.ini"); 
        Connection stockPointConn = DbConn.getConnDB("stockConf/conn_point_db.ini");
        Connection stockSummaryConn = DbConn.getConnDB("stockConf/conn_summary_db.ini");
		
		StockExcelReadMain seRead = new StockExcelReadMain(stockBaseConn,stockDataConn,stockPointConn,stockSummaryConn);
		//seRead.create_summarY("123");
		//seRead.readStockAnsyleExcelData(ConstantsInfo.StockMarket);
		seRead.readStockAnsyleExcelData(ConstantsInfo.FuturesMarket);
		
		stockBaseConn.close();
	    stockDataConn.close();
	    stockPointConn.close();
	    stockSummaryConn.close();

	}

}
