package com.nuaa.art.vrmverify.handler;

import com.nuaa.art.vrm.entity.Mode;
import com.nuaa.art.vrm.entity.StateMachine;
import com.nuaa.art.vrm.entity.Type;
import com.nuaa.art.vrm.model.VariableRelationModel;
import com.nuaa.art.vrm.model.hvrm.HVRM;
import com.nuaa.art.vrm.model.hvrm.TableOfModule;
import com.nuaa.art.vrm.model.hvrm.VariableWithPort;
import com.nuaa.art.vrm.model.vrm.ModeClassOfVRM;
import com.nuaa.art.vrm.model.vrm.TableOfVRM;
import com.nuaa.art.vrm.model.vrm.TableRow;
import com.nuaa.art.vrmverify.common.utils.TableUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 处理vrm转smv
 * @author djl
 * @date 2024-05-16
 */
public class Vrm2SmvHandler {

    public static Set<String> SMV_KEYWORDS_SET; //smv中保留关键字

    static {
        SMV_KEYWORDS_SET = new HashSet<>(Arrays.asList(
            "@F~", "@O~", "A", "ABF", "ABG", "abs", "AF", "AG", "array", "ASSIGN", "at next", "at last", "AX", "bool",
            "boolean", "BU", "case", "Clock", "clock", "COMPASSION", "COMPID", "COMPUTE", "COMPWFF", "CONSTANTS",
            "CONSTARRAY","CONSTRAINT", "cos", "count", "CTLSPEC", "CTLWFF", "DEFINE", "E", "EBF", "EBG", "EF", "EG", "esac",
            "EX", "exp", "extend", "F", "FAIRNESS", "FALSE", "floor", "FROZENVAR", "FUN", "G", "H", "IN", "in", "INIT", "init",
            "Integer", "integer", "INVAR", "INVARSPEC", "ISA", "ITYPE", "IVAR", "JUSTICE", "ln", "LTLSPEC", "LTLWFF",
            "MAX", "max", "MDEFINE", "MIN", "min", "MIRROR", "mod", "MODULE", "NAME", "next", "NEXTWFF", "noncontinuous",
            "O", "of", "PRED", "PREDICATES", "pi", "pow", "PSLSPEC", "PARSYNTH", "READ", "Real", "real", "resize", "S", "SAT",
            "self", "signed", "SIMPWFF", "sin", "sizeof", "SPEC", "swconst", "T", "tan", "time", "time since", "time until",
            "toint", "TRANS", "TRUE", "typeof", "U", "union", "unsigned", "URGENT", "uwconst", "V", "VALID", "VAR", "Word",
            "word", "word1", "WRITE", "X", "xnor", "xor", "X~ Y", "Y~", "Z"
        ));
    }

    private HVRM model;
    private String systemName;
    private String user;
    private Set<String> termsSet = new HashSet<>();
    private Set<String> outputsSet = new HashSet<>();
    private Set<String> modeClassesSet = new HashSet<>();
    private Map<String, TableOfVRM> vars2CTMap = new HashMap<>(); // 中间/输出变量与条件表映射
    private Map<String, TableOfVRM> vars2ETMap = new HashMap<>(); // 中间/输出变量与事件表映射
    private Map<String, Set<String>> paramsOfVarsMap = new HashMap<>(); // 中间/输出变量关联的变量
    private Map<String, Set<String>> paramsOfModeClassesMap = new HashMap<>(); // 模式集关联的变量
    private Map<String, String[]> vars2TypeAndInitVal = new HashMap<>(); // 变量与其类型和初始值的映射
    private Set<String> copyVars = new HashSet<>(); // 需要提前声明副本的变量
    private Set<String> needInstantiationVars = new HashSet<>(); // 还未实例化的变量
    private List<String> instantiationOrder = new ArrayList<>(); // 模块实例化顺序

    public Vrm2SmvHandler(HVRM model, String systemName, String user){
        this.model = model;
        this.systemName = systemName;
        this.user = user;
    }

    /**
     * 将vrm模型转换为smv字符串
     * @return
     */
    public String transferHvrm2Smv(){
        StringBuilder smvStr = new StringBuilder();

        // 预处理
        preHandle();
        // 添加额外信息
        smvStr.append(genExtraInfo());
        // 拓扑排序得到模块实例化顺序并消除循环依赖
        genInstantiationOrder();
        // 生成中间/输出变量模块
        smvStr.append("\n").append(genTermsAndOutputsModules());
        // 生成模式集模块
        smvStr.append("\n").append(genModeClassesModules());
        // 生成main模块
        smvStr.append("\n").append(genMainModule());

        return smvStr.toString();
    }

    private String genExtraInfo(){
        StringBuilder result = new StringBuilder();
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        result.append("-- ")
                .append(formatter.format(date))
                .append("\n-- Model: ")
                .append(systemName)
                .append("\n-- User: ")
                .append(user)
                .append("\n-- Automatic generation from VRM\n");
        return result.toString();
    }

    /**
     * 预处理
     */
    private void preHandle(){
        // 变量预处理
        preHandleVars();
        // 条件表预处理
        preHandleCT();
        // 事件表预处理
        preHandleET();
        // 删除无用的中间/输出变量
        clearUselessVars();
//        // 返回模型中用到的变量
//        getUsedVars();
        // 模式集预处理
        preHandleModeClasses();
    }

    /**
     * 预处理变量
     */
    private void preHandleVars(){
        for(int i = 0; i < 3; i++)
            preHandleVars(i);
    }

    private void preHandleVars(int flag){
        List<VariableWithPort> vars = switch (flag) {
            case 0 -> model.inputs;
            case 1 -> model.terms;
            default -> model.outputs;
        };
        Map<String, String> dataTypeMap = new HashMap<>();
        for (Type type : model.types) {
            dataTypeMap.put(type.getTypeName(), type.getDataType());
        }
        for (VariableWithPort var : vars) {
            String name = var.getConceptName();
            if(SMV_KEYWORDS_SET.contains(name)){
                name = "_" + name;
                var.setConceptName(name);
            }
            if(flag == 1)
                termsSet.add(name);
            if(flag == 2)
                outputsSet.add(name);
            String datatype = dataTypeMap.get(var.getConceptDatatype());
            String conceptRange = var.getConceptRange();
            String value = var.getConceptValue();
            switch (datatype) {
                case "boolean" -> {
                    var.setConceptDatatype("boolean");
                    var.setConceptValue(value.toUpperCase());
                }
                case "integer", "unsigned" -> var.setConceptDatatype(conceptRange);
                case "float", "double" -> var.setConceptDatatype("real");
                case "enumerated" -> {
                    String[] split = conceptRange.split(",");
                    for (String s : split) {
                        if(SMV_KEYWORDS_SET.contains(s))
                            conceptRange = conceptRange.replace(s, "_" + s);
                    }
                    var.setConceptDatatype("{" + conceptRange + "}");
                }
                default -> throw new RuntimeException("未知类型错误，待解决！");
            }
            if(flag != 0){
                needInstantiationVars.add(name);
                vars2TypeAndInitVal.put(name, new String[]{ var.getConceptDatatype(), var.getConceptValue()});
            }
        }
    }

    /**
     * 预处理条件表
     */
    private void preHandleCT(){
        for (TableOfModule ct : model.conditions) {
            String name = ct.getName();
            if(SMV_KEYWORDS_SET.contains(name)){
                name = "_" + name;
                ct.setName(name);
            }
            vars2CTMap.put(name, ct);
            Set<String> args = new HashSet<>();
            for (TableRow row : ct.getRows()) {
                String condition = row.getDetails();
                args.addAll(TableUtils.getVarsFromCondition(condition));
                row.setDetails(modifyConditionAndEvent(condition));
                String assignment = row.getAssignment();
                if(assignment.equalsIgnoreCase("True") || assignment.equalsIgnoreCase("False"))
                    row.setAssignment(assignment.toUpperCase());
                else if(SMV_KEYWORDS_SET.contains(assignment))
                    row.setAssignment("_" + assignment);
            }
            paramsOfVarsMap.put(name, args);
        }
    }

    /**
     * 预处理事件表
     */
    private void preHandleET(){
        for (TableOfModule et : model.events) {
            String name = et.getName();
            if(SMV_KEYWORDS_SET.contains(name)){
                name = "_" + name;
                et.setName(name);
            }
            vars2ETMap.put(name, et);
            Set<String> args = new HashSet<>();
            ArrayList<TableRow> newRows = new ArrayList<>();
            for (TableRow row : et.getRows()) {
                String assignment = row.getAssignment();
                if(assignment.equalsIgnoreCase("True") || assignment.equalsIgnoreCase("False"))
                   assignment = assignment.toUpperCase();
                else if(SMV_KEYWORDS_SET.contains(assignment))
                    row.setAssignment("_" + assignment);
                String[] events = row.getDetails().split("}\\|\\|\\{");
                for (String event : events){
                    TableRow newRow = new TableRow();
                    event = event.replaceAll("\\{", "").replaceAll("}", "");
                    args.addAll(TableUtils.getVarsFromEvent(event));
                    newRow.setAssignment(assignment);
                    newRow.setDetails(modifyConditionAndEvent(event));
                    newRow.setMode(row.getMode());
                    newRow.setRelateReq(row.getRelateReq());
                    newRows.add(newRow);
                }
            }
            paramsOfVarsMap.put(name, args);
            et.setRows(newRows);
        }
    }

    /**
     * 删除无用的中间/输出变量
     */
    private void clearUselessVars(){
        for (String term : termsSet) {
            if(!paramsOfVarsMap.containsKey(term)){
                needInstantiationVars.remove(term);
                vars2TypeAndInitVal.remove(term);
            }
        }
        for (String output : outputsSet) {
            if(!paramsOfVarsMap.containsKey(output)){
                needInstantiationVars.remove(output);
                vars2TypeAndInitVal.remove(output);
            }
        }
    }

    private void getUsedVars(){
        Set<String> usedVarsSet = new HashSet<>();
        usedVarsSet.addAll(paramsOfVarsMap.keySet());
        for (Set<String> value : paramsOfVarsMap.values()) {
            usedVarsSet.addAll(value);
        }
        usedVarsSet.addAll(paramsOfModeClassesMap.keySet());
        for (Set<String> value : paramsOfModeClassesMap.values()) {
            usedVarsSet.addAll(value);
        }
        System.out.println("涉及变量：");
        for (String s : usedVarsSet) {
            System.out.println("\t" + s);
        }
    }

    /**
     * 根据smv语法修改条件或事件
     * @param str
     * @return
     */
    private String modifyConditionAndEvent(String str){
        StringBuilder result = new StringBuilder(str);
        String prefix = "(|&!>=<", suffix = ")|&>=<";
        for (int i = 0; i < result.length(); i++) {
            String curCh = result.charAt(i) + "";
            if(prefix.contains(curCh)){
                while(i < result.length() && prefix.contains(curCh))
                    curCh = result.charAt(i++) + "";
                int start = i - 1;
                while(i < result.length() && !suffix.contains(curCh))
                    curCh = result.charAt(i++) + "";
                if(i == result.length())
                    break;
                int end = i - 1;
                String v = result.substring(start, end);
                if(SMV_KEYWORDS_SET.contains(v))
                    result.insert(start, "_");
            }
        }
        prefix = "(@";
        suffix = ")|&";
        for (int i = 0; i < result.length(); i++) {
            String curCh = result.charAt(i) + "";
            if(curCh.equals("!")){
                if(prefix.contains(result.charAt(i + 1) + ""))
                    continue;
                result.insert(i + 1, "(");
                while(i < result.length() && !suffix.contains(curCh))
                    curCh = result.charAt(i++) + "";
                result.insert(i - 1, ")");
            }
        }
        return result.toString()
                .replaceAll("\\|\\|", "|")
                .replaceAll("=False", "=FALSE")
                .replaceAll("=false", "=FALSE")
                .replaceAll("=True", "=TRUE")
                .replaceAll("=true", "=TRUE");
    }

    /**
     * 预处理模式集
     */
    private void preHandleModeClasses(){
        for (ModeClassOfVRM mc : model.modeClass) {
            String name = mc.getModeClass().getModeClassName();
            if(SMV_KEYWORDS_SET.contains(name)){
                name = "_" + name;
                mc.getModeClass().setModeClassName(name);
            }
            modeClassesSet.add(name);
            StringBuilder dataType = new StringBuilder("{");
            String initialMode = null;
            List<Mode> modes = mc.getModes();
            for (int i = 0; i < modes.size(); i++) {
                Mode mode = modes.get(i);
                dataType.append(mode.getModeName());
                if(i < modes.size() - 1)
                    dataType.append(",");
                if(mode.getInitialStatus() == 1)
                    initialMode = mode.getModeName();
                String modeName = mode.getModeName();
                if(SMV_KEYWORDS_SET.contains(modeName))
                    mode.setModeName("_" + modeName);
            }
            dataType.append("}");
            needInstantiationVars.add(name);
            vars2TypeAndInitVal.put(name, new String[]{ dataType.toString(), initialMode});
            Set<String> args = new HashSet<>();
            ArrayList<StateMachine> newModeTrans = new ArrayList<>();
            for (StateMachine modeTran : mc.getModeTrans()) {
                String[] events = modeTran.getEvent().split("}\\|\\|\\{");
                for (String event : events){
                    StateMachine newModeTran = new StateMachine();
                    newModeTran.setDependencyModeClassId(modeTran.getDependencyModeClassId());
                    newModeTran.setDependencyModeClass(modeTran.getDependencyModeClass());
                    newModeTran.setSystemId(modeTran.getSystemId());
                    String sourceState = modeTran.getSourceState();
                    if(SMV_KEYWORDS_SET.contains(sourceState))
                        sourceState = "_" + sourceState;
                    newModeTran.setSourceState(sourceState);
                    String endState = modeTran.getEndState();
                    if(SMV_KEYWORDS_SET.contains(endState))
                        endState = "_" + endState;
                    newModeTran.setEndState(endState);
                    args.addAll(TableUtils.getVarsFromEvent(event));
                    event = modifyConditionAndEvent(event.replaceAll("\\{", "").replaceAll("}", ""));
                    newModeTran.setEvent(event);
                    newModeTrans.add(newModeTran);
                }
            }
            paramsOfModeClassesMap.put(name, args);
            mc.setModeTrans(newModeTrans);
        }
    }






    /**
     * 拓扑排序得到模块实例化顺序并消除循环依赖
     */
    private void genInstantiationOrder(){
        int n = needInstantiationVars.size();   // (n + 1) * n / 2
        int i = 0;
        while(!needInstantiationVars.isEmpty()){
            Iterator<String> iterator = needInstantiationVars.iterator();
            while(iterator.hasNext()){
                String name = iterator.next();
                if(!isDependable(!modeClassesSet.contains(name), name)){
                    instantiationOrder.add(name);
                    iterator.remove();
                }
                i++;
            }
            if(i >= (n + 1) * n / 2 && !needInstantiationVars.isEmpty()){ // 集合不为空，则存在循环依赖
                String name = needInstantiationVars.iterator().next();
                Map<String, Set<String>> paramsSet = modeClassesSet.contains(name) ? paramsOfModeClassesMap : paramsOfVarsMap;
                for (String param : paramsSet.get(name)) {
                    if(needInstantiationVars.contains(param))
                        copyVars.add(param);
                }
                needInstantiationVars.remove(name);
                instantiationOrder.add(name);
                n = needInstantiationVars.size();
                i = 0;
            }
        }
    }

    /**
     * 判断模块是否有对未实例化模块的依赖
     * @param isVariable
     * @param name
     * @return
     */
    private boolean isDependable(boolean isVariable, String name){
        Map<String, Set<String>> paramsMap = isVariable ? paramsOfVarsMap : paramsOfModeClassesMap;
        Set<String> paramsSet = paramsMap.get(name);
        if(paramsSet == null)
            return false;
        for (String param : paramsSet) {
            if(needInstantiationVars.contains(param))
                return true;
        }
        return false;
    }

    /**
     * 生成中间/输出变量模块字符串
     * @return
     */
    private String genTermsAndOutputsModules(){
        StringBuilder result = new StringBuilder();

        for (VariableWithPort term : model.terms) {
            result.append(genSingleTermOrOutputModule(term)).append("\n");
        }
        for (VariableWithPort output : model.outputs) {
            result.append(genSingleTermOrOutputModule(output)).append("\n");
        }

        return result.toString();
    }

    /**
     * 生成单个中间/输出模块字符串
     * @return
     */
    private String genSingleTermOrOutputModule(VariableWithPort var){
        String name = var.getConceptName();
        if(!paramsOfVarsMap.containsKey(name))
            return "";

        StringBuilder result = new StringBuilder();
        StringBuilder moduleStatement = new StringBuilder();
        StringBuilder varStatement = new StringBuilder();
        StringBuilder varAssignment = new StringBuilder();

        moduleStatement.append("MODULE m_")
                .append(name)
                .append("(\n");
        Iterator<String> iterator = paramsOfVarsMap.get(name).iterator();
        while(iterator.hasNext()){
            String param = iterator.next();
            if(copyVars.contains(param))
                param = "c_" + param;
            moduleStatement.append("\t").append(param);
            if(iterator.hasNext())
                moduleStatement.append(",\n");
        }
        moduleStatement.append(")\n");

        varStatement.append("VAR\n")
                .append("\tresult : ")
                .append(var.getConceptDatatype())
                .append(";\n");

        varAssignment.append("ASSIGN\n")
                .append("\tinit(result) := ")
                .append(var.getConceptValue())
                .append(";\n")
                .append("\tnext(result) :=\n\t\tcase\n");
        TableOfVRM ct = vars2CTMap.get(name);
        if(ct != null){
            for (TableRow row : ct.getRows()) {
                varAssignment.append("\t\t\t")
                        .append(injectNextToCaseCondition(injectDotToCaseCondition(row.getDetails())))
                        .append(" : ")
                        .append(row.getAssignment())
                        .append(";\n");
            }
        }
        TableOfVRM et = vars2ETMap.get(name);
        if(et != null){
            for (TableRow row : et.getRows()) {
                varAssignment.append("\t\t\t")
                        .append(transformEvent2CaseCondition(row.getDetails()))
                        .append(" : ")
                        .append(row.getAssignment())
                        .append(";\n");
            }
        }
        varAssignment.append("\t\t\tTRUE : result;\n\t\tesac;\n");

        result.append(moduleStatement)
                .append(varStatement)
                .append(varAssignment);
        return result.toString();
    }

    /**
     * 将事件转换为smv中case语句中的条件
     * @param event
     * @return
     */
    private String transformEvent2CaseCondition(String event){
        StringBuilder result = new StringBuilder();

        String[] conditions = event
                .replaceAll("\\{", "")
                .replaceAll("}", "")
                .split(TableUtils.EVENT_SIGNAL);
        boolean isReversal = conditions[0].equals("!");
        if(isReversal)
            result.append("!").append("(");
        if(conditions[1].equals("(true)"))
            conditions[1] = "TRUE";
        String condition1 = injectDotToCaseCondition(conditions[1]);
        String nextCondition1 = injectNextToCaseCondition(condition1);
        if(event.contains("@T")){
            result.append("!")
                    .append("(")
                    .append(condition1)
                    .append(")&(")
                    .append(nextCondition1)
                    .append(")");
        }
        else if(event.contains("@F")){
            result.append("(")
                    .append(condition1)
                    .append(")&!(")
                    .append(nextCondition1)
                    .append(")");
        }
        else if(event.contains("@C")){
            result.append("(!")
                    .append("(")
                    .append(condition1)
                    .append(")&(")
                    .append(nextCondition1)
                    .append("))|((")
                    .append(condition1)
                    .append(")&!(")
                    .append(nextCondition1)
                    .append("))");
        }
        if(conditions.length == 3){
            if(conditions[2].equals("(true)"))
                conditions[2] = "TRUE";
            String condition2 = injectDotToCaseCondition(conditions[2]);
            String nextCondition2 = injectNextToCaseCondition(condition2);
            if(event.contains("WHEN")){
                result.append("&(")
                        .append(condition2)
                        .append(")");
            }
            else if(event.contains("WHERE")){
                result.append("&(")
                        .append(nextCondition2)
                        .append(")");
            }
            else if(event.contains("WHILE")){
                result.append("&(")
                        .append(condition2)
                        .append(")&(")
                        .append(nextCondition2)
                        .append(")");
            }
        }
        if(isReversal)
            result.append(")");

        return result.toString();
    }

    /**
     * 向case条件中注入.result
     * @param condition
     * @return
     */
    private String injectDotToCaseCondition(String condition){
        StringBuilder result = new StringBuilder(condition);

        String prefix = "(|&!", suffix = ">=<";
        for (int i = 0; i < result.length(); i++) {
            String curCh = result.charAt(i) + "";
            if(prefix.contains(curCh)){
                while(i < result.length() && prefix.contains(curCh))
                    curCh = result.charAt(i++) + "";
                int start = i - 1;
                while(i < result.length() && !suffix.contains(curCh))
                    curCh = result.charAt(i++) + "";
                if(i == result.length())
                    break;
                int end = i - 1;
                String v = result.substring(start, end);
                if(copyVars.contains(v)){
                    result.insert(start, "c_");
                    i++;
                }
                else if(termsSet.contains(v) || outputsSet.contains(v) || modeClassesSet.contains(v)){
                    result.insert(end, ".result");
                    i += 6;
                }
            }
        }

        return result.toString();
    }

    /**
     * 向case条件中注入next表达式
     * @param condition
     * @return
     */
    private String injectNextToCaseCondition(String condition){
        StringBuilder result = new StringBuilder(condition);

        String prefix = "(|&!", suffix = ">=<";
        for (int i = 0; i < result.length(); i++) {
            String curCh = result.charAt(i) + "";
            if(prefix.contains(curCh)){
                while(i < result.length() && prefix.contains(curCh))
                    curCh = result.charAt(i++) + "";
                int start = i - 1;
                while(i < result.length() && !suffix.contains(curCh))
                    curCh = result.charAt(i++) + "";
                if(i == result.length())
                    break;
                int end = i - 1;
                result.insert(start, "next(");
                result.insert(end + 5, ")");
                i += 5;
            }
        }

        return result.toString();
    }

    /**
     * 生成模式集模块字符串
     * @return
     */
    private String genModeClassesModules(){
        StringBuilder result = new StringBuilder();

        for (ModeClassOfVRM mc : model.modeClass) {
            result.append(genSingleModeClassModule(mc)).append("\n");
        }

        return result.toString();
    }

    /**
     * 生成单个模式集模块字符串
     * @return
     */
    private String genSingleModeClassModule(ModeClassOfVRM mc){
        StringBuilder result = new StringBuilder();
        StringBuilder moduleStatement = new StringBuilder();
        StringBuilder varStatement = new StringBuilder();
        StringBuilder varAssignment = new StringBuilder();
        String name = mc.getModeClass().getModeClassName();
        ArrayList<Mode> modes = mc.getModes();

        moduleStatement.append("MODULE m_")
                .append(name)
                .append("(\n");
        Iterator<String> iterator = paramsOfModeClassesMap.get(name).iterator();
        while(iterator.hasNext()){
            String param = iterator.next();
            if(copyVars.contains(param))
                param = "c_" + param;
            moduleStatement.append("\t").append(param);
            if(iterator.hasNext())
                moduleStatement.append(",\n");
        }
        moduleStatement.append(")\n");

        varStatement.append("VAR\n")
                .append("\tresult : {");
        String initialMode = null;
        for (int i = 0; i < modes.size(); i++) {
            Mode mode = modes.get(i);
            varStatement.append(mode.getModeName());
            if(i < modes.size() - 1)
                varStatement.append(",");
            if(mode.getInitialStatus() == 1)
                initialMode = mode.getModeName();
        }
        varStatement.append("};\n");

        if(initialMode != null){
            varAssignment.append("ASSIGN\n")
                    .append("\tinit(result) := ")
                    .append(initialMode)
                    .append(";\n");
            List<StateMachine> modeTrans = mc.getModeTrans();
            if(modeTrans != null && !modeTrans.isEmpty()){
                varAssignment.append("\tnext(result) :=\n\t\tcase\n");
                for (StateMachine modeTran : mc.getModeTrans()) {
                    varAssignment.append("\t\t\t(result=")
                            .append(modeTran.getSourceState())
                            .append(")&")
                            .append(transformEvent2CaseCondition(modeTran.getEvent()))
                            .append(" : ")
                            .append(modeTran.getEndState())
                            .append(";\n");
                }
                varAssignment.append("\t\t\tTRUE : result;\n");
                varAssignment.append("\t\tesac;\n");
            }
        }

        result.append(moduleStatement)
                .append(varStatement)
                .append(varAssignment);
        return result.toString();
    }

    /**
     * 生成main模块字符串
     * @return
     */
    private String genMainModule(){
        StringBuilder result = new StringBuilder();

        result.append("MODULE main\n");
        // 输入变量声明及赋初始值
        result.append(stateInputVars());
        // 副本变量声明
        result.append(stateCopyVars());
        // 中间/输出变量、模式集按顺序声明
        result.append(stateLeftVarsByOrder());
        // 副本变量赋值
        result.append(assignCopyVars());

        return result.toString();
    }

    /**
     * 生成输入变量在main模块中的声明及初始化
     * @return
     */
    private String stateInputVars(){
        StringBuilder result = new StringBuilder();
        if(model.inputs != null && !model.inputs.isEmpty()){
            for (VariableWithPort input : model.inputs) {
                String name = input.getConceptName();
                String value = input.getConceptValue();
                result.append("VAR ")
                        .append(name)
                        .append(" : ")
                        .append(input.getConceptDatatype())
                        .append(";\nASSIGN init(")
                        .append(name)
                        .append(") := ")
                        .append(value)
                        .append(";\n\n");
            }
        }
        return result.toString();
    }

    /**
     * 副本变量声明
     * @return
     */
    private String stateCopyVars(){
        if(copyVars.isEmpty())
            return "";
        StringBuilder result = new StringBuilder();
        result.append("VAR\n");
        for (String copyVar : copyVars) {
            result.append("\tc_")
                    .append(copyVar)
                    .append(" : ")
                    .append(vars2TypeAndInitVal.get(copyVar)[0])
                    .append(";\n");
        }
        result.append("\n");
        return result.toString();
    }

    /**
     * 中间/输出变量、模式集按顺序声明
     * @return
     */
    private String stateLeftVarsByOrder(){
        if(instantiationOrder.isEmpty())
            return "";
        StringBuilder result = new StringBuilder("VAR\n");
        for (String name : instantiationOrder) {
            result.append("\t")
                    .append(name)
                    .append(" : \n\t\t")
                    .append("m_")
                    .append(name)
                    .append("(\n");
            Map<String, Set<String>> paramsMap = modeClassesSet.contains(name) ? paramsOfModeClassesMap : paramsOfVarsMap;
            Iterator<String> iterator = paramsMap.get(name).iterator();
            while (iterator.hasNext()){
                String param = iterator.next();
                if(copyVars.contains(param))
                    param = "c_" + param;
                result.append("\t\t\t").append(param);
                if(iterator.hasNext())
                    result.append(",\n");
            }
            result.append(");\n\n");
        }
        return result.toString();
    }

    /**
     * 副本变量赋值
     * @return
     */
    private String assignCopyVars(){
        if(copyVars.isEmpty())
            return "";
        StringBuilder result = new StringBuilder("ASSIGN\n");
        for (String copyVar : copyVars) {
            result.append("\tinit(c_")
                    .append(copyVar)
                    .append(") := ")
                    .append(vars2TypeAndInitVal.get(copyVar)[1])
                    .append(";\n\tnext(c_")
                    .append(copyVar)
                    .append(") := ")
                    .append(copyVar)
                    .append(".result;\n\n");
        }
        return result.toString();
    }

}
