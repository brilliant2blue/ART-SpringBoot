package com.nuaa.art.vrm.service.dao;

import com.nuaa.art.vrm.entity.Module;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author konsin
* @description 针对表【vrm_module】的数据库操作Service
* @createDate 2023-11-02 15:00:41
*/
public interface ModuleService extends IService<Module> {

    Module getModuleById(Integer Id);
    List<Module> listModulesBySystemId(Integer systemId);

    List<Module> listChildrenModulesBySystemIdAndModuleId(Integer systemId, Integer id);

    boolean insertModule(Module module);

    boolean insertModuleList(List<Module> modules);

    boolean updateModule(Module module);

    boolean deleteModuleById(Integer id);

    boolean deleteModule(Module module);

    boolean deleteModulesBySystemId(Integer systemId);

    boolean deleteChildModulesBySystemIdAndId(Integer systemId, Integer id);


}
