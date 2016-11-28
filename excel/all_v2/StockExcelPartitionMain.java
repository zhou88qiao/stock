package excel.all_v2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.Collator;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.point.stock.PointClass;
import common.ConstantsInfo;
import common.stockLogger;

import dao.DbConn;
import dao.StockBaseDao;
import dao.StockBaseFace;
import dao.StockBaseYearInfo;
import dao.StockConcept;
import dao.StockConceptInFirstIndustry;
import dao.StockData;
import dao.StockDataDao;
import dao.StockIndustry;
import dao.StockMarket;
import dao.StockOperation;
import dao.StockPoint;
import dao.StockPointDao;
import dao.StockSingle;
import dao.StockSummary;
import dao.StockSummaryDao;
import dao.StockToConcept;
import dao.StockToFutures;
import dao.StockToIndustry;
import date.timer.stockDateTimer;

//��Ƭ����excel �ֳɶ��excel

public class StockExcelPartitionMain {

	private StockDataDao sdDao;
	private StockPointDao spDao;
	private StockBaseDao sbDao;
	private StockSummaryDao ssDao;

	static PointClass pClass=new PointClass();

	
 	//static ExcelCommon eCommon=new ExcelCommon();
 	static int stockRow = 1;
 	static int stockTotalRow = 1;
 	static int stockDesiredDate = 10;	
 	static int stockMaxRow = 10;	
 	static int sheetCount=0;
 	
 	String SHDate=null; //��¼��֤���̵�ǰʱ�䣬������������Ʊʱ�����Աȣ��ǲ���ͣ������
 	
 	//��¼market ��������Ʊ�Աȼ���
 	StockCurValue MarketStockCurInfo[][]=  new StockCurValue[4][3];
 	//ȫ�ֱ��涨λͳ������ʱ
 	HashMap<String, Integer> stockDateColumnmap = new HashMap<String,Integer>();
 	//����������λС����
 	static DecimalFormat decimalFormat=new DecimalFormat(".00");
 	
 	public StockExcelPartitionMain(Connection stockBaseConn,Connection stockDataConn,Connection stockPointConn,Connection stockSummaryConn)
	{
		   this.sbDao = new StockBaseDao(stockBaseConn);
		   this.sdDao =new StockDataDao(stockDataConn);
		   this.spDao =new StockPointDao(stockPointConn);
		   this.ssDao = new StockSummaryDao(stockSummaryConn);
	}
    
    public StockExcelPartitionMain(StockBaseDao sbDao,StockDataDao sdDao,StockPointDao spDao,StockSummaryDao ssDao)
	{
		this.sbDao = sbDao;
		this.sdDao = sdDao;
		this.spDao = spDao;
		this.ssDao = ssDao;
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
 		if(value1==0)
 			return 0;
 		float tmpValue = (float) (value2 - value1)/value1;
		float gap = getDec(tmpValue,4);
		return gap;
 	} 	 
 	 
 	//��ȡ������ʾ
 	public int getDealWarn(int flag,int pointSuspectedDateGAP,int pointCurDateGAP,int suspectedCurDateGap)
 	{
 		int ret=0;
 		if(flag == 1) //���� 
 		{
 			/*
 			if(pointSuspectedDateGAP < 8)
	 			ret= ConstantsInfo.DEAL_WARN_SEE; //����
	 		else if(suspectedCurDateGap <=2)
	 		*/
 			ret = ConstantsInfo.DEAL_WARN_SALE;//����
 		} else {
 			/*
 			if(pointSuspectedDateGAP < 8)
	 			ret= ConstantsInfo.DEAL_WARN_INTEREST; //��ע
	 		else if(suspectedCurDateGap <=2)
	 		*/
	 		//	ret= ConstantsInfo.DEAL_WARN_BUG;//����
 			ret= ConstantsInfo.DEAL_WARN_BUG;//����
 		}
 		return ret;
 	}
 	
 	public StockDesireValue getStockDesireValue(float curValue,float curStartValue,float  curEndValue) 
 	{
 		float tmpValue=0;
 		//Ԥ��
 		float desireValue1,desireValue2,desireValue3,desireValue4,desireValue5,desireValue6=0;
 		float desireValue1Gap,desireValue2Gap,desireValue3Gap,desireValue4Gap,desireValue5Gap,desireValue6Gap=0;
 		float desireRange1,desireRange2,desireRange3,desireRange4,desireRange5,desireRange6=0;
 		float desireRate1,desireRate2,desireRate3,desireRate4,desireRate5,desireRate6=0;
 		
 		StockDesireValue sdValue;//Ԥ��
 		
 		//System.out.println("rate:"+rate);			
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
		tmpValue = (float)(curValue - desireValue1)/desireValue1;
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
		tmpValue = (float)(curValue - desireValue2)/desireValue2;
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
		tmpValue = (float)(curValue - desireValue3)/desireValue3;
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
		tmpValue = (float)(curValue - desireValue4)/desireValue4;
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
		tmpValue = (float)(curValue - desireValue5)/desireValue5;
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
		tmpValue = (float)(curValue - desireValue6)/desireValue6;
		desireValue6Gap = getDec(tmpValue,4);
		
		sdValue=new StockDesireValue(desireValue1,desireRange1,desireRate1,desireValue1Gap,
				desireValue2,desireRange2,desireRate2,desireValue2Gap,
				desireValue3,desireRange3,desireRate3,desireValue3Gap,
				desireValue4,desireRange4,desireRate4,desireValue4Gap,
				desireValue5,desireRange5,desireRate5,desireValue5Gap,
				desireValue6,desireRange6,desireRate6,desireValue6Gap);
		
		return sdValue;//Ԥ��
 		
 	}
 	
 	public float getRegion(float curValue, StockDesireValue sdValue)
 	{
 		float ret = 0;
 		if(curValue < sdValue.getDesireValue1())
 			ret = ConstantsInfo.STOCK_DESIRE1;
 		else if(curValue < sdValue.getDesireValue2())
 			ret = ConstantsInfo.STOCK_DESIRE2;
 		else if(curValue < sdValue.getDesireValue3())
 			ret = ConstantsInfo.STOCK_DESIRE3;
 		else if(curValue < sdValue.getDesireValue4())
 			ret = ConstantsInfo.STOCK_DESIRE4;
 		else if(curValue < sdValue.getDesireValue5())
 			ret = ConstantsInfo.STOCK_DESIRE5;
 		else if(curValue < sdValue.getDesireValue6())
 			ret = ConstantsInfo.STOCK_DESIRE6;
 		else 
 			ret = ConstantsInfo.STOCK_DESIRE7;
 		
 		return ret;
 	}
 	
 	//��ת����  ���Ƽ��� ǰһ���� ǰ������
 	public float getReversalRegion(float suspectedValue, float lastExtrmeValue,float priHighOrLowestValue){
 		float tmpValue =0;
 		float reversalRegion = 0;
 		tmpValue = (lastExtrmeValue- suspectedValue)/(lastExtrmeValue -priHighOrLowestValue);
 		
 		if(tmpValue < ConstantsInfo.STOCK_DESIRE1)
 			reversalRegion = ConstantsInfo.STOCK_DESIRE1;
 		else if(tmpValue < ConstantsInfo.STOCK_DESIRE2)
 			reversalRegion = ConstantsInfo.STOCK_DESIRE2;
 		else if(tmpValue < ConstantsInfo.STOCK_DESIRE3)
 			reversalRegion = ConstantsInfo.STOCK_DESIRE3;
 		else if(tmpValue < ConstantsInfo.STOCK_DESIRE4)
 			reversalRegion = ConstantsInfo.STOCK_DESIRE4;
 		else if(tmpValue < ConstantsInfo.STOCK_DESIRE5)
 			reversalRegion = ConstantsInfo.STOCK_DESIRE5;
 		else if(tmpValue < ConstantsInfo.STOCK_DESIRE6)
 			reversalRegion = ConstantsInfo.STOCK_DESIRE6;
 		else 
 			reversalRegion = ConstantsInfo.STOCK_DESIRE7;
 		return reversalRegion;
 	}
 	
 	
 	//����ǰһ��ͣ����
 	public int priUpData(String stockFullId) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException
 	{
 		String curDate=null;
 		int priCurDateGap=-1; 
 		//ȡ������������Ϊ���㣬�����ܣ�������
		StockData sdata = sdDao.getLastDataStock(stockFullId,ConstantsInfo.DayDataType);
		//ǰһ��ͣ����
		StockData prisdata =sdDao.getPriUpValue(stockFullId);
		
		if(sdata!=null && prisdata!=null) {
			curDate= sdata.getDate().toString();
			priCurDateGap = sdDao.getStockDataDateGap(stockFullId,prisdata.getDate().toString(),curDate, ConstantsInfo.DayDataType);
			//System.out.println(curDate);
			//System.out.println(prisdata.getDate().toString());
		}
		
		return priCurDateGap;
 	}
 	
 	//��ȡ����̶Ա���ʱ���
 	public int getToMarketGAP(String sDate, String mDate,int dataType) throws ParseException, SecurityException, IOException, ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException
 	{
 		int dayCom = 0;
 		int gap = 0;
		dayCom = stockDateTimer.compareDate(sDate,mDate);
		if(dayCom>0)			
			gap = sdDao.getStockDataDateGap("SH000001",mDate,sDate,dataType);	
		else if (dayCom<0)
			gap = -sdDao.getStockDataDateGap("SH000001",sDate,mDate,dataType);
		else 
			gap=0;
		return gap;
 	}
 	
 	//����date���� �������ƣ��ϣ���ת��
 	public StockExcelItem getExcelItem(String stockFullId,int dataType,int stockType) throws IOException, ClassNotFoundException, SQLException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
 	{
 	
 		StockExcelItem eItem=null;
 		//String curExtremeDate=null;//�����ֵʱ��
 		String lastExtremeDate =null; //ǰһ������ʱ��
 		float lastExtrmePrice=0;
 		float tmpValue=0;
 		//����
 		String nowTime=stockDateTimer.getCurDate(); 
 		int curTread =0;//��ǰ���� 0�� 1��
 		//�ǵ�ͣ��
 		int upType=0;//�ǵ�������
 		int upOrdownTimes=0;
 		int curState=0;//״̬��0Ϊ������1Ϊ���ǣ�2Ϊ�µ�
 		int dealWarn=0;//������ʾ 0 ���� 1���� 2��ע 3���� 
 		
 		//ǰһ������ʱ��(��ʼ)�����Ƽ��㣨�������� ��ǰ�� ʱ��   //��ǰ��ʱ��
 		String curStartDate = null,curEndDate = null, curDate = null,priDate=null;
 		//ǰһ������ʱ�䣨��ʼ�������Ƽ��㣨�������� ��ǰ ����λ 
 		float curStartValue = 0,curEndValue = 0, curValue = 0, priHighOrLowest=0;
 		float workRegion =0, reversalRegion=0;
 		
 		float range=0;//�����Ƿ�
 		int priUpDateGap =0; //ǰһ��ͣʱ���
 		//���� �ɵ� ��ǰʱ������ ʱ���
 		int pointSuspectedDateGAP,pointCurDateGAP,suspectedCurDateGap=0;
 		//���� �ɵ� ��ǰʱ������ ��λ��
 		float pointSuspectedValueGap,pointCurValueGAP,suspectedCurValueGap=0;
 		
 		//��Ӯ��
 		float bugValue = 0,winValue = 0,loseValue=0; 		
 	
 		//����̶Ա�
 		int toMarketPSDateGAP,toMarketPCDateGAP,toMarketSCDateGAP;
 		float toMaretPSSpaceGAP=0,toMaretPCSpaceGAP,toMaretSCSpaceGAP;
 		int trendConsistent=0; //�����Ƿ�һ��
 		
 		int marketIndex=0;
 		
 		StockStatValue ssValue;//ͳ��
 		StockCurValue scValue;//��ǰ
 		StockDesireValue sdValue;//Ԥ��
 		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  	
    	    
 		//���������ֵ��
 		List<StockPoint> listSP = new ArrayList<StockPoint>();
 		listSP = spDao.getLastTwoPointStock(stockFullId,dataType);
 		
 		StockPoint lastSp = null,priSp=null;
 		if(listSP == null || listSP.size() == 0) {
			stockLogger.logger.fatal("stockFullId��"+stockFullId+"����� type:"+dataType+"������");
			System.out.println(stockFullId+"�����type:"+dataType+"������");
			return null;  
		} else if(listSP.size() ==1 ){
			lastSp  = listSP.get(0);
			priSp = null;
			System.out.println(stockFullId+"��ǰ�ߵ͵�");
		} else if(listSP.size() ==2){
			lastSp  = listSP.get(0);
			priSp = listSP.get(1);
		}
 		
 		//��ǰ����
 		curTread = lastSp.getWillFlag()>0?lastSp.getWillFlag()-1:lastSp.getWillFlag()+1;
 		
		//ǰһ������
		lastExtrmePrice = lastSp.getExtremePrice();
		lastExtremeDate = sdf.format(lastSp.getExtremeDate());
		String crossLastDate=lastSp.getToDate().toString();				
		
		//ȡ������������Ϊ���㣬�����ܣ�������
		StockData sdata = sdDao.getLastDataStock(stockFullId,ConstantsInfo.DayDataType);
		curDate= sdata.getDate().toString();		
		range = sdata.getRange();
		StockData sMinData=null;
		StockData sMaxData=null;
		
		
		if (curTread == 0) { 
			
			//3 �����͵� ���Ƽ���
			//sMinData=sdDao.getMinStockDataPoint(stockFullId,crossLastDate,nowTime,ConstantsInfo.DayDataType);
			sMinData=sdDao.getMinStockDataPoint(stockFullId,lastExtremeDate,nowTime,ConstantsInfo.DayDataType);
			if ( sMinData == null){
				stockLogger.logger.fatal("****stockFullId��"+stockFullId+"������͵�****");
				return null; 
			}
			//float minPrice = sMinData.getLowestPrice();
			//ǰһ���� ��ʼʱ��
			curStartDate = 	lastExtremeDate;
			//��ʼ��λ
			curStartValue = lastExtrmePrice;//sdDao.getStockLowestPrice(stockFullId,curDownStartDate,dataType);
			
			//���Ƽ��� ����ʱ��
			curEndDate = sMinData.getDate().toString();	
			////curExtremeDate = curEndDate;�������ʱ��				
			//������λ
			curEndValue = sMinData.getLowestPrice();
			//��ǰ��
			//curDownRange = sdDao.getStockCurHigestRange(stockFullId,curDownEndDate,dataType,0);		
				
		} else {			

			//3 �����ߵ� ���Ƽ���
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
			
			//��
			//curUpRange = sdDao.getStockCurHigestRange(stockFullId,curUpEndDate,dataType,1);
		}
		
		//��ת������ǰ�ߵ͵�
		if(priSp == null) {
 			priHighOrLowest = 0 ;
 			priDate = "��";
 		} else {
 			priHighOrLowest = priSp.getExtremePrice();	
 			priDate = priSp.getExtremeDate().toString();
 		}
		reversalRegion = getReversalRegion(curEndValue,curStartValue,priHighOrLowest);
		
		//��ǰʱ��
		curDate = sdata.getDate().toString();
		//��ǰ���̼۸�
		curValue = sdata.getClosingPrice();
				
		//���������� �� ��λ��
		pointSuspectedDateGAP = sdDao.getStockDataDateGap(stockFullId,curStartDate,curEndDate,dataType);
		pointSuspectedValueGap = getGAP(curStartValue,curEndValue);
		
		//������ ��ǰ�� ��λ��
		pointCurDateGAP = sdDao.getStockDataDateGap(stockFullId,curStartDate,curDate,dataType);
		pointCurValueGAP = getGAP(curStartValue,curValue);
		
		//���Ƶ��뵱ǰ ʱ��� ��λ��
		suspectedCurDateGap = sdDao.getStockDataDateGap(stockFullId,curEndDate,curDate,dataType);
		
		suspectedCurValueGap = getGAP(curEndValue,curValue);
		
		//״̬
		if(curEndDate.equals(curDate)) {
			curState = curTread;
		} else {
			curState = -1; //����
		}
				
		//������ʾ
		dealWarn = getDealWarn(curTread,pointSuspectedDateGAP,pointSuspectedDateGAP,suspectedCurDateGap);
		
		//Ԥ�����
		sdValue= getStockDesireValue(curValue,curStartValue,curEndValue);
		if (curTread == 0) { 		
			//��
			tmpValue = (float) (1 + pointSuspectedValueGap/100)* curEndValue;
			bugValue = getDec(tmpValue,2);			
			
			//Ӯ 0.618
			tmpValue = (float) (sdValue.getDesireValue3()/bugValue-1);
			winValue = getDec(tmpValue,4);	
			
			//��
			tmpValue = (float) (curEndValue/bugValue -1);	
			loseValue = getDec(tmpValue,4);	
		}
					
		
		//��������
		workRegion = getRegion(curValue,sdValue);
		
		if(dataType == ConstantsInfo.DayDataType){
   		//������һ��ͣʱ���
			priUpDateGap = priUpData(stockFullId);
		}
   		
		//�����ǵ�ͣ��
		if(sdata.getRange()>9.9) {
			upType=1;
			upOrdownTimes=sdDao.getUpOrDownTimes(stockFullId,curStartDate,curDate,upType);
		} else if(sdata.getRange()< -9.9){
			upType=0;
			upOrdownTimes=sdDao.getUpOrDownTimes(stockFullId,curStartDate,curDate,upType);
		} else {
			upType=0;
			upOrdownTimes=0;
		}			
		
		ssValue=new StockStatValue(curTread,range,priUpDateGap,upType,upOrdownTimes,pointSuspectedDateGAP,suspectedCurDateGap,dealWarn);		
		//����ָ��  ����Ʒ
		if (stockType == ConstantsInfo.DPMarket){
			marketIndex = sbDao.getMarketNum(stockFullId);
			scValue=new StockCurValue(priDate,priHighOrLowest,reversalRegion,curStartDate,curStartValue,curEndDate,curEndValue,curDate,curValue,workRegion,
					bugValue,winValue,loseValue,dealWarn,curState,curTread,
					pointSuspectedDateGAP,pointSuspectedValueGap,pointCurDateGAP,pointCurValueGAP,suspectedCurDateGap,suspectedCurValueGap,
					0,0,0,0,0,0,0);
			
			//����������Ϣ��������Ա�
			MarketStockCurInfo[marketIndex][dataType-1] = scValue;
		} else {
			marketIndex = stockType-2;	
			//System.out.println("curStartDate:"+curStartDate);	
			
			StockCurValue scMarketValue = MarketStockCurInfo[marketIndex][dataType-1];
			
			//����ʱ��
			String marketPDate = scMarketValue.getStartDate();
			//�ɵ�ʱ��
			String marketSDate = scMarketValue.getEndDate();
			//��ǰʱ��
			String marketCDate = scMarketValue.getCurDate();
			//����̼��ɶԱ�	
			toMarketPSDateGAP = getToMarketGAP(curStartDate,marketPDate,dataType);
			//�ռ�
			toMaretPSSpaceGAP = pointSuspectedValueGap - scMarketValue.getPointSuspectedValueGap();
			
			//����̼����Ա�	
			toMarketPCDateGAP = getToMarketGAP(curEndDate,marketSDate,dataType);
			//�ռ�
			toMaretPCSpaceGAP = pointCurValueGAP - scMarketValue.getPointCurValueGap();
			
			//������ɵ��Ա�	
			toMarketSCDateGAP = getToMarketGAP(curDate,marketCDate,dataType);
			//�ռ�
			toMaretSCSpaceGAP = suspectedCurValueGap - scMarketValue.getSuspectedCurValueGap();
			
			trendConsistent =  (curTread == scMarketValue.getTread()? 0:1);
			scValue=new StockCurValue(priDate,priHighOrLowest,reversalRegion,curStartDate,curStartValue,curEndDate,curEndValue,curDate,curValue,workRegion,
					bugValue,winValue,loseValue,dealWarn,curState,curTread,
					pointSuspectedDateGAP,pointSuspectedValueGap,pointCurDateGAP,pointCurValueGAP,suspectedCurDateGap,suspectedCurValueGap,
					toMarketPSDateGAP,toMaretPSSpaceGAP,toMarketPCDateGAP,toMaretPCSpaceGAP,toMarketSCDateGAP,toMaretSCSpaceGAP,trendConsistent);
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
 	public void writeOperationExcelFromMarket(Workbook wb,Sheet sheet,String filePath, String fileName, String filetime) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
 		 List<StockMarket> listStockMarket = new ArrayList<StockMarket>(); 
		 listStockMarket=sbDao.getStockMarket(ConstantsInfo.StockMarket);
		 
		//�ȷ�������
	    //����ָ�� һ�� 
		stockRow++;
		StockBaseFace sbFace = new StockBaseFace(0,"sh1111","","","","","","");
		ExcelCommon.writeExcelItemTitle(wb,sheet,"����ָ��",sbFace,stockRow);
		int stockType=0;
		StockData sdata = sdDao.getLastDataStock("SH000001",ConstantsInfo.DayDataType);
   		SHDate = sdata.getDate().toString();
	   	for(Iterator itMarket = listStockMarket.iterator();itMarket.hasNext();)
	   	{
	   		StockMarket sMarket = (StockMarket)itMarket.next();	
	   		stockRow++;
	   		String stockFullId = sMarket.getCode().toString();
	   		System.out.println(stockFullId);
	   		stockType=sbDao.getMarketType(stockFullId);
	 
	   		
	   		//if(!sMarket.getCode().toString().equals("sh000001"))
	   		//	continue;
	   		
	   		//������
			StockBaseFace baseFace = new StockBaseFace(0,stockFullId,sMarket.getBaseExpect(),sMarket.getMain(),
					sMarket.getPsychology(),sMarket.getRisk(),sMarket.getPotential(),sMarket.getFaucet());
	   		
			//����ֵ
	   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,sMarket.getName().toString(),0,0,baseFace,null);
	   		ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow,0);
	   		
	   		//��ȡ���ͳ������
			List<StockOperation> stockOperationInfo=new ArrayList<StockOperation>();
			stockOperationInfo = ssDao.getOperationFromOperationTable(stockFullId,30);
			
			int extremeCol = 0;
			int earn=0,stop=0,loss=0;
		    for (int ij=0;ij<stockOperationInfo.size();ij++)	
			{
		    	StockOperation sSop = stockOperationInfo.get(ij);
		    	            
		    	if(sSop!=null && stockDateColumnmap.containsKey(sSop.getOpDate())) {
		        	extremeCol = stockDateColumnmap.get(sSop.getOpDate());
		        	String psState = ssDao.getpsStatusFromSummaryTable(stockFullId,sSop.getOpDate());
		        	
		        	ExcelCommon.writeOperationExcelItem(wb,sheet,sSop,psState,extremeCol ,stockRow);
		        	if(sSop.getEarnRatio()!=0)
			    		earn++;
			    	if(sSop.getStopRatio()!=0)
			    		stop++;
			    	if(sSop.getLossRatio()!=0)
			    		loss++;
		    	}
		    	
		    	
		 	}
		    
		    int totalsize = earn+stop+loss;
		    if(totalsize > 0) {
		    	ExcelCommon.writeOperationTotalExcelItem(wb,sheet,0 ,stockRow,earn,stop,loss,totalsize,0);
		    }	    
	   	}
 		
	}
 	
 	
 	//��������
 	public void writeSummaryExcelFromMarket(Workbook wb,Sheet sheet,String filePath, String fileName, String filetime) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
 		 List<StockMarket> listStockMarket = new ArrayList<StockMarket>(); 
		 listStockMarket=sbDao.getStockMarket(ConstantsInfo.StockMarket);
		 
		//�ȷ�������
	    //����ָ�� һ�� 
		stockRow++;
		StockBaseFace sbFace = new StockBaseFace(0,"sh1111","","","","","","");
		ExcelCommon.writeExcelItemTitle(wb,sheet,"����ָ��",sbFace,stockRow);
		int stockType=0;
		StockData sdata = sdDao.getLastDataStock("SH000001",ConstantsInfo.DayDataType);
   		SHDate = sdata.getDate().toString();
	   	for(Iterator itMarket = listStockMarket.iterator();itMarket.hasNext();)
	   	{
	   		StockMarket sMarket = (StockMarket)itMarket.next();	
	   		stockRow++;
	   		String stockFullId = sMarket.getCode().toString();
	   		System.out.println(stockFullId);
	   		stockType=sbDao.getMarketType(stockFullId);
	 
	   		
	   		//if(!sMarket.getCode().toString().equals("sh000001"))
	   		//	continue;
	   		
	   		//������
			StockBaseFace baseFace = new StockBaseFace(0,stockFullId,sMarket.getBaseExpect(),sMarket.getMain(),
					sMarket.getPsychology(),sMarket.getRisk(),sMarket.getPotential(),sMarket.getFaucet());
			StockBaseYearInfo yearInfo = sbDao.lookUpStockBaseYearInfo(stockFullId);
			//����ֵ
	   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,sMarket.getName().toString(),0,0,baseFace,yearInfo);
	   		ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow,0);
	   		
	   		//��ȡ���ͳ������
			List<StockSummary> stockSummaryInfo=new ArrayList<StockSummary>();
			stockSummaryInfo = ssDao.getSummaryFromSummaryTable(stockFullId,15);
			
			int extremeCol = 0;

		    for (int ij=0;ij<stockSummaryInfo.size();ij++)	
			{
		    	StockSummary sSum = stockSummaryInfo.get(ij);
		    	            
		    	if(sSum!=null && stockDateColumnmap.containsKey(sSum.getDayCurDate())) {
		        	extremeCol = stockDateColumnmap.get(sSum.getDayCurDate());				        	
		        	ExcelCommon.writeSummaryExcelItem(wb,sheet,sSum,extremeCol ,stockRow,1);
		    	}
		    	
		 	}
	   	}
 		
	}
 	
 	//��������
 	public void writeExcelFromMarket(Workbook wb,Sheet sheet,String filePath, String fileName, String filetime) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
 		
		 List<StockMarket> listStockMarket = new ArrayList<StockMarket>(); 
		 listStockMarket=sbDao.getStockMarket(ConstantsInfo.StockMarket);
		 
		//�ȷ�������
	    //����ָ�� һ�� 
		stockRow++;
		StockBaseFace sbFace = new StockBaseFace(0,"sh1111","","","","","","");
		ExcelCommon.writeExcelItemTitle(wb,sheet,"����ָ��",sbFace,stockRow);
		int stockType=0;
		StockData sdata = sdDao.getLastDataStock("SH000001",ConstantsInfo.DayDataType);
   		SHDate = sdata.getDate().toString();
	   	for(Iterator itMarket = listStockMarket.iterator();itMarket.hasNext();)
	   	{
	   		StockMarket sMarket = (StockMarket)itMarket.next();	
	   		stockRow++;
	   		String stockFullId = sMarket.getCode().toString();
	   		System.out.println(stockFullId);
	   		stockType=sbDao.getMarketType(stockFullId);	 
	   		
	   		//if(!sMarket.getCode().toString().equals("sh000001"))
	   		//	continue;
	   		
	   		//������
			StockBaseFace baseFace = new StockBaseFace(0,stockFullId,sMarket.getBaseExpect(),sMarket.getMain(),
					sMarket.getPsychology(),sMarket.getRisk(),sMarket.getPotential(),sMarket.getFaucet());
	   		
			StockBaseYearInfo yearInfo = sbDao.lookUpStockBaseYearInfo(stockFullId);
			
			//����ֵ
	   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,sMarket.getName().toString(),0,0,baseFace,yearInfo);
	   		ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow,1);
	   		
	   		//������
	   	//	stockLogger.logger.debug("*****������*****");
	   		StockExcelItem dayItem = getExcelItem(stockFullId,ConstantsInfo.DayDataType,stockType); 
			ExcelCommon.writeExcelItem(wb,sheet,dayItem, stockRow, ConstantsInfo.DayDataType);
			if (dayItem == null) 
				continue;
			//������Ԥ��ֵ
	//		stockLogger.logger.debug("*****������*****");
			StockExcelItem weekItem = getExcelItem(stockFullId,ConstantsInfo.WeekDataType,stockType);		
			ExcelCommon.writeExcelItem(wb,sheet,weekItem, stockRow, ConstantsInfo.WeekDataType);
			if (weekItem == null)
				continue;			
			//������Ԥ��ֵ
	//		stockLogger.logger.debug("*****������*****");
			StockExcelItem monthItem = getExcelItem(stockFullId,ConstantsInfo.MonthDataType,stockType);		
			ExcelCommon.writeExcelItem(wb,sheet,monthItem, stockRow, ConstantsInfo.MonthDataType);
			if (monthItem == null)
				continue;
			

			StockExcelStatItem statItem = getExcelStatItem(dayItem, weekItem, monthItem);
			
			//ͳ��
			ExcelCommon.writeExcelStatItem(wb,sheet,statItem,stockRow);
			
	   	}

	}
 	
 	
 	//���ڻ�����excel ���ܵ�ǰ��������
	public void writeExcelFormFuturesOrderBy(String filePath, String fileTime) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
	 	List<StockMarket> listMarket = new ArrayList<StockMarket>(); 
		 
	 	//�õ���ǰ�����г�
	 	listMarket=sbDao.getStockMarket(ConstantsInfo.FuturesMarket);
    	System.out.println("�г�������"+listMarket.size());
    	XSSFWorkbook wb=null;
    	XSSFSheet sheet =null;
    	String excleFileName=null;
    	sheetCount=0;
    	int titleRow =0;//��¼��ҵ����������
        //ÿ����ҵ�����excelһ��
        for (int i=0;i<listMarket.size();i++)	
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
	   			excleFileName="Stock_Futures_"+fileTime+"_All_"+sheetCount+".xlsx";	   		    
	   		    stockRow = 1;
	   			//����excel
	   	 		ExcelCommon.createExcel(wb,sheet,filePath,excleFileName);
	   			writeExcelFromMarket(wb,sheet,filePath,excleFileName,fileTime);
	   			FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);
	   			wb.write(fileOStream);		
	   	        fileOStream.close(); 
	   	        wb=null;
	   	        sheet=null;
   			}
   			
   			excleFileName="Stock_Futures_"+fileTime+"_All_"+sheetCount+".xlsx"; 			
   			File file = new File(filePath +fileTime+ "\\"+excleFileName);
			// ������   
			FileInputStream fileIStream = new FileInputStream(file);  			
			wb = new XSSFWorkbook(fileIStream);   
			sheet = wb.getSheetAt(0);  	
			
			StockExcelStatItem  statItem;  		
   			
			//��ǰ�г�
			StockMarket sMarket = listMarket.get(i);	
			String induCode = sMarket.getCode();
			String induName = sMarket.getName();
			if(induCode == null || induName == null)
				continue;				
			stockLogger.logger.fatal("�г���"+induName);   		
			System.out.println("�г���"+induName);			
			//��ҵ���� 
			stockRow++;
			titleRow = stockRow;
			
			//������
			StockBaseFace baseFaceIndu = new StockBaseFace(0,induCode,sMarket.getBaseExpect(),sMarket.getMain(),
					sMarket.getPsychology(),sMarket.getRisk(),sMarket.getPotential(),sMarket.getFaucet());
			
			ExcelCommon.writeExcelItemTitle(wb,sheet,i+":"+induName,baseFaceIndu,stockRow);
   					 	
   			//������ҵ���й�Ʊ
   			List<StockToFutures> listFuturesStock = new ArrayList<StockToFutures>();   
   			listFuturesStock=sbDao.getFuturesToStock(induCode);	   	
   		
   			stockLogger.logger.debug("�г���Ʊ����"+listFuturesStock.size());
   			int stockType=0;
   			
   			//�����Ƿ�������
   			List<StockExcelTotalInfo> listStockTotalInfoOrderBy = new ArrayList<StockExcelTotalInfo>(); 
   			List<String> listName = new ArrayList<String>(); 
   			for(Iterator ie=listFuturesStock.iterator();ie.hasNext();)
   			{
   				//stockRow++;
   				StockToFutures toInduStock = (StockToFutures) ie.next();
   				String stockFullId = toInduStock.getCode();	
   				System.out.println("stockFullId:"+stockFullId);
   				
   				//��Ʒ ��ʱ��������֤��Ʊ��������֤�Ա�
   				//stockType=sbDao.getMarketType(stockFullId);   				
   				stockType = ConstantsInfo.SHMarket;
   					
   				int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_POINT_STOCK);
   		    	if(isTableExist == 0){//������
   					stockLogger.logger.fatal("****stockFullId��"+stockFullId+"�����ڼ�ֵ��****");
   					System.out.println(stockFullId+"��ֵ������****");
   					continue;  
   				}
   		    	
   				//if(!stockFullId.equals("SH601268"))
   				//	continue;
   		    	
   				stockLogger.logger.fatal("****stockFullId��"+stockFullId+"****");
   				
   				StockExcelItem dayItem;
				StockExcelItem weekItem;
				StockExcelItem monthItem;				
				
				//�Ƿ�ͣ��
				int enableTingPai = 0; //getEnableTingPai(stockFullId);		
				//�Ƿ�����
				int enableTwoRong = sbDao.lookUpStockTwoRong(stockFullId);		
				//������
				//StockBaseFace baseFace = sbDao.lookUpStockBaseFace(stockFullId);
			//	StockBaseYearInfo yearInfo = sbDao.lookUpStockBaseYearInfo(stockFullId);
				StockBaseFace baseFace = new StockBaseFace(0,stockFullId,toInduStock.getBaseExpect(),toInduStock.getMain(),
						toInduStock.getPsychology(),toInduStock.getRisk(),toInduStock.getPotential(),toInduStock.getFaucet());
				//����ֵ
		   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,toInduStock.getName(),enableTwoRong,enableTingPai,baseFace,null);
								
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
   			
   				statItem = getExcelStatItem(dayItem, weekItem, monthItem);
   				
   				StockExcelTotalInfo setInfo =new StockExcelTotalInfo(soiValue,dayItem,weekItem,monthItem,statItem);
   				//ͣ�� �ź���
   				//ͣ�� �ź���
   				if(enableTingPai!=0 && setInfo.getStatItem()!=null  && setInfo.getStatItem().getDayMixStatItem()!=null) {
   					setInfo.getStatItem().getDayMixStatItem().setComPSState("211211");
   				} 
   				listStockTotalInfoOrderBy.add(setInfo);
   				listName.add(toInduStock.getName());
   				
   			}
   			  
   			
   			//��¼��ҵ�ڸ���Ʊ��ʾ
   			int dealWarns[] = new int[4];
   			//����
   			//Collections.sort(listStockTotalInfoOrderBy);    
   			Collections.sort(listName,Collator.getInstance(java.util.Locale.CHINA));
   			for (int kk=0;kk<listName.size();kk++)
   			{
   			for (int j=0;j<listStockTotalInfoOrderBy.size();j++)	
   			{
   				StockExcelTotalInfo setInfo = (StockExcelTotalInfo) listStockTotalInfoOrderBy.get(j);
   				if(!listName.get(kk).equals(setInfo.getSoiValue().getName()))
   					continue;
   				
   				stockRow++;   			
   				ExcelCommon.writeExcelStockOtherInfo(wb, sheet, setInfo.getSoiValue(), stockRow,1);
   				
   				//δͣ��
   				if (setInfo.getSoiValue().getEnableTingPai() == 0) {
   					statItem = setInfo.getStatItem();
   					
   					//ͳ�� ������ͳ��ֵ
   					ExcelCommon.writeExcelStatItem(wb,sheet,statItem,stockRow);
   							
   	   		    	//ͳ�������Ѿ��ڸ����� ��������
   					//ssDao.insertStockSummaryTable(setInfo.getSoiValue().getFullId(),ssu);
   				
	   				if (setInfo.getDayItem() != null) {
	   					ExcelCommon.writeExcelItem(wb,sheet,setInfo.getDayItem(), stockRow, ConstantsInfo.DayDataType);
	   					//��¼��Ʊ������ʾ
		   				dealWarns[setInfo.getDayItem().getScValue().getDealWarn()]++;
	   				}
	   				
	   				if (setInfo.getWeekItem() != null)
	   					ExcelCommon.writeExcelItem(wb,sheet,setInfo.getWeekItem(), stockRow, ConstantsInfo.WeekDataType);
	   				if (setInfo.getMonthItem() != null)
	   					ExcelCommon.writeExcelItem(wb,sheet,setInfo.getMonthItem(),stockRow, ConstantsInfo.MonthDataType);
   				}
   			} 	
   			}
   			
   			int sub=5;//δ֪
   			if(dealWarns != null && dealWarns.length!=0)
   				sub = getMax(dealWarns);			
			//������ʾ
			ExcelCommon.writeExcelItemDealWall(wb,sheet,null,sub,titleRow);
   			
   			listStockTotalInfoOrderBy =null;
   			
			FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);
			wb.write(fileOStream);
			fileOStream.flush();
			fileIStream.close();
			fileOStream.close();              
			  
			listFuturesStock = null;
          //��������
			//if(stockRow>10)
			//	break;
   		}
 
        listMarket = null;
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
    	sheetCount=0;
    	int titleRow =0;//��¼��ҵ����������
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
	   		    stockRow = 1;
	   			//����excel
	   	 		ExcelCommon.createExcel(wb,sheet,filePath,excleFileName);
	   			writeExcelFromMarket(wb,sheet,filePath,excleFileName,fileTime);
	   			FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);
	   			wb.write(fileOStream);		
	   	        fileOStream.close(); 
	   	        wb=null;
	   	        sheet=null;
   			}
   			
   			excleFileName="Stock_Industry_"+fileTime+"_All_"+sheetCount+".xlsx"; 			
   			File file = new File(filePath +fileTime+ "\\"+excleFileName);
			// ������   
			FileInputStream fileIStream = new FileInputStream(file);  			
			wb = new XSSFWorkbook(fileIStream);   
			sheet = wb.getSheetAt(0);  	
			
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
			titleRow = stockRow;
			
			//������
			StockBaseFace baseFaceIndu = new StockBaseFace(0,induCode,indu.getBaseExpect(),indu.getMain(),
					indu.getPsychology(),indu.getRisk(),indu.getPotential(),indu.getFaucet());
			
			ExcelCommon.writeExcelItemTitle(wb,sheet,i+":"+induName,baseFaceIndu,stockRow);
   					 	
   			//������ҵ���й�Ʊ
   			List<StockToIndustry> listIndustryStock = new ArrayList<StockToIndustry>();   
   			listIndustryStock=sbDao.getIndustryToStock(induCode);	   	
   		
   			stockLogger.logger.debug("��ҵ��Ʊ����"+listIndustryStock.size());
   			int stockType=0;
   			
   			//�����Ƿ�������
   			List<StockExcelTotalInfo> listStockTotalInfoOrderBy = new ArrayList<StockExcelTotalInfo>();
   			List<String> listName = new ArrayList<String>(); 
   			
   			//��������
   			for(Iterator ie=listIndustryStock.iterator();ie.hasNext();)
   			{
   				//stockRow++;
   				StockToIndustry toInduStock = (StockToIndustry) ie.next();
   				String stockFullId = toInduStock.getStockFullId();	
   				System.out.println("stockFullId:"+stockFullId);
   				stockType=sbDao.getMarketType(stockFullId);
   					
   				int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_DATA_STOCK);
   		    	if(isTableExist == 0){//������
   					stockLogger.logger.fatal("****stockFullId��"+stockFullId+"�����ڼ�ֵ��****");
   					System.out.println(stockFullId+"��ֵ������****");
   					continue;  
   				}
   		    	
   			//	if(!stockFullId.equals("SH600000"))
   			//		continue;
   		    	
   				stockLogger.logger.fatal("****stockFullId��"+stockFullId+"****");
   				
   				StockExcelItem dayItem;
				StockExcelItem weekItem;
				StockExcelItem monthItem;				
				
				//�Ƿ�ͣ��
				int enableTingPai = getEnableTingPai(stockFullId);		
				//�Ƿ�����
				int enableTwoRong = sbDao.lookUpStockTwoRong(stockFullId);		
				//������
				StockBaseFace baseFace = sbDao.lookUpStockBaseFace(stockFullId);
				StockBaseYearInfo yearInfo = sbDao.lookUpStockBaseYearInfo(stockFullId);
				//����ֵ
		   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,toInduStock.getStockName(),enableTwoRong,enableTingPai,baseFace,yearInfo);
				
				
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
   				statItem = getExcelStatItem(dayItem, weekItem, monthItem);
   				
   				StockExcelTotalInfo setInfo =new StockExcelTotalInfo(soiValue,dayItem,weekItem,monthItem,statItem);
   				
   				//ͣ�� �ź���
   				if(enableTingPai!=0 && setInfo.getStatItem()!=null  && setInfo.getStatItem().getDayMixStatItem()!=null) {
   					setInfo.getStatItem().getDayMixStatItem().setComPSState("211211");
   				} 
   					
   				listName.add(toInduStock.getStockName());   				
   				listStockTotalInfoOrderBy.add(setInfo);
   			}
   			  		
   			//��¼��ҵ�ڸ���Ʊ��ʾ
   			int dealWarns[] = new int[4];
   			// ���Ƿ�������
   			//Collections.sort(listStockTotalInfoOrderBy); 
   			//������������
   			Collections.sort(listName,Collator.getInstance(java.util.Locale.CHINA));
   		
   			
   			for (int kk=0;kk<listName.size();kk++)
   			{
   			for (int j=0;j<listStockTotalInfoOrderBy.size();j++)	
   			{
   				
   				StockExcelTotalInfo setInfo = (StockExcelTotalInfo) listStockTotalInfoOrderBy.get(j);
   				if(!listName.get(kk).equals(setInfo.getSoiValue().getName()))
   					continue;
   				stockRow++;   			
   				ExcelCommon.writeExcelStockOtherInfo(wb, sheet, setInfo.getSoiValue(), stockRow,1);
   				
   				//δͣ��
   				if (setInfo.getSoiValue().getEnableTingPai() == 0) {
   					statItem = setInfo.getStatItem();
   					
   					//ͳ�� ������ͳ��ֵ
   					ExcelCommon.writeExcelStatItem(wb,sheet,statItem,stockRow);
   							
   	   		    	//ͳ�������Ѿ��ڸ����� ��������
   					//ssDao.insertStockSummaryTable(setInfo.getSoiValue().getFullId(),ssu);
   				
	   				if (setInfo.getDayItem() != null) {
	   					ExcelCommon.writeExcelItem(wb,sheet,setInfo.getDayItem(), stockRow, ConstantsInfo.DayDataType);
	   					//��¼��Ʊ������ʾ
		   				dealWarns[setInfo.getDayItem().getScValue().getDealWarn()]++;
	   				}
	   				
	   				if (setInfo.getWeekItem() != null)
	   					ExcelCommon.writeExcelItem(wb,sheet,setInfo.getWeekItem(), stockRow, ConstantsInfo.WeekDataType);
	   				if (setInfo.getMonthItem() != null)
	   					ExcelCommon.writeExcelItem(wb,sheet,setInfo.getMonthItem(),stockRow, ConstantsInfo.MonthDataType);
   				}
   			} 
   			}
   			
   			int sub=5;//δ֪
   			if(dealWarns != null && dealWarns.length!=0)
   				sub = getMax(dealWarns);			
			//������ʾ
			ExcelCommon.writeExcelItemDealWall(wb,sheet,null,sub,titleRow);
   			
   			listStockTotalInfoOrderBy =null;
   			
			FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);
			wb.write(fileOStream);
			fileOStream.flush();
			fileIStream.close();
			fileOStream.close();              
			  
			listIndustryStock = null;
          //��������
		//	if(stockRow>10)
		//		break;
   		}
 
   		listIndustry = null;
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
    	sheetCount =0;
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
	   			writeExcelFromMarket(wb,sheet,filePath,excleFileName,fileTime);
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

			//������
			StockBaseFace baseFaceCon = new StockBaseFace(0,conceptName,"","","","","","");
			ExcelCommon.writeExcelItemTitle(wb,sheet,i+":"+conceptName,baseFaceCon,stockRow);
			
   					 	
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
   				int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_DATA_STOCK);
   		    	if(isTableExist == 0){//������
   					stockLogger.logger.fatal("****stockFullId��"+stockFullId+"�����ڼ�ֵ��****");
   					System.out.println(stockFullId+"��ֵ������****");
   					continue;  
   				}
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
				StockBaseYearInfo yearInfo = sbDao.lookUpStockBaseYearInfo(stockFullId);
				
				//����ֵ
		   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,ss.getStockName(),ss.getEnableMarginTrading(),enableTingPai,baseFace,yearInfo);
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
   				statItem = getExcelStatItem(dayItem, weekItem, monthItem);
   				StockExcelTotalInfo setInfo =new StockExcelTotalInfo(soiValue,dayItem,weekItem,monthItem,statItem);
   				listStockTotalInfoOrderBy.add(setInfo);
   				
   			}
   			
   			//����
   			Collections.sort(listStockTotalInfoOrderBy); 
   		           
   			for (int j=0;j<listStockTotalInfoOrderBy.size();j++)	
   			{
   				StockExcelTotalInfo setInfo = (StockExcelTotalInfo) listStockTotalInfoOrderBy.get(j);
   				stockRow++;
   				ExcelCommon.writeExcelStockOtherInfo(wb, sheet, setInfo.getSoiValue(), stockRow,1);
   				if (setInfo.getDayItem() != null) {
   					ExcelCommon.writeExcelItem(wb,sheet,setInfo.getDayItem(), stockRow, ConstantsInfo.DayDataType);
   					//ExcelCommon.writeExcelStatItem(wb,sheet,setInfo.getStatItem(), stockRow);
   				}
   				if (setInfo.getWeekItem() != null)
   					ExcelCommon.writeExcelItem(wb,sheet,setInfo.getWeekItem(), stockRow, ConstantsInfo.WeekDataType);
   				if (setInfo.getMonthItem() != null)
   					ExcelCommon.writeExcelItem(wb,sheet,setInfo.getMonthItem(),stockRow, ConstantsInfo.MonthDataType);
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
	
	//���������ֵ��Ӧ�±�
	public static int getMax(int[] arr)
	{
		int max = arr[0];	
		int sub =0;
		for(int x=0;x<arr.length;x++)
		{
			if(arr[x]> max) {
				max = arr[x];	
				sub = x;
			}
		}

		return sub;	
	}
	
	//�ּ�����
    public static String getBuySaleGrade(StockExcelItem eDayItem,StockExcelItem eWeekItem,StockExcelItem eMonthItem,int dataType)
    {
    	String desc = "";
    	int largePSTrend=0;//
    	int smallPSTrend=0;
    	float ratio=0;
    	
    	//�շּ����� ͨ���ա������ݼ���
    	if (dataType == ConstantsInfo.DayDataType){
    		if(eDayItem ==null || eWeekItem==null)
        		return null;
    		
    		if(eDayItem.getScValue().getTread() == 0){  
    			smallPSTrend = 0; //�ռ�������
        	} else {
        		smallPSTrend = 1;
        	}
        	
        	if(eWeekItem.getScValue().getTread() == 0){  
        		largePSTrend = 0; //�ܼ�������
        	} else {
        		largePSTrend = 1;
        	}
        	
        	if(eWeekItem.getScValue().getPointSuspectedDateGap() == 0){
        		ratio = 2; //����1
        	} else {
        		ratio = (float) eWeekItem.getScValue().getSuspectedCurDateGap()/eWeekItem.getScValue().getPointSuspectedDateGap();
        	}
    		
    	} else {
    		
    		if(eWeekItem ==null || eMonthItem==null)
        		return null;
    		
    		if(eWeekItem.getScValue().getTread() == 0){  
    			smallPSTrend = 0; //�ռ�������
        	} else {
        		smallPSTrend = 1;
        	}
        	
        	if(eMonthItem.getScValue().getTread() == 0){  
        		largePSTrend = 0; //�ܼ�������
        	} else {
        		largePSTrend = 1;
        	}
        	
        	if(eMonthItem.getScValue().getPointSuspectedDateGap() == 0){
        		ratio = 2; //����1
        	} else {
        		ratio = (float) eMonthItem.getScValue().getSuspectedCurDateGap()/eMonthItem.getScValue().getPointSuspectedDateGap();
        	}
    	}
    	

    	/*
    	System.out.println("dayCurTrend:"+eDayItem.getScValue().getTread());
    	System.out.println("dayPSTrend:"+dayPSTrend);
    	
      	System.out.println("weekCurTrend:"+eWeekItem.getScValue().getTread());
    	System.out.println("weekPSTrend:"+weekPSTrend);
    	System.out.println("weekSCDate:"+eWeekItem.getScValue().getSuspectedCurDateGap());
    	System.out.println("dayPSDate:"+eWeekItem.getScValue().getPointSuspectedDateGap());
      	System.out.println("ratioWeek:"+ratioWeek);
   	*/
   
    	
    	//�ܵ�
    	if(largePSTrend == 0 ) {
    		//�յ�
    		if(smallPSTrend ==0){
    			
    			if(ratio<=0.382)
					desc="һ������";
				else if(ratio<=0.618)
					desc="��������";
				else if(ratio<=1)
					desc="��������";
				else
					desc="�ļ�����";
    			
    		}else{
    			
    			if(ratio<=0.382)
					desc="�ļ�����";
				else if(ratio<=0.618)
					desc="��������";
				else if(ratio<=1)
					desc="��������";
				else
					desc="һ������";
    		}
    		
    	} else {
    		//����
    		if(smallPSTrend == 1){
    			
    			if(ratio<=0.382)
    				desc="һ������";
    			else if(ratio<=0.618)
    				desc="��������";
    			else if(ratio<=1)
    				desc="��������";
    			else
    				desc="�ļ�����";
    		} else {
    			
    			if(ratio<=0.382)
					desc="�ļ�����";
				else if(ratio<=0.618)
					desc="��������";
				else if(ratio<=1)
					desc="��������";
				else
					desc="һ������";
    		}
    	}
    			
	   return desc;	
    }
    
    //��ǰ���ǻ����µ�����
    public int getTread(StockExcelItem item)
    {
    	if(item.getScValue().getTread() == 0){  
			return  0; //�ռ������� ��
    	} else {
    		return 1;
    	}
    }
    
    //����״̬���
    public String getPriPStateValue(StockExcelItem eDayItem,StockExcelItem eWeekItem,StockExcelItem eMonthItem,int type)
    {
    	String monthPSStateDesc="�µ�";//���ɵ�λ��״̬
    	String weekPSStateDesc="�ܵ�";//���ɵ�λ��״̬
    	String dayPSStateDesc="�յ�";//���ɵ�λ��״̬
    	
    	int dayPSValue=0;//���ɵ�λ��״̬
    	int weekPSValue=0;//���ɵ�λ��״̬
    	int monthPSValue=0;//���ɵ�λ��״̬
    	String comState = "";
    	if(eDayItem!=null) {
    		dayPSValue = eDayItem.getScValue().getPointSuspectedValueGap()<0?1:0;
    		if(dayPSValue == 1){
    			dayPSStateDesc = "�յ�";
    		} else {
    			dayPSStateDesc = "����";
    		}
    	}
    	 
    	if(eWeekItem!=null){
    		weekPSValue = eWeekItem.getScValue().getPointSuspectedValueGap()<0?1:0;
    		if(weekPSValue == 1){
    			weekPSStateDesc = "�ܵ�";
    		} else {
    			weekPSStateDesc = "����";
    		}
    	}
    		
    	if(eMonthItem!=null){
    		monthPSValue = eMonthItem.getScValue().getPointSuspectedValueGap()<0?1:0;
    		if(monthPSValue == 1){
    			monthPSStateDesc = "�µ�";
    		} else {
    			monthPSStateDesc = "����";
    		}
    	}
    	
    	if (type == 1 ){
    		comState = monthPSStateDesc+":"+weekPSStateDesc+":"+dayPSStateDesc;
    	} else {
    		comState = String.valueOf(monthPSValue)+String.valueOf(weekPSValue)+String.valueOf(dayPSValue);
    	}
    	return comState;
    	
    }
    
    
    //���Ƽ���״̬���
    public String getPSStateValue(StockExcelItem eDayItem,StockExcelItem eWeekItem,StockExcelItem eMonthItem,int type)
    {
    	String monthPSStateDesc="�µ�";//���ɵ�λ��״̬
    	String weekPSStateDesc="�ܵ�";//���ɵ�λ��״̬
    	String dayPSStateDesc="�յ�";//���ɵ�λ��״̬
    	
    	int dayPSValue=0;//���ɵ�λ��״̬
    	int weekPSValue=0;//���ɵ�λ��״̬
    	int monthPSValue=0;//���ɵ�λ��״̬
    	String comState = "";
    	if(eDayItem!=null) {
    		dayPSValue = eDayItem.getScValue().getPointSuspectedValueGap()<0?1:0;
    		if(dayPSValue == 1){
    			dayPSStateDesc = "����";
    		} else {
    			dayPSStateDesc = "�յ�";
    		}
    	}
    	 
    	if(eWeekItem!=null){
    		weekPSValue = eWeekItem.getScValue().getPointSuspectedValueGap()<0?1:0;
    		if(weekPSValue == 1){
    			weekPSStateDesc = "����";
    		} else {
    			weekPSStateDesc = "�ܵ�";
    		}
    	}
    		
    	if(eMonthItem!=null){
    		monthPSValue = eMonthItem.getScValue().getPointSuspectedValueGap()<0?1:0;
    		if(monthPSValue == 1){
    			monthPSStateDesc = "����";
    		} else {
    			monthPSStateDesc = "�µ�";
    		}
    	}
    	
    	if (type == 1 ){
    		comState = monthPSStateDesc+":"+weekPSStateDesc+":"+dayPSStateDesc;
    	} else {
    		comState = String.valueOf(monthPSValue)+String.valueOf(weekPSValue)+String.valueOf(dayPSValue);
    	}
    	return comState;
    	
    }
    
    //����״̬���
    public String getPriPointState(StockExcelItem eDayItem,StockExcelItem eWeekItem,StockExcelItem eMonthItem) throws IOException, ClassNotFoundException, SQLException
    {
    	int monthTrend=0;
    	int weekPointNum = 0;
    	int dayPointNum = 0;
    	String psState="";
    	float monthPSValueGap=0;//���ɵ�λ��
    	String fullId ="";
    	
    	if(eDayItem != null){
    		fullId= eDayItem.getFullId();
    	}
    	
    	if(eMonthItem == null) {
    		monthTrend = 0; //��
    		weekPointNum = 0;
    	} else {
    		monthPSValueGap = eMonthItem.getScValue().getPointSuspectedValueGap();
    		if(monthPSValueGap < 0)
    			monthTrend = 0;
    		else 
    			monthTrend = 1;
    		//monthTrend = getTread(eMonthItem);
    		
    		//ǰ����ʱ��
	    	String sMonthDate = eMonthItem.getScValue().getStartDate();
	    	weekPointNum = spDao.getUpOrDownPointNum(fullId,ConstantsInfo.WeekDataType,sMonthDate);
    	}
    	
    	
    	if(eWeekItem == null){
    		dayPointNum = 1;
    	} else {
    		//ǰ����ʱ��
    		String sWeekDate = eWeekItem.getScValue().getStartDate();
	    	dayPointNum = spDao.getUpOrDownPointNum(fullId,ConstantsInfo.DayDataType,sWeekDate);
	    	//System.out.println("sWeekDate:"+sWeekDate);
    	}
    	   	
    	//System.out.println(monthTrend+":"+weekPointNum+":"+dayPointNum);
    	psState = String.valueOf(monthTrend) +":"+ String.valueOf(weekPointNum)+":"+String.valueOf(dayPointNum);
    	
    	return psState;
    }
    
    //���Ƽ���״̬���
    public String getPSState(StockExcelItem eDayItem,StockExcelItem eWeekItem,StockExcelItem eMonthItem) throws IOException, ClassNotFoundException, SQLException
    {
    	int monthTrend=0;
    	int weekPointNum = 0;
    	int dayPointNum = 0;
    	String psState="";
    	float monthPSValueGap=0;//���ɵ�λ��
    	float weekPSValueGap=0;//���ɵ�λ��
    	int dayPSValueGap=0;//���ɵ�λ��
    	String fullId ="";
    	
    	if(eDayItem != null){
    		fullId= eDayItem.getFullId();
    	}
    	
    	if(eMonthItem == null) {
    		monthTrend = 1; //��
    		weekPointNum = 1;
    	} else {
    		monthPSValueGap = eMonthItem.getScValue().getPointSuspectedValueGap();
    		if(monthPSValueGap < 0)
    			monthTrend = 1;
    		else 
    			monthTrend =0;
    		//monthTrend = getTread(eMonthItem);
    		
    		//���Ƶ�
	    	String sMonthDate = eMonthItem.getScValue().getEndDate();
	    	weekPointNum = spDao.getUpOrDownPointNum(fullId,ConstantsInfo.WeekDataType,sMonthDate);
    	}
    	
    	
    	if(eWeekItem == null){
    		dayPointNum = 1;
    	} else {
    		String sWeekDate = eWeekItem.getScValue().getEndDate();
	    	dayPointNum = spDao.getUpOrDownPointNum(fullId,ConstantsInfo.DayDataType,sWeekDate);
	    	//System.out.println("sWeekDate:"+sWeekDate);
    	}
    	   	
    	//System.out.println(monthTrend+":"+weekPointNum+":"+dayPointNum);
    	psState = String.valueOf(monthTrend) + String.valueOf(weekPointNum)+String.valueOf(dayPointNum);
    	
    	return psState;
    }
    //��Ʊ״̬
    public String getBuySaleState(StockExcelItem eDayItem,StockExcelItem eWeekItem,StockExcelItem eMonthItem,int dataType) throws IOException, ClassNotFoundException, SQLException
    {
    	int largePSTrend=0;//
    	int smallPSTrend=0;
    	String desc ="";
    	String startDate =null;
    	String endDate =null;
    	String fullId =null;
    	int countI =0;
    	float spPri=0;
    	float spNext=0;
    	int flag = 0;
    	List<Float> spList = new ArrayList<Float>();
    	
    	//��  ͨ���ա������ݼ���
    	if (dataType == ConstantsInfo.DayDataType){
    		if(eDayItem ==null || eWeekItem==null)
        		return null;
    		
    		if(eDayItem.getScValue().getTread() == 0){  
    			smallPSTrend = 0; //�ռ�������
        	} else {
        		smallPSTrend = 1;
        	}
        	
        	if(eWeekItem.getScValue().getTread() == 0){  
        		largePSTrend = 0; //�ܼ�������
        	} else {
        		largePSTrend = 1;
        	}
        	
        	//���ɼ�ʱ����Ϊ��ʼʱ�� ����յļ�����Ϣ
        	startDate = eWeekItem.getScValue().getEndDate();
        	endDate = eWeekItem.getScValue().getCurDate(); 
        	fullId = eWeekItem.getFullId();
    	
    	} else {
    		
    		if(eWeekItem ==null || eMonthItem==null)
        		return null;
    		
    		if(eWeekItem.getScValue().getTread() == 0){  
    			smallPSTrend = 0; //�ռ�������
        	} else {
        		smallPSTrend = 1;
        	}
        	
        	if(eMonthItem.getScValue().getTread() == 0){  
        		largePSTrend = 0; //�ܼ�������
        	} else {
        		largePSTrend = 1;
        	}
        	
        	//���ɼ�ʱ����Ϊ��ʼʱ�� ����ܵļ�����Ϣ
        	startDate = eMonthItem.getScValue().getEndDate();
        	endDate = eMonthItem.getScValue().getCurDate(); 
        	fullId = eWeekItem.getFullId();
    	}
    	
    	///System.out.println("startDate:"+startDate);
    //	System.out.println("trend:"+largePSTrend);
    	
    	if(largePSTrend == 0) {
    		spList = spDao.getUpOrDownPoint(fullId , dataType, startDate, 1); //��������ֵ
    		//���ӵ�ǰ���Ƶ�
    		if (smallPSTrend == 1)
    			spList.add(eDayItem.getScValue().getEndValue());
    	} else {
			spList = spDao.getUpOrDownPoint(fullId , dataType, startDate, 0); //������Сֵ
			//���ӵ�ǰ���Ƶ�
    		if (smallPSTrend == 0)
    			spList.add(eDayItem.getScValue().getEndValue());
    	}
    	
		//System.out.println("spList size:"+spList.size());
		if(spList.size()<=1){
			if(largePSTrend == 0)
				desc="�����µ�";
			else
				desc="��������";
			
		} else {
			spPri = spList.get(0);
			//System.out.println("first:"+spPri);
    		for (countI=1; countI<spList.size();countI++){
    			spNext = spList.get(countI);
    			//System.out.println("next:"+spNext);
    			if(largePSTrend == 0) {//�µ�
	    			if(spNext>spPri){
	    				flag = 1;
	    				break;
	    			}
    			} else {
    				if(spNext<spPri){
	    				flag = 1;
	    				break;
	    			}
    			}
    			;
    			spPri = spNext; 
    	   	}
    		
    		if(flag == 1) {
    			
    			if (dataType == ConstantsInfo.DayDataType) {
    				if(largePSTrend == 0)
	    				desc ="�ܵײ�";
	    			else 
	    				desc ="�ܶ���";
    			} else {
    				if(largePSTrend == 0)
	    				desc ="�µײ�";
	    			else 
	    				desc ="�¶���";
    			}
    			
    		} else {
    			
    			if(largePSTrend == 0)
    				desc ="�ײ�����";
    			else 
    				desc ="��������";
    		}
		}
	    	
    //	System.out.println(desc);
    	return desc;
    }
	
    //�����ͳ��������Ҫ��ϼ������
	StockMixStatValue getMixStatValue(StockExcelItem eDayItem,StockExcelItem eWeekItem,StockExcelItem eMonthItem,int dataType) throws IOException, ClassNotFoundException, SQLException
	{
		StockMixStatValue sMixValue;
		
		String priComStateDesc=""; //ǰ����״̬��϶�Ӧ����
		String priComStateValue=""; //ǰ����״̬��϶�Ӧ����
		String priState=""; //ǰ�������
	
		String combStateDesc=""; //״̬��϶�Ӧ����
		String combStateValue=""; //״̬��϶�Ӧ����
		String comPSState="";//״̬�뼫�����߽�ϵ����֣���������
		String pSState=""; //���Ƽ������
		
		
		String buySaleGrade=""; //�ּ�����
		String buySaleState=""; //״̬
		
		buySaleGrade = getBuySaleGrade(eDayItem,eWeekItem,eMonthItem,dataType);
		buySaleState = getBuySaleState(eDayItem,eWeekItem,eMonthItem,dataType);
			
		priState= getPriPointState(eDayItem,eWeekItem,eMonthItem);
		priComStateDesc = getPriPStateValue(eDayItem,eWeekItem,eMonthItem,1);	
		//priComStateValue = getPriPStateValue(eDayItem,eWeekItem,eMonthItem,0);
		
		pSState= getPSState(eDayItem,eWeekItem,eMonthItem);
		combStateDesc = getPSStateValue(eDayItem,eWeekItem,eMonthItem,1);	
		combStateValue = getPSStateValue(eDayItem,eWeekItem,eMonthItem,0);
		
		//Ϊ����
		comPSState = combStateValue+pSState;
		
		//System.out.println("comPSState:"+comPSState);
		sMixValue = new StockMixStatValue(priComStateDesc,priState,comPSState,combStateDesc,pSState,buySaleGrade,buySaleState);

		return sMixValue;
	}
	
	
	StockExcelStatItem getExcelStatItem(StockExcelItem eDayItem,StockExcelItem eWeekItem,StockExcelItem eMonthItem)
	{
		StockExcelStatItem statItem;
		
		StockMixStatValue dayMixValue=null;
		StockMixStatValue weekMixValue=null;
		
		//�ȼ�����
		try {
			dayMixValue = getMixStatValue(eDayItem,eWeekItem,eMonthItem,ConstantsInfo.DayDataType);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//�ټ�����
		try {
			weekMixValue = getMixStatValue(eDayItem,eWeekItem,eMonthItem,ConstantsInfo.WeekDataType);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//ͳ��
		if (eDayItem == null){
			statItem = new StockExcelStatItem(null,null,null,null,null);   					
		}else if (eWeekItem == null){
			statItem = new StockExcelStatItem(eDayItem.getSsValue(),dayMixValue,
					null,null,null);
			
		} else if (eMonthItem == null) {
			statItem = new StockExcelStatItem(eDayItem.getSsValue(),dayMixValue,
					eWeekItem.getSsValue(),weekMixValue,null);
		} else {	   			
			statItem = new StockExcelStatItem(eDayItem.getSsValue(),dayMixValue,
					eWeekItem.getSsValue(),weekMixValue,eMonthItem.getSsValue());
		}
		return statItem;
	}
	
	//��������һ����ҵ������excel orderby
	public void writeExcelFormConceptInFirstIndustryOrderBy(String filePath, String fileTime) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
		     
	 	List<StockConceptInFirstIndustry> listConcept = new ArrayList<StockConceptInFirstIndustry>(); 
	 	List<String> listFirstIndustry = new ArrayList<String>(); 
    	//�õ���ǰһ����ҵcode
	 	listFirstIndustry=sbDao.getStockFirstIndustry();    	
    	
    	XSSFWorkbook wb=null;
    	XSSFSheet sheet =null;
    	String excleFileName=null;
        int flag_first=0;
        int titleRow =0;//��¼��ҵ����������
        sheetCount =0;
    	//�������һ����ҵ
        for (int countI=0; countI<listFirstIndustry.size();countI++)	
   		{
        	//�õ���ǰһ����ҵ�¸���code
        	String firstIndustryId = listFirstIndustry.get(countI);
        	
        	//һ����ҵ����
        	String firstIndustryName = sbDao.getStockFirstIndustryName(firstIndustryId);        	
        	System.out.println("һ����ҵ��"+firstIndustryName);        	
        	
        	//�õ�����id
        	listConcept = sbDao.getStockFirstIndustryConceptCode(firstIndustryId);
        	
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
		   		    stockRow = 1;
		   			//����excel
		   	 		ExcelCommon.createExcel(wb,sheet,filePath,excleFileName);
		   			writeExcelFromMarket(wb,sheet,filePath,excleFileName,fileTime);
		   			FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
		   			wb.write(fileOStream);		
		   	        fileOStream.close(); 
		   	        wb=null;
		   	        sheet=null;
	   			}
	   			
	   			StockExcelStatItem  statItem;   	
	   			
	   			excleFileName="Stock_Concept_"+fileTime+"_All_"+sheetCount+".xlsx";
	 			
	   			File file = new File(filePath +fileTime+ "\\"+excleFileName);
				// ������   
				FileInputStream fileIStream = new FileInputStream(file);  	
				wb = new XSSFWorkbook(fileIStream);   
				sheet = wb.getSheetAt(0);  
 
				//��ǰһ����ҵ
				StockConceptInFirstIndustry scon= listConcept.get(i);	
				String conceptCode = scon.getConceptCode();
				String conceptName = scon.getConceptName();
				if(conceptName==null)
					continue;
				 
				System.out.println("���"+conceptName);
				//if(!conceptName.equals("�ƽ��鱦"))
	   			//	continue;
				
				stockLogger.logger.fatal("���"+conceptName);
				//������� 
				stockRow++;
				titleRow = stockRow;
				
				//������
				StockBaseFace baseFaceConcept = new StockBaseFace(0,conceptCode,scon.getBaseExpect(),scon.getMain(),
						scon.getPsychology(),scon.getRisk(),scon.getPotential(),scon.getFaucet());
				ExcelCommon.writeExcelItemTitle(wb,sheet,i+":"+firstIndustryName,baseFaceConcept,stockRow);
	   					 	
	   			//�����������й�Ʊ
	   			List<StockToConcept> listConceptStock = new ArrayList<StockToConcept>();
	   			listConceptStock=sbDao.getStockToConcept(conceptCode);
	   			stockLogger.logger.debug("�����Ʊ����"+listConceptStock.size());
	   			
	   			//�����Ƿ�������
	   			List<StockExcelTotalInfo> listStockTotalInfoOrderBy = new ArrayList<StockExcelTotalInfo>(); 
	   			List<String> listName = new ArrayList<String>(); 
	   			int stockType=0;
	   			for(Iterator ie=listConceptStock.iterator();ie.hasNext();)
	   			{
	   				//stockRow++;
	   				StockToConcept toConstock =(StockToConcept) ie.next();
	   				String stockFullId = toConstock.getStockFullId();
	   			
	   				stockType=sbDao.getMarketType(stockFullId);
	   				//if(!stockFullId.equals("SZ002041"))
	   				//	continue;
	   				
	   				System.out.println("stockFullId:"+stockFullId);
	   				int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_DATA_STOCK);
	   		    	if(isTableExist == 0){//������
	   					stockLogger.logger.fatal("****stockFullId��"+stockFullId+"�����ڼ�ֵ��****");
	   					System.out.println(stockFullId+"��ֵ������****");
	   					continue;  
	   				}
	   				stockLogger.logger.fatal("****stockFullId��"+stockFullId+"****");
	   				
	   				StockExcelItem dayItem;
					StockExcelItem weekItem;
					StockExcelItem monthItem;
					
				//	int enableTingPai = 0;
					int enableTingPai = getEnableTingPai(stockFullId);
					
					//������
					StockBaseFace baseFace = sbDao.lookUpStockBaseFace(stockFullId);
					StockBaseYearInfo yearInfo = sbDao.lookUpStockBaseYearInfo(stockFullId);
			
					//�Ƿ�����
					int enableTwoRong = sbDao.lookUpStockTwoRong(stockFullId);	
					//����ֵ
			   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,toConstock.getStockName(),enableTwoRong,enableTingPai,baseFace,yearInfo);
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
	   				
	   				//�����ͳ����
	   				statItem = getExcelStatItem(dayItem, weekItem, monthItem);
	   				
	   				StockExcelTotalInfo setInfo =new StockExcelTotalInfo(soiValue,dayItem,weekItem,monthItem,statItem);
	   				
	   				//ͣ�� �ź���
	   			
	   				if(enableTingPai!=0 && setInfo.getStatItem()!=null  && setInfo.getStatItem().getDayMixStatItem()!=null) {
	   					setInfo.getStatItem().getDayMixStatItem().setComPSState("211211");
	   				} 
	   					
	   				listStockTotalInfoOrderBy.add(setInfo);
	   				listName.add(toConstock.getStockName()); 
	   			}
	   			//��¼��ҵ�ڸ���Ʊ��ʾ
	   			int dealWarns[] = new int[4];
	   			//����
	   			//Collections.sort(listStockTotalInfoOrderBy); 
	   			Collections.sort(listName,Collator.getInstance(java.util.Locale.CHINA));
	   			
	   			for (int kk=0;kk<listName.size();kk++)
	   			{
	   			for (int j=0;j<listStockTotalInfoOrderBy.size();j++)	
	   			{
	   				StockExcelTotalInfo setInfo = (StockExcelTotalInfo) listStockTotalInfoOrderBy.get(j);
	   				if(!listName.get(kk).equals(setInfo.getSoiValue().getName()))
	   					continue;
	   				stockRow++;
	   				stockLogger.logger.fatal("**write**stockFullId��"+setInfo.getSoiValue().getFullId()+"****");
	   				System.out.println("**write**stockFullId��"+setInfo.getSoiValue().getFullId()+"****");
	   				ExcelCommon.writeExcelStockOtherInfo(wb, sheet, setInfo.getSoiValue(), stockRow,1);
	   				//δͣ��
	   				if (setInfo.getSoiValue().getEnableTingPai() == 0) {
	   					
	   					statItem = setInfo.getStatItem();	   				
	   						
	   					//ͳ�� ������ͳ��ֵ
	   					ExcelCommon.writeExcelStatItem(wb,sheet,statItem,stockRow);
	   						
		   				if (setInfo.getDayItem() != null) {
		   					ExcelCommon.writeExcelItem(wb,sheet,setInfo.getDayItem(), stockRow, ConstantsInfo.DayDataType);
		   					//��¼��Ʊ������ʾ
			   				dealWarns[setInfo.getDayItem().getScValue().getDealWarn()]++;
		   				}
		   				if (setInfo.getWeekItem() != null)
		   					ExcelCommon.writeExcelItem(wb,sheet,setInfo.getWeekItem(), stockRow, ConstantsInfo.WeekDataType);
		   				if (setInfo.getMonthItem() != null)
		   					ExcelCommon.writeExcelItem(wb,sheet,setInfo.getMonthItem(),stockRow, ConstantsInfo.MonthDataType);	
		   			}
	   			}
	   			}
	   			
	   			int sub=5;//δ֪
	   			if(dealWarns != null && dealWarns.length!=0)
	   				sub = getMax(dealWarns);	
	   			
	   			//��ҵ������Ӧ��������ʾ
	   			ExcelCommon.writeExcelItemDealWall(wb,sheet,conceptName,sub,titleRow);
	   			listStockTotalInfoOrderBy =null;
	   			
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
	        
	      //��������
			//if(stockRow>20)
			//	break;	        
   		}
   		
        listConcept = null;
	}
	

	
	//��������һ����ҵ������excel orderby
	public void writeSummaryExcelFormConceptInFirstIndustryOrderBy(String filePath, String fileTime) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
		List<StockConceptInFirstIndustry> listConcept = new ArrayList<StockConceptInFirstIndustry>(); 
	 	List<String> listFirstIndustry = new ArrayList<String>(); 
	
    	//�õ���ǰһ����ҵcode
	 	listFirstIndustry=sbDao.getStockFirstIndustry();    	
    	
    	XSSFWorkbook wb=null;
    	XSSFSheet sheet =null;
    	String excleFileName=null;
        int flag_first=0;
        int titleRow =0;//��¼��ҵ����������
        sheetCount =0;
    	//�������һ����ҵ
        for (int countI=0; countI<listFirstIndustry.size();countI++)	
   		{
        	//�õ���ǰһ����ҵ�¸���code
        	String firstIndustryId = listFirstIndustry.get(countI);
        	
        	//һ����ҵ����
        	String firstIndustryName = sbDao.getStockFirstIndustryName(firstIndustryId);        	
        	System.out.println("һ����ҵ��"+firstIndustryName);        	
        	
        	//�õ�����id
        	listConcept = sbDao.getStockFirstIndustryConceptCode(firstIndustryId);
        	
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
		   			excleFileName="Stock_Concept_"+fileTime+"_Summary_"+sheetCount+".xlsx";	   		    
		   		    stockRow = 1;
		   			//����excel
		   	 		ExcelCommon.createSummaryExcel(wb,sheet,filePath,excleFileName,sdDao,stockDateColumnmap);
		   	 		writeSummaryExcelFromMarket(wb,sheet,filePath,excleFileName,fileTime);
		   			FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
		   			wb.write(fileOStream);		
		   	        fileOStream.close(); 
		   	        wb=null;
		   	        sheet=null;
	   			}
	   			
	   			StockExcelStatItem  statItem;   	
	   			
	   			excleFileName="Stock_Concept_"+fileTime+"_Summary_"+sheetCount+".xlsx";
	 			
	   			File file = new File(filePath +fileTime+ "\\"+excleFileName);
				// ������   
				FileInputStream fileIStream = new FileInputStream(file);  	
				wb = new XSSFWorkbook(fileIStream);   
				sheet = wb.getSheetAt(0);  
 
				//��ǰһ����ҵ
				StockConceptInFirstIndustry scon= listConcept.get(i);	
				 String conceptCode = scon.getConceptCode();
				 String conceptName=scon.getConceptName();
				 if(conceptName==null)
					 continue;
				 
				System.out.println("���"+conceptName);
				//if(!conceptName.equals("�ƽ��鱦"))
	   			//	continue;
				
				stockLogger.logger.fatal("���"+conceptName);			
	  	
				//������� 
				stockRow++;
				titleRow = stockRow;
				/*
				//������
				StockBaseFace baseFaceConcept = new StockBaseFace(0,conceptCode,scon.getBaseExpect(),scon.getMain(),
						scon.getPsychology(),scon.getRisk(),scon.getPotential(),scon.getFaucet());
				ExcelCommon.writeExcelItemTitle(wb,sheet,i+":"+firstIndustryName,baseFaceConcept,stockRow);
				*/
				
				ExcelCommon.writeExcelItemTitle(wb,sheet,conceptName,null,stockRow);
	   			//�����������й�Ʊ
	   			List<StockToConcept> listConceptStock = new ArrayList<StockToConcept>();
	   			listConceptStock=sbDao.getStockToConcept(conceptCode);
	   			stockLogger.logger.debug("�����Ʊ����"+listConceptStock.size());
	   			
	   			//���������
	   			List<StockSummary> listStockSummaryOrderBy = new ArrayList<StockSummary>(); 
	   			List<String> listName = new ArrayList<String>(); 	 
	   		
	   			for(Iterator ie=listConceptStock.iterator();ie.hasNext();)
	   			{
	   			//stockRow++;
	   				StockToConcept toConstock =(StockToConcept) ie.next();
	   				String stockName = toConstock.getStockName();
	   				listName.add(stockName);
	   			}
	   			//����������
	   			Collections.sort(listName,Collator.getInstance(java.util.Locale.CHINA));
	   			 			
	   				
   				for (int kk=0;kk<listName.size();kk++)
   	   			{
   	   			for (int j=0;j<listConceptStock.size();j++)	
   	   			{
   	   				
   	   				StockToConcept toConstock =(StockToConcept) listConceptStock.get(j);
   	   				if(!listName.get(kk).equals(toConstock.getStockName()))
   	   					continue;
	   			
	   				String stockFullId = toConstock.getStockFullId();
	   			
	   				System.out.println("stockFullId:"+stockFullId);	   				
	   				stockLogger.logger.fatal("****stockFullId��"+stockFullId+"****");
	   				
	   				int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_SUMMARY_STOCK);
	   		    	if(isTableExist == 0){//������
	   					stockLogger.logger.fatal("****stockFullId��"+stockFullId+"������ͳ�Ʊ�****");
	   					System.out.println(stockFullId+"ͳ�Ʊ�����****");
	   					continue;  
	   				}
	   		    	
	   		    	//ssDao.truncateSummaryStock(stockFullId);
	   		    		   		    	
					//����ֵ
			   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,toConstock.getStockName(),0,0,null,null);
			   		//ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow);
			   		
			   		stockRow++;
			   		ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow,0);
	   				
			   		//��ȡ���ͳ������
			   		List<StockSummary> stockSummaryInfo=new ArrayList<StockSummary>();
			   		
			   		stockSummaryInfo = ssDao.getSummaryFromSummaryTable(stockFullId,15);
			   		
			   		int extremeCol = 0;			  
			   		//���Ҷ�Ӧλ�ò�д��excel
				    for (int ij=0;ij<stockSummaryInfo.size();ij++)	
					{
				    	StockSummary sSum = stockSummaryInfo.get(ij);
				    	            
				    	if(sSum!=null && stockDateColumnmap.containsKey(sSum.getDayCurDate())) {
				        	extremeCol = stockDateColumnmap.get(sSum.getDayCurDate());				        	
				        	ExcelCommon.writeSummaryExcelItem(wb,sheet,sSum,extremeCol,stockRow,0);
				    	}
				    	
				 	}
	   					
				    //��һ���������һ���
				   // listStockSummaryOrderBy.add(stockSummaryInfo.get(0));
			   		//����
			   		//Collections.sort(listStockTotalInfoOrderBy); 	   		           
	   			
	   			}
   	   			}
	   			
	   		   		           	   			
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
	        
	      //��������
			//if(stockRow>30)
			//	break;	        
   		}
   		
        listConcept = null;
	}
	
	
	//��������һ����ҵ������excel orderby
	public void writeSummaryExcelFormFuturesOrderBy(String filePath, String fileTime) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
		List<StockMarket> listMarket = new ArrayList<StockMarket>(); 
		 
	 	//�õ���ǰ�����г�
	 	listMarket=sbDao.getStockMarket(ConstantsInfo.FuturesMarket);
		
    	XSSFWorkbook wb=null;
    	XSSFSheet sheet =null;
    	String excleFileName=null;
        int flag_first=0;
        int titleRow =0;//��¼��ҵ����������
        sheetCount =0;
    	//�������һ����ҵ
        for (int countI=0; countI<listMarket.size();countI++)	
   		{
        	      	
	    	//ÿ����ҵ�����excelһ��	      
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
		   			excleFileName="Stock_Futures_"+fileTime+"_Summary_"+sheetCount+".xlsx";	   		    
		   		    stockRow = 1;
		   			//����excel
		   	 		ExcelCommon.createSummaryExcel(wb,sheet,filePath,excleFileName,sdDao,stockDateColumnmap);
		   	 		//writeSummaryExcelFromMarket(wb,sheet,filePath,excleFileName,fileTime);
		   			FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
		   			wb.write(fileOStream);		
		   	        fileOStream.close(); 
		   	        wb=null;
		   	        sheet=null;
	   			}
	   			
	   			StockExcelStatItem  statItem;   	
	   			
	   			excleFileName="Stock_Futures_"+fileTime+"_Summary_"+sheetCount+".xlsx";
	 			
	   			File file = new File(filePath +fileTime+ "\\"+excleFileName);
				// ������   
				FileInputStream fileIStream = new FileInputStream(file);  	
				wb = new XSSFWorkbook(fileIStream);   
				sheet = wb.getSheetAt(0);  
 
				//�õ���ǰһ����ҵ�¸���code
	        	StockMarket sMarket = listMarket.get(countI);
	        	
				 String conceptCode = sMarket.getCode();
				 String conceptName=sMarket.getName();
				 if(conceptName==null)
					 continue;
				 
				System.out.println("�г���"+conceptName);
				//if(!conceptName.equals("�ƽ��鱦"))
	   			//	continue;
				
				stockLogger.logger.fatal("�г���"+conceptName);		
				//������� 
				stockRow++;
				titleRow = stockRow;				
				ExcelCommon.writeExcelItemTitle(wb,sheet,titleRow+":"+conceptName,null,stockRow);
   				 	
	   			//��ǰһ����ҵ
				List<StockToFutures> listFuturesStock = new ArrayList<StockToFutures>();   
	   			listFuturesStock=sbDao.getFuturesToStock(sMarket.getCode());
	   			stockLogger.logger.debug("�����Ʊ����"+listFuturesStock.size());	   			
	   			
	   			List<String> listName = new ArrayList<String>(); 	 
		   		
	   			for(Iterator ie=listFuturesStock.iterator();ie.hasNext();)
	   			{
	   			//stockRow++;
	   				StockToFutures toConstock =(StockToFutures) ie.next();
	   				String stockName = toConstock.getName();
	   				listName.add(stockName);
	   			}
	   			//����������
	   			Collections.sort(listName,Collator.getInstance(java.util.Locale.CHINA));
	   			 			
	   				
   				for (int kk=0;kk<listName.size();kk++)
   	   			{
   	   			for (int j=0;j<listFuturesStock.size();j++)	
   	   			{
	   			
	   				//stockRow++;
	   				StockToFutures toConstock =(StockToFutures) listFuturesStock.get(j);
	   			
   	   				if(!listName.get(kk).equals(toConstock.getName()))
   	   					continue;
	   				String stockFullId = toConstock.getCode();
	   			
	   				System.out.println("stockFullId:"+stockFullId);	   				
	   				stockLogger.logger.fatal("****stockFullId��"+stockFullId+"****");
	   				
	   				int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_SUMMARY_STOCK);
	   		    	if(isTableExist == 0){//������
	   					stockLogger.logger.fatal("****stockFullId��"+stockFullId+"������ͳ�Ʊ�****");
	   					System.out.println(stockFullId+"ͳ�Ʊ�����****");
	   					continue;  
	   				}
	   		    	//ssDao.truncateSummaryStock(stockFullId);
	   		    		   		    	
					//����ֵ
			   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,toConstock.getName(),0,0,null,null);
			   		//ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow);
			   		
			   		stockRow++;
			   		ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow,0);
	   				
			   		//��ȡ���ͳ������
			   		List<StockSummary> stockSummaryInfo=new ArrayList<StockSummary>();
			   		
			   		stockSummaryInfo = ssDao.getSummaryFromSummaryTable(stockFullId,15);
			   		
			   		int extremeCol = 0;			  
			   		//���Ҷ�Ӧλ�ò�д��excel
				    for (int ij=0;ij<stockSummaryInfo.size();ij++)	
					{
				    	StockSummary sSum = stockSummaryInfo.get(ij);
				    	            
				    	if(sSum!=null && stockDateColumnmap.containsKey(sSum.getDayCurDate())) {
				        	extremeCol = stockDateColumnmap.get(sSum.getDayCurDate());				        	
				        	ExcelCommon.writeSummaryExcelItem(wb,sheet,sSum,extremeCol,stockRow,1);
				    	}
				    	
				 	}
	   					
				    //��һ���������һ���
				   // listStockSummaryOrderBy.add(stockSummaryInfo.get(0));
			   		//����
			   		//Collections.sort(listStockTotalInfoOrderBy); 	   		           
	   			}
   	   			}
	   			   		           	   			
				FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
				wb.write(fileOStream);
				fileOStream.flush();
				fileIStream.close();
				fileOStream.close();              
				  
				listFuturesStock = null;	          
				 //��������
			//	if(stockRow>30)
			//		break;				
	   		}
	        
	      //��������
			//if(stockRow>30)
			//	break;	        
   		
   		
        listMarket = null;
	}

	
	//��������һ����ҵ������excel orderby
	public void writeOperationExcelFormFuturesOrderBy(String filePath, String fileTime) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
		List<StockMarket> listMarket = new ArrayList<StockMarket>(); 
		 
	 	//�õ���ǰ�����г�
	 	listMarket=sbDao.getStockMarket(ConstantsInfo.FuturesMarket);
		
    	XSSFWorkbook wb=null;
    	XSSFSheet sheet =null;
    	String excleFileName=null;
        int flag_first=0;
        int titleRow =0;//��¼��ҵ����������
        sheetCount =0;
    	//�������һ����ҵ
        for (int countI=0; countI<listMarket.size();countI++)	
   		{
        	      	
	    	//ÿ����ҵ�����excelһ��	      
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
		   			excleFileName="Stock_Futures_"+fileTime+"_Operation_"+sheetCount+".xlsx";	   		    
		   		    stockRow = 1;
		   			//����excel
		   	 		ExcelCommon.createOperationExcel(wb,sheet,filePath,excleFileName,sdDao,stockDateColumnmap);
		   	 		//writeSummaryExcelFromMarket(wb,sheet,filePath,excleFileName,fileTime);
		   			FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
		   			wb.write(fileOStream);		
		   	        fileOStream.close(); 
		   	        wb=null;
		   	        sheet=null;
	   			}
	   			
	   			StockExcelStatItem  statItem;   	
	   			
	   			excleFileName="Stock_Futures_"+fileTime+"_Operation_"+sheetCount+".xlsx";
	 			
	   			File file = new File(filePath +fileTime+ "\\"+excleFileName);
				// ������   
				FileInputStream fileIStream = new FileInputStream(file);  	
				wb = new XSSFWorkbook(fileIStream);   
				sheet = wb.getSheetAt(0);  
 
				//�õ���ǰһ����ҵ�¸���code
	        	StockMarket sMarket = listMarket.get(countI);
	        	
				 String conceptCode = sMarket.getCode();
				 String conceptName=sMarket.getName();
				 if(conceptName==null)
					 continue;
				 
				System.out.println("�г���"+conceptName);
				//if(!conceptName.equals("�ƽ��鱦"))
	   			//	continue;
				
				stockLogger.logger.fatal("�г���"+conceptName);			
	  	
				//������� 
				stockRow++;
				titleRow = stockRow;
				
				ExcelCommon.writeExcelItemTitle(wb,sheet,titleRow+":"+conceptName,null,stockRow);
   				 	
	   			//��ǰһ����ҵ
				List<StockToFutures> listFuturesStock = new ArrayList<StockToFutures>();   
	   			listFuturesStock=sbDao.getFuturesToStock(sMarket.getCode());
	   			stockLogger.logger.debug("�����Ʊ����"+listFuturesStock.size());
	   			
	   			List<String> listName = new ArrayList<String>(); 	 
		   		
	   			for(Iterator ie=listFuturesStock.iterator();ie.hasNext();)
	   			{
	   			//stockRow++;
	   				StockToFutures toConstock =(StockToFutures) ie.next();
	   				String stockName = toConstock.getName();
	   				listName.add(stockName);
	   			}
	   			//����������
	   			Collections.sort(listName,Collator.getInstance(java.util.Locale.CHINA));
	   			 			
	   				
   				for (int kk=0;kk<listName.size();kk++)
   	   			{
   	   			for (int j=0;j<listFuturesStock.size();j++)	
   	   			{
   	   				
   	   				StockToFutures toConstock =(StockToFutures) listFuturesStock.get(j);
   	   				if(!listName.get(kk).equals(toConstock.getName()))
   	   					continue;   			
	   			//stockRow++;
	   				String stockFullId = toConstock.getCode();
	   				
	   				//if(!stockFullId.equals("SH600895"))
	   				//	continue;
	   				System.out.println("stockFullId:"+stockFullId);	   				
	   				stockLogger.logger.fatal("****stockFullId��"+stockFullId+"****");
	   				
	   				int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_OPERATION_STOCK);
	   		    	if(isTableExist == 0){//������
	   					stockLogger.logger.fatal("****stockFullId��"+stockFullId+"������ͳ�Ʊ�****");
	   					System.out.println(stockFullId+"ͳ�Ʊ�����****");
	   					continue;  
	   				}
	   		    	
	   		    	//ssDao.truncateSummaryStock(stockFullId);
	   		    		   		    	
					//����ֵ
			   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,toConstock.getName(),0,0,null,null);
			   		//ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow);
			   		
			   		stockRow++;
			   		ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow,0);
	   				
			   		//��ȡ���ͳ������
			   		List<StockOperation> stockOperationInfo=new ArrayList<StockOperation>();
			   		
			   		stockOperationInfo = ssDao.getOperationFromOperationTable(stockFullId,30);
			   
			   		int extremeCol = 0;		
			   		int earn=0,stop=0,loss=0;
			   		float totalShouyi = 0; 
			   		//���Ҷ�Ӧλ�ò�д��excel
				    for (int ij=0;ij<stockOperationInfo.size();ij++)	
					{
				    	StockOperation sSop = stockOperationInfo.get(ij);
				    	        
				    	if(sSop!=null && stockDateColumnmap.containsKey(sSop.getOpDate())) {
				        	extremeCol = stockDateColumnmap.get(sSop.getOpDate());	
				        	
				        	String psState = ssDao.getpsStatusFromSummaryTable(stockFullId,sSop.getOpDate());
				        	
				        	ExcelCommon.writeOperationExcelItem(wb,sheet,sSop,psState, extremeCol,stockRow);
				        	
				        	//Ӯ
				        	if(sSop.getOpType() == ConstantsInfo.STOP ){ //ֹ
					    		stop++;
					    		totalShouyi += sSop.getStopRatio();
				        	} else if(sSop.getOpType() == ConstantsInfo.SALE && sSop.getEarnRatio()>=0) {
					    		earn++;	
					    		totalShouyi += sSop.getEarnRatio();
				        	} else if(sSop.getOpType() == ConstantsInfo.SALE && sSop.getLossRatio()<-0.000001) { //��
					    		loss++;
					    		totalShouyi += sSop.getLossRatio();
				        	}
					    	
				    	}    	
				 	}
				    System.out.println(earn+":"+stop+":"+loss);
				    int totalsize = earn+stop+loss;
				    if(totalsize > 0) {				    	
				    	ExcelCommon.writeOperationTotalExcelItem(wb,sheet,0 ,stockRow,earn,stop,loss,totalsize, totalShouyi);
				    }
	   					
				    //��һ���������һ���
				   // listStockSummaryOrderBy.add(stockSummaryInfo.get(0));
			   		//����
			   		//Collections.sort(listStockTotalInfoOrderBy); 	   		           
	   			
	   			}
   	   			}
	   			
	   		   		           	   			
				FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
				wb.write(fileOStream);
				fileOStream.flush();
				fileIStream.close();
				fileOStream.close();              
				  
				listFuturesStock = null;	          
				 //��������
			//	if(stockRow>30)
			//		break;				
	   		}
	        
	      //��������
			//if(stockRow>30)
			//	break;	        
   		
   		
        listMarket = null;
	}

	
	//��������һ����ҵ������excel orderby
	public void writeOperationExcelFormConceptInFirstIndustryOrderBy(String filePath, String fileTime) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
		List<StockConceptInFirstIndustry> listConcept = new ArrayList<StockConceptInFirstIndustry>(); 
	 	List<String> listFirstIndustry = new ArrayList<String>(); 
	
    	//�õ���ǰһ����ҵcode
	 	listFirstIndustry=sbDao.getStockFirstIndustry();    	
    	
    	XSSFWorkbook wb=null;
    	XSSFSheet sheet =null;
    	String excleFileName=null;
        int flag_first=0;
        int titleRow =0;//��¼��ҵ����������
        sheetCount =0;
    	//�������һ����ҵ
        for (int countI=0; countI<listFirstIndustry.size();countI++)	
   		{
        	//�õ���ǰһ����ҵ�¸���code
        	String firstIndustryId = listFirstIndustry.get(countI);
        	
        	//һ����ҵ����
        	String firstIndustryName = sbDao.getStockFirstIndustryName(firstIndustryId);        	
        	System.out.println("һ����ҵ��"+firstIndustryName);        	
        	
        	//�õ�����id
        	listConcept = sbDao.getStockFirstIndustryConceptCode(firstIndustryId);
        	
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
		   			excleFileName="Stock_Concept_"+fileTime+"_Operation_"+sheetCount+".xlsx";	   		    
		   		    stockRow = 1;
		   			//����excel
		   	 		ExcelCommon.createOperationExcel(wb,sheet,filePath,excleFileName,sdDao,stockDateColumnmap);
		   	 		writeOperationExcelFromMarket(wb,sheet,filePath,excleFileName,fileTime);
		   			FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
		   			wb.write(fileOStream);		
		   	        fileOStream.close(); 
		   	        wb=null;
		   	        sheet=null;
	   			}
	   		  	
	   			excleFileName="Stock_Concept_"+fileTime+"_Operation_"+sheetCount+".xlsx";
	 			
	   			File file = new File(filePath +fileTime+ "\\"+excleFileName);
				// ������   
				FileInputStream fileIStream = new FileInputStream(file);  	
				wb = new XSSFWorkbook(fileIStream);   
				sheet = wb.getSheetAt(0);  
 
				//��ǰһ����ҵ
				StockConceptInFirstIndustry scon= listConcept.get(i);	
				 String conceptCode = scon.getConceptCode();
				 String conceptName=scon.getConceptName();
				 if(conceptName==null)
					 continue;
				 
				System.out.println("���"+conceptName);
				//if(!conceptName.equals("�ƽ��鱦"))
	   			//	continue;
				
				stockLogger.logger.fatal("���"+conceptName);			
	  	
				//������� 
				stockRow++;
				titleRow = stockRow;
				
				//������				
				ExcelCommon.writeExcelItemTitle(wb,sheet,conceptName,null,stockRow);
				
	   			//�����������й�Ʊ
	   			List<StockToConcept> listConceptStock = new ArrayList<StockToConcept>();
	   			listConceptStock=sbDao.getStockToConcept(conceptCode);
	   			stockLogger.logger.debug("�����Ʊ����"+listConceptStock.size());
	   			
	   	
	   			List<String> listName = new ArrayList<String>(); 	 
		   		
	   			for(Iterator ie=listConceptStock.iterator();ie.hasNext();)
	   			{
	   			//stockRow++;
	   				StockToConcept toConstock =(StockToConcept) ie.next();
	   				String stockName = toConstock.getStockName();
	   				listName.add(stockName);
	   			}
	   			//����������
	   			Collections.sort(listName,Collator.getInstance(java.util.Locale.CHINA));
	   			 			
	   				
   				for (int kk=0;kk<listName.size();kk++)
   	   			{
   	   			for (int j=0;j<listConceptStock.size();j++)	
   	   			{
   	   				
   	   				StockToConcept toConstock =(StockToConcept) listConceptStock.get(j);
   	   				if(!listName.get(kk).equals(toConstock.getStockName()))
   	   					continue;
	   				//stockRow++;  			
	   				String stockFullId = toConstock.getStockFullId();
	   				
	   				//if(!stockFullId.equals("SH600598"))
	   				//	continue;
	   				System.out.println("stockFullId:"+stockFullId);	   				
	   				stockLogger.logger.fatal("****stockFullId��"+stockFullId+"****");
	   				
	   				int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_OPERATION_STOCK);
	   		    	if(isTableExist == 0){//������
	   					stockLogger.logger.fatal("****stockFullId��"+stockFullId+"������ͳ�Ʊ�****");
	   					System.out.println(stockFullId+"ͳ�Ʊ�����****");
	   					continue;  
	   				}
	   		    	
	   		    	//ssDao.truncateSummaryStock(stockFullId);
	   		    		   		    	
					//����ֵ
			   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,toConstock.getStockName(),0,0,null,null);
			   		//ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow);
			   		
			   		stockRow++;
			   		ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow,0);
	   				
			   		//��ȡ���ͳ������
			   		List<StockOperation> stockOperationInfo=new ArrayList<StockOperation>();
			   		
			   		stockOperationInfo = ssDao.getOperationFromOperationTable(stockFullId,30);
			   
			   		int extremeCol = 0;		
			   		int earn=0,stop=0,loss=0;
			   		float totalShouyi=0;
			   		//���Ҷ�Ӧλ�ò�д��excel
				    for (int ij=0;ij<stockOperationInfo.size();ij++)	
					{
				    	StockOperation sSop = stockOperationInfo.get(ij);
				    	            
				    	if(sSop!=null && stockDateColumnmap.containsKey(sSop.getOpDate())) {
				        	extremeCol = stockDateColumnmap.get(sSop.getOpDate());			
				        	
				        	String psState = ssDao.getpsStatusFromSummaryTable(stockFullId,sSop.getOpDate());
				        	
				        	ExcelCommon.writeOperationExcelItem(wb,sheet,sSop,psState,extremeCol,stockRow);
				        	
				        	//Ӯ
				        	if(sSop.getOpType() == ConstantsInfo.STOP ){ //ֹ
					    		stop++;
					    		totalShouyi += sSop.getStopRatio();
				        	} else if(sSop.getOpType() == ConstantsInfo.SALE && sSop.getEarnRatio()>=0) {
					    		earn++;	
					    		totalShouyi += sSop.getEarnRatio();
				        	} else if(sSop.getOpType() == ConstantsInfo.SALE && sSop.getLossRatio()<-0.000001) { //��
					    		loss++;
					    		totalShouyi += sSop.getLossRatio();
				        	}
				    	}    	
				 	}
				    System.out.println(earn+":"+stop+":"+loss);
				    int totalsize = earn+stop+loss;
				    if(totalsize > 0) {				    	
				    	ExcelCommon.writeOperationTotalExcelItem(wb,sheet,0 ,stockRow,earn,stop,loss,totalsize,totalShouyi);
				    }
	   					
				    //��һ���������һ���
				   // listStockSummaryOrderBy.add(stockSummaryInfo.get(0));
			   		//����
			   		//Collections.sort(listStockTotalInfoOrderBy); 	   		           
	   			
	   			}
   	   			}
	   			
	   		   		           	   			
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
	        
	      //��������
			//if(stockRow>30)
			//	break;	        
   		}
   		
        listConcept = null;
	}
	
	//����ҵ������excel orderby
	public void writeOperationExcelFormIndustryOrderBy(String filePath, String fileTime) throws SQLException, IOException, ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
	{
		
	 	List<StockIndustry> listIndustry = new ArrayList<StockIndustry>(); 	 
	 	//�õ���ǰ������ҵ
    	listIndustry=sbDao.getStockIndustry();
    	System.out.println("��ҵ������"+listIndustry.size());
    	
    	XSSFWorkbook wb=null;
    	XSSFSheet sheet =null;
    	String excleFileName=null;
        int flag_first=0;
        int titleRow =0;//��¼��ҵ����������
        sheetCount =0;
    	//�������һ����ҵ
        for (int countI=0; countI<listIndustry.size();countI++)	
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
		   			excleFileName="Stock_Industry_"+fileTime+"_Operation_"+sheetCount+".xlsx";	   		    
		   		    stockRow = 1;
		   			//����excel
		   	 		ExcelCommon.createOperationExcel(wb,sheet,filePath,excleFileName,sdDao,stockDateColumnmap);
		   	 		writeOperationExcelFromMarket(wb,sheet,filePath,excleFileName,fileTime);
		   			FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
		   			wb.write(fileOStream);		
		   	        fileOStream.close(); 
		   	        wb=null;
		   	        sheet=null;
	   			}
	   		  	
	   			excleFileName="Stock_Industry_"+fileTime+"_Operation_"+sheetCount+".xlsx";
	 			
	   			File file = new File(filePath +fileTime+ "\\"+excleFileName);
				// ������   
				FileInputStream fileIStream = new FileInputStream(file);  	
				wb = new XSSFWorkbook(fileIStream);   
				sheet = wb.getSheetAt(0);  
 
				//��ǰ��ҵ
				StockIndustry indu = listIndustry.get(countI);	
				String induCode = indu.getThirdcode();
				String induName = indu.getThirdname();
				if(induCode == null || induName == null)
					continue;				
				stockLogger.logger.fatal("��ҵ��"+induName);   		
				System.out.println("��ҵ��"+induName);			
				//��ҵ���� 
				stockRow++;
				titleRow = stockRow;		
	  			
				//������				
				ExcelCommon.writeExcelItemTitle(wb,sheet,induName,null,stockRow);
				
				//������ҵ���й�Ʊ
	   			List<StockToIndustry> listIndustryStock = new ArrayList<StockToIndustry>();   
	   			listIndustryStock=sbDao.getIndustryToStock(induCode);	   	
	   			stockLogger.logger.debug("��ҵ��Ʊ����"+listIndustryStock.size());
	   			
	   			List<String> listName = new ArrayList<String>(); 	 
		   		
	   			for(Iterator ie=listIndustryStock.iterator();ie.hasNext();)
	   			{
	   			//stockRow++;
	   				StockToIndustry toConstock =(StockToIndustry) ie.next();
	   				String stockName = toConstock.getStockName();
	   				listName.add(stockName);
	   			}
	   			//����������
	   			Collections.sort(listName,Collator.getInstance(java.util.Locale.CHINA));
	   			 			
	   				
   				for (int kk=0;kk<listName.size();kk++)
   	   			{
   	   			for (int j=0;j<listIndustryStock.size();j++)	
   	   			{
   	   				
   	   				StockToIndustry toConstock =(StockToIndustry) listIndustryStock.get(j);
   	   				if(!listName.get(kk).equals(toConstock.getStockName()))
   	   					continue;
	   				//stockRow++;  			
	   				String stockFullId = toConstock.getStockFullId();
	   				
	   				//if(!stockFullId.equals("SH600598"))
	   				//	continue;
	   				System.out.println("stockFullId:"+stockFullId);	   				
	   				stockLogger.logger.fatal("****stockFullId��"+stockFullId+"****");
	   				
	   				int isTableExist=sdDao.isExistStockTable(stockFullId,ConstantsInfo.TABLE_OPERATION_STOCK);
	   		    	if(isTableExist == 0){//������
	   					stockLogger.logger.fatal("****stockFullId��"+stockFullId+"������ͳ�Ʊ�****");
	   					System.out.println(stockFullId+"ͳ�Ʊ�����****");
	   					continue;  
	   				}
	   		    	
	   		    	//ssDao.truncateSummaryStock(stockFullId);
	   		    		   		    	
					//����ֵ
			   		StockOtherInfoValue soiValue=new StockOtherInfoValue(stockFullId,toConstock.getStockName(),0,0,null,null);
			   		//ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow);
			   		
			   		stockRow++;
			   		ExcelCommon.writeExcelStockOtherInfo(wb, sheet, soiValue, stockRow,0);
	   				
			   		//��ȡ���ͳ������
			   		List<StockOperation> stockOperationInfo=new ArrayList<StockOperation>();
			   		
			   		stockOperationInfo = ssDao.getOperationFromOperationTable(stockFullId,30);
			   
			   		int extremeCol = 0;		
			   		int earn=0,stop=0,loss=0;
			   		float totalShouyi=0;
			   		//���Ҷ�Ӧλ�ò�д��excel
				    for (int ij=0;ij<stockOperationInfo.size();ij++)	
					{
				    	StockOperation sSop = stockOperationInfo.get(ij);
				    	            
				    	if(sSop!=null && stockDateColumnmap.containsKey(sSop.getOpDate())) {
				        	extremeCol = stockDateColumnmap.get(sSop.getOpDate());			
				        	
				        	String psState = ssDao.getpsStatusFromSummaryTable(stockFullId,sSop.getOpDate());
				        	
				        	ExcelCommon.writeOperationExcelItem(wb,sheet,sSop,psState,extremeCol,stockRow);
				        	
				        	//Ӯ
				        	if(sSop.getOpType() == ConstantsInfo.STOP ){ //ֹ
					    		stop++;
					    		totalShouyi += sSop.getStopRatio();
				        	} else if(sSop.getOpType() == ConstantsInfo.SALE && sSop.getEarnRatio()>=0) {
					    		earn++;	
					    		totalShouyi += sSop.getEarnRatio();
				        	} else if(sSop.getOpType() == ConstantsInfo.SALE && sSop.getLossRatio()<-0.000001) { //��
					    		loss++;
					    		totalShouyi += sSop.getLossRatio();
				        	}
				    	}    	
				 	}
				    System.out.println(earn+":"+stop+":"+loss);
				    int totalsize = earn+stop+loss;
				    if(totalsize > 0) {				    	
				    	ExcelCommon.writeOperationTotalExcelItem(wb,sheet,0 ,stockRow,earn,stop,loss,totalsize,totalShouyi);
				    }
	   					
				    //��һ���������һ���
				   // listStockSummaryOrderBy.add(stockSummaryInfo.get(0));
			   		//����
			   		//Collections.sort(listStockTotalInfoOrderBy); 	   		           
	   			
	   			}
   	   			}
	   			
	   		   		           	   			
				FileOutputStream fileOStream = new FileOutputStream(filePath +fileTime+ "\\"+excleFileName);;
				wb.write(fileOStream);
				fileOStream.flush();
				fileIStream.close();
				fileOStream.close();              
				  
				listIndustryStock = null;	          
				 //��������
			//	if(stockRow>30)
			//		break;				
	   		}
	        
   		
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
        Connection stockSummaryConn = DbConn.getConnDB("stockConf/conn_summary_db.ini");
	   
     
		StockExcelPartitionMain se = new StockExcelPartitionMain(stockBaseConn,stockDataConn,stockPointConn,stockSummaryConn);
		//����
		
		//��Ʒ
	//	se.writeExcelFormFuturesOrderBy("export\\",dateNowStr);
	//	se.writeSummaryExcelFormFuturesOrderBy("export\\",dateNowStr);
		//����
//	se.writeOperationExcelFormFuturesOrderBy("export\\",dateNowStr);
		
	  //��Ʊ 
	//se.writeOperationExcelFormConceptInFirstIndustryOrderBy("export\\",dateNowStr);	
//	se.writeOperationExcelFormIndustryOrderBy("export\\",dateNowStr);	
	//se.writeSummaryExcelFormConceptInFirstIndustryOrderBy("export\\",dateNowStr);
		
	//se.writeExcelFormConceptInFirstIndustryOrderBy("export\\",dateNowStr);
	se.writeExcelFormIndustryOrderBy("export\\",dateNowStr);	
		
		stockBaseConn.close();
	    stockDataConn.close();
	    stockPointConn.close();
	    stockSummaryConn.close();
		
		Date endDate = new Date();
		//��������ʱ�����������
		long seconds =(endDate.getTime() - startDate.getTime())/1000;
		System.out.println("�ܹ���ʱ��"+seconds+"��");
		System.out.println("end");
		stockLogger.logger.fatal("excel stock export end");		
	}

}
