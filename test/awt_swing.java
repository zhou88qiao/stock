package test;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.CheckboxMenuItem;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.Panel;
import java.awt.PopupMenu;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class awt_swing {

	private Frame f=new Frame("����");
	private TextArea ta=new TextArea(10,100);
	private Button b1=new Button("��ť1");
	private Button b2=new Button("��ť2");
	private MenuBar mb=new MenuBar();
	Menu file =new Menu("�ļ�");
	Menu edit =new Menu("�༭");
	PopupMenu pop=new PopupMenu();
	MenuItem newItem=new MenuItem("�½�");
	MenuItem saveItem=new MenuItem("����");
	MenuItem exitItem=new MenuItem("�˳�",new MenuShortcut(KeyEvent.VK_X));
	CheckboxMenuItem autoWrap=new CheckboxMenuItem("�Զ�����");
	MenuItem copyItem=new MenuItem("����");
	MenuItem pasteItem=new MenuItem("ճ��");
	Menu format=new Menu("��ʽ");
	MenuItem commentItem=new MenuItem("ע��",new MenuShortcut(KeyEvent.VK_SLASH,true));
	MenuItem cancelItem=new MenuItem("ȡ��ע��");
	public void init()
	{
		FirstListener f1=new FirstListener();
		b1.addActionListener(f1);
		b1.addActionListener(new SecondListener());
		b2.addActionListener(f1);
		
		final Panel p=new Panel();
		p.add(b1);
		p.add(b2);
	//	f.addWindowListener(new MyListener());//�ڲ���
		
		//�������ڲ������ʽ�����˵�������
		ActionListener menuListener=new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				String cmd=e.getActionCommand();
				ta.append("����"+cmd+"�˵�"+"\n");
				if(cmd.equals("�˳�"))
				{
					System.exit(0);
				}
				
			}
			
		};
		
		////�������ڲ������ʽ�����˵�������
		saveItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				ta.append("����"+"save�˵�"+"\n");	
			}
		}); 
		
		commentItem.addActionListener(menuListener);
		exitItem.addActionListener(menuListener);
		newItem.addActionListener(menuListener);
		file.add(newItem);
		file.add(saveItem);
		file.add(exitItem);
		
		edit.add(autoWrap);
		edit.addSeparator();
		edit.add(copyItem);
		edit.add(pasteItem);
		//�Ҽ��˵�
		pop.add(autoWrap);
		pop.addSeparator();
		pop.add(copyItem);
		pop.add(pasteItem);
		
		format.add(commentItem);
		format.add(cancelItem);
		
		
		edit.add(new MenuItem("-"));
		//��format��ϵ�edit�˵� �����˵�
		edit.add(format);
		
		pop.add(new MenuItem("-"));
		//��format��ϵ�edit�˵� �����˵�
		pop.add(format);
		
		p.setPreferredSize(new Dimension(300,160));
		p.add(pop);
		

		
		
		//��file edit�˵���ӵ�mb�˵�����
		mb.add(file);
		mb.add(edit);
		//Ϊf�������ò˵���
		f.setMenuBar(mb);
		
		////�������ڲ������ʽ�����˵�������
		f.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.out.println("�û��رմ���");
				System.exit(0);
				
			}
		}); 
		
		////�������ڲ������ʽ�����˵�������
		f.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.out.println("�û��رմ���");
				System.exit(0);
				
			}
		}); 
		
		f.add(ta);
		f.add(p,BorderLayout.SOUTH);
		f.pack();
		f.setVisible(true);	
		
	}
	class MyListener extends WindowAdapter
	{
		public void windowClosing(WindowEvent e)
		{
			System.out.println("�û��رմ���");
			System.exit(0);
			
		}
	}
	class FirstListener implements ActionListener
	{

		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			ta.append("��һ���¼����������������¼�Դ��"+e.getActionCommand()+"\n");
		}
		
	}
	
	class SecondListener implements ActionListener
	{

		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			ta.append("������"+e.getActionCommand()+"��ť\n");
		}
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		awt_swing as=new awt_swing();
		as.init();
	}

}
