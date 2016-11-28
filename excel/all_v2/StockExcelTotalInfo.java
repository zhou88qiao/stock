package excel.all_v2;

import java.text.ParseException;

import date.timer.stockDateTimer;

//public class StockExcelTotalInfo implements Comparable<StockExcelTotalInfo>{
//�����Ƿ�������
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

	
	/*
	//����������  //
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
	
		StockExcelTotalInfo setInfo = (StockExcelTotalInfo)o;
		
		if(this.getWeekItem()== null ||setInfo.getWeekItem()== null )
			return 0;
		
		
		float retRange = this.getWeekItem().getScValue().getPointSuspectedValueGap() - setInfo.getWeekItem().getScValue().getPointSuspectedValueGap();
		
		 if (retRange > 0)
			 return 1;
		 else if(retRange < 0)
			 return -1;
		 else 
			 return 0;
		
	}
	*/
	//���������  //
	@Override

	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		int thisState=0;
		int otherState=0;
		
		StockExcelTotalInfo setInfo = (StockExcelTotalInfo)o;
		
		if(this.getDayItem()== null ||setInfo.getDayItem()== null )
			return -1;
		
		//���״̬
		thisState= Integer.parseInt(this.getStatItem().getDayMixStatItem().getComPSState());
		otherState = Integer.parseInt(setInfo.getStatItem().getDayMixStatItem().getComPSState());
		
		int retRange = thisState-otherState;
		 if (retRange > 0) 
			 return 1;
		 else if(retRange < 0)
			 return -1;
		 else 
			 return 0;
		
	}


}
