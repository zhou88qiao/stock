package mybatis;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import dao.StockSingle;

public interface StockSingleMapper {
	
	 /** 
     * ��õ����û� 
     * @param userid 
     * @return 
     */  
    public List<StockSingle> getAllStockSingle();  
    /** 
     * ���һ���û����� 
     * @param userids 
     * @return 
     */  
    public List<String> getStockSingleAllFullId();  
    /** 
     * ɾ��һ���û� 
     * @param userid 
     */  
   // public void deleteUserInfo(String userid);  
    /** 
     * ���һ���û� 
     * @param userinfo 
     */  
  //  public void addUserInfo(StockSingle userinfo);  
    /** 
     * �޸��û� 
     * @param userinfo 
     * @return 
     */  
 //   public void updateUserInfo(StockSingle userinfo);  
    /** 
     * ��ҳ��ѯ 
     * @param currpage 
     * @param pageSize 
     * @param conds 
     * @return 
     */  
   // public List<StockSingle> getUserInfoPage(int currpage,int pageSize,Map<String,Object> conds);  
    
}
