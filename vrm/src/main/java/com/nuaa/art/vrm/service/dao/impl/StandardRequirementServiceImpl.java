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
    public List<StandardRequirement> listStandardRequirementByReqId(Integer id) {
        return list(new QueryWrapper<StandardRequirement>().eq("naturalLanguageReqId",id));
    }

    @Override
    public StandardRequirement getStandardRequirementById(Integer id) {
        return getOne(new QueryWrapper<StandardRequirement>().eq("standardRequirementId",id));
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
        remove(new QueryWrapper<StandardRequirement>().eq("naturalLanguageReqId", id));
    }

    @Override
    public void deleteOneStandardRequirement(Integer nid, Integer sid) {
        remove(new QueryWrapper<StandardRequirement>()
                .eq("naturalLanguageReqId",nid)
                .eq("standardRequirementId", sid));
    }

    @Override
    public void deleteStandardRequirementBySystemId(Integer sid) {
        remove(new QueryWrapper<StandardRequirement>().eq("systemId",sid));
    }
}




