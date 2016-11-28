package stockGUI;

import getStockData.GetMA;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;

import javax.swing.JFrame;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;

import com.point.stock.PointClass;
import com.timer.stock.DateStock;

import common.ConstantsInfo;

import stockGUI.stocktable.StockTableModel;
import stockGUI.stocktableheader.DefaultGroup;
import stockGUI.stocktableheader.GroupableTableHeader;
import stockGUI.stocktree.StockObjectType;
import stockGUI.stocktree.StockTreeNodeData;
import stockGUI.stocktree.StockTreeCellRenderer;

import dao.DayStock;
import dao.DayStockDao;
import dao.StockDataDao;
import dao.StockInformation;
import dao.StockInformationDao;
import dao.StockPoint;

public class stockDialog {

	static StockInformationDao sid =new StockInformationDao();
	static DayStockDao dsd=new DayStockDao();
	static StockDataDao sdDao=new StockDataDao();
	static GetMA gm=new GetMA();
	static PointClass pClass=new PointClass();
	static List<StockInformation> listStockInfo = new ArrayList<StockInformation>(); 
	List<String> listIndustry = new ArrayList<String>(); 
	List<String> listConcept = new ArrayList<String>(); 	
	List<String> listRegional = new ArrayList<String>(); 
	List<String> listStockIndustry = new ArrayList<String>(); 
	List<String> listStockConcept = new ArrayList<String>(); 	
	List<String> listStockRegional = new ArrayList<String>(); 
	List<String> listStockOfYear = new ArrayList<String>(); 
	
	private static final int COLUMN_COUNT = 8;
	private JFrame jf=new JFrame("��Ʊ");
	JTree stockTree;
	
	DefaultMutableTreeNode root=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.ROOT,"���ǻ�"));
	DefaultMutableTreeNode hs=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.MARKET,"�������"));
	DefaultMutableTreeNode industry=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.PLATE,"��ҵ���"));	
	DefaultMutableTreeNode concept=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.PLATE,"������"));
	DefaultMutableTreeNode regional=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.PLATE,"������"));
	DefaultMutableTreeNode market=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.PLATE,"����"));
	DefaultMutableTreeNode fund=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.MARKET,"����"));
	DefaultMutableTreeNode openFund=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.PLATE,"����ʽ����"));
	DefaultMutableTreeNode closeFund=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.PLATE,"���ʽ����"));

	DefaultTableModel model;
	JTable stockTable;
	StockTableModel stTabMod;
	JScrollPane stockSP;
	JScrollPane stockTreeSP;
	GroupableTableHeader tableHeader;
		
	List<StockPoint> stockExtremeData=new ArrayList<StockPoint>();	
	JTextArea eventTxt=new JTextArea(5,20); 
	boolean flag=true;
	
	public void init() throws IOException, ClassNotFoundException, SQLException
	{
		
		Iterator it,ie;
		/*
		listStockOfYear=dsd.getStockOfYear("sh600000");
		 for(it = listStockOfYear.iterator();it.hasNext();)
		 {
			 String stockYear = (String) it.next();			
			System.out.println(stockYear);
		 }
		 */
		listIndustry=sid.getAllIndustry(listStockInfo);		
		
		 for(it = listIndustry.iterator();it.hasNext();)
		 {
			String stockIndu = (String) it.next();			
			//System.out.println(stockIndu);
			DefaultMutableTreeNode stIndustry=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.CLASS,stockIndu));
			industry.add(stIndustry);
			if(stockIndu.equals("����"))
				market.add(stIndustry);
			listStockIndustry=sid.getStockIndustry(stockIndu);
			for(ie=listStockIndustry.iterator();ie.hasNext();)
			{
				String stockInduFullID = (String) ie.next();		
				DefaultMutableTreeNode stStockIndustry=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.STOCK,stockInduFullID));
				stIndustry.add(stStockIndustry);
			}
		 }
		 
		 listConcept=sid.getAllConcept(listStockInfo);
		 for(it = listConcept.iterator();it.hasNext();)
		 {
			String stockCon = (String) it.next();	
			//System.out.println(stockCon);
			DefaultMutableTreeNode stConcept=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.CLASS,stockCon));
			
			concept.add(stConcept);
			listStockConcept=sid.getStockConcept(stockCon);
			for(ie=listStockConcept.iterator();ie.hasNext();)
			{
				String stockConFullID = (String) ie.next();		
				DefaultMutableTreeNode stStockConcept=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.STOCK,stockConFullID));
				stConcept.add(stStockConcept);
			}
		 }
		 
		 listRegional=sid.getAllRegional(listStockInfo);
		 for(it = listRegional.iterator();it.hasNext();)
		 {
			String stockReg = (String) it.next();			
			//System.out.println(stockReg);
			DefaultMutableTreeNode stRegional=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.CLASS,stockReg));
			regional.add(stRegional);
			listStockRegional=sid.getStockRegional(stockReg);
			for(ie=listStockRegional.iterator();ie.hasNext();)
			{
				String stockRegFullID = (String) ie.next();		
				DefaultMutableTreeNode stStockRegional=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.STOCK,stockRegFullID));
				stRegional.add(stStockRegional);
			}
		 }
		
		hs.add(market);
		hs.add(industry);		
		hs.add(concept);
		hs.add(regional);
		fund.add(openFund);
		fund.add(closeFund);
		root.add(hs);
		root.add(fund);
		stockTree=new JTree(root);	
		
		stockTree.setCellRenderer(new StockTreeCellRenderer());
		
		stockTree.setShowsRootHandles(true);
		//tree.putClientProperty("JTree.lineStyle", "none");
		stockTree.setRootVisible(true);
		//stockTree.setSize(100, 400);
		StockTableListener stlistner=new StockTableListener();
		stockTree.addTreeSelectionListener(stlistner);
		
		
		tableHeader = new GroupableTableHeader();	
		addTableHeader();

		stockTreeSP=new JScrollPane(stockTree);
		jf.add(stockTreeSP,BorderLayout.WEST);
		stockTreeSP.setSize(200, 400);
	//	stockSP=new JScrollPane(table);
	//	jf.add(stockSP,BorderLayout.CENTER);
		jf.add(new JScrollPane(eventTxt),BorderLayout.SOUTH);
	
		jf.pack();
		jf.setExtendedState(JFrame.MAXIMIZED_BOTH);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
        jf.setVisible(true);
	}
	
	class StockTableListener implements TreeSelectionListener
	{
		public void valueChanged(TreeSelectionEvent e) {
			if(stockSP!=null)
			{
				jf.remove(stockSP);
			}				
			if(e.getOldLeadSelectionPath()!=null)
			{
				eventTxt.append("�½ڵ㣺"+e.getNewLeadSelectionPath().toString()+"\n");
				DefaultMutableTreeNode node=(DefaultMutableTreeNode) stockTree.getLastSelectedPathComponent(); //�������ѡ�еĽ��          
				String noteName=node.toString();//��������������
				System.out.println(noteName);
				try {
					if(noteName.contains("s"))//ѡȡ��sh sz��Ʊ������
					{	
						//stockDayData=dsd.getStockLatestWeekData(noteName);
						listStockOfYear=dsd.getStockOfYear(noteName);				
					//	System.out.println(listStockOfYear.size());
						List<String> listStockDays=new ArrayList<String>();
						List<String> listStockWeeks=new ArrayList<String>();
						List<String> listStockMonths=new ArrayList<String>();
						List<String> listStockSeasons=new ArrayList<String>();
						List<String> listStockYears=new ArrayList<String>();
		
						listStockDays=sdDao.getDates(noteName,ConstantsInfo.DayDataType);
						listStockWeeks=sdDao.getDates(noteName,ConstantsInfo.WeekDataType);
						listStockMonths=sdDao.getDates(noteName,ConstantsInfo.MonthDataType);
						listStockSeasons=sdDao.getDates(noteName,ConstantsInfo.SeasonDataType);
						listStockYears=sdDao.getDates(noteName,ConstantsInfo.YearDataType);
					//	listStockYears=sdDao.getDates("sh000001_copy",ConstantsInfo.YearDataType);
						DateStock dStock=new DateStock(listStockDays,listStockWeeks,listStockMonths,listStockSeasons,listStockYears);
						
					
						List<StockPoint> stockDayPoint=new ArrayList<StockPoint>();
						List<StockPoint> stockWeekPoint=new ArrayList<StockPoint>();
						List<StockPoint> stockMonthPoint=new ArrayList<StockPoint>();
						
						try {
							stockDayPoint=pClass.getStockDayExtremePoint(noteName, dStock,ConstantsInfo.StockCalAllData);
							stockWeekPoint=pClass.getStockWeekExtremePoint(noteName, dStock,ConstantsInfo.StockCalAllData);
							stockMonthPoint=pClass.getStockMonthExtremePoint(noteName, dStock,ConstantsInfo.StockCalAllData);
						} catch (SecurityException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (InstantiationException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IllegalAccessException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (NoSuchFieldException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						System.out.println("day size:"+stockDayPoint.size());
						System.out.println("week size:"+stockWeekPoint.size());
						System.out.println("month size:"+stockMonthPoint.size());
						
						stTabMod=new StockTableModel(stockDayPoint,stockWeekPoint,stockMonthPoint);
						//noteName
						
						stockTable=new JTable(stTabMod);	
						stockTable.setRowSelectionAllowed(false);
						stockTable.setCellSelectionEnabled(false);
						stockTable.setColumnSelectionAllowed(false);	
						
						stockTable.setTableHeader(tableHeader);//���ñ�ͷ��ǩ
		
						stockTable.setBorder(BorderFactory.createEtchedBorder());  
						stockSP=new JScrollPane(stockTable);
						jf.add(stockSP,BorderLayout.CENTER);
						jf.validate();
					}
				} catch (IOException e1) {			
					e1.printStackTrace();
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}	
		}
		
	}
	
	void addTableHeader()
	{
		DefaultGroup group = new DefaultGroup();
        group.setRow(0);
        group.setRowSpan(3);//
        group.setColumn(0);
        group.setColumnSpan(1);
        group.setHeaderValue("����"); //λ�ڵ�1��1�� ��ռ3��
        tableHeader.addGroup(group);
        
		group = new DefaultGroup();
        group.setRow(0);
        group.setColumn(1);
        group.setColumnSpan(6);
        group.setHeaderValue("����");//λ�ڵ�1�е�2�У�ռ6��
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(0);
        group.setColumn(7);
        group.setColumnSpan(5);
        group.setHeaderValue("������");//λ�ڵ�1�е�8�У�ռ5��
        tableHeader.addGroup(group);
      
        
        group = new DefaultGroup();
        group.setRow(1);
        group.setColumn(1);
        group.setRowSpan(2);
        group.setHeaderValue("ʱ��");//λ�ڵ�2�е�2�У�ռ2��
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(1);
        group.setColumn(2);
       group.setRowSpan(2);
        group.setHeaderValue("��λ");//λ�ڵ�2�е�3�У�ռ2��
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(1);
        group.setColumn(3);
        group.setColumnSpan(2);
        group.setHeaderValue("�����");//λ�ڵ�2�е�4�У�ռ2��
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(1);
        group.setColumn(5);
        group.setColumnSpan(2);
        group.setHeaderValue("ͳ��");//λ�ڵ�2�е�6�У�ռ2��
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(1);
        group.setColumn(7);
        group.setColumnSpan(2);
        group.setHeaderValue("����");//λ�ڵ�2�е�8�У�ռ2��
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(1);
        group.setColumn(9);
      //  group.setColumnSpan(2);
        group.setHeaderValue("����");//λ�ڵ�2�е�10�У�ռ1��
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(1);
        group.setColumn(10);
        group.setColumnSpan(2);
        group.setHeaderValue("��ҵ");//λ�ڵ�2�е�11�У�ռ2��
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(3);
        group.setHeaderValue("ǰ");//λ�ڵ�3�е�4��
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(4);
        group.setHeaderValue("��");//λ�ڵ�3�е�5��
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(5);
        group.setHeaderValue("����");//λ�ڵ�3�е�6��
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(6);
        group.setHeaderValue("ʱ��");//λ�ڵ�3�е�7��
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(7);
        group.setHeaderValue("ʡ");//λ�ڵ�3�е�8��
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(8);
        group.setHeaderValue("��");//λ�ڵ�3�е�9��
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(9);
        group.setHeaderValue("����");//λ�ڵ�3�е�10��
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(10);
        group.setHeaderValue("��ҵ");//λ�ڵ�3�е�11��
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(11);
        group.setHeaderValue("ϸ��");//λ�ڵ�3�е�12��
        tableHeader.addGroup(group);
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
		
		stockDialog sd=new stockDialog();
	
		listStockInfo=sid.getStockDaoList();
		
		sd.init();		
	}
}










