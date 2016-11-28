package excel.all_v2;

//ͳ����
public class StockExcelStatItem {
	private String fullId;
	private StockStatValue dayStatItem; //�����
	private StockMixStatValue dayMixStatItem; //�ջ��
	private StockStatValue weekStatItem; //�����
	private StockMixStatValue weekMixStatItem; //�ܻ��
	private StockStatValue monthStatItem; //�����
	
	/*
	private int dayFlag;//����1 �µ�0  ���
	private int weekFlag;//����1 �µ�0  ���
	private int monthFlag;//����1 �µ�0  ���
	
	private int upOrDownFlag; //��ǰ��ͣ1���ͣ0���
	private int dayUpOrDownTimes;//�ǵ�ͣ����
	private int weekUpOrDownTimes;//�ǵ�ͣ����
	private int monthUpOrDownTimes;//�ǵ�ͣ����
	*/
	
	public StockExcelStatItem(StockStatValue dayStatItem,StockMixStatValue dayMixStatItem,
			StockStatValue weekStatItem,StockMixStatValue weekMixStatItem,
			StockStatValue monthStatItem)
	{
		
		this.dayStatItem = dayStatItem;
		this.dayMixStatItem = dayMixStatItem;
		this.weekStatItem = weekStatItem;		
		this.weekMixStatItem = weekMixStatItem;
		this.monthStatItem = monthStatItem;
	}
	
	/*
	public StockExcelStatItem(int dayFlag,int weekFlag,int monthFlag,int upOrDownFlag,int dayUpOrDownTimes,int weekUpOrDownTimes,int monthUpOrDownTimes)
	{
		this.dayFlag = dayFlag;
		this.weekFlag = weekFlag;
		this.monthFlag = monthFlag;		
		this.upOrDownFlag = upOrDownFlag;
		this.dayUpOrDownTimes = dayUpOrDownTimes;
		this.weekUpOrDownTimes = weekUpOrDownTimes;
		this.monthUpOrDownTimes = monthUpOrDownTimes;
	}
	public String getFullId() {
		return fullId;
	}
	public void setFullId(String fullId) {
		this.fullId = fullId;
	}
	public int getDayFlag() {
		return dayFlag;
	}
	public void setDayFlag(int dayFlag) {
		this.dayFlag = dayFlag;
	}
	public int getWeekFlag() {
		return weekFlag;
	}
	public void setWeekFlag(int weekFlag) {
		this.weekFlag = weekFlag;
	}
	public int getMonthFlag() {
		return monthFlag;
	}
	public void setMonthFlag(int monthFlag) {
		this.monthFlag = monthFlag;
	}
	public int getDayUpOrDownTimes() {
		return dayUpOrDownTimes;
	}
	public void setDayUpOrDownTimes(int dayUpOrDownTimes) {
		this.dayUpOrDownTimes = dayUpOrDownTimes;
	}
	public int getWeekUpOrDownTimes() {
		return weekUpOrDownTimes;
	}
	public void setWeekUpOrDownTimes(int weekUpOrDownTimes) {
		this.weekUpOrDownTimes = weekUpOrDownTimes;
	}
	public int getMonthUpOrDownTimes() {
		return monthUpOrDownTimes;
	}
	public void setMonthUpOrDownTimes(int monthUpOrDownTimes) {
		this.monthUpOrDownTimes = monthUpOrDownTimes;
	}
	
	public int getUpOrDownFlag() {
		return upOrDownFlag;
	}
	public void setUpOrDownFlag(int upOrDownFlag) {
		this.upOrDownFlag = upOrDownFlag;
	}
	*/
	public StockStatValue getDayStatItem() {
		return dayStatItem;
	}

	public void setDayStatItem(StockStatValue dayStatItem) {
		this.dayStatItem = dayStatItem;
	}

	public StockMixStatValue getDayMixStatItem() {
		return dayMixStatItem;
	}

	public void setDayMixStatItem(StockMixStatValue dayMixStatItem) {
		this.dayMixStatItem = dayMixStatItem;
	}

	public StockStatValue getWeekStatItem() {
		return weekStatItem;
	}

	public void setWeekStatItem(StockStatValue weekStatItem) {
		this.weekStatItem = weekStatItem;
	}

	public StockMixStatValue getWeekMixStatItem() {
		return weekMixStatItem;
	}

	public void setWeekMixStatItem(StockMixStatValue weekMixStatItem) {
		this.weekMixStatItem = weekMixStatItem;
	}

	public StockStatValue getMonthStatItem() {
		return monthStatItem;
	}

	public void setMonthStatItem(StockStatValue monthStatItem) {
		this.monthStatItem = monthStatItem;
	}
	

	
}
