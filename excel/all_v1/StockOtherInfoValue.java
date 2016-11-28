package excel.all_v1;

import dao.StockBaseFace;

/*excel�й�Ʊ������Ϣ���Ժ��������ֵ �����*/
public class StockOtherInfoValue {
	private String fullId;//����
	private String name;//����
	private int marginTrading;//����
	private int enableTingPai;//�Ƿ�ͣ��
	private StockBaseFace baseFace;//������


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
