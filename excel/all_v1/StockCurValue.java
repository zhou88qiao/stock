package excel.all_v1;

public class StockCurValue {
	
	

	//ǰ�벿��
	private String startDate;//��ʼʱ��
	private float startValue;//��ʼ��λ	
	private String endDate; //����ʱ��
	private float endValue;//������λ	
	private float curRange; //��ǰ��
	
	private float bugValue;//��
	private float winValue;//Ӯ
	private float loseVaule;//��
	
	//��벿��
	private String curDate;//��ǰʱ��
	private float curValue;//��ǰ��λ
	
	private int  startDateGap;//����һ������ʱ���
	private float startValueGap;//��ʼ��λ��
	private int  endDateGap;//���������ʱ���
	private float endValueGap;//������λ��	
	
	private int  startMarketDateGap;//����̿�ʼʱ���c
	private int  endMarketDateGap;////����̽���ʱ���
	private float  marketSpace;////�ռ�	
	
	private float ratio; //�յ����б���
	
	public StockCurValue(String startDate, float startValue,
			String endDate, float endValue, float curRange,
			float bugValue,float winValue,float loseVaule,
			String curDate, float curValue, int startDateGap,
			float startValueGap, int endDateGap, float endValueGap,
			int startMarketDateGap, int endMarketDateGap,
			float marketSpace, float ratio) {
		
		this.startDate = startDate;
		this.startValue = startValue;
		this.endDate = endDate;		
		this.endValue = endValue;
		this.curRange = curRange;
		this.bugValue = bugValue;
		this.winValue = winValue;
		this.loseVaule = loseVaule;
		
		this.curDate = curDate;
		this.curValue = curValue;
		this.startDateGap = startDateGap;
		this.startValueGap = startValueGap;
		this.endDateGap = endDateGap;
		this.endValueGap = endValueGap;
		this.startMarketDateGap = startMarketDateGap;
		this.endMarketDateGap = endMarketDateGap;
		this.marketSpace = marketSpace;
		this.ratio = ratio;
		
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

	public float getCurRange() {
		return curRange;
	}

	public void setCurRange(float curRange) {
		this.curRange = curRange;
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

	public float getStartValueGap() {
		return startValueGap;
	}

	public void setStartValueGap(float startValueGap) {
		this.startValueGap = startValueGap;
	}

	public int getEndDateGap() {
		return endDateGap;
	}

	public void setEndDateGap(int endDateGap) {
		this.endDateGap = endDateGap;
	}

	public float getEndValueGap() {
		return endValueGap;
	}

	public void setEndValueGap(float endValueGap) {
		this.endValueGap = endValueGap;
	}

	public int getStartMarketDateGap() {
		return startMarketDateGap;
	}

	public void setStartMarketDateGap(int startMarketDateGap) {
		this.startMarketDateGap = startMarketDateGap;
	}

	public int getEndMarketDateGap() {
		return endMarketDateGap;
	}

	public void setEndMarketDateGap(int endMarketDateGap) {
		this.endMarketDateGap = endMarketDateGap;
	}

	public float getMarketSpace() {
		return marketSpace;
	}

	public void setMarketSpace(float marketSpace) {
		this.marketSpace = marketSpace;
	}

	public float getRatio() {
		return ratio;
	}

	public void setRatio(float ratio) {
		this.ratio = ratio;
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

}
