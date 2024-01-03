package com.nuaa.art.vrm.model.vrm;

import com.nuaa.art.vrm.entity.ConceptLibrary;
import com.nuaa.art.vrm.entity.SystemProject;
import com.nuaa.art.vrm.entity.Type;
import com.nuaa.art.vrm.model.hvrm.HVRM;
import lombok.Data;
import org.springframework.beans.BeanUtils;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 变量关系模型
 *
 * @author konsin
 * @date 2023/09/04
 */
@Data
public class VRM
        extends com.nuaa.art.vrm.model.VariableRelationModel <SystemProject, Type, ConceptLibrary, ConceptLibrary, TableOfVRM, ModeClassOfVRM> {
//public class VariableRelationModel extends com.nuaa.art.vrm.model.VariableRelationModel<SystemProject, Type, ConceptLibrary, ConceptLibrary, TableOfVRM, ModeClassOfVRM> {


    public VRM() {
        this.system = new SystemProject();
        this.date = "";
        this.types = new ArrayList<>();
        this.inputs = new ArrayList<>();
        this.terms = new ArrayList<>();
        this.outputs = new ArrayList<>();
        this.constants = new ArrayList<>();
        this.conditions = new ArrayList<>();
        this.events = new ArrayList<>();
        this.modeClass = new ArrayList<>();
    }

    public HVRM convertToHVRM(){
        var model = new HVRM();
        BeanUtils.copyProperties(this, model);
        return model;
    }

}
