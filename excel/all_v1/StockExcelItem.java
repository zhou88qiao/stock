package excel.all_v1;

public class StockExcelItem {
	
	//
	private String fullId;	
	private StockStatValue ssValue;//ͳ����
	private StockCurValue scValue;//��ǰֵ
	private StockDesireValue sdValue;//Ԥ��ֵ 
	
	
	
	
	
	
	//���³�Ա��ʹ��
	
	private int beforeDays; //��ǰ
	private int flag;//����1 �µ�0  ���
	private int nextFlag;//�´�����1 �µ�0  ���
	private int pointTimes;//��ֵ���ִ�
	private int curUpTimes;//��ǰ���Ǵ���
	private int curDownTimes;//��ǰ�µ�����
	
	
	private String curDate;//��ǰʱ��
	private float curValue;//��ǰ��λ
	
	private String startDate;//��ʼʱ��
	private float startValue;//��ʼ��λ
	private int  startDateGap;//����һ������ʱ���
	private float startValueGap;//��ʼ��λ��
	private String endDate; //����ʱ��
	private float endValue;//������λ
	private int  endDateGap;//���������ʱ���
	private float endValueGap;//������λ��
	private float curRange; //��ǰ��
	private String desireDate; //Ԥ������
	private int desireDateGap; //Ԥ�����ڲ�		
	private float ratio; //����
	
	private float bugValue;//��
	private float winValue;//Ӯ
	private float loseVaule;//��
	
	
	
	public float getStartValue() {
		return startValue;
	}
	public void setStartValue(float startValue) {
		this.startValue = startValue;
	}
	public float getEndValue() {
		return endValue;
	}
	public void setEndValue(float endValue) {
		this.endValue = endValue;
	}
	
	public StockExcelItem(String fullId,StockStatValue ssValue,StockCurValue scValue,StockDesireValue sdValue)
	{
		this.fullId=fullId;
		this.ssValue=ssValue;
		this.scValue=scValue;
		this.sdValue=sdValue;		
	}
	
	public StockExcelItem(String fullId,int flag,int nextFlag,int beforeDays,int pointTimes, int curUpTimes, int curDownTimes,String curDate, float curValue,String startDate,float startValue,int startDateGap,float startValueGap,String endDate,float endValue,int endDateGap,float endValueGap,float curRange,String desireDate,int desireDateGap,StockDesireValue sdValue,float ratio,float bugValue,float winValue, float loseVaule)
	{
		
		this.fullId = fullId;
		this.flag = flag;
		this.nextFlag = nextFlag;
		this.beforeDays = beforeDays;
		this.pointTimes = pointTimes;
		this.curUpTimes = curUpTimes;
		this.curDownTimes = curDownTimes;		
		this.curDate = curDate;
		this.curValue = curValue;
		this.startDate = startDate;
		this.startValue = startValue;
		this.startDateGap = startDateGap;
		this.startValueGap = startValueGap;
		this.endDate = endDate;
		this.endValue = endValue;
		this.endDateGap = endDateGap;
		this.endValueGap = endValueGap;
		this.curRange = curRange;
		this.desireDate = desireDate;
		this.desireDateGap = desireDateGap;
		this.sdValue = sdValue;
		this.ratio=ratio;
		this.bugValue = bugValue;
		this.winValue = winValue;
		this.loseVaule = loseVaule;		
		
	}
	public String getFullId() {
		return fullId;
	}
	public void setFullId(String fullId) {
		this.fullId = fullId;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
	public String getDesireDate() {
		return desireDate;
	}
	public void setDesireDate(String desireDate) {
		this.desireDate = desireDate;
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
	public int getBeforeDays() {
		return beforeDays;
	}
	public void setBeforeDays(int beforeDays) {
		this.beforeDays = beforeDays;
	}
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public int getPointTimes() {
		return pointTimes;
	}
	public void setPointTimes(int pointTimes) {
		this.pointTimes = pointTimes;
	}
	public int getCurUpTimes() {
		return curUpTimes;
	}
	public void setCurUpTimes(int curUpTimes) {
		this.curUpTimes = curUpTimes;
	}
	public int getCurDownTimes() {
		return curDownTimes;
	}
	public void setCurDownTimes(int curDownTimes) {
		this.curDownTimes = curDownTimes;
	}
	public int getNextFlag() {
		return nextFlag;
	}
	public void setNextFlag(int nextFlag) {
		this.nextFlag = nextFlag;
	}
	public float getCurRange() {
		return curRange;
	}
	public void setCurRange(float curRange) {
		this.curRange = curRange;
	}
	public StockDesireValue getSdValue() {
		return sdValue;
	}
	public void setSdValue(StockDesireValue sdValue) {
		this.sdValue = sdValue;
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
	public int getStartDateGap() {
		return startDateGap;
	}
	public void setStartDateGap(int startDateGap) {
		this.startDateGap = startDateGap;
	}
	public int getEndDateGap() {
		return endDateGap;
	}
	public void setEndDateGap(int endDateGap) {
		this.endDateGap = endDateGap;
	}
	public float getStartValueGap() {
		return startValueGap;
	}
	public void setStartValueGap(float startValueGap) {
		this.startValueGap = startValueGap;
	}
	public float getEndValueGap() {
		return endValueGap;
	}
	public void setEndValueGap(float endValueGap) {
		this.endValueGap = endValueGap;
	}
	public int getDesireDateGap() {
		return desireDateGap;
	}
	public void setDesireDateGap(int desireDateGap) {
		this.desireDateGap = desireDateGap;
	}
	public float getRatio() {
		return ratio;
	}
	public void setRatio(float ratio) {
		this.ratio = ratio;
	}
	public StockStatValue getSsValue() {
		return ssValue;
	}
	public void setSsValue(StockStatValue ssValue) {
		this.ssValue = ssValue;
	}
	public StockCurValue getScValue() {
		return scValue;
	}
	public void setScValue(StockCurValue scValue) {
		this.scValue = scValue;
	}
	

}
