package com.nuaa.art.vrm.service.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuaa.art.vrm.entity.ProperNoun;
import com.nuaa.art.vrm.entity.StandardRequirement;
import com.nuaa.art.vrm.mapper.ProperNounMapper;
import com.nuaa.art.vrm.service.dao.ProperNounService;
import com.nuaa.art.vrm.service.dao.StandardRequirementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author konsin
* {@code @description} 针对表【vrm_propernoun】的数据库操作Service实现
* {@code @createDate} 2023-06-10 19:04:31
 */
@Service
@Transactional
public class ProperNounServiceImpl extends ServiceImpl<ProperNounMapper, ProperNoun>
    implements ProperNounService {
    @Autowired
    StandardRequirementService standardRequirementService;

    @Override
    public ProperNoun getProperNounById(Integer id) {
        return getById(id);
    }

    @Override
    public List<ProperNoun> listProperNounBySystemId(Integer systemId) {
        return list(new QueryWrapper<ProperNoun>().eq("systemId",systemId));
    }

    @Override
    public ProperNoun getProperNounByNameandId(String name, Integer systemId) {
        return getOne(new QueryWrapper<ProperNoun>()
                .eq("properNounName",name)
                .eq("systemId",systemId));
    }

    @Override
    public boolean insertProperNoun(ProperNoun properNoun) {
        return save(properNoun);
    }

    @Override
    public boolean updateProperNoun(ProperNoun properNoun) {
        ProperNoun tempProperNoun = getProperNounById(properNoun.getProperNounId());
        boolean res = true;
        res = updateById(properNoun);

        for(StandardRequirement standardRequirement : standardRequirementService
                .listStandardRequirementBySystemId(properNoun.getSystemId())){
            if(standardRequirement.getStandardReqVariable().contains(tempProperNoun.getProperNounName())) {
                standardRequirement.setStandardReqVariable(properNoun.getProperNounName());
            }
            res = standardRequirementService.updateStandardRequirement(standardRequirement);
        }
        return res;
    }

    @Override
    public boolean deleteProperNoun(ProperNoun properNoun) {
        return removeById(properNoun);
    }

    @Override
    public boolean deleteProperNounById(Integer systemId) {
        return remove(new QueryWrapper<ProperNoun>().eq("systemId",systemId));
    }

    @Override
    public boolean deleteProperNounByProId(Integer proId) {
        return removeById(proId);
    }
}




