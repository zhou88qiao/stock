package test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.awt.*;
import javax.swing.*;



public  class sqldata extends JFrame implements ActionListener{

	/**
	 * 
	 */
	
	JButton add,select,del,update;
	    JTable table;
	    Object body[][]=new Object[50][4];
	    String title[]={"���","����","����","����"};
	    Connection conn;
	    Statement stat;
	    ResultSet rs;
	    JTabbedPane tp;
	    public sqldata() {
	        super("���ݿ����");
	        this.setSize(400,300);
	        this.setLocation(300,200);
	        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        JPanel ps=new JPanel();
	        add=new JButton("���");
	        select=new JButton("��ʾ");
	        update=new JButton("����");
	        del=new JButton("ɾ��");
	        add.addActionListener(this);
	        select.addActionListener(this);
	        update.addActionListener(this);
	        del.addActionListener(this);
	        ps.add(add);ps.add(select);ps.add(update);ps.add(del);
	        table=new JTable(body,title);
	        tp=new JTabbedPane();
	        tp.add("bankAccount��",new JScrollPane(table));
	        this.getContentPane().add(tp,"Center");
	        this.getContentPane().add(ps,"South");
	        this.setVisible(true);
	       // this.connection();


	    }
	    public void connection(){
	    try {
	        Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver");
	        String url="jdbc:microsoft:sqlserver://127.0.0.1:1433;DatabaseName=dxaw";
	        conn=DriverManager.getConnection(url,"sa","");
	        stat = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
	        		ResultSet.CONCUR_READ_ONLY);
	    } catch (Exception ex) {
	    }
	    }
	    
	    public static void main(String[] args) {
	        sqldata data = new sqldata();
	      //  test te=new test();
	      //  te.talk();
	    }

	    public void actionPerformed1(ActionEvent e) {
	        if(e.getSource()==add)
	        {adddata();}
	        if(e.getSource()==select)
	        {select();}
	        if(e.getSource()==update)
	        {updata();}
	        if(e.getSource()==del)
	        {del();}
	    }


	    public void del() {
	    try {

	    int row=table.getSelectedRow();
	    stat.executeUpdate("delete bankAccount where accountID='"+body[row][0]+"'");
	    JOptionPane.showMessageDialog(null,"�����ѳɹ�ɾ��");
	       this.select();
	    } catch (SQLException ex) {
	        }
	    }

	    public void updata() {
	    try {
	            int row=table.getSelectedRow();

	            JTextField t[]=new JTextField[6];
	               t[0]=new JTextField("��������:");
	               t[0].setEditable(false);
	               t[1]=new JTextField();
	               t[2]=new JTextField("�������:");
	               t[2].setEditable(false);
	               t[3]=new JTextField();
	               t[4]=new JTextField("���뼶��:");
	               t[4].setEditable(false);
	               t[5]=new JTextField();
	               String but[]={"ȷ��","ȡ��"};
	               int go=JOptionPane.showOptionDialog(
	null,t,"������Ϣ",JOptionPane.YES_OPTION,
	JOptionPane.INFORMATION_MESSAGE,null,but,but[0]);
	   if(go==0){
	   //String ownerName=new String(t[1].getText().getBytes("ISO-8859-1"),"GBK");//���ݿ���Ϊ���ı���ȡ���˾�
	   String ownerName=new String(t[1].getText());//���ݿ���Ϊ���ı�����ô˾�
	   String accountValue=t[3].getText();
	   int accountLevel=Integer.parseInt(t[5].getText());
	   stat.executeUpdate("update bankAccount set ownerName='"
	   +ownerName+"',accountValue='"+accountValue+"',accountLevel='"

	   +accountLevel+"' where accountID='"+body[row][0]+"'");
	   JOptionPane.showMessageDialog(null,"�޸����ݳɹ�");
	    this.select();
	    }

	     } catch (Exception ex) {
	    }
	    }

	    public void select() {
	        try {
	            for(int x=0;x<body.length;x++){
	            body[x][0]=null;
	            body[x][1]=null;
	            body[x][2]=null;
	            body[x][3]=null;
	            }
	            int i=0;
	            rs=stat.executeQuery("select * from bankAccount");
	            while(rs.next()){
	            	/*
	            body[0]=rs.getInt(1);
	            body[1]=rs.getString(2);
	            body[2]=rs.getString(3);
	            body[3]=rs.getInt(4);*/
	            i=i+1;
	            }
	            this.repaint();
	        } catch (SQLException ex) {
	        } 

	    }

	    private void adddata() {

	        try {

	            JTextField t[]=new JTextField[6];
	            t[0]=new JTextField("��������:");
	            t[0].setEditable(false);
	            t[1]=new JTextField();
	            t[2]=new JTextField("�������:");
	            t[2].setEditable(false);
	            t[3]=new JTextField();
	            t[4]=new JTextField("���뼶��:");
	            t[4].setEditable(false);
	            t[5]=new JTextField();
	            String but[]={"ȷ��","ȡ��"};

	            int go=JOptionPane.showOptionDialog(null,t,"������Ϣ",
	            
	JOptionPane.YES_OPTION,JOptionPane.INFORMATION_MESSAGE,null,but,but[0]);
	            if(go==0){
	            
	try{
	            
	//String ownerName=new String(t[1].getText().getBytes("ISO-8859-1"),"GBK");//���ݿ���Ϊ���ı���ȡ���˾�
	            
	String ownerName=new String(t[1].getText());//���ݿ���Ϊ���ı�����ô˾�
	            
	String accountValue=t[3].getText();
	            
	int accountLevel=Integer.parseInt(t[5].getText());
	            
	stat.executeUpdate("insert into bankAccount(ownerName,accountValue,accountLevel) values('"
	            
	+ownerName+"','"+accountValue+"','"+accountLevel+"')");
	            
	JOptionPane.showMessageDialog(null,"�����ѳɹ����룡");
	            
	}catch(Exception ee){
	            
	JOptionPane.showMessageDialog(null,"�������ݴ���");
	            
	} 
	            }

	        }
	        catch (Exception ex) {}
	    }
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			
		}
	}
