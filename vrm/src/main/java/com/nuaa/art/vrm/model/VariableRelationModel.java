package com.nuaa.art.vrm.model;

import com.nuaa.art.vrm.entity.ConceptLibrary;
import com.nuaa.art.vrm.entity.SystemProject;
import com.nuaa.art.vrm.entity.Type;
import com.nuaa.art.vrm.model.vrm.ModeClassOfVRM;
import com.nuaa.art.vrm.model.vrm.TableOfVRM;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;


@Data
public abstract class VariableRelationModel<ST extends SystemProject, T extends Type, CT extends ConceptLibrary, VT extends ConceptLibrary, TB extends TableOfVRM, MC extends ModeClassOfVRM> {
    public ST system;
    public String date;
    public ArrayList<T> types;
    public ArrayList<CT> constants;
    public ArrayList<VT> inputs;
    public ArrayList<VT> terms;
    public ArrayList<VT> outputs;

    public ArrayList<TB> conditions;
    public ArrayList<TB> events;
    public ArrayList<MC> modeClass;

    public void creatDate(){
        SimpleDateFormat sdfDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdfDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        this.date = sdfDateFormat.format(new Date());
    }

}
