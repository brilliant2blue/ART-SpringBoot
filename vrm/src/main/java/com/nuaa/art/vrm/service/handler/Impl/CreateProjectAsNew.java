package com.nuaa.art.vrm.service.handler.impl;

import com.nuaa.art.vrm.entity.SystemProject;
import com.nuaa.art.vrm.service.dao.DaoHandler;
import com.nuaa.art.vrm.service.dao.SystemProjectService;
import com.nuaa.art.vrm.service.handler.CreateProjectHandler;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("createProjectAsNew")
public class CreateProjectAsNew implements CreateProjectHandler {

    @Resource
    DaoHandler daoHandler;

    /**
     * 新建项目
     *
     * @param project 项目
     * @param baseId  基于项目Id
     * @return int
     */
    @Override
    public int newProject(SystemProject project, Integer baseId) {
        project.setSystemId(null);
        if(daoHandler.getDaoService(SystemProjectService.class).insertSystemProject(project)){
            return project.getSystemId();
        }else {
            return -1;
        }
    }
}
