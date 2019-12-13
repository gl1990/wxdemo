package com.ota.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.mail.internet.NewsAddress;
import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.*;


public class XmlUtil {
	
	@SuppressWarnings("rawtypes")
	public static Map<String,VersionInfoItem> getVersionList(String manifastPath, String product) {
		
			File file = new File(manifastPath);
			try {
				FileReader fileReader = new FileReader(file);
				@SuppressWarnings("unchecked")
				Map<String,VersionInfoItem> map = new HashMap();
			        SAXReader reader = new SAXReader();
			        Document document = reader.read(fileReader);
			     // 得到xml根元素
			        Element root = document.getRootElement();
			        // 得到根元素的所有子节点
			        @SuppressWarnings("unchecked")
					List<Element> elementList = root.elements();

			        // 遍历所有子节点
			        for (Element e : elementList) {
			        	DefaultAttribute defaultAttribute = (DefaultAttribute) e.attributes().get(0);
			        	if (defaultAttribute.getValue().equalsIgnoreCase(product)) {
			        		System.out.println(defaultAttribute.getValue());
			        		System.out.println(e.content().get(1).getClass().getName());
			        		for (int i = 0 ; i < e.content().size() - 1; i+=2) {
			        			DefaultElement defaultElement = (DefaultElement)e.content().get(i+1);
			        			@SuppressWarnings("unchecked")
								List<DefaultAttribute> list = defaultElement.attributes();
				        		String version = null;
				        		VersionInfoItem eInfoItem = new VersionInfoItem();
				        		for (int j = 0; j < list.size(); j++) {
									if (list.get(j).getName().equalsIgnoreCase("name")) {
										version = list.get(0).getValue();
									} else if (list.get(j).getName().equalsIgnoreCase("package_path")) {
										eInfoItem.setPackagePath(list.get(j).getValue());
									} else if (list.get(j).getName().equalsIgnoreCase("package_size")) {
										eInfoItem.setPackageLength(list.get(j).getValue());
									} else if (list.get(j).getName().equalsIgnoreCase("description")) {
										eInfoItem.setDescriptionPath(list.get(j).getValue());
									}
								}
				        		if (version != null && eInfoItem != null) {
				        			map.put(version, eInfoItem);
				        		}
			        		}
			        		break;
			        	}
			        }

			     // 释放资源
			        fileReader.close();
			        fileReader = null;
			        return map;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return null;
	}
	
	public static void getDescription(String descriptionPath) {
		
		
		
	}
}
