package dao;

public class StockConceptInFirstIndustry {
	
	private int  id;
	private String firstIndustryCode;//һ��
	private String firstIndustryName;
	private String conceptCode; //����
	private String conceptName;
	private String baseExpect;//����Ԥ��
	private String main; //����
	private String psychology; //����
	private String risk; //����
	private String potential; //Ǳ��
	private String faucet; //��ͷ
	
	
	
	public StockConceptInFirstIndustry()
	{
		
	}

	
	
	public String getFirstIndustryCode() {
		return firstIndustryCode;
	}
	public void setFirstIndustryCode(String firstIndustryCode) {
		this.firstIndustryCode = firstIndustryCode;
	}
	public String getFirstIndustryName() {
		return firstIndustryName;
	}
	public void setFirstIndustryName(String firstIndustryName) {
		this.firstIndustryName = firstIndustryName;
	}
	public String getConceptCode() {
		return conceptCode;
	}
	public void setConceptCode(String conceptCode) {
		this.conceptCode = conceptCode;
	}
	public String getConceptName() {
		return conceptName;
	}
	public void setConceptName(String conceptName) {
		this.conceptName = conceptName;
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
}
