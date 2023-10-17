package com.nuaa.art.common.utils;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.net.MalformedURLException;

public class FileUtils {
    /**
     * 读取xml文件
     *
     * @param FileUrl 文件url
     * @return {@link Document}
     */
    public static Document readXML(String FileUrl){
        SAXReader reader = new SAXReader();
        Document document = null;
        try {
            document = reader.read(new File(FileUrl));
            return document;
        } catch (DocumentException | MalformedURLException e) {
            LogUtils.error("文件打开失败： "+FileUrl);
            return null;
        }
    }

    /**
     * 保存xml文件
     *
     * @param document 文档
     * @param FileUrl  文件url
     * @return {@link Boolean}
     */
    public static Boolean saveXML(Document document,String FileUrl){
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");
        format.setTrimText(false);
        XMLWriter writer = null;
        try {
            File file = new File(FileUrl);
            if(!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(file);
            writer = new XMLWriter(fos, format);
            writer.write(document);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            LogUtils.error(e.getMessage());
        }
        return true;
    }

    /**
     * 读取excel文件
     *
     * @param FileUrl 文件url
     * @return {@link Workbook}
     */
    public static Workbook readExcel(String FileUrl){
        try {
            FileInputStream fis = new FileInputStream(FileUrl);
//            if(FileUrl.contains(".xlsx"))
//                return new HSSFWorkbook(fis);
//            else if(FileUrl.contains(".xls"))
//                return new XSSFWorkbook(fis);
            return WorkbookFactory.create(fis);
        } catch (IOException e) {
            LogUtils.error(e.getMessage());
        }
        return null;
    }


    /**
     * 保存excel文件
     *
     * @param excelDoc excel文档
     * @param FileUrl  文件url
     * @return {@link Boolean}
     */
    public static <T extends HSSFWorkbook, XSSFWorkbook> Boolean saveExcel(T excelDoc, String FileUrl){
        OutputFormat format = OutputFormat.createPrettyPrint();
        try {
            File file = new File(FileUrl);
            if(!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(file);
            excelDoc.write(fos);
            fos.flush();
            fos.close();
            return true;
        } catch (IOException e) {
            LogUtils.error(e.getMessage());
        }
        return false;
    }

    /**
     * 文件下载方法封装，直接返回响应体
     *
     * @param path     文件路径
     * @param response 响应题
     */
    public static HttpServletResponse download(String path , HttpServletResponse response){
        File file = new File(path);
        // 文件名称
        String filename = file.getName();
        byte[] buffer = new byte[1024];
        BufferedInputStream bis = null;
        OutputStream os = null;
        try {
            //文件是否存在
            if (file.exists()) {
                //设置响应
                response.setContentType("application/octet-stream;charset=UTF-8");
                // 将响应头中的Content-Disposition暴露出来，不然前端获取不到
                response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
                // 在响应头中的Content-Disposition里设置文件名称
                response.setHeader("Content-Disposition","attachment;filename="+filename);
                os = response.getOutputStream();
                bis = new BufferedInputStream(new FileInputStream(file));
                while(bis.read(buffer) != -1){
                    os.write(buffer);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(bis != null) {
                    bis.close();
                }
                if(os != null) {
                    os.flush();
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return response;
    }
}
