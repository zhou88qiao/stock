package excel.all_v1;

import dao.StockBaseFace;

/*excel中股票其他信息，以后可增加市值 心理等*/
public class StockOtherInfoValue {
	private String fullId;//名字
	private String name;//名字
	private int marginTrading;//两融
	private int enableTingPai;//是否停牌
	private StockBaseFace baseFace;//基本面


	public StockOtherInfoValue(String fullId,String name,int marginTrading,int enableTingPai, StockBaseFace baseFace )
	{
		this.fullId =fullId;
		this.name=name;
		this.marginTrading=marginTrading;
		this.enableTingPai=enableTingPai;
		this.baseFace = baseFace;
	}

	public int getEnableTingPai() {
		return enableTingPai;
	}

	public void setEnableTingPai(int enableTingPai) {
		this.enableTingPai = enableTingPai;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMarginTrading() {
		return marginTrading;
	}

	public void setMarginTrading(int marginTrading) {
		this.marginTrading = marginTrading;
	}

	public String getFullId() {
		return fullId;
	}

	public void setFullId(String fullId) {
		this.fullId = fullId;
	}

	public StockBaseFace getBaseFace() {
		return baseFace;
	}

	public void setBaseFace(StockBaseFace baseFace) {
		this.baseFace = baseFace;
	}
}
