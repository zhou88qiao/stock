package excel.all_v1;

public class StockExcelItem {
	
	//
	private String fullId;	
	private StockStatValue ssValue;//统计项
	private StockCurValue scValue;//当前值
	private StockDesireValue sdValue;//预期值 
	
	
	
	
	
	
	//以下成员不使用
	
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
	private int  startDateGap;//与上一个极点时间差
	private float startValueGap;//开始点位差
	private String endDate; //结束时间
	private float endValue;//结束点位
	private int  endDateGap;//与最近极点时间差
	private float endValueGap;//结束点位差
	private float curRange; //当前幅
	private String desireDate; //预期日期
	private int desireDateGap; //预期日期差		
	private float ratio; //比率
	
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
