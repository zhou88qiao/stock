package excel.all_v2;

//����ա��ܡ���ÿ�ֵ�ͳ���е������������ݣ�����Ҫ��ϼ����
public class StockStatValue {
	
	private int tread;//��ǰ ����1 �µ�0 
	private float range;//�����Ƿ�
	private int priUpDateGap;//ǰһ��ͣʱ���
	private int upOrdownFlag;//�ǵ�ͣ��ǣ�1��ͣ 0��ͣ
	private int upOrdownTimes;//�ǵ�ͣ����	
	
	private int  pointSuspectedDateGap;//���������Ƽ���ʱ���
	private int  suspectedCurDateGap;//���Ƽ����뵱ǰʱ���
	private int buySaleWarn; //������ʾ
	
	public StockStatValue(){
		
	}
	
	public StockStatValue(int tread,float range,int priUpDateGap, int upOrdownFlag,int upOrdownTimes,
			int pointSuspectedDateGap,int suspectedCurDateGap,int buySaleWarn){
		this.tread = tread;
		this.range = range;
		this.priUpDateGap = priUpDateGap;
		this.upOrdownFlag = upOrdownFlag;
		this.upOrdownTimes = upOrdownTimes;
		this.pointSuspectedDateGap = pointSuspectedDateGap;
		this.suspectedCurDateGap = suspectedCurDateGap;
		this.buySaleWarn = buySaleWarn;
	}
	
	

	public int getTread() {
		return tread;
	}

	public void setTread(int tread) {
		this.tread = tread;
	}

	public int getUpOrdownTimes() {
		return upOrdownTimes;
	}

	public void setUpOrdownTimes(int upOrdownTimes) {
		this.upOrdownTimes = upOrdownTimes;
	}

	public int getUpOrdownFlag() {
		return upOrdownFlag;
	}

	public void setUpOrdownFlag(int upOrdownFlag) {
		this.upOrdownFlag = upOrdownFlag;
	}

	public float getRange() {
		return range;
	}

	public void setRange(float range) {
		this.range = range;
	}

	public int getPriUpDateGap() {
		return priUpDateGap;
	}

	public void setPriUpDateGap(int priUpDateGap) {
		this.priUpDateGap = priUpDateGap;
	}

	public int getPointSuspectedDateGap() {
		return pointSuspectedDateGap;
	}

	public void setPointSuspectedDateGap(int pointSuspectedDateGap) {
		this.pointSuspectedDateGap = pointSuspectedDateGap;
	}

	public int getSuspectedCurDateGap() {
		return suspectedCurDateGap;
	}

	public void setSuspectedCurDateGap(int suspectedCurDateGap) {
		this.suspectedCurDateGap = suspectedCurDateGap;
	}

	public int getBuySaleWarn() {
		return buySaleWarn;
	}

	public void setBuySaleWarn(int buySaleWarn) {
		this.buySaleWarn = buySaleWarn;
	}
	
}
