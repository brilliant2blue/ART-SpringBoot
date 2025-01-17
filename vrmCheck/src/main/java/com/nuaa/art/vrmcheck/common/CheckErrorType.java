package com.nuaa.art.vrmcheck.common;

public enum CheckErrorType {

    BasicConstantType("错误类型：基本范式检查，基本类型不存在","常量"),
    BasicConstantMiss("错误类型：基本范式检查，值未定义","常量"),
    BasicConstantValue("错误类型：基本范式检查，值不符合类型","常量"),
    BasicVariableMiss("错误类型：基本范式检查，信息不完整","变量"),
    BasicVariableType("错误类型：基本范式检查，数据类型不存在","变量"),
    BasicVariableInit("错误类型：基本范式检查，初始值不符合类型","变量"),
    BasicVariableModeClassMiss("错误类型：基本范式检查，依赖模式不存在","变量") ,

    BasicType("错误类型：基本范式检查，基本类型不存在","类型"),

    BasicModeClassMiss("错误类型：基本范式检查，模式定义不完整", "模式集"),
    BasicModeClassNoInit("错误类型：基本范式检查，无初始模式", "模式集"),
    BasicModeClassManyInit("错误类型：基本范式检查，初始模式不唯一", "模式集"),
    BasicModeClassTransEmpty("错误类型：基本范式检查，表格为空", "模式集"),
    BasicModeClassModeMiss("错误类型：基本范式检查，模式不存在", "模式集"),
    BasicModeClassEvent("错误类型：基本范式检查，事件语法错误", "模式集"),
    BasicModeClassCondition("错误类型：基本范式检查，条件语法错误", "模式集"),
    BasicModeClassVariableMiss("错误类型：基本范式检查，涉及变量不存在", "模式集"),
    BasicModeClassVariableType("错误类型：基本范式检查，涉及变量的值不符合数据类型", "模式集"),

    BasicEvent("错误类型：基本范式检查，事件语法错误","事件表"),
    BasicEventVariableMiss("错误类型：基本范式检查，涉及变量不存在", "事件表"),
    BasicEventVariableType("错误类型：基本范式检查，涉及变量的值不符合数据类型","事件表"),
    BasicCondition("-错误类型：基本范式检查，条件语法错误", "条件表"),
    BasicConditionVariableMiss("错误类型：基本范式检查，涉及变量不存在", "条件表"),
    BasicConditionVariableType("错误类型：基本范式检查，涉及变量的值不符合数据类型","条件表"),

    //模块一致性完整性错误类型
    ModuleIntegrityInputInPort("错误类型：模块完整性检查，输入变量未享有输入端口", "输入变量"),
    ModuleIntegrityTermInPort("错误类型：模块完整性检查，中间变量未享有输入端口", "中间变量"),
    ModuleIntegrityTermOutPort("错误类型：模块完整性检查，中间变量未享有输出端口", "中间变量"),
    ModuleIntegrityOutputOutPort("错误类型：模块完整性检查，输出变量未享有输出端口", "输出变量"),
    ModuleConsistencyInputOutPort("错误类型：模块一致性检查，输入变量享有输出端口", "输入变量"),
    ModuleConsistencyTermOutPort("错误类型：模块完整性检查，中间变量享有多个输出端口", "中间变量"),
    ModuleConsistencyOutputOutPort("错误类型：模块完整性检查，输出变量享有多个输出端口", "输出变量"),

    // 表函数输入完整性错误类型
    InputIntegrityConditionModeClassMiss("错误类型：输入完整性检查，依赖模式集不存在","条件表"),
    InputIntegrityConditionModeMiss("错误类型：输入完整性检查，依赖模式不存在","条件表"),
    InputIntegrityConditionModeUnmeet("错误类型：输入完整性检查，模式未引用","条件表"),
    InputIntegrityEventModeClassMiss("错误类型：输入完整性检查，依赖模式集不存在","事件表"),
    InputIntegrityEventModeMiss("错误类型：输入完整性检查，依赖模式不存在","事件表"),
    InputIntegrityEventModeUnmeet("错误类型：输入完整性检查，模式未引用","事件表"),

    InputIntegrityModeTransModeMiss("错误类型：输入完整性检查，模式不存在","模式集"),
    InputIntegrityModeTransNextMiss("错误类型：输入完整性检查，没有离开该模式集的模式","模式集"),

    //表函数输出完整性错误类型
    OutputIntegrityConditionValueMiss("错误类型：输出完整性检查，缺失输出值","条件表"),
    OutputIntegrityConditionOutRange("错误类型：输出完整性检查，输出值有误","条件表"),
    OutputIntegrityEventValueMiss("错误类型：输出完整性检查，缺失输出值","事件表"),
    OutputIntegrityEventOutRange("错误类型：输出完整性检查，输出值有误","事件表"),

    //事件表一致性错误类型
    EventConsistencyModeTrans("错误类型：模式转换一致性检查，可转换到的模式不唯一","模式集"),
    EventConsistencyModeTransTrue("错误类型：模式转换一致性检查，多个永真/默认类型模式转换事件的模式冲突","模式集"),
    EventConsistencyValue("错误类型：事件一致性检查，事件结果不唯一","事件表"),
    EventConsistencyTrue("错误类型：事件一致性检查，多个永真/默认类型事件输出值冲突","事件表"),
    //条件表一致性错误类型
    ConditionConsistencyTrue("错误类型：条件一致性检查，多个永真条件输出值冲突","条件表"),
    ConditionConsistencyValue("错误类型：条件一致性检查，条件结果不唯一","条件表"),

    //条件表完整性错误类型
    ConditionIntegrityOnFalse("错误类型：条件完整性检查，只存在永假条件","条件表"),
    ConditionIntegrityValue("错误类型：条件完整性检查，条件无输出","条件表"),
    ;

    private final String errorType;
    private final String relateType;

    CheckErrorType(String errorType, String relateType){
        this.errorType = errorType;
        this.relateType = relateType;
    }

    public String getErrorType() {
        return errorType;
    }

    public String getRelateType() {
        return relateType;
    }
}
