package excel.all_v1;

public class StockExcelItemV1 {
	
	private String fullId;
	private int beforeDays; //提前
	private int flag;//上涨1 下跌0  编号
	private int nextFlag;//下次上涨1 下跌0  编号
	private int pointTimes;//极值点轮次
	private int curUpTimes;//当前上涨次数
	private int curDownTimes;//当前下跌次数
	
	private String curDate;//当前时间
	private float curValue;//当前点位
	
	private String startDate;//开始时间
	private float startValue;//开始点位
	private String endDate; //结束时间
	private float endValue;//结束点位
	private float curRange; //当前幅
	private String desireDate; //预期日期
	private StockDesireValue sdValue;
	private float bugValue;//买
	private float winValue;//赢
	private float loseVaule;//亏
	
	
	
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
	
	public StockExcelItemV1(String fullId,int flag,int nextFlag,int beforeDays,int pointTimes, int curUpTimes, int curDownTimes,String startDate,float startValue,String endDate,float endValue,float curRange,String desireDate,StockDesireValue sdValue,float bugValue,float winValue, float loseVaule)
	{
		
		this.fullId = fullId;
		this.flag = flag;
		this.nextFlag = nextFlag;
		this.beforeDays = beforeDays;
		this.pointTimes = pointTimes;
		this.curUpTimes = curUpTimes;
		this.curDownTimes = curDownTimes;		
		this.startDate = startDate;
		this.startValue = startValue;
		this.endDate = endDate;
		this.endValue = endValue;
		this.curRange = curRange;
		this.desireDate = desireDate;
		this.sdValue = sdValue;
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

	


}
