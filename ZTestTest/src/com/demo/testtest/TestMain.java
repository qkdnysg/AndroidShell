package com.demo.testtest;

import java.util.Arrays;
import java.util.UUID;

public class TestMain {

	public static void main(String[] args) {
		
		InserIntoTable.InserIntoTablewithNrows(10);
		UUID uuid = UUID.randomUUID();
		String uuidstr = uuid.toString();
		String s = uuidstr;
		uuidstr = s.substring(0,8)+s.substring(9,13)+s.substring(14,18)+s.substring(19,23)+s.substring(24);
        System.out.println("uuidstr:" + uuidstr + "\n"
        		+ "length:" + uuidstr.length());
		String poem = "This is a famous poem in China : "
				+ "大江东去，浪淘尽，千古风流人物。"
				+ "故垒西边，人道是，三国周郎赤壁。";
		
		long ltime = System.currentTimeMillis();
		//System.out.println("ltime：" + ltime);
		String seed = String.valueOf(ltime);
		//System.out.println("seed：" + seed);
		byte[] bpoem = poem.getBytes();
		//System.out.println("明文内存地址：" + bpoem + "长度: " + bpoem.length);//打印结果为在某内存地址的Byte型数组
		//System.out.println("明文字节数组：" + Arrays.toString(bpoem));
		try {
			byte[] poemEn = AESUtils2.encrypt(seed, bpoem);
			//System.out.println("密文内存地址：" + poemEn + "长度: " + poemEn.length);
			//System.out.println("密文字节数组：" + Arrays.toString(poemEn));

			byte[] poemDe = AESUtils2.decrypt(seed, poemEn);
			//System.out.println("解密所得明文内存地址：" + poemDe + "长度: " + poemDe.length);
			//System.out.println("解密所得明文字节数组：" + Arrays.toString(poemDe));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		byte[] Original = new byte[]{33,23,35,33,23,35,33,23,35,33,23,35};
		//String OriginalStr = new String(Original);
		System.out.println("原始字节数组：" + Arrays.toString(Original) + 
				"长度：" + Original.length);
		byte[] rc4en = RC4.RC4Base(Original, seed);
		System.out.println("RC4加密后字节数组：" + Arrays.toString(rc4en) + 
				"长度：" + rc4en.length);
		byte[] rc4de = RC4.RC4Base(rc4en, seed);
		System.out.println("RC4解密后字节数组：" + Arrays.toString(rc4de) + 
				"长度：" + rc4de.length);
		
		//RC4Tool.parseOrCreateRC4(aInput, aKey)
		//System.out.println("Byte.MAX_VALUE:" + Byte.MAX_VALUE);
		//System.out.println("Byte.MIN_VALUE:" + Byte.MIN_VALUE);
		
//		String str = "Hello world!";
//		  // string转byte
//		  byte[] bs = str.getBytes();
//		  System.out.println(bs);//打印结果为在某内存地址的Byte型数组
//		  System.out.println(Arrays.toString(bs));
//		  
//		  // byte转string
//		  String str2 = new String(bs);
//		  System.out.println(str2);
	
	}//main

}//TestMain
