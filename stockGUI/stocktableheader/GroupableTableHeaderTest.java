package stockGUI.stocktableheader;

import java.awt.BorderLayout;
import java.awt.HeadlessException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * �ϲ���ͷ����
 * 
 * @author Brad.Wu
 * @version 1.0
 */
@SuppressWarnings("serial")
public class GroupableTableHeaderTest extends JFrame {

	GroupableTableHeader tableHeader;
    /**
     * @param args
     */
    public static void main(String[] args) {
        GroupableTableHeaderTest test = new GroupableTableHeaderTest();
        test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      test.setSize(800, 600);
      //  test.setExtendedState(JFrame.MAXIMIZED_BOTH);
        test.setVisible(true);
    }

    private DefaultTableModel tableModel = new DefaultTableModel() {

        public int getColumnCount() {
       // 	return 6;
            return 17;
        }

        public int getRowCount() {
            return 4;
        }

    };

    private JTable table = new JTable(tableModel);

    private JScrollPane scroll = new JScrollPane(table);

    /**
     * @throws HeadlessException
     */
    
   
    public GroupableTableHeaderTest() throws HeadlessException {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        GroupableTableHeader tableHeader = new GroupableTableHeader();
        table.setTableHeader(tableHeader);
        
      
        DefaultGroup group = new DefaultGroup();
        group.setRow(0);
        group.setColumn(0);
        group.setColumnSpan(9);
        group.setHeaderValue("ǰһ����");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(0);
        group.setColumn(9);
        group.setColumnSpan(8);
        group.setHeaderValue("��ǰ");
        tableHeader.addGroup(group);
        
        
        group = new DefaultGroup();
        group.setRow(1);
        group.setColumn(0);
        group.setColumnSpan(2);
        group.setHeaderValue("���");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(1);
        group.setColumn(2);
        group.setColumnSpan(2);
        group.setHeaderValue("�յ�");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(1);
        group.setColumn(4);
        group.setColumnSpan(2);
        group.setHeaderValue("����");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(1);
        group.setColumn(6);
        group.setColumnSpan(3);
        group.setHeaderValue("����");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(1);
        group.setColumn(9);
      //  group.setColumnSpan(1);
        group.setHeaderValue("����");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(1);
        group.setColumn(10);
        group.setColumnSpan(2);
        group.setHeaderValue("���");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(1);
        group.setColumn(12);
        group.setColumnSpan(2);
        group.setHeaderValue("��ǰ");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(1);
        group.setColumn(14);
        group.setColumnSpan(3);
        group.setHeaderValue("����");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(0);
        group.setHeaderValue("ʱ��");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(1);
        group.setHeaderValue("��λ");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(2);
        group.setHeaderValue("ʱ��");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(3);
        group.setHeaderValue("��λ");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(4);
        group.setHeaderValue("ʱ��");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(5);
        group.setHeaderValue("��λ");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(6);
        group.setHeaderValue("ʱ��");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(7);
        group.setHeaderValue("�ռ�");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(8);
        group.setHeaderValue("б��");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(9);
        group.setHeaderValue("����");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(10);
        group.setHeaderValue("ʱ��");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(11);
        group.setHeaderValue("��λ");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(12);
        group.setHeaderValue("ʱ��");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(13);
        group.setHeaderValue("��λ");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(14);
        group.setHeaderValue("ʱ��");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(15);
        group.setHeaderValue("�ռ�");
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(2);
        group.setColumn(16);
        group.setHeaderValue("б��");
        tableHeader.addGroup(group);
        
    
        /*
        DefaultGroup group = new DefaultGroup();
        group.setRow(0);
        group.setRowSpan(2);
        group.setColumn(0);
        group.setHeaderValue("¥��");
        tableHeader.addGroup(group);

        group = new DefaultGroup();
        group.setRow(0);
        group.setRowSpan(2);
        group.setColumn(1);
        group.setHeaderValue("ˮƽ/��ֱϵ��");
        tableHeader.addGroup(group);

        
        group = new DefaultGroup();
        group.setRow(0);
        group.setColumn(2);
        group.setColumnSpan(2);
        group.setHeaderValue("A & B");
        tableHeader.addGroup(group);

        group = new DefaultGroup();
        group.setRow(1);
        group.setColumn(2);
        group.setHeaderValue("Column A");
        tableHeader.addGroup(group);

        group = new DefaultGroup();
        group.setRow(1);
        group.setColumn(3);
        group.setHeaderValue("Column B");
        tableHeader.addGroup(group);

        group = new DefaultGroup();
        group.setRow(0);
        group.setColumn(4);
        group.setColumnSpan(2);
        group.setHeaderValue("C & D");
        tableHeader.addGroup(group);

        group = new DefaultGroup();
        group.setRow(1);
        group.setColumn(4);
        group.setHeaderValue("Column C");
        tableHeader.addGroup(group);

        group = new DefaultGroup();
        group.setRow(1);
        group.setColumn(5);
        group.setHeaderValue("Column D");
        tableHeader.addGroup(group);
*/
        getContentPane().add(scroll, BorderLayout.CENTER);
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
   //    group.setRowSpan(2);
        group.setHeaderValue("ʱ��");//λ�ڵ�2�е�2�У�ռ2��
        tableHeader.addGroup(group);
        
        group = new DefaultGroup();
        group.setRow(1);
        group.setColumn(4);
   //     group.setRowSpan(3);
        group.setHeaderValue("��λ");//λ�ڵ�2�е�2�У�ռ2��
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

    
}
