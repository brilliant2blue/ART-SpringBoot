package com.nuaa.art.vrm.model.hvrm;

import com.nuaa.art.vrm.entity.ConceptLibrary;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;

@EqualsAndHashCode(callSuper = true)
@Data
public class VariableWithPort extends ConceptLibrary {
    ArrayList<Integer> inPort;
    ArrayList<Integer> outPort;
    public VariableWithPort(){
        super();
        inPort = new ArrayList<>();
        outPort = new ArrayList<>();
    }
    public VariableWithPort(ConceptLibrary i){
        BeanUtils.copyProperties(i, this);
        inPort = new ArrayList<>();
        outPort = new ArrayList<>();
    }
}
