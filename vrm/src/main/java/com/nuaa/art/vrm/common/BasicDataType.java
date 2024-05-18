package com.nuaa.art.vrm.common;

/**
 * VRM基本数据类型
 *
 * @author konsin
 * @date 2023/06/10
 */
public enum BasicDataType {
    // 20240125: 补充预置类型值域
    IntegerType("integer","int",Integer.MIN_VALUE+".."+ Integer.MAX_VALUE,""),
    DoubleType("double","double","0..10E308","0.1"),
    FloatType("float","float","0..10E38","0.1"),
    CharacterType("character","char","",""),
    EnumeratedType("enumerated","enum","",""),
    StringType("string","string","",""),
    BooleanType("boolean","bool","true,false",""),
    UnsignedType("unsigned","unsigned","","");
    private String typeName;
    private String dataType;
    private String typeRange;
    private String typeAccuracy;
    public String getTypeName() {
        return typeName;
    }
    public String getDataType() {
        return dataType;
    }
    public String getTypeRange() {
        return typeRange;
    }
    public String getTypeAccuracy() {
        return typeAccuracy;
    }
    private BasicDataType(String typeName, String dataType, String typeRange, String typeAccuracy) {
        this.typeName = typeName;
        this.dataType = dataType;
        this.typeRange = typeRange;
        this.typeAccuracy = typeAccuracy;
    }

    public static BasicDataType findTypeByName(String name){
        for(BasicDataType type: BasicDataType.values()){
            if(type.typeName.equalsIgnoreCase(name)){
                return type;
            };
        }
       return null;
    }
}
