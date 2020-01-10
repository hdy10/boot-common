package com.github.hdy.common.util;

import lombok.Getter;
import lombok.Setter;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 文件工具类
 *
 * @author 贺大爷
 * @date 2018/6/55
 */
public class FileUtil {
    /**
     * 上传文件(支持多文件)
     *
     * @param request
     * @param absolutePath 绝对路劲
     */
    public static List<Files> upload(HttpServletRequest request, String absolutePath) throws IOException {
        if (!new File(absolutePath).exists()) {
            new File(absolutePath).mkdirs();
        }
        List<Files> result = new ArrayList<>();
        //将request变成多部分request
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
        //获取multiRequest 中所有的文件名
        Iterator iter = multiRequest.getFileNames();
        int i = 0;
        while (iter.hasNext()) {
            //一次遍历所有文件
            MultipartFile file = multiRequest.getFile(iter.next().toString());
            String originalFileName = file.getOriginalFilename();
            String suffix = originalFileName.substring(originalFileName.lastIndexOf("."));
            String newFileName = IdWorker.getIdStr();
            if (file != null) {
                //上传
                file.transferTo(new File(absolutePath + "/" + newFileName + suffix));
            }
            Files files = new Files();
            files.setOriginalFileName(originalFileName);
            files.setFileName(newFileName);
            files.setSize(file.getSize());
            files.setSuffix(suffix.substring(1));
            files.setIndex(i);
            result.add(files);
            i++;
        }
        return result;
    }

    /**
     * 删除文件
     *
     * @param fileName
     * @return
     */
    public static void deleteFile(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
        } else {
            file.delete();
        }
    }

    /**
     * 导出excel
     *
     * @param list      数据
     * @param fileName  文件名
     * @param sheetName sheet名
     * @param titles    表头
     * @param fields    表头对应的字段
     */
    public static void export(List<Map<String, Object>> list, String fileName, String sheetName, String[] titles, String[] fields, HttpServletResponse response) {
        // 创建一个新的Excel
        HSSFWorkbook workBook = new HSSFWorkbook();
        // 创建sheet页
        HSSFSheet sheet = workBook.createSheet();
        // sheet页名称
        workBook.setSheetName(0, sheetName);
        HSSFCellStyle cellStyle = workBook.createCellStyle();// 创建一个单元的样式
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 设置水平居中
        cellStyle.setVerticalAlignment(HSSFCellStyle.ALIGN_CENTER);// 上下居中
        // 设置第一行为Header
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = null;
        // 设置字符集
        // HSSFRichTextString str = new HSSFRichTextString();
        // 设置第一行各列列名
        for (int i = 0; i < titles.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(titles[i]);
            cell.setCellStyle(cellStyle);
            sheet.setColumnWidth(i, 5000);
        }
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = list.get(i);
            // 每一列设置值
            Object[] values = new Object[titles.length];
            for (int k = 0; k < fields.length; k++) {
                values[k] = map.get(fields[k]);
            }
            row = sheet.createRow(i + 1);
            for (int j = 0; j < values.length; j++) {
                cell = row.createCell(j);
                String value = String.valueOf(values[j]);
                if (value == null || value.equalsIgnoreCase("null")) {
                    value = "-";
                }
                cell.setCellValue(value);
                cell.setCellStyle(cellStyle);
            }
        }
        // 通过Response把数据以Excel格式保存
        try {
            response.reset();
            response.setContentType("application/msexcel;charset=UTF-8");
            response.addHeader("Content-Disposition", "attachment;filename=\"" + new String((fileName + ".xls").getBytes("GB2312"), "ISO8859-1") + "\"");
            OutputStream out = response.getOutputStream();
            workBook.write(out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

@Getter
@Setter
class Files {
    private String originalFileName;    // 原文件名称
    private String fileName;            // 储存文件名称
    private long size;                  // 文件大小
    private String suffix;              // 文件类型
    private int index;                  // 第几个文件
}
