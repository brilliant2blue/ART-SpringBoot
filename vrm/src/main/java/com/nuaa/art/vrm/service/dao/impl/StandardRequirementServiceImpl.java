package com.nuaa.art.vrm.service.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuaa.art.vrm.entity.StandardRequirement;
import com.nuaa.art.vrm.mapper.StandardRequirementMapper;
import com.nuaa.art.vrm.service.dao.StandardRequirementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author konsin
* @description 针对表【vrm_standardrequirement】的数据库操作Service实现
* @createDate 2023-06-10 19:04:25
*/
@Service
@Transactional
public class StandardRequirementServiceImpl extends ServiceImpl<StandardRequirementMapper, StandardRequirement>
    implements StandardRequirementService {

    @Override
    public List<StandardRequirement> listStandardRequirementBySystemId(Integer systemId) {
        return list(new QueryWrapper<StandardRequirement>().eq("systemId",systemId));
    }

    @Override
    public List<StandardRequirement> listStandardRequirementByReqIdAndSystemId(Integer systemId, Integer reqId) {
        return list(new QueryWrapper<StandardRequirement>().eq("naturalLanguageReqId",reqId).eq("systemId", systemId));
    }

    @Override
    public StandardRequirement getStandardRequirementById(Integer id) {
        return getOne(new QueryWrapper<StandardRequirement>().eq("standardRequirementId",id));
    }

    @Override
    public boolean insertStandardRequirement(StandardRequirement standardRequirement) {
        return save(standardRequirement);
    }

    @Override
    public boolean updateStandardRequirement(StandardRequirement standardRequirement) {
        return updateById(standardRequirement);
    }

    @Override
    public boolean deleteStandardRequirementByReqIdAndSystemId(Integer systemId, Integer reqId) {
        return remove(new QueryWrapper<StandardRequirement>().eq("naturalLanguageReqId",reqId).eq("systemId", systemId));
    }

    @Override
    public boolean deleteOneStandardRequirement(Integer sReqId) {
        return remove(new QueryWrapper<StandardRequirement>()
                .eq("standardRequirementId", sReqId));
    }

    @Override
    public boolean deleteStandardRequirementBySystemId(Integer sid) {
        return remove(new QueryWrapper<StandardRequirement>().eq("systemId",sid));
    }
}




