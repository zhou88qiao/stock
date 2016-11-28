package excel.all_v1;

public class StockStatValue {
	private int beforeDays; //提前
	private int flag;//上涨1 下跌0  编号
	private int nextFlag;//下次上涨1 下跌0  编号
	private int pointTimes;//极值点轮次
	private int curUpTimes;//当前上涨次数
	private int curDownTimes;//当前下跌次数
	
	public StockStatValue(){
		
	}
	
	public StockStatValue(int beforeDays,int flag,int nextFlag,int pointTimes,int curUpTimes,int curDownTimes){
		this.beforeDays = beforeDays;
		this.flag = flag;
		this.nextFlag = nextFlag;
		this.pointTimes = pointTimes;
		this.curUpTimes = curUpTimes;
		this.curDownTimes = curDownTimes;
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
	public int getNextFlag() {
		return nextFlag;
	}
	public void setNextFlag(int nextFlag) {
		this.nextFlag = nextFlag;
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
}
