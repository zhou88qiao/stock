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
	
	private static JFrame jFrame=new JFrame("��Ʊ������Ϣ");
	String[] baseItems = new String[] { "�г�", "����", "��ҵ", "����","����"};
	String[] operButtons = new String[] { "�½�", "�༭", "ɾ��","��ѯ"}; //0 ,1,2,3
	char[] baseShortcuts = { 'M','R','I','C','S'};
    char[] editShortcuts = { 'Z','X','C','V' };
    Color[] colors = { Color.RED, Color.YELLOW, Color.BLUE, Color.GREEN, Color.DARK_GRAY};
	private JMenuBar mb=new JMenuBar();
	JMenu stockBase=new JMenu("������Ϣ");	
	JMenu stockFunction=new JMenu("����");
	JMenu stockAnalyze=new JMenu("����");	
	JMenu stockMessage=new JMenu("��Ѷ");
	
	JMenuItem GetStockData=new JMenuItem("��ȡ��������");
	JMenuItem ExtremeAnalyze=new JMenuItem("�������켫ֵ");
	JMenuItem LoadPointExcel=new JMenuItem("��ֵ���ݵ���");
	JMenuItem DayAnalyze=new JMenuItem("ma�շ���");
	JMenuItem WeekAnalyze=new JMenuItem("ma�ܷ���");
	JButton addButton=new JButton("�½�");	//0
	JButton editButton=new JButton("�༭");//1
	JButton deleteButton=new JButton("ɾ��");//2
	JButton queryButton=new JButton("��ѯ");//3
	
	JPanel Panel=new JPanel();
	JLabel jlMarket=new JLabel("�г�");
	JLabel jlConcept=new JLabel("����");
	JLabel jlProvince=new JLabel("ʡ����");
	JLabel jlCity=new JLabel("������");
	JLabel jlFirstIndustry=new JLabel("һ����ҵ����");
	JLabel jlSecondIndustry=new JLabel("������ҵ����");
	JLabel jlThirdIndustry=new JLabel("������ҵ����");
	JLabel jlStockFullId=new JLabel("��Ʊ����:");
	JLabel jlStockName=new JLabel("��Ʊ����:");
	JLabel jlStockIndustry=new JLabel("������ҵ:");
	JLabel jlStockConcept=new JLabel("����:");
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
             item.setActionCommand("" + i); //����i �¼���������ȡ
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
		
		GetStockData.setActionCommand("" + 6); //����i �¼���������ȡ
		ExtremeAnalyze.setActionCommand("" + 7); //����i �¼���������ȡ		
		LoadPointExcel.setActionCommand("" + 8); //����i �¼���������ȡ
		
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
		
		jFrame.addWindowListener(new WindowAdapter() { // ���ڹر��¼�
		     public void windowClosing(WindowEvent e) {
		      System.exit(0);
		     };

		     public void windowIconified(WindowEvent e) { // ������С���¼�

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
		//	System.out.println("�˵�:"+index);
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
		
			case 6://��ȡ����
			
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
					jop.showMessageDialog(jFrame, "�����Ѿ��������","��ʾ", JOptionPane.INFORMATION_MESSAGE); 
					return;
			case 7://������������
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
					//���㵱ǰ����
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
					jop.showMessageDialog(jFrame, "�����Ѿ��������","��ʾ", JOptionPane.INFORMATION_MESSAGE); 
					return;
			case 8: //��������
			default:
				break;
			
			}
			
			//���Ӳ���pane
			operationPane.add(queryButton);			
			jFrame.add(operationPane,BorderLayout.NORTH);
			
			//���ӱ�pane
			
			stockTable.addMouseListener(new JtableMouseListenter());
			
		//	stockTable.setRowSelectionAllowed(false);
		//	stockTable.setCellSelectionEnabled(false);
		//	stockTable.setColumnSelectionAllowed(false);
			stockTable.setBorder(BorderFactory.createEtchedBorder());  
			stockTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			stockTable.setSelectionForeground(Color.blue);
			stockTable.setShowVerticalLines(false);//  
		        // �����Ƿ���ʾ��Ԫ���ķָ���
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
			
		  if(e.getClickCount() == 2) //ʵ��˫�� 
          { 
                int row =((JTable)e.getSource()).rowAtPoint(e.getPoint()); //�����λ�� 
               // int col=((JTable)e.getSource()).columnAtPoint(e.getPoint()); //�����λ�� 
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
	    			
					//δ��¼�򲻴��ڸ�����
		    		if(stockDayPoint==null || stockDayPoint.size()==0)
		    		{
		    			JOptionPane jop=new JOptionPane();					
						jFrame.add(jop,BorderLayout.NORTH);
						jop.showMessageDialog(jFrame, "�����ڸ�����","��ʾ", JOptionPane.INFORMATION_MESSAGE); 
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
        	   //�ȵ��� ��༭
        	   int row =((JTable)e.getSource()).rowAtPoint(e.getPoint()); //�����λ�� 
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
					JLabel jlCode=new JLabel("����");
					JLabel jlName=new JLabel("����");
					final JTextField jtfCode=new JTextField(6);
					final JTextField jtfName=new JTextField(24);
					JButton cancelButton=new JButton("ȡ��");	
					JButton confirmButton=new JButton("ȷ��");	
					
					
					//JFrame jfDialog = new JFrame(); 
					JPanel dialogJP=new JPanel();
					
					dialogJP.add(jlCode);
					dialogJP.add(jtfCode);
					dialogJP.add(jlName);
					dialogJP.add(jtfName);
					dialogJP.add(cancelButton);	
					dialogJP.add(confirmButton);	
					//ȡ��
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
					int selectedRow = stockTable.getSelectedRow();//���ѡ���е�����
					System.out.println("del selectedRow:"+selectedRow);
	                if(selectedRow!=-1)  //����ѡ����
	                {
	                	stMarketTabMod.removeRow(selectedRow);  //ɾ����
	                }
	               
					System.out.println("del market");
				}else if(paneNmae.equals("stock")){
					jop=new JOptionPane();					
					jfDialog.add(jop,BorderLayout.NORTH);
					jop.showMessageDialog(jfDialog, "�ݲ�֧�ֶԹ�Ʊ��Ϣɾ��","��ʾ", JOptionPane.INFORMATION_MESSAGE); 
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
	        this.setTitle("������");  
	        this.setLocationRelativeTo(null);//����
//	        this.setVisible(true);  
	    }  
	}
	
	/*
	class manualJMenu extends JMenuItem
	{
		actionListener
	}
	*/
	
	

	
	
	 private static void miniTray() {  //������С��������������

		  ImageIcon trayImg = new ImageIcon("image/stock.png");//����ͼ��

		  PopupMenu pop = new PopupMenu();  //���������һ��˵�
		  MenuItem show = new MenuItem("��ԭ");
		  MenuItem exit = new MenuItem("�˳�");

		  show.addActionListener(new ActionListener() {
			  public void actionPerformed(ActionEvent e) { // ���»�ԭ��
				  tray.remove(trayIcon);
				  jFrame.setVisible(true);
				//  jFrame.setExtendedState(JFrame.NORMAL);
					jFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
				  jFrame.toFront();
		   }		
		  });
		  
		  exit.addActionListener(new ActionListener() { // �����˳���
		     public void actionPerformed(ActionEvent e) {
		      tray.remove(trayIcon);
		      System.exit(0);
		     }
		    });

			  pop.add(show);
			  pop.add(exit);

			  trayIcon = new TrayIcon(trayImg.getImage(), "��Ʊϵͳ", pop);
			  trayIcon.setImageAutoSize(true);

			  trayIcon.addMouseListener(new MouseAdapter() {
			   public void mouseClicked(MouseEvent e) { // �����˫���¼�

			    if (e.getClickCount() == 2) {

			     tray.remove(trayIcon); // ��ȥ����ͼ��
			     jFrame.setVisible(true);
			   //  jFrame.setExtendedState(JFrame.NORMAL); // ��ԭ����
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
