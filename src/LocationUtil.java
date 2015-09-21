import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import net.sf.json.JSONObject;

/**
 * 
 * @author dalu
 *
 */
public class LocationUtil {

	private static final String BAIDU_APP_KEY = "Your Key";
    //http://api.map.baidu.com/direction/v1/routematrix?output=json&origins="石家庄市&destinations=呼和浩特市&ak=B672c22b04e71549fac5eb006f9edecc
	/**
	 * 输入地址的起始城市名称
	 */
	public static Map<String, String> getLatitude(String origins, String destinations) {
		try {
			// 将地址转换成utf-8的16进制
			origins = URLEncoder.encode(origins, "UTF-8");
			destinations = URLEncoder.encode(destinations, "UTF-8");
			URL resjson = new URL("http://api.map.baidu.com/direction/v1/routematrix?output=json&origins=" + origins
					+ "&destinations=" + destinations + "&ak=" + BAIDU_APP_KEY);
			BufferedReader in = new BufferedReader(new InputStreamReader(resjson.openStream()));
			String res;
			StringBuilder sb = new StringBuilder("");
			while ((res = in.readLine()) != null) {
				sb.append(res.trim());
			}
			in.close();
			String str = sb.toString();
			 System.out.println("return str:" + str);
			// 解析json
			JSONObject dataJson = JSONObject.fromObject(str);
			String status = dataJson.getString("status");
			Map<String, String> map = null;
			if (status.equals("0")) {// 调用成功
				String result = dataJson.getString("result");
				System.out.println("return result:" + result);
				// 存入map
				if (result != null && !result.equals("")) {

					int disStart = result.indexOf("text\":");
					int disEnd = result.indexOf(",\"value");
					if (disStart > 0 && disEnd > 0) {
						String distance = result.substring(disStart + 7, disEnd - 1);
						map = new HashMap<String, String>();
						String outPutOrigins = URLDecoder.decode(origins, "UTF-8");
						String outPutDestinations = URLDecoder.decode(destinations, "UTF-8");
						map.put("origins", outPutOrigins);
						map.put("destinations", outPutDestinations);
						map.put("distance", distance);
						return map;
					}
				} else {

				}
			} else {// 调用失败
				System.out.println("404 error");
				map = new HashMap<String, String>();
				map.put("origins", null);
				map.put("destinations", null);
				map.put("distance", null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String args[]) {
		Map<String, String> map = null;
		String[] cities = {};
		XlsFile xls = new XlsFile();
		File file = new File("file/201景点31省会城市 - 副本.xls");
		try {
			cities = xls.getContent(file);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int length = (cities.length - 1) * (cities.length - 2) / 2;// 范围偏大，以防万一
		//String[] distances = new String[length];
		int m = 0;
		// 定义一个root作为xml文档的根元素
		Element root = new Element("CDRS");
		// 生成一个文档
		Document Doc = new Document(root);
		for (int i = 0; i < cities.length-1; i++) {
			for (int j = i + 1; j < cities.length - 1; j++) {
				System.out.println(cities[i] + " " + cities[j]);
				map = LocationUtil.getLatitude(cities[i], cities[j]);// 获取map
				if (null != map) {
					// 在生成的名称为CDRS的跟元素下生成下一级元素标签名称为序列
					Element elements = new Element("序列");
					// 为序列设置属性名和属性值
					elements.setAttribute("num", "" + m);
					// 在序列标签内部添加新的元素，即序列的下一级标签
					elements.addContent(new Element("开始地点").setText(map.get("origins")));
					elements.addContent(new Element("结束地点").setText(map.get("destinations")));
					elements.addContent(new Element("距离").setText(map.get("distance")+"\n"));
					// 将已经设置好值的elements赋给root
					root.addContent(elements);
					// distances[m] = map.get("distance");
					 m++;
					// System.out.println(map.get("origins"));
					// System.out.println(map.get("destinations"));
					// System.out.println(map.get("distance"));
				}
			}

		}
		XMLOutputter XMLOut = new XMLOutputter();
		try {
			// 将生成的xml文档Doc输出到c盘的test.xml文档中
			XMLOut.output(Doc, new FileOutputStream("file/test.xml"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 写入到execl文件中
		// xls.write(cities, distances);//2003版本的最多支持256列，不符合要求
		// xls.printXml(map,cities.length);//写入xml文件中
	}
}