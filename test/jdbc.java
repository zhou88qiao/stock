package test;
import java.sql.*;


public class jdbc {
	
	

	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// 驱动程序名
		String driver = "com.mysql.jdbc.Driver";
		// URL指向要访问的数据库名
		String url = "jdbc:mysql://127.0.0.1:3306/stock";
		// MySQL配置时的用户名
		String user = "root";
		// MySQL配置时的密码
		String password = "mysql";
	
		/*
		try {
			// 加载驱动程序
			Class.forName(driver);
			// 连续数据库
			Connection conn = DriverManager.getConnection(url, user, password);
			if(!conn.isClosed())
			{
				System.out.println("Succeeded connecting to the Database!");
				// statement用来执行SQL语句
				Statement statement = conn.createStatement();
				// 要执行的SQL语句
				String sql = "select stock_id,stock_name from stock_base_information";
				// 结果集
				ResultSet rs = statement.executeQuery(sql);
				System.out.println("-----------------");
				System.out.println("执行结果如下所示:");
				System.out.println("-----------------");
				System.out.println(" stock_id" + "\t" + " 股票名");
				System.out.println("-----------------");
				String name = null;
				while(rs.next()) {
					// 选择username这列数据
					name = rs.getString("stock_name");
					// 首先使用ISO-8859-1字符集将name解码为字节序列并将结果存储新的字节数组中。
					// 然后使用GB2312字符集解码指定的字节数组
					name = new String(name.getBytes("ISO-8859-1"),"GB2312");
					// 输出结果
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
