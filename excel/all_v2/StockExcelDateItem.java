package excel.all_v2;

public class StockExcelDateItem {
	
	private String curDate;//��ǰʱ��
	private float curValue;//��ǰ��λ
	
	private String startDate;//��ʼʱ��
	private float startValue;//��ʼ��λ
	private int  startDateGap;//����һ������ʱ���
	private String endDate; //����ʱ��
	private float endValue;//������λ
	private int  endDateGap;//���������ʱ���
	
	public StockExcelDateItem(){
		
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
	public int getStartDateGap() {
		return startDateGap;
	}
	public void setStartDateGap(int startDateGap) {
		this.startDateGap = startDateGap;
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
	public int getEndDateGap() {
		return endDateGap;
	}
	public void setEndDateGap(int endDateGap) {
		this.endDateGap = endDateGap;
	}

}
