package com.nuaa.art.vrm.model.hvrm;

import com.nuaa.art.vrm.model.vrm.TableOfVRM;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

@EqualsAndHashCode(callSuper = true)
@Data
public class TableOfModule extends TableOfVRM {
    public Integer moduleId;
    public String module;
    public TableOfModule(){
        super();
        module="";
        moduleId = 0;
    }

    public TableOfModule( TableOfVRM t){
        BeanUtils.copyProperties(t, this);
        module="";
        moduleId = 0;
    }
}
