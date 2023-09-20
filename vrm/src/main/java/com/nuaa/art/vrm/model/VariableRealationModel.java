package com.nuaa.art.vrm.model;

import com.nuaa.art.vrm.entity.ConceptLibrary;
import com.nuaa.art.vrm.entity.SystemProject;
import com.nuaa.art.vrm.entity.Type;
import lombok.Data;


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
public class VariableRealationModel {
    private SystemProject system;
    private String date;
    private ArrayList<Type> types;
    private ArrayList<ConceptLibrary> constants;
    private ArrayList<ConceptLibrary> inputs;
    private ArrayList<ConceptLibrary> terms;
    private ArrayList<ConceptLibrary> outputs;
    private ArrayList<TableOfVRM> conditions;
    private ArrayList<TableOfVRM> events;
    private ArrayList<ModeClassOfVRM> modeClass;

    public VariableRealationModel() {
        this.system = new SystemProject();
        SimpleDateFormat sdfDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        this.date = sdfDateFormat.format(new Date());
        this.types = new ArrayList<>();
        this.inputs = new ArrayList<>();
        this.terms = new ArrayList<>();
        this.outputs = new ArrayList<>();
        this.constants = new ArrayList<>();
        this.conditions = new ArrayList<>();
        this.events = new ArrayList<>();
        this.modeClass = new ArrayList<>();
    }

}
