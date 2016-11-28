package excel.rw;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import common.ConstantsInfo;

import dao.DbConn;
import dao.StockBaseDao;
import dao.StockBaseFace;
import dao.StockConcept;
import dao.StockDataDao;
import dao.StockIndustry;
import dao.StockInformation;
import dao.StockInformationDao;
import dao.StockPointDao;
import dao.StockSingle;

public class ExcelReader {
	private POIFSFileSystem fs;
	private HSSFWorkbook wb;
	private XSSFWorkbook xb;
	private HSSFSheet sheet;
	private Sheet xsheet;
	private HSSFRow row;
	private Row xrow;

	private StockDataDao sdDao;
	private StockPointDao spDao;
	private StockBaseDao sbDao;

	public ExcelReader(Connection stockBaseConn) {
		this.sbDao = new StockBaseDao(stockBaseConn);

	}

	static StockInformationDao sd = new StockInformationDao();

	static int insetNum = 0;
	static int insetNoNum = 0;

	public ExcelReader(StockBaseDao sbDao) {
		this.sbDao = sbDao;
		// this.sdDao = sdDao;
		// this.spDao = spDao;
	}

	/**
	 * ��ȡExcel����ͷ������
	 * 
	 * @param InputStream
	 * @return String ��ͷ���ݵ�����
	 */
	public String[] readExcelTitle(InputStream is) {
		try {
			fs = new POIFSFileSystem(is);
			wb = new HSSFWorkbook(fs);
		} catch (IOException e) {
			e.printStackTrace();
		}
		sheet = wb.getSheetAt(0);
		row = sheet.getRow(0);
		// ����������
		int colNum = row.getPhysicalNumberOfCells();
		System.out.println("colNum:" + colNum);
		String[] title = new String[colNum];
		for (int i = 0; i < colNum; i++) {
			// title[i] = getStringCellValue(row.getCell((short) i));
			title[i] = getCellFormatValue(row.getCell((short) i));
		}
		return title;
	}

	/**
	 * ��ȡ03Excel��������
	 * 
	 * @param InputStream
	 * @return Map ������Ԫ���������ݵ�Map����
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public Map<Integer, String> readExcelContent_for_stock(InputStream is)
			throws IOException, ClassNotFoundException, SQLException {

		String[] stock = new String[5];// ��ȡ2��3��4��
		HSSFCell cell;

		Map<Integer, String> content = new HashMap<Integer, String>();
		String str = "";
		try {
			fs = new POIFSFileSystem(is);
			wb = new HSSFWorkbook(fs);
		} catch (IOException e) {
			e.printStackTrace();
		}
		sheet = wb.getSheetAt(0);
		// �õ�������
		int rowNum = sheet.getLastRowNum();
		row = sheet.getRow(0);
		System.out.println("������" + rowNum);
		int colNum = row.getPhysicalNumberOfCells();// ����
		// ��������Ӧ�ôӵڶ��п�ʼ,��һ��Ϊ��ͷ�ı���

		// rowNum=20;
		for (int i = 1; i <= rowNum; i++) {
			row = sheet.getRow(i);
			// int j = 0;
			// while (j < colNum) {
			int j = 1;// ��ȡ2��3��4����
			while (j < 4) {
				// ÿ����Ԫ�������������"-"�ָ���Ժ���Ҫʱ��String���replace()������ԭ����
				// Ҳ���Խ�ÿ����Ԫ����������õ�һ��javabean�������У���ʱ��Ҫ�½�һ��javabean
				// str += getStringCellValue(row.getCell((short) j)).trim() +
				// "-";
				// cell = row.getCell((short) j);
				str += getCellFormatValue(row.getCell((short) j)).trim()
						+ "    ";

				stock[j] = getCellFormatValue(row.getCell((short) j)).trim()
						.trim();
				if (j == 2) {
					switch (stock[j].length()) {
					case 6:
					default:
						if (stock[j].charAt(0) == '6')// ����A��
						{
							stock[4] = "sh" + stock[j];
							stock[0] = "����A��";
						} else if (stock[j].charAt(0) == '9') {
							stock[4] = "sh" + stock[j];
							stock[0] = "����B��";
						} else if (stock[j].charAt(0) == '2') {
							stock[4] = "sz" + stock[j];
							stock[0] = "����B��";
						} else if (stock[j].charAt(0) == '3') {
							stock[4] = "sz" + stock[j];
							stock[0] = "��ҵ��";
						} else {
							stock[4] = "sz" + stock[j];
							stock[0] = "����B��";
						}
						break;
					case 3:
						stock[4] = "sz000" + stock[j];
						stock[0] = "����A��";
						break;
					case 4:
						if (stock[j].charAt(0) == '2')
							stock[0] = "��С��ҵ��";
						else
							stock[0] = "����A��";
						stock[4] = "sz00" + stock[j];
						break;
					case 2:
						stock[4] = "sz0000" + stock[j];
						stock[0] = "����A��";
						break;
					case 1:
						stock[4] = "sz00000" + stock[j];
						stock[0] = "����A��";
						break;
					case 5:
						stock[4] = "sz0" + stock[j];
						stock[0] = "����A��";
						break;
					}

					System.out.println("length:" + stock[4].length() + "stock:"
							+ stock[4]);
					System.out.println("length0:" + stock[0].length()
							+ "stock0:" + stock[0]);
				}

				// System.out.println("stock:"+stock[j]);
				j++;
			}
			stockInsertMysql(sd, stock);
			content.put(i, str);
			// System.out.println("stock:"+str);
			str = "";
		}
		return content;
	}

	/**
	 * ��ȡ03Excel ��Ʊ��ҵ�������� //��ȡ��Ʊ��ҵһ����������
	 */
	public int readIndustry() throws IOException, ClassNotFoundException,
			SQLException {

		String[] stock = new String[14];
		HSSFCell cell;

		String curPath = System.getProperty("user.dir");
		curPath = curPath.replaceAll("\\\\", "/"); // ת�彫\ תΪ/
		File file = new File(curPath + "/StockBaseData/1Industry.xls");
		if (file == null || !file.exists()) {
			System.out.println("no xlsx");
			return -1;
		}

		InputStream is = new FileInputStream("StockBaseData/1Industry.xls");
		if (is == null) {
			return -1;
		}

		// ���һ��������ҵ��
		sbDao.truncateStockFirstIndustry();
		sbDao.truncateStockSecondIndustry();
		sbDao.truncateStockThirdIndustry();

		String str = "";
		try {
			fs = new POIFSFileSystem(is);
			wb = new HSSFWorkbook(fs);
		} catch (IOException e) {
			e.printStackTrace();
		}
		sheet = wb.getSheetAt(0);
		// �õ�������
		int rowNum = sheet.getLastRowNum();
		row = sheet.getRow(0);
		System.out.println("������" + rowNum);
		int colNum = row.getPhysicalNumberOfCells();// ����
		System.out.println("������" + colNum);

		// ��������Ӧ�ôӵڶ��п�ʼ,��һ��Ϊ��ͷ�ı���

		String beforeFirstCode = "";
		String beforeSecondCode = "";
		String beforeFirstName = "";
		String beforeSecondName = "";
		int firstIndustryNum = 0;
		int secondIndustryNum = 0;
		int thirdIndustryNum = 0;

		// ��ȡһ����ҵ
		for (int i = 2; i <= rowNum; i++) {
			row = sheet.getRow(i);

			int j = 0;// ��ȡ0,1,2��3��4,5����

			// 11111 ��ȡ0��1�� һ������ һ������
			colNum = 2;
			while (j < colNum) {
				stock[j] = getCellFormatValue(row.getCell((short) j)).trim();
				// System.out.println("stock:"+stock[j]);
				j++;
			}

			if (stock[0].equals(""))
				continue;
			firstIndustryNum++;
			System.out.println("first industry code:" + stock[0] + " name:"
					+ stock[1]);
			sbDao.insertStockFirstIndustry(stock[0], stock[1]);
		}

		// ��ȡ������ҵ
		for (int i = 2; i <= rowNum; i++) {
			row = sheet.getRow(i);

			int j = 0;// ��ȡ0,1,2��3��4,5����

			// 2222 ��ȡ0��,2,3�� һ������ �������� ��������
			colNum = 4;
			for (j = 0; j < colNum; j++) {
				stock[j] = getCellFormatValue(row.getCell((short) j)).trim();
				if (j == 0 && stock[0].equals("")) {
					stock[0] = beforeFirstCode;
				} else
					beforeFirstCode = stock[0];
				if (j == 1 && stock[1].equals("")) {
					stock[1] = beforeFirstName;
				} else
					beforeFirstName = stock[1];
			}

			if (stock[2].equals(""))
				continue;
			secondIndustryNum++;
			System.out.println("first industry code:" + stock[0] + "first name"
					+ stock[1] + "second code:" + stock[2] + "seconde name:"
					+ stock[3]);
			sbDao.insertStockSecondIndustry(stock[2], stock[3], stock[0],
					stock[1]);
		}

		// ��ȡ������ҵ
		for (int i = 2; i <= rowNum; i++) {
			row = sheet.getRow(i);
			int j = 0;// ��ȡ0,1,2��3��4,5����
			colNum = 12;
			for (j = 0; j < colNum; j++) {
				stock[j] = "";
				stock[j] = getCellFormatValue(row.getCell((short) j)).trim();
				if (j == 0 && stock[0].equals("")) // һ��
				{
					stock[0] = beforeFirstCode;
				} else
					beforeFirstCode = stock[0];

				if (j == 1 && stock[1].equals("")) {
					stock[1] = beforeFirstName;
				} else
					beforeFirstName = stock[1];
				if (j == 2 && stock[2].equals(""))// ����
				{
					stock[2] = beforeSecondCode;
				} else
					beforeSecondCode = stock[2];
				if (j == 3 && stock[3].equals("")) {
					stock[3] = beforeSecondName;
				} else
					beforeSecondName = stock[3];
			}
			thirdIndustryNum++;
			// System.out.println(stock[5]);
			System.out.println("first industry code:" + stock[0]
					+ "first name:" + stock[1] + "second code:" + stock[2]
					+ "second name:" + stock[3] + "third code:" + stock[4]
					+ "third name:" + stock[5] + "baseExcept" + stock[6]);
			sbDao.insertStockThirdIndustry(stock[4], stock[5], stock[2],
					stock[3], stock[0], stock[1], stock[6], stock[7], stock[8],
					stock[9], stock[10], stock[11]);
			str = "";
		}
		is.close();
		System.out.println("firstIndustryNum:" + firstIndustryNum);
		System.out.println("secondIndustryNum:" + secondIndustryNum);
		System.out.println("thirdIndustryNum:" + thirdIndustryNum);
		return 0;

	}

	/**
	 * ��ȡ03Excel ��Ʊ��ҵ�������� //��ȡ��Ʊ��ҵһ����������
	 */
	public int read9Stock_GongJiJin() throws IOException,
			ClassNotFoundException, SQLException {

		String[] stock = new String[10];
		HSSFCell cell;
		XSSFWorkbook xb = null;
		Sheet xsheet;
		String str = "";

		String curPath = System.getProperty("user.dir");
		curPath = curPath.replaceAll("\\\\", "/"); // ת�彫\ תΪ/
		File file = new File(curPath + "/StockBaseData/9Stock_GongJiJin.xlsx");
		if (file == null || !file.exists()) {
			System.out.println("no xlsx");
			return -1;
		}

		InputStream is = new FileInputStream(
				"StockBaseData/9Stock_GongJiJin.xlsx");
		if (is == null) {
			return -1;
		}

		try {

			xb = new XSSFWorkbook(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		xsheet = xb.getSheetAt(0);
		// �õ�������
		int rowNum = xsheet.getLastRowNum();
		xrow = xsheet.getRow(0);
		System.out.println("������" + rowNum);
		int colNum = xrow.getPhysicalNumberOfCells();// ����
		System.out.println("������" + colNum);
		// ��������Ӧ�ôӵڶ��п�ʼ,��һ��Ϊ��ͷ�ı���

		// rowNum=40;

		int Num = 0;

		for (int i = 1; i <= rowNum; i++) {
			xrow = xsheet.getRow(i);

			int j = 0;// ��ȡ0,1,2��
			String fullId = "";
			colNum = 4;
			for (j = 0; j < colNum; j++) {

				stock[j] = "";

				if (xrow.getCell(j) == null) {
					stock[j] = "";
				} else {
					xrow.getCell(j).setCellType(Cell.CELL_TYPE_STRING);
					stock[j] = xrow.getCell(j).getStringCellValue();
				}

				if (stock[0].equals(""))
					continue;

			}

			if (stock[0].startsWith("6"))
				fullId = "SH" + stock[0];
			else
				fullId = "SZ" + stock[0];

			System.out.println("stock code:" + fullId + " price:" + stock[3]);
			sbDao.updateStock_baseyearinfo_GongJiJin(fullId, Float.parseFloat(stock[3]));

			Num++;

			str = "";

		}
		is.close();
		System.out.println("Num:" + Num);
		return 0;

	}

	public int read10Stock_LiRun() throws IOException, ClassNotFoundException,
			SQLException {
		String[] stock = new String[10];
		HSSFCell cell;
		XSSFWorkbook xb = null;
		Sheet xsheet;
		String str = "";

		String curPath = System.getProperty("user.dir");
		curPath = curPath.replaceAll("\\\\", "/"); // ת�彫\ תΪ/
		File file = new File(curPath + "/StockBaseData/10Stock_LiRun.xlsx");
		if (file == null || !file.exists()) {
			System.out.println("no xlsx");
			return -1;
		}

		InputStream is = new FileInputStream("StockBaseData/10Stock_LiRun.xlsx");
		if (is == null) {
			return -1;
		}

		try {

			xb = new XSSFWorkbook(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		xsheet = xb.getSheetAt(0);
		// �õ�������
		int rowNum = xsheet.getLastRowNum();
		xrow = xsheet.getRow(0);
		System.out.println("������" + rowNum);
		int colNum = xrow.getPhysicalNumberOfCells();// ����
		System.out.println("������" + colNum);
		// ��������Ӧ�ôӵڶ��п�ʼ,��һ��Ϊ��ͷ�ı���

		// rowNum=40;

		int Num = 0;

		for (int i = 1; i <= rowNum; i++) {
			xrow = xsheet.getRow(i);

			int j = 0;// ��ȡ0,1,2��
			String fullId = "";
			colNum = 4;
			for (j = 0; j < colNum; j++) {

				stock[j] = "";

				if (xrow.getCell(j) == null) {
					stock[j] = "";
				} else {
					xrow.getCell(j).setCellType(Cell.CELL_TYPE_STRING);
					stock[j] = xrow.getCell(j).getStringCellValue();
				}

				if (stock[0].equals(""))
					continue;
			}

			if (stock[0].startsWith("6"))
				fullId = "SH" + stock[0];
			else
				fullId = "SZ" + stock[0];

			System.out.println("stock code:" + fullId + " price:" + stock[3]);
			sbDao.updateStock_baseyearinfo_lirun(fullId, Float
					.parseFloat(stock[3]));

			Num++;

			str = "";

		}

		is.close();
		System.out.println("Num:" + Num);
		return 0;

	}

	public int read11Stock_Yejiyugao() throws IOException,
			ClassNotFoundException, SQLException {

		String[] stock = new String[10];
		HSSFCell cell;
		XSSFWorkbook xb = null;
		Sheet xsheet;
		String str = "";

		String curPath = System.getProperty("user.dir");
		curPath = curPath.replaceAll("\\\\", "/"); // ת�彫\ תΪ/
		File file = new File(curPath + "/StockBaseData/11Stock_YeJiYuGao.xlsx");
		if (file == null || !file.exists()) {
			System.out.println("no xlsx");
			return -1;
		}

		InputStream is = new FileInputStream(
				"StockBaseData/11Stock_YeJiYuGao.xlsx");
		if (is == null) {
			return -1;
		}

		try {
			xb = new XSSFWorkbook(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		xsheet = xb.getSheetAt(0);
		// �õ�������
		int rowNum = xsheet.getLastRowNum();
		xrow = xsheet.getRow(0);
		System.out.println("������" + rowNum);
		int colNum = xrow.getPhysicalNumberOfCells();// ����
		System.out.println("������" + colNum);
		// ��������Ӧ�ôӵڶ��п�ʼ,��һ��Ϊ��ͷ�ı���
		// rowNum=40;
		int Num = 0;

		for (int i = 1; i <= rowNum; i++) {
			xrow = xsheet.getRow(i);

			int j = 0;// ��ȡ0,1,2��
			String fullId = "";
			colNum = 6;
			for (j = 1; j < colNum; j++) {

				stock[j] = "";

				if (xrow.getCell(j) == null) {
					stock[j] = "";
				} else {
					xrow.getCell(j).setCellType(Cell.CELL_TYPE_STRING);
					stock[j] = xrow.getCell(j).getStringCellValue();
				}

				if (stock[1].equals(""))
					continue;

			}

			if (stock[1].startsWith("6"))
				fullId = "SH" + stock[1];
			else
				fullId = "SZ" + stock[1];

			System.out.println("stock code:" + fullId + " content:" + stock[5]);
			sbDao.updateStock_baseyearinfo_yuGaoContent(fullId, stock[5]);
			Num++;
			str = "";
		}

		is.close();
		return 0;
	}

	public int read12Stock_PiLouShiJian() throws IOException,
			ClassNotFoundException, SQLException {

		String[] stock = new String[10];
		HSSFCell cell;
		XSSFWorkbook xb = null;
		Sheet xsheet;
		String str = "";

		String curPath = System.getProperty("user.dir");
		curPath = curPath.replaceAll("\\\\", "/"); // ת�彫\ תΪ/
		File file = new File(curPath + "/StockBaseData/12Stock_PiLouShiJian.xlsx");
		if (file == null || !file.exists()) {
			System.out.println("no xlsx");
			return -1;
		}

		InputStream is = new FileInputStream(
				"StockBaseData/12Stock_PiLouShiJian.xlsx");
		if (is == null) {
			return -1;
		}

		try {
			xb = new XSSFWorkbook(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		xsheet = xb.getSheetAt(0);
		// �õ�������
		int rowNum = xsheet.getLastRowNum();
		xrow = xsheet.getRow(0);
		System.out.println("������" + rowNum);
		int colNum = xrow.getPhysicalNumberOfCells();// ����
		System.out.println("������" + colNum);
		// ��������Ӧ�ôӵڶ��п�ʼ,��һ��Ϊ��ͷ�ı���
		// rowNum=40;
		int Num = 0;

		for (int i = 1; i <= rowNum; i++) {
			xrow = xsheet.getRow(i);

			int j = 0;// ��ȡ0,1,2��
			String fullId = "";
			colNum = 4;
			for (j = 1; j < colNum; j++) {

				stock[j] = "";

				if (xrow.getCell(j) == null) {
					stock[j] = "";
				} else {
					xrow.getCell(j).setCellType(Cell.CELL_TYPE_STRING);
					stock[j] = xrow.getCell(j).getStringCellValue();
				}

				if (stock[1].equals(""))
					continue;

			}

			if (stock[1].startsWith("6"))
				fullId = "SH" + stock[1];
			else
				fullId = "SZ" + stock[1];

			System.out.println("stock code:" + fullId + " content:" + stock[3]);
			sbDao.updateStock_baseyearinfo_piLouDate(fullId, stock[3]);
			Num++;
			str = "";
		}

		is.close();
		return 0;
	}

	// ��ȡ��Ʊ�Ƿ�����
	public int readTwoRong() throws IOException, ClassNotFoundException,
			SQLException {

		String[] stock = new String[4];
		HSSFCell cell;
		XSSFWorkbook xb = null;
		Sheet xsheet;
		String str = "";

		String curPath = System.getProperty("user.dir");
		curPath = curPath.replaceAll("\\\\", "/"); // ת�彫\ תΪ/
		File file = new File(curPath + "/StockBaseData/5TwoRong.xlsx");
		if (file == null || !file.exists()) {
			System.out.println("no xlsx");
			return -1;
		}

		InputStream is = new FileInputStream("StockBaseData/5TwoRong.xlsx");
		if (is == null) {
			return -1;
		}
		// ���
		sbDao.truncateStockTwoRong();

		try {
			// fs = new POIFSFileSystem(is);
			xb = new XSSFWorkbook(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		xsheet = xb.getSheetAt(0);
		// �õ�������
		int rowNum = xsheet.getLastRowNum();
		xrow = xsheet.getRow(0);
		System.out.println("������" + rowNum);
		int colNum = xrow.getPhysicalNumberOfCells();// ����
		System.out.println("������" + colNum);
		// ��������Ӧ�ôӵڶ��п�ʼ,��һ��Ϊ��ͷ�ı���

		// rowNum=20;

		int Num = 0;
		String fullId = "";
		for (int i = 1; i < rowNum; i++) {
			xrow = xsheet.getRow(i);

			int j = 0;// ��ȡ0,1,2����

			for (j = 0; j < colNum; j++) {
				xrow.getCell(j).setCellType(Cell.CELL_TYPE_STRING);
				// stock[j]=getCellFormatValue(xrow.getCell((short) j))
				stock[j] = xrow.getCell(j).getStringCellValue();
			}
			// ��6��ͷ
			if (stock[0].startsWith("6"))
				fullId = "SH" + stock[0];
			else
				fullId = "SZ" + stock[0];
			Num++;
			System.out.println("stock code:" + fullId + " name:" + stock[1]);

			sbDao.insertStockTwoRong(fullId, stock[1], 1);

			str = "";
		}
		is.close();
		System.out.println("Num:" + Num);

		return 0;

	}

	// ������Ʊ����Ԥ��
	// �м��п��еģ�Ϊ�յ�
	public int readstock_baseExpect() throws IOException,
			ClassNotFoundException, SQLException {

		String[] stock = new String[14];
		HSSFCell cell;
		XSSFWorkbook xb = null;
		Sheet xsheet;
		String str = "";

		String curPath = System.getProperty("user.dir");
		curPath = curPath.replaceAll("\\\\", "/"); // ת�彫\ תΪ/
		File file = new File(curPath + "/StockBaseData/6Stock_BaseInfo.xlsx");
		if (file == null || !file.exists()) {
			System.out.println("no xlsx");
			return -1;
		}

		InputStream is = new FileInputStream(
				"StockBaseData/6Stock_BaseInfo.xlsx");
		if (is == null) {
			return -1;
		}

		// ��ձ�
		sbDao.truncateStockBaseInfo();

		try {

			xb = new XSSFWorkbook(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		xsheet = xb.getSheetAt(0);

		// �õ�������
		int rowNum = xsheet.getLastRowNum();
		xrow = xsheet.getRow(0);
		System.out.println("������" + rowNum);
		int colNum = xrow.getPhysicalNumberOfCells();// ����
		System.out.println("������" + colNum);
		// ��������Ӧ�ôӵڶ��п�ʼ,��һ��Ϊ��ͷ�ı���

		// rowNum=40;

		int Num = 0;
		String fullId = "";
		for (int i = 0; i <= rowNum; i++) {
			xrow = xsheet.getRow(i);

			int j = 0;// ��ȡ0,1,2��

			colNum = 9;
			for (j = 0; j < colNum; j++) {
				stock[j] = "";
				if (xrow.getCell(j) == null) {
					stock[j] = "";
					// break;
				} else {
					xrow.getCell(j).setCellType(Cell.CELL_TYPE_STRING);
					// stock[j]=getCellFormatValue(xrow.getCell((short) j))
					stock[j] = xrow.getCell(j).getStringCellValue();
				}
			}

			if (stock[1] == null)
				continue;

			Num++;
			if (stock[1].equals("SZ002086")) {
				System.out.println("stock code:" + stock[1] + " stock name:"
						+ stock[2] + "baseexpect:" + stock[3]);
				System.out.println("stock pot:" + stock[7] + " stock fau:"
						+ stock[8]);
			}
			// sbDao.addStockBaseFace(stock[1],stock[3]);
			if (stock[2] != null && !stock[2].equals("")) {
				// System.out.println("baseexpect:"+stock[3]);
				sbDao.addStockBaseInfo(stock[1], stock[3], stock[4], stock[5],
						stock[6], stock[7], stock[8]);
			}

			str = "";
			stock[1] = null;

		}
		is.close();
		System.out.println("Num:" + Num);
		return 0;
	}

	// �ڻ���Ʒ��Ӧ��Ʊ����Ʒ
	public int readStockToFeatures() throws IOException,
			ClassNotFoundException, SQLException {

		String[] stock = new String[14];
		HSSFCell cell;
		XSSFWorkbook xb = null;
		Sheet xsheet;
		String str = "";

		String curPath = System.getProperty("user.dir");
		curPath = curPath.replaceAll("\\\\", "/"); // ת�彫\ תΪ/
		File file = new File(curPath + "/StockBaseData/8ExMarket-to-stock.xlsx");
		if (file == null || !file.exists()) {
			System.out.println("no xlsx");
			return -1;
		}

		InputStream is = new FileInputStream(
				"StockBaseData/8ExMarket-to-stock.xlsx");
		if (is == null) {
			return -1;
		}

		// ��ձ�
		sbDao.truncateStockToFutures();

		try {

			xb = new XSSFWorkbook(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		xsheet = xb.getSheetAt(0);
		// �õ�������
		int rowNum = xsheet.getLastRowNum();
		xrow = xsheet.getRow(0);
		System.out.println("������" + rowNum);
		int colNum = xrow.getPhysicalNumberOfCells();// ����
		System.out.println("������" + colNum);
		// ��������Ӧ�ôӵڶ��п�ʼ,��һ��Ϊ��ͷ�ı���

		// rowNum=40;

		int Num = 0;

		String beforeFirstCode = "";

		String beforeFirstName = "";

		for (int i = 1; i <= rowNum; i++) {
			xrow = xsheet.getRow(i);

			int j = 0;// ��ȡ0,1,2��

			colNum = 11;
			for (j = 0; j < colNum; j++) {

				stock[j] = "";

				if (xrow.getCell(j) == null) {
					stock[j] = "";
				} else {
					xrow.getCell(j).setCellType(Cell.CELL_TYPE_STRING);
					stock[j] = xrow.getCell(j).getStringCellValue();
				}


			}

			Num++;

			// ȥ���ո���&
			stock[0] = stock[0].replaceAll(" ", "");
			stock[0] = stock[0].replaceAll("&", "");

			System.out.println("code:" + stock[0]
					+ "name:" + stock[1] + " Fcode:"
					+ stock[2] + "Fname:" + stock[3] + "baseexpect:"
					+ stock[4]+"faucet:"+stock[9]);

			sbDao.addStockToFutures(stock[0], stock[1], stock[2], stock[3],stock[4], stock[5], stock[6], stock[7],stock[8], stock[9]);

			for (int count = 4; count < 10; count++)
				stock[count] = "";

			str = "";

		}
		is.close();
		System.out.println("Num:" + Num);

		return 0;
	}

	// һ����ҵ�¶�Ӧ�ĸ���
	public int readFirstIndustry_To_Concept() throws IOException,
			ClassNotFoundException, SQLException {

		String[] stock = new String[10];
		HSSFCell cell;
		XSSFWorkbook xb = null;
		Sheet xsheet;
		String str = "";

		String curPath = System.getProperty("user.dir");
		curPath = curPath.replaceAll("\\\\", "/"); // ת�彫\ תΪ/
		File file = new File(curPath
				+ "/StockBaseData/2FirstIndustry-to-Concept.xlsx");
		if (file == null || !file.exists()) {
			System.out.println("no xlsx");
			return -1;
		}

		InputStream is = new FileInputStream(
				"StockBaseData/2FirstIndustry-to-Concept.xlsx");
		if (is == null) {
			return -1;
		}

		// ��ձ�
		sbDao.truncateStockFirstIndustryToConcept();

		try {

			xb = new XSSFWorkbook(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		xsheet = xb.getSheetAt(0);
		// �õ�������
		int rowNum = xsheet.getLastRowNum();
		xrow = xsheet.getRow(0);
		System.out.println("������" + rowNum);
		int colNum = xrow.getPhysicalNumberOfCells();// ����
		System.out.println("������" + colNum);
		// ��������Ӧ�ôӵڶ��п�ʼ,��һ��Ϊ��ͷ�ı���

		// rowNum=40;

		int Num = 0;

		String beforeFirstCode = "";

		String beforeFirstName = "";

		for (int i = 2; i <= rowNum; i++) {
			xrow = xsheet.getRow(i);

			int j = 0;// ��ȡ0,1,2��

			colNum = 10;
			for (j = 0; j < colNum; j++) {

				stock[j] = "";
				
				if (xrow.getCell(j) == null) {
					stock[j] = "";
				} else {
					xrow.getCell(j).setCellType(Cell.CELL_TYPE_STRING);
					stock[j] = xrow.getCell(j).getStringCellValue();
				}
				

				if (j == 0 && (stock[0] == null || stock[0].equals(""))) // һ��
				{
					stock[0] = beforeFirstCode;
				} else
					beforeFirstCode = stock[0];

				if (j == 1 && (stock[1] == null || stock[1].equals(""))) {
					stock[1] = beforeFirstName;
				} else
					beforeFirstName = stock[1];
				// stock[j]=getCellFormatValue(xrow.getCell((short) j))

			}

			Num++;

			System.out.println("first industry code:" + stock[0]
					+ "first industry name:" + stock[1] + " cocept code:"
					+ stock[2] + "cocept name:" + stock[3] + "baseexpect:"
					+ stock[4]);

			sbDao.addStockFirstIndustryToConcept(stock[0], stock[1], stock[2],
					stock[3], stock[4], stock[5], stock[6], stock[7], stock[8],
					stock[9]);

			for (int count = 4; count < 10; count++)
				stock[count] = "";

			str = "";

		}
		is.close();
		System.out.println("Num:" + Num);

		return 0;
	}

	// �ڻ���Ʒ�г�
	public int readFuturesInfo() throws IOException, ClassNotFoundException,
			SQLException {

		String[] stock = new String[10];
		HSSFCell cell;
		XSSFWorkbook xb = null;
		Sheet xsheet;
		String str = "";

		String curPath = System.getProperty("user.dir");
		curPath = curPath.replaceAll("\\\\", "/"); // ת�彫\ תΪ/
		File file = new File(curPath + "/StockBaseData/7ExMarket_BaseInfo.xlsx");
		if (file == null || !file.exists()) {
			System.out.println("no xlsx");
			return -1;
		}

		InputStream is = new FileInputStream(
				"StockBaseData/7ExMarket_BaseInfo.xlsx");
		if (is == null) {
			return -1;
		}

		// ��ձ�
		sbDao.truncateMarket(ConstantsInfo.FuturesMarket);

		try {
			xb = new XSSFWorkbook(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		xsheet = xb.getSheetAt(0);
		// �õ�������
		int rowNum = xsheet.getLastRowNum();
		xrow = xsheet.getRow(0);
		System.out.println("������" + rowNum);
		int colNum = xrow.getPhysicalNumberOfCells();// ����
		System.out.println("������" + colNum);
		// ��������Ӧ�ôӵڶ��п�ʼ,��һ��Ϊ��ͷ�ı���

		int Num = 0;

		for (int i = 1; i <= rowNum; i++) {
			xrow = xsheet.getRow(i);

			int j = 0;
			for (j = 0; j < colNum; j++) {
				stock[j] = "";
				if (xrow.getCell(j) == null) {
					stock[j] = "";
				} else {
					xrow.getCell(j).setCellType(Cell.CELL_TYPE_STRING);
					stock[j] = xrow.getCell(j).getStringCellValue();
				}
			}

			Num++;

			System.out.println("code:" + stock[0] + "name:" + stock[1]
					+ "baseexpect:" + stock[2] + "main:" + stock[3] + ":"
					+ stock[4]);

			sbDao.addStockMarket(ConstantsInfo.FuturesMarket, stock[0],
					stock[1], stock[2], stock[3], stock[4], stock[5], stock[6],
					stock[7]);

			// / sbDao.insertStockConceptInfo(stock[2],stock[3]);
			str = "";
			for (int count = 3; count < 10; count++)
				stock[count] = "";

		}
		is.close();
		System.out.println("Num:" + Num);

		return 0;
	}

	// һ����ҵ�¶�Ӧ�ĸ���
	public int readMarketInfo() throws IOException, ClassNotFoundException,
			SQLException {

		String[] stock = new String[10];
		HSSFCell cell;
		XSSFWorkbook xb = null;
		Sheet xsheet;
		String str = "";

		String curPath = System.getProperty("user.dir");
		curPath = curPath.replaceAll("\\\\", "/"); // ת�彫\ תΪ/
		File file = new File(curPath + "/StockBaseData/0Market_BaseInfo.xlsx");
		if (file == null || !file.exists()) {
			System.out.println("no xlsx");
			return -1;
		}

		InputStream is = new FileInputStream(
				"StockBaseData/0Market_BaseInfo.xlsx");
		if (is == null) {
			return -1;
		}

		// ��ձ�
		sbDao.truncateMarket(ConstantsInfo.StockMarket);

		try {
			xb = new XSSFWorkbook(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		xsheet = xb.getSheetAt(0);
		// �õ�������
		int rowNum = xsheet.getLastRowNum();
		xrow = xsheet.getRow(0);
		System.out.println("������" + rowNum);
		int colNum = xrow.getPhysicalNumberOfCells();// ����
		System.out.println("������" + colNum);
		// ��������Ӧ�ôӵڶ��п�ʼ,��һ��Ϊ��ͷ�ı���

		int Num = 0;

		for (int i = 1; i <= rowNum; i++) {
			xrow = xsheet.getRow(i);

			int j = 0;
			for (j = 0; j < colNum; j++) {
				stock[j] = "";
				if (xrow.getCell(j) == null) {
					stock[j] = "";
				} else {
					xrow.getCell(j).setCellType(Cell.CELL_TYPE_STRING);
					stock[j] = xrow.getCell(j).getStringCellValue();
				}
			}

			Num++;

			System.out.println("code:" + stock[0] + "name:" + stock[1]
					+ "baseexpect:" + stock[2] + "main:" + stock[3] + ":"
					+ stock[4]);

			sbDao.addStockMarket(ConstantsInfo.StockMarket, stock[0], stock[1],
					stock[2], stock[3], stock[4], stock[5], stock[6], stock[7]);

			// / sbDao.insertStockConceptInfo(stock[2],stock[3]);
			str = "";
			for (int count = 3; count < 10; count++)
				stock[count] = "";

		}
		is.close();
		System.out.println("Num:" + Num);

		return 0;
	}

	// ����������ҵ��Ӧ�Ĺ�Ʊ
	public int readThirdIndustry_to_stock() throws IOException,
			ClassNotFoundException, SQLException {

		String[] stock = new String[4];
		HSSFCell cell;
		XSSFWorkbook xb = null;
		Sheet xsheet;

		String curPath = System.getProperty("user.dir");
		curPath = curPath.replaceAll("\\\\", "/"); // ת�彫\ תΪ/
		File file = new File(curPath
				+ "/StockBaseData/3ThirdIndustry-to-stock.xlsx");
		if (file == null || !file.exists()) {
			System.out.println("no xlsx");
			return -1;
		}

		InputStream is = new FileInputStream(
				"StockBaseData/3ThirdIndustry-to-stock.xlsx");
		if (is == null) {
			return -1;
		}

		// ���������ҵ��Ʊ��Ӧ��
		sbDao.truncateStocktoIndustry();

		String str = "";
		try {
			// fs = new POIFSFileSystem(is);
			xb = new XSSFWorkbook(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		xsheet = xb.getSheetAt(0);
		// �õ�������
		int rowNum = xsheet.getLastRowNum();
		xrow = xsheet.getRow(0);
		System.out.println("������" + rowNum);
		int colNum = xrow.getPhysicalNumberOfCells();// ����
		System.out.println("������" + colNum);
		// ��������Ӧ�ôӵڶ��п�ʼ,��һ��Ϊ��ͷ�ı���

		// rowNum=20;

		int Num = 0;
		String fullId = "";
		for (int i = 0; i <= rowNum; i++) {
			xrow = xsheet.getRow(i);

			int j = 0;// ��ȡ0,1,2��

			// colNum=3;
			for (j = 0; j < colNum; j++) {
				xrow.getCell(j).setCellType(Cell.CELL_TYPE_STRING);
				// stock[j]=getCellFormatValue(xrow.getCell((short) j))
				stock[j] = xrow.getCell(j).getStringCellValue();
			}

			Num++;
			System.out.println("stock code:" + stock[0] + " stock name:"
					+ stock[1] + " third industry name:" + stock[2]
					+ " third industry code:" + stock[3]);
			// sbDao.updateStockSingle(fullId);
			StockIndustry sindustry = sbDao
					.lookUpIndustryFromThirdCode(stock[3]);
			if (sindustry == null) {
				System.out.println("cannot not found this code");
			}

			// �����¹�Ʊ
			StockSingle sStock = new StockSingle(1, stock[0], stock[1],
					sindustry.getThirdcode(), sindustry.getThirdname(),
					sindustry.getSecondcode(), sindustry.getSecondname(),
					sindustry.getFirstcode(), sindustry.getFirstname(), " ", 0);
			try {
				sbDao.insertStockSingleToIndustry(sStock);
				// sbDao.insertStockSingle(sStock);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			str = "";
		}
		is.close();
		System.out.println("Num:" + Num);
		return 0;
	}

	// ����������ҵ��Ӧ�Ĺ�Ʊ
	public int readConcept_to_stock() throws IOException,
			ClassNotFoundException, SQLException {

		String[] stock = new String[5];
		HSSFCell cell;
		XSSFWorkbook xb = null;
		Sheet xsheet;

		String curPath = System.getProperty("user.dir");
		curPath = curPath.replaceAll("\\\\", "/"); // ת�彫\ תΪ/
		File file = new File(curPath + "/StockBaseData/4Concept-to-stock.xlsx");
		if (file == null || !file.exists()) {
			System.out.println("no xlsx");
			return -1;
		}

		InputStream is = new FileInputStream(
				"StockBaseData/4Concept-to-stock.xlsx");
		if (is == null) {
			return -1;
		}

		// ���������ҵ��Ʊ��Ӧ��
		sbDao.truncateStockToConcept();

		String str = "";
		try {
			// fs = new POIFSFileSystem(is);
			xb = new XSSFWorkbook(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		xsheet = xb.getSheetAt(0);
		// �õ�������
		int rowNum = xsheet.getLastRowNum();
		xrow = xsheet.getRow(0);
		System.out.println("������" + rowNum);
		int colNum = xrow.getPhysicalNumberOfCells();// ����
		System.out.println("������" + colNum);
		// ��������Ӧ�ôӵڶ��п�ʼ,��һ��Ϊ��ͷ�ı���

		// rowNum=20;

		int Num = 0;

		for (int i = 0; i <= rowNum; i++) {
			xrow = xsheet.getRow(i);

			int j = 0;// ��ȡ0,1,2��

			for (j = 0; j < colNum; j++) {
				xrow.getCell(j).setCellType(Cell.CELL_TYPE_STRING);
				// stock[j]=getCellFormatValue(xrow.getCell((short) j))
				stock[j] = xrow.getCell(j).getStringCellValue();
			}

			Num++;
			System.out
					.println("stock code:" + stock[0] + " stock name:"
							+ stock[1] + " first industry code:" + stock[3]
							+ " concept code:" + stock[2] + " concept name:"
							+ stock[4]);

			sbDao.insertStockToConcept(stock[0], stock[1], stock[3], stock[2],
					stock[4]);

			str = "";
		}
		is.close();
		System.out.println("Num:" + Num);
		return 0;
	}

	public static int stockInsertMysql(StockInformationDao sd, String[] st)
			throws IOException, ClassNotFoundException, SQLException {
		/*
		 * // for(int i=1;i<st.length;i++) // { if(st[i] == null ||
		 * st[i].length() <= 0) { insetNoNum++; return -1; } }
		 */

		// StockInformation stif=new
		// StockInformation(st[2],st[4],st[3],st[0],st[1]," "," ");
		// sd.addStock2(stif);
		// stock_info st[1] id st[6] fullid st[2]���� st[7]���� st[3] ��ҵ st[5] ����
		// st[4] ����
		StockInformation stif = new StockInformation(st[1], st[6], st[2],
				st[7], st[3], st[5], st[4]);
		sd.addStockInfo(stif);
		insetNum++;
		return 0;
	}

	/**
	 * ��ȡ��Ԫ����������Ϊ�ַ������͵�����
	 * 
	 * @param cell
	 *            Excel��Ԫ��
	 * @return String ��Ԫ����������
	 */
	private String getStringCellValue(HSSFCell cell) {
		String strCell = "";
		switch (cell.getCellType()) {
		case HSSFCell.CELL_TYPE_STRING:
			strCell = cell.getStringCellValue();
			break;
		case HSSFCell.CELL_TYPE_NUMERIC:
			strCell = String.valueOf(cell.getNumericCellValue());
			break;
		case HSSFCell.CELL_TYPE_BOOLEAN:
			strCell = String.valueOf(cell.getBooleanCellValue());
			break;
		case HSSFCell.CELL_TYPE_BLANK:
			strCell = "";
			break;
		default:
			strCell = "";
			break;
		}
		if (strCell.equals("") || strCell == null) {
			return "";
		}
		if (cell == null) {
			return "";
		}
		return strCell;
	}

	/**
	 * ��ȡ��Ԫ����������Ϊ�������͵�����
	 * 
	 * @param cell
	 *            Excel��Ԫ��
	 * @return String ��Ԫ����������
	 */
	private String getDateCellValue(HSSFCell cell) {
		String result = "";
		try {
			int cellType = cell.getCellType();
			if (cellType == HSSFCell.CELL_TYPE_NUMERIC) {
				Date date = cell.getDateCellValue();
				result = (date.getYear() + 1900) + "-" + (date.getMonth() + 1)
						+ "-" + date.getDate();
			} else if (cellType == HSSFCell.CELL_TYPE_STRING) {
				String date = getStringCellValue(cell);
				result = date.replaceAll("[����]", "-").replace("��", "").trim();
			} else if (cellType == HSSFCell.CELL_TYPE_BLANK) {
				result = "";
			}
		} catch (Exception e) {
			System.out.println("���ڸ�ʽ����ȷ!");
			e.printStackTrace();
		}
		return result;
	}

	public static String getPrettyNumber(String number) {
		return BigDecimal.valueOf(Double.parseDouble(number))
				.stripTrailingZeros().toPlainString();
	}

	/**
	 * ����HSSFCell������������
	 * 
	 * @param cell
	 * @return
	 */
	private String getCellFormatValue(Cell cell) {
		String cellvalue = "";
		String celltmpvalue = "";
		if (cell != null) {
			// �жϵ�ǰCell��Type
			switch (cell.getCellType()) {
			// �����ǰCell��TypeΪNUMERIC
			case HSSFCell.CELL_TYPE_NUMERIC:
			case HSSFCell.CELL_TYPE_FORMULA: {
				// �жϵ�ǰ��cell�Ƿ�ΪDate
				if (HSSFDateUtil.isCellDateFormatted(cell)) {
					// �����Date������ת��ΪData��ʽ

					// ����1�������ӵ�data��ʽ�Ǵ�ʱ����ģ�2011-10-12 0:00:00
					// cellvalue = cell.getDateCellValue().toLocaleString();

					// ����2�������ӵ�data��ʽ�ǲ�����ʱ����ģ�2011-10-12
					Date date = cell.getDateCellValue();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					cellvalue = sdf.format(date);

				}
				// ����Ǵ�����
				else {
					// ȡ�õ�ǰCell����ֵ
					celltmpvalue = String.valueOf(cell.getNumericCellValue());// ���ط���ֵ
					cellvalue = getPrettyNumber(celltmpvalue);
				}
				break;
			}
				// �����ǰCell��TypeΪSTRIN
			case HSSFCell.CELL_TYPE_STRING:
				// ȡ�õ�ǰ��Cell�ַ���
				cellvalue = cell.getRichStringCellValue().getString();
				break;
			// Ĭ�ϵ�Cellֵ
			default:
				cellvalue = " ";
			}
		} else {
			cellvalue = "";
		}
		return cellvalue;

	}

	public int readYearInfo() throws IOException, ClassNotFoundException,
			SQLException {

		sbDao.createStockbaseyearinfoTable();
		int ret=0;
		//sbDao.truncateStockTable("stock_baseyearinfo");
		ret = read9Stock_GongJiJin();
		if(ret<0)
			return -1;
		ret =read10Stock_LiRun();
		 if(ret<0)
				return -1;
		 ret =read11Stock_Yejiyugao();
		if(ret<0)
			return -1;
		ret = read12Stock_PiLouShiJian();
		if(ret<0)
			return -1;
		
		return 0;
	}

	public static void main(String[] args) throws IOException,
			ClassNotFoundException, SQLException {
		// StockInformationDao sd=new StockInformationDao();

		Connection stockBaseConn = DbConn
				.getConnDB("stockConf/conn_base_db.ini");

		ExcelReader excelReader = new ExcelReader(stockBaseConn);

		// �����г�
		// excelReader.readMarketInfo();

		// ������Ʒ�г�
		//excelReader.readFuturesInfo();
		// һ��������ҵ //03excel
		// excelReader.readIndustry();

		// һ����ҵ�¸���
		// excelReader.readFirstIndustry_To_Concept();
		
		 excelReader.readStockToFeatures();

		// ������ҵ��Ʊ
		// excelReader.readThirdIndustry_to_stock();
		// ��Ʊ������
		// excelReader.readstock_baseExpect();
		// �����¶�Ӧ��Ʊ
		// excelReader.readConcept_to_stock();
		// excelReader.readTwoRong();

		//excelReader.readYearInfo();

		stockBaseConn.close();

	}
}