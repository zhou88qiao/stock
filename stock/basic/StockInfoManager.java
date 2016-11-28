package stock.basic;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.CellEditor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


import common.ConstantsInfo;
import common.stockLogger;

import dao.DayStockDao;
import dao.StockDataDao;
import dao.StockInformation;
import dao.StockInformationDao;

import stockGUI.stockDialog;

import stockGUI.stocktableheader.GroupableTableHeader;
import stockGUI.stocktree.StockObjectType;
import stockGUI.stocktree.StockTreeCellRenderer;
import stockGUI.stocktree.StockTreeNodeData;
 

public class StockInfoManager {

    //static Logger logger=Logger.getLogger("stock");
        
	static StockInformationDao sid =new StockInformationDao();
	static StockDataDao sdDao=new StockDataDao();
	static List<StockInformation> listStockInfo = new ArrayList<StockInformation>(); 
	List<String> listIndustry = new ArrayList<String>(); 
	List<String> listConcept = new ArrayList<String>(); 	
	List<String> listRegional = new ArrayList<String>(); 
	List<String> listStockIndustry = new ArrayList<String>(); 
	List<String> listStockConcept = new ArrayList<String>(); 	
	List<String> listStockRegional = new ArrayList<String>(); 
	List<String> listStockOfYear = new ArrayList<String>(); 
	
	DefaultMutableTreeNode root=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.ROOT,"���ǻ�"));
	DefaultMutableTreeNode hs=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.MARKET,"�������"));
	DefaultMutableTreeNode industry=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.PLATE,"��ҵ���"));	
	DefaultMutableTreeNode concept=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.PLATE,"������"));
	DefaultMutableTreeNode regional=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.PLATE,"������"));
	DefaultMutableTreeNode market=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.PLATE,"����ָ��"));
	DefaultMutableTreeNode fund=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.MARKET,"����"));
	DefaultMutableTreeNode openFund=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.PLATE,"����ʽ����"));
	DefaultMutableTreeNode closeFund=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.PLATE,"���ʽ����"));
	
	private JFrame jFrame=new JFrame("��Ʊ������Ϣ");
	JTree stockTree;
	JPanel operationPane;
	DefaultTreeModel stockModel;
	JScrollPane stockSP;
	JScrollPane stockTreeSP;
	
	JButton addBrotherButton=new JButton("����ֵܽ��");
	JButton addChildButton=new JButton("����ӽ��");
	JButton deleteButton=new JButton("ɾ��");
	JButton editButton=new JButton("�༭");
	
	TreePath movePath;
	String movePathName;

	public void init() throws IOException, ClassNotFoundException, SQLException
	{
		
		Iterator it,ie;
	
		listIndustry=sid.getAllIndustry(listStockInfo);		
		stockLogger.logger.debug("list industry size��"+listIndustry.size());
		 for(it = listIndustry.iterator();it.hasNext();)
		 {
			String stockIndu = (String) it.next();		
			stockLogger.logger.debug("��ҵ��"+stockIndu);
			//System.out.println(stockIndu);
			DefaultMutableTreeNode stIndustry=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.CLASS,stockIndu));
		//	DefaultMutableTreeNode stIndustry=new DefaultMutableTreeNode(stockIndu);
			industry.add(stIndustry);
			if(stockIndu.equals("����"))
				market.add(stIndustry);
			listStockIndustry=sid.getStockIndustry(stockIndu);
			stockLogger.logger.debug("��ҵ��Ʊ����"+listStockIndustry.size());
			for(ie=listStockIndustry.iterator();ie.hasNext();)
			{
				String stockInduFullID = (String) ie.next();		
				DefaultMutableTreeNode stStockIndustry=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.STOCK,stockInduFullID));
				//DefaultMutableTreeNode stStockIndustry=new DefaultMutableTreeNode(stockInduFullID);
				stockLogger.logger.debug("��Ʊ��"+stockInduFullID);
				stIndustry.add(stStockIndustry);
			}
		 }
		 
		 listConcept=sid.getAllConcept(listStockInfo);
		 stockLogger.logger.debug("list concept size��"+listConcept.size());
		 for(it = listConcept.iterator();it.hasNext();)
		 {
			String stockCon = (String) it.next();	
			stockLogger.logger.debug("���"+stockCon);
			//System.out.println(stockCon);
			DefaultMutableTreeNode stConcept=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.CLASS,stockCon));
			//DefaultMutableTreeNode stConcept=new DefaultMutableTreeNode(stockCon);
			concept.add(stConcept);
			listStockConcept=sid.getStockConcept(stockCon);
			stockLogger.logger.debug("�����Ʊ����"+listStockConcept.size());
			for(ie=listStockConcept.iterator();ie.hasNext();)
			{
				String stockConFullID = (String) ie.next();		
				DefaultMutableTreeNode stStockConcept=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.STOCK,stockConFullID));
			//	DefaultMutableTreeNode stStockConcept=new DefaultMutableTreeNode(stockConFullID);
				stockLogger.logger.debug("��Ʊ��"+stockConFullID);
				stConcept.add(stStockConcept);
			}
		 }
		 
		 listRegional=sid.getAllRegional(listStockInfo);
		 stockLogger.logger.debug("list regional size��"+listRegional.size());
		 for(it = listRegional.iterator();it.hasNext();)
		 {
			String stockReg = (String) it.next();	
			stockLogger.logger.debug("����"+stockReg);
			//System.out.println(stockReg);
			DefaultMutableTreeNode stRegional=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.CLASS,stockReg));
			//DefaultMutableTreeNode stRegional=new DefaultMutableTreeNode(stockReg);
			regional.add(stRegional);
			listStockRegional=sid.getStockRegional(stockReg);
			stockLogger.logger.debug("�����Ʊ����"+listStockRegional.size());
			for(ie=listStockRegional.iterator();ie.hasNext();)
			{
				String stockRegFullID = (String) ie.next();		
				DefaultMutableTreeNode stStockRegional=new DefaultMutableTreeNode(new StockTreeNodeData(StockObjectType.STOCK,stockRegFullID));
				//DefaultMutableTreeNode stStockRegional=new DefaultMutableTreeNode(stockRegFullID);
				stockLogger.logger.debug("��Ʊ��"+stockRegFullID);
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
		stockModel=(DefaultTreeModel)stockTree.getModel();
		
		//�ɱ༭
		stockTree.setEditable(true);		
		//ʹ���Զ���ڵ������
		//stockTree.setCellRenderer(new StockTreeCellRenderer());		
		
	//	Object obj = ((DefaultMutableTreeNode)path.getLastPathComponent()).getUserObject(); 
	//	((MyClass)obj).setName(newValue.toString()); 
	//	super.valueForPathChanged(path, obj);
	//	http://stackoverflow.com/questions/11554583/jtree-node-rename-preserve-user-object
		
		stockTree.setShowsRootHandles(true);
		stockTree.setRootVisible(true);
		
		MouseListener mouseListener=new MouseAdapter()
		{
			//�������
			public void mousePressed(MouseEvent e)
			{
				TreePath tp=stockTree.getPathForLocation(e.getX(), e.getY());
				
				if(tp!=null)
				{	
					movePath=tp;
					
		            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)tp.getLastPathComponent();
					movePathName=selectedNode.toString();
				}
			}
			
			//�ɿ����
			public void mouseReleased(MouseEvent e)
			{
				TreePath curPath=stockTree.getPathForLocation(e.getX(), e.getY());
				if(curPath!=null && movePath!=null)
				{				
					//��ֹ���ӽڵ��϶�
					if(movePath.isDescendant(curPath) && movePath!=curPath)
					{
						JOptionPane.showMessageDialog(jFrame,"�ýڵ㲻���ƶ�","�Ƿ�����",JOptionPane.ERROR_MESSAGE);
						return;
					}
					//�������ӽڵ��ƶ�����갴�£��ɿ���Ҳ����ͬһ���ڵ�
					else if(movePath!=curPath)
					{
						System.out.println("ԭ��"+movePathName);						
						if(curPath.getPathCount()==4 && movePath.getPathCount()==5)
						{
							DefaultMutableTreeNode selectedNode=(DefaultMutableTreeNode)curPath.getLastPathComponent();
							String curPathName=selectedNode.toString();
							System.out.println("�£�"+curPathName);
							//add �����Ƚ��ýڵ��ԭ���ڵ���ɾ��������ӵ��¸��ڵ�
							((DefaultMutableTreeNode)curPath.getLastPathComponent()).add((DefaultMutableTreeNode)movePath.getLastPathComponent());
							movePath=null;
							stockTree.updateUI();
							
							int updateType=0;
				            if(selectedNode.getParent().toString().contains("��ҵ"))
				            	updateType=ConstantsInfo.StockIndustry;
				            else if(selectedNode.getParent().toString().contains("����"))
				            	updateType=ConstantsInfo.StockConcept;
				            else if(selectedNode.getParent().toString().contains("����"))
				            	updateType=ConstantsInfo.StockRegional;
							
							//����
							try {
								sid.updateFromStockName(movePathName,curPathName,updateType);
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
							
						}
						else
						{	
							JOptionPane.showMessageDialog(jFrame,"�ýڵ㲻���ƶ�","�Ƿ�����",JOptionPane.ERROR_MESSAGE);
							return;
						}
					}
					
				}
			}
			
		};
		//��������
		stockTree.addMouseListener(mouseListener);
	
		operationPane=new JPanel();
		
		//����ֵܽڵ�
		addBrotherButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				//��ȡѡ�нڵ�
				DefaultMutableTreeNode selectedNode=(DefaultMutableTreeNode)stockTree.getLastSelectedPathComponent();
				if(selectedNode==null)
					return;
				//��ȡѡ�нڵ㸸�ڵ�
				DefaultMutableTreeNode parent=(DefaultMutableTreeNode)selectedNode.getParent();
				if(parent==null)
					return;
				//2����������ֵܽ��
				if(selectedNode.getLevel()==3)
				{
					/*
					//�����½ڵ�
					DefaultMutableTreeNode newNode=new DefaultMutableTreeNode("�½ڵ�");
					//��ȡѡ�нڵ�ѡ������
					int selectedIndex=parent.getIndex(selectedNode)+1;
					//��ѡ��λ�ò����½ڵ�
					stockModel.insertNodeInto(newNode, parent, selectedIndex);
					selectedNode.add(newNode);
					//��ȡ�Ӹ��ڵ㵽�½ڵ�����нڵ�
					TreeNode[] nodes=stockModel.getPathToRoot(newNode);
					TreePath path=new TreePath(nodes);
					//��ʾָ����treePath
					stockTree.scrollPathToVisible(path);
					//stockTree.updateUI();		
					*/
								
					int selectedIndex = parent.getIndex(selectedNode) + 1;
					String nodeName = (String)JOptionPane.showInputDialog(null,"����������","�½��",JOptionPane.PLAIN_MESSAGE);
					DefaultMutableTreeNode newNode =new DefaultMutableTreeNode(nodeName);
					System.out.println("�����ֵܽ������:"+nodeName);
					stockModel.insertNodeInto(newNode, parent, selectedIndex);
									
				}
				else
					JOptionPane.showMessageDialog(jFrame,"�ýڵ��ǲ�������ֵܽڵ�","�Ƿ�����",JOptionPane.ERROR_MESSAGE);
					
			}
		});
		operationPane.add(addBrotherButton);
		
		addChildButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				DefaultMutableTreeNode selectedNode=(DefaultMutableTreeNode)stockTree.getLastSelectedPathComponent();
				if(selectedNode==null)
					return;
				
				//2����������ӽ��
				if(selectedNode.getLevel()==2)
				{
					//�����½ڵ�
					String nodeName = (String)JOptionPane.showInputDialog(null,"����������","�½��",JOptionPane.PLAIN_MESSAGE);
					DefaultMutableTreeNode newNode=new DefaultMutableTreeNode(nodeName);
					selectedNode.add(newNode);
					TreeNode[] nodes=stockModel.getPathToRoot(newNode);
					TreePath path=new TreePath(nodes);
					stockTree.scrollPathToVisible(path);
					stockTree.updateUI();	
				}
				else
					JOptionPane.showMessageDialog(jFrame,"�ýڵ��ǲ�������ӽڵ�","�Ƿ�����",JOptionPane.ERROR_MESSAGE);
					
			}
		});
		operationPane.add(addChildButton);
		
		
		deleteButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				DefaultMutableTreeNode selectedNode=(DefaultMutableTreeNode) stockTree.getLastSelectedPathComponent();
			///	StockTreeNodeData
				if(selectedNode==null)
					return;
				//System.out.println(selectedNode.getLevel());
				if(selectedNode.getParent()==null)
					return;
				//ɾ��ָ���ڵ� �������ſ�ɾ��
				if(selectedNode!=null && selectedNode.getParent()!=null && selectedNode.getLevel()==3)
				{
					
					String nodeName=selectedNode.toString();					
					
					int updateType=0;
		            if(selectedNode.getParent().toString().contains("��ҵ"))
		            	updateType=ConstantsInfo.StockIndustry;
		            else if(selectedNode.getParent().toString().contains("����"))
		            	updateType=ConstantsInfo.StockConcept;
		            else if(selectedNode.getParent().toString().contains("����"))
		            	updateType=ConstantsInfo.StockRegional;
		            
		            System.out.println("ɾ���������:"+nodeName);
		            System.out.println(updateType);
		            //����
		            try {
						sid.deleteFromStockPlate(nodeName,updateType);
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
					stockModel.removeNodeFromParent(selectedNode);
				}
				else
					JOptionPane.showMessageDialog(jFrame,"�ýڵ㲻�ܱ�ɾ��","�Ƿ�����",JOptionPane.ERROR_MESSAGE);
			}
		});
		operationPane.add(deleteButton);
		
		editButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				TreePath selectedPath=stockTree.getSelectionPath();
				if(selectedPath!=null)
				{
					//��Ʊ�ɱ༭ѡ�еĽڵ㣬���಻�ɱ༭ ���ļ��ſɱ༭
					if(selectedPath.getPathCount()==4)			
					{	
						//selectedNode.get
						stockTree.getCellEditor().addCellEditorListener(new CellEditorAction());
						stockTree.startEditingAtPath(selectedPath);
					}
					else
						JOptionPane.showMessageDialog(jFrame,"�ýڵ㲻�ܱ༭","�Ƿ�����",JOptionPane.ERROR_MESSAGE);
						//System.out.println(selectedPath.toString());
				}
			
			}
		});
		operationPane.add(editButton);
		//stockTree.setSize(100, 400);
		//stockTreeSP=new JScrollPane(stockTree);
		jFrame.add(new JScrollPane(stockTree));
		jFrame.add(operationPane,BorderLayout.SOUTH);		
		
		jFrame.pack();
		jFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
		jFrame.setVisible(true);
	}
	
	private class CellEditorAction implements CellEditorListener{
		
	    public void editingCanceled(ChangeEvent e) {
	        System.out.println("==========�༭ȡ��");
	    }
	    public void editingStopped(ChangeEvent e) {
	    	
	    	Object selectNode = stockTree.getLastSelectedPathComponent();
	    	String oldNodeName=selectNode.toString();
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectNode;
            if(node.getLevel()==3)
            {
	            System.out.println("�ɽ������:"+oldNodeName);
	            CellEditor cellEditor = (CellEditor) e.getSource();
	            String newNodeName = (String) cellEditor.getCellEditorValue();
	            node.setUserObject(newNodeName);
	            System.out.println("�½������:"+newNodeName);
	            DefaultTreeModel model = (DefaultTreeModel) stockTree.getModel();
	            model.nodeStructureChanged(node);
	            
	            int updateType=0;
	            if(node.getParent().toString().contains("��ҵ"))
	            	updateType=ConstantsInfo.StockIndustry;
	            else if(node.getParent().toString().contains("����"))
	            	updateType=ConstantsInfo.StockConcept;
	            else if(node.getParent().toString().contains("����"))
	            	updateType=ConstantsInfo.StockRegional;
	            //����
	            try {
					sid.updateFromStockPlate(oldNodeName,newNodeName,updateType);
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
		        System.out.println("=======�༭����");
            }
	    }	  
	}
	 
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
		StockInfoManager sif=new StockInfoManager();
		PropertyConfigurator.configure("log4j.properties");
		listStockInfo=sid.getStockDaoList();
		
		sif.init();	

	}

}


