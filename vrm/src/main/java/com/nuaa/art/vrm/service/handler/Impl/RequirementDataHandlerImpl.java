package com.nuaa.art.vrm.service.handler.impl;

import com.nuaa.art.common.utils.FileUtils;
import com.nuaa.art.common.utils.LogUtils;
import com.nuaa.art.vrm.entity.NaturalLanguageRequirement;
import com.nuaa.art.vrm.entity.StandardRequirement;
import com.nuaa.art.vrm.service.dao.DaoHandler;
import com.nuaa.art.vrm.service.dao.NaturalLanguageRequirementService;
import com.nuaa.art.vrm.service.dao.StandardRequirementService;
import com.nuaa.art.vrm.service.handler.RequirementDataHandler;
import jakarta.annotation.Resource;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.IOException;
import java.util.List;

@Service
public class RequirementDataHandlerImpl implements RequirementDataHandler {
    @Resource
    DaoHandler daoHandler;
    /**
     * 导入需求
     *
     * @param systemId 项目id
     * @param fileUrl  文件名称
     * @return int 返回导入条目号
     */
    @Override
    @Transactional
    public int importFromFile(int systemId, String fileUrl) {
        Workbook workbook = FileUtils.readExcel(fileUrl);
        if(workbook == null){
            return -1;
        }
        try{
            Sheet sheetAt = workbook.getSheetAt(0);
            int count = 0;
            daoHandler.getDaoService(NaturalLanguageRequirementService.class).deleteNLRById(systemId);
            for (Row row : sheetAt) {
                if(row.getCell(0)==null)
                    continue;
                int reqNum = (int) row.getCell(0).getNumericCellValue();
                if (reqNum == 0)
                    continue;
                String content = row.getCell(1).getStringCellValue();
                NaturalLanguageRequirement requirement = new NaturalLanguageRequirement();
                requirement.setReqContent(content);
                requirement.setSystemId(systemId);
                requirement.setReqExcelId(reqNum);
                daoHandler.getDaoService(NaturalLanguageRequirementService.class).insertNLR(requirement);
                count++;
            }
            // 5、关闭流
            workbook.close();
            return count;
        } catch (Exception e){
            LogUtils.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 导出需求
     *
     * @param systemId  系统编号
     * @param exportUrl 导出文件名
     * @return int
     */
    @Override
    public int exportToFile(int systemId, String exportUrl) {
        int count=1;
        int num = 1;

        List<NaturalLanguageRequirement> naturals = daoHandler
                .getDaoService(NaturalLanguageRequirementService.class)
                .listNaturalLanguageRequirementBySystemId(systemId);

        List<StandardRequirement> standards = daoHandler
                .getDaoService(StandardRequirementService.class).listStandardRequirementBySystemId(systemId);


        HSSFWorkbook wb = new HSSFWorkbook();
        // 创建HSSFSheet对象
        HSSFSheet sheet = wb.createSheet("规范化需求");
        // setSizeColumn(sheet,15);
        sheet.setColumnWidth(0, 15 * 256);
        sheet.setColumnWidth(1, 30 * 256);
        sheet.setColumnWidth(2, 50 * 256);

        // 设置单元格样式
        HSSFCellStyle cellStyle = wb.createCellStyle();

        // 字体样式
        HSSFFont fontStyle = wb.createFont();
//			fontStyle.setBold(true);
        // 字体
        fontStyle.setFontName("TimesNewRoma");
        // 大小
        fontStyle.setFontHeightInPoints((short) 11);
        // 水平居中
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        // 垂直居中
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 将字体样式添加到单元格样式中
        cellStyle.setFont(fontStyle);
        cellStyle.setWrapText(true);

        // 设置单元格样式
        HSSFCellStyle cellTitleStyle = wb.createCellStyle();
        HSSFFont fontTitleStyle = wb.createFont();
        fontTitleStyle.setBold(true);
        // 字体
        fontTitleStyle.setFontName("TimesNewRoma");
        // 大小
        fontTitleStyle.setFontHeightInPoints((short) 11);
        // 水平居中
        cellTitleStyle.setAlignment(HorizontalAlignment.CENTER);
        // 垂直居中
        cellTitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 将字体样式添加到单元格样式中
        cellTitleStyle.setFont(fontTitleStyle);
        cellTitleStyle.setWrapText(true);

        HSSFRow rowTitle;
        HSSFCell cellTitle1;
        HSSFCell cellTitle2;
        HSSFCell cellTitle3;
        // 创建一行数据
        rowTitle = sheet.createRow(0);
        cellTitle1 = rowTitle.createCell(0);
        // 设值
        cellTitle1.setCellValue("原始需求条目编号");
        cellTitle1.setCellStyle(cellTitleStyle);
        cellTitle2 = rowTitle.createCell(1);
        // 设值
        cellTitle2.setCellValue("原始需求条目内容");
        cellTitle2.setCellStyle(cellTitleStyle);
        cellTitle3 = rowTitle.createCell(2);
        // 设值
        cellTitle3.setCellValue("规范化需求条目内容");
        cellTitle3.setCellStyle(cellTitleStyle);

        // 填充内容
        HSSFRow row;
        HSSFRow rowMuch;
        HSSFCell cellContent1;
        HSSFCell cellContent2;
        HSSFCell cellContent3;
        HSSFCell cellMuchContent1;
        HSSFCell cellMuchContent2;
        HSSFCell cellMuchContent3;

        for (NaturalLanguageRequirement req : naturals) {
            row = sheet.createRow(num);
            cellContent1 = row.createCell(0);
            cellContent1.setCellValue(req.getReqExcelId());
            cellContent1.setCellStyle(cellStyle);
            cellContent2 = row.createCell(1);
            cellContent2.setCellValue(req.getReqContent());
            cellContent2.setCellStyle(cellStyle);
            count = 1;
            for (StandardRequirement sR : standards) {
                if (req.getReqId().equals(sR.getNaturalLanguageReqId()) && count == 1) {
                    cellContent3 = row.createCell(2);
                    cellContent3.setCellValue(sR.getStandardReqContent());
                    cellContent3.setCellStyle(cellStyle);
                    count++;
                } else if (req.getReqId().equals(sR.getNaturalLanguageReqId()) && count == 2) {
                    rowMuch = sheet.createRow(++num);
                    cellMuchContent1 = rowMuch.createCell(0);
                    cellMuchContent1.setCellValue(" ");
                    cellMuchContent1.setCellStyle(cellStyle);
                    cellMuchContent2 = rowMuch.createCell(1);
                    cellMuchContent2.setCellValue(" ");
                    cellMuchContent2.setCellStyle(cellStyle);
                    cellMuchContent3 = rowMuch.createCell(2);
                    cellMuchContent3.setCellValue(sR.getStandardReqContent());
                    cellMuchContent3.setCellStyle(cellStyle);
                }
            }
            num++;
        }



        // 设置自动列宽
        sheet.setColumnWidth(1, sheet.getColumnWidth(1) * 17 / 10);
        sheet.setColumnWidth(2, sheet.getColumnWidth(2) * 17 / 10);

        if(FileUtils.saveExcel(wb,exportUrl)){
            return num;
        } else {
            return -1;
        }
    }
}
