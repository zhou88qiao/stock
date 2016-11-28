package dao;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;


public class DbConn {
	
	
//	private static String url = "jdbc:mysql://127.0.0.1:3306/stock";	
//	private static String username = "root";
//	private static String password = "mysql";
//	private static String driverName = "com.mysql.jdbc.Driver";
	
	 /** 
     * ���һ�����ݿ����� 
     *  
     * @return 
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
     */  
	
    public static Connection getConnDB(String confFile) throws IOException, ClassNotFoundException, SQLException { 
    	
    	Properties props=new Properties();
	//	FileInputStream in=new FileInputStream("conn.ini");
    	FileInputStream in=new FileInputStream(confFile);
		props.load(in);
		in.close();
		String driverName=props.getProperty("jdbc.drivers");
		String url=props.getProperty("jdbc.url");
		String username=props.getProperty("jdbc.username");
		String password=props.getProperty("jdbc.password");
    	
        Connection conn = null;  
        Class.forName(driverName);   
        conn = DriverManager.getConnection(url, username, password);  

        return conn;  
    } 
    
    
    public static Connection getConn() throws IOException, ClassNotFoundException, SQLException { 
    	
    	Properties props=new Properties();
	//	FileInputStream in=new FileInputStream("conn.ini");
    	FileInputStream in=new FileInputStream("StockConf/conn_data.ini");
		props.load(in);
		in.close();
		String driverName=props.getProperty("jdbc.drivers");
		String url=props.getProperty("jdbc.url");
		String username=props.getProperty("jdbc.username");
		String password=props.getProperty("jdbc.password");
    	
        Connection conn = null;  
        Class.forName(driverName);   
        conn = DriverManager.getConnection(url, username, password);  

        return conn;  
    } 
    
    
  
    /** 
     * �ر����ݿ�������Դ 
     *  
     * @param conn 
     * @param ps 
     * @param rs 
     * @throws SQLException 
     */  
    public static void closeConn(Connection conn, Statement ps, ResultSet rs) throws SQLException {  
        
        if (rs != null) {  
            rs.close();  
            rs = null;  
        }  
        if (ps != null) {  
            ps.close();  
            ps = null;  
        }  
        if (conn != null) {  
            conn.close();  
            conn = null;  
        }  
      
    }  
    
    public static void closeResult(Statement ps, ResultSet rs) throws SQLException {  
        
        if (rs != null) {  
            rs.close();  
            rs = null;  
        }  
        if (ps != null) {  
            ps.close();  
            ps = null;  
        }  
        
      
    } 

	
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// ����������
		String driver = "com.mysql.jdbc.Driver";
		// URLָ��Ҫ���ʵ����ݿ���
		String url = "jdbc:mysql://127.0.0.1:3306/stock";
		// MySQL����ʱ���û���
		String user = "root";
		// MySQL����ʱ������
		String password = "mysql";
	
		/*
		try {
			// ������������
			Class.forName(driver);
			// �������ݿ�
			Connection conn = DriverManager.getConnection(url, user, password);
			if(!conn.isClosed())
			{
				System.out.println("Succeeded connecting to the Database!");
				// statement����ִ��SQL���
				Statement statement = conn.createStatement();
				// Ҫִ�е�SQL���
				String sql = "select stock_id,stock_name from stock_base_information";
				// �����
				ResultSet rs = statement.executeQuery(sql);
				System.out.println("-----------------");
				System.out.println("ִ�н��������ʾ:");
				System.out.println("-----------------");
				System.out.println(" stock_id" + "\t" + " ��Ʊ��");
				System.out.println("-----------------");
				String name = null;
				while(rs.next()) {
					// ѡ��username��������
					name = rs.getString("stock_name");
					// ����ʹ��ISO-8859-1�ַ�����name����Ϊ�ֽ����в�������洢�µ��ֽ������С�
					// Ȼ��ʹ��GB2312�ַ�������ָ�����ֽ�����
					name = new String(name.getBytes("ISO-8859-1"),"GB2312");
					// ������
					System.out.println(rs.getString("stock_id") + "\t" + name);
				}
				rs.close();
				conn.close();
			}
			else
			{
				System.out.println("failed connecting to the Database!");
			}
			
			} catch(ClassNotFoundException e) {
			System.out.println("Sorry,can`t find the Driver!");
			e.printStackTrace();
			} catch(SQLException e) {
			e.printStackTrace();
			} catch(Exception e) {
			e.printStackTrace();
			}	
*/
		
		
	}
		

}
