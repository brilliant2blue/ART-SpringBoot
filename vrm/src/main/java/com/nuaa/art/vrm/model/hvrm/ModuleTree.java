package com.nuaa.art.vrm.model.hvrm;

import com.nuaa.art.vrm.entity.Module;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ModuleTree extends Module {
    public ArrayList<ModuleTree> children;

    public ModuleTree(){
        super();
        this.children = new ArrayList<>();
    }
    public ModuleTree(Module module){
        this.setId(module.getId());
        this.setName(module.getName());
        this.setParentId(module.getParentId());
        this.setSystemId(module.getSystemId());
        this.children = new ArrayList();
    }
}
