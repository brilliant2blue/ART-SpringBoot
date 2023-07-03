package com.nuaa.art.vrm.service.dao;


import com.baomidou.mybatisplus.extension.service.IService;

public interface DaoHandler {
    <T extends IService> T getDaoService(Class<T> Class);
}
