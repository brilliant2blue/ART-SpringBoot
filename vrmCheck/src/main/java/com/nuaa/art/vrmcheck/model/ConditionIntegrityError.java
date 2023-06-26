package com.nuaa.art.vrmcheck.model;

import java.util.ArrayList;

/**
 * 条件完整性错误 存储类
 *
 * @author konsin
 * @date 2023/06/12
 */
public class ConditionIntegrityError {
    public int id;
    //		boolean isError = false;
    public boolean isTrueNotExist = false;
    public ArrayList<ConcreteState> lostStates = new ArrayList<ConcreteState>();

}