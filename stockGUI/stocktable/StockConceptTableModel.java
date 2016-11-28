package stockGUI.stocktable;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import dao.StockConcept;



public class StockConceptTableModel extends AbstractTableModel{
	
	private List<StockConcept> stockAllConcept;
	
	private static final int STOCK_MARKET_COLUMN_COUNT = 2;
	
	public StockConceptTableModel(List<StockConcept> stockAllConcept) {
		this.stockAllConcept=stockAllConcept;	
	}
	
	//��дgetColumnName��������������
	public String getColumnName(int c){
		String colummName=null;
		switch(c)
		{
		case 0:
			colummName="����";
			break;
		case 1:			
		default:
			colummName="����";
			break;
		}
		return colummName;
	}
	//��������
	public int getColumnCount() {
		
		return STOCK_MARKET_COLUMN_COUNT;
	}

	@Override
	public int getRowCount() {
		
		return stockAllConcept.size();
	}

	@Override//���õ�Ԫ��ֵ
	public Object getValueAt(int row, int col) {
		Object oj;
		StockConcept sc;
		sc=stockAllConcept.get(row);
		switch(col)
		{
		case 0:		
			oj=sc.getCode();
			break;	
		case 1:				
			oj=sc.getName();
			break;
		default:
			return null;
		}
		return oj;
	}

}
