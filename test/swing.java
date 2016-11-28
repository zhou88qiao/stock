package test;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;


public class swing {
	private JFrame jf=new JFrame("��Ʊ���ݿ�");
	JScrollPane scrollPane =new JScrollPane();
	String[] petStrings = {"" };
	ResultSetTableModel model;
	JComboBox tableNames=new JComboBox(petStrings);
	JTextArea changeMsg=new JTextArea(4,80);
	ResultSet rs;
	Connection conn;
	Statement stmt;
	
	public void init() throws IOException, ClassNotFoundException, SQLException
	{
		//jf.setLayout(new GridLayout(2,4));
	//	Border bb=BorderFactory.createBevelBorder();
		//Ϊjcombox����¼����û�ѡ��ĳ�����ݱ�
		tableNames.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event) {
				if(scrollPane!=null){
						jf.remove(scrollPane);
					}
				String tableName=(String) tableNames.getSelectedItem();
				if(rs!=null)
				{
					
					try {
						rs.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				String query="select * from "+tableName;
				
				System.out.println("query:"+query);
				
				try {
					rs=stmt.executeQuery(query);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				model =new ResultSetTableModel(rs);
				model.addTableModelListener(new TableModelListener()
				{
					
					@Override
					public void tableChanged(TableModelEvent evt) {
						// TODO Auto-generated method stub
						int row=evt.getFirstRow();
						int column=evt.getColumn();
						changeMsg.append("�޸ĵ��У�"+column+",�޸ĵ��У�"+row+"�޸ĺ��ֵ��"+model.getValueAt(row,column));
						System.out.println("�޸ĵ��У�"+column+",�޸ĵ��У�"+row+"�޸ĺ��ֵ��"+model.getValueAt(row,column));
						
					}
				});
				
				JTable table=new JTable(model);
				scrollPane=new JScrollPane(table);
				jf.add(scrollPane,BorderLayout.CENTER);
				jf.validate();
			}
			
		});
		
		
		JPanel p=new JPanel();
		p.add(tableNames);
		jf.add(p,BorderLayout.NORTH);
		jf.add(new JScrollPane(changeMsg),BorderLayout.SOUTH);

		conn=getConnection();
		DatabaseMetaData meta=conn.getMetaData();
		stmt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
		ResultSet tables=meta.getTables(null,null,null,new String[]{"TABLE"});
		while(tables.next())
		{
			tableNames.addItem(tables.getString(3));
		}
		tables.close();
		
        jf.pack();
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
        jf.setVisible(true);
		
		jf.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent event)
			{
				if(conn!=null)
					try {
						conn.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		});
	}
	
	private static Connection getConnection() throws IOException, ClassNotFoundException, SQLException
	{
		Properties props=new Properties();
		FileInputStream in=new FileInputStream("conn.ini");
		props.load(in);
		in.close();
		String drivers=props.getProperty("jdbc.drivers");
		String url=props.getProperty("jdbc.url");
		String username=props.getProperty("jdbc.username");
		String password=props.getProperty("jdbc.password");
		System.out.println(drivers);
		System.out.println(url);
		System.out.println(username);
		System.out.println(password);
		Class.forName(drivers);
		return DriverManager.getConnection(url,username,password);
	}
	
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
		// TODO Auto-generated method stub
		swing as=new swing();
		as.init();
	}
	
	
	

}

class ResultSetTableModel extends AbstractTableModel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8600260604524808975L;
	private ResultSet rs;
	private ResultSetMetaData rsmd;
	
	public ResultSetTableModel(ResultSet aResultSet)
	{
		rs=aResultSet;
		try
		{
			rsmd=rs.getMetaData();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	//��������
	public String getColumnName(int c)
	{
		try
		{
			return rsmd.getColumnName(c+1);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			return "";
		}
	}
	//��ȡ����
	public int getColumnCount() {
		try
		{
			return rsmd.getColumnCount();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			return 0;
		}
		
	}

//��ȡ����
	public int getRowCount() {
		
		try
		{
			rs.last();
			return rs.getRow();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			return 0;
		}
	}

	//����ָ����Ԫ��ֵ
	public Object getValueAt(int r, int c) {
		
		try
		{
			rs.absolute(r+1);
			return rs.getObject(c+1);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	//��д��ÿ����Ԫ��ɱ༭
	public boolean isCellEditable(int rowIndex,int columnIndex)
	{
		return true;
	}

	//��д�������û��༭��Ԫ��
	public void setValueAt(Object aValue,int row,int column)
	{
		try
		{
			//�������λ����Ӧ����
			rs.absolute(row);
			//�޸ĵ�Ԫ���Ӧֵ
			rs.updateObject(column+1, aValue);
			//�ύ�޸�
			rs.updateRow();
			//������Ԫ����޸��¼�
			fireTableCellUpdated(row,column);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
}
