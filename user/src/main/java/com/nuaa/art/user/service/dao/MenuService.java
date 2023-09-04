package com.nuaa.art.user.service.dao;

import com.nuaa.art.user.entity.Menu;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author konsin
* @description 针对表【sys_menu】的数据库操作Service
* @createDate 2023-07-20 17:02:47
*/
public interface MenuService extends IService<Menu> {
    public Menu getMenu(Integer id);
}
