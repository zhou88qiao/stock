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
import java.sql.SQLException;
import java.util.ArrayList;
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

import com.point.stock.PointClass;
import com.timer.stock.DateStock;
import common.ConstantsInfo;

import dao.StockBaseDao;
import dao.StockConcept;
import dao.StockDataDao;
import dao.StockIndustry;
import dao.StockInformationDao;
import dao.StockMarket;
import dao.StockPoint;
import dao.StockPointDao;
import dao.StockRegional;
import dao.StockSingle;
import getStockData.GetStockData;

import stockGUI.stockbase.StockSingleGui;
import stockGUI.stocktable.StockConceptTableModel;
import stockGUI.stocktable.StockIndustryTableModel;
import stockGUI.stocktable.StockMarketTableModel;
import stockGUI.stocktable.StockRegionalTableModel;
import stockGUI.stocktable.StockSingleTableModel;
import stockGUI.stocktable.StockTableModel;


public class StockBaseManagerV1 {
	private final static int  addIndex=0;
	private final static int  editIndex=1;
	private final static int  delIndex=2;
	private final static int  queryIndex=3;	
	
	static StockBaseManagerV1 stockBM;
	static StockDataDao sdDao=new StockDataDao();
	static StockPointDao spDao=new StockPointDao();
	static StockBaseDao sbDao=new StockBaseDao();
	static StockInformationDao siDao =new StockInformationDao();
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
	JMenu stockBase=new JMenu("基本信息");	
	JMenu stockFunction=new JMenu("功能");
	JMenu stockAnalyze=new JMenu("分析");	
	JMenu stockMessage=new JMenu("资讯");
	
	JMenuItem GetStockData=new JMenuItem("获取当天数据");
	JMenuItem ExtremeAnalyze=new JMenuItem("分析当天极值");
	JMenuItem LoadPointExcel=new JMenuItem("极值数据导出");
	JMenuItem DayAnalyze=new JMenuItem("ma日分析");
	JMenuItem WeekAnalyze=new JMenuItem("ma周分析");
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
	JLabel jlStockFullId=new JLabel("股票代码:");
	JLabel jlStockName=new JLabel("股票名称:");
	JLabel jlStockIndustry=new JLabel("三级行业:");
	JLabel jlStockConcept=new JLabel("概念:");
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
	
	StockSingle  sSingle=null;
	
	
	public void init(){
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
	
		stockAnalyze.addActionListener(actionL);
		
		
		addButton.addMouseListener(actionM);
		deleteButton.addMouseListener(actionM);
		editButton.addMouseListener(actionM);
		queryButton.addMouseListener(actionM);
		
		addButton.setActionCommand(""+addIndex);
		deleteButton.setActionCommand(""+delIndex);
		editButton.setActionCommand(""+editIndex);
		queryButton.setActionCommand("" + queryIndex);
	
		stockAnalyze.add(DayAnalyze);
		stockAnalyze.add(WeekAnalyze);
		
		GetStockData.setActionCommand("" + 6); //根据i 事件监听器获取
		ExtremeAnalyze.setActionCommand("" + 7); //根据i 事件监听器获取		
		LoadPointExcel.setActionCommand("" + 8); //根据i 事件监听器获取
		
		GetStockData.addActionListener(actionL);
		ExtremeAnalyze.addActionListener(actionL);
		LoadPointExcel.addActionListener(actionL);
		
		stockFunction.add(GetStockData);
		stockFunction.add(ExtremeAnalyze);		
		stockFunction.add(LoadPointExcel);

		mb.add(stockBase);		
		mb.add(stockFunction);
		mb.add(stockAnalyze);
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

		     public void windowIconified(WindowEvent e) { // 窗口最小化事件

		      jFrame.setVisible(false);
		      stockBM.miniTray();

		     }

		    });
		
	}
	
	class menuActionListenter implements ActionListener
	{
		
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
			
			operationPane=new JPanel();
			operationPane.add(addButton);
			operationPane.add(deleteButton);
			operationPane.add(editButton);
		
			int index=Integer.parseInt(e.getActionCommand());
		//	System.out.println("菜单:"+index);
			switch(index)
			{
			case 0:

				try {
						listStockMarket=sbDao.getStockMarket();
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
			case 1:
				
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
			case 2:
			
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
			case 3:
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
			case 4:
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
			
				GetStockData gs=new GetStockData();
				try {
						gs.getStockInfoFromSina();
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
					jop=new JOptionPane();					
					jFrame.add(jop,BorderLayout.NORTH);
					jop.showMessageDialog(jFrame, "数据已经下载完成","提示", JOptionPane.INFORMATION_MESSAGE); 
					return;
			case 7://分析当天数据
				List<String> listStockFullId = new ArrayList<String>();
				PointClass pc=new PointClass();
				
				try {
						listStockFullId=siDao.getAllFullId();
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
				
				try {
					//计算当前数据
						pc.getPointToTable(ConstantsInfo.StockCalCurData,ConstantsInfo.ALLMarket);
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
			case 8: //导出数据
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
	
	
	
	class JtableMouseListenter implements MouseListener
	{

		
		public void mouseClicked(MouseEvent e) {
			
		String paneNmae=operationPane.getName();
			
		  if(e.getClickCount() == 2) //实现双击 
          { 
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
					String fullId=listStockSingle.get(row).getStockFullId();
						
					StockTableModel stTabMod;
					JTable stockTable;
					final JFrame jfDialog = new JFrameDialog();  
					System.out.println("operate stock "+fullId);
					JScrollPane stockSP=new JScrollPane();
					
					List<String> listStockDays=new ArrayList<String>();
					List<String> listStockWeeks=new ArrayList<String>();
					List<String> listStockMonths=new ArrayList<String>();
					List<String> listStockSeasons=new ArrayList<String>();
					List<String> listStockYears=new ArrayList<String>();
	
					
					try {
						listStockDays=sdDao.getDates(fullId,ConstantsInfo.DayDataType);
						listStockWeeks=sdDao.getDates(fullId,ConstantsInfo.WeekDataType);
						listStockMonths=sdDao.getDates(fullId,ConstantsInfo.MonthDataType);
						listStockSeasons=sdDao.getDates(fullId,ConstantsInfo.SeasonDataType);
						listStockYears=sdDao.getDates(fullId,ConstantsInfo.YearDataType);
					} catch (SQLException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					
				//	listStockYears=sdDao.getDates("sh000001_copy",ConstantsInfo.YearDataType);
					DateStock dStock=new DateStock(listStockDays,listStockWeeks,listStockMonths,listStockSeasons,listStockYears);
					
					
					List<StockPoint> stockDayPoint=new ArrayList<StockPoint>();
					List<StockPoint> stockWeekPoint=new ArrayList<StockPoint>();
					List<StockPoint> stockMonthPoint=new ArrayList<StockPoint>();

					try {
						stockDayPoint=spDao.getAllPointStock(fullId,ConstantsInfo.DayDataType);
						stockWeekPoint=spDao.getAllPointStock(fullId, ConstantsInfo.WeekDataType);
		    			stockMonthPoint=spDao.getAllPointStock(fullId,  ConstantsInfo.MonthDataType);
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
	    			
					//未记录或不存在该数据
		    		if(stockDayPoint==null || stockDayPoint.size()==0)
		    		{
		    			JOptionPane jop=new JOptionPane();					
						jFrame.add(jop,BorderLayout.NORTH);
						jop.showMessageDialog(jFrame, "不存在该数据","提示", JOptionPane.INFORMATION_MESSAGE); 
		    			return;
		    		}
					//	stockDayPoint=pClass.getStockDayExtremePoint(fullId, dStock,ConstantsInfo.StockCalAllData);
					//	stockWeekPoint=pClass.getStockWeekExtremePoint(fullId, dStock,ConstantsInfo.StockCalAllData);
					//	stockMonthPoint=pClass.getStockMonthExtremePoint(fullId, dStock,ConstantsInfo.StockCalAllData);
				
					System.out.println("DayPoint len:"+stockDayPoint.size());
					System.out.println("WeekPoint len:"+stockWeekPoint.size());
					System.out.println("MonthPoint len:"+stockMonthPoint.size());
					stTabMod=new StockTableModel(stockDayPoint,stockWeekPoint,stockMonthPoint);	
					stockTable=new JTable(stTabMod);	
					stockTable.setRowSelectionAllowed(false);
					stockTable.setCellSelectionEnabled(false);
					stockTable.setColumnSelectionAllowed(false);
					stockSP=new JScrollPane(stockTable);
					jfDialog.add(stockSP,BorderLayout.CENTER);					
					jfDialog.setVisible(true);  
					
				}

           }else if(e.getClickCount() == 1){
        	   //先单击 后编辑
        	   int row =((JTable)e.getSource()).rowAtPoint(e.getPoint()); //获得行位置 
        	   if(paneNmae.equals("stock")){
        		   sSingle=listStockSingle.get(row);
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
	

	
	class buttonMouseListenter implements MouseListener
	{
		public void mouseClicked(MouseEvent e) {
			//System.out.println("mouse:"+e.getSource());			
			
		}

		
		public void mouseEntered(MouseEvent e) {
			
			
		}

		
		public void mouseExited(MouseEvent e) {
			
			
		}

		
		public void mousePressed(MouseEvent event) {
		
			String paneNmae=operationPane.getName();
			//System.out.println("pane name:"+operationPane.getName());
			final JFrame jfDialog = new JFrameDialog();  
			if(jop!=null)
			{
				jFrame.remove(jop);
			}
			
			if(event.getSource()==addButton){
				
				
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
					StockSingleGui ssGui=new StockSingleGui();
					ssGui.init(null,ConstantsInfo.ADD);					
				}
				
            }
			else if(event.getSource()==editButton){
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
					if(sSingle==null){
						jfDialog.dispose();
						return;
					}
					StockSingleGui ssGui=new StockSingleGui(sSingle);
					ssGui.init(sSingle,ConstantsInfo.EDIT);	
					
				}
					
            }
			else if(event.getSource()==deleteButton){
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
				}else if(paneNmae.equals("stock")){
					jop=new JOptionPane();					
					jfDialog.add(jop,BorderLayout.NORTH);
					jop.showMessageDialog(jfDialog, "暂不支持对股票信息删除","提示", JOptionPane.INFORMATION_MESSAGE); 
					return;
				}
            }			
			else if(event.getSource()==queryButton){
            	queryButton.setBackground(Color.PINK);
            	if(paneNmae.equals("concept"))
					System.out.println("operate concept");
            	else if(paneNmae.equals("industry"))
					System.out.println("operate industry");
				else if(paneNmae.equals("regional"))
					System.out.println("operate regional");
				else if(paneNmae.equals("market")){
					System.out.println(jtfMarket.getText());
				}else if(paneNmae.equals("stock")){
					System.out.println(jtfMarket.getText());				
					
					try {
						listStockSingle=sbDao.lookUpStockSingleForALL(jtfFullId.getText(),jtfStockName.getText(),jtfStockIndustry.getText(),jtfStockConcept.getText());
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
					StockSingleGui ssGui=new StockSingleGui();
					ssGui.queryInit(listStockSingle);
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
	        init();  
	    }  
	  
	    public void init()  
	    {  
	        this.setSize(300, 500);  
	        this.setTitle("弹出框");  
	        this.setLocationRelativeTo(null);//居中
//	        this.setVisible(true);  
	    }  
	}
	
	/*
	class manualJMenu extends JMenuItem
	{
		actionListener
	}
	*/
	
	

	
	
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
	
	public static void main(String[] args) {
		
		stockBM=new StockBaseManagerV1();
		PropertyConfigurator.configure("log4j.properties");
		stockBM.init();	

	}

}
