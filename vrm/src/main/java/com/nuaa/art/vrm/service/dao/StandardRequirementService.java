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
    List<StandardRequirement> listStandardRequirementByReqIdAndSystemId(Integer systemId, Integer reqId);
    List<StandardRequirement> listStandardRequirementByModuleIdAndSystemId(Integer systemId, Integer moduleId);
    StandardRequirement getStandardRequirementById(Integer id);
    boolean insertStandardRequirement(StandardRequirement standardRequirement);
    boolean updateStandardRequirement(StandardRequirement standardRequirement);
    boolean deleteStandardRequirementByReqIdAndSystemId(Integer systemId, Integer reqId);
    boolean deleteOneStandardRequirement(Integer sReqId);
    boolean deleteStandardRequirementBySystemId(@Param("sid")Integer sid);
    boolean bindModuleByReqId(Integer id, Integer moduleId);
    boolean bindModuleByReqId(List<Integer> ids, Integer moduleId);
    boolean releaseModuleByReqIdsAndModuleId(List<Integer> ids, Integer moduleId);
    boolean releaseModuleByModuleId(Integer systemId, Integer moduleId);

}
