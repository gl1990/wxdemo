package accessToken;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@SuppressWarnings("restriction")
public class AlgorithmUtil {
	public final static String ENCODING = "UTF-8";  //"UTF-8";

	
	/**
	 * �ַ�����������
	 */
	
	public static String sort(String token, String timestamp, String nonce) {
		String[] strArray = {token, timestamp, nonce};
		StringBuffer sb = new StringBuffer();
		Arrays.sort(strArray);
		for (String string : strArray) {
			sb.append(string);
		}
		return sb.toString();
	}
	/**
     * ��������ת����16����
     * �ֽ�������ÿ��Ԫ�ؾ��Ƕ�����0b01110000��ʽ byte[] b = {0b01110000, 0b00100010, ....}
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
//            sb.append(hex);
            
        }
        return sb.toString();
    }

    /**
     * ��16����ת��Ϊ������
     *
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    /**
     * ������Կ
     * �Զ�����base64 ������AES128λ��Կ
     *
     * @throws //NoSuchAlgorithmException
     * @throws //UnsupportedEncodingException
     */
    public static String getAESKey() throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(128);//Ҫ���ɶ���λ��ֻ��Ҫ�޸����Ｔ��128, 192��256
        SecretKey sk = kg.generateKey();
        byte[] b = sk.getEncoded();
        return parseByte2HexStr(b);
    }

    /**
     * AES ����
     *
     * @param base64Key base64������ AES key
     * @param text      �����ܵ��ַ���
     * @return ���ܺ��byte[] ����
     * @throws Exception
     */
    public static byte[] getAESEncode(String base64Key, String text) throws Exception {
    	System.out.println("����ǰ���ı�: " + text);
        byte[] key = parseHexStr2Byte(base64Key);
        SecretKeySpec sKeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, sKeySpec);
        byte[] bjiamihou = cipher.doFinal(text.getBytes(ENCODING));
        return bjiamihou;
    }

    /**
     * AES����
     *
     * @param base64Key base64������ AES key
     * @param text      �����ܵ��ַ���
     * @return ���ܺ��byte[] ����
     * @throws Exception
     */
    public static byte[] getAESDecode(String base64Key, byte[] text) throws Exception {
    	System.out.println("����ǰ���ı�: " + parseByte2HexStr(text));
        byte[] key = parseHexStr2Byte(base64Key);
        SecretKeySpec sKeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, sKeySpec);
        byte[] bjiemihou = cipher.doFinal(text);
        return bjiemihou;
    }
    
    
    
    
    
    //�ڶ��ַ���
    
    /**
	 * AES����
	 *
	 * @param content
	 *            ����
	 * @return ����
	 */
	public static String encryptAES(String content, String pass) {
		byte[] encryptResult = encrypt(content, pass);
		String encryptResultStr = parseByte22HexStr(encryptResult);
		// BASE64λ����
		encryptResultStr = ebotongEncrypto(encryptResultStr);
		return encryptResultStr;
	}
 
	/**
	 * AES����
	 *
	 * @param encryptResultStr
	 *            ����
	 * @return ����
	 */
	public static String decryptAES(String encryptResultStr, String pass) {
		// BASE64λ����
		try {
			String decrpt = ebotongDecrypto(encryptResultStr);
			byte[] decryptFrom = parseHexStr22Byte(decrpt);
			byte[] decryptResult = decrypt(decryptFrom, pass);
			return new String(decryptResult);
		} catch (Exception e) { // �����Ĳ��淶ʱ�ᱨ���ɺ��ԣ������õĵط���Ҫ����
			return null;
		}
	}
 
	/**
	 * �����ַ���
	 */
	private static String ebotongEncrypto(String str) {
		BASE64Encoder base64encoder = new BASE64Encoder();
		String result = str;
		if (str != null && str.length() > 0) {
			try {
				byte[] encodeByte = str.getBytes(ENCODING);
				result = base64encoder.encode(encodeByte);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// base64���ܳ���һ�����Ȼ��Զ����� ��Ҫȥ�����з�
		return result.replaceAll("\r\n", "").replaceAll("\r", "").replaceAll("\n", "");
	}
 
	/**
	 * �����ַ���
	 */
	private static String ebotongDecrypto(String str) {
		BASE64Decoder base64decoder = new BASE64Decoder();
		try {
			byte[] encodeByte = base64decoder.decodeBuffer(str);
			return new String(encodeByte);
		} catch (IOException e) {
			return str;
		}
	}
 
	/**
	 * ����
	 *
	 * @param content
	 *            ��Ҫ���ܵ�����
	 * @param password
	 *            ��������
	 * @return
	 */
	private static byte[] encrypt(String content, String password) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			// ע������ǹؼ�����ֹlinux�� �������key����������ʽ��Windows����������Linux�ϻ�������
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
			secureRandom.setSeed(password.getBytes());
			kgen.init(128, secureRandom);
			// kgen.init(128, new SecureRandom(password.getBytes()));
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
			Cipher cipher = Cipher.getInstance("AES");// ����������
			byte[] byteContent = content.getBytes("utf-8");
			cipher.init(Cipher.ENCRYPT_MODE, key);// ��ʼ��
			byte[] result = cipher.doFinal(byteContent);
			return result; // ����
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
 
	/**
	 * ����
	 * 
	 * @param content
	 *            ����������
	 * @param password
	 *            ������Կ
	 * @return
	 */
	private static byte[] decrypt(byte[] content, String password) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			// ��ֹlinux�� �������key
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
			secureRandom.setSeed(password.getBytes());
			kgen.init(128, secureRandom);
			// kgen.init(128, new SecureRandom(password.getBytes()));
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
			Cipher cipher = Cipher.getInstance("AES");// ����������
			cipher.init(Cipher.DECRYPT_MODE, key);// ��ʼ��
			byte[] result = cipher.doFinal(content);
			return result; // ����
		} catch (Exception e) {
			System.out.println("�����쳣" + e.toString());
		}
		return null;
	}
 
	/**
	 * ��������ת����16����
	 * 
	 * @param buf
	 * @return
	 */
	private static String parseByte22HexStr(byte buf[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}
 
	/**
	 * ��16����ת��Ϊ������
	 * 
	 * @param hexStr
	 * @return
	 */
	private static byte[] parseHexStr22Byte(String hexStr) {
		if (hexStr.length() < 1)
			return null;
		byte[] result = new byte[hexStr.length() / 2];
		for (int i = 0; i < hexStr.length() / 2; i++) {
			int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}
}
