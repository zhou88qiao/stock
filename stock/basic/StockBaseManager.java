package stock.basic;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.PropertyConfigurator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;

import com.calculation.stock.CalculationStock;
import com.point.stock.PointClass;
import com.timer.stock.DateStock;
import common.ConstantsInfo;
import common.stockLogger;

import dao.DbConn;
import dao.StockBaseDao;
import dao.StockConcept;
import dao.StockDataDao;
import dao.StockIndustry;
import dao.StockSummaryDao;

import dao.StockMarket;

import dao.StockPointDao;
import dao.StockRegional;
import dao.StockSingle;

import excel.all_v2.StockExcelOperationMain;
import excel.all_v2.StockExcelPartitionMain;
import excel.all_v2.StockExcelReadMain;
import excel.rw.ExcelReader;
import excel.simple.StockRecentWriter;
import file.FileReader;

import stockGUI.StockTimeSeriesChart;
import stockGUI.stocktable.StockConceptTableModel;
import stockGUI.stocktable.StockIndustryTableModel;
import stockGUI.stocktable.StockMarketTableModel;
import stockGUI.stocktable.StockRegionalTableModel;
import stockGUI.stocktable.StockSingleTableModel;



public class StockBaseManager {
	private final static int  addIndex=0;
	private final static int  editIndex=1;
	private final static int  delIndex=2;
	private final static int  queryIndex=3;	
	
	static StockBaseManager stockBM;
	private StockDataDao sdDao;
	private StockPointDao spDao;
	private StockBaseDao sbDao;
	private StockSummaryDao ssDao;
	//static StockInformationDao siDao =new StockInformationDao();
	static PointClass pClass=new PointClass();
	 private static TrayIcon trayIcon = null;
	 static SystemTray tray = SystemTray.getSystemTray();
	
	private static JFrame jFrame=new JFrame("股票基本信息");
	String[] baseItems = new String[] { "市场", "地区", "行业", "概念","个股"};
	String[] operButtons = new String[] { "新建", "编辑", "删除","查询"}; //0 ,1,2,3
	char[] baseShortcuts = { 'M','R','I','C','S'};
    char[] editShortcuts = { 'Z','X','C','V' };
    Color[] colors = { Color.RED, Color.YELLOW, Color.BLUE, Color.GREEN, Color.DARK_GRAY};
	private JMenuBar mb=new JMenuBar();
	JMenu stockBase=new JMenu("股票信息");	
	JMenu stockFunction=new JMenu("股票核心功能");
	JMenu futuresFunction=new JMenu("期货核心功能");
	JMenu stockOperate=new JMenu("基础操作");	
	JMenu stockMessage=new JMenu("资讯");
	
	JMenuItem GetStockData=new JMenuItem("1 导入交易数据");
	JMenuItem ExtremeAnalyze=new JMenuItem("2 交易数据分析");
	JMenuItem LoadPointExcel=new JMenuItem("3 导出分析数据");
	JMenuItem LoadAnalyExcel=new JMenuItem("4 导入分析数据");
	JMenuItem LoadSummaryExcel=new JMenuItem("5 导出统计数据");
	JMenuItem LoadOperationAnalyse=new JMenuItem("6 买卖操作分析");
	JMenuItem LoadOperationExcel=new JMenuItem("7 导出操作分析数据");

	

	JMenuItem GetFuturesData=new JMenuItem("1 导入期货商品数据");
	JMenuItem FuturesExtremeAnalyze=new JMenuItem("2 交易数据分析");
	JMenuItem FuturesLoadPointExcel=new JMenuItem("3 导出分析数据");
	JMenuItem FuturesLoadAnalyExcel=new JMenuItem("4 导入分析数据");
	JMenuItem FuturesLoadSummaryExcel=new JMenuItem("5 导出统计数据");
	JMenuItem FuturesLoadOperationAnalyse=new JMenuItem("6 买卖操作分析");
	JMenuItem FuturesLoadOperationExcel=new JMenuItem("7 导出操作分析数据");
	
	JMenuItem LoadMarket=new JMenuItem("0 导入股票或期货商品市场");
	JMenuItem LoadIndustry=new JMenuItem("1 导入一二三级行业");
	JMenuItem LoadFirstIndustryConcept=new JMenuItem("2 导入一级行业对应概念");
	JMenuItem LoadThirdIndustrytoStock=new JMenuItem("3 导入三级行业对应个股数据");
	JMenuItem LoadConcepttoStock=new JMenuItem("4 导入概念对应个股数据");
	JMenuItem LoadStockTwoRong=new JMenuItem("5 导入个股融资融券");
	JMenuItem LoadStockBaseface=new JMenuItem("6 导入个股基本面");
	JMenuItem LoadStockToFuturesBaseface=new JMenuItem("7 导入期货商品对应个股数据");
	JMenuItem LoadStockBaseYearface=new JMenuItem("8 导入股票年份交易信息");
	

	JButton addButton=new JButton("新建");	//0
	JButton editButton=new JButton("编辑");//1
	JButton deleteButton=new JButton("删除");//2
	JButton queryButton=new JButton("查询");//3
	
	JPanel Panel=new JPanel();
	JLabel jlMarket=new JLabel("市场");
	JLabel jlConcept=new JLabel("概念");
	JLabel jlProvince=new JLabel("省名称");
	JLabel jlCity=new JLabel("市名称");
	JLabel jlFirstIndustry=new JLabel("一级行业名称");
	JLabel jlSecondIndustry=new JLabel("二级行业名称");
	JLabel jlThirdIndustry=new JLabel("三级行业名称");
	JLabel jlStockFullId=new JLabel("股票代码");
	JLabel jlStockName=new JLabel("股票名称");
	JLabel jlStockIndustry=new JLabel("三级行业");
	JLabel jlStockConcept=new JLabel("概念");
	JTextField jtfMarket=new JTextField(10);//
	JTextField jtfConcept=new JTextField(10);
	
	JTextField jtfFirstIndustry=new JTextField(10);
	JTextField jtfSecondIndustry=new JTextField(10);
	JTextField jtfThirdIndustry=new JTextField(10);
	
	JTextField jtfProvince=new JTextField(10);
	JTextField jtfCity=new JTextField(10);
	JTextField jtfArea=new JTextField(10);
	
	JTextField jtfFullId=new JTextField(10);
	JTextField jtfStockName=new JTextField(10);
	JTextField jtfStockIndustry=new JTextField(10);
	JTextField jtfStockConcept=new JTextField(10);
	
	menuActionListenter actionL=new menuActionListenter();
	buttonMouseListenter actionM=new buttonMouseListenter();
	
	StockMarketTableModel stMarketTabMod;
	StockConceptTableModel stConceptTabMod;
	StockIndustryTableModel stIndustyTabMod;
	StockRegionalTableModel stRegionalTabMod;
	StockSingleTableModel stSingleTabMod;
	JTable stockTable;
	JScrollPane stockSP;
	JPanel operationPane;
	JOptionPane jop;	
	List<StockMarket> listStockMarket = new ArrayList<StockMarket>(); 
	List<StockConcept> listStockConcept = new ArrayList<StockConcept>(); 
	List<StockIndustry> listStockIndustry = new ArrayList<StockIndustry>(); 
	List<StockRegional> listStockRegional = new ArrayList<StockRegional>(); 
	List<StockSingle> listStockSingle = new ArrayList<StockSingle>(); 
	
	FileReader fr;//导入数据
	
	
	public StockBaseManager(Connection stockBaseConn,Connection stockDataConn,Connection stockPointConn,Connection stockSummaryConn)
	{
		sbDao = new StockBaseDao(stockBaseConn);
		sdDao = new StockDataDao(stockDataConn);
		spDao = new StockPointDao(stockPointConn);
		ssDao = new StockSummaryDao(stockSummaryConn);
	}
	
	public void init()
	{
		int i=0;
		 for (i=0; i < baseItems.length; i++) {
         //    JMenuItem item = new JMenuItem(baseItems[i], baseShortcuts[i]);
			 JMenuItem item = new JMenuItem(baseItems[i]);
             item.setAccelerator(KeyStroke.getKeyStroke(baseShortcuts[i],
                    Toolkit.getDefaultToolkit( ).getMenuShortcutKeyMask( ), false));
             item.setActionCommand("" + i); //根据i 事件监听器获取
             item.addActionListener(actionL);
             stockBase.add(item);
         }
	
		stockOperate.addActionListener(actionL);
		
		
		addButton.addMouseListener(actionM);
		deleteButton.addMouseListener(actionM);
		editButton.addMouseListener(actionM);
		queryButton.addMouseListener(actionM);
		
		addButton.setActionCommand(""+addIndex);
		deleteButton.setActionCommand(""+delIndex);
		editButton.setActionCommand(""+editIndex);
		queryButton.setActionCommand("" + queryIndex);
	
		stockOperate.add(LoadMarket);
		stockOperate.add(LoadIndustry);
		stockOperate.add(LoadFirstIndustryConcept);
		stockOperate.add(LoadThirdIndustrytoStock);
		stockOperate.add(LoadConcepttoStock);
		stockOperate.add(LoadStockTwoRong);
		stockOperate.add(LoadStockBaseface);
		stockOperate.add(LoadStockToFuturesBaseface);
		stockOperate.add(LoadStockBaseYearface);
		
	
		
		GetStockData.setActionCommand("" + 6); //根据i 事件监听器获取
		ExtremeAnalyze.setActionCommand("" + 7); //根据i 事件监听器获取		
		LoadPointExcel.setActionCommand("" + 8); //根据i 事件监听器获取		
		LoadAnalyExcel.setActionCommand(""+20); //导入数据库
		LoadSummaryExcel.setActionCommand(""+21); //导出统计表
		LoadOperationAnalyse.setActionCommand(""+22); //操作分析
		LoadOperationExcel.setActionCommand(""+23); //导出操作分析表
		
	
		
		GetFuturesData.setActionCommand("" + 31); //根据i 事件监听器获取
		FuturesExtremeAnalyze.setActionCommand("" + 32); //根据i 事件监听器获取		
		FuturesLoadPointExcel.setActionCommand("" + 33); //根据i 事件监听器获取		
		FuturesLoadAnalyExcel.setActionCommand(""+34); //导入数据库
		FuturesLoadSummaryExcel.setActionCommand(""+35); //导出统计表
		FuturesLoadOperationAnalyse.setActionCommand(""+36); //操作分析
		FuturesLoadOperationExcel.setActionCommand(""+37); //导出操作分析表
		
		
		LoadIndustry.setActionCommand(""+9); //导入行业
		LoadFirstIndustryConcept.setActionCommand(""+10); // 导入一级行业对应的概念
		LoadThirdIndustrytoStock.setActionCommand(""+11); // 导入三级行业对应的股票
		LoadConcepttoStock.setActionCommand(""+12); // 概念对应股票
		LoadStockTwoRong.setActionCommand(""+13); // 两融
		LoadStockBaseface.setActionCommand(""+14); //基本面
		LoadMarket.setActionCommand(""+15);//导入 市场
		LoadStockToFuturesBaseface.setActionCommand(""+16);//导入期货对应个股票
		LoadStockBaseYearface.setActionCommand(""+17);//导入年份信息
		
		GetStockData.addActionListener(actionL);
		ExtremeAnalyze.addActionListener(actionL);
		LoadPointExcel.addActionListener(actionL);
		LoadSummaryExcel.addActionListener(actionL);
		LoadAnalyExcel.addActionListener(actionL);
		LoadOperationExcel.addActionListener(actionL);
		LoadOperationAnalyse.addActionListener(actionL);
				
		GetFuturesData.addActionListener(actionL);
		FuturesExtremeAnalyze.addActionListener(actionL);
		FuturesLoadPointExcel.addActionListener(actionL);
		FuturesLoadSummaryExcel.addActionListener(actionL);
		FuturesLoadAnalyExcel.addActionListener(actionL);
		FuturesLoadOperationExcel.addActionListener(actionL);
		FuturesLoadOperationAnalyse.addActionListener(actionL);
		
		LoadMarket.addActionListener(actionL);
		LoadIndustry.addActionListener(actionL);
		LoadFirstIndustryConcept.addActionListener(actionL);
		LoadThirdIndustrytoStock.addActionListener(actionL);
		LoadConcepttoStock.addActionListener(actionL);
		LoadStockTwoRong.addActionListener(actionL);
		LoadStockBaseface.addActionListener(actionL);
		LoadStockToFuturesBaseface.addActionListener(actionL);
		LoadStockBaseYearface.addActionListener(actionL);

		stockFunction.add(GetStockData);
		stockFunction.add(ExtremeAnalyze);		
		stockFunction.add(LoadPointExcel);
		stockFunction.add(LoadAnalyExcel);
		stockFunction.add(LoadSummaryExcel);
		stockFunction.add(LoadOperationAnalyse);
		stockFunction.add(LoadOperationExcel);				
		
	
		futuresFunction.add(GetFuturesData);
		futuresFunction.add(FuturesExtremeAnalyze);		
		futuresFunction.add(FuturesLoadPointExcel);
		futuresFunction.add(FuturesLoadAnalyExcel);
		futuresFunction.add(FuturesLoadSummaryExcel);	
		futuresFunction.add(FuturesLoadOperationAnalyse);
		futuresFunction.add(FuturesLoadOperationExcel);	
		

		mb.add(stockBase);		
		mb.add(stockFunction);
		mb.add(futuresFunction);
		mb.add(stockOperate);		
		mb.add(stockMessage);
		
	//	Panel.add(mb);
		
	//	jFrame.addMouseListener(mouseL);
		
		
		jFrame.setJMenuBar(mb);
		jFrame.pack();
		jFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
		jFrame.setVisible(true);
		
		jFrame.addWindowListener(new WindowAdapter() { // 窗口关闭事件
		     public void windowClosing(WindowEvent e) {
		      System.exit(0);
		     };
		     /*
		     public void windowIconified(WindowEvent e) { // 窗口最小化事件

		      jFrame.setVisible(false);
		      stockBM.miniTray();

		     }*/

		    });
		
	}
	
	
	
	class menuActionListenter implements ActionListener
	{
		int ret=0;
		ExcelReader excelReader=null;
		public void actionPerformed(ActionEvent e) {
			
			if(stockSP!=null)
			{
				jFrame.remove(stockSP);
			}	
			
			if(operationPane!=null)
			{
				jFrame.remove(operationPane);
			}
			
			if(jop!=null)
			{
				jFrame.remove(jop);
			}
			
			//复用按钮
			operationPane=new JPanel();
			operationPane.add(addButton);
			operationPane.add(deleteButton);
			operationPane.add(editButton);
		
			int index=Integer.parseInt(e.getActionCommand());
		//	System.out.println("菜单:"+index);
			switch(index)
			{
			case 0: //市场
				try {
						listStockMarket=sbDao.getStockMarket(ConstantsInfo.StockMarket);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ClassNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}		
				
				stMarketTabMod=new StockMarketTableModel(listStockMarket);
				stockTable=new JTable(stMarketTabMod);	
				
				operationPane.setName("market");
				operationPane.add(jlMarket);	
				operationPane.add(jtfMarket);
		
				break;
			case 1: //地区
				
				try {
						listStockRegional=sbDao.getStockRegional();
					} catch (IOException e3) {
						// TODO Auto-generated catch block
						e3.printStackTrace();
					} catch (ClassNotFoundException e3) {
						// TODO Auto-generated catch block
						e3.printStackTrace();
					} catch (SQLException e3) {
						// TODO Auto-generated catch block
						e3.printStackTrace();
					}
				stRegionalTabMod=new StockRegionalTableModel(listStockRegional);
				stockTable=new JTable(stRegionalTabMod);	
				
				
				operationPane.setName("regional");
				operationPane.add(jlProvince);	
				operationPane.add(jtfProvince);
				operationPane.add(jlCity);			
				operationPane.add(jtfCity);
				break;
			case 2: //行业
			
				try {
						listStockIndustry=sbDao.getStockIndustry();
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					} catch (ClassNotFoundException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					} catch (SQLException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					
				stIndustyTabMod=new StockIndustryTableModel(listStockIndustry);
				stockTable=new JTable(stIndustyTabMod);	
				
				operationPane.setName("industry");
				operationPane.add(jlFirstIndustry);	
				operationPane.add(jtfFirstIndustry);
				operationPane.add(jlSecondIndustry);	
				operationPane.add(jtfSecondIndustry);
				operationPane.add(jlThirdIndustry);	
				operationPane.add(jtfThirdIndustry);
				break;
			case 3: //概念
				try {
						listStockConcept=sbDao.getStockConcept();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ClassNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}		
			
				stConceptTabMod=new StockConceptTableModel(listStockConcept);
				stockTable=new JTable(stConceptTabMod);	
				
				operationPane.setName("concept");
				operationPane.add(jlConcept);	
				operationPane.add(jtfConcept);				
				break;
			case 4: //股票
				try {
						listStockSingle=sbDao.getStockSingle();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ClassNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				
				stSingleTabMod=new StockSingleTableModel(listStockSingle);
				stockTable=new JTable(stSingleTabMod);	
				
				operationPane.setName("stock");
				operationPane.add(jlStockFullId);	
				operationPane.add(jtfFullId);
				operationPane.add(jlStockName);
				operationPane.add(jtfStockName);
				operationPane.add(jlStockIndustry);
				operationPane.add(jtfStockIndustry);
				operationPane.add(jlStockConcept);				
				operationPane.add(jtfStockConcept);
				
				break;
		
			case 6://获取数据
			case 31:				
				Date startDate = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
		        String dateNowStr = sdf.format(startDate);  
				fr=new FileReader(sbDao,sdDao,spDao,ssDao);				
				//导入 部分日数据 并计算ma5 涨幅
				try {
						if(index == 6)
							ret=fr.loadAllDataInfile(dateNowStr);
						else 
							ret=fr.loadAllFuturesDataInfile(dateNowStr);
					//	ret=fr.deleteDataInfile(); //删除数据
					} catch (SecurityException e3) {
						// TODO Auto-generated catch block
						e3.printStackTrace();
					} catch (IOException e3) {
						// TODO Auto-generated catch block
						e3.printStackTrace();
					} catch (ClassNotFoundException e3) {
						// TODO Auto-generated catch block
						e3.printStackTrace();
					} catch (SQLException e3) {
						// TODO Auto-generated catch block
						e3.printStackTrace();
					} catch (InstantiationException e3) {
						// TODO Auto-generated catch block
						e3.printStackTrace();
					} catch (IllegalAccessException e3) {
						// TODO Auto-generated catch block
						e3.printStackTrace();
					} catch (NoSuchFieldException e3) {
						// TODO Auto-generated catch block
						e3.printStackTrace();
					}				
									
					jop=new JOptionPane();					
					jFrame.add(jop,BorderLayout.NORTH);
					if (ret == 0)
						jop.showMessageDialog(jFrame, "数据已经导入完成","提示", JOptionPane.INFORMATION_MESSAGE); 
					else
						jop.showMessageDialog(jFrame, "数据已经导入，不用重复","提示", JOptionPane.INFORMATION_MESSAGE); 
					return;
			case 7://分析当天数据
				analyseStockData(ConstantsInfo.StockMarket); 
				return;
			case 32://分析当天数据
				analyseStockData(ConstantsInfo.FuturesMarket); 
				return;
			case 8: //导出数据
				exportExcelFile(ConstantsInfo.StockMarket); 
				return;
			case 33: //导出数据
				exportExcelFile(ConstantsInfo.FuturesMarket); 
				return;
				
			case 20:
				loadSummaryExcelFile(ConstantsInfo.StockMarket);
				return;
			case 34:
				loadSummaryExcelFile(ConstantsInfo.FuturesMarket);
				return;
			case 21://导出统计数据
				exportSummaryExcelFile(ConstantsInfo.StockMarket);
				return;
			case 35:
				exportSummaryExcelFile(ConstantsInfo.FuturesMarket);
				return;
				
			case 22://分析操作数据
				analyseOperation(ConstantsInfo.StockMarket);
				return;
			case 36://分析操作数据
				analyseOperation(ConstantsInfo.FuturesMarket);
				return;
			case 23://导出操作数据
				exportOperationExcelFile(ConstantsInfo.StockMarket);
				return;	
			case 37://导出操作数据
				exportOperationExcelFile(ConstantsInfo.FuturesMarket);
				return;	
			case 9: 		
			case 10: 	
			case 11: 		
			case 12: 				
			case 13: 	
			case 14: 
			case 15: 	
			case 16:
			case 17:
				System.out.println(index);
				try {
						loadData(index);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ClassNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				return;
			default:
				break;
			
			}
			
			//增加操作pane
			operationPane.add(queryButton);			
			jFrame.add(operationPane,BorderLayout.NORTH);
			
			//增加表pane			
			stockTable.addMouseListener(new JtableMouseListenter());
			
		//	stockTable.setRowSelectionAllowed(false);
		//	stockTable.setCellSelectionEnabled(false);
		//	stockTable.setColumnSelectionAllowed(false);
			stockTable.setBorder(BorderFactory.createEtchedBorder());  
			stockTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			stockTable.setSelectionForeground(Color.blue);
			stockTable.setShowVerticalLines(false);//  
		     // 设置是否显示单元格间的分割线
			stockTable.setShowHorizontalLines(false);
			stockSP=new JScrollPane(stockTable);
			
			jFrame.add(stockSP,BorderLayout.CENTER);
			jFrame.validate();						
		}
		
	}
	
	
	//表格鼠标事件
	class JtableMouseListenter implements MouseListener
	{

		public void mouseClicked(MouseEvent e) {				
			
		  if(e.getClickCount() == 2) //实现双击 
          { 
			  	String paneNmae=operationPane.getName();
                int row =((JTable)e.getSource()).rowAtPoint(e.getPoint()); //获得行位置 
               // int col=((JTable)e.getSource()).columnAtPoint(e.getPoint()); //获得列位置 
                if(paneNmae.equals("concept"))
                {	
                	System.out.println("name:"+listStockConcept.get(row).getName());
                	
                }
            	else if(paneNmae.equals("industry"))
            	{
					System.out.println("operate industry");
            	}
				else if(paneNmae.equals("regional"))
					System.out.println("operate regional");
				else if(paneNmae.equals("market"))
				{
					System.out.println("operate market");
				}
				else if(paneNmae.equals("stock"))
				{
					System.out.println("operate stock");
					String fullId=listStockSingle.get(row).getStockFullId();		
					JFreeChart timeSeriesChart = null;
					StockTimeSeriesChart stsc=new StockTimeSeriesChart(sbDao,spDao);
					
					try {
						timeSeriesChart=stsc.createTimeSeriesChart(fullId);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ClassNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					ChartFrame cframe = new ChartFrame("StockChart", timeSeriesChart);
					 
					cframe.pack();
			        cframe.setVisible(true);						
								
				}

           }
			
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			
		}

	
		public void mouseExited(MouseEvent arg0) {
			
	
		}


		public void mousePressed(MouseEvent arg0) {
			
		}


		public void mouseReleased(MouseEvent arg0) {
			
	
		}
	
	}
	

	//按钮事件
	class buttonMouseListenter implements MouseListener
	{
		public void mousePressed(MouseEvent e) {
			//System.out.println("mouse:"+e.getSource());			
			
		}
		
		public void mouseEntered(MouseEvent e) {
			
			
		}

		
		public void mouseExited(MouseEvent e) {
			
			
		}

		
		public void mouseClicked(MouseEvent e) {
		
			
			String paneNmae=operationPane.getName();
			//System.out.println("pane name:"+operationPane.getName());
			
			if(jop!=null)
			{
				jFrame.remove(jop);
			}
			
			if(e.getSource()==addButton){
				
				
				addButton.setBackground(Color.BLUE);
				
				if(paneNmae.equals("concept"))
					System.out.println("operate concept");
            	else if(paneNmae.equals("industry"))
            	{
					System.out.println("operate industry");
            	}
				else if(paneNmae.equals("regional"))
					System.out.println("operate regional");
				else if(paneNmae.equals("market"))
				{
					JLabel jlCode=new JLabel("代码");
					JLabel jlName=new JLabel("名称");
					final JTextField jtfCode=new JTextField(6);
					final JTextField jtfName=new JTextField(24);
					JButton cancelButton=new JButton("取消");	
					JButton confirmButton=new JButton("确定");	
					
					final JFrame jfDialog = new JFrameDialog();  
					//JFrame jfDialog = new JFrame(); 
					JPanel dialogJP=new JPanel();
					
					dialogJP.add(jlCode);
					dialogJP.add(jtfCode);
					dialogJP.add(jlName);
					dialogJP.add(jtfName);
					dialogJP.add(cancelButton);	
					dialogJP.add(confirmButton);	
					//取消
					cancelButton.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent event)
						{
							jfDialog.dispose();
						}
					});
					confirmButton.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent event)
						{
							 
							StockMarket sMarket=new StockMarket(1,jtfCode.getText(),jtfName.getText(),"");
							try {
								sbDao.insertStockMarket(sMarket);
							} catch (IOException e) {
								e.printStackTrace();
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							} catch (SQLException e) {
								e.printStackTrace();
							}
						//	stockBase.setSelectedIndex(stockBase.getModel().getSize() -1);
							//stockBase.					
							
							jfDialog.dispose();
						}
					});
					
					jfDialog.add(dialogJP,BorderLayout.CENTER);
					jfDialog.setVisible(true);  
					System.out.println("operate market info");
				}
				else if(paneNmae.equals("stock"))
				{
					addSingleStock();
				}
				
            }
			else if(e.getSource()==editButton){
            	editButton.setBackground(Color.green);
            	if(paneNmae.equals("concept"))
					System.out.println("operate concept");
            	else if(paneNmae.equals("industry"))
					System.out.println("operate industry");
				else if(paneNmae.equals("regional"))
					System.out.println("operate regional");
				else if(paneNmae.equals("market"))
					System.out.println("operate market");
				else if(paneNmae.equals("stock")){
					System.out.println("operate stock");
				}
					
            }
			else if(e.getSource()== deleteButton){
            	deleteButton.setBackground(Color.RED);
            	if(paneNmae.equals("concept"))
					System.out.println("operate concept");
            	else if(paneNmae.equals("industry"))
					System.out.println("operate industry");
				else if(paneNmae.equals("regional"))
					System.out.println("operate regional");
				else if(paneNmae.equals("market"))
				{
					int selectedRow = stockTable.getSelectedRow();//获得选中行的索引
					System.out.println("del selectedRow:"+selectedRow);
	                if(selectedRow!=-1)  //存在选中行
	                {
	                	stMarketTabMod.removeRow(selectedRow);  //删除行
	                }
	               
					System.out.println("del market");
				}
				else if(paneNmae.equals("stock"))
				{
					int selectedRow = stockTable.getSelectedRow();//获得选中行的索引
					System.out.println("del selectedRow:"+selectedRow);
	                if(selectedRow!=-1)  //存在选中行
	                {
	                	System.out.println("del 1111selectedRow:"+selectedRow);
	                	stSingleTabMod.removeRow(selectedRow);  //删除行
	                	
	                	StockSingle ss = listStockSingle.get(selectedRow);
	                	System.out.println("del stock:"+ss.getStockFullId()+":"+ss.getStockName());
	                	//删除
	                	try {
							sbDao.deleteStockSingle(ss);
						} catch (IOException ev) {
							// TODO Auto-generated catch block
							ev.printStackTrace();
						} catch (ClassNotFoundException ev) {
							// TODO Auto-generated catch block
							ev.printStackTrace();
						} catch (SQLException ev) {
							// TODO Auto-generated catch block
							ev.printStackTrace();
						}
						listStockSingle.remove(ss);
						System.out.println(listStockSingle.size());	                	
	                }						
				}
            }			
			else if(e.getSource()==queryButton){
            	queryButton.setBackground(Color.PINK);
            	if(paneNmae.equals("concept"))
					System.out.println("operate concept");
            	else if(paneNmae.equals("industry"))
					System.out.println("operate industry");
				else if(paneNmae.equals("regional"))
					System.out.println("operate regional");
				else if(paneNmae.equals("market"))
				{
					System.out.println(jtfMarket.getText());
				}
				
            }
			
		
		}

		
		public void mouseReleased(MouseEvent e) {
			
			
		}
	
		
		
	}
	
	class JFrameDialog extends JFrame  
	{  
	    public JFrameDialog()  
	    {  
	        Jfinit();  
	    }  
	  
	    public void Jfinit()  
	    {  
	        this.setSize(300, 500);  
	        this.setTitle("弹出框");  
	        this.setLocationRelativeTo(null);//居中
//	        this.setVisible(true);  
	    }  
	}
	

	 private static void miniTray() {  //窗口最小化到任务栏托盘

		  ImageIcon trayImg = new ImageIcon("image/stock.png");//托盘图标

		  PopupMenu pop = new PopupMenu();  //增加托盘右击菜单
		  MenuItem show = new MenuItem("还原");
		  MenuItem exit = new MenuItem("退出");

		  show.addActionListener(new ActionListener() {
			  public void actionPerformed(ActionEvent e) { // 按下还原键
				  tray.remove(trayIcon);
				  jFrame.setVisible(true);
				//  jFrame.setExtendedState(JFrame.NORMAL);
					jFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
				  jFrame.toFront();
		   }		
		  });
		  
		  exit.addActionListener(new ActionListener() { // 按下退出键
		     public void actionPerformed(ActionEvent e) {
		      tray.remove(trayIcon);
		      System.exit(0);
		     }
		    });

			  pop.add(show);
			  pop.add(exit);

			  trayIcon = new TrayIcon(trayImg.getImage(), "股票系统", pop);
			  trayIcon.setImageAutoSize(true);

			  trayIcon.addMouseListener(new MouseAdapter() {
			   public void mouseClicked(MouseEvent e) { // 鼠标器双击事件

			    if (e.getClickCount() == 2) {

			     tray.remove(trayIcon); // 移去托盘图标
			     jFrame.setVisible(true);
			   //  jFrame.setExtendedState(JFrame.NORMAL); // 还原窗口
				jFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
			     jFrame.toFront();
			    }

			   }

			  });

			  try {
			   tray.add(trayIcon);
			  } catch (AWTException e1) {
			   // TODO Auto-generated catch block
			   e1.printStackTrace();
			  } 
		
	 }
	 
	 
	public void loadData(int loadType) throws IOException, ClassNotFoundException, SQLException{
		int ret = 0;
		ExcelReader excelReader=null;
		excelReader = new ExcelReader(sbDao);	
		String desc=null;
	
		switch(loadType){
			case 9: //导入一级行业	
				ret=excelReader.readIndustry();
				desc = "数据导入出错，请检查文件1Industry";
				break;
			case 10: //导入一级行业对应概念	
				ret=excelReader.readFirstIndustry_To_Concept();
				desc = "数据导入出错，请检查文件2FirstIndustry-to-Concept.xlsx";
				break;
			case 11: //导入三级行业对应股票	
				ret=excelReader.readThirdIndustry_to_stock();
				desc="数据导入出错，请检查文件3ThirdIndustry-to-stock.xlsx";
				break;
			case 12: //导入概念对应股票	
				ret=excelReader.readConcept_to_stock();
				desc="数据导入出错，请检查文件4Concept-to-stock.xlsx";
				break;
			case 13: //导入两融	
				ret=excelReader.readTwoRong();
				desc="数据导入出错，请检查文件5TwoRong.xlsx";
				break;
			case 14: //导入基本面
				ret=excelReader.readstock_baseExpect();
				desc="数据导入出错，请检查文件5TwoRong.xlsx";
				break;
			case 15: //导入基本面
				ret=excelReader.readMarketInfo();
				ret=excelReader.readFuturesInfo();
				desc="数据导入出错，请检查文件0Market_BaseInfo.xlsx或7ExMarket_BaseInfo.xlsx";
				break;
			case 16: //导入基本面
				ret=excelReader.readStockToFeatures();
				desc="数据导入出错，请检查文件8ExMarket-to-stock.xlsx";
				break;
			case 17: //年份信息面
				ret=excelReader.readYearInfo();
				desc="数据导入出错，请检查文件9 10 11 12四个文件";
				break;
			default:
				break;
		}
		
		jop=new JOptionPane();					
		jFrame.add(jop,BorderLayout.NORTH);				
		if (ret!=0){					
			jop.showMessageDialog(jFrame, desc,"提示", JOptionPane.INFORMATION_MESSAGE); 
		} else {
			jop.showMessageDialog(jFrame, "数据导入成功","提示", JOptionPane.INFORMATION_MESSAGE); 
		}
		return;
		
	}
	
	//分析股票数据
	public void analyseStockData(int type){
		PointClass pc=new PointClass(sbDao,sdDao,spDao);						
		try {
			//计算当前数据
				if(type == ConstantsInfo.StockMarket) 
					pc.getPointToTable(ConstantsInfo.StockCalCurData,ConstantsInfo.ALLMarket,ConstantsInfo.StockMarket);
				else 
					pc.getPointToTable(ConstantsInfo.StockCalCurData,ConstantsInfo.ALLMarket,ConstantsInfo.FuturesMarket);
			} catch (SecurityException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (ClassNotFoundException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (SQLException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (InstantiationException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (IllegalAccessException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (NoSuchFieldException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			jop=new JOptionPane();					
			jFrame.add(jop,BorderLayout.NORTH);
			jop.showMessageDialog(jFrame, "数据已经分析完成","提示", JOptionPane.INFORMATION_MESSAGE); 
			return;
	}
	 
	 //导出数据
    public void exportExcelFile(int type){
		Date startDate1 = new Date();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");  
		String dateNowStr1 = sdf1.format(startDate1);    	
			
		StockExcelPartitionMain sep = new StockExcelPartitionMain(sbDao,sdDao,spDao,ssDao);	   	
		StockRecentWriter ew=new StockRecentWriter(sbDao,sdDao,spDao);
		try {
			//ew.exportRecentlyStockFromConcept("export\\",dateNowStr1);
		   // ew.exportRecentlyStockFromIndustry("export\\",dateNowStr1);	
			
			if(type == ConstantsInfo.StockMarket) {
				sep.writeExcelFormIndustryOrderBy("export\\",dateNowStr1);
				sep.writeExcelFormConceptInFirstIndustryOrderBy("export\\",dateNowStr1);
			} else {
				sep.writeExcelFormFuturesOrderBy("export\\",dateNowStr1);
			}
			//sep.writeExcelFormConceptOrderBy("export\\",dateNowStr1);
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
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
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}			
		
		jop=new JOptionPane();					
		jFrame.add(jop,BorderLayout.NORTH);
		jop.showMessageDialog(jFrame, "导出成功","提示", JOptionPane.INFORMATION_MESSAGE); 
		return;
	 }
    
    //导入统计表
    public void loadSummaryExcelFile(int type) 
    {
    	
    	StockExcelReadMain seRead = new StockExcelReadMain(sbDao,sdDao,spDao,ssDao);
		//seRead.create_summarY("123");
		try {
			
			seRead.readStockAnsyleExcelData(type);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	jop=new JOptionPane();					
		jFrame.add(jop,BorderLayout.NORTH);
		jop.showMessageDialog(jFrame, "导入成功","提示", JOptionPane.INFORMATION_MESSAGE); 
		return;
    }
    
    
    
    public void analyseOperation(int type)
    {
    	Date startDate1 = new Date();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");  
		String dateNowStr1 = sdf1.format(startDate1);    	
			
		StockExcelOperationMain sop = new StockExcelOperationMain(sbDao,sdDao,spDao,ssDao);	 
    	
	/*
			try {
				
		
				if(type == ConstantsInfo.StockMarket) {
					sop.delete_data(ConstantsInfo.StockMarket);
				} else {
					sop.delete_data(ConstantsInfo.FuturesMarket);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		*/
    	
		try {
			if(type == ConstantsInfo.StockMarket) {
				sop.analyseStockOperation(ConstantsInfo.StockMarket,null);
			} else {
				sop.analyseStockOperation(ConstantsInfo.FuturesMarket,null);
			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	jop=new JOptionPane();					
		jFrame.add(jop,BorderLayout.NORTH);
		jop.showMessageDialog(jFrame, "操作分析成功","提示", JOptionPane.INFORMATION_MESSAGE); 
		return;
    }
	 
    
    public void exportOperationExcelFile(int type)
    {
    	Date startDate1 = new Date();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");  
		String dateNowStr1 = sdf1.format(startDate1);    	
			
		StockExcelPartitionMain sep = new StockExcelPartitionMain(sbDao,sdDao,spDao,ssDao);	    
    	try {
    		if(type == ConstantsInfo.StockMarket) {
    			sep.writeOperationExcelFormConceptInFirstIndustryOrderBy("export\\",dateNowStr1);
    			sep.writeOperationExcelFormIndustryOrderBy("export\\",dateNowStr1);
    		} else {
    			sep.writeOperationExcelFormFuturesOrderBy("export\\",dateNowStr1);
    		}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	jop=new JOptionPane();					
		jFrame.add(jop,BorderLayout.NORTH);
		jop.showMessageDialog(jFrame, "导出成功","提示", JOptionPane.INFORMATION_MESSAGE); 
		return;
    }
	 
    
    
    public void exportSummaryExcelFile(int type)
    {
    	Date startDate1 = new Date();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");  
		String dateNowStr1 = sdf1.format(startDate1);    	
			
		StockExcelPartitionMain sep = new StockExcelPartitionMain(sbDao,sdDao,spDao,ssDao);	   
    	try {
    		if(type == ConstantsInfo.StockMarket) {
    			sep.writeSummaryExcelFormConceptInFirstIndustryOrderBy("export\\",dateNowStr1);
    		} else {
    			sep.writeSummaryExcelFormFuturesOrderBy("export\\",dateNowStr1);
    		}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	jop=new JOptionPane();					
		jFrame.add(jop,BorderLayout.NORTH);
		jop.showMessageDialog(jFrame, "导出成功","提示", JOptionPane.INFORMATION_MESSAGE); 
		return;
    }
	 
	 //增加股票
	 public void addSingleStock()
	 {
		 	String listItem[] = {"否","是"};
			JLabel jlCode=new JLabel("股票代码");
			JLabel jlName=new JLabel("名称");
			JLabel jlIndustry=new JLabel("行业");
			JLabel jlConcept=new JLabel("概念");
		
			JLabel jlMarginTrading=new JLabel("融资融券");					
			final JTextField jtfCode=new JTextField(6);
			final JTextField jtfName=new JTextField(12);
			final JTextField jtfieldIndu=new JTextField(24); //记录选择的行业
			final JTextField jtfieldConc=new JTextField(48); //记录选择的概念
		//	final JTextField jtfIndustry=new JTextField(24);
		//	final JTextField jtfConcept=new JTextField(24);
			
			//概念
			List<String> allConceptNameList = null;
			try {
				allConceptNameList = sbDao.getAllConceptName();
			} catch (SQLException e3) {
				
				e3.printStackTrace();
			}
			final String[] conceptName = (String[])allConceptNameList.toArray(new String[allConceptNameList.size()]);
			final JList jlistConcept=new JList(conceptName);
			
			//行业
			List<String> allThirdIndustryNameList = null;
			try {
				allThirdIndustryNameList = sbDao.getAllThirdIndustryName();
			} catch (SQLException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}	
			String[] industryName = (String[])allThirdIndustryNameList.toArray(new String[allThirdIndustryNameList.size()]);
			final JList jlistIndustry=new JList(industryName);
			jlistIndustry.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
			//融资融券
			final JList jlistMarginTrading=new JList(listItem);
			jlistMarginTrading.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			JButton cancelButton=new JButton("取消");	
			JButton confirmButton=new JButton("确定");	
			
			final JFrame jfDialog = new JFrame();  // JFrameDialog
			
			//JFrame jfDialog = new JFrame(); 
			JPanel dialogJP=new JPanel();
			JPanel dialogJPCenter=new JPanel();
			JPanel dialogJPBottom=new JPanel();
			
			dialogJP.add(jlCode);
			dialogJP.add(jtfCode);
			dialogJP.add(jlName);
			dialogJP.add(jtfName);
			dialogJPCenter.add(jlIndustry);
			dialogJPCenter.add(new JScrollPane(jlistIndustry));
		//	dialogJPCenter.add(jtfIndustry);
			dialogJPCenter.add(jlConcept);
			dialogJPCenter.add(new JScrollPane(jlistConcept));
		//	dialogJP.add(jtfConcept);
		//	dialogJP.add(jlConceptCommit);
			dialogJPCenter.add(jlMarginTrading);
			dialogJPCenter.add(jlistMarginTrading);
			dialogJPBottom.add(cancelButton);	
			dialogJPBottom.add(confirmButton);	
			
			if(jop!=null)
			{
				//jFrame.remove(jop);
				jfDialog.remove(jop);
			}
			
			//行业选择框				
			jlistIndustry.addListSelectionListener(new ListSelectionListener()
			{
				public void valueChanged(ListSelectionEvent arg0) {
					// TODO Auto-generated method stub
					int tmp = 0;	
					jtfieldIndu.setText(((JList)arg0.getSource()).getSelectedValue().toString());				
			        System.out.println(jlistIndustry.getSelectedValue().toString());
				}
			});
			
			//概念选择框
			jlistConcept.addListSelectionListener(new ListSelectionListener()
			{
			
				public void valueChanged(ListSelectionEvent arg0) {
					// TODO Auto-generated method stub
					int tmp = 0;	
					jtfieldConc.setText(((JList)arg0.getSource()).getSelectedValue().toString());				       
				}

			});
			
			
			//取消
			cancelButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{
					jfDialog.dispose();
				}
			});
			
			confirmButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{
					String stockCode=null;
					StockSingle sStock=null;
					//检查股票代码长度
					if (jtfCode.getText().length()!=6){
						jop=new JOptionPane();					
						//jFrame.add(jop,BorderLayout.NORTH);
						jfDialog.add(jop,BorderLayout.NORTH);
						
						jop.showMessageDialog(jFrame, "股票代号长度有错误,请输入6位正确的代码","提示", JOptionPane.INFORMATION_MESSAGE); 
						return;	
					}
					
					if (jtfCode.getText().startsWith("6")) {
						stockCode = "SH"+jtfCode.getText();
					} else {
						stockCode = "SZ"+jtfCode.getText();
					}
					
					//检查股票代码
					try {
						sStock = sbDao.lookUpStockSingle(stockCode);
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					} catch (ClassNotFoundException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					} catch (SQLException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					//已经存在
					if(sStock != null){
						jop=new JOptionPane();					
						//jFrame.add(jop,BorderLayout.NORTH);
						jfDialog.add(jop,BorderLayout.NORTH);
						
						jop.showMessageDialog(jFrame, "该股票信息已经存在","提示", JOptionPane.INFORMATION_MESSAGE); 
						return;	
					}							
					
					//检查行业
					StockIndustry sindustry=null;
					try {
						sindustry= sbDao.lookUpIndustry(jtfieldIndu.getText());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ClassNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if(sindustry==null){
						jop=new JOptionPane();					
						//jFrame.add(jop,BorderLayout.NORTH);
						jfDialog.add(jop,BorderLayout.NORTH);
						jop.showMessageDialog(jFrame, "行业名有错误,请输入正确的行业","提示", JOptionPane.INFORMATION_MESSAGE); 
						return;	
					}
							
					//检查概念	
					StockConcept sconcept=null;		    
			    	try {
						sconcept=sbDao.lookUpStockConcept(jtfieldConc.getText());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					if(sconcept==null){
						jop=new JOptionPane();					
						jFrame.add(jop,BorderLayout.NORTH);						
						jop.showMessageDialog(jFrame, "概念不存在,请输入正确的概念","提示", JOptionPane.INFORMATION_MESSAGE); 
						return;	
					}
			    				
					//插入新股票
					sStock=new StockSingle(1,stockCode,jtfName.getText(),sindustry.getThirdcode(),jtfieldIndu.getText(),sindustry.getSecondcode(),sindustry.getSecondname(),sindustry.getFirstcode(),sindustry.getFirstname(),jtfieldConc.getText(),jlistMarginTrading.getSelectedIndex());
					try {
						//插入stock_to_industry
						sbDao.insertStockSingleToIndustry(sStock);
						//插入allinfo
						sbDao.insertStockSingle(sStock);
						//插入stock_to_concept
						//sbDao.insertStockToConcept(stockCode,jtfName.getText(),sconcept.getCode(),sconcept.getName());
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (SQLException e) {
						e.printStackTrace();
					}			
					
					//已经存在					
					jop=new JOptionPane();					
					//jFrame.add(jop,BorderLayout.NORTH);
					jfDialog.add(jop,BorderLayout.NORTH);
					jop.showMessageDialog(jFrame, "数据更新成功","提示", JOptionPane.INFORMATION_MESSAGE); 
					
					jfDialog.dispose();//关闭
				}
			});
			jfDialog.add(dialogJP,BorderLayout.NORTH);
			jfDialog.add(dialogJPCenter,BorderLayout.CENTER);
			jfDialog.add(dialogJPBottom,BorderLayout.SOUTH);
		//	jfDialog.setExtendedState(Frame.MAXIMIZED_BOTH);
			jfDialog.setSize(500,300);  
				
			jfDialog.setVisible(true);  
			jfDialog.setLocationRelativeTo(null);
			System.out.println("operate stock info");
		
	 }
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
		
		Connection stockBaseConn = DbConn.getConnDB("stockConf/conn_base_db.ini"); 
	    Connection stockDataConn = DbConn.getConnDB("stockConf/conn_data_db.ini"); 
	    Connection stockPointConn = DbConn.getConnDB("stockConf/conn_point_db.ini");  
	    Connection stockSummaryConn = DbConn.getConnDB("stockConf/conn_summary_db.ini");
	   
		stockBM=new StockBaseManager(stockBaseConn,stockDataConn,stockPointConn,stockSummaryConn);
		PropertyConfigurator.configure("StockConf/log4j_manage.properties");
		stockLogger.logger.fatal("stock manager start");
		stockBM.init();	
		
		//stockBaseConn.close();
		//stockPointConn.close();
		//stockDataConn.close();

	}

}
