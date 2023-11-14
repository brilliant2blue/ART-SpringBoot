package com.nuaa.art.vrm.service.handler.impl;

import com.nuaa.art.common.utils.LogUtils;
import com.nuaa.art.vrm.entity.Module;
import com.nuaa.art.vrm.model.ModuleTree;
import com.nuaa.art.vrm.service.dao.DaoHandler;
import com.nuaa.art.vrm.service.handler.ModuleHandler;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModuleHandlerImpl implements ModuleHandler {
    @Resource
    DaoHandler daoHandler;

    @Override
    public List<ModuleTree> ModulesToModuleTree(List<Module> modules) {
        try {
           return toTree(modules);
        } catch (Exception e){
            LogUtils.error(e.getMessage());
            e.printStackTrace();
            return null;
        }

    }

    public List<ModuleTree> toTree(List<Module> modules){
        List<ModuleTree> treeList = new ArrayList<>();
        Map<Integer, List<ModuleTree>> treeMap = new HashMap<>();
        modules.forEach(module -> {
            Integer id = module.getParentId();
            if(id == null) id = 0;
            if(treeMap.containsKey(module.getParentId())) {
                treeMap.get(module.getParentId()).add(new ModuleTree(module));
            } else {
                if(id == 0)
                    treeMap.put(0, new ArrayList<>());
                else treeMap.put(id, new ArrayList<>());
                treeMap.get(id).add(new ModuleTree(module));
            }
        });

        treeMap.forEach((parentId, moduleTrees)->{
            if(parentId == 0){
                treeList.addAll(moduleTrees);
            }
            moduleTrees.forEach(item -> {
                item.setChildren(treeMap.get(item.getId()));
            });
        });

        return treeList;
    }
}
