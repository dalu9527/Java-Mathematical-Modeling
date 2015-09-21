import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * 
 * @author dalu
 */
public class XlsFile {

	

	/**
	 * 方法说明：读取execl文件操作
	 * 
	 * @param f
	 * @return
	 * @throws Exception
	 */
	public String[] getContent(File f) throws Exception {
		// 构建Workbook对象, 只读Workbook对象
		// 直接从本地文件创建Workbook
		// 从输入流创建Workbook
		String[] cities = null;
		FileInputStream fis = new FileInputStream(f);
		// StringBuilder sb = new StringBuilder();
		jxl.Workbook rwb = Workbook.getWorkbook(fis);
		// 一旦创建了Workbook，我们就可以通过它来访问
		// Excel Sheet的数组集合(术语：工作表)，
		// 也可以调用getsheet方法获取指定的工作表
		Sheet[] sheet = rwb.getSheets();
		System.out.println("sheet.length: " + sheet.length);
		// 只取第一个sheet
		Sheet rs = rwb.getSheet(0);
		cities = new String[rs.getRows()];
		System.out.println("rs.getRows(): " + rs.getRows());
		for (int j = 1, m = 0; j < rs.getRows(); j++, m++) {
			Cell[] cells = rs.getRow(j);
			cities[m] = cells[1].getContents().toString();
		}

		fis.close();
		return cities;
	}

	/**
	 * 方法说明：写入execl文件操作
	 * 
	 * @param cities
	 * @param distance
	 */
	public void write(String[] cities, String[] distance) {
		try {
			// 创建一个可写入的excel文件对象
			WritableWorkbook workbook = Workbook.createWorkbook(new File("file/output.xlsx"));
			// 使用第一张工作表，将其命名为“Result”
			WritableSheet sheet = workbook.createSheet("Result", 0);
			// 表头
			Label label0 = new Label(0, 0, "城市");// 列、行、名称
			sheet.addCell(label0);
			for (int i = 0; i < cities.length; i++) {
				Label labelCFC = new Label(0, i + 1, cities[i]);
				sheet.addCell(labelCFC);
			}
			for (int j = 0; j < cities.length; j++) {
				Label labelCFC = new Label(j + 1, 0, cities[j]);
				sheet.addCell(labelCFC);
			}
			int length = 0;
			System.out.println("cities.length: " + cities.length + " distance.length= " + distance.length);
			for (int m = 0; m < cities.length - 1; m++) {
				for (int n = 0; n < cities.length - m - 1; n++) {
					if (distance[length] != null) {
						Label labelCFC = new Label(n + m + 2, m + 1, distance[length]);
						length++;
						sheet.addCell(labelCFC);
					} else {
						//System.out.println("null");
					}
				}
			}
			// 关闭对象，释放资源
			workbook.write();
			workbook.close();

		} catch (Exception e) {
			System.out.println(e);
		}
	}
	/**
	 * 方法说明：写入xml文件操作
	 * @param map
	 */
	 public static void printXml(Map<String, String> map,int length){
		  //定义一个root作为xml文档的根元素
		  Element root = new Element("CDRS"); 
		  //生成一个文档
		  Document Doc = new Document(root);   
		  //System.out.println(map.size()+" size");
		  length = (length-1)*(length-2)/2;
		System.out.println(length+" length");
		  for (int j = 0; j <= length; j++) { 
		      Element elements = new Element("序列");   
		      elements.setAttribute("num", "" + j);   
		      elements.addContent(new Element("开始地点").setText(map.get("origins")));
		      elements.addContent(new Element("结束地点").setText(map.get("destinations"))); 
		      elements.addContent(new Element("距离").setText(map.get("distance"))); 
		      //将已经设置好值的elements赋给root
		      root.addContent(elements);  
		      
		     } 
		     //定义一个用于输出xml文档的类
		     XMLOutputter XMLOut = new XMLOutputter();  
		     
		     try {
		   //将生成的xml文档Doc输出到c盘的test.xml文档中
		   XMLOut.output(Doc, new FileOutputStream("file/test.xml"));
		  } catch (FileNotFoundException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
		  } catch (IOException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
		  } 
		 }

}