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
    List<Mode> listMode();
    Mode getModeById(Integer id);
    Mode getModeByNameAndId(@Param("modeName") String name, @Param("systemId")Integer id);
    List<Mode> listModeBySystemId(Integer systemId);
    List<Mode> listModeByClassId(@Param("modeClassId")Integer id);
    boolean insertMode(Mode mode);
    boolean insertModeList(@Param("modeList")List<Mode> modeList);
    boolean updateMode(Mode mode);
    boolean deleteMode(Mode mode);
    boolean deleteModeById(Integer systemId);
    boolean deleteModeListByModeClassId(Integer modeClassId);

}
