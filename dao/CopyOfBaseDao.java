package dao;

import java.io.IOException;
import java.lang.reflect.Field;  
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import common.Test;
import common.stockLogger;

public class CopyOfBaseDao {
	
	 private Connection conn;  
	 private PreparedStatement ps = null;  
	 private ResultSet rs = null;  
//	 private DbConn dbconn;  
	 public CopyOfBaseDao()
	 {
		 
	 }
	 
	 public CopyOfBaseDao(Connection conn)
	 {
		 this.conn  = conn;
	 }
	    
	  //��ȡ��ѯ����
	    public List<String> getQuery(String sql,String parameters[]) throws SQLException{
	    	List list = new ArrayList();  
	        try{
	        	Connection	conn = DbConn.getConn();  
	        	PreparedStatement ps = conn.prepareStatement(sql);  
	            
	            if(parameters!=null&&!parameters.equals("")){
	                for(int i=0;i<parameters.length;i++){
	                    ps.setString(i+1,parameters[i]);
	                }                    
	            }    
	            ResultSet rs = ps.executeQuery();
	         // ��ȡ�� ResultSet ������еı�š����ͺ����ԡ�
	            ResultSetMetaData metaData = rs.getMetaData();
	            int col_len = metaData.getColumnCount();// ��ȡ�еĳ���
	            while(rs.next())
	            {
	            	 for (int i = 0; i < col_len; i++)
	                 {
	            	//	 rs.
	            		 String value=rs.getString(i+1);
	            		 /*
	                     String cols_name = metaData.getColumnName(i + 1);
	                     Object cols_value = rs.getObject(cols_name);
	                     if (cols_value == null)// �е�ֵû��ʱ��������ֵΪ����
	                     {
	                         cols_value = "";	                     
	                  	}
	                  	 System.out.println(+cols_name+":"+cols_name);
	                     */
	                 //    System.out.println(value);	 
	                     list.add(value);
	                 }
	            }
	        }catch (SQLException e) {
	            System.out.println("MySQL��������");
	            e.printStackTrace();
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	        	//DbConn.closeResult(ps,rs);
	        	DbConn.closeConn(conn, ps, rs);  
	        }

	        return list;
	    } 
	    
	  //��ȡ��ѯ����float
	    public List<Float> getFloatQuery(String sql,String parameters[]) throws SQLException{
	    	/////List list = new ArrayList(); /// 
	    	List<Float> list = new ArrayList<Float>();
	        try{
	        	Connection conn = DbConn.getConn();  
	        	PreparedStatement ps = conn.prepareStatement(sql);  
	            
	            if(parameters!=null&&!parameters.equals("")){
	                for(int i=0;i<parameters.length;i++){
	                    ps.setString(i+1,parameters[i]);
	                }                    
	            }    
	            ResultSet rs = ps.executeQuery();
	         // ��ȡ�� ResultSet ������еı�š����ͺ����ԡ�
	            ResultSetMetaData metaData = rs.getMetaData();
	            int col_len = metaData.getColumnCount();// ��ȡ�еĳ���
	            while(rs.next())
	            {
	            	 for (int i = 0; i < col_len; i++)
	                 {
	            		 float value=rs.getFloat(i+1);
	                     list.add(value);
	                 }
	            }
	        }catch (SQLException e) {
	            System.out.println("MySQL��������");
	            e.printStackTrace();
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	        	//DbConn.closeResult(ps,rs);
	        	DbConn.closeConn(conn, ps, rs);  
	        }

	        return list;
	    } 
	    
	    //��ȡ��ѯ����float
	    public List<Integer> getIntQuery(String sql,String parameters[]) throws SQLException{
	    	/////List list = new ArrayList(); /// 
	    	List<Integer> list = new ArrayList<Integer>();
	        try{
	        	Connection conn = DbConn.getConn();  
	        	PreparedStatement ps = conn.prepareStatement(sql);  
	            
	            if(parameters!=null&&!parameters.equals("")){
	                for(int i=0;i<parameters.length;i++){
	                    ps.setString(i+1,parameters[i]);
	                }                    
	            }    
	            ResultSet rs = ps.executeQuery();
	         // ��ȡ�� ResultSet ������еı�š����ͺ����ԡ�
	            ResultSetMetaData metaData = rs.getMetaData();
	            int col_len = metaData.getColumnCount();// ��ȡ�еĳ���
	            while(rs.next())
	            {
	            	 for (int i = 0; i < col_len; i++)
	                 {
	            		 int value=rs.getInt(i+1);
	                     list.add(value);
	                 }
	            }
	        }catch (SQLException e) {
	            System.out.println("MySQL��������");
	            e.printStackTrace();
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	        	//DbConn.closeResult(ps,rs);
	        	DbConn.closeConn(conn, ps, rs);  
	        }

	        return list;
	    } 
	    
	  //��ȡ���в�ѯ����
	    public String getSingleQuery(String sql,String parameters[]) throws SQLException{
	    	String value="";
	        try{
	        	Connection conn = DbConn.getConn();  
	        	PreparedStatement ps = conn.prepareStatement(sql);  
	            
	            if(parameters!=null&&!parameters.equals("")){
	                for(int i=0;i<parameters.length;i++){
	                    ps.setString(i+1,parameters[i]);
	                }                    
	            }    
	            ResultSet rs = ps.executeQuery();
	         // ��ȡ�� ResultSet ������еı�š����ͺ����ԡ�
	            ResultSetMetaData metaData = rs.getMetaData();
	            int col_len = metaData.getColumnCount();// ��ȡ�еĳ���
	           // System.out.println("col_len:"+col_len);
	            if(col_len!=1)
	            	return null;
	            while(rs.next())
	            {
	            	 for (int i = 0; i < col_len; i++)
	                 {
	            		  value=rs.getString(i+1);
	            		 /*
	                     String cols_name = metaData.getColumnName(i + 1);
	                     Object cols_value = rs.getObject(cols_name);
	                     if (cols_value == null)// �е�ֵû��ʱ��������ֵΪ����
	                     {
	                         cols_value = "";	                     
	                  	}
	                  	 System.out.println(+cols_name+":"+cols_name);
	                     */
	                 //    System.out.println(value);	 
	                     return value;
	                 }
	            }
	        }catch (SQLException e) {
	            System.out.println("MySQL��������");
	            e.printStackTrace();
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	        //	DbConn.closeResult(ps,rs);
	        	DbConn.closeConn(conn, ps, rs);  
	        }

	        return value;
	    } 
	    //��ȡ���в�ѯ����
	    public Date getSingleDateQuery(String sql,String parameters[]) throws SQLException{
	    	Date value = null;
	        try{
	        	Connection conn = DbConn.getConn();  
	        	PreparedStatement ps = conn.prepareStatement(sql);  
	            
	            if(parameters!=null&&!parameters.equals("")){
	                for(int i=0;i<parameters.length;i++){
	                    ps.setString(i+1,parameters[i]);
	                }                    
	            }    
	            rs = ps.executeQuery();
	         // ��ȡ�� ResultSet ������еı�š����ͺ����ԡ�
	            ResultSetMetaData metaData = rs.getMetaData();
	            int col_len = metaData.getColumnCount();// ��ȡ�еĳ���
	           // System.out.println("col_len:"+col_len);
	            if(col_len!=1)
	            	return null;
	            while(rs.next())
	            {
	            	 for (int i = 0; i < col_len; i++)
	                 {
	            		  value=rs.getDate(i+1);
	            		 /*
	                     String cols_name = metaData.getColumnName(i + 1);
	                     Object cols_value = rs.getObject(cols_name);
	                     if (cols_value == null)// �е�ֵû��ʱ��������ֵΪ����
	                     {
	                         cols_value = "";	                     
	                  	}
	                  	 System.out.println(+cols_name+":"+cols_name);
	                     */
	                 //    System.out.println(value);	 
	                     return value;
	                 }
	            }
	        }catch (SQLException e) {
	            System.out.println("MySQL��������");
	            e.printStackTrace();
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	        	//DbConn.closeResult(ps,rs);
	        	DbConn.closeConn(conn, ps, rs);  
	        }

	        return value;
	    } 
	    //��ȡ����float��ѯ����
	    public float getSingleFloatQuery(String sql,String parameters[]) throws SQLException{
	    	float value=0.0f;
	        try{
	        	Connection conn = DbConn.getConn();  
	        	PreparedStatement ps = conn.prepareStatement(sql);  
	            
	            if(parameters!=null&&!parameters.equals("")){
	                for(int i=0;i<parameters.length;i++){
	                    ps.setString(i+1,parameters[i]);
	                }                    
	            }    
	            ResultSet rs = ps.executeQuery();
	         // ��ȡ�� ResultSet ������еı�š����ͺ����ԡ�
	            ResultSetMetaData metaData = rs.getMetaData();
	            int col_len = metaData.getColumnCount();// ��ȡ�еĳ���
	           // System.out.println("col_len:"+col_len);
	            if(col_len!=1)
	            	return 0;
	            while(rs.next())
	            {
	            	 for (int i = 0; i < col_len; i++)
	                 {
	            		  value=rs.getFloat(i+1);
	            		  	            		  
	            		 /*
	                     String cols_name = metaData.getColumnName(i + 1);
	                     Object cols_value = rs.getObject(cols_name);
	                     if (cols_value == null)// �е�ֵû��ʱ��������ֵΪ����
	                     {
	                         cols_value = "";	                     
	                  	}
	                  	 System.out.println(+cols_name+":"+cols_name);
	                     */
	                 //    System.out.println(value);	 
	                     return value;
	                 }
	            }
	        }catch (SQLException e) {
	            System.out.println("MySQL��������");
	            e.printStackTrace();
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	        	//DbConn.closeResult(ps,rs);
	        	DbConn.closeConn(conn, ps, rs);  
	        }

	        return value;
	    } 
	    
	    //��ȡ����int��ѯ����
	    public int getSingleIntQuery(String sql,String parameters[]) throws SQLException{
	    	int value=0;
	        try{
	        	Connection conn = DbConn.getConn(); 
	        	
	        	PreparedStatement ps = conn.prepareStatement(sql);  
	            
	            if(parameters!=null&&!parameters.equals("")){
	                for(int i=0;i<parameters.length;i++){
	                    ps.setString(i+1,parameters[i]);
	                }                    
	            }    
	            ResultSet rs = ps.executeQuery();
	         // ��ȡ�� ResultSet ������еı�š����ͺ����ԡ�
	            ResultSetMetaData metaData = rs.getMetaData();
	            int col_len = metaData.getColumnCount();// ��ȡ�еĳ���
	           // System.out.println("col_len:"+col_len);
	            if(col_len!=1)
	            	return 0;
	            while(rs.next())
	            {
	            	 for (int i = 0; i < col_len; i++)
	                 {
	            		  value=rs.getInt(i+1);
	            		 /*
	                     String cols_name = metaData.getColumnName(i + 1);
	                     Object cols_value = rs.getObject(cols_name);
	                     if (cols_value == null)// �е�ֵû��ʱ��������ֵΪ����
	                     {
	                         cols_value = "";	                     
	                  	}
	                  	 System.out.println(+cols_name+":"+cols_name);
	                     */
	                 //    System.out.println(value);	 
	                     return value;
	                 }
	            }
	        }catch (SQLException e) {
	            System.out.println("MySQL��������");
	            e.printStackTrace();
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	        	//DbConn.closeResult(ps,rs);
	        	 DbConn.closeConn(conn, ps, rs);  
	        }

	        return value;
	    } 
	    
	    /** 
	     * ��ѯ���������ļ�¼�� 
	     *  
	     * @param sql 
	     *            Ҫִ�е�sql��� 
	     * @param args 
	     *            ��sql����еģ���ֵ�Ĳ����б� 
	     * @return ���������ļ�¼�� 
	     * @throws SQLException 
	     * @throws ClassNotFoundException 
	     * @throws IOException 
	     */  
	    public long getCount(String sql, Object... args) throws IOException, ClassNotFoundException, SQLException {  
	      Connection conn = DbConn.getConn();  
	        try {  
	        	PreparedStatement ps = conn.prepareStatement(sql);  
	            for (int i = 0; i < args.length; i++) {  
	                ps.setObject(i + 1, args[i]);  
	            }  
	            ResultSet rs = ps.executeQuery();  
	            if (rs.next()) {  
	                return rs.getLong(1);  
	            }  
	        } catch (SQLException e) {  
	            e.printStackTrace();  
	        } finally {  
	        	
	        	//DbConn.closeResult(ps,rs);	  
	        	DbConn.closeConn(conn, ps, rs); 
	        }  
	        return 0L;  
	    }  
	  
	    /** 
	     * ��ѯʵ�����ģ�����װ��һ������ 
	     *  
	     * @param <T> 
	     *            Ҫ��ѯ�Ķ���ļ��� 
	     * @param sql 
	     *            Ҫִ�е�sql��� 
	     * @param clazz 
	     *            Ҫ��ѯ�Ķ�������� 
	     * @param args 
	     *            ��sql����еģ���ֵ�Ĳ����б� 
	     * @return Ҫ��ѯ����ļ��ϣ��޽��ʱ����null 
	     * @throws SQLException 
	     * @throws ClassNotFoundException 
	     * @throws IOException 
	     */  
	    public <T> List<T> executeQuery(String sql, Class<T> clazz, Object... args) throws IOException, ClassNotFoundException, SQLException {  
	    	Connection conn = DbConn.getConn();         
	       	        
	        List list = new ArrayList();  
	        try {  
	        	PreparedStatement ps = conn.prepareStatement(sql);  
	            for (int i = 0; i < args.length; i++) {  
	                ps.setObject(i + 1, args[i]);  
	            }  
	            ResultSet rs = ps.executeQuery();  
	            Field[] fs = clazz.getDeclaredFields();  
	            String[] colNames = new String[fs.length];  
	            String[] rTypes = new String[fs.length];  
	            Method[] methods = clazz.getMethods();  
	            while (rs.next()) {  	            	 
	                for (int i = 0; i < fs.length; i++) {  
	                    Field f = fs[i];  
	                    String colName = f.getName().substring(0, 1).toUpperCase()  
	                            + f.getName().substring(1);  	                 
	                    colNames[i] = colName;  
	                    String rType = f.getType().getSimpleName();  
	                    rTypes[i] = rType;  
	                }  
	  
	                Object object = (T) clazz.newInstance();  
	                for (int i = 0; i < colNames.length; i++) {  
	                    String colName = colNames[i];  
	                    String methodName = "set" + colName;  
	                    // ���Ҳ����ö�Ӧ��setter������  
	                    for (Method m : methods) {  
	                        if (methodName.equals((m.getName()))) {  
	                            // ������˲�����ƥ���쳣�����JavaBean�и��������ͣ������else��֧���д���  
	                            if ("int".equals(rTypes[i])  
	                                    || "Integer".equals(rTypes[i])) {  
	                            	//  m.invoke(object, rs.getObject(colName));  
	                              int b=rs.getInt(colName);  	                           
	                              m.invoke(object, rs.getInt(colName));  
	                            } else if ("Date".equals(rTypes[i])) {  
	                                m.invoke(object, rs.getDate(colName));  
	                            } else if ("Timestamp".equals(rTypes[i])) {  
	                                m.invoke(object, rs.getTimestamp(colName));  
	                            } else {  	                            
	                                m.invoke(object, rs.getObject(colName));  
	                            }  
	                            break;  
	                        }  
	                    }  
	                }  
	               
	                list.add(object);  
	            } 
	            
	            return list;  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        } finally {  
	        	
	        	//DbConn.closeResult(ps,rs);
	            DbConn.closeConn(conn, ps, rs);  
	        }  
	        
	        return null;  
	    }  
	   
	    /*
	    public <T> T executeSingleQuery(String sql, Class<T>cls, List<Object> params) throws SQLException, InstantiationException, IllegalAccessException, SecurityException, NoSuchFieldException 
	    {		
    		T resultObject = null;		
    		int index = 1;		
    		ps = conn.prepareStatement(sql);  		
    	 	if(params != null && !params.isEmpty()){		
	    		for(int i = 0; i<params.size(); i++){			
	    			ps.setObject(index++, params.get(i));	
	    		}	
	    	}			
    		ResultSet rs = ps.executeQuery();	
    		ResultSetMetaData metaData  = rs.getMetaData();	
    		int cols_len = metaData.getColumnCount();		
    		while(rs.next())
    		{		
    			//ͨ��������ƴ���һ��ʵ��		
    			resultObject = (T) cls.newInstance();		
    			for(int i = 0; i<cols_len; i++){		
    				String cols_name = metaData.getColumnName(i+1);			
    				Object cols_value = rs.getObject(cols_name);		
    				if(cols_value == null){		
    					cols_value = "";			
    				}			
    				Field field = cls.getDeclaredField(cols_name);		
    				field.setAccessible(true); //��javabean�ķ���Ȩ��	
    				field.set(resultObject, cols_value);		
    			}		
    		}		
    		return resultObject;    	
    		
	    }
	    */
	  
	    /** 
	     * �Զ������ʽ��������һ��ʵ�� 
	     *  
	     * @param sql 
	     *            Ҫִ�е�sql��� 
	     * @param object 
	     *            Ҫ�������µ�ʵ����� 
	     * @param args 
	     *            ����Ҫ��ֵ���б���ɵ����飬����sql��� 
	     *            "insert into tbl_user values(seq_user.nextval,?,?,?)"ӦΪ1 
	     * @return ���������1 �ɹ���0 ʧ�� 
	     * @throws SQLException 
	     * @throws ClassNotFoundException 
	     * @throws IOException 
	     */  
	    public int saveEntity(String sql, Object object, int... args) throws SQLException, IOException, ClassNotFoundException {  
	    	Connection conn = DbConn.getConn();  
	        try {  
	        	PreparedStatement ps = conn.prepareStatement(sql);  
	            Class c = object.getClass();  
	            Field[] fields = object.getClass().getDeclaredFields();  
	            int temp = 1;// ����ֵ�ģ����±꣬����±�Ϊargs�ĳ���  
	            int colIndex = 1;// SQL����еĵ�ǰ�ֶ��±�  
	            int t = 0;// args������±�  
	            for (int j = 0; j < fields.length; j++) {  
	                Field field = fields[j];// �õ�ĳ����������  
	                String methodName = "get"  
	                        + field.getName().substring(0, 1).toUpperCase()  
	                        + field.getName().substring(1);  
	                Method method = c.getMethod(methodName);// �õ��˵�ǰ���е�һ��method  
	                String rType = field.getType().getSimpleName().toString();  
	                if (t < args.length && colIndex == args[t]) {  
	                    t++;  
	                } else if ("int".equals(rType) || "INTEGER".equals(rType)) {  
	                    ps.setInt(temp++, (Integer) method.invoke(object));  
	                } else {  
	                    ps.setObject(temp++, method.invoke(object));  
	                }  
	                colIndex++;// ���������±�  
	            }  
	            return ps.executeUpdate();  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        } finally {  
	        	//DbConn.closeResult(ps,rs);
	           DbConn.closeConn(conn, ps, null);  
	        }  
	        return 0;  
	    }  
	    
	    //����
	    public <T>T executeSingleQuery(String sql, Class<T> clazz, Object... args) throws IOException, ClassNotFoundException, SQLException {  
	    	Connection conn = DbConn.getConn(); 
	        T object = null;
	        try {  
	        	PreparedStatement ps = conn.prepareStatement(sql);  
	            for (int i = 0; i < args.length; i++) {  
	                ps.setObject(i + 1, args[i]);  
	            }  
	            ResultSet rs = ps.executeQuery();  
	            Field[] fs = clazz.getDeclaredFields();  
	            String[] colNames = new String[fs.length];  
	            String[] rTypes = new String[fs.length];  
	            Method[] methods = clazz.getMethods();  
	            while (rs.next()) {  
	                for (int i = 0; i < fs.length; i++) {  
	                    Field f = fs[i];  
	                    String colName = f.getName().substring(0, 1).toUpperCase()  
	                            + f.getName().substring(1);  	                 
	                    colNames[i] = colName;  
	                    String rType = f.getType().getSimpleName();  
	                    rTypes[i] = rType;  
	                }  
	  
	                object = (T) clazz.newInstance();  
	                for (int i = 0; i < colNames.length; i++) {  
	                    String colName = colNames[i];  
	                    String methodName = "set" + colName;  
	                    // ���Ҳ����ö�Ӧ��setter������  
	                    for (Method m : methods) {  
	                        if (methodName.equals((m.getName()))) {  
	                            // ������˲�����ƥ���쳣�����JavaBean�и��������ͣ������else��֧���д���  
	                            if ("int".equals(rTypes[i])  
	                                    || "Integer".equals(rTypes[i])) {  
	                            	//  m.invoke(object, rs.getObject(colName));  
	                              int b=rs.getInt(colName);  	                           
	                              m.invoke(object, rs.getInt(colName));  
	                            } else if ("Date".equals(rTypes[i])) {  
	                                m.invoke(object, rs.getDate(colName));  
	                            } else if ("Timestamp".equals(rTypes[i])) {  
	                                m.invoke(object, rs.getTimestamp(colName));  
	                            } else {  
	                            //	System.out.println(colName);
	                                m.invoke(object, rs.getObject(colName));  
	                            }  
	                            break;  
	                        }  
	                    }  
	                }  
	                 
	            }  
	            return object;  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        } finally {  
	        //	DbConn.closeResult(ps,rs);
	           DbConn.closeConn(conn, ps, rs);  
	        }  
	        return null;  
	    }  
	   
	  
	    /** 
	     * ִ�пɱ������SQL��䣬���б��桢ɾ������²��� 
	     *  
	     * @param sql 
	     *            Ҫִ�е�sql��䣬?�ĸ�ֵ˳�������args�����˳����ͬ 
	     * @param args 
	     *            Ҫ��ֵ�Ĳ����б� 
	     * @return ������������� �ɹ���0 ʧ�� 
	     * @throws SQLException 
	     * @throws ClassNotFoundException 
	     * @throws IOException 
	     */  
	    public int saveOrUpdate(String sql, Object... args) throws IOException, ClassNotFoundException, SQLException {  
	      Connection conn = DbConn.getConn();  
	        try {  
	        	PreparedStatement ps = conn.prepareStatement(sql);  
	            for (int j = 0; j < args.length; j++) {  
	                ps.setObject(j + 1, args[j]); 
	               // System.out.println(args[j]);
	            }  
	            return ps.executeUpdate();  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        } finally {  
	        	
	        	//DbConn.closeResult(ps,rs);
	            DbConn.closeConn(conn, ps, null);  
	        }  
	        return 0;  
	    }  

}
