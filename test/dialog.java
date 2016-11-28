package test;


import java.awt.BorderLayout;
import java.awt.BorderLayout;
import java.awt.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;


public class dialog {
	
	private JFrame jfStock=new JFrame("��Ʊ");

	JButton add,select,del,update;
    JTable table;
    
    Object body[][]=new Object[50][50];
  // String title[] = {"��Ʊ����","���տ��̼� ","�������̼�","��ǰ�۸�","������߼�","������ͼ�","�����","������","�ɽ��Ĺ�Ʊ��","�ɽ����(Ԫ)","��һ��","��һ��","�����","�����","������","������","������","���ļ�","������","�����","��һ��","��һ��","������","������","������","������","������","���ļ�","������","�����","����","ʱ��"} ;  
    String title[] = {"��Ʊ����","���տ��̼� ","�������̼�","��ǰ�۸�","����","ʱ��"} ;  
   
    JTabbedPane tp;
    JPanel ps;
    JMenuBar menuBar=new JMenuBar();
    JMenu priceMenu,analyseMenu,functionMenu,helpMenu;

    JScrollPane scrollPane;
 //  ResultSetTableModel model;
 //   JComboBox<String> tablename=new JComboBox<>();
	public void init() {
   
		ps=new JPanel();
        add=new JButton("���");
        select=new JButton("��ʾ");
        update=new JButton("����");
        del=new JButton("ɾ��");  
               
   
        ps.add(add);
        ps.add(select);
        ps.add(update);
        ps.add(del);
        
        DataOperationAction doAc=new DataOperationAction();
        add.addActionListener(doAc);
        select.addActionListener(doAc);
        update.addActionListener(doAc);
        del.addActionListener(doAc);
        
        priceMenu=new JMenu();
        analyseMenu=new JMenu();
        functionMenu=new JMenu();
        helpMenu=new JMenu();
        menuBar.add(priceMenu);
        menuBar.add(analyseMenu);
        menuBar.add(functionMenu);
        menuBar.add(helpMenu);
        
        table=new JTable(body,title);
        table.setPreferredScrollableViewportSize(new Dimension(500,40));
        table.setPreferredScrollableViewportSize(new Dimension(700,100));
        table.setPreferredScrollableViewportSize(new Dimension(900,300));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        tp=new JTabbedPane();
        tp.add("��Ʊ��Ϣ",new JScrollPane(table));
        
       // jfStock.add(menuBar);
        jfStock.setJMenuBar(menuBar);
      //  jfStock.add(ps);
     //   jfStock.add(tp);
       jfStock.add(ps,BorderLayout.SOUTH);
        jfStock.add(tp,BorderLayout.NORTH);
        
        
        jfStock.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jfStock.pack();
        jfStock.setVisible(true);
    }
	
	private class DataOperationAction implements ActionListener
	{
		public void actionPerformed(ActionEvent e) {
			if(e.getSource()==add)
			{
				add();
			}
			if(e.getSource()==select)
			{
				select();
			}
			if(e.getSource()==update)
			{
				update();
			}
			if(e.getSource()==del)
			{
				del();
			}
		}
	}
	  
	  public void del() {
		   
	  }
	  
	  public void update() {

	  }
	  
	  public void select() {
	  
	  }
	  public void add() {
		  
	  }
	  
	  public static void main(String[] args) {
			
			dialog dl=new dialog();
			dl.init();
			
		}
	
}
