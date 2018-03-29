package com.xckoohttp.net;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Tools {

    public static <T> T checkNotNull(T object, String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
        return object;
    }

    public static String md5(String s) {
        try {
            byte[] btInput = s.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("md5");
            mdInst.update(btInput);
            return bytes2HexString(mdInst.digest());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String sha1(String strSrc) {
        MessageDigest md = null;
        String strDes = null;
        byte[] bt = strSrc.getBytes();
        try {
            md = MessageDigest.getInstance("SHA-1");// 将此换成SHA-1、SHA-512、SHA-384等参数
            md.update(bt);
            strDes = bytes2HexString(md.digest()); // to HexString
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        return strDes;
    }

    public static String bytes2HexString(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(Integer.toHexString((b >> 4) & 0xf));
            builder.append(Integer.toHexString(b & 0xf));
        }
        return builder.toString();
    }

    public static String bytes2Utf8String(byte[] bytes) {
        try {
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static byte[] readBAFromInputStream(InputStream inStream) {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        try {
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            inStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outStream.toByteArray();
    }


    private static final String KEY_ALGORITHM = "AES";
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/CBC/PKCS7Padding";//默认的加密算法
    private static final String ivString = "1111111111111111";
    private static final String TAG = "Tools";

    public static String encryptAES(String content, String password) {
        try {
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);// 创建密码器

            byte[] byteContent = content.getBytes("utf-8");

            IvParameterSpec ivSpec = new IvParameterSpec(ivString.getBytes("utf-8"));

            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(password), ivSpec);// 初始化为加密模式的密码器

            byte[] result = cipher.doFinal(byteContent);// 加密

//            Log.i(TAG, "encryptAES: 原串:" + content);
//            Log.i(TAG, "encryptAES: 加密后:" + Base64.encodeToString(result, Base64.NO_WRAP));
            return Base64.encodeToString(result, Base64.NO_WRAP);//通过Base64转码返回
        } catch (Exception ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public static String decryptAES(String content, String password) {

        try {
            //实例化
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);

            IvParameterSpec ivSpec = new IvParameterSpec(ivString.getBytes("utf-8"));
            //使用密钥初始化，设置为解密模式
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(password), ivSpec);

            //执行操作
            byte[] result = cipher.doFinal(Base64.decode(content, Base64.NO_WRAP));

//            Log.i(TAG, "decryptAES: 加密后:" + content);
//            Log.i(TAG, "decryptAES: 解密出原串:" + new String(result, "utf-8"));

            return new String(result, "utf-8");
        } catch (Exception ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    private static SecretKeySpec getSecretKey(String password) {
        return generateKey(password);

    }

    private static final String HASH_ALGORITHM = "SHA-256";

    private static SecretKeySpec getStandardKey(final String password) {
        return new SecretKeySpec(password.getBytes(), KEY_ALGORITHM);
    }

    private static SecretKeySpec generateKey(final String password) {
        try {
            final MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] bytes = password.getBytes("UTF-8");
            digest.update(bytes, 0, bytes.length);
            byte[] key = digest.digest();
            return new SecretKeySpec(key, KEY_ALGORITHM);
        } catch (Exception e) {
            return null;
        }
    }
}
