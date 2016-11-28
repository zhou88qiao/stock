package common;

import org.apache.poi.xssf.usermodel.XSSFColor;

public class ConstantsInfo {
	public final static int ALLDataType=6;
	public final static int UnComputeDataType=0; //δ�����
	public final static int DayDataType=1;
	public final static int WeekDataType=2;
	public final static int MonthDataType=3;
	public final static int SeasonDataType=4;
	public final static int YearDataType=5;
	
	public final static int MA5Type=5;
	public final static int MA10Type=10;
	public final static boolean DEBUG=false;
	
	public final static int  XSSFNORMAL=-1;
	
	public final static int  NoColor=0;
	public final static int  REDColor=1;	
	public final static int  CYANColor=2;
	public final static int  GRAYColor=3;
	public final static int  BLUEColor=4;
	public final static int  ORANGEColor=5;
	public final static int  GREENColor=6;

		

	public final static int  ExtremeDateDay=1;	
	public final static int  ExtremeDateDayWeek=2;
	public final static int  ExtremeDateDayWeekMonth=3;
	public final static int  ExtremeDateWeek=4;
	public final static int  ExtremeDateMonth=5;
	public final static int  ExtremeDateDayMonth=6;
	public final static int  ExtremeDateWeekMonth=7;
	public final static int  ExtremeDateOther=8;
	
	public final static int  StockIndustry=1;
	public final static int  StockConcept=2;
	public final static int  StockRegional=3;
	
	public final static int  StockCalAllData=0;//������ʷȫ������
	public final static int  StockCalCurData=1;//���㵱ǰ����
	
	
	public final static int  StockMarket=0;//��Ʊ�г�
	public final static int  FuturesMarket=1;//��Ʒ�г�
	
	
	public final static int  ALLMarket=0;//����
	public final static int  DPMarket=1;//���� sh000001 sz399001 sz399005 sz399006
	public final static int  SHMarket=2;//��֤ sh600
	public final static int  SZMarket=3;//��֤ sz000	
	public final static int  CYMarket=4;//��ҵ��sz300
	public final static int  ZXMarket=5;//��С��sz002
	
	public final static int  ADD=1;//
	public final static int  DEL=2;//
	public final static int  EDIT=3;//
	
	
	public final static int  TABLE_DATA_STOCK=1;//
	public final static int  TABLE_POINT_STOCK=2;//
	public final static int  TABLE_SUMMARY_STOCK=3;//
	public final static int  TABLE_OPERATION_STOCK=4;//
	
		
	public final static String  STOCK_DATA_TABLE_NAME="stock_data_";
	public final static String  STOCK_POINT_TABLE_NAME="stock_point_";
	public final static String  STOCK_SUMMARY_TABLE_NAME="stock_summary_";
	public final static String  STOCK_OPERATION_TABLE_NAME="stock_operation_";
	
	public final static float  STOCK_DESIRE1=(float) 0.382;//
	public final static float  STOCK_DESIRE2=(float) 0.5;//
	public final static float  STOCK_DESIRE3=(float) 0.682;//
	public final static float  STOCK_DESIRE4=(float) 0.75;//
	public final static float  STOCK_DESIRE5=1;//
	public final static float  STOCK_DESIRE6=(float) 1.08;//	
	public final static float  STOCK_DESIRE7=(float) 1.5;//
	
	// 0 ���� 1���� 2��ע 3����
	public final static int  DEAL_WARN_BUG=3;//
	public final static int  DEAL_WARN_INTEREST=2;//
	public final static int  DEAL_WARN_SALE=1;//
	public final static int  DEAL_WARN_SEE=0;//
	
	
	public final static int  BUG=1;//����
	public final static int  STOP=2;//ֹ
	public final static int  SALE=3;//����
	
	public final static int  DEAL_DATE=11;//

}
