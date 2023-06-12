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
    public List<StandardRequirement> listStandardRequirementBySystemId(Integer systemId);
    public List<StandardRequirement> listStandardRequirementByReqId(Integer id);
    public StandardRequirement getStandardRequirementById(Integer id);
    public void insertStandardRequirement(StandardRequirement standardRequirement);
    public void updateStandardRequirement(StandardRequirement standardRequirement);
    public void deleteStandardRequirement(Integer id);
    public void deleteOneStandardRequirement(@Param("nid")Integer nid, @Param("sid")Integer sid);
    public void deleteStandardRequirementBySystemId(@Param("sid")Integer sid);
}
