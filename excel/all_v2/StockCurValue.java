package excel.all_v2;

public class StockCurValue {
	
	private String priDate; //ǰ��ʱ��
	private float priHighOrLowest; //ǰ�߻�ǰ�ͼ��㣨�����ڶ������㣩
	private float reversalRegion;//��ת����	
	private String startDate;//��ʼʱ�� ����ʱ�䣨ǰһ�����㣩
	private float startValue;//��ʼ��λ	
	private String endDate; //����ʱ��  ���Ƽ���
	private float endValue;//������λ	
	private String curDate;//��ǰʱ��
	private float curValue;//��ʼ��λ	
	private float workRegion;//�������� 6����0 0.385 1 0.5
	
	private float bugValue;//��
	private float winValue;//Ӯ
	private float loseVaule;//��
	
	private int dealWarn;//������ʾ 0 ���� 1���� 2��ע 3���� 
	private int curState;//��ǰ״̬ -1Ϊ������0Ϊ�µ� 1Ϊ����
	private int tread;//��ǰ����	0�� 1��
	
	//��벿��
	private int  pointSuspectedDateGap;//���������Ƽ���ʱ���
	private float pointSuspectedValueGap;//���������Ƽ���λ��
	private int  pointCurDateGap;//��һ�������뵱ǰʱ���
	private float  pointCurValueGap;//��һ�������뵱ǰ��λ��
	private int  suspectedCurDateGap;//���Ƽ����뵱ǰʱ���
	private float suspectedCurValueGap;//���Ƽ����뵱ǰ��λ��	
	
	private int  marketPSDateGap;//����̼���ʱ���
	private float  marketPSSpace;//�ռ�	
	private int  marketPCDateGap;////����̼���ʱ���
	private float  marketPCSpace;//�ռ�
	private int  marketSCDateGap;////������ɵ�ʱ���
	private float  marketSCSpace;////�ռ�		
	private int trendConsistent; //����������Ƿ�һ�� 0 һ�£�1��һ��
	
	public StockCurValue(String priDate,float priHighOrLowest,float reversalRegion, String startDate,  float startValue,String endDate, float endValue,
			String curDate,float curValue,float workRegion,
			float bugValue,float winValue,float loseVaule,int dealWarn,int curState,int tread,
			int pointSuspectedDateGap,float pointSuspectedValueGap, int pointCurDateGap, float pointCurValueGap, 
			int suspectedCurDateGap,float suspectedCurValueGap,
			int marketPSDateGap, float marketPSSpace,int marketPCDateGap, float marketPCSpace,int marketSCDateGap, float marketSCSpace,
			int trendConsistent) {
		
		this.priDate = priDate;
		this.priHighOrLowest = priHighOrLowest;
		this.reversalRegion = reversalRegion;
		this.startDate = startDate;
		this.startValue = startValue;
		this.endDate = endDate;		
		this.endValue = endValue;		
		this.curDate = curDate;
		this.curValue = curValue;
		this.workRegion = workRegion;
		
		this.bugValue = bugValue;
		this.winValue = winValue;
		this.loseVaule = loseVaule;		
		this.dealWarn = dealWarn;
		this.curState = curState;
		this.tread = tread;
		
		this.pointSuspectedDateGap = pointSuspectedDateGap;
		this.pointSuspectedValueGap = pointSuspectedValueGap;
		this.pointCurDateGap = pointCurDateGap;
		this.pointCurValueGap = pointCurValueGap;
		this.suspectedCurDateGap = suspectedCurDateGap;
		this.suspectedCurValueGap = suspectedCurValueGap;
		
		this.marketPSDateGap = marketPSDateGap;
		this.marketPSSpace = marketPSSpace;
		this.marketPCDateGap = marketPCDateGap;
		this.marketPCSpace = marketPCSpace;
		this.marketSCDateGap = marketSCDateGap;
		this.marketSCSpace = marketSCSpace;		
		
		this.trendConsistent = trendConsistent;
		
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public float getStartValue() {
		return startValue;
	}

	public void setStartValue(float startValue) {
		this.startValue = startValue;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public float getEndValue() {
		return endValue;
	}

	public void setEndValue(float endValue) {
		this.endValue = endValue;
	}

	public String getCurDate() {
		return curDate;
	}

	public void setCurDate(String curDate) {
		this.curDate = curDate;
	}

	public float getCurValue() {
		return curValue;
	}

	public void setCurValue(float curValue) {
		this.curValue = curValue;
	}




	public int getPointSuspectedDateGap() {
		return pointSuspectedDateGap;
	}

	public void setPointSuspectedDateGap(int pointSuspectedDateGap) {
		this.pointSuspectedDateGap = pointSuspectedDateGap;
	}

	public float getPointSuspectedValueGap() {
		return pointSuspectedValueGap;
	}

	public void setPointSuspectedValueGap(float pointSuspectedValueGap) {
		this.pointSuspectedValueGap = pointSuspectedValueGap;
	}

	public int getPointCurDateGap() {
		return pointCurDateGap;
	}

	public void setPointCurDateGap(int pointCurDateGap) {
		this.pointCurDateGap = pointCurDateGap;
	}

	public float getPointCurValueGap() {
		return pointCurValueGap;
	}

	public void setPointCurValueGap(float pointCurValueGap) {
		this.pointCurValueGap = pointCurValueGap;
	}

	public int getSuspectedCurDateGap() {
		return suspectedCurDateGap;
	}

	public void setSuspectedCurDateGap(int suspectedCurDateGap) {
		this.suspectedCurDateGap = suspectedCurDateGap;
	}

	public float getSuspectedCurValueGap() {
		return suspectedCurValueGap;
	}

	public void setSuspectedCurValueGap(float suspectedCurValueGap) {
		this.suspectedCurValueGap = suspectedCurValueGap;
	}

	

	public int getTread() {
		return tread;
	}

	public void setTread(int tread) {
		this.tread = tread;
	}

	public int getTrendConsistent() {
		return trendConsistent;
	}

	public void setTrendConsistent(int trendConsistent) {
		this.trendConsistent = trendConsistent;
	}

	public float getBugValue() {
		return bugValue;
	}

	public void setBugValue(float bugValue) {
		this.bugValue = bugValue;
	}

	public float getWinValue() {
		return winValue;
	}

	public void setWinValue(float winValue) {
		this.winValue = winValue;
	}

	public float getLoseVaule() {
		return loseVaule;
	}

	public void setLoseVaule(float loseVaule) {
		this.loseVaule = loseVaule;
	}

	public int getDealWarn() {
		return dealWarn;
	}

	public void setDealWarn(int dealWarn) {
		this.dealWarn = dealWarn;
	}

	public int getCurState() {
		return curState;
	}

	public void setCurState(int curState) {
		this.curState = curState;
	}

	public float getWorkRegion() {
		return workRegion;
	}

	public void setWorkRegion(float workRegion) {
		this.workRegion = workRegion;
	}

	public float getPriHighOrLowest() {
		return priHighOrLowest;
	}

	public void setPriHighOrLowest(float priHighOrLowest) {
		this.priHighOrLowest = priHighOrLowest;
	}

	public float getReversalRegion() {
		return reversalRegion;
	}

	public void setReversalRegion(float reversalRegion) {
		this.reversalRegion = reversalRegion;
	}

	public String getPriDate() {
		return priDate;
	}

	public void setPriDate(String priDate) {
		this.priDate = priDate;
	}

	public int getMarketPSDateGap() {
		return marketPSDateGap;
	}

	public void setMarketPSDateGap(int marketPSDateGap) {
		this.marketPSDateGap = marketPSDateGap;
	}

	public float getMarketPSSpace() {
		return marketPSSpace;
	}

	public void setMarketPSSpace(float marketPSSpace) {
		this.marketPSSpace = marketPSSpace;
	}

	public int getMarketPCDateGap() {
		return marketPCDateGap;
	}

	public void setMarketPCDateGap(int marketPCDateGap) {
		this.marketPCDateGap = marketPCDateGap;
	}

	public float getMarketPCSpace() {
		return marketPCSpace;
	}

	public void setMarketPCSpace(float marketPCSpace) {
		this.marketPCSpace = marketPCSpace;
	}

	public int getMarketSCDateGap() {
		return marketSCDateGap;
	}

	public void setMarketSCDateGap(int marketSCDateGap) {
		this.marketSCDateGap = marketSCDateGap;
	}

	public float getMarketSCSpace() {
		return marketSCSpace;
	}

	public void setMarketSCSpace(float marketSCSpace) {
		this.marketSCSpace = marketSCSpace;
	}

}
