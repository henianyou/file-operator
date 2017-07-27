package com.heny.io;


import java.io.File;

/**
 * 清理maven下载失败的文件
 * 
 * @Description
 * @author hny
 * @date 2017年6月6日 上午11:05:20
 * @version V1.0
 * @since jdk1.7
 */
public class CleanMvn {
    public static void main(String[] args) {
        findAndDelete(new File("D:\\ProgramDeveloper\\maven\\mavenRepository"));
    }

    public static boolean findAndDelete(File file) {
        if (!file.exists()) {
        } else if (file.isFile()) {
            if (file.getName().endsWith("lastUpdated")) {
                deleteFile(file.getParentFile());
                return true;
            }
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                if (findAndDelete(f)) {
                    break;
                }
            }
        }
        return false;
    }

    public static void deleteFile(File file) {
        if (!file.exists()) {
        } else if (file.isFile()) {
            System.out.println("删除文件:" + file.getAbsolutePath());
            file.delete();
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                deleteFile(f);
            }
            System.out.println("删除文件夹:" + file.getAbsolutePath());
            System.out.println("====================================");
            file.delete();
        }
    }
}
