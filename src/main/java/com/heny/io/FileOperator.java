package com.heny.io;

import java.io.File;

public class FileOperator {

    @SuppressWarnings("unused")
    private static int fileNumber = 0;

    public static void main(String[] args) {
    }

    /**
     * 判断目录下是否存在文件
     * @param file
     * @return
     */
    public static boolean isEmptyDirectory(File file) {
        // sadasd    
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File file2 : files) {
                    if (!isEmptyDirectory(file2)) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 删除文件夹或文件
     * @param file
     */
    public static void deleteFile(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File file2 : files) {
                        deleteFile(file2);
                    }
                }
                file.delete();
            } else if (file.isFile()) {
                System.out.println("删除文件：" + file.getAbsolutePath());
                file.delete();
            }
        }
    }

    /**
     * 打印文件名称
     * @param file
     */
    public static void pringFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                System.out.println(file.getAbsolutePath());
                fileNumber++;
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File file2 : files) {
                        pringFile(file2);
                    }
                }
            }
        }
    }
}
