package com.nuaa.art.vrm.service.dao;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nuaa.art.vrm.entity.StandardRequirement;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author konsin
* @description 针对表【vrm_standardrequirement】的数据库操作Service
* @createDate 2023-06-10 19:04:25
*/
public interface StandardRequirementService extends IService<StandardRequirement> {
    List<StandardRequirement> listStandardRequirementBySystemId(Integer systemId);
    List<StandardRequirement> listStandardRequirementByReqId(Integer id);
    StandardRequirement getStandardRequirementById(Integer id);
    boolean insertStandardRequirement(StandardRequirement standardRequirement);
    boolean updateStandardRequirement(StandardRequirement standardRequirement);
    boolean deleteStandardRequirement(Integer id);
    boolean deleteOneStandardRequirement(@Param("nid")Integer nid, @Param("sid")Integer sid);
    boolean deleteStandardRequirementBySystemId(@Param("sid")Integer sid);
}
