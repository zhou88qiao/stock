package test;
import java.sql.*;


public class jdbc {
	
	

	

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
