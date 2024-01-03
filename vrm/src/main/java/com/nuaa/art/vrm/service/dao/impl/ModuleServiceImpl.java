package com.nuaa.art.vrm.service.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuaa.art.vrm.entity.Module;
import com.nuaa.art.vrm.service.dao.ModuleService;
import com.nuaa.art.vrm.mapper.ModuleMapper;
import com.nuaa.art.vrm.service.dao.NaturalLanguageRequirementService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author konsin
* @description 针对表【vrm_module】的数据库操作Service实现
* @createDate 2023-11-02 15:00:41
*/
@Service
public class ModuleServiceImpl extends ServiceImpl<ModuleMapper, Module>
    implements ModuleService{

    @Resource
    NaturalLanguageRequirementService requirementService;

    @Override
    public Module getModuleById(Integer Id) {
        return getById(Id);
    }

    @Override
    public List<Module> listModulesBySystemId(Integer systemId) {
        return list(new LambdaQueryWrapper<Module>().eq(Module::getSystemId, systemId));
    }

    @Override
    public List<Module> listChildrenModulesBySystemIdAndModuleId(Integer systemId, Integer id) {
        return list(new LambdaQueryWrapper<Module>()
                .eq(Module::getSystemId, systemId)
                .eq(Module::getId, id));
    }

    @Override
    public boolean insertModule(Module module) {
        return save(module);
    }

    @Override
    public boolean insertModuleList(List<Module> modules) {
        return saveBatch(modules);
    }

    @Override
    public boolean updateModule(Module module) {
        return updateById(module);
    }

    //删除前要先删除需求条目
    @Override
    @Transactional
    public boolean deleteModuleById(Integer id) {
        Module module = getModuleById(id);
        requirementService.releaseModuleByModuleId(module.getSystemId(), id);
        return removeById(id);
    }

    @Override
    @Transactional
    public boolean deleteModule(Module module) {
        requirementService.releaseModuleByModuleId(module.getSystemId(), module.getId());
        return removeById(module);
    }

    @Override
    @Transactional
    public boolean deleteModulesBySystemId(Integer systemId) {
        boolean flag = true;
            List<Module> modules = list(new LambdaQueryWrapper<Module>().eq(Module::getSystemId, systemId));
            for (Module module : modules) {
                requirementService.releaseModuleByModuleId(systemId, module.getId());
                if (!removeById(module)) {
                    flag = false;
                }
            }
            return flag;
    }

    @Override
    @Transactional
    public boolean deleteChildModulesBySystemIdAndId(Integer systemId, Integer id) {
            if(deleteAllChilds(systemId,id))
                return true;
            else return false;
    }

    public boolean deleteAllChilds(Integer systemId, Integer id){
        boolean flag = true;
        List<Module> modules = list(new LambdaQueryWrapper<Module>().eq(Module::getSystemId, systemId).eq(Module::getParentId, id));
        for(Module module : modules) {
            deleteAllChilds(systemId, module.getId());
            requirementService.releaseModuleByModuleId(systemId, module.getId());
            if (!(removeById(module))) {
                flag = false;
                break;
            }
        }
        return flag;
    }
}




