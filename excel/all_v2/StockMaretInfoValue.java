package excel.all_v2;

public class StockMaretInfoValue {
	
	private String fullId;
	private int flag; //1Ϊ���ǣ���1Ϊ�µ�
	private String startDate;//��ʼʱ��
	private String endDate; //����ʱ��
	private float valueGap; //��ֵ
	private int dataType;
	public StockMaretInfoValue()
	{
		
	}
	
	public StockMaretInfoValue(String fullId,int flag,String startDate,String endDate,float valueGap)
	{
		this.fullId = fullId;
		this.flag = flag;
		this.startDate = startDate;
		this.endDate = endDate;
		this.valueGap = valueGap;
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

	
	public float getValueGap() {
		return valueGap;
	}

	public void setValueGap(float valueGap) {
		this.valueGap = valueGap;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}
	
	
}
