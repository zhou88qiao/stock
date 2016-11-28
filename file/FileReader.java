package file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.PropertyConfigurator;

import com.calculation.stock.CalculationStock;
import com.point.stock.PointClass;

import common.ConstantsInfo;
import common.stockLogger;

import dao.BaseDao;
import dao.DayStock;
import dao.DayStockDao;
import dao.DbConn;
import dao.StockBaseDao;
import dao.StockConcept;
import dao.StockDataDao;
import dao.StockInformation;
import dao.StockInformationDao;
import dao.StockPointDao;
import dao.StockSingle;
import dao.StockSummaryDao;

public class FileReader {
	
	   static DayStockDao ds;
	   private StockBaseDao sbDao; 
	   private StockDataDao sdDao;
	   private StockPointDao spDao;
	   private StockSummaryDao ssDao;
	 //  static StockBaseDao sbDao =new StockBaseDao();
	   
	   
	   public FileReader(Connection stockBaseConn,Connection stockDataConn,Connection stockPointConn,Connection stockSummaryConn)
		{
		   this.sbDao = new StockBaseDao(stockBaseConn);
		   this.sdDao =new StockDataDao(stockDataConn);
		   this.spDao =new StockPointDao(stockPointConn);
		   this.ssDao = new StockSummaryDao(stockSummaryConn);
		}
	   
	   public FileReader(StockBaseDao sbDao,StockDataDao sdDao,StockPointDao spDao,StockSummaryDao ssDao)
		{
			this.sbDao = sbDao;
			this.sdDao = sdDao;
			this.spDao = spDao;
			this.ssDao = ssDao;
		}
	   
	/**
    * * ����Ϊ��λ��ȡ�ļ��������ڶ������еĸ�ʽ���ļ�
    * ���裺1���Ȼ���ļ����
    * 2������ļ��������������һ���ֽ���������Ҫ��������������ж�ȡ
    * 3����ȡ������������Ҫ��ȡ�����ֽ���
    * 4��һ��һ�е������readline()��
    * ��ע����Ҫ���ǵ����쳣���
    */
	 public static int readFileByLines(String filePath,String stockId, int type)
	 {
		 int stockDataNum = 0;  
		 long stockVolume =0;
		 try {
               String encoding="GBK";
               File file=new File(filePath);
               FileWriter fw = null;
               String wfPath=null;
                
               if (stockId.equals("SH999999"))  				
            	   wfPath="StockLoadFile\\"+"SH000001"+".sql";
               else {
            	   wfPath="StockLoadFile\\"+stockId+".sql";
               }
             //  System.out.println("wfpath:"+wfPath);
               String  tdx= "������Դ:ͨ���� ";
               fw = new FileWriter(wfPath);

               if(file.isFile() && file.exists()){ //�ж��ļ��Ƿ����
                   InputStreamReader read = new InputStreamReader(
                   new FileInputStream(file),encoding);//���ǵ������ʽ
                   BufferedReader bufferedReader = new BufferedReader(read);
                   String lineTxt = null;
                         
                   int line = 0;
                   while((lineTxt = bufferedReader.readLine()) != null){                 
                       line++;
                       //�ų���һ �ڶ� ���һ������
                       if(line==1 || line==2 )
                    	   continue;
                      // System.out.println(lineTxt);
                       
                       //ֻ����5�� ��ʱ�� ���̼� ���̼� ��߼� ��ͼۣ�
                       //ʱ��ĳ������� ��mysql����һ��
                       if(lineTxt.length()>36)//ȥ�����һ�� ����Ϊ8
                       { 
                    	   // stock_info=lineTxt.substring(0, 40);
                    	   //stock_info=lineTxt.substring(0, 40);    
                    	   String stock_time=lineTxt.substring(0, 10);              	
                    	//   System.out.println(stock_time);                   	 
                    	   
                    	   String[] sourceStrArray = lineTxt.split("\t");//�ָ�������ַ�����
                    	   /*
                           for (int i = 0; i < sourceStrArray.length; i++) {
                        	  // System.out.println(sourceStrArray[i]);
                           }
                           */
                    	  // System.out.println(sourceStrArray[0]+":"+sourceStrArray[5]);
                           stockVolume =  Long.parseLong(sourceStrArray[5]);                    	  
                          // stock_time=lineTxt.substring(0, 10);   
                           //���ܽ������Ƿ�Ϊ��
                           if(type == ConstantsInfo.FuturesMarket) {
                        	   stockDataNum++;
                        	   fw.write(lineTxt);
	                    	   fw.write("\r\n"); 
                           } else {
                        	   //ͣ�ƹ�Ʊ���ܵ���
	                           if(stockVolume>0) {
	                        	   stockDataNum++;
		                    	   fw.write(lineTxt);
		                    	   fw.write("\r\n"); 
	                           }
                           }
                       }   
                    
                   }
                //   System.out.println("line:"+line);
                   read.close();
                   fw.close();             
       } else {
           System.out.println("�Ҳ���ָ�����ļ�");
       }
       } catch (Exception e) {
           System.out.println("��ȡ�ļ����ݳ���");
           e.printStackTrace();
       }
       
       return stockDataNum;
	     
	}
	 
	 
	 //��ȡ�����ļ���һ�е�ʱ��
	 public static String readFileFirstLineDate(){
		 String stock_time = "";  
		 try {
			 String encoding="GBK";
			 String wfPath=null;            
        	 wfPath="StockData\\"+"SH999999.txt";
        	 File file=new File(wfPath);

        	 if(file.isFile() && file.exists()){ //�ж��ļ��Ƿ����
               InputStreamReader read = new InputStreamReader(new FileInputStream(file),encoding);//���ǵ������ʽ
               BufferedReader bufferedReader = new BufferedReader(read);
               String lineTxt = null;
               int line = 0;
               while((lineTxt = bufferedReader.readLine()) != null){                 
                   line++;
                   //�ų���һ �ڶ� ���һ������
                   if(line==1 || line==2 )
                	   continue;
                   stock_time=lineTxt.substring(0, 10);
                   System.out.println(stock_time);
                   break;
               }
               read.close();           
	       }else{
	           System.out.println("�Ҳ���ָ�����ļ�");
	       }
       } catch (Exception e) {
           System.out.println("��ȡ�ļ����ݳ���");
           e.printStackTrace();
       }
	     
       return stock_time;
	}
		 
	 
	 public void readFileByLinesForStockConcept(String filePath)
	 {
	      
	 try {
           String encoding="GBK";	             
           File file=new File(filePath);
           //FileWriter fw = null;
           String concept_name;
           int txtIndex=0;
           
           txtIndex=filePath.indexOf(".txt");
	       concept_name=filePath.substring(14, txtIndex);
	       
	       String conceptCode=null;
	       conceptCode=sbDao.getConceptCodeFromFirstIndeustyToConceptTable(concept_name);			
     
	       //
	       
           if(file.isFile() && file.exists()){ //�ж��ļ��Ƿ����               
        	   //ɾ��ԭ�и����Ʊ
        	   sbDao.deleteStockToConcept(conceptCode,concept_name);
        	   InputStreamReader read = new InputStreamReader(new FileInputStream(file),encoding);//���ǵ������ʽ
               BufferedReader bufferedReader = new BufferedReader(read);
               String lineTxt = null;	 
               String newlineTxt=null;
               String fullId=null;
               String Id=null;
               String zhengquan=null;
               String name=null;
               String newFullId=null;
               int line = 0;
               while((lineTxt = bufferedReader.readLine()) != null){
            //       System.out.println(lineTxt);
                   fullId=lineTxt.substring(0, 9);
                   System.out.println("id:"+fullId);
                   Id=lineTxt.substring(0, 6);
                   if(Id.charAt(0)== '6')
                	   newFullId="SH"+Id;
                   else
                	   newFullId="SZ"+Id;
                   /*
                   zhengquan=lineTxt.substring(7, 9);
                   System.out.println(Id);
                   System.out.println(zhengquan);                  
                   newFullId=zhengquan+Id;
                    */
                   System.out.println("fullId:"+newFullId);
                   name=lineTxt.substring(7,lineTxt.length());
                   System.out.println("stock_name:"+name);
                   line++;
                  // newlineTxt=lineTxt+"  "+concept_name;
                 //  System.out.println(newlineTxt);
                   stockLogger.logger.fatal("fullId��"+newFullId+"-name:"+name);
                   //���Ӹ���
                   if(conceptCode==null) {                	  
                	   sbDao.insertStockToConcept(newFullId,name," "," ",concept_name);
                   } else{      			 		
      			 		sbDao.insertStockToConcept(newFullId,name," ", conceptCode,concept_name);
      			 	}
                  
                //   fw.write(newlineTxt);
                 // fw.write("\r\n");                    	  
               }
              System.out.println("�ø�����:"+line+"��Ʊ");
               read.close();
           //   fw.close();             
       }else{
           System.out.println("�Ҳ���ָ�����ļ�");
       }
       } catch (Exception e) {
           System.out.println("��ȡ�ļ����ݳ���");
           e.printStackTrace();
       }
	     
	 }
	 
	 public void readFileByLinesForStockThirdIndustry(String filePath){
	      
		 try {
	               String encoding="GBK";	             
	               File file=new File(filePath);
	               //FileWriter fw = null;
	               String industry_name;
	               String newindustry_name;
	               int txtIndex=0;
	               String allindustry_name;
	               
	               
		          	 txtIndex=filePath.indexOf(".txt");
					 industry_name=filePath.substring(15, txtIndex);
				//	 if(industry_name.lastIndexOf(ch)=='��')
					// System.out.println("industry name:"+industry_name);	
					 if(industry_name.contains("��"))
					 {	
					 	newindustry_name=industry_name.substring(0, industry_name.lastIndexOf('��'));
					 	System.out.println("new industry name:"+newindustry_name);	
					 	allindustry_name=newindustry_name;
					 }
					 else
						 allindustry_name=industry_name;			
					 
					 
		            //   String wfPath="dataLoadMysql\\"+concept_name+".sql";
	              //  fw = new FileWriter(wfPath); 
	                              
	               if(file.isFile() && file.exists()){ //�ж��ļ��Ƿ����
	                   InputStreamReader read = new InputStreamReader(new FileInputStream(file),encoding);//���ǵ������ʽ
	                   BufferedReader bufferedReader = new BufferedReader(read);
	                   String lineTxt = null;	 
	                   String newlineTxt=null;
	                   String fullId=null;
	                   String Id=null;
	                   String zhengquan=null;
	                   String name=null;
	                   String newFullId=null;
	                   int line = 0;
	                   while((lineTxt = bufferedReader.readLine()) != null){
	                       System.out.println(lineTxt);
	                       fullId=lineTxt.substring(0, 9);
	                       System.out.println(fullId);
	                       Id=lineTxt.substring(0, 6);
	                       zhengquan=lineTxt.substring(7, 9);
	                       System.out.println(Id);
	                       System.out.println(zhengquan);
	                       newFullId=zhengquan+Id;
	                       System.out.println(newFullId);
	                       name=lineTxt.substring(12,lineTxt.length());
	                       System.out.println(name);
	                       line++;
	                       newlineTxt=lineTxt+"  "+allindustry_name;
	                     //  System.out.println(newlineTxt);
	                       sbDao.addStockToIndustry(newFullId,name,allindustry_name);
	                    //   fw.write(newlineTxt);
	                     // fw.write("\r\n");                    	  
	                   }
	                  System.out.println("�ø�����:"+line+"��Ʊ");
	                   read.close();
	               //   fw.close();             
	       }else{
	           System.out.println("�Ҳ���ָ�����ļ�");
	       }
	       } catch (Exception e) {
	           System.out.println("��ȡ�ļ����ݳ���");
	           e.printStackTrace();
	       }
		     
		}
	 
	 /**
	     * ���ֽ�Ϊ��λ��ȡ�ļ��������ڶ��������ļ�����ͼƬ��������Ӱ����ļ���
	     * 
	     * @param fileName
	     *            �ļ�����
	     */
	    public static void readFileByBytes(String fileName) {
	        File file = new File(fileName);
	        InputStream in = null;
	        try {
	            System.out.println("���ֽ�Ϊ��λ��ȡ�ļ����ݣ�һ�ζ�һ���ֽڣ�");
	            // һ�ζ�һ���ֽ�
	            in = new FileInputStream(file);
	            int tempbyte;
	            while ((tempbyte = in.read()) != -1) {
	                System.out.write(tempbyte);
	            }
	            in.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	            return;
	        }
	        try {
	            System.out.println("���ֽ�Ϊ��λ��ȡ�ļ����ݣ�һ�ζ�����ֽڣ�");
	            // һ�ζ�����ֽ�
	            byte[] tempbytes = new byte[100];
	            int byteread = 0;
	            in = new FileInputStream(fileName);
	            FileReader.showAvailableBytes(in);
	            // �������ֽڵ��ֽ������У�bytereadΪһ�ζ�����ֽ���
	            while ((byteread = in.read(tempbytes)) != -1) {
	                System.out.write(tempbytes, 0, byteread);
	            }
	        } catch (Exception e1) {
	            e1.printStackTrace();
	        } finally {
	            if (in != null) {
	                try {
	                    in.close();
	                } catch (IOException e1) {
	                }
	            }
	        }
	    }
	 
	    /**
	     * ���ַ�Ϊ��λ��ȡ�ļ��������ڶ��ı������ֵ����͵��ļ�
	     * 
	     * @param fileName
	     *            �ļ���
	     */
	    public static void readFileByChars(String fileName) {
	        File file = new File(fileName);
	        Reader reader = null;
	        try {
	            System.out.println("���ַ�Ϊ��λ��ȡ�ļ����ݣ�һ�ζ�һ���ֽڣ�");
	            // һ�ζ�һ���ַ�
	            reader = new InputStreamReader(new FileInputStream(file));
	            int tempchar;
	            while ((tempchar = reader.read()) != -1) {
	                // ����windows�£�\r\n�������ַ���һ��ʱ����ʾһ�����С�
	                // ������������ַ��ֿ���ʾʱ���ỻ�����С�
	                // ��ˣ����ε�\r����������\n�����򣬽������ܶ���С�
	                if (((char) tempchar) != '\r') {
	                    System.out.print((char) tempchar);
	                }
	            }
	            reader.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        try {
	            System.out.println("���ַ�Ϊ��λ��ȡ�ļ����ݣ�һ�ζ�����ֽڣ�");
	            // һ�ζ�����ַ�
	            char[] tempchars = new char[30];
	            int charread = 0;
	            reader = new InputStreamReader(new FileInputStream(fileName));
	            // �������ַ����ַ������У�charreadΪһ�ζ�ȡ�ַ���
	            while ((charread = reader.read(tempchars)) != -1) {
	                // ͬ�����ε�\r����ʾ
	                if ((charread == tempchars.length)
	                        && (tempchars[tempchars.length - 1] != '\r')) {
	                    System.out.print(tempchars);
	                } else {
	                    for (int i = 0; i < charread; i++) {
	                        if (tempchars[i] == '\r') {
	                            continue;
	                        } else {
	                            System.out.print(tempchars[i]);
	                        }
	                    }
	                }
	            }
	 
	        } catch (Exception e1) {
	            e1.printStackTrace();
	        } finally {
	            if (reader != null) {
	                try {
	                    reader.close();
	                } catch (IOException e1) {
	                }
	            }
	        }
	    }
	 
	 
	 /**
	     * �����ȡ�ļ�����
	     * 
	     * @param fileName
	     *            �ļ���
	     */
	    public static void readFileByRandomAccess(String fileName) {
	        RandomAccessFile randomFile = null;
	        try {
	            System.out.println("�����ȡһ���ļ����ݣ�");
	            // ��һ����������ļ�������ֻ����ʽ
	            randomFile = new RandomAccessFile(fileName, "r");
	            // �ļ����ȣ��ֽ���
	            long fileLength = randomFile.length();
	            // ���ļ�����ʼλ��
	            int beginIndex = (fileLength > 4) ? 4 : 0;
	            // �����ļ��Ŀ�ʼλ���Ƶ�beginIndexλ�á�
	            randomFile.seek(beginIndex);
	            byte[] bytes = new byte[10];
	            int byteread = 0;
	            // һ�ζ�10���ֽڣ�����ļ����ݲ���10���ֽڣ����ʣ�µ��ֽڡ�
	            // ��һ�ζ�ȡ���ֽ�������byteread
	            while ((byteread = randomFile.read(bytes)) != -1) {
	                System.out.write(bytes, 0, byteread);
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            if (randomFile != null) {
	                try {
	                    randomFile.close();
	                } catch (IOException e1) {
	                }
	            }
	        }
	   }
	   
	    /**
	     * ��ʾ�������л�ʣ���ֽ���
	     * 
	     * @param in
	     */
	    private static void showAvailableBytes(InputStream in) {
	        try {
	            System.out.println("��ǰ�ֽ��������е��ֽ���Ϊ:" + in.available());
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	
	   //��ȡĿ¼�ļ���
	  private static List<String> getListFiles(String path)
	  {
		 List<String> lstFileNames=new ArrayList<String>();
		 File file=new File(path);
		 int fileNums=0;
			
		  if(file.isDirectory())
		  {
			  File []tmpList=file.listFiles();
			  for(int i=0;i<tmpList.length;i++)
			  {
				  if(tmpList[i].isFile())
				  {
					  fileNums++;
					  lstFileNames.add(tmpList[i].getPath());
				  }
			  }
		  }
		 
		System.out.println("fileNums:"+fileNums);
		  return lstFileNames;
	  }
	  
	  private static void createTable(String stock_name) throws IOException, ClassNotFoundException, SQLException
	  {
		  Date time= new java.sql.Date(new java.util.Date().getTime());
		//  BigInteger vol=new BigInteger("111111" ); 
		  DayStock stif=new DayStock(time,2.0f,2.0f,2.2f,2.3f,2,2.0f,0,0);
		  ds.createTable(stock_name,1);
	  }
	  
	  
	  private void loadDataInfileForConcept(String concept_name) throws IOException, ClassNotFoundException, SQLException
	  {
		  
		//  String stock_name=filePath.substring(12, 20);
          String wfPath="D:\\\\Javawork\\\\stock\\\\dataLoadMysql\\\\"+concept_name+".sql";
          sbDao.loadDataInfileForConcept(wfPath,concept_name);
	  }
	  
	  /**
	   *  1 ���뵱�콻������   ����ma �Ƿ� ������������ӹ�Ʊ����Ҫ�������ױ���������㼫��
	 * @param type
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws NoSuchFieldException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws SecurityException 
	 */
	public int loadAllDataInfile(String loadDate) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException
	{
		
		CalculationStock cas =new CalculationStock(sbDao,sdDao);
		
		String dirPath = "StockData\\";
		 List<String> lstStockFileNames=null;
		 lstStockFileNames= getListFiles(dirPath);
		 String stock_time = "";
		 int stockDataNum =0;
		 String stock_source_data=null;
		 String stock_name=null;
		 int stockFile=0;
		 int isTableExist=0;
		 String loadfilePath ="";
		 String curPath=System.getProperty("user.dir");
		 curPath = curPath.replaceAll("\\\\", "/"); //ת�彫\ תΪ/
		 int id =0;
		
		 //�ж��ظ����� ��ȡSH99999.txt�ļ�
		 stock_time= readFileFirstLineDate();		
		 
		 id = sdDao.getDataValueIsExist("SH000001",stock_time);
		 if(id > 0) {
			 return 1;
		 }
		
		 //���stock_load_FullId����Ϊ��һ������ʱ��
		 sbDao.truncateStockLoadFullId(ConstantsInfo.StockMarket);

		 for (int i=0;i<lstStockFileNames.size();i++)		 
		 {
			 stock_source_data = lstStockFileNames.get(i);	
			 	 
			 int begin = stock_source_data.indexOf('\\');
			 int end = stock_source_data.indexOf('.');
			 stock_name = stock_source_data.substring(begin+1, end);//stock_source_data.substring(10, 18);
			 System.out.println("stock name:"+stock_name);
			 stockLogger.logger.fatal("StockFullId:"+stock_name);
			 if (stock_name.equals("SH999999")) //����֤ȯ999999��Ϊ000001
				 stock_name="SH000001";
			
			 sbDao.insertStockLoadFullId(ConstantsInfo.StockMarket,stock_name);
			// if(!stock_name.equals("SH601899"))//ֻ����SH000001
			//	 continue;	
		
			 isTableExist=sdDao.isExistStockTable(stock_name,ConstantsInfo.TABLE_DATA_STOCK);
			 if(isTableExist==0)//������
			 {
				 stockLogger.logger.fatal(stock_name + " not found");
				 stockLogger.logger.fatal(stock_name + " creat table");
				 sdDao.createStockDataTable(stock_name);	
			 }
			 
			 
			 stockDataNum = readFileByLines(stock_source_data,stock_name,ConstantsInfo.StockMarket);
			//�Ѿ����ڱ��ս������ݻ�ͣ�Ʋ��õ���
			 if((isTableExist>0) && (stockDataNum <= 0)) {
				 System.out.println("load stock data num:"+stockDataNum);
				 stockLogger.logger.fatal("load stock data num:"+stockDataNum);
				 continue;
			 }
			 
			 //ɾ�����������
			// sdDao.delStockDataDay(stock_name,loadDate);
			 //�޸�Ĭ��ֵ
			// sdDao.alterStockDeafaultDataType(stock_name);
			
			 loadfilePath=curPath+"/StockLoadFile/"+stock_name+".sql";
			 sdDao.loadDatafFiletoDB(loadfilePath,stock_name);
			
			 if(isTableExist==0) {//������				 
				 stockLogger.logger.fatal("cal new stock table");
				//����ȫ��ma5 �Ƿ�	
				 cas.calculStockAllDataForSingleStock(stock_name,ConstantsInfo.StockCalAllData); 		 
				 //���������
				 spDao.createStockPointTable(stock_name);
				 //�������ܱ�
				 ssDao.createStockSummaryTable(stock_name);
				 
				 PointClass pc =new PointClass(sbDao,sdDao,spDao);
				 //���㼫��
				 pc.getPiontToTableForSingleStock(stock_name,ConstantsInfo.StockCalAllData);
			 }else {				
				 cas.calculStockAllDataForSingleStock(stock_name,ConstantsInfo.StockCalCurData); //���㲿��
			 }
			
			stockFile++;
			
		 }
		 System.out.println("stockFile:"+stockFile);
		
		return 0;
	  }
	
	//������Ʒ����
	public int loadAllFuturesDataInfile(String loadDate) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException
	{
		
		CalculationStock cas =new CalculationStock(sbDao,sdDao);
		
		String dirPath = "FuturesData\\";
		 List<String> lstStockFileNames=null;
		 lstStockFileNames= getListFiles(dirPath);
		 int stockDataNum =0;
		 String stock_source_data=null;
		 String stock_name=null;
		 int stockFile=0;
		 int isTableExist=0;
		 String loadfilePath ="";
		 String curPath=System.getProperty("user.dir");
		 curPath = curPath.replaceAll("\\\\", "/"); //ת�彫\ תΪ/
		 int id =0;
		
		 /*
		 //�ж��ظ����� ��ȡSH99999.txt�ļ�
		 stock_time= readFileFirstLineDate();
		
		 id = sdDao.getDataValueIsExist("SH000001",stock_time);
		 if(id > 0) {
			 return 1;
		 }
		*/
		 //���stock_load_FullId����Ϊ��һ������ʱ��
		 sbDao.truncateStockLoadFullId(ConstantsInfo.FuturesMarket);

		 for (int i=0;i<lstStockFileNames.size();i++)		 
		 {
			 stock_source_data = lstStockFileNames.get(i);	
			 
			// System.out.println(stock_source_data);
			 int begin = stock_source_data.indexOf('\\');
			 begin +=2;//ȥ��ǰ�������ַ�
			 int end = stock_source_data.indexOf('.');
			 stock_name = stock_source_data.substring(begin+1, end);//stock_source_data.substring(10, 18);
			 
			 //ȥ���ո���&
			 stock_name = stock_name.replaceAll(" ", "");
			 stock_name = stock_name.replaceAll("&", "");
			 
			 System.out.println("stock name:"+stock_name);
			 stockLogger.logger.fatal("StockFullId:"+stock_name);
			 if (stock_name.equals("SH999999")) //����֤ȯ999999��Ϊ000001
				 stock_name="SH000001";
			
			 sbDao.insertStockLoadFullId(ConstantsInfo.FuturesMarket,stock_name);
			// if(!stock_name.equals("SH601899"))//ֻ����SH000001
			//	 continue;	
					
			 
			 isTableExist=sdDao.isExistStockTable(stock_name,ConstantsInfo.TABLE_DATA_STOCK);
			 if(isTableExist==0)//������
			 {
				 stockLogger.logger.fatal(stock_name + " not found");
				 stockLogger.logger.fatal(stock_name + " creat table");
				 sdDao.createStockDataTable(stock_name);	
			 }
			 
			 stockDataNum = readFileByLines(stock_source_data,stock_name,ConstantsInfo.FuturesMarket);				
			 
			 //ɾ�����������
			// sdDao.delStockDataDay(stock_name,loadDate);
			 //�޸�Ĭ��ֵ
			// sdDao.alterStockDeafaultDataType(stock_name);
			
			 loadfilePath=curPath+"/StockLoadFile/"+stock_name+".sql";
			 int ret = sdDao.loadDatafFiletoDB(loadfilePath,stock_name);
			 stockLogger.logger.fatal("load data nums:"+ret);
			 System.out.println("load data nums:"+ret);
			
			 if(isTableExist==0) {//������
				 
				 stockLogger.logger.fatal("cal new stock table");
				//����ȫ��ma5 �Ƿ�	
				 cas.calculStockAllDataForSingleStock(stock_name,ConstantsInfo.StockCalAllData); 		 
				 //���������
				 spDao.createStockPointTable(stock_name);
				 //�������ܱ�
				 ssDao.createStockSummaryTable(stock_name);
				 
				 PointClass pc =new PointClass(sbDao,sdDao,spDao);
				 //���㼫��
				 pc.getPiontToTableForSingleStock(stock_name,ConstantsInfo.StockCalAllData);
			 }else {				
				 cas.calculStockAllDataForSingleStock(stock_name,ConstantsInfo.StockCalCurData); //���㲿��
			 }
			
			stockFile++;
			
		 }
		 System.out.println("stockFile:"+stockFile);
		
		return 0;
	  }
	
	
	//ĳ����Ʊ����
	public int loadSingleAllDataInfile(String loadDate) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException
	{
		
		CalculationStock cas =new CalculationStock(sbDao,sdDao);
		
		String dirPath = "StockData\\";
		 List<String> lstStockFileNames=null;
		 lstStockFileNames= getListFiles(dirPath);

		 String stock_name=null;
		 int stockFile=0;
		 int isTableExist=0;
		 String loadfilePath ="";
		 String curPath=System.getProperty("user.dir");
		 curPath = curPath.replaceAll("\\\\", "/"); //ת�彫\ תΪ/
		

			 stock_name ="D12";
			 /*			
			 System.out.println("stock name:"+stock_name);
			 stockLogger.logger.fatal("StockFullId:"+stock_name);
			 isTableExist=sdDao.isExistStockTable(stock_name,ConstantsInfo.TABLE_DATA_STOCK);
			 if(isTableExist==0)//������
			 {
				 stockLogger.logger.fatal(stock_name + "not found");
				 stockLogger.logger.fatal(stock_name + "creat table");
				 sdDao.createStockDataTable(stock_name);	
			 }
	*/		
			 //ɾ�����������
			// sdDao.delStockDataDay(stock_name,loadDate);

			 //�޸�Ĭ��ֵ
			 //	 sdDao.alterStockDeafaultDataType(stock_name);
			
			 loadfilePath=curPath+"/StockLoadFile/"+stock_name+".sql";
			 int ret = sdDao.loadDatafFiletoDB(loadfilePath,stock_name);
			System.out.println("ret:"+ret);
			 if(isTableExist==0) {//������
				 
				 stockLogger.logger.fatal("cal new stock table");
				//����ȫ��ma5 �Ƿ�	
				 cas.calculStockAllDataForSingleStock(stock_name,ConstantsInfo.StockCalAllData); 		 
				 //���������
				 spDao.createStockPointTable(stock_name);
				 PointClass pc =new PointClass(sbDao,sdDao,spDao);
				 //���㼫��
				 pc.getPiontToTableForSingleStock(stock_name,ConstantsInfo.StockCalAllData);
			 }else {				
				 cas.calculStockAllDataForSingleStock(stock_name,ConstantsInfo.StockCalCurData); //���㲿��
			 }

			stockFile++;
			
		
		 System.out.println("stockFile:"+stockFile);
		
		return 0;
	  }
	
	
	public int deleteDataInfile() throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException
	{
		
		CalculationStock cas =new CalculationStock(sbDao,sdDao);
		String dirPath = "StockData\\";
		 List<String> lstStockFileNames=null;
		 lstStockFileNames= getListFiles(dirPath);
		
		 String stock_source_data=null;
		 String stock_name=null;
		 int stockFile=0;
		 int isTableExist=0;
		 String loadfilePath ="";
		 String curPath=System.getProperty("user.dir");
		 curPath = curPath.replaceAll("\\\\", "/"); //ת�彫\ תΪ/
	
		 for (int i=0;i<lstStockFileNames.size();i++)		 
		 {
			
			 stock_source_data = lstStockFileNames.get(i);	
		
			 stock_name=stock_source_data.substring(10, 18);
			
			if (stock_name.equals("SH999999")) //����֤ȯ999999��Ϊ000001
				 stock_name="SH000001";
			 
			//if(!stock_name.equals("SH000001")) //ֻ����SH000001
			//	 continue;
			
			 System.out.println("stock name:"+stock_name);
			
			 isTableExist=sdDao.isExistStockTable(stock_name,ConstantsInfo.TABLE_DATA_STOCK);
			 if(isTableExist==0)//������
			 {
				 continue;
			 }
			
			 //ɾ�����������
		//	 sdDao.delStockDataDay(stock_name,loadDate);
			 sdDao.delStockDataDay(stock_name,"2015-09-22");
			 sdDao.delStockDataDay(stock_name,"2015-09-23");
			 spDao.delStockPointDay(stock_name,"2015-09-22");
			 spDao.delStockPointDay(stock_name,"2015-09-23");
			
		 }
		 System.out.println("stockFile:"+stockFile);
		
		return 0;
	  }
	  
	  
	  //��ȡ�����ļ��и����ļ��� �����������
	  private static void loadAllConceptInfile() throws IOException, ClassNotFoundException, SQLException
	  {
		  StockBaseDao sbDao=new StockBaseDao();		
		  
		  String dirPath = "dataConcept\\";
			 List<String> lstStockConceptNames=null;
			 lstStockConceptNames= getListFiles(dirPath);
			 ListIterator<String> lit=lstStockConceptNames.listIterator();
			 String stock_source_data=null;
			 String concept_name=null;
			 int stockConceptFile=0;
			 int txtIndex=0;
			 while(lit.hasNext())
			 {
				 stock_source_data=lit.next().toString();
				 
				// System.out.println("load file name:"+stock_source_data);
			
				 txtIndex=stock_source_data.indexOf(".txt");
				 concept_name=stock_source_data.substring(12, txtIndex);
				// if(!concept_name.equals("4G"))
				//	 continue;
				 System.out.println("concept name:"+concept_name);
				
				 StockConcept sConcept=new StockConcept(1,"",concept_name);
				 sbDao.insertStockConcept(sConcept);
				
				 stockConceptFile++;
			 }
			 System.out.println("stockConceptFile:"+stockConceptFile);
	  }
	  
	  
	//��ȡ�����ļ��и����ļ��ڸ������µĹ�Ʊ����Ʊ�����
	  public  int  loadAllConceptContainStockInfile() throws IOException, ClassNotFoundException, SQLException
	  {
		  	
		    String dirPath = "StockBaseData\\";//14�ֽ�
			 List<String> lstStockConceptNames=null;
			 lstStockConceptNames= getListFiles(dirPath);
			 ListIterator<String> lit=lstStockConceptNames.listIterator();
			 String stock_source_data=null;
			 String concept_name=null;
			 int stockConceptFile=0;
			 int txtIndex=0;
			 
			 while(lit.hasNext())
			 {
				 stock_source_data=lit.next().toString();		
				
				 if(!stock_source_data.contains(".txt"))
					 continue;
				 txtIndex=stock_source_data.indexOf(".txt");
				 concept_name=stock_source_data.substring(14, txtIndex);
				// if(!concept_name.equals("4G"))
				//	 continue;
				 System.out.println("concept name:"+concept_name);	
				 stockLogger.logger.fatal("���"+concept_name);
				
				 String code=null;
				 //ɾ������
				 sbDao.deleteConceptFromConceptTable(concept_name);
				 //���Ӹ���  
				 code=sbDao.getConceptCodeFromFirstIndeustyToConceptTable(concept_name);
				 if(code==null){					 
					 stockLogger.logger.fatal("����code��null");
					 sbDao.insertStockConceptInfo(" ",concept_name);					
				 } else {
					 stockLogger.logger.fatal("����code��"+code);
					 sbDao.insertStockConceptInfo(code,concept_name);
				 }
				 //��ȡ�����ļ����ݲ������µ�����
				 readFileByLinesForStockConcept(stock_source_data);					
				 stockConceptFile++;			
			
			 }
			 System.out.println("stockConceptFile:"+stockConceptFile);
			 return 0;
	  }
	  
	 public static void loadreadIndustryInfile() throws IOException, ClassNotFoundException, SQLException
	  {
		  StockBaseDao sbDao=new StockBaseDao();	
		    String dirPath = "dataIndustry\\";
			 List<String> lstStockIndustryNames=null;
			 lstStockIndustryNames= getListFiles(dirPath);
			 ListIterator<String> lit=lstStockIndustryNames.listIterator();
			 String stock_source_data=null;
			 String industry_name=null;
			 String allindustry_name=null;
			 String newindustry_name=null;
			 int stockIndustryFile=0;
			 int txtIndex=0;
			 while(lit.hasNext())
			 {
				 stock_source_data=lit.next().toString();
				
				// System.out.println("load file name:"+stock_source_data);
				 
				 txtIndex=stock_source_data.indexOf(".txt");
				 industry_name=stock_source_data.substring(15, txtIndex);
					
				 if(industry_name.contains("��"))
				 {	
				 	newindustry_name=industry_name.substring(0, industry_name.lastIndexOf('��'));
				 //	System.out.println("new industry name:"+newindustry_name);	
				 	allindustry_name=newindustry_name;
				 }
				 else
					 allindustry_name=industry_name;	
				 System.out.println(allindustry_name);	
				 sbDao.insertStockIndustryFromSW(allindustry_name);
				// if(!industry_name.equals("LED"))
				//	 continue;
			 }
	  }
	  
	  //��ȡ������ҵ�ļ��и����ļ��ڸ���ҵ�µĹ�Ʊ����Ʊ��ҵ��
	  private  void loadAllIndustryContainStockInfile() throws IOException, ClassNotFoundException, SQLException
	  {
		  StockBaseDao sbDao=new StockBaseDao();	
		    String dirPath = "dataIndustry\\";
			 List<String> lstStockIndustryNames=null;
			 lstStockIndustryNames= getListFiles(dirPath);
			 ListIterator<String> lit=lstStockIndustryNames.listIterator();
			 String stock_source_data=null;
			 String industry_name=null;
			 String newindustry_name=null;
			 int stockIndustryFile=0;
			 int txtIndex=0;
			 while(lit.hasNext())
			 {
				 stock_source_data=lit.next().toString();
				
				// System.out.println("load file name:"+stock_source_data);
				 
				 txtIndex=stock_source_data.indexOf(".txt");
				 industry_name=stock_source_data.substring(15, txtIndex);
				System.out.println(industry_name);		
				// if(!industry_name.equals("LED"))
				//	 continue;
				 //��ȡ�����ļ����ݲ������µ�����
				 readFileByLinesForStockThirdIndustry(stock_source_data);
				 	
				 stockIndustryFile++;
				// sbDao.setCharacter("set names gb2312");
				 //����
				// loadDataInfileForConcept(concept_name);
				 System.out.println("��stockIndustryFile:"+stockIndustryFile);
			 }
			 System.out.println("stockIndustryFile:"+stockIndustryFile);
	  }
	  
	  
	  
	public void loadDataInit() throws IOException, ClassNotFoundException, SQLException
	{
		
	}
	  
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException {
		
		 Connection stockBaseConn = DbConn.getConnDB("stockConf/conn_base_db.ini");  
	     Connection stockDataConn = DbConn.getConnDB("stockConf/conn_data_db.ini"); 
	     Connection stockPointConn = DbConn.getConnDB("stockConf/conn_point_db.ini");  
	     Connection stockSummaryConn = DbConn.getConnDB("stockConf/conn_summary_db.ini");

		FileReader fr=new FileReader(stockBaseConn,stockDataConn,stockPointConn,stockSummaryConn);
		
		PropertyConfigurator.configure("StockConf/log4j.properties");
		java.util.Date startDate = new java.util.Date();
		System.out.println("file load start");
		stockLogger.logger.fatal("file load start");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
        String dateNowStr = sdf.format(startDate); //"2015-09-11";	
		
		
			
		
		//loadAllConceptInfile(); //���������
		//fr.loadAllConceptContainStockInfile();//��������¶�Ӧ��Ʊ
		//loadreadIndustryInfile();//��ȡ������ҵ��
		//loadAllIndustryContainStockInfile(); //����������ҵ�¶�Ӧ ��Ʊ
     fr.loadAllFuturesDataInfile(dateNowStr);
      fr.loadAllDataInfile(dateNowStr);
 	//	fr.loadSingleAllDataInfile(dateNowStr);	
		
		//fr.deleteDataInfile();
		
		java.util.Date endDate = new java.util.Date();
		// ��������ʱ�����������
		long seconds =(endDate.getTime() - startDate.getTime())/1000;
		System.out.println("�ܹ���ʱ��"+seconds+"��");
		System.out.println("file load end");		
		
		
		stockBaseConn.close();
	    stockDataConn.close();
	    stockPointConn.close();
	    stockSummaryConn.close();
		stockLogger.logger.fatal("file load end");
	}
}
