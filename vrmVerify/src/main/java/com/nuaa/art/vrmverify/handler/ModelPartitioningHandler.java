package com.nuaa.art.vrmverify.handler;

import com.nuaa.art.vrm.entity.ConceptLibrary;
import com.nuaa.art.vrm.model.hvrm.HVRM;
import com.nuaa.art.vrm.model.hvrm.TableOfModule;
import com.nuaa.art.vrm.model.hvrm.VariableWithPort;
import com.nuaa.art.vrmverify.common.exception.UnknownVariableTypeException;
import com.nuaa.art.vrmverify.common.utils.CTLParseUtils;
import com.nuaa.art.vrmverify.common.utils.StringUtils;
import com.nuaa.art.vrmverify.common.utils.TableUtils;
import com.nuaa.art.vrmverify.model.ModelPartsWithProperties;
import com.nuaa.art.vrmverify.model.VariableNode;
import com.nuaa.art.vrm.model.vrm.ModeClassOfVRM;
import com.nuaa.art.vrmverify.model.formula.ctl.CTLFormula;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 模型划分
 *
 * @author djl
 * @date 2024-07-18
 */
public class ModelPartitioningHandler {

    public static final String INPUT_TYPE = "InputVariable";
    public static final String TERM_TYPE = "TermVariable";
    public static final String OUTPUT_TYPE = "OutputVariable";
    private HVRM model;
    private List<String> properties;
    private Map<String, VariableNode> VDG = new HashMap<>();

    public ModelPartitioningHandler(HVRM model) {
        this.model = model;
    }

    public Map<String, HVRM> partitionModel() {
        createVDG();
        return minimumPartitionVDG();
    }

    public List<ModelPartsWithProperties> partitionModel(List<String> properties) {
        this.properties = properties;
        createVDG();
        return propertyBasedMinimumPartitionVDG();
    }

    /**
     * 创建变量依赖图
     */
    private void createVDG() {
        createVariableNodes();
        createDependencies();
        printVariableDependencies();
    }

    private void printVariableDependencies() {
        VDG.forEach((name, variableNode) -> {
            System.out.println(name + ":");
            variableNode.getDependencies().forEach(dependency -> System.out.print(dependency.getName() + " "));
            System.out.println();
            System.out.println();
        });
    }

    /**
     * 创建变量节点
     */
    private void createVariableNodes() {
        createVariableNodes(model.inputs);
        createVariableNodes(model.terms);
        createVariableNodes(model.outputs);
        createVariableNodes(model.modeClass);
    }

    private void createVariableNodes(List<?> vars) {
        VDG.putAll(vars.stream()
                .map(var -> var.getClass() == VariableWithPort.class
                                ? new VariableNode((VariableWithPort) var)
                                : new VariableNode((ModeClassOfVRM) var))
                .collect(Collectors.toMap(VariableNode::getName, Function.identity())));
    }

    /**
     * 创建变量间依赖
     */
    private void createDependencies() {
        for (TableOfModule ct : model.conditions) {
            VariableNode variableNode = VDG.get(ct.getName());
            Set<String> dependencies = new HashSet<>();
            ct.getRows().forEach(
                    row -> dependencies.addAll(TableUtils.getVarsFromCondition(row.getDetails()))
            );
            dependencies.forEach(var -> variableNode.addDependency(VDG.get(var)));
        }
        for (TableOfModule et : model.events) {
            VariableNode variableNode = VDG.get(et.getName());
            Set<String> dependencies = new HashSet<>();
            et.getRows().forEach(row -> {
                for (String event : row.getDetails().split("}\\|\\|\\{"))
                    dependencies.addAll(TableUtils.getVarsFromEvent(event));
            });
            dependencies.forEach(var -> variableNode.addDependency(VDG.get(var)));
        }
        for (ModeClassOfVRM mc : model.modeClass) {
            VariableNode variableNode = VDG.get(mc.getModeClass().getModeClassName());
            Set<String> dependencies = new HashSet<>();
            mc.getModeTrans().forEach(mt -> {
                for (String event : mt.getEvent().split("}\\|\\|\\{"))
                    dependencies.addAll(TableUtils.getVarsFromEvent(event));
            });
            dependencies.forEach(var -> variableNode.addDependency(VDG.get(var)));
        }
    }

    /**
     * 最小强独立性划分
     * @return
     */
    private Map<String, HVRM> minimumPartitionVDG() {
        Set<Map<String, VariableNode>> VDG_parts = new HashSet<>();
        while(!VDG.isEmpty()) {
            Map<String, VariableNode> part = BFS(VDG.entrySet().iterator().next().getValue());
            VDG_parts.removeIf(vdgPart -> part.keySet().containsAll(vdgPart.keySet()));
            VDG_parts.add(new HashMap<>(part));
            part.keySet().forEach(VDG::remove);
        }

        Map<String, HVRM> modelParts = new HashMap<>();
        String prefix = model.system.getSystemName() + "_part";
        int i = 1;
        for (Map<String, VariableNode> vdgPart : VDG_parts) {
            modelParts.put(prefix + i++, constructHVRM(vdgPart));
        }

        return modelParts;
    }

    /**
     * 属性驱动最小强独立性划分
     * @return
     */
    private List<ModelPartsWithProperties> propertyBasedMinimumPartitionVDG() {
        if(properties == null || properties.isEmpty())
            return null;

        Set<String> varSet = new HashSet<>();
        Map<Set<String>, List<String>> vars2Properties = new HashMap<>();

        for (String property : properties) {
            CTLFormula ctlFormula = CTLParseUtils.parseCTLStr(property);
            StringBuilder property1 = new StringBuilder(property);
            Set<String> vars = ctlFormula.variableSet();
            Iterator<String> itr = vars.iterator();
            while(itr.hasNext()) {
                String var = itr.next();
                if(VDG.containsKey(var)){
                    for (Integer i : StringUtils.KMP(property1.toString(), var))
                        property1.insert(i, ".result");
                    varSet.add(var);
                }
                else itr.remove();
            }
            List<String> properties = new ArrayList<>();
            properties.add(property1.toString());
            boolean isContained = false;
            Iterator<Set<String>> iterator = vars2Properties.keySet().iterator();
            while(iterator.hasNext()) {
                Set<String> vars0 = iterator.next();
                if(vars0.containsAll(vars)) {
                    vars2Properties.get(vars0).addAll(properties);
                    isContained = true;
                    break;
                } else if(vars.containsAll(vars0)) {
                    properties.addAll(vars2Properties.get(vars0));
                    iterator.remove();
                }
            }
            if(!isContained) {
                vars2Properties.put(vars, properties);
            }
        }

        Map<String, Map<String, VariableNode>> var2Part = new HashMap<>();
        for (String var : varSet) {
            var2Part.put(var, BFS(VDG.get(var)));
        }

        Map<Map<String, VariableNode>, List<String>> part2Properties = new HashMap<>();
        for (Set<String> vars : vars2Properties.keySet()) {
            Map<String, VariableNode> part = new HashMap<>();
            vars.forEach(var -> part.putAll(var2Part.get(var)));
            Set<String> partVars = part.keySet();
            List<String> properties = vars2Properties.get(vars);
            boolean isContained = false;
            Iterator<Map<String, VariableNode>> iterator = part2Properties.keySet().iterator();
            while(iterator.hasNext()) {
                Map<String, VariableNode> part0 = iterator.next();
                Set<String> partVars0 = part0.keySet();
                if(partVars0.containsAll(partVars)) {
                    part2Properties.get(part0).addAll(properties);
                    isContained = true;
                    break;
                } else if(partVars.containsAll(partVars0)) {
                    properties.addAll(part2Properties.get(part0));
                    iterator.remove();
                }
            }
            if(!isContained) {
                part2Properties.put(part, properties);
            }
        }

        List<ModelPartsWithProperties> modelParts = new ArrayList<>();
        String prefix = model.system.getSystemName() + "_part";
        int i = 1;
        for (Map<String, VariableNode> part : part2Properties.keySet()) {
            modelParts.add(new ModelPartsWithProperties(
                    prefix + i++,
                    constructHVRM(part),
                    part2Properties.get(part)));
        }

        return modelParts;
    }

    private Map<String, VariableNode> BFS(VariableNode start) {
        if(start == null)
            return null;
        Map<String, VariableNode> res = new HashMap<>();
        Deque<VariableNode> queue = new LinkedList<>();
        queue.addLast(start);
        while (!queue.isEmpty()) {
            VariableNode node = queue.removeFirst();
            res.put(node.getName(), node);
            node.getDependencies().forEach(dependency -> {
                if(!res.containsKey(dependency.getName()) && !queue.contains(dependency))
                    queue.addLast(dependency);
            });
        }
        return res;
    }

    private HVRM constructHVRM(Map<String, VariableNode> part) {
        ArrayList<VariableWithPort> inputs = new ArrayList<>();
        ArrayList<VariableWithPort> terms = new ArrayList<>();
        ArrayList<VariableWithPort> outputs = new ArrayList<>();
        ArrayList<ModeClassOfVRM> modeClass = new ArrayList<>();
        part.keySet().forEach(name -> {
            VariableNode node = part.get(name);
            if(node.isMcType()) {
                modeClass.add(node.getMc());
            } else {
                VariableWithPort var = node.getVar();
                switch (var.getConceptType()) {
                    case INPUT_TYPE -> inputs.add(var);
                    case TERM_TYPE -> terms.add(var);
                    case OUTPUT_TYPE -> outputs.add(var);
                    default -> throw new UnknownVariableTypeException();
                }
           }
        });
        HVRM hvrm = new HVRM();
        hvrm.setTypes(model.getTypes());
        hvrm.setInputs(inputs);
        hvrm.setTerms(terms);
        hvrm.setOutputs(outputs);
        hvrm.setModeClass(modeClass);
        hvrm.setConditions(
                (ArrayList<TableOfModule>) model.conditions
                        .stream()
                        .filter(condition -> part.containsKey(condition.getName()))
                        .collect(Collectors.toList()));
        hvrm.setEvents(
                (ArrayList<TableOfModule>) model.events
                        .stream()
                        .filter(event -> part.containsKey(event.getName()))
                        .collect(Collectors.toList())
        );
        return hvrm;
    }

}
