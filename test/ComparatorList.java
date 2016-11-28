package test;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import excel.all_v2.StockExcelTotalInfo;

public class ComparatorList {

	public int compare(Object value1, Object value2) {
		// http://j2ee-yohn.javaeye.com/blog/272006 此帖子关于中文拼音排序很详细。
		// TODO Auto-generated method stub
		/*if (value1.getClass().getName().equals(
				"org.openjweb.core.util.CodeNameBean")) {
			String s1 = ((CodeNameBean) value1).getName().toString();
			String s2 = ((CodeNameBean) value2).getName().toString();
			return Collator.getInstance(Locale.CHINESE).compare(s1, s2);
		} else
			*/
			
		if (value1.getClass().getName().equals("java.lang.String")) {
			String s1 = value1.toString();
			String s2 = value2.toString();
			return Collator.getInstance(Locale.CHINESE).compare(s1, s2);
		}
		return 0; // 0表示相同。
	}

	public static List sort(List strList) {
		ComparatorList comp = new ComparatorList();
		Collections.sort(strList);
		return strList; // 返回排序后的列表
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		List<String> list = new ArrayList<String>(); 
		list.add("沈阳平安");
		list.add("中国人");
		list.add("启明星辰");
		
		//list = ComparatorList.sort(list);
		Collections.sort(list,Collator.getInstance(java.util.Locale.CHINA));
		
		for(int i =0 ;i<list.size();i++)
			System.out.println(list.get(i));
		
		

	}

}
