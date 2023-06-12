package com.nuaa.art.vrm.service.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuaa.art.vrm.entity.SystemProject;
import com.nuaa.art.vrm.mapper.SystemProjectMapper;
import com.nuaa.art.vrm.service.dao.SystemProjectService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author konsin
* @description 针对表【vrm_systemproject】的数据库操作Service实现
* @createDate 2023-06-10 19:04:02
*/
@Service
public class SystemProjectServiceImpl extends ServiceImpl<SystemProjectMapper, SystemProject>
    implements SystemProjectService {

    @Override
    public List<SystemProject> listSystemProject() {
        return list();
    }

    @Override
    public SystemProject getSystemProjectById(Integer id) {
        return getById(id);
    }

    @Override
    public SystemProject getSystemProjectByName(String systemName) {
        return getOne(new QueryWrapper<SystemProject>().eq("systemName",systemName));
    }

    @Override
    public void insertSystemProject(SystemProject systemProject) {
        save(systemProject);
    }

    @Override
    public void deleteSystemProject(String systemName) {
        remove(new QueryWrapper<SystemProject>().eq("systemName",systemName));
    }
}




