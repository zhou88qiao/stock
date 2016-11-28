package test;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class tablemode {
	
	private JFrame mainWin=new JFrame("�����ݿ�");
	
	final int COLUMN_COUNT=8;
	DefaultTableModel model;
	JTable table;
	ArrayList<TableColumn> hiddenColumns=new ArrayList<TableColumn>();
	
	public void init()
	{
		model=new DefaultTableModel(COLUMN_COUNT,COLUMN_COUNT);
		for(int i=0;i<COLUMN_COUNT;i++)
		{
			for(int j=0;j<COLUMN_COUNT;j++)
				model.setValueAt("�ϵ�Ԫ��"+i+""+j, i,j);
		}
		
		table=new JTable(model);
		mainWin.add(new JScrollPane(table),BorderLayout.CENTER);
		JMenuBar menuBar=new JMenuBar();
		mainWin.setJMenuBar(menuBar);
		JMenu tableMenu=new JMenu("����");
		menuBar.add(tableMenu);
		JMenuItem hideColumnsItem=new JMenuItem("����ѡ����");
		hideColumnsItem.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent event) {
				// TODO Auto-generated method stub
				int selected[]=table.getSelectedColumns();
				TableColumnModel columnModel=table.getColumnModel();
				for(int i=selected.length-1;i>=0;i--)
				{
					TableColumn column=columnModel.getColumn(selected[i]);
					table.removeColumn(column);
					hiddenColumns.add(column);
				}
			}
			
		});
		
		tableMenu.add(hideColumnsItem);
		
		JMenuItem showColumnsItem=new JMenuItem("��ʾ������");
		showColumnsItem.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent event) {
				// TODO Auto-generated method stub
				for(TableColumn tc:hiddenColumns)
				{
					table.addColumn(tc);
				}
				hiddenColumns.clear();
				
			}
			
		});
		tableMenu.add(showColumnsItem);
		
		JMenuItem addColumnsItem=new JMenuItem("����ѡ����");
		addColumnsItem.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent event) {
				int selected[]=table.getSelectedColumns();
				TableColumnModel columnModel=table.getColumnModel();
				for(int i=selected.length-1;i>=0;i--)
				{
					TableColumn column=columnModel.getColumn(selected[i]);
					table.addColumn(column);
				}
				
			}
			
		});
		tableMenu.add(addColumnsItem);
		
		JMenuItem addRowItem=new JMenuItem("������");
		addRowItem.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent event) {
				String [] newCells=new String[COLUMN_COUNT];
				for(int i=0;i<newCells.length;i++)
				{
					newCells[i]="�µ�Ԫ��"+model.getRowCount()+""+i;
				}
				model.addRow(newCells);
			}
			
		});
		tableMenu.add(addRowItem);
		
		JMenuItem removeRowItem=new JMenuItem("ɾ��ѡ����");
		addRowItem.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent event) {
				int selected[]=table.getSelectedColumns();
			
				for(int i=selected.length-1;i>=0;i--)
				{
					
					model.removeRow(selected[i]);
				}
				
			}
			
		});
		tableMenu.add(removeRowItem);
		
		
		mainWin.pack();
		mainWin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
		mainWin.setVisible(true);
		
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		tablemode tm=new tablemode();
		tm.init();

	}

}
