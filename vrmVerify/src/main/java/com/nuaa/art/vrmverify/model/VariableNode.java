package com.nuaa.art.vrmverify.model;

import com.nuaa.art.vrm.model.hvrm.VariableWithPort;
import com.nuaa.art.vrm.model.vrm.ModeClassOfVRM;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 变量依赖图中的节点
 * @author djl
 * @date 2024-07-18
 */
@Getter
@Setter
public class VariableNode {

    private String name;
    private int weight;
    private VariableWithPort var;
    private ModeClassOfVRM mc;
    private boolean mcType;
    private List<VariableNode> dependencies = new ArrayList<>();

    public VariableNode(VariableWithPort var) {
        this.name = var.getConceptName();
        this.var = var;
        this.mcType = false;
    }

    public VariableNode(ModeClassOfVRM mc) {
        this.name = mc.getModeClass().getModeClassName();
        this.mc = mc;
        this.mcType = true;
    }

    public void addDependency(VariableNode node) {
        this.dependencies.add(node);
    }

}
