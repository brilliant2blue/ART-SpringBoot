package com.nuaa.art.vrm.service.handler;


import com.baomidou.mybatisplus.extension.service.IService;

public interface DaoHandler {
    <T extends IService> T getDaoService(Class<T> Class);
}
