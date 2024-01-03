package com.nuaa.art.vrm.service.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuaa.art.vrm.entity.NaturalLanguageRequirement;
import com.nuaa.art.vrm.mapper.NaturalLanguageRequirementMapper;
import com.nuaa.art.vrm.service.dao.NaturalLanguageRequirementService;
import com.nuaa.art.vrm.service.dao.StandardRequirementService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author konsin
* @description 针对表【vrm_naturallanguagerequirement】的数据库操作Service实现
* @createDate 2023-06-10 19:04:37
*/
@Service
@Transactional
public class NaturalLanguageRequirementServiceImpl extends ServiceImpl<NaturalLanguageRequirementMapper, NaturalLanguageRequirement>
    implements NaturalLanguageRequirementService {

    @Resource
    StandardRequirementService service;
    @Override
    public List<NaturalLanguageRequirement> listNaturalLanguageRequirementBySystemId(Integer systemId) {
        return list(new QueryWrapper<NaturalLanguageRequirement>().eq("systemId",systemId));
    }

    @Override
    public NaturalLanguageRequirement getNaturalLanguageRequirementById(Integer id) {
        return getById(id);
    }

    @Override
    public List<NaturalLanguageRequirement> listNaturalLanguageRequirement() {
        return list();
    }

    @Override
    public NaturalLanguageRequirement getNaturalLanguageRequirementByExcelId(Integer systemId, Integer excelId) {
        return getOne(new QueryWrapper<NaturalLanguageRequirement>()
                .eq("systemId",systemId)
                .eq("reqExcelId",excelId));
    }

    @Override
    public boolean insertNLR(NaturalLanguageRequirement naturalLanguageRequirement) {
        return save(naturalLanguageRequirement);
    }

    @Override
    public boolean updateNLR(NaturalLanguageRequirement naturalLanguageRequirement) {
        return update(new QueryWrapper<NaturalLanguageRequirement>().eq("systemId",naturalLanguageRequirement.getSystemId()));
    }

    @Override
    @Transactional
    public boolean deleteNLR(NaturalLanguageRequirement naturalLanguageRequirement) {
        return service.deleteStandardRequirementByReqIdAndSystemId(naturalLanguageRequirement.getSystemId()
                ,naturalLanguageRequirement.getReqId()) //按数据库中的id删除
                &&removeById(naturalLanguageRequirement);
    }

    @Override
    @Transactional
    public boolean deleteNLRById(Integer systemId) {
        return service.deleteStandardRequirementBySystemId(systemId) && remove(new QueryWrapper<NaturalLanguageRequirement>().eq("systemId",systemId));

    }

    @Override
    public List<NaturalLanguageRequirement> listNaturalLanguageRequirementBySystemIdAndModuleId(Integer systemId, Integer moduleId) {
        return list(new LambdaQueryWrapper<NaturalLanguageRequirement>()
                .eq(NaturalLanguageRequirement::getSystemId,systemId)
                .eq(NaturalLanguageRequirement::getModuleId, moduleId));
    }

    @Override
    @Transactional
    public boolean deleteNLRBySystemIdAndModuleId(Integer systemId, Integer moduleId) {
        boolean flag = true;
        List<NaturalLanguageRequirement> nreqs = list(new LambdaQueryWrapper<NaturalLanguageRequirement>()
                .eq(NaturalLanguageRequirement::getSystemId,systemId)
                .eq(NaturalLanguageRequirement::getModuleId, moduleId));
        for(NaturalLanguageRequirement requirement: nreqs){
            if(!service.deleteStandardRequirementByReqIdAndSystemId(requirement.getSystemId(),requirement.getReqId())&&removeById(requirement))
                flag=false;
        }
        return flag;
    }

    @Override
    public boolean bindModuleById(Integer id, Integer moduleId){
        LambdaUpdateWrapper<NaturalLanguageRequirement> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(NaturalLanguageRequirement::getReqId, id);
        wrapper.set(NaturalLanguageRequirement::getModuleId, moduleId);
        service.bindModuleByReqId(id, moduleId);
        return update(wrapper);
    }

    @Override
    public boolean bindModuleById(List<Integer> ids, Integer moduleId){
        LambdaUpdateWrapper<NaturalLanguageRequirement> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(NaturalLanguageRequirement::getReqId, ids);
        wrapper.set(NaturalLanguageRequirement::getModuleId, moduleId);
        service.bindModuleByReqId(ids, moduleId);
        return update(wrapper);
    }

    @Override
    public boolean releaseModuleByReqIdsAndModuleId(List<Integer> ids, Integer moduleId){
        LambdaUpdateWrapper<NaturalLanguageRequirement> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(NaturalLanguageRequirement::getReqId, ids);
        wrapper.eq(NaturalLanguageRequirement::getModuleId, moduleId);
        wrapper.set(NaturalLanguageRequirement::getModuleId, 0);
        service.releaseModuleByReqIdsAndModuleId(ids, moduleId);
        return update(wrapper);
    }

    @Override
    public boolean releaseModuleByModuleId(Integer systemId, Integer moduleId){
        LambdaUpdateWrapper<NaturalLanguageRequirement> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(NaturalLanguageRequirement::getSystemId, systemId);
        wrapper.eq(NaturalLanguageRequirement::getModuleId, moduleId);
        wrapper.set(NaturalLanguageRequirement::getModuleId, 0);
        service.releaseModuleByModuleId(systemId, moduleId);
        return update(wrapper);
    }
}




