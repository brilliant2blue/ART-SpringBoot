package com.nuaa.art.vrm.model.vrm;

import com.nuaa.art.vrm.entity.StandardRequirement;
import lombok.Data;

@Data
public class TableRow {
        String assignment;
        String details;

        String mode;
        
        Integer relateReq;

        public TableRow(){
            super();
        };

        public TableRow(StandardRequirement s) {
            this.assignment = s.getStandardReqValue();
            if(s.getTemplateType().contains("Condition"))
                this.details = s.getStandardReqCondition();
            else this.details = s.getStandardReqEvent();
            this.mode = s.getMode();
            this.relateReq = s.getNaturalLanguageReqId();
        }


}
