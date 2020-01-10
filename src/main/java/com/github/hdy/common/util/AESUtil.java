package com.github.hdy.common.util;

import com.github.hdy.common.spring.SpringContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * AES加密解密
 *
 * @author 贺大爷
 * @date 2018/2/6
 */
@Component
public class AESUtil {

    private static String password;
    private static String password_d;

    // static变量需要set一下
    @Value("${aes.password:hdyshinidie}")
    public void setPassword(String password) {
        this.password = password;
    }

    @Value("${aes.password.t:hdyshinidie_t}")
    public void setPassword_d(String password_d) {
        this.password_d = password_d;
    }

    /**
     * 加密
     *
     * @param content 需要加密的内容
     * @return
     */
    public static String encrypt(String content) {
        String os = System.getProperties().getProperty("os.name");
        if (os.toLowerCase().contains("window")) {
            return windowsEncrypt(windowsEncrypt(content, null), password_d);
        } else {
            return linuxEncrypt(linuxEncrypt(content, null), password_d);
        }
    }

    /**
     * 解密
     *
     * @param content 待解密内容
     * @return
     */
    public static String decrypt(String content) {
        String os = System.getProperties().getProperty("os.name");
        if (os.toLowerCase().contains("window")) {
            return windowsDecrypt(windowsDecrypt(content, password_d), null);
        } else {
            return linuxDecrypt(linuxDecrypt(content, password_d), null);
        }
    }


    /**
     * 加密 windows
     *
     * @param content 需要加密的内容
     * @param keys    加密密码
     * @return
     */
    private static String windowsEncrypt(String content, String keys) {
        if (Strings.isNull(keys)) {
            keys = password;
        }
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128, new SecureRandom(keys.getBytes()));
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(byteContent);
            return URLEncoder.encode(parseByte2HexStr(result), "utf-8"); // 加密
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加密 linux
     *
     * @param content 需要加密的内容
     * @param keys    加密密码
     * @return
     */
    private static String linuxEncrypt(String content, String keys) {
        if (Strings.isNull(keys)) {
            keys = password;
        }
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(keys.getBytes());
            kgen.init(128, secureRandom);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(byteContent);
            return URLEncoder.encode(parseByte2HexStr(result), "utf-8"); // 加密
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密 window
     *
     * @param contents 待解密内容
     * @param keys     解密密钥
     * @return
     */
    private static String windowsDecrypt(String contents, String keys) {
        if (Strings.isNull(keys)) {
            keys = password;
        }
        byte[] content = parseHexStr2Byte(contents);
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128, new SecureRandom(keys.getBytes()));
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(content);
            return URLDecoder.decode(new String(result, "utf-8"), "utf-8");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 解密 linux
     *
     * @param contents 待解密内容
     * @param keys     解密密钥
     * @return
     */
    private static String linuxDecrypt(String contents, String keys) {
        if (Strings.isNull(keys)) {
            keys = password;
        }
        byte[] content = parseHexStr2Byte(contents);
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(keys.getBytes());
            kgen.init(128, secureRandom);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(content);
            return URLDecoder.decode(new String(result, "utf-8"), "utf-8");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 将二进制转换成16进制
     *
     * @param buf
     * @return
     */
    private static String parseByte2HexStr(byte buf[]) {
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
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return
     */
    private static byte[] parseHexStr2Byte(String hexStr) {
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
