package test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.*;

import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.*;
import org.jfree.data.time.Month;
import org.jfree.data.time.Year;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

import common.ConstantsInfo;

import dao.DbConn;
import dao.StockDataDao;
import dao.StockPoint;
import dao.StockPointDao;

public class testJfreeChart extends ApplicationFrame  {

	static StockPointDao spDao;
	/**
	 * @param args
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	
	 public testJfreeChart(final String title) throws IOException, ClassNotFoundException, SQLException  
	   {  
	        super(title);  
	   
	        final JFreeChart chart = createChart();  
	        final ChartPanel chartPanel = new ChartPanel(chart);  
	      //  chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));  
	        setContentPane(chartPanel); 
	        String curPath=System.getProperty("user.dir");
			 curPath = curPath.replaceAll("\\\\", "/"); //ת�彫\ תΪ/
			 System.out.println(curPath);
	        saveAsFile(chart, curPath+"/StockImage/a.png"); 
	       
	    }  
	 
	 private JFreeChart createChart() throws IOException, ClassNotFoundException, SQLException  
	 {
		 
		 	       
	        List<StockPoint> sPointListDay = new ArrayList<StockPoint>();
	        List<StockPoint> sPointListWeek = new ArrayList<StockPoint>();
	        List<StockPoint> sPointListMonth = new ArrayList<StockPoint>();
	        sPointListDay=spDao.getLastNumPointStock("SH600000",ConstantsInfo.DayDataType,15);
	        sPointListWeek=spDao.getLastNumPointStock("SH600000",ConstantsInfo.WeekDataType,6);
	        sPointListMonth=spDao.getLastNumPointStock("SH600000",ConstantsInfo.MonthDataType,2);
	        // ��ͼ���ݼ�  
	        DefaultCategoryDataset dataSet = new DefaultCategoryDataset();  
	       
	        
	    	Iterator it; 
	    	
			 String series = "�ռ�����";  // seriesָ�ľ��Ǳ����������������
			 for(it = sPointListDay.iterator();it.hasNext();)
			 {
				 StockPoint sp = (StockPoint) it.next();
				 dataSet.addValue((float) sp.getExtremePrice(),series, sp.getExtremeDate().toString());  
			 }		

			 String seriesWeek = "�ܼ�����";  // seriesָ�ľ��Ǳ����������������
			 for(it = sPointListWeek.iterator();it.hasNext();)
			 {
				 StockPoint sp = (StockPoint) it.next();
				 dataSet.addValue((float) sp.getExtremePrice(),seriesWeek, sp.getExtremeDate().toString());  
			 }
			 
			 String seriesMonth = "�¼�����";  // seriesָ�ľ��Ǳ����������������
			 for(it = sPointListMonth.iterator();it.hasNext();)
			 {
				 StockPoint sp = (StockPoint) it.next();
				 dataSet.addValue((float) sp.getExtremePrice(),seriesMonth, sp.getExtremeDate().toString());  
			 }
	       	      
	        //�����createLineChart��ΪcreateLineChart3D�ͱ�Ϊ��3DЧ��������ͼ       
	        JFreeChart  chart = ChartFactory.createLineChart("ͼ�����", "X�����", "Y�����", dataSet,  
	                PlotOrientation.VERTICAL, // ���Ʒ���  
	                true, // ��ʾͼ��  
	                true, // ���ñ�׼������  
	                false // �Ƿ����ɳ�����  
	                );  
	         
	        Font titleFont = new Font("����", Font.BOLD, 15);
	        Font font = new Font("����", Font.ITALIC, 10);
	        Color bgColor=Color.WHITE;
			chart.getTitle().setFont(titleFont); // ���ñ�������  
	        chart.getLegend().setItemFont(font);// ����ͼ���������  
	        chart.setBackgroundPaint(bgColor);// ���ñ���ɫ   
	        //��ȡ��ͼ������  
	        CategoryPlot plot = chart.getCategoryPlot();  
	        plot.setBackgroundPaint(Color.LIGHT_GRAY); // ���û�ͼ������ɫ  
	        plot.setRangeGridlinePaint(Color.WHITE); // ����ˮƽ���򱳾�����ɫ  
	        plot.setRangeGridlinesVisible(true);// �����Ƿ���ʾˮƽ���򱳾���,Ĭ��ֵΪtrue  
	        plot.setDomainGridlinePaint(Color.WHITE); // ���ô�ֱ���򱳾�����ɫ  
	        plot.setDomainGridlinesVisible(true); // �����Ƿ���ʾ��ֱ���򱳾���,Ĭ��ֵΪfalse  
	  //      XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer)plot.getRenderer();
	  //      xylineandshaperenderer.setBaseShapesVisible(true);
	      
	         //�����
	        
	        CategoryAxis domainAxis = plot.getDomainAxis();     
	        domainAxis.setLabelFont(font); // ���ú�������  
	        domainAxis.setTickLabelFont(font);// ������������ֵ����  
	        
	        domainAxis.setLowerMargin(0.01);// ��߾� �߿����  
	        domainAxis.setUpperMargin(0.06);// �ұ߾� �߿����,��ֹ���ߵ�һ�����ݿ����������ᡣ  
	        domainAxis.setMaximumCategoryLabelLines(2);  
	         
	      
	        //����Y��
	        ValueAxis rangeAxis = plot.getRangeAxis();  
	        rangeAxis.setLabelFont(font);   
	        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());//Y����ʾ����  
	        rangeAxis.setAutoRangeMinimumSize(1);   //��С���  
	        rangeAxis.setUpperMargin(0.18);//�ϱ߾�,��ֹ����һ�����ݿ����������ᡣ     
	        rangeAxis.setLowerBound(0);   //��Сֵ��ʾ0  
	        rangeAxis.setAutoRange(false);   //���Զ�����Y������  
	        rangeAxis.setTickMarkStroke(new BasicStroke(1.6f));     // ���������Ǵ�С  
	        rangeAxis.setTickMarkPaint(Color.BLACK);     // ������������ɫ  
	  
	          
	     // ��ȡ���߶���  
	        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();  
	 	        
	        BasicStroke realLine = new BasicStroke(1.8f); // ����ʵ��  
	        // ��������  
	        float dashes[] = { 5.0f };   
	        BasicStroke brokenLine = new BasicStroke(2.2f, // ������ϸ  
	                BasicStroke.CAP_ROUND, // �˵���  
	                BasicStroke.JOIN_ROUND, // �۵���  
	                8f, dashes, 0.6f);   
	       
	        for (int i = 0; i < dataSet.getRowCount(); i++) {  
	            if (i % 2 == 0)  
	                renderer.setSeriesStroke(i, realLine); // ����ʵ�߻���  
	            else  
	                renderer.setSeriesStroke(i, brokenLine); // �������߻���  
	        }  
	        
	      
	          
	        plot.setNoDataMessage("�޶�Ӧ�����ݣ������²�ѯ��");  
	        plot.setNoDataMessageFont(titleFont);//����Ĵ�С  
	        plot.setNoDataMessagePaint(Color.RED);//������ɫ  
			return chart;
		}
	 
	 /**����Ϊ�ļ�*/     
	    public void saveAsFile(JFreeChart chart, String outputPath) {     
	        FileOutputStream out = null;     
	        try {     
	            File outFile = new File(outputPath);     
	            if (!outFile.getParentFile().exists()) {     
	                outFile.getParentFile().mkdirs();     
	            }     
	            out = new FileOutputStream(outputPath);     
	            // ����ΪPNG     
	            ChartUtilities.writeChartAsPNG(out, chart, 800, 600);     
	            // ����ΪJPEG     
	            // ChartUtilities.writeChartAsJPEG(out, chart, width, height);     
	            out.flush();     
	        } catch (FileNotFoundException e) {     
	            e.printStackTrace();     
	        } catch (IOException e) {     
	            e.printStackTrace();     
	        } finally {     
	            if (out != null) {     
	                try {     
	                    out.close();     
	                } catch (IOException e) {     
	                    // do nothing     
	                }     
	            }     
	        }     
	    }     
	      
        public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
	        
	        Connection stockDataConn = DbConn.getConnDB("stockConf/conn_point_db.ini");  
	        spDao =new StockPointDao(stockDataConn);
	        final testJfreeChart demo = new testJfreeChart("test"); 
	        
	       
	       demo.pack();  
	       RefineryUtilities.centerFrameOnScreen(demo);  
	       demo.setVisible(true); 
	       stockDataConn.close();
        }

	

}
