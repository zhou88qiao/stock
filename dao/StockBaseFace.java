package dao;

public class StockBaseFace {	
	private int id;
	private String stockFullId;	
	private String baseExpect;//����Ԥ��	
	private String main; //����
	private String psychology; //����
	private String risk; //����
	private String potential; //Ǳ��
	private String faucet; //��ͷ
	
	public StockBaseFace()
	{
		
	}
	

	public StockBaseFace(int id, String stockFullId,String baseExpect,String main,String psychology,
			String risk,String potential,String faucet) {
		this.id = id;
		this.stockFullId = stockFullId;
		this.baseExpect= baseExpect;		
		this.main = main;
		this.psychology =psychology;
		this.risk =risk;
		this.potential = potential;
		this.faucet =faucet;
	}
	

	public String getStockFullId() {
		return stockFullId;
	}


	public void setStockFullId(String stockFullId) {
		this.stockFullId = stockFullId;
	}


	public String getBaseExpect() {
		return baseExpect;
	}


	public void setBaseExpect(String baseExpect) {
		this.baseExpect = baseExpect;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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


	
}
