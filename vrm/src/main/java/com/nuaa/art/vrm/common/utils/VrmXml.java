package com.nuaa.art.vrm.common.utils;

import com.nuaa.art.common.utils.ListUtils;
import com.nuaa.art.common.utils.LogUtils;
import com.nuaa.art.vrm.common.ConceptItemType;
import com.nuaa.art.vrm.entity.*;
import com.nuaa.art.vrm.model.ModeClassOfVRM;
import com.nuaa.art.vrm.model.TableOfVRM;
import com.nuaa.art.vrm.model.TableRow;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * VRM模型的持久化存储工具类
 * 对各类属性与xml相互转换的方法进行统一管理
 *
 * @author konsin
 * @date 2023/07/20
 */
@Component
public class VrmXml extends EntityXmlConvert {

    /**
     * 常量转XML
     *
     * @param conLibrary 常量对象
     * @return {@link Element}
     */
    public Element Constant(ConceptLibrary conLibrary) {
        Element constant = DocumentHelper.createElement("constant");
        //设置constant属性 name
        if (conLibrary.getConceptName() != null) {
            constant.addAttribute("name", conLibrary.getConceptName());
        }
        //添加constant属性 constantType
        Element constantType = constant.addElement("constantType");
        if (conLibrary.getConceptDatatype() != null) {
            constantType.setText(conLibrary.getConceptDatatype());
        }
        //添加constant属性 value
        Element value = constant.addElement("value");
        if (conLibrary.getConceptValue() != null) {
            value.setText(conLibrary.getConceptValue());
        }
        //添加constant属性 description
        Element description = constant.addElement("description");
        if (conLibrary.getConceptDescription() != null) {
            description.setText(conLibrary.getConceptDescription());
        }

        Element relateReq = constant.addElement("relateReq");
        if (conLibrary.getSourceReqId() != null) {
            relateReq.setText(ListUtils.NumArrayToString(conLibrary.getSourceReqId()));
        }
        return constant;
    }

    public ConceptLibrary Constant(Element conEle) {
        ConceptLibrary con = new ConceptLibrary();
        con.setConceptName(conEle.elementText("name"));
        con.setConceptDatatype(conEle.elementText("constantType"));
        con.setConceptValue(conEle.elementText("value"));
        con.setConceptDescription(conEle.elementText("description"));
        con.setSourceReqId(ListUtils.StringToNumArray(conEle.elementText("relateReq")));
        return con;
    }

    /**
     * 输入转XML
     *
     * @param iv
     * @return {@link Element}
     */
    public Element Input(ConceptLibrary iv) {
        Element input = DocumentHelper.createElement("input");
        //设置input属性 name
        if (iv.getConceptName() != null) {
            input.addAttribute("name", iv.getConceptName());
        }
        //添加input属性 inputType
        Element inputType = input.addElement("inputType");
        if (iv.getConceptDatatype() != null) {
            inputType.setText(iv.getConceptDatatype());
        }
        //添加input属性 initialValue
        Element initialValue = input.addElement("initialValue");
        if (iv.getConceptValue() != null) {
            initialValue.setText(iv.getConceptValue());
        }
        //添加input属性 range
        Element range = input.addElement("range");
        if (iv.getConceptRange() != null) {
            range.setText(iv.getConceptRange());
        }
        //添加input属性accuracy
        Element accuracy = input.addElement("accuracy");
        if (iv.getConceptAccuracy() != null) {
            accuracy.setText(iv.getConceptAccuracy());
        }

        Element relateReq = input.addElement("relateReq");
        if (iv.getSourceReqId() != null) {
            relateReq.setText(ListUtils.NumArrayToString(iv.getSourceReqId()));
        }
        return input;
    }

    public ConceptLibrary Input(Element inputEle) {
        ConceptLibrary iv = new ConceptLibrary();
        iv.setConceptName(inputEle.attributeValue("name"));
        iv.setConceptDatatype(inputEle.elementText("inputType"));
        iv.setConceptValue(inputEle.elementText("initialValue"));
        iv.setConceptAccuracy(inputEle.elementText("accuracy"));
        iv.setConceptRange(inputEle.elementText("range"));
        iv.setSourceReqId(ListUtils.StringToNumArray(inputEle.elementText("relateReq")));
        return iv;
    }

    /**
     * 中间变量转XML
     *
     * @param tv
     * @return {@link Element}
     */
    public Element Term(ConceptLibrary tv) {
        Element term = DocumentHelper.createElement("term");
        //设置term属性 name
        if (tv.getConceptName() != null) {
            term.addAttribute("name", tv.getConceptName());
        }
        //添加term属性 termType
        Element termType = term.addElement("termType");
        if (tv.getConceptDatatype() != null) {
            termType.setText(tv.getConceptDatatype());
        }
        //添加term属性 initialValue
        Element initialValue = term.addElement("initialValue");
        if (tv.getConceptValue() != null) {
            initialValue.setText(tv.getConceptValue());
        }
        //添加term属性 range
        Element range = term.addElement("range");
        if (tv.getConceptRange() != null) {
            range.setText(tv.getConceptRange());
        }
        //添加term属性accuracy
        Element accuracy = term.addElement("accuracy");
        if (tv.getConceptAccuracy() != null) {
            accuracy.setText(tv.getConceptAccuracy());
        }

        Element modeClass = term.addElement("relateReqMC");
        if (tv.getConceptDependencyModeClass() != null) {
            accuracy.setText(tv.getConceptDependencyModeClass());
        }

        Element relateReq = term.addElement("relateReq");
        if (tv.getSourceReqId() != null) {
            relateReq.setText(ListUtils.NumArrayToString(tv.getSourceReqId()));
        }
        return term;
    }

    public ConceptLibrary Term(Element termEle) {
        ConceptLibrary tv = new ConceptLibrary();
        tv.setConceptName(termEle.attributeValue("name"));
        tv.setConceptDatatype(termEle.elementText("termType"));
        tv.setConceptValue(termEle.elementText("initialValue"));
        tv.setConceptAccuracy(termEle.elementText("accuracy"));
        tv.setConceptRange(termEle.elementText("range"));
        tv.setSourceReqId(ListUtils.StringToNumArray(termEle.elementText("relateReq")));
        return tv;
    }

    /**
     * 输出变量转XML
     *
     * @param ov
     * @return {@link Element}
     */
    public Element Output(ConceptLibrary ov) {
        Element output = DocumentHelper.createElement("output");
        //设置output属性 name
        if (ov.getConceptName() != null) {
            output.addAttribute("name", ov.getConceptName());
        }
        //添加output属性 outputType
        Element outputType = output.addElement("outputType");
        if (ov.getConceptDatatype() != null) {
            outputType.setText(ov.getConceptDatatype());
        }
        //添加output属性 initialValue
        Element initialValue = output.addElement("initialValue");
        if (ov.getConceptValue() != null) {
            initialValue.setText(ov.getConceptValue());
        }
        //添加output属性 range
        Element range = output.addElement("range");
        if (ov.getConceptRange() != null) {
            range.setText(ov.getConceptRange());
        }
        //添加output属性accuracy
        Element accuracy = output.addElement("accuracy");
        if (ov.getConceptAccuracy() != null) {
            accuracy.setText(ov.getConceptAccuracy());
        }

        Element modeClass = output.addElement("relateReqMC");
        if (ov.getConceptDependencyModeClass() != null) {
            accuracy.setText(ov.getConceptDependencyModeClass());
        }

        Element relateReq = output.addElement("relateReq");
        if (ov.getSourceReqId() != null) {
            relateReq.setText(ListUtils.NumArrayToString(ov.getSourceReqId()));
        }
        return output;
    }

    public ConceptLibrary Output(Element outputEle) {
        ConceptLibrary tv = new ConceptLibrary();
        tv.setConceptName(outputEle.attributeValue("name"));
        tv.setConceptDatatype(outputEle.elementText("outputType"));
        tv.setConceptValue(outputEle.elementText("initialValue"));
        tv.setConceptAccuracy(outputEle.elementText("accuracy"));
        tv.setConceptRange(outputEle.elementText("range"));
        tv.setSourceReqId(ListUtils.StringToNumArray(outputEle.elementText("relateReq")));
        return tv;
    }
    public ConceptLibrary XMLToOutput(Element outputEle) {
        ConceptLibrary ov = new ConceptLibrary();
        ov.setConceptName(outputEle.attributeValue("name"));
        ov.setConceptDatatype(outputEle.elementText("outputType"));
        ov.setConceptValue(outputEle.elementText("initialValue"));
        ov.setConceptAccuracy(outputEle.elementText("accuracy"));
        ov.setConceptRange(outputEle.elementText("range"));
        ov.setSourceReqId(ListUtils.StringToNumArray(outputEle.elementText("relateReq")));
        return ov;
    }


    /**
     * 条件表xml
     *
     * @param con 反对
     * @return {@link Element}
     */
    public Element Condition(TableOfVRM con) {
        Element table = DocumentHelper.createElement("table");
        ConceptLibrary var = con.getRelateVar();
        if (var.getConceptType().equals(ConceptItemType.Term.getNameEN())) {
            table.addAttribute("name", con.getName());
            table.addAttribute("isOutput", "0");
        } else if (var.getConceptType().equals(ConceptItemType.Output.getNameEN())) {
            table.addAttribute("name", con.getName());
            table.addAttribute("isOutput", "1");
        }

        //添加table属性referencedStateMachine
        Element referencedStateMachine = table.addElement("referencedStateMachine");
        if (var.getConceptDependencyModeClass() != null) {
            referencedStateMachine.addAttribute("name", var.getConceptDependencyModeClass());
        }

        Element conditionTable = table.addElement("conditionTable");
        for (TableRow standardReq : con.getRows()) {
            //添加conditionTable节点row
            Element row = conditionTable.addElement("row");
            Element assignment = row.addElement("assignment");
            assignment.setText(standardReq.getAssignment()+ "");
            Element condition = row.addElement("condition");
            condition.setText(standardReq.getDetails()+ "");
            Element state = row.addElement("state");
            state.setText(standardReq.getMode()+ "");
            Element relateSReq = row.addElement("relateSReq");
            relateSReq.setText(standardReq.getRelateReq() + "");
        }

        //添加table属性 tableVariableType
        Element tableVariableType = table.addElement("tableVariableType");
        if (var.getConceptDatatype() != null) {
            tableVariableType.setText(var.getConceptDatatype());
        }
        //添加table属性 initialValue
        Element initialValue = table.addElement("initialValue");
        if (var.getConceptValue() != null) {
            initialValue.setText(var.getConceptValue());
        }
        //添加table属性 range
        Element range = table.addElement("range");
        if (var.getConceptRange() != null) {
            range.setText(var.getConceptRange());
        }
        //添加table属性accuracy
        Element accuracy = table.addElement("accuracy");
        if (var.getConceptAccuracy() != null) {
            accuracy.setText(var.getConceptAccuracy());
        }
        return table;
    }

    public TableOfVRM Condition(Element table) {
        TableOfVRM con = new TableOfVRM();
        ConceptLibrary var = new ConceptLibrary();
        con.setName(table.attributeValue("name"));
        var.setConceptName(table.attributeValue("name"));

        ArrayList<TableRow> rowList = new ArrayList<>();

        if (table.attributeValue("isOutput").equals("1")) {
            var.setConceptType(ConceptItemType.Output.getNameEN());
        } else {
            var.setConceptType(ConceptItemType.Term.getNameEN());
        }

        var.setConceptDependencyModeClass(table.element("referencedStateMachine").attributeValue("name"));

        Element t = table.element("conditionTable");
        Iterator rows = t.elementIterator("row");

        while (rows.hasNext()) {
            //添加conditionTable节点row
            Element row = (Element) rows.next();
            TableRow r = new TableRow();
            r.setAssignment(row.elementText("assignment"));

            r.setDetails(row.elementText("condition"));

            r.setMode(row.elementText("state"));

            String req = row.elementText("relateSReq");
            if (req != null && req.length() > 0) {
                r.setRelateReq(Integer.valueOf(req));
            }
            rowList.add(r);
        }

        //添加table属性 tableVariableType
        var.setConceptDatatype(table.elementText("tableVariableType"));

        //添加table属性 initialValue
        var.setConceptValue(table.elementText("initialValue"));

        //添加table属性 range
        var.setConceptRange(table.elementText("range"));

        //添加table属性accuracy
        var.setConceptAccuracy(table.elementText("accuracy"));

        con.setRelateVar(var);
        con.setRows(rowList);

        return con;
    }


    /**
     * 事件表xml
     *
     * @param ent 事件表
     * @return {@link Element}
     */
    public Element Event(TableOfVRM ent) {
        Element table = DocumentHelper.createElement("table");
        ConceptLibrary var = ent.getRelateVar();
        if (var.getConceptType().equals(ConceptItemType.Term.getNameEN())) {
            table.addAttribute("name", ent.getName());
            table.addAttribute("isOutput", "0");
        } else if (var.getConceptType().equals(ConceptItemType.Output.getNameEN())) {
            table.addAttribute("name", ent.getName());
            table.addAttribute("isOutput", "1");
        }

        //添加table属性referencedStateMachine
        Element referencedStateMachine = table.addElement("referencedStateMachine");
        if (var.getConceptDependencyModeClass() != null) {
            referencedStateMachine.addAttribute("name", var.getConceptDependencyModeClass());
        }

        Element eventTable = table.addElement("eventTable");
        for (TableRow standardReq : ent.getRows()) {
            //添加conditionTable节点row
            Element row = eventTable.addElement("row");
            Element assignment = row.addElement("assignment");
            assignment.setText(standardReq.getAssignment()+ "");
            Element event = row.addElement("event");
            event.setText(standardReq.getDetails()+ "");
            Element state = row.addElement("state");
            state.setText(standardReq.getMode() + "");
            // 对应的自然语言需求 excelId
            Element relateSReq = row.addElement("relateSReq");
            relateSReq.setText(standardReq.getRelateReq() + "");
        }

        //添加table属性 tableVariableType
        Element tableVariableType = table.addElement("tableVariableType");
        if (var.getConceptDatatype() != null) {
            tableVariableType.setText(var.getConceptDatatype());
        }
        //添加table属性 initialValue
        Element initialValue = table.addElement("initialValue");
        if (var.getConceptValue() != null) {
            initialValue.setText(var.getConceptValue());
        }
        //添加table属性 range
        Element range = table.addElement("range");
        if (var.getConceptRange() != null) {
            range.setText(var.getConceptRange());
        }
        //添加table属性accuracy
        Element accuracy = table.addElement("accuracy");
        if (var.getConceptAccuracy() != null) {
            accuracy.setText(var.getConceptAccuracy());
        }
        return table;
    }

    public TableOfVRM Event(Element table) {
        TableOfVRM ent = new TableOfVRM();
        ConceptLibrary var = new ConceptLibrary();
        ent.setName(table.attributeValue("name"));
        var.setConceptName(table.attributeValue("name"));

        ArrayList<TableRow> rowList = new ArrayList<>();

        if (table.attributeValue("isOutput").equals("1")) {
            var.setConceptType(ConceptItemType.Output.getNameEN());
        } else {
            var.setConceptType(ConceptItemType.Term.getNameEN());
        }
        ;
        var.setConceptDependencyModeClass(table.element("referencedStateMachine").attributeValue("name"));

        Element t = table.element("eventTable");
        Iterator rows = t.elementIterator("row");

        while (rows.hasNext()) {
            //添加conditionTable节点row
            Element row = (Element) rows.next();
            TableRow r = new TableRow();
            r.setAssignment(row.elementText("assignment"));

            r.setDetails(row.elementText("event"));

            r.setMode(row.elementText("state"));

            String req = row.elementText("relateSReq");
            if (req != null && req.length() > 0) {
                r.setRelateReq(Integer.valueOf(req));
            }
            rowList.add(r);
        }

        //添加table属性 tableVariableType
        var.setConceptDatatype(table.elementText("tableVariableType"));

        //添加table属性 initialValue
        var.setConceptValue(table.elementText("initialValue"));

        //添加table属性 range
        var.setConceptRange(table.elementText("range"));

        //添加table属性accuracy
        var.setConceptAccuracy(table.elementText("accuracy"));

        ent.setRelateVar(var);
        ent.setRows(rowList);

        return ent;
    }

    /**
     * 模式集转xml
     *
     * @param mcv ”
     * @return {@link Element}
     */
    public Element ModeClassVRM(ModeClassOfVRM mcv) {
        Element stateMachine = DocumentHelper.createElement("stateMachine");
        ModeClass mc = mcv.getModeClass();
        if (mc.getModeClassName() != null) {
            stateMachine.addAttribute("name", mc.getModeClassName());
        }
        //添加节点description
        Element description = stateMachine.addElement("description");
        if (mc.getModeClassDescription() != null) {
            description.setText(mc.getModeClassDescription());
        }

        //添加节点stateList
        Element stateList = stateMachine.addElement("stateList");
        ArrayList<Mode> mdoes = mcv.getModes();
        for (Mode m : mdoes) {
            if (m.getModeClassName().equals(mc.getModeClassName())) {
                //添加节点state
                Element state = stateList.addElement("state");
                //添加四个属性name，initial，final，enum；
                if (m.getModeName() != null) {
                    state.addAttribute("name", m.getModeName());
                }
                if (m.getInitialStatus() != null) {
                    state.addAttribute("initial", m.getInitialStatus().toString());
                }
                if (m.getFinalStatus() != null) {
                    state.addAttribute("final", m.getFinalStatus().toString());
                }
                if (m.getValue() != null) {
                    state.addAttribute("enum", m.getValue().toString());
                }
            }
        }

        //添加节点stateTransition
        Element stateTransition = stateMachine.addElement("stateTransition");
        ArrayList<StateMachine> sts = mcv.getModeTrans();
        for (StateMachine stateM : sts) {
            if (stateM.getDependencyModeClass().equals(mc.getModeClassName())) {
                //添加节点row  stateM.getDependencyModeClass().equals(mc.getModeClassName())
                Element row = stateTransition.addElement("row");
                Element source = row.addElement("source");
                if (stateM.getSourceState() != null) {
                    source.setText(stateM.getSourceState());
                }
                Element Event = row.addElement("event");
                if (stateM.getEvent() != null) {
                    Event.setText(stateM.getEvent());
                }
                Element destinate = row.addElement("destination");
                if (stateM.getEndState() != null) {
                    destinate.setText(stateM.getEndState());
                }
            }
        }
        return stateMachine;
    }


    public ModeClassOfVRM ModeClassVRM(Element mcv) {
        ModeClassOfVRM item = new ModeClassOfVRM();
        ModeClass mc = new ModeClass();
        ArrayList<Mode> mdoes = new ArrayList<>();
        ArrayList<StateMachine> sts = new ArrayList<>();


        mc.setModeClassName(mcv.attributeValue("name"));
        //添加节点description
        mc.setModeClassDescription(mcv.elementText("description"));

        //添加节点stateList
        Iterator stateList = mcv.element("stateList").elementIterator();
        while (stateList.hasNext()) {
            Element se = (Element) stateList.next();

            Mode m = new Mode();
            //添加四个属性name，initial，final，enum；
            m.setModeName(se.attributeValue("name"));
            String i = se.attributeValue("initial");
            if (i != null && !i.isEmpty())
                m.setInitialStatus(Integer.valueOf(i));

            String e = se.attributeValue("final");
            if (e != null && !e.isEmpty())
                m.setFinalStatus(Integer.valueOf(e));

            String v = se.attributeValue("enum");
            if (v != null && !v.isEmpty())
                m.setValue(Integer.valueOf(v));
            mdoes.add(m);
        }

        //添加节点stateTransition
        Iterator rows = mcv.element("stateTransition").elementIterator("row");
        while(rows.hasNext()){
                //添加节点row  stateM.getDependencyModeClass().equals(mc.getModeClassName())
                Element row = (Element) rows.next();
                StateMachine sm = new StateMachine();
                sm.setSourceState(row.elementText("source"));
                sm.setEvent(row.elementText("event"));
                sm.setEndState(row.elementText("destination"));
            sts.add(sm);
        }

        item.setModeClass(mc);
        item.setModes(mdoes);
        item.setModeTrans(sts);

        return item;
    }


}
