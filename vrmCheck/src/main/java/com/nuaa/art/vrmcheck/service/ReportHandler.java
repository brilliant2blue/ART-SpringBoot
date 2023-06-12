package com.nuaa.art.vrmcheck.service;

import com.nuaa.art.vrmcheck.model.CheckErrorReporter;

/**
 * 报告处理
 *
 * @author konsin
 * @date 2023/07/09
 */
public interface ReportHandler {

    /**
     * 读取检查报告
     *
     * @param systemId 系统标识
     * @return {@link CheckErrorReporter}
     */
    CheckErrorReporter readCheckReport(int systemId);
    /**
     * 导出检查报告
     *
     * @return boolean
     */
    boolean saveCheckReport(CheckErrorReporter reporter, String fileUrl);
    /**
     * 导出检查报告
     *
     * @return boolean
     */
    boolean exportCheckReport(int systemID, String fileUrl);
}
