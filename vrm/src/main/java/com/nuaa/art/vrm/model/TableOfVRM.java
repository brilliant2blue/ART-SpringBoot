package com.nuaa.art.vrm.model;

import com.nuaa.art.vrm.entity.ConceptLibrary;
import com.nuaa.art.vrm.entity.StandardRequirement;
import lombok.Data;

import java.util.ArrayList;


/**
 * vrm模型 条件表和事件表及对应的变量
 *
 * @author konsin
 * @date 2023/08/30
 */
@Data
public class TableOfVRM {
    String name;
    ConceptLibrary relateVar;
    ArrayList<TableRow> rows;

}
