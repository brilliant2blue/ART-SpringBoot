package com.nuaa.art.vrm.model.hvrm;

import com.nuaa.art.vrm.entity.ConceptLibrary;
import com.nuaa.art.vrm.entity.SystemProject;
import com.nuaa.art.vrm.entity.Type;
import com.nuaa.art.vrm.model.vrm.ModeClassOfVRM;
import com.nuaa.art.vrm.model.vrm.VRM;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;
import lombok.Data;
import java.util.ArrayList;

/**
 * 层次化变量关系模型
 *
 * @author konsin
 * @date 2023/11/20
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class HVRM extends com.nuaa.art.vrm.model.VariableRelationModel<SystemProject, Type, ConceptLibrary, VariableWithPort, TableOfModule, ModeClassOfVRM> {
    private ArrayList<ModuleTree> modules;

    public HVRM() {
        this.system =  new SystemProject();
        this.date = "";
        this.types = new ArrayList<>();
        this.inputs = new ArrayList<>();
        this.terms = new ArrayList<>();
        this.outputs = new ArrayList<>();
        this.constants = new ArrayList<>();
        this.modules = new ArrayList<>();
        this.conditions = new ArrayList<>();
        this.events = new ArrayList<>();
        this.modeClass = new ArrayList<>();
    }

    public VRM convertToVRM(){
        var model = new VRM();
        BeanUtils.copyProperties(this, model);
        return model;
    }

}