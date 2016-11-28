package excel.all_v1;

public class StockMaretInfoValue {
	
	private String fullId;
	private String startDate;//��ʼʱ��
	private String endDate; //����ʱ��
	private float curRange; //��ǰ��
	private int dataType;
	public StockMaretInfoValue()
	{
		
	}
	
	public StockMaretInfoValue(String fullId,String startDate,String endDate,float curRange)
	{
		this.fullId = fullId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.curRange = curRange;
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

	public float getCurRange() {
		return curRange;
	}

	public void setCurRange(float curRange) {
		this.curRange = curRange;
	}
	
	
}
