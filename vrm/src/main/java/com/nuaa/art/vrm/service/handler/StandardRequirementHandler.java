package com.nuaa.art.vrm.service.handler;

import com.nuaa.art.vrm.entity.StandardRequirement;

public interface StandardRequirementHandler {
    /**
     * 生成规范化需求文本内容
     *
     * @param s    规范化需求对象
     * @param type 应用模板类型 0为条件 1为事件
     * @return {@link String}
     */
    String createContent(StandardRequirement s, Integer type);
}
