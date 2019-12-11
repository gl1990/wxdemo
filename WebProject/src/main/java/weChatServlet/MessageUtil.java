package weChatServlet;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.InputStream;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import com.qq.weixin.mp.aes.AesException;
import com.qq.weixin.mp.aes.WXBizMsgCrypt;

import accessToken.GlobleInfo;

public class MessageUtil {
	/**
     * 解析微信发来的请求（XML）
     * @param request
     * @return map
     * @throws Exception
     */
    public static Map<String,String> parseXml(HttpServletRequest request) throws Exception {
      // 将解析结果存储在HashMap中
        @SuppressWarnings("unchecked")
		Map<String,String> map = new HashMap();
     // 从request中取得输入流
        InputStream inputStream = request.getInputStream();
        System.out.println("获取输入流");
     // 读取输入流
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputStream);
     // 得到xml根元素
        Element root = document.getRootElement();
        // 得到根元素的所有子节点
        List<Element> elementList = root.elements();

        // 遍历所有子节点
        for (Element e : elementList) {
            map.put(e.getName(), e.getText());
        }
        Map<String, String[]> params = request.getParameterMap();
    	Set<Entry<String, String[]>> paraset = params.entrySet();
    	for (Entry<String, String[]> entry : paraset) {
			map.put(entry.getKey(), entry.getValue()[0]);
		}
        Set<Entry<String,String>> entryset = map.entrySet();
        for(Entry<String,String> ent : entryset) {
        	System.out.println(ent.getKey() + "|" + ent.getValue());
		}

     // 释放资源
        inputStream.close();
        inputStream = null;
        return map;
    }

    public static String extract(String xmltext)     {
		String result = "text";
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader(xmltext);
			InputSource is = new InputSource(sr);
			org.w3c.dom.Document document = db.parse(is);

			org.w3c.dom.Element root = document.getDocumentElement();
			NodeList nodeMsgType = root.getElementsByTagName("MsgType");
			result = nodeMsgType.item(0).getTextContent();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

    /**
     * 根据消息类型 构造返回消息 FromUserName | oHFXGvtk-xXMJ33kcV-cQKd_2zco
     */
    public static String buildXml(Map<String,String> map) {
        String result;
        String format = "<xml><ToUserName><![CDATA[%1$s]]></ToUserName><Encrypt><![CDATA[%2$s]]></Encrypt></xml>";
		String fromXML = String.format(format, map.get("ToUserName"), map.get("Encrypt"));
		System.out.println("fromXML: " + fromXML);
        WXBizMsgCrypt wxBizMsgCrypt;
        String mingwen;
		try {
			System.out.println("解密前的密文: " + map.get("Encrypt"));
			wxBizMsgCrypt = new WXBizMsgCrypt(GlobleInfo.getToken(), GlobleInfo.getAeskey(), GlobleInfo.getAppId());
			mingwen = wxBizMsgCrypt.decryptMsg(map.get("msg_signature"), map.get("timestamp"), map.get("nonce"), fromXML);
			System.out.println("解密验证正确，开始解密....");
			System.out.println("解密后的明文: " + mingwen);
		} catch (AesException e) {
			// TODO Auto-generated catch block
			System.out.println("解密验证错误");
			mingwen = "";
		}
       
        String MsgType = extract(mingwen);
        if(MsgType.toUpperCase().equals("TEXT")){
        	 result = buildTextMessage(map, "Cherry的小小窝, 请问客官想要点啥?");
        }else{
            String fromUserName = map.get("openid");
            // 开发者微信号
            String toUserName = map.get("ToUserName");
            String toTimestamp = getUtcTime();
            String nonce = String.valueOf(Integer.parseInt(toTimestamp.substring(0, 10))+ new Random().nextInt(10));
            result = String
                    .format(
                            "<xml>" +
                                    "<ToUserName><![CDATA[%s]]></ToUserName>" +
                                    "<FromUserName><![CDATA[%s]]></FromUserName>" +
                                    "<CreateTime>%s</CreateTime>" +
                                    "<MsgType><![CDATA[text]]></MsgType>" +
                                    "<Content><![CDATA[%s]]></Content>" +
                                    "</xml>",
                            fromUserName, toUserName, toTimestamp,
                            "请回复如下关键词：\n文本\n图片\n语音\n视频\n音乐\n图文");
            System.out.println("加密前: " + result);
            try {
            	wxBizMsgCrypt = new WXBizMsgCrypt(GlobleInfo.getToken(), GlobleInfo.getAeskey(), GlobleInfo.getAppId());
				result = wxBizMsgCrypt.encryptMsg(result, toTimestamp, nonce);
				System.out.println("加密后: " + result);
			} catch (AesException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
       
        return result;
    }

    /**
     * 构造文本消息
     *
     * @param map
     * @param content
     * @return
     */
    private static String buildTextMessage(Map<String,String> map, String content) {
    	String result;
    	String fromUserName = map.get("openid");
        // 开发者微信号
        String toUserName = map.get("ToUserName");
        String toTimestamp = getUtcTime();
        String nonce = String.valueOf(Integer.parseInt(toTimestamp.substring(0, 10))+ new Random().nextInt(10));
        /**
         * 文本消息XML数据格式
         */
        result =  String.format(
                "<xml>" +
                        "<ToUserName><![CDATA[%s]]></ToUserName>" +
                        "<FromUserName><![CDATA[%s]]></FromUserName>" +
                        "<CreateTime>%s</CreateTime>" +
                        "<MsgType><![CDATA[text]]></MsgType>" +
                        "<Content><![CDATA[%s]]></Content>" + "</xml>",
                fromUserName, toUserName, toTimestamp, content);
        System.out.println("加密前: " + result);
        try {
        	WXBizMsgCrypt wxBizMsgCrypt = new WXBizMsgCrypt(GlobleInfo.getToken(), GlobleInfo.getAeskey(), GlobleInfo.getAppId());
			result = wxBizMsgCrypt.encryptMsg(result, toTimestamp, nonce);
			System.out.println("加密后: " + result);
		} catch (AesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return result;
    }

    private static String getUtcTime() {
        Date dt = new Date();// 如果不需要格式,可直接用dt,dt就是当前系统时间
        DateFormat df = new SimpleDateFormat("yyyyMMddhhmm");// 设置显示格式
        String nowTime = df.format(dt);
        long dd = (long) 0;
        try {
            dd = df.parse(nowTime).getTime();
        } catch (Exception e) {

        }
        return String.valueOf(dd);
    }
}
