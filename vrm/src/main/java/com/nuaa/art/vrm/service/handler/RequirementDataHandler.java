package com.nuaa.art.vrm.service.handler;

public interface RequirementDataHandler {
    /**
     * 导入需求
     *
     * @param systemId 项目id
     * @param fileUrl    文件名称
     * @return int 返回导入条目号
     */
    int importFromFile(int systemId, String fileUrl);

    /**
     * 导出需求
     *
     * @param systemId   系统编号
     * @param exportUrl 导出文件名
     * @return int
     */
    int exportToFile(int systemId, String exportUrl);
}
