package com.nuaa.art.vrm.model.hvrm;

import com.nuaa.art.vrm.entity.Module;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ModuleTree extends Module {
    public ArrayList<ModuleTree> children = new ArrayList<>();

    public ModuleTree(){
        super();
        this.children = new ArrayList<>();
    }
    public ModuleTree(Module module){
        this.setId(module.getId());
        this.setName(module.getName());
        this.setParentId(module.getParentId());
        this.setSystemId(module.getSystemId());
        this.children = new ArrayList<>();
    }
}
