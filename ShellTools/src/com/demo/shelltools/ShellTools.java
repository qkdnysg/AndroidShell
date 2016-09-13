package com.demo.shelltools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
//import java.sql.*;
import java.util.zip.Adler32;

//import com.mysql.jdbc.Connection;


public class ShellTools {
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			File payloadFile = new File("workspace/OriginalAPK.apk");   //待加固APK
			System.out.println("OriginalAPK size: "+payloadFile.length());
			File unShellDexFile = new File("workspace/classes.dex");	//脱壳程序dex文件
			
			
			KeyEntity ket = DButils.getKeyEntityFromDB();
						
			byte[] payloadArray = addShell(readFileToBytes(payloadFile), ket.getKeyvalue());
			//以字节数组形式读出待加固APK，随后对其加壳
			byte[] unShellDexArray = readFileToBytes(unShellDexFile);
			//以字节数组形式读出脱壳APK的dex文件
			int shellID = ket.getId();
			int payloadLen = payloadArray.length;
			int unShellDexLen = unShellDexArray.length;
			int totalLen = payloadLen + unShellDexLen + 4 + 4;
			//最后4字节用来存放待加固APK大小//再加的4字节为加固ID的长度
			byte[] newdex = new byte[totalLen]; 
			
			//先将原始的脱壳程序dex文件拷入newdex中
			System.arraycopy(unShellDexArray, 0, newdex, 0, unShellDexLen);
			//再在脱壳程序dex文件后拷入壳APK的内容
			System.arraycopy(payloadArray, 0, newdex, unShellDexLen, payloadLen);
			//添加壳APK大小
			System.arraycopy(intToByte(payloadLen), 0, newdex, totalLen-4-4, 4);
			//最后添加加固ID
			System.arraycopy(intToByte(shellID), 0, newdex, totalLen-4, 4);
			
			//此处的修改顺序要特别注意！踏马的,为此折腾了一天！
			//修改DEX文件头文件大小值ֵ
			fixFileSizeHeader(newdex);
			//修改DEX文件头SHA-1哈希值 
			fixSHA1Header(newdex);
			//修改DEX文件头adler32校验值ֵ
			fixCheckSumHeader(newdex);
			
			

			String str = "workspace/classesWithPayload.dex";
			File file = new File(str);
			if (!file.exists()) {
				file.createNewFile();
			}
			
			FileOutputStream localFileOutputStream = new FileOutputStream(str);
			localFileOutputStream.write(newdex);
			localFileOutputStream.flush();
			localFileOutputStream.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//加壳方法，此处为逐字节与0xFF异或，相当于逐字节取反，
	//然后再使用加固ID对应的key进行了RC4加密
	private static byte[] addShell(byte[] srcdata, String key) throws Exception{
		
		for(int i = 0; i<srcdata.length; i++){
			srcdata[i] = (byte)(0xFF ^ srcdata[i]);
		}
		
		srcdata = RC4.RC4Base(srcdata, key);//RC4加密
		System.out.println("srcdata.length: " + srcdata.length);
				
		return srcdata;
	}

	
	/**
	 * int转byte[]
	 * @param number
	 * @return
	 */
	public static byte[] intToByte(int number) {
		byte[] b = new byte[4];
		for (int i = 3; i >= 0; i--) {
			b[i] = (byte) (number % 256);
			number >>= 8;
		}
		return b;
	}

	/**
	 * 修改dex文件头，checksum: adler32校验值ֵ
	 * @param dexBytes
	 */
	private static void fixCheckSumHeader(byte[] dexBytes) {
		Adler32 adler = new Adler32();
		adler.update(dexBytes, 12, dexBytes.length - 12);////从12到文件末尾计算adler32校验值ֵ
		long value = adler.getValue();
		int va = (int) value;
		byte[] newcs = intToByte(va);
		//高位在前，低位在前掉个
		byte[] recs = new byte[4];
		for (int i = 0; i < 4; i++) {
			recs[i] = newcs[newcs.length - 1 - i];
			System.out.println(Integer.toHexString(newcs[i]));
		}
		System.arraycopy(recs, 0, dexBytes, 8, 4);//校验值赋值（8-11）
		System.out.println(Long.toHexString(value));
		System.out.println();
	}

	/**
	 * 修改dex头 sha-1值ֵ
	 * @param dexBytes
	 * @throws NoSuchAlgorithmException
	 */
	private static void fixSHA1Header(byte[] dexBytes)
			throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.update(dexBytes, 32, dexBytes.length - 32);//从32为到结束计算sha-1哈希值ֵ
		byte[] newdt = md.digest();
		System.arraycopy(newdt, 0, dexBytes, 12, 20);//修改sha-1值（12-31）
		String hexstr = "";
		for (int i = 0; i < newdt.length; i++) {
			hexstr += Integer.toString((newdt[i] & 0xff) + 0x100, 16)
					.substring(1);
		}
		System.out.println(hexstr);
	}

	/**
	 * 修改dex头 file_size值ֵ
	 * @param dexBytes
	 */
	private static void fixFileSizeHeader(byte[] dexBytes) {
		//新文件长度
		byte[] newfs = intToByte(dexBytes.length);
		System.out.println(Integer.toHexString(dexBytes.length));
		byte[] refs = new byte[4];
		//高位在前，低位在前掉个个
		for (int i = 0; i < 4; i++) {
			refs[i] = newfs[newfs.length - 1 - i];
			System.out.println(Integer.toHexString(newfs[i]));
		}
		System.arraycopy(refs, 0, dexBytes, 32, 4);//�޸ģ�32-35��
	}


	/**
	 * 以字节数组形式读取文件内容
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private static byte[] readFileToBytes(File file) throws IOException {
		byte[] arrayOfByte = new byte[1024];
		ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
		FileInputStream fis = new FileInputStream(file);
		while (true) {
			int i = fis.read(arrayOfByte);
			if (i != -1) {
				localByteArrayOutputStream.write(arrayOfByte, 0, i);
			} else {
				return localByteArrayOutputStream.toByteArray();
			}
		}
	}
}
