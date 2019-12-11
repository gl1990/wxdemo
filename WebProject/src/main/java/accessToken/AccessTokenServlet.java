package accessToken;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class AccessTokenServlet
 */
@WebServlet("/AccessTokenServlet")
public class AccessTokenServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
//	static Logger logger = LoggerFactory.getLogger(AccessTokenServlet.class);
	
	@Override
	public void init() throws ServletException {
		System.out.println("-----����AccessTokenServlet-----");
		super.init();
		
		final String appId = GlobleInfo.getAppId();
        final String appSecret =GlobleInfo.getAppSecret() ;

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        //��ȡaccessToken
                        AccessTokenInfo.accessToken = getAccessToken(appId, appSecret);
                        //��ȡ�ɹ�
                        if (AccessTokenInfo.accessToken != null) {
                            //��ȡ��access_token ����7000��,��Լ2��Сʱ����
                            Thread.sleep(7000 * 1000);
                        } else {
                            //��ȡʧ��
                            Thread.sleep(1000 * 60 * 5); //��ȡ��access_tokenΪ�� ����3��
                        }
                    } catch (Exception e) {
                        System.out.println("�����쳣��" + e.getMessage());
                        e.printStackTrace();
                        try {
                            Thread.sleep(1000 * 10); //�����쳣����1��
                        } catch (Exception e1) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
	}
	
	private AccessToken getAccessToken(String appId, String appSecret) {
        NetWorkUtil netHelper = new NetWorkUtil();
        /**
         * �ӿڵ�ַΪhttps://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET������grant_type�̶�дΪclient_credential���ɡ�
         */
        String Url = String.format("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s", appId, appSecret);
        //������Ϊhttps��get���󣬷��ص����ݸ�ʽΪ{"access_token":"ACCESS_TOKEN","expires_in":7200}
        String result = netHelper.getHttpsResponse(Url, "");
//        System.out.println("��ȡ����access_token="+result);

        //ʹ��FastJson��Json�ַ���������Json����
        JSONObject json = JSON.parseObject(result);

    	AccessToken token = new AccessToken();

    	token.setTokenName(json.getString("access_token"));
        token.setExpireSecond(json.getInteger("expires_in"));
        return token;
    }
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AccessTokenServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		byte[] b = new byte[] {
//			0b00000100, (byte) 0b11111111
//		};
//		System.out.println(Integer.toHexString(b[0] & 0xFF).toUpperCase());
//		System.out.println(Integer.parseInt("f", 16));
//		response.getWriter().append("Served at: ").append(request.getContextPath());
		String sortString = AlgorithmUtil.sort("cd5a3c4f027fc9179e8649876ed51623", "1248675899", "768798");
		String signString = sha1(sortString);  //sha1ǩ��
		System.out.println("signString: " + signString);
		
		//AES�ӽ���
		//������Կ
		try {
			System.out.println("���ܽ������ԣ�");
			String secret = AlgorithmUtil.getAESKey();
			byte[] b1 = AlgorithmUtil.getAESEncode(secret, "<xml>��С��");
			b1 = AlgorithmUtil.getAESDecode(secret, b1);
			System.out.println("���ܺ������Ϊ��" + new String(b1, "UTF-8"));
			
//			String content = "����yuuygiu11344GHJJ";
//			System.out.println("ԭ����Ϊ��" + content);
//			String encryContent = AlgorithmUtil.encryptAES(content, "111");
//			System.out.println("���ܺ������Ϊ��" + encryContent);
//			String decryContent = AlgorithmUtil.decryptAES(encryContent, "111");
//			System.out.println("���ܺ������Ϊ��" + new String(decryContent.getBytes(), "UTF-8"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/**
	 * md5�ַ�������
	 */
	
	public String sha1(String str) {
		try {
			MessageDigest digest = MessageDigest.getInstance("sha1");
			digest.update(str.getBytes());
			byte[] mess = digest.digest();
		    return AlgorithmUtil.parseByte2HexStr(mess);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
