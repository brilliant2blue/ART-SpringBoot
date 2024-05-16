package com.nuaa.art.vrmverify.handler;

import com.nuaa.art.vrm.entity.Mode;
import com.nuaa.art.vrm.entity.StateMachine;
import com.nuaa.art.vrm.model.hvrm.HVRM;
import com.nuaa.art.vrm.model.hvrm.TableOfModule;
import com.nuaa.art.vrm.model.hvrm.VariableWithPort;
import com.nuaa.art.vrm.model.vrm.ModeClassOfVRM;
import com.nuaa.art.vrm.model.vrm.TableOfVRM;
import com.nuaa.art.vrm.model.vrm.TableRow;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 处理vrm转smv
 * @author djl
 * @date 2024-05-16
 */
public class Vrm2SmvHandler {

    public final String EVENT_SIGNAL = "@T|@F|@C|WHEN|WHILE|WHERE";

    private HVRM model;
    private String systemName;
    private String user;
    private Set<String> termsSet = new HashSet<>();
    private Set<String> outputsSet = new HashSet<>();
    private Set<String> modeClassesSet = new HashSet<>();
    private Map<String, TableOfVRM> vars2CTMap = new HashMap<>(); // 中间/输出变量与条件表映射
    private Map<String, TableOfVRM> vars2ETMap = new HashMap<>(); // 中间/输出变量与事件表映射
    private Map<String, Set<String>> paramsOfVarsMap = new HashMap<>(); //中间/输出变量关联的变量
    private Map<String, Set<String>> paramsOfModeClassesMap = new HashMap<>(); //模式集关联的变量

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
        // 变量类型和初始值预处理
        preHandleDatatypeAndValueOfVars();
        // 条件表预处理
        preHandleCT();
        // 事件表预处理
        preHandleET();
        // 模式集预处理
        preHandleModeClasses();
    }

    /**
     * 预处理变量的类型和初始值
     */
    private void preHandleDatatypeAndValueOfVars(){
        for(int i = 0; i < 3; i++)
            preHandleDatatypeAndValueOfVars(i);
    }

    private void preHandleDatatypeAndValueOfVars(int flag){
        List<VariableWithPort> vars = switch (flag) {
            case 0 -> model.inputs;
            case 1 -> model.terms;
            default -> model.outputs;
        };
        for (VariableWithPort var : vars) {
            if(flag == 1)
                termsSet.add(var.getConceptName());
            if(flag == 2)
                outputsSet.add(var.getConceptName());
            String datatype = var.getConceptDatatype();
            String conceptRange = var.getConceptRange();
            String value = var.getConceptValue();
            switch (datatype) {
                case "Boolean" -> {
                    var.setConceptDatatype("boolean");
                    var.setConceptValue(value.toUpperCase());
                }
                case "Integer" -> var.setConceptDatatype("integer");
                case "Float" -> var.setConceptDatatype("unsigned word[32]");
                default -> {
                    if (conceptRange.contains(".."))
                        var.setConceptDatatype(conceptRange);
                    else
                        var.setConceptDatatype("{" + conceptRange + "}");
                }
            }
        }
    }

    /**
     * 预处理条件表
     */
    private void preHandleCT(){
        for (TableOfModule ct : model.conditions) {
            vars2CTMap.put(ct.getName(), ct);
            Set<String> args = new HashSet<>();
            for (TableRow row : ct.getRows()) {
                String condition = row.getDetails();
                args.addAll(getVarsFromCondition(condition));
                row.setDetails(modifyConditionAndEvent(condition));
                String assignment = row.getAssignment();
                if(assignment.equalsIgnoreCase("True") || assignment.equalsIgnoreCase("False"))
                    row.setAssignment(assignment.toUpperCase());
            }
            paramsOfVarsMap.put(ct.getName(), args);
        }
    }

    /**
     * 预处理事件表
     */
    private void preHandleET(){
        for (TableOfModule et : model.events) {
            vars2ETMap.put(et.getName(), et);
            Set<String> args = new HashSet<>();
            ArrayList<TableRow> newRows = new ArrayList<>();
            for (TableRow row : et.getRows()) {
                String assignment = row.getAssignment();
                if(assignment.equalsIgnoreCase("True") || assignment.equalsIgnoreCase("False"))
                   assignment = assignment.toUpperCase();
                String[] events = row.getDetails().split("}\\|\\|\\{");
                for (String event : events){
                    TableRow newRow = new TableRow();
                    event = event.replaceAll("\\{", "").replaceAll("}", "");
                    args.addAll(getVarsFromEvent(event));
                    newRow.setAssignment(assignment);
                    newRow.setDetails(modifyConditionAndEvent(event));
                    newRow.setMode(row.getMode());
                    newRow.setRelateReq(row.getRelateReq());
                    newRows.add(newRow);
                }
            }
            paramsOfVarsMap.put(et.getName(), args);
            et.setRows(newRows);
        }
    }

    /**
     * 根据smv语法修改条件或事件
     * @param str
     * @return
     */
    private String modifyConditionAndEvent(String str){
        return str.replaceAll("\\|\\|", "|")
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
            modeClassesSet.add(mc.getModeClass().getModeClassName());
            Set<String> args = new HashSet<>();
            ArrayList<StateMachine> newModeTrans = new ArrayList<>();
            for (StateMachine modeTran : mc.getModeTrans()) {
                String[] events = modeTran.getEvent().split("}\\|\\|\\{");
                for (String event : events){
                    StateMachine newModeTran = new StateMachine();
                    event = event.replaceAll("\\{", "").replaceAll("}", "");
                    args.addAll(getVarsFromEvent(event));
                    newModeTran.setEvent(modifyConditionAndEvent(event));
                    newModeTran.setDependencyModeClassId(modeTran.getDependencyModeClassId());
                    newModeTran.setDependencyModeClass(modeTran.getDependencyModeClass());
                    newModeTran.setSystemId(modeTran.getSystemId());
                    newModeTran.setSourceState(modeTran.getSourceState());
                    newModeTran.setEndState(modeTran.getEndState());
                    newModeTrans.add(newModeTran);
                }
            }
            paramsOfModeClassesMap.put(mc.getModeClass().getModeClassName(), args);
            mc.setModeTrans(newModeTrans);
        }
    }

    /**
     * 从条件中获取变量列表
     * @param condition
     */
    private Set<String> getVarsFromCondition(String condition){
        String[] exprs = condition
                .replaceAll("!", "")
                .replaceAll("\\(", "")
                .replaceAll("\\)", "")
                .split("&|\\|\\|");
        Set<String> vars = new HashSet<>();
        for (String expr : exprs) {
            String var = expr.split(">|=|<")[0];
            if(var.equalsIgnoreCase("True") || var.equalsIgnoreCase("False"))
                continue;
            vars.add(var);
        }
        return vars;
    }

    /**
     * 从事件中获取变量列表
     * @param event
     * @return
     */
    private Set<String> getVarsFromEvent(String event){
        String[] conditions = event
                .replaceAll("\\{", "")
                .replaceAll("}", "")
                .split(EVENT_SIGNAL);
        Set<String> vars = new HashSet<>();
        for (int i = 1; i < conditions.length; i++) {
            vars.addAll(getVarsFromCondition(conditions[i]));
        }
        return vars;
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
        StringBuilder result = new StringBuilder();
        StringBuilder moduleStatement = new StringBuilder();
        StringBuilder varStatement = new StringBuilder();
        StringBuilder varAssignment = new StringBuilder();
        String name = var.getConceptName();

        moduleStatement.append("MODULE m_")
                .append(name)
                .append("(\n");
        Iterator<String> iterator = paramsOfVarsMap.get(name).iterator();
        while(iterator.hasNext()){
            moduleStatement.append("\t").append(iterator.next());
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
                        .append(injectDotToCaseCondition(row.getDetails()))
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
                .split(EVENT_SIGNAL);
        boolean isReversal = conditions[0].equals("!");
        if(isReversal)
            result.append("!").append("(");
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
            String condition2 = injectDotToCaseCondition(conditions[2]);
            String nextCondition2 = injectNextToCaseCondition(condition2);
            if(event.contains("WHEN")){
                result.append("&(")
                        .append(condition2)
                        .append(")");
            }
            else if(event.contains("WHILE")){
                result.append("&(")
                        .append(nextCondition2)
                        .append(")");
            }
            else if(event.contains("WHERE")){
                result.append("&(")
                        .append(nextCondition2)
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
                if(termsSet.contains(v) || outputsSet.contains(v) || modeClassesSet.contains(v)){
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
            moduleStatement.append("\t").append(iterator.next());
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
        result.append(genIvarOfInputs());
        // 中间变量声明
        result.append("\n").append(genVarOfTermsOrOutputs(true));
        // 输出变量声明
        result.append("\n").append(genVarOfTermsOrOutputs(false));
        // 模式集声明
        result.append("\n").append(genVarOfModeClasses());

        return result.toString();
    }

    /**
     * 生成输入变量在main模块中的声明及初始化
     * @return
     */
    private String genIvarOfInputs(){
        StringBuilder result = new StringBuilder();

        for (VariableWithPort input : model.inputs) {
            String name = input.getConceptName();
            result.append("IVAR ")
                    .append(name)
                    .append(" : ")
                    .append(input.getConceptDatatype())
                    .append(";\nINIT ")
                    .append(name)
                    .append(" = ")
                    .append(input.getConceptValue())
                    .append("\n\n");
        }

        return result.toString();
    }

    /**
     * 生成中间/输出变量在main模块中的声明
     * @param isTerm
     * @return
     */
    private String genVarOfTermsOrOutputs(boolean isTerm){
        StringBuilder result = new StringBuilder();

        result.append("VAR\n");
        List<VariableWithPort> vars = isTerm ? model.terms : model.outputs;
        for (VariableWithPort var : vars) {
            String name = var.getConceptName();
            result.append("\t")
                    .append(name)
                    .append(" :\n\t\t")
                    .append("m_")
                    .append(name)
                    .append("(\n");
            Iterator<String> iterator = paramsOfVarsMap.get(name).iterator();
            while(iterator.hasNext()){
                result.append("\t\t\t")
                        .append(iterator.next());
                if(iterator.hasNext())
                    result.append(",\n");
            }
            result.append(");\n\n");
        }

        return result.toString();
    }

    /**
     * 生成模式集在main模块中的声明
     * @return
     */
    private String genVarOfModeClasses(){
        StringBuilder result = new StringBuilder();

        result.append("VAR\n");
        for (ModeClassOfVRM mc : model.modeClass) {
            String name = mc.getModeClass().getModeClassName();
            result.append("\t")
                    .append(name)
                    .append(" :\n\t\t")
                    .append("m_")
                    .append(name)
                    .append("(\n");
            Iterator<String> iterator = paramsOfModeClassesMap.get(name).iterator();
            while(iterator.hasNext()){
                result.append("\t\t\t")
                        .append(iterator.next());
                if(iterator.hasNext())
                    result.append(",\n");
            }
            result.append(");\n\n");
        }

        return result.toString();
    }

}
