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
 * Author��SHI Xiaolong
 */
public class AESUtils2 {
	
	public static byte[] encrypt(String seed, byte[] plaintext)
            throws Exception {
 
		return encrypt(seed, new String(plaintext)).getBytes();
 
    } 
	
	public static byte[] decrypt(String seed, byte[] encrypted)
            throws Exception {
 
		String encry = new String(encrypted);
		System.out.println("�����ַ����ɹ���");
		String en = decrypt(seed, encry);
		return en.getBytes();
 
    }
 
	private static String encrypt(String seed, String plaintext)
            throws Exception {
 
        byte[] rawKey = getRawKey(seed.getBytes());//��ȡkey
 
        byte[] result = encrypt(rawKey, plaintext.getBytes());
 
        return BytetoHexString(result);
 
    }
 
    private static String decrypt(String seed, String encrypted)
            throws Exception {
 
        byte[] rawKey = getRawKey(seed.getBytes());
 
        byte[] enc = HexStringtoByte(encrypted);//����ת�ֽ�����
 
        byte[] result = decrypt(rawKey, enc);
 
        return new String(result);
 
    }
/**
 * ��ȡ RawKey
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
 * ����ʵ�ֺ��� 
 * @param byte[] raw KEY
 * @param byte[] plain ����
 * @return byte[] ����
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
 * ����ʵ�ֺ��� 
 * @param raw
 * @param encrypted
 * @return ����
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
 * ��16�����ַ���תΪ�ֽ����� 
 * @param hexString
 * @return
 */
    private static byte[] HexStringtoByte(String hexString) {
 
        int len = hexString.length() / 2;
 
        byte[] result = new byte[len];
 
        for (int i = 0; i < len; i++)
 
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2),
                    16).byteValue();////ʵ�ֹ������ƣ�0x5AתΪ90
 
        return result;
 
    }
/**
 * ���ֽ�����bufתΪ16�����ַ��� 
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
 * ���ֽ�bתΪ2λ16�����ַ�����ʽ���ӵ�sb��
 * @param sb
 * @param b
 */
    
    private static void appendHex(StringBuffer sb, byte b) {
 
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
 
    }
 
}
