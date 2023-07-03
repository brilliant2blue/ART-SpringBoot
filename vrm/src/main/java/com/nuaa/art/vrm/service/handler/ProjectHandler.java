package com.nuaa.art.vrm.service.handler;

public interface ProjectHandler {
    /**
     * 导入项目
     *
     * @param projectName 项目名称
     * @param fileName    文件名称
     * @return int 返回项目id
     */
    int importProject(String projectName, String fileName);
    int exportProject(int systemId, String exportName);


}
