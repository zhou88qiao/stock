package excel.all_v1;

import java.text.ParseException;

import date.timer.stockDateTimer;

//public class StockExcelTotalInfo implements Comparable<StockExcelTotalInfo>{
//按周涨幅比排序
public class StockExcelTotalInfo implements Comparable{
	
	
	private StockOtherInfoValue soiValue;
	private StockExcelItem dayItem;
	private StockExcelItem weekItem;
	private StockExcelItem monthItem;
	private StockExcelStatItem  statItem;
	
	
	public StockExcelTotalInfo(StockOtherInfoValue soiValue,StockExcelItem dayItem,StockExcelItem weekItem,StockExcelItem monthItem,StockExcelStatItem statItem)
	{
		this.soiValue = soiValue;
		this.dayItem = dayItem;
		this.weekItem = weekItem;
		this.monthItem = monthItem;
		this.statItem = statItem;
	}
	public StockOtherInfoValue getSoiValue() {
		return soiValue;
	}

	public void setSoiValue(StockOtherInfoValue soiValue) {
		this.soiValue = soiValue;
	}

	public StockExcelItem getDayItem() {
		return dayItem;
	}

	public void setDayItem(StockExcelItem dayItem) {
		this.dayItem = dayItem;
	}

	public StockExcelItem getWeekItem() {
		return weekItem;
	}

	public void setWeekItem(StockExcelItem weekItem) {
		this.weekItem = weekItem;
	}

	public StockExcelItem getMonthItem() {
		return monthItem;
	}

	public void setMonthItem(StockExcelItem monthItem) {
		this.monthItem = monthItem;
	}

	public StockExcelStatItem getStatItem() {
		return statItem;
	}

	public void setStatItem(StockExcelStatItem statItem) {
		this.statItem = statItem;
	}

	
	
	//按幅度排序
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
	
		StockExcelTotalInfo setInfo = (StockExcelTotalInfo)o;
		
		if(this.getWeekItem()== null ||setInfo.getWeekItem()== null )
			return 0;
		
		
		float retRange = this.getWeekItem().getScValue().getCurRange() - setInfo.getWeekItem().getScValue().getCurRange();
		
		 if (retRange > 0)
			 return 1;
		 else if(retRange < 0)
			 return -1;
		 else 
			 return 0;
		
	}
	
	/*
	//按时间与幅度双重排序
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
	
		StockExcelTotalInfo setInfo = (StockExcelTotalInfo)o;
		
		if(this.getWeekItem()== null ||setInfo.getWeekItem()== null )
			return 0;
		
		int retDate = 0;
		try {
			retDate = stockDateTimer.compareDate(this.getWeekItem().getScValue().getStartDate(),setInfo.getWeekItem().getScValue().getStartDate());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		float retRange = this.getWeekItem().getScValue().getCurRange() - setInfo.getWeekItem().getScValue().getCurRange();
		
		if(retDate > 0)
		{
			return 1;//时间靠前
		} else if (retDate == 0) { 
			 if (retRange > 0)
				 return 1;
			 else if(retRange < 0)
				 return -1;
			 else 
				 return 1;
		} else {
			return -1; //时间靠后
		}
	
	}
	*/
	

}
