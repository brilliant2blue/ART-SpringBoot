package com.nuaa.art.common.model;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.util.List;

/**
 * 查询结果分页，便于前端处理返回结果
 *
 * @author konsin
 * @date 2023/07/15
 */
@Data
public class ResultPage<T> {
    List<T> list;
    long total;
    public ResultPage(Page<T> page){
        this.setList(page.getRecords());
        this.setTotal(page.getTotal());
    }
}
