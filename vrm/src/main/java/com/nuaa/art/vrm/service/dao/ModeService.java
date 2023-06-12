package com.nuaa.art.vrm.service.dao;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nuaa.art.vrm.entity.Mode;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author konsin
* @description 针对表【vrm_mode】的数据库操作Service
* @createDate 2023-06-10 19:04:55
*/
public interface ModeService extends IService<Mode> {
    public List<Mode> listMode();
    public Mode getModeById(Integer id);
    public List<Mode> listModeBySystemId(Integer systemId);
    public List<Mode> listModeByNameandId(@Param("modeClassName")String name, @Param("systemId")Integer systemId);
    public void insertMode(Mode mode);
    public void insertModeList(@Param("modeList")List<Mode> modeList);
    public void updateMode(Mode mode);
    public void deleteMode(Mode mode);
    public void deleteModeById(Integer systemId);
    public void deleteModeListByModeClassId(Integer modeClassId);

}
