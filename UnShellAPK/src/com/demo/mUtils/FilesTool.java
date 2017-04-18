package com.demo.mUtils;

import java.io.File;

public class FilesTool {
	/**
	 * 删除指定文件夹下所有文件
	 * param dirPath 文件夹完整绝对路径
	 * 
	 * 
	 */
	  public static boolean delAllFilesInDir(String dirPath) {
       boolean flag = false;
       File file = new File(dirPath);
       if (!file.exists()) {
         return flag;
       }
       if (!file.isDirectory()) {
         return flag;
       }
       String[] tempFileList = file.list();
       File temp = null;
       for (int i = 0; i < tempFileList.length; i++) {
          if (dirPath.endsWith(File.separator)) {
             temp = new File(dirPath + tempFileList[i]);
          } else {
              temp = new File(dirPath + File.separator + tempFileList[i]);
          }
          if (temp.isFile()) {
             temp.delete();
          }
          
       }
       flag = true;
       return flag;
     }

}
