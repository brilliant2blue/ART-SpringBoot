package com.nuaa.art.vrm.common;

public enum ConceptItemType {
    Input("输入变量", "InputVariable"),
    Term("中间变量","TermVariable"),
    Output("输出变量","OutputVariable"),
    Const("常量","ConstVariable");


    private String nameZH;
    private String nameEN;

    ConceptItemType(String nameZH, String nameEN) {
        this.nameZH = nameZH;
        this.nameEN = nameEN;
    }

    public String getNameZH() {return this.nameZH; }

    public void setNameZH(String nameZH) {
        this.nameZH = nameZH;
    }

    public String getNameEN() {
        return this.nameEN;
    }

    public void setNameEN(String nameEN) {
        this.nameEN = nameEN;
    }
}
