package cn.zhengyiyi.banklite.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 密码加密工具类。
 * 提供密码加密相关的功能，包括生成随机盐值和使用MD5加盐加密。
 */
public class EncryptionUtil {

    /**
     * 生成随机盐值。
     * 使用安全的随机数生成器生成16字节的随机盐值。
     *
     * @return 生成的盐值的Base64编码字符串
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * 使用MD5和盐值进行加密。
     * 先将盐值转换为字节后添加到消息摘要中，然后将密码转换为字节进行处理。
     *
     * @param password 需要加密的密码
     * @param salt 使用的盐值
     * @return 加密后的密码的十六进制字符串
     */
    public static String md5WithSalt(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(salt.getBytes());
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}