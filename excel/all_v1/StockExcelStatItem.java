package excel.all_v1;

//统计项
public class StockExcelStatItem {
	private String fullId;
	private int dayBeforeDays; //提前
	private int weekBeforeDays; //提前
	private int monthBeforeDays; //提前
	private int dayFlag;//上涨1 下跌0  编号
	private int weekFlag;//上涨1 下跌0  编号
	private int monthFlag;//上涨1 下跌0  编号
	private int dayPointTimes;//极值点轮次
	private int weekPointTimes;//极值点轮次
	private int monthPointTimes;//极值点轮次
	private int dayCurUpTimes;//当前上涨次数
	private int weekCurUpTimes;//当前上涨次数
	private int monthCurUpTimes;//当前上涨次数
	private int dayCurDownTimes;//当前下跌次数
	private int weekCurDownTimes;//当前下跌次数
	private int monthCurDownTimes;//当前下跌次数
	public StockExcelStatItem(int dayBeforeDays,int weekBeforeDays,int monthBeforeDays,int dayFlag,int weekFlag,int monthFlag,int dayPointTimes,int weekPointTimes,int monthPointTimes,int dayCurUpTimes,
			int weekCurUpTimes,int monthCurUpTimes,int dayCurDownTimes,int weekCurDownTimes,int monthCurDownTimes)
	{
		this.dayBeforeDays = dayBeforeDays;
		this.weekBeforeDays = weekBeforeDays;
		this.monthBeforeDays = monthBeforeDays;
		this.dayFlag = dayFlag;
		this.weekFlag = weekFlag;
		this.monthFlag = monthFlag;		
		this.dayPointTimes = dayPointTimes;
		this.weekPointTimes = weekPointTimes;
		this.monthPointTimes = monthPointTimes;
		this.dayCurUpTimes = dayCurUpTimes;		
		this.weekCurUpTimes = weekCurUpTimes;
		this.monthCurUpTimes = monthCurUpTimes;
		this.dayCurDownTimes = dayCurDownTimes;
		this.weekCurDownTimes = weekCurDownTimes;
		this.monthCurDownTimes = monthCurDownTimes;
	}
	public String getFullId() {
		return fullId;
	}
	public void setFullId(String fullId) {
		this.fullId = fullId;
	}
	public int getDayBeforeDays() {
		return dayBeforeDays;
	}
	public void setDayBeforeDays(int dayBeforeDays) {
		this.dayBeforeDays = dayBeforeDays;
	}
	public int getWeekBeforeDays() {
		return weekBeforeDays;
	}
	public void setWeekBeforeDays(int weekBeforeDays) {
		this.weekBeforeDays = weekBeforeDays;
	}
	public int getMonthBeforeDays() {
		return monthBeforeDays;
	}
	public void setMonthBeforeDays(int monthBeforeDays) {
		this.monthBeforeDays = monthBeforeDays;
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
	public int getDayPointTimes() {
		return dayPointTimes;
	}
	public void setDayPointTimes(int dayPointTimes) {
		this.dayPointTimes = dayPointTimes;
	}
	public int getWeekPointTimes() {
		return weekPointTimes;
	}
	public void setWeekPointTimes(int weekPointTimes) {
		this.weekPointTimes = weekPointTimes;
	}
	public int getMonthPointTimes() {
		return monthPointTimes;
	}
	public void setMonthPointTimes(int monthPointTimes) {
		this.monthPointTimes = monthPointTimes;
	}
	public int getDayCurUpTimes() {
		return dayCurUpTimes;
	}
	public void setDayCurUpTimes(int dayCurUpTimes) {
		this.dayCurUpTimes = dayCurUpTimes;
	}
	public int getWeekCurUpTimes() {
		return weekCurUpTimes;
	}
	public void setWeekCurUpTimes(int weekCurUpTimes) {
		this.weekCurUpTimes = weekCurUpTimes;
	}
	public int getMonthCurUpTimes() {
		return monthCurUpTimes;
	}
	public void setMonthCurUpTimes(int monthCurUpTimes) {
		this.monthCurUpTimes = monthCurUpTimes;
	}
	public int getDayCurDownTimes() {
		return dayCurDownTimes;
	}
	public void setDayCurDownTimes(int dayCurDownTimes) {
		this.dayCurDownTimes = dayCurDownTimes;
	}
	public int getWeekCurDownTimes() {
		return weekCurDownTimes;
	}
	public void setWeekCurDownTimes(int weekCurDownTimes) {
		this.weekCurDownTimes = weekCurDownTimes;
	}
	public int getMonthCurDownTimes() {
		return monthCurDownTimes;
	}
	public void setMonthCurDownTimes(int monthCurDownTimes) {
		this.monthCurDownTimes = monthCurDownTimes;
	}
	
}
