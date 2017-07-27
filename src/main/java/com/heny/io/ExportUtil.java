package com.heny.io;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;

/**
 * 数据导出工具类
* @Description 
* @author hny
* @date 2017年2月24日 下午3:16:56 
* @version V1.0
* @since jdk1.7
 */
public class ExportUtil {

    /** CSV文件列分隔符 */
    private static final String CSV_COLUMN_SEPARATOR = ",";
    /** CSV文件换行符 */
    private static final String CSV_NEXT_LINE = "\r\n";

    /**
     * 导出生成csv文件
     * @param titles 标题头
     * @param propertys 每一列标题头对应数据集合里对象的属性名或Map的key
     * @param list 数据集合 元素可以为Map或自定义类型
     * @param fileName 文件名称(注意不能有空格以及冒号)
     * @param request
     * @param response
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    @SuppressWarnings("unchecked")
    public static <T> void exportCsv(String[] titles, String[] propertys, List<T> list, String fileName,
            HttpServletResponse response) throws IOException, IllegalArgumentException, IllegalAccessException {

        if (!fileName.toLowerCase().endsWith(".csv")) {
            fileName += ".csv";
        }

        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));

        // 构建输出流，同时指定编码
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), "gbk"));

        // csv文件是逗号分隔，除第一个外，每次写入一个单元格数据后需要输入逗号
        for (String title : titles) {
            bw.write(title);
            bw.write(CSV_COLUMN_SEPARATOR);
        }
        // 写完文件头后换行
        bw.write(CSV_NEXT_LINE);

        // 写内容
        for (Object obj : list) {
            String value;
            if (obj instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) obj;
                for (String property : propertys) {
                    value = String.valueOf(map.get(property));
                    bw.write(value);
                    // 如果包含:说明是日期最后写一个|否则日期不显示秒
                    if ((value).indexOf(":") > -1) {
                        bw.write("|");
                    }
                    bw.write(CSV_COLUMN_SEPARATOR);
                }
            } else {
                // 利用反射获取所有字段
                Field[] fields = obj.getClass().getDeclaredFields();
                for (String property : propertys) {
                    for (Field field : fields) {
                        // 设置字段可见性
                        field.setAccessible(true);
                        if (property.equals(field.getName())) {
                            value = String.valueOf(field.get(obj));
                            bw.write(value);
                            // 如果包含:说明是日期最后写一个|否则日期不显示秒
                            if ((value).indexOf(":") > -1) {
                                bw.write("|");
                            }
                            bw.write(CSV_COLUMN_SEPARATOR);
                            break;
                        }
                    }
                }
            }
            // 写完一行换行
            bw.write(CSV_NEXT_LINE);
        }
        // 刷新缓冲
        bw.flush();
        bw.close();
    }

    /**
     * 导出excel文件
     * @param titles 表头
     * @param datas 表数据
     * @param fileName 导出文件名称
     * @param response
     * @throws Exception 
     */
    public static void exportExcel(String[] titles, String[][] datas, String fileName, HttpServletResponse response) throws Exception {

        if (!fileName.toLowerCase().endsWith(".xlsx")) {
            if (fileName.toLowerCase().endsWith(".xls")) {
                fileName.replace(".xls", ".xlsx");
            } else {
                fileName += ".xlsx";
            }
        }

        // 临时文件路径
        String excelFile = "../" + UUID.randomUUID().toString() + ".xlsx";
        try {
            ExcelUtils.write(excelFile, datas, titles);
            File file = new File(excelFile);
            byte[] data = FileUtils.readFileToByteArray(file);
            response.reset();
            response.setContentType("application/octet-stream");
            response.addHeader("Content-Length", data.length + "");
            response.addHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(fileName, "UTF-8"));
            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            outputStream.write(data);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            throw e;
        } finally {
            File file = new File(excelFile);
            // 路径为文件且不为空则进行删除
            if (file.isFile() && file.exists()) {
                file.delete();
            }
        }
    }
}
