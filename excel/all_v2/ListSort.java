package excel.all_v2;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ListSort <E>{
	

	    /** 
	     *  
	     * @param list Ҫ����ļ��� 
	     * @param method Ҫ�����ʵ�����������Ӧ��get���� 
	     * @param sort desc Ϊ����   
	     */  
	    public void Sort(List<E> list, final String method, final String sort) {  
	        // ���ڲ���ʵ������  
	        Collections.sort(list, new Comparator<E>() {  
	  
	            public int compare(E a, E b) {  
	                int ret = 0;  
	                try {  
	                    // ��ȡm1�ķ�����  
	                    Method m1 = a.getClass().getMethod(method, null);  
	                    // ��ȡm2�ķ�����  
	                    Method m2 = b.getClass().getMethod(method, null);  
	                      
	                    if (sort != null && "desc".equals(sort)) {  
	  
	                        ret = m2.invoke(((E)b), null).toString().compareTo(m1.invoke(((E)a),null).toString());  
	  
	                    } else {  
	                        // ��������  
	                        ret = m1.invoke(((E)a), null).toString().compareTo(m2.invoke(((E)b), null).toString());  
	                    }  
	                } catch (NoSuchMethodException ne) {  
	                    System.out.println(ne);  
	                } catch (IllegalArgumentException e) {  
	                    // TODO Auto-generated catch block  
	                    e.printStackTrace();  
	                } catch (IllegalAccessException e) {  
	                    // TODO Auto-generated catch block  
	                    e.printStackTrace();  
	                } catch (InvocationTargetException e) {  
	                    // TODO Auto-generated catch block  
	                    e.printStackTrace();  
	                }  
	                return ret;  
	            }  
	        });  
	    }  
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
