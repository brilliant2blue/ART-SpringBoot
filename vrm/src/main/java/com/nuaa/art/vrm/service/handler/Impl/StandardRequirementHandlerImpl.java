package com.nuaa.art.vrm.service.handler.impl;

import com.nuaa.art.vrm.entity.StandardRequirement;
import com.nuaa.art.vrm.service.handler.StandardRequirementHandler;
import org.springframework.stereotype.Service;

@Service
public class StandardRequirementHandlerImpl implements StandardRequirementHandler {
    /**
     * 生成规范化需求文本内容
     *
     * @param s 规范化需求对象
     * @return {@link String}
     */
    @Override
    public String createContent(StandardRequirement s, Integer type) {
        String str = "";
        switch(type){
            case 0:
                str = "当满足以下条件，" + s.getStandardReqVariable() + "应能够" + s.getStandardReqFunction() + s.getStandardReqValue() + ":" + s.getStandardReqCondition();
                break;
            case 1:
                str = "当满足以下事件，" + s.getStandardReqVariable() + "应能够" + s.getStandardReqFunction() + s.getStandardReqValue() + ":" + s.getStandardReqEvent();
                break;
        }

        return str;
    }
}
