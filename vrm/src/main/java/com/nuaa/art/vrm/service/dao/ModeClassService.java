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
    public List<ModeClass> listModeClass();
    public ModeClass getModeClassById(Integer id);
    public List<ModeClass> listModeClassBySystemId(Integer systemId);
    public ModeClass getModeClassByNameandId(@Param("modeClassName")String name, @Param("systemId")Integer systemId);
    public void insertModeClass(ModeClass modeClass);
    public void updateModeClass(ModeClass modeClass);
    public void deleteModeClass(ModeClass modeClass);
    public void deleteModeClassById(Integer systemId);
    public void deleteModeClassByProId(Integer proId);
}
