package excel.all_v2;


//����ա��ܡ���ͳ����Ҫ��ϼ���� 
public class StockMixStatValue {
	
	
	private String priComState; //ǰ����״̬���
	private String priState; //ǰ�������
	
	
	private String comPSState;//״̬�뼫�����߽�ϵ����֣���������
	private String comState; //����״̬���
	private String psState; //���Ƽ������
	
	
	private String buySaleGrade; //�ּ�����
	private String buySaleState; //�£��ܶ������������ǵ�
	
	
	public StockMixStatValue(){
		
	}
	
	public StockMixStatValue(String priComState,String priState,String comPSState,String comState,String psState,String buySaleGrade,String buySaleState){
		this.priComState = priComState;
		this.priState = priState;
		this.comPSState = comPSState;
		this.comState = comState;
		this.psState = psState;
		this.buySaleGrade = buySaleGrade;
		this.buySaleState = buySaleState;
	}
	
	public String getBuySaleGrade() {
		return buySaleGrade;
	}

	public void setBuySaleGrade(String buySaleGrade) {
		this.buySaleGrade = buySaleGrade;
	}

	public String getBuySaleState() {
		return buySaleState;
	}

	public void setBuySaleState(String buySaleState) {
		this.buySaleState = buySaleState;
	}

	public String getComState() {
		return comState;
	}

	public void setComState(String comState) {
		this.comState = comState;
	}

	public String getPsState() {
		return psState;
	}

	public void setPsState(String psState) {
		this.psState = psState;
	}

	public String getComPSState() {
		return comPSState;
	}

	public void setComPSState(String comPSState) {
		this.comPSState = comPSState;
	}

	public String getPriComState() {
		return priComState;
	}

	public void setPriComState(String priComState) {
		this.priComState = priComState;
	}

	public String getPriState() {
		return priState;
	}

	public void setPriState(String priState) {
		this.priState = priState;
	}


	
	
}
