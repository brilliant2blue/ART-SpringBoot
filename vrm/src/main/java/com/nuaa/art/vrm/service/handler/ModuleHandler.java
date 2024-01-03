package com.nuaa.art.vrm.service.handler;

import com.nuaa.art.vrm.entity.Module;
import com.nuaa.art.vrm.model.hvrm.ModuleTree;
import com.nuaa.art.vrm.model.vrm.VRM;

import java.util.List;

/**
 * 模块处理为前端可接收的树形结构
 *
 * @author konsin
 * @date 2023/11/02
 */
public interface ModuleHandler {
    List<ModuleTree> ModulesToModuleTree(List<Module> modules);

}
