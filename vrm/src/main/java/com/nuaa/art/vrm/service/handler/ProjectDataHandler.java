package com.nuaa.art.vrm.service.handler;

/**
 * 需求工程项目处理
 *
 * @author konsin
 * @date 2023/07/06
 */
public interface ProjectDataHandler {
    /**
     * 导入项目
     *
     * @param projectName 项目名称
     * @param fileUrl    文件名称
     * @return int 返回项目id
     */
    int importProjectFromFile(String projectName, String fileUrl);

    /**
     * 导出项目文件
     *
     * @param systemId  系统编号
     * @param exportUrl 导出文件名
     * @return {@link String} 导出文件名
     */
    String exportProjectToFile(int systemId, String exportUrl);


    /**
     * 删除项目文件
     *
     * @param systemId 系统编号
     * @return boolean
     */
    boolean deleteProject(int systemId);




}
