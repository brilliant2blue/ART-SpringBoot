package com.nuaa.art.vrm.service.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuaa.art.vrm.entity.StandardRequirement;
import com.nuaa.art.vrm.mapper.StandardRequirementMapper;
import com.nuaa.art.vrm.service.dao.StandardRequirementService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author konsin
* @description 针对表【vrm_standardrequirement】的数据库操作Service实现
* @createDate 2023-06-10 19:04:25
*/
@Service
public class StandardRequirementServiceImpl extends ServiceImpl<StandardRequirementMapper, StandardRequirement>
    implements StandardRequirementService {

    @Override
    public List<StandardRequirement> listStandardRequirementBySystemId(Integer systemId) {
        return list(new QueryWrapper<StandardRequirement>().eq("systemId",systemId));
    }

    @Override
    public List<StandardRequirement> listStandardRequirementByReqId(Integer id) {
        return list(new QueryWrapper<StandardRequirement>().eq("naturalLangugeReqId",id));
    }

    @Override
    public StandardRequirement getStandardRequirementById(Integer id) {
        return getOne(new QueryWrapper<StandardRequirement>().eq("standardrequirementId",id));
    }

    @Override
    public void insertStandardRequirement(StandardRequirement standardRequirement) {
        save(standardRequirement);
    }

    @Override
    public void updateStandardRequirement(StandardRequirement standardRequirement) {
        updateById(standardRequirement);
    }

    @Override
    public void deleteStandardRequirement(Integer id) {
        remove(new QueryWrapper<StandardRequirement>().eq("naturalLangugeReqId", id));
    }

    @Override
    public void deleteOneStandardRequirement(Integer nid, Integer sid) {
        remove(new QueryWrapper<StandardRequirement>()
                .eq("naturalLangugeReqId",nid)
                .eq("standardrequirementId", sid));
    }

    @Override
    public void deleteStandardRequirementBySystemId(Integer sid) {
        remove(new QueryWrapper<StandardRequirement>().eq("systemId",sid));
    }
}




