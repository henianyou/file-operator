package com.heny.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

public class CSVUtils {

    private static final char MARK = ',';

    public static List<String[]> read(String filePath) {
        return read(filePath, false, 0, Integer.MAX_VALUE);
    }

    /**
     * 读取csv文件
     * @param filePath 文件路径
     * @param includeHeader 返回结果是否排除表头
     * @param offset 偏移量
     * @param 读取条数
     * @return
     */
    public static List<String[]> read(String filePath, boolean excludeHeader, int offset, int limit) {
        List<String[]> retList = new ArrayList<>();

        CsvReader reader = null;
        try {
            checkFile(filePath);
            // 生成CsvReader对象，以,为分隔符，GBK编码方式防止中文乱码
            reader = new CsvReader(filePath, MARK, Charset.forName("UTF-8"));

            if (excludeHeader) {
                // 读取表头
                reader.readHeaders();
            }

            // 跳过空白行

            // 逐条读取记录，直至读完
            int index = 0;
            int maxIndex = offset + limit;
            int colCount;
            while (reader.readRecord()) {
                if (index++ < offset) {
                    continue;
                }
                colCount = reader.getColumnCount();
                String[] arr = new String[colCount];
                for (int i = 0; i < colCount; i++) {
                    arr[i] = reader.get(i);
                }
                retList.add(arr);
                if (index >= maxIndex) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return retList;
    }

    public static boolean write(String filePath, String[] header, List<String[]> records) {
        CsvWriter writer = null;
        try {
            // 生成CsvReader对象，以,为分隔符，GBK编码方式防止中文乱码
            writer = new CsvWriter(filePath, MARK, Charset.forName("GBK"));
            if (header != null) {
                writer.writeRecord(header);
            }
            for (String[] record : records) {
                if (record != null) {
                    writer.writeRecord(record);
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
        return false;
    }

    private static void checkFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (file == null || !file.exists()) {
            throw new FileNotFoundException("文件不存在！");
        }
        if (!file.isFile()) {
            throw new IOException("输入的不是文件路径！");
        }

        // 获得文件名  
        String fileName = file.getName().toLowerCase();
        if (!fileName.endsWith(".csv")) {
            throw new IOException("[" + fileName + "]不是csv文件!");
        }
    }

    public static void main(String[] args) {
        String filePath = CSVUtils.class.getClassLoader().getResource("test.csv").getPath();
        List<String[]> list = read(filePath);
        for (String[] arr : list) {
            for (String string : arr) {
                System.out.print(string + "\t");
            }
            System.out.println();
        }

//        write("D:/test.csv", null, list);
    }
}
