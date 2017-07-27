package com.heny.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtils {

    /**
     * 读取excel文件(读取全部数据)
     * 
     * @param filePath 文件路径
     * @return
     * @throws IOException
     */
    public static String[][] read(String filePath) throws IOException {
        return read(filePath, 0, 0);
    }

    /**
     * 读取excel文件(默认读取第一个sheet的数据)
     * 
     * @param filePath 文件路径
     * @param startRows 开始行 0代表第一行
     * @return
     * @throws IOException
     */
    public static String[][] read(String filePath, int startRows) throws IOException {
        return read(filePath, startRows, 0);
    }

    /**
     * 读取excel文件
     * 
     * @param filePath 文件路径
     * @param startRows 开始行 0代表第一行
     * @param sheetIndex sheet下标
     * @return
     * @throws IOException
     */
    public static String[][] read(String filePath, int startRows, int sheetIndex) throws IOException {
        String fileType = filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length());
        InputStream is = null;
        Workbook wb = null;
        if ("xls".equals(fileType) || "xlsx".equals(fileType)) {
            is = new FileInputStream(filePath);
            wb = "xls".equals(fileType) ? new HSSFWorkbook(is) : new XSSFWorkbook(is);
        } else {
            System.out.println("您输入的excel格式不正确");
            return null;
        }

        Sheet sheet = wb.getSheetAt(sheetIndex);
        if (null == sheet) {
            wb.close();
            return null;
        }
        int rowSize = sheet.getLastRowNum() + 1;
        int tempColSize = 0;
        List<String[]> list = new ArrayList<String[]>();
        for (int j = startRows; j < rowSize; j++) {
            Row row = sheet.getRow(j);
            if (row == null) {
                continue;
            }
            int colSize = row.getLastCellNum();
            if (tempColSize > colSize) {
                colSize = tempColSize;
            }
            String[] values = new String[colSize];
            for (short k = 0; k < colSize; k++) {
                String value = "";
                Cell cell = row.getCell(k);
                if (cell == null) {
                    continue;
                }
                switch (cell.getCellType()) {
                case Cell.CELL_TYPE_STRING:
                    value = cell.getStringCellValue();
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        Date date = cell.getDateCellValue();
                        if (null != date) {
                            value = new SimpleDateFormat("yyyy-MM-dd").format(date);
                        }
                    } else {
                        value = new DecimalFormat("0").format(cell.getNumericCellValue());
                    }
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    value = cell.getBooleanCellValue() == true ? "Y" : "N";
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    // 导入时如果为公式生成的数据则无值
                    if (!"".equals(cell.getStringCellValue())) {
                        value = cell.getStringCellValue();
                    } else {
                        value = cell.getNumericCellValue() + "";
                    }
                    break;
                default:
                    break;
                }
                values[k] = value;
//                 System.out.print(cell.getStringCellValue() + " ");
            }
            list.add(values);
//             System.out.println();
        }
        String[][] resultArray = new String[list.size()][tempColSize];
        for (int h = 0; h < list.size(); h++) {
            resultArray[h] = (String[]) list.get(h);
        }
        if (null != is) {
            is.close();
        }
        if (wb != null) {
            wb.close();
        }
        return resultArray;
    }

    /**
     * 数据写入excel文件
     * @param path 输出文件路径与文件名
     * @throws Exception
     */
    public static boolean write(String path, String[][] data) throws Exception {
        return write(path, data, null);
    }

    /**
     * 数据写入excel文件
     * @param path 输出文件路径与文件名
     * @param data 写入数据-二位数组
     * @param titles 标题
     * @return 写入成功返回true否则返回false
     * @throws Exception
     */
    public static boolean write(String path, String[][] data, String[] titles) throws Exception {
        return write(path, data, titles, 0, 0);
    }

    /**
     * 数据写入excel文件
     * @param path 输出文件路径与文件名
     * @param data 写入数据-二维数组
     * @param titles 标题
     * @param startRow 开始行 0代表第一行
     * @param startCol 开始列 0代表第一列
     * @return 写入成功返回true否则返回false
     * @throws Exception
     */
    public static boolean write(String path, String[][] data, String[] titles, int startRow, int startCol)
            throws Exception {

        // 创建工作文档对象
        Workbook wb = null;
        if (path.endsWith(".xls")) {
            wb = new HSSFWorkbook();
        } else if (path.endsWith(".xlsx")) {
            wb = new XSSFWorkbook();
        } else {
            System.out.println("您的文档格式不正确！");
            return false;
        }

        File file = new File(path);
        int index = 0;
        String suffix = path.substring(path.lastIndexOf("."), path.length());
        String temp = null;
        while (file.exists()) {
            index++;
            temp = path.substring(0, path.lastIndexOf(".")) + "(" + index + ")" + suffix;
            file = new File(temp);
        }
        if (temp != null) {
            path = temp;
        }

        // 创建sheet对象
        Sheet sheet1 = (Sheet) wb.createSheet("sheet1");
        // 循环写入行数据
        Cell cell = null;
        if (null != titles) {
            CellStyle style = wb.createCellStyle();
            Font font = wb.createFont();
            font.setBoldweight(Font.BOLDWEIGHT_BOLD);
            style.setFont(font);
            style.setAlignment(CellStyle.ALIGN_CENTER);
            
            Row row = sheet1.createRow(startRow);
            for (int i = 0; i < titles.length; i++) {
                cell = row.createCell(i + startCol);
                cell.setCellValue(titles[i]);
                cell.setCellStyle(style);
            }
            startRow++;
        }
        for (int i = 0; i < data.length; i++) {
            Row row = sheet1.createRow(i + startRow);
            for (int j = 0; j < data[i].length; j++) {
                cell = row.createCell(j + startCol);
                cell.setCellValue(data[i][j]);
            }
        }
        // 创建文件流
        OutputStream os = new FileOutputStream(path);
        // 写入数据
        wb.write(os);
        // 关闭文件流
        os.close();
        wb.close();
        System.out.println("写入完成！");
        return true;
    }

    public static void main(String[] args) throws Exception {
        String filePath = "D:/excel-test.xlsx";
        String[][] datas = { { "1", "2" }, { "3", "4" } };
        String[] title = { "a", "b" };
        write(filePath, datas, title);
        datas = read(filePath);
        for (String[] data : datas) {
            for (String string : data) {
                System.out.print(string + "\t");
            }
            System.out.println();
        }
        
    }
}