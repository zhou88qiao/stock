package dao;

public class StockToFutures {
	
	private int  id;
	private String code;
	private String name;

	private String futuresCode;
	private String futuresName;
	
	private String baseExpect;//����Ԥ��	
	private String main; //����
	private String psychology; //����
	private String risk; //����
	private String potential; //Ǳ��
	private String faucet; //��ͷ
	
	public String getBaseExpect() {
		return baseExpect;
	}

	public void setBaseExpect(String baseExpect) {
		this.baseExpect = baseExpect;
	}

	public String getMain() {
		return main;
	}

	public void setMain(String main) {
		this.main = main;
	}

	public String getPsychology() {
		return psychology;
	}

	public void setPsychology(String psychology) {
		this.psychology = psychology;
	}

	public String getRisk() {
		return risk;
	}

	public void setRisk(String risk) {
		this.risk = risk;
	}

	public String getPotential() {
		return potential;
	}

	public void setPotential(String potential) {
		this.potential = potential;
	}

	public String getFaucet() {
		return faucet;
	}

	public void setFaucet(String faucet) {
		this.faucet = faucet;
	}

	public StockToFutures()
	{
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFuturesCode() {
		return futuresCode;
	}

	public void setFuturesCode(String futuresCode) {
		this.futuresCode = futuresCode;
	}

	public String getFuturesName() {
		return futuresName;
	}

	public void setFuturesName(String futuresName) {
		this.futuresName = futuresName;
	}
}
