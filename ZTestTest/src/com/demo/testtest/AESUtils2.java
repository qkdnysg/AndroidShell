package com.demo.testtest;

//import android.annotation.SuppressLint;
import java.security.SecureRandom;
 
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
 
/**
 * 
 * 
 * Author：SHI Xiaolong
 */
public class AESUtils2 {
	
	public static byte[] encrypt(String seed, byte[] plaintext)
            throws Exception {
 
		return encrypt(seed, new String(plaintext)).getBytes();
 
    } 
	
	public static byte[] decrypt(String seed, byte[] encrypted)
            throws Exception {
 
		String encry = new String(encrypted);
		System.out.println("创建字符串成功！");
		String en = decrypt(seed, encry);
		return en.getBytes();
 
    }
 
	private static String encrypt(String seed, String plaintext)
            throws Exception {
 
        byte[] rawKey = getRawKey(seed.getBytes());//获取key
 
        byte[] result = encrypt(rawKey, plaintext.getBytes());
 
        return BytetoHexString(result);
 
    }
 
    private static String decrypt(String seed, String encrypted)
            throws Exception {
 
        byte[] rawKey = getRawKey(seed.getBytes());
 
        byte[] enc = HexStringtoByte(encrypted);//密文转字节数组
 
        byte[] result = decrypt(rawKey, enc);
 
        return new String(result);
 
    }
/**
 * 获取 RawKey
 * @param seed
 * @return
 * @throws Exception
 */
//    @SuppressLint("TrulyRandom")
    private static byte[] getRawKey(byte[] seed) throws Exception {
 
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
 
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
 
        sr.setSeed(seed);
 
        kgen.init(128, sr); // 192 and 256 bits may not be available
 
        SecretKey skey = kgen.generateKey();
 
        byte[] raw = skey.getEncoded();
 
        return raw;
 
    }
/**
 * 加密实现函数 
 * @param byte[] raw KEY
 * @param byte[] plain 明文
 * @return byte[] 密文
 * @throws Exception
 */
    private static byte[] encrypt(byte[] raw, byte[] plain) throws Exception {
 
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
 
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
 
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(
                new byte[cipher.getBlockSize()]));
 
        byte[] encrypted = cipher.doFinal(plain);
 
        return encrypted;
 
    }
/**
 * 解密实现函数 
 * @param raw
 * @param encrypted
 * @return 明文
 * @throws Exception
 */
    private static byte[] decrypt(byte[] raw, byte[] encrypted)
            throws Exception {
 
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
 
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
 
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(
                new byte[cipher.getBlockSize()]));
 
        byte[] decrypted = cipher.doFinal(encrypted);
 
        return decrypted;
 
    }
/**
 * 将16进制字符串转为字节数组 
 * @param hexString
 * @return
 */
    private static byte[] HexStringtoByte(String hexString) {
 
        int len = hexString.length() / 2;
 
        byte[] result = new byte[len];
 
        for (int i = 0; i < len; i++)
 
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2),
                    16).byteValue();////实现功能类似：0x5A转为90
 
        return result;
 
    }
/**
 * 将字节数组buf转为16进制字符串 
 * @param buf
 * @return String
 */
    private static String BytetoHexString(byte[] buf) {
 
        if (buf == null)
 
            return "";
 
        StringBuffer result = new StringBuffer(2 * buf.length);
 
        for (int i = 0; i < buf.length; i++) {
 
            appendHex(result, buf[i]);
 
        }
 
        return result.toString();
 
    }
 
    private final static String HEX = "0123456789ABCDEF";
 
/**
 * 将字节b转为2位16进制字符串形式附加到sb后
 * @param sb
 * @param b
 */
    
    private static void appendHex(StringBuffer sb, byte b) {
 
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
 
    }
 
}
