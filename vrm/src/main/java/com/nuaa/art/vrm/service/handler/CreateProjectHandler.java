package com.nuaa.art.vrm.service.handler;

import com.nuaa.art.vrm.entity.SystemProject;

public interface CreateProjectHandler {
    /**
     * 新建项目,
     *
     * @param project 项目
     * @param baseId  基于项目Id
     * @return int
     */
    int newProject(SystemProject project, Integer baseId);
}
