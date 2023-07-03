package com.nuaa.art.vrm.service.dao.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nuaa.art.common.utils.BeanGetUtils;
import com.nuaa.art.vrm.service.dao.DaoHandler;

import org.springframework.stereotype.Service;

@Service
public class DaoHandlerImpl implements DaoHandler {

    @Override
    public <T extends IService> T getDaoService(Class<T> Class)  {
        return BeanGetUtils.getBean(Class);
    }
}
