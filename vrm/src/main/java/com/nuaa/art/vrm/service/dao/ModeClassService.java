package com.nuaa.art.vrm.service.dao;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nuaa.art.vrm.entity.ModeClass;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author konsin
* @description 针对表【vrm_modeclass】的数据库操作Service
* @createDate 2023-06-10 19:04:50
*/
public interface ModeClassService extends IService<ModeClass> {
    List<ModeClass> listModeClass();
    ModeClass getModeClassById(Integer id);
    List<ModeClass> listModeClassBySystemId(Integer systemId);
    ModeClass getModeClassByNameandId(@Param("modeClassName")String name, @Param("systemId")Integer systemId);
    boolean insertModeClass(ModeClass modeClass);
    boolean updateModeClass(ModeClass modeClass);
    boolean deleteModeClass(ModeClass modeClass);
    boolean deleteModeClassById(Integer systemId);
    boolean deleteModeClassByProId(Integer proId);
}
