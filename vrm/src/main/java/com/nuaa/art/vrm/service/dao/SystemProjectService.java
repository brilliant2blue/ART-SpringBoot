package com.nuaa.art.vrm.service.dao;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nuaa.art.vrm.entity.SystemProject;

import java.util.List;

/**
* @author konsin
* @description 针对表【vrm_systemproject】的数据库操作Service
* @createDate 2023-06-10 19:04:02
*/
public interface SystemProjectService extends IService<SystemProject> {
    public List<SystemProject> listSystemProject();
    public SystemProject getSystemProjectById(Integer id);
    public SystemProject getSystemProjectByName(String systemName);
    public void insertSystemProject(SystemProject systemProject);
    public void deleteSystemProject(String systemName);
}
