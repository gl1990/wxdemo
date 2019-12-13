package com.ota.util;

import java.io.File;
import java.util.HashMap;

public class XmlHelper {
	public static boolean checkManifast(String manifastPath) {
		File file = new File(manifastPath);
		if(file.exists()) {
			return true;
		} else {
			return false;
		}
	}
	public static String getDescription(String descriptionPath) {
		File file = new File(descriptionPath);
		if(file.exists()) {
			
		}
		return null;
	}
	
	public static HashMap<String, VersionInfoItem> getVersionList(String manifastPath, String product) {
		System.out.println("XmlHelper.getVersionList");
		System.out.println("manifastPath: " + manifastPath);
		System.out.println("product: " + product);
		return (HashMap<String, VersionInfoItem>) XmlUtil.getVersionList(manifastPath, product);
	}
	public static String getFullPackagePath(String manifastPath, String product) {
		return null;
	}
	public static String getRkImagePath(String manifastPath, String product) {
		return null;
	} 
}
