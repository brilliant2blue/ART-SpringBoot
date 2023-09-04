package com.nuaa.art.user.service.dao.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuaa.art.user.entity.Menu;
import com.nuaa.art.user.service.dao.MenuService;
import com.nuaa.art.user.mapper.MenuMapper;
import org.springframework.stereotype.Service;

/**
* @author konsin
* @description 针对表【sys_menu】的数据库操作Service实现
* @createDate 2023-07-20 17:02:47
*/
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu>
    implements MenuService{
    @Override
    public Menu getMenu(Integer id) {
        return getBaseMapper().selectById(id);
    }
}




