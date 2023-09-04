package com.nuaa.art.user;
import com.nuaa.art.main.MainApplication;
import com.nuaa.art.user.entity.Menu;
import com.nuaa.art.user.model.MenuTree;
import com.nuaa.art.user.service.handler.MenuHandler;
import com.nuaa.art.user.service.handler.impl.MenuHandlerImpl;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = MainApplication.class)
public class testTree {
    @Resource
    MenuHandlerImpl menuHandler;

    @Test
    public void testTreeConvert() {
        List<Menu> menus = new ArrayList<>();
        for(Integer i = 1; i < 15; i++){
            Menu menu = new Menu();
            menu.setId(i);
            menu.setMenuName(i.toString());
            menu.setUrl(i.toString());
            menu.setParentId(i / 3);
            menus.add(menu);
        }
//        System.out.println(menus.toString());
        List<MenuTree> menuTrees = menuHandler.toTree(menus);
        System.out.println(menuTrees.toString());
    }
}
