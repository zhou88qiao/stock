package stockGUI.stocktable;

import java.util.Iterator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import dao.DayStock;
import dao.StockPoint;

public class StockTableModel extends AbstractTableModel
{
	private List<StockPoint> stockDayPoint;
	private List<StockPoint> stockWeekPoint;
	private List<StockPoint> stockMonthPoint;
	private List<StockPoint> stockPoint;
	
	//private static final int STOCK_COLUMN_COUNT = 12;
	private static final int STOCK_COLUMN_COUNT = 5;
	
	public StockTableModel(List<StockPoint> stockDayPoint,List<StockPoint> stockWeekPoint,List<StockPoint> stockMonthPoint)
	{
		this.stockDayPoint=stockDayPoint;
		this.stockWeekPoint=stockWeekPoint;
		this.stockMonthPoint=stockMonthPoint;
	/*	
	   for(int week= 0; week < stockWeekPoint.size(); week++)  
        {  
           
            for(int day= 0; day < stockDayPoint.size(); day++)  
            {
            	if(stockWeekPoint.get(week).getExtremeDate().equals(stockDayPoint.get(day).getExtremeDate()))
            	{
            			
            	
            		StockPoint dayWeekPoint=new StockPoint(stockDayPoint.get(day).get());
            		stockDayPoint.set(day, element)
            	}
            }
            
        }
		for(Iterator itWeek = stockWeekPoint.iterator();itWeek.hasNext();)
    	{
            StockPoint wPoint=(StockPoint)itWeek.next();	 	            
            for(Iterator itDay = stockDayPoint.iterator();itDay.hasNext();)
        	{
            	StockPoint dPoint=(StockPoint)itDay.next();	 
              	if(dPoint.getExtremeDate().equals(wPoint.getExtremeDate()))
              	{
              			
              	
              	}
              		
        	}
          
        	
    	}
		for(Iterator itMonth = stockMonthPoint.iterator();itMonth.hasNext();)
	    {
    	}
		
		for(Iterator itWeek = stockWeekPoint.iterator();itWeek.hasNext();)
    	{
            StockPoint sPointWeek=(StockPoint)itWeek.next();	 
            for(Iterator itWeek = stockWeekPoint.iterator();itWeek.hasNext();)
        	{
            stockPoint.add(sPointDay);
    	}
		*/
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
			colummName="��ֵʱ��";
			break;
		case 2:			
			colummName="��ֵ";
			break;
		case 3:			
			colummName="����ָ��";
			break;
		case 4:		
			colummName="������";
			break;
		case 5:
		default:
			colummName="������";
			break;
		
		}
		return colummName;
	}
	
	public int getColumnCount() {
		return STOCK_COLUMN_COUNT;
	}
	
	@Override
	public int getRowCount() {
		return stockDayPoint.size()+stockWeekPoint.size()+stockMonthPoint.size();
		//return 20;
	}
	
//	static 
	
	@Override
	public Object getValueAt(int row, int col) {
		
		Object oj;
		String type="��";
		StockPoint sp=null;
		int dayPonitSize=stockDayPoint.size();
		int weekPonitSize=stockWeekPoint.size();
		int monthPonitSize=stockMonthPoint.size();
		if(row<dayPonitSize)
			sp=stockDayPoint.get(row);
		else if(row<dayPonitSize+weekPonitSize)
			sp=stockWeekPoint.get(row-dayPonitSize);
		else 
			sp=stockMonthPoint.get(row-dayPonitSize-weekPonitSize);
		
		switch(col)
		{
		case 0:		
			if(row<dayPonitSize)
				type="��";
			else if(row<dayPonitSize+weekPonitSize)
				type="��";
			else 
				type="��";	
			oj=type;
			break;	
		case 1:				
			oj=sp.getExtremeDate();
			break;		
		case 2:		
			oj=sp.getExtremePrice();
			break;
		case 3:
			oj=sp.getRatio();
			break;	
		case 4:
			oj=sp.getFromDate();
			break;			
		case 5:
			oj=sp.getToDate();
			break;
		default:
			return null;
			
		}
		return oj;
	}
}
