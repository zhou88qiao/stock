package ibatis;

import java.util.List;

public class testIbatis {

	
	public static void main(String[] args) {
		StockSingleImpl stockSingleImpl = new StockSingleImpl();
		System.out.println("���Բ�ѯ����");
		List<String> fullId = stockSingleImpl.getStockSingleAllFullId();
		for (String str : fullId) {
			System.out.println(str);
		}

	}

}
