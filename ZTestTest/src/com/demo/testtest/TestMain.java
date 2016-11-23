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
				+ "�󽭶�ȥ�����Ծ���ǧ�ŷ������"
				+ "�������ߣ��˵��ǣ��������ɳ�ڡ�";
		
		long ltime = System.currentTimeMillis();
		//System.out.println("ltime��" + ltime);
		String seed = String.valueOf(ltime);
		//System.out.println("seed��" + seed);
		byte[] bpoem = poem.getBytes();
		//System.out.println("�����ڴ��ַ��" + bpoem + "����: " + bpoem.length);//��ӡ���Ϊ��ĳ�ڴ��ַ��Byte������
		//System.out.println("�����ֽ����飺" + Arrays.toString(bpoem));
		try {
			byte[] poemEn = AESUtils2.encrypt(seed, bpoem);
			//System.out.println("�����ڴ��ַ��" + poemEn + "����: " + poemEn.length);
			//System.out.println("�����ֽ����飺" + Arrays.toString(poemEn));

			byte[] poemDe = AESUtils2.decrypt(seed, poemEn);
			//System.out.println("�������������ڴ��ַ��" + poemDe + "����: " + poemDe.length);
			//System.out.println("�������������ֽ����飺" + Arrays.toString(poemDe));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		byte[] Original = new byte[]{33,23,35,33,23,35,33,23,35,33,23,35};
		//String OriginalStr = new String(Original);
		System.out.println("ԭʼ�ֽ����飺" + Arrays.toString(Original) + 
				"���ȣ�" + Original.length);
		byte[] rc4en = RC4.RC4Base(Original, seed);
		System.out.println("RC4���ܺ��ֽ����飺" + Arrays.toString(rc4en) + 
				"���ȣ�" + rc4en.length);
		byte[] rc4de = RC4.RC4Base(rc4en, seed);
		System.out.println("RC4���ܺ��ֽ����飺" + Arrays.toString(rc4de) + 
				"���ȣ�" + rc4de.length);
		
		//RC4Tool.parseOrCreateRC4(aInput, aKey)
		//System.out.println("Byte.MAX_VALUE:" + Byte.MAX_VALUE);
		//System.out.println("Byte.MIN_VALUE:" + Byte.MIN_VALUE);
		
//		String str = "Hello world!";
//		  // stringתbyte
//		  byte[] bs = str.getBytes();
//		  System.out.println(bs);//��ӡ���Ϊ��ĳ�ڴ��ַ��Byte������
//		  System.out.println(Arrays.toString(bs));
//		  
//		  // byteתstring
//		  String str2 = new String(bs);
//		  System.out.println(str2);
	
	}//main

}//TestMain
