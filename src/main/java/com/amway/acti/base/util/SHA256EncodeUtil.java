package com.amway.acti.base.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;

import java.security.MessageDigest;

/**
 * @author Harry Xu
 * <p>
 * created at 2017/8/31 20:39
 */
@Slf4j
public class SHA256EncodeUtil {
    private SHA256EncodeUtil() {
    }

    /**
     * 进行SHA 256加密方式对用户密码进行加密
     */
    public static String encode(String plaintPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plaintPassword.getBytes("UTF-8"));
            String output = Hex.encodeHexString(hash);
            return output;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
