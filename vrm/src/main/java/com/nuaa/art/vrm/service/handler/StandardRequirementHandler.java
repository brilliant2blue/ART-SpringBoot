package com.nuaa.art.vrm.service.handler;

import com.nuaa.art.vrm.entity.StandardRequirement;

public interface StandardRequirementHandler {
    /**
     * 生成规范化需求文本内容
     *
     * @param s    规范化需求对象
     * @param type 应用模板类型
     * @return {@link String}
     */
    // todo 后期修改为动态创建，需要根据需要调整
    String createContent(StandardRequirement s, Integer type);
}
