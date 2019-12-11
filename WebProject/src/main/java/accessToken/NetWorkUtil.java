package accessToken;
import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * created by longguo
 * 2019/12/6
 * ����HTTPS����Ĺ�����
 * getHttpsResponse����������һ��https��ַ������requestMethodΪ�ַ�����GET�����ߡ�POST������null���ߡ���Ĭ��Ϊget��ʽ��
 */

public class NetWorkUtil {
	 /**
     * ����HTTPS����
     * @param reqUrl
     * @param requestMethod
     * @return ��Ӧ�ַ���
     */
    public String getHttpsResponse(String reqUrl, String requestMethod) {
        URL url;
        InputStream is;
        String result ="";

        try {
            url = new URL(reqUrl);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

            TrustManager[] tm = {xtm};
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, tm, null);

            con.setSSLSocketFactory(ctx.getSocketFactory());
            con.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });

            con.setDoInput(true); //����������������������

            //��android�б��뽫��������Ϊfalse
            con.setDoOutput(false); //������������������ϴ�
            con.setUseCaches(false); //��ʹ�û���
            if (null != requestMethod && !requestMethod.equals("")) {
                con.setRequestMethod(requestMethod); //ʹ��ָ���ķ�ʽ
            } else {
                con.setRequestMethod("GET"); //ʹ��get����
            }
            is = con.getInputStream();   //��ȡ����������ʱ��������������
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader bufferReader = new BufferedReader(isr);
            String inputLine;
            while ((inputLine = bufferReader.readLine()) != null) {
                result += inputLine + "\n";
            }
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    X509TrustManager xtm = new X509TrustManager() {
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1)
                throws CertificateException {
        }

        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1)
                throws CertificateException {
        }
    };
}
