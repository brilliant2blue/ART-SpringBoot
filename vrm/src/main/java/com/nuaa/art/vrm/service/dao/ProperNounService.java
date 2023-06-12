package com.nuaa.art.vrm.service.dao;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nuaa.art.vrm.entity.ProperNoun;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author konsin
* @description 针对表【vrm_propernoun】的数据库操作Service
* @createDate 2023-06-10 19:04:31
*/
public interface ProperNounService extends IService<ProperNoun> {
    public ProperNoun getProperNounById(Integer id);
    public List<ProperNoun> listProperNounBySystemId(Integer systemId);
    public ProperNoun getProperNounByNameandId(@Param("properNounName")String name, @Param("systemId")Integer systemId);
    public void insertProperNoun(ProperNoun properNoun);
    public void updateProperNoun(ProperNoun properNoun);
    public void deleteProperNoun(ProperNoun properNoun);
    public void deleteProperNounById(Integer systemId);
    public void deleteProperNounByProId(Integer proId);
}
