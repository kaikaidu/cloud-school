package com.amway.acti.base.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;

/**
 * @Description:DES算法加密解密
 * @Date:2018/3/26 14:17
 * @Author:wsc
 */
@Slf4j
public class EncrypDES {

    static  EncrypDES instance;
    Key key;
    Cipher encryptCipher;
    Cipher decryptCipher;

    //私有化构造
    private EncrypDES(){}

    //构造
    public EncrypDES(String strKey) throws GeneralSecurityException , IOException{
        key = setKey(strKey);
        encryptCipher = Cipher.getInstance(Constants.DES);
        encryptCipher.init(Cipher.ENCRYPT_MODE, key);
        decryptCipher = Cipher.getInstance(Constants.DES);
        decryptCipher.init(Cipher.DECRYPT_MODE, key);
    }

    //单例
    public static EncrypDES getInstance() throws GeneralSecurityException ,IOException{
        if (instance == null) {
            instance = new EncrypDES(Constants.DES_KEY);
        }
        return instance;
    }

    //  根据参数生成KEY
    private Key setKey(String strKey) throws GeneralSecurityException ,IOException{
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(Constants.DES);
        DESKeySpec keySpec = new DESKeySpec(strKey.getBytes(Constants.UTF));
        keyFactory.generateSecret(keySpec);
        return keyFactory.generateSecret(keySpec);
    }

    //  加密String明文输入,String密文输出
    public String setEncString(String strMing) throws GeneralSecurityException ,IOException{
        Base64 base64 = new Base64();
        byte[] byteMing = strMing.getBytes(Constants.UTF);
        byte[] byteMi = this.getEncCode(byteMing);
        return base64.encodeToString(byteMi);
    }

    //加密以byte[]明文输入,byte[]密文输出
    private byte[] getEncCode(byte[] byteS) throws GeneralSecurityException {
        return encryptCipher.doFinal(byteS);
    }

    //   解密:以String密文输入,String明文输出
    public String setDesString(String strMi) throws GeneralSecurityException ,IOException{
        Base64 base64 = new Base64();
        byte[] byteMi = base64.decode(strMi);
        byte[] byteMing = this.getDesCode(byteMi);
        return new String(byteMing, Constants.UTF);
    }

    // 解密以byte[]密文输入,以byte[]明文输出
    private byte[] getDesCode(byte[] byteD) throws GeneralSecurityException {
        return decryptCipher.doFinal(byteD);
    }
}
