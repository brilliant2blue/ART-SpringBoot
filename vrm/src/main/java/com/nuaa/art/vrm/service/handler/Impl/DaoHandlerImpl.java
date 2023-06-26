package com.nuaa.art.vrm.service.handler.Impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nuaa.art.common.utils.BeanGetUtils;
import com.nuaa.art.vrm.entity.Type;
import com.nuaa.art.vrm.service.handler.DaoHandler;
import com.nuaa.art.vrm.service.dao.*;

import org.springframework.stereotype.Service;

@Service
public class DaoHandlerImpl implements DaoHandler {

    @Override
    public <T extends IService> T getDaoService(Class<T> Class)  {
        return BeanGetUtils.getBean(Class);
    }
}
