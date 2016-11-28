package stockGUI.stocktable;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import dao.StockConcept;
import dao.StockRegional;
import dao.StockSingle;

public class StockSingleTableModel extends AbstractTableModel{
	
	private List<StockSingle> stockAllSingle;	
	private static final int STOCK_SINGLE_COLUMN_COUNT =5;
	
	public StockSingleTableModel(List<StockSingle> stockAllSingle) {
		this.stockAllSingle=stockAllSingle;	
	}

	//��дgetColumnName��������������
	public String getColumnName(int c){
		String colummName="";
		switch(c)
		{
		case 0:
			colummName="��Ʊ����";
			break;
		case 1:		
			colummName="��Ʊ����";
			break;
		case 2:		
			colummName="������ҵ";
			break;
		case 3:		
			colummName="����";
			break;	
		case 4:		
			colummName="������ȯ";
			break;
		default:
			break;
		}
		return colummName;
	}
	
	public int getColumnCount() {
		return STOCK_SINGLE_COLUMN_COUNT;
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return stockAllSingle.size();
	}
	
	public void removeRow(int row) {		
		stockAllSingle.remove(row);		
	}

	@Override
	public Object getValueAt(int row, int col) {
		Object oj;
		StockSingle ss;
		ss=stockAllSingle.get(row);
		switch(col)
		{
		case 0:		
			oj=ss.getStockFullId();
			break;	
		case 1:				
			oj=ss.getStockName();
			break;
		case 2:				
			oj=ss.getThirdName();
			break;
		case 3:				
			oj=ss.getStockConcept();
			break;	
		case 4:				
			oj=ss.getEnableMarginTrading();
			break;
		default:
			return null;
		}
		return oj;
	}

}
