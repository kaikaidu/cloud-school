package com.amway.acti.base.util;


import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;

//import com.amway.common.util.PubFunc;
//import com.ibm.crypto.provider.AESKeySpec;


/**
 * 提供AES加密、解密方法的类。
 * 
 * @author Dennis Ou
 *  
 */
public class AESUtils {
    
    //private static String seed; // 密匙
//	private static String seed = "1234567890abcdef";				// 5G最新所使用的真實seed
//    private static String originalSeed = "1234567890abcdefg";//舊有的真实的seed（用於香港解密，臺灣4G的加解密）
    private static String rawKey4G = "5DE32101141D3079F8CE5040E817A152";
    private static String seed = "1234567890abcdef";
    private static String originalSeed = "1234567890abcdefg";
    private static String seedByte = "49,50,51,52,53,54,55,56,57,48,97,98,99,100,101,102";
    private static Provider oneProvider = new com.sun.crypto.provider.SunJCE();
    
    private static byte[] getBytes(String text) {
    	if (text!=null){
    		try{
    			return text.trim().getBytes("UTF-8");
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    	}
    	return new byte[0];
    	
    }
    
    private static Provider getProvider(){
    	return oneProvider;
    }
    
    /**
     * 5G加解密處理Key
     * @param seed
     * @return
     * @throws Exception
     */
    private static byte[] getRawKey(byte[] seed) throws Exception {

//        KeyGenerator kgen = KeyGenerator.getInstance("AES");
//        kgen.init(128, new SecureRandom(seed));
//        SecretKey secretKey = kgen.generateKey();
//        byte[] raw = secretKey.getEncoded();

//        Security.addProvider(getProvider());
//        SecretKey skey = SecretKeyFactory.getInstance("AES").generateSecret(new AESKeySpec(seed));
//
//        byte[] raw = skey.getEncoded();
        byte []raw=new byte[16];
        String [] seedByteList=seedByte.split (",");
        for(int i=0;i<seedByteList.length;i++){
            raw[i]=Byte.valueOf (seedByteList[i]);
        }
        return raw;
    }
    
    /**
     * 加密。密文长度 = 32 * (明文长度 / 16 +1)
     * 
     * @param cleartext
     *            明文
     * @return
     * @throws Exception
     */
    public static String encrypt(String cleartext) throws Exception {
//        if (PubFunc.isEmpty(cleartext)) {
//            return cleartext;
//        }
        return encrypt(seed, cleartext);
    }

    public static String encrypt(String seed, String cleartext) throws Exception {
    	byte[] rawKey = getRawKey(getBytes(seed));
        byte[] result = encrypt(rawKey, getBytes(cleartext));
        return toHex(result);
    }

    /**
     * 解密
     * 
     * @param encrypted
     *            密文
     * @return
     * @throws Exception
     */
    public static String decrypt(String encrypted) throws Exception {
//        if (PubFunc.isEmpty(encrypted)) {
//            return encrypted;
//        }
        return decrypt(seed, encrypted);
    }

    public static String decrypt(String seed, String encrypted) throws Exception {
    	byte[] result = null;
        byte[] enc = null;
        byte[] rawKey = null;
        
		try {
			enc = toByte(encrypted.trim());
			rawKey = getRawKey(getBytes(seed));
			result = decrypt(rawKey, enc);
		} catch (Exception e) {
			// 如不能使用新加解密的密鈅，則使用舊的方式
			rawKey = getRawKeyForTW4G(getBytes(originalSeed));
			result = decrypt(rawKey, enc);
		}
        
        return new String(result);
    }

    // ------------------------------------------------------------------------------------    
    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
//        Cipher cipher = Cipher.getInstance("AES");
        Cipher cipher = Cipher.getInstance("AES",getProvider());
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
//        Cipher cipher = Cipher.getInstance("AES");
        Cipher cipher = Cipher.getInstance("AES" ,getProvider());
        
        String csn = Charset.defaultCharset().name();
        StringBuilder cipherSB = new StringBuilder(csn).append("\n");

        cipherSB.append(cipher.getProvider().toString()).append("\n");
        cipherSB.append(cipher.getProvider().getName()).append("\n");
        cipherSB.append(cipher.getAlgorithm()).append("\n");
        cipherSB.append(cipher.getBlockSize()).append("\n");
//        cipherSB.append(cipher.getParameters().getEncoded());
//        logger.error(" -------------------decrypt Cipher:"+cipherSB);
        

        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    protected static String toHex(String txt) {
        return toHex(getBytes(txt));
    }

    protected static String fromHex(String hex) {
        return new String(toByte(hex));
    }

    protected static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
        return result;
    }

    protected static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }

    protected final static String HEX = "0123456789ABCDEF";

    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }

    public static void main(String[] args) throws Exception {
        String originalText = "9115921";
//        String encryptText = "2ADEF18AAC2D970E5FB18C0AB0A154DE02DEC5652C0215F93AF69F009F0F6D58";
        String encryptText = "2D70F1B3B0B0A0DB0EC94C1C2F8BE63902DEC5652C0215F93AF69F009F0F6D58";	// 4G

        encryptText = AESUtils.encrypt(originalText);
        //5179514600869501
        //5179514600869501
        originalText = AESUtils.decrypt(encryptText);
        
//        encryptText = AESEncrypt.encrypt4G(originalText);
//        originalText = AESEncrypt.decrypt4G(encryptText);
        
        String csn = Charset.defaultCharset().name();
       // System.out.println("字符集："+csn);
        //System.out.println("Seed:" + seed);

        //System.out.println("加密后长度位：" + (32 * (getBytes(originalText).length / 16 + 1)));

        System.out.println("明文：" + originalText);
        System.out.println("加密：" + encryptText);
        System.out.println("解密：" + originalText);

       // System.out.println("AES加密结果的长度与密匙无关。长度 = 32 * (明文长度 / 16 +1)");


//        
    }
    
    // ------------------------------------------------------------------------------------    
    /**
     * 臺灣4g使用加解密處理Key
     * @param seed
     * @return
     * @throws Exception
     */
    protected static byte[] getRawKeyForTW4G(byte[] seed) throws Exception {
    	Security.addProvider(getProvider());
    	KeyGenerator kgen = KeyGenerator.getInstance("AES",getProvider());
//    	logger.error(" -------------------getRawKeyForTW KeyGenerator:"+kgen.getProvider());
    	
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG",getProvider());
//        logger.error(" -------------------getRawKeyForTW SecureRandom:"+sr.getProvider());
        
        sr.setSeed(seed);
        kgen.init(128, sr); // 192 and 256 bits需要替换JCE相关文件
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
//        logger.error(" -------------------getRawKeyForTW raw:"+ raw);
//        logger.error(" -------------------getRawKeyForTW raw toHex:"+ toHex(raw));
//    	byte[] raw = rawKey4G.getBytes();
        return raw;
    }
    
    /**
	 * 解码 4G -> 5G
	 * @param encrypted
	 * @return
	 * @throws Exception
	 * @auth Andy Wang
	 */
	public static String decrypt4G(String encrypted) {
		byte[] result = null;
		byte[] enc = null;
		byte[] rawKey = null;
		try {
			enc = AESUtils.toByte(encrypted.trim());
//	        rawKey = AESEncrypt.getRawKeyForTW4G(getBytes(originalSeed));
			rawKey = toByte(rawKey4G);
			result = AESUtils.decrypt(rawKey, enc);
		} catch (Exception e) {
//			 e.printStackTrace();
//			 Logger logger = LogHelper
//						.getLogger(AESEncrypt.class);
//			 logger.error("",e);
//			 logger.error(encrypted);
		}
		if ( result == null ) {
			return null;
		}
        return new String(result);
    }
		
    public static String encrypt4G(String cleartext) {
    	byte[] result = null;
    	byte[] rawKey = null;
    	try {
//    		rawKey = getRawKeyForTW4G(getBytes(originalSeed));
    		rawKey = toByte(rawKey4G);
            result = encrypt(rawKey, getBytes(cleartext));
		} catch (Exception e) {
			e.printStackTrace();
		}
    	if ( result == null ) {
    		return null;
    	}
        return toHex(result);
    }

}
